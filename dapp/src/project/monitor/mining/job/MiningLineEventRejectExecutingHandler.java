package project.monitor.mining.job;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

import kernel.util.ThreadUtils;

public class MiningLineEventRejectExecutingHandler implements RejectedExecutionHandler {

	public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {

		// 等1秒后重试
		ThreadUtils.sleep(100);
		executor.execute(r);

	}

}
