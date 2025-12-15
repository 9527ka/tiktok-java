package project.web.api.seller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import kernel.exception.BusinessException;
import kernel.util.Arith;
import kernel.util.PageInfo;
import kernel.util.RandomUtils;
import kernel.util.StringUtils;
import kernel.web.BaseAction;
import kernel.web.ResultObject;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import project.invest.InvestRedisKeys;
import project.invest.LanguageEnum;
import project.invest.goods.model.GoodsLang;
import project.invest.vip.model.Vip;
import project.mall.MallRedisKeys;
import project.mall.auto.AutoConfig;
import project.mall.combo.ComboService;
import project.mall.combo.model.Combo;
import project.mall.combo.model.ComboLang;
import project.mall.combo.model.ComboRecord;
import project.mall.orders.GoodsOrdersService;
import project.mall.seller.SellerService;
import project.mall.utils.MallPageInfo;
import project.party.PartyService;
import project.party.model.Party;
import project.redis.RedisHandler;
import project.syspara.SysparaService;
import util.DateUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

/**
 * 商户后台-直通车
 */
@RestController
@CrossOrigin
public class SellerPromotionalController extends BaseAction {

    private final String action = "/seller/promotional!";

    @Resource
    protected RedisHandler redisHandler;

    @Resource
    protected SellerService sellerService;

    @Resource
    private ComboService comboService;

    @Resource
    private PartyService partyService;

    @Resource
    private SysparaService sysparaService;


    @PostMapping(action + "view.action")
    public Object line(HttpServletRequest request){

        ResultObject resultObject = new ResultObject();
        JSONArray jsonArray = new JSONArray();
//        String lang = this.getLanguage(request); 这里不能走之前 非cn en tw就en的判断默认三种，其他语言有可能配置也有可能没有配置
        String lang = request.getParameter("lang");
        if (StringUtils.isEmptyString(lang)) {
            lang = LanguageEnum.EN.getLang();
        }
        List<Combo> combos = comboService.listCombo();
        for(Combo c : combos){
            JSONObject o = new JSONObject();
            o.put("id", c.getId());
            o.put("name","");
            o.put("desc1","");
            o.put("prize", c.getAmount());
            o.put("per", c.getDay());
            o.put("count", c.getPromoteNum());
            o.put("icon", c.getIconImg());
            String js  = redisHandler.getString(MallRedisKeys.MALL_COMBO_LANG+lang+":"+c.getId().toString());
            if(!StringUtils.isEmptyString(js)){
                ComboLang pLang = JSONArray.parseObject(js, ComboLang.class);
                if(pLang!=null && pLang.getStatus() == 0){
                    o.put("name",pLang.getName());
                    o.put("desc1",pLang.getContent());
                    jsonArray.add(o);
                }
            }else{//传输的语言没有配置时，默认走英文的
                js  = redisHandler.getString(MallRedisKeys.MALL_COMBO_LANG+LanguageEnum.EN.getLang()+":"+c.getId().toString());
                ComboLang pLang = JSONArray.parseObject(js, ComboLang.class);
                if(pLang!=null && pLang.getStatus() == 0){
                    o.put("name",pLang.getName());
                    o.put("desc1",pLang.getContent());
                    jsonArray.add(o);
                }
            }
        }

        JSONObject object = new JSONObject();
        object.put("line", jsonArray);
        resultObject.setData(object);
        return resultObject;
    }


    @PostMapping(action + "buy.action")
    public Object goods(HttpServletRequest request){

        ResultObject resultObject = readSecurityContextFromSession(new ResultObject());
        if (!"0".equals(resultObject.getCode())) {
            return resultObject;
        }
        String partyId = this.getLoginPartyId();

        String orderId = request.getParameter("id");
        if(StringUtils.isEmptyString(orderId)||orderId.length()<5||orderId.length()>50){
            resultObject.setCode("1");
            resultObject.setMsg("订单不存在");
            return resultObject;
        }
        String safeword = request.getParameter("safeword");

        if (StringUtils.isEmptyString(safeword)) {
            resultObject.setCode("1");
            resultObject.setMsg("资金密码不能为空");
            return resultObject;
        }

        if (safeword.length() < 6 || safeword.length() > 12) {
            resultObject.setCode("1");
            resultObject.setMsg("资金密码必须6-12位");
            return resultObject;
        }

        Party party = partyService.cachePartyBy(partyId, false);
        String partySafeword = party.getSafeword();
        if(StringUtils.isEmptyString(partySafeword)){
            resultObject.setCode("999");
            resultObject.setMsg("请设置资金密码");
        }

        if (!this.partyService.checkSafeword(safeword, partyId)) {
            resultObject.setCode("1");
            resultObject.setMsg("资金密码错误");
        }

        if (!party.getEnabled()) {
            resultObject.setCode("1");
            resultObject.setMsg("业务已锁定，请联系客服！");
            return resultObject;
        }

        String lang = this.getLanguage(request);

        String js  = redisHandler.getString(MallRedisKeys.MALL_COMBO_LANG+lang+":"+orderId);
        if(StringUtils.isEmptyString(js)){
            resultObject.setCode("1");
            resultObject.setMsg("商品不存在或者已下架");
        }
        ComboLang pLang = JSONArray.parseObject(js, ComboLang.class);
        if(pLang==null){
            resultObject.setCode("1");
            resultObject.setMsg("商品不存在或者已下架");
        }
        try {
            comboService.updateBuy(partyId,orderId,pLang.getName());
        }catch (BusinessException e){
            e.printStackTrace();
            resultObject.setCode("1");
            resultObject.setMsg(e.getMessage());
            return resultObject;
        }catch (Exception e1){
            e1.printStackTrace();
            resultObject.setCode("1");
            resultObject.setMsg("购买失败");
        }

        return resultObject;
    }

    /**
     * 列表
     */
    @PostMapping( action+"listBuy.action")
    public Object list(HttpServletRequest request){
        ResultObject resultObject = readSecurityContextFromSession(new ResultObject());
        if (!"0".equals(resultObject.getCode())) {
            return resultObject;
        }

        String begin = request.getParameter("begin");
        String end = request.getParameter("end");

        Integer type=null;
        String statusString = request.getParameter("content_type");
        if(statusString!=null){
            type = 0;
        }
        String lang = this.getLanguage(request);
        String partyId = this.getLoginPartyId();
        PageInfo pageInfo = getPageInfo(request);
        JSONArray jsonArray = new JSONArray();

       MallPageInfo mallPageInfo =  comboService.listComboRecord(partyId,begin,end,pageInfo.getPageNum(),pageInfo.getPageSize());

       List<ComboRecord> list = mallPageInfo.getElements();

        for(ComboRecord cr: list){
            JSONObject o = new JSONObject();
            o.put("name", "");
            o.put("startTime", DateUtils.getLongDate(cr.getCreateTime()));
            o.put("stopTime", DateUtils.getLongDate(new Date(cr.getStopTime())));
            o.put("prize", cr.getAmount());
            String js  = redisHandler.getString(MallRedisKeys.MALL_COMBO_LANG+lang+":"+cr.getComboId());
            if(!StringUtils.isEmptyString(js)){
                ComboLang pLang = JSONArray.parseObject(js, ComboLang.class);
                if(pLang!=null){
                    o.put("name",pLang.getName());
                }
            }
            jsonArray.add(o);
        }

        JSONObject object = new JSONObject();
        pageInfo.setTotalElements(mallPageInfo.getTotalElements());
        object.put("pageInfo", pageInfo);
        object.put("pageList", jsonArray);
        resultObject.setData(object);
        return resultObject;
    }


    /**
     * 我的推广
     * @return
     */
    @PostMapping( action+"my.action")
    public Object stats(){
        ResultObject resultObject = readSecurityContextFromSession(new ResultObject());
        if (!"0".equals(resultObject.getCode())) {
            return resultObject;
        }
        String partyId = this.getLoginPartyId();

        Party party = partyService.cachePartyBy(partyId,false);

        JSONObject object = new JSONObject();

        //String promote_link = this.sysparaService.find("promote_link").getValue();
        String promote_link = AutoConfig.getBaseUrl();
        String rebate_ratio_one = this.sysparaService.find("level_one_rebate_ratio").getValue();
        String rebate_ratio_two = this.sysparaService.find("level_two_rebate_ratio").getValue();
        String rebate_ratio_three = this.sysparaService.find("level_three_rebate_ratio").getValue();
        object.put("promoRate1", rebate_ratio_one);
        object.put("promoRate2", rebate_ratio_two);
        object.put("promoRate3", rebate_ratio_three);
        object.put("code", party.getUsercode());
        // 该页面是个API文档页面，是不是搞错了？  TODO
        object.put("download", promote_link);

        resultObject.setData(object);
        return resultObject;

    }

    /**
     * 三级推广
     * @return
     */
    @PostMapping( action+"team_level.action")
    public Object teamLevel(HttpServletRequest request){


        ResultObject resultObject = readSecurityContextFromSession(new ResultObject());
        if (!"0".equals(resultObject.getCode())) {
            return resultObject;
        }

        String levelString = request.getParameter("level");
        if(levelString==null){
            levelString = "0";
        }
        Integer level = Integer.parseInt(levelString);
        if(level==null||level<1||level>2){
            level  = 1;
        }

        String partyId = this.getLoginPartyId();
        PageInfo pageInfo = getPageInfo(request);
        if(pageInfo.getPageSize()>10){
            pageInfo.setPageSize(10);
        }

        JSONObject object = new JSONObject();
        object.put("pageInfo", pageInfo);
        //object.put("pageList", goodsOrdersService.listRebateByLevel(partyId,level,pageInfo.getPageNum(),pageInfo.getPageSize()));
        JSONArray jsonArray = new JSONArray();
        for(int i=0;i<pageInfo.getPageSize();i++){
            JSONObject o = new JSONObject();
            o.put("username", "mln***"+i);
            o.put("regTime", "2022-06-07 01:30:40");
            o.put("rebate", 857489.65);
            o.put("avatar", 3);
            o.put("countOrder", 6425);
            jsonArray.add(o);
        }
        object.put("pageList", jsonArray);
        resultObject.setData(object);
        return resultObject;
    }


}
