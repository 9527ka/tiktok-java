package project.wallet.internal;

import cn.hutool.core.collection.CollectionUtil;
import com.github.benmanes.caffeine.cache.Cache;
import kernel.cache.LocalCachePool;
import kernel.constants.LocalCacheBucketKey;
import kernel.web.Page;
import kernel.web.PagedQueryDao;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Property;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;
import project.Constants;
import project.RedisKeys;
import project.redis.RedisHandler;
import project.user.kyc.KycHighLevel;
import project.wallet.Wallet;
import project.wallet.WalletLog;
import project.wallet.WalletLogService;
import project.wallet.dto.RechargePartyDTO;
import project.wallet.dto.RechargePartyResultDTO;
import util.cache.CacheOperation;
import util.concurrent.gofun.core.FunParams;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

public class WalletLogServiceImpl extends HibernateDaoSupport implements WalletLogService {

    private Logger log = LoggerFactory.getLogger(WalletLogServiceImpl.class);

    private PagedQueryDao pagedQueryDao;
    private RedisHandler redisHandler;
    private JdbcTemplate jdbcTemplate;

    @Override
    public void save(WalletLog entity) {
        entity.setCreateTime(new Date());
        getHibernateTemplate().setCheckWriteOperations(false);
        getHibernateTemplate().save(entity);
        redisHandler.setSync(RedisKeys.WALLET_LOG_ORDERNO + entity.getOrder_no(), entity);
//        getHibernateTemplate().flush();
    }

    @Override
    public WalletLog find(String category, String order_no) {
        return find(order_no);
    }


    private Object[] today() {
        LocalDateTime start = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime end = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
        return new Object[]{
                Date.from(start.atZone(ZoneId.systemDefault()).toInstant()),
                Date.from(end.atZone(ZoneId.systemDefault()).toInstant())
        };
    }

    public WalletLog findById(String id) {
        WalletLog entity = getHibernateTemplate().get(WalletLog.class, id);
        return entity;
    }

    public WalletLog find(String order_no) {
        if (StringUtils.isBlank(order_no)) {
            return null;
        }

        WalletLog walletLog = (WalletLog) redisHandler.get(RedisKeys.WALLET_LOG_ORDERNO + order_no);
        if (walletLog != null) {
            return walletLog;
        }

        DetachedCriteria query = DetachedCriteria.forClass(WalletLog.class);
        query.add(Property.forName("order_no").eq(order_no.trim()));

        List<WalletLog> list = (List<WalletLog>) getHibernateTemplate().findByCriteria(query, 0, 1);
        if (list == null || list.isEmpty()) {
            return null;
        }
        walletLog = list.get(0);

        redisHandler.setSync(RedisKeys.WALLET_LOG_ORDERNO + walletLog.getOrder_no(), walletLog);
        return walletLog;
    }

    @Override
    public void update(WalletLog entity) {
        this.getHibernateTemplate().update(entity);
        redisHandler.setSync(RedisKeys.WALLET_LOG_ORDERNO + entity.getOrder_no(), entity);

    }

    public void updateStatus(String orderNo, int status) {
        // 日志状态更新
        WalletLog walletLog = this.find(orderNo);
        if (null == walletLog) {
            log.error("walletLog is not exist,order_no:{}", orderNo);
        } else {
            walletLog.setStatus(status);
            this.update(walletLog);
        }
    }

    @Override
    public Page pagedQuery(int pageNo, int pageSize, String partyId, String category, String order_no_null) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        StringBuffer queryString = new StringBuffer("SELECT ");
        queryString.append(
                "log.WALLETTYPE AS wallettype,log.AMOUNT AS amount,log.USDT_AMOUNT as usdtAmount,log.STATUS AS status,DATE_FORMAT(log.CREATE_TIME,'%Y-%m-%d %H:%i:%S') as createTimeStr, ");
        queryString.append("withdraw.FAILURE_MSG AS withdraw_failure_msg,"
                + " recharge.DESCRIPTION AS recharge_failure_msg,log.ORDER_NO order_no ");
        queryString.append("FROM T_WALLET_LOG AS log ");
        queryString.append("LEFT JOIN T_WITHDRAW_ORDER AS withdraw ON withdraw.ORDER_NO = log.ORDER_NO ");
        queryString.append("LEFT JOIN T_RECHARGE_BLOCKCHAIN_ORDER AS recharge ON recharge.ORDER_NO = log.ORDER_NO ");

        queryString.append("WHERE 1=1  ");
        queryString.append(" and log.PARTY_ID = :partyId  ");
        parameters.put("partyId", partyId);

        if (StringUtils.isNotEmpty(category)) {
            queryString.append(" and log.CATEGORY = :category ");
            parameters.put("category", category);
        }
        if ("1".equals(order_no_null)) {
            queryString.append(" and log.ORDER_NO is not null and log.ORDER_NO !='' ");
        }
        queryString.append(" order by log.CREATE_TIME desc ");

        Page page = this.pagedQueryDao.pagedQuerySQL(pageNo, pageSize, queryString.toString(), parameters);
        return page;
    }

    @Override
    public Page pagedQueryWithdraw(int pageNo, int pageSize, String partyId, String order_no_null) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("partyId", partyId);
        parameters.put("category", "withdraw");

        StringBuffer queryString = new StringBuffer("SELECT ");

        queryString.append(
                " log.ORDER_NO AS order_no, log.WALLETTYPE AS coin, withdraw.AMOUNT AS amount, withdraw.ARRIVAL_AMOUNT AS arrivalAmount, "
                        + " withdraw.SUCCEEDED AS state, DATE_FORMAT(log.CREATE_TIME,'%Y-%m-%d %H:%i:%S') as createTime, ");
        queryString.append(" withdraw.AMOUNT_FEE AS fee, withdraw.CHAIN_ADDRESS AS 'to', withdraw.FAILURE_MSG AS failure_msg, withdraw.TX AS tx, "
                + " withdraw.VOLUME AS volume, withdraw.METHOD AS coin_blockchain, DATE_FORMAT(withdraw.REVIEWTIME,'%Y-%m-%d %H:%i:%S') as reviewTime, "
                + " withdraw.bank as bank, withdraw.account as bankCardNo, withdraw.username as bankUserName, "
                + " withdraw.ROUTING_NUM as routingNum, withdraw.ACCOUNT_ADDRESS accountAddress, withdraw.BANK_ADDRESS AS bankAddress ,withdraw.COUNTRY_NAME as countryName ");
        queryString.append("FROM T_WALLET_LOG AS log ");
        queryString.append("LEFT JOIN T_WITHDRAW_ORDER AS withdraw ON withdraw.ORDER_NO = log.ORDER_NO ");
        queryString.append("WHERE 1=1  ");
        queryString.append(" and log.PARTY_ID = :partyId  ");
        queryString.append(" and log.CATEGORY = :category ");

        if ("1".equals(order_no_null)) {
            queryString.append(" and log.ORDER_NO is not null and log.ORDER_NO !='' ");
        }
        queryString.append(" order by log.CREATE_TIME desc ");

        Page page = this.pagedQueryDao.pagedQuerySQL(pageNo, pageSize, queryString.toString(), parameters);

        StringBuffer countSql = new StringBuffer("SELECT count(*) ");
        countSql.append("FROM T_WALLET_LOG AS log ");
        countSql.append("LEFT JOIN T_WITHDRAW_ORDER AS withdraw ON withdraw.ORDER_NO = log.ORDER_NO ");
        countSql.append("WHERE log.PARTY_ID = '").append(partyId).append("' ");
        countSql.append(" and log.CATEGORY = '").append("withdraw").append("' ");
        if ("1".equals(order_no_null)) {
            countSql.append(" and log.ORDER_NO is not null and log.ORDER_NO !='' ");
        }
        int total = this.jdbcTemplate.queryForObject(countSql.toString(), Integer.class);
        page.setTotalElements(total);

        return page;
    }

    @Override
    public Page pagedQueryRecharge(int pageNo, int pageSize, String partyId, String order_no_null) {

        Map<String, Object> parameters = new HashMap<String, Object>();

        StringBuffer queryString = new StringBuffer("SELECT ");
        queryString.append(
                "log.ORDER_NO AS order_no, log.WALLETTYPE AS coin, recharge.AMOUNT AS amount, recharge.SUCCEEDED AS state, DATE_FORMAT(recharge.CREATED,'%Y-%m-%d %H:%i:%S') as createTime, ");
        queryString.append("recharge.DESCRIPTION AS failure_msg, recharge.TX AS hash, recharge.ADDRESS AS address, recharge.CHANNEL_ADDRESS AS channel_address, recharge.CHANNEL_AMOUNT AS volume, recharge.IMG img, ");
        queryString.append("recharge.COIN AS symbol, recharge.BLOCKCHAIN_NAME AS blockchain_name, recharge.TX AS tx, DATE_FORMAT(recharge.REVIEWTIME,'%Y-%m-%d %H:%i:%S') as reviewTime ");

        queryString.append("FROM T_WALLET_LOG AS log ");
        queryString.append("LEFT JOIN T_RECHARGE_BLOCKCHAIN_ORDER AS recharge ON recharge.ORDER_NO = log.ORDER_NO ");

        queryString.append("WHERE case when recharge.ORDER_NO is null then '' else recharge.ORDER_NO > '' end ");
        queryString.append(" and log.PARTY_ID = :partyId ");
        parameters.put("partyId", partyId);

        queryString.append(" and log.CATEGORY = :category ");
        parameters.put("category", "recharge");

        if ("1".equals(order_no_null)) {
            queryString.append(" and case when log.ORDER_NO is null then '' else log.ORDER_NO > '' end ");
        }

        queryString.append(" order by log.CREATE_TIME desc ");

        Page page = this.pagedQueryDao.pagedQuerySQL(pageNo, pageSize, queryString.toString(), parameters);

        StringBuffer countSql = new StringBuffer("SELECT count(*) ");
        countSql.append("FROM T_WALLET_LOG AS log ");
        countSql.append("LEFT JOIN T_RECHARGE_BLOCKCHAIN_ORDER AS recharge ON recharge.ORDER_NO = log.ORDER_NO ");
        countSql.append("WHERE case when recharge.ORDER_NO is null then '' else recharge.ORDER_NO > '' end ");
        countSql.append(" and log.PARTY_ID = '").append(partyId).append("' ");
        countSql.append(" and log.CATEGORY = '").append("recharge").append("' ");
        if ("1".equals(order_no_null)) {
            countSql.append(" and case when log.ORDER_NO is null then '' else log.ORDER_NO > '' end ");
        }

        int total = this.jdbcTemplate.queryForObject(countSql.toString(), Integer.class);
        page.setTotalElements(total);

        return page;
    }

    @Override
    public Page pagedQueryRecords(int pageNo, int pageSize, String partyId, String category) {

        Map<String, Object> parameters = new HashMap<String, Object>();

        StringBuffer queryString = new StringBuffer("SELECT ");

        queryString.append(
                "log.CATEGORY AS category, log.WALLETTYPE AS wallet_type, log.AMOUNT AS amount, log.USDT_AMOUNT AS usdtAmount, log.STATUS AS status, log.ORDER_NO order_no, DATE_FORMAT(log.CREATE_TIME,'%Y-%m-%d %H:%i:%S') as createTimeStr, ");
        queryString.append("withdraw.FAILURE_MSG AS withdraw_failure_msg, recharge.DESCRIPTION AS recharge_failure_msg ");

        queryString.append("FROM T_WALLET_LOG AS log ");
        queryString.append("LEFT JOIN T_WITHDRAW_ORDER AS withdraw ON withdraw.ORDER_NO = log.ORDER_NO ");
        queryString.append("LEFT JOIN T_RECHARGE_BLOCKCHAIN_ORDER AS recharge ON recharge.ORDER_NO = log.ORDER_NO ");

        queryString.append("WHERE 1=1  ");

        queryString.append(" and log.ORDER_NO is not null and log.ORDER_NO != ''  ");

        queryString.append(" and log.PARTY_ID = :partyId  ");
        parameters.put("partyId", partyId);

        if (StringUtils.isNotEmpty(category)) {
            queryString.append(" and log.CATEGORY = :category ");
            parameters.put("category", category);
        }

        queryString.append(" and log.STATUS = :status ");
        parameters.put("status", 1);

        queryString.append(" order by log.CREATE_TIME desc ");

        Page page = this.pagedQueryDao.pagedQuerySQL(pageNo, pageSize, queryString.toString(), parameters);
        return page;
    }

    public double getComputeRechargeAmount(String partyId) {
        String sql = "select IFNULL(sum(AMOUNT), 0) as amount from T_MONEY_LOG where PARTY_ID = ? and CONTENT_TYPE=? ";
        return jdbcTemplate.queryForObject(sql, Double.class, partyId, Constants.MONEYLOG_CATEGORY_RECHARGE);
    }

    public double getRechargeAmount(String partyId) {
        String sql = "select IFNULL(MONEY_RECHARGE_ACC, 0) as amount from T_MALL_USER_METRICS where PARTY_ID = ? ";
        List<Double> result = jdbcTemplate.queryForList(sql, Double.class, partyId);
        double amount = result.isEmpty() ? 0.0 : result.get(0);
        return amount;
    }


    /**
     * 批量统计指定用户累计充值金额
     *
     * @param partyIdList
     * @param
     * @return
     */
    public Map<String, Double> getComputeRechargeAmount(List<String> partyIdList, double limitAmount) {
        Map<String, Double> rechargeAmountMap = new HashMap();
        if (CollectionUtil.isEmpty(partyIdList)) {
            return rechargeAmountMap;
        }

        StringBuffer partyIdsBuf = new StringBuffer();
        for (String onePartyId : partyIdList) {
            partyIdsBuf.append("'").append(onePartyId.trim()).append("',");
        }
        partyIdsBuf.deleteCharAt(partyIdsBuf.length() - 1);

//        String sql = "select PARTY_ID as partyId, IFNULL(sum(AMOUNT), 0) as amount " +
//                " FROM T_MONEY_LOG " +
//                " WHERE PARTY_ID in (" + partyIdsBuf.toString() + ") AND CATEGORY=?  " +
//                " GROUP BY PARTY_ID ";
//        if (limitAmount > 0) {
//            sql = sql + " having amount >= " + limitAmount + " ";
//        }

        String sql = "SELECT " +
                " PARTY_ID AS partyId, " +
                " IFNULL( STORE_MONEY_RECHARGE_ACC, 0 ) AS amount " +
                " FROM " +
                " T_MALL_USER_METRICS  " +
                " WHERE " +
                " PARTY_ID IN ("+ partyIdsBuf.toString() +")";
        if(limitAmount>0){
            sql =sql + " AND STORE_MONEY_RECHARGE_ACC >= "+limitAmount;
        }

        List list = this.jdbcTemplate.queryForList(sql/*, Constants.MONEYLOG_CATEGORY_RECHARGE*/);

        Iterator iterable = list.iterator();
        while (iterable.hasNext()) {
            Map rowMap = (Map) iterable.next();
            String partyId = (String) rowMap.getOrDefault("partyId", "");
            Double totalAmount = (Double) rowMap.getOrDefault("amount", "0.0");

            rechargeAmountMap.put(partyId, totalAmount);
        }

        return rechargeAmountMap;
    }

    /**
     * 根据时间统计充值人数
     *
     * @param startTime
     * @param endTime
     * @return 统计总人数
     */
    public Integer getCountRechargeByDay(String startTime, String endTime) {

        StringBuilder countSql = new StringBuilder();

        Cache<String, Integer> cacheResult = LocalCachePool.buildCache(LocalCacheBucketKey.CountRechargeByDay, 20, 300, null);

        String key = this.getKey(startTime, endTime, LocalCacheBucketKey.CountRechargeByDay);

        Integer cacheresults = cacheResult.getIfPresent(key);

        if (Objects.nonNull(cacheresults)) {
            return cacheresults;
        }

        countSql.append("select IFNULL(SUM(t1.count),0) from (select count(1) as count  from T_RECHARGE_BLOCKCHAIN_ORDER where  SUCCEEDED = 1 ");

        List<Object> params = new ArrayList<>();

        if (kernel.util.StringUtils.isNotEmpty(startTime)) {
            params.add(startTime);
            countSql.append("AND CREATED >= ? ");
        }

        if (kernel.util.StringUtils.isNotEmpty(endTime)) {
            params.add(endTime);
            countSql.append("AND CREATED <= ? ");
        }

        countSql.append("GROUP BY PARTY_ID) t1 ");

        Integer result = jdbcTemplate.queryForObject(countSql.toString(), params.toArray(), Integer.class);

        if (Objects.nonNull(result)) {
            cacheResult.put(key, result);
        }
        return result;
    }


    @Override
    public Integer getCacheCountRechargeByDay(String startTime, String endTime) {

        String funKey = this.getKey(startTime, endTime, LocalCacheBucketKey.CountRechargeByDay);

        FunParams funParams = FunParams.newParam().set("walletLogService", this);

        Integer result = CacheOperation.execute(funKey, true, 600L * 1000L, funParams, (params) -> {
            WalletLogService partyService = params.get("walletLogService").getAs(WalletLogService.class);
            Integer getResult = partyService.getCountRechargeByDay(startTime, endTime);
            return getResult;
        });
        return result;
    }

    @Override
    public Map<String, Object> getTotalProfitByDay(String startTime, String endTime) {

        StringBuilder countSql = new StringBuilder("select  CAST(SUM(IFNULL(AMOUNT,0)) AS DECIMAL (19, 2)) as profit from T_MONEY_LOG where  CONTENT_TYPE = 'order-income' ");
        countSql.append(" and PARTY_ID IN (SELECT UUID  FROM  PAT_PARTY where ROLENAME <> 'GUEST') ");

        List<Object> params = new ArrayList<>();

        if (kernel.util.StringUtils.isNotEmpty(startTime)) {
            params.add(startTime);
            countSql.append("AND CREATE_TIME >= ? ");
        }

        if (kernel.util.StringUtils.isNotEmpty(endTime)) {
            params.add(endTime);
            countSql.append("AND CREATE_TIME  <= ? ");
        }

        List<Map<String, Object>> results = jdbcTemplate.queryForList(countSql.toString(), params.toArray());

        Map<String, Object> resultMap = new HashMap<>();

        if (CollectionUtil.isNotEmpty(results)) {

            for (Map<String, Object> data : results) {
                resultMap.put("profit", Objects.isNull(data.get("profit")) ? 0 : data.get("profit"));
            }

            BigDecimal result = new BigDecimal(String.valueOf(resultMap.get("profit"))).setScale(2, BigDecimal.ROUND_DOWN);
            resultMap.put("profit", result);
        }
        return resultMap;
    }

    @Override
    public Map<String, Object> getCacheTotalProfitByDay(String startTime, String endTime) {

        String funKey = this.getKey(startTime, endTime, LocalCacheBucketKey.TotalProfitByDay);

        FunParams funParams = FunParams.newParam().set("walletLogService", this);

        Map<String, Object> result = CacheOperation.execute(funKey, true, 600L * 1000L, funParams, (params) -> {
            WalletLogService partyService = params.get("walletLogService").getAs(WalletLogService.class);
            Map<String, Object> getResult = partyService.getTotalProfitByDay(startTime, endTime);
            return getResult;
        });
        return result;
    }


    public Map<String, Object> getSumWithdrawByDay(String startTime, String endTime) {

        StringBuilder countSql = new StringBuilder("select CAST(SUM(IFNULL(AMOUNT,0)) AS DECIMAL (19, 3)) as withdraw from T_WITHDRAW_ORDER where  ");
        countSql.append(" PARTY_ID IN (SELECT UUID  FROM  PAT_PARTY where ROLENAME <> 'GUEST') and SUCCEEDED = 1 ");

        List<Object> params = new ArrayList<>();

        if (kernel.util.StringUtils.isNotEmpty(startTime)) {
            params.add(startTime);
            countSql.append("AND CREATE_TIME >= ? ");
        }

        if (kernel.util.StringUtils.isNotEmpty(endTime)) {
            params.add(endTime);
            countSql.append("AND CREATE_TIME  <= ? ");
        }

        List<Map<String, Object>> results = jdbcTemplate.queryForList(countSql.toString(), params.toArray());

        Map<String, Object> resultMap = new HashMap<>();

        if (CollectionUtil.isNotEmpty(results)) {

            for (Map<String, Object> data : results) {
                resultMap.put("withdraw", Objects.isNull(data.get("withdraw")) ? 0 : data.get("withdraw"));
            }

            BigDecimal result = new BigDecimal(String.valueOf(resultMap.get("withdraw"))).setScale(2, BigDecimal.ROUND_DOWN);

            resultMap.put("withdraw", result);
        }
        return resultMap;
    }

    @Override
    public Map<String, Object> getCacheSumWithdrawByDay(String startTime, String endTime) {

        String funKey = this.getKey(startTime, endTime, LocalCacheBucketKey.SumWithdrawByDay);

        FunParams funParams = FunParams.newParam().set("walletLogService", this);

        Map<String, Object> result = CacheOperation.execute(funKey, true, 600L * 1000L, funParams, (params) -> {
            WalletLogService partyService = params.get("walletLogService").getAs(WalletLogService.class);
            Map<String, Object> getResult = partyService.getSumWithdrawByDay(startTime, endTime);
            return getResult;
        });
        return result;
    }

    @Override
    public Map<String, Object> getSumRechargeByDay(String startTime, String endTime, List<String> sellerIds) {

        StringBuilder countSql = new StringBuilder("select CAST(SUM(IFNULL(AMOUNT,0)) AS DECIMAL (19, 3)) as recharge from T_RECHARGE_BLOCKCHAIN_ORDER where  ");
        countSql.append(" PARTY_ID IN (SELECT UUID  FROM  PAT_PARTY where ROLENAME <> 'GUEST') and SUCCEEDED = 1 ");

        List<Object> params = new ArrayList<>();

        if (kernel.util.StringUtils.isNotEmpty(startTime)) {
            params.add(startTime);
            countSql.append("AND CREATED >= ? ");
        }

        if (kernel.util.StringUtils.isNotEmpty(endTime)) {
            params.add(endTime);
            countSql.append("AND CREATED <= ? ");
        }

        if (CollectionUtils.isNotEmpty(sellerIds)) {
            params.add(sellerIds);
            countSql.append("AND PARTY_ID in ? ");
        }

        List<Map<String, Object>> results = jdbcTemplate.queryForList(countSql.toString(), params.toArray());

        Map<String, Object> resultMap = new HashMap<>();

        if (CollectionUtil.isNotEmpty(results)) {

            for (Map<String, Object> data : results) {
                resultMap.put("recharge", Objects.isNull(data.get("recharge")) ? 0 : data.get("recharge"));
            }

            BigDecimal result = new BigDecimal(String.valueOf(resultMap.get("recharge"))).setScale(2, BigDecimal.ROUND_DOWN);

            resultMap.put("recharge", result);
        }
        return resultMap;
    }


    @Override
    public Map<String, Object> getCacheSumRechargeByDay(String startTime, String endTime, List<String> sellerIds) {

        String funKey = this.getKey(startTime, endTime, LocalCacheBucketKey.SumRechargeByDay);

        FunParams funParams = FunParams.newParam().set("walletLogService", this);

        Map<String, Object> result = CacheOperation.execute(funKey, true, 600L * 1000L, funParams, (params) -> {
            WalletLogService partyService = params.get("walletLogService").getAs(WalletLogService.class);
            Map<String, Object> getResult = partyService.getSumRechargeByDay(startTime, endTime, null);
            return getResult;
        });
        return result;
    }


    /**
     * 根据时间统计提现人数
     *
     * @param startTime
     * @param endTime
     * @return 统计总人数
     */
    public Integer getCountWithdrawByDay(String startTime, String endTime) {

        StringBuilder countSql = new StringBuilder();


        countSql.append("select IFNULL(SUM(t1.count),0) from (select count(1) as count  from T_WITHDRAW_ORDER where  SUCCEEDED = 1 ");

        List<Object> params = new ArrayList<>();

        if (kernel.util.StringUtils.isNotEmpty(startTime)) {
            params.add(startTime);
            countSql.append("AND CREATE_TIME >= ? ");
        }

        if (kernel.util.StringUtils.isNotEmpty(endTime)) {
            params.add(endTime);
            countSql.append("AND CREATE_TIME <= ? ");
        }

        countSql.append("GROUP BY PARTY_ID) t1 ");

        Integer result = jdbcTemplate.queryForObject(countSql.toString(), params.toArray(), Integer.class);

        return result;
    }

    @Override
    public Integer getCacheCountWithdrawByDay(String startTime, String endTime) {

        String funKey = this.getKey(startTime, endTime, LocalCacheBucketKey.CountWithdrawByDay);

        FunParams funParams = FunParams.newParam()
                .set("walletLogService", this);

        // 基于一个缓存上次执行结果的组件进行数据统计方法的调用，如果上次缓存结果满足条件，则优先使用缓存结果，否则，触发真实的统计执行
        // 执行结果缓存 1 个小时
        Integer result = CacheOperation.execute(funKey, true, 600L * 1000L, funParams, (params) -> {
            WalletLogService partyService = params.get("walletLogService").getAs(WalletLogService.class);
            Integer getResult = partyService.getCountWithdrawByDay(startTime, endTime);
            return getResult;
        });
        return result;
    }


    /**
     * 根据时间统计新充值人数
     *
     * @param startTime
     * @param endTime
     * @return 统计新提现人数
     */
    public RechargePartyResultDTO getNewRechargeByDay(String startTime, String endTime) {

        StringBuilder countSql = new StringBuilder();

        countSql.append("select PARTY_ID as sellerId, AMOUNT from T_RECHARGE_BLOCKCHAIN_ORDER where SUCCEEDED = 1 and  PARTY_ID  ");

        countSql.append("in (select UUID  from PAT_PARTY  where  1= 1 and ROLENAME <> 'GUEST' ");

        List<Object> params = new ArrayList<>();

        if (kernel.util.StringUtils.isNotEmpty(startTime)) {
            params.add(startTime);
            countSql.append("AND CREATE_TIME >= ? ");
        }

        if (kernel.util.StringUtils.isNotEmpty(endTime)) {
            params.add(endTime);
            countSql.append("AND CREATE_TIME <= ? ");
        }

        countSql.append(")");

        List<RechargePartyDTO> results = jdbcTemplate.query(countSql.toString(), new BeanPropertyRowMapper<>(RechargePartyDTO.class), params.toArray());

        RechargePartyResultDTO rechargePartyResultDTO = new RechargePartyResultDTO();

        if (CollectionUtils.isNotEmpty(results)) {
            Map<String, List<RechargePartyDTO>> collect = results.stream().collect(Collectors.groupingBy(RechargePartyDTO::getSellerId));
            rechargePartyResultDTO.setNumber(collect.keySet().size());
            Double amounts = results.stream().flatMapToDouble(value -> DoubleStream.of(value.getAmount())).sum();
            rechargePartyResultDTO.setAmount(String.valueOf(amounts));
        }

        return rechargePartyResultDTO;
    }

    @Override
    public RechargePartyResultDTO getCacheNewRechargeByDay(String startTime, String endTime) {

        String funKey = this.getKey(startTime, endTime, LocalCacheBucketKey.NewRechargeByDay);

        FunParams funParams = FunParams.newParam()
                .set("walletLogService", this);

        // 基于一个缓存上次执行结果的组件进行数据统计方法的调用，如果上次缓存结果满足条件，则优先使用缓存结果，否则，触发真实的统计执行
        // 执行结果缓存 1 个小时
        RechargePartyResultDTO result = CacheOperation.execute(funKey, true, 600L * 1000L, funParams, (params) -> {
            WalletLogService partyService = params.get("walletLogService").getAs(WalletLogService.class);
            RechargePartyResultDTO getResult = partyService.getNewRechargeByDay(startTime, endTime);
            return getResult;
        });
        return result;
    }


    /**
     * 根据时间统计新提现人数
     *
     * @param startTime
     * @param endTime
     * @return 统计新提现人数
     */
    public RechargePartyResultDTO getNewWithdrawByDay(String startTime, String endTime) {

        StringBuilder countSql = new StringBuilder();

        countSql.append("select PARTY_ID sellerId , AMOUNT from T_WITHDRAW_ORDER where SUCCEEDED = 1 and  PARTY_ID  ");

        countSql.append("in (select UUID  from PAT_PARTY  where  1= 1 and ROLENAME <> 'GUEST' ");

        List<Object> params = new ArrayList<>();

        if (kernel.util.StringUtils.isNotEmpty(startTime)) {
            params.add(startTime);
            countSql.append("AND CREATE_TIME >= ? ");
        }

        if (kernel.util.StringUtils.isNotEmpty(endTime)) {
            params.add(endTime);
            countSql.append("AND CREATE_TIME <= ? ");
        }

        countSql.append(")");

        List<RechargePartyDTO> results = jdbcTemplate.query(countSql.toString(), new BeanPropertyRowMapper<>(RechargePartyDTO.class), params.toArray());

        RechargePartyResultDTO rechargePartyResultDTO = new RechargePartyResultDTO();

        if (CollectionUtils.isNotEmpty(results)) {
            Map<String, List<RechargePartyDTO>> collect = results.stream().collect(Collectors.groupingBy(RechargePartyDTO::getSellerId));
            rechargePartyResultDTO.setNumber(collect.keySet().size());
            Double amounts = results.stream().flatMapToDouble(value -> DoubleStream.of(value.getAmount())).sum();
            rechargePartyResultDTO.setAmount(String.valueOf(amounts));
        }

        return rechargePartyResultDTO;
    }

    @Override
    public RechargePartyResultDTO getCacheNewWithdrawByDay(String startTime, String endTime) {

        String funKey = this.getKey(startTime, endTime, LocalCacheBucketKey.NewWithdrawByDay);

        FunParams funParams = FunParams.newParam()
                .set("walletLogService", this);

        // 基于一个缓存上次执行结果的组件进行数据统计方法的调用，如果上次缓存结果满足条件，则优先使用缓存结果，否则，触发真实的统计执行
        // 执行结果缓存 1 个小时
        RechargePartyResultDTO result = CacheOperation.execute(funKey, true, 600L * 1000L, funParams, (params) -> {
            WalletLogService partyService = params.get("walletLogService").getAs(WalletLogService.class);
            RechargePartyResultDTO getResult = partyService.getNewWithdrawByDay(startTime, endTime);
            return getResult;
        });
        return result;
    }

    public List<WalletLog> getAll() {
        return (List<WalletLog>) this.getHibernateTemplate().find(" FROM WalletLog ");
    }

    /**
     * 批量统计指定用户累计充值金额
     *
     * @param partyId
     * @param limitTime
     * @return
     */
    public WalletLog getFirstRechargeLogInTimeRange(String partyId, Date limitTime) {
        DetachedCriteria criteria = DetachedCriteria.forClass(WalletLog.class);
        criteria.add(Property.forName("partyId").eq(partyId));
        criteria.add(Property.forName("status").eq(1));
        criteria.add(Property.forName("createTime").ge(limitTime));
        criteria.add(Property.forName("category").eq(Constants.MONEYLOG_CATEGORY_RECHARGE));

        criteria.addOrder(Order.asc("createTime"));

        try {
            List<WalletLog> wallets = (List<WalletLog>) getHibernateTemplate().findByCriteria(criteria);
            if (CollectionUtils.isEmpty(wallets)) {
                return null;
            }

            return wallets.get(0);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public void setPagedQueryDao(PagedQueryDao pagedQueryDao) {
        this.pagedQueryDao = pagedQueryDao;
    }

    public void setRedisHandler(RedisHandler redisHandler) {
        this.redisHandler = redisHandler;
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    private String getKey(String fromTime, String toTime, String key) {
        String from = fromTime.replace("-", "")
                .replace(":", "")
                .replace("000000", "")
                .replace(" ", "");

        String to = toTime.replace("-", "")
                .replace(":", "")
                .replace("000000", "")
                .replace(" ", "");

        String funKey = key + ":" + from + "-" + to;

        return funKey;
    }
}
