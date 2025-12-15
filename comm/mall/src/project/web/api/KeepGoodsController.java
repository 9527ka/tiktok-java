package project.web.api;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import kernel.util.PageInfo;
import kernel.web.BaseAction;
import kernel.web.ResultObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import project.mall.evaluation.EvaluationService;
import project.mall.goods.KeepGoodsService;
import project.mall.goods.SellerGoodsService;
import project.mall.goods.model.*;
import project.mall.seller.SellerService;
import project.mall.seller.model.Seller;
import project.mall.utils.MallPageInfo;
import project.redis.RedisHandler;
import util.DateUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

@Slf4j
@RestController
@CrossOrigin
public class KeepGoodsController extends BaseAction {
    @Resource
    protected RedisHandler redisHandler;

    @Resource
    protected KeepGoodsService keepGoodsService;

    @Resource
    protected EvaluationService evaluationService;
    @Resource
    protected SellerGoodsService sellerGoodsService;

    @Resource
    protected SellerService sellerService;
    private final String action = "/api/keepGoods!";

    @PostMapping(action + "list.action")
    public Object list(HttpServletRequest request) {
        ResultObject resultObject = readSecurityContextFromSession(new ResultObject());
        if (!"0".equals(resultObject.getCode())) {
            return resultObject;
        }
        PageInfo pageInfo = getPageInfo(request);
        String partyId = getLoginPartyId();
        String lang = this.getLanguage(request);
        JSONArray jsonArray = new JSONArray();

        MallPageInfo mallPageInfo = keepGoodsService.listKeepGoods(pageInfo.getPageNum(), pageInfo.getPageSize(), partyId);
        List<KeepGoods> list = mallPageInfo.getElements();
        for (KeepGoods pl : list) {
            KeepGoodsVo keepGoodsVo = new KeepGoodsVo();
            BeanUtils.copyProperties(pl, keepGoodsVo);
            SellerGoods sellerGoods = sellerGoodsService.getSellerGoods(keepGoodsVo.getSellerGoodsId());

            if (null != sellerGoods){
                Seller seller = sellerService.getSeller(sellerGoods.getSellerId());
                SystemGoodsLang pLang = this.sellerGoodsService.selectGoodsLang(lang, sellerGoods.getGoodsId());
                if (null == pLang || pLang.getType() == 1){
                    continue;
                }
                GoodsVo goodsVo = new GoodsVo();
                BeanUtils.copyProperties(sellerGoods.getSystemGoods(), goodsVo);
                goodsVo.setName(pLang.getName());
                goodsVo.setUnit(pLang.getUnit());
                goodsVo.setDes(pLang.getDes());
                goodsVo.setImgDes(pLang.getImgDes());
                goodsVo.setId(sellerGoods.getId());
                goodsVo.setGoodsId(sellerGoods.getGoodsId());
                goodsVo.setSellingPrice(sellerGoods.getSellingPrice());
                goodsVo.setViewsNum(sellerGoodsService.getViewNums(sellerGoods.getId().toString()));
                goodsVo.setCategoryId(sellerGoods.getCategoryId());
                goodsVo.setSoldNum(sellerGoods.getSoldNum());
                goodsVo.setIsShelf(sellerGoods.getIsShelf());
                if (null != seller){
                    goodsVo.setBlack(seller.getBlack());
                } else {
                    goodsVo.setBlack(1);
                }
                Date discountStartTime = sellerGoods.getDiscountStartTime();
                Date discountEndTime = sellerGoods.getDiscountEndTime();
                if (null != discountStartTime && null != discountEndTime){
                    goodsVo.setDiscountStartTime(DateUtils.getLongDate(discountStartTime));
                    goodsVo.setDiscountEndTime(DateUtils.getLongDate(discountEndTime));
                    Date now = new Date();
                    if (discountStartTime.compareTo(now)<=0 && now.compareTo(discountEndTime)<=0) {
                        goodsVo.setDiscountRatio(sellerGoods.getDiscountRatio());
                        goodsVo.setDiscountPrice(sellerGoods.getDiscountPrice());
                    }
                }
                jsonArray.add(goodsVo);
            }
        }
        JSONObject object = new JSONObject();
        pageInfo.setTotalElements(mallPageInfo.getTotalElements());
        object.put("pageInfo", pageInfo);
        object.put("pageList", jsonArray);
        resultObject.setData(object);

        return resultObject;
    }

    @PostMapping(action + "count.action")
    public Object count(){
        ResultObject resultObject = readSecurityContextFromSession(new ResultObject());
        if (!"0".equals(resultObject.getCode())) {
            return resultObject;
        }
        String partyId = getLoginPartyId();
        Integer count = this.keepGoodsService.getKeepGoodsCount(partyId);
        count = null == count ? 0 : count;
        resultObject.setData(count);
        return resultObject;
    }

    @PostMapping(action + "add.action")
    public Object add(HttpServletRequest request) {
        ResultObject resultObject = readSecurityContextFromSession(new ResultObject());
        if (!"0".equals(resultObject.getCode())) {
            return resultObject;
        }
        String sellerGoodsId = request.getParameter("sellerGoodsId");
        if (sellerGoodsId == null) {
            resultObject.setCode("1");
            resultObject.setMsg("商品id不能为空");
            return resultObject;
        }
        SellerGoods sellerGoods = sellerGoodsService.getSellerGoods(sellerGoodsId);
        if (sellerGoods == null) {
            resultObject.setCode("1");
            resultObject.setMsg("商品id错误");
            return resultObject;
        }
        String partyId = this.getLoginPartyId();

        if (keepGoodsService.queryIsKeep(sellerGoodsId, partyId) == 1) {
            resultObject.setCode("1");
            resultObject.setMsg("请勿重复添加");
            return resultObject;
        }
        keepGoodsService.addKeep(partyId, sellerGoodsId);

        resultObject.setCode("0");
        resultObject.setMsg("操作成功");
        return resultObject;
    }

    @PostMapping(action + "del.action")
    public Object del(HttpServletRequest request) {
        ResultObject resultObject = readSecurityContextFromSession(new ResultObject());
        if (!"0".equals(resultObject.getCode())) {
            return resultObject;
        }
        String sellerGoodsId = request.getParameter("sellerGoodsId");
        if (sellerGoodsId == null) {
            resultObject.setCode("1");
            resultObject.setMsg("收藏id不能为空");
            return resultObject;
        }

        String partyId = this.getLoginPartyId();

        keepGoodsService.deleteKeep(sellerGoodsId,partyId);

        resultObject.setCode("0");
        resultObject.setMsg("操作成功");
        return resultObject;
    }
}
