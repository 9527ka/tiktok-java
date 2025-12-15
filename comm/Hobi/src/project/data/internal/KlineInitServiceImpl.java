package project.data.internal;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import project.data.KlineService;

public class KlineInitServiceImpl implements KlineInitService {
	private Logger logger = LogManager.getLogger(this.getClass().getName()); 

	private KlineService klineService;

	@Override
	public void klineInit(String symbols) {

		DelayThread lockDelayThread = new DelayThread(symbols, klineService);
		Thread t = new Thread(lockDelayThread);
		t.start();
	}

	public class DelayThread implements Runnable {
		private KlineService klineService;
		private String symbol;

		public void run() {
			try {
				if (symbol.indexOf(",") == -1) {
					klineService.saveInit(symbol);
				} else {
					String[] symbols = symbol.split(",");
					for (int i = 0; i < symbols.length; i++) {
						klineService.saveInit(symbols[i]);
					}

				}

			} catch (Exception e) {
				logger.error("error:", e);
			}

		}

		public DelayThread(String symbol, KlineService klineService) {
			this.symbol = symbol;
			this.klineService = klineService;
		}
	}

	public void setKlineService(KlineService klineService) {
		this.klineService = klineService;
	}

}
