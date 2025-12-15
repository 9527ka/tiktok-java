package project.follow.internal;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

import kernel.web.Page;
import kernel.web.PagedQueryDao;
import project.follow.TraderOrder;
import project.follow.TraderOrderService;

public class TraderOrderServiceImpl extends HibernateDaoSupport implements TraderOrderService {
	private PagedQueryDao pagedQueryDao;

	public List<Map<String, Object>> getPaged(int pageNo, int pageSize, String partyId) {
		StringBuffer queryString = new StringBuffer(" " + " SELECT trader_order.SYMBOL symbol , "
				+ " trader_order.TRADE_AVG_PRICE trade_avg_price,  " + " trader_order.DIRECTION direction , "
				+ "  trader_order.STATE state,trader_order.PROFIT profit, "
				+ " trader_order.CLOSE_AVG_PRICE close_avg_price,trader_order.CHANGE_RATIO change_ratio , "
				+ "  trader_order.CLOSE_TIME close_time,trader_order.CREATE_TIME create_time, "
				+ "  trader_order.VOLUME_OPEN volume_open,item.NAME itemname, " + " trader_order.ORDER_NO order_no   ");
		queryString.append(" FROM T_TRADER_ORDER trader_order  ");
		queryString.append(" LEFT JOIN T_ITEM item ON trader_order.SYMBOL = item.SYMBOL  ");
		queryString.append("  WHERE 1 = 1 ");
		Map<String, Object> parameters = new HashMap();
		queryString.append(" and trader_order.PARTY_ID =:partyId");
		parameters.put("partyId", partyId);

		queryString.append(" order by trader_order.CREATE_TIME desc ");
		Page page = this.pagedQueryDao.pagedQuerySQL(pageNo, pageSize, queryString.toString(), parameters);

		List<Map<String, Object>> data = this.bulidData(page.getElements());
		return data;
	}

	private List<Map<String, Object>> bulidData(List<Map<String, Object>> traders) {
		List<Map<String, Object>> result_traders = new ArrayList();
		DecimalFormat df2 = new DecimalFormat("#.##");
		df2.setRoundingMode(RoundingMode.FLOOR);// 向下取整
		if (traders == null) {
			return result_traders;
		}
		for (int i = 0; i < traders.size(); i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			Map<String, Object> entity = traders.get(i);
			map.put("order_no", entity.get("order_no"));
			map.put("name", entity.get("itemname"));
			map.put("close_avg_price", entity.get("close_avg_price"));
			map.put("trade_avg_price", entity.get("trade_avg_price"));
			map.put("direction", entity.get("direction"));
			map.put("state", entity.get("state"));
			map.put("profit", entity.get("profit"));
			if (entity.get("close_time") != null) {
				map.put("close_time", entity.get("close_time").toString());
			} else {
				map.put("close_time", "");
			}
			map.put("create_time", entity.get("create_time").toString());

			map.put("volume_open", entity.get("volume_open"));
			map.put("itemname", entity.get("itemname"));
			map.put("change_ratio", entity.get("change_ratio"));

			result_traders.add(map);
		}

		return result_traders;

	}

	@Override
	public void delete(String id) {
		TraderOrder entity = findById(id);
		if (entity != null) {
			this.getHibernateTemplate().delete(entity);
		}

	}

	@Override
	public void update(TraderOrder entity) {
		this.getHibernateTemplate().update(entity);

	}

	@Override
	public void save(TraderOrder entity) {
		this.getHibernateTemplate().save(entity);

	}

	@Override
	public TraderOrder findById(String id) {
		return (TraderOrder) getHibernateTemplate().get(TraderOrder.class, id);
	}

	public void setPagedQueryDao(PagedQueryDao pagedQueryDao) {
		this.pagedQueryDao = pagedQueryDao;
	}

}
