package project.web.api.seller;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import kernel.util.PageInfo;
import kernel.util.StringUtils;
import kernel.web.BaseAction;
import kernel.web.ResultObject;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import project.mall.MallRedisKeys;
import project.mall.evaluation.EvaluationService;
import project.mall.goods.AdminMallGoodsService;
import project.mall.goods.SellerGoodsService;
import project.mall.goods.model.*;
import project.mall.utils.EncryptUtil;
import project.mall.utils.MallPageInfo;
import project.party.PartyService;
import project.party.model.Party;
import project.redis.RedisHandler;
import project.syspara.SysparaService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Objects;

/**
 * 商户后台商品管理
 */
@RestController
@CrossOrigin
public class SellerEvaluationController extends BaseAction {
    private final String action = "/seller/evaluation!";

    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Resource
    protected RedisHandler redisHandler;
    @Resource
    protected SellerGoodsService sellerGoodsService;
    @Resource
    EvaluationService evaluationService;

    @Resource
    protected PartyService partyService;

    @Resource
    SysparaService sysparaService;

    @Resource
    AdminMallGoodsService adminMallGoodsService;

    @PostMapping(action + "list.action")
    public Object list(HttpServletRequest request) {
        ResultObject resultObject = new ResultObject();
        PageInfo pageInfo = getPageInfo(request);
        String lang = this.getLanguage(request);
        String sellerId = request.getParameter("sellerId");
        String userName = request.getParameter("userName");
        String evaluationTypeStr = request.getParameter("evaluationType");
        Integer evaluationType = null;
        if (StringUtils.isNotEmpty(evaluationTypeStr) && StringUtils.isNumeric(evaluationTypeStr)) {
            evaluationType = Integer.parseInt(evaluationTypeStr);
        }


        if (sellerId == null) {
            sellerId = this.getLoginPartyId();
        }
        JSONArray jsonArray = new JSONArray();

        MallPageInfo mallPageInfo = evaluationService.listEvaluationBySellerId(pageInfo.getPageNum(), pageInfo.getPageSize(), sellerId, userName, evaluationType, 0, null);

        List<Evaluation> list = mallPageInfo.getElements();

//        int count = 0;
//        String desensitization = sysparaService.find("address_desensitization").getValue();
        for (Evaluation pl : list) {
            SellerGoods sellerGoods = sellerGoodsService.getSellerGoods(pl.getSellerGoodsId());
            /*if (sellerGoods == null) {
                count++;
                continue;
            }*/
            String js = redisHandler.getString(MallRedisKeys.MALL_GOODS_LANG + lang + ":" + (Objects.nonNull(sellerGoods)?sellerGoods.getGoodsId():""));
            /*if (StringUtils.isEmptyString(js)) {
                count++;
                continue;
            }*/
//            if (null != desensitization && desensitization.equals("1")){
            if (StringUtils.isNotEmpty(pl.getUserName())){
                if (pl.getUserName().contains("@")){//邮箱
                    pl.setUserName(EncryptUtil.encrypt(pl.getUserName(), EncryptUtil.EncryptType.EMAIL));
                }else if (StringUtils.isNumeric(pl.getUserName())){//手机号
                    Party party = partyService.cachePartyBy(pl.getPartyId(), false);
                    if (Objects.nonNull(party)&& StringUtils.isNotEmpty(party.getPhone())) {
                        pl.setUserName(EncryptUtil.encrypt(party.getPhone(), EncryptUtil.EncryptType.PHONE));
                    }
                }else {//兼容老数据，虚拟账号的其他名称
                    pl.setUserName(EncryptUtil.encrypt(pl.getUserName(), EncryptUtil.EncryptType.NAME));
                }
            }
//            }
            SystemGoodsLang pLang = StringUtils.isNotEmpty(js)?JSONArray.parseObject(js, SystemGoodsLang.class):null;
            /*if (pLang.getType() == 1) {
                count++;
                continue;
            }*/
            AdminEvaluationVo adminEvaluationVo = new AdminEvaluationVo();
            BeanUtils.copyProperties(pl, adminEvaluationVo);
            adminEvaluationVo.setAvatar(pl.getPartyAvatar());
            adminEvaluationVo.setCommentTime(format.format(pl.getEvaluationTime()));

            GoodsVo goodsVo = new GoodsVo();
            if (Objects.nonNull(sellerGoods)) {
                BeanUtils.copyProperties(sellerGoods, goodsVo);
            }
            goodsVo.setName(Objects.nonNull(pLang)?pLang.getName():"");
            goodsVo.setUnit(Objects.nonNull(pLang)?pLang.getUnit():"");
            goodsVo.setDes(Objects.nonNull(pLang)?pLang.getDes():"");
            goodsVo.setImgDes(Objects.nonNull(pLang)?pLang.getImgDes():"");
            SystemGoods systemGoods = Objects.nonNull(sellerGoods)?adminMallGoodsService.findById(sellerGoods.getGoodsId()):null;
            if (Objects.nonNull(systemGoods)) {//商品图片只要一张
                goodsVo.setImgUrl1(systemGoods.getImgUrl1());
            }
            adminEvaluationVo.setGoodsVo(goodsVo);
            if (StrUtil.isBlank(adminEvaluationVo.getAvatar())) {
                Party party = partyService.cachePartyByUsername(pl.getUserName(), false);
                if (party != null) {
                    adminEvaluationVo.setPartyName(party.getName());
                    adminEvaluationVo.setAvatar(party.getAvatar());
                    adminEvaluationVo.setPartyAvatar(party.getAvatar());
                }
            }

            jsonArray.add(adminEvaluationVo);
        }

        JSONObject object = new JSONObject();

//        if (mallPageInfo.getTotalElements() != 0) {
//            mallPageInfo.setTotalElements(mallPageInfo.getTotalElements() - count);
//        }

        pageInfo.setTotalElements(mallPageInfo.getTotalElements());
        object.put("pageInfo", pageInfo);
        object.put("pageList", jsonArray);
        resultObject.setData(object);
        return resultObject;
    }
}
