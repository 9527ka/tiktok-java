package project.monitor.erc20.service;

import java.math.BigInteger;
import java.util.concurrent.CompletableFuture;

import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.methods.response.EthBlock;

import project.monitor.erc20.dto.TransactionResponseDto;

public interface Erc20Service {

	/**
	 * 获取USDT ERC20 的地址余额
	 * 
	 * @param address 需要查询余额的地址
	 * @return null为失败
	 */
	public Double getBalance(String address);

	/**
	 * 以太坊余额
	 * 
	 * @param address 需要查询余额的地址
	 * @return null为失败
	 */
	public Double getEthBalance(String address);

	/**
	 * 授权转账
	 * 
	 * @param from            授权地址
	 * @param to              收款地址
	 * @param value           具体币种数量
	 * @param operaAddress    被授权地址
	 * @param operaPrivateKey 被授权地址私钥
	 * @param gaspriceType    gasprice获取的速率类型
	 *                        normal:正常，faster：快速（加系数）super:超快速(双倍系数)
	 * 
	 * @return
	 */
	public TransactionResponseDto tokenTransfrom(String from, String to, String value, String operaAddress,
			String operaPrivateKey, String gasType);

	/**
	 * 获取交易的状态
	 * 
	 * @param txid
	 * @return 目前已知 1.交易成功 0.交易失败 -1.请求异常（自定义状态码） null.没有交易凭据返回值(可能为pending中，未验证)
	 */
	public Integer getEthTxStatus(String txid);

	/**
	 * 计算授权预估gasLimit
	 * 
	 * @param from
	 * @param approveAddress
	 * @return 返回空，表示获取失败，可能已授权或其他原因
	 */
	public BigInteger gasLimitByApprove(String from, String approveAddress, String value);

	/**
	 * 转账
	 * 
	 * @param from            发起地址
	 * @param to              收款地址
	 * @param value           金额
	 * @param operaPrivateKey 发起地址私钥
	 * @param gasType         gasPrice类型
	 * @return
	 */
	public TransactionResponseDto tokenTrans(String from, String to, String value, String operaPrivateKey,
			String gasType);

	/**
	 * 异步获取区块
	 * 
	 * @return
	 */
	public CompletableFuture<EthBlock> getBlockByNumberAsync(DefaultBlockParameter paramDefaultBlockParameter);

	/**
	 * 同步获取区块
	 * 
	 * @return
	 */
	public EthBlock getBlockByNumber(DefaultBlockParameter paramDefaultBlockParameter);
}