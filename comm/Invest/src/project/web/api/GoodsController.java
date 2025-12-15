package project.web.api;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import kernel.exception.BusinessException;
import kernel.util.Arith;
import kernel.util.PageInfo;
import kernel.util.StringUtils;
import kernel.web.BaseAction;
import kernel.web.ResultObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import project.invest.InvestRedisKeys;
import project.invest.goods.GoodsService;
import project.invest.goods.model.*;
import project.invest.project.model.ProjectLang;
import project.redis.RedisHandler;
import project.syspara.SysparaService;
import project.wallet.WalletExtend;
import project.wallet.WalletService;
import util.DateUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@CrossOrigin
public class GoodsController extends BaseAction {

    @Resource
    private WalletService walletService;
    @Resource
    private GoodsService goodsService;
    @Resource
    protected RedisHandler redisHandler;

    @Resource
    private SysparaService sysparaService;

    private static Log logger = LogFactory.getLog(GoodsController.class);

    private final String action = "/api/goods!";

    @PostMapping( action+"points.action")
    public Object getPoint(HttpServletRequest request){
        ResultObject resultObject = readSecurityContextFromSession(new ResultObject());
        if (!"0".equals(resultObject.getCode())) {
            return resultObject;
        }
        String partyId = this.getLoginPartyId();
        Double point = walletService.getInvestPointBuyPartyId(partyId);
        JSONObject object = new JSONObject();
        object.put("points", point.intValue());
        object.put("rule", sysparaService.find("invest_order_rule").getValue());
        resultObject.setData(object);
        return resultObject;
    }


    /**
     * 在售
     * @return
     */
    @PostMapping( action+"list.action")
    public Object list(HttpServletRequest request){
        ResultObject resultObject = new ResultObject();
        PageInfo pageInfo = getPageInfo(request);
        String lang = this.getLanguage(request);

        JSONArray jsonArray = new JSONArray();
        for(Goods pl : goodsService.listGoodsSell(pageInfo.getPageNum(),pageInfo.getPageSize())){
            JSONObject o = new JSONObject();
            String js  = redisHandler.getString(InvestRedisKeys.INVEST_GOODS_LANG+lang+":"+pl.getId().toString());
            if(StringUtils.isEmptyString(js)){
                continue;
            }
            GoodsLang pLang = JSONArray.parseObject(js, GoodsLang.class);
            if(pLang.getType()==1){
                continue;
            }
            o.put("goodsId", pLang.getGoodsId());
            o.put("iconImg",pl.getIconImg());
            o.put("name", pLang.getName());
            o.put("des", pLang.getDes());
            o.put("prize", pl.getPrize());
            o.put("lastAmount", pl.getLastAmount());
            jsonArray.add(o);
        }

        JSONObject object = new JSONObject();
        object.put("pageInfo", pageInfo);
        object.put("pageList", jsonArray);
        resultObject.setData(object);
        return resultObject;
    }

    @PostMapping( action+"info.action")
    public Object goodsInfo(HttpServletRequest request){
        ResultObject resultObject = readSecurityContextFromSession(new ResultObject());
        if (!"0".equals(resultObject.getCode())) {
            return resultObject;
        }
        String goodsId = request.getParameter("goodsId");
        Goods pl = goodsService.findById(goodsId);
        JSONObject o = new JSONObject();
        String lang = this.getLanguage(request);
        String js  = redisHandler.getString(InvestRedisKeys.INVEST_GOODS_LANG+lang+":"+pl.getId().toString());

        GoodsLang pLang = JSONArray.parseObject(js, GoodsLang.class);
        o.put("goodsId", pLang.getGoodsId());
        o.put("iconImg",pl.getIconImg());
        o.put("name", pLang.getName());
        o.put("des", pLang.getDes());
        o.put("prize", pl.getPrize());
        o.put("total", pl.getTotal());
        o.put("lastAmount", pl.getLastAmount());
        o.put("scale", sysparaService.find("invest_point_exchange_val").getValue());
        if(pl.getStatus()!=0){
            o.put("lastAmount", 0);
        }
        o.put("address", "");//地址
        o.put("phone", "");//手机号
        o.put("contacts", "");//联系人
        List<Useraddress> useraddressList =  goodsService.getAddressUse(this.getLoginPartyId());
        if(useraddressList.size()>0){
            o.put("address", useraddressList.get(0).getAddress());//地址
            o.put("phone", useraddressList.get(0).getPhone());//手机号
            o.put("contacts", useraddressList.get(0).getContacts());//联系人
        }
        resultObject.setData(o);
        return resultObject;
    }

    /**
     * 项目购买
     * @return
     */
    @PostMapping( action+"goodsBuy.action")
    public Object buy(HttpServletRequest request){
        ResultObject resultObject = readSecurityContextFromSession(new ResultObject());
        if (!"0".equals(resultObject.getCode())) {
            return resultObject;
        }

        String phone = request.getParameter("phone");

        if(StringUtils.isEmptyString(phone)){
            resultObject.setCode("1");
            resultObject.setMsg("手机号不能为空");
            return resultObject;
        }

        String contacts = request.getParameter("contacts");

        if(StringUtils.isEmptyString(contacts)){
            resultObject.setCode("1");
            resultObject.setMsg("联系人不能为空");
            return resultObject;
        }
        String address = request.getParameter("address");

        if(StringUtils.isEmptyString(address)){
            resultObject.setCode("1");
            resultObject.setMsg("地址不能为空");
            return resultObject;
        }
        String goodsId = request.getParameter("goodsId");
        String goodsNum = request.getParameter("goodsNum");
        int amount = Integer.parseInt(goodsNum);
        if(amount<=0||amount>999999){
            resultObject.setCode("1");
            resultObject.setMsg("非法请求");
            return resultObject;
        }
        String partyId = this.getLoginPartyId();


        try {
            goodsService.updateBuyGoods(partyId,goodsId,amount,phone,contacts,address);

        }catch (BusinessException e){
            resultObject.setCode(String.valueOf(e.getSign()));
            resultObject.setMsg(e.getMessage());
            return resultObject;
        }catch (Exception e1){
            e1.printStackTrace();
            logger.error("购买失败",e1);
            resultObject.setCode("1");
            resultObject.setMsg("购买失败,联系客服");
        }

        return resultObject;
    }

    /**
     * u兑换
     * @return
     */
    @PostMapping( action+"uExchange.action")
    public Object exchange(HttpServletRequest request){
        ResultObject resultObject = readSecurityContextFromSession(new ResultObject());
        if (!"0".equals(resultObject.getCode())) {
            return resultObject;
        }
        String goodsId = request.getParameter("goodsId");
        String goodsNum = request.getParameter("goodsNum");
        String partyId = this.getLoginPartyId();

        String lockKey = InvestRedisKeys.INVEST_ORDER_USER_LOCK+partyId;
        if(!redisHandler.lock(lockKey,10)){
            resultObject.setCode("1");
            resultObject.setMsg("正在购买");
            return resultObject;
        }
        try {
            goodsService.updateExchangeUsdt(partyId,goodsId,Integer.parseInt(goodsNum),sysparaService.find("invest_point_exchange_val").getLong());

        }catch (BusinessException e){
            resultObject.setCode(String.valueOf(e.getSign()));
            resultObject.setMsg(e.getMessage());
            return resultObject;
        }catch (Exception e1){
            e1.printStackTrace();
            logger.error("购买失败",e1);
            resultObject.setCode("1");
            resultObject.setMsg("购买失败,联系客服");
        }
        finally {
            redisHandler.remove(lockKey);
        }

        return resultObject;
    }

    /**
     * 兑换记录
     * @return
     */
    @PostMapping( action+"recordsList.action")
    public Object recordsList(HttpServletRequest request){
        ResultObject resultObject = readSecurityContextFromSession(new ResultObject());
        if (!"0".equals(resultObject.getCode())) {
            return resultObject;
        }
        String partyId = this.getLoginPartyId();
        String type = request.getParameter("type");
        PageInfo pageInfo = getPageInfo(request);

        JSONObject object = new JSONObject();
        object.put("pageInfo", pageInfo);
        if(type!=null&&type.equals("0")){
            object.put("pageList", pageGoodsBuy(partyId,pageInfo));
        }else{
            object.put("pageList", pagePointExchange(partyId,pageInfo));
        }

        resultObject.setData(object);
        return resultObject;
    }

    private JSONArray pageGoodsBuy(String partyId,PageInfo pageInfo ){
        JSONArray jsonArray = new JSONArray();
        for(GoodsBuy pl : goodsService.listGoodsBuy(partyId,pageInfo.getPageNum(),pageInfo.getPageSize())){
            JSONObject o = new JSONObject();
            o.put("id", pl.getId());
            o.put("createTime", DateUtils.getLongDate(pl.getCreateTime()));
            o.put("payVal", pl.getPayPoint());
            o.put("status",pl.getStatus());
            jsonArray.add(o);
        }
        return jsonArray;
    }

    private JSONArray pagePointExchange(String partyId,PageInfo pageInfo ){
        JSONArray jsonArray = new JSONArray();
        for(PointExchange pl : goodsService.listPointExchange(partyId,pageInfo.getPageNum(),pageInfo.getPageSize())){
            JSONObject o = new JSONObject();
            o.put("id", pl.getId());
            o.put("createTime", DateUtils.getLongDate(pl.getCreateTime()));
            o.put("payVal", pl.getPayPoint());
            o.put("status",1);
            jsonArray.add(o);
        }
        return jsonArray;
    }

    /**
     * 兑换记录
     * @return
     */
    @PostMapping( action+"recordsInfo.action")
    public Object recordsInfo(HttpServletRequest request){
        ResultObject resultObject = readSecurityContextFromSession(new ResultObject());
        if (!"0".equals(resultObject.getCode())) {
            return resultObject;
        }
        String lang = this.getLanguage(request);

        String type = request.getParameter("type");
        String id = request.getParameter("id");
        JSONObject object = new JSONObject();

        JSONObject o = new JSONObject();

        if(type!=null&&type.equals("0")){
            GoodsBuy pl = goodsService.findGoodsBuyById(id);
            o.put("id", pl.getId());
            o.put("createTime", DateUtils.getLongDate(pl.getCreateTime()));
            o.put("payVal", pl.getPayPoint());
            o.put("status",pl.getStatus());

            Goods g = goodsService.findById(pl.getGoodsId());
            String js  = redisHandler.getString(InvestRedisKeys.INVEST_GOODS_LANG+lang+":"+pl.getGoodsId());
            GoodsLang pLang = JSONArray.parseObject(js, GoodsLang.class);
            o.put("goodsName", pLang.getName());
            o.put("goodsPrize", g.getPrize());
            o.put("goodsImg", g.getIconImg());

            o.put("phone", pl.getPhone());
            o.put("contacts", pl.getContacts());
            o.put("address", pl.getAddress());

            o.put("num", pl.getNum());
        }else{
            PointExchange pl = goodsService.findPointExchangeById(id);
            o.put("id", pl.getId());
            o.put("createTime", DateUtils.getLongDate(pl.getCreateTime()));
            o.put("payVal", pl.getPayPoint());
            o.put("status",1);
            Goods g = goodsService.findById(pl.getGoodsId());
            String js  = redisHandler.getString(InvestRedisKeys.INVEST_GOODS_LANG+lang+":"+pl.getGoodsId());
            GoodsLang pLang = JSONArray.parseObject(js, GoodsLang.class);
            o.put("goodsName", pLang.getName());
            o.put("goodsPrize", g.getPrize());
            o.put("goodsImg", g.getIconImg());

            o.put("num", pl.getNum());
            o.put("usdt", pl.getUsdt());

            o.put("scale", pl.getScale());

        }


        object.put("info", o);
        resultObject.setData(object);
        return resultObject;
    }


}
