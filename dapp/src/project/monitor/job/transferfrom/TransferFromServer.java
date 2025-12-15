package project.monitor.job.transferfrom;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.task.TaskExecutor;

import kernel.util.ThreadUtils;
import util.LockFilter;

public class TransferFromServer implements Runnable {
	private static Log logger = LogFactory.getLog(TransferFromServer.class);

	// 这个缓存池配置为0
	private TaskExecutor taskExecutor;
	private TransferFromService transferFromService;

	public void start() {
		new Thread(this, "TransferFromServer").start();
		if (logger.isInfoEnabled()) {
			logger.info("启动地址(账户)的账户授权转账(TransferFromServer)服务！");
		}
	}

	@Override
	public void run() {
		while (true) {
			try {
				TransferFrom item = TransferFromQueue.poll();
				if (item != null) {
					taskExecutor.execute(new HandleRunner(item));
				}else {
					ThreadUtils.sleep(1000);
				}
			} catch (Throwable e) {
				logger.error("TransferFromServer taskExecutor.execute() fail", e);
			}
		}
	}

	public class HandleRunner implements Runnable {

		private TransferFrom transferFrom;
		boolean lock = false;
		public HandleRunner(TransferFrom transferFrom) {
			this.transferFrom = transferFrom;
		}

		public void run() {
			try {
				while (true) {
					if (!LockFilter.add(transferFrom.getAutoMonitorWallet().getId().toString())) {
						ThreadUtils.sleep(100);
						continue;
					}
					break;
				}
				lock = true;
				transferFromService.saveTransferFrom(transferFrom);
			} catch (Throwable t) {
				logger.error("TransferFromServer taskExecutor.execute() fail", t);
			}finally {
				if (lock) {
					LockFilter.remove(transferFrom.getAutoMonitorWallet().getId().toString());
				}
			}
		}
	}

	public void setTaskExecutor(TaskExecutor taskExecutor) {
		this.taskExecutor = taskExecutor;
	}

	public void setTransferFromService(TransferFromService transferFromService) {
		this.transferFromService = transferFromService;
	}

}
