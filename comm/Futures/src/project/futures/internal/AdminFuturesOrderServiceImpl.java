package project.futures.internal;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

import kernel.util.StringUtils;
import kernel.web.Page;
import kernel.web.PagedQueryDao;
import project.futures.AdminFuturesOrderService;
import project.futures.FuturesOrder;
import project.futures.FuturesOrderService;
import project.futures.FuturesPara;
import project.party.recom.UserRecomService;

public class AdminFuturesOrderServiceImpl extends HibernateDaoSupport implements AdminFuturesOrderService {
	private PagedQueryDao pagedQueryDao;
	private UserRecomService userRecomService;
	private FuturesOrderService futuresOrderService;

	public Page pagedQuery(int pageNo, int pageSize, String status, String rolename, String loginPartyId,
			String username,String orderNo,String symbol,String direction,Double volume) {
		StringBuffer queryString = new StringBuffer("SELECT party.USERCODE usercode,party.ROLENAME rolename,orders.UUID id,orders.SYMBOL symbol,"
				+ "orders.ORDER_NO order_no,orders.TRADE_AVG_PRICE trade_avg_price,orders.DIRECTION direction,orders.PROFIT_LOSS profit_loss,"
				+ "orders.UNIT_AMOUNT unit_amount,orders.STATE state,FORMAT(orders.FEE,1) fee," + "orders.PROFIT profit,"
				+ "FORMAT(orders.VOLUME,1) volume,item.NAME itemname,orders.TIMENUM timenum,orders.TIMEUNIT timeunit,"
				+ "orders.CLOSE_AVG_PRICE close_avg_price,DATE_FORMAT(orders.CREATE_TIME,'%Y-%m-%d %H:%i:%s') create_time,DATE_FORMAT(orders.SETTLEMENT_TIME,'%Y-%m-%d %H:%i:%s')  settlement_time,");
//		queryString.append(" wallet.MONEY money,  ");
		queryString.append(" party.USERNAME username  ");

		queryString.append(
				" FROM T_FUTURES_ORDER orders LEFT JOIN PAT_PARTY party ON orders.PARTY_ID = party.UUID "
//						+ "LEFT JOIN T_WALLET wallet ON wallet.PARTY_ID = party.UUID  "
						+ "LEFT JOIN T_ITEM item ON orders.SYMBOL=item.SYMBOL WHERE 1 = 1 ");

		Map parameters = new HashMap();
		if (!StringUtils.isNullOrEmpty(loginPartyId)) {
			List children = this.userRecomService.findChildren(loginPartyId);
			if (children.size() == 0) {
				return Page.EMPTY_PAGE;
			}
			queryString.append(" and orders.PARTY_ID in (:children) ");
			parameters.put("children", children);
		}
		
		if (!StringUtils.isNullOrEmpty(symbol)) {
			queryString.append(" and orders.SYMBOL =  :symbol ");
			parameters.put("symbol", symbol);
		}
		if (!StringUtils.isNullOrEmpty(direction)) {
			queryString.append(" and orders.DIRECTION =  :direction ");
			parameters.put("direction", direction);
		}
		if (volume!=null&&volume>0d) {
			queryString.append(" and orders.VOLUME =  :volume ");
			parameters.put("volume", volume);
		}
		
		
		if (!StringUtils.isNullOrEmpty(status)) {
			queryString.append(" and orders.STATE =  :status ");
			parameters.put("status", status);
		}

		if (!StringUtils.isNullOrEmpty(rolename)) {
			queryString.append(" and party.ROLENAME = :rolename  ");
			parameters.put("rolename", rolename);

		}
//		if (!StringUtils.isNullOrEmpty(username)) {
//			queryString.append(" and party.username like:username  ");
//			parameters.put("username", "%"+username+"%");
//
//		}
		if (!StringUtils.isNullOrEmpty(orderNo)) {
			queryString.append(" and orders.ORDER_NO = :orderNo  ");
			parameters.put("orderNo", orderNo);
			
		}
		if (!StringUtils.isNullOrEmpty(username)) {
			queryString.append("AND (party.USERNAME like:username OR party.USERCODE like:username ) ");
			parameters.put("username","%"+username+"%");
		}
		queryString.append(" order by orders.CREATE_TIME desc ");
		Page page = this.pagedQueryDao.pagedQuerySQL(pageNo, pageSize, queryString.toString(), parameters);
		//预留对持仓单的处理
		handleDatas((List<Map<String,Object>>)page.getElements());
		return page;
	}
	
	private void handleDatas(List<Map<String,Object>> datas) {
		List<FuturesOrder> list = futuresOrderService.cacheSubmitted();
		Map<String,FuturesOrder> maps = new HashMap<String,FuturesOrder>();
		for(FuturesOrder order:list) {
			maps.put(order.getOrder_no(), order);
		}
		for(Map<String,Object> data:datas) {
			String timeUnitCn = FuturesPara.TIMENUM.valueOf(data.get("timeunit").toString()).getCn();
			FuturesOrder order = maps.get(data.get("order_no").toString());
			if(null!=order) {
				data.put("close_avg_price", new BigDecimal(order.getClose_avg_price().toString()).toPlainString());
				data.put("profit", new BigDecimal(new Double(order.getProfit()).toString()).toPlainString());
				data.put("remain_time", order.getRemain_time());
				data.put("profit_loss", order.getProfit_loss());
			}else {
				data.put("close_avg_price", new BigDecimal(data.get("close_avg_price").toString()).toPlainString());
				data.put("profit", new BigDecimal(data.get("profit").toString()).toPlainString());
				data.put("remain_time", "0:0:0");
			}
			data.put("timenum", data.get("timenum").toString()+timeUnitCn);
			data.put("trade_avg_price", new BigDecimal(data.get("trade_avg_price").toString()).toPlainString());
		}
	}
	
	
	public FuturesOrder get(String id) {
		return getHibernateTemplate().get(FuturesOrder.class, id);
	}

	public void setPagedQueryDao(PagedQueryDao pagedQueryDao) {
		this.pagedQueryDao = pagedQueryDao;
	}

	public void setUserRecomService(UserRecomService userRecomService) {
		this.userRecomService = userRecomService;
	}

	public void setFuturesOrderService(FuturesOrderService futuresOrderService) {
		this.futuresOrderService = futuresOrderService;
	}

	
}
