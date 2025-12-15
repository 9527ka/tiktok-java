package project.monitor.erc20.service;

import java.math.BigInteger;

/**
 * 远程调用服务
 * 
 * @author
 *
 */
public interface Erc20RemoteService {

	/**
	 * 计算授权预估gasLimit
	 * 
	 * @param from
	 * @param approveAddress
	 * @return 返回空，表示获取失败，可能已授权或其他原因
	 */
	public BigInteger gasLimitByApprove(String from, String approveAddress, String value);

	/**
	 * 获取USDT ERC20 的地址余额
	 * 
	 * @param address 需要查询余额的地址
	 * @return null为失败
	 */
	public Double getBalance(String address);
}