package project.contract.job;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import kernel.util.ThreadUtils;
import project.contract.ContractLock;
import project.contract.ContractOrder;
import project.contract.ContractOrderService;

public class ContractOrderCalculationJob implements Runnable {
	private static Log logger = LogFactory.getLog(ContractOrderCalculationJob.class);
	private ContractOrderService contractOrderService;
	private ContractOrderCalculationService contractOrderCalculationService;

	public void run() {

		while (true) {
			try {
				List<ContractOrder> list = this.contractOrderService.findSubmitted();
				for (int i = 0; i < list.size(); i++) {
					ContractOrder order = list.get(i);

					boolean lock = false;
					try {
						if (!ContractLock.add(order.getOrder_no())) {
							continue;
						}
						lock = true;
						this.contractOrderCalculationService.saveCalculation(order.getOrder_no());

					} catch (Throwable e) {
						logger.error("error:", e);
					} finally {
						if (lock) {
							/**
							 * 每秒处理20个订单
							 */
							ThreadUtils.sleep(100);
							ContractLock.remove(order.getOrder_no());
						}

					}

				}

			} catch (Throwable e) {
				e.printStackTrace();
				logger.error("run fail", e);
			} finally {
				/**
				 * 暂停0.1秒
				 */
				ThreadUtils.sleep(1000);
			}
		}
	}

	public void start(){

		new Thread(this, "ContractOrderCalculationJob").start();
		if (logger.isInfoEnabled()) {
			logger.info("持仓单盈亏计算线程启动！");
		}

	}

	public void setContractOrderService(ContractOrderService contractOrderService) {
		this.contractOrderService = contractOrderService;
	}

	public void setContractOrderCalculationService(ContractOrderCalculationService contractOrderCalculationService) {
		this.contractOrderCalculationService = contractOrderCalculationService;
	}

}
