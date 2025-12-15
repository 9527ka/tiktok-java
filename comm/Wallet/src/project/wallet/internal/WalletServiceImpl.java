package project.wallet.internal;

import cn.hutool.core.collection.CollectionUtil;
import kernel.exception.BusinessException;
import kernel.util.Arith;
import kernel.util.StringUtils;
import kernel.util.ThreadUtils;
import kernel.util.UUIDGenerator;
import org.apache.commons.collections.CollectionUtils;
import org.checkerframework.checker.units.qual.A;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Property;
import org.hibernate.transform.Transformers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;
import project.Constants;
import project.data.DataService;
import project.data.model.Realtime;
import project.log.MoneyLog;
import project.log.MoneyLogService;
import project.party.PartyService;
import project.party.model.Party;
import project.redis.RedisHandler;
import project.syspara.SysparaService;
import project.user.UserDataService;
import project.wallet.Wallet;
import project.wallet.WalletExtend;
import project.wallet.WalletRedisKeys;
import project.wallet.WalletService;
import project.wallet.consumer.WalletExtendMessage;
import project.wallet.consumer.WalletMessage;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

public class WalletServiceImpl extends HibernateDaoSupport implements WalletService {
    private Logger log = LoggerFactory.getLogger(WalletServiceImpl.class);
    private PartyService partyService;
    private MoneyLogService moneyLogService;
    private RedisHandler redisHandler;
    private DataService dataService;
    private SysparaService sysparaService;
    private UserDataService userDataService;
    private JdbcTemplate jdbcTemplate;

    @Override
    public Wallet selectOne(String partyId) {
//        String sql = "SELECT * FROM T_WALLET WHERE PARTY_ID = :partyId";
//        MapSqlParameterSource parameter = new MapSqlParameterSource();
//        parameter.addValue("partyId", partyId);
//        NamedParameterJdbcTemplate parameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
//        List<Wallet> wallets = parameterJdbcTemplate.query(sql, parameter, new RowMapper<Wallet>() {
//            @Override
//            public Wallet mapRow(ResultSet rs, int rowNum) throws SQLException {
//                Wallet wallet = new Wallet();
//                wallet.setId(rs.getString("UUID"));
//                wallet.setPartyId(rs.getString("PARTY_ID"));
//                wallet.setRebate(rs.getDouble("REBATE"));
//                wallet.setMoney(rs.getDouble("MONEY"));
//                wallet.setEntityVersion(0);
//                return wallet;
//            }
//        });
        DetachedCriteria criteria = DetachedCriteria.forClass(Wallet.class);
        criteria.add(Property.forName("partyId").eq(partyId));
        try {
            List<Wallet> wallets = (List<Wallet>) getHibernateTemplate().findByCriteria(criteria);
            if (CollectionUtils.isEmpty(wallets)) {
                return null;
            }
            if (wallets.size() > 1) {
                log.error("用户出现非法钱包");
            }
            if (CollectionUtil.isNotEmpty(wallets)) {
                this.redisHandler.setSync(WalletRedisKeys.WALLET_PARTY_ID + partyId, wallets.get(0));
                return wallets.get(0);
            }
            return null;
        } catch (EmptyResultDataAccessException e) {
            return null;
        }

    }

    @Override
    public Wallet saveWalletByPartyId(String partyId) {
        Wallet wallet = null;
        try {//这里大量测试时会偶现异常，这里改为捕获异常，让程序继续执行，并重新获取一次钱包
            wallet = selectOne(partyId.toString());
        } catch (Exception e) {
            log.error("钱包获取失败，并重试", e);
        }
        if (wallet != null) {
            return wallet;
        } else {
            wallet = new Wallet();
            wallet.setPartyId(partyId);
            save(wallet);
            return wallet;
        }
    }

    @Override
    public void save(Wallet entity) {
        Wallet existWallet = findWalletByPartyId(entity);
        if (existWallet == null) {
            entity.setTimestamp(new Date());
            try {
                getHibernateTemplate().save(entity);
//                getHibernateTemplate().flush();
            } catch (Exception e) {
                // 防止并发创建
                // 并发场景下会出现主键冲突，如果不执行 clear 方法，以下的 get 获取的将不是数据库里的数据，而是前面 save 到 hibernate 缓存里的数据
//                getHibernateTemplate().clear();
                existWallet = findWalletByPartyId(entity);
                if (existWallet == null) {
                    throw e;
                }
            }
        } else {
            BeanUtils.copyProperties(existWallet, entity);
//            entity = existWallet;
        }

        redisHandler.setSync(WalletRedisKeys.WALLET_PARTY_ID + entity.getPartyId().toString(), entity);
    }

    @Override
    public void update(Wallet entity) {
        Wallet existWallet = findWalletByPartyId(entity);
        if (existWallet == null) {
            entity.setTimestamp(new Date());
            try {
                getHibernateTemplate().save(entity);
//                getHibernateTemplate().flush();
            } catch (Exception e) {
                // 防止并发创建
                // 并发场景下会出现主键冲突，如果不执行 clear 方法，以下的 get 获取的将不是数据库里的数据，而是前面 save 到 hibernate 缓存里的数据
//                getHibernateTemplate().clear();
                existWallet = findWalletByPartyId(entity);
                if (existWallet == null) {
                    throw e;
                }
            }
        } else {
            getHibernateTemplate().merge(entity);
        }

        redisHandler.setSync(WalletRedisKeys.WALLET_PARTY_ID + entity.getPartyId().toString(), entity);
    }

    private Wallet findWalletByPartyId(Wallet entity) {
        DetachedCriteria criteria = DetachedCriteria.forClass(Wallet.class);
        criteria.add(Property.forName("partyId").eq(entity.getPartyId().toString()));
        try {
            List<Wallet> wallets = (List<Wallet>) getHibernateTemplate().findByCriteria(criteria);
            if (CollectionUtils.isEmpty(wallets)) {
                return null;
            }
            if (wallets.size() > 1) {
                log.error("用户出现非法钱包");
            }
            return wallets.get(0);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public void update(String partyId, double amount) {
        Wallet wallet = saveWalletByPartyId(partyId);
        if (wallet.getFrozenState()==0) {
            wallet.setMoney(Arith.roundDown(Arith.add(wallet.getMoney(), amount),2));
        }else {
            wallet.setMoneyAfterFrozen(Arith.roundDown(Arith.add(wallet.getMoneyAfterFrozen(), amount),2));
        }

        // 此处不要执行持久化处理，因为 WalletConsumeServer 方法会以异步方式从 redis 中读取 WALLET_QUEUE_UPDATE 资金变更队列中的
        // 数据来单线程模式执行每笔变更记录，刷新钱包余额。
        //getHibernateTemplate().save(wallet);
        //getHibernateTemplate().flush();
        log.info("----> WalletServiceImpl.update 更新用户:{} 的钱包余额为:{}", partyId, wallet.getMoney());
        getHibernateTemplate().merge(wallet);
        redisHandler.setSync(WalletRedisKeys.WALLET_PARTY_ID + wallet.getPartyId().toString(), wallet);
//        redisHandler.pushAsyn(WalletRedisKeys.WALLET_QUEUE_UPDATE, new WalletMessage(partyId, amount));
    }

    @Override
    public void updateMoeny(String partyId, double amount) {
        Wallet wallet = saveWalletByPartyId(partyId);
        wallet.setMoney(Arith.roundDown(Arith.add(wallet.getMoney(), amount),2));

        // 此处不要执行持久化处理，因为 WalletConsumeServer 方法会以异步方式从 redis 中读取 WALLET_QUEUE_UPDATE 资金变更队列中的
        // 数据来单线程模式执行每笔变更记录，刷新钱包余额。
        //getHibernateTemplate().save(wallet);
        //getHibernateTemplate().flush();
        log.info("----> WalletServiceImpl.update 更新用户:{} 的钱包余额为:{}", partyId, wallet.getMoney());
        getHibernateTemplate().merge(wallet);
        redisHandler.setSync(WalletRedisKeys.WALLET_PARTY_ID + wallet.getPartyId().toString(), wallet);
//        redisHandler.pushAsyn(WalletRedisKeys.WALLET_QUEUE_UPDATE, new WalletMessage(partyId, amount));
    }

    @Override
    public void update(String partyId, double amount, double rebate) {
        Wallet wallet = saveWalletByPartyId(partyId);
        wallet.setMoney(Arith.roundDown(Arith.add(wallet.getMoney(), amount),2));
        wallet.setRebate(Arith.add(wallet.getRebate(), rebate));
        getHibernateTemplate().merge(wallet);
        redisHandler.setSync(WalletRedisKeys.WALLET_PARTY_ID + wallet.getPartyId().toString(), wallet);
//        WalletMessage updateMsg = new WalletMessage(partyId, amount);
//        updateMsg.setRebate(rebate);
//        redisHandler.pushAsyn(WalletRedisKeys.WALLET_QUEUE_UPDATE, updateMsg);
    }

    @Override
    public void update(String partyId, double amount, double rebate, double rechargeCommission) {
        Wallet wallet = saveWalletByPartyId(partyId);

        if(wallet.getFrozenState() == 1){
            wallet.setMoneyAfterFrozen(Arith.roundDown(Arith.add(wallet.getMoneyAfterFrozen(), amount),2));
        } else if (wallet.getFrozenState() == 0){
            wallet.setMoney(Arith.roundDown(Arith.add(wallet.getMoney(), amount),2));
        }

        wallet.setRebate(Arith.add(wallet.getRebate(), rebate));
        wallet.setRechargeCommission(Arith.add(wallet.getRechargeCommission(), rechargeCommission));
        getHibernateTemplate().merge(wallet);
        redisHandler.setSync(WalletRedisKeys.WALLET_PARTY_ID + wallet.getPartyId().toString(), wallet);
//        WalletMessage updateMsg = new WalletMessage(partyId, amount);
//        updateMsg.setRebate(rebate);
//        updateMsg.setRechargeCommission(rechargeCommission);
//        redisHandler.pushAsyn(WalletRedisKeys.WALLET_QUEUE_UPDATE, updateMsg);
    }


    public double selectTotalIncome(String partyId) {
        String sql = "SELECT SUM(REBATE) FROM T_MALL_REBATE WHERE PARTY_ID = '" + partyId + "'";
        Double value = this.jdbcTemplate.queryForObject(sql, Double.class);
        return null == value ? 0.0D : value;
    }

    @Override
    public WalletExtend saveExtendByPara(String partyId, String wallettype) {
        if (StringUtils.isEmptyString(wallettype) || partyId == null || StringUtils.isEmptyString(partyId.toString())) {
            log.error("saveExtendByPara fail,partyId:{},wallettype:{}", new Object[]{partyId, wallettype});
            throw new RuntimeException("更新钱包失败，请联系客服");
        }
        WalletExtend walletExtend = (WalletExtend) redisHandler
                .get(WalletRedisKeys.WALLET_EXTEND_PARTY_ID + partyId.toString() + wallettype);
        if (walletExtend != null) {
            return walletExtend;
        }
        walletExtend = new WalletExtend();
        walletExtend.setPartyId(partyId);
        walletExtend.setWallettype(wallettype);
        save(walletExtend);

        ThreadUtils.sleep(10);

        return walletExtend;
    }

    @Override
    public void save(WalletExtend entity) {
        entity.setId(UUIDGenerator.getUUID());
        final String sql = "INSERT INTO T_WALLET_EXTEND(UUID,PARTY_ID,WALLETTYPE,AMOUNT) VALUES('" + entity.getId().toString() + "','" + entity.getPartyId().toString() + "','" + entity.getWallettype() + "','0')";
        FutureTask<Object> future = new FutureTask<Object>(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                // TODO Auto-generated method stub
                int update = jdbcTemplate.update(sql);
                return update;
            }
        });
        Thread thread = new Thread(future);
        thread.start();
        try {
            future.get();
        } catch (Exception e) {
            // TODO: handle exception
            throw new BusinessException("create WalletExtend fail,partyId:{" + entity.getPartyId().toString() + "},symbol:{" + entity.getWallettype() + "}");
        }


        // getHibernateTemplate().save(entity);

        redisHandler.setSync(
                WalletRedisKeys.WALLET_EXTEND_PARTY_ID + entity.getPartyId().toString() + entity.getWallettype(),
                entity);
    }

    @Override
    public void updateExtend(String partyId, String walletType, double amount) {
        WalletExtend walletExtend = (WalletExtend) redisHandler
                .get(WalletRedisKeys.WALLET_EXTEND_PARTY_ID + partyId.toString() + walletType);

        if (walletExtend == null) {
            walletExtend = this.saveExtendByPara(partyId, walletType);
        }

        // TODO 待验证
        getHibernateTemplate().evict(walletExtend);

        walletExtend.setAmount(Arith.add(walletExtend.getAmount(), amount));

        redisHandler.setSync(WalletRedisKeys.WALLET_EXTEND_PARTY_ID + partyId.toString() + walletType, walletExtend);

        redisHandler.pushAsyn(WalletRedisKeys.WALLET_EXTEND_QUEUE_UPDATE,
                new WalletExtendMessage(partyId, walletType, amount));
    }

    @Override
    public List<WalletExtend> findExtend(String partyId) {
//		List<String> list = (List<String>) this.getHibernateTemplate().find("SELECT wallettype FROM WalletExtend WHERE partyId=?0");
        List<String> list = (List<String>) this.getHibernateTemplate()
                .find("SELECT wallettype FROM WalletExtend WHERE partyId=?0", partyId);
        List<String> keys = new LinkedList<String>();
        for (String key : list) {
            keys.add(WalletRedisKeys.WALLET_EXTEND_PARTY_ID + partyId.toString() + key);
        }
        Object[] objects = redisHandler.getList(keys.toArray(new String[0]));
        if (objects != null && objects.length > 0) {
            List<WalletExtend> result = new ArrayList<WalletExtend>();
            for (Object obj : objects) {
                result.add((WalletExtend) obj);
            }
            return result;
        }
        return new ArrayList<WalletExtend>();
    }

    @Override
    public List<WalletExtend> findExtend(String partyId, List<String> list_symbol) {

        List<String> keys = new LinkedList<String>();
        for (String key : list_symbol) {
            keys.add(WalletRedisKeys.WALLET_EXTEND_PARTY_ID + partyId.toString() + key);
        }
        Object[] objects = redisHandler.getList(keys.toArray(new String[0]));
        if (objects != null && objects.length > 0) {
            List<WalletExtend> result = new ArrayList<WalletExtend>();
            for (Object obj : objects) {
                if (obj != null)
                    result.add((WalletExtend) obj);
            }
            return result;
        }
        return new ArrayList<WalletExtend>();
    }

//	public Wallet findWalletByPartyId(Serializable partyId) {
////		return this.saveWalletByPartyId(partyId);
//		return (Wallet) redisHandler.get(WalletRedisKeys.WALLET_PARTY_ID + partyId.toString());
//	}

//	public WalletExtend findExtendByPara(Serializable partyId, String wallettype) {// 用户转账
//		return this.saveExtendByPara(partyId, wallettype);
//	}

    public void updateTransfer_wallet(String byPartyId, String safeword, String toPartyId, String coin, double amount,
                                      double fee_amount) {

        if ("false".equals(this.sysparaService.find("transfer_wallet_open").getValue())) {
            throw new BusinessException(1, "无权限");
        }

        /**
         * 实际到账
         */

        double get_amount = Arith.sub(amount, fee_amount);
        /**
         * 币种改小写
         */
        coin = coin.toLowerCase();

        /**
         * 转账方
         */
        Party byParty = this.partyService.cachePartyBy(byPartyId, false);
        String giftMoneyLog = "";
        if (byParty.getGift_money_flag()) {
            giftMoneyLog = "赠送金额";
        }
        /**
         * 正式用户才有转账权限
         */
        if (!Constants.SECURITY_ROLE_MEMBER.equals(byParty.getRolename())) {
            throw new BusinessException(1, "无权限");
        }
        if (!byParty.getEnabled()) {
            throw new BusinessException(506, "无权限");
        }

        /**
         * 收款方
         */
        Party toParty = this.partyService.cachePartyBy(toPartyId, false);
        if (toParty == null || toParty.getId().toString().equals(byParty.getId().toString())) {
            throw new BusinessException(1, "收款方输入错误");
        }
        /*
         * 转出金额，usdt计价
         */
        double outAmountToUsdt = amount;
        /*
         * 转入金额，usdt计价
         */
        double inAmountToUsdt = get_amount;
        if ("usdt".equals(coin) || "USDT".equals(coin)) {
            /**
             * 转账方
             */
            Wallet walletBy = saveWalletByPartyId(byPartyId);

            if (walletBy.getMoney() < amount) {
                throw new BusinessException(1, "余额不足");
            }

            double walletBy_before_amount = walletBy.getMoney();
//			walletBy.setMoney(Arith.sub(walletBy.getMoney(), amount));

            /*
             * 保存资金日志
             */
            MoneyLog moneyLog = new MoneyLog();
            moneyLog.setCategory(Constants.MONEYLOG_CATEGORY_COIN);
            moneyLog.setAmount_before(walletBy_before_amount);
            moneyLog.setAmount(Arith.sub(0, amount));
            moneyLog.setAmount_after(Arith.sub(walletBy.getMoney(), amount));

            moneyLog.setLog("用户手动转账" + giftMoneyLog + "给" + toParty.getUsername());
            moneyLog.setPartyId(byPartyId);
            moneyLog.setWallettype(Constants.WALLET);
            moneyLog.setContent_type(Constants.MONEYLOG_CONTENT_WITHDRAW);
            moneyLogService.save(moneyLog);

            update(walletBy.getPartyId().toString(), Arith.sub(0, amount));

            /**
             * 收款方 获得金额
             */

            Wallet walletTo = saveWalletByPartyId(toPartyId);

            double walletTo_before_amount = walletTo.getMoney();
//			walletTo.setMoney(Arith.add(walletTo.getMoney(), get_amount));

            /*
             * 保存资金日志
             */
            MoneyLog moneyLogto = new MoneyLog();
            moneyLogto.setCategory(Constants.MONEYLOG_CATEGORY_COIN);
            moneyLogto.setAmount_before(walletTo_before_amount);
            moneyLogto.setAmount(get_amount);
            moneyLogto.setAmount_after(Arith.add(walletTo.getMoney(), get_amount));

            moneyLogto.setLog("收到" + byParty.getUsername() + giftMoneyLog + "的转账");
            moneyLogto.setPartyId(toPartyId);
            moneyLogto.setWallettype(Constants.WALLET);
            moneyLogto.setContent_type(Constants.MONEYLOG_CONTENT_RECHARGE);
            moneyLogService.save(moneyLogto);

//			update(walletTo);
            update(walletTo.getPartyId().toString(), get_amount);

            /**
             * 充值到账后给他增加提现流水限制金额
             */
            toParty.setWithdraw_limit_amount(Arith.add(toParty.getWithdraw_limit_amount(), get_amount));
            partyService.update(toParty);
        } else {

            /**
             * 转账方
             */
            WalletExtend walletBy = saveExtendByPara(byPartyId, coin);

            if (walletBy.getAmount() < amount) {
                throw new BusinessException(1, "余额不足");
            }

            double walletBy_before_amount = walletBy.getAmount();
//			walletBy.setAmount(Arith.sub(walletBy.getAmount(), amount));

            /*
             * 保存资金日志
             */
            MoneyLog moneyLog = new MoneyLog();
            moneyLog.setCategory(Constants.MONEYLOG_CATEGORY_COIN);
            moneyLog.setAmount_before(walletBy_before_amount);
            moneyLog.setAmount(Arith.sub(0, amount));
            moneyLog.setAmount_after(Arith.sub(walletBy.getAmount(), amount));

            moneyLog.setLog("用户手动转账" + giftMoneyLog + "给" + toParty.getUsername());
            // moneyLog.setExtra(withdraw.getOrder_no());
            moneyLog.setPartyId(byPartyId);
            moneyLog.setWallettype(coin);
            moneyLog.setContent_type(Constants.MONEYLOG_CONTENT_WITHDRAW);
            moneyLogService.save(moneyLog);

//			update(walletBy);
            updateExtend(walletBy.getPartyId().toString(), coin, Arith.sub(0, amount));
            /**
             * 收款方 获得金额
             */

            WalletExtend walletTo = saveExtendByPara(toPartyId, coin);

            double walletTo_before_amount = walletTo.getAmount();
//			walletTo.setAmount(Arith.add(walletTo.getAmount(), get_amount));

            /*
             * 保存资金日志
             */
            MoneyLog moneyLogto = new MoneyLog();
            moneyLogto.setCategory(Constants.MONEYLOG_CATEGORY_COIN);
            moneyLogto.setAmount_before(walletTo_before_amount);
            moneyLogto.setAmount(get_amount);
            moneyLogto.setAmount_after(Arith.add(walletTo.getAmount(), get_amount));

            moneyLogto.setLog("收到" + byParty.getUsername() + giftMoneyLog + "的转账");
            // moneyLog.setExtra(withdraw.getOrder_no());
            moneyLogto.setPartyId(toPartyId);
            moneyLogto.setWallettype(coin);
            moneyLogto.setContent_type(Constants.MONEYLOG_CONTENT_RECHARGE);
            moneyLogService.save(moneyLogto);

            List<Realtime> realtime_list = this.dataService.realtime(coin);
            Realtime realtime = null;
            if (realtime_list.size() > 0) {
                realtime = realtime_list.get(0);
            } else {
                throw new BusinessException("系统错误，请稍后重试");
            }
            outAmountToUsdt = Arith.mul(amount, realtime.getClose());
            inAmountToUsdt = Arith.mul(get_amount, realtime.getClose());
            /**
             * 充值到账后给他增加提现流水限制金额
             */
            toParty.setWithdraw_limit_amount(
                    Arith.add(toParty.getWithdraw_limit_amount(), Arith.mul(get_amount, realtime.getClose())));
            partyService.update(toParty);

            updateExtend(walletTo.getPartyId().toString(), coin, get_amount);

        }
        userDataService.saveTransferMoneyHandle(byPartyId, toPartyId, outAmountToUsdt, inAmountToUsdt);
    }

    @Override
    public List<WalletExtend> findAllWalletExtend() {
        return (List<WalletExtend>) this.getHibernateTemplate().find(" FROM WalletExtend ");
    }

    @Override
    public List<Wallet> findAllWallet() {
        @SuppressWarnings("unchecked")
        List<Wallet> list = getHibernateTemplate().getSessionFactory().getCurrentSession().createSQLQuery(
                "SELECT w.MONEY money  FROM T_WALLET w LEFT JOIN PAT_PARTY p ON w.PARTY_ID = p.UUID WHERE p.ROLENAME = 'MEMBER'")
                .addScalar("money")
                .setResultTransformer(Transformers.aliasToBean(Wallet.class)).list();
        return list;
    }

    @Override
    public WalletExtend getInvestPoint(String partyId) {
        return this.getHibernateTemplate().get(WalletExtend.class, partyId);
    }


    @Override
    public void updateInvestPoint(String partyId, int addPoint) {
        WalletExtend walletExtend = getInvestPoint(partyId);
        if (walletExtend == null) {
            walletExtend = new WalletExtend();
            walletExtend.setId(partyId);
            walletExtend.setPartyId(partyId);
            walletExtend.setAmount(addPoint);
            walletExtend.setWallettype("POINT");
            this.getHibernateTemplate().save(walletExtend);
            return;
        }
        Double amount = walletExtend.getAmount();
        walletExtend.setAmount(amount.intValue() + addPoint);
        this.getHibernateTemplate().update(walletExtend);
    }

    @Override
    public Double getInvestPointBuyPartyId(String partyId) {
        WalletExtend walletExtend = getInvestPoint(partyId);
        if (walletExtend == null) {
            return 0D;
        }
        return walletExtend.getAmount();
    }

    public void setPartyService(PartyService partyService) {
        this.partyService = partyService;
    }

    public void setMoneyLogService(MoneyLogService moneyLogService) {
        this.moneyLogService = moneyLogService;
    }

    public void setRedisHandler(RedisHandler redisHandler) {
        this.redisHandler = redisHandler;
    }

    public void setDataService(DataService dataService) {
        this.dataService = dataService;
    }

    public void setSysparaService(SysparaService sysparaService) {
        this.sysparaService = sysparaService;
    }

    public void setUserDataService(UserDataService userDataService) {
        this.userDataService = userDataService;
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

}
