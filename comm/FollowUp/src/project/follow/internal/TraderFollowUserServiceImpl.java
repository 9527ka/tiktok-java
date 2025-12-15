package project.follow.internal;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

import kernel.exception.BusinessException;
import kernel.util.Arith;
import kernel.util.StringUtils;
import kernel.web.Page;
import kernel.web.PagedQueryDao;
import project.follow.Trader;
import project.follow.TraderFollowUser;
import project.follow.TraderFollowUserService;
import project.follow.TraderService;
import project.follow.TraderUserService;

public class TraderFollowUserServiceImpl extends HibernateDaoSupport implements TraderFollowUserService {
	private TraderService traderService;
	private TraderUserService traderUserService;
	private PagedQueryDao pagedQueryDao;

	public List<Map<String, Object>> getPaged(int pageNo, int pageSize, String partyId, String profit) {

		StringBuffer queryString = new StringBuffer("");
		queryString.append(" FROM ");
		queryString.append(" TraderFollowUser ");
		queryString.append(" where 1=1 ");

		Map<String, Object> parameters = new HashMap();

		queryString.append(" and trader_partyId = :partyId");
		parameters.put("partyId", partyId);

		if (!StringUtils.isNullOrEmpty(profit)) {
			queryString.append(" and profit >= 0 ");
		}

		queryString.append(" order by profit desc ");

		Page page = this.pagedQueryDao.pagedQueryHql(pageNo, pageSize, queryString.toString(), parameters);
		List<Map<String, Object>> data = this.bulidData(page.getElements());
		return data;

	}

	private List<Map<String, Object>> bulidData(List<TraderFollowUser> traderFollowUsers) {
		List<Map<String, Object>> result_traders = new ArrayList();
		DecimalFormat df2 = new DecimalFormat("#.##");
		df2.setRoundingMode(RoundingMode.FLOOR);// 向下取整
		if (traderFollowUsers == null) {
			return result_traders;
		}
		for (int i = 0; i < traderFollowUsers.size(); i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			TraderFollowUser entity = traderFollowUsers.get(i);
			map.put("name", entity.getUsername());
			map.put("profit", df2.format(entity.getProfit()));
			map.put("amount_sum", df2.format(entity.getAmount_sum()));

			result_traders.add(map);
		}

		return result_traders;

	}

	@Override
	public void save(TraderFollowUser entity, String trader_id) {
		if (entity.getVolume() % 1 != 0 || entity.getVolume() <= 0 || entity.getVolume_max() % 1 != 0) {
			throw new BusinessException(1, "跟单参数输入错误");
		}
		if (entity.getFollow_type() == "1" && (entity.getVolume() > 3000 || entity.getVolume() < 1)) {
			throw new BusinessException(1, "跟单参数输入错误");
		}
		if (entity.getFollow_type() == "2" && (entity.getVolume() > 5 || entity.getVolume() < 1)) {
			throw new BusinessException(1, "跟单倍数输入错误");
		}
		Trader trader = this.traderService.findById(trader_id);
		if (trader == null) {
			throw new BusinessException(1, "交易员不存在");
		}
		if ("0".equals(trader.getState())) {
			throw new BusinessException(1, "交易员未开启带单");
		}
		if (findByStateAndPartyId(entity.getPartyId().toString(), trader.getPartyId().toString(), "1") != null) {
			throw new BusinessException(1, "用户已跟随交易员");
		}
		if (Arith.sub(trader.getFollower_max(), trader.getFollower_now()) < 1) {
			throw new BusinessException(1, "交易员跟随人数已满");
		}
		if (entity.getPartyId().equals(trader.getPartyId())) {
			throw new BusinessException(1, "交易员不能跟随自己");
		}
		Trader trader_user = this.traderService.findByPartyId(entity.getPartyId().toString());
		if (trader_user != null) {
			throw new BusinessException(1, "交易员无法跟随另一个交易员");
		}
		// 跟单固定张数/固定比例---选择 1,固定张数，2，固定比例
		if (trader.getFollow_volumn_min() > 0) {
			switch (entity.getFollow_type()) {
			case "1":
				if (entity.getVolume() < trader.getFollow_volumn_min()) {
					throw new BusinessException(1, "跟单参数输入错误");
				}
				if (entity.getVolume_max() < trader.getFollow_volumn_min()) {
					throw new BusinessException(1, "跟单参数输入错误");
				}
				break;
			case "2":
				throw new BusinessException(1, "交易员已设置最小下单数，无法通过固定比例跟单");
			default:
				break;
			}
		}

		entity.setTrader_partyId(trader.getPartyId().toString());
		entity.setCreate_time(new Date());

		trader.setFollower_now((int) Arith.add(trader.getFollower_now(), 1));
		trader.setFollower_sum((int) Arith.add(trader.getFollower_sum(), 1));
		this.traderService.update(trader);
		/**
		 * 创建累计用户跟随累计表
		 */
		this.traderUserService.saveTraderUserByPartyId(entity.getPartyId());

		this.getHibernateTemplate().save(entity);

	}

	@Override
	public void update(TraderFollowUser entity) {
		if (entity.getVolume() % 1 != 0 || entity.getVolume() <= 0 || entity.getVolume_max() % 1 != 0) {
			throw new BusinessException(1, "跟单参数输入错误");
		}
		if (entity.getFollow_type() == "1" && (entity.getVolume() > 3000 || entity.getVolume() < 1)) {
			throw new BusinessException(1, "跟单参数输入错误");
		}
		if (entity.getFollow_type() == "2" && (entity.getVolume() > 5 || entity.getVolume() < 1)) {
			throw new BusinessException(1, "跟单倍数输入错误");
		}

		this.getHibernateTemplate().update(entity);

	}

	@Override
	public void deleteCancel(String id) {
		TraderFollowUser entity = findById(id);
		/**
		 * 将旧的交易员跟随用户-1
		 */
		Trader trader_before = this.traderService.findByPartyId(entity.getTrader_partyId().toString());
		trader_before.setFollower_now((int) Arith.sub(trader_before.getFollower_now(), 1));
		this.traderService.update(trader_before);

		if (entity != null) {
			this.getHibernateTemplate().delete(entity);
		}

	}

	public List<TraderFollowUser> findByStateAndPartyId(String partyId, String trader_partyId, String state) {
		List<TraderFollowUser> list = (List<TraderFollowUser>) getHibernateTemplate().find(
				" FROM TraderFollowUser   WHERE partyId = ? AND trader_partyId = ?  AND state = ?  ",
				new Object[] { partyId, trader_partyId, state });
		if (list.size() > 0)
			return list;
		return null;
	}

	public List<TraderFollowUser> findByTrader_partyId(String trader_partyId) {
		List<TraderFollowUser> list = (List<TraderFollowUser>) getHibernateTemplate().find(" FROM TraderFollowUser   WHERE trader_partyId = ? ",
				new Object[] { trader_partyId });
		if (list.size() > 0)
			return list;
		return null;
	}

	public List<TraderFollowUser> findByPartyId(String partyId) {
		List<TraderFollowUser> list = (List<TraderFollowUser>) getHibernateTemplate().find(" FROM TraderFollowUser   WHERE partyId = ? ",
				new Object[] { partyId });
		if (list.size() > 0)
			return list;
		return null;
	}

	public TraderFollowUser findByPartyIdAndTrader_partyId(String partyId, String trader_partyId) {
		List<TraderFollowUser> list = (List<TraderFollowUser>) getHibernateTemplate().find(
				" FROM TraderFollowUser   WHERE partyId= ? and trader_partyId = ? ",
				new Object[] { partyId, trader_partyId });
		if (list.size() > 0)
			return list.get(0);
		return null;
	}

	public TraderFollowUser findById(String id) {
		return (TraderFollowUser) getHibernateTemplate().get(TraderFollowUser.class, id);
	}

	public void setTraderService(TraderService traderService) {
		this.traderService = traderService;
	}

	public void setTraderUserService(TraderUserService traderUserService) {
		this.traderUserService = traderUserService;
	}

	public void setPagedQueryDao(PagedQueryDao pagedQueryDao) {
		this.pagedQueryDao = pagedQueryDao;
	}

}
