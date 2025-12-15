package project.mall.combo.impl;

import kernel.exception.BusinessException;
import kernel.util.Arith;
import kernel.util.DateUtils;
import kernel.util.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;
import project.Constants;
import project.log.MoneyLog;
import project.log.MoneyLogService;
import project.mall.MallRedisKeys;
import project.mall.combo.ComboService;
import project.mall.combo.model.Combo;
import project.mall.combo.model.ComboRecord;
import project.mall.combo.model.ComboUser;
import project.mall.utils.MallPageInfo;
import project.mall.utils.MallPageInfoUtil;
import project.redis.RedisHandler;
import project.wallet.Wallet;
import project.wallet.WalletService;

import java.util.Date;
import java.util.List;

public class ComboServiceImpl extends HibernateDaoSupport implements ComboService {

    private WalletService walletService;

    private MoneyLogService moneyLogService;

    private RedisHandler redisHandler;


    @Override
    public ComboUser findComboUserByPartyId(String partyId) {
        ComboUser address = this.getHibernateTemplate().get(ComboUser.class, partyId);
        return address;
    }

    @Override
    public Combo findComboByPartyId(String partyId) {
        ComboUser comboUserByPartyId = findComboUserByPartyId(partyId);

        if(comboUserByPartyId == null){
            return null;
        }

        if(comboUserByPartyId.getStopTime() <= System.currentTimeMillis()) {
            return  null;
        }

        String comboId = comboUserByPartyId.getComboId();
        return this.getHibernateTemplate().get(Combo.class, comboId);

    }

    @Override
    public void updateComboUser(ComboUser comboUser) {
        getHibernateTemplate().update(comboUser);
    }

    @Override
    public List<Combo> listCombo() {
        DetachedCriteria query = DetachedCriteria.forClass(Combo.class);
        return (List<Combo>) getHibernateTemplate().findByCriteria(query, 0, 10);
    }

    @Override
    public void updateBuy(String partyId, String id, String name) {
        String lockKey = MallRedisKeys.MALL_ORDER_USER_LOCK + partyId;
        if (!redisHandler.lock(lockKey, 20)) {
            throw new BusinessException("操作失败，请稍后再试");
        }
        try {

            Combo combo = this.getHibernateTemplate().get(Combo.class, id);
            if (combo == null) {
                throw new BusinessException("订单不存在");
            }

            double prize = combo.getAmount();

            Wallet wallet = walletService.saveWalletByPartyId(partyId);


            double amount_before = wallet.getFrozenState()==0?wallet.getMoney():wallet.getMoneyAfterFrozen();
            MoneyLog moneyLog = new MoneyLog();
            if (amount_before < prize) {
                throw new BusinessException("余额不足");
            }
            // 不够原子，TODO
            if (wallet.getFrozenState() == 0) {
                wallet.setMoney(Arith.roundDown(Arith.sub(wallet.getMoney(), prize),2));
                moneyLog.setAmount_after(wallet.getMoney());
                moneyLog.setFreeze(0);
            }else {//处于冻结状态时
                wallet.setMoneyAfterFrozen(Arith.roundDown(Arith.sub(wallet.getMoneyAfterFrozen(), prize),2));
                moneyLog.setAmount_after(wallet.getMoneyAfterFrozen());
                moneyLog.setFreeze(1);
            }
            walletService.update(wallet);

            moneyLog.setCategory(Constants.MONEYLOG_CATEGORY_COIN);
            moneyLog.setAmount_before(amount_before);
            moneyLog.setAmount(Arith.sub(0, prize));
            moneyLog.setAmount_after(wallet.getMoney());

            moneyLog.setLog("直通车购买[" + combo.getDay() + "天]");
            moneyLog.setPartyId(partyId);
            moneyLog.setWallettype(Constants.WALLET);
            moneyLog.setContent_type(Constants.MONEYLOG_CONTNET_COMBO_ORDER);

            moneyLogService.save(moneyLog);

            ComboUser comboUser = findComboUserByPartyId(partyId);
            long nw = System.currentTimeMillis();
            if (comboUser == null) {
                comboUser = new ComboUser();
                comboUser.setId(partyId);
                comboUser.setPromoteNum(combo.getPromoteNum());
                comboUser.setComboId(combo.getId());
                comboUser.setStopTime(DateUtils.addDate(new Date(nw),combo.getDay()).getTime());
                this.getHibernateTemplate().save(comboUser);
            } else if (nw >= comboUser.getStopTime()) {//产品说设计上的漏洞
                comboUser.setPromoteNum(combo.getPromoteNum());
                comboUser.setComboId(combo.getId().toString());
                comboUser.setStopTime(DateUtils.addDate(new Date(nw), combo.getDay()).getTime());
                this.getHibernateTemplate().update(comboUser);
            } else {
                comboUser.setPromoteNum(combo.getPromoteNum() + comboUser.getPromoteNum());
                comboUser.setComboId(combo.getId().toString());
                comboUser.setStopTime(DateUtils.addDate(new Date(comboUser.getStopTime()), combo.getDay()).getTime());
                this.getHibernateTemplate().update(comboUser);
            }

            ComboRecord comboRecord = new ComboRecord();
            comboRecord.setComboId(combo.getId());
            comboRecord.setCreateTime(new Date());
            comboRecord.setPartyId(partyId);
            comboRecord.setPromoteNum(combo.getPromoteNum());
            comboRecord.setName(name);
            comboRecord.setAmount(prize);
            comboRecord.setDay(combo.getDay());
//            comboRecord.setStopTime(DateUtils.addDate(new Date(nw),combo.getDay()).getTime());
            comboRecord.setStopTime(comboUser.getStopTime());
            this.getHibernateTemplate().save(comboRecord);
        } catch (BusinessException e) {
            logger.error("支付失败", e);
            throw new BusinessException(e.getMessage());
        } finally {
            redisHandler.remove(lockKey);
        }
    }

    @Override
    public MallPageInfo listComboRecord(String partyId, String begin, String end, int pageNum, int pageSize) {
        DetachedCriteria query = DetachedCriteria.forClass(ComboRecord.class);
        query.add(Property.forName("partyId").eq(partyId));
        query.addOrder(Order.desc("createTime"));
        if (StringUtils.isNotEmpty(begin)) {
            query.add(Property.forName("createTime").ge(DateUtils.dayStringToDate(begin)));
        }
        if (StringUtils.isNotEmpty(end)) {
            query.add(Property.forName("createTime").le(DateUtils.getDayEnd(DateUtils.dayStringToDate(end))));
        }

        // 查询总条数
        Long totalCount = (Long) query.setProjection(Projections.rowCount()).getExecutableCriteria(this.getHibernateTemplate().getSessionFactory().getCurrentSession()).uniqueResult();
        query.setProjection(null);

        MallPageInfo mallPageInfo = MallPageInfoUtil.getMallPage(pageSize, pageNum, totalCount, getHibernateTemplate().findByCriteria(query, (pageNum - 1) * pageSize, pageSize));

        return mallPageInfo;

    }

    public void setWalletService(WalletService walletService) {
        this.walletService = walletService;
    }

    public void setMoneyLogService(MoneyLogService moneyLogService) {
        this.moneyLogService = moneyLogService;
    }

    public void setRedisHandler(RedisHandler redisHandler) {
        this.redisHandler = redisHandler;
    }
}
