package project.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.query.NativeQuery;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;
import org.springframework.util.CollectionUtils;

import kernel.exception.BusinessException;
import kernel.util.Arith;
import kernel.util.DateUtils;
import kernel.web.Page;
import kernel.web.PagedQueryDao;
import project.Constants;
import project.contract.ContractOrder;
import project.data.model.Realtime;
import project.exchange.ExchangeApplyOrder;
import project.finance.FinanceOrder;
import project.futures.FuturesOrder;
import project.mall.utils.PlatformNameEnum;
import project.miner.model.MinerOrder;
import project.party.PartyService;
import project.party.model.Party;
import project.party.model.UserRecom;
import project.party.recom.UserRecomService;
import project.syspara.SysparaService;
import project.user.ChildrenLever;
import project.user.UserData;
import project.user.UserDataService;
import project.user.UserDataSum;
import project.wallet.dto.PartySumDataDTO;
import project.wallet.dto.RechargePartyDTO;
import security.Role;
import security.SecUser;
import security.internal.SecUserService;

@Slf4j
public class UserDataServiceImpl extends HibernateDaoSupport implements UserDataService {
    private UserRecomService userRecomService;
    private SecUserService secUserService;
    private PagedQueryDao pagedQueryDao;
    private PartyService partyService;

    private DataService dataService;

    private JdbcTemplate jdbcTemplate;

    private SysparaService sysparaService;
    /*
     * key=partyid Map<"2020-01-01",UserData>
     */
    private Map<String, Map<String, UserData>> cache = new ConcurrentHashMap<String, Map<String, UserData>>();

    public Map<String, UserData> cacheByPartyId(String partyId) {
        return cache.get(partyId);
    }

    public List<Map<String, UserData>> cacheByPartyIds(List<String> partyIds) {
        if (CollectionUtils.isEmpty(partyIds)) return new LinkedList<Map<String, UserData>>();

        List<Map<String, UserData>> result = new LinkedList<Map<String, UserData>>();
        for (String id : partyIds) {
            result.add(cache.get(id));
        }
        return result;
    }


    public void init() {
        List all = getAll();
        for (int i = 0; i < all.size(); i++) {
            UserData item = (UserData) all.get(i);
            this.setCache(item);
        }
    }

    public List<UserData> getAll() {
        return (List<UserData>) this.getHibernateTemplate().find(" FROM UserData ");
    }

    private UserData findBydate(String partyId, Date date) {
        Date createTime_begin = null;
        Date createTime_end = null;
        if (date != null) {
            createTime_begin = DateUtils.toDate(DateUtils.format(date, "yyyy-MM-dd"));
            createTime_end = DateUtils.addDate(createTime_begin, 1);
        }
        List list = getHibernateTemplate().find("FROM UserData WHERE partyId=?0 and createTime >= ?1 and createTime < ?2",
                new Object[]{partyId, createTime_begin, createTime_end});
        if (list.size() > 0) {
            return (UserData) list.get(0);
        }
        return null;
    }

    public void updateSum(UserDataSum entity) {
        this.getHibernateTemplate().update(entity);

    }

    /**
     * 根据partyId获取UserDataSum
     */
    public UserDataSum saveBySum(String partyId) {

        List list = getHibernateTemplate().find("FROM UserDataSum WHERE partyId=?0 ", new Object[]{partyId});
        if (list.size() > 0) {
            return (UserDataSum) list.get(0);
        }
        UserDataSum userDataSum = new UserDataSum();
        userDataSum.setPartyId(partyId);
        this.getHibernateTemplate().save(userDataSum);
        return userDataSum;
    }


    @Override
    public PartySumDataDTO getPartyNewDataBtDay(String startTime, String endTime) {
        StringBuilder countSql = new StringBuilder();
        countSql.append("SELECT ");
        countSql.append("COUNT(DISTINCT CASE WHEN u.RECHARGE > 0 AND p.FIRST_RECHARGE_TIME BETWEEN ? AND ? THEN p.UUID END) AS rechargeNum, ");
        countSql.append("SUM(CASE WHEN u.RECHARGE > 0 AND p.FIRST_RECHARGE_TIME BETWEEN ? AND ? THEN u.RECHARGE ELSE 0 END) AS totalRecharge, ");
        countSql.append("COUNT(DISTINCT CASE WHEN u.WITHDRAW > 0 AND u.CREATE_TIME BETWEEN ? AND ? THEN p.UUID END) AS withdrawNum, ");
        countSql.append("SUM(CASE WHEN u.WITHDRAW > 0 AND u.CREATE_TIME BETWEEN ? AND ? THEN u.WITHDRAW ELSE 0 END) AS totalWithdraw ");
        countSql.append("FROM T_USERDATA AS u ");
        countSql.append("JOIN PAT_PARTY AS p ON u.PARTY_ID = p.UUID ");
        countSql.append("WHERE (u.RECHARGE > 0 OR u.WITHDRAW > 0) ");
        countSql.append("AND (p.FIRST_RECHARGE_TIME BETWEEN ? AND ? OR u.CREATE_TIME BETWEEN ? AND ?)");

        return jdbcTemplate.queryForObject(
                countSql.toString(),
                new Object[]{
                        startTime, endTime,
                        startTime, endTime,
                        startTime, endTime,
                        startTime, endTime,
                        startTime, endTime,
                        startTime, endTime
                },
                (rs, rowNum) -> {
                    PartySumDataDTO result = new PartySumDataDTO();
                    result.setTotalRecharge(rs.getDouble("totalRecharge"));
                    result.setTotalWithdraw(rs.getDouble("totalWithdraw"));
                    result.setWithdrawNum(rs.getInt("withdrawNum"));
                    result.setRechargeNum(rs.getInt("rechargeNum"));
                    return result;
                }
        );
    }

    @Override
    public PartySumDataDTO getPartyDataBtDay(String startTime, String endTime) {
        StringBuilder countSql = new StringBuilder("SELECT ");
        countSql.append("IFNULL(SUM(RECHARGE),0) AS totalRecharge, ");
        countSql.append("IFNULL(SUM(WITHDRAW),0) AS totalWithdraw, ");
        countSql.append("COUNT(DISTINCT CASE WHEN WITHDRAW > 0 THEN PARTY_ID END) AS withdrawNum, ");
        countSql.append("COUNT(DISTINCT CASE WHEN RECHARGE > 0 THEN PARTY_ID END) AS rechargeNum ");
        countSql.append("FROM T_USERDATA ");
        countSql.append("WHERE CREATE_TIME >= ? AND CREATE_TIME <= ?");

        return jdbcTemplate.queryForObject(countSql.toString(), new Object[]{startTime,endTime}, (rs, rowNum) -> {
            PartySumDataDTO result = new PartySumDataDTO();
            result.setTotalRecharge(rs.getDouble("totalRecharge"));
            result.setTotalWithdraw(rs.getDouble("totalWithdraw"));
            result.setWithdrawNum(rs.getInt("withdrawNum"));
            result.setRechargeNum(rs.getInt("rechargeNum"));
            return result;
        });
    }

    public void setCache(UserData entity) {

        if (Objects.nonNull(entity.getPartyId())) {

            Map<String, UserData> map_party = cache.get(entity.getPartyId().toString());
            if (map_party == null) {
                map_party = new ConcurrentHashMap<>();
            }
            map_party.put(DateUtils.format(entity.getCreateTime(), DateUtils.DEFAULT_DATE_FORMAT), entity);
            cache.put(entity.getPartyId().toString(), map_party);
        }
    }

    public void save(UserData entity) {
        UserData db = findBydate(entity.getPartyId().toString(), entity.getCreateTime());
        if (db != null) {
            db.setRecharge_dapp(Arith.add(db.getRecharge_dapp(), entity.getRecharge_dapp()));
            db.setWithdraw_dapp(Arith.add(db.getWithdraw_dapp(), entity.getWithdraw_dapp()));

            db.setRechargeCommission(Arith.add(db.getRechargeCommission(), entity.getRechargeCommission()));

            db.setWithdrawCommission(Arith.add(db.getWithdrawCommission(), entity.getWithdrawCommission()));

            db.setRecharge(Arith.add(db.getRecharge(), entity.getRecharge()));
            db.setRecharge_eth(Arith.add(db.getRecharge_eth(), entity.getRecharge_eth()));
            db.setRecharge_usdt(Arith.add(db.getRecharge_usdt(), entity.getRecharge_usdt()));
            db.setRecharge_btc(Arith.add(db.getRecharge_btc(), entity.getRecharge_btc()));
            db.setRecharge_usdc(Arith.add(db.getRecharge_usdc(), entity.getRecharge_usdc()));
            db.setRecharge_ltc(Arith.add(db.getRecharge_ltc(), entity.getRecharge_ltc()));
            // 充值返佣
            db.setRechargeRecom(Arith.add(db.getRechargeRecom(), entity.getRechargeRecom()));

            db.setWithdraw_all(Arith.add(db.getWithdraw_all(), entity.getWithdraw_all()));
            db.setWithdraw(Arith.add(db.getWithdraw(), entity.getWithdraw()));
            db.setWithdraw_eth(Arith.add(db.getWithdraw_eth(), entity.getWithdraw_eth()));
            db.setWithdraw_btc(Arith.add(db.getWithdraw_btc(), entity.getWithdraw_btc()));
            db.setWithdraw_usdt(Arith.add(db.getWithdraw_usdt(), entity.getWithdraw_usdt()));
            db.setWithdraw_usdc(Arith.add(db.getWithdraw_usdc(), entity.getWithdraw_usdc()));

            db.setAmount(Arith.add(db.getAmount(), entity.getAmount()));
            db.setFee(Arith.add(db.getFee(), entity.getFee()));
            db.setOrder_income(Arith.add(db.getOrder_income(), entity.getOrder_income()));
            db.setFinance_amount(Arith.add(db.getFinance_amount(), entity.getFinance_amount()));
            db.setFinance_income(Arith.add(db.getFinance_income(), entity.getFinance_income()));
            db.setExchange_amount(Arith.add(db.getExchange_amount(), entity.getExchange_amount()));
            db.setExchange_fee(Arith.add(db.getExchange_fee(), entity.getExchange_fee()));
            db.setExchange_income(Arith.add(db.getExchange_income(), entity.getExchange_income()));
            db.setCoin_income(Arith.add(db.getCoin_income(), entity.getCoin_income()));
            db.setFurtures_amount(Arith.add(db.getFurtures_amount(), entity.getFurtures_amount()));
            db.setFurtures_fee(Arith.add(db.getFurtures_fee(), entity.getFurtures_fee()));
            db.setFurtures_income(Arith.add(db.getFurtures_income(), entity.getFurtures_income()));

            db.setReco_num(db.getReco_num() + entity.getReco_num());
            db.setRecharge_withdrawal_fee(
                    Arith.add(db.getRecharge_withdrawal_fee(), entity.getRecharge_withdrawal_fee()));
            db.setGift_money(Arith.add(db.getGift_money(), entity.getGift_money()));

//			db.setMiner_amount(Arith.add(db.getMiner_amount(), entity.getMiner_amount()));
//			db.setMiner_income(Arith.add(db.getMiner_income(), entity.getMiner_income()));

//			db.setInvestAmount(Arith.add(db.getInvestAmount(), entity.getInvestAmount()));
//			db.setProjectTotal(db.getProjectTotal()+entity.getProjectTotal());
//			db.setPrincipal(db.getPrincipal()+entity.getPrincipal());

            // 质押2.0
            //db.setGalaxy_amount(Arith.add(db.getGalaxy_amount(), entity.getGalaxy_amount()));
            //db.setGalaxy_income(Arith.add(db.getGalaxy_income(), entity.getGalaxy_income()));

            // 理财
//			db.setInvestIncome(Arith.add(db.getInvestIncome(), entity.getInvestIncome()));
            db.setRebate1(Arith.add(db.getRebate1(), entity.getRebate1()));
            db.setRebate2(Arith.add(db.getRebate2(), entity.getRebate2()));

            db.setThird_recharge_amount(Arith.add(db.getThird_recharge_amount(), entity.getThird_recharge_amount()));
            db.setHolding_money(Arith.add(db.getHolding_money(), entity.getHolding_money()));
            db.setTransfer_in_money(Arith.add(db.getTransfer_in_money(), entity.getTransfer_in_money()));
            db.setTransfer_out_money(Arith.add(db.getTransfer_out_money(), entity.getTransfer_out_money()));

            db.setExchange_lever_amount(Arith.add(db.getExchange_lever_amount(), entity.getExchange_lever_amount()));
            db.setExchange_lever_fee(Arith.add(db.getExchange_lever_fee(), entity.getExchange_lever_fee()));
            db.setExchange_lever_order_income(Arith.add(db.getExchange_lever_order_income(), entity.getExchange_lever_order_income()));
            db.setTranslate(Arith.add(db.getTranslate(), entity.getTranslate()));
            db.setSellerTotalSales(Arith.add(db.getSellerTotalSales(), entity.getSellerTotalSales()));
            getHibernateTemplate().update(db);
            setCache(db);
        } else {
            getHibernateTemplate().save(entity);
            setCache(entity);
        }
    }

    public void saveRechargeHandle(String partyId, double amount, String symbol, double volume) {

//		if ("USDT_DAPP".equals(symbol)) {
//			Party party = this.partyService.cachePartyBy(partyId, true);
//			UserData userData = new UserData();
//			userData.setRolename(party.getRolename());
//			userData.setCreateTime(new Date());
//			userData.setPartyId(partyId);
//			userData.setRecharge_dapp(amount);
//			save(userData);
//			return;
//		}

        SecUser user = this.secUserService.findUserByPartyId(partyId);
        user.getRoles();

        boolean guest = false;
        for (Role role : user.getRoles()) {
            if (Constants.SECURITY_ROLE_GUEST.equals(role.getRoleName())) {
                guest = true;
            }
        }
        if (guest) {
            return;
        }

        Party party = this.partyService.cachePartyBy(partyId, false);
        UserData userData = new UserData();
        userData.setRolename(party.getRolename());
        userData.setCreateTime(new Date());
        userData.setPartyId(party.getId().toString());

        String platformName = sysparaService.find("platform_name").getValue();
        if(PlatformNameEnum.WORTEN.getDescription().equals(platformName)){
            userData.setRecharge(amount);
            if ("usdt".equalsIgnoreCase(symbol)) {
                userData.setRecharge_usdt(volume);
            } else if ("usdc".equalsIgnoreCase(symbol)) {
                userData.setRecharge_usdc(volume);
            } else if ("eth".equalsIgnoreCase(symbol)) {
                userData.setRecharge_eth(volume);
            } else if ("btc".equalsIgnoreCase(symbol)) {
                userData.setRecharge_btc(volume);
            }
        } else {
            userData.setRecharge(amount);
            userData.setRecharge_usdt(amount);
        }
        save(userData);
    }

    public void saveRechargeHandleDapp(String partyId, double amount, String symbol) {
        SecUser user = this.secUserService.findUserByPartyId(partyId);
        user.getRoles();
        boolean guest = false;
        for (Role role : user.getRoles()) {
            if (Constants.SECURITY_ROLE_GUEST.equals(role.getRoleName())) {
                guest = true;
            }
        }
        if (guest) {
            return;
        }
        Party party = partyService.cachePartyBy(partyId, false);
        UserData userData = new UserData();
        userData.setRolename(party.getRolename());
        userData.setCreateTime(new Date());
        userData.setPartyId(party.getId().toString());
        if ("usdt".equals(symbol)) {
            userData.setRecharge_dapp(amount);
        }
        save(userData);
    }

    /**
     * 更新充值返佣
     */
    public void saveUserDataForRechargeRecom(String partyId, double amount) {
        SecUser user = this.secUserService.findUserByPartyId(partyId);
        user.getRoles();
        boolean guest = false;
        for (Role role : user.getRoles()) {
            if (Constants.SECURITY_ROLE_GUEST.equals(role.getRoleName())) {
                guest = true;
            }
        }
        if (guest) {
            return;
        }

        Party party = partyService.cachePartyBy(partyId, false);
        UserData userData = new UserData();
        userData.setRolename(party.getRolename());
        userData.setCreateTime(new Date());
        userData.setPartyId(party.getId().toString());
        userData.setRechargeRecom(amount);
        save(userData);
    }

    public void saveWithdrawHandle(String partyId, double amount, double amount_fee, String symbol, double arrival_amount) {
        log.info("---> saveWithdrawHandle partyId:{}, amount:{},amount_fee:{},symbol:{}", partyId, amount, amount_fee, symbol);
        SecUser user = this.secUserService.findUserByPartyId(partyId);
        user.getRoles();
        boolean guest = false;
        for (Role role : user.getRoles()) {
            if (Constants.SECURITY_ROLE_GUEST.equals(role.getRoleName())) {
                guest = true;
            }
        }
        log.info("->>>>>>>guest:{}", guest);
        if (guest) {
            return;
        }

        Party party = partyService.cachePartyBy(partyId, false);
        if (null == party) {
            throw new BusinessException("该用户异常，请稍后重试或联系客服");
        }
        UserData userData = new UserData();
        userData.setRolename(party.getRolename() == null ? Constants.SECURITY_ROLE_MEMBER : party.getRolename());
        userData.setCreateTime(new Date());
        userData.setPartyId(partyId);

        String platformName = sysparaService.find("platform_name").getValue();
        if(PlatformNameEnum.WORTEN.getDescription().equals(platformName)){
            if ("usdt".equalsIgnoreCase(symbol)) {
                userData.setWithdraw(amount);
                userData.setWithdraw_usdt(amount);
                userData.setRecharge_withdrawal_fee(amount_fee);
                userData.setWithdraw_all(amount);
            } else if ("eth".equalsIgnoreCase(symbol)) {
                userData.setWithdraw(amount);
                userData.setRecharge_withdrawal_fee(amount_fee);
                userData.setWithdraw_all(amount);
                userData.setWithdraw_eth(arrival_amount);
            } else if ("btc".equalsIgnoreCase(symbol)) {
                userData.setWithdraw(amount);
                userData.setRecharge_withdrawal_fee(amount_fee);
                userData.setWithdraw_all(amount);
                userData.setWithdraw_btc(arrival_amount);
            } else if ("usdc".equalsIgnoreCase(symbol)) {
                userData.setWithdraw(amount);
                userData.setRecharge_withdrawal_fee(amount_fee);
                userData.setWithdraw_all(amount);
                userData.setWithdraw_usdc(arrival_amount);
            }
        } else {
            userData.setWithdraw(amount);
            userData.setRecharge_withdrawal_fee(amount_fee);
            userData.setWithdraw_all(amount);
        }

//        if (StringUtils.isEmpty(symbol)
//                || "usdt".equals(symbol)
//                || "usdc".equals(symbol)) {
//            userData.setWithdraw(amount);
//            userData.setRecharge_withdrawal_fee(amount_fee);
//            userData.setWithdraw_all(amount);
//        } else {
//            List<Realtime> realtime_list = this.dataService.realtime(symbol);
//            Realtime realtime = null;
//            if (realtime_list.size() > 0) {
//                realtime = realtime_list.get(0);
//            } else {
//                throw new BusinessException("系统错误，请稍后重试");
//            }
//            if ("btc".equals(symbol)) {
//                userData.setRecharge_withdrawal_fee(Arith.mul(amount_fee, realtime.getClose()));
//                userData.setWithdraw_btc(amount);
//                userData.setWithdraw_all(Arith.mul(amount, realtime.getClose()));
//            }
//            if ("eth".equals(symbol)) {
//                userData.setRecharge_withdrawal_fee(Arith.mul(amount_fee, realtime.getClose()));
//                userData.setWithdraw_eth(amount);
//                userData.setWithdraw_all(Arith.mul(amount, realtime.getClose()));
//            }
//        }
        save(userData);

    }

    public void saveWithdrawHandleDapp(String partyId, double amount, double amount_fee, String symbol) {
        System.out.println("saveWithdrawHandleDapp -> partyId:" + partyId);
        SecUser user = this.secUserService.findUserByPartyId(partyId);
        user.getRoles();
        boolean guest = false;
        for (Role role : user.getRoles()) {
            if (Constants.SECURITY_ROLE_GUEST.equals(role.getRoleName())) {
                guest = true;
            }
        }
        if (guest) {
            return;
        }

        Party party = partyService.cachePartyBy(partyId, false);
        UserData userData = new UserData();
        userData.setRolename(party.getRolename());
        userData.setCreateTime(new Date());
        userData.setPartyId(party.getId().toString());

        if (StringUtils.isEmpty(symbol) || "usdt".equals(symbol)) {
            userData.setWithdraw_dapp(amount);
        }
        save(userData);
    }

    @Override
    public void saveBrushRebate(String partyId, double amount, int level, double ben) {
        SecUser user = this.secUserService.findUserByPartyId(partyId);
        user.getRoles();
        boolean guest = false;
        for (Role role : user.getRoles()) {
            if (Constants.SECURITY_ROLE_GUEST.equals(role.getRoleName())) {
                guest = true;
            }
        }
        if (guest) {
            return;
        }

        Party party = partyService.cachePartyBy(partyId, false);
        UserData userData = new UserData();
        userData.setRolename(party.getRolename());
        userData.setCreateTime(new Date());
        userData.setPartyId(party.getId().toString());


//		if(level==0){
//			userData.setInvestIncome(amount);
//			if(ben>0){
//				userData.setPrincipal(ben);
//			}
//		}else if(level ==1){
//			userData.setRebate1(amount);
//		}else if(level ==2){
//			userData.setRebate2(amount);
//		}else{
//			return;
//		}

        save(userData);
    }


    @Override
    public void saveInsvestBuy(String partyId, double amount) {
        SecUser user = this.secUserService.findUserByPartyId(partyId);
        user.getRoles();
        boolean guest = false;
        for (Role role : user.getRoles()) {
            if (Constants.SECURITY_ROLE_GUEST.equals(role.getRoleName())) {
                guest = true;
            }
        }
        if (guest) {
            return;
        }

        Party party = partyService.cachePartyBy(partyId, false);
        UserData userData = new UserData();
        userData.setRolename(party.getRolename());
        userData.setCreateTime(new Date());
        userData.setPartyId(party.getId().toString());

//		userData.setInvestAmount(amount);
//		userData.setProjectTotal(1);


        save(userData);
    }

    public void saveClose(ContractOrder order) {
        SecUser user = this.secUserService.findUserByPartyId(order.getPartyId());
        user.getRoles();
        boolean guest = false;
        for (Role role : user.getRoles()) {
            if (Constants.SECURITY_ROLE_GUEST.equals(role.getRoleName()) || Constants.SECURITY_ROLE_TEST.equals(role.getRoleName())) {
                guest = true;
            }
        }
        if (guest) {
            return;
        }

        Party party = partyService.cachePartyBy(order.getPartyId(), false);
        UserData userData = new UserData();
        userData.setRolename(party.getRolename());
        userData.setCreateTime(new Date());
        userData.setPartyId(party.getId().toString());
        userData.setAmount(order.getDeposit_open());
        userData.setFee(order.getFee());
        userData.setOrder_income(Arith.sub(order.getAmount_close(), order.getDeposit_open()));
        save(userData);

//		UserRecom userRecom = this.userRecomService.findByPartyId(order.getPartyId());
//		if (userRecom == null) {
//			return;
//		}
//		List<UserRecom> parents = this.userRecomService.getParents(userRecom.getPartyId());
//
//		for (int i = 0; i < parents.size(); i++) {
//			Party party_parent = partyService.cachePartyBy(parents.get(i).getReco_id());
//
//			if (Constants.SECURITY_ROLE_AGENT.equals(party_parent.getRolename())) {
//				UserData userData_reco = new UserData();
//				userData_reco.setRolename(party_parent.getRolename());
//				userData_reco.setCreateTime(new Date());
//				userData_reco.setPartyId(parents.get(i).getReco_id());
//				userData_reco.setAmount(order.getDeposit());
//				userData_reco.setFee(order.getFee());
//				userData_reco.setOrder_income(Arith.sub(order.getAmount_close(), order.getDeposit_open()));
//				save(userData_reco);
//			}
//		}
    }

    /**
     * 交割奖励
     *
     * @param partyId
     * @param profit
     */
    public void saveFuturesProfit(String partyId, double profit) {
        SecUser user = this.secUserService.findUserByPartyId(partyId);
        user.getRoles();
        boolean guest = false;
        for (Role role : user.getRoles()) {
            if (Constants.SECURITY_ROLE_GUEST.equals(role.getRoleName())) {
                guest = true;
            }
        }
        if (guest) {
            return;
        }

        Party party = partyService.cachePartyBy(partyId, false);
        UserData userData = new UserData();
        userData.setRolename(party.getRolename());
        userData.setCreateTime(new Date());
        userData.setPartyId(party.getId().toString());
        userData.setFurtures_income(profit);
        save(userData);
    }

    /**
     * 订单结算
     *
     * @param partyId
     * @param translate
     */
    public void saveFreedAmountProfit(String partyId, double translate, double sellerTotalSales) {
        SecUser user = this.secUserService.findUserByPartyId(partyId);
        if(user == null){
            return;
        }
        user.getRoles();
        boolean guest = false;
        for (Role role : user.getRoles()) {
            if (Constants.SECURITY_ROLE_GUEST.equals(role.getRoleName())) {
                guest = true;
            }
        }
        if (guest) {
            return;
        }

        Party party = partyService.cachePartyBy(partyId, false);
        UserData userData = new UserData();
        userData.setRolename(party.getRolename());
        userData.setCreateTime(new Date());
        userData.setPartyId(party.getId().toString());
        userData.setTranslate(translate);
        userData.setSellerTotalSales(sellerTotalSales);
        save(userData);
    }

    /**
     * 矿机买入
     */
    @Override
    public void saveMinerBuy(MinerOrder order) {
        SecUser user = this.secUserService.findUserByPartyId(order.getPartyId());
        user.getRoles();
        boolean guest = false;
        for (Role role : user.getRoles()) {
            if (Constants.SECURITY_ROLE_GUEST.equals(role.getRoleName()) || Constants.SECURITY_ROLE_TEST.equals(role.getRoleName())) {
                guest = true;
            }
        }
        if (guest) {
            return;
        }

        Party party = partyService.cachePartyBy(order.getPartyId(), false);
        UserData userData = new UserData();
        userData.setRolename(party.getRolename());
        userData.setCreateTime(new Date());
        userData.setPartyId(party.getId().toString());
        userData.setMiner_amount(order.getAmount());
        save(userData);
    }

    /**
     * 矿机赎回
     */
    @Override
    public void saveMinerClose(MinerOrder order) {
        SecUser user = this.secUserService.findUserByPartyId(order.getPartyId());
        user.getRoles();
        boolean guest = false;
        for (Role role : user.getRoles()) {
            if (Constants.SECURITY_ROLE_GUEST.equals(role.getRoleName()) || Constants.SECURITY_ROLE_TEST.equals(role.getRoleName())) {
                guest = true;
            }
        }
        if (guest) {
            return;
        }

        Party party = partyService.cachePartyBy(order.getPartyId(), false);
        UserData userData = new UserData();
        userData.setRolename(party.getRolename());
        userData.setCreateTime(new Date());
        userData.setPartyId(party.getId().toString());
        userData.setMiner_amount(Arith.sub(0, order.getAmount()));
        save(userData);
    }

    /**
     * 矿机利息
     *
     * @param partyId 获利人
     * @param profit  利息
     */
    @Override
    public void saveMinerProfit(String partyId, double profit) {
        SecUser user = this.secUserService.findUserByPartyId(partyId);
        user.getRoles();
        boolean guest = false;
        for (Role role : user.getRoles()) {
            if (Constants.SECURITY_ROLE_GUEST.equals(role.getRoleName())) {
                guest = true;
            }
        }
        if (guest) {
            return;
        }

        Party party = partyService.cachePartyBy(partyId, false);
        UserData userData = new UserData();
        userData.setRolename(party.getRolename());
        userData.setCreateTime(new Date());
        userData.setPartyId(party.getId().toString());
        userData.setMiner_income(profit);
        save(userData);
    }

    /**
     * 质押2.0收益
     */
    public void saveUserDataForGalaxy(String partyId, double amount, boolean ifIncome) {
        SecUser user = this.secUserService.findUserByPartyId(partyId);
        user.getRoles();
        boolean guest = false;
        for (Role role : user.getRoles()) {
            if (Constants.SECURITY_ROLE_GUEST.equals(role.getRoleName())) {
                guest = true;
            }
        }
        if (guest) {
            return;
        }

        Party party = partyService.cachePartyBy(partyId, false);
        UserData userData = new UserData();
        userData.setRolename(party.getRolename());
        userData.setCreateTime(new Date());
        userData.setPartyId(party.getId().toString());
        if (ifIncome) {
            System.out.println("质押2.0 更新userdata 收益 " + amount);
            userData.setGalaxy_income(amount);
        } else {
            System.out.println("质押2.0 更新userdata 下单金额 " + amount);
            userData.setGalaxy_amount(amount);
        }
        save(userData);
    }

    /**
     * 矿机利息
     *
     * @param partyId 获利人
     * @param profit  利息
     */
    @Override
    public void saveGiftMoneyHandle(String partyId, double amount) {
        SecUser user = this.secUserService.findUserByPartyId(partyId);
        user.getRoles();
        boolean guest = false;
        for (Role role : user.getRoles()) {
            if (Constants.SECURITY_ROLE_GUEST.equals(role.getRoleName())) {
                guest = true;
            }
        }
        if (guest) {
            return;
        }

        Party party = partyService.cachePartyBy(partyId, false);
        UserData userData = new UserData();
        userData.setRolename(party.getRolename());
        userData.setCreateTime(new Date());
        userData.setPartyId(party.getId().toString());
        userData.setGift_money(amount);
        save(userData);

    }

    public void setUserRecomService(UserRecomService userRecomService) {
        this.userRecomService = userRecomService;
    }

    public void setSecUserService(SecUserService secUserService) {
        this.secUserService = secUserService;
    }

    @Override
    public ChildrenLever getCacheChildrenLever4(String partyId) {

        ChildrenLever childrenLever = new ChildrenLever();
        /**
         * lever1
         */
        List<UserRecom> userrecom_lever1 = userRecomService.findRecoms(partyId);
        for (int i = 0; i < userrecom_lever1.size(); i++) {
            childrenLever.getLever1().add(userrecom_lever1.get(i).getPartyId().toString());
        }
        /**
         * lever2
         */
        if (childrenLever.getLever1().size() == 0) {
            return childrenLever;
        }
        for (int i = 0; i < childrenLever.getLever1().size(); i++) {
            List<UserRecom> userrecom_lever2 = userRecomService.findRecoms(childrenLever.getLever1().get(i));
            for (int j = 0; j < userrecom_lever2.size(); j++) {
                childrenLever.getLever2().add(userrecom_lever2.get(j).getPartyId().toString());
            }
        }

//		/**
//		 * lever3
//		 */
//		if (childrenLever.getLever2().size() == 0) {
//			return childrenLever;
//		}
//		for (int i = 0; i < childrenLever.getLever2().size(); i++) {
//			List<UserRecom> userrecom_lever3 = userRecomService.findRecoms(childrenLever.getLever2().get(i));
//			for (int j = 0; j < userrecom_lever3.size(); j++) {
//				childrenLever.getLever3().add(userrecom_lever3.get(j).getPartyId().toString());
//			}
//		}
//
//		/**
//		 * lever4
//		 */
//		if (childrenLever.getLever3().size() == 0) {
//			return childrenLever;
//		}
//		for (int i = 0; i < childrenLever.getLever3().size(); i++) {
//			List<UserRecom> userrecom_lever4 = userRecomService.findRecoms(childrenLever.getLever3().get(i));
//			for (int j = 0; j < userrecom_lever4.size(); j++) {
//				childrenLever.getLever4().add(userrecom_lever4.get(j).getPartyId().toString());
//			}
//
//		}

        return childrenLever;
    }

    @Override
    public void saveRegister(String partyId) {
        SecUser user = this.secUserService.findUserByPartyId(partyId);
        user.getRoles();
        boolean guest = false;
        for (Role role : user.getRoles()) {
            if (Constants.SECURITY_ROLE_GUEST.equals(role.getRoleName())) {
                guest = true;
            }
        }
        if (guest) {
            return;
        }

        UserRecom userRecom = this.userRecomService.findByPartyId(partyId);
        if (userRecom == null) {
            return;
        }
        List<UserRecom> parents = this.userRecomService.getParents(userRecom.getPartyId());
        int loop = 4;
        for (int i = 0; i < parents.size(); i++) {
            Party party_parent = partyService.cachePartyBy(parents.get(i).getReco_id(), true);
            if (Constants.SECURITY_ROLE_MEMBER.equals(party_parent.getRolename()) && loop > 0) {
                UserData userData_reco = new UserData();
                userData_reco.setRolename(party_parent.getRolename());
                userData_reco.setCreateTime(new Date());
                userData_reco.setPartyId(parents.get(i).getReco_id());
                userData_reco.setReco_num(1);
                save(userData_reco);

                UserDataSum userDataSum = this.saveBySum(parents.get(i).getReco_id().toString());
                userDataSum.setReco_num(userDataSum.getReco_num() + 1);
                this.updateSum(userDataSum);
                loop--;
            }

//			if (Constants.SECURITY_ROLE_AGENT.equals(party_parent.getRolename())) {
//				UserDataSum userDataSum = this.findBySum(parents.get(i).getReco_id().toString());
//				userDataSum.setReco_num(userDataSum.getReco_num() + 1);
//				this.updateSum(userDataSum);
//			}
        }

    }

    /**
     * 资金盘定制化需求，等盘口下架可以删除
     */
    public List<Map<String, Object>> getChildrenLevelPagedForGalaxy(int pageNo, int pageSize, String partyId, Integer levelNum) {

        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

        ChildrenLever children = getCacheChildrenLever4(partyId);
        // 获取子代partyId
        List<String> level = new ArrayList<String>();
        if (levelNum == 1) {
            level = children.getLever1();
        }
        if (levelNum == 2) {
            level = children.getLever2();
        }
        if (levelNum == 3) {
            level = children.getLever3();
        }
        if (levelNum == 4) {
            level = children.getLever4();
        }
        if (level == null || level.isEmpty()) {
            return list;
        }

        StringBuffer queryString = new StringBuffer(
                " SELECT party.USERNAME username ,party.UUID partyId ");
        queryString.append(
                "  from PAT_PARTY party  WHERE 1 = 1 ");
        Map parameters = new HashMap();

        queryString.append(" and party.UUID in ( :partyId ) ");
        parameters.put("partyId", level);

        Page page = this.pagedQueryDao.pagedQuerySQL(pageNo, pageSize, queryString.toString(), parameters);
        List element = new ArrayList();
        element = page.getElements();
        Map<String, Object> element_map = new HashMap<String, Object>();
        // double reco_sum = Arith.add(v1, v2)
        for (int i = 0; i < page.getElements().size(); i++) {
            element_map = (Map<String, Object>) element.get(i);

            ChildrenLever childrenLever = getCacheChildrenLever4(element_map.get("partyId").toString());
            // 获取子代partyId
            List<String> level_children = new ArrayList<String>();
            if (levelNum == 1) {
                level_children = childrenLever.getLever1();
            }
            if (levelNum == 2) {
                level_children = childrenLever.getLever2();
            }
            if (levelNum == 3) {
                level_children = childrenLever.getLever3();
            }
            if (levelNum == 4) {
                level_children = childrenLever.getLever4();
            }

            element_map.put("reco_sum", level_children.size());
            // list里面的总业绩
            Map<String, UserData> map = cacheByPartyId(partyId);
            double sum = 0;
            if (null != map && map.size() > 0) {
                for (UserData userData : map.values()) {
                    sum += userData.getGalaxy_income();
                }
            }

            element_map.put("recharge_sum", sum);

            list.add(element_map);
        }
        return list;
    }

    /**
     * 资金盘-抢单项目
     */
    public List<Map<String, Object>> getChildrenLevelPagedForBrush(int pageNo, int pageSize, String partyId, Integer levelNum) {

        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

        ChildrenLever children = getCacheChildrenLever4(partyId);
        // 获取子代partyId
        List<String> level = new ArrayList<String>();
        if (levelNum == 1) {
            level = children.getLever1();
        }
        if (levelNum == 2) {
            level = children.getLever2();
        }
        if (levelNum == 3) {
            level = children.getLever3();
        }

        if (level == null || level.isEmpty()) {
            return list;
        }

        StringBuffer queryString = new StringBuffer(
                " SELECT party.USERNAME username ,party.UUID partyId ,party.CREATE_TIME createTime ,party.AVATAR avatar ");
        queryString.append(
                "  from PAT_PARTY party  WHERE 1 = 1 ");
        Map parameters = new HashMap();

        queryString.append(" and party.UUID in ( :partyId ) ORDER BY CREATE_TIME DESC");
        parameters.put("partyId", level);

        Page page = this.pagedQueryDao.pagedQuerySQL(pageNo, pageSize, queryString.toString(), parameters);
        List element = page.getElements();
        Map<String, Object> element_map = new HashMap<String, Object>();
        for (int i = 0; i < page.getElements().size(); i++) {
            element_map = (Map<String, Object>) element.get(i);

            String sql = "select count(1) ,IFNULL(sum(REBATE),0) from T_INVEST_REBATE where `LEVEL` = ?0 and ORDER_PARTY_ID = ?1 ";
            NativeQuery<Object[]> nativeQuery = this.getHibernateTemplate().getSessionFactory().getCurrentSession().createNativeQuery(sql);
            nativeQuery.setParameter(0, levelNum);
            nativeQuery.setParameter(1, element_map.get("partyId").toString());

            Object[] results = nativeQuery.getSingleResult();
            element_map.put("countOrder", results[0]);
            element_map.put("rebate", results[1]);
            element_map.put("createTime", element_map.get("createTime").toString().replace("T", " "));

            list.add(element_map);
        }
        return list;
    }

    /**
     * 交易所
     */
    public List<Map<String, Object>> getChildrenLevelPaged(int pageNo, int pageSize, String partyId, Integer levelNum) {

        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

        ChildrenLever children = getCacheChildrenLever4(partyId);
        // 获取子代partyId
        List<String> level = new ArrayList<String>();
        if (levelNum == 1) {
            level = children.getLever1();
        }
        if (levelNum == 2) {
            level = children.getLever2();
        }
        if (levelNum == 3) {
            level = children.getLever3();
        }
        if (levelNum == 4) {
            level = children.getLever4();
        }
        if (level == null || level.isEmpty()) {
            return list;
        }

        StringBuffer queryString = new StringBuffer(
                " SELECT party.USERNAME username ,party.UUID partyId, party.KYC_AUTHORITY kyc_authority ");
        queryString.append(
                "  from PAT_PARTY party  WHERE 1 = 1 ");
        Map parameters = new HashMap();

        queryString.append(" and party.UUID in ( :partyId ) ");
        parameters.put("partyId", level);

        Page page = this.pagedQueryDao.pagedQuerySQL(pageNo, pageSize, queryString.toString(), parameters);
        List element = new ArrayList();
        element = page.getElements();
        Map<String, Object> element_map = new HashMap<String, Object>();

        for (int i = 0; i < page.getElements().size(); i++) {
            element_map = (Map<String, Object>) element.get(i);

            ChildrenLever childrenLever = getCacheChildrenLever4(element_map.get("partyId").toString());
            // 获取子代partyId
            List<String> level_children = new ArrayList<String>();
            if (levelNum == 1) {
                level_children = childrenLever.getLever1();
            }
            if (levelNum == 2) {
                level_children = childrenLever.getLever2();
            }
            if (levelNum == 3) {
                level_children = childrenLever.getLever3();
            }
            if (levelNum == 4) {
                level_children = childrenLever.getLever4();
            }
            element_map.put("reco_sum", level_children.size());
            list.add(element_map);
        }
        return list;
    }

    /**
     * 交易所-推广数据汇总
     */
    public Map<String, String> getPromoteData(String partyId, Map<String, String> data, Date startTime, Date endTime) {

        ChildrenLever children = getCacheChildrenLever4(partyId);
        // 获取子代partyId
        List<String> level1 = new ArrayList<String>();
        List<String> level2 = new ArrayList<String>();
        List<String> level3 = new ArrayList<String>();
        List<String> level4 = new ArrayList<String>();
        level1 = children.getLever1();
        level2 = children.getLever2();
        level3 = children.getLever3();
        level4 = children.getLever4();

        Map<String, String> peoples = new HashMap<>();
        double rechangeAmount = 0;
        for (String value1 : level1) {
            Map<String, UserData> map = cacheByPartyId(value1);
            if (null != map && map.size() > 0) {
                for (UserData userData : map.values()) {
                    if (userData.getRecharge() <= 0) {
                        continue;
                    }
                    if (null != startTime && null != endTime) {
                        Date createTime = userData.getCreateTime();
                        if (createTime.after(endTime) && createTime.before(startTime)) {
                            rechangeAmount += userData.getRecharge();
                            peoples.put(String.valueOf(userData.getPartyId()), "");
                        }
                    } else {
                        rechangeAmount += userData.getRecharge();
                        peoples.put(String.valueOf(userData.getPartyId()), "");
                    }
                }
            }
        }

        for (String value2 : level2) {
            Map<String, UserData> map = cacheByPartyId(value2);
            if (null != map && map.size() > 0) {
                for (UserData userData : map.values()) {
                    if (userData.getRecharge() <= 0) {
                        continue;
                    }
                    if (null != startTime && null != endTime) {
                        Date createTime = userData.getCreateTime();
                        if (createTime.after(endTime) && createTime.before(startTime)) {
                            rechangeAmount += userData.getRecharge();
                            peoples.put(String.valueOf(userData.getPartyId()), "");
                        }
                    } else {
                        rechangeAmount += userData.getRecharge();
                        peoples.put(String.valueOf(userData.getPartyId()), "");
                    }
                }
            }
        }

        for (String value3 : level3) {
            Map<String, UserData> map = cacheByPartyId(value3);
            if (null != map && map.size() > 0) {
                for (UserData userData : map.values()) {
                    if (userData.getRecharge() <= 0) {
                        continue;
                    }
                    if (null != startTime && null != endTime) {
                        Date createTime = userData.getCreateTime();
                        if (createTime.after(endTime) && createTime.before(startTime)) {
                            rechangeAmount += userData.getRecharge();
                            peoples.put(String.valueOf(userData.getPartyId()), "");
                        }
                    } else {
                        rechangeAmount += userData.getRecharge();
                        peoples.put(String.valueOf(userData.getPartyId()), "");
                    }
                }
            }
        }

        for (String value4 : level4) {
            Map<String, UserData> map = cacheByPartyId(value4);
            if (null != map && map.size() > 0) {
                for (UserData userData : map.values()) {
                    if (userData.getRecharge() <= 0) {
                        continue;
                    }
                    if (null != startTime && null != endTime) {
                        Date createTime = userData.getCreateTime();
                        if (createTime.after(endTime) && createTime.before(startTime)) {
                            rechangeAmount += userData.getRecharge();
                            peoples.put(String.valueOf(userData.getPartyId()), "");
                        }
                    } else {
                        rechangeAmount += userData.getRecharge();
                        peoples.put(String.valueOf(userData.getPartyId()), "");
                    }
                }
            }
        }

        data.put("peopleNum", String.valueOf(peoples.size()));
        data.put("rechangeAmount", String.valueOf(rechangeAmount));
        return data;
    }

    /**
     * 三方充值
     */
    public void saveThirdRechargeMoneyHandle(String partyId, double amount) {
        SecUser user = this.secUserService.findUserByPartyId(partyId);
        user.getRoles();
        boolean guest = false;
        for (Role role : user.getRoles()) {
            if (Constants.SECURITY_ROLE_GUEST.equals(role.getRoleName())) {
                guest = true;
            }
        }
        if (guest) {
            return;
        }

        Party party = partyService.cachePartyBy(partyId, false);
        UserData userData = new UserData();
        userData.setRolename(party.getRolename());
        userData.setCreateTime(new Date());
        userData.setPartyId(party.getId().toString());
        userData.setThird_recharge_amount(amount);
        userData.setRecharge(amount);
        save(userData);

    }

    /**
     * 持有金额（钱包+扩展钱包）
     *
     * @param partyId
     * @param amount
     */
    public void saveHodingMoneyHandle(String partyId, double amount) {
        SecUser user = this.secUserService.findUserByPartyId(partyId);
        user.getRoles();
        boolean guest = false;
        for (Role role : user.getRoles()) {
            if (Constants.SECURITY_ROLE_GUEST.equals(role.getRoleName())) {
                guest = true;
            }
        }
        if (guest) {
            return;
        }

        Party party = partyService.cachePartyBy(partyId, false);
        UserData userData = new UserData();
        userData.setRolename(party.getRolename());
        userData.setCreateTime(new Date());
        userData.setPartyId(party.getId().toString());
        userData.setHolding_money(amount);
        save(userData);

    }
//	/**
//	 * 币币杠杆平仓
//	 * 
//	 * @param partyId
//	 * @param amount
//	 */
//	public void saveExchangeLeverClose(ExchangeLeverOrder order) {
//		SecUser user = this.secUserService.findUserByPartyId(order.getPartyId());
//		user.getRoles();
//		boolean guest = false;
//		for (Role role : user.getRoles()) {
//			if (Constants.SECURITY_ROLE_GUEST.equals(role.getRoleName())) {
//				guest = true;
//			}
//		}
//		if (guest) {
//			return;
//		}
//
//		Party party = partyService.cachePartyBy(order.getPartyId(), true);
//		UserData userData = new UserData();
//		userData.setRolename(party.getRolename());
//		userData.setCreateTime(new Date());
//		userData.setPartyId(order.getPartyId());
//		
//		userData.setExchange_lever_amount(order.getDeposit_open());
//		userData.setExchange_lever_fee(order.getFee());
//		userData.setExchange_lever_order_income(Arith.sub(order.getAmount_close(), order.getDeposit_open()));
//		save(userData);
//	}

    /**
     * 根据日期获取当日充值数
     *
     * @param date 字符串日期，"2020-01-01"
     * @return
     */
    public int filterRechargeByDate(String date) {
        int sum = 0;
        List<Map<String, UserData>> list = new ArrayList<>(cache.values());
        for (Map<String, UserData> ud : list) {
            UserData userData = ud.get(date);
            if (userData != null && userData.getRecharge() > 0) {//当日有充值
                sum++;
            }
        }
        return sum;
    }

    /**
     * 转账
     *
     * @param byPartyId       转出用户id
     * @param toPartyId       转入用户id
     * @param outAmountToUsdt 转出金额（USDT计价）
     * @param inAmountToUsdt  转入金额（USDT计价）
     */
    public void saveTransferMoneyHandle(String byPartyId, String toPartyId, double outAmountToUsdt, double inAmountToUsdt) {
        saveTransferOutMoneyHandle(byPartyId, outAmountToUsdt);
        saveTransferInMoneyHandle(toPartyId, inAmountToUsdt);
    }

    /**
     * 转入金额
     *
     * @param partyId
     * @param amountToUsdt
     */
    public void saveTransferInMoneyHandle(String partyId, double amountToUsdt) {
        SecUser user = this.secUserService.findUserByPartyId(partyId);
        user.getRoles();
        boolean guest = false;
        for (Role role : user.getRoles()) {
            if (Constants.SECURITY_ROLE_GUEST.equals(role.getRoleName())) {
                guest = true;
            }
        }
        if (guest) {
            return;
        }
        Party party = partyService.cachePartyBy(partyId, false);
        UserData userData = new UserData();
        userData.setRolename(party.getRolename());
        userData.setCreateTime(new Date());
        userData.setPartyId(party.getId().toString());
        userData.setTransfer_in_money(amountToUsdt);
        save(userData);
    }

    /**
     * 转出金额
     *
     * @param partyId
     * @param amountToUsdt
     */
    public void saveTransferOutMoneyHandle(String partyId, double amountToUsdt) {
        SecUser user = this.secUserService.findUserByPartyId(partyId);
        user.getRoles();
        boolean guest = false;
        for (Role role : user.getRoles()) {
            if (Constants.SECURITY_ROLE_GUEST.equals(role.getRoleName())) {
                guest = true;
            }
        }
        if (guest) {
            return;
        }
        Party party = partyService.cachePartyBy(partyId, false);
        UserData userData = new UserData();
        userData.setRolename(party.getRolename());
        userData.setCreateTime(new Date());
        userData.setPartyId(partyId);
        userData.setTransfer_out_money(amountToUsdt);
        save(userData);
    }

    public void setPagedQueryDao(PagedQueryDao pagedQueryDao) {
        this.pagedQueryDao = pagedQueryDao;
    }

    public void setPartyService(PartyService partyService) {
        this.partyService = partyService;
    }

    public void setDataService(DataService dataService) {
        this.dataService = dataService;
    }


    @Override
    public void saveBuy(ExchangeApplyOrder order) {
        SecUser user = this.secUserService.findUserByPartyId(order.getPartyId());
        user.getRoles();
        boolean guest = false;
        for (Role role : user.getRoles()) {
            if (Constants.SECURITY_ROLE_GUEST.equals(role.getRoleName()) || Constants.SECURITY_ROLE_TEST.equals(role.getRoleName())) {
                guest = true;
            }
        }
        if (guest) {
            return;
        }

//		List<Realtime> realtime_list = this.dataService.realtime(order.getSymbol());
//		Realtime realtime = null;
//		if (realtime_list.size() > 0) {
//			realtime = realtime_list.get(0);
//		} else {
//			throw new BusinessException("系统错误，请稍后重试");
//		}

        Party party = partyService.cachePartyBy(order.getPartyId(), false);
        UserData userData = new UserData();
        userData.setRolename(party.getRolename());
        userData.setCreateTime(new Date());
        userData.setPartyId(order.getPartyId());
        userData.setExchange_amount(order.getVolume());
        userData.setExchange_fee(0);
        save(userData);

    }

    public void saveSell(ExchangeApplyOrder order) {
        SecUser user = this.secUserService.findUserByPartyId(order.getPartyId());
        user.getRoles();
        boolean guest = false;
        for (Role role : user.getRoles()) {
            if (Constants.SECURITY_ROLE_GUEST.equals(role.getRoleName()) || Constants.SECURITY_ROLE_TEST.equals(role.getRoleName())) {
                guest = true;
            }
        }
        if (guest) {
            return;
        }

        List<Realtime> realtime_list = this.dataService.realtime(order.getSymbol());
        Realtime realtime = null;
        if (realtime_list.size() > 0) {
            realtime = realtime_list.get(0);
        } else {
            throw new BusinessException("系统错误，请稍后重试");
        }

        Party party = partyService.cachePartyBy(order.getPartyId(), false);
        UserData userData = new UserData();
        userData.setRolename(party.getRolename());
        userData.setCreateTime(new Date());
        userData.setPartyId(order.getPartyId());
        userData.setExchange_amount(Arith.mul(realtime.getClose(), order.getVolume()));
        userData.setExchange_fee(Arith.mul(realtime.getClose(), order.getFee()));
        userData.setExchange_income(0);
        save(userData);

//		UserRecom userRecom = this.userRecomService.findByPartyId(order.getPartyId());
//		if (userRecom == null) {
//			return;
//		}
//		List<UserRecom> parents = this.userRecomService.getParents(userRecom.getPartyId());
//
//		for (int i = 0; i < parents.size(); i++) {
//			Party party_parent = partyService.cachePartyBy(parents.get(i).getReco_id());
//
//			if (Constants.SECURITY_ROLE_AGENT.equals(party_parent.getRolename())) {
//				UserData userData_reco = new UserData();
//				userData_reco.setRolename(party_parent.getRolename());
//				userData_reco.setCreateTime(new Date());
//				userData_reco.setPartyId(parents.get(i).getReco_id());
//				userData_reco.setExchange_amount(Arith.mul(realtime.getClose(), order.getVolume()));
//				userData_reco.setExchange_fee(Arith.mul(realtime.getClose(), order.getFee()));
//				userData_reco.setExchange_income(exchange_income);
//				save(userData_reco);
//			}
//		}
    }

    /**
     * 理财产品平仓
     */
    public void saveSellFinance(FinanceOrder order) {
        SecUser user = this.secUserService.findUserByPartyId(order.getPartyId());
        user.getRoles();
        boolean guest = false;
        for (Role role : user.getRoles()) {
            if (Constants.SECURITY_ROLE_GUEST.equals(role.getRoleName()) || Constants.SECURITY_ROLE_TEST.equals(role.getRoleName())) {
                guest = true;
            }
        }
        if (guest) {
            return;
        }

        Party party = partyService.cachePartyBy(order.getPartyId(), false);
        UserData userData = new UserData();
        userData.setRolename(party.getRolename());
        userData.setCreateTime(new Date());
        userData.setPartyId(order.getPartyId());
        userData.setFinance_amount(order.getAmount());
        userData.setFinance_income(order.getProfit());
        save(userData);

//		UserRecom userRecom = this.userRecomService.findByPartyId(order.getPartyId());
//		if (userRecom == null) {
//			return;
//		}
//		List<UserRecom> parents = this.userRecomService.getParents(userRecom.getPartyId());
//
//		for (int i = 0; i < parents.size(); i++) {
//			Party party_parent = partyService.cachePartyBy(parents.get(i).getReco_id());
//
//			if (Constants.SECURITY_ROLE_AGENT.equals(party_parent.getRolename())) {
//				UserData userData_reco = new UserData();
//				userData_reco.setRolename(party_parent.getRolename());
//				userData_reco.setCreateTime(new Date());
//				userData_reco.setPartyId(parents.get(i).getReco_id());
//				userData_reco.setFinance_amount(order.getAmount());
//				userData_reco.setFinance_income(order.getProfit());
//				save(userData_reco);
//			}
//		}
    }


    /**
     * 交割合约平仓
     */
    @Override
    public void saveFuturesClose(FuturesOrder order) {
        SecUser user = this.secUserService.findUserByPartyId(order.getPartyId());
        user.getRoles();
        boolean guest = false;
        for (Role role : user.getRoles()) {
            if (Constants.SECURITY_ROLE_GUEST.equals(role.getRoleName()) || Constants.SECURITY_ROLE_TEST.equals(role.getRoleName())) {
                guest = true;
            }
        }
        if (guest) {
            return;
        }

        Party party = partyService.cachePartyBy(order.getPartyId(), false);
        UserData userData = new UserData();
        userData.setRolename(party.getRolename());
        userData.setCreateTime(new Date());
        userData.setPartyId(order.getPartyId());
        userData.setFurtures_amount(order.getVolume());
        userData.setFurtures_fee(order.getFee());
        userData.setFurtures_income(order.getProfit());
        save(userData);

//		UserRecom userRecom = this.userRecomService.findByPartyId(order.getPartyId());
//		if (userRecom == null) {
//			return;
//		}
//		List<UserRecom> parents = this.userRecomService.getParents(userRecom.getPartyId());
//
//		for (int i = 0; i < parents.size(); i++) {
//			Party party_parent = partyService.cachePartyBy(parents.get(i).getReco_id());
//
//			if (Constants.SECURITY_ROLE_AGENT.equals(party_parent.getRolename())) {
//				UserData userData_reco = new UserData();
//				userData_reco.setRolename(party_parent.getRolename());
//				userData_reco.setCreateTime(new Date());
//				userData_reco.setPartyId(parents.get(i).getReco_id());
//				userData_reco.setFurtures_amount(order.getVolume());
//				userData_reco.setFurtures_fee(order.getFee());
//				userData_reco.setFurtures_income(order.getProfit());
//				save(userData_reco);
//			}
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    public void setSysparaService(SysparaService sysparaService) {
        this.sysparaService = sysparaService;
    }
}
