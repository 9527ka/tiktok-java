package project.log.internal;

import kernel.util.Arith;
import kernel.web.Page;
import kernel.web.PagedQueryDao;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
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
		// 可冻结基数 = 账户总额。已冻结(frozenState=1)时 money 是冻结部分, 必须用 冻结部分+可用部分 作总额,
		// 否则对已冻结账号再次冻结会用冻结部分当基数, 把可用余额算没了(资金损失)。未冻结时总额= money(行为不变)。
		boolean alreadyFrozen = wallet.getFrozenState() != null && wallet.getFrozenState() == 1;
		double amount_before = alreadyFrozen ? Arith.add(wallet.getMoney(), wallet.getMoneyAfterFrozen()) : wallet.getMoney();
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

		// 只自动解冻店铺冻结(freezeType=1); 提现冻结(freezeType=2)到期由 sumActiveWithdrawFrozen 自然失效, 绝不能走钱包合并解冻
		if (freezeEntity.getFreezeType() != null && freezeEntity.getFreezeType() != 1) {
			return 0;
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

		// 该方法只处理店铺冻结(freezeType=1)的钱包合并解冻; 提现冻结(freezeType=2)由 updateUnFreezeWithdraw 单独释放, 不能走这里(否则会连提现冻结一起放掉并错误合并钱包)
		if (freezeEntity.getFreezeType() != null && freezeEntity.getFreezeType() != 1) {
			throw new RuntimeException("该冻结记录非店铺冻结, 不能用店铺解冻流程");
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
        // 只挑店铺冻结(freezeType=1)做自动解冻; 提现冻结(freezeType=2)不走钱包合并解冻
        queryString.append(" FROM MoneyFreeze WHERE status=1 AND freezeType=1 ");

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
		// 只取店铺冻结(freezeType=1)记录: 店铺解冻不能误解冻提现冻结(freezeType=2)
		query.add(Property.forName("freezeType").eq(1));
		query.addOrder(Order.desc("createTime"));

		List retList = getHibernateTemplate().findByCriteria(query);
		if (retList == null || retList.isEmpty()) {
			return null;
		}

		return (MoneyFreeze)retList.get(0);
	}

	/**
	 * 管理员提现金额冻结(freezeType=2)。仅插入一条冻结记录, 不改动钱包字段。
	 */
	@Override
	@Transactional
	public MoneyFreeze updateFreezeWithdraw(String partyId, double amount, int freezeDays, String reason, String operator) {
		if (partyId == null || partyId.trim().isEmpty()) {
			throw new RuntimeException("未指定用户");
		}
		if (amount <= 0) {
			throw new RuntimeException("冻结金额必须大于0");
		}
		if (freezeDays <= 0) {
			throw new RuntimeException("冻结天数必须大于0");
		}

		Wallet wallet = this.walletService.saveWalletByPartyId(partyId);
		// 可提现基数: 与提现校验保持一致(冻结状态下用 moneyAfterFrozen, 否则用 money)
		double base = (wallet.getFrozenState() != null && wallet.getFrozenState() == 1)
				? wallet.getMoneyAfterFrozen() : wallet.getMoney();
		// 已生效的提现冻结额度
		double existsFrozen = sumActiveWithdrawFrozen(partyId);
		double available = Arith.sub(base, existsFrozen);
		if (amount > available) {
			throw new RuntimeException("冻结金额超过用户可冻结余额(可冻结:" + available + ")");
		}

		Date now = new Date();
		Date endTime = new Date(now.getTime() + 24L * freezeDays * 3600L * 1000L);
		MoneyFreeze freeze = new MoneyFreeze();
		freeze.setPartyId(partyId);
		freeze.setReason(reason);
		freeze.setStatus(1);
		freeze.setFreezeType(2);
		freeze.setAmount(amount);
		freeze.setBeginTime(now);
		freeze.setEndTime(endTime);
		freeze.setCreateTime(now);
		freeze.setOperator(operator);
		this.save(freeze);
		return freeze;
	}

	/**
	 * 合计某用户当前"冻结中且未到期"的提现冻结额度。endTime<=now 的记录自动不计入 → 到期自动解冻。
	 */
	@Override
	public double sumActiveWithdrawFrozen(String partyId) {
		if (partyId == null || partyId.trim().isEmpty()) {
			return 0.0;
		}
		DetachedCriteria query = DetachedCriteria.forClass(MoneyFreeze.class);
		query.add(Property.forName("partyId").eq(partyId));
		query.add(Property.forName("status").eq(1));
		query.add(Property.forName("freezeType").eq(2));
		query.add(Restrictions.gt("endTime", new Date()));
		query.setProjection(Projections.sum("amount"));
		List result = getHibernateTemplate().findByCriteria(query);
		if (result == null || result.isEmpty() || result.get(0) == null) {
			return 0.0;
		}
		return ((Number) result.get(0)).doubleValue();
	}

	/**
	 * 管理员手动提前解冻某用户全部生效中的提现冻结。
	 */
	@Override
	@Transactional
	public int updateCancelWithdrawFreeze(String partyId, String operator) {
		if (partyId == null || partyId.trim().isEmpty()) {
			return 0;
		}
		if (operator == null || operator.trim().isEmpty()) {
			operator = "0";
		}
		Session currentSession = getHibernateTemplate().getSessionFactory().getCurrentSession();
		String sql = " update T_MONEY_FREEZE set STATUS=0, OPERATOR= :operator, END_TIME=now() where PARTY_ID= :partyId and STATUS=1 and FREEZE_TYPE=2 ";
		NativeQuery query = currentSession.createSQLQuery(sql);
		query.setParameter("operator", operator);
		query.setParameter("partyId", partyId);
		return query.executeUpdate();
	}

	/**
	 * 列出某用户当前生效中的提现冻结记录(用于后台展示)。
	 */
	@Override
	public List<MoneyFreeze> listActiveWithdrawFreeze(String partyId) {
		List<MoneyFreeze> list = new ArrayList();
		if (partyId == null || partyId.trim().isEmpty()) {
			return list;
		}
		DetachedCriteria query = DetachedCriteria.forClass(MoneyFreeze.class);
		query.add(Property.forName("partyId").eq(partyId));
		query.add(Property.forName("status").eq(1));
		query.add(Property.forName("freezeType").eq(2));
		query.add(Restrictions.gt("endTime", new Date()));
		query.addOrder(Order.desc("createTime"));
		List retList = getHibernateTemplate().findByCriteria(query);
		if (retList != null && !retList.isEmpty()) {
			list.addAll(retList);
		}
		return list;
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
