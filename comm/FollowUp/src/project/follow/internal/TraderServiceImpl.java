package project.follow.internal;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

import kernel.util.Arith;
import kernel.util.StringUtils;
import kernel.web.Page;
import kernel.web.PagedQueryDao;
import project.Constants;
import project.contract.ContractOrder;
import project.contract.ContractOrderService;
import project.follow.Trader;
import project.follow.TraderService;

public class TraderServiceImpl extends HibernateDaoSupport implements TraderService {
	private PagedQueryDao pagedQueryDao;
	private ContractOrderService contractOrderService;

	public Trader findById(String id) {
		return (Trader) getHibernateTemplate().get(Trader.class, id);
	}

	public Trader findByPartyId(String partyId) {
		List<Trader> list = (List<Trader>) getHibernateTemplate().find(" FROM Trader WHERE partyId = ?0", new Object[] { partyId });
		if (list.size() > 0)
			return list.get(0);
		return null;
	}

	public List<Map<String, Object>> getPaged(int pageNo, int pageSize, String name, String state,
			String orderBy_type) {

		StringBuffer queryString = new StringBuffer("");
		queryString.append(" FROM ");
		queryString.append(" Trader ");
		queryString.append(" where 1=1 ");

		Map<String, Object> parameters = new HashMap();

		if (!StringUtils.isNullOrEmpty(name)) {
			queryString.append(" and name like :name");
			parameters.put("name", "%" + name + "%");
		}

		if (!StringUtils.isNullOrEmpty(state)) {
			queryString.append(" and state =:state ");
			parameters.put("state", state);
		}
		if (!StringUtils.isNullOrEmpty(orderBy_type) && !"create_time".equals(orderBy_type)) {
			queryString.append(" order by (" + orderBy_type + "+ DEVIATION_" + orderBy_type + ") desc ");
//			parameters.put("orderBy_type", orderBy_type);
		} else {
			queryString.append(" order by create_time desc ");
		}

		Page page = this.pagedQueryDao.pagedQueryHql(pageNo, pageSize, queryString.toString(), parameters);
		List<Map<String, Object>> data = this.bulidData(page.getElements());
		return data;

	}

	
	private List<Map<String, Object>> bulidData(List<Trader> traders) {
		List<Map<String, Object>> result_traders = new ArrayList();
		DecimalFormat df2 = new DecimalFormat("#.##");
		df2.setRoundingMode(RoundingMode.FLOOR);// 向下取整
		if (traders == null) {
			return result_traders;
		}
		for (int i = 0; i < traders.size(); i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			Trader entity = traders.get(i);
			String path = Constants.WEB_URL + "/public/showimg!showImg.action?imagePath=" + entity.getImg();
			map.put("img", path);
			map.put("id", entity.getId());
			map.put("partyId", entity.getPartyId());
			map.put("name", entity.getName());
			map.put("remarks", entity.getRemarks());
//			map.put("symbols", "btc;eth");
			map.put("symbol_name", entity.getSymbols());
			map.put("follower_max", entity.getFollower_max());

			/**
			 * 近3周收益week_3_profit
			 */
			map.put("week_3_profit",
					df2.format(Arith.add(entity.getWeek_3_profit(), entity.getDeviation_week_3_profit())));
			/**
			 * 近3周累计金额week_3_order_amount
			 */
			map.put("week_3_order_amount",
					df2.format(Arith.add(entity.getWeek_3_order_amount(), entity.getDeviation_week_3_order_amount())));

			/**
			 * 近3周盈利笔数week_3_order_profit
			 */
			map.put("week_3_order_profit",
					df2.format(Arith.add(entity.getWeek_3_order_profit(), entity.getDeviation_week_3_order_profit())));
			/**
			 * 近3周交易笔数week_3_order_sum
			 */
			map.put("week_3_order_sum",
					df2.format(Arith.add(entity.getWeek_3_order_sum(), entity.getDeviation_week_3_order_sum())));

			/**
			 * 累计金额order_amount
			 */
			map.put("order_amount",
					df2.format(Arith.add(entity.getOrder_amount(), entity.getDeviation_order_amount())));

//			map.put("symbol_name", "BTC/USDT;ETH/USDT");
			map.put("profit", df2.format(Arith.add(entity.getProfit(), entity.getDeviation_profit())));

			map.put("order_profit", (int) Arith.add(entity.getOrder_profit(), entity.getDeviation_order_profit()));

			map.put("order_loss", (int) Arith.add(entity.getOrder_loss(), entity.getDeviation_order_loss()));
			map.put("order_sum", (int) Arith.add(Arith.add(entity.getOrder_profit(), entity.getOrder_loss()),
					Arith.add(entity.getDeviation_order_profit(), entity.getDeviation_order_loss())));
			map.put("follower_sum", (int) Arith.add(entity.getFollower_sum(), entity.getDeviation_follower_sum()));

			map.put("follower_now", (int) Arith.add(entity.getFollower_now(), entity.getDeviation_follower_now()));
			// Deviation_w

			/**
			 * 近3周收益率 = 近3周收益/近3周累计金额 + 近3周收益率偏差值
			 */
			map.put("week_3_profit_ratio",
					df2.format(Arith.add(Arith.mul(entity.getDeviation_week_3_profit_ratio(), 100),
							Arith.mul(entity.getWeek_3_profit_ratio(), 100))));
			/**
			 * 近3周胜率 week_3_order_profit_ratio
			 */
			double week_3_order_profit_ratio = 0;
			if(Arith.add(entity.getWeek_3_order_sum(), entity.getDeviation_week_3_order_sum()) == 0) {
				week_3_order_profit_ratio = 0;
			}else {
				week_3_order_profit_ratio = Arith.mul(
						Arith.div(Arith.add(entity.getWeek_3_order_profit(), entity.getDeviation_week_3_order_profit()),
								Arith.add(entity.getWeek_3_order_sum(), entity.getDeviation_week_3_order_sum())),
						100);
			}
			
			if (week_3_order_profit_ratio > 100) {
				week_3_order_profit_ratio = 100;
			}
			if (week_3_order_profit_ratio < 0) {
				week_3_order_profit_ratio = 0;
			}

			map.put("week_3_order_profit_ratio", df2.format(week_3_order_profit_ratio));

			/**
			 * 累计收益率
			 */
			map.put("profit_ratio", df2.format(Arith.add(Arith.mul(entity.getDeviation_profit_ratio(), 100),
					Arith.mul(entity.getProfit_ratio(), 100))));

			map.put("profit_share_ratio", df2.format(Arith.mul(entity.getProfit_share_ratio(), 100)));

			result_traders.add(map);
		}

		return result_traders;

	}
	
	
	public void update(Trader entity) {
		this.getHibernateTemplate().update(entity);
	}
	
	public void updateTrader(Trader entity) {
		List<ContractOrder> orders =  contractOrderService.findSubmitted(entity.getPartyId().toString(),"","");
		double week_3_profit = 0;
		double week_3_order_amount = 0;;
		if(orders != null) {
			Date date_now = new Date();// 取时间
			for(ContractOrder order : orders) {
				double last_days = 22;
				try {
					last_days = daysBetween(date_now, order.getCreate_time());
				} catch (ParseException e) {
					e.printStackTrace();
				}
				if (last_days <= 21 ) {
					week_3_profit = Arith.add(week_3_profit, order.getProfit());
					week_3_order_amount = Arith.add(week_3_order_amount, Arith.mul(order.getVolume_open(), order.getUnit_amount()));
				} 
			}
			if(week_3_order_amount != 0) {
				entity.setWeek_3_profit_ratio(Arith.div(week_3_profit, week_3_order_amount));
			}else {
				entity.setWeek_3_profit_ratio(0);
			}
		}
		if( entity.getOrder_amount() != 0) {
			entity.setProfit_ratio(Arith.div(entity.getProfit(), entity.getOrder_amount()));
		}else {
			entity.setProfit_ratio(0);
		}
		
		update(entity);
	}
	
	
	public static int daysBetween(Date smdate, Date bdate) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		smdate = sdf.parse(sdf.format(smdate));
		bdate = sdf.parse(sdf.format(bdate));
		Calendar cal = Calendar.getInstance();
		cal.setTime(smdate);
		long time1 = cal.getTimeInMillis();
		cal.setTime(bdate);
		long time2 = cal.getTimeInMillis();
		long between_days = (time2 - time1) / (1000 * 3600 * 24);

		return Integer.parseInt(String.valueOf(between_days));
	}


	public void setPagedQueryDao(PagedQueryDao pagedQueryDao) {
		this.pagedQueryDao = pagedQueryDao;
	}

	public void setContractOrderService(ContractOrderService contractOrderService) {
		this.contractOrderService = contractOrderService;
	}
	

}
