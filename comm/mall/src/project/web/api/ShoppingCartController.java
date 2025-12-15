package project.web.api;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import ext.translate.Locales;
import ext.translate.TranslateLocale;
import kernel.exception.BusinessException;
import kernel.util.Arith;
import kernel.util.PageInfo;
import kernel.util.StringUtils;
import kernel.web.BaseAction;
import kernel.web.Page;
import kernel.web.ResultObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import project.mall.MallRedisKeys;
import project.mall.goods.GoodsSkuAtrributionService;
import project.mall.goods.SellerGoodsService;
import project.mall.goods.model.*;
import project.mall.goods.vo.SellerGoodsSkuInfoVO;
import project.mall.orders.GoodsOrdersService;
import project.mall.seller.SellerService;
import project.mall.seller.model.Seller;
import project.redis.RedisHandler;
import project.syspara.SysParaCode;
import project.syspara.Syspara;
import project.syspara.SysparaService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RestController
@CrossOrigin
public class ShoppingCartController extends BaseAction {

    @Resource
    private GoodsSkuAtrributionService goodsSkuAtrributionService;

    @Resource
    private GoodsOrdersService goodsOrdersService;

    @Resource
    protected RedisHandler redisHandler;

    @Resource
    private SellerGoodsService sellerGoodsService;

    @Resource
    private SysparaService sysparaService;

    @Resource
    private SellerService sellerService ;

    private static Log logger = LogFactory.getLog(ShoppingCartController.class);
    private final String action = "/api/cart!";

    /**
     * 查询购物车列表
     *
     * @param request
     * @return
     */
    @PostMapping(action + "list.action")
    public Object list(HttpServletRequest request) {
        ResultObject resultObject = readSecurityContextFromSession(new ResultObject());
        if (!"0".equals(resultObject.getCode())) {
            return resultObject;
        }
        String lang = request.getParameter("lang"); // this.getLanguage(request);

        String partyId = this.getLoginPartyId();
        PageInfo pageInfo = getPageInfo(request);
        JSONArray jsonArray = new JSONArray();

        Page page = null;
        try {
            page = goodsOrdersService.listShoppingCartAboutPage(partyId, pageInfo.getPageNum(), pageInfo.getPageSize());
            List<Map<String, Object>> list = page.getElements();
            List<ShoppingCart> shoppingCarts = new ArrayList<>();
            for (Map<String, Object> stringObjectMap : list) {
                ShoppingCart shoppingCart = new ShoppingCart();
                shoppingCart.setSellerId((String) stringObjectMap.get("SELLER_ID"));
                shoppingCart.setSkuId((String) stringObjectMap.get("SKU_ID"));
                shoppingCart.setGoodsId((String) stringObjectMap.get("GOODS_ID"));
                shoppingCart.setPartyId((String) stringObjectMap.get("PARTY_ID"));
                shoppingCart.setBuyNum(((Long) stringObjectMap.get("BUY_NUM")).intValue());
                shoppingCart.setTempId((String) stringObjectMap.get("TEMP_ID"));
                shoppingCarts.add(shoppingCart);
            }

            int globalBuyMin = 0;
            Syspara globalBuyMinInfo = this.sysparaService.find(SysParaCode.GOODS_BUY_MIN.getCode());
            if (globalBuyMinInfo != null && StrUtil.isNotBlank(globalBuyMinInfo.getValue())) {
                // 选取最大值
                globalBuyMin = Integer.parseInt(globalBuyMinInfo.getValue());
            }
            List<String> skuIds = shoppingCarts.stream().filter(s -> StringUtils.isNotEmpty(s.getSkuId())).map(ShoppingCart::getSkuId).collect(Collectors.toList());
            Map<String, List<GoodsAttributeVo>> skuIdAttributes = goodsSkuAtrributionService.listGoodsAttributeBySkuIds(skuIds, lang);
            for (ShoppingCart shoppingCart : shoppingCarts) {
                JSONObject o = new JSONObject();
                String skuId = shoppingCart.getSkuId();
                o.put("attributes", skuIdAttributes.get(skuId));
                o.put("tempId", shoppingCart.getTempId());
                o.put("skuId", skuId);
                o.put("goodsId", shoppingCart.getGoodsId());
                o.put("goodsName", "");
                o.put("goodsNum", shoppingCart.getBuyNum());
                SellerGoods sellerGoods = goodsOrdersService.getSellerGoods(shoppingCart.getGoodsId());
                if (sellerGoods == null) {
                    continue;
                }
                Seller seller = sellerService.getSeller(sellerGoods.getSellerId());
                o.put("sellerName",Objects.nonNull(seller)?seller.getName():"");
                // 取代 TODO
                SellerGoodsSkuInfoVO sellerGoodSku = sellerGoodsService.findSellerGoodSku(sellerGoods, skuId);
                //SellerGoodsSku sellerGoodSku = sellerGoodsService.findSellerGoodSku(sellerGoods, skuId);
                Double sellingPrice = sellerGoodSku.getSellingPrice();
                Double discountPrice = sellerGoodSku.getDiscountPrice();
                //            这里返回的价格要跟生成订单的价格一致
                o.put("sellingPrice", Arith.roundDown(Objects.isNull(discountPrice) || discountPrice == 0.0D ? sellingPrice : discountPrice, 2));
                o.put("isShelf", sellerGoods.getIsShelf());//是否上架(上架1  不上架0)
                o.put("isValid", sellerGoods.getIsValid());//是否删除(有效1  无效0)
                SystemGoods systemGoods = sellerGoods.getSystemGoods();
                if (systemGoods != null) {
                    o.put("goodsIcon", systemGoods.getImgUrl1());
                    String gl = this.getLanguage(request);
                    String js = redisHandler.getString(MallRedisKeys.MALL_GOODS_LANG + gl + ":" + systemGoods.getId().toString());
                    if (StringUtils.isNotEmpty(js)) {
                        SystemGoodsLang pLang = JSONArray.parseObject(js, SystemGoodsLang.class);
                        o.put("goodsName", pLang.getName());
                        if("en".equals(gl) && !lang.equals(gl)) {
                            // 购物车翻译商品名称
                            o.put("goodsName", TranslateLocale.get(pLang.getName(), Locales.LANG_EN,lang));
                        }
                    }
                }

                int buyMin = Objects.nonNull(systemGoods) ? systemGoods.getBuyMin() : 1;
                if (globalBuyMin > buyMin) {
                    buyMin = globalBuyMin;
                }
                o.put("buyMin", buyMin); //最小购买数量
                o.put("freightAmount",Objects.nonNull(systemGoods)?systemGoods.getFreightAmount():0);
                o.put("goodsTax",Objects.nonNull(systemGoods)?systemGoods.getGoodsTax():0);
                jsonArray.add(o);
            }
        } catch (Exception e) {
            resultObject.setCode("1");
            resultObject.setMsg("程序错误");
            logger.error("error:", e);
        }
        JSONObject object = new JSONObject();
        pageInfo.setTotalElements(page.getTotalElements());
        object.put("pageInfo", pageInfo);
        object.put("pageList", jsonArray);
        resultObject.setData(object);
        return resultObject;
    }

    /**
     * 更新购物车商品或者删除商品
     *
     * @param request
     * @return
     */
    @PostMapping(action + "update.action")
    public Object update(HttpServletRequest request) {
        ResultObject resultObject = new ResultObject();
        String partyId = getLoginPartyId();
        if (StringUtils.isEmptyString(partyId)) {
            resultObject.setCode("403");
            resultObject.setMsg("登录已过期");
            return resultObject;
        }
        String skuId = request.getParameter("skuId");
        String sellerGoodsId = request.getParameter("sellerGoodsId");
        String buyNumStr = request.getParameter("buyNum");
        String isMove = request.getParameter("isMove");
        if (StrUtil.isBlank(skuId)) {
            resultObject.setCode("1");
            resultObject.setMsg("缺少必要参数skuId");
            return resultObject;
        }
        if (StrUtil.isBlank(sellerGoodsId)) {
            resultObject.setCode("1");
            resultObject.setMsg("缺少必要参数sellerGoodsId");
            return resultObject;
        }

        if ("1".equals(isMove)) {
            goodsOrdersService.deleteShoppingCart(partyId, skuId);
            resultObject.setMsg("操作成功");
            return resultObject;
        }

        int buyNum = 1;
        try {
            SellerGoods sellerGoods = goodsOrdersService.getSellerGoods(sellerGoodsId);
            String sellerId = sellerGoods.getSellerId();
            if (partyId.equals(sellerId)) {
                throw new BusinessException("无法购买本店商品");
            }
            if (sellerGoods == null) {
                throw new BusinessException("部分商品已下架");
            }
            if (sellerGoods != null && 0 == sellerGoods.getIsShelf()) {
                throw new BusinessException("部分商品已下架");
            }

            int buyMin = sellerGoods.getBuyMin() == null ? 1 : sellerGoods.getBuyMin();
            Syspara buyMax_para = sysparaService.find("mall_max_goods_number_in_order");
            buyNum = Integer.parseInt(buyNumStr);
            if (buyNum < buyMin) {
                throw new BusinessException("少于最小采购数量");
            }
            if (Objects.nonNull(buyMax_para)) {
                int buyMax = buyMax_para.getInteger();
                if (buyNum > buyMax) {
                    throw new BusinessException("大于最大采购数量");
                }
            }

            ShoppingCart shoppingCart = goodsOrdersService.findShoppingCart(partyId, sellerGoodsId, skuId);
            shoppingCart.setBuyNum(buyNum);
            goodsOrdersService.updateshoppingCart(shoppingCart);
            resultObject.setMsg("操作成功");
            return resultObject;
        } catch (BusinessException e) {
            resultObject.setCode("1");
            resultObject.setMsg(e.getMessage());
            return resultObject;
        } catch (Exception e) {
            resultObject.setCode("1");
            resultObject.setMsg("程序错误");
            logger.error("更新购物车或者移除购物车商品失败:", e);
            return resultObject;
        }
    }

    /**
     * 添加购物车
     */
    @PostMapping(action + "add.action")
    public Object add(HttpServletRequest request) {
        ResultObject resultObject = new ResultObject();
        String partyId = getLoginPartyId();
        if (StringUtils.isEmptyString(partyId)) {
            resultObject.setCode("403");
            resultObject.setMsg("登录已过期");
            return resultObject;
        }
        String skuId = request.getParameter("skuId");
        String sellerGoodsId = request.getParameter("sellerGoodsId");
        String sellerId = request.getParameter("sellerId");
        String buyNumStr = request.getParameter("buyNum");
        String tempId = request.getParameter("tempId");

        int buyNum = 0;
        try {
            if (partyId.equals(sellerId)) {
                throw new BusinessException("无法购买本店商品");
            }

            SellerGoods sellerGoods = goodsOrdersService.getSellerGoods(sellerGoodsId);
            if (sellerGoods == null) {
                throw new BusinessException("部分商品已下架");
            }
            if (sellerGoods != null && 0 == sellerGoods.getIsShelf()) {
                throw new BusinessException("部分商品已下架");
            }
            int buyMin = sellerGoods.getBuyMin() == null ? 0 : sellerGoods.getBuyMin();
            Syspara buyMax_para = sysparaService.find("mall_max_goods_number_in_order");
            buyNum = Integer.parseInt(buyNumStr);
            if (buyNum < buyMin) {
                throw new BusinessException("少于最小采购数量");
            }
            if (Objects.nonNull(buyMax_para)) {
                int buyMax = buyMax_para.getInteger();
                if (buyNum > buyMax) {
                    throw new BusinessException("大于最大采购数量");
                }
            }
            if (goodsOrdersService.getShoppingCartNumByPartyId(partyId) >= 100) {
                throw new BusinessException("购物车已满");
            }

            ShoppingCart shoppingCart = goodsOrdersService.findShoppingCart(partyId, sellerGoodsId, skuId);
            if (Objects.nonNull(shoppingCart)) {
                int newBuyNum = shoppingCart.getBuyNum() + buyNum;
                if (Objects.nonNull(buyMax_para)) {
                    int buyMax = buyMax_para.getInteger();
                    if (newBuyNum > buyMax) {
                        throw new BusinessException("大于最大采购数量");
                    }
                }
                shoppingCart.setBuyNum(newBuyNum);
                this.goodsOrdersService.updateshoppingCart(shoppingCart);
                resultObject.setMsg("操作成功");
                return resultObject;
            }

            ShoppingCart cart = new ShoppingCart();
            cart.setPartyId(partyId);
            cart.setGoodsId(sellerGoodsId);
            cart.setSkuId(skuId);
            cart.setBuyNum(buyNum);
            cart.setCreateTime(new Date());
            cart.setSellerId(sellerId);
            cart.setTempId(tempId);
            this.goodsOrdersService.updateshoppingCart(cart);
            resultObject.setMsg("操作成功");
            return resultObject;
        } catch (BusinessException e) {
            resultObject.setCode("1");
            resultObject.setMsg(e.getMessage());
            return resultObject;
        } catch (Exception e) {
            resultObject.setCode("1");
            resultObject.setMsg("程序错误");
            logger.error("添加购物车失败:", e);
            return resultObject;
        }
    }

}
