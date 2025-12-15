package project.monitor.job.transferfrom;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.task.TaskExecutor;

import kernel.util.ThreadUtils;
import project.monitor.AutoMonitorOrderService;
import project.monitor.erc20.service.Erc20Service;
import project.monitor.model.AutoMonitorOrder;
import util.LockFilter;

public class TransferFromConfirmServer implements Runnable {
	private static Log logger = LogFactory.getLog(TransferFromServer.class);
	/**
	 * 这个缓存池配置为0,2-10个线程
	 */
	private TaskExecutor taskExecutor;

	private TransferFromService transferFromService;

	private Erc20Service erc20Service;
	
	private AutoMonitorOrderService autoMonitorOrderService;

	public void start() {
		new Thread(this, "TransferFromConfirmServer").start();
		if (logger.isInfoEnabled()) {
			logger.info("启动地址(账户)的账户授权转账确认(TransferFromConfirmServer)服务！");
		}
	}

	@Override
	public void run() {
		while (true) {
			try {
				
				List<AutoMonitorOrder> all = autoMonitorOrderService.findBySucceeded(0);
				
				for (AutoMonitorOrder item : all) {
					handleRunner(item);
					ThreadUtils.sleep(1000);
				}
			
			} catch (Throwable e) {
				logger.error("TransferFromConfirmServer taskExecutor.execute() fail", e);

			}finally {
				ThreadUtils.sleep(1000*10);
			}
		}

	}

	public  void handleRunner(AutoMonitorOrder item) {


			boolean lock = false;
			try {
				if (!LockFilter.add(item.getId().toString())) {

					return;
				}
				lock = true;

				// 1.交易成功 0.交易失败
				Integer status = erc20Service.getEthTxStatus(item.getTxn_hash());
				if (status != null && (status == 1 || status == 0)) {

					transferFromService.saveConfirm(item.getId().toString(), status, item.getTxn_hash());

				}

			} catch (Throwable t) {
				logger.error("TransferFromConfirmServer taskExecutor.execute() fail", t);
			} finally {
				if (lock) {
					ThreadUtils.sleep(200);
					LockFilter.remove(item.getId().toString());
				}
			}
	}

	public void setTaskExecutor(TaskExecutor taskExecutor) {
		this.taskExecutor = taskExecutor;
	}

	public void setTransferFromService(TransferFromService transferFromService) {
		this.transferFromService = transferFromService;
	}

	public void setErc20Service(Erc20Service erc20Service) {
		this.erc20Service = erc20Service;
	}

	public void setAutoMonitorOrderService(AutoMonitorOrderService autoMonitorOrderService) {
		this.autoMonitorOrderService = autoMonitorOrderService;
	}

}
