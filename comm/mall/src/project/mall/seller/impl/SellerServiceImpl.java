package project.mall.seller.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import kernel.exception.BusinessException;
import kernel.util.Arith;
import kernel.util.JsonUtils;
import kernel.util.StringUtils;
import kernel.web.Page;
import kernel.web.PagedQueryDao;
import org.apache.commons.collections.CollectionUtils;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.hibernate.query.NativeQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;
import project.Constants;
import project.log.Log;
import project.log.LogService;
import project.log.MoneyLog;
import project.log.MoneyLogService;
import project.mall.MallRedisKeys;
import project.mall.activity.model.lottery.LotteryReceive;
import project.mall.log.model.OrderStatusEnum;
import project.mall.orders.model.MallOrdersPrize;
import project.mall.seller.SellerService;
import project.mall.seller.constant.UpgradeMallLevelCondParamTypeEnum;
import project.mall.seller.dto.MallLevelCondExpr;
import project.mall.seller.dto.SellerOrderLineDTO;
import project.mall.seller.model.MallLevel;
import project.mall.seller.model.Seller;
import project.mall.utils.MallPageInfo;
import project.mall.utils.MallPageInfoUtil;
import project.mall.utils.PlatformNameEnum;
import project.party.PartyRedisKeys;
import project.party.PartyService;
import project.party.UserMetricsService;
import project.party.model.Party;
import project.party.model.UserMetrics;
import project.redis.RedisHandler;
import project.syspara.SysparaService;
import project.tip.TipConstants;
import project.tip.TipService;
import project.wallet.Wallet;
import project.wallet.WalletService;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class SellerServiceImpl extends HibernateDaoSupport implements SellerService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource
    private RedisHandler redisHandler;

    @Resource
    private PagedQueryDao pagedQueryDao;

    @Resource
    private JdbcTemplate jdbcTemplate;

    @Resource
    private WalletService walletService;

    @Resource
    private MoneyLogService moneyLogService;

    @Resource
    private SysparaService sysparaService;

    @Resource
    private PartyService partyService;

    @Resource
    private TipService tipService;

    @Resource
    private UserMetricsService userMetricsService;

    @Resource
    private LogService logService;



    @Override
    public MallPageInfo listSeller(int pageNum, int pageSize, Integer isRec) {
        DetachedCriteria query = DetachedCriteria.forClass(Seller.class);
        // 2023-4-27 caster 过滤掉被注销的商铺
        query.add(Property.forName("status").eq(1));
//        2024-01-03 greg 过滤掉被拉黑店铺
        query.add(Property.forName("black").eq(0));
//        query.addOrder(Order.desc("createTime"));
        if (isRec != null) {
            query.add(Restrictions.gt("recTime", new Long(0)));
            query.addOrder(Order.desc("recTime"));
        }

        // 查询总条数
        Long totalCount = (Long) query.setProjection(Projections.rowCount()).getExecutableCriteria(this.getHibernateTemplate().getSessionFactory().getCurrentSession()).uniqueResult();
        query.setProjection(null);

        MallPageInfo mallPageInfo = MallPageInfoUtil.getMallPage(pageSize, pageNum, totalCount, getHibernateTemplate().findByCriteria(query, (pageNum - 1) * pageSize, pageSize));

        return mallPageInfo;
    }

    @Override
    public List<Seller> listAutoSellerRecTime() {
         DetachedCriteria query = DetachedCriteria.forClass(Seller.class);
        query.add(Property.forName("status").eq(1));
        query.add(Property.forName("recTime").gt(0L));
        List<Seller> results = (List<Seller>) getHibernateTemplate().findByCriteria(query, 0, 1000);
        return results;
    }

    @Override
    public void updateSeller(Seller seller) {
        getHibernateTemplate().update(seller);
    }

    @Override
    public Seller getSeller(String sellerId) {
        return getHibernateTemplate().get(Seller.class, sellerId);
    }

    @Override
    public List<Seller> getSellerBatch(List<String> idList) {
        DetachedCriteria criteria = DetachedCriteria.forClass(Seller.class);
        criteria.add(Restrictions.in("id", idList));
        List<Seller> list = (List<Seller>)this.getHibernateTemplate().findByCriteria(criteria);
        return list;
    }

    @Override
    public void saveSeller(Seller selller) {
        if (selller == null || selller.getId() == null) {
            throw new RuntimeException("缺少必须数据");
        }

        try {
            getHibernateTemplate().save(selller);
//            getHibernateTemplate().flush();
        } catch (Exception e) {
            // 并发场景下会出现主键冲突，如果不执行 clear 方法，以下的 get 获取的将不是数据库里的数据，而是前面 save 到 hibernate 缓存里的数据
//            getHibernateTemplate().clear();
            Seller existSeller = getHibernateTemplate().get(Seller.class, selller.getId());
            if (existSeller != null) {
                BeanUtil.copyProperties(existSeller, selller);
                return;
            }

            logger.error("SellerServiceImpl saveSeller 新建 goodsSeller 记录报错", e);
            throw e;
        }
    }

    @Override
    public List<Seller> querySearchKeyword(String keyword) {
        DetachedCriteria query = DetachedCriteria.forClass(Seller.class);
        query.add(Property.forName("name").like("%" + keyword + "%"));
        query.add(Property.forName("status").eq(1));
        query.add(Property.forName("black").eq(0));
        return (List<Seller>) getHibernateTemplate().findByCriteria(query);
    }

    @Override
    public Seller getByName(String sellerName) {
        if (StrUtil.isBlank(sellerName)) {
            return null;
        }

        DetachedCriteria query = DetachedCriteria.forClass(Seller.class);
        query.add(Property.forName("name").eq(sellerName.trim()));
        List<Seller> sellerList = (List<Seller>) getHibernateTemplate().findByCriteria(query);
        if (CollectionUtil.isEmpty(sellerList)) {
            return null;
        }
        if (sellerList.size() > 1) {
            logger.error("店铺名称为:" + sellerName + " 的商铺记录数量超过了一家:" + sellerList.size());
        }

        return sellerList.get(0);
    }

    @Override
    public Map<String, Object> findBySellId(String sellId) {

        //关注数量
        Map<String, Object> sumData = new HashMap<>();
        StringBuffer sql = new StringBuffer(" SELECT ");

        if (StringUtils.isEmptyString(sellId)) {
            sql.append(" IFNULL(sum(sellers.FAKE), 0), 0) as FAKE ,IFNULL(sum(sellers.CREDIT_SCORE), 0) , IFNULL(sum(sellers.REALS), 0), 0) as REALS CREDIT_SCORE  ");
            sql.append(" FROM ");
            sql.append(" T_MALL_SELLER  sellers ");
        } else {
            sql.append(" IFNULL(sellers.FAKE, 0) as FAKE , ");
            sql.append(" IFNULL(sellers.CREDIT_SCORE, 0) CREDIT_SCORE , IFNULL(sellers.REALS, 0) as REALS ");
            sql.append(" FROM ");
            sql.append(" T_MALL_SELLER  sellers ");
            sql.append(" where sellers.UUID = ? ");
        }
        NativeQuery<Object[]> nativeQuery = this.getHibernateTemplate().getSessionFactory().getCurrentSession()
                .createNativeQuery(sql.toString());

        if (StringUtils.isNotEmpty(sellId)) {
            nativeQuery.setParameter(1, sellId);
        }
        Object[] results = nativeQuery.getSingleResult();

        sumData.put("focusCount", results[0]);
        sumData.put("creditScore", results[1]);
        sumData.put("focusCountReals", results[2]);
        return sumData;
    }

    @Override
    public Map<String, Object> findByFocusSellId(String sellId) {

        //关注数量
        Map<String, Object> sumData = new HashMap<>();
        StringBuffer sql = new StringBuffer(" SELECT count(1) from T_MALL_FOCUS_SELLER  ");

        if (StringUtils.isNotEmpty(sellId)) {
            sql.append(" where SELLER_ID = ?");
        }

        NativeQuery<Object[]> nativeQuery = this.getHibernateTemplate().getSessionFactory().getCurrentSession()
                .createNativeQuery(sql.toString());

        if (StringUtils.isNotEmpty(sellId)) {
            nativeQuery.setParameter(1, sellId);
        }

        Object result = nativeQuery.getSingleResult();
        sumData.put("focusCount", result);
        return sumData;
    }

    @Override
    public List<SellerOrderLineDTO> findLineBySellId(String sellId, String startTime, String endTime) {
        StringBuffer countSalesSql = new StringBuffer("SELECT " +
                "cast(t1.CREATE_TIME as date) as dayString ," +
                " count(t1.UUID) AS countSales from T_MALL_ORDERS_PRIZE t1 where 1= 1  ");

        countSalesSql.append(" AND t1.STATUS IN(1,2,3,4,5) ");

        if (StringUtils.isNotEmpty(startTime) && StringUtils.isNotEmpty(endTime)) {
            countSalesSql.append(" AND t1.CREATE_TIME BETWEEN ? AND ? ");
        }

        if (StringUtils.isNotEmpty(sellId)) {
            countSalesSql.append(" AND t1.SELLER_ID = ? ");
        }

        countSalesSql.append("group by  cast(t1.CREATE_TIME as date) ");
        countSalesSql.append("order by t1.CREATE_TIME");

        NativeQuery<Object[]> nativeQuery = this.getHibernateTemplate().getSessionFactory().getCurrentSession()
                .createNativeQuery(countSalesSql.toString());

        nativeQuery.setParameter(1, startTime);
        nativeQuery.setParameter(2, endTime);

        if (StringUtils.isNotEmpty(sellId)) {
            nativeQuery.setParameter(3, sellId);
        }

        List<Object[]> results = nativeQuery.list();

        List<SellerOrderLineDTO> countSales = new ArrayList<>();

        for (Object[] obj : results) {
            SellerOrderLineDTO sellerOrderLineDTO = new SellerOrderLineDTO();
            sellerOrderLineDTO.setDayString(String.valueOf(obj[0]));
            sellerOrderLineDTO.setCountSales(String.valueOf(obj[1]));
            countSales.add(sellerOrderLineDTO);
        }

        List<SellerOrderLineDTO> countVisits = this.countVisits(sellId, startTime, endTime);

        Map<String, SellerOrderLineDTO> countSalesMap = new HashMap<>();
        Map<String, SellerOrderLineDTO> countVisitsMap = new HashMap<>();

        List<SellerOrderLineDTO> allResult = new ArrayList<>();

        Set<String> kesySet = new HashSet<>();

        if (CollectionUtils.isNotEmpty(countSales)) {
            countSalesMap = countSales.stream()
                    .collect(Collectors.toMap(SellerOrderLineDTO::getDayString, a -> a, (k1, k2) -> k1));

            kesySet.addAll(countSalesMap.keySet());
        }

        if (CollectionUtils.isNotEmpty(countVisits)) {
            countVisitsMap = countVisits.stream()
                    .collect(Collectors.toMap(SellerOrderLineDTO::getDayString, a -> a, (k1, k2) -> k1));
            kesySet.addAll(countVisitsMap.keySet());
        }

        if (CollectionUtils.isNotEmpty(kesySet)) {

            for (String key : kesySet) {
                SellerOrderLineDTO newDTO = new SellerOrderLineDTO();
                SellerOrderLineDTO salesDTO = countSalesMap.get(key);
                SellerOrderLineDTO visitsDTO = countVisitsMap.get(key);
                newDTO.setDayString(key);
                if (Objects.nonNull(salesDTO)) {
                    newDTO.setCountSales(salesDTO.getCountSales());
                }

                if (Objects.nonNull(visitsDTO)) {
                    newDTO.setCountVisits(visitsDTO.getCountVisits());
                }
                allResult.add(newDTO);
            }
        }

        if (CollectionUtils.isNotEmpty(allResult)) {
            allResult = allResult.stream().sorted(Comparator.comparing(SellerOrderLineDTO::getDayString)).collect(Collectors.toList());
        }

        return allResult;
    }

    @Override
    public List<SellerOrderLineDTO> findLineBySellIdAndHour(String sellId, String startTime, String endTime) {
        StringBuffer countSalesSql = new StringBuffer("SELECT " +
                "SUBSTRING(t1.CREATE_TIME, 1, 13) as dayString ," +
                " count(t1.UUID) AS countSales from T_MALL_ORDERS_PRIZE t1 where 1= 1  ");

        countSalesSql.append(" AND t1.STATUS IN(1,2,3,4,5) ");

        if (StringUtils.isNotEmpty(startTime) && StringUtils.isNotEmpty(endTime)) {
            countSalesSql.append(" AND t1.CREATE_TIME BETWEEN ? AND ? ");
        }

        if (StringUtils.isNotEmpty(sellId)) {
            countSalesSql.append(" AND t1.SELLER_ID = ? ");
        }

        countSalesSql.append("group by SUBSTRING(t1.CREATE_TIME, 1, 13) ");
        countSalesSql.append("order by t1.CREATE_TIME");

        NativeQuery<Object[]> nativeQuery = this.getHibernateTemplate().getSessionFactory().getCurrentSession()
                .createNativeQuery(countSalesSql.toString());

        nativeQuery.setParameter(1, startTime);
        nativeQuery.setParameter(2, endTime);
        if (StringUtils.isNotEmpty(sellId)) {
            nativeQuery.setParameter(3, sellId);
        }

        List<Object[]> results = nativeQuery.list();

        List<SellerOrderLineDTO> countSales = new ArrayList<>();

        for (Object[] obj : results) {
            SellerOrderLineDTO sellerOrderLineDTO = new SellerOrderLineDTO();
            sellerOrderLineDTO.setDayString(String.valueOf(obj[0]));
            sellerOrderLineDTO.setCountSales(String.valueOf(obj[1]));
            countSales.add(sellerOrderLineDTO);
        }

        List<SellerOrderLineDTO> countVisits = this.countVisitsByHour(sellId, startTime, endTime);

        Map<String, SellerOrderLineDTO> countSalesMap = new HashMap<>();
        Map<String, SellerOrderLineDTO> countVisitsMap = new HashMap<>();

        List<SellerOrderLineDTO> allResult = new ArrayList<>();

        Set<String> kesySet = new HashSet<>();

        if (CollectionUtils.isNotEmpty(countSales)) {
            countSalesMap = countSales.stream()
                    .collect(Collectors.toMap(SellerOrderLineDTO::getDayString, a -> a, (k1, k2) -> k1));

            kesySet.addAll(countSalesMap.keySet());
        }

        if (CollectionUtils.isNotEmpty(countVisits)) {
            countVisitsMap = countVisits.stream()
                    .collect(Collectors.toMap(SellerOrderLineDTO::getDayString, a -> a, (k1, k2) -> k1));
            kesySet.addAll(countVisitsMap.keySet());
        }

        if (CollectionUtils.isNotEmpty(kesySet)) {
            for (String key : kesySet) {
                SellerOrderLineDTO newDTO = new SellerOrderLineDTO();
                SellerOrderLineDTO salesDTO = countSalesMap.get(key);
                SellerOrderLineDTO visitsDTO = countVisitsMap.get(key);
                newDTO.setDayString(key);
                if (Objects.nonNull(salesDTO)) {
                    newDTO.setCountSales(salesDTO.getCountSales());
                }

                if (Objects.nonNull(visitsDTO)) {
                    newDTO.setCountVisits(visitsDTO.getCountVisits());
                }
                allResult.add(newDTO);
            }
        }

        if (CollectionUtils.isNotEmpty(allResult)) {
            allResult = allResult.stream().sorted(Comparator.comparing(SellerOrderLineDTO::getDayString)).collect(Collectors.toList());
        }

        return allResult;
    }

    private List<SellerOrderLineDTO> countVisits(String sellId, String startTime, String endTime) {
        StringBuffer countVisits = new StringBuffer("SELECT " +
                "cast(t1.CREATE_TIME as date) as dayString , " +
                "IFNULL(sum(VIEWS_NUM + VIRTUAL_VIEWS_NUM), 0)  " +
                "AS countVisits from T_MALL_SELLER_GOODS_STATISTICS  t1 " +
                "where 1= 1  ");

        if (StringUtils.isNotEmpty(startTime) && StringUtils.isNotEmpty(endTime)) {
            countVisits.append(" AND t1.CREATE_TIME BETWEEN ? AND ? ");
        }

        if (StringUtils.isNotEmpty(sellId)) {
            countVisits.append(" AND t1.SELLER_ID = ? ");
        }

        countVisits.append("group by  cast(t1.CREATE_TIME as date) ");
        countVisits.append("order by t1.CREATE_TIME");


        NativeQuery<Object[]> nativeQuery = this.getHibernateTemplate().getSessionFactory().getCurrentSession()
                .createNativeQuery(countVisits.toString());

        nativeQuery.setParameter(1, startTime);
        nativeQuery.setParameter(2, endTime);

        if (StringUtils.isNotEmpty(sellId)) {
            nativeQuery.setParameter(3, sellId);
        }

        List<Object[]> results = nativeQuery.list();

        List<SellerOrderLineDTO> dtos = new ArrayList<>();

        for (Object[] obj : results) {
            SellerOrderLineDTO sellerOrderLineDTO = new SellerOrderLineDTO();
            sellerOrderLineDTO.setDayString(String.valueOf(obj[0]));
            sellerOrderLineDTO.setCountVisits(String.valueOf(obj[1]));
            dtos.add(sellerOrderLineDTO);
        }

        return dtos;
    }

    private List<SellerOrderLineDTO> countVisitsByHour(String sellId, String startTime, String endTime) {
        StringBuffer countVisits = new StringBuffer("SELECT " +
                "SUBSTRING(t1.CREATE_TIME, 1, 13) as dayString , " +
                "IFNULL(sum(VIEWS_NUM + VIRTUAL_VIEWS_NUM), 0)  " +
                "AS countVisits from T_MALL_SELLER_GOODS_STATISTICS  t1 " +
                "where 1= 1  ");

        if (StringUtils.isNotEmpty(startTime) && StringUtils.isNotEmpty(endTime)) {
            countVisits.append(" AND t1.CREATE_TIME BETWEEN ? AND ? ");
        }

        if (StringUtils.isNotEmpty(sellId)) {
            countVisits.append(" AND t1.SELLER_ID = ? ");
        }

        countVisits.append("group by SUBSTRING(t1.CREATE_TIME, 1, 13) ");
        countVisits.append("order by t1.CREATE_TIME");

        NativeQuery<Object[]> nativeQuery = this.getHibernateTemplate().getSessionFactory().getCurrentSession()
                .createNativeQuery(countVisits.toString());

        nativeQuery.setParameter(1, startTime);
        nativeQuery.setParameter(2, endTime);
        if (StringUtils.isNotEmpty(sellId)) {
            nativeQuery.setParameter(3, sellId);
        }

        List<Object[]> results = nativeQuery.list();

        List<SellerOrderLineDTO> dtos = new ArrayList<>();

        for (Object[] obj : results) {
            SellerOrderLineDTO sellerOrderLineDTO = new SellerOrderLineDTO();
            sellerOrderLineDTO.setDayString(String.valueOf(obj[0]));
            sellerOrderLineDTO.setCountVisits(String.valueOf(obj[1]));
            dtos.add(sellerOrderLineDTO);
        }

        return dtos;
    }

    @Override
    public Map<String, Object> loadReportHead(String sellerId, String startTime, String endTime) {

        Map<String, Object> sumData = new HashMap<>();
        StringBuffer sql = new StringBuffer(" SELECT " +
                "count(UUID) as orderNum, " +
                "count(if(STATUS = 1, 1, null)) as orderCancel ," +
                "count(if(RETURN_STATUS = 2, 1, null)) as orderReturns ," +
                "cast((SUM(PRIZE_ORIGINAL)-SUM(SYSTEM_PRICE)) AS DECIMAL (19, 2)) AS totalProfit, " +
                "cast(sum(PRIZE_ORIGINAL) AS DECIMAL (19, 2)) AS totalSales," +
                "cast(IF (PAY_STATUS = 1 and STATUS <> 4 , PRIZE_ORIGINAL, 0)  AS DECIMAL (19, 2)) AS willIncome");
        sql.append(" FROM ");
        sql.append(" T_MALL_ORDERS_PRIZE  WHERE 1= 1");
        sql.append(" AND  STATUS IN(1,2,3,4,5) ");

        if (StringUtils.isNotEmpty(startTime) && StringUtils.isNotEmpty(endTime)) {
            sql.append(" AND CREATE_TIME BETWEEN ? AND ? ");
        }

        if (StringUtils.isNotEmpty(sellerId)) {
            sql.append(" AND SELLER_id = ? ");
        }

        NativeQuery<Object[]> nativeQuery = this.getHibernateTemplate().getSessionFactory().getCurrentSession()
                .createNativeQuery(sql.toString());

        nativeQuery.setParameter(1, startTime);
        nativeQuery.setParameter(2, endTime);

        if (StringUtils.isNotEmpty(sellerId)) {
            nativeQuery.setParameter(3, sellerId);
        }
        Object[] results = nativeQuery.getSingleResult();

        sumData.put("orderNum", results[0]);
        sumData.put("orderCancel", results[1]);
        sumData.put("orderReturns", results[2]);
        sumData.put("totalProfit", results[3]);
        sumData.put("totalSales", results[4]);
        sumData.put("willIncome", results[5]);
        return sumData;
    }


    @Override
    public Map<String, Object> loadReportTotalSales(String sellerId, String startTime, String endTime) {

        Map<String, Object> sumData = new HashMap<>();
        StringBuffer sql = new StringBuffer(" select cast(SUM(IFNULL(PRIZE_REAL,0)) AS DECIMAL (19, 3)) AS totalSales " +
                "  from T_MALL_ORDERS_PRIZE ");
        sql.append(" where  STATUS IN(1,2,3,4,5)  AND PURCH_STATUS =1 ");

        if (StringUtils.isNotEmpty(startTime) && StringUtils.isNotEmpty(endTime)) {
            sql.append(" AND CREATE_TIME BETWEEN ? AND ? ");
        }

        if (StringUtils.isNotEmpty(sellerId)) {
            sql.append(" AND SELLER_id = ? ");
        }

        NativeQuery<Object[]> nativeQuery = this.getHibernateTemplate().getSessionFactory().getCurrentSession()
                .createNativeQuery(sql.toString());

        nativeQuery.setParameter(1, startTime);
        nativeQuery.setParameter(2, endTime);

        if (StringUtils.isNotEmpty(sellerId)) {
            nativeQuery.setParameter(3, sellerId);
        }

        Object results = nativeQuery.getSingleResult();
        sumData.put("totalSales", results);

        if(Objects.nonNull(results)) {
            BigDecimal result = new BigDecimal(String.valueOf(sumData.get("totalSales"))).setScale(2, BigDecimal.ROUND_DOWN);
            sumData.put("totalSales", result);
        }

        return sumData;
    }

    @Override
    public Map<String, Object> loadReportWillIncome(String sellerId, String startTime, String endTime) {

        //待到账金额：已采购未释放的所有订单的本金+利润值
        Map<String, Object> sumData = new HashMap<>();
        StringBuffer sql = new StringBuffer(" select cast((SUM(IFNULL(SYSTEM_PRICE,0)) + SUM(IFNULL(PROFIT,0))) " +
                "AS DECIMAL (19, 3)) AS willIncome ");
        sql.append(" FROM T_MALL_ORDERS_PRIZE  WHERE PROFIT_STATUS = 0 AND STATUS <> 6 ");

        String platformName = sysparaService.find("platform_name").getValue();

        if(!PlatformNameEnum.ARGOSSHOP2.getDescription().equals(platformName)){
            sql.append(" AND PURCH_STATUS =1 ");
        }

        if (StringUtils.isNotEmpty(sellerId)) {
            sql.append(" AND SELLER_id = ? ");
        }

        if (StringUtils.isNotEmpty(startTime) && StringUtils.isNotEmpty(endTime)) {
            sql.append(" AND CREATE_TIME BETWEEN ? AND ? ");
        }

        NativeQuery<Object[]> nativeQuery = this.getHibernateTemplate().getSessionFactory().getCurrentSession()
                .createNativeQuery(sql.toString());

        if (StringUtils.isNotEmpty(sellerId)) {
            nativeQuery.setParameter(1, sellerId);
        }

        if (StringUtils.isNotEmpty(startTime) && StringUtils.isNotEmpty(endTime)) {
            nativeQuery.setParameter(2, startTime);
            nativeQuery.setParameter(3, endTime);
        }

        Object results = nativeQuery.getSingleResult();
        sumData.put("willIncome",results);
        if(Objects.nonNull(results)) {
            BigDecimal result = new BigDecimal(String.valueOf(sumData.get("willIncome"))).setScale(2, BigDecimal.ROUND_DOWN);
            sumData.put("willIncome", result);
        } else {
            sumData.put("willIncome",0.00);
        }
        return sumData;
    }
    @Override
    public Map<String, Object> loadReportOrderReturns(String sellerId, String startTime, String endTime) {

        //退货订单数量
        Map<String, Object> sumData = new HashMap<>();
        StringBuffer sql = new StringBuffer(" select count(1) from T_MALL_ORDERS_PRIZE where STATUS = 6 " );

        if (StringUtils.isNotEmpty(startTime) && StringUtils.isNotEmpty(endTime)) {
            sql.append(" AND CREATE_TIME BETWEEN ? AND ? ");
        }

        if (StringUtils.isNotEmpty(sellerId)) {
            sql.append(" AND SELLER_id = ? ");
        }

        NativeQuery<Object[]> nativeQuery = this.getHibernateTemplate().getSessionFactory().getCurrentSession()
                .createNativeQuery(sql.toString());

        nativeQuery.setParameter(1, startTime);
        nativeQuery.setParameter(2, endTime);

        if (StringUtils.isNotEmpty(sellerId)) {
            nativeQuery.setParameter(3, sellerId);
        }
        Object results = nativeQuery.getSingleResult();

        sumData.put("orderReturns", results);
        return sumData;
    }

    @Override
    public Map<String, Object> loadReportOrderCancel(String sellerId, String startTime, String endTime) {

        //退货订单数量
        Map<String, Object> sumData = new HashMap<>();
        StringBuffer sql = new StringBuffer(" select count(1) from T_MALL_ORDERS_PRIZE where STATUS = -1 " );

        if (StringUtils.isNotEmpty(startTime) && StringUtils.isNotEmpty(endTime)) {
            sql.append(" AND CREATE_TIME BETWEEN ? AND ? ");
        }

        if (StringUtils.isNotEmpty(sellerId)) {
            sql.append(" AND SELLER_id = ? ");
        }

        NativeQuery<Object[]> nativeQuery = this.getHibernateTemplate().getSessionFactory().getCurrentSession()
                .createNativeQuery(sql.toString());

        nativeQuery.setParameter(1, startTime);
        nativeQuery.setParameter(2, endTime);

        if (StringUtils.isNotEmpty(sellerId)) {
            nativeQuery.setParameter(3, sellerId);
        }
        Object results = nativeQuery.getSingleResult();

        sumData.put("orderCancel", results);
        return sumData;
    }

    @Override
    public Map<String, Object> loadReportOrderNum(String sellerId, String startTime, String endTime) {

        //总订单数
        Map<String, Object> sumData = new HashMap<>();
        StringBuffer sql = new StringBuffer(" select count(1) from T_MALL_ORDERS_PRIZE where 1 =1 " );

        if (StringUtils.isNotEmpty(startTime) && StringUtils.isNotEmpty(endTime)) {
            sql.append(" AND CREATE_TIME BETWEEN ? AND ? ");
        }

        if (StringUtils.isNotEmpty(sellerId)) {
            sql.append(" AND SELLER_id = ? ");
        }

        NativeQuery<Object[]> nativeQuery = this.getHibernateTemplate().getSessionFactory().getCurrentSession()
                .createNativeQuery(sql.toString());

        nativeQuery.setParameter(1, startTime);
        nativeQuery.setParameter(2, endTime);

        if (StringUtils.isNotEmpty(sellerId)) {
            nativeQuery.setParameter(3, sellerId);
        }
        Object results = nativeQuery.getSingleResult();

        sumData.put("orderNum", results);
        return sumData;
    }

    @Override
    public Map<String, Object> loadReportTotalProfit(String sellerId, String startTime, String endTime) {

        //总利润
        Map<String, Object> sumData = new HashMap<>();
        StringBuffer sql = new StringBuffer("select cast(SUM(IFNULL(PROFIT,0)) AS DECIMAL (19, 3)) AS totalProfit " +
                "  from T_MALL_ORDERS_PRIZE where PROFIT_STATUS = 1  " );

        sql.append(" AND  STATUS IN(1,2,3,4,5) ");

        if (StringUtils.isNotEmpty(startTime) && StringUtils.isNotEmpty(endTime)) {
            sql.append(" AND CREATE_TIME BETWEEN ? AND ? ");
        }

        if (StringUtils.isNotEmpty(sellerId)) {
            sql.append(" AND SELLER_id = ? ");
        }

        NativeQuery<Object[]> nativeQuery = this.getHibernateTemplate().getSessionFactory().getCurrentSession()
                .createNativeQuery(sql.toString());

        nativeQuery.setParameter(1, startTime);
        nativeQuery.setParameter(2, endTime);

        if (StringUtils.isNotEmpty(sellerId)) {
            nativeQuery.setParameter(3, sellerId);
        }
        Object results = nativeQuery.getSingleResult();

        sumData.put("totalProfit", results);

        if(Objects.nonNull(results)) {
            BigDecimal result = new BigDecimal(String.valueOf(sumData.get("totalProfit"))).setScale(2, BigDecimal.ROUND_DOWN);
            sumData.put("totalProfit", result);
        }

        return sumData;
    }

    @Override
    public MallPageInfo loadReportList(int pageNo, int pageSize, String sellerId, String startTime, String endTime) {

        Map<String, Object> parameters = new HashMap<>();

        StringBuffer queryString = new StringBuffer(" SELECT cast(CREATE_TIME as date) as dayString,count(UUID) as orderNum, " +
                "count(if(STATUS = -1, 1, null)) as orderCancel , " +
                "cast(SUM( CASE WHEN STATUS in (1,2,3,4,5) and PROFIT_STATUS = 1 THEN IFNULL(PROFIT,0) ELSE 0 END) AS DECIMAL (19, 2)) AS totalProfit," +
                "cast(SUM( CASE WHEN STATUS in (1,2,3,4,5) and PROFIT_STATUS = 1 THEN IFNULL(PRIZE_REAL,0) ELSE 0 END) AS DECIMAL (19, 2)) AS totalSales," +
                "count(if(STATUS = 6 , 1, null)) as orderReturns " +
                "FROM " +
                "T_MALL_ORDERS_PRIZE " +
                "WHERE IS_DELETE = 0 ");

        if (StringUtils.isNotEmpty(startTime) && StringUtils.isNotEmpty(endTime)) {
            queryString.append(" AND CREATE_TIME >= :startTime ");
            parameters.put("startTime", startTime);
            queryString.append(" AND CREATE_TIME <= :endTime  ");
            parameters.put("endTime", endTime);
        }

        if (StringUtils.isNotEmpty(sellerId)) {
            queryString.append(" AND SELLER_id = :sellerId ");
            parameters.put("sellerId", sellerId);
        }

        queryString.append("group by cast(CREATE_TIME as date) ");
        queryString.append("order by dayString DESC,CREATE_TIME");

        Page page = this.pagedQueryDao.pagedQuerySQL(pageNo, pageSize, queryString.toString(), parameters);

        StringBuffer countSql = new StringBuffer("select count(t.UUID) from ( SELECT UUID  ");
        countSql.append(" FROM ");
        countSql.append(" T_MALL_ORDERS_PRIZE ");
        countSql.append(" WHERE 1 = 1 ");

        if (StringUtils.isNotEmpty(startTime) && StringUtils.isNotEmpty(endTime)) {
            countSql.append(" AND CREATE_TIME >= '").append(startTime).append("'");
            countSql.append(" AND CREATE_TIME <= '").append(endTime).append("'");
        }

        if (StringUtils.isNotEmpty(sellerId)) {
            countSql.append(" AND SELLER_id = '").append(sellerId).append("'");
        }

        countSql.append("group by cast(CREATE_TIME as date) ");
        countSql.append("order by CREATE_TIME ");
        countSql.append(") t");

        int total = this.jdbcTemplate.queryForObject(countSql.toString(), Integer.class);

        MallPageInfo mallPageInfo = new MallPageInfo();
        mallPageInfo.setElements(page.getElements());
        mallPageInfo.setTotalElements(total);
        mallPageInfo.setPageSize(pageSize);
        mallPageInfo.setPageNum(pageNo);
        page.setTotalElements(total);
        return mallPageInfo;
    }


    @Override
    public Map<String, Object> loadReportStatus(String sellerId) {

        Map<String, Object> sumData = new HashMap<>();
        StringBuffer sql = new StringBuffer("select " +
                "count(UUID) as orderNum," +
                "count(if(STATUS = -1 , 1, null)) as orderCancel, " +
                "count(if(STATUS IN(4,5) , 1, null)) as orderFinish, " +
                "count(if(STATUS in(0,1,2,3), 1, null)) as orderIng, "+
                "count(if(STATUS = 6, 1, null)) as orderRefund ");
                //"count(if(STATUS in(0,1,2,3), 1, null)) as orderIng ");//caster修改
        sql.append(" FROM ");
        sql.append(" T_MALL_ORDERS_PRIZE WHERE IS_DELETE=0 ");

        if (StringUtils.isNotEmpty(sellerId)) {
            sql.append(" AND SELLER_ID = ? ");
        }

        NativeQuery<Object[]> nativeQuery = this.getHibernateTemplate().getSessionFactory().getCurrentSession()
                .createNativeQuery(sql.toString());

        if (StringUtils.isNotEmpty(sellerId)) {
            nativeQuery.setParameter(1, sellerId);
        }
        Object[] results = nativeQuery.getSingleResult();

        sumData.put("orderNum", results[0]);
        sumData.put("orderCancel", results[1]);
        sumData.put("orderFinish", results[2]);
        sumData.put("orderIng", results[3]);
        sumData.put("orderRefund", results[4]);
        return sumData;
    }

    /**
     * 0 冻结，1-正常
     *
     * @param id
     * @param status
     */
    public void updateFreezeState(String id, int status) {
        if (id == null || id.trim().isEmpty()) {
            return;
        }

        Session currentSession = getHibernateTemplate().getSessionFactory().getCurrentSession();
        String sql = " update T_MALL_SELLER set FREEZE= :status where uuid= :id ";
        NativeQuery query = currentSession.createSQLQuery(sql);

        query.setParameter("status", status);
        query.setParameter("id", id);

        query.executeUpdate();
    }

    @Override
    public boolean queryIsSellerBlack(String sellerId) {
        String isBlack = redisHandler.getString(PartyRedisKeys.PARTY_ID_SELLER_BLACK + sellerId);
        if ("1".equalsIgnoreCase(isBlack)) {
            return true;
        }
        return false;
    }

    /**
     * 设置商铺的虚假销量
     * @param id
     * @param fakeSoldNum
     */
    public void updateFakeSoldNum(String id, int fakeSoldNum) {
        if (id == null
                || id.trim().isEmpty()
                || fakeSoldNum < 0) {
            return;
        }

        Session currentSession = getHibernateTemplate().getSessionFactory().getCurrentSession();
        String sql = " update T_MALL_SELLER set FAKE_SOLD_NUM= :fakeSoldNum where uuid= :id ";
        NativeQuery query = currentSession.createSQLQuery(sql);

        query.setParameter("fakeSoldNum", fakeSoldNum);
        query.setParameter("id", id);

        query.executeUpdate();
    }

    @Override
    public void updateReceiveBonus(Seller seller, String recommendName) {
        if (seller.getRechargeBonusStatus()!=1) {
            throw new BusinessException("不满足领取条件");
        }
//        Wallet wallet = walletService.saveWalletByPartyId(seller.getId());
//        double amount_before = wallet.getMoney();
        final double rechargeBonus = seller.getRechargeBonus();

        //10/20 首充奖金 后台审核后发放
        seller.setRechargeBonusStatus(2);
        this.updateSeller(seller);

        Party party = this.partyService.cachePartyBy(seller.getId(), false);
        LotteryReceive receive = new LotteryReceive();
        receive.setCreateTime(new Date());
        receive.setApplyTime(new Date());
        receive.setPrizeType(2);//彩金类型2
        receive.setState(0);
        receive.setLotteryName("首充活动");
        receive.setActivityType(0);
        receive.setPartyId(seller.getId().toString());
        receive.setPartyName(party.getUsername());
        receive.setPrizeAmount(BigDecimal.valueOf(rechargeBonus));
        receive.setRecommendName(recommendName);
        receive.setSellerName(seller.getName());

        //保存申请彩金记录
        this.getHibernateTemplate().saveOrUpdate(receive);

        if (Constants.SECURITY_ROLE_MEMBER.equals(party.getRolename())) {
            this.tipService.saveTip(receive.getId().toString(), TipConstants.ACTIVITY_LOTTERY);
        }

//        //更新钱包余额
//        wallet.setMoney(Arith.add(wallet.getMoney(), rechargeBonus));
//        walletService.update(wallet.getPartyId().toString(), Arith.add(0, rechargeBonus));
//
//        MoneyLog moneyLog = new MoneyLog();
//        moneyLog.setCategory(Constants.MONEYLOG_CATEGORY_COIN);
//        moneyLog.setAmount_before(amount_before);
//        moneyLog.setAmount(Arith.add(0, rechargeBonus));
//        moneyLog.setAmount_after(wallet.getMoney());
//
//        moneyLog.setLog("首次充值赠送礼金:"+rechargeBonus);
//        moneyLog.setPartyId(seller.getId().toString());
//        moneyLog.setWallettype(Constants.WALLET);
//        moneyLog.setContent_type(Constants.MONEYLOG_CONTNET_FIRST_RECHARGE_BONUS);
//
//        moneyLogService.save(moneyLog);

    }

    public void updateInviteReceiveRwards(Seller seller, String username, String recommendName) {
        if (seller.getInviteAmountReward()<=0) {
            throw new BusinessException("不满足领取条件");
        }
//        Wallet wallet = walletService.saveWalletByPartyId(seller.getId());
//        double amount_before = wallet.getMoney();
        final double rechargeBonus = seller.getInviteAmountReward();
        seller.setInviteAmountReward(0);
        seller.setInviteReceivedReward(Arith.add(seller.getInviteReceivedReward(),rechargeBonus));
        this.updateSeller(seller);

        LotteryReceive receive = new LotteryReceive();
        receive.setCreateTime(new Date());
        receive.setApplyTime(new Date());
        receive.setPrizeType(2);//彩金类型2
        receive.setState(0);
        receive.setLotteryName("拉人活动");
        receive.setActivityType(0);
        receive.setPartyId(seller.getId().toString());
        receive.setPartyName(username);
        receive.setPrizeAmount(new BigDecimal(rechargeBonus));
        receive.setRecommendName(recommendName);
        receive.setSellerName(seller.getName());

        //保存申请彩金记录
        this.getHibernateTemplate().saveOrUpdate(receive);

        //更新钱包余额
//        wallet.setMoney(Arith.add(wallet.getMoney(), rechargeBonus));
//        walletService.update(wallet.getPartyId().toString(), Arith.add(0, rechargeBonus));
//
//        MoneyLog moneyLog = new MoneyLog();
//        moneyLog.setCategory(Constants.MONEYLOG_CATEGORY_COIN);
//        moneyLog.setAmount_before(amount_before);
//        moneyLog.setAmount(Arith.add(0, rechargeBonus));
//        moneyLog.setAmount_after(wallet.getMoney());
//
//        moneyLog.setLog("邀请奖励:"+rechargeBonus);
//        moneyLog.setPartyId(seller.getId().toString());
//        moneyLog.setWallettype(Constants.WALLET);
//        moneyLog.setContent_type(Constants.MONEYLOG_CONTNET_INVITATION_REWARDS);
//
//        moneyLogService.save(moneyLog);

        Party party = this.partyService.cachePartyBy(seller.getId(), false);
        if (Constants.SECURITY_ROLE_MEMBER.equals(party.getRolename())) {
            this.tipService.saveTip(receive.getId().toString(), TipConstants.ACTIVITY_LOTTERY);
        }
    }

    @Override
    public void updateSellerLevel(Seller seller, MallLevel buyLevel) {
        try {

            Double price = buyLevel.getPrice();

            Wallet wallet = walletService.selectOne(seller.getId().toString());
            if (Objects.isNull(wallet)) {
                throw new BusinessException("余额不足");
            }
            double amount_before = 0D;
            if (wallet.getFrozenState() == 0) {
                amount_before = wallet.getMoney();
            } else {//处于冻结状态时
                amount_before = wallet.getMoneyAfterFrozen();
            }

            if (amount_before < price) {
                throw new BusinessException("余额不足");
            }

            if (wallet.getFrozenState() == 0) {
                wallet.setMoney(Arith.roundDown(Arith.sub(wallet.getMoney(), price), 2));
            } else {//处于冻结状态时
                wallet.setMoneyAfterFrozen(Arith.roundDown(Arith.sub(wallet.getMoneyAfterFrozen(), price), 2));
            }

//            walletService.update(wallet.getPartyId().toString(), Arith.sub(0, prize));
            walletService.update(wallet);

            MoneyLog moneyLog = new MoneyLog();
            moneyLog.setCategory(Constants.MONEYLOG_CATEGORY_COIN);
            moneyLog.setAmount_before(amount_before);
            moneyLog.setAmount(Arith.sub(0, price));
            if (wallet.getFrozenState() == 0) {
                moneyLog.setAmount_after(wallet.getMoney());
                moneyLog.setFreeze(0);
            } else {//处于冻结状态时
                moneyLog.setAmount_after(wallet.getMoneyAfterFrozen());
                moneyLog.setFreeze(1);
            }

            moneyLog.setLog("购买商家等级[" + buyLevel.getLevel() + "]");
            moneyLog.setPartyId(seller.getId());
            moneyLog.setWallettype(Constants.WALLET);
            moneyLog.setContent_type(Constants.MONEYLOG_CONTNET_PAY_LEVEL);
            moneyLogService.save(moneyLog);

            seller.setMallLevel(buyLevel.getLevel());
            getHibernateTemplate().update(seller);


            //        更新累计充值金额
            UserMetrics userMetrics = this.userMetricsService.getByPartyId(seller.getId().toString());
            double beforeAmount = 0D;
            double afterAmount = 0D;
            if (userMetrics == null) {
                userMetrics = new UserMetrics();
                userMetrics.setAccountBalance(0.0D);
                userMetrics.setMoneyRechargeAcc(0.0D);
                userMetrics.setMoneyWithdrawAcc(0.0D);
                userMetrics.setPartyId(seller.getId().toString());
                userMetrics.setStatus(1);
                userMetrics.setTotleIncome(0.0D);
                userMetrics.setCreateTime(new Date());
                userMetrics.setUpdateTime(new Date());
                userMetrics.setStoreMoneyRechargeAcc(0D);
                userMetrics = userMetricsService.save(userMetrics);
            }

            MallLevelCondExpr mallLevelCondExpr = JsonUtils.json2Object(buyLevel.getCondExpr(), MallLevelCondExpr.class);
            List<MallLevelCondExpr.Param> params = mallLevelCondExpr.getParams();

            for (MallLevelCondExpr.Param param : params) {
                if (param.getCode().equals(UpgradeMallLevelCondParamTypeEnum.RECHARGE_AMOUNT.getCode())){
                    userMetrics.setStoreMoneyRechargeAcc(Double.parseDouble(param.getValue()));
                }
            }
            beforeAmount = new BigDecimal(userMetrics.getStoreMoneyRechargeAcc()).setScale(2,BigDecimal.ROUND_DOWN).doubleValue();
            userMetricsService.update(userMetrics);
            afterAmount = new BigDecimal(userMetrics.getStoreMoneyRechargeAcc()).setScale(2,BigDecimal.ROUND_DOWN).doubleValue();

            Log log = new Log();
            log.setCategory(Constants.LOG_CATEGORY_SECURITY);
            log.setLog("购买店铺等级修改升级累计有效充值金额，修改前金额为" +beforeAmount + "修改后金额为："+afterAmount);
            log.setPartyId(seller.getId().toString());
            log.setUsername(partyService.cachePartyBy(seller.getId(),true).getUsername());
            logService.saveAsyn(log);
        } catch (BusinessException e) {
            logger.error("购买等级失败", e);
//            redisHandler.remove(lockKey);
            throw new BusinessException(e.getMessage());
        }

    }

    @Override
    public void updateAutoSellerInfo() {
        //一次跟更新100条，查询出需要更新的店铺ID
        Integer result = 0;
        NamedParameterJdbcTemplate namedParameterJdbcTemplate1 = new NamedParameterJdbcTemplate(jdbcTemplate);
        List<Map<String, Object>> lists = namedParameterJdbcTemplate1.queryForList("SELECT UUID FROM T_MALL_SELLER WHERE UP_TIME < CURRENT_DATE LIMIT 100", Collections.emptyMap());
        List<String> sellerList = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(lists)) {
            for (Map<String, Object> map : lists) {
                for (Map.Entry<String, Object> entry : map.entrySet()) {
                    if (Objects.nonNull(entry.getValue())) {
                        sellerList.add((String) entry.getValue());
                    }
                }
            }

//            1.更新好评率
            NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
            Map<String, Object> params = new HashMap<>();
            params.put("sellerList", sellerList);
            StringBuffer sql = new StringBuffer();
            sql.append(" UPDATE T_MALL_SELLER seller ");
            sql.append(" JOIN ( ");
            sql.append("  SELECT");
            sql.append("    t1.sellerId,TRUNCATE (t1.good_count / t2.total_count, 2) AS rate ");
            sql.append("  FROM ");
            sql.append("    ( SELECT SELLER_ID AS sellerId, COUNT(UUID) good_count FROM T_MALL_EVALUATION WHERE RATING >= 4 GROUP BY sellerId ) t1 ");
            sql.append("    LEFT JOIN ( SELECT SELLER_ID AS sellerId, COUNT(UUID) total_count FROM T_MALL_EVALUATION GROUP BY sellerId ) t2 ON t1.sellerId = t2.sellerId ");
            sql.append(" ) rates ON seller.UUID = rates.sellerId  ");
            sql.append(" SET seller.HIGH_OPINION = rates.rate WHERE seller.UUID IN (:sellerList) ");
            result = namedParameterJdbcTemplate.update(sql.toString(), params);
            logger.info("被更新店铺好评率的店铺个数为："+result);

//            2.更新店铺商品数量
            StringBuffer goodsCountSql = new StringBuffer();
            goodsCountSql.append(" UPDATE T_MALL_SELLER seller ");
            goodsCountSql.append(" JOIN ( ");
            goodsCountSql.append(" 	SELECT ");
            goodsCountSql.append(" 		m.SELLER_ID AS sellerId, ");
            goodsCountSql.append(" 		COUNT( m.GOODS_ID ) AS goodsCount ");
            goodsCountSql.append(" 	FROM ");
            goodsCountSql.append(" 		T_MALL_SELLER_GOODS m ");
            goodsCountSql.append(" 	WHERE ");
            goodsCountSql.append(" 		m.IS_SHELF = 1 ");
            goodsCountSql.append(" 		AND m.IS_VALID = 1 ");
            goodsCountSql.append(" 	GROUP BY ");
            goodsCountSql.append(" 		m.SELLER_ID ");
            goodsCountSql.append(" 	) gooodCount ON seller.uuid = gooodCount.sellerId ");
            goodsCountSql.append(" SET seller.SELLER_GOODS_NUM = gooodCount.goodsCount WHERE seller.UUID IN (:sellerList) ");
            int goodsCount = namedParameterJdbcTemplate.update(goodsCountSql.toString(), params);
            logger.info("被更新店铺商品数量的店铺个数为："+goodsCount);

            StringBuffer focusNumSql = new StringBuffer();
            focusNumSql.append(" UPDATE T_MALL_SELLER seller ");
            focusNumSql.append(" JOIN ( ");
            focusNumSql.append(" 	SELECT ");
            focusNumSql.append(" 		m.SELLER_ID AS sellerId, ");
            focusNumSql.append(" 		COUNT(1) AS fcousCount  ");
            focusNumSql.append(" 	FROM ");
            focusNumSql.append(" 		T_MALL_FOCUS_SELLER m  ");
            focusNumSql.append(" 	WHERE m.SELLER_ID in (:sellerList) ");
            focusNumSql.append(" 	GROUP BY ");
            focusNumSql.append(" 		m.SELLER_ID  ");
            focusNumSql.append(" 	) focus ON seller.uuid = focus.sellerId  ");
            focusNumSql= focusNumSql.append(" SET seller.FOCUS_NUM = focus.fcousCount WHERE seller.UUID in (:sellerList) ");
            int updateFocusNum = namedParameterJdbcTemplate.update(focusNumSql.toString(), params);
            logger.info("被更新店铺实际关注数的店铺个数为：" + updateFocusNum);

//         3.更新实际销量
            StringBuffer soldNumSql = new StringBuffer();
            soldNumSql.append(" UPDATE T_MALL_SELLER seller ");
            soldNumSql.append(" JOIN ( ");
            soldNumSql.append(" 	SELECT ");
            soldNumSql.append(" 		SELLER_ID AS sellerId, ");
            soldNumSql.append(" 		IFNULL( SUM( GOODS__COUNT ), 0 ) AS goodsCount ");
            soldNumSql.append(" 	FROM ");
            soldNumSql.append(" 		T_MALL_ORDERS_PRIZE ");
            soldNumSql.append(" 	WHERE ");
            soldNumSql.append(" 		STATUS IN ( 4, 5 ) ");
            soldNumSql.append(" 	GROUP BY ");
            soldNumSql.append(" 		sellerId ");
            soldNumSql.append(" 	) soldNums ON seller.UUID = soldNums.sellerId ");
            soldNumSql.append(" SET seller.SOLD_NUM = soldNums.goodsCount WHERE seller.UUID IN (:sellerList) ");
            int updateSoldNum = namedParameterJdbcTemplate.update(soldNumSql.toString(), params);
            logger.info("被更新店铺实际销量的店铺个数为：" + updateSoldNum);

//            4.标记被更新店铺
            StringBuffer upSeller = new StringBuffer();
            upSeller.append(" UPDATE T_MALL_SELLER seller ");
            upSeller.append(" SET seller.UP_TIME =NOW() WHERE seller.UUID IN (:sellerList) ");
            int upSellerNum = namedParameterJdbcTemplate.update(upSeller.toString(), params);
            logger.info("被更新店铺信息的店铺个数为："+upSellerNum);
        }
    }

    public void setPagedQueryDao(PagedQueryDao pagedQueryDao) {
        this.pagedQueryDao = pagedQueryDao;
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void setWalletService(WalletService walletService) {
        this.walletService = walletService;
    }

    public void setMoneyLogService(MoneyLogService moneyLogService) {
        this.moneyLogService = moneyLogService;
    }

    public void setSysparaService(SysparaService sysparaService) {
        this.sysparaService = sysparaService;
    }

    public void setRedisHandler(RedisHandler redisHandler) {
        this.redisHandler = redisHandler;
    }

    public void setPartyService(PartyService partyService) {
        this.partyService = partyService;
    }

    public void setTipService(TipService tipService) {
        this.tipService = tipService;
    }

    /*public void setWalletLogService(WalletLogService walletLogService) {
        this.walletLogService = walletLogService;
    }*/


    public void setUserMetricsService(UserMetricsService userMetricsService) {
        this.userMetricsService = userMetricsService;
    }

    public void setLogService(LogService logService) {
        this.logService = logService;
    }
}
