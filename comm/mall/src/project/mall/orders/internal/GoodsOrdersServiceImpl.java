package project.mall.orders.internal;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import kernel.exception.BusinessException;
import kernel.util.Arith;
import kernel.util.DateTimeTools;
import kernel.util.DateUtils;
import kernel.util.StringUtils;
import kernel.web.Page;
import kernel.web.PagedQueryDao;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.*;
import org.hibernate.query.NativeQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import project.Constants;
import project.RedisKeys;
import project.log.LogService;
import project.log.MoneyLog;
import project.log.MoneyLogService;
import project.mall.MallRedisKeys;
import project.mall.area.MallAddressAreaService;
import project.mall.area.model.MallCity;
import project.mall.area.model.MallCountry;
import project.mall.area.model.MallState;
import project.mall.combo.ComboService;
import project.mall.combo.model.Combo;
import project.mall.comment.AdminSystemCommentService;
import project.mall.evaluation.EvaluationService;
import project.mall.event.message.SellerGoodsViewCountEvent;
import project.mall.event.model.SellerGoodsViewCountInfo;
import project.mall.goods.GoodsSkuAtrributionService;
import project.mall.goods.SellerGoodsService;
import project.mall.goods.dto.GoodSkuAttrDto;
import project.mall.goods.dto.SkuDto;
import project.mall.goods.model.*;
import project.mall.goods.vo.OrderFlagVO;
import project.mall.goods.vo.SellerGoodsSkuInfoVO;
import project.mall.log.OrderLogService;
import project.mall.log.model.OrderLog;
import project.mall.log.model.OrderStatusEnum;
import project.mall.notification.utils.notify.client.CommonNotifyService;
import project.mall.notification.utils.notify.client.NotificationHelperClient;
import project.mall.orders.GoodsOrdersService;
import project.mall.orders.model.MallAddress;
import project.mall.orders.model.MallOrderRebate;
import project.mall.orders.model.MallOrdersGoods;
import project.mall.orders.model.MallOrdersPrize;
import project.mall.orders.vo.MallOrderVO;
import project.mall.orders.vo.OrderGoodsVO;
import project.mall.orders.vo.SellerGoodsSkuVO;
import project.mall.seller.AdminSellerService;
import project.mall.seller.MallLevelService;
import project.mall.seller.model.MallLevel;
import project.mall.seller.model.Seller;
import project.mall.utils.IdUtils;
import project.mall.utils.MallPageInfo;
import project.mall.utils.MallPageInfoUtil;
import project.mall.version.MallClientSeller;
import project.party.PartyService;
import project.party.model.Party;
import project.party.model.UserRecom;
import project.redis.RedisHandler;
import project.syspara.SysParaCode;
import project.syspara.Syspara;
import project.syspara.SysparaService;
import project.user.UserData;
import project.user.UserDataService;
import project.wallet.Wallet;
import project.wallet.WalletService;
import project.web.api.dto.IntegratedScoreDto;
import util.DateUtil;
import util.RandomUtil;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.*;

@Slf4j
public class GoodsOrdersServiceImpl extends HibernateDaoSupport implements GoodsOrdersService {

    public static final String ADDRESS_DESENSITIZATION = "address_desensitization";
    private static final String DESENSITIZATION_STR = "*****";
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private WalletService walletService;

    private MoneyLogService moneyLogService;

    private OrderLogService orderLogService;

    private LogService logService;

    private PartyService partyService;

    private RedisHandler redisHandler;

    private UserDataService userDataService;

    private AdminSellerService adminSellerService;

    private SellerGoodsService sellerGoodsService;

    private SysparaService sysparaService;

    private PagedQueryDao pagedQueryDao;

    private ComboService comboService;
    private AdminSystemCommentService adminSystemCommentService;
    private EvaluationService evaluationService;
    //    private CommonNotifyManager commonNotifyManager;
    private CommonNotifyService commonNotifyService;

    private MallAddressAreaService mallAddressAreaService;

    private GoodsSkuAtrributionService goodsSkuAtrributionService;

    private MallLevelService mallLevelService;


    private JdbcTemplate jdbcTemplate;

    // 当前服务运行在 data 服务中，所以不要使用 rpc 的客户端bean
    private NotificationHelperClient notificationHelperClient;
    //private NotificationHelper notificationHelper;

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void saveAddress(String partyId, int use, String phone, String email, String postcode, String contacts,
                            String country, String province, String city, String address, int countryId, int provinceId, int cityId) {
        Criteria criteria = getHibernateTemplate().getSessionFactory().getCurrentSession().createCriteria(MallAddress.class);
        criteria.add(Restrictions.eq("partyId", partyId));
        criteria.setProjection(Projections.rowCount());
        Integer totalCount = ((Long) criteria.uniqueResult()).intValue();
        if (totalCount == null) {
            totalCount = 0;
        }
        if (totalCount > 5) {
            throw new BusinessException("收货地址达上限");
        }

        if (use == 1) {
            for (MallAddress b : getAddressUse(partyId)) {
                b.setStatus(0);
                getHibernateTemplate().update(b);
            }
        }

        MallAddress addRess = new MallAddress();
        addRess.setPartyId(partyId);
        addRess.setAddress(address);
        addRess.setPhone(phone);
        addRess.setContacts(contacts);
        addRess.setStatus(use);
        addRess.setCreateTime(new Date());
        addRess.setEmail(email);
        addRess.setPostcode(postcode);
        addRess.setCountry(country);
        addRess.setProvince(province);
        addRess.setCity(city);
        addRess.setCountryId(countryId);
        addRess.setProvinceId(provinceId);
        addRess.setCityId(cityId);
        getHibernateTemplate().save(addRess);
    }

    @Override
    public void updateAddress(String id, String partyId, int use, String phone, String email, String postcode, String contacts,
                              String country, String province, String city, String address, int countryId, int provinceId, int cityId) {
        MallAddress mallAddress = this.getHibernateTemplate().get(MallAddress.class, id);
        if (mallAddress == null || !partyId.equals(mallAddress.getPartyId())) {
            throw new BusinessException("地址不存在,或者已删除");
        }
        mallAddress.setStatus(use);
        mallAddress.setAddress(address);
        mallAddress.setPhone(phone);
        mallAddress.setContacts(contacts);
        mallAddress.setEmail(email);
        mallAddress.setPostcode(postcode);
        mallAddress.setCountry(country);
        mallAddress.setProvince(province);
        mallAddress.setCity(city);
        mallAddress.setCountryId(countryId);
        mallAddress.setProvinceId(provinceId);
        mallAddress.setCityId(cityId);
        this.getHibernateTemplate().update(mallAddress);
        if (use == 1) {
            for (MallAddress b : getAddressUse(partyId)) {
                if (id.equals(b.getId())) {
                    continue;
                }
                b.setStatus(0);
                getHibernateTemplate().update(b);
            }
        }


    }

    @Override
    public void removeAddress(String id) {
        MallAddress address = this.getHibernateTemplate().get(MallAddress.class, id);
        if (address == null) {
            throw new BusinessException("地址不存在,或者已删除");
        }
        getHibernateTemplate().delete(address);
    }

    @Override
    public List<MallAddress> listAddress(String partyId) {
        DetachedCriteria query = DetachedCriteria.forClass(MallAddress.class);
        query.add(Property.forName("partyId").eq(partyId));
        query.addOrder(Order.desc("status"));
        query.addOrder(Order.desc("createTime"));
        return (List<MallAddress>) getHibernateTemplate().findByCriteria(query, 0, 10);
    }

    @Override
    public List<MallAddress> getAddressUse(String partyId) {
        DetachedCriteria query = DetachedCriteria.forClass(MallAddress.class);
        query.add(Property.forName("partyId").eq(partyId));
        query.add(Property.forName("status").eq(1));
        return (List<MallAddress>) getHibernateTemplate().findByCriteria(query, 0, 10);
    }

    private MallAddress getMallAddress(String id) {
        MallAddress address = this.getHibernateTemplate().get(MallAddress.class, id);
        return address;
    }


    /**
     * 获取店铺评分  今天订单数 今日销售额  今日利润
     *
     * @param sellerId
     */
    public IntegratedScoreDto getIntegratedScoreCount(String sellerId) {
        Date now = new Date();
        String startTime = DateUtil.formatDate(DateUtil.minDate(now), DateUtil.DATE_FORMAT_FULL);
        String endTime = DateUtil.formatDate(DateUtil.maxDate(now), DateUtil.DATE_FORMAT_FULL);
        List list = jdbcTemplate.queryForList("SELECT SUM(PRIZE_REAL)  as 'todaySales' ,COUNT(*) as 'todayOrder',(SUM(PROFIT))  as 'todayProfit'  FROM T_MALL_ORDERS_PRIZE WHERE SELLER_ID='" +
                sellerId + "'  AND  STATUS IN(1,2,3,4,5) AND CREATE_TIME BETWEEN '" + startTime + "' AND '" + endTime + "'");
        Iterator iterable = list.iterator();

        IntegratedScoreDto integratedScoreDto = new IntegratedScoreDto();
        if (iterable.hasNext()) {
            Map rowrMap = (Map) iterable.next();
            integratedScoreDto.setTodayOrder(Integer.valueOf(rowrMap.getOrDefault("todayOrder", "0").toString()));
            Object todaySalesO = rowrMap.get("todaySales");
            Object todayProfitO = rowrMap.get("todayProfit");
            if (todaySalesO != null) {
                integratedScoreDto.setTodaySales(new BigDecimal(todaySalesO.toString()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
            }
            if (todayProfitO != null) {
                integratedScoreDto.setTodayProfit(new BigDecimal(todayProfitO.toString()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
            }

        }
        return integratedScoreDto;
    }

    @Override
    public Float selectAvgEvaluationBySellerId(String sellerId) {
        return evaluationService.selectAvgEvaluationBySellerId(sellerId);
    }

    // 修改了原始实现，等稳定后移除该代码
//    public JSONArray saveOrderSubmit0(String partyId, String orderInfo, String addressId) {
//        MallAddress address = null;
//        if (StringUtils.isEmptyString(addressId)) {
//            List<MallAddress> addressList = getAddressUse(partyId);
//            if (addressList.size() > 0) {
//                address = addressList.get(0);
//            }
//        } else {
//            address = getMallAddress(addressId);
//        }
//        if (address == null) {
//            throw new BusinessException("请选择收货地址");
//        }
//        JSONArray jsonArray = new JSONArray();
//        Party party = partyService.cachePartyBy(partyId, false);
//        // 每个 goodsId 下，每个 skuId分组 下的最终商品数量
//        Map<String, Map<String, Integer>> goodsIdSkuIdNums = goodsIdSkuIdNums(orderInfo);
//        // 每个商家下，每个商品分组（基于 goodsId 分组，不同于上面的基于 skuId 分组）下的商品数量
//        Map<String, Map<String, Integer>> orderMap = new HashMap<>();
//        // sellerGoodsId 到 sellerGoods 的映射
//        Map<String, SellerGoods> sellerGoodsMap = new HashMap<>();
//
//        for (String goodsId : goodsIdSkuIdNums.keySet()) {
//            Integer goodsNum = goodsIdSkuIdNums.get(goodsId).values().stream().reduce(Integer::sum).orElse(0);
//
//            SellerGoods sellerGoods = getSellerGoods(goodsId);
//            if (sellerGoods == null) {
//                throw new BusinessException("部分商品已下架");
//            }
//            if (sellerGoods != null && "1".equals(sellerGoods.getIsShelf())) {
//                throw new BusinessException("部分商品已下架");
//            }
//            sellerGoodsMap.put(goodsId, sellerGoods);
//            int buyMin = sellerGoods.getBuyMin() == null ? 0 : sellerGoods.getBuyMin();
//            Syspara buyMax_para = sysparaService.find("mall_max_goods_number_in_order");
//            if (goodsNum < buyMin) {
//                throw new BusinessException("少于最小采购数量");
//            }
//            if (Objects.nonNull(buyMax_para)) {
//                int buyMax = buyMax_para.getInteger();
//                if (goodsNum > buyMax){
//                    throw new BusinessException("大于最大采购数量");
//                }
//            }
//            String sellerId = sellerGoods.getSellerId();
//            if (orderMap.containsKey(sellerId)) {
//                Map<String, Integer> goodsMap = orderMap.get(sellerId);
//                if (goodsMap.containsKey(goodsId)) {
//                    goodsMap.put(goodsId, goodsNum + goodsMap.get(goodsId));
//                } else {
//                    goodsMap.put(goodsId, goodsNum);
//                }
//
//            } else {
//                orderMap.put(sellerId, new HashMap<>());
//                orderMap.get(sellerId).put(goodsId, goodsNum);
//            }
//        }
//
//        for (String sellerId : orderMap.keySet()) {
//            MallOrdersPrize orders = new MallOrdersPrize();
//            orders.setId(IdUtils.getOrderNum());
//            orders.setPartyId(partyId);
//            orders.setUserCode(party.getUsercode());
//            orders.setSellerId(sellerId);
//            orders.setSellerName(getSeller(sellerId).getName());
//            orders.setStatus(0);
//            orders.setCreateTime(new Date());
//            orders.setAddress(address.getAddress());
//            orders.setPhone(address.getPhone());
//            orders.setContacts(address.getContacts());
//            orders.setEmail(address.getEmail());
//            orders.setPostcode(address.getPostcode());
//            orders.setCountry(address.getCountry());
//            orders.setProvince(address.getProvince());
//            orders.setCity(address.getCity());
//
//            if (party.getRolename().contains(Constants.SECURITY_ROLE_GUEST)) {
//                orders.setOrderStatus(1);
//            } else {
//                orders.setOrderStatus(0);
//            }
//
//            getHibernateTemplate().save(orders);
//
//            Map<String, Integer> goodsMap = orderMap.get(sellerId);
//            int sort = 0;
//            double prizeOriginal = 0;
//            double goodsReal = 0;
//            double systemPrice = 0;
//            int goodsCount = 0;
//            double fees = 0;
//            double tax = 0;
//            for (String googsId : goodsMap.keySet()) {
//                SellerGoods sellerGoods = sellerGoodsMap.get(googsId);
//                SystemGoods systemGoods = sellerGoods.getSystemGoods();
//                // 同一个goods对根据skuId拆单
//                Map<String, Integer> skuIdNums = goodsIdSkuIdNums.get(googsId);
//                for (String skuId : skuIdNums.keySet()) {
//                    MallOrdersGoods goods = new MallOrdersGoods();
//                    goods.setGoodsId(googsId);
//                    goods.setSkuId(skuId);
//                    SellerGoodsSku sellerGoodSku = sellerGoodsService.findSellerGoodSku(sellerGoods, skuId);
//                    goods.setGoodsNum(skuIdNums.get(skuId));
//                    goodsCount += goods.getGoodsNum();
//                    goods.setOrderId(orders.getId().toString());
//                    goods.setGoodsSort(sort);
//                    goods.setFees(systemGoods.getFreightAmount());
//                    if (systemGoods.getGoodsTax() != null) {
//                        goods.setTax(systemGoods.getGoodsTax());
//                    }
//                    Double sellingPrice = sellerGoodSku.getSellingPrice();
//                    Double discountPrice = sellerGoodSku.getDiscountPrice();
//                    goods.setGoodsPrize(sellingPrice);
//                    goods.setGoodsReal(null == discountPrice ? sellingPrice : discountPrice);
//                    Optional.ofNullable(sellerGoodSku.getSystemPrice()).ifPresent(goods::setSystemPrice);
//                    Optional.ofNullable(systemGoods.getId()).ifPresent(x -> goods.setSystemGoodsId(x.toString()));
//                    Optional.ofNullable(systemGoods.getFreightAmount()).ifPresent(goods::setFees);
//                    Optional.ofNullable(systemGoods.getGoodsTax()).ifPresent(goods::setTax);
//                    getHibernateTemplate().save(goods);
////                    sellerGoods.setSoldNum(sellerGoods.getSoldNum() + goods.getGoodsNum());
////                    updateSellerGoods(sellerGoods);
//                    sort += 1;
//                    Double count = Double.valueOf(goods.getGoodsNum());
//                    double orderRealPrize = Arith.mul(goods.getGoodsReal(), null == count ? 1.0D : count);
//                    double orderGoodsPrize = Arith.mul(goods.getGoodsPrize(), null == count ? 1.0D : count);
//                    double orderSystemPrize = Arith.mul(goods.getSystemPrice(), null == count ? 1.0D : count);
//                    double orderTaxPrize = Arith.mul(goods.getTax(), null == count ? 1.0D : count);
//                    double orderFee = Arith.mul(goods.getFees(), null == count ? 1.00 : count);
//                    prizeOriginal = Arith.add(prizeOriginal, orderGoodsPrize);
//                    goodsReal = Arith.add(goodsReal, orderRealPrize);
//                    systemPrice = Arith.add(systemPrice, orderSystemPrize);
//                    fees = Arith.add(fees, orderFee);
//                    tax = Arith.add(tax, orderTaxPrize);
//                }
//            }
//            orders.setFees(fees);
//            orders.setTax(tax);
//            orders.setPrizeOriginal(prizeOriginal);
//            orders.setPrizeReal(goodsReal);
//            orders.setSystemPrice(systemPrice);
//            orders.setGoodsCount(goodsCount);
//            orders.setProfitStatus(0);
//            orders.setProfit(Arith.sub(goodsReal, systemPrice));
//            this.saveOrderLog(partyId, orders.getId().toString(), OrderStatusEnum.ORDER_SUCCESS, "订单" + orders.getId().toString() + "下单成功，等待付款");
//
//            getHibernateTemplate().update(orders);
//
//            JSONObject o = new JSONObject();
//            o.put("orderId", orders.getId().toString());
//            o.put("prizeReal", orders.getPrizeReal());
//            o.put("fees", orders.getFees());
//            o.put("tax", orders.getTax());
//            jsonArray.add(o);
//
//            // 通知商家有人创建订单
//            // notifySellerWithCreateOrder(orders);
//            try {
//                notificationHelperClient.notifySellerWithCreateOrder(orders, 7);
//            } catch (Exception e) {
//                logger.error("发送通知消息提醒商家下单事件报错:", e);
//            }
//        }
//
//        return jsonArray;
//    }

    @Override
    public JSONArray saveOrderSubmit(String partyId, String orderInfo, String addressId) {
        MallAddress address = null;
        if (StringUtils.isEmptyString(addressId)) {
            List<MallAddress> addressList = getAddressUse(partyId);
            if (addressList.size() > 0) {
                address = addressList.get(0);
            }
        } else {
            address = getMallAddress(addressId);
        }
        if (address == null || 0 == address.getCountryId() || Objects.isNull(address.getCountry())) {
            throw new BusinessException("请选择收货地址");
        }
        JSONArray jsonArray = new JSONArray();
        Party party = partyService.cachePartyBy(partyId, false);
        // 每个 goodsId 下，每个 skuId分组 下的最终商品数量
        Map<String, Map<String, Integer>> goodsIdSkuIdNums = goodsIdSkuIdNums(orderInfo);
        // 每个商家下，每个商品分组（基于 goodsId 分组，不同于上面的基于 skuId 分组）下的商品数量
        Map<String, Map<String, Integer>> orderMap = new HashMap<>();
        // sellerGoodsId 到 sellerGoods 的映射
        Map<String, SellerGoods> sellerGoodsMap = new HashMap<>();

        for (String goodsId : goodsIdSkuIdNums.keySet()) {
            Integer goodsNum = goodsIdSkuIdNums.get(goodsId).values().stream().reduce(Integer::sum).orElse(0);

            SellerGoods sellerGoods = getSellerGoods(goodsId);
            if (sellerGoods == null) {
                throw new BusinessException("部分商品已下架");
            }
            if (sellerGoods != null && 0 == sellerGoods.getIsShelf()) {
                throw new BusinessException("部分商品已下架");
            }
            sellerGoodsMap.put(goodsId, sellerGoods);
            // 如果 sellerGoods 表的 buyMin 字段废弃，可以从 systemGoods 里面取
            int buyMin = sellerGoods.getBuyMin() == null ? 1 : sellerGoods.getBuyMin();
            Syspara globalBuyMinInfo = this.sysparaService.find(SysParaCode.GOODS_BUY_MIN.getCode());
            if (globalBuyMinInfo != null && StrUtil.isNotBlank(globalBuyMinInfo.getValue())) {
                // 选取最大值
                Integer globalBuyMin = Integer.parseInt(globalBuyMinInfo.getValue());
                if (globalBuyMin > goodsNum) {
                    buyMin = globalBuyMin;
                }
            }
            if (goodsNum < buyMin) {
                throw new BusinessException("少于最小采购数量");
            }

            Syspara buyMax_para = sysparaService.find("mall_max_goods_number_in_order");
            if (Objects.nonNull(buyMax_para)) {
                int buyMax = buyMax_para.getInteger();
                if (goodsNum > buyMax) {
                    throw new BusinessException("大于最大采购数量");
                }
            }

            String sellerId = sellerGoods.getSellerId();
            if (partyId.equals(sellerId)) {
                throw new BusinessException("无法购买本店商品");
            }
            if (orderMap.containsKey(sellerId)) {
                Map<String, Integer> goodsMap = orderMap.get(sellerId);
                if (goodsMap.containsKey(goodsId)) {
                    goodsMap.put(goodsId, goodsNum + goodsMap.get(goodsId));
                } else {
                    goodsMap.put(goodsId, goodsNum);
                }

            } else {
                orderMap.put(sellerId, new HashMap<>());
                orderMap.get(sellerId).put(goodsId, goodsNum);
            }
        }

        for (String sellerId : orderMap.keySet()) {
            Map<String, Integer> goodsCountMap = orderMap.get(sellerId);
            Map<String, Integer> goodsSkuCountMap = new HashMap<>();
            // goodsIdSkuIdNums 里的信息属于多个商铺的销售信息，现在需要提取针对当前商家的 sku 信息
            for (String currentOrderGoodsId : goodsCountMap.keySet()) {
                Map<String, Integer> currentGoodsSkuCountMap = goodsIdSkuIdNums.get(currentOrderGoodsId);
                if (currentGoodsSkuCountMap != null) {
                    goodsSkuCountMap.putAll(currentGoodsSkuCountMap);
                }
            }
            MallOrdersPrize orders = createOneOrder(party, address, sellerId, sellerGoodsMap, goodsCountMap, goodsIdSkuIdNums);

            JSONObject o = new JSONObject();
            o.put("orderId", orders.getId().toString());
            o.put("prizeReal", orders.getPrizeReal());
            o.put("fees", orders.getFees());
            o.put("tax", orders.getTax());
            jsonArray.add(o);

            // 通知商家有人创建订单
            // notifySellerWithCreateOrder(orders);
            try {
                notificationHelperClient.notifySellerWithCreateOrder(orders, 5);
            } catch (Exception e) {
                logger.error("发送通知消息提醒商家下单事件报错:", e);
            }
        }

        logger.info("---> GoodsOrdersServiceImpl.saveOrderSubmit 当前买家用户:{} 提交购物内容:{}, 返回订单结果:{}", partyId, orderInfo, jsonArray);
        return jsonArray;
    }

    @Override
    public MallOrderVO saveFakeOrder(String partyId, String fakeUserName, String sellerGoodsId) {
        MallOrderVO retOrder = new MallOrderVO();
        List<OrderGoodsVO> goodsList = new ArrayList();
        retOrder.setGoodsList(goodsList);

        OrderGoodsVO oneOrderGoods = new OrderGoodsVO();
        SellerGoodsSkuVO goodsSku = new SellerGoodsSkuVO();
        oneOrderGoods.setGoodsSku(goodsSku);
        goodsList.add(oneOrderGoods);

        SellerGoods sellerGoods = getSellerGoods(sellerGoodsId);
        if (sellerGoods == null) {
            throw new BusinessException("部分商品已下架");
        }
        if (sellerGoods != null && 0 == sellerGoods.getIsShelf()) {
            throw new BusinessException("部分商品已下架");
        }
        oneOrderGoods.setId(sellerGoods.getId().toString());
        if (sellerGoods.getSystemGoods() != null) {
            oneOrderGoods.setSystemGoodsId(sellerGoods.getSystemGoods().getId().toString());
        }
        oneOrderGoods.setCategoryId(sellerGoods.getCategoryId());
        oneOrderGoods.setSecondaryCategoryId(sellerGoods.getSecondaryCategoryId());
        oneOrderGoods.setSellerId(sellerGoods.getSellerId());
        oneOrderGoods.setSoldNum(1);

        // 选择一个随机地址
        MallAddress address = getRdmAddress();
        retOrder.setCountryId(address.getCountryId());
        retOrder.setLang(address.getLanguage());

        Party party = null;
        if (StrUtil.isNotEmpty(partyId) && !Objects.equals(partyId, "0")) {
            party = partyService.cachePartyBy(partyId, false);
        } else {
            // 构造虚假用户
            party = new Party();
            party.setId("0");
            party.setUsername(fakeUserName);
            party.setName(fakeUserName);
            //party.setAvatar();
            party.setUsercode("0");
            party.setRolename(Constants.SECURITY_ROLE_GUEST);
        }
        retOrder.setPartyId("0");
        if (party.getId() != null) {
            retOrder.setPartyId(party.getId().toString());
        }

        String sellerId = sellerGoods.getSellerId();

        // 随机商品 SKU 信息
        String skuId = "0";
        String language = address.getLanguage();
        if (StrUtil.isBlank(language)) {
            language = "en";
        }
        GoodSkuAttrDto goodsSkuAttrInfo = goodsSkuAtrributionService.getGoodsAttrListSku(sellerGoodsId, language);
        if (goodsSkuAttrInfo != null) {
            List<SkuDto> skuList = goodsSkuAttrInfo.getSkus();
            if (CollectionUtil.isNotEmpty(skuList)) {
                Random skuRdm = new Random();
                int skuRdmIdx = skuRdm.nextInt(skuList.size());
                skuId = skuList.get(skuRdmIdx).getSkuId();
            } else {
                log.error("======> createFakeOrder 商品:" + sellerGoodsId + ", 在语言版本:" + language + " 下没有 sku 属性详情记录");
            }
        } else {
            log.error("======> createFakeOrder 商品:" + sellerGoodsId + ", 在语言版本:" + language + " 下没有 sku 记录");
        }
        goodsSku.setId(skuId);
        goodsSku.setLang(language);

        // 根据最新需求，创建虚假评论不会产生虚假订单记录， caster 改于 2023-4-5
//        // 每个商品分组（基于 goodsId 分组，不同于上面的基于 skuId 分组）下的商品数量
//        Map<String, Integer> goodsSkuCountMap = new HashMap<>();
//        Map<String, Integer> goodsCountMap = new HashMap<>();
//        // sellerGoodsId 到 sellerGoods 的映射
//        Map<String, SellerGoods> sellerGoodsMap = new HashMap<>();
//        sellerGoodsMap.put(sellerGoodsId, sellerGoods);
//
//        Integer goodsNum = 1;
//        int buyMin = sellerGoods.getBuyMin() == null ? 0 : sellerGoods.getBuyMin();
//        //Syspara buyMax_para = sysparaService.find("mall_max_goods_number_in_order");
//        goodsNum = buyMin;
//
//        goodsSkuCountMap.put(skuId, goodsNum);
//        goodsCountMap.put(sellerGoodsId, goodsNum);
//
//        // 模拟下单
//        MallOrdersPrize orders = createOneOrder(party, address, sellerId, sellerGoodsMap, goodsCountMap, goodsSkuCountMap);
//        // 将订单设置为完成状态 TODO
//        orders.setOrderStatus(1);// 虚拟订单
//        orders.setPayStatus(1);
//        orders.setProfitStatus(1);
//        orders.setPurchStatus(1);
//        orders.setStatus(OrderStatusEnum.ORDER_SEND_CONFIRM.getCode());
//        getHibernateTemplate().update(orders);
//
//        return orders;

        retOrder.setId("0");
        return retOrder;
    }

    private MallOrdersPrize createOneOrder(Party party, MallAddress address, String sellerId,
                                           Map<String, SellerGoods> sellerGoodsMap,
                                           Map<String, Integer> goodsCountMap,
                                           Map<String, Map<String, Integer>> goodsIdSkuIdNums) {
        if (CollectionUtil.isEmpty(sellerGoodsMap)) {
            log.error("======> createOneOrder sellerGoodsMap 值为空");
        }
        if (CollectionUtil.isEmpty(goodsCountMap)) {
            log.error("======> createOneOrder goodsCountMap 值为空");
        }

        String partyId = party.getId().toString();
        Party sellerParty = partyService.cachePartyBy(sellerId, false);
        Seller seller = getSeller(sellerId);
        String mallLevel = seller.getMallLevel();
        List<MallLevel> mallLevels = mallLevelService.listLevel();
        double sellerDiscount = 0D;
        if (StringUtils.isNotEmpty(mallLevel) && CollectionUtils.isNotEmpty(mallLevels)) {
            for (MallLevel level : mallLevels) {
                if (level.getLevel().equals(mallLevel)) {
                    sellerDiscount = level.getSellerDiscount();
                }
            }
        }
        MallOrdersPrize orders = new MallOrdersPrize();
        orders.setId(IdUtils.getOrderNum());
        orders.setPartyId(partyId);
        orders.setUserCode(party.getUsercode());
        orders.setSellerId(sellerId);
        orders.setSellerName(seller.getName());
        orders.setSellerDiscount(sellerDiscount);
        orders.setStatus(0);
        orders.setCreateTime(new Date());
        orders.setAddress(address.getAddress());
        orders.setPhone(address.getPhone());
        orders.setContacts(address.getContacts());
        orders.setEmail(address.getEmail());
        orders.setPostcode(address.getPostcode());
        orders.setCountry(address.getCountry());
        orders.setProvince(address.getProvince());
        orders.setCity(address.getCity());
        orders.setCountryId(address.getCountryId());
        orders.setProvinceId(address.getProvinceId());
        orders.setCityId(address.getCityId());

        if (party.getRolename().contains(Constants.SECURITY_ROLE_GUEST)) {
            orders.setOrderStatus(1);
        } else {
            orders.setOrderStatus(0);
        }

        // 订单特殊标记：1-买家是演示账号，2-卖家是演示账号，3-买家和卖家都是演示账号
        orders.setFlag(0);
        // 左侧为高位，右侧为低位
        // 买家用户账号类型标记
        int buyerFlag = 0B00;
        // 卖家用户账号类型标记
        int sellerFlag = 0B00;

        if (sellerParty.getRolename().contains(Constants.SECURITY_ROLE_GUEST)) {
            // 卖家是演示账号
            sellerFlag = 0B10;
        }
        if (party.getRolename().contains(Constants.SECURITY_ROLE_GUEST)) {
            // 买家是演示账号
            buyerFlag = 0B01;
        }
        orders.setFlag(buyerFlag | sellerFlag);

        getHibernateTemplate().save(orders);

        int sort = 0;
        double prizeOriginal = 0;
        double goodsReal = 0;
        double systemPrice = 0;
        int goodsCount = 0;
        double fees = 0;
        double tax = 0;
        for (String googsId : goodsCountMap.keySet()) {
            SellerGoods sellerGoods = sellerGoodsMap.get(googsId);
            SystemGoods systemGoods = sellerGoods.getSystemGoods();
            // 同一个goods对根据skuId拆单
            Map<String, Integer> skuIdNums = goodsIdSkuIdNums.get(googsId);//此Map为同一个goodId下的多个skuId-num
            for (String skuId : skuIdNums.keySet()) {
                MallOrdersGoods goods = new MallOrdersGoods();
                goods.setGoodsId(googsId);
                goods.setSkuId(skuId);
                // 取代 TODO
                SellerGoodsSkuInfoVO sellerGoodSku = sellerGoodsService.findSellerGoodSku(sellerGoods, skuId);
                //SellerGoodsSku sellerGoodSku = sellerGoodsService.findSellerGoodSku(sellerGoods, skuId);
                goods.setGoodsNum(skuIdNums.get(skuId));
                goodsCount += goods.getGoodsNum();
                goods.setOrderId(orders.getId().toString());
                goods.setGoodsSort(sort);
                goods.setFees(systemGoods.getFreightAmount());
                if (systemGoods.getGoodsTax() != null) {
                    goods.setTax(systemGoods.getGoodsTax());
                }
                Double sellingPrice = sellerGoodSku.getSellingPrice();
                Double discountPrice = sellerGoodSku.getDiscountPrice();
//                此处必须保留俩位小数，否则有精度问题
                goods.setGoodsPrize(Arith.roundDown(sellingPrice, 2));
                goods.setGoodsReal(Arith.roundDown(Objects.isNull(discountPrice) || discountPrice == 0.0D ? sellingPrice : discountPrice, 2));
                Optional.ofNullable(sellerGoodSku.getSystemPrice()).ifPresent(goods::setSystemPrice);
                Optional.ofNullable(systemGoods.getId()).ifPresent(x -> goods.setSystemGoodsId(x.toString()));
                Optional.ofNullable(systemGoods.getFreightAmount()).ifPresent(goods::setFees);
                Optional.ofNullable(systemGoods.getGoodsTax()).ifPresent(goods::setTax);

                getHibernateTemplate().save(goods);
//              sellerGoods.setSoldNum(sellerGoods.getSoldNum() + goods.getGoodsNum());
//              updateSellerGoods(sellerGoods);

                sort += 1;
                Double count = Double.valueOf(goods.getGoodsNum());
                double orderRealPrize = Arith.mul(goods.getGoodsReal(), null == count ? 1.0D : count);
                double orderGoodsPrize = Arith.mul(goods.getGoodsPrize(), null == count ? 1.0D : count);
                double orderSystemPrize = Arith.mul(goods.getSystemPrice(), null == count ? 1.0D : count);
                double orderTaxPrize = Arith.mul(goods.getTax(), null == count ? 1.0D : count);
                double orderFee = Arith.mul(goods.getFees(), null == count ? 1.00 : count);
                prizeOriginal = Arith.add(prizeOriginal, orderGoodsPrize);
                goodsReal = Arith.add(goodsReal, orderRealPrize);
                systemPrice = Arith.add(systemPrice, orderSystemPrize);
                fees = Arith.add(fees, orderFee);
                tax = Arith.add(tax, orderTaxPrize);
            }
        }

        orders.setFees(BigDecimal.valueOf(fees).setScale(2, BigDecimal.ROUND_DOWN).doubleValue());
        orders.setTax(BigDecimal.valueOf(tax).setScale(2, BigDecimal.ROUND_DOWN).doubleValue());
        orders.setPrizeOriginal(BigDecimal.valueOf(prizeOriginal).setScale(2, BigDecimal.ROUND_DOWN).doubleValue());
        orders.setPrizeReal(BigDecimal.valueOf(goodsReal).setScale(2, BigDecimal.ROUND_DOWN).doubleValue());
        orders.setSystemPrice(BigDecimal.valueOf(systemPrice).setScale(2, BigDecimal.ROUND_DOWN).doubleValue());
        orders.setGoodsCount(goodsCount);
        orders.setProfitStatus(0);
        orders.setProfit(new BigDecimal(Double.toString(goodsReal)).setScale(2,BigDecimal.ROUND_DOWN)
                .subtract(new BigDecimal(Double.toString(systemPrice)).setScale(2,BigDecimal.ROUND_DOWN)).setScale(2,BigDecimal.ROUND_DOWN).doubleValue());

        // 虚假订单生成 orderLog 记录是否会有副作用？ caster 添加
        if (StrUtil.isNotBlank(partyId) && !Objects.equals(partyId, "0")) {
            this.saveOrderLog(partyId, orders.getId().toString(), OrderStatusEnum.ORDER_SUCCESS, "订单" + orders.getId().toString() + "下单成功，等待付款");
        }

        getHibernateTemplate().update(orders);

        return orders;
    }

    private MallAddress getRdmAddress() {
        MallAddress address = new MallAddress();
        List<MallCountry> allCountry = mallAddressAreaService.listAllCountry();
        Random countryRdm = new Random();
        String language = "en";
        int countryRdmIdx = countryRdm.nextInt(allCountry.size());
        MallCountry country = allCountry.get(countryRdmIdx);
        if (country.getCountryNameEn().equalsIgnoreCase("china")) {
            language = "cn";
        }
        address.setCountryId(Integer.parseInt(country.getId().toString()));
        address.setLanguage(language);

        List<MallState> stateList = mallAddressAreaService.listAllState(Long.parseLong(country.getId().toString()));
        MallState rdmState = null;
        MallCity rdmCity = null;
        if (CollectionUtil.isNotEmpty(stateList)) {
            Random stateRdm = new Random();
            int stateRdmIdx = stateRdm.nextInt(stateList.size());
            rdmState = stateList.get(stateRdmIdx);
        }
        if (rdmState != null) {
            List<MallCity> cityList = mallAddressAreaService.listAllCity(Long.parseLong(rdmState.getId().toString()));
            if (CollectionUtil.isNotEmpty(cityList)) {
                Random cityRdm = new Random();
                int cityRdmIdx = cityRdm.nextInt(cityList.size());
                rdmCity = cityList.get(cityRdmIdx);
            }
        }

        if (language.equals("en")) {
            address.setCountry(country.getCountryNameEn());
            if (rdmState == null) {
                address.setProvince("none");
            } else {
                address.setProvince(rdmState.getStateNameEn());
            }
            if (rdmCity == null) {
                address.setCity("none");
            } else {
                address.setCity(rdmCity.getCityNameEn());
            }
        } else if (language.equals("cn")) {
            address.setCountry(country.getCountryNameCn());
            if (rdmState == null) {
                address.setProvince("暂无");
            } else {
                address.setProvince(rdmState.getStateNameCn());
            }
            if (rdmCity == null) {
                address.setCity("暂无");
            } else {
                address.setCity(rdmCity.getCityNameCn());
            }
        }

        String fullAddress = address.getCountry() + " " + address.getProvince() + " " + address.getCity();
        if (language.equals("cn")) {
            address.setAddress(fullAddress + " 匿名地址");
        } else {
            address.setAddress(fullAddress + " Anonymous address");
        }

        return address;
    }

    public void updateSellerGoods(SellerGoods sellerGoods) {
        getHibernateTemplate().update(sellerGoods);
    }


    @Override
    @Transactional
    public JSONArray saveGoodsBuy(String partyId, String uuid, String num) {
        Map<String, Map<String, Integer>> goodsIdSkuIdNums = goodsIdSkuIdNums(uuid, num);

        MallAddress address = null;
        List<MallAddress> addressList = getAddressUse(partyId);
        if (addressList.size() > 0) {
            address = addressList.get(0);
        }
        if (address == null || 0 == address.getCountryId() || Objects.isNull(address.getCountry())) {
            throw new BusinessException("请选择收货地址");
        }
        JSONArray jsonArray = new JSONArray();
        Party party = partyService.cachePartyBy(partyId, false);

        Map<String, Map<String, Integer>> orderMap = new HashMap<>();

        Map<String, SellerGoods> sellerGoodsMap = new HashMap<>();
        Date now = new Date();
        for (String goodsId : goodsIdSkuIdNums.keySet()) {

            Integer goodsNum = goodsIdSkuIdNums.get(goodsId).values().stream().reduce(Integer::sum).orElse(0);

            SellerGoods sellerGoods = getSellerGoods(goodsId);
            if (sellerGoods == null) {
                throw new BusinessException("部分商品已下架");
            }
            if (sellerGoods != null && 0 == sellerGoods.getIsShelf()) {
                throw new BusinessException("部分商品已下架");
            }
            sellerGoodsMap.put(goodsId, sellerGoods);

            String sellerId = sellerGoods.getSellerId();
            if (orderMap.containsKey(sellerId)) {
                Map<String, Integer> goodsMap = orderMap.get(sellerId);
                if (goodsMap.containsKey(goodsId)) {
                    goodsMap.put(goodsId, goodsNum + goodsMap.get(goodsId));
                } else {
                    goodsMap.put(goodsId, goodsNum);
                }

            } else {
                orderMap.put(sellerId, new HashMap<>());
                orderMap.get(sellerId).put(goodsId, goodsNum);
            }

        }


        for (String sellerId : orderMap.keySet()) {
            MallOrdersPrize orders = new MallOrdersPrize();
            orders.setId(IdUtils.getOrderNum());
            orders.setPartyId(partyId);
            orders.setUserCode(party.getUsercode());
            orders.setSellerId(sellerId);

            Seller seller = getSeller(sellerId);
            String mallLevel = seller.getMallLevel();
            List<MallLevel> mallLevels = mallLevelService.listLevel();
            double sellerDiscount = 0D;
            if (StringUtils.isNotEmpty(mallLevel) && CollectionUtils.isNotEmpty(mallLevels)) {
                for (MallLevel level : mallLevels) {
                    if (level.getLevel().equals(mallLevel)) {
                        sellerDiscount = level.getSellerDiscount();
                    }
                }
            }

            orders.setSellerName(seller.getName());
            orders.setSellerDiscount(sellerDiscount);
            //orders.setStatus(0);
            orders.setStatus(1);
            orders.setPayStatus(1);
            orders.setPayTime(now);
            orders.setOrderStatus(1);
            orders.setCreateTime(now);
            orders.setAddress(address.getAddress());
            orders.setPhone(address.getPhone());
            orders.setContacts(address.getContacts());
            orders.setEmail(address.getEmail());
            orders.setPostcode(address.getPostcode());
            orders.setCountryId(address.getCountryId());
            orders.setProvinceId(address.getProvinceId());
            orders.setCityId(address.getCityId());
            orders.setCountry(address.getCountry());
            orders.setProvince(address.getProvince());
            orders.setCity(address.getCity());

            Party sellerParty = partyService.cachePartyBy(sellerId, false);
            // 订单特殊标记：1-买家是演示账号，2-卖家是演示账号，3-买家和卖家都是演示账号
            orders.setFlag(0);
            // 左侧为高位，右侧为低位
            // 买家用户账号类型标记
            // 买家是演示账号
            int buyerFlag = 0B01;
            // 卖家用户账号类型标记
            int sellerFlag = 0B00;

            if (sellerParty.getRolename().contains(Constants.SECURITY_ROLE_GUEST)) {
                // 卖家是演示账号
                sellerFlag = 0B10;
            }
            orders.setFlag(buyerFlag | sellerFlag);

            getHibernateTemplate().save(orders);

            this.saveOrderLog(partyId, orders.getId(), OrderStatusEnum.ORDER_PAY_SUCCESS, "订单" + orders.getId().toString() + "下单成功，付款成功");

            Map<String, Integer> goodsMap = orderMap.get(sellerId);
            int sort = 0;
            double prizeOriginal = 0;
            double goodsReal = 0;
            double systemPrice = 0;
            int goodsCount = 0;
            double fees = 0;
            double tax = 0;
            for (String googsId : goodsMap.keySet()) {
                SellerGoods sellerGoods = sellerGoodsMap.get(googsId);

                //增加虚拟浏览量
                // 此举会触发事件在另外一个地方更新 sellerGoods 的一些字段值，所以需要重新查询一下，否则会覆盖部分字段值
                int random = RandomUtil.random(1, 6);

                sellerGoodsService.addRealViews(sellerId, sellerGoods.getId(), partyId, random);

                SystemGoods systemGoods = sellerGoods.getSystemGoods();
                // 同一个goods对根据skuId拆单
                Map<String, Integer> skuIdNums = goodsIdSkuIdNums.get(googsId);
                for (String skuId : skuIdNums.keySet()) {
                    // 取代 TODO
                    SellerGoodsSkuInfoVO sellerGoodSku = sellerGoodsService.findSellerGoodSku(sellerGoods, skuId);
                    //SellerGoodsSku sellerGoodSku = sellerGoodsService.findSellerGoodSku(sellerGoods, skuId);
                    MallOrdersGoods goods = new MallOrdersGoods();
                    goods.setGoodsId(googsId);
                    goods.setSkuId(skuId);
                    goods.setGoodsNum(skuIdNums.get(skuId));
                    goodsCount += goods.getGoodsNum();
                    goods.setOrderId(orders.getId().toString());
                    goods.setGoodsSort(sort);
                    goods.setFees(systemGoods.getFreightAmount() == null ? 0.0 : systemGoods.getFreightAmount());
                    goods.setTax(systemGoods.getGoodsTax() == null ? 0.0 : systemGoods.getGoodsTax());
                    goods.setGoodsPrize(BigDecimal.valueOf(sellerGoodSku.getSellingPrice()).setScale(2, BigDecimal.ROUND_DOWN).doubleValue());
//                    LocalDate nowDate = DateTimeTools.date2LocalDate(now);
//                    LocalDate startDate = DateTimeTools.date2LocalDate(sellerGoods.getDiscountStartTime());
//                    LocalDate endDate = DateTimeTools.date2LocalDate(sellerGoods.getDiscountEndTime());
                    if (sellerGoods.getDiscountPrice() != null
                            && sellerGoods.getDiscountStartTime() != null
                            && sellerGoods.getDiscountEndTime() != null
                            && now.after(sellerGoods.getDiscountStartTime())
                            && now.before(sellerGoods.getDiscountEndTime())) {
                        // 折扣期间, GoodsReal 填写的是折扣价，如果折扣过期，填写的是原价
                        goods.setGoodsReal(BigDecimal.valueOf(sellerGoodSku.getDiscountPrice()).setScale(2, BigDecimal.ROUND_DOWN).doubleValue());
                    } else {
                        goods.setGoodsReal(BigDecimal.valueOf(sellerGoodSku.getSellingPrice()).setScale(2, BigDecimal.ROUND_DOWN).doubleValue());
                    }
                    logger.info("---> 处理自动订单, 商品id:{}, skuId:{}, 原价，折扣价:{}", sellerGoods.getId(), skuId,
                            goods.getGoodsPrize(), goods.getGoodsReal());
                    goods.setSystemPrice(BigDecimal.valueOf(sellerGoodSku.getSystemPrice()).setScale(2, BigDecimal.ROUND_DOWN).doubleValue());
                    goods.setSystemGoodsId(systemGoods.getId());
                    getHibernateTemplate().save(goods);
//                    sellerGoods.setSoldNum(sellerGoods.getSoldNum() + goods.getGoodsNum());
//                    updateSellerGoods(sellerGoods);
                    sort += 1;
                    Double count = Double.valueOf(goods.getGoodsNum());
                    double orderRealPrize = Arith.mul(goods.getGoodsReal(), null == count ? 1.0D : count);
                    double orderGoodsPrize = Arith.mul(goods.getGoodsPrize(), null == count ? 1.0D : count);
                    double orderSystemPrize = Arith.mul(goods.getSystemPrice(), null == count ? 1.0D : count);
                    double orderTaxPrize = Arith.mul(goods.getTax(), null == count ? 1.0D : count);
                    double orderFee = Arith.mul(goods.getFees(), null == count ? 1.00 : count);
                    prizeOriginal = Arith.add(prizeOriginal, orderGoodsPrize);
                    goodsReal = Arith.add(goodsReal, orderRealPrize);
                    systemPrice = Arith.add(systemPrice, orderSystemPrize);
                    fees = Arith.add(fees, orderFee);
                    tax = Arith.add(tax, orderTaxPrize);
                }

            }
            orders.setFees(BigDecimal.valueOf(fees).setScale(2,BigDecimal.ROUND_DOWN).doubleValue());
            orders.setTax(BigDecimal.valueOf(tax).setScale(2, BigDecimal.ROUND_DOWN).doubleValue());
            orders.setPrizeOriginal(BigDecimal.valueOf(prizeOriginal).setScale(2, BigDecimal.ROUND_DOWN).doubleValue());
            orders.setPrizeReal(BigDecimal.valueOf(goodsReal).setScale(2, BigDecimal.ROUND_DOWN).doubleValue());
            orders.setSystemPrice(BigDecimal.valueOf(systemPrice).setScale(2, BigDecimal.ROUND_DOWN).doubleValue());
            orders.setGoodsCount(goodsCount);
            orders.setProfitStatus(0);
            orders.setProfit(BigDecimal.valueOf(goodsReal).setScale(2,BigDecimal.ROUND_DOWN)
                    .subtract(BigDecimal.valueOf(systemPrice).setScale(2,BigDecimal.ROUND_DOWN)).setScale(2,BigDecimal.ROUND_DOWN).doubleValue());
            getHibernateTemplate().update(orders);

            Wallet walletSeller = walletService.saveWalletByPartyId(orders.getPartyId());
            double money = walletSeller.getMoney();
            double prize = Arith.add(orders.getTax(), Arith.add(orders.getPrizeReal(), orders.getFees()));
            walletService.update(walletSeller.getPartyId().toString(), Arith.sub(0, prize));

            MoneyLog moneyLog = new MoneyLog();
            moneyLog.setCategory(Constants.MONEYLOG_CATEGORY_COIN);
            moneyLog.setAmount_before(money);
            moneyLog.setAmount(Arith.sub(0, prize));
            moneyLog.setAmount_after(BigDecimal.valueOf(Arith.sub(money, prize)).setScale(2, BigDecimal.ROUND_DOWN).doubleValue());

            moneyLog.setLog("批量下单[" + orders.getId() + "]");
            moneyLog.setPartyId(partyId);
            moneyLog.setWallettype(Constants.WALLET);
            moneyLog.setContent_type(Constants.MONEYLOG_CONTNET_PAY_ORDER);

            moneyLogService.save(moneyLog);

            JSONObject o = new JSONObject();
            o.put("orderId", orders.getId());
            o.put("prizeReal", orders.getPrizeReal());
            o.put("fees", orders.getFees());
            o.put("tax", orders.getTax());
            jsonArray.add(o);

            // 通知商家有人创建订单
            try {
                notificationHelperClient.notifySellerWithCreateOrder(orders, 5);
            } catch (Exception e) {
                logger.error("发送通知消息提醒商家下单事件报错:", e);
            }
        }
        return jsonArray;
    }

    public Seller getSeller(String sellerId) {
        return getHibernateTemplate().get(Seller.class, sellerId);
    }

    @Override
    public Double updatePayOrders(String partyId, String orderInfo, String safeword) {
        String lockKey = MallRedisKeys.MALL_ORDER_USER_LOCK + partyId;
        if (!redisHandler.lock(lockKey, 5)) {
            throw new BusinessException("操作频繁，请稍后再试");
        }

        Party party = partyService.cachePartyBy(partyId, false);

        String partySafeword = party.getSafeword();
        if (StringUtils.isEmptyString(partySafeword)) {
            throw new BusinessException(999, "请设置资金密码");
        }

        String errorPassCount = sysparaService.find("number_of_wrong_passwords").getValue();
        if (Objects.isNull(errorPassCount)) {
            logger.error("number_of_wrong_passwords 系统参数未配置！");
            throw new BusinessException("参数异常");
        }
        String lockPassworkErrorKey = MallRedisKeys.MALL_PASSWORD_ERROR_LOCK + partyId;
        int needSeconds = util.DateUtils.getTomorrowStartSeconds();
        boolean exit = redisHandler.exists(lockPassworkErrorKey);//是否已经错误过
        if (exit && ("true".equals(redisHandler.getString(lockPassworkErrorKey)))) {//已经尝试错误过且次数已经超过number_of_wrong_passwords配置的次数
            throw new BusinessException(1, "密码输入错误次数过多，请明天再试");
        } else if (exit && errorPassCount.equals(redisHandler.getString(lockPassworkErrorKey))) {//已经尝试密码错误过且次数刚好等于number_of_wrong_passwords配置的次数
            redisHandler.setSyncStringEx(lockPassworkErrorKey, "true", needSeconds);
            throw new BusinessException(1, "密码输入错误次数过多，请明天再试");
        } else {//失败次数小于配置次数或者未失败
            boolean checkSafeWord = this.partyService.checkSafeword(safeword, partyId);
            if (checkSafeWord) {//交易密码校验成功
                redisHandler.remove(lockPassworkErrorKey);
            } else {//交易密码校验失败
                if (exit) {//已经失败过，执行加1操作
                    redisHandler.incr(lockPassworkErrorKey);
                } else {//未失败，set值，并计1
                    redisHandler.setSyncStringEx(lockPassworkErrorKey, "1", needSeconds);
                }
                throw new BusinessException(1, "资金密码错误");
            }
        }

        String[] ordersArray = orderInfo.split(",");

        try {
            double prize = 0;
            List<MallOrdersPrize> list = new ArrayList<>();
            for (String orderId : ordersArray) {
                MallOrdersPrize order = this.getHibernateTemplate().get(MallOrdersPrize.class, orderId);
                if (order == null || !partyId.equals(order.getPartyId())) {
                    throw new BusinessException("订单不存在");
                }
                if (order.getStatus() != 0) {
                    throw new BusinessException("该订单未满足支付条件");
                }
                prize = Arith.add(prize, order.getPrizeReal());
                prize = Arith.add(prize, order.getFees());
                prize = Arith.add(prize, order.getTax());

                list.add(order);
            }

            Wallet wallet = walletService.saveWalletByPartyId(partyId);

            double amount_before = 0D;
            if (wallet.getFrozenState() == 0) {
                amount_before = wallet.getMoney();
            } else {//处于冻结状态时
                amount_before = wallet.getMoneyAfterFrozen();
            }
            if (amount_before < prize) {
                throw new BusinessException("余额不足");
            }
            if (wallet.getFrozenState() == 0) {
                wallet.setMoney(Arith.roundDown(Arith.sub(wallet.getMoney(), prize), 2));
            } else {//处于冻结状态时
                wallet.setMoneyAfterFrozen(Arith.roundDown(Arith.sub(wallet.getMoneyAfterFrozen(), prize), 2));
            }
//            walletService.update(wallet.getPartyId().toString(), Arith.sub(0, prize));
            walletService.update(wallet);

            MoneyLog moneyLog = new MoneyLog();
            moneyLog.setCategory(Constants.MONEYLOG_CATEGORY_COIN);
            moneyLog.setAmount_before(amount_before);
            moneyLog.setAmount(Arith.sub(0, prize));
            if (wallet.getFrozenState() == 0) {
                moneyLog.setAmount_after(wallet.getMoney());
                moneyLog.setFreeze(0);
            } else {//处于冻结状态时
                moneyLog.setAmount_after(wallet.getMoneyAfterFrozen());
                moneyLog.setFreeze(1);
            }
            moneyLog.setLog("支付[" + list.size() + "]个订单");
            moneyLog.setPartyId(partyId);
            moneyLog.setWallettype(Constants.WALLET);
            moneyLog.setContent_type(Constants.MONEYLOG_CONTNET_PAY_ORDER);

            moneyLogService.save(moneyLog);

            for (MallOrdersPrize order : list) {
                order.setStatus(1);
                order.setPayStatus(1);
                order.setPayTime(new Date());
                this.saveOrderLog(partyId, order.getId().toString(), OrderStatusEnum.ORDER_PAY_SUCCESS, "订单" + order.getId().toString() + "下单成功，付款成功");
                getHibernateTemplate().update(order);
            }

            return wallet.getMoney();
        } catch (BusinessException e) {
            logger.error("支付失败", e);
            throw new BusinessException(e.getMessage());
        } finally {
            redisHandler.remove(lockKey);
        }
    }

    public void updateSellerGoodsSoldNum(String orderId) {
        DetachedCriteria query = DetachedCriteria.forClass(MallOrdersGoods.class);
        query.add(Property.forName("orderId").eq(orderId));
        List<MallOrdersGoods> mallOrdersGoods = (List<MallOrdersGoods>) getHibernateTemplate().findByCriteria(query);
        if (CollectionUtil.isNotEmpty(mallOrdersGoods)) {
            mallOrdersGoods.forEach(e -> {
                SellerGoods sellerGoods = this.getHibernateTemplate().get(SellerGoods.class, e.getGoodsId());
                if (null != sellerGoods) {
                    sellerGoods.setSoldNum(sellerGoods.getSoldNum() + e.getGoodsNum());
                    updateSellerGoods(sellerGoods);
                }
            });
        }
    }

    @Override
    public void updateCalcelOrders(String partyId, String orderId, String returnReason) {
        try {

            MallOrdersPrize order = this.getHibernateTemplate().get(MallOrdersPrize.class, orderId);
            if (order == null || !partyId.equals(order.getPartyId())) {
                throw new BusinessException("订单不存在");
            }
            if (order.getStatus() != 0) {
                throw new BusinessException("该订单状态无法操作");
            }

            order.setStatus(-1);
            order.setReturnReason(returnReason);
            getHibernateTemplate().update(order);

            this.saveOrderLog(partyId, orderId, OrderStatusEnum.ORDER_CANCEL, "订单" + orderId + "订单已取消，原因：" + returnReason);

        } catch (BusinessException e) {
            logger.error("取消失败", e);
            throw new BusinessException(e.getMessage());
        }

    }

    @Override
    public void updateReceiptOrders(String partyId, String orderId) {
        try {

            MallOrdersPrize order = this.getHibernateTemplate().get(MallOrdersPrize.class, orderId);
            if (order == null || !partyId.equals(order.getPartyId())) {
                throw new BusinessException("订单不存在");
            }
            if (order.getStatus() != 3) {
                throw new BusinessException("该订单状态无法操作");
            }

            order.setStatus(4);
            order.setUpTime(System.currentTimeMillis());

            getHibernateTemplate().update(order);

            this.saveOrderLog(partyId, orderId, OrderStatusEnum.ORDER_SEND_CONFIRM, "订单" + orderId + "订单已签收");

        } catch (BusinessException e) {
            logger.error("取消失败", e);
            throw new BusinessException(e.getMessage());
        }
    }

    @Override
    public void updateReturnsOrders(String partyId, String orderId, String returnReason, String returnDetail) {
        try {

            MallOrdersPrize order = this.getHibernateTemplate().get(MallOrdersPrize.class, orderId);
            if (order == null || !partyId.equals(order.getPartyId())) {
                throw new BusinessException("订单不存在");
            }
            if (order.getStatus() > 3) {
                throw new BusinessException("该订单状态无法操作");
            }
            if (order.getStatus() == 3) {
                Double mall_returnTimeOut = sysparaService.find("mall_returnTimeOut").getDouble();
                double timeOut = Objects.isNull(mall_returnTimeOut) ? 0d : mall_returnTimeOut;
                if (order.getUpTime() < System.currentTimeMillis() - (long) (60 * 60 * 1000 * timeOut)) {
                    throw new BusinessException("订单已经发货，无法退款");
                }
            }

//            2023-10-11 新增需求，退款驳回可以不影响主流程
            order.setStatusBeforeLastRefund(order.getStatus());
            order.setStatus(6);
            order.setReturnStatus(1);
            order.setReturnReason(returnReason);
            order.setReturnDetail(returnDetail);
            order.setRefundTime(new Date());

            this.saveOrderLog(partyId, orderId, OrderStatusEnum.REFUND, "订单" + orderId + "发起退款申请");

            getHibernateTemplate().update(order);

        } catch (BusinessException e) {
            logger.error("退款失败", e);
//            this.saveOrderLog(partyId, orderId, OrderStatusEnum.REFUND_FAIL, "订单" + orderId + "订单退款失败");
            throw new BusinessException(e.getMessage());
        }
    }

    @Override
    public void updateReturnsOrdersByAdmin(String orderId, boolean agree, String reason) {
        try {

            MallOrdersPrize order = this.getHibernateTemplate().get(MallOrdersPrize.class, orderId);
            if (order == null) {
                throw new BusinessException("订单不存在");
            }
            if (order.getStatus() != 6 || order.getReturnStatus() != 1) {
                throw new BusinessException("该订单状态无法操作");
            }
            if (agree) {
                order.setReturnStatus(2);
                double amount_before;
                //商家退款
                if (order.getPurchStatus() == 1) {
                    Wallet walletSeller = walletService.saveWalletByPartyId(order.getSellerId());
                    amount_before = walletSeller.getMoney();
                    double pushAmount = order.getPushAmount();
                    walletSeller.setMoney(Arith.roundDown(Arith.add(walletSeller.getMoney(), pushAmount), 2));
//                    walletService.updateMoeny(walletSeller.getPartyId().toString(), pushAmount);
                    walletService.update(walletSeller);

                    MoneyLog moneyLog = new MoneyLog();
                    moneyLog.setCategory(Constants.MONEYLOG_CATEGORY_COIN);
                    moneyLog.setAmount_before(amount_before);
                    moneyLog.setAmount(Arith.add(0, pushAmount));
                    moneyLog.setAmount_after(walletSeller.getMoney());

                    moneyLog.setLog("商家退货[" + order.getId().toString() + "]");
                    moneyLog.setPartyId(order.getSellerId());
                    moneyLog.setWallettype(Constants.WALLET);
                    moneyLog.setContent_type(Constants.MONEYLOG_CONTNET_ORDER_SELLER);
                    moneyLogService.save(moneyLog);
                }

                //会员退款
                Wallet wallet = walletService.saveWalletByPartyId(order.getPartyId());

                amount_before = wallet.getMoney();

                double prize = order.getPrizeReal();
                prize = Arith.add(prize, order.getFees());
                prize = Arith.add(prize, order.getTax());

                wallet.setMoney(Arith.roundDown(Arith.add(wallet.getMoney(), prize), 2));
//                walletService.update(wallet.getPartyId().toString(), prize);
                walletService.update(wallet);

                MoneyLog moneyLog2 = new MoneyLog();
                moneyLog2.setCategory(Constants.MONEYLOG_CATEGORY_COIN);
                moneyLog2.setAmount_before(amount_before);
                moneyLog2.setAmount(Arith.add(0, prize));
                moneyLog2.setAmount_after(wallet.getMoney());

                moneyLog2.setLog("会员退货[" + order.getId().toString() + "]");
                moneyLog2.setPartyId(order.getPartyId());
                moneyLog2.setWallettype(Constants.WALLET);
                moneyLog2.setContent_type(Constants.MONEYLOG_CONTNET_ORDER_USER);
                moneyLogService.save(moneyLog2);
                this.saveOrderLog(order.getPartyId(), orderId, OrderStatusEnum.REFUND_SUCCESS, "订单" + orderId + "退款成功");
            } else {
                order.setReturnStatus(3);
//                2023-10-11  新增退款驳回不影响原始流程
                order.setStatus(order.getStatusBeforeLastRefund());
                order.setUpTime(System.currentTimeMillis());
                this.saveOrderLog(order.getPartyId(), orderId, OrderStatusEnum.REFUND_FAIL, "订单" + orderId + "退款失败");
            }
            order.setRefundRemark(reason);
            order.setRefundDealTime(new Date());
            getHibernateTemplate().update(order);
        } catch (BusinessException e) {
            logger.error("退货失败", e);
            throw new BusinessException(e.getMessage());
        }
    }

    @Override
    public MallPageInfo listMallOrdersPrize(String partyId, Integer status, int pageNum, int pageSize) {
        DetachedCriteria query = DetachedCriteria.forClass(MallOrdersPrize.class);
        query.add(Property.forName("partyId").eq(partyId));
        query.add(Property.forName("isDelete").eq(0));
        if (status != null) {
            if (status == 1) {
                Disjunction orCondition = Restrictions.disjunction();
                orCondition.add(Property.forName("status").eq(1));
                orCondition.add(Property.forName("status").eq(2));
                query.add(orCondition);
            } else {
                query.add(Property.forName("status").eq(status));
            }
        }
        query.addOrder(Order.desc("createTime"));

        // 查询总条数
        Long totalCount = (Long) query.setProjection(Projections.rowCount()).getExecutableCriteria(this.getHibernateTemplate().getSessionFactory().getCurrentSession()).uniqueResult();
        query.setProjection(null);

        MallPageInfo mallPageInfo = MallPageInfoUtil.getMallPage(pageSize, pageNum, totalCount, getHibernateTemplate().findByCriteria(query, (pageNum - 1) * pageSize, pageSize));

        return mallPageInfo;
    }


    @Override
    public MallOrdersPrize getMallOrdersPrize(String orderId) {
        String ades = "0";
        try {
            HttpServletRequest request = ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes())).getRequest();
            ades = request.getParameter("ades");
        } catch (Exception e) {
            // 定时任务场景此处会报错
            log.info("定时任务，订单地址无需加密");
        }

        MallOrdersPrize address = this.getHibernateTemplate().get(MallOrdersPrize.class, orderId);
        // 前端不需要加密地址的场景
        if ("0".equalsIgnoreCase(ades)) {
            return address;
        }
        // 定时任务环境下不需要加密地址
        if (sysparaService == null) {
            return address;
        }
        if (address == null) {
            return address;
        }
        if (1 == address.getIsDelete()) {
            return null;
        }
//        Syspara addressDesensitization = sysparaService.find(ADDRESS_DESENSITIZATION);
//        if (addressDesensitization != null && "1".equalsIgnoreCase(addressDesensitization.getValue())) {
//            address.setAddress(DESENSITIZATION_STR);
//        }
        return address;
    }

    @Override
    public MallOrdersGoods getMallOrdersGoods(String orderId, String goodsId) {
        DetachedCriteria query = DetachedCriteria.forClass(MallOrdersGoods.class);
        query.add(Property.forName("orderId").eq(orderId));
        query.add(Property.forName("goodsId").eq(goodsId));
        List<MallOrdersGoods> mallOrdersGoodsList = (List<MallOrdersGoods>) getHibernateTemplate().findByCriteria(query, 0, 1);
        if (Objects.nonNull(mallOrdersGoodsList) && mallOrdersGoodsList.size() > 0) {
            return mallOrdersGoodsList.get(0);
        } else {
            return null;
        }
    }

    public SystemGoods getSystemGoods(String id) {
        SystemGoods address = this.getHibernateTemplate().get(SystemGoods.class, id);
        return address;
    }

    @Override
    public List<MallOrdersGoods> listMallOrdersGoods(String orderId, int pageNum, int pageSize) {
        DetachedCriteria query = DetachedCriteria.forClass(MallOrdersGoods.class);
        query.add(Property.forName("orderId").eq(orderId));
        query.addOrder(Order.asc("goodsSort"));
        return (List<MallOrdersGoods>) getHibernateTemplate().findByCriteria(query, (pageNum - 1) * pageSize, pageSize);
    }

    public Page listShoppingCartAboutPage(String partyId, int pageNum, int pageSize) {
//        此处排序sql
//        SELECT *
//                FROM t_mall_shopping_cart main
//        JOIN (
//                SELECT SELLER_ID AS sellerId, MAX(CREATE_TIME) AS maxCreateTime
//                FROM t_mall_shopping_cart
//                GROUP BY SELLER_ID
//        ) subquery ON main.SELLER_ID = subquery.sellerId
//        WHERE PARTY_ID ='PARTY_ID'
//        ORDER BY subquery.maxCreateTime DESC, main.CREATE_TIME DESC;
        StringBuffer queryString = new StringBuffer();
        queryString.append(
                "SELECT main.* FROM T_MALL_SHOPPING_CART main "+
                        "LEFT JOIN ( "+
                        "        SELECT SELLER_ID AS sellerIdsub, MAX(CREATE_TIME) AS maxTime "+
                        "        FROM T_MALL_SHOPPING_CART "+
                        "        GROUP BY SELLER_ID "+
                        ") sub ON main.SELLER_ID = sub.sellerIdsub WHERE 1=1 ");

        Map<String, Object> parameters = new HashMap<String, Object>();
        if (!StringUtils.isNullOrEmpty(partyId)) {
            queryString.append(" AND PARTY_ID=:partyId  ");
            parameters.put("partyId",partyId);
        }
        queryString.append(" ORDER BY sub.maxTime DESC, main.CREATE_TIME DESC ");

        DetachedCriteria query = DetachedCriteria.forClass(ShoppingCart.class);
        query.add(Property.forName("partyId").eq(partyId));
        // 查询总条数
        Long totalCount = (Long) query.setProjection(Projections.rowCount()).getExecutableCriteria(this.getHibernateTemplate().getSessionFactory().getCurrentSession()).uniqueResult();

        Page page = this.pagedQueryDao.pagedQuerySQL(pageNum, pageSize, queryString.toString(), parameters);
        page.setTotalElements(totalCount.intValue());
        return page;

    }

    public MallPageInfo listMallOrdersGoodsAboutPage(String orderId, int pageNum, int pageSize) {
        DetachedCriteria query = DetachedCriteria.forClass(MallOrdersGoods.class);
        query.add(Property.forName("orderId").eq(orderId));
        query.addOrder(Order.asc("goodsSort"));
        // 查询总条数
        Long totalCount = (Long) query.setProjection(Projections.rowCount()).getExecutableCriteria(this.getHibernateTemplate().getSessionFactory().getCurrentSession()).uniqueResult();
        query.setProjection(null);
        MallPageInfo mallPageInfo = MallPageInfoUtil.getMallPage(pageSize, pageNum, totalCount, getHibernateTemplate().findByCriteria(query, (pageNum - 1) * pageSize, pageSize));
        return mallPageInfo;
    }

    @Override
    public SellerGoods getSellerGoods(String sellerGoodsId) {
        SellerGoods address = this.getHibernateTemplate().get(SellerGoods.class, sellerGoodsId);
        return address;
    }

    @Override
    public MallPageInfo listSellerOrdersInfo(String sellerId, String status, String orderId, String payStatus, String purchStatus, String begin, String end, int pageNum, int pageSize) {
        DetachedCriteria query = DetachedCriteria.forClass(MallOrdersPrize.class);
        query.add(Property.forName("sellerId").eq(sellerId));
        query.add(Property.forName("isDelete").eq(0));
        // 如果不是只差退款的，退款的不显示
        /*if (!"6".equalsIgnoreCase(status)) {
            query.add(Property.forName("status").ne(6));
        }*/

        if (StringUtils.isNotEmpty(orderId)) {
            List<MallOrdersPrize> list = new ArrayList<>();
            MallOrdersPrize mallOrdersPrize = getMallOrdersPrize(orderId);
            if (mallOrdersPrize != null && mallOrdersPrize.getSellerId().equals(sellerId)) {
                list.add(mallOrdersPrize);
            }
            MallPageInfo pageInfo = new MallPageInfo();
            pageInfo.setPageNum(pageNum);
            pageInfo.setPageSize(pageSize);
            pageInfo.setElements(list);
            pageInfo.setTotalElements(list.size());
            return pageInfo;
        }

        if (StringUtils.isNotEmpty(payStatus)) {
            if (payStatus.equals("-1")) {
                query.add(Property.forName("status").eq(Integer.parseInt(payStatus)));
            } else {
                query.add(Property.forName("payStatus").eq(Integer.parseInt(payStatus)));
            }
        }
        if (StringUtils.isNotEmpty(status)) {
            query.add(Property.forName("status").eq(Integer.parseInt(status)));
        }
        if (StringUtils.isNotEmpty(purchStatus)) {
            if (Integer.parseInt(purchStatus)==0) {
                query.add(Property.forName("status").ne(-1));
            }
            query.add(Property.forName("purchStatus").eq(Integer.parseInt(purchStatus)));
        }
        if (begin != null) {
            Date beginDate;
            try {
                beginDate = DateUtils.strToDate(begin);
            } catch (Exception e) {
                String sub = StrUtil.sub(begin, 0, 10);
                beginDate = DateUtils.dayStringToDate(sub);
            }
            query.add(Property.forName("createTime").ge(beginDate));
        }
        if (end != null) {
            Date endDate;
            try {
                endDate = DateUtils.strToDate(end);
            } catch (Exception e) {
                String sub = StrUtil.sub(end, 0, 10);
                endDate = DateUtils.dayStringToDate(sub);
            }
            query.add(Property.forName("createTime").le(DateUtils.getDayEnd(endDate)));
        }
        query.addOrder(Order.desc("createTime"));

        // 查询总条数
        Long totalCount = (Long) query.setProjection(Projections.rowCount()).getExecutableCriteria(this.getHibernateTemplate().getSessionFactory().getCurrentSession()).uniqueResult();
        query.setProjection(null);

        MallPageInfo mallPageInfo = MallPageInfoUtil.getMallPage(pageSize, pageNum, totalCount, getHibernateTemplate().findByCriteria(query, (pageNum - 1) * pageSize, pageSize));

        return mallPageInfo;
    }


    @Override
    public MallPageInfo listSellerReturns(String sellerId, String returnStatus, String begin, String end, int pageNum, int pageSize) {
        DetachedCriteria query = DetachedCriteria.forClass(MallOrdersPrize.class);
        query.add(Property.forName("sellerId").eq(sellerId));
        query.add(Property.forName("status").eq(6));

        if (StringUtils.isNotEmpty(returnStatus)) {
            query.add(Property.forName("returnStatus").eq(Integer.parseInt(returnStatus)));
        }
        if (StringUtils.isNotEmpty(begin)) {
            query.add(Property.forName("refundTime").ge(DateUtils.dayStringToDate(begin)));
        }
        if (StringUtils.isNotEmpty(end)) {
            query.add(Property.forName("refundTime").le(DateUtils.getDayEnd(DateUtils.dayStringToDate(end))));
        }
        query.addOrder(Order.desc("createTime"));

        // 查询总条数
        Long totalCount = (Long) query.setProjection(Projections.rowCount()).getExecutableCriteria(this.getHibernateTemplate().getSessionFactory().getCurrentSession()).uniqueResult();
        query.setProjection(null);

        MallPageInfo mallPageInfo = MallPageInfoUtil.getMallPage(pageSize, pageNum, totalCount, getHibernateTemplate().findByCriteria(query, (pageNum - 1) * pageSize, pageSize));

        return mallPageInfo;
    }

    @Override
    public void updatePushOrders(String partyId, String orderId) {
        String lockKey = MallRedisKeys.MALL_ORDER_USER_LOCK + orderId;
        try {
            MallOrdersPrize order = this.getHibernateTemplate().get(MallOrdersPrize.class, orderId);
            if (order == null || !order.getSellerId().equals(partyId)) {
                throw new BusinessException("订单不存在");
            }
            if (order.getStatus() != 1) {
                throw new BusinessException("该订单状态无法操作");
            }
            if (!redisHandler.lock(lockKey, 5)) {
                throw new BusinessException("操作失败，请稍后再试");
            }

            Date now = new Date();
            double prize = order.getSystemPrice();
//            如果有折扣，按照采购折扣价格计算
            if (Double.compare(order.getSellerDiscount(), 0D) != 0) {
                prize = Arith.roundDown(Arith.mul(prize, 1 - order.getSellerDiscount()), 2);
            }
//            Wallet wallet = walletService.saveWalletByPartyId(partyId); 这里经常出现缓存不一致的问题，改为直接查库
            Wallet wallet = walletService.selectOne(partyId);
            if (Objects.isNull(wallet)) {
                throw new BusinessException("余额不足");
            }
            double amount_before = 0D;
            if (wallet.getFrozenState() == 0) {
                amount_before = wallet.getMoney();
            } else {//处于冻结状态时
                amount_before = wallet.getMoneyAfterFrozen();
            }

            if (amount_before < prize) {
                throw new BusinessException("余额不足");
            }

            if (wallet.getFrozenState() == 0) {
                wallet.setMoney(Arith.roundDown(Arith.sub(wallet.getMoney(), prize), 2));
            } else {//处于冻结状态时
                wallet.setMoneyAfterFrozen(Arith.roundDown(Arith.sub(wallet.getMoneyAfterFrozen(), prize), 2));
            }

//            walletService.update(wallet.getPartyId().toString(), Arith.sub(0, prize));
            walletService.update(wallet);

            MoneyLog moneyLog = new MoneyLog();
            moneyLog.setCategory(Constants.MONEYLOG_CATEGORY_COIN);
            moneyLog.setAmount_before(amount_before);
            moneyLog.setAmount(Arith.sub(0, prize));
            if (wallet.getFrozenState() == 0) {
                moneyLog.setAmount_after(wallet.getMoney());
                moneyLog.setFreeze(0);
            } else {//处于冻结状态时
                moneyLog.setAmount_after(wallet.getMoneyAfterFrozen());
                moneyLog.setFreeze(1);
            }

            moneyLog.setLog("采购订单[" + orderId + "]");
            moneyLog.setPartyId(partyId);
            moneyLog.setWallettype(Constants.WALLET);
            moneyLog.setContent_type(Constants.MONEYLOG_CONTNET_PUSH_ORDER);

            moneyLogService.save(moneyLog);

            order.setStatus(2);
            order.setPurchStatus(1);
            order.setPurchTime(now);
            order.setPushAmount(prize);

            getHibernateTemplate().update(order);
//            getHibernateTemplate().flush();

            this.saveOrderLog(partyId, orderId, OrderStatusEnum.PURCH_SUCESS, "订单" + orderId + "商家已确认，已进入备货状态");
            this.updateSellerGoodsSoldNum(order.getId().toString());
//            if (order.getOrderStatus() == 1) {
//                order.setStatus(3);
//                order.setUpTime(new Date().getTime());
//                this.getHibernateTemplate().update(order);
//            }
        } catch (BusinessException e) {
            logger.error("采购失败", e);
//            redisHandler.remove(lockKey);
            throw new BusinessException(e.getMessage());
        }

    }

    private void updateVirtualPushOrders(MallOrdersPrize order) {

        log.info("自动确认订单ID{} 商家ID信息{}", order.getId(), order.getSellerId());

        String lockKey = MallRedisKeys.MALL_ORDER_USER_LOCK + order.getId();
        if (!redisHandler.lock(lockKey, 20)) {
            log.info("操作失败，请稍后再试");
            return;
        }

        try {

            if (order.getStatus() != 1) {
                log.info("该订单状态无法操作");
                return;
            }

            Date now = new Date();
            double prize = order.getSystemPrice();
//            如果有折扣，按照采购折扣价格计算
            if (Double.compare(order.getSellerDiscount(), 0D) != 0) {
                prize = Arith.roundDown(Arith.mul(prize, 1 - order.getSellerDiscount()), 2);
            }

            Wallet wallet = walletService.saveWalletByPartyId(order.getSellerId());

            double amount_before = wallet.getMoney();

            wallet.setMoney(Arith.sub(wallet.getMoney(), prize));

            walletService.update(wallet);

            MoneyLog moneyLog = new MoneyLog();
            moneyLog.setCategory(Constants.MONEYLOG_CATEGORY_COIN);
            moneyLog.setAmount_before(amount_before);
            moneyLog.setAmount(Arith.sub(0, prize));
            moneyLog.setAmount_after(wallet.getMoney());

            moneyLog.setLog("自动采购订单[" + order.getId() + "]");
            moneyLog.setPartyId(order.getSellerId());
            moneyLog.setWallettype(Constants.WALLET);
            moneyLog.setContent_type(Constants.MONEYLOG_CONTNET_PUSH_ORDER);

            moneyLogService.save(moneyLog);

            order.setStatus(2);
            order.setPurchStatus(1);
            order.setPurchTime(now);

            getHibernateTemplate().update(order);

            this.saveOrderLog(order.getSellerId(), order.getId().toString(), OrderStatusEnum.PURCH_SUCESS, "订单" + order.getId() + "商家已确认，已进入备货状态");
            this.updateSellerGoodsSoldNum(order.getId().toString());
        } catch (BusinessException e) {
            logger.error("采购失败", e);
        } finally {
            redisHandler.remove(lockKey);
        }
    }

    @Override
    public JSONArray listRebateByLevel(String partyId, int level, int pageNum, int pageSize) {
        JSONArray jsonArray = new JSONArray();
        for (Map<String, Object> rebateMap : userDataService.getChildrenLevelPagedForBrush(pageNum, pageSize, partyId, level)) {
            JSONObject o = new JSONObject();
            String username = rebateMap.get("username").toString();
            if (username.length() > 3) {
                username = username.substring(0, 3) + "***" + username.substring(username.length() - 3);
            }
            o.put("username", username);
            o.put("regTime", rebateMap.get("createTime"));
            o.put("rebate", Arith.roundDown(Double.parseDouble(rebateMap.get("rebate").toString()), 2));
            o.put("avatar", rebateMap.get("avatar"));
            o.put("countOrder", rebateMap.get("countOrder"));
            jsonArray.add(o);
        }
        return jsonArray;
    }

    @Override
    public MallClientSeller getMallClientSeller(String id) {
        MallClientSeller client = this.getHibernateTemplate().get(MallClientSeller.class, id);
        return client;
    }

    /*@Override
    public Integer updateAutoCancel() {
        if (sysparaService.find("mall_autoCancel_timeout") == null) {
            log.error("查询自动取消超时时间为null");
            return 0;
        }
        Double mall_autoCancel_timeout = sysparaService.find("mall_autoCancel_timeout").getDouble();
        String sql = "UPDATE T_MALL_ORDERS_PRIZE SET STATUS=-1 WHERE STATUS=0 AND CREATE_TIME < ?";
        Integer c = jdbcTemplate.update(sql, DateUtils.addMinute(new Date(), -(int) (mall_autoCancel_timeout * 60)));
        return c;
    }*/

    public Integer updateAutoCancel() {
        Integer result = 0;
        if (sysparaService.find("mall_autoCancel_timeout") == null) {
            log.error("查询自动取消超时时间为null");
            return 0;
        }
        NamedParameterJdbcTemplate namedParameterJdbcTemplate1 = new NamedParameterJdbcTemplate(jdbcTemplate);
        Map<String, Object> queryParams = new HashMap<>();
        Double mall_autoCancel_timeout = sysparaService.find("mall_autoCancel_timeout").getDouble();
        queryParams.put("nowDate", DateUtils.addMinute(new Date(), -(int) (mall_autoCancel_timeout * 60)));
        List<Map<String, Object>> lists = namedParameterJdbcTemplate1.queryForList("SELECT UUID FROM T_MALL_ORDERS_PRIZE WHERE STATUS=0 AND CREATE_TIME <:nowDate ORDER BY CREATE_TIME DESC LIMIT 50", queryParams);
        List<String> orderList = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(lists)) {
            for (Map<String, Object> map : lists) {
                for (Map.Entry<String, Object> entry : map.entrySet()) {
                    if (Objects.nonNull(entry.getValue())) {
                        orderList.add((String) entry.getValue());
                    }
                }
            }
            NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
            Map<String, Object> params = new HashMap<>();
            params.put("orderList", orderList);
            String sql = "UPDATE T_MALL_ORDERS_PRIZE SET STATUS = -1 WHERE UUID IN (:orderList) ";
            result = namedParameterJdbcTemplate.update(sql, params);
        }
        for (String orderId : orderList) {
            MallOrdersPrize mallOrdersPrize = getMallOrdersPrize(orderId);
            if (Objects.nonNull(mallOrdersPrize)) {
                this.saveOrderLog(mallOrdersPrize.getPartyId(), orderId, OrderStatusEnum.ORDER_CANCEL_TIME, "订单" + orderId + "长时间未付款，系统自动取消订单");
            }
        }
        return result;
    }

    @Override
    public Integer updateAutoReceipt() {
        Integer result = 0;

        NamedParameterJdbcTemplate namedParameterJdbcTemplate1 = new NamedParameterJdbcTemplate(jdbcTemplate);
        Map<String, Object> queryParams = new HashMap<>();
        if (sysparaService.find("mall_autoReceipt_timeout") == null) {
            log.error("查询自动收货超时时间为null");
            return 0;
        }
        Double mall_autoReceipt_timeout = sysparaService.find("mall_autoReceipt_timeout").getDouble();
        queryParams.put("nowDate", System.currentTimeMillis() - 86400000 * mall_autoReceipt_timeout);
        List<Map<String, Object>> lists = namedParameterJdbcTemplate1.queryForList("SELECT UUID FROM T_MALL_ORDERS_PRIZE  WHERE STATUS=3 AND MANUAL_RECEIPT_STATUS=0 AND UP_TIME <:nowDate ORDER BY UP_TIME DESC LIMIT 50", queryParams);

        List<String> orderList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(lists)) {
            for (Map<String, Object> map : lists) {
                for (Map.Entry<String, Object> entry : map.entrySet()) {

                    if (Objects.nonNull(entry.getValue())) {
                        orderList.add((String) entry.getValue());
                    }
                }
            }
            NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
            Map<String, Object> params = new HashMap<>();
            params.put("orderList", orderList);
            params.put("recTime", new Date().getTime());
            String sql = "UPDATE T_MALL_ORDERS_PRIZE SET STATUS = 4,UP_TIME=:recTime WHERE UUID IN (:orderList) ";
            result = namedParameterJdbcTemplate.update(sql, params);
        }
        for (String orderId : orderList) {
            MallOrdersPrize mallOrdersPrize = getMallOrdersPrize(orderId);
            if (Objects.nonNull(mallOrdersPrize)) {
                this.saveOrderLog(mallOrdersPrize.getPartyId(), orderId, OrderStatusEnum.ORDER_SEND_CONFIRM, "订单" + orderId + "订单已签收");
            }
        }
        return result;
    }

    @Override
    public Integer updateStopCombo() {

        Integer result = 0;
        Long now = System.currentTimeMillis();


        NamedParameterJdbcTemplate namedParameterJdbcTemplate1 = new NamedParameterJdbcTemplate(jdbcTemplate);

        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("nowDate", now);

        List<Map<String, Object>> lists = namedParameterJdbcTemplate1.queryForList("select UUID FROM T_MALL_COMBO_USER WHERE STOP_TIME <:nowDate ", queryParams);

        if (CollectionUtils.isNotEmpty(lists)) {
            List<String> sellerIds = new ArrayList<>();
            for (Map<String, Object> map : lists) {
                for (Map.Entry<String, Object> entry : map.entrySet()) {

                    if (Objects.nonNull(entry.getValue())) {
                        sellerIds.add((String) entry.getValue());
                    }
                }
            }
            NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
            Map<String, Object> params = new HashMap<>();
            params.put("sellerList", sellerIds);
            String sql = "UPDATE T_MALL_SELLER_GOODS SET IS_COMBO = 0 WHERE IS_COMBO = 1  AND SELLER_ID IN (:sellerList) ";
            result = namedParameterJdbcTemplate.update(sql, params);
        }
        return result;
    }

    @Override
    public List<MallOrdersPrize> listAutoProfit() {
        List<Integer> returnStatusList = Arrays.asList(0, 3);
        DetachedCriteria query = DetachedCriteria.forClass(MallOrdersPrize.class);
        query.add(Property.forName("status").ge(4));
        query.add(Property.forName("status").le(5));
        query.add(Property.forName("profitStatus").eq(0));
        query.add(Property.forName("returnStatus").in(returnStatusList));
        Double mall_autoProfit = sysparaService.find("mall_autoProfit").getDouble();
        query.add(Property.forName("upTime").lt(System.currentTimeMillis() - (long) (60 * 60 * 1000 * mall_autoProfit)));//自动释放佣金-一天改为1小时
        return (List<MallOrdersPrize>) getHibernateTemplate().findByCriteria(query, 0, 200);
    }

    @Override
    public List<MallOrdersPrize> listAutoComment() {
        DetachedCriteria query = DetachedCriteria.forClass(MallOrdersPrize.class);
        query.add(Property.forName("status").eq(4));
        query.add(Restrictions.or(Property.forName("hasComment").eq(0),
                Property.forName("hasComment").isNull()));

        Double autoComment = sysparaService.find("mall_AutoComment").getDouble();
        query.add(Property.forName("upTime").lt(System.currentTimeMillis() - (long) (autoComment * 86400000)));

        query.addOrder(Order.desc("createTime"));

        List<MallOrdersPrize> results = (List<MallOrdersPrize>) getHibernateTemplate().findByCriteria(query, 0, 500);
        return results;
    }

    public List<MallOrdersPrize> listAutoPurchTimeOut() {
        DetachedCriteria query = DetachedCriteria.forClass(MallOrdersPrize.class);
        query.add(Property.forName("status").eq(1));
        query.add(Property.forName("purchStatus").eq(0));
        query.add(Property.forName("purchTimeOutStatus").eq(0));
        query.add(Property.forName("payTime").lt(DateUtils.addMilliSecond(new Date(), (int) (-sysparaService.find("mall_order_purch_time_out").getDouble() * 3600000))));//1h *60*60*1000
        query.addOrder(Order.desc("createTime"));
        List<MallOrdersPrize> results = (List<MallOrdersPrize>) getHibernateTemplate().findByCriteria(query, 0, 500);
        return results;
    }

    @Override
    public List<MallOrdersPrize> listAutoConfirm() {
        //查询状态等于1，已支付，虚拟订单,支付超过多少小时
        DetachedCriteria query = DetachedCriteria.forClass(MallOrdersPrize.class);
        query.add(Property.forName("status").eq(1));
        query.add(Property.forName("payStatus").eq(1));
        query.add(Property.forName("payTime").lt(DateUtils.addSecond(new Date(), (int) (-sysparaService.find("mall_order_virtual_auto_confirm").getDouble() * 3600))));
        query.addOrder(Order.desc("createTime"));
        List<MallOrdersPrize> results = (List<MallOrdersPrize>) getHibernateTemplate().findByCriteria(query, 0, 500);
        return results;
    }

    @Override
    public List<MallOrdersPrize> listAutoVirtualOrderDelivery() {
        DetachedCriteria query = DetachedCriteria.forClass(MallOrdersPrize.class);
        query.add(Property.forName("status").eq(2));
        query.add(Property.forName("purchStatus").eq(1));
        query.add(Property.forName("orderStatus").eq(1));
        query.add(Property.forName("manualShipStatus").eq(0));
        query.add(Property.forName("purchTime").lt(DateUtils.addSecond(new Date(), (int) (-sysparaService.find("mall_order_virtual_auto_delivery").getDouble() * 3600))));
        List<MallOrdersPrize> results = (List<MallOrdersPrize>) getHibernateTemplate().findByCriteria(query, 0, 50);
        return results;
    }

    @Override
    public void updateAutoProfit(String orderId) {

        String lockKey = RedisKeys.ORDER_LOCK + orderId;

        if (!redisHandler.lock(lockKey, 20)) {
            return;
        }
        MallOrdersPrize ordersPrize = this.getHibernateTemplate().get(MallOrdersPrize.class, orderId);

        if (Objects.isNull(ordersPrize)) {
            return;
        }

        int status = ordersPrize.getStatus();
        int profitStatus = ordersPrize.getProfitStatus();

        try {
            if ((status == 4 || status == 5) && profitStatus == 0) {

                log.info("订单[{}][status:{}][profit-status:{}]释放佣金!", orderId, status, profitStatus);

                Set<String> result = new HashSet<>();

                DetachedCriteria query = DetachedCriteria.forClass(MoneyLog.class);
                query.add(Property.forName("log").like("%" + orderId + "%"));
                query.add(Property.forName("content_type").eq(Constants.MONEYLOG_CONTNET_ORDER_INCOME));

                List<MoneyLog> moneyLogs = (List<MoneyLog>) this.getHibernateTemplate().findByCriteria(query);

                if (CollectionUtils.isNotEmpty(moneyLogs)) {
                    ordersPrize.setProfitStatus(1);
                    this.getHibernateTemplate().merge(ordersPrize);
                    return;
                }

                String sellerId = ordersPrize.getSellerId();

                Map<Integer, String> parents = this.findParents(sellerId);

                BigDecimal profit = new BigDecimal(ordersPrize.getProfit()).setScale(2, BigDecimal.ROUND_DOWN);

                BigDecimal remain = profit;
                if (CollectionUtil.isNotEmpty(parents)) {
//                                TODO 判断是否扣除订单利润
                    Syspara sysparaRebateIsDeducted = sysparaService.find(SysParaCode.REBATE_IS_DEDUCTED.getCode());
                    int rebateIsDeducted =1;//默认扣除,1扣除，0不扣除
                    if (sysparaRebateIsDeducted != null) {
                        String rebateIsDeductedInfo = sysparaRebateIsDeducted.getValue().trim();
                        try {
                            if (StrUtil.isNotBlank(rebateIsDeductedInfo)) {
                                rebateIsDeducted = Integer.valueOf(rebateIsDeductedInfo);
                            }
                        } catch (NumberFormatException e) {
                            logger.error(SysParaCode.REBATE_IS_DEDUCTED.getCode()+"系统参数错误");
                        }
                    }
                    for (Map.Entry<Integer, String> entry : parents.entrySet()) {
                        Integer level = entry.getKey();
                        String userId = entry.getValue();
                        Party party = this.getHibernateTemplate().get(Party.class, userId);
                        if (Objects.isNull(party) || party.getRolename().equals(Constants.SECURITY_ROLE_AGENT)) {
                            continue;
                        }
                        BigDecimal rebate_ratio = this.getLevelRatio(level);
                        if (null != rebate_ratio) {
                            BigDecimal rebateAmount = profit.multiply(rebate_ratio).setScale(2, BigDecimal.ROUND_DOWN);
                            if (rebateAmount.compareTo(new BigDecimal("0.00")) > 0) {
                                remain = rebateIsDeducted==1?remain.subtract(rebateAmount):remain;
                                this.updateWallet(orderId, userId, Constants.MONEY_LOG_CONTENT_ORDER_REBATE, null, rebateAmount);
                                this.saveMallRebate(orderId, userId, sellerId, level, rebateAmount);
                                this.saveUserData(userId, level, rebateAmount);
                                result.add(userId);
                            }
                        }
                    }
                }
                result.add(sellerId);
                this.updateWallet(orderId, sellerId, Constants.MONEYLOG_CONTNET_ORDER_INCOME, new BigDecimal(ordersPrize.getSystemPrice()), remain);
                ordersPrize.setProfitStatus(1);
                this.getHibernateTemplate().merge(ordersPrize);

                log.info("订单结算成功:{},更新用户余额缓存：{}", orderId, result);

                if (CollectionUtil.isNotEmpty(result)) {
                    String sql = "SELECT * FROM T_WALLET WHERE PARTY_ID IN (:ids)";
                    MapSqlParameterSource parameter = new MapSqlParameterSource();
                    parameter.addValue("ids", result);
                    NamedParameterJdbcTemplate parameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
                    List<Wallet> wallets = parameterJdbcTemplate.query(sql, parameter, (rs, rowNum) -> {
                        Wallet wallet = new Wallet();
                        wallet.setId(rs.getString("UUID"));
                        wallet.setPartyId(rs.getString("PARTY_ID"));
                        wallet.setRebate(rs.getDouble("REBATE"));
                        wallet.setMoney(rs.getDouble("MONEY"));
                        wallet.setEntityVersion(0);
                        return wallet;
                    });
                    if (CollectionUtil.isNotEmpty(wallets)) {
                        for (Wallet wallet : wallets) {
                            this.redisHandler.setSync(RedisKeys.WALLET_PARTY_ID + wallet.getPartyId(), wallet);
                        }
                    }
                }

                this.addOrderFake(ordersPrize.getSellerId());
                userDataService.saveFreedAmountProfit(ordersPrize.getSellerId(), ordersPrize.getProfit(), ordersPrize.getPrizeReal());
            }
        } catch (Exception e) {
            logger.error("释放佣金异常", e);
        } finally {
            redisHandler.remove(lockKey);
        }
    }

    @Override
    public void updateAutoComment(String orderId) {

        MallOrdersPrize orders = this.getHibernateTemplate().get(MallOrdersPrize.class, orderId);
        if (orders == null || orders.getStatus() < 4) {
            return;
        }
        String lockKey = MallRedisKeys.MALL_ORDER_USER_LOCK + orders.getId();
        if (!redisHandler.lock(lockKey, 20)) {
            return;
        }
        try {
            DetachedCriteria query = DetachedCriteria.forClass(MallOrdersGoods.class);
            query.add(Property.forName("orderId").eq(orderId));

            List<MallOrdersGoods> orderGoods = (List<MallOrdersGoods>) this.getHibernateTemplate().findByCriteria(query, 0, 10);
            MallOrdersGoods mallOrdersGoods = new MallOrdersGoods();

            if (CollectionUtils.isNotEmpty(orderGoods)) {
                mallOrdersGoods = orderGoods.get(0);
            }

            Party party = partyService.cachePartyBy(orders.getPartyId(), false);

            //虚拟用户，并且开启虚拟自动评价功能

            if (party.getRolename().contains(Constants.SECURITY_ROLE_GUEST) && party.isAutoComment()) {
                List<SystemComment> comments = adminSystemCommentService.queryTop50Comments(mallOrdersGoods.getSystemGoodsId(), mallOrdersGoods.getGoodsId());
                if (comments == null || comments.size() == 0) {
                    log.info("系统评论库好评记录为空订单id{}", orderId);
                    // 系统默认好评，也应该有个 commentId 值的，此处仍然填 0
                    evaluationService.addEvaluation(orders.getPartyId(), orders.getSellerId(), mallOrdersGoods.getGoodsId(),
                            "1", "5", "", orderId, "0", null);
                } else {

                    log.info("虚拟订评价订单ID:{},partyId:{} sellerId:{} systemGoods is {} sellerGoodsId {}", orderId, orders.getPartyId(), orders.getSellerId(), mallOrdersGoods.getSystemGoodsId(), mallOrdersGoods.getGoodsId());

                    SystemComment comment;
                    if (comments.size() == 1) {
                        comment = comments.get(0);
                    } else {
                        comment = comments.get(RandomUtil.random(0, comments.size() - 1));
                    }

                    Evaluation evo = new Evaluation();
                    evo.setContent(comment.getContent());
                    evo.setOrderId(orders.getId().toString());
                    evo.setCreateTime(new Date());
                    if (comment.getScore() == 1) {
                        evo.setEvaluationType(3);
                    } else if (comment.getScore() == 2 || comment.getScore() == 3) {
                        evo.setEvaluationType(2);
                    } else if (comment.getScore() == 4 || comment.getScore() == 5) {
                        evo.setEvaluationType(1);
                    }
                    evo.setRating(comment.getScore());
                    evo.setSellerId(orders.getSellerId());
                    evo.setUserName(party.getUsername());
                    evo.setImgUrl1(comment.getImgUrl1());
                    evo.setImgUrl2(comment.getImgUrl2());
                    evo.setImgUrl3(comment.getImgUrl3());
                    evo.setImgUrl4(comment.getImgUrl4());
                    evo.setImgUrl5(comment.getImgUrl5());
                    evo.setImgUrl6(comment.getImgUrl6());
                    evo.setImgUrl7(comment.getImgUrl7());
                    evo.setImgUrl8(comment.getImgUrl8());
                    evo.setImgUrl9(comment.getImgUrl9());

                    // 额外增加冗余字段
                    evo.setPartyId(party.getId().toString());
                    evo.setPartyName(party.getName());

                    if (StringUtils.isNotEmpty(party.getAvatar())) {
                        evo.setPartyAvatar(party.getAvatar());
                    } else {
                        Random random = new Random();
                        String string = 1 + random.nextInt(19) + "";
                        evo.setPartyAvatar(string);
                    }
                    evo.setTemplate(comment.getId().toString());
                    evo.setSellerGoodsId(mallOrdersGoods.getGoodsId());
                    evo.setSystemGoodsId(mallOrdersGoods.getSystemGoodsId());
                    evo.setSourceType(1);
                    if (party.getRolename().equalsIgnoreCase(Constants.SECURITY_ROLE_GUEST)) {
                        evo.setSourceType(2);
                    } else if (party.getRolename().equalsIgnoreCase(Constants.SECURITY_ROLE_TEST)) {
                        evo.setSourceType(3);
                    }

                    evo.setEvaluationTime(new Date());

                    if (orders.getCountryId() == 0) {
                        Random random = new Random();
                        int randomCountryId = 1 + random.nextInt(249);
                        evo.setCountryId(randomCountryId);
                    } else {
                        evo.setCountryId(orders.getCountryId());
                    }

                    evo.setGoodsStatus(1);
                    evo.setSkuId(mallOrdersGoods.getSkuId());

                    orders.setHasComment(1);
                    orders.setStatus(5);
                    orders.setUpTime(System.currentTimeMillis());

                    getHibernateTemplate().update(orders);
                    evaluationService.addSystemEvaluation(evo);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        } finally {
            redisHandler.remove(lockKey);
        }
    }

    public void updateVirtualOrderdelivery(String orderId) {
        log.info("虚拟订单自动发货ID:{}", orderId);
        MallOrdersPrize orders = this.getHibernateTemplate().get(MallOrdersPrize.class, orderId);
        if (orders == null || orders.getStatus() != 2 || orders.getPurchStatus() != 1) {
            return;
        }
        orders.setStatus(3);
        orders.setUpTime(new Date().getTime());
        this.getHibernateTemplate().saveOrUpdate(orders);
        this.saveOrderLog(orders.getPartyId(), orderId, OrderStatusEnum.ORDER_SEND, "订单" + orderId + "已发货，正在运输中");
    }

    public void updatePurchTimeOut(String orderId) {
        log.info("订单卖家超时未采购,订单号:{}", orderId);
        MallOrdersPrize orders = this.getHibernateTemplate().get(MallOrdersPrize.class, orderId);
        if (orders == null || orders.getStatus() != 1 || orders.getPayStatus() != 1 || orders.getPurchStatus() != 0) {
            return;
        }
        orders.setPurchTimeOutStatus(1);
        orders.setPurchTimeOutTime(new Date());
        this.getHibernateTemplate().saveOrUpdate(orders);

        if (orders.getIsDelete() == 0) {//未删除时才需要通知商家
            // 通知商家采购订单超时
            try {
                notificationHelperClient.notifySellerWithPushTimeOut(orders, 5);
            } catch (Exception e) {
                logger.error("发送通知消息提醒商家采购超时事件报错:", e);
            }
        }
    }

    /**
     * 商品虚增流量处理，先计算出商铺流量对每个商品分配的流量，然后判断商铺是否开通直通车
     * 对直通车的商品。80%对新增流量分给直通车商品，20概率分给普通商品
     */
    @Override
    public void updateAutoIncreaseViewCount() {
        List<Seller> sellers = adminSellerService.queryAllSeller();
        // sellerId, goodsId, 新增流量
        for (Seller seller : sellers) {
            if (Objects.equals(1, seller.getBlack()) || Objects.equals(1,seller.getFlow())) {
                continue;
            }
            Map<String, Long> goodsViewAddNum = new HashMap<>();
            String sellerId = (String) seller.getId();

            // 开通直通车的商品id
            List<String> sellerComboGoodsIds = sellerGoodsService.getSellerGoodsId(sellerId, 1);

            // 针对开直通车，但是后台没开默认增加流量
            List<String> allShelfGoodsId = sellerGoodsService.getSellerGoodsId(sellerId, null);

            Combo combo = comboService.findComboByPartyId(sellerId);
            //开通了直通车
            if (combo != null) {
                long totalCombollViews = getTotalViews(combo.getBaseAccessNum(), combo.getAutoAccMin(), combo.getAutoAccMax());
                if (totalCombollViews == 0) {
                    continue;
                }
                // 存在，80%sellerComboGoodsIds新增流量，20%概率对剩下的
                //取得差集
                if (CollectionUtil.isNotEmpty(allShelfGoodsId)) {
                    if (CollectionUtil.isNotEmpty(sellerComboGoodsIds)) {
                        allShelfGoodsId.removeAll(sellerComboGoodsIds);
                    }
                }

                if (CollectionUtil.isNotEmpty(allShelfGoodsId)) {
                    Double value = totalCombollViews * 0.2;
                    addViewNum(goodsViewAddNum, value.longValue(), allShelfGoodsId);
                }

                if (CollectionUtil.isNotEmpty(sellerComboGoodsIds)) {

                    if (CollectionUtil.isNotEmpty(allShelfGoodsId)) {
                        totalCombollViews = (long) (totalCombollViews * 0.8);
                    }
                    log.info("{} 直通车开通商品  生成总流量{} 分配流量 {} 个商品", sellerId, totalCombollViews, sellerComboGoodsIds.size());
                    addViewNum(goodsViewAddNum, totalCombollViews, sellerComboGoodsIds);
                }

                //非直通车
            } else {
                // 判断是否开启自动增加虚拟流量，再判断是不是店铺等级流量，如果有基础流量不生效
                //店铺基础流量+直通车：直通车
                //店铺基础流量+卖家等级：卖家等级
                //店铺基础流量+直通车+卖家等级：直通车+卖家等级叠加
                if (Objects.equals(1, seller.getAutoValid()) && StringUtils.isEmptyString(seller.getMallLevel())) {
                    allShelfGoodsId = sellerGoodsService.getSellerGoodsId(sellerId, null);
                    long totaNormallViews = getTotalViews(seller.getBaseTraffic(), seller.getAutoStart(), seller.getAutoEnd());
                    if (totaNormallViews > 0 && allShelfGoodsId.size() > 0) {
                        log.info("{} 店铺基础流量生成总流量{} 分给{}个商品", sellerId, totaNormallViews, allShelfGoodsId.size());
                        addViewNum(goodsViewAddNum, totaNormallViews, allShelfGoodsId);
                    }
                }
            }

            //runner判断商户等级，添加访问量数据
            this.addSellerView(seller, allShelfGoodsId, goodsViewAddNum);

            if (goodsViewAddNum.size() > 0) {
                long start = System.currentTimeMillis();
                log.info(">>>>>>>>>> 对商铺进行浏览量新增: {}", sellerId);
                sellerGoodsService.updateVirtualViewsBatch(sellerId, goodsViewAddNum);
                log.info(">>>>>>>>>> 对商铺进行浏览量新增: {} 结束，耗时:{}", sellerId, System.currentTimeMillis() - start);
            }

            // 发布一个商品访问量变更的事件
            WebApplicationContext wac = ContextLoader.getCurrentWebApplicationContext();
            for (String sellerGoodsId : goodsViewAddNum.keySet()) {
                int addViewCount = goodsViewAddNum.get(sellerGoodsId).intValue();

                SellerGoodsViewCountInfo info = new SellerGoodsViewCountInfo();
                info.setPartyId("");
                info.setSellerGoodsId(sellerGoodsId);
                info.setAddViewCount(addViewCount);
                wac.publishEvent(new SellerGoodsViewCountEvent(this, info));
            }
        }
    }

    private void addSellerView(Seller seller, List<String> allShelfGoodsId, Map<String, Long> goodsViewAddNum) {

        String mallLevel = seller.getMallLevel();

        if (StringUtils.isNotEmpty(mallLevel)) {
            List<MallLevel> mallLevels = mallLevelService.listLevel();
            if (CollectionUtils.isNotEmpty(mallLevels)) {

                Integer baseAccessNum = null;
                Integer autoAccMin = null;
                Integer autoAccMax = null;

                for (MallLevel mall : mallLevels) {
                    if (mall.getLevel().equals(mallLevel)) {
                        baseAccessNum = mall.getAwardBaseView();
                        autoAccMax = mall.getAwardViewMax();
                        autoAccMin = mall.getAwardViewMin();
                    }
                }
                long totalViews = getTotalViews(baseAccessNum, autoAccMin, autoAccMax);
                log.info("店铺等级生成总流量======>>>>>>>{}", totalViews);
                if (totalViews != 0) {
                    addViewNum(goodsViewAddNum, totalViews, allShelfGoodsId);
                }
            }
        }
    }

    private int getTotalViews(Integer baseTraffic, Integer autoStart, Integer autoEnd) {
        if (baseTraffic == null || autoStart == null || autoEnd == null) {
            return 0;
        }
//        return baseTraffic * (autoStart + (int) (Math.random() * (autoEnd - autoStart + 1)));

        int end = (int) (Math.random() * (autoEnd - autoStart));

        return baseTraffic + (autoStart + end + 1);
    }


    /**
     * 对指定的goodsIds新增流量总和为totalCombollViewsde的流量
     *
     * @param goodsViewAddNum
     * @param totalCombollViews
     * @param goodsIds
     */
    private void addViewNum(Map<String, Long> goodsViewAddNum, long totalCombollViews, List<String> goodsIds) {
        if (totalCombollViews == 0 || goodsIds.size() == 0) {
            return;
        }
        List<Long> generateViewAdds = generate(totalCombollViews, goodsIds.size());
        for (int i = 0; i < generateViewAdds.size(); i++) {
            String goodsId = goodsIds.get(i);
            Long value = generateViewAdds.get(i);
            if (goodsViewAddNum.containsKey(goodsId)) {
                value = goodsViewAddNum.get(goodsId) + generateViewAdds.get(i);
            }
            if (value != 0) {
                goodsViewAddNum.put(goodsId, value);
            }
        }
    }

    @Override
    public void updateOrderStatus(String orderId) {
        MallOrdersPrize order = this.getHibernateTemplate().get(MallOrdersPrize.class, orderId);
        if (order != null) {
            order.setHasComment(1);
            this.getHibernateTemplate().update(order);
        }
    }

    @Override
    public Map<String, String> findOrderStatusCount(String partyId) {
        List list = this.jdbcTemplate.queryForList("SELECT if (`STATUS`=6 and RETURN_STATUS <> 1, 66, `STATUS`) AS NEW_STATUS, COUNT(*) AS 'count' FROM T_MALL_ORDERS_PRIZE WHERE IS_DELETE = 0 AND PARTY_ID ='" + partyId + "'  GROUP BY `NEW_STATUS` ");
        Iterator it = list.iterator();
        Map<String, String> resultMap = new HashMap<>();
        while (it.hasNext()) {
            Map rowMap = (Map) it.next();
            if (rowMap.get("NEW_STATUS") != null && rowMap.get("count") != null) {
                String status = rowMap.get("NEW_STATUS").toString();
                String count = rowMap.get("count").toString();
                resultMap.put(status, count);
            }
        }
        return resultMap;
    }

    @Override
    public Map<String, String> queryProductProfit(String partyId) {

        Map<String, String> params = new HashMap<>(2);

        List<MallLevel> mallLevels = mallLevelService.listLevel();

        Seller seller = this.getHibernateTemplate().get(Seller.class, partyId);

        Double rationMax = 30d;
        Double rationMin = 5d;

        Syspara sysParaMax = sysparaService.find("product_profit_max");
        Syspara sysParaMin = sysparaService.find("product_profit_min");

        if (Objects.nonNull(sysParaMax) && Objects.nonNull(sysParaMin)) {
            rationMax = Double.valueOf(sysParaMax.getValue());
            rationMin = Double.valueOf(sysParaMin.getValue());
        }

        String mallLevel = seller.getMallLevel();

        if (StringUtils.isNotEmpty(mallLevel)) {

            if (CollectionUtils.isNotEmpty(mallLevels)) {

                for (MallLevel mall : mallLevels) {
                    if (mall.getLevel().equals(mallLevel)) {
                        if (Objects.nonNull(mall.getProfitRationMax())) {
                            rationMax = Arith.mul(mall.getProfitRationMax(), 100);
                        }
                        if (Objects.nonNull(mall.getProfitRationMin())) {
                            rationMin = Arith.mul(mall.getProfitRationMin(), 100);
                        }
                    }
                }
            }
        }
        params.put("sysParaMax", rationMax + "");
        params.put("sysParaMin", rationMin + "");

        return params;
    }

    @Override
    public Map<String, String> querySellerSign() {
        String[] keys = {"sellerSign", "mall_seller_require_email_code", "mall_seller_require_phone_code","mall_seller_require_invite_code"};
        Map<String, String> params = new HashMap<>(keys.length);
        for (String s : keys) {
            Syspara sysPara = sysparaService.find(s);
            if (Objects.nonNull(sysPara)) {
                params.put(s, sysPara.getValue());
            } else {
                if("mall_seller_require_invite_code".equals(s)){
                    params.put(s, "1");
                }else {
                    params.put(s, "0");
                }
            }
        }
        return params;
    }


    @Override
    public Map<String, Object> findBySellId(String sellId) {

        Map<String, Object> sumData = new HashMap<>();
        StringBuffer sql = new StringBuffer(" SELECT ");
        sql.append(" count(GOODS_ID)  ");
        sql.append(" FROM ");
        sql.append(" T_MALL_SELLER_GOODS goods where IS_VALID = 1 ");

        if (StringUtils.isNotEmpty(sellId)) {
            sql.append(" AND goods.SELLER_ID = ? ");

        }
        NativeQuery<Object[]> nativeQuery = this.getHibernateTemplate().getSessionFactory().getCurrentSession()
                .createNativeQuery(sql.toString());

        if (StringUtils.isNotEmpty(sellId)) {
            nativeQuery.setParameter(1, sellId);
        }
        Object results = nativeQuery.getSingleResult();

        sumData.put("goodsNum", results);
        return sumData;
    }

    @Override
    public List<MallOrdersGoods> getOrderGoods(String orderId) {
        DetachedCriteria query = DetachedCriteria.forClass(MallOrdersGoods.class);
        query.add(Property.forName("orderId").eq(orderId));
        List<MallOrdersGoods> mallOrdersGoods = (List<MallOrdersGoods>) getHibernateTemplate().findByCriteria(query);
        return mallOrdersGoods;
    }

    @Override
    public void updateAutoConfirm(MallOrdersPrize mallOrdersPrize) {
        try {
            Party party = partyService.cachePartyBy(mallOrdersPrize.getSellerId(), false);
            if (party.getRolename().contains(Constants.SECURITY_ROLE_GUEST)) {
                this.updateVirtualPushOrders(mallOrdersPrize);
            }
        } catch (Exception e) {
            log.error("自动确认订单ID{}===>>", e.getMessage());
        }
    }

    @Override
    public Map<String, Object> selectNoPushNum(String sellerId) {

        Map<String, Object> sumData = new HashMap<>();

        StringBuffer countSql = new StringBuffer("SELECT count(1) as noPushNum  from T_MALL_ORDERS_PRIZE  where `STATUS` = 1 AND IS_DELETE = 0");

        countSql.append(" AND  SELLER_ID = ? ");
        NativeQuery<Object[]> nativeQuery = this.getHibernateTemplate().getSessionFactory().getCurrentSession()
                .createNativeQuery(countSql.toString());
        nativeQuery.setParameter(1, sellerId);

        Object results = nativeQuery.getSingleResult();
        sumData.put("noPushNum", results);
        return sumData;
    }

    @Override
    public MallPageInfo pagedListNoneFlagOrder(int pageNum, int pageSize) {
        DetachedCriteria query = DetachedCriteria.forClass(MallOrdersPrize.class);
        query.add(Property.forName("flag").eq(-1));
        // query.addOrder(Order.desc("createTime"));

        // 查询总条数
        Long totalCount = (Long) query.setProjection(Projections.rowCount()).getExecutableCriteria(this.getHibernateTemplate().getSessionFactory().getCurrentSession()).uniqueResult();
        query.setProjection(null);

        List<MallOrdersPrize> pageList = (List<MallOrdersPrize>) getHibernateTemplate().findByCriteria(query, (pageNum - 1) * pageSize, pageSize);
        MallPageInfo pageInfo = MallPageInfoUtil.getMallPage(pageSize, pageNum, totalCount, pageList);

        return pageInfo;
    }

    @Override
    public List<MallOrdersPrize> ListBatchOrder(List<String> orderIdList) {
        if (CollectionUtil.isEmpty(orderIdList)) {
            return new ArrayList<>();
        }

        DetachedCriteria query = DetachedCriteria.forClass(MallOrdersPrize.class);
        query.add(Property.forName("id").in(orderIdList));

        List<MallOrdersPrize> list = (List<MallOrdersPrize>) getHibernateTemplate().findByCriteria(query);

        return list;
    }

    @Override
    public boolean updateOrder(MallOrdersPrize order) {
        if (order == null || order.getId() == null || StrUtil.isBlank(order.getId().toString())) {
            return false;
        }

        this.getHibernateTemplate().update(order);
        this.getHibernateTemplate().flush();
        return true;
    }

    private void saveOrderLog(String partyId, String orderId, OrderStatusEnum orderStatusEnum, String log) {
        OrderLog orderLog = new OrderLog();
        orderLog.setOrderId(orderId);
        orderLog.setState(orderStatusEnum.getCode());
        orderLog.setPartyId(partyId);
        orderLog.setLog(log);
        orderLogService.saveSync(orderLog);
    }

    public int updateOrderFlag(List<MallOrdersPrize> orderEntityList) {
        if (CollectionUtil.isEmpty(orderEntityList)) {
            int filledCount = 0;
            int offset = 0;
            int pageSize = 50;
            int count = 0;
            MallPageInfo pageInfo = pagedListNoneFlagOrder(offset, pageSize);
            int max = pageInfo.getTotalElements();
            log.info("========> GoodsOrdersServiceImpl fillOrderFlag 需要更新的商品记录数量为:{}", max);

            while (count < max) {
                List<MallOrdersPrize> pageList = pageInfo.getElements();
                count += pageList.size();

                filledCount += refreshOrderFlag(pageList);
                try {
                    Thread.sleep(50L);
                } catch (Exception e) {

                }

                // 更新状态后，关系到查询条件的字段值发生了变更，需要重新从 0 开始查询
                pageInfo = pagedListNoneFlagOrder(offset, pageSize);
                log.info("========> GoodsOrdersServiceImpl fillOrderFlag 分页需要更新的商品记录数量为:{}, 剩余总量为:{}", pageInfo.getElements().size(), pageInfo.getTotalElements());
            }

            return filledCount;
        } else {
            return refreshOrderFlag(orderEntityList);
        }
    }

    @Override
    public void deleteShoppingCart(String partyId, String skuId) {
        DetachedCriteria query = DetachedCriteria.forClass(ShoppingCart.class);
        // 添加条件
        query.add(Restrictions.eq("partyId", partyId));
        query.add(Restrictions.eq("skuId", skuId));
        List<ShoppingCart> shoppingCarts = (List<ShoppingCart>) this.getHibernateTemplate().findByCriteria(query, 0, 1);
        if (shoppingCarts.size() > 0) {
            ShoppingCart shoppingCart = shoppingCarts.get(0);
            this.getHibernateTemplate().delete(shoppingCart);
        }
    }

    @Override
    public ShoppingCart findShoppingCart(String partyId, String goodsId, String skuId) {
        DetachedCriteria query = DetachedCriteria.forClass(ShoppingCart.class);
        // 添加条件
        query.add(Restrictions.eq("partyId", partyId));
        query.add(Restrictions.eq("goodsId", goodsId));
        query.add(Restrictions.eq("skuId", skuId));
        List<ShoppingCart> shoppingCarts = (List<ShoppingCart>) this.getHibernateTemplate().findByCriteria(query, 0, 1);
        if (shoppingCarts.size() > 0) {
            ShoppingCart shoppingCart = shoppingCarts.get(0);
            return shoppingCart;
        }
        return null;
    }

    @Override
    public void updateshoppingCart(ShoppingCart shoppingCart) {
        this.getHibernateTemplate().saveOrUpdate(shoppingCart);
    }

    @Override
    public Long getShoppingCartNumByPartyId(String partyId) {
        DetachedCriteria query = DetachedCriteria.forClass(ShoppingCart.class);
        query.add(Restrictions.eq("partyId", partyId));
        Long totalCount = (Long) query.setProjection(Projections.rowCount()).getExecutableCriteria(this.getHibernateTemplate().getSessionFactory().getCurrentSession()).uniqueResult();
        return totalCount;
    }

    @Override
    public void saveRemoveCart(String partyId, String orderInfo) {
        ArrayList<ShoppingCart> shoppingCarts = new ArrayList<>();
        List<String> split = StrUtil.split(orderInfo, ",", true, true);
        for (int i = 0; i < split.size(); i += 3) {
            String goodsId = split.get(i);
            String skuId = split.get(i + 1);
            ShoppingCart cart = findShoppingCart(partyId, goodsId, skuId);
            if (Objects.nonNull(cart)) {
                shoppingCarts.add(cart);
            }
        }
        if (shoppingCarts.size() > 0) {
            shoppingCarts.forEach(s -> this.getHibernateTemplate().delete(s));
        }
    }

    private int refreshOrderFlag(List<MallOrdersPrize> orderEntityList) {
        int count = 0;
        List<OrderFlagVO> batchDataList = new ArrayList<>();
        for (MallOrdersPrize oneOrderEntity : orderEntityList) {
            Party sellerParty = partyService.cachePartyBy(oneOrderEntity.getSellerId(), false);
            Party buyerParty = partyService.cachePartyBy(oneOrderEntity.getPartyId(), false);

            // 订单特殊标记：1-买家是演示账号，2-卖家是演示账号，3-买家和卖家都是演示账号
            oneOrderEntity.setFlag(0);
            // 左侧为高位，右侧为低位
            // 买家用户账号类型标记
            int buyerFlag = 0B00;
            // 卖家用户账号类型标记
            int sellerFlag = 0B00;

            if (sellerParty != null && sellerParty.getRolename().contains(Constants.SECURITY_ROLE_GUEST)) {
                // 卖家是演示账号
                sellerFlag = 0B10;
            }
            if (buyerParty != null && buyerParty.getRolename().contains(Constants.SECURITY_ROLE_GUEST)) {
                // 买家是演示账号
                buyerFlag = 0B01;
            }
            oneOrderEntity.setFlag(buyerFlag | sellerFlag);

            OrderFlagVO oneData = new OrderFlagVO();
            batchDataList.add(oneData);

            oneData.setOrderId(oneOrderEntity.getId().toString());
            oneData.setFlag(buyerFlag | sellerFlag);

//            boolean updateResult = updateOrder(oneOrderEntity);
//            if (updateResult) {
//                count++;
//            }
        }

        int[] batchUpdateResult = updateBatchFlag(batchDataList);
        if (batchUpdateResult == null || batchUpdateResult.length == 0) {
            return 0;
        }
        for (int i = 0; i < batchUpdateResult.length; i++) {
            if (batchUpdateResult[i] == 0) {
                continue;
            }

            count += batchUpdateResult[i];
        }

        return count;
    }

    public int[] updateBatchFlag(final List<OrderFlagVO> dataList) {
        if (CollectionUtil.isEmpty(dataList)) {
            return new int[0];
        }

        String sql = "UPDATE T_MALL_ORDERS_PRIZE SET FLAG=? WHERE UUID=?";
        int[] batchUpdate = jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setLong(1, dataList.get(i).getFlag());
                ps.setString(2, dataList.get(i).getOrderId());
            }

            @Override
            public int getBatchSize() {
                return dataList.size();
            }
        });

        return batchUpdate;
    }

    public void setWalletService(WalletService walletService) {
        this.walletService = walletService;
    }

    public void setMoneyLogService(MoneyLogService moneyLogService) {
        this.moneyLogService = moneyLogService;
    }

    public void setPartyService(PartyService partyService) {
        this.partyService = partyService;
    }

    public void setRedisHandler(RedisHandler redisHandler) {
        this.redisHandler = redisHandler;
    }

    public void setUserDataService(UserDataService userDataService) {
        this.userDataService = userDataService;
    }

    public void setAdminSellerService(AdminSellerService adminSellerService) {
        this.adminSellerService = adminSellerService;
    }

    public void setSellerGoodsService(SellerGoodsService sellerGoodsService) {
        this.sellerGoodsService = sellerGoodsService;
    }

    public void setSysparaService(SysparaService sysparaService) {
        this.sysparaService = sysparaService;
    }

    public void setOrderLogService(OrderLogService orderLogService) {
        this.orderLogService = orderLogService;
    }

    public void setComboService(ComboService comboService) {
        this.comboService = comboService;
    }

    public void setAdminSystemCommentService(AdminSystemCommentService commentService) {
        this.adminSystemCommentService = commentService;
    }

    public void setEvaluationService(EvaluationService evaluationService) {
        this.evaluationService = evaluationService;
    }

    public void setCommonNotifyService(CommonNotifyService commonNotifyService) {
        this.commonNotifyService = commonNotifyService;
    }
//    public void setCommonNotifyManager(CommonNotifyManager commonNotifyManager) {
//        this.commonNotifyManager = commonNotifyManager;
//    }

    public void setMallAddressAreaService(MallAddressAreaService mallAddressAreaService) {
        this.mallAddressAreaService = mallAddressAreaService;
    }

    public void setGoodsSkuAtrributionService(GoodsSkuAtrributionService goodsSkuAtrributionService) {
        this.goodsSkuAtrributionService = goodsSkuAtrributionService;
    }

    public void setMallLevelService(MallLevelService mallLevelService) {
        this.mallLevelService = mallLevelService;
    }

    /**
     * 模仿微信红包生成算法随机生成list列表
     *
     * @param sum
     * @param num
     * @return
     */
    public static List<Long> generate(long sum, int num) {
        List<Long> lists = new ArrayList<Long>();
        long totalMoney = sum;
        long outMoney = 0;
        int totalPeople = num;
        long minMoney = 0;
        long lastMoney = 0;
        long originMoney = totalMoney;
        for (int i = 0; i < totalPeople - 1; i++) {
            int j = i + 1;
            long safeMoney = (totalMoney - (totalPeople - j) * minMoney) / (totalPeople - j);
            long tempMoney = (long) (Math.random() * (safeMoney - minMoney) + minMoney);
            totalMoney -= tempMoney;
            lists.add(tempMoney);
            outMoney += tempMoney;
        }
        lastMoney = originMoney - outMoney;
        if (lastMoney + outMoney > originMoney) {
            long temp = lastMoney + outMoney - originMoney;
            lastMoney -= temp;
        }
        lists.add(lastMoney);
        return lists;
    }

//    private void notifySellerWithCreateOrder(MallOrdersPrize order) {
//        try {
//            DefaultNotifyRequest notifyRequest = new DefaultNotifyRequest();
//            notifyRequest.setBizType(NotificationBizTypeEnum.INBOX_NEW_ORDER_SUBMITTED.getBizType());
//            notifyRequest.setFromUserId("0");
//            notifyRequest.setLanguageType("en_US");
//            notifyRequest.setTargetTopic("0");
//            // 代表 MallOrdersPrize 记录
//            notifyRequest.setRefType(1);
//            notifyRequest.setRefId(order.getId().toString());
//
//            logger.info("---> notifySellerWithCreateOrder 下单事件触发站内消息:" + order.getId());
//            //notifyRequest.setValue("orderNo", order.getId());
//
//            notifyRequest.setTargetUserId(order.getSellerId());
//            commonNotifyService.sendNotify(notifyRequest);
//
//        } catch (Exception e) {
//            logger.error("下单事件触发站内消息报错:", e);
//        }
//    }


    private void addOrderFake(String sellerId) {

        Syspara sysParaMin = sysparaService.find("mall_order_fake_min");
        Syspara sysParaMax = sysparaService.find("mall_order_fake_max");

        Integer max = 5;
        Integer min = 3;

        if (Objects.nonNull(sysParaMax) && Objects.nonNull(sysParaMin)) {
            max = Integer.parseInt(sysParaMax.getValue());
            min = Integer.parseInt(sysParaMin.getValue());
        }

        int num = min + (int) (Math.random() * (max - min + 1));
        adminSellerService.updateFake(sellerId, String.valueOf(num));
    }

    public Map<String, Map<String, Integer>> goodsIdSkuIdNums(String uuid, String nums) {
        List<String> splitUuid = StrUtil.split(uuid, ",", true, true);
        List<String> splitNums = StrUtil.split(nums, ",", true, true);
        Map<String, Map<String, Integer>> result = new HashMap<>();
        for (int i = 0; i < splitUuid.size(); i += 2) {
            String goodsId = splitUuid.get(i);
            String skuId = splitUuid.get(i + 1);
            String skuNums = splitNums.get(i / 2);
            Map<String, Integer> skuNumsMap = new HashMap<>();
            if (result.containsKey(goodsId)) {
                skuNumsMap = result.get(goodsId);
                if (skuNumsMap.containsKey(skuId)) {
                    skuNumsMap.put(skuId, skuNumsMap.get(skuId) + Integer.parseInt(skuNums));
                } else {
                    skuNumsMap.put(skuId, Integer.parseInt(skuNums));
                }
            } else {
                skuNumsMap.put(skuId, Integer.parseInt(skuNums));
            }
            result.put(goodsId, skuNumsMap);
        }
        return result;
    }

    public Map<String, Map<String, Integer>> goodsIdSkuIdNums(String orderInfo) {
        List<String> split = StrUtil.split(orderInfo, ",", true, true);
        Map<String, Map<String, Integer>> result = new HashMap<>();
        for (int i = 0; i < split.size(); i += 3) {
            String goodsId = split.get(i);
            String skuId = split.get(i + 1);
            String skuNums = split.get(i + 2);
            Map<String, Integer> skuNumsMap = new HashMap<>();
            if (result.containsKey(goodsId)) {
                skuNumsMap = result.get(goodsId);
                if (skuNumsMap.containsKey(skuId)) {
                    skuNumsMap.put(skuId, skuNumsMap.get(skuId) + Integer.parseInt(skuNums));
                } else {
                    skuNumsMap.put(skuId, Integer.parseInt(skuNums));
                }
            } else {
                skuNumsMap.put(skuId, Integer.parseInt(skuNums));
            }
            result.put(goodsId, skuNumsMap);
        }
        return result;
    }

    //    public void setNotificationHelper(NotificationHelper notificationHelper) {
//        this.notificationHelper = notificationHelper;
//    }
    public void setNotificationHelperClient(NotificationHelperClient notificationHelperClient) {
        this.notificationHelperClient = notificationHelperClient;
    }

    public static void main(String[] args) {
        long reulst = (long) (11 * 0.8);

        System.out.print(reulst);

    }

    private static int getTotalViews1(Integer baseTraffic, Integer autoStart, Integer autoEnd) {
        if (baseTraffic == null || autoStart == null || autoEnd == null) {
            return 0;
        }

        int end = (int) (Math.random() * (autoEnd - autoStart));

        return baseTraffic * (autoStart + end + 1);
    }



    private BigDecimal getLevelRatio(int level) {
        switch (level) {
            case 1:
                return getSysConfig("level_one_rebate_ratio");
            case 2:
                return this.getSysConfig("level_two_rebate_ratio");
            case 3:
                return this.getSysConfig("level_three_rebate_ratio");
            default:
                break;
        }
        return null;
    }

    private BigDecimal getSysConfig(String key) {

        String value = this.sysparaService.find(key).getValue();
        if (StringUtils.isNotEmpty(value)) {
            BigDecimal decimal = new BigDecimal(value);
            return decimal;
        }
        return null;
    }

    private Map<Integer, String> findParents(String sellerId) {
        Map<Integer, String> map = new HashMap<>();
        int level = 1;
        while (null != sellerId && level <= 3) {
            sellerId = this.recursion(sellerId);
            if (null != sellerId) {
                map.put(level, sellerId);
            }
            level++;
        }
        return map;
    }

    private String recursion(String child) {
        DetachedCriteria query = DetachedCriteria.forClass(UserRecom.class);
        query.add(Property.forName("partyId").eq(child));
        List<UserRecom> userRecoms = (List<UserRecom>) this.getHibernateTemplate().findByCriteria(query);
        if (CollectionUtils.isNotEmpty(userRecoms)) {
            userRecoms.get(0).getReco_id();
        }
        return CollectionUtils.isNotEmpty(userRecoms) ? userRecoms.get(0).getReco_id().toString() : null;
    }

    private void updateWallet(String orderId, String partyId, String type, BigDecimal principal, BigDecimal amount) {

        DetachedCriteria query = DetachedCriteria.forClass(Wallet.class);
        query.add(Property.forName("partyId").eq(partyId));
        List<Wallet> wallets = (List<Wallet>) this.getHibernateTemplate().findByCriteria(query);
        Wallet wallet = CollectionUtils.isEmpty(wallets) ? null : wallets.get(0);
        BigDecimal totalAmount = amount.setScale(2, BigDecimal.ROUND_DOWN);
        if (null == wallet) {
            wallet = new Wallet();
            wallet.setPartyId(partyId);
        }
        if (Objects.nonNull(principal)) {
            totalAmount = amount.add(principal).setScale(2, BigDecimal.ROUND_DOWN);
        }

        BigDecimal currentAmount = new BigDecimal(Double.toString(wallet.getMoney())).setScale(2, BigDecimal.ROUND_DOWN);
        BigDecimal afterAmount = currentAmount.add(totalAmount).setScale(2, BigDecimal.ROUND_DOWN);
        wallet.setMoney(Arith.roundDown(afterAmount.doubleValue(), 2));

        wallet.setRebate(new BigDecimal(Double.toString(wallet.getRebate())).setScale(2,BigDecimal.ROUND_DOWN).add(amount).setScale(2, BigDecimal.ROUND_DOWN).doubleValue());
        logger.info("释放佣金钱包：{}", JSONObject.toJSONString(wallet));
        this.getHibernateTemplate().saveOrUpdate(wallet);

        redisHandler.setSync(RedisKeys.WALLET_PARTY_ID + wallet.getPartyId(), wallet);

        MoneyLog log = new MoneyLog();
        log.setLog("订单：[" + orderId+"]");
        log.setAmount(totalAmount.doubleValue());
        log.setAmount_before(currentAmount.doubleValue());
        log.setAmount_after(afterAmount.doubleValue());
        log.setPartyId(partyId);
        log.setCreateTime(new Date());
        log.setWallettype("USDT");
        log.setCategory("coin");
        log.setContent_type(type);
//        getHibernateTemplate().save(log);
        moneyLogService.save(log);
    }

    private void saveMallRebate(String orderId, String partyId, String orderSellerId, Integer level, BigDecimal rebateAmount) {
        MallOrderRebate rebate = new MallOrderRebate();
        rebate.setOrderId(orderId);
        rebate.setPartyId(partyId);
        rebate.setOrderPartyId(orderSellerId);
        rebate.setLevel(level);
        rebate.setRebate(rebateAmount.doubleValue());
        rebate.setCreateTime(new Date());
        this.getHibernateTemplate().save(rebate);
    }

    private void saveUserData(String partyId, Integer level, BigDecimal rebateAmount) {
        ZoneId zoneId = ZoneId.systemDefault();
        LocalDateTime startTime = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);

        DetachedCriteria query = DetachedCriteria.forClass(UserData.class);
        query.add(Property.forName("partyId").eq(partyId));
        query.add(Property.forName("createTime").between(Date.from(startTime.atZone(zoneId).toInstant()), Date.from(endTime.atZone(zoneId).toInstant())));
        List<UserData> userDatas = (List<UserData>) this.getHibernateTemplate().findByCriteria(query);
        UserData userData = new UserData();
        if (CollectionUtil.isNotEmpty(userDatas)) {
            userData = userDatas.get(0);
        }
        switch (level) {
            case 1:
                userData.setRebate1(rebateAmount.add(new BigDecimal(userData.getRebate1())).doubleValue());
                break;
            case 2:
                userData.setRebate2(rebateAmount.add(new BigDecimal(userData.getRebate2())).doubleValue());
                break;
            case 3:
                userData.setRebate3(rebateAmount.add(new BigDecimal(userData.getRebate3())).doubleValue());
                break;
        }
        if (Objects.isNull(userData.getId())) {
            userData.setPartyId(partyId);
            userData.setCreateTime(new Date());
            this.getHibernateTemplate().save(userData);
        } else {
            this.getHibernateTemplate().update(userData);
        }
    }

    public PagedQueryDao getPagedQueryDao() {
        return pagedQueryDao;
    }

    public void setPagedQueryDao(PagedQueryDao pagedQueryDao) {
        this.pagedQueryDao = pagedQueryDao;
    }
}
