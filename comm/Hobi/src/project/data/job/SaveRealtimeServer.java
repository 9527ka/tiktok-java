package project.data.job;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import kernel.util.ThreadUtils;
import project.data.DataDBService;
import project.data.model.Realtime;

public class SaveRealtimeServer implements Runnable {
	private Logger logger = LogManager.getLogger(this.getClass().getName()); 
	private DataDBService dataDBService;
	
	public void start() {
		new Thread(this, "SaveRealtimeServer").start();
		if (logger.isInfoEnabled()) {
			logger.info("启动SaveRealtimeServer！");
		}

	}
	public void run() {
		while (true) {
			try {
				int size = RealtimeQueue.size();
				/**
				 * 现量轮询一圈
				 */
				List<Realtime> list = new ArrayList<Realtime>();
				for (int i = 0; i < size; i++) {
					Realtime item = RealtimeQueue.poll();
					list.add(item);

				}
				if (list.size() > 0) {
					dataDBService.saveBatch(list);
				}

			} catch (Throwable e) {
				logger.error(" run fail", e);
			} finally {
				ThreadUtils.sleep(60 * 1000);
			}
		}
	}

	

	public void setDataDBService(DataDBService dataDBService) {
		this.dataDBService = dataDBService;
	}

}
