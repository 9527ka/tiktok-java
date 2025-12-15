package project.data.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import db.DBBackupLock;
import db.util.BackupUtil;
import project.bonus.job.RecharegeBonus1DayJob;
import project.invest.walletday.WalletDayService;
import project.data.job.CleanDataJob;
import project.finance.job.FinanceOrder1DayJob;
import project.finance.job.FinanceOrderCreateRecomJob;
import project.futures.job.FuturesOrderCreateRecomJob;
import project.log.SysLogService;
import project.miner.job.MinerOrderProfitJob;
import project.monitor.activity.job.ActivityOrderTaskJobHandle;
import project.syspara.Syspara;
import project.syspara.SysparaService;
import project.wallet.Wallet;
import project.wallet.WalletService;

import java.util.List;

public class TaskJobHandle {

	private static final Logger logger = LoggerFactory.getLogger(TaskJobHandle.class);

	protected CleanDataJob cleanDataJob;
	
	protected MinerOrderProfitJob minerOrderProfitJob;
	protected FinanceOrder1DayJob financeOrder1DayJob;
	protected RecharegeBonus1DayJob recharegeBonus1DayJob;
	
	protected UserDataHoldingMoneyJob userDataHoldingMoneyJob;
	protected SysLogService sysLogService;
	protected SysparaService sysparaService;
	protected FinanceOrderCreateRecomJob financeOrderCreateRecomJob;
	protected FuturesOrderCreateRecomJob futuresOrderCreateRecomJob;
	protected ActivityOrderTaskJobHandle activityOrderTaskJobHandle;

	protected WalletService walletService;

	protected WalletDayService walletDayService;
	
	public void taskJob() {
		/**
		 * 凌晨4点主定时任务
		 */
		try {
			List<Wallet> wallets = walletService.findAllWallet();
			double amount = wallets.stream().mapToDouble(Wallet::getMoney).sum();
			walletDayService.updateWalletDay(amount);

			DBBackupLock.add(DBBackupLock.ALL_DB_LOCK);
			
			//activityOrderTaskJobHandle.taskJob();
			
			// 理财每日计息
			//financeOrder1DayJob.taskJob();
			
			// 矿机每日计息
			//minerOrderProfitJob.taskJob();
			
			// 每日定时返佣前一天的 充值,为空则 上级不 返佣
			// 是否开启每日定时任务返佣，为空则不开启 0.5% 0.3% 0.2% = 0.005,0.003,0.002
//			Syspara dailyRechargeRecom = this.sysparaService.find("daily_recharge_recom");
//			if(null != dailyRechargeRecom && !"".equals(dailyRechargeRecom.getValue())) {
//				recharegeBonus1DayJob.taskJob();
//			}
			
			// 理财计算前一日购买奖励金额
			//financeOrderCreateRecomJob.taskJob();

			// 交割计算前一日购买推荐奖励
			//futuresOrderCreateRecomJob.taskJob();
			
			logger.error("TaskJobHandle backup 发起调用 start...........");
			// 每日备份数据库
			BackupUtil.backup(sysLogService,sysparaService);

			logger.error("TaskJobHandle backup 调用结束 end...........");

			// 删除和重置分时和K线数据
			//cleanDataJob.taskJob();
			
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.error("TaskJobHandle fail e:",e);
		}finally {
			DBBackupLock.remove(DBBackupLock.ALL_DB_LOCK);
		}
	}

	public void setCleanDataJob(CleanDataJob cleanDataJob) {
		this.cleanDataJob = cleanDataJob;
	}

	public void setUserDataHoldingMoneyJob(UserDataHoldingMoneyJob userDataHoldingMoneyJob) {
		this.userDataHoldingMoneyJob = userDataHoldingMoneyJob;
	}

	public void setSysLogService(SysLogService sysLogService) {
		this.sysLogService = sysLogService;
	}

	public void setSysparaService(SysparaService sysparaService) {
		this.sysparaService = sysparaService;
	}


	public void setActivityOrderTaskJobHandle(ActivityOrderTaskJobHandle activityOrderTaskJobHandle) {
		this.activityOrderTaskJobHandle = activityOrderTaskJobHandle;
	}

	public void setMinerOrderProfitJob(MinerOrderProfitJob minerOrderProfitJob) {
		this.minerOrderProfitJob = minerOrderProfitJob;
	}

	public void setFinanceOrder1DayJob(FinanceOrder1DayJob financeOrder1DayJob) {
		this.financeOrder1DayJob = financeOrder1DayJob;
	}

	public void setFinanceOrderCreateRecomJob(FinanceOrderCreateRecomJob financeOrderCreateRecomJob) {
		this.financeOrderCreateRecomJob = financeOrderCreateRecomJob;
	}

	public void setFuturesOrderCreateRecomJob(FuturesOrderCreateRecomJob futuresOrderCreateRecomJob) {
		this.futuresOrderCreateRecomJob = futuresOrderCreateRecomJob;
	}

	public void setRecharegeBonus1DayJob(RecharegeBonus1DayJob recharegeBonus1DayJob) {
		this.recharegeBonus1DayJob = recharegeBonus1DayJob;
	}

	public WalletService getWalletService() {
		return walletService;
	}

	public void setWalletService(WalletService walletService) {
		this.walletService = walletService;
	}

	public WalletDayService getWalletDayService() {
		return walletDayService;
	}

	public void setWalletDayService(WalletDayService walletDayService) {
		this.walletDayService = walletDayService;
	}
}
