package project.wallet.consumer;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import kernel.util.DateUtils;
import kernel.util.ThreadUtils;
import project.redis.RedisHandler;
import project.wallet.WalletRedisKeys;

public class WalletExtendConsumeServer {
	private static Log logger = LogFactory.getLog(WalletExtendConsumeServer.class);
	private volatile static Map<String, Date> lastTime = new ConcurrentHashMap<String, Date>();

	private static ConcurrentLinkedQueue<WalletExtendMessage> WORKING_SLOW = new ConcurrentLinkedQueue<WalletExtendMessage>();

	private RedisHandler redisHandler;
	private WalletDao walletDao;

	ExecutorService FAST_THREAD = Executors.newSingleThreadExecutor();

	ExecutorService SLOW_THREAD = Executors.newSingleThreadExecutor();

	public void start() {
		FAST_THREAD.execute(new Runnable() {
			@Override
			public void run() {

				while (true) {
					WalletExtendMessage item = null;
					boolean lock = false;
					try {
						item = (WalletExtendMessage) redisHandler.poll(WalletRedisKeys.WALLET_EXTEND_QUEUE_UPDATE);

						if (item != null) {
							if (!WalletLockFilter.add(item.getPartyId().toString() + item.getWalletType())) {
								WORKING_SLOW.add(item);
								continue;
							}
							lock = true;
							if (lastTime.containsKey(item.getPartyId().toString())) {
								long millis = DateUtils.calcTimeBetweenInMillis(
										lastTime.get(item.getPartyId().toString()), new Date());
								if (millis >= 100) {
									lastTime.put(item.getPartyId().toString(), new Date());
									walletDao.update(item);
								} else {// 进入慢队列
									WORKING_SLOW.add(item);
								}

							} else {
								lastTime.put(item.getPartyId().toString(), new Date());
								walletDao.update(item);
							}

						}

					} catch (Throwable e) {
						logger.error("WalletConsumeServer FAST_THREAD() fail", e);

					} finally {
						if (item == null) {
							ThreadUtils.sleep(100);
						}
						if (lock) {
							WalletLockFilter.remove(item.getPartyId().toString() + item.getWalletType());
						}
					}
				}
			}
		});

		SLOW_THREAD.execute(new Runnable() {
			@Override
			public void run() {

				while (true) {
					WalletExtendMessage item = null;
					boolean lock = false;
					try {
						item = WORKING_SLOW.poll();

						if (item != null) {
							if (!WalletLockFilter.add(item.getPartyId().toString() + item.getWalletType())) {
								WORKING_SLOW.add(item);
								continue;
							}
							lock = true;
							if (lastTime.containsKey(item.getPartyId().toString())) {
								long millis = DateUtils.calcTimeBetweenInMillis(
										lastTime.get(item.getPartyId().toString()), new Date());
								if (millis >= 100) {// 超过500毫秒则执行一次
									lastTime.put(item.getPartyId().toString(), new Date());
									walletDao.update(item);
								} else {// 重新进入慢队列，等待下次运行
									WORKING_SLOW.add(item);
								}
							} else {
								lastTime.put(item.getPartyId().toString(), new Date());
								walletDao.update(item);
							}

						} else {
							ThreadUtils.sleep(100);
						}

					} catch (Throwable e) {
						logger.error("SmsServer taskExecutor.execute() fail", e);

					} finally {
						ThreadUtils.sleep(100);
						if (lock) {
							WalletLockFilter.remove(item.getPartyId().toString() + item.getWalletType());
						}
					}
				}
			}
		});

	}

	public void setRedisHandler(RedisHandler redisHandler) {
		this.redisHandler = redisHandler;
	}

	public void setWalletDao(WalletDao walletDao) {
		this.walletDao = walletDao;
	}

}
