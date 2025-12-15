package smsbao.sender;

import email.sender.EmailServer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.task.TaskExecutor;

import kernel.util.ThreadUtils;
import smsbao.exception.InvalidMobileException;
import smsbao.internal.InternalSmsSenderService;

import java.util.HashMap;
import java.util.Map;

/**
 * 短信服务类，负责从短信消息队列取出短信消息并发送
 */
public class SmsServer implements InitializingBean, Runnable {

	//private static Log logger = LogFactory.getLog(SmsServer.class);
    private static org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(SmsServer.class);

	private TaskExecutor taskExecutor;

	private InternalSmsSenderService internalSmsSenderService;

	// 优化
	// 一次发送失败的手机号，在 5 分钟内再次遇到发送同一手机号的短信时直接忽略
	long invalidTimeout = 5L * 60L * 1000L;
	// 记录发送失败的手机号上次真实发送的时间戳
	private Map<String, Long> failMobileMap = new HashMap();

	/**
	 * 服务运行： 1. 从消息队列获取message 2.调用currentProvider发送短信
	 */
	public void run() {
		while (true) {

			try {
				SmsMessage item = SmsMessageQueue.poll();

				if (item != null) {
					long now = System.currentTimeMillis();
					Long lastSendTime = failMobileMap.get(item.getMobile());
					if (lastSendTime != null && lastSendTime > 0) {
						if (lastSendTime + invalidTimeout > now) {
							// 上次发送失败，并且还没过时限，忽略本次发送
							logger.warn("当前待发送消息的目标手机:" + item.getMobile() + " 在时刻:" + lastSendTime + " 发送失败，尚未过冷却期，忽略本次消息的发送, 消息内容:" + item.getContent());
							continue;
						}
					}

					taskExecutor.execute(new HandleRunner(item));
				} else {
					/*
					 * 限速，最多1秒2个
					 */
					ThreadUtils.sleep(500);
				}
			} catch (Throwable e) {
				logger.error("SmsServer taskExecutor.execute() fail", e);
			}
		}
	}

	public class HandleRunner implements Runnable {
		private SmsMessage item;

		public HandleRunner(SmsMessage item) {
			this.item = item;
		}

		public void run() {
			long now = System.currentTimeMillis();
			try {
				internalSmsSenderService.send(item);
			} catch (InvalidMobileException e) {
				failMobileMap.put(this.item.getMobile(), now);
				logger.error("SmsServer taskExecutor.execute() fail, mobile:" + item.getMobile(), e);
			} catch (Throwable t) {
				logger.error("SmsServer taskExecutor.execute() fail", t);
			}

		}

	}

	public void afterPropertiesSet() throws Exception {

		new Thread(this, "SmsbaoServer").start();
		if (logger.isInfoEnabled()) {
			logger.info("启动短信(Smsbao)服务！");
		}

	}

	public void setTaskExecutor(TaskExecutor taskExecutor) {
		this.taskExecutor = taskExecutor;
	}

	public void setInternalSmsSenderService(InternalSmsSenderService internalSmsSenderService) {
		this.internalSmsSenderService = internalSmsSenderService;
	}

}
