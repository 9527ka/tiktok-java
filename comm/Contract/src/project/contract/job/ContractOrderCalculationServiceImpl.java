package project.contract.job;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;

import kernel.util.Arith;
import kernel.util.ThreadUtils;
import project.contract.ContractLock;
import project.contract.ContractOrder;
import project.contract.ContractOrderService;
import project.data.DataService;
import project.data.model.Realtime;
import project.syspara.SysparaService;
import project.wallet.Wallet;
import project.wallet.WalletService;

public class ContractOrderCalculationServiceImpl implements ContractOrderCalculationService {
	private static Log logger = LogFactory.getLog(ContractOrderCalculationServiceImpl.class);
	private ContractOrderService contractOrderService;
	private DataService dataService;
	private WalletService walletService;
	/**
	 * 平仓线 110%（订金价值 /收益=110%）
	 */
	public double order_close_line = 1.1;
	/**
	 * 平仓方式 1全仓 2单个持仓
	 */
	public int order_close_line_type = 1;
	private SysparaService sysparaService;

	public void saveCalculation(String order_no) {

		try {
			ContractOrder order = contractOrderService.findByOrderNo(order_no);
			if (order == null || !ContractOrder.STATE_SUBMITTED.equals(order.getState())) {
				/**
				 * 状态已改变，退出处理
				 */
				return;
			}
			List<Realtime> list = this.dataService.realtime(order.getSymbol());
			if (list.size() == 0) {
				return;
			}
			Realtime realtime = list.get(0);

			double close = realtime.getClose();

			if (ContractOrder.DIRECTION_BUY.equals(order.getDirection())) {
				/*
				 * 0 买涨
				 */
				if (close >= Arith.add(order.getTrade_avg_price(), order.getPips())) {
					settle(order, "profit", close);
				}

				if (close <= Arith.sub(order.getTrade_avg_price(), order.getPips())) {
					settle(order, "loss", close);
				}

			} else {
				/*
				 * 1 买跌
				 */
				if (close <= Arith.sub(order.getTrade_avg_price(), order.getPips())) {
					settle(order, "profit", close);
				}
				if (close >= Arith.add(order.getTrade_avg_price(), order.getPips())) {
					settle(order, "loss", close);
				}
			}
		} catch (Throwable e) {
			logger.error("OrderCalculationServiceImpll run fail", e);
		}

	}

	/**
	 * 盈亏计算
	 * 
	 * @param profit_loss  profit 盈 loss亏
	 * @param currentPrice 当前点位
	 */
	public void settle(ContractOrder order, String profit_loss, double currentPrice) {

		/**
		 * 偏差点位
		 */
		double point = Arith.div(Math.abs(Arith.sub(currentPrice, order.getTrade_avg_price())), order.getPips());
		
		/*
		 * 根据偏 差点数和手数算出盈亏金额
		 */
		double amount = Arith.mul(Arith.mul(order.getPips_amount(), point), order.getVolume());
		
		if ("profit".equals(profit_loss)) {
			/**
			 * 盈 正数
			 */
			order.setProfit(Arith.add(0.0D, amount));
		} else if ("loss".equals(profit_loss)) {
			order.setProfit(Arith.sub(0.0D, amount));
		}
		/**
		 * 多次平仓价格不对，后续修
		 */
		order.setClose_avg_price(currentPrice);
		this.contractOrderService.update(order);
		/**
		 * 止盈价
		 */
		Double profit_stop = order.getStop_price_profit();
		if (profit_stop != null && profit_stop > 0 && ContractOrder.DIRECTION_BUY.equals(order.getDirection())) {
			/*
			 * 买涨
			 */
			if (currentPrice >= profit_stop) {
				this.contractOrderService.saveClose(order.getPartyId().toString(), order.getOrder_no());
				return;
			}
		} else if (profit_stop != null && profit_stop > 0
				&& ContractOrder.DIRECTION_SELL.equals(order.getDirection())) {
			/**
			 * 买跌
			 */
			if (currentPrice <= profit_stop) {
				this.contractOrderService.saveClose(order.getPartyId().toString(), order.getOrder_no());
				return;
			}
		}

		/**
		 * 止亏线
		 */
		Double loss_stop = order.getStop_price_loss();

		if (loss_stop != null && loss_stop > 0 && ContractOrder.DIRECTION_BUY.equals(order.getDirection())) {
			/*
			 * 买涨
			 */
			if (currentPrice <= loss_stop) {
				this.contractOrderService.saveClose(order.getPartyId().toString(), order.getOrder_no());
				return;

			}
		} else if (loss_stop != null && loss_stop > 0 && ContractOrder.DIRECTION_SELL.equals(order.getDirection())) {
			/**
			 * 买跌
			 */

			if (currentPrice >= loss_stop) {
				this.contractOrderService.saveClose(order.getPartyId().toString(), order.getOrder_no());
				return;
			}
		}
		if (order_close_line_type == 1) {
			/**
			 * 收益
			 */
			double profit = 0;

			List<ContractOrder> list = contractOrderService.findSubmitted(order.getPartyId().toString(), null, null);
			for (int i = 0; i < list.size(); i++) {
				ContractOrder close_line = list.get(i);
				profit = Arith.add(profit, Arith.add(close_line.getProfit(), close_line.getDeposit()));
			}
			Wallet wallet = this.walletService.saveWalletByPartyId(order.getPartyId().toString());

			if (Arith.add(profit, wallet.getMoney()) <= 0) {
				/**
				 * 触发全仓强平
				 */
				this.contractOrderService.saveClose(order.getPartyId().toString(), order.getOrder_no());
				ThreadUtils.sleep(100);
				for (int i = 0; i < list.size(); i++) {
					ContractOrder close_line = list.get(i);
					if (!order.getOrder_no().equals(close_line.getOrder_no())) {
						try {

							while (true) {
								if (ContractLock.add(close_line.getOrder_no())) {
									this.contractOrderService.saveClose(close_line.getPartyId().toString(),
											close_line.getOrder_no());
									/**
									 * 处理完退出
									 */
									break;
								}
								ThreadUtils.sleep(500);

							}

						} catch (Exception e) {
							logger.error("error:", e);
						} finally {
							ContractLock.remove(close_line.getOrder_no());
							ThreadUtils.sleep(100);
						}

					}
				}

			}
		} else {
			if (order.getProfit() < 0 && (Arith.div(order.getDeposit(), Math.abs(order.getProfit())) <= Arith
					.div(order_close_line, 100))) {
				/**
				 * 低于系统默认平仓线，进行强平
				 */
				this.contractOrderService.saveClose(order.getPartyId().toString(), order.getOrder_no());
				return;
			}

		}

	}

//	@Override
//	public void afterPropertiesSet() throws Exception {
//		order_close_line = this.sysparaService.find("order_close_line").getDouble();
//		order_close_line_type = this.sysparaService.find("order_close_line_type").getInteger();
//
//	}

	public void setDataService(DataService dataService) {
		this.dataService = dataService;
	}

	public void setSysparaService(SysparaService sysparaService) {
		this.sysparaService = sysparaService;
	}

	public void setContractOrderService(ContractOrderService contractOrderService) {
		this.contractOrderService = contractOrderService;
	}

	public void setWalletService(WalletService walletService) {
		this.walletService = walletService;
	}

	public void setOrder_close_line(double order_close_line) {
		this.order_close_line = order_close_line;
	}

	public void setOrder_close_line_type(int order_close_line_type) {
		this.order_close_line_type = order_close_line_type;
	}

}
