package project.wallet.consumer;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import kernel.util.JsonUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import kernel.util.DateUtils;
import kernel.util.ThreadUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import project.redis.RedisHandler;
import project.wallet.WalletRedisKeys;

public class WalletConsumeServer {
	private static final Logger logger = LoggerFactory.getLogger(WalletConsumeServer.class);

	private volatile static Map<String, Date> lastTime = new ConcurrentHashMap<String, Date>();

	private static ConcurrentLinkedQueue<WalletMessage> WORKING_SLOW = new ConcurrentLinkedQueue<WalletMessage>();

	private RedisHandler redisHandler;

	private WalletDao walletDao;

	ExecutorService FAST_THREAD = Executors.newSingleThreadExecutor();

	ExecutorService SLOW_THREAD = Executors.newSingleThreadExecutor();

	public void start() {
		FAST_THREAD.execute(new Runnable() {
			@Override
			public void run() {

				while (true) {
					WalletMessage item = null;
					boolean lock = false;
					try {
						item = (WalletMessage) redisHandler.poll(WalletRedisKeys.WALLET_QUEUE_UPDATE);

						if (item != null) {
							if (!WalletLockFilter.add(item.getPartyId().toString())) {
								WORKING_SLOW.add(item);
								continue;
							}
							lock = true;
							if (lastTime.containsKey(item.getPartyId().toString())) {
								long millis = DateUtils.calcTimeBetweenInMillis(
										lastTime.get(item.getPartyId().toString()), new Date());
								if (millis >= 100) {
									lastTime.put(item.getPartyId().toString(), new Date());
									logger.info("------> WalletConsumeServer.run1 为用户:{} 累加账户余额:{}", item.getPartyId(), item.getMoney());
									walletDao.update(item);
								} else {// 进入慢队列
									WORKING_SLOW.add(item);
								}
							} else {
								lastTime.put(item.getPartyId().toString(), new Date());
								logger.info("------> WalletConsumeServer.run2 为用户:{} 累加账户余额:{}", item.getPartyId(), item.getMoney());
								walletDao.update(item);
							}
						}

					} catch (Throwable e) {
						logger.error("------> WalletConsumeServer FAST_THREAD() fail", e);

					} finally {
						if (item == null) {
							ThreadUtils.sleep(100);
						}
						if (lock) {
							WalletLockFilter.remove(item.getPartyId().toString());
						}
					}
				}
			}
		});

		SLOW_THREAD.execute(new Runnable() {
			@Override
			public void run() {

				while (true) {
					WalletMessage item = null;
					boolean lock = false;
					try {
						item = WORKING_SLOW.poll();

						if (item != null) {
							if (!WalletLockFilter.add(item.getPartyId().toString())) {
								logger.info("------> WalletConsumeServer.run3 账户变更信息再次放入 SLOW 队列:{}", JsonUtils.bean2Json(item));
								WORKING_SLOW.add(item);
								continue;
							}
							lock = true;
							if (lastTime.containsKey(item.getPartyId().toString())) {
								long millis = DateUtils.calcTimeBetweenInMillis(
										lastTime.get(item.getPartyId().toString()), new Date());
								if (millis >= 100) {// 超过100毫秒则执行一次
									lastTime.put(item.getPartyId().toString(), new Date());
									logger.info("------> WalletConsumeServer.run4 为用户:{} 累加账户余额:{}", item.getPartyId(), item.getMoney());
									walletDao.update(item);
								} else {// 重新进入慢队列，等待下次运行
									WORKING_SLOW.add(item);
									logger.info("------> WalletConsumeServer.run5 账户变更信息再次放入 SLOW 队列:{}", JsonUtils.bean2Json(item));
								}
							} else {
								lastTime.put(item.getPartyId().toString(), new Date());
								logger.info("------> WalletConsumeServer.run6 为用户:{} 累加账户余额:{}", item.getPartyId(), item.getMoney());
								walletDao.update(item);
							}
						} else {
							ThreadUtils.sleep(100);
						}

					} catch (Throwable e) {
						logger.error("WalletConsumeServer SLOW_THREAD.execute() fail", e);
					} finally {
						ThreadUtils.sleep(100);
						if (lock) {
							WalletLockFilter.remove(item.getPartyId().toString());
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
