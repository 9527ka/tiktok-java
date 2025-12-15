package email.sender;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.task.TaskExecutor;

import email.internal.InternalEmailSenderService;
import kernel.util.ThreadUtils;
import project.mall.task.MallOrdersJob;

import java.util.HashMap;
import java.util.Map;

/**
 * 邮件服务类，负责从短信消息队列取出短信消息并发送
 */
public class EmailServer implements InitializingBean, Runnable {

    //private static Log logger = LogFactory.getLog(EmailServer.class);
    private static org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(EmailServer.class);

    private TaskExecutor taskExecutor;
    
    private InternalEmailSenderService internalEmailSenderService;

    // 优化
    // 一次发送失败的邮箱，在 5 分钟内再次遇到发送邮件时直接忽略
    long invalidTimeout = 5L * 60L * 1000L;
    // 记录发送失败的邮箱上次真实发送的时间戳
    private Map<String, Long> failEmailMap = new HashMap();

    /**
     * 服务运行：
     * 1. 从消息队列获取message
     * 2.调用currentProvider发送短信
     */
    public void run() {
        while (true) {

            try {
                EmailMessage item = EmailMessageQueue.poll();

                if (item != null) {
                	logger.info("---> EmailServer.run 邮寄地址:{}", item.getTomail());

                    long now = System.currentTimeMillis();
                    Long lastSendTime = failEmailMap.get(item.getTomail());
                    if (lastSendTime != null && lastSendTime > 0) {
                        if (lastSendTime + invalidTimeout > now) {
                            // 上次发送失败，并且还没过时限，忽略本次发送
                            logger.warn("当前待发送消息的目标邮箱:" + item.getTomail() + " 在时刻:" + lastSendTime + " 发送失败，尚未过冷却期，忽略本次消息的发送, 消息内容:" + item.getContent());
                            continue;
                        }
                    }

                    taskExecutor.execute(new HandleRunner(item));
                }
                Thread.sleep(200);
            } catch (Throwable e) {
                logger.error("EmailServer taskExecutor.execute() fail", e);
            }
        }
    }

    public class HandleRunner implements Runnable {
        private EmailMessage item;

        public HandleRunner(EmailMessage item) {
            this.item = item;
        }

        public void run() {
            long now = System.currentTimeMillis();
            try {
            	internalEmailSenderService.send(item);
                logger.info("---> HandleRunner.run 向邮件地址:{} 发送邮件:{} 正常结束", item.getTomail(), item.getContent());
            } catch (Throwable t) {
                failEmailMap.put(this.item.getTomail(), now);
                logger.error("EmailServer taskExecutor.execute() fail, email:" + item.getTomail(), t);
            }
        }
    }


    public void afterPropertiesSet() throws Exception {

        new Thread(this, "EmailServer").start();
        if (logger.isInfoEnabled()) {
            logger.info("启动邮件发送服务！");
        }

    }
    

    public void setTaskExecutor(TaskExecutor taskExecutor) {
        this.taskExecutor = taskExecutor;
    }


	public void setInternalEmailSenderService(InternalEmailSenderService internalEmailSenderService) {
		this.internalEmailSenderService = internalEmailSenderService;
	}




  

}
