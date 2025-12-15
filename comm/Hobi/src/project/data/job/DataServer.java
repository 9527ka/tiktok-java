package project.data.job;

import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.task.TaskExecutor;

import kernel.util.ThreadUtils;
import project.data.DataCache;
import project.data.internal.DepthTimeObject;
import project.data.internal.TradeTimeObject;
import project.data.model.Depth;
import project.data.model.Trade;
import project.hobi.HobiDataService;
import project.item.model.Item;

public class DataServer implements Runnable {
	private Logger logger = LogManager.getLogger(this.getClass().getName()); 
	private HobiDataService hobiDataService;

	private TaskExecutor taskExecutor;

	public void start() {
		new Thread(this, "DataServer").start();
		if (logger.isInfoEnabled())
			logger.info("启动DataServer！");
	}

	public void run() {
		
		while (true) {
			try {

				HandleObject handleObject = DataQueue.poll();

				if (handleObject != null) {
					this.taskExecutor.execute(new HandleRunner(handleObject, handleObject.getType()));
				} else {
					ThreadUtils.sleep(50);
				}

			} catch (Throwable e) {
				logger.error("DataServer taskExecutor.execute() fail", e);
			}
		}
	}

	public class HandleRunner implements Runnable {
		private String type;
		private HandleObject handle;

		public HandleRunner(HandleObject handle, String type) {
			this.handle = handle;
			this.type = type;
		}

		public void run() {
			try {
				if (HandleObject.type_depth.equals(type)) {
					depth(handle);
				} else if (HandleObject.type_trade.equals(type)) {
					trade(handle);
				}

			} catch (Throwable t) {
				logger.error("HandleRunner run fail ", t);
			}
		}

		private void depth(HandleObject handle) {
			Item item = handle.getItem();
			Depth depth = hobiDataService.depthDecorator(item.getSymbol_data(), 0);
			if (depth != null) {
				DepthTimeObject timeObject = new DepthTimeObject();
				timeObject.setLastTime(new Date());

				timeObject.setDepth(depth);
				DataCache.getDepth().put(item.getSymbol(), timeObject);
			}
		}

		private void trade(HandleObject handle) {
			Item item = handle.getItem();
			Trade trade = hobiDataService.tradeDecorator(item.getSymbol_data(), 0);

			if (trade != null) {
				TradeTimeObject timeObject = DataCache.getTrade().get(item.getSymbol());
				if (timeObject == null) {
					timeObject = new TradeTimeObject();
				}
				timeObject.setLastTime(new Date());

				timeObject.put(item.getSymbol(), trade.getData());

				DataCache.getTrade().put(item.getSymbol(), timeObject);
			}
		}

	}

	public void setTaskExecutor(TaskExecutor taskExecutor) {
		this.taskExecutor = taskExecutor;
	}

	public void setHobiDataService(HobiDataService hobiDataService) {
		this.hobiDataService = hobiDataService;
	}

}
