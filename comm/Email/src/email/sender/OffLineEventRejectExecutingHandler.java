package email.sender;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

import kernel.util.ThreadUtils;

public class OffLineEventRejectExecutingHandler implements RejectedExecutionHandler {
  
    
    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
        ThreadUtils.sleep(1000 * 10);
        executor.execute(r);
    }

}
