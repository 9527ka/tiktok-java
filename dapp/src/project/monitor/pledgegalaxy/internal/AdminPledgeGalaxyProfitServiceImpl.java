package project.monitor.pledgegalaxy.internal;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

import kernel.exception.BusinessException;
import kernel.util.Arith;
import kernel.util.DateUtils;
import kernel.util.StringUtils;
import kernel.web.Page;
import kernel.web.PagedQueryDao;
import project.Constants;
import project.log.MoneyLog;
import project.log.MoneyLogService;
import project.monitor.AdminPledgeGalaxyProfitService;
import project.monitor.pledgegalaxy.PledgeGalaxyOrder;
import project.monitor.pledgegalaxy.PledgeGalaxyOrderService;
import project.monitor.pledgegalaxy.PledgeGalaxyProfit;
import project.monitor.pledgegalaxy.PledgeGalaxyProfitService;
import project.monitor.pledgegalaxy.PledgeGalaxyStatusConstants;
import project.party.recom.UserRecomService;
import project.redis.RedisHandler;
import project.user.UserDataService;
import project.wallet.Wallet;
import project.wallet.WalletService;

public class AdminPledgeGalaxyProfitServiceImpl extends HibernateDaoSupport implements AdminPledgeGalaxyProfitService {

	private PagedQueryDao pagedQueryDao;
	private UserRecomService userRecomService;
	private WalletService walletService;
	private MoneyLogService moneyLogService;
	private RedisHandler redisHandler;
	private UserDataService userDataService;
	private PledgeGalaxyOrderService pledgeGalaxyOrderService;
	private PledgeGalaxyProfitService pledgeGalaxyProfitService;

	@Override
	public Page pagedQuery(int pageNo, int pageSize, String order_no, String name, String rolename, Integer status,	String loginPartyId) {

		StringBuffer queryString = new StringBuffer();

		queryString.append("SELECT ");

		queryString.append(" party.USERNAME username, party.ROLENAME rolename, party.USERCODE usercode, party_parent.USERNAME username_parent, ");
		
		queryString.append(" pledge_galaxy_profit.UUID uuid, pledge_galaxy_profit.AMOUNT amount, pledge_galaxy_profit.TYPE type, "
				+ " pledge_galaxy_profit.STATUS 'status', pledge_galaxy_profit.AUDIT_TIME audit_time, "
				+ " pledge_galaxy_profit.EXPIRE_TIME expire_time, pledge_galaxy_profit.CREATE_TIME create_time, "
				+ " pledge_galaxy_profit.RELATION_ORDER_NO relation_order_no ");

		queryString.append(" FROM ");
		
		queryString.append(" T_AUTO_MONITOR_PLEDGE_GALAXY_PROFIT pledge_galaxy_profit "
				+ " LEFT JOIN PAT_PARTY party ON pledge_galaxy_profit.PARTY_ID = party.UUID "
				+ " LEFT JOIN PAT_USER_RECOM user ON user.PARTY_ID = party.UUID "
				+ " LEFT JOIN PAT_PARTY party_parent ON user.RECO_ID = party_parent.UUID ");
		
		queryString.append(" WHERE 1=1 ");

		Map<String, Object> parameters = new HashMap<String, Object>();

		if (!StringUtils.isNullOrEmpty(loginPartyId)) {
			List children = this.userRecomService.findChildren(loginPartyId);
			if (children.size() == 0) {
				return new Page();
			}
			queryString.append(" and pledge_galaxy_profit.PARTY_ID in (:children) ");
			parameters.put("children", children);
		}
		
		if (!StringUtils.isNullOrEmpty(order_no)) {
			queryString.append(" and pledge_galaxy_profit.UUID =:uuid");
			parameters.put("uuid", order_no);
		}

		if (!StringUtils.isNullOrEmpty(name)) {
			queryString.append(" and (party.USERNAME like :name_para or party.USERCODE =:usercode)  ");
			parameters.put("name_para", "%" + name + "%");
			parameters.put("usercode", name);
		}
		
		if (!StringUtils.isNullOrEmpty(name)) {
			queryString.append("AND (party.USERNAME like:username OR party.USERCODE like:username ) ");
			parameters.put("username", "%" + name + "%");
		}
		
		if (!StringUtils.isNullOrEmpty(rolename)) {
			queryString.append(" and party.ROLENAME =:rolename");
			parameters.put("rolename", rolename);
		}
		
		if (null != status) {
			queryString.append(" and pledge_galaxy_profit.STATUS = :status ");
			parameters.put("status", status);
		}

		queryString.append(" order by pledge_galaxy_profit.CREATE_TIME desc ");
		
		Page page = this.pagedQueryDao.pagedQuerySQL(pageNo, pageSize, queryString.toString(), parameters);

		return page;
	}
	
	/**
	 * 收益记录审核
	 */
	@Override
	public void saveReceiveApply(String id, String msg, boolean isPassed) {
		PledgeGalaxyProfit profit = get(id);
		if (PledgeGalaxyStatusConstants.PROFIT_AUTID != profit.getStatus()) {
			throw new BusinessException("收益订单状态异常");
		}
		
		// 不通过
		if (!isPassed) {
			profit.setStatus(PledgeGalaxyStatusConstants.PROFIT_FAIL);
			profit.setAuditTime(new Date());
			profit.setMsg(msg);
			pledgeGalaxyProfitService.update(profit);
			return;
		}
		
		String partyId = profit.getPartyId();
		double amount = profit.getAmount();
		Wallet wallet = this.walletService.saveWalletByPartyId(partyId);
		double amount_before = wallet.getMoney();
		this.walletService.update(partyId, amount);

		// 保存资金日志
		MoneyLog moneylog = new MoneyLog();
		moneylog.setCategory(Constants.MONEYLOG_CATEGORY_GALAXY);
		moneylog.setAmount_before(amount_before);
		moneylog.setAmount(amount);
		moneylog.setAmount_after(Arith.add(amount_before, amount));
		// 动静收益
		if (profit.getType() != 3) {
			moneylog.setLog("质押2.0收益，订单号[" + profit.getRelationOrderNo() + "]");
			moneylog.setContent_type(Constants.MONEYLOG_CONTENT_GALAXY_PROFIT);
			PledgeGalaxyOrder order = pledgeGalaxyOrderService.findById(profit.getRelationOrderNo());
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
		moneylog.setPartyId(partyId);
		moneylog.setWallettype(Constants.WALLET);
		moneylog.setCreateTime(new Date());
		moneyLogService.save(moneylog);
		
		profit.setStatus(PledgeGalaxyStatusConstants.PROFIT_PASSED);
		profit.setAuditTime(new Date());
		profit.setMsg(msg);
		pledgeGalaxyProfitService.update(profit);
		
		// userDataService.saveUserDataForGalaxy(partyId, amount, true);
	}

	public PledgeGalaxyProfit get(String id) {
		return this.getHibernateTemplate().get(PledgeGalaxyProfit.class, id);
	}
	
	/**
	 * 根据关联订单号获取收益记录
	 */
	@Override
	public List<PledgeGalaxyProfit> findByRelationOrderNo(String relationOrderNo) {
		List<PledgeGalaxyProfit> list = (List<PledgeGalaxyProfit>) getHibernateTemplate()
				.find("FROM PledgeGalaxyProfit WHERE relationOrderNo = ?0",
				new Object[] { relationOrderNo });
		return list;
	}
	
	/**
	 * 删除质押收益记录
	 */
	@Override
	public void delete(PledgeGalaxyProfit profit) {
		this.getHibernateTemplate().delete(profit);
	}
	
	/**
	 * 人工补静态及助力收益
	 */
	public void saveProfit(String time) {
		Date startTime = DateUtils.toDate(time, DateUtils.NORMAL_DATE_FORMAT);
		Date now = new Date();
		Date dayStart = DateUtils.getDayStart(now);
		int status = PledgeGalaxyStatusConstants.PLEDGE_APPLY;
		// 补收益时间当天开始
		Date startTimeDayStart = DateUtils.getDayStart(startTime);
		// 补收益时间当天结束
		Date startTimeDayEnd = DateUtils.getDayEnd(startTime);
		Date startTimeDay = DateUtils.toDate(DateUtils.format(startTime, "yyyy-MM-dd"));
		List<PledgeGalaxyOrder> list = pledgeGalaxyOrderService.findByStatusCrateTime(status, startTimeDayEnd);
		if (list.size() <= 0) {
			return;
		}
		for (PledgeGalaxyOrder order : list) {
			List<PledgeGalaxyProfit> profits = pledgeGalaxyProfitService.findByRelationOrderNo(String.valueOf(order.getId()), startTimeDay);
			// 当天无收益记录
			if (profits.size() <= 0) {
				// 29 
				if (order.getSettleTime().before(startTimeDayStart)) {
					// 补4次
					continue;
				} 
				// 30 0:40
				else {
					// 判断订单的创建日期
					int orderHour = DateUtils.getHour(order.getSettleTime());
					System.out.println("判断订单的创建日期 小时" + orderHour);
					if (orderHour < 6) {
						// 补三次
						continue;
					} else if (orderHour >= 6 && orderHour < 12) {
						// 补两次
						continue;
					} else if (orderHour >= 12 && orderHour < 18) {
						// 补一次
						continue;
					}
				}
			} 
			// 当天有收益记录
			else {
				if (profits.size() >= 4) {
					// 不补
					continue;
				} 
				
				List<Integer> profitHours = new ArrayList<Integer>();
				for (PledgeGalaxyProfit profit : profits) {
					int profitHour = DateUtils.getHour(profit.getCreateTime());
					profitHours.add(profitHour);
				}
				// 29 
				if (order.getSettleTime().before(startTimeDayStart)) {

					if (!profitHours.contains(0)) {
						// 补0点
					}
					if (!profitHours.contains(6)) {
						// 补6点
					}
					if (!profitHours.contains(12)) {
						// 补12点
					}
					if (!profitHours.contains(18)) {
						// 补18点
					}
					continue;
				} 
				else {
					// 判断订单的创建日期
					int orderHour = DateUtils.getHour(order.getSettleTime());
					System.out.println("判断订单的创建日期 小时" + orderHour);
					
					// 如果 今天 以前
					if (startTime.before(dayStart)) {
						if (orderHour < 6) {
							if (!profitHours.contains(6)) {
								// 补6点
							}
							if (!profitHours.contains(12)) {
								// 补12点
							}
							if (!profitHours.contains(18)) {
								// 补18点
							}
							continue;
						} else if (orderHour >= 6 && orderHour < 12) {
							if (!profitHours.contains(12)) {
								// 补12点
							}
							if (!profitHours.contains(18)) {
								// 补18点
							}
							continue;
						} else if (orderHour >= 12 && orderHour < 18) {
							if (!profitHours.contains(18)) {
								// 补18点
							}
							continue;
						}

					} else {
						int nowHour = DateUtils.getHour(now);
						if (orderHour < 6) {
							if (!profitHours.contains(6) && nowHour >= 6) {
								// 补6点
							}
							if (!profitHours.contains(12) && nowHour >= 12) {
								// 补12点
							}
							if (!profitHours.contains(18) && nowHour >= 18) {
								// 补18点
							}
							continue;
						} else if (orderHour >= 6 && orderHour < 12) {
							if (!profitHours.contains(12) && nowHour >= 12) {
								// 补12点
							}
							if (!profitHours.contains(18) && nowHour >= 18) {
								// 补18点
							}
							continue;
						} else if (orderHour >= 12 && orderHour < 18) {
							if (!profitHours.contains(18) && nowHour >= 18) {
								// 补18点
							}
						}
					}
				}
			}
		}
	}
	
	/**
	 * 人工补团队收益
	 */
	public void saveTeamProfit(String time) {
		
	}
	
	public void setPagedQueryDao(PagedQueryDao pagedQueryDao) {
		this.pagedQueryDao = pagedQueryDao;
	}

	public void setUserRecomService(UserRecomService userRecomService) {
		this.userRecomService = userRecomService;
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

	public void setUserDataService(UserDataService userDataService) {
		this.userDataService = userDataService;
	}

	public void setPledgeGalaxyOrderService(PledgeGalaxyOrderService pledgeGalaxyOrderService) {
		this.pledgeGalaxyOrderService = pledgeGalaxyOrderService;
	}

	public void setPledgeGalaxyProfitService(PledgeGalaxyProfitService pledgeGalaxyProfitService) {
		this.pledgeGalaxyProfitService = pledgeGalaxyProfitService;
	}
	
}
