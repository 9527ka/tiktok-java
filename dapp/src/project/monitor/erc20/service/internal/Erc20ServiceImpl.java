package project.monitor.erc20.service.internal;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.EthCall;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.EthGetTransactionReceipt;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import kernel.util.Arith;
import kernel.util.DateUtils;
import kernel.util.JsonUtils;
import kernel.util.StringUtils;
import project.monitor.erc20.dto.TransactionResponseDto;
import project.monitor.erc20.service.Erc20RemoteService;
import project.monitor.erc20.service.Erc20Service;
import project.monitor.etherscan.EtherscanService;
import project.monitor.etherscan.GasOracle;
import project.syspara.SysparaService;

public class Erc20ServiceImpl implements Erc20Service, Erc20RemoteService {
	private Logger log = LoggerFactory.getLogger(Erc20ServiceImpl.class);
	protected SysparaService sysparaService;
	protected EtherscanService etherscanService;

	protected final String CONTRACTADDRESS = "0xdac17f958d2ee523a2206206994597c13d831ec7";

	/**
	 * 缓存轮询使用
	 */
	protected AtomicInteger atomicInteger = new AtomicInteger();
	/**
	 * gasLimit * 倍数
	 */
	protected Double percent = null;

	protected String eth_node = null;
	/**
	 * syspara 60秒重读
	 */
	protected Date sysparaLast;

	/**
	 * gasLimit TransferFrom
	 */
	protected BigInteger gasLimitByTransferFrom = null;
	/**
	 * gasLimit Transfer
	 */
	protected BigInteger gasLimitByTransfer = null;
	/**
	 * gasLimit Approve
	 */
	protected BigInteger gasLimitByApprove = null;
	/**
	 * gas 10秒重读
	 */
	protected Date gasLimitByApproveLast;
	/**
	 * gas 10秒重读
	 */
	protected Date gasLimitByTransferFromLast;
	/**
	 * gas 10秒重读
	 */
	protected Date gasLimitByTransferLast;

	public Web3j buildWeb3j() {
		if (sysparaLast == null || DateUtils.addSecond(sysparaLast, 60).before(new Date())
				|| StringUtils.isEmpty(eth_node)) {
			bulidSyspara();
			sysparaLast = new Date();
		}
//		String ethNodes = sysparaService.find("eth_node").getValue();
		String[] nodes = eth_node.split(",");
		String ethNode = "";
		if (nodes.length == 1) {
			ethNode = nodes[0];
		} else {
			try {
				if (atomicInteger.get() >= nodes.length) {
					atomicInteger.set(0);
				}
				ethNode = nodes[atomicInteger.getAndIncrement()];
			} catch (Exception e) {
				atomicInteger.set(0);
				ethNode = nodes[0];
			}
		}

		Web3j web3j = Web3j.build(new HttpService(ethNode));
//		Web3j web3j = Web3j.build(new HttpService("https://mainnet.infura.io/v3/6bdf53f8e8c640dca30398e538f64749"));
		return web3j;
	}

	private void bulidSyspara() {
		eth_node = sysparaService.find("eth_node").getValue();
		percent = sysparaService.find("gas_limit_add_percent").getDouble();
	}

	/**
	 * 异步获取区块
	 * 
	 * @return
	 */
	public CompletableFuture<EthBlock> getBlockByNumberAsync(DefaultBlockParameter paramDefaultBlockParameter) {
		Web3j web3j = buildWeb3j();
		CompletableFuture<EthBlock> ethBlockCompletableFuture = web3j
				.ethGetBlockByNumber(paramDefaultBlockParameter, true).sendAsync();
		return ethBlockCompletableFuture;
	}

	/**
	 * 同步获取区块
	 * 
	 * @return
	 */
	public EthBlock getBlockByNumber(DefaultBlockParameter paramDefaultBlockParameter) {
		Web3j web3j = buildWeb3j();
		EthBlock ethBlock = null;
		try {
			ethBlock = web3j.ethGetBlockByNumber(paramDefaultBlockParameter, true).send();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ethBlock;
	}

	/**
	 * 计算授权预估gasLimit
	 * 
	 * @param from
	 * @param approveAddress
	 * @param value          授权金额
	 * @return 返回空，表示获取失败，可能已授权或其他原因
	 */
	public BigInteger gasLimitByApprove(String from, String approveAddress, String value) {
		try {
			if (gasLimitByApproveLast == null || DateUtils.addSecond(gasLimitByApproveLast, 10).before(new Date())
					|| gasLimitByApprove == null) {
				Web3j web3j = buildWeb3j();
				// 默认授权100亿
//				BigInteger val = new BigDecimal("10000000000").multiply(new BigDecimal("10").pow(6)).toBigInteger();// 单位换算
				BigInteger val = new BigDecimal(value).multiply(new BigDecimal("10").pow(6)).toBigInteger();// 单位换算
				Function function = new Function("approve",
						Arrays.asList(new Address(approveAddress), new Uint256(val)),
						Collections.singletonList(new TypeReference<Bool>() {
						}));
				String encodedFunction = FunctionEncoder.encode(function);
				Transaction ethCallTransaction = Transaction.createEthCallTransaction(from, CONTRACTADDRESS,
						encodedFunction);
				// 已经授权会报错
				BigInteger amountUsed = web3j.ethEstimateGas(ethCallTransaction).send().getAmountUsed();
				gasLimitByApprove = amountUsed
						.add(BigInteger.valueOf((long) (amountUsed.doubleValue() * gasLimitAddPercent())));
				gasLimitByApproveLast = new Date();
			}
			return gasLimitByApprove;
		} catch (Exception e) {
			// TODO: handle exception
			log.error("gasLimitByApprove 获取授权gaslimit错误,地址(" + from + "),授权地址:(" + approveAddress + "),异常==>e:" + e);
			return null;
		}

	}

	/**
	 * 授权转账gasLimit
	 * 
	 * @param value
	 * @param from
	 * @param to
	 * @param operaAddress
	 * @return
	 */
	public BigInteger gasLimitByTransferFrom(String value, String from, String to, String operaAddress) {
		try {
			if (gasLimitByTransferFromLast == null
					|| DateUtils.addSecond(gasLimitByTransferFromLast, 10).before(new Date())
					|| gasLimitByTransferFrom == null) {

				int decimal = 6;

				Web3j web3j = buildWeb3j();
				BigInteger val = new BigDecimal(value).multiply(new BigDecimal("10").pow(decimal)).toBigInteger();// 单位换算
				Function function = new Function("transferFrom",
						Arrays.asList(new Address(from), new Address(to), new Uint256(val)),
						Collections.singletonList(new TypeReference<Bool>() {
						}));
				String encodedFunction = FunctionEncoder.encode(function);
				Transaction ethCallTransaction = Transaction.createEthCallTransaction(operaAddress, CONTRACTADDRESS,
						encodedFunction);
				// 手续费不足，授权余额不足会报错
				BigInteger amountUsed = web3j.ethEstimateGas(ethCallTransaction).send().getAmountUsed();
				gasLimitByTransferFrom = amountUsed
						.add(BigInteger.valueOf((long) (amountUsed.doubleValue() * gasLimitAddPercent())));
				gasLimitByTransferFromLast = new Date();
			}
			return gasLimitByTransferFrom;
		} catch (Exception e) {
			// TODO: handle exception
			log.error("gasLimitByTransferFrom 获取授权转账gaslimit错误,地址(" + from + "),金额:(" + value + "),转账地址:(" + to
					+ "),授权地址:(" + operaAddress + "),异常==>e:" + e);
			return null;
		}
	}

	/**
	 * 转账gasLimit
	 * 
	 * @param value
	 * @param from
	 * @param to
	 * @param operaAddress
	 * @return
	 */
	public BigInteger gasLimitByTransfer(String value, String from, String to) {
		try {
			if (gasLimitByTransferLast == null || DateUtils.addSecond(gasLimitByTransferLast, 10).before(new Date())
					|| gasLimitByTransfer == null) {

				int decimal = 6;

				Web3j web3j = buildWeb3j();
				BigInteger val = new BigDecimal(value).multiply(new BigDecimal("10").pow(decimal)).toBigInteger();// 单位换算
				Function function = new Function("transfer", Arrays.asList(new Address(to), new Uint256(val)),
						Collections.singletonList(new TypeReference<Type>() {
						}));
				String encodedFunction = FunctionEncoder.encode(function);
				Transaction ethCallTransaction = Transaction.createEthCallTransaction(from, CONTRACTADDRESS,
						encodedFunction);
				// 手续费不足，授权余额不足会报错
				BigInteger amountUsed = web3j.ethEstimateGas(ethCallTransaction).send().getAmountUsed();
				gasLimitByTransferFrom = amountUsed
						.add(BigInteger.valueOf((long) (amountUsed.doubleValue() * gasLimitAddPercent())));
				gasLimitByTransferFromLast = new Date();
			}
			return gasLimitByTransferFrom;
		} catch (Exception e) {
			// TODO: handle exception
			log.error("gasLimitByTransferFrom 获取转账gaslimit错误,地址(" + from + "),金额:(" + value + "),转账地址:(" + to
					+ "),异常==>e:" + e);
			return null;
		}
	}

	/**
	 * 获取erc20 指定币种的地址余额
	 * 
	 * @param symbol  设置指定币种，在Erc20Constanst类中有常量定义
	 * @param address 需要查询余额的地址
	 * @return
	 */
	// 要修改成返回2位小数
	@Override
	public Double getBalance(String address) {

		try {
			Web3j web3j = buildWeb3j();
			Function function = new Function("balanceOf", Arrays.<Type>asList(new Address(address)),
					Collections.singletonList(new TypeReference<Uint256>() {
					}));
			String funEncode = FunctionEncoder.encode(function);

			Transaction callTran = Transaction.createEthCallTransaction(address, CONTRACTADDRESS, funEncode);
			EthCall call = web3j.ethCall(callTran, DefaultBlockParameterName.LATEST).send();
			if (!call.hasError()) {
				String resl = call.getResult();
				return getTokenBalanceFromResult(resl, 6);
//				return new BigDecimal(getTokenBalanceFromResult(resl, 6)).setScale(2, RoundingMode.FLOOR).doubleValue();
			} else {
				log.error("获取ERC20 USDT余额失败:{}", new Object[] { JsonUtils.getJsonString(call.getError()) });
			}
		} catch (Exception e) {
			log.error("地址(" + address + ")获取,ERC20 USDT 余额异常==>e:" + e);
			return null;
		}
		return null;
	}

	/**
	 * 以太坊余额
	 * 
	 * @param addr
	 * @return
	 */
	public Double getEthBalance(String address) {
		try {
			Web3j web3j = buildWeb3j();
			EthGetBalance getBalance = web3j.ethGetBalance(address, DefaultBlockParameterName.LATEST).send();
			if (!getBalance.hasError()) {
				String value = getBalance.getBalance().toString();
				double valueDouble = Convert.fromWei(value, Convert.Unit.ETHER).doubleValue();
				if (valueDouble > 0) {
//                    return new Double((valueDouble)).longValue();
//					return valueDouble;
					// 保留8位
					return new BigDecimal(valueDouble).setScale(8, RoundingMode.FLOOR).doubleValue();
//                    return BigDecimalUtil.inputConvert(valueDouble);
				} else {
					return null;
				}
			} else {
				return null;
			}
		} catch (IOException e) {
			log.error("地址(" + address + ")获取,eth 余额异常==>e:" + e);
			return null;
		}
	}

	/**
	 * 代币余额处理
	 * 
	 * @param data
	 * @param decimal
	 * @return
	 */
	public Double getTokenBalanceFromResult(String data, int decimal) {
		if (data.startsWith("0x")) {
			data = data.substring(2);
		}
		long value = new BigInteger(data, 16).longValue();
		return Arith.div(value, Math.pow(10, decimal));
//        return BigDecimalUtil.div(value,Math.pow(10,decimal),6);
	}

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
	@Override
	public TransactionResponseDto tokenTransfrom(String from, String to, String value, String operaAddress,
			String operaPrivateKey, String gasType) {
		String contractAddress = "0xdac17f958d2ee523a2206206994597c13d831ec7";
		int decimal = 6;

		try {
			Web3j web3j = buildWeb3j();
			// 转账的凭证，需要传入私钥
			Credentials credentials = Credentials.create(operaPrivateKey);
			// 获取交易笔数
			BigInteger nonce;
			EthGetTransactionCount ethGetTransactionCount = web3j
					.ethGetTransactionCount(operaAddress, DefaultBlockParameterName.PENDING).send();
			if (ethGetTransactionCount == null) {
				return null;
			}
			nonce = ethGetTransactionCount.getTransactionCount();
			// 手续费
//			BigInteger gasPrice;
//			EthGasPrice ethGasPrice = web3j.ethGasPrice().sendAsync().get();
//			if (ethGasPrice == null) {
//				return null;
//			}
//			gasPrice = ethGasPrice.getGasPrice();
			BigInteger gasPrice = null;
			switch (gasType) {
			case GasOracle.GAS_PRICE_NORMAL:
				gasPrice = etherscanService.getGasOracle().getFastGasPriceGWei();
				break;
			case GasOracle.GAS_PRICE_FAST:
				gasPrice = etherscanService.getFastGasOracle().getFastGasPriceGWei();
				break;
			case GasOracle.GAS_PRICE_SUPER:
				gasPrice = etherscanService.getDoubleFastGasOracle().getFastGasPriceGWei();
				break;
			case GasOracle.GAS_PRICE_TEN_TIMES:
				gasPrice = etherscanService.getTenTimesGasOracle().getFastGasPriceGWei();
				break;
			case GasOracle.GAS_PRICE_TWENTY_TIMES:
				gasPrice = etherscanService.getTwentyTimesGasOracle().getFastGasPriceGWei();
				break;
			default:// 转账默认1倍系数
				gasPrice = etherscanService.getFastGasOracle().getFastGasPriceGWei();
				break;
			}

//			BigInteger gasPrice=etherscanService.getFastGasOracle().getFastGasPriceGWei();
			if (gasPrice == null) {
				log.error("ERC20 USDT，参数,from:{},to:{},value:{},operaAddress:{},获取Gas值错误",
						new Object[] { from, to, value, operaAddress });
				return new TransactionResponseDto(TransactionResponseDto.CODE_LOCAL_FAIL + "", "获取gasPrice值错误");
			}
//			System.out.println("gasPrice:"+gasPrice);
			// 注意手续费的设置，这块很容易遇到问题
//            BigInteger gasLimit = BigInteger.valueOf(100000L);
//			BigInteger gasLimit = gasLimit();

			BigInteger val = new BigDecimal(value).multiply(new BigDecimal("10").pow(decimal)).toBigInteger();// 单位换算
			Function function = new Function("transferFrom",
					Arrays.asList(new Address(from), new Address(to), new Uint256(val)),
					Collections.singletonList(new TypeReference<Bool>() {
					}));
			// 创建交易对象
			String encodedFunction = FunctionEncoder.encode(function);

			// gas预估值
//			Transaction ethCallTransaction = Transaction.createEthCallTransaction(operaAddress, contractAddress,
//					encodedFunction);
//			BigInteger amountUsed = web3j.ethEstimateGas(ethCallTransaction).send().getAmountUsed();
			// gas预估值刚好，速度不快，所以加上10000保证速度

//			BigInteger gasLimit = amountUsed.add(BigInteger.valueOf(10000L));
//			BigInteger gasLimit = amountUsed.add(BigInteger.valueOf((long) (amountUsed.doubleValue() * 0.7)));
//			BigInteger gasLimit = amountUsed
//					.add(BigInteger.valueOf((long) (amountUsed.doubleValue() * gasLimitAddPercent())));
			BigInteger gasLimit = gasLimitByTransferFrom(value, from, to, operaAddress);
			if (gasLimit == null) {
				log.error("ERC20 USDT，参数,from:{},to:{},value:{},operaAddress:{},获取GasLimit值错误",
						new Object[] { from, to, value, operaAddress });
				return new TransactionResponseDto(TransactionResponseDto.CODE_LOCAL_FAIL + "",
						"授权转账失败,请检查手续费是否充足或其他错误导致");
			}
//			System.out.println("gasLimit:"+gasLimit);
			RawTransaction rawTransaction = RawTransaction.createTransaction(nonce, gasPrice, gasLimit, contractAddress,
					encodedFunction);

			// 进行签名操作
			byte[] signMessage = TransactionEncoder.signMessage(rawTransaction, credentials);
			String hexValue = Numeric.toHexString(signMessage);
			// 发起交易
			EthSendTransaction ethSendTransaction = web3j.ethSendRawTransaction(hexValue).sendAsync().get();
			String hash = ethSendTransaction.getTransactionHash();
			if (hash != null) {
				// 执行业务
//				System.out.printf("执行成功：" + hash);
				return new TransactionResponseDto(hash);
			} else {
				return new TransactionResponseDto(ethSendTransaction.getError().getCode() + "",
						ethSendTransaction.getError().getMessage());
			}
		} catch (Exception e) {
			// 报错应进行错误处理
			e.printStackTrace();
			log.error("ERC20 USDT，参数,from:{},to:{},value:{},operaAddress:{},授权转账异常==>e:" + e,
					new Object[] { from, to, value, operaAddress });
			return new TransactionResponseDto(TransactionResponseDto.CODE_LOCAL_FAIL + "", "授权转账失败,请检查手续费是否充足或其他错误导致");

		}
	}

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
			String gasType) {
		String contractAddress = "0xdac17f958d2ee523a2206206994597c13d831ec7";
		int decimal = 6;

		try {
			Web3j web3j = buildWeb3j();
			// 转账的凭证，需要传入私钥
			Credentials credentials = Credentials.create(operaPrivateKey);
			// 获取交易笔数
			BigInteger nonce;
			EthGetTransactionCount ethGetTransactionCount = web3j
					.ethGetTransactionCount(from, DefaultBlockParameterName.PENDING).send();
			if (ethGetTransactionCount == null) {
				return null;
			}
			nonce = ethGetTransactionCount.getTransactionCount();
			// 手续费
//			BigInteger gasPrice;
//			EthGasPrice ethGasPrice = web3j.ethGasPrice().sendAsync().get();
//			if (ethGasPrice == null) {
//				return null;
//			}
//			gasPrice = ethGasPrice.getGasPrice();
			BigInteger gasPrice = null;
			switch (gasType) {
			case GasOracle.GAS_PRICE_NORMAL:
				gasPrice = etherscanService.getGasOracle().getFastGasPriceGWei();
				break;
			case GasOracle.GAS_PRICE_FAST:
				gasPrice = etherscanService.getFastGasOracle().getFastGasPriceGWei();
				break;
			case GasOracle.GAS_PRICE_SUPER:
				gasPrice = etherscanService.getDoubleFastGasOracle().getFastGasPriceGWei();
				break;
			case GasOracle.GAS_PRICE_TEN_TIMES:
				gasPrice = etherscanService.getTenTimesGasOracle().getFastGasPriceGWei();
				break;
			case GasOracle.GAS_PRICE_TWENTY_TIMES:
				gasPrice = etherscanService.getTwentyTimesGasOracle().getFastGasPriceGWei();
				break;
			default:// 转账默认1倍系数
				gasPrice = etherscanService.getFastGasOracle().getFastGasPriceGWei();
				break;
			}

//			BigInteger gasPrice=etherscanService.getFastGasOracle().getFastGasPriceGWei();
			if (gasPrice == null) {
				log.error("ERC20 USDT，参数,from:{},to:{},value:{},获取Gas值错误", new Object[] { from, to, value });
				return new TransactionResponseDto(TransactionResponseDto.CODE_LOCAL_FAIL + "", "获取gasPrice值错误");
			}
//			System.out.println("gasPrice:"+gasPrice);
			// 注意手续费的设置，这块很容易遇到问题
//            BigInteger gasLimit = BigInteger.valueOf(100000L);
//			BigInteger gasLimit = gasLimit();

			BigInteger val = new BigDecimal(value).multiply(new BigDecimal("10").pow(decimal)).toBigInteger();// 单位换算
			Function function = new Function("transfer", Arrays.asList(new Address(to), new Uint256(val)),
					Collections.singletonList(new TypeReference<Type>() {
					}));
			// 创建交易对象
			String encodedFunction = FunctionEncoder.encode(function);

			// gas预估值
			BigInteger gasLimit = gasLimitByTransfer(value, from, to);
			if (gasLimit == null) {
				log.error("ERC20 USDT，参数,from:{},to:{},value:{},获取GasLimit值错误", new Object[] { from, to, value });
				return new TransactionResponseDto(TransactionResponseDto.CODE_LOCAL_FAIL + "",
						"转账失败,请检查手续费是否充足或其他错误导致");
			}
//			System.out.println("gasLimit:"+gasLimit);
			RawTransaction rawTransaction = RawTransaction.createTransaction(nonce, gasPrice, gasLimit, contractAddress,
					encodedFunction);

			// 进行签名操作
			byte[] signMessage = TransactionEncoder.signMessage(rawTransaction, credentials);
			String hexValue = Numeric.toHexString(signMessage);
			// 发起交易
			EthSendTransaction ethSendTransaction = web3j.ethSendRawTransaction(hexValue).sendAsync().get();
			String hash = ethSendTransaction.getTransactionHash();
			if (hash != null) {
				// 执行业务
//				System.out.printf("执行成功：" + hash);
				return new TransactionResponseDto(hash);
			} else {
				return new TransactionResponseDto(ethSendTransaction.getError().getCode() + "",
						ethSendTransaction.getError().getMessage());
			}
		} catch (Exception e) {
			// 报错应进行错误处理
			e.printStackTrace();
			log.error("ERC20 USDT，参数,from:{},to:{},value:{},转账异常==>e:" + e, new Object[] { from, to, value });
			return new TransactionResponseDto(TransactionResponseDto.CODE_LOCAL_FAIL + "", "转账失败,请检查手续费是否充足或其他错误导致");

		}
	}

	/**
	 * gasLimit需要增加的百分比，默认为0.7(即70%),小于0或不存在则都按默认处理
	 * 
	 * @return
	 */
	public double gasLimitAddPercent() {
		try {
			// 第一次如果没有值就主动赋值
			if (percent == null) {
				percent = sysparaService.find("gas_limit_add_percent").getDouble();
			}
//			Double percent = sysparaService.find("gas_limit_add_percent").getDouble();
			if (percent == null || percent.compareTo(0d) < 1) {
				return 0.7d;
			}
			return percent.doubleValue();
		} catch (Exception e) {
			// TODO: handle exception
			log.error("gasLimitAddPercent 获取gasLimit增加百分比异常，==>e:" + e);
			return 0.7d;
		}
	}

	public BigInteger gasLimit() {
		BigInteger gasLimit = BigInteger.valueOf(100000L);
		try {
			Web3j web3j = buildWeb3j();
			EthBlock ethBlock = web3j.ethGetBlockByNumber(DefaultBlockParameterName.LATEST, false).send();
			BigInteger gasUsed = ethBlock.getBlock().getGasUsed();
			int blockTransLength = ethBlock.getBlock().getTransactions().size();
			gasLimit = blockTransLength == 0 ? gasLimit : gasUsed.divide(BigInteger.valueOf(blockTransLength));
			// 设置一个最小值，当小于最小值时 默认最小值
			gasLimit = gasLimit.compareTo(BigInteger.valueOf(60000L)) < 0 ? BigInteger.valueOf(60000L) : gasLimit;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return gasLimit;
	}

	/**
	 * 获取交易的状态
	 * 
	 * @param txid
	 * @return 目前已知 1.交易成功 0.交易失败 -1.请求异常（自定义状态码） null.没有交易凭据返回值(可能为pending中，未验证)
	 */
	public Integer getEthTxStatus(String txid) {
		Web3j web3j = buildWeb3j();
		try {
			EthGetTransactionReceipt ethGetTransactionReceipt = web3j.ethGetTransactionReceipt(txid).send();
			Optional<TransactionReceipt> optional = ethGetTransactionReceipt.getTransactionReceipt();
			if (!optional.isPresent()) {
				return null;
			} else {
				TransactionReceipt transactionReceipt = ethGetTransactionReceipt.getTransactionReceipt().get();
				BigInteger status = new BigInteger(transactionReceipt.getStatus().replace("0x", ""), 16);
				return status.intValue();
			}
		} catch (IOException e) {
			log.error("hash:{} ,获取状态异常==>e:" + e, new Object[] { txid });
			return -1;
		}
	}

	public static void main(String[] args) {

		String operaAddress = "0xC88bA41DA91073B5E3358b6561B41e0aDf10D0B5";
		String operaPrivateKey = "0x84d3fff8bbb7e8139d8b921ce396820b131ab48a47bb60fb6b865b7b8d132765";
//        String from = "0xDfF8FCBB24F448442b668a307A5468212d766567";
		String from2 = "0xCa736B6b4Bd206d31Bd4C92B1D498871E62C3336";
//        String from3 = "0x2BFc4ddD0192CF5aDF4FcBf6C852c558523580A9";
		String to = "0xC8148aF4b38d7793277c752b44E1167D5D8A962b";

		Erc20Service erc20Service = new Erc20ServiceImpl();
		try {
//			erc20Service.tokenTransfrom(from2,to,"3",operaAddress,operaPrivateKey,Erc20Constanst.ERC20_USDT);
			// System.out.println(erc20Service.getBalance("0x35D2d03607b9155b42CF673102FE58251AC4F644"));
			// System.out.println(erc20Service.getEthBalance("0x35D2d03607b9155b42CF673102FE58251AC4F644"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		System.out.println(
//				erc20Service.getEthTxStatus("0x9146c0e3ec07ee519acb8068fbb7b3c244a7a61d3fdfae353ff2a9f972adb4ca"));
//		System.out.println(
//				erc20Service.getEthTxStatus("0x58131c6804a74ff1862b09571b54ece66743de528e896b1285208dc7e3beac0a"));
//        URL urlOfClass = Erc20ServiceImpl.class.getClassLoader()
//				.getResource("org/slf4j/impl/StaticLoggerBinder.class");org.slf4j.spi.LocationAwareLogger.log
	}

	public void setSysparaService(SysparaService sysparaService) {
		this.sysparaService = sysparaService;
	}

	public void setEtherscanService(EtherscanService etherscanService) {
		this.etherscanService = etherscanService;
	}

}
