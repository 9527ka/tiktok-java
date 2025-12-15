package project.wallet.internal;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

import kernel.exception.BusinessException;
import kernel.util.Arith;
import kernel.util.StringUtils;
import project.contract.ContractApplyOrder;
import project.contract.ContractApplyOrderService;
import project.contract.ContractOrder;
import project.contract.ContractOrderService;
import project.contract.ContractRedisKeys;
import project.data.DataService;
import project.data.model.Realtime;
import project.exchange.ExchangeApplyOrder;
import project.exchange.ExchangeApplyOrderService;
import project.finance.FinanceOrder;
import project.finance.FinanceOrderService;
import project.futures.FuturesOrder;
import project.futures.FuturesOrderService;
import project.futures.FuturesRedisKeys;
import project.item.ItemService;
import project.item.model.Item;
import project.miner.MinerOrderService;
import project.miner.MinerRedisKeys;
import project.miner.model.MinerOrder;
import project.redis.RedisHandler;
import project.syspara.SysparaService;
import project.wallet.AssetService;
import project.wallet.Wallet;
import project.wallet.WalletExtend;
import project.wallet.WalletService;

public class AssetServiceImpl extends HibernateDaoSupport implements AssetService {
	
	private Logger log = LoggerFactory.getLogger(AssetServiceImpl.class);
	
	private ContractOrderService contractOrderService;
	private ContractApplyOrderService contractApplyOrderService;
	private WalletService walletService;
	private DataService dataService;
	private FinanceOrderService financeOrderService;
	private MinerOrderService minerOrderService;
	private FuturesOrderService futuresOrderService;
	private ExchangeApplyOrderService exchangeApplyOrderService;
	private SysparaService sysparaService;
	private ItemService itemService;
	private RedisHandler redisHandler;
	
	public Map<String, Object> getMoneyAll(String partyId) {

		Map<String, Object> data = new HashMap<String, Object>();
		DecimalFormat df2 = new DecimalFormat("#.##");

		double money = 0;
		double money_wallet = 0;
		double money_coin = 0;
		double money_all_coin = 0;
		double money_finance = 0;
		double money_miner = 0;
		double money_contractApply = 0;
		double money_contract = 0;
		double money_contract_deposit = 0;
		double money_contract_profit = 0;
		double money_futures = 0;
		double money_futures_profit = 0;

		// 先获取一次所有币种的数据来计算
		String data_symbol = "";
		List<String> list_symbol = new ArrayList<String>();

		List<Item> list_items = this.itemService.cacheGetByMarket("");
		for (int i = 0; i < list_items.size(); i++) {
			Item items = list_items.get(i);
			list_symbol.add(items.getSymbol());
			if (i != 0) {
				data_symbol = data_symbol + "," + items.getSymbol();
			} else {
				data_symbol = items.getSymbol();
			}
		}

		List<Realtime> realtime_all = this.dataService.realtime(data_symbol);
		if (realtime_all.size() <= 0) {
			throw new BusinessException("系统错误，请稍后重试");
		}

		// usdt余额
		Wallet wallet = new Wallet();
		if (!"".equals(partyId) && partyId != null) {
			wallet = this.walletService.saveWalletByPartyId(partyId);
		}

		money = wallet.getMoney();
		// 钱包USDT
		money_wallet = wallet.getMoney();
		// 币余额
		money_coin = this.getMoneyCoin(partyId, realtime_all, list_symbol);
		money = money + money_coin;
		// 钱包USDT+币余额
		money_all_coin = money;
		// 理财
		money_finance = this.getMoneyFinance(partyId, realtime_all);
		money = money + money_finance;
		// 矿机
		money_miner = this.getMoneyMiner(partyId, realtime_all);
//		money_miner_redis = this.getMoneyMinerRedis(partyId, realtime_all);
		money = money + money_miner;
		// 永续委托
		money_contractApply = this.getMoneyContractApply(partyId);
		money = money + money_contractApply;
				
		Map<String, Object> moneys_contract = this.getMoneyContract(partyId);
//		Map<String, Object> moneys_contract_redis = this.getMoneyContractRedis(partyId);
		// 永续
		money_contract = (Double) moneys_contract.get("money_contract");
		// 永续总保证金
		money_contract_deposit = (Double) moneys_contract.get("money_contract_deposit");
		// 永续总未实现盈亏
		money_contract_profit = (Double) moneys_contract.get("money_contract_profit");
		
		money = money + money_contract;
		
		Map<String, Object> moneys_futures = this.getMoneyFutures(partyId);
//		Map<String, Object> moneys_futures_redis = this.getMoneyFuturesRedis(partyId);
		// 交割
		money_futures = (Double) moneys_futures.get("money_futures");
		// 交割未实现盈亏
		money_futures_profit = (Double) moneys_futures.get("money_futures_profit");
		
		money = money + money_futures;

		// 币币交易
		money = money + this.getMoneyexchangeApplyOrders(partyId, realtime_all);

		data.put("total", df2.format(money));
		data.put("money_wallet", df2.format(money_wallet));
		data.put("money_coin", df2.format(money_coin));
		data.put("money_all_coin", df2.format(money_all_coin));
		data.put("money_miner", df2.format(money_miner));
		data.put("money_finance", df2.format(money_finance));
		data.put("money_contract", df2.format(Arith.add(money_contract, money_contractApply)));
		data.put("money_contract_deposit", df2.format(money_contract_deposit));
		data.put("money_contract_profit", df2.format(money_contract_profit));
		data.put("money_futures", df2.format(money_futures));
		data.put("money_futures_profit", df2.format(money_futures_profit));
//		data.put("money_miner_redis", df2.format(money_miner_redis));
//		data.put("moneys_contract_redis", df2.format(moneys_contract_redis));
//		data.put("moneys_futures_redis", df2.format(moneys_futures_redis));

		return data;
	}

	public double getMoneyCoin(String partyId, List<Realtime> realtime_all, List<String> list_symbol) {
		double money_coin = 0;

		List<WalletExtend> walletExtends = this.walletService.findExtend(partyId, list_symbol);
		WalletExtend walletExtend = new WalletExtend();
		if (realtime_all.size() <= 0) {

			String data_symbol = "";

			for (int i = 0; i < walletExtends.size(); i++) {
				walletExtend = walletExtends.get(i);
				if (walletExtend.getAmount() > 0) {
					if (i != 0) {
						data_symbol = data_symbol + "," + walletExtend.getWallettype();
					} else {
						data_symbol = walletExtend.getWallettype();
					}
				}
			}

			walletExtend = new WalletExtend();

			realtime_all = this.dataService.realtime(data_symbol);
			if (realtime_all.size() <= 0) {
				throw new BusinessException("系统错误，请稍后重试");
			}
		}

		Realtime realtime = null;

		// 如果2个相同，则说明用户所有币账户已经生成 .toUpperCase()/
		if (walletExtends != null && walletExtends.size() != 0) {

			for (int i = 0; i < walletExtends.size(); i++) {
				if (null == walletExtends.get(i)) {
					continue;
				}

				walletExtend = walletExtends.get(i);
				if (walletExtend.getAmount() > 0) {
					realtime = null;

					for (Realtime real : realtime_all) {
						if (real.getSymbol().equals(walletExtend.getWallettype().toLowerCase())) {
							realtime = real;
							break;
						}
					}

					if (realtime != null) {
						money_coin = Arith.add(money_coin, Arith.mul(realtime.getClose(), walletExtend.getAmount()));
					}
				}
			}
		}

		return money_coin;
	}
	
	public double getMoneyFinance(String partyId, List<Realtime> realtimeall) {
		double money_finance = 0;
		List<FinanceOrder> financeOrders = financeOrderService.findByState(partyId.toString(), "1");
		String finance_profit_symbol = sysparaService.find("finance_profit_symbol").getValue();
		if (financeOrders != null) {
			Realtime realtime = null;
			if (!"".equals(finance_profit_symbol) && finance_profit_symbol != null && finance_profit_symbol != "usdt") {
				realtime = new Realtime();

				if (realtimeall.size() <= 0) {
					List<Realtime> realtime_list = this.dataService.realtime(finance_profit_symbol);

					if (realtime_list.size() > 0) {
						realtime = realtime_list.get(0);
					} else {
						throw new BusinessException("系统错误，请稍后重试");
					}
				} else {
					for (Realtime real : realtimeall) {
						if (real.getSymbol().equals(finance_profit_symbol)) {
							realtime = real;
							break;
						}
					}
				}
			}
			for (FinanceOrder order : financeOrders) {
				double amount = 0;
				if (!"".equals(finance_profit_symbol) && finance_profit_symbol != null
						&& finance_profit_symbol != "usdt") {
					amount = Arith.mul(order.getAmount(), realtime.getClose());
				} else {
					amount = order.getAmount();
				}
				money_finance = Arith.add(amount, money_finance);
			}
		}
		return money_finance;
	}
	
	/*
	 * 获取 所有订单 矿机总资产 redis
	 */
	public double getMoneyMinerRedis(String partyId, List<Realtime> realtimeall) {
		
		Double minerAssets = (Double) this.redisHandler.get(MinerRedisKeys.MINER_ASSETS_PARTY_ID + partyId.toString());		
		double money_miner = null == minerAssets ? 0.000D : minerAssets;
		
		String minerBuySymbol = this.sysparaService.find("miner_buy_symbol").getValue();
		if (!StringUtils.isEmptyString(minerBuySymbol)) {
			Realtime realtime = new Realtime();

			if (realtimeall.size() <= 0) {
				List<Realtime> realtime_list = this.dataService.realtime(minerBuySymbol);

				if (realtime_list.size() > 0) {
					realtime = realtime_list.get(0);
				} else {
					throw new BusinessException("系统错误，请稍后重试");
				}
			} else {				
				for (Realtime real : realtimeall) {
					if (real.getSymbol().equals(minerBuySymbol)) {
						realtime = real;
						break;
					}
				}
			}
			
			double minerBuyClose = realtime.getClose();
			money_miner = Arith.mul(money_miner, minerBuyClose);
		}
		
		return money_miner;
	}
	
	/*
	 * 获取 所有订单 矿机总资产
	 */
	public double getMoneyMiner(String partyId, List<Realtime> realtimeall) {
		double money_miner = 0;
		List<MinerOrder> minerOrders = minerOrderService.findByState(partyId.toString(), "1");
		if (minerOrders != null) {
			for (MinerOrder order : minerOrders) {
				double amount = Arith.add(order.getAmount(), 0);
				money_miner = Arith.add(amount, money_miner);
			}
			String minerBuySymbol = sysparaService.find("miner_buy_symbol").getValue();
			if (!StringUtils.isEmptyString(minerBuySymbol)) {
				Realtime realtime = new Realtime();

				if (realtimeall.size() <= 0) {
					List<Realtime> realtime_list = this.dataService.realtime(minerBuySymbol);

					if (realtime_list.size() > 0) {
						realtime = realtime_list.get(0);
					} else {
						throw new BusinessException("系统错误，请稍后重试");
					}
				} else {
					for (Realtime real : realtimeall) {
						if (real.getSymbol().equals(minerBuySymbol)) {
							realtime = real;
							break;
						}
					}
				}
				double minerBuyClose = realtime.getClose();
				money_miner = Arith.mul(money_miner, minerBuyClose);
			}
		}

		return money_miner;
	}
	
	public double getMoneyContractApply(String partyId) {
		double money_contractApply = 0;

		List<ContractApplyOrder> contractApplyOrders = this.contractApplyOrderService.findSubmitted(partyId.toString(), "", "", "");
		if (contractApplyOrders != null) {
			
			for (ContractApplyOrder order : contractApplyOrders) {
				double amount = Arith.mul(order.getVolume_open(), order.getUnit_amount());
				money_contractApply = Arith.add(amount, money_contractApply);
			}
		}

		return money_contractApply;
	}
	
	/*
	 * 获取 所有订单 永续合约总资产、总保证金、总未实现盈利 redis
	 */
	public Map<String, Object> getMoneyContractRedis(String partyId) {
		
		Double contractAssets = (Double) this.redisHandler.get(ContractRedisKeys.CONTRACT_ASSETS_PARTY_ID + partyId.toString());
		Double contractAssetsDeposit = (Double) this.redisHandler.get(ContractRedisKeys.CONTRACT_ASSETS_DEPOSIT_PARTY_ID + partyId.toString());
		Double contractAssetsProfit = (Double) this.redisHandler.get(ContractRedisKeys.CONTRACT_ASSETS_PROFIT_PARTY_ID + partyId.toString());

		Map<String, Object> moneys_contract = new HashMap<String, Object>();
		moneys_contract.put("money_contract", null == contractAssets ? 0.000D : contractAssets);
		moneys_contract.put("money_contract_deposit", null == contractAssetsDeposit ? 0.000D : contractAssetsDeposit);
		moneys_contract.put("money_contract_profit", null == contractAssetsProfit ? 0.000D : contractAssetsProfit);

		return moneys_contract;
	}
	
	/*
	 * 获取 所有订单 永续合约总资产、总保证金、总未实现盈利
	 */
	public Map<String, Object> getMoneyContract(String partyId) {
		double money_contract = 0;
		double money_contract_deposit = 0;
		double money_contract_profit = 0;
		
		List<ContractOrder> contractOrders = this.contractOrderService.findSubmitted(partyId.toString(), "", "");
		if (contractOrders != null) {
			
			for (ContractOrder order : contractOrders) {
				double order_volume = 1;
				
				if (order.getLever_rate() != null && order.getLever_rate() != 0) {
					order_volume = Arith.div(order.getVolume_open(), order.getLever_rate());
				} else {
					order_volume = order.getVolume_open();
				}
				
				double amount = Arith.add(Arith.mul(order_volume, order.getUnit_amount()), order.getProfit());
				money_contract = Arith.add(amount, money_contract);
				money_contract_deposit = Arith.add(order.getDeposit(), money_contract_deposit);
				money_contract_profit = Arith.add(order.getProfit(), money_contract_profit);
			}
		}
		
		Map<String, Object> moneys_contract = new HashMap<String, Object>();
		moneys_contract.put("money_contract", money_contract);
		moneys_contract.put("money_contract_deposit", money_contract_deposit);
		moneys_contract.put("money_contract_profit", money_contract_profit);

		return moneys_contract;
	}
	
	/*
	 * 获取 单个订单 永续合约总资产、总保证金、总未实现盈利
	 */
	public Map<String, Double> getMoneyContractByOrder(ContractOrder order) {
		Map<String, Double> moneys_contract = new HashMap<String, Double>();
		
		if (null == order) {
			moneys_contract.put("money_contract", 0.000D);
			moneys_contract.put("money_contract_deposit", 0.000D);
			moneys_contract.put("money_contract_profit", 0.000D);
			return moneys_contract;
		}
		
		double order_volume = 1;

		if (order.getLever_rate() != null && order.getLever_rate() != 0) {
			order_volume = Arith.div(order.getVolume_open(), order.getLever_rate());
		} else {
			order_volume = order.getVolume_open();
		}
		
		double money_contract = Arith.add(Arith.mul(order_volume, order.getUnit_amount()), order.getProfit());
		double money_contract_deposit = order.getDeposit();
		double money_contract_profit = order.getProfit();
		
		moneys_contract.put("money_contract", money_contract);
		moneys_contract.put("money_contract_deposit", money_contract_deposit);
		moneys_contract.put("money_contract_profit", money_contract_profit);
		return moneys_contract;
	}
	
	/*
	 * 获取 所有订单 交割合约总资产、总未实现盈利 redis
	 */
	public Map<String, Object> getMoneyFuturesRedis(String partyId) {

		Double futuresAssets = (Double) this.redisHandler.get(FuturesRedisKeys.FUTURES_ASSETS_PARTY_ID + partyId.toString());
		Double futuresAssetsProfit = (Double) this.redisHandler.get(FuturesRedisKeys.FUTURES_ASSETS_PROFIT_PARTY_ID + partyId.toString());

		Map<String, Object> moneys_futures = new HashMap<String, Object>();
		moneys_futures.put("money_futures", null == futuresAssets ? 0.000D : futuresAssets);
		moneys_futures.put("money_futures_profit", null == futuresAssetsProfit ? 0.000D : futuresAssetsProfit);

		return moneys_futures;
	}
	
	/*
	 * 获取 所有订单 交割合约总资产、总未实现盈利
	 */
	public Map<String, Object> getMoneyFutures(String partyId) {
		double money_futures = 0;
		double money_futures_profit = 0;
		
		List<FuturesOrder> futuresOrders = this.futuresOrderService.cacheSubmitted();
		if (futuresOrders != null) {
			
			for (FuturesOrder order : futuresOrders) {
				if (partyId.equals(order.getPartyId().toString())) {
					money_futures = Arith.add(order.getVolume(), money_futures);
					money_futures_profit = Arith.add(order.getProfit(), money_futures_profit);
				}
			}
		}
		
		Map<String, Object> moneys_futures = new HashMap<String, Object>();
		moneys_futures.put("money_futures", money_futures);
		moneys_futures.put("money_futures_profit", money_futures_profit);

		return moneys_futures;
	}
	
	/*
	 * 获取 单个订单 交割合约总资产、总未实现盈利
	 */
	public Map<String, Double> getMoneyFuturesByOrder(FuturesOrder order) {
		Map<String, Double> moneys_futures = new HashMap<String, Double>();
		
		if (null == order) {
			moneys_futures.put("money_futures", 0.000D);
			moneys_futures.put("money_futures_profit", 0.000D);
			return moneys_futures;
		}
		
		double money_futures = order.getVolume();
		double money_futures_profit = order.getProfit();
		
		moneys_futures.put("money_futures", money_futures);
		moneys_futures.put("money_futures_profit", money_futures_profit);
		return moneys_futures;
	}
	
	public double getMoneyexchangeApplyOrders(String partyId, List<Realtime> realtimeall) {
		double moneyExchange = 0;
		
		List<ExchangeApplyOrder> exchangeApplyOrders = this.exchangeApplyOrderService.findSubmitted();
		
		if (exchangeApplyOrders != null) {
			
			for (ExchangeApplyOrder order : exchangeApplyOrders) {
				
				if (partyId.equals(order.getPartyId().toString())) {
					
					if ("open".equals(order.getOffset())) {
						moneyExchange = Arith.add(moneyExchange, order.getVolume());
					}
					
					if ("close".equals(order.getOffset())) {						
						Realtime realtime = new Realtime();
						
						if (realtimeall.size() <= 0) {
							List<Realtime> realtime_list = this.dataService.realtime(order.getSymbol());

							if (realtime_list.size() > 0) {
								realtime = realtime_list.get(0);
							} else {
								throw new BusinessException("系统错误，请稍后重试");
							}
						} else {
							for (Realtime real : realtimeall) {
								if (real.getSymbol().equals(order.getSymbol())) {
									realtime = real;
									break;
								}
							}
						}

						moneyExchange = Arith.add(moneyExchange, Arith.mul(order.getVolume(), realtime.getClose()));
					}
				}
			}
		}

		return moneyExchange;
	}

	public void setContractOrderService(ContractOrderService contractOrderService) {
		this.contractOrderService = contractOrderService;
	}

	public void setContractApplyOrderService(ContractApplyOrderService contractApplyOrderService) {
		this.contractApplyOrderService = contractApplyOrderService;
	}

	public void setWalletService(WalletService walletService) {
		this.walletService = walletService;
	}

	public void setDataService(DataService dataService) {
		this.dataService = dataService;
	}

	public void setFinanceOrderService(FinanceOrderService financeOrderService) {
		this.financeOrderService = financeOrderService;
	}

	public void setMinerOrderService(MinerOrderService minerOrderService) {
		this.minerOrderService = minerOrderService;
	}

	public void setFuturesOrderService(FuturesOrderService futuresOrderService) {
		this.futuresOrderService = futuresOrderService;
	}

	public void setExchangeApplyOrderService(ExchangeApplyOrderService exchangeApplyOrderService) {
		this.exchangeApplyOrderService = exchangeApplyOrderService;
	}

	public void setSysparaService(SysparaService sysparaService) {
		this.sysparaService = sysparaService;
	}

	public void setItemService(ItemService itemService) {
		this.itemService = itemService;
	}

	public void setRedisHandler(RedisHandler redisHandler) {
		this.redisHandler = redisHandler;
	}

}
