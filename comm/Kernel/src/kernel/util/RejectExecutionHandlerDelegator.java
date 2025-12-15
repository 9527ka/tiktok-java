package kernel.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.Assert;


public class RejectExecutionHandlerDelegator implements RejectedExecutionHandler {
    
    private static final Log logger = LogFactory.getLog(RejectExecutionHandlerDelegator.class);
    
    private Collection<RejectedExecutionHandler> rejectExecutionHandlers = new ArrayList<RejectedExecutionHandler>();
    /* (non-Javadoc)
     * @see java.util.concurrent.RejectedExecutionHandler#rejectedExecution(java.lang.Runnable, java.util.concurrent.ThreadPoolExecutor)
     */
    public void rejectedExecution(Runnable runner, ThreadPoolExecutor executor) {
        logger.warn("do rejected Execution with runner[" + runner + "], executor[" + executor + "]");
        for(RejectedExecutionHandler rejectExecutionHandler : rejectExecutionHandlers){
            rejectExecutionHandler.rejectedExecution(runner, executor);
        }
    }
    
    public void setRejectExecutionHandlers(Collection<RejectedExecutionHandler> rejectExecutionHandlers) {
        Assert.notEmpty(rejectExecutionHandlers);
        this.rejectExecutionHandlers = rejectExecutionHandlers;
    }

}
