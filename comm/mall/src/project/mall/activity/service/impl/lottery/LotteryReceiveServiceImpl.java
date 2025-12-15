package project.mall.activity.service.impl.lottery;

import kernel.exception.BusinessException;
import kernel.util.Arith;
import kernel.util.DateUtils;
import kernel.web.Page;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;
import project.Constants;
import project.log.MoneyLog;
import project.log.MoneyLogService;
import project.mall.activity.dto.lottery.LotteryReceiveDTO;
import project.mall.activity.model.lottery.LotteryReceive;
import project.mall.activity.service.lottery.LotteryReceiveService;
import project.party.PartyService;
import project.wallet.Wallet;
import project.wallet.WalletLog;
import project.wallet.WalletLogService;
import project.wallet.WalletService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;


public class LotteryReceiveServiceImpl extends HibernateDaoSupport implements LotteryReceiveService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private JdbcTemplate jdbcTemplate;

    private WalletService walletService;

    private WalletLogService walletLogService;

    private PartyService partyService;

    private MoneyLogService moneyLogService;

    @Override
    public void add(LotteryReceive lotteryReceive) {

        lotteryReceive.setCreateTime(new Date());
        lotteryReceive.setUpdateTime(new Date());

        this.getHibernateTemplate().save(lotteryReceive);
    }

    @Override
    public void delete(String id) {
        LotteryReceive lotteryReceive = getHibernateTemplate().get(LotteryReceive.class, id);

        if (Objects.nonNull(lotteryReceive)) {
            getHibernateTemplate().delete(lotteryReceive);
        }
    }

    @Override
    public LotteryReceive detail(String id) {
        LotteryReceive lotteryReceive = getHibernateTemplate().get(LotteryReceive.class, id);
        return lotteryReceive;
    }

    @Override
    public void update(LotteryReceive lotteryReceive) {
        getHibernateTemplate().update(lotteryReceive);
    }

    @Override
    public Page paged(String username, String uid, String sellerName, Integer state, Integer prizeType, String startTime, String endTime, Integer pageNum, Integer pageSize) {

        Page page = new Page();

        List<Object> params = new ArrayList<>();

        StringBuilder queryString = new StringBuilder("SELECT ");

        StringBuilder countString = new StringBuilder("SELECT count(1) from ACTIVITY_LOTTERY_RECEIVE t1  " +
                "LEFT JOIN PAT_PARTY t2 ON t1.PARTY_ID = t2.UUID  where ACTIVITY_TYPE =1 ");

        queryString.append(
                        "t1.PARTY_ID AS partyId," +
                        "t1.PARTY_NAME AS partyName," +
                        "t1.LOTTERY_NAME as lotteryName," +
                        "t1.SELLER_NAME as sellerName," +
                        "t1.PRIZE_IDS as prizeIds," +
                        "t1.CREATE_TIME as createTime," +
                        "t1.RECOMMEND_NAME AS recommendName," +
                        "t1.APPLY_TIME AS applyTime," +
                        "t1.ISSUE_TIME AS issueTime," +
                        "t1.STATE AS state," +
                        "t1.PRIZE_AMOUNT AS prizeAmount," +
                        "t1.PRIZE_TYPE AS prizeType," +
                        "t2.EMAIL email," +
                        "t2.PHONE AS phone," +
                        "t2.USERNAME AS username," +
                        "t2.USERCODE AS uid," +
                        "t1.UUID AS id ");

        queryString.append("FROM ");

        queryString.append(
                "ACTIVITY_LOTTERY_RECEIVE t1  LEFT JOIN PAT_PARTY t2 ON t1.PARTY_ID = t2.UUID where ACTIVITY_TYPE =1  "
        );

        if (StringUtils.isNotEmpty(username)) {
            queryString.append(" and t1.PARTY_NAME like ? ");
            countString.append(" and t1.PARTY_NAME like ? ");
            params.add("%" + username + "%");
        }

        if (StringUtils.isNotEmpty(uid)) {
            queryString.append(" and t2.USERCODE =  ? ");
            countString.append(" and t2.USERCODE =  ? ");
            params.add(uid);
        }

        if (StringUtils.isNotEmpty(sellerName)) {
            queryString.append(" and t1.SELLER_NAME like ? ");
            countString.append(" and t1.SELLER_NAME like ? ");
            params.add("%" + sellerName + "%");
        }

        if (Objects.nonNull(state)) {
            queryString.append(" and t1.STATE =  ? ");
            countString.append(" and t1.STATE =  ? ");
            params.add(state);
        }

        if (Objects.nonNull(prizeType)) {
            queryString.append(" and t1.PRIZE_TYPE =  ? ");
            countString.append(" and t1.PRIZE_TYPE =  ? ");
            params.add(prizeType);
        }

        if (StringUtils.isNotEmpty(startTime)) {
            queryString.append(" and t1.CREATE_TIME > ? ");
            countString.append(" and t1.CREATE_TIME > ? ");
            params.add(startTime);
        }

        if (StringUtils.isNotEmpty(endTime)) {
            queryString.append(" and t1.CREATE_TIME <= ? ");
            countString.append(" and t1.CREATE_TIME <= ? ");
            params.add(startTime);
        }

        queryString.append(" order by t1.CREATE_TIME desc ");

        queryString.append(" limit " + (pageNum - 1) * pageSize + "," + pageSize);

        List list = jdbcTemplate.queryForList(queryString.toString(), params.toArray());
        int totalCount = jdbcTemplate.queryForObject(countString.toString(), params.toArray(), Integer.class);
        Iterator iterator = list.iterator();
        List<LotteryReceiveDTO> resultList = new ArrayList<>();
        while (iterator.hasNext()) {
            Map rowMap = (Map) iterator.next();
            String id = (String) rowMap.get("id");
            LocalDateTime createTime = (LocalDateTime) rowMap.get("createTime");
            LocalDateTime applyTime = (LocalDateTime) rowMap.get("applyTime");
            LocalDateTime issueTime = (LocalDateTime) rowMap.get("issueTime");

            String partyName = (String) rowMap.getOrDefault("partyName", "");
            String rpartyId = (String) rowMap.getOrDefault("partyId", "");
            String rpuid = (String) rowMap.getOrDefault("uid", "");

            String lotteryName = (String) rowMap.getOrDefault("lotteryName", "");
            String prizeIds = (String) rowMap.getOrDefault("prizeIds", "");
            String recommendName = (String) rowMap.getOrDefault("recommendName", "");
            Integer rstate = (Integer) rowMap.get("state");
            BigDecimal prizeAmount = (BigDecimal) rowMap.get("prizeAmount");
            Integer rprizeType = (Integer) rowMap.get("prizeType");
            String email = (String) rowMap.getOrDefault("email", "");
            String phone = (String) rowMap.getOrDefault("phone", "");
            String rusername = (String) rowMap.getOrDefault("username", "");
            String rsellerName = (String) rowMap.getOrDefault("sellerName", "");

            LotteryReceiveDTO dto = new LotteryReceiveDTO();
            dto.setId(id);
            dto.setEmail(email);
            dto.setPartyId(rpartyId);
            dto.setUid(rpuid);
            dto.setPartyName(partyName);
            dto.setPrizeAmount(prizeAmount);
            dto.setPrizeIds(prizeIds);
            dto.setPhone(phone);
            dto.setRecommendName(recommendName);
            dto.setUsername(rusername);
            dto.setState(rstate);
            dto.setPrizeType(rprizeType);
            dto.setSellerName(rsellerName);
            dto.setLotteryName(lotteryName);
            dto.setApplyTime(DateUtils.format(Date.from(applyTime.atZone(ZoneId.systemDefault()).toInstant()), DateUtils.NORMAL_DATE_FORMAT));
            if (Objects.nonNull(issueTime)) {
                dto.setIssueTime(DateUtils.format(Date.from(issueTime.atZone(ZoneId.systemDefault()).toInstant()), DateUtils.NORMAL_DATE_FORMAT));
            }
            dto.setCreateTime(DateUtils.format(Date.from(createTime.atZone(ZoneId.systemDefault()).toInstant()), DateUtils.NORMAL_DATE_FORMAT));
            resultList.add(dto);
        }

        page.setPageSize(pageSize);
        page.setElements(resultList);
        page.setThisPageNumber(pageNum);
        page.setTotalElements(totalCount);

        return page;
    }

    /**
     * 派发彩金
     */
    public void updatePayout(String partyId, double amount) {
        Wallet wallet = this.walletService.saveWalletByPartyId(partyId);
        double amount_before = wallet.getMoney();
        if (Arith.add(amount, wallet.getMoney()) < 0.0D) {
            throw new BusinessException("操作失败！修正后账户余额小于0。");
        }

        // 更新金额
        logger.info("---> LotteryReceiveServiceImpl updatePayout 发起更新钱包余额的的请求, partyId:{}, amount:{} ...", wallet.getPartyId(), amount);
        this.walletService.updateMoeny(wallet.getPartyId().toString(), amount);
        logger.info("---> LotteryReceiveServiceImpl updatePayout 已提交请求更新钱包余额, partyId:{}, amount:{}", wallet.getPartyId(), amount);

        //Party party = this.partyService.cachePartyBy(partyId, false);
        //String rechargeOrderNo = DateUtil.getToday("yyMMddHHmmss") + RandomUtil.getRandomNum(8);

        // 账变日志
        MoneyLog moneyLog = new MoneyLog();
        moneyLog.setAmount_before(amount_before);
        moneyLog.setAmount(amount);
        moneyLog.setAmount_after(Arith.add(amount_before, amount));
        moneyLog.setPartyId(partyId);
        moneyLog.setWallettype(Constants.WALLET);
        moneyLog.setContent_type(Constants.MONEYLOG_CONTENT_RECHARGE);
        moneyLog.setLog("活动奖励彩金");
        moneyLog.setCategory(Constants.MONEYLOG_CATEGORY_JACKPOT);
        moneyLog.setContent_type(Constants.MONEYLOG_CONTNET_JACKPOT);

        // 钱包日志
        WalletLog walletLog = new WalletLog();
        walletLog.setCategory(Constants.MONEYLOG_CATEGORY_COIN);
        walletLog.setCategory(Constants.MONEYLOG_CATEGORY_JACKPOT);
        walletLog.setPartyId(partyId);
        walletLog.setOrder_no("");
        walletLog.setStatus(1);
        walletLog.setAmount(amount);
        // 换算成USDT单位
        walletLog.setUsdtAmount(amount);
        walletLog.setWallettype(Constants.WALLET);
        this.walletLogService.save(walletLog);

        // 操作日志
        //Log log = new Log();
        //log.setCategory(Constants.LOG_CATEGORY_OPERATION);
        //log.setUsername(party.getUsername());
        //log.setOperator("system");
        //log.setLog("");

        // 添加赠送金额
        // 只有正式用户才需要记录报表
        //if (null != party && Constants.SECURITY_ROLE_MEMBER.equals(party.getRolename())) {
        //    this.userDataService.saveGiftMoneyHandle(partyId, money_revise);
        //}

        // this.checkGiftUserLine(party);

        this.moneyLogService.save(moneyLog);
        //this.logService.saveSync(log);

        // 充值到账后给他增加提现流水限制金额
        //party.setWithdraw_limit_amount(Arith.add(party.getWithdraw_limit_amount(), amount));
        //this.partyService.update(party);
    }

    public void setWalletService(WalletService walletService) {
        this.walletService = walletService;
    }

    public void setWalletLogService(WalletLogService walletLogService) {
        this.walletLogService = walletLogService;
    }

    public void setPartyService(PartyService partyService) {
        this.partyService = partyService;
    }

    public void setMoneyLogService(MoneyLogService moneyLogService) {
        this.moneyLogService = moneyLogService;
    }


    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }



}
