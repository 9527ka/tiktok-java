package project.monitor.mining.job;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;

import kernel.util.Arith;
import kernel.util.ThreadUtils;
import project.monitor.AutoMonitorPoolDataService;
import project.monitor.mining.MiningConfig;
import project.monitor.mining.MiningConfigService;
import project.monitor.mining.MiningService;
import project.party.model.Party;
import project.party.model.UserRecom;
import project.party.recom.UserRecomService;

/**
 * 授权后，授权记录里面映射的金额
 * 计算挖矿收益（不需要质押）
 *
 */
public class MiningServer implements InitializingBean, Runnable {
	private static Log logger = LogFactory.getLog(MiningServer.class);

	private MiningConfigService miningConfigService;

	private UserRecomService userRecomService;

	private List<Party> items;

	private volatile boolean isRunning = false;

	private volatile boolean islock = false;

	private MiningService miningService;

	private AutoMonitorPoolDataService autoMonitorPoolDataService;
	
	// 推荐奖励的map
	// private volatile Map<String, MiningIncome> recomIncomes = new ConcurrentHashMap<>();

	private volatile Map<String, MiningIncome> incomes = new ConcurrentHashMap<>();

	// 任务需要处理的数
	private volatile AtomicInteger tasksNum = new AtomicInteger();

	// 300个批处理提交
	private final int SAVE_BATCH_INCOME = 300;

	private List<MiningConfig> configs = new ArrayList<MiningConfig>();

	public void afterPropertiesSet() throws Exception {
		new Thread(this, "MiningServer").start();
		if (logger.isInfoEnabled()) {
			logger.info("启动地址(账户)的账户余额读取(BalanceOfServer)服务！");
		}
	}

	// 开始处理任务
	public void start(List<Party> items) {
		this.items = items;
		this.tasksNum.set(items.size());
		this.incomes = new ConcurrentHashMap<>();
		this.configs = miningConfigService.getAll();
		this.isRunning = true;

	}

	// 锁住，先拿到服务权限
	public void lock() {
		this.islock = true;
	}
	
	
	public void unlock() {
		this.islock = false;
	}

	// 处理任务结束，持久化数据等操作
	public void stop() {
		
		try {
			List<MiningIncome> list = new ArrayList<MiningIncome>();

			Iterator<String> it = incomes.keySet().iterator();
			// 统计总收益eth，并更新到矿池数据
			double sumIncome = 0d;
			while (it.hasNext()) {
				String key = it.next();
				list.add(incomes.get(key));
				// 总额累加
				sumIncome = Arith.add(sumIncome, incomes.get(key).getValue());
				if (list.size() >= SAVE_BATCH_INCOME) {
					miningService.saveBatchIncome(list);
					list = new ArrayList<MiningIncome>();
				}
			}

			if (list.size() > 0) {
				// 没有整 除的，再处理一次
				miningService.saveBatchIncome(list);
			}

			if (sumIncome > 0) {
				autoMonitorPoolDataService.updateDefaultOutPut(sumIncome);
			}
			
//			List<MiningIncome> recomlist = new ArrayList<MiningIncome>();
//			
//			for (MiningIncome recomIncome : recomIncomes.values()) {
//				
//			}
			
			
			
		} catch (Throwable t) {
			logger.error("MiningServer taskExecutor.execute() fail", t);
		}

		this.isRunning = false;
		this.islock = false;

	}

	// 确认服务是否在启动中，如果被启动，外部线程自行阻塞等到处理完后调用
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
					Party item = items.get(i);

					this.execute(item);
					// 每秒处理20个，按1万用户评估，10分钟内处理完
					ThreadUtils.sleep(50);
				}
				// 处理完，置空
				items = new ArrayList<Party>();
			} catch (Throwable e) {
				logger.error("MiningServer taskExecutor.execute() fail", e);
			} 
			
			this.stop();
		}

	}

	private void execute(Party item) {
		try {
			List<UserRecom> parents = userRecomService.getParents(item.getId().toString());
			MiningConfig config = miningConfigService.getConfig(item.getId().toString(), parents, configs);
			List<MiningIncome> list = miningService.incomeProcess(item, config, parents);

			for (int i = 0; i < list.size(); i++) {
				MiningIncome income = incomes.get(list.get(i).getPartyId().toString());
				if (income != null) {
					income.setValue(Arith.add(income.getValue(), list.get(i).getValue()));
				} else {
					income = list.get(i);
				}

				incomes.put(list.get(i).getPartyId().toString(), income);
				
//				if(MiningIncome.TYPE_RECOM == income.getType()) {
//					recomIncomes.put(list.get(i).getPartyId().toString(), income);
//				}
			}

		} catch (Throwable t) {
			logger.error("MiningServer taskExecutor.execute() fail", t);
		}

	}

	public class HandleRunner implements Runnable {

		private Party item;

		private MiningServer miningServer;

		public HandleRunner(Party item, MiningServer miningServer) {
			this.item = item;
			this.miningServer = miningServer;
		}

		public void run() {
			try {
				List<UserRecom> parents = userRecomService.getParents(item.getId().toString());
				MiningConfig config = miningConfigService.getConfig(item.getId().toString(), parents, configs);
				// 获取挖矿收益集合
				List<MiningIncome> list = miningService.incomeProcess(item, config, parents);

				for (int i = 0; i < list.size(); i++) {
					MiningIncome income = incomes.get(list.get(i).getPartyId().toString());
					if (income != null) {
						income.setValue(Arith.add(income.getValue(), list.get(i).getValue()));
					} else {
						income = list.get(i);
					}
					incomes.put(list.get(i).getPartyId().toString(), income);
				}

			} catch (Throwable t) {
				logger.error("MiningServer taskExecutor.execute() fail", t);
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

	public void setMiningService(MiningService miningService) {
		this.miningService = miningService;
	}

	public void setMiningConfigService(MiningConfigService miningConfigService) {
		this.miningConfigService = miningConfigService;
	}

	public void setUserRecomService(UserRecomService userRecomService) {
		this.userRecomService = userRecomService;
	}

	public void setAutoMonitorPoolDataService(AutoMonitorPoolDataService autoMonitorPoolDataService) {
		this.autoMonitorPoolDataService = autoMonitorPoolDataService;
	}

}
