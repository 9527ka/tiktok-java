package project.log.internal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import kernel.util.ThreadUtils;
import project.log.AbstractLog;
import project.log.LogService;
import project.log.SysLog;
import project.log.SysLogService;

public class SaveLogServer implements Runnable {
    private static Log logger = LogFactory.getLog(SaveLogServer.class);
    private LogService logService;
    private SysLogService sysLogService;

    public void run() {
        while (true)
            try {
                int size = AbstractLogQueue.size();
                /**
                 * 现量轮询一圈
                 */
                for (int i = 0; i < size; i++) {
                    AbstractLog log = AbstractLogQueue.poll();
                    if (log != null) {
                        if (log instanceof project.log.Log) {
                            logService.saveSync((project.log.Log) log);
                        } else if (log instanceof SysLog) {
                            sysLogService.saveSync((SysLog) log);
                        }
                        /**
                         * 1秒最多100个日志
                         */
                        ThreadUtils.sleep(10);

                    }
                }

            } catch (Throwable e) {
                logger.error("SmsServer taskExecutor.execute() fail", e);
            } finally {
                ThreadUtils.sleep(1000);
            }
    }

    public void start() {
        new Thread(this, "SaveLogServer").start();
        if (logger.isInfoEnabled()) {
            logger.info("启动SaveLogServer！");
        }

    }

    public void setLogService(LogService logService) {
        this.logService = logService;
    }

    public void setSysLogService(SysLogService sysLogService) {
        this.sysLogService = sysLogService;
    }
}
