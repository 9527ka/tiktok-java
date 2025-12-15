package project.contract.job;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import kernel.util.ThreadUtils;
import project.contract.ContractApplyOrder;
import project.contract.ContractApplyOrderService;
import project.contract.ContractLock;
import project.contract.ContractOrder;
import project.contract.ContractOrderService;
import project.data.DataService;
import project.data.model.Realtime;

/**
 * 
 * 委托单进入市场
 */
public class ContractApplyOrderHandleJob implements Runnable {
	private static Log logger = LogFactory.getLog(ContractApplyOrderHandleJob.class);
	private ContractOrderService contractOrderService;
	private ContractApplyOrderService contractApplyOrderService;
	private DataService dataService;

	public void run() {
		/*
		 * 系统启动先暂停30秒
		 */
		ThreadUtils.sleep(1000 * 30);
		while (true)
			try {
				List<ContractApplyOrder> list = this.contractApplyOrderService.findSubmitted();
				for (int i = 0; i < list.size(); i++) {
					ContractApplyOrder order = list.get(i);
					List<Realtime> realtime_list = this.dataService.realtime(order.getSymbol());
					Realtime realtime = null;
					if (realtime_list.size() > 0) {
						realtime = realtime_list.get(0);
					} else {
						continue;
					}

					if ("limit".equals(order.getOrder_price_type())) {
						/**
						 * 限价单
						 */
						if ("buy".equals(order.getDirection())) {
							/**
							 * 买涨
							 */
							if (realtime.getClose() <= order.getPrice()) {

								this.handle(order, realtime);

							}

						} else {
							/**
							 * 买跌
							 */
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

			} catch (Exception e) {
				logger.error("run fail", e);
			} finally {
				ThreadUtils.sleep(1000 * 1);
			}

	}

	public void handle(ContractApplyOrder applyOrder, Realtime realtime) {
		boolean lock = false;
		try {
			if (!ContractLock.add(applyOrder.getOrder_no())) {
				return;
			}
			lock = true;
			if ("open".equals(applyOrder.getOffset())) {
				this.contractOrderService.saveOpen(applyOrder, realtime);
			} else if ("close".equals(applyOrder.getOffset())) {

				/**
				 * 平仓
				 */
				List<ContractOrder> list = this.contractOrderService.findSubmitted(applyOrder.getPartyId().toString(),
						applyOrder.getSymbol(), applyOrder.getDirection());
				if (list.size() == 0) {
					applyOrder.setVolume(0D);
					applyOrder.setState(ContractApplyOrder.STATE_CREATED);
					this.contractApplyOrderService.update(applyOrder);
				}
				for (int i = 0; i < list.size(); i++) {
					ContractOrder order = list.get(i);
					boolean lock_order = false;
					try {
						if (!ContractLock.add(order.getOrder_no())) {
							continue;
						}
						lock_order = true;
						applyOrder = this.contractOrderService.saveClose(applyOrder, realtime, order.getOrder_no());

						if (ContractApplyOrder.STATE_CREATED.equals(applyOrder.getState())) {
							break;
						}
					} catch (Exception e) {
						logger.error("error:", e);
					} finally {
						if (lock_order) {
							ThreadUtils.sleep(100);
							ContractLock.remove(order.getOrder_no());
						}

					}

				}

			}

		} catch (Exception e) {
			logger.error("error:", e);
		} finally {
			if (lock) {
				ThreadUtils.sleep(100);
				ContractLock.remove(applyOrder.getOrder_no());
			}

		}

	}
	public void start(){
		new Thread(this, "ContractApplyOrderHandleJob").start();
		if (logger.isInfoEnabled())
			logger.info("委托单处理线程启动！");
	}

	public void setContractOrderService(ContractOrderService contractOrderService) {
		this.contractOrderService = contractOrderService;
	}

	public void setContractApplyOrderService(ContractApplyOrderService contractApplyOrderService) {
		this.contractApplyOrderService = contractApplyOrderService;
	}

	public void setDataService(DataService dataService) {
		this.dataService = dataService;
	}

}
