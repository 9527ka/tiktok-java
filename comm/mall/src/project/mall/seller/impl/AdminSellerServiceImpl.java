package project.mall.seller.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import kernel.exception.BusinessException;
import kernel.util.Arith;
import kernel.util.DateUtils;
import kernel.util.JsonUtils;
import kernel.util.StringUtils;
import kernel.web.Page;
import kernel.web.PagedQueryDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;
import project.Constants;
import project.log.Log;
import project.log.LogService;
import project.log.MoneyLog;
import project.log.MoneyLogService;
import project.mall.activity.model.lottery.LotteryReceive;
import project.mall.seller.AdminSellerService;
import project.mall.seller.MallLevelService;
import project.mall.seller.SellerService;
import project.mall.seller.constant.UpgradeMallLevelCondParamTypeEnum;
import project.mall.seller.dto.MallLevelCondExpr;
import project.mall.seller.dto.QueryMallLevelDTO;
import project.mall.seller.model.MallLevel;
import project.mall.seller.model.Seller;
import project.party.PartyRedisKeys;
import project.party.UserMetricsService;
import project.party.model.UserMetrics;
import project.party.recom.UserRecomService;
import project.redis.RedisHandler;
import project.tip.TipService;
import project.user.kyc.Kyc;
import project.user.kyc.KycService;
import project.wallet.Wallet;
import project.wallet.WalletService;
import security.SecUser;
import security.internal.SecUserService;
import util.TokenUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
public class AdminSellerServiceImpl extends HibernateDaoSupport implements AdminSellerService {

    private PagedQueryDao pagedQueryDao;

    private RedisHandler redisHandler;

    private SecUserService secUserService;

    private UserRecomService userRecomService;

    private JdbcTemplate jdbcTemplate;

    private KycService kycService;

    private MallLevelService mallLevelService;

    private SellerService sellerService;

    private UserMetricsService userMetricsService;

    private LogService logService;

    private WalletService walletService;

    private MoneyLogService moneyLogService;

    private TipService tipService;


    @Override
    public Page pagedQuery(int pageNo, int pageSize, String name_para, String loginPartyId, String sellerId, String sellerName, String startTime,
                           String endTime, String roleName, String username_parent, String mallLevel) {
        StringBuffer queryString = new StringBuffer();
        queryString.append(" SELECT ");
        queryString.append(" s.UUID sellerId, s.CREATE_TIME createTime, s.NAME sellerName, s.AVATAR avatar, s.SHOP_PHONE shopPhone, s.MALL_LEVEL level, s.FLOW, ");
        queryString.append(" wallet.MONEY money, wallet.FROZEN_STATE frozenState, wallet.MONEY_AFTER_FROZEN moneyAfterFrozen, s.REC_TIME recTime, s.FREEZE freeze, ");
        queryString.append(" s.REALS reals, s.FAKE fake, s.BLACK  black, party.USERCODE userCode, party.ROLENAME rolename, s.CREDIT_SCORE creditScore, party.USERNAME userName,");
        queryString.append(" s.BASE_TRAFFIC baseTraffic, s.AUTO_START autoStart, s.AUTO_END autoEnd,s.TEAM_NUM teamNum ,s.CHILD_NUM childNum, s.RECOMMEND_DURATION recommendDuration, ");
        queryString.append(" party_parent.USERNAME username_parent, kyc.REMARK remark, party.REMARKS remarks , s.HIGH_OPINION highOpinion");
        queryString.append(" FROM ");
        queryString.append(" T_MALL_SELLER s ");
        queryString.append(" LEFT JOIN PAT_PARTY party ON s.UUID = party.UUID ");
        queryString.append(" LEFT JOIN PAT_USER_RECOM user ON user.PARTY_ID = party.UUID ");
        queryString.append(" LEFT JOIN PAT_PARTY party_parent ON user.RECO_ID = party_parent.UUID ");
        queryString.append(" LEFT JOIN T_WALLET wallet ON wallet.PARTY_ID = party.UUID ");
        queryString.append(" LEFT JOIN T_KYC kyc ON kyc.PARTY_ID = s.UUID ");
        queryString.append(" WHERE 1=1 AND s.STATUS = 1 ");

        Map<String, Object> parameters = new HashMap<String, Object>();

//        if (!StringUtils.isNullOrEmpty(userCode)) {
//            queryString.append(" AND party.USERCODE =:userCode ");
//            parameters.put("userCode", userCode);
//        }

        if (!StringUtils.isNullOrEmpty(name_para)) {
            queryString.append("AND (party.USERNAME like:username OR party.USERCODE like:username ) ");
            parameters.put("username", "%" + name_para + "%");
        }
        if (!StringUtils.isNullOrEmpty(username_parent)) {
            queryString.append(" AND trim(replace(party_parent.USERNAME,' ','')) like:username_parent ");
            username_parent = username_parent.replace(" ", "");
            parameters.put("username_parent", "%" + username_parent + "%");
        }
        if (!StringUtils.isNullOrEmpty(loginPartyId)) {
            List children = this.userRecomService.findChildren(loginPartyId);
            if (children.size() == 0) {
//				return Page.EMPTY_PAGE;
                return new Page();
            }
            queryString.append(" AND s.UUID in (:children) ");
            parameters.put("children", children);
        }

//        if (!StringUtils.isNullOrEmpty(sellerId)) {
//            queryString.append(" AND s.UUID =:sellerId ");
//            parameters.put("sellerId", sellerId);
//        }
        if (!StringUtils.isNullOrEmpty(sellerName)) {
            queryString.append(" AND trim(replace(s.NAME,' ','')) like:sellerName ");
            sellerName = sellerName.replace(" ", "");
            parameters.put("sellerName", "%" + sellerName + "%");
        }
        if (!StringUtils.isNullOrEmpty(roleName)) {
            queryString.append(" AND party.ROLENAME =:roleName");
            parameters.put("roleName", roleName);
        }

        if (!StringUtils.isNullOrEmpty(mallLevel)) {

            if (mallLevel.equals("0")) {
                queryString.append(" AND s.MALL_LEVEL IS NULL ");
            } else {
                queryString.append(" AND s.MALL_LEVEL =:mallLevel ");
                parameters.put("mallLevel", mallLevel);
            }
        }

        if (!StringUtils.isNullOrEmpty(startTime)) {
            queryString.append(" AND DATE(s.CREATE_TIME) >= DATE(:startTime)  ");
            parameters.put("startTime", DateUtils.toDate(startTime));
        }
        if (!StringUtils.isNullOrEmpty(endTime)) {
            queryString.append(" AND DATE(s.CREATE_TIME) <= DATE(:endTime)  ");
            parameters.put("endTime", DateUtils.toDate(endTime));
        }
        queryString.append(" GROUP BY s.UUID ORDER BY s.CREATE_TIME DESC ");
        Page page = this.pagedQueryDao.pagedQuerySQL(pageNo, pageSize, queryString.toString(), parameters);
        return page;
    }

    //    this.pageNo, this.pageSize, userName, userCode,sellerName,flag,startTime, endTime
    public Page invitePagedQuery(int pageNo, int pageSize, String userName, String userCode, String sellerName, String state, String startTime, String endTime, String lotteryName) {
        StringBuffer queryString = new StringBuffer();
        queryString.append(" SELECT ");
        queryString.append(" alr.UUID, alr.PARTY_NAME AS partyName, party.USERCODE AS userCode, party.PHONE, party.EMAIL, alr.SELLER_NAME AS sellerName,alr.REMARK, ");
        queryString.append(" alr.LOTTERY_NAME AS lotteryName, alr.PRIZE_TYPE AS prizeType, alr.PRIZE_AMOUNT AS prizeAmount, alr.PARTY_ID AS partyId, ");
        queryString.append(" alr.RECOMMEND_NAME AS recommendName, alr.STATE, alr.APPLY_TIME AS applyTime, alr.ISSUE_TIME AS issueTime, alr.CREATE_USER createUser ");
        queryString.append(" FROM ACTIVITY_LOTTERY_RECEIVE alr ");
        queryString.append(" LEFT JOIN PAT_PARTY party ON alr.PARTY_ID = party.UUID  ");
        queryString.append(" WHERE ACTIVITY_TYPE = 0 ");
        Map<String, Object> parameters = new HashMap<String, Object>();
        if (!StringUtils.isNullOrEmpty(userName)) {
            queryString.append("AND alr.PARTY_NAME like:username ");
            parameters.put("username", "%" + userName + "%");
        }
        if (!StringUtils.isNullOrEmpty(userCode)) {
            queryString.append(" AND party.USERCODE =:usercode ");
            parameters.put("usercode", userCode);
        }
        if (!StringUtils.isNullOrEmpty(sellerName)) {
            queryString.append(" AND alr.SELLER_NAME LIKE :sellerName ");
            parameters.put("sellerName", sellerName);
        }
        if (!StringUtils.isNullOrEmpty(state)) {
            queryString.append(" AND alr.STATE =:state ");
            parameters.put("state", state);
        }
        if (!StringUtils.isNullOrEmpty(lotteryName)) {
            queryString.append(" AND alr.LOTTERY_NAME =:lotteryName ");
            parameters.put("lotteryName", lotteryName);
        }
        if (!StringUtils.isNullOrEmpty(startTime)) {
            queryString.append(" AND alr.ISSUE_TIME >= :startTime ");
            parameters.put("startTime", DateUtils.toDate(startTime));
        }
        if (!StringUtils.isNullOrEmpty(endTime)) {
            queryString.append(" AND alr.ISSUE_TIME <= :endTime ");
            parameters.put("endTime", DateUtils.toDate(endTime));
        }
        queryString.append(" ORDER BY alr.APPLY_TIME DESC ");
        Page page = this.pagedQueryDao.pagedQuerySQL(pageNo, pageSize, queryString.toString(), parameters);
        return page;
    }

    public Seller findSellerById(String id) {
        return this.getHibernateTemplate().get(Seller.class, id);
    }

    @Override
    public void update(String seller_id, String auto_start, String auto_end, String base_traffic, String auto_valid) {
        Seller seller = findSellerById(seller_id);
        if (null == seller) {
            throw new BusinessException("系统错误");
        }
        seller.setAutoStart(Integer.parseInt(auto_start));
        seller.setAutoEnd(Integer.parseInt(auto_end));
        seller.setBaseTraffic(Integer.parseInt(base_traffic));
        seller.setAutoValid(Integer.parseInt(auto_valid));
        this.getHibernateTemplate().update(seller);
    }

    public void updateDistributeBonuses(String partyId, String activityId, double prizeAmountD, String remark, String username_login) {
        LotteryReceive lotteryReceive = this.getHibernateTemplate().get(LotteryReceive.class, activityId);
        if (Objects.isNull(lotteryReceive) || lotteryReceive.getState() == 1) {
            throw new BusinessException("该申请不满足发放礼金要求");
        }

//        更新申请单状态
        lotteryReceive.setIssueTime(new Date());
        if (StringUtils.isNotEmpty(remark)) {
            lotteryReceive.setRemark(remark);
        }
        lotteryReceive.setState(1);
        lotteryReceive.setCreateUser(username_login);
        this.getHibernateTemplate().saveOrUpdate(lotteryReceive);

        BigDecimal rechargeBonus = lotteryReceive.getPrizeAmount();
        Wallet wallet = walletService.saveWalletByPartyId(partyId);
        double amount_before = wallet.getMoney();

        //更新钱包余额
        wallet.setMoney(Arith.roundDown(Arith.add(wallet.getMoney(), rechargeBonus.doubleValue()), 2));
        walletService.update(wallet);

        MoneyLog moneyLog = new MoneyLog();
        moneyLog.setCategory(Constants.MONEYLOG_CATEGORY_COIN);
        moneyLog.setAmount_before(amount_before);
        moneyLog.setAmount(Arith.add(0, rechargeBonus.doubleValue()));
        moneyLog.setAmount_after(wallet.getMoney());
        moneyLog.setFreeze(0);


        moneyLog.setLog(lotteryReceive.getLotteryName() + "奖励:" + rechargeBonus);
        moneyLog.setPartyId(partyId);
        moneyLog.setWallettype(Constants.WALLET);

        if (Objects.equals("首充活动",lotteryReceive.getLotteryName())){
            moneyLog.setContent_type(Constants.MONEYLOG_CONTNET_FIRST_RECHARGE_BONUS);
        } else if (Objects.equals("拉人活动",lotteryReceive.getLotteryName())){
            moneyLog.setContent_type(Constants.MONEYLOG_CONTNET_INVITATION_REWARDS);
        }
        moneyLogService.save(moneyLog);

        tipService.deleteTip(lotteryReceive.getId().toString());
    }


    @Override
    public void updateAttention(String seller_id, String fakeAttention) {
        Seller seller = findSellerById(seller_id);
        if (null == seller) {
            throw new BusinessException("系统错误");
        }
        seller.setFake(Integer.parseInt(fakeAttention));
        this.getHibernateTemplate().update(seller);
    }

    @Override
    public void updateFake(String sellerId, String fakeAttention) {
        Seller seller = findSellerById(sellerId);
        if (null == seller) {
            throw new BusinessException("系统错误");
        }
        Integer fake = seller.getFake() == null ? 0 : seller.getFake();
        seller.setFake(fake + Integer.parseInt(fakeAttention));

        log.info("原来粉丝数量{},添加粉丝数量{} 商家ID {} ", fake, fakeAttention, sellerId);

        this.getHibernateTemplate().update(seller);
        seller = findSellerById(sellerId);
        log.info("更新商家ID {} 的粉丝数量后最新值为:{}", sellerId, seller.getFake());
//        this.getHibernateTemplate().flush();
    }

    @Override
    public String getLoginFree(String id, String logInUserName) {

        SecUser user = secUserService.findUserByPartyId(id);

        if (Objects.isNull(user)) {
            throw new BusinessException("商家用户未注册");
        }

        String token = TokenUtils.token(user.getUsername(), logInUserName, true, null);
        redisHandler.setSyncString(PartyRedisKeys.LOGIN_PARTY_ID_TOKEN + user.getUsername(), token);
        return token;
    }

    @Override
    public void updateStatus(String id, int status, int recommendDuration) {
        Seller seller = this.findSellerById(id);
            if(status!=0){
                seller.setRecTime(new Date().getTime());
                seller.setRecommendDuration(recommendDuration);
            } else {
                seller.setRecTime(0L);
            }
        this.getHibernateTemplate().update(seller);

    }

    @Override
    public List<Seller> queryConfigValidAutoIncreaseViewCountSeller() {
        List<?> objects = this.getHibernateTemplate().find(" from Seller where autoValid = 1");
        return (List<Seller>) objects;
    }

    @Override
    public List<Seller> queryAllSeller() {
        List<?> objects = this.getHibernateTemplate().find(" from Seller");
        return (List<Seller>) objects;
    }

    public void updateFreeze(String id, int freeze) {
        Seller seller = this.findSellerById(id);
        seller.setFreeze(freeze);
        this.getHibernateTemplate().update(seller);
    }

    @Override
    public void updateFlow(String id, int flow) {
        Seller seller = this.findSellerById(id);
        seller.setFlow(flow);
        this.getHibernateTemplate().update(seller);
    }
    @Override
    public void updateBlack(String id, int black) {
        Seller seller = this.findSellerById(id);
        seller.setBlack(black);
        if (black == 1) {
            redisHandler.setSync(PartyRedisKeys.PARTY_ID_SELLER_BLACK + id, 1);
        } else {
            redisHandler.remove(PartyRedisKeys.PARTY_ID_SELLER_BLACK + id);
        }

        this.getHibernateTemplate().update(seller);
    }

    @Override
    public Map<String, Long> getViewNumsBySellerIds(List<String> sellerIds) {

        Map<String, Long> result = new HashMap<>();
        if (CollectionUtil.isEmpty(sellerIds)) {
            return result;
        }

        Date now = new Date();

        String startTime = util.DateUtil.formatDate(util.DateUtil.minDate(now), util.DateUtil.DATE_FORMAT_FULL);
        String endTime = util.DateUtil.formatDate(util.DateUtil.maxDate(now), util.DateUtil.DATE_FORMAT_FULL);

        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("sellersList", sellerIds);
        params.put("startTime", startTime);
        params.put("endTime", endTime);
        String sql = "SELECT SELLER_ID as sellerId, IFNULL(sum(VIEWS_NUM + VIRTUAL_VIEWS_NUM), 0)  as viewsNum " +
                " from T_MALL_SELLER_GOODS_STATISTICS where SELLER_ID in (:sellersList) " +
                " AND CREATE_TIME and CREATE_TIME >=:startTime and CREATE_TIME <=:endTime " +
                " GROUP BY SELLER_ID";
        List<Map<String, Object>> maps = namedParameterJdbcTemplate.queryForList(sql, params);

        for (Map<String, Object> data : maps) {
            result.put(data.get("sellerId").toString(), Long.parseLong(data.get("viewsNum").toString()));
        }
        return result;
    }

    @Override
    public Long getViewNumsBySellerId(String sellerId) {

        Long result = 0L;
        if (StringUtils.isEmptyString(sellerId)) {
            return result;
        }

        Date now = new Date();

        String startTime = util.DateUtil.formatDate(util.DateUtil.minDate(now), util.DateUtil.DATE_FORMAT_FULL);
        String endTime = util.DateUtil.formatDate(util.DateUtil.maxDate(now), util.DateUtil.DATE_FORMAT_FULL);

        result = jdbcTemplate.queryForObject("SELECT IFNULL(sum(VIEWS_NUM + VIRTUAL_VIEWS_NUM), 0)  " +
                "FROM T_MALL_SELLER_GOODS_STATISTICS  WHERE  " +
                "SELLER_ID='" + sellerId + "' AND  CREATE_TIME  BETWEEN '" + startTime + "' AND '" + endTime + "'", Long.class);

        return result;
    }

    @Override
    public void updateStoreLevel(String partyId, String level, double rechargeAmount, String operatorName, String ip, String userName) {
//        将这个等级全部查询出来 判断当前传入的等级是出于哪一个等级的
        List<MallLevel> list = this.mallLevelService.listLevel();
        List<QueryMallLevelDTO> levelInfoList = new ArrayList();
//        看来还是要全部查询出来
        for (MallLevel mallLevel : list) {
            MallLevelCondExpr mallLevelCondExprs = JsonUtils.json2Object(mallLevel.getCondExpr(), MallLevelCondExpr.class);
            List<MallLevelCondExpr.Param> params = mallLevelCondExprs.getParams();

            QueryMallLevelDTO oneDto = new QueryMallLevelDTO();
            BeanUtil.copyProperties(mallLevel, oneDto);
            params.forEach(e -> {
                if (e.getCode().equals(UpgradeMallLevelCondParamTypeEnum.RECHARGE_AMOUNT.getCode())) {
                    oneDto.setRechargeAmount(Long.parseLong(e.getValue()));
                }
                if (e.getCode().equals(UpgradeMallLevelCondParamTypeEnum.POPULARIZE_UNDERLING_NUMBER.getCode())) {
                    oneDto.setPopularizeUserCount(Long.parseLong(e.getValue()));
                }
                if (e.getCode().equals(UpgradeMallLevelCondParamTypeEnum.TEAM_NUM.getCode())) {
                    oneDto.setTeamNum(Integer.parseInt(e.getValue()));
                }
            });
            levelInfoList.add(oneDto);
        }

        Seller seller = this.sellerService.getSeller(partyId);
        int currentChildNum = seller.getChildNum();

        Map<String, Integer> levelSortMap = new HashMap<>();
        levelSortMap.put("C", 1);
        levelSortMap.put("B", 2);
        levelSortMap.put("A", 3);
        levelSortMap.put("S", 4);
        levelSortMap.put("SS", 5);
        levelSortMap.put("SSS", 6);
        CollUtil.sort(levelInfoList, new Comparator<QueryMallLevelDTO>() {
            @Override
            public int compare(QueryMallLevelDTO o1, QueryMallLevelDTO o2) {
                Integer seq1 = levelSortMap.get(o1.getLevel());
                Integer seq2 = levelSortMap.get(o2.getLevel());
                seq1 = seq1 == null ? 0 : seq1;
                seq2 = seq2 == null ? 0 : seq2;
                return seq1 - seq2;
            }
        });

//        根据id得到下一级的索引，用团队人数和直属下级人数进行比较得到是否可以修改等级，可能是最高级也可能是不是
        Integer currenLevelIndex = levelSortMap.get(level);
        if (currenLevelIndex <= list.size() - 2) {
            Integer nextLevelIndex = currenLevelIndex + 1;
            QueryMallLevelDTO currentMallLevelDto = levelInfoList.get(currenLevelIndex - 1);
            //得到目标等级的下一个等级信息 list下标从0开始
            QueryMallLevelDTO nextMallLevelDto = levelInfoList.get(nextLevelIndex - 1);
            Long nextMallLevelDtoPopularizeUserCount = nextMallLevelDto.getPopularizeUserCount();
            if (rechargeAmount>0 && currentChildNum >= nextMallLevelDtoPopularizeUserCount) {
                throw new BusinessException("当前会员通过推广升级，无法操作降级");
            }
            if (rechargeAmount>0 && (rechargeAmount < currentMallLevelDto.getRechargeAmount() || rechargeAmount > nextMallLevelDto.getRechargeAmount())) {
                throw new BusinessException("有效充值金额需在:" + currentMallLevelDto.getRechargeAmount() + "-" + nextMallLevelDto.getRechargeAmount() + "之间");
            }
        }

        seller.setMallLevel(level);
        Date now = new Date();
        this.sellerService.updateSeller(seller);
//        更新累计充值金额
        UserMetrics userMetrics = this.userMetricsService.getByPartyId(partyId);
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
            userMetrics.setCreateTime(now);
            userMetrics.setUpdateTime(now);
            userMetrics.setStoreMoneyRechargeAcc(0D);
            userMetrics = userMetricsService.save(userMetrics);
        }
        beforeAmount = new BigDecimal(userMetrics.getStoreMoneyRechargeAcc()).setScale(2, BigDecimal.ROUND_DOWN).doubleValue();
        if(rechargeAmount>0){
            userMetrics.setStoreMoneyRechargeAcc(rechargeAmount);
            userMetricsService.update(userMetrics);
        }
        afterAmount = new BigDecimal(userMetrics.getStoreMoneyRechargeAcc()).setScale(2, BigDecimal.ROUND_DOWN).doubleValue();


        Log log = new Log();
        log.setCategory(Constants.LOG_CATEGORY_SECURITY);
        log.setLog(operatorName + "修改了店铺升级累计有效充值金额，修改前金额为" + beforeAmount + "修改后金额为：" + afterAmount + "操作ip[" + ip + "]");
        log.setPartyId(partyId);
        log.setOperator(operatorName);
        log.setUsername(userName);
        logService.saveAsyn(log);
    }

    @Override
    public void updateHighOpinion(String partyId, Double highOpinion) {
        Seller seller = this.sellerService.getSeller(partyId);
        seller.setHighOpinion(highOpinion);
        this.sellerService.updateSeller(seller);
    }


        @Override
    public int getGoodsNumBySellerIds(String sellerId) {
        Integer result = 0;
        result = jdbcTemplate.queryForObject("SELECT COUNT(GOODS_ID) AS goodNum  " +
                "FROM T_MALL_SELLER_GOODS WHERE IS_SHELF = 1 AND (IS_VALID = 1 OR IS_VALID IS NULL) AND  " +
                "SELLER_ID='" + sellerId + "'", Integer.class);

        return result;
    }

    @Override
    public void updateRemarks(String sellerId, String remarks) {
        Kyc kyc = kycService.get(sellerId);
        if (!Objects.isNull(kyc)) {
            kyc.setRemark(remarks);
            getHibernateTemplate().update(kyc);
//            getHibernateTemplate().flush();
        }
    }


    public void setPagedQueryDao(PagedQueryDao pagedQueryDao) {
        this.pagedQueryDao = pagedQueryDao;
    }


    public void setSecUserService(SecUserService secUserService) {
        this.secUserService = secUserService;
    }

    public void setRedisHandler(RedisHandler redisHandler) {
        this.redisHandler = redisHandler;
    }

    public void setUserRecomService(UserRecomService userRecomService) {
        this.userRecomService = userRecomService;
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public KycService getKycService() {
        return kycService;
    }

    public void setKycService(KycService kycService) {
        this.kycService = kycService;
    }

    public void setMallLevelService(MallLevelService mallLevelService) {
        this.mallLevelService = mallLevelService;
    }

    public void setSellerService(SellerService sellerService) {
        this.sellerService = sellerService;
    }

    public void setUserMetricsService(UserMetricsService userMetricsService) {
        this.userMetricsService = userMetricsService;
    }

    public void setLogService(LogService logService) {
        this.logService = logService;
    }

    public void setWalletService(WalletService walletService) {
        this.walletService = walletService;
    }

    public void setMoneyLogService(MoneyLogService moneyLogService) {
        this.moneyLogService = moneyLogService;
    }

    public void setTipService(TipService tipService) {
        this.tipService = tipService;
    }
}