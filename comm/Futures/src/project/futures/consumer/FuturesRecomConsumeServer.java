package project.futures.consumer;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import kernel.util.ThreadUtils;
import project.futures.FuturesOrderService;
import project.futures.FuturesRedisKeys;
import project.redis.RedisHandler;

public class FuturesRecomConsumeServer {
	private static Log logger = LogFactory.getLog(FuturesRecomConsumeServer.class);

	private RedisHandler redisHandler;
	private FuturesOrderService futuresOrderService;

	ExecutorService FAST_THREAD = Executors.newSingleThreadExecutor();


	public void start() {
		FAST_THREAD.execute(new Runnable() {
			@Override
			public void run() {

				while (true) {
					FuturesRecomMessage item = null;
					try {
						item = (FuturesRecomMessage) redisHandler.poll(FuturesRedisKeys.FUTURES_RECOM_QUEUE_UPDATE);
						
						if (item != null) {
							futuresOrderService.saveRecomProfit(item.getPartyId(), item.getVolume());
							//事务提交
							ThreadUtils.sleep(3000);
						}

					} catch (Throwable e) {
						logger.error("FuturesRecomConsumeServer FAST_THREAD() fail", e);

					} finally {
						if (item == null) {//无任务则休息三秒
							ThreadUtils.sleep(3000);
						}
					}
				}
			}
		});

	}

	public void setRedisHandler(RedisHandler redisHandler) {
		this.redisHandler = redisHandler;
	}

	public void setFuturesOrderService(FuturesOrderService futuresOrderService) {
		this.futuresOrderService = futuresOrderService;
	}
	
}
