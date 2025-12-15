package project.monitor.job.transferfrom;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

import kernel.util.ThreadUtils;

public class TransferFromLineEventRejectExecutingHandler implements RejectedExecutionHandler {

	public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {

		//等1秒后重试
		ThreadUtils.sleep(200);
		executor.execute(r);

	}

}
