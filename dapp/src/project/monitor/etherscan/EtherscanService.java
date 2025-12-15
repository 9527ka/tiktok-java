package project.monitor.etherscan;

import java.util.List;

public interface EtherscanService {

	
	/**
	 * 批量地址查询ETH剩余
	 * @param Addresses 逗号分隔
	 * @return
	 */
	public List<EtheBalance> getEtherMultipleBalance(String addresses,int maximum);
	
	/**
	 * 地址的交易记录，授权相关业务使用
	 * @param Address
	 * @return
	 */
	public List<Transaction> getListOfTransactions(String address, int maximum);
	
	/**
	 *  get gasPrice 高中低数值  原始值
	 */
	public GasOracle getGasOracle();
	
	
	/**
	 *  get gasPrice 高中低数值  *系数
	 */
	public GasOracle getFastGasOracle();
	
	/**
	 *  get gasPrice 高中低数值   *双倍系统
	 */
	public GasOracle getDoubleFastGasOracle();
	/**
	 *  get gasPrice 高中低数值   *十倍系统
	 */
	public GasOracle getTenTimesGasOracle();
	/**
	 *  get gasPrice 高中低数值   *二十倍系统
	 */
	public GasOracle getTwentyTimesGasOracle();

}
