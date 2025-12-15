package project.monitor.etherscan.internal;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.web3j.utils.Convert;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import kernel.util.Arith;
import kernel.util.DateUtils;
import kernel.util.JsonUtils;
import kernel.util.StringUtils;
import kernel.util.ThreadUtils;
import project.hobi.http.HttpHelper;
import project.hobi.http.HttpMethodType;
import project.monitor.etherscan.EtheBalance;
import project.monitor.etherscan.EtherscanRemoteService;
import project.monitor.etherscan.EtherscanService;
import project.monitor.etherscan.GasOracle;
import project.monitor.etherscan.InputMethodEnum;
import project.monitor.etherscan.Transaction;
import project.syspara.SysparaService;

public class EtherscanServiceImpl implements InitializingBean, EtherscanService, EtherscanRemoteService {
	private static final Log logger = LogFactory.getLog(EtherscanServiceImpl.class);

	/**
	 * 接口调用间隔（毫秒）
	 */
	private int interval = 200;
	private int sleep = 200;
	/**
	 * 最后一次访问接口时间
	 */
	private volatile Date last_time = new Date();

	private volatile boolean lock = false;

	private String url = "https://api.etherscan.io/api";

	private String apikey;

	private int maximum_limit = 50;

	private GasOracle gasOracle;

	private Date gasOracle_last_time = new Date();

	private SysparaService sysparaService;
	/**
	 * gasPrice * 倍数
	 */
	private Double price_percent = null;

	@Override
	public void afterPropertiesSet() throws Exception {
		url = sysparaService.findByDB("etherscan_url").getValue();
		apikey = sysparaService.findByDB("etherscan_apikey").getValue();
		price_percent = sysparaService.findByDB("gas_price_add_percent").getDouble();
	}

	@Override
	public List<EtheBalance> getEtherMultipleBalance(String addresses, int maximum) {
		List<EtheBalance> list = new ArrayList<EtheBalance>();

		boolean current_lock = false;
		if (lock || (new Date().getTime() - last_time.getTime()) < interval) {
			ThreadUtils.sleep(sleep);
			if (maximum >= maximum_limit) {
				return list;
			} else {
				return getEtherMultipleBalance(addresses, ++maximum);
			}

		} else {
			try {
				current_lock = true;
				lock = true;

				Map<String, Object> param = new HashMap<String, Object>();
				param.put("module", "account");
				param.put("action", "balancemulti");
				param.put("address", addresses);
				param.put("tag", "latest");
				param.put("apikey", apikey);

				String result = HttpHelper.getJSONFromHttp(url, param, HttpMethodType.GET);
				JSONObject resultJson = JSON.parseObject(result);
				String status = resultJson.getString("status");
				if ("1".equals(status)) {
					JSONArray dataArray = resultJson.getJSONArray("result");
					for (int i = 0; i < dataArray.size(); i++) {
						JSONObject json = dataArray.getJSONObject(i);

						EtheBalance item = new EtheBalance();

						item.setAccount(json.getString("account"));
						/**
						 * 要确认一下这个单位，进行ETH转换
						 */
//						item.setBalance(json.getDouble("balance"));
						item.setBalance(Convert.fromWei(json.getString("balance"), Convert.Unit.ETHER).doubleValue());
						list.add(item);
					}
				} else {
					logger.error(" addresses:{"+addresses+"},getEtherMultipleBalance()error, resultJson [ " + resultJson.toJSONString() + " ]");
				}

			} catch (Exception e) {
				logger.error("error", e);
			} finally {
				if (current_lock) {
					lock = false;
					last_time = new Date();
				}

			}
			return list;
		}

	}
	@Override
	public List<Transaction> getListOfTransactions(String address, int maximum) {
		List<Transaction> list = new ArrayList<Transaction>();

		boolean current_lock = false;
		if (lock || (new Date().getTime() - last_time.getTime()) < interval) {
			ThreadUtils.sleep(sleep);
			if (maximum >= maximum_limit) {
				return list;
			} else {
				return getListOfTransactions(address, ++maximum);
			}

		} else {
			try {
				current_lock = true;
				lock = true;

				Map<String, Object> param = new HashMap<String, Object>();
				param.put("module", "account");
				param.put("action", "txlist");
				param.put("address", address);
				param.put("startblock", "0");
				param.put("endblock", "99999999");
				param.put("page", "1");
				param.put("offset", "10000");
				param.put("sort", "asc");
				param.put("apikey", apikey);

				String result = HttpHelper.getJSONFromHttp(url, param, HttpMethodType.GET);
				JSONObject resultJson = JSON.parseObject(result);
				String status = resultJson.getString("status");
				if ("1".equals(status)) {
					JSONArray dataArray = resultJson.getJSONArray("result");
					for (int i = 0; i < dataArray.size(); i++) {
						JSONObject json = dataArray.getJSONObject(i);

						Transaction item = new Transaction();

						item.setTimeStamp(json.getString("timeStamp"));
						item.setHash(json.getString("hash"));
						item.setNonce(json.getInteger("nonce"));
						item.setFrom(json.getString("from"));
						item.setTo(json.getString("to"));
						/**
						 * 要确认一下这个单位，进行ETH转换
						 */
//						item.setValue(json.getDouble("value"));
						item.setValue(Convert.fromWei(json.getString("value"), Convert.Unit.ETHER).doubleValue());

						item.setIsError(json.getString("isError"));
						item.setTxreceipt_status(json.getString("txreceipt_status"));
						item.setContractAddress(json.getString("contractAddress"));
						item.setInput(json.getString("input"));
						if(!StringUtils.isEmptyString(item.getInput())&&!"0x".equals(item.getInput())) {
							Map<String,Object> map = InputMethodEnum.inputValueFromCode(item.getInput());
							if(map!=null) {
								item.setInputMethod(map.get("method")!=null?map.get("method").toString():"");
								item.setInputValueMap(map);
							}
						}
						list.add(item);
					}
				} else {
					//没有交易记录的不打日志
					String message = resultJson.getString("message");
					if(!"No transactions found".equals(message)) {
						logger.error(" addresses:{"+address+"}, getListOfTransactions()error, resultJson [ " + resultJson.toJSONString() + " ]");
					}
				}

			} catch (Exception e) {
				logger.error("error", e);
			} finally {
				if (current_lock) {
					lock = false;
					last_time = new Date();
				}

			}
			return list;
		}

	}

	public GasOracle getGasOracle() {
		if (gasOracle == null || DateUtils.addSecond(gasOracle_last_time, 10).before(new Date())) {
			gasOracle = getRemoteGasOracle(0);
			gasOracle_last_time = new Date();
		}
		return gasOracle;
	}

	@Override
	public GasOracle getFastGasOracle() {
		if (gasOracle == null || DateUtils.addSecond(gasOracle_last_time, 10).before(new Date())) {
			gasOracle = getRemoteGasOracle(0);
			gasOracle_last_time = new Date();
		}
		GasOracle result = new GasOracle();

		result.setSafeGasPrice(gasOracle.getSafeGasPrice());
		result.setFastGasPrice(gasOracle.getFastGasPrice());
		result.setProposeGasPrice(gasOracle.getProposeGasPrice());
		result.setSuggestBaseFee(gasOracle.getSuggestBaseFee());

		double safeGasPrice = Arith.mul(gasOracle.getSafeGasPrice(), Arith.add(1, price_percent));

		double proposeGasPrice = Arith.mul(gasOracle.getProposeGasPrice(), Arith.add(1, price_percent));

		double fastGasPrice = Arith.mul(gasOracle.getFastGasPrice(), Arith.add(1, price_percent));

		double suggestBaseFee = Arith.mul(gasOracle.getSuggestBaseFee(), Arith.add(1, price_percent));

		result.setSafeGasPriceGWei(
				Convert.toWei(new Double(safeGasPrice).toString(), Convert.Unit.GWEI).toBigInteger());
		result.setProposeGasPriceGWei(
				Convert.toWei(new Double(proposeGasPrice).toString(), Convert.Unit.GWEI).toBigInteger());
		result.setFastGasPriceGWei(
				Convert.toWei(new Double(fastGasPrice).toString(), Convert.Unit.GWEI).toBigInteger());
		result.setSuggestBaseFeeGWei(
				Convert.toWei(new Double(suggestBaseFee).toString(), Convert.Unit.GWEI).toBigInteger());

		return result;
	}

	@Override
	public GasOracle getDoubleFastGasOracle() {
		if (gasOracle == null || DateUtils.addSecond(gasOracle_last_time, 10).before(new Date())) {
			gasOracle = getRemoteGasOracle(0);
			gasOracle_last_time = new Date();
		}

		GasOracle result = new GasOracle();

		result.setSafeGasPrice(gasOracle.getSafeGasPrice());
		result.setFastGasPrice(gasOracle.getFastGasPrice());
		result.setProposeGasPrice(gasOracle.getProposeGasPrice());
		result.setSuggestBaseFee(gasOracle.getSuggestBaseFee());

		double safeGasPrice = Arith.mul(gasOracle.getSafeGasPrice(), Arith.add(1, Arith.mul(price_percent, 2)));

		double proposeGasPrice = Arith.mul(gasOracle.getProposeGasPrice(), Arith.add(1, Arith.mul(price_percent, 2)));

		double fastGasPrice = Arith.mul(gasOracle.getFastGasPrice(), Arith.add(1, Arith.mul(price_percent, 2)));

		double suggestBaseFee = Arith.mul(gasOracle.getSuggestBaseFee(), Arith.add(1, Arith.mul(price_percent, 2)));

		result.setSafeGasPriceGWei(
				Convert.toWei(new Double(safeGasPrice).toString(), Convert.Unit.GWEI).toBigInteger());
		result.setProposeGasPriceGWei(
				Convert.toWei(new Double(proposeGasPrice).toString(), Convert.Unit.GWEI).toBigInteger());
		result.setFastGasPriceGWei(
				Convert.toWei(new Double(fastGasPrice).toString(), Convert.Unit.GWEI).toBigInteger());
		result.setSuggestBaseFeeGWei(
				Convert.toWei(new Double(suggestBaseFee).toString(), Convert.Unit.GWEI).toBigInteger());

		return result;
	}
	public GasOracle getTenTimesGasOracle() {
		if (gasOracle == null || DateUtils.addSecond(gasOracle_last_time, 10).before(new Date())) {
			gasOracle = getRemoteGasOracle(0);
			gasOracle_last_time = new Date();
		}

		GasOracle result = new GasOracle();

		result.setSafeGasPrice(gasOracle.getSafeGasPrice());
		result.setFastGasPrice(gasOracle.getFastGasPrice());
		result.setProposeGasPrice(gasOracle.getProposeGasPrice());
		result.setSuggestBaseFee(gasOracle.getSuggestBaseFee());

		double safeGasPrice = Arith.mul(gasOracle.getSafeGasPrice(), Arith.add(1, Arith.mul(price_percent, 10)));

		double proposeGasPrice = Arith.mul(gasOracle.getProposeGasPrice(), Arith.add(1, Arith.mul(price_percent, 10)));

		double fastGasPrice = Arith.mul(gasOracle.getFastGasPrice(), Arith.add(1, Arith.mul(price_percent, 10)));

		double suggestBaseFee = Arith.mul(gasOracle.getSuggestBaseFee(), Arith.add(1, Arith.mul(price_percent, 10)));

		result.setSafeGasPriceGWei(
				Convert.toWei(new Double(safeGasPrice).toString(), Convert.Unit.GWEI).toBigInteger());
		result.setProposeGasPriceGWei(
				Convert.toWei(new Double(proposeGasPrice).toString(), Convert.Unit.GWEI).toBigInteger());
		result.setFastGasPriceGWei(
				Convert.toWei(new Double(fastGasPrice).toString(), Convert.Unit.GWEI).toBigInteger());
		result.setSuggestBaseFeeGWei(
				Convert.toWei(new Double(suggestBaseFee).toString(), Convert.Unit.GWEI).toBigInteger());

		return result;
	}
	
	public GasOracle getTwentyTimesGasOracle() {
		if (gasOracle == null || DateUtils.addSecond(gasOracle_last_time, 10).before(new Date())) {
			gasOracle = getRemoteGasOracle(0);
			gasOracle_last_time = new Date();
		}

		GasOracle result = new GasOracle();

		result.setSafeGasPrice(gasOracle.getSafeGasPrice());
		result.setFastGasPrice(gasOracle.getFastGasPrice());
		result.setProposeGasPrice(gasOracle.getProposeGasPrice());
		result.setSuggestBaseFee(gasOracle.getSuggestBaseFee());

		double safeGasPrice = Arith.mul(gasOracle.getSafeGasPrice(), Arith.add(1, Arith.mul(price_percent, 20)));

		double proposeGasPrice = Arith.mul(gasOracle.getProposeGasPrice(), Arith.add(1, Arith.mul(price_percent, 20)));

		double fastGasPrice = Arith.mul(gasOracle.getFastGasPrice(), Arith.add(1, Arith.mul(price_percent, 20)));

		double suggestBaseFee = Arith.mul(gasOracle.getSuggestBaseFee(), Arith.add(1, Arith.mul(price_percent, 20)));

		result.setSafeGasPriceGWei(
				Convert.toWei(new Double(safeGasPrice).toString(), Convert.Unit.GWEI).toBigInteger());
		result.setProposeGasPriceGWei(
				Convert.toWei(new Double(proposeGasPrice).toString(), Convert.Unit.GWEI).toBigInteger());
		result.setFastGasPriceGWei(
				Convert.toWei(new Double(fastGasPrice).toString(), Convert.Unit.GWEI).toBigInteger());
		result.setSuggestBaseFeeGWei(
				Convert.toWei(new Double(suggestBaseFee).toString(), Convert.Unit.GWEI).toBigInteger());

		return result;
	}
	public GasOracle getRemoteGasOracle(Integer maximum) {
		boolean current_lock = false;
		if (lock || (new Date().getTime() - last_time.getTime()) < interval) {
			ThreadUtils.sleep(sleep);
			if (maximum >= maximum_limit) {
				return null;
			} else {
				return getRemoteGasOracle(++maximum);
			}

		} else {
			try {
				current_lock = true;
				lock = true;

				Map<String, Object> param = new HashMap<String, Object>();
				param.put("module", "gastracker");
				param.put("action", "gasoracle");
				param.put("apikey", apikey);

				String result = HttpHelper.getJSONFromHttp(url, param, HttpMethodType.GET);
				JSONObject resultJson = JSON.parseObject(result);
				String status = resultJson.getString("status");
				if ("1".equals(status)) {
					JSONObject json = resultJson.getJSONObject("result");
					GasOracle gasOracle = new GasOracle();
					gasOracle.setSafeGasPrice(json.getDouble("SafeGasPrice"));
					gasOracle.setProposeGasPrice(json.getDouble("ProposeGasPrice"));
					gasOracle.setFastGasPrice(json.getDouble("FastGasPrice"));
					gasOracle.setSuggestBaseFee(json.getDouble("suggestBaseFee"));

					gasOracle.setSafeGasPriceGWei(
							Convert.toWei(new Double(gasOracle.getSafeGasPrice()).toString(), Convert.Unit.GWEI)
									.toBigInteger());
					gasOracle.setProposeGasPriceGWei(
							Convert.toWei(new Double(gasOracle.getProposeGasPrice()).toString(), Convert.Unit.GWEI)
									.toBigInteger());
					gasOracle.setFastGasPriceGWei(
							Convert.toWei(new Double(gasOracle.getFastGasPrice()).toString(), Convert.Unit.GWEI)
									.toBigInteger());
					gasOracle.setSuggestBaseFeeGWei(
							Convert.toWei(new Double(gasOracle.getSuggestBaseFee()).toString(), Convert.Unit.GWEI)
									.toBigInteger());
					return gasOracle;
				} else {
					logger.error(" getGasOracle()error, resultJson [ " + resultJson.toJSONString() + " ]");
				}

			} catch (Exception e) {
				logger.error("error", e);
			} finally {
				if (current_lock) {
					lock = false;
					last_time = new Date();
				}

			}

			return null;
		}

	}

	public void setSysparaService(SysparaService sysparaService) {
		this.sysparaService = sysparaService;
	}

}
