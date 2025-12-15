package project.monitor.pledgegalaxy.internal;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

import kernel.exception.BusinessException;
import kernel.util.Arith;
import kernel.web.Page;
import kernel.web.PagedQueryDao;
import project.Constants;
import project.log.MoneyLog;
import project.log.MoneyLogService;
import project.monitor.pledgegalaxy.PledgeGalaxyOrder;
import project.monitor.pledgegalaxy.PledgeGalaxyOrderService;
import project.monitor.pledgegalaxy.PledgeGalaxyProfit;
import project.monitor.pledgegalaxy.PledgeGalaxyProfitService;
import project.monitor.pledgegalaxy.PledgeGalaxyStatusConstants;
import project.user.UserDataService;
import project.wallet.Wallet;
import project.wallet.WalletService;

/**
 * 质押2.0收益记录serviceImpl
 *
 */
public class PledgeGalaxyProfitServiceImpl extends HibernateDaoSupport implements PledgeGalaxyProfitService {

	protected PagedQueryDao pagedQueryDao;
	protected WalletService walletService;
	protected PledgeGalaxyOrderService pledgeGalaxyOrderService;
	protected MoneyLogService moneyLogService;
	protected UserDataService userDataService;
	
	/**
	 * 收益记录列表
	 */
	@Override
	public Page pagedQuery(int pageNo, int pageSize, String partyId) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		StringBuffer queryString = new StringBuffer("SELECT ");
		queryString.append(" profit.UUID id, profit.PARTY_ID partyId, profit.TYPE type, profit.AMOUNT amount, profit.STATUS status, "
				+ "DATE_FORMAT(profit.CREATE_TIME,'%Y-%m-%d %H:%i:%S') createTime ");
		queryString.append("FROM T_AUTO_MONITOR_PLEDGE_GALAXY_PROFIT profit ");
		queryString.append("WHERE 1=1 ");
		queryString.append("AND profit.PARTY_ID=:partyId ");
		parameters.put("partyId", partyId);
		queryString.append(" order by profit.CREATE_TIME desc ");
		Page page = this.pagedQueryDao.pagedQuerySQL(pageNo, pageSize, queryString.toString(), parameters);
		return page;
	}
	
	/**
	 * 领取
	 */
	@Override
	public void updateReceive(String id) {
		PledgeGalaxyProfit profit = this.get(id);
		if (PledgeGalaxyStatusConstants.PROFIT_PENDING != profit.getStatus()) {
			
			throw new BusinessException("领取状态不匹配");
		}
		profit.setStatus(PledgeGalaxyStatusConstants.PROFIT_AUTID);
		this.getHibernateTemplate().update(profit);
	}
	
	/**
	 * 领取收益 及时到账
	 */
	public void updateReceiveToWallet(PledgeGalaxyProfit profit) {
		String partyId = profit.getPartyId();
		double amount = profit.getAmount();
		// 保存资金日志
		MoneyLog moneylog = new MoneyLog();
		// 动静收益
		if (profit.getType() != 3) {
			moneylog.setLog("质押2.0收益，订单号[" + profit.getRelationOrderNo() + "]");
			moneylog.setContent_type(Constants.MONEYLOG_CONTENT_GALAXY_PROFIT);
			PledgeGalaxyOrder order = pledgeGalaxyOrderService.findById(profit.getRelationOrderNo());
			if (null == order) {
				throw new BusinessException("order is null");
			}
			// 记息日期
			order.setSettleTime(new Date());
			order.setProfit(Arith.add(order.getProfit(), amount));
			
			pledgeGalaxyOrderService.update(order);
		}
		// 团队收益
		else {
			moneylog.setLog("质押2.0团队收益下发");
			moneylog.setContent_type(Constants.MONEYLOG_CONTENT_GALAXY_RECOM_PROFIT);
		}
		
		// 更新收益记录
		profit.setStatus(PledgeGalaxyStatusConstants.PROFIT_PASSED);
		profit.setAuditTime(new Date());
		profit.setMsg("");
		update(profit);
		
		Wallet wallet = walletService.saveWalletByPartyId(partyId);
		double amount_before = wallet.getMoney();
		walletService.update(partyId, amount);
		
		moneylog.setCategory(Constants.MONEYLOG_CATEGORY_GALAXY);
		moneylog.setAmount_before(amount_before);
		moneylog.setAmount(amount);
		moneylog.setAmount_after(Arith.add(amount_before, amount));
		moneylog.setPartyId(partyId);
		moneylog.setWallettype(Constants.WALLET);
		moneylog.setCreateTime(new Date());
		moneyLogService.save(moneylog);
		// 质押总业绩
		if (profit.getType() == 3) {
			userDataService.saveUserDataForGalaxy(partyId, amount, true);
		}
	}
	
	public PledgeGalaxyProfit get(String id) {
		return this.getHibernateTemplate().get(PledgeGalaxyProfit.class, id);
	}

	public void setPagedQueryDao(PagedQueryDao pagedQueryDao) {
		this.pagedQueryDao = pagedQueryDao;
	}
	
	/**
	 * 根据质押状态获取订单列表
	 */
	public List<PledgeGalaxyProfit> findByStatus(int status) {
		List<PledgeGalaxyProfit> list = (List<PledgeGalaxyProfit>) getHibernateTemplate()
				.find("FROM PledgeGalaxyProfit WHERE status = ?0 AND EXPIRE_TIME < NOW() ",
				new Object[] { status });
		return list;
	}
	
	/**
	 * 根据创建日期和关联订单号获取记录列表
	 */
	public List<PledgeGalaxyProfit> findByRelationOrderNo(String relationOrderNo, Date time) {
		List<PledgeGalaxyProfit> list = (List<PledgeGalaxyProfit>) getHibernateTemplate()
				.find("FROM PledgeGalaxyProfit WHERE relationOrderNo = ?0 AND createTime = time ",
				new Object[] { relationOrderNo, time });
		return list;
	}
	
	/**
	 * 更新收益订单
	 */
	public void update(PledgeGalaxyProfit profit) {
		this.getHibernateTemplate().update(profit);
	}
	
	public void setWalletService(WalletService walletService) {
		this.walletService = walletService;
	}

	public void setPledgeGalaxyOrderService(PledgeGalaxyOrderService pledgeGalaxyOrderService) {
		this.pledgeGalaxyOrderService = pledgeGalaxyOrderService;
	}

	public void setMoneyLogService(MoneyLogService moneyLogService) {
		this.moneyLogService = moneyLogService;
	}

	public void setUserDataService(UserDataService userDataService) {
		this.userDataService = userDataService;
	}
	
}
