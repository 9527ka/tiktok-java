package project.futures.job;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import kernel.util.ThreadUtils;
//import project.exchange.ExchangeLock;
import project.futures.FuturesLock;
import project.futures.FuturesOrder;
import project.futures.FuturesOrderService;
import project.syspara.SysparaService;

public class FuturesOrderCalculationJob implements Runnable {
	private static Log logger = LogFactory.getLog(FuturesOrderCalculationJob.class);
	private FuturesOrderService futuresOrderService;
	private FuturesOrderCalculationService futuresOrderCalculationService;
	private SysparaService sysparaService;

	public void run() {
		try {
			while (true) {
				try {
					List<FuturesOrder> list = this.futuresOrderService.cacheSubmitted();
					
					
					
					
					
					
					//每单处理的时间间隔（毫秒）
					int futures_cal_time = sysparaService.find("futures_cal_time").getInteger();
					for (int i = 0; i < list.size(); i++) {
						FuturesOrder order = list.get(i);
						boolean lock = false;
						try {

							if (!FuturesLock.add(order.getOrder_no())) {
								continue;
							}
							lock = true;

							this.futuresOrderCalculationService.saveCalculation(order);

						} catch (Throwable e) {
							logger.error("error:", e);
						} finally {
							if (lock) {
								if (futures_cal_time>0) {
									/**
									 * 每秒处理30个订单
									 */
									ThreadUtils.sleep(futures_cal_time);
								} else {
									/**
									 * 每秒处理200个订单
									 */
									ThreadUtils.sleep(5);
								}
								FuturesLock.remove(order.getOrder_no());
							}

						}

					}

				} catch (Throwable e) {
					logger.error("run fail", e);
				} finally {
					/**
					 * 暂停0.1秒
					 */
					ThreadUtils.sleep(1000);
				}
			}
		} catch (Throwable e) {
			logger.error("run fail", e);
		} finally

		{
			/**
			 * 暂停0.1秒
			 */
			ThreadUtils.sleep(1000);
			/**
			 * 重新启动
			 */
			new Thread(this, "FuturesOrderCalculationJob").start();
			if (logger.isInfoEnabled()) {
				logger.info("交割合约持仓单盈亏计算线程启动！");
			}
		}

	}

	public void start() {

		new Thread(this, "FuturesOrderCalculationJob").start();
		if (logger.isInfoEnabled()) {
			logger.info("交割合约持仓单盈亏计算线程启动！");
		}

	}

	public void setFuturesOrderCalculationService(FuturesOrderCalculationService futuresOrderCalculationService) {
		this.futuresOrderCalculationService = futuresOrderCalculationService;
	}

	public void setFuturesOrderService(FuturesOrderService futuresOrderService) {
		this.futuresOrderService = futuresOrderService;
	}

	public void setSysparaService(SysparaService sysparaService) {
		this.sysparaService = sysparaService;
	}

}
