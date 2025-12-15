package project.monitor.pledge.job;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

import kernel.util.ThreadUtils;

public class OffLineEventRejectExecutingHandler implements RejectedExecutionHandler {

	public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
		//等1秒后重试
				ThreadUtils.sleep(1000);
				executor.execute(r);
	}

}
