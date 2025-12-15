package project.futures.internal;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

import kernel.exception.BusinessException;
import kernel.util.Arith;
import kernel.util.DateUtils;
import kernel.util.StringUtils;
import project.futures.FuturesOrder;
import project.futures.FuturesOrderLocalService;
import project.futures.FuturesPara;
import project.futures.FuturesParaService;
import project.futures.FuturesRedisKeys;
import project.item.ItemService;
import project.item.model.Item;
import project.redis.RedisHandler;

public class FuturesOrderLocalServiceImpl extends HibernateDaoSupport implements FuturesOrderLocalService {
	protected ItemService itemService;
	protected RedisHandler redisHandler;
	protected FuturesParaService futuresParaService;

	public FuturesOrder cacheByOrderNo(String order_no) {
		FuturesOrder futuresOrder = (FuturesOrder) redisHandler
				.get(FuturesRedisKeys.FUTURES_SUBMITTED_ORDERNO + order_no);
//		FuturesOrder futuresOrder = cache.get(order_no);
		if (null == futuresOrder) {
			futuresOrder = findByOrderNo(order_no);
		}
		return futuresOrder;
	}

	public FuturesOrder findByOrderNo(String order_no) {

		StringBuffer queryString = new StringBuffer(" FROM FuturesOrder where order_no=?0");
		List<FuturesOrder> list = (List<FuturesOrder>) getHibernateTemplate().find(queryString.toString(), new Object[] { order_no });
		if (list.size() > 0) {
			return list.get(0);
		}
		return null;
	}

	public Map<String, Object> bulidOne(FuturesOrder order) {
//		FuturesOrder order_cache = cache.get(order.getOrder_no());
		FuturesOrder order_cache = (FuturesOrder) redisHandler
				.get(FuturesRedisKeys.FUTURES_SUBMITTED_ORDERNO + order.getOrder_no());
		if (order_cache != null) {
			order = order_cache;
		}

		List<FuturesPara> paras = futuresParaService.cacheGetBySymbol(order.getSymbol());
		double ratio_min = 0D;
		double ratio_max = 0D;
		for (int i = 0; i < paras.size(); i++) {
			if (paras.get(i).getTimeUnit().equals(order.getTimeUnit())
					&& paras.get(i).getTimeNum() == order.getTimeNum()) {
				ratio_min = paras.get(i).getProfit_ratio();
				ratio_max = paras.get(i).getProfit_ratio_max();
			}
		}

		Item item = this.itemService.cacheBySymbol(order.getSymbol(), false);
		if (item == null) {
			throw new BusinessException("参数错误");
		}
		String decimals = "#.";

		for (int i = 0; i < item.getDecimals(); i++) {
			decimals = decimals + "#";
		}
		if (item.getDecimals() == 0) {
			decimals = "#";
		}
		DecimalFormat df_symbol = new DecimalFormat(decimals);
		df_symbol.setRoundingMode(RoundingMode.FLOOR);// 向下取整

		DecimalFormat df = new DecimalFormat("#.##");
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("order_no", order.getOrder_no());
		map.put("name", item.getName());
		map.put("symbol", order.getSymbol());
		map.put("open_time", DateUtils.format(order.getCreate_time(), DateUtils.DF_yyyyMMddHHmmss));
		if (order.getClose_time() != null) {
			map.put("close_time", DateUtils.format(order.getClose_time(), DateUtils.DF_yyyyMMddHHmmss));
		} else {
			map.put("close_time", "--");
		}

		map.put("direction", order.getDirection());
		map.put("open_price", df_symbol.format(order.getTrade_avg_price()));
		map.put("state", order.getState());
		map.put("amount", order.getVolume());
		map.put("fee", order.getFee());
		/**
		 * 收益
		 */
		if (order.getProfit() > 0) {
//			if ("submitted".equals(order.getState())) {
//				map.put("profit", df.format(Arith.mul(order.getVolume(), ratio_min)) + " " + "~ "
//						+ df.format(Arith.mul(order.getVolume(), ratio_max)));
//			} else {
				map.put("profit", df.format(order.getProfit()));
//			}
			map.put("profit_state", "1");

		} else {
			map.put("profit", df.format(order.getProfit()));
			map.put("profit_state", "0");
		}

		map.put("volume", order.getVolume());

		map.put("settlement_time", DateUtils.format(order.getSettlement_time(), DateUtils.DF_yyyyMMddHHmmss));// 交割时间
		map.put("close_price", df_symbol.format(order.getClose_avg_price()));
		map.put("remain_time", StringUtils.isEmptyString(order.getRemain_time()) ? "0:0:0" : order.getRemain_time());
		map.put("time_num", order.getTimeNum());
		map.put("time_unit", order.getTimeUnit().substring(0, 1));
		return map;
	}

	public void setItemService(ItemService itemService) {
		this.itemService = itemService;
	}

	public void setRedisHandler(RedisHandler redisHandler) {
		this.redisHandler = redisHandler;
	}

	public void setFuturesParaService(FuturesParaService futuresParaService) {
		this.futuresParaService = futuresParaService;
	}

}
