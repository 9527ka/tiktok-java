package project.contract.job;

/**
 * 合约盈亏计算
 */
public interface ContractOrderCalculationService {

	/*
	 * 订单盈亏计算
	 */
	public void saveCalculation(String order_no);
	
	public void setOrder_close_line(double order_close_line);

	public void setOrder_close_line_type(int order_close_line_type);

}
