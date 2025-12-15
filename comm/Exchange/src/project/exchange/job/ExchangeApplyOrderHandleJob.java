package project.exchange.job;

import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;

import kernel.util.ThreadUtils;
import project.data.DataService;
import project.data.model.Realtime;
import project.exchange.ExchangeApplyOrder;
import project.exchange.ExchangeApplyOrderService;
import project.exchange.ExchangeLock;

/**
 * 
 * 委托单进入市场
 */
public class ExchangeApplyOrderHandleJob implements  Runnable {
	private static Log logger = LogFactory.getLog(ExchangeApplyOrderHandleJob.class);
	private ExchangeApplyOrderService exchangeApplyOrderService;
	private DataService dataService;

	public void run() {
		/*
		 * 系统启动先暂停30秒
		 */
		ThreadUtils.sleep(1000 * 30);
		while (true)
			try {
				List<ExchangeApplyOrder> list = this.exchangeApplyOrderService.findSubmitted();
				for (int i = 0; i < list.size(); i++) {
					ExchangeApplyOrder order = list.get(i);
					List<Realtime> realtime_list = this.dataService.realtime(order.getSymbol());
					Realtime realtime = null;
					if (realtime_list.size() > 0) {
						realtime = realtime_list.get(0);
					} else {
						continue;
					}
					
					//如果不是计划委托则按原代码执行
					if(!order.isIs_trigger_order()) {
						if ("limit".equals(order.getOrder_price_type())) {
							/**
							 * 限价单
							 */
							if (ExchangeApplyOrder.OFFSET_OPEN.equals(order.getOffset())) {// 买入时 限制 低价买入
								if (realtime.getClose() <= order.getPrice()) {
									this.handle(order, realtime);
								}
							} else {// 卖出时 限制 高价卖出
								if (realtime.getClose() >= order.getPrice()) {
									this.handle(order, realtime);
								}
							}

						} else {
							/**
							 * 非限制，直接进入市 场
							 */
							this.handle(order, realtime);
						}

					}
					
					
					
					//如果是计划委托单则
					if(order.isIs_trigger_order()) {
							//需要满足触发价条件
							if (ExchangeApplyOrder.OFFSET_OPEN.equals(order.getOffset())) {// 买入时 限制 低价买入
								if (realtime.getClose() <= order.getTrigger_price()) {
									this.handleTrigger(order, realtime);
								}
							} else {// 卖出时 限制 高价卖出
								if (realtime.getClose() >= order.getTrigger_price()) {
									this.handleTrigger(order, realtime);
								}
							}

						
					}
					
					

					
				}

			} catch (Exception e) {
				logger.error("run fail", e);
			} finally {
				ThreadUtils.sleep(1000 * 1);
			}

	}

	public void handle(ExchangeApplyOrder applyOrder, Realtime realtime) {
		boolean lock = false;
		try {
			if (!ExchangeLock.add(applyOrder.getOrder_no())) {
				return;
			}
			lock = true;
			if ("open".equals(applyOrder.getOffset())) {
				this.exchangeApplyOrderService.saveOpen(applyOrder, realtime);
			} else if ("close".equals(applyOrder.getOffset())) {
				/**
				 * 平仓
				 */
				this.exchangeApplyOrderService.saveClose(applyOrder, realtime);

			}

		} catch (Exception e) {
			logger.error("error:", e);
		} finally {
			if (lock) {
				ThreadUtils.sleep(100);
				ExchangeLock.remove(applyOrder.getOrder_no());
			}

		}

	}
	public void handleTrigger(ExchangeApplyOrder applyOrder, Realtime realtime) {
		boolean lock = false;
		try {
			if (!ExchangeLock.add(applyOrder.getOrder_no())) {
				return;
			}
			lock = true;
			if ("open".equals(applyOrder.getOffset())) {
				ExchangeApplyOrder order = new ExchangeApplyOrder();
				order.setPartyId(applyOrder.getPartyId());
				order.setSymbol(applyOrder.getSymbol());
				order.setOffset(applyOrder.getOffset());
				order.setVolume(applyOrder.getVolume());
				order.setPrice(applyOrder.getPrice());
				order.setIs_trigger_order(false);
				order.setTrigger_price(applyOrder.getTrigger_price());
				order.setOrder_price_type(applyOrder.getOrder_price_type());

				this.exchangeApplyOrderService.saveCreate(order);
				
				applyOrder.setClose_time(new Date());
				applyOrder.setState(ExchangeApplyOrder.STATE_CREATED);
				this.exchangeApplyOrderService.update(applyOrder);
			} else if ("close".equals(applyOrder.getOffset())) {
				
				ExchangeApplyOrder order = new ExchangeApplyOrder();
				order.setPartyId(applyOrder.getPartyId());
				order.setSymbol(applyOrder.getSymbol());
				order.setOffset(ExchangeApplyOrder.OFFSET_CLOSE);
				order.setVolume(applyOrder.getVolume());
				order.setPrice(applyOrder.getPrice());
				order.setIs_trigger_order(false);
				order.setTrigger_price(applyOrder.getTrigger_price());
				order.setOrder_price_type(applyOrder.getOrder_price_type());

				this.exchangeApplyOrderService.saveCreate(order);
				/**
				 * 平仓
				 */
				applyOrder.setClose_time(new Date());
				applyOrder.setState(ExchangeApplyOrder.STATE_CREATED);
				this.exchangeApplyOrderService.update(applyOrder);

			}

		} catch (Exception e) {
			logger.error("error:", e);
		} finally {
			if (lock) {
				ThreadUtils.sleep(100);
				ExchangeLock.remove(applyOrder.getOrder_no());
			}

		}

	}

	public void start(){
		new Thread(this, "ExchangeApplyOrderHandleJob").start();
		if (logger.isInfoEnabled())
			logger.info("币币委托单处理线程启动！");
	}

	public void setDataService(DataService dataService) {
		this.dataService = dataService;
	}

	public void setExchangeApplyOrderService(ExchangeApplyOrderService exchangeApplyOrderService) {
		this.exchangeApplyOrderService = exchangeApplyOrderService;
	}

}
