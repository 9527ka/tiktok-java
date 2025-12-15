package project.miner.job;

import java.util.Date;
import java.util.List;

import kernel.web.Page;
import project.data.model.Realtime;
import project.miner.model.MinerOrder;

public interface MinerOrderProfitService {
	/**
	 * 分页获取计息中的矿机订单
	 * 
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	public Page pagedQueryComputeOrder(int pageNo, int pageSize);

	/**
	 * 计算订单收益
	 * 
	 * @param orders                 订单列表
	 * @param miner_profit_symbol    指定币种
	 * @param realtime               币种行情
	 * @param miner_bonus_parameters 推荐人收益参数
	 */
	public void saveComputeOrderProfit(List<MinerOrder> orders, String miner_profit_symbol, Realtime realtime,
			String miner_bonus_parameters);
	
	public void saveComputeOrderProfit(List<MinerOrder> orders, String miner_profit_symbol, Realtime realtime,
			String miner_bonus_parameters,Date systemTime);
	/**
	 * 推荐人收益清空
	 */
	public void cacheRecomProfitClear();

	/**
	 * 推荐人收益持久化数据库
	 */
	public void saveRecomProfit();
	public void saveRecomProfit(Date systemTime);
}
