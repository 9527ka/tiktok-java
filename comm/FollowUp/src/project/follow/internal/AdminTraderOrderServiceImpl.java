package project.follow.internal;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

import kernel.web.Page;
import kernel.web.PagedQueryDao;
import project.follow.AdminTraderFollowUserOrderService;
import project.follow.AdminTraderOrderService;
import project.follow.TraderOrder;
import project.follow.TraderOrderService;

public class AdminTraderOrderServiceImpl extends HibernateDaoSupport
		implements AdminTraderOrderService {
	private PagedQueryDao pagedQueryDao;
	private TraderOrderService traderOrderService;

	public Page pagedQuery(int pageNo, int pageSize, String name, String username, String rolename) {
		StringBuffer queryString = new StringBuffer(" SELECT trader.NAME trader_name,"
				+ " party.USERNAME username,party.USERCODE usercode,party.ROLENAME rolename,"
				+ " trader_order.UUID id,trader_order.STATE state  ,  "
				+ " trader_order.VOLUME_OPEN  volume_open,trader_order.ORDER_NO order_no,  "
				+ " trader_order.CREATE_TIME create_time,trader_order.CLOSE_TIME close_time,   "
				+ " trader_order.TRADE_AVG_PRICE trade_avg_price,trader_order.close_avg_price CLOSE_AVG_PRICE, "
				+ "trader_order.CLOSE_TIME close_time,trader_order.CHANGE_RATIO change_ratio, "
				+ " trader_order.DIRECTION direction,trader_order.PROFIT profit,item.NAME itemname   ");
		queryString.append(" FROM T_TRADER_ORDER trader_order  ");
		queryString.append(" LEFT JOIN PAT_PARTY party ON  party.UUID  = trader_order.PARTY_ID  ");
		queryString.append(" LEFT JOIN T_TRADER trader ON  trader.PARTY_ID   = trader_order.PARTY_ID  ");
		queryString.append(" LEFT JOIN T_ITEM item ON trader_order.SYMBOL=item.SYMBOL  ");
		
		queryString.append("  WHERE 1 = 1 ");

		Map<String, Object> parameters = new HashMap();
		if (StringUtils.isNotEmpty(name)) {
			queryString.append(" AND trader.NAME =:name ");
			parameters.put("name", name);
		}
		if (StringUtils.isNotEmpty(rolename)) {
			queryString.append(" and party.ROLENAME = :rolename  ");
			parameters.put("rolename", rolename);

		}
		if (StringUtils.isNotEmpty(username)) {
			queryString.append("AND (party.USERNAME like:username OR party.USERCODE like:username ) ");
			parameters.put("username", "%" + username + "%");
		}

		queryString.append(" order by trader_order.CREATE_TIME desc ");
		Page page = this.pagedQueryDao.pagedQuerySQL(pageNo, pageSize, queryString.toString(), parameters);
		return page;
	}

	public void setPagedQueryDao(PagedQueryDao pagedQueryDao) {
		this.pagedQueryDao = pagedQueryDao;
	}

	@Override
	public void delete(String id) {
		traderOrderService.delete(id);
		
	}

	@Override
	public void update(TraderOrder entity) {
		traderOrderService.update(entity);
		
	}

	@Override
	public void save(TraderOrder entity) {
		traderOrderService.save(entity);
		
	}

	@Override
	public TraderOrder findById(String id) {
		return traderOrderService.findById(id);
	}

	public void setTraderOrderService(TraderOrderService traderOrderService) {
		this.traderOrderService = traderOrderService;
	}

	
}
