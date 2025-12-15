package project.web.api;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import kernel.exception.BusinessException;
import kernel.util.Arith;
import kernel.util.PageInfo;
import kernel.util.StringUtils;
import kernel.web.BaseAction;
import kernel.web.ResultObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import project.Constants;
import project.log.MoneyLog;
import project.log.MoneyLogService;
import project.mall.MallRedisKeys;
import project.mall.area.MallAddressAreaService;
import project.mall.goods.GoodsSkuAtrributionService;
import project.mall.goods.model.GoodsAttributeVo;
import project.mall.goods.model.SellerGoods;
import project.mall.goods.model.SystemGoods;
import project.mall.goods.model.SystemGoodsLang;
import project.mall.orders.GoodsOrdersService;
import project.mall.orders.model.MallOrdersGoods;
import project.mall.orders.model.MallOrdersPrize;
import project.mall.seller.SellerService;
import project.mall.seller.model.Seller;
import project.mall.utils.EncryptUtil;
import project.mall.utils.MallPageInfo;
import project.party.PartyService;
import project.party.model.Party;
import project.redis.RedisHandler;
import project.syspara.SysparaService;
import project.tip.TipConstants;
import project.tip.TipService;
import project.web.api.dto.OrderCountStatusDto;
import util.DateUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@RestController
@CrossOrigin
public class OrdersController extends BaseAction {

    @Resource
    private GoodsOrdersService goodsOrdersService;

    @Resource
    private GoodsSkuAtrributionService goodsSkuAtrributionService;

    @Resource
    protected RedisHandler redisHandler;

    @Resource
    private PartyService partyService;

    @Resource
    SysparaService sysparaService;

    @Resource
    MallAddressAreaService mallAddressAreaService;

    @Resource
    protected MoneyLogService moneyLogService;

    @Resource
    private SellerService sellerService;

    @Resource
    protected TipService tipService;

    private static Log logger = LogFactory.getLog(OrdersController.class);
    private final String action = "/api/order!";

    /**
     * 添加
     * @return
     */
    @PostMapping( action+"submit.action")
    public Object add(HttpServletRequest request){
        ResultObject resultObject = readSecurityContextFromSession(new ResultObject());
        if (!"0".equals(resultObject.getCode())) {
            return resultObject;
        }

        String partyId = this.getLoginPartyId();
        String orderInfo = request.getParameter("orderInfo");

        String addressId = request.getParameter("addressId");

        String fromCart = request.getParameter("fromCart");

        if(StringUtils.isEmptyString(orderInfo)){
            resultObject.setCode("1");
            resultObject.setMsg("非法请求");
            return resultObject;
        }

        JSONObject object = new JSONObject();
        try {
            object.put("orderList", goodsOrdersService.saveOrderSubmit(partyId, orderInfo, addressId));
        } catch (BusinessException e) {
            e.printStackTrace();
            resultObject.setCode("1");
            resultObject.setMsg(e.getMessage());
            return resultObject;
        } catch (Exception e1) {
            e1.printStackTrace();
            resultObject.setCode("1");
            resultObject.setMsg("提交失败");
        }
        resultObject.setData(object);
//        如果是购物车的下单的，增加清理购物车操作
        if ("1".equals(fromCart)) {
            goodsOrdersService.saveRemoveCart(partyId,orderInfo);
        }

        return resultObject;
    }

    /**
     * 支付
     * @return
     */
    @PostMapping( action+"pay.action")
    public Object pay(HttpServletRequest request) {
        ResultObject resultObject = readSecurityContextFromSession(new ResultObject());
        if (!"0".equals(resultObject.getCode())) {
            return resultObject;
        }
        String safeword = request.getParameter("safeword");

        String orderId = request.getParameter("orderId");
        if (StringUtils.isEmptyString(orderId)||orderId.length()<5||orderId.length()>1000) {
            resultObject.setCode("1");
            resultObject.setMsg("订单不存在");
            return resultObject;
        }

        String partyId = this.getLoginPartyId();

        boolean isGuestUser = false;
        Party partyEntity = partyService.cachePartyBy(partyId, false);
        if (partyEntity.getRolename().contains(Constants.SECURITY_ROLE_GUEST)) {
            // 不是真实买家
            isGuestUser = true;
        }

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
        try {
            goodsOrdersService.updatePayOrders(partyId, orderId, safeword);

            if (!isGuestUser) {
                // 真实买家才有提醒
                // 标记一个提醒发货的 tip
                String[] ordersArray = orderId.split(",");
                for (String oneOrderId : ordersArray) {
                    if (StrUtil.isBlank(oneOrderId)) {
                        continue;
                    }
                    tipService.saveTip(oneOrderId.trim(), TipConstants.GOODS_ORDER_WAITDELIVER);
                }
            }
        }catch (BusinessException e){
            e.printStackTrace();
            resultObject.setCode("1");
            resultObject.setMsg(e.getMessage());
            return resultObject;
        }catch (Exception e1){
            e1.printStackTrace();
            resultObject.setCode("1");
            resultObject.setMsg("删除失败");
        }

        return resultObject;
    }

    /**
     * 支付
     * @return
     */
    @PostMapping( action+"countOrderStatus.action")
    public ResultObject countOrderStatus() {

        ResultObject resultObject = readSecurityContextFromSession(new ResultObject());
        if (!"0".equals(resultObject.getCode())) {
            return resultObject;
        }
//        ResultObject resultObject=new ResultObject();
        String partyId = getLoginPartyId();
        Map<String,String> map=  goodsOrdersService.findOrderStatusCount(partyId);
        OrderCountStatusDto orderCountStatusDto=new OrderCountStatusDto();
        orderCountStatusDto.setWaitPayCount(map.getOrDefault("0","0"));//待付款
        orderCountStatusDto.setWaitDeliverCount(Integer.valueOf(map.getOrDefault("1","0"))+Integer.valueOf(map.getOrDefault("2","0")));//待发货
        orderCountStatusDto.setWaitReceiptCount(map.getOrDefault("3","0"));//待收货
        orderCountStatusDto.setWaitEvaluateCount(map.getOrDefault("4","0"));//待评价
        orderCountStatusDto.setRefundCount(map.getOrDefault("6","0"));  //退款
        resultObject.setData(orderCountStatusDto);
        return resultObject;
    }

    /**
     * 取消
     * @return
     */
    @PostMapping( action+"cancel.action")
    public Object cancel(HttpServletRequest request){
        ResultObject resultObject = readSecurityContextFromSession(new ResultObject());
        if (!"0".equals(resultObject.getCode())) {
            return resultObject;
        }
        String partyId = this.getLoginPartyId();

        String orderId = request.getParameter("orderId");
        if(StringUtils.isEmptyString(orderId)||orderId.length()<5||orderId.length()>50){
            resultObject.setCode("1");
            resultObject.setMsg("订单不存在");
            return resultObject;
        }

        String returnReason = request.getParameter("returnReason");

        try {
            goodsOrdersService.updateCalcelOrders(partyId,orderId, returnReason);
        } catch (BusinessException e) {
            e.printStackTrace();
            resultObject.setCode("1");
            resultObject.setMsg(e.getMessage());
            return resultObject;
        } catch (Exception e1) {
            e1.printStackTrace();
            resultObject.setCode("1");
            resultObject.setMsg("取消失败");
        }


        return resultObject;
    }

    /**
     * 收货
     * @return
     */
    @PostMapping( action+"receipt.action")
    public Object receipt(HttpServletRequest request){
        ResultObject resultObject = readSecurityContextFromSession(new ResultObject());
        if (!"0".equals(resultObject.getCode())) {
            return resultObject;
        }
        String partyId = this.getLoginPartyId();

        String orderId = request.getParameter("orderId");
        if(StringUtils.isEmptyString(orderId)||orderId.length()<5||orderId.length()>50){
            resultObject.setCode("1");
            resultObject.setMsg("订单不存在");
            return resultObject;
        }
        try {
            goodsOrdersService.updateReceiptOrders(partyId,orderId);
        }catch (BusinessException e){
            e.printStackTrace();
            resultObject.setCode("1");
            resultObject.setMsg(e.getMessage());
            return resultObject;
        }catch (Exception e1){
            e1.printStackTrace();
            resultObject.setCode("1");
            resultObject.setMsg("收货失败");
        }

        return resultObject;
    }

    /**
     * 退货
     * @return
     */
    @PostMapping( action+"returns.action")
    public Object returns(HttpServletRequest request){
        ResultObject resultObject = readSecurityContextFromSession(new ResultObject());
        if (!"0".equals(resultObject.getCode())) {
            return resultObject;
        }
        String partyId = this.getLoginPartyId();

        boolean isGuestUser = false;
        Party partyEntity = partyService.cachePartyBy(partyId, false);
        if (partyEntity.getRolename().contains(Constants.SECURITY_ROLE_GUEST)) {
            // 不是真实买家
            isGuestUser = true;
        }

        String orderId = request.getParameter("orderId");
        if (StringUtils.isEmptyString(orderId)
                || orderId.length() < 5
                || orderId.length() > 50) {
            resultObject.setCode("1");
            resultObject.setMsg("订单不存在");
            return resultObject;
        }

        String returnReason = request.getParameter("returnReason");

        String returnDetail = request.getParameter("returnDetail");

        try {
            goodsOrdersService.updateReturnsOrders(partyId, orderId, returnReason, returnDetail);

            if (!isGuestUser) {
                // 真实买家，产生提醒记录
                // 标记一个退货 tip
                tipService.saveTip(orderId, TipConstants.GOODS_ORDER_RETURN);
            }
        }catch (BusinessException e){
            e.printStackTrace();
            resultObject.setCode("1");
            resultObject.setMsg(e.getMessage());
            return resultObject;
        }catch (Exception e1){
            e1.printStackTrace();
            resultObject.setCode("1");
            resultObject.setMsg("退货失败");
        }

        return resultObject;
    }


    /**
     * 列表
     */
    @PostMapping( action+"listMain.action")
    public Object list(HttpServletRequest request){
        ResultObject resultObject = readSecurityContextFromSession(new ResultObject());
        if (!"0".equals(resultObject.getCode())) {
            return resultObject;
        }
        Integer status=null;
        String statusString = request.getParameter("status");
        if(StringUtils.isNotEmpty(statusString)){
            status = Integer.parseInt(statusString);
        }

        String partyId = this.getLoginPartyId();
        PageInfo pageInfo = getPageInfo(request);
        JSONArray jsonArray = new JSONArray();

        MallPageInfo mallPageInfo =  goodsOrdersService.listMallOrdersPrize(partyId,status,pageInfo.getPageNum(),pageInfo.getPageSize());

        List<MallOrdersPrize> list = mallPageInfo.getElements();

        Set<String> sellerIds = list.stream().map(entity -> entity.getSellerId()).collect(Collectors.toSet());
        List<String> sellerIdList = new ArrayList<>(sellerIds);
        List<Seller> sellerEntityList = sellerService.getSellerBatch(sellerIdList);
        Map<String, Seller> sellerMap = sellerEntityList.stream().collect(Collectors.toMap(entity -> entity.getId().toString(), Function.identity(), (key1, key2) -> key2));

        for (MallOrdersPrize address : list) {
            JSONObject o = new JSONObject();
            Party party = this.partyService.cachePartyBy(address.getPartyId(), false);
            o.put("partyId", address.getPartyId());
            o.put("username", party.getUsername());
            o.put("id", address.getId().toString());
            o.put("status", address.getStatus());
            o.put("goodsCount", address.getGoodsCount());
            o.put("priceCount", address.getPrizeReal());
            o.put("goodsFrom", address.getSellerName());
            o.put("returnStatus", address.getReturnStatus());
            o.put("fees", address.getFees());
            o.put("tax", address.getTax());

            int sellerStatus = 0;
            Seller sellerEntity = sellerMap.get(address.getSellerId());
            if (sellerEntity != null) {
                sellerStatus = sellerEntity.getStatus();
            }
            o.put("sellerStatus", sellerStatus);

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
     * 订单详情
     * @return
     */
    @PostMapping( action+"info.action")
    public Object info(HttpServletRequest request){
        ResultObject resultObject = new ResultObject();
        String orderId = request.getParameter("orderId");
        String type = request.getParameter("type");
        String lang = request.getParameter("lang");
        String loginType = request.getParameter("loginType");

        String partyId = this.getLoginPartyId();

        int type_value = 0;
        if (StringUtils.isNotEmpty(type)){
            try{
                type_value = Integer.valueOf(type);
            }catch (Exception ex){
                // noop
                type_value = 0;
            }
        }
        if (StringUtils.isNullOrEmpty(orderId)) {
            resultObject.setCode("1");
            resultObject.setMsg("订单号不能为空");
            return resultObject;
        }
        MallOrdersPrize pl = goodsOrdersService.getMallOrdersPrize(orderId);
//        买家端需要校验当前的订单是否属于当前用户，商家端不用校验(商家端可能会用此接口来采购肯定不属于当前用户，不可校验)
        if ("user".equals(loginType)) {
            if (Objects.isNull(pl) || !pl.getPartyId().equals(partyId)) {
                resultObject.setCode("1");
                resultObject.setMsg("无此订单号");
                return resultObject;
            }
        }
        JSONObject o = new JSONObject();

        o.put("id", pl.getId().toString());
        o.put("createTime", DateUtils.getLongDate(pl.getCreateTime()));
        o.put("status", pl.getStatus());

        Date purchTime = pl.getPurchTime();
        if (purchTime == null) {//pl.getPurchStatus() == 1 &&
            // 兼容早期旧记录
            String log = "采购订单[" + orderId + "]";
            List<MoneyLog> byLogs = moneyLogService.findByLog(Constants.MONEYLOG_CONTNET_PUSH_ORDER, log);
            if (CollectionUtil.isEmpty(byLogs)){
                Long upTime = pl.getUpTime();
                if (upTime != null) {
                    purchTime = DateUtil.date(upTime).toJdkDate();
                }
            } else {
                purchTime = byLogs.get(0).getCreateTime();
            }
        }
        if (purchTime == null) {
            o.put("pushTime", "");
        } else {
            o.put("pushTime", DateUtils.getLongDate(purchTime));
        }

        Party party = this.partyService.cachePartyBy(pl.getPartyId(), false);
        Seller seller = this.sellerService.getSeller(pl.getSellerId());
        String desensitization = sysparaService.find("address_desensitization").getValue();
//        虚假评论的username为'0'
        String username = Objects.nonNull(party)?party.getUsername():"0";
        String phone = pl.getPhone();
        String email = pl.getEmail();
        String contacts = pl.getContacts();
        String postcode = pl.getPostcode();
        String country = pl.getCountry();
        String province = pl.getProvince();
        String city = pl.getCity();
        String address = pl.getAddress();
        if (type_value == 0 && null != desensitization && desensitization.equals("1")){
            username = EncryptUtil.encrypt(username, EncryptUtil.EncryptType.NAME);
            phone = EncryptUtil.encrypt(phone, EncryptUtil.EncryptType.PHONE);
            email = EncryptUtil.encrypt(email, EncryptUtil.EncryptType.EMAIL);
            address = EncryptUtil.encrypt(address, EncryptUtil.EncryptType.ADDRESS);
            contacts = EncryptUtil.encrypt(contacts, EncryptUtil.EncryptType.NAME);
        }
        o.put("partyId", pl.getPartyId());
        o.put("username", username);
        o.put("phone", phone);
        o.put("email", email);
        o.put("contacts",contacts);
        o.put("postcode",postcode);
        List<String> addressDetail = mallAddressAreaService.findAddressWithCodeAndLanguage(Long.valueOf(pl.getCountryId()),
                Long.valueOf(pl.getProvinceId()), Long.valueOf(pl.getCityId()), lang);
        o.put("country", StringUtils.isEmptyString(addressDetail.get(0))?country:addressDetail.get(0));
        o.put("province", StringUtils.isEmptyString(addressDetail.get(1))?province:addressDetail.get(1));
        o.put("city", StringUtils.isEmptyString(addressDetail.get(1))?city:addressDetail.get(2));
        o.put("address",address);
        o.put("payStatus",pl.getPayStatus());

        o.put("prizeReal",pl.getPrizeReal());
        o.put("prizeOriginal", pl.getPrizeOriginal());
        o.put("systemPrice",pl.getSystemPrice());

        o.put("profit",pl.getProfit());

        o.put("fees",pl.getFees());
        o.put("tax",pl.getTax());
        o.put("discount", Arith.sub(pl.getPrizeOriginal(), pl.getPrizeReal()));

        o.put("goodsCount",pl.getGoodsCount());
        o.put("purchStatus",pl.getPurchStatus());
//        商家采购优惠后的价格
        if (Double.compare(pl.getSellerDiscount(),0D)!=0) {
            o.put("sellerDiscount",pl.getSellerDiscount());
            o.put("sellerDiscountPrice",Arith.mul(pl.getSystemPrice(),1-pl.getSellerDiscount(),2));
        }
        if (Objects.nonNull(seller)){
            o.put("blank",seller.getBlack());
        }
        JSONObject object = new JSONObject();
        object.put("orderInfo", o);
        resultObject.setData(object);
        return resultObject;
    }

    /**
     * 列表
     */
    @PostMapping( action+"listGoods.action")
    public Object listGoods(HttpServletRequest request){
        ResultObject resultObject = new ResultObject();
//        ResultObject resultObject = readSecurityContextFromSession(new ResultObject());
//        if (!"0".equals(resultObject.getCode())) {
//            return resultObject;
//        }
        String lang = this.getLanguage(request);

        String orderId = request.getParameter("orderId");

        PageInfo pageInfo = getPageInfo(request);
        JSONArray jsonArray = new JSONArray();


//        List<MallOrdersGoods> mallOrdersGoods = goodsOrdersService.listMallOrdersGoods(orderId, pageInfo.getPageNum(), pageInfo.getPageSize());
        MallPageInfo mallPageInfo = goodsOrdersService.listMallOrdersGoodsAboutPage(orderId, pageInfo.getPageNum(), pageInfo.getPageSize());
        List<MallOrdersGoods> mallOrdersGoods = mallPageInfo.getElements();
        List<String> skuIds = mallOrdersGoods.stream().filter(s -> StringUtils.isNotEmpty(s.getSkuId())).map(MallOrdersGoods::getSkuId).collect(Collectors.toList());
        Map<String, List<GoodsAttributeVo>> skuIdAttributes = goodsSkuAtrributionService.listGoodsAttributeBySkuIds(skuIds, lang);
        for(MallOrdersGoods ordersGoods: mallOrdersGoods){
            JSONObject o = new JSONObject();
            String skuId = ordersGoods.getSkuId();
//            String coverImg = this.goodsSkuAtrributionService.selectSkuCoverImg(skuId);
            o.put("attributes", skuIdAttributes.get(skuId));
            o.put("skuId", skuId);
            o.put("goodsId", ordersGoods.getGoodsId());
            o.put("goodsName", "");
            o.put("goodsNum", ordersGoods.getGoodsNum());
            o.put("goodsReal", ordersGoods.getGoodsReal());
//            o.put("goodsIcon", coverImg);
            o.put("goodsIcon", "");
            o.put("fees", ordersGoods.getFees());
            o.put("tax", ordersGoods.getTax());
            o.put("systemPrice",Objects.nonNull(ordersGoods.getSystemPrice())?BigDecimal.valueOf(ordersGoods.getSystemPrice()).setScale(2,BigDecimal.ROUND_DOWN).doubleValue():0);

            o.put("profit", ordersGoods.getGoodsNum() * ordersGoods.getSystemPrice());

            // 总成本 = profit(总采购价) + tax(税收) + fee(运费)
            o.put("totalCost", Double.valueOf(o.get("systemPrice").toString()) * Double.valueOf(o.get("goodsNum").toString()));

            SellerGoods sellerGoods = goodsOrdersService.getSellerGoods(ordersGoods.getGoodsId());
            if (sellerGoods == null) {
                continue;
            }
            o.put("sellingPrice",Objects.nonNull(sellerGoods.getSellingPrice())? BigDecimal.valueOf(sellerGoods.getSellingPrice()).setScale(2,BigDecimal.ROUND_DOWN).doubleValue():0);
            o.put("isShelf",sellerGoods.getIsShelf());//是否上架(上架1  不上架0)
            o.put("isValid",sellerGoods.getIsValid());//是否删除(有效1  无效0)
            SystemGoods systemGoods = sellerGoods.getSystemGoods();
            if(systemGoods!=null){
                o.put("goodsIcon", systemGoods.getImgUrl1());
                String js = redisHandler.getString(MallRedisKeys.MALL_GOODS_LANG + lang + ":" + systemGoods.getId().toString());
                if (StringUtils.isNotEmpty(js)) {
                    SystemGoodsLang pLang = JSONArray.parseObject(js, SystemGoodsLang.class);
                    o.put("goodsName", pLang.getName());
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
}
