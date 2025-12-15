package project.futures.job;

import project.futures.FuturesOrder;

/**
 * 交割合约盈亏计算
 */
public interface FuturesOrderCalculationService {

	/*
	 * 订单盈亏计算
	 */
	public void saveCalculation(FuturesOrder order);

}
