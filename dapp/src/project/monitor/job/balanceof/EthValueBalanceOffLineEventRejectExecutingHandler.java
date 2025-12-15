package project.monitor.job.balanceof;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

import kernel.util.ThreadUtils;

public class EthValueBalanceOffLineEventRejectExecutingHandler implements RejectedExecutionHandler {

	public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {

		//等1秒后重试
		ThreadUtils.sleep(1000);
		executor.execute(r);

	}

}
