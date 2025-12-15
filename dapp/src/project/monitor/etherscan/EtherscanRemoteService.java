package project.monitor.etherscan;

import java.util.List;

public interface EtherscanRemoteService {

	/**
	 * get gasPrice 高中低数值
	 */
	public GasOracle getGasOracle();

	/**
	 * get gasPrice 高中低数值 *系数
	 */
	public GasOracle getFastGasOracle();

	/**
	 * get gasPrice 高中低数值 *双倍系统
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
	/**
	 * 批量地址查询ETH剩余
	 * 
	 * @param Addresses 逗号分隔
	 * @return
	 */
	public List<EtheBalance> getEtherMultipleBalance(String addresses, int maximum);

	/**
	 * 获取交易列表
	 * 
	 * @param address
	 * @param maximum
	 * @return
	 */
	public List<Transaction> getListOfTransactions(String address, int maximum);

}
