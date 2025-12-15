package project.log.internal;

import kernel.util.Arith;
import kernel.web.Page;
import kernel.web.PagedQueryDao;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Property;
import org.hibernate.query.NativeQuery;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;
import org.springframework.transaction.annotation.Transactional;
import project.Constants;
import project.log.*;
import project.mall.seller.SellerService;
import project.redis.RedisHandler;
import project.redis.interal.KeyValue;
import project.syspara.SysparaService;
import project.wallet.Wallet;
import project.wallet.WalletService;

import java.util.*;

public class MoneyFreezeServiceImpl extends HibernateDaoSupport implements MoneyFreezeService {
	protected PagedQueryDao pagedDao;

	protected WalletService walletService;

	protected MoneyLogService moneyLogService;

	protected SellerService sellerService;

	private RedisHandler redisHandler;

	@Override
	public void save(MoneyFreeze entity) {
		if (entity.getCreateTime() == null) {
			entity.setCreateTime(new Date());
		}

		getHibernateTemplate().save(entity);
	}

	@Override
	public MoneyFreeze getById(String id) {
		if (id == null || id.trim().isEmpty()) {
			throw new RuntimeException("未指定记录");
		}

		return getHibernateTemplate().get(MoneyFreeze.class, id);
	}

	/**
	 * 冻结资金的完整逻辑
	 *
	 * @param sellerId
	 * @param freezeAmout : 传进的值都是正值
	 * @param freezeDays
	 * @param freezeReason
	 * @param operator
	 */
	@Override
	@Transactional
	public MoneyFreeze updateFreezeSeller(String sellerId, double freezeAmout, int freezeDays, String freezeReason, String operator) {

		Wallet wallet = this.walletService.saveWalletByPartyId(sellerId);
		double amount_before = wallet.getMoney();
		double moneyAfterFrozenBefore = wallet.getMoneyAfterFrozen();
		if (freezeAmout == 0.0D) {
			// 提交 0 意味着全部冻结
			freezeAmout = amount_before;
		}
		if (amount_before < freezeAmout) {
			throw new RuntimeException("冻结资金额度超过商家拥有资金数量");
		}
		if (freezeAmout < 0) {
			throw new RuntimeException("错误的冻结资金数量");
		}

		// 更新商家资金冻结字段 状态改变后 moneyAfterFrozen为用户钱包金额
		wallet.setFrozenState(1);
		wallet.setMoneyAfterFrozen(Arith.sub(amount_before,freezeAmout));
		wallet.setMoney(freezeAmout);
		walletService.update(wallet);

		this.sellerService.updateFreezeState(sellerId, 1);

		MoneyLog moneylog = new MoneyLog();
		moneylog.setCategory(Constants.MONEYLOG_CATEGORY_CONTRACT);
		moneylog.setAmount_before(amount_before);
		moneylog.setAmount(-freezeAmout);
		moneylog.setAmount_after(Arith.sub(amount_before,freezeAmout));
		moneylog.setLog("冻结商家资金");
		moneylog.setPartyId(sellerId);
		moneylog.setWallettype(Constants.WALLET);
		moneylog.setContent_type(Constants.MONEYLOG_FREEZE_SELLER);

		moneyLogService.save(moneylog);

		Date now = new Date();
		Date endTime = new Date(now.getTime() + 24L * freezeDays * 3600L * 1000L);
		MoneyFreeze freeze = new MoneyFreeze();
		freeze.setPartyId(sellerId);
		freeze.setReason(freezeReason);
		freeze.setStatus(1);
		freeze.setAmount(freezeAmout);
		freeze.setBeginTime(now);
		freeze.setEndTime(endTime);
		freeze.setCreateTime(now);
		freeze.setMoneyLog(moneylog.getId().toString());
		freeze.setOperator(operator);
		this.save(freeze);


		MoneyLog moneylog1 = new MoneyLog();
		moneylog1.setCategory(Constants.MONEYLOG_CATEGORY_CONTRACT);
		moneylog1.setFreeze(1);
		moneylog1.setAmount_before(moneyAfterFrozenBefore);
		moneylog1.setAmount(Arith.sub(amount_before,freezeAmout));
		moneylog1.setAmount_after(wallet.getMoneyAfterFrozen());
		moneylog1.setLog("冻结商家资金");
		moneylog1.setPartyId(sellerId);
		moneylog1.setWallettype(Constants.WALLET);
		moneylog1.setContent_type(Constants.MONEYLOG_FREEZE_SELLER);
		moneyLogService.save(moneylog1);
//
//		// 优化定时任务的处理速度，方便轮询定时解冻的记录
//		redisHandler.zadd(MallLogRedisKeys.SELLER_MONEY_FREEZE, endTime.getTime(), freeze.getId().toString());

		return freeze;
	}


	/**
	 * 解冻资金的完整逻辑
	 *
	 * @param id
	 * @param operator
	 */
	@Override
	@Transactional
	public int updateAutoUnFreezeSeller(String id, String operator) {
		MoneyFreeze freezeEntity = getById(id);

		Wallet wallet = this.walletService.saveWalletByPartyId(freezeEntity.getPartyId());

		if (freezeEntity == null) {
			throw new RuntimeException("不存在的冻结记录");
		}

		if (wallet.getFrozenState() != 1){
			throw new RuntimeException("用户不处于冻结状态");
		}
		if (freezeEntity.getStatus() == 0) {
			return 0;
		}

		int check = updateSetUnFreezeState(id, operator);
		if (check == 0) {
			// 防止并发情况下重复回退资金
			return 0;
		}


		//用户被冻结后钱包余额
		double amount_before = wallet.getMoneyAfterFrozen();

		//被冻结金额
		double money = wallet.getMoney();

		//更新商家资金余额，余额给加回去 钱包冻结状态解除， moneyAfterFrozen值清零
		wallet.setFrozenState(0);
		wallet.setMoney(Arith.roundDown(Arith.add(wallet.getMoney(),wallet.getMoneyAfterFrozen()),2));
		wallet.setMoneyAfterFrozen(0);
		this.walletService.update(wallet);

		this.sellerService.updateFreezeState(freezeEntity.getPartyId().toString(), 0);

		MoneyLog moneylog = new MoneyLog();
		moneylog.setCategory(Constants.MONEYLOG_CATEGORY_CONTRACT);
		moneylog.setAmount_before(amount_before);
		moneylog.setAmount(money);
		moneylog.setAmount_after(Arith.add(amount_before, money));
		moneylog.setLog("定时解冻商家资金");
		moneylog.setPartyId(freezeEntity.getPartyId().toString());
		moneylog.setWallettype(Constants.WALLET);
		moneylog.setContent_type(Constants.MONEYLOG_UNFREEZE_SELLER);


		moneyLogService.save(moneylog);
//		MoneyLog moneylog1 = new MoneyLog();
//		moneylog1.setCategory(Constants.MONEYLOG_CATEGORY_CONTRACT);
//		moneylog1.setAmount_before(amount_before);
//		moneylog1.setFreeze(1);
//		moneylog1.setAmount(-amount_before);
//		moneylog1.setAmount_after(Arith.sub(amount_before, amount_before));
//		moneylog1.setLog("定时解冻商家资金");
//		moneylog1.setPartyId(freezeEntity.getPartyId().toString());
//		moneylog1.setWallettype(Constants.WALLET);
//		moneylog1.setContent_type(Constants.MONEYLOG_UNFREEZE_SELLER);
//
//		moneyLogService.save(moneylog1);
		// 优化定时任务的处理速度
		redisHandler.zrem(MallLogRedisKeys.SELLER_MONEY_FREEZE, id);

		return 1;
	}

	/**
	 * 解冻资金的完整逻辑
	 *
	 * @param id
	 * @param operator
	 */
	@Override
	@Transactional
	public int updateUnFreezeSeller(String id, String operator) {
		MoneyFreeze freezeEntity = getById(id);

		Wallet wallet = this.walletService.saveWalletByPartyId(freezeEntity.getPartyId());

		if (freezeEntity == null) {
			throw new RuntimeException("不存在的冻结记录");
		}

		if (wallet.getFrozenState() != 1){
			throw new RuntimeException("用户不处于冻结状态");
		}
		if (freezeEntity.getStatus() == 0) {
			return 0;
		}

		int check = updateSetUnFreezeState(id, operator);
		if (check == 0) {
			// 防止并发情况下重复回退资金
			return 0;
		}


		//用户被冻结后钱包余额
		double amount_before = wallet.getMoneyAfterFrozen();

		//被冻结金额
		double money = wallet.getMoney();

		//更新商家资金余额，余额给加回去 钱包冻结状态解除， moneyAfterFrozen值清零
		wallet.setFrozenState(0);
		wallet.setMoney(Arith.roundDown(Arith.add(wallet.getMoney(),wallet.getMoneyAfterFrozen()),2));
		wallet.setMoneyAfterFrozen(0.0);
		this.walletService.update(wallet);

		this.sellerService.updateFreezeState(freezeEntity.getPartyId().toString(), 0);

		MoneyLog moneylog = new MoneyLog();
		moneylog.setCategory(Constants.MONEYLOG_CATEGORY_CONTRACT);
		moneylog.setAmount_before(amount_before);
		moneylog.setAmount(money);
		moneylog.setAmount_after(Arith.add(amount_before, money));
		moneylog.setLog("解冻商家资金");
		moneylog.setPartyId(freezeEntity.getPartyId().toString());
		moneylog.setWallettype(Constants.WALLET);
		moneylog.setContent_type(Constants.MONEYLOG_UNFREEZE_SELLER);


		moneyLogService.save(moneylog);
		MoneyLog moneylog1 = new MoneyLog();
		moneylog1.setCategory(Constants.MONEYLOG_CATEGORY_CONTRACT);
		moneylog1.setAmount_before(amount_before);
		moneylog1.setFreeze(1);
		moneylog1.setAmount(-amount_before);
		moneylog1.setAmount_after(Arith.sub(amount_before, amount_before));
		moneylog1.setLog("解冻商家资金");
		moneylog1.setPartyId(freezeEntity.getPartyId().toString());
		moneylog1.setWallettype(Constants.WALLET);
		moneylog1.setContent_type(Constants.MONEYLOG_UNFREEZE_SELLER);

		moneyLogService.save(moneylog1);

		// 优化定时任务的处理速度
		redisHandler.zrem(MallLogRedisKeys.SELLER_MONEY_FREEZE, id);

		return 1;
	}

	@Override
	public int updateSetUnFreezeState(String id, String operator) {
		if (id == null || id.trim().isEmpty()) {
			return 0;
		}
		if (operator == null || operator.trim().isEmpty()) {
			operator = "0";
		}

		Session currentSession = getHibernateTemplate().getSessionFactory().getCurrentSession();
		String sql = " update T_MONEY_FREEZE set STATUS= :status, OPERATOR= :operator, END_TIME=now() where UUID= :id and STATUS=1 ";
		NativeQuery query = currentSession.createSQLQuery(sql);

		query.setParameter("status", 0);
		query.setParameter("operator", operator);
		query.setParameter("id", id);

		return query.executeUpdate();
	}

	@Override
	public Page pagedListFreeze(String partyId, int status, int pageNum, int pageSize) {
		StringBuffer queryString = new StringBuffer("");
		queryString.append(" FROM MoneyFreeze WHERE 1=1 ");
		Map parameters = new HashMap();

		if (StringUtils.isNotEmpty(partyId)) {
			queryString.append(" AND partyId =:partyId ");
			parameters.put("partyId", partyId);
		}
		if(status >= 0) {
			queryString.append(" AND status = :status ");
			parameters.put("status", status);
		}

		queryString.append(" order by createTime desc ");
		Page page = this.pagedDao.pagedQueryHql(pageNum, pageSize, queryString.toString(), parameters);
		return page;
	}

	public List<String> listPendingFreezeRecords() {
		double min = 0;
		double max = System.currentTimeMillis();
		Set<KeyValue<String, Double>> pendingItems = redisHandler.zRange(MallLogRedisKeys.SELLER_MONEY_FREEZE, min, max);
		List<String> idList = new ArrayList();
		if (pendingItems == null || pendingItems.isEmpty()) {
			return idList;
		}

		for (KeyValue<String, Double> oneItem : pendingItems) {
			idList.add(oneItem.getKey());
		}

		return idList;
	}

    public List<MoneyFreeze> listPendingFreezeRecords(int size) {
        StringBuffer queryString = new StringBuffer("");
        queryString.append(" FROM MoneyFreeze WHERE status=1 ");

        Map parameters = new HashMap();
        Page page = this.pagedDao.pagedQueryHql(1, size, queryString.toString(), parameters);

        return  (List<MoneyFreeze>) page.getElements();
    }

	@Override
	public List<MoneyFreeze> listByIds(List<String> ids) {
		List<MoneyFreeze> list = new ArrayList();
		if (ids == null || ids.isEmpty()) {
			return list;
		}

		DetachedCriteria query = DetachedCriteria.forClass(MoneyFreeze.class);
		query.add(Property.forName("id").in(ids));

		List retList = getHibernateTemplate().findByCriteria(query);
		if (retList == null || retList.isEmpty()) {
			return list;
		}

		list.addAll(retList);
		return list;
	}

	@Override
	public MoneyFreeze getLastFreezeRecord(String sellerId) {
		if (sellerId == null || sellerId.isEmpty()) {
			return null;
		}

		DetachedCriteria query = DetachedCriteria.forClass(MoneyFreeze.class);
		query.add(Property.forName("partyId").eq(sellerId));
		query.add(Property.forName("status").eq(1));
		query.addOrder(Order.desc("createTime"));

		List retList = getHibernateTemplate().findByCriteria(query);
		if (retList == null || retList.isEmpty()) {
			return null;
		}

		return (MoneyFreeze)retList.get(0);
	}

	public void setPagedDao(PagedQueryDao pagedDao) {
		this.pagedDao = pagedDao;
	}

	public PagedQueryDao getPagedDao() {
		return pagedDao;
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

	public void setSellerService(SellerService sellerService) {
		this.sellerService = sellerService;
	}

}
