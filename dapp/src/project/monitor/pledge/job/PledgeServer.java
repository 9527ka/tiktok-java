package project.monitor.pledge.job;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.task.TaskExecutor;

import kernel.util.ThreadUtils;
import project.monitor.pledge.PledgeOrder;
import project.monitor.pledge.PledgeService;

public class PledgeServer implements InitializingBean, Runnable {
	private static Log logger = LogFactory.getLog(PledgeServer.class);
	/**
	 * 这个缓存池配置为0 线程最大只能配2-8之间
	 */
	private TaskExecutor taskExecutor;

	private List<PledgeOrder> items;

	private volatile boolean isRunning = false;

	private volatile boolean islock = false;

	private PledgeService pledgeService;

	/**
	 * 任务需要处理的数
	 */
	private AtomicInteger tasksNum = new AtomicInteger();

	public void afterPropertiesSet() throws Exception {
		new Thread(this, "PledgeServer").start();
		if (logger.isInfoEnabled()) {
			logger.info("质押收益清算服务！");
		}
	}

	/**
	 * 开始处理任务
	 */
	public void start(List<PledgeOrder> items) {
		this.items = items;
		this.tasksNum.set(items.size());
		this.isRunning = true;

	}

	/**
	 * 锁住，先拿到服务权限
	 */
	public void lock() {
		this.islock = true;
	}
	
	public void unlock() {
		this.islock = false;
	}


	/**
	 * 处理任务结束，持久化数据等操作
	 */
	public void stop() {

		this.isRunning = false;
		this.islock = false;

	}

	/**
	 * 确认服务是否在启动中，如果被启动，外部线程自行阻塞等到处理完后调用
	 * 
	 * @return
	 */
	public boolean isRunning() {

		return isRunning;
	}

	public boolean islock() {

		return islock;
	}

	@Override
	public void run() {
		while (true) {

			if (!isRunning) {
				ThreadUtils.sleep(1000);
				continue;
			}

			try {
				for (int i = 0; i < items.size(); i++) {
					PledgeOrder item = items.get(i);
					this.execute(item);
					/**
					 * 每秒处理20个
					 */
					ThreadUtils.sleep(50);
				}

				items = new ArrayList<PledgeOrder>();

			} catch (Throwable e) {
				logger.error("PledgeServer taskExecutor.execute() fail", e);

			} finally {
				ThreadUtils.sleep(1000);
			}
			this.stop();
		}

	}
	
	
	public void execute(PledgeOrder item) {
		try {
			pledgeService.saveIncomeProcess(item);

		} catch (Throwable t) {
			logger.error("PledgeServer taskExecutor.execute() fail", t);
		} 
	}

	public class HandleRunner implements Runnable {

		private PledgeOrder item;

		private PledgeServer miningServer;

		public HandleRunner(PledgeOrder item, PledgeServer miningServer) {
			this.item = item;
			this.miningServer = miningServer;
		}

		public void run() {
			try {
				pledgeService.saveIncomeProcess(item);

			} catch (Throwable t) {
				logger.error("PledgeServer taskExecutor.execute() fail", t);
			} finally {

				if (tasksNum.decrementAndGet() == 0) {

					/**
					 * 任务处理完，持久化数据并释放任务执行权限
					 */
					ThreadUtils.sleep(1000);
					miningServer.stop();
				}

			}

		}

	}

	public void setTaskExecutor(TaskExecutor taskExecutor) {
		this.taskExecutor = taskExecutor;
	}

	public void setPledgeService(PledgeService pledgeService) {
		this.pledgeService = pledgeService;
	}

}
