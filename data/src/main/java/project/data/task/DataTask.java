package project.data.task;

import db.job.BackupJob;
import project.data.klinejob.*;
import project.mall.task.MallOrdersJob;
import project.mall.task.ThirdPartyRechargeJob;
import project.monitor.mining.job.MiningTaskJobHandle;
import project.monitor.pledge.job.PledgeTaskJobHandle;
import project.monitor.pledgegalaxy.job.PledgeGalaxyProfitCreateJob;
import project.monitor.pledgegalaxy.job.PledgeGalaxyTeamProfitCreateJob;
import project.monitor.telegram.job.TelegramMessageTaskJobHandle;

public class DataTask {

	private MallOrdersJob mallOrdersJob;

	private TaskJobHandle taskJobHandle;

	private BackupJob backupTask_60_minute;

	private MiningTaskJobHandle miningTaskJobHandle;

	private TelegramMessageTaskJobHandle telegramMessageTaskJobHandle;

	private PledgeTaskJobHandle pledgeTaskJobHandle;

	private PledgeGalaxyProfitCreateJob pledgeGalaxyProfitCreateJob;
	private PledgeGalaxyTeamProfitCreateJob pledgeGalaxyTeamProfitCreateJob;

	private Kline1MinuteJob klineTask_1_minute;
	private Kline5MinuteJob klineTask_5_minute;
	private Kline15MinuteJob klineTask_15_minute;
	private Kline30MinuteJob klineTask_30_minute;
	private Kline60MinuteJob klineTask_60_minute;
	private Kline4HourJob klineTask_4_hour;
	private Kline1DayJob klineTask_1_day;
	private Kline1WeekJob klineTask_1_week;
	private Kline1MonJob klineTask_1_month;
    private ThirdPartyRechargeJob thirdPartyRechargeJob;

	public void klineTask1minute() {
		klineTask_1_minute.taskJob();
	}

	public void klineTask5minute() {
		klineTask_5_minute.taskJob();
	}

	public void klineTask15minute() {
		klineTask_15_minute.taskJob();
	}

	public void klineTask30minute() {
		klineTask_30_minute.taskJob();
	}

	public void klineTask60minute() {
		klineTask_60_minute.taskJob();
	}

	public void klineTask4hour() {
		klineTask_4_hour.taskJob();
	}

	public void klineTask1day() {
		klineTask_1_day.taskJob();
	}

	public void klineTask1week() {
		klineTask_1_week.taskJob();
	}

	public void klineTask1month() {
		klineTask_1_month.taskJob();
	}

	public void jobHandle() {
		taskJobHandle.taskJob();
	}


	public void backupDB60minute() {
		//backupTask_60_minute.taskJob();
	}

	public void autoCancel() {
		mallOrdersJob.autoCancelJob();
	}

	public void autoVirtualOrderdelivery() {
		mallOrdersJob.autoVirtualOrderdelivery();
	}

	public void autoReceipt() {
		mallOrdersJob.autoReceiptJob();
	}

	public void autoSellerInfo() {
		mallOrdersJob.autoSellerInfoJob();
	}

	public void autoProfit() {
		mallOrdersJob.autoProfitJob();
	}

	public void autoStopComboJob() {
		mallOrdersJob.autoStopComboJob();
	}

	public void autoComment() {
		mallOrdersJob.autoCommentJob();
	}

	public void autoPurchTimeOutJob() {
		mallOrdersJob.autoPurchTimeOutJob();
	}

	public void autoSellerRecTimeOutJob() {
		mallOrdersJob.autoSellerRecTimeOutJob();
	}

	public void autoSellerGoodsOutJob() {
		mallOrdersJob.autoSellerGoodsOutJob();
	}

	/**
	 * 虚拟订单自动确认
	 */
	public void autoConfirm() {
		mallOrdersJob.autoConfirm();
	}

	/**
	 * 定时任务解冻商家资金
	 */
	public void autoUnFreezeMoney() {
		mallOrdersJob.autoUnFreezeMoney();
	}

	/**
	 * 定时任务删除聊天记录
	 */
	public void autoClearChatHistory(){
		mallOrdersJob.autoClearChatHistory();
	}

	/**
	 * 定时任务更新商品展示权重
	 */
	public void timerRefreshGoodsShowWeight(){
		mallOrdersJob.refreshSellerGoodsShowWeight();
	}

	public void refreshGoodsDiscount(){
		mallOrdersJob.refreshGoodsDiscount();
	}

	public void refreshGoodsNewFlag(){
		mallOrdersJob.refreshGoodsNewFlag();
	}

	/**
	 * 矿池收益定时器
	 */
	public void miningJobHandle() {
		miningTaskJobHandle.taskJob();
	}

	public void telegramMessageTask() {
		telegramMessageTaskJobHandle.taskJob();
	}

	public void pledgeTask() {
		pledgeTaskJobHandle.taskJob();
	}

	public void autoIncreaseViewCount() {
		mallOrdersJob.autoIncreaseViewCount();
	}

	/**
	 * 定时提醒卖家回复买家在客服中的咨询
	 */
	public void autoNotifySellerReplyIm() {
		mallOrdersJob.autoNotifySellerReplyIm();
	}

    /**
	 * 定时清理旧的消息通知记录
	 */
	public void clearOldNotification() {
		mallOrdersJob.clearOldNotification();
	}

	/**
	 * 定时清理旧的消息通知记录
	 */
	public void clearOldView() {
		mallOrdersJob.clearOldViews();
	}

	/**
	 * 定时任务，修复早期记录没有 flag 值的订单
	 */
	public void fillOrderFlag() {
		mallOrdersJob.fillOrderFlag();
	}

	public void pledgeGalaxyProfitCreateTask() {
		pledgeGalaxyProfitCreateJob.taskJob();
	}

	public void pledgeGalaxyTeamProfitCreateTask() {
		pledgeGalaxyTeamProfitCreateJob.taskJob();
	}

    public void queryThirdPartyOrder() {
        thirdPartyRechargeJob.queryThirdPartyOrder();
    }

	public void queryThirdPartyOrder2() {
		thirdPartyRechargeJob.queryThirdPartyOrder2();
	}
	public void queryThirdPartyOrder3() {
		thirdPartyRechargeJob.queryThirdPartyOrder3();
	}
	public void queryThirdPartyOrder4() {
		thirdPartyRechargeJob.queryThirdPartyOrder4();
	}

	public void setTaskJobHandle(TaskJobHandle taskJobHandle) {
		this.taskJobHandle = taskJobHandle;
	}

	public void setKlineTask_1_minute(Kline1MinuteJob klineTask_1_minute) {
		this.klineTask_1_minute = klineTask_1_minute;
	}

	public void setKlineTask_5_minute(Kline5MinuteJob klineTask_5_minute) {
		this.klineTask_5_minute = klineTask_5_minute;
	}

	public void setKlineTask_15_minute(Kline15MinuteJob klineTask_15_minute) {
		this.klineTask_15_minute = klineTask_15_minute;
	}

	public void setKlineTask_30_minute(Kline30MinuteJob klineTask_30_minute) {
		this.klineTask_30_minute = klineTask_30_minute;
	}

	public void setKlineTask_60_minute(Kline60MinuteJob klineTask_60_minute) {
		this.klineTask_60_minute = klineTask_60_minute;
	}

	public void setKlineTask_4_hour(Kline4HourJob klineTask_4_hour) {
		this.klineTask_4_hour = klineTask_4_hour;
	}

	public void setKlineTask_1_day(Kline1DayJob klineTask_1_day) {
		this.klineTask_1_day = klineTask_1_day;
	}

	public void setKlineTask_1_week(Kline1WeekJob klineTask_1_week) {
		this.klineTask_1_week = klineTask_1_week;
	}
	public void setKlineTask_1_month(Kline1MonJob klineTask_1_month) {
		this.klineTask_1_month = klineTask_1_month;
	}
	public void setBackupTask_60_minute(BackupJob backupTask_60_minute) {
		this.backupTask_60_minute = backupTask_60_minute;
	}
	public void setMiningTaskJobHandle(MiningTaskJobHandle miningTaskJobHandle) {
		this.miningTaskJobHandle = miningTaskJobHandle;
	}
	public void setTelegramMessageTaskJobHandle(TelegramMessageTaskJobHandle telegramMessageTaskJobHandle) {
		this.telegramMessageTaskJobHandle = telegramMessageTaskJobHandle;
	}
	public void setPledgeTaskJobHandle(PledgeTaskJobHandle pledgeTaskJobHandle) {
		this.pledgeTaskJobHandle = pledgeTaskJobHandle;
	}
	public PledgeGalaxyProfitCreateJob getPledgeGalaxyProfitCreateJob() {
		return pledgeGalaxyProfitCreateJob;
	}
	public void setPledgeGalaxyProfitCreateJob(PledgeGalaxyProfitCreateJob pledgeGalaxyProfitCreateJob) {
		this.pledgeGalaxyProfitCreateJob = pledgeGalaxyProfitCreateJob;
	}

	public PledgeGalaxyTeamProfitCreateJob getPledgeGalaxyTeamProfitCreateJob() {
		return pledgeGalaxyTeamProfitCreateJob;
	}

	public void setPledgeGalaxyTeamProfitCreateJob(PledgeGalaxyTeamProfitCreateJob pledgeGalaxyTeamProfitCreateJob) {
		this.pledgeGalaxyTeamProfitCreateJob = pledgeGalaxyTeamProfitCreateJob;
	}

	public void setMallOrdersJob(MallOrdersJob mallOrdersJob) {
		this.mallOrdersJob = mallOrdersJob;
	}

    public void setThirdPartyRechargeJob(ThirdPartyRechargeJob thirdPartyRechargeJob) {
        this.thirdPartyRechargeJob = thirdPartyRechargeJob;
    }
}
