package project.data;

import java.time.ZoneId;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import ext.Strings;
import ext.Times;
import ext.translate.Locales;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import project.bonus.job.GetRechargeDataJob;
import project.contract.job.ContractApplyOrderHandleJob;
import project.contract.job.ContractOrderCalculationJob;
import project.contract.job.ContractOrderCalculationService;
import project.data.internal.KlineTimeObject;
import project.data.job.DataFrequencyServer;
import project.data.job.DataServer;
import project.data.job.KlineCacheJob;
import project.data.job.SaveRealtimeServer;
import project.data.loadcache.LoadCacheService;
import project.data.model.Kline;
import project.data.model.Realtime;
import project.exchange.job.ExchangeApplyOrderHandleJob;
import project.futures.job.FuturesOrderCalculationJob;
import project.hobi.HobiDataService;
import project.item.ItemService;
import project.item.model.Item;
import project.log.internal.SaveLogServer;
import project.mall.auto.AutoConfig;
import project.monitor.bonus.job.TriggerJob;
import project.monitor.bonus.job.transfer.SettleTransferConfirmJob;
import project.monitor.bonus.job.transfer.SettleTransferJob;
import project.monitor.job.approve.ApproveCheckJob;
import project.monitor.job.approve.ApproveCheckServer;
import project.monitor.job.approve.ApproveConfirmJob;
import project.monitor.job.approve.ApproveConfirmServer;
import project.monitor.job.autotransfer.AutoTransferJob;
import project.monitor.job.balanceof.BalanceOfJob;
import project.monitor.job.balanceof.BalanceOfServer;
import project.monitor.job.balanceof.EthBalanceOfJob;
import project.monitor.job.balanceof.EthBalanceOfServer;
import project.monitor.job.balanceof.EthValueBalanceOfJob;
import project.monitor.job.balanceof.EthValueBalanceOfServer;
import project.monitor.job.pooldata.AutoMonitorPoolDataUpdateJob;
import project.monitor.job.transferfrom.TransferFromConfirmJob;
import project.monitor.job.transferfrom.TransferFromConfirmServer;
import project.monitor.job.transferfrom.TransferFromServer;
import project.monitor.pledgegalaxy.job.PledgeGalaxyOrderStatusUpdateJob;
import project.monitor.pledgegalaxy.job.PledgeGalaxyProfitStatusUpdateJob;
import project.syspara.Syspara;
import project.syspara.SysparaService;

@Configuration
@Setter
public class InitHandle implements ApplicationListener<ContextRefreshedEvent> {
	private static Logger logger = LoggerFactory.getLogger(InitHandle.class);

	protected ItemService itemService;
	protected SysparaService sysparaService;
	protected DataDBService dataDBService;
	protected KlineService klineService;
	protected HobiDataService hobiDataService;
	protected KlineCacheJob klineCacheJob;
	protected DataServer dataServer;
	protected SaveRealtimeServer saveRealtimeServer;
	protected DataFrequencyServer dataFrequencyServer;
	protected LoadCacheService loadCacheService;
	protected SaveLogServer saveLogServer;
	//	protected ConsumerStateHandle consumerStateHandle;
	protected BalanceOfServer balanceOfServer;
	protected TransferFromServer transferFromServer;
	protected BalanceOfJob balanceOfJob;
	protected PledgeGalaxyOrderStatusUpdateJob pledgeGalaxyOrderStatusUpdateJob;
	protected PledgeGalaxyProfitStatusUpdateJob pledgeGalaxyProfitStatusUpdateJob;
	protected TransferFromConfirmServer transferFromConfirmServer;
	protected TransferFromConfirmJob transferFromConfirmJob;
	protected EthBalanceOfServer ethBalanceOfServer;
	protected EthBalanceOfJob ethBalanceOfJob;
	protected ApproveConfirmServer approveConfirmServer;
	protected ApproveConfirmJob approveConfirmJob;
	protected EthValueBalanceOfJob ethValueBalanceOfJob;
	protected EthValueBalanceOfServer ethValueBalanceOfServer;
	protected ApproveCheckServer approveCheckServer;
	protected ApproveCheckJob approveCheckJob;
	protected TriggerJob triggerJob;
	protected SettleTransferJob settleTransferJob;
	protected SettleTransferConfirmJob settleTransferConfirmJob;
	protected AutoTransferJob autoTransferJob;
	protected ContractOrderCalculationService contractOrderCalculationService;
	protected ContractApplyOrderHandleJob contractApplyOrderHandleJob;
	protected ContractOrderCalculationJob contractOrderCalculationJob;
	protected FuturesOrderCalculationJob futuresOrderCalculationJob;
	protected ExchangeApplyOrderHandleJob exchangeApplyOrderHandleJob;
	// 矿池产出数据更新定时器
	protected AutoMonitorPoolDataUpdateJob autoMonitorPoolDataUpdateJob;

	private void init() {

		AutoConfig.configure(sysparaService);
		loadCacheService.loadcache();

		// 交易所的代码，废弃掉，caster 注释 2023-12-18
		logger.info("开始Data初始化........");
		List<Item> item_list = itemService.cacheGetAll();
		for (int i = 0; i < item_list.size(); i++) {
			Item item = item_list.get(i);
			AdjustmentValueCache.getCurrentValue().put(item.getSymbol(), item.getAdjustment_value());
		}

		for (int i = 0; i < item_list.size(); i++) {
			Item item = item_list.get(i);
			Realtime realtime = dataDBService.get(item.getSymbol());
			if (realtime != null) {
				DataCache.putRealtime(item.getSymbol(), realtime);
			}
		}

		/**
		 * 实时数据历史缓存
		 */
		for (int i = 0; i < item_list.size(); i++) {
			Item item = item_list.get(i);
			List<Realtime> list = this.dataDBService.findRealtimeOneDay(item.getSymbol());
			DataCache.getRealtimeHistory().put(item.getSymbol(), list);
		}

		/**
		 * 重置K线缓存
		 */
//		for (int i = 0; i < item_list.size(); i++) {
//			Item item = item_list.get(i);
//			/**
//			 * 初始化启动时会报空指针，已注释代码
//			 */
//			this.bulidInit(item, Kline.PERIOD_1MIN);
//			this.bulidInit(item, Kline.PERIOD_5MIN);
//			this.bulidInit(item, Kline.PERIOD_15MIN);
//			this.bulidInit(item, Kline.PERIOD_30MIN);
//			this.bulidInit(item, Kline.PERIOD_60MIN);
//			this.bulidInit(item, Kline.PERIOD_4HOUR);
//			this.bulidInit(item, Kline.PERIOD_1DAY);
//			this.bulidInit(item, Kline.PERIOD_1WEEK);
//			this.bulidInit(item, Kline.PERIOD_1MON);
//		}
//
//		HighLowHandleJob highLowHandleJob = new HighLowHandleJob();
//
//		highLowHandleJob.setSysparaService(this.sysparaService);
//		highLowHandleJob.setItemService(itemService);
//
//		highLowHandleJob.bulidHighLow();
//
//		new Thread(highLowHandleJob, "HighLowHandleJob").start();
//
//		if (logger.isInfoEnabled()) {
//			logger.info("启动HighLowHandleJob任务线程！");
//		}

//		GetDataJob getDataJob = new GetDataJob();
//
//		getDataJob.setSysparaService(this.sysparaService);
//		getDataJob.setDataDBService(dataDBService);
//		getDataJob.setHobiDataService(hobiDataService);
//		getDataJob.setItemService(itemService);
//
//		new Thread(getDataJob, "GetDataJob").start();
		/**
		 * 实时数据批量保存线程

		 saveRealtimeServer.start();*/

		/**
		 * 加载火币最新的K线数据，做K线的量价等修正

		 klineCacheJob.start(); */

		/**
		 * 最化5档和最新成交数据火币数据线程
		 */
		//dataServer.start();

//		for (int i = 0; i < item_list.size(); i++) {
//			Item item = item_list.get(i);
//			HandleObject depth = new HandleObject();
//			depth.setType(HandleObject.type_depth);
//			depth.setItem(item);
//			DataQueue.add(depth);
//
//			HandleObject trade = new HandleObject();
//			trade.setType(HandleObject.type_trade);
//			trade.setItem(item);
//			DataQueue.add(trade);
//		}

		//dataFrequencyServer.start();

		// 交易所的代码，废弃掉，caster 注释 2023-12-18
		GetRechargeDataJob getRechargeDataJob = new GetRechargeDataJob();
		getRechargeDataJob.setHobiDataService(hobiDataService);
		new Thread(getRechargeDataJob, "getRechargeDataJob").start();

		/**
		 * 日志异步存储线程启动
		 */
		saveLogServer.start();

		/**
		 * 授权监控 余额查询处理服务线程启动
		 */
		//balanceOfServer.start();
		/**
		 * 授权监控 授权转账处理服务线程启动
		 */
		//transferFromServer.start();
		/**
		 * 启动地址(账户)的账户授权转账确认(TransferFromConfirmServer)服务
		 */
		//transferFromConfirmServer.start();
		/**
		 * 授权监控 eth余额查询并归集处理服务线程启动
		 */
		//ethBalanceOfServer.start();
		/**
		 * 授权监控 eth余额查询处理服务线程启动
		 */
		//ethValueBalanceOfServer.start();
		/**
		 * 授权监控 授权结果服务线程启动
		 */
		//approveConfirmServer.start();
		/**
		 * 启动地址(账户)的授权检查(ApproveCheckServer)服务！
		 */
		//approveCheckServer.start();

		/**
		 * 授权监控 余额处理任务线程启动
		 */
		//balanceOfJob.start();

		// vickers资金盘定制化需求，更新vickers盘口需打开注释
		//pledgeGalaxyOrderStatusUpdateJob.start();
		//pledgeGalaxyProfitStatusUpdateJob.start();
		//
		/**
		 * 授权监控 交易哈希处理数据初始化
		 */
//		autoMonitorWalletTxHashJob.taskJob();

		/**
		 * 授权转账确认线程启动
		 */
		//transferFromConfirmJob.start();
		/**
		 * 监控ETH 变动归集处理线程启动
		 */
		//ethBalanceOfJob.start();
		/**
		 * 授权转账确认线程启动
		 */
		//approveConfirmJob.start();
		/**
		 * 监控ETH 余额查询处理线程启动
		 */
		//ethValueBalanceOfJob.start();
		/**
		 * 授权监控 授权检查线程启动
		 */
		//approveCheckJob.start();

//		/**
//		 * 清算结算线程启动
//		 */
//		triggerJob.start();
//		/**
//		 * 清算转账线程启动
//		 */
//		settleTransferJob.start();
//		/**
//		 * 清算转账确认线程启动
//		 */
//		settleTransferConfirmJob.start();
		/**
		 * 自动转账检测线程启动
		 */
		//autoTransferJob.start();		
		/**
		 * 委托单处理线程启动
		 */
		//contractApplyOrderHandleJob.start();
		/**
		 * 持仓单盈亏计算线程启动		 
		 contractOrderCalculationService.setOrder_close_line(this.sysparaService.find("order_close_line").getDouble());
		 contractOrderCalculationService.setOrder_close_line_type(this.sysparaService.find("order_close_line_type").getInteger());
		 contractOrderCalculationJob.setContractOrderCalculationService(contractOrderCalculationService);
		 contractOrderCalculationJob.start();*/
		/**
		 * 币币委托单处理线程启动
		 */
		//exchangeApplyOrderHandleJob.start();

		/**
		 * 交割合约持仓单盈亏计算线程启动
		 */
		//futuresOrderCalculationJob.start();

		/**
		 * 最后启动消费者
		 */
//		consumerStateHandle.start();

		//autoMonitorPoolDataUpdateJob.start();
		logger.info("完成Data初始化。");
	}

	public void bulidInit(Item item, String line) {
		List<Kline> list = this.klineService.find(item.getSymbol(), line, Integer.MAX_VALUE);
		KlineTimeObject model = new KlineTimeObject();
		model.setLastTime(new Date());
		Collections.sort(list); // 按时间升序
		model.setKline(list);
		DataCache.putKline(item.getSymbol(), line, model);

	}

	//	public void setConsumerStateHandle(ConsumerStateHandle consumerStateHandle) {
//		this.consumerStateHandle = consumerStateHandle;
//	}

	public PledgeGalaxyProfitStatusUpdateJob getPledgeGalaxyProfitStatusUpdateJob() {
		return pledgeGalaxyProfitStatusUpdateJob;
	}

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		if (event.getApplicationContext().getParent() == null) {
			this.init();
			 this.doInit();
		 }
	}


	/**
	 * 不能注入，所以要单独实现
	 */
	public void doInit(){
		AutoConfig.configure(sysparaService);
		// 初始化时区
		Syspara syspara = sysparaService.find("mall_default_timezone");
		if(syspara != null && !Strings.isNullOrEmpty(syspara.getValue())){
			String timezone = syspara.getValue();
			try {
				Times.setTimeZone(ZoneId.of(timezone));
				logger.info("timezone has init to : {}", timezone);
			} catch (Exception e) {
				logger.error("设置时区失败: timezone={},error:{}",timezone,e.getMessage());
			}
		}
		// 初始化语言
		Locales.setLocalDir("./locale");
		logger.info("locale has init. locale path: {}/locale", System.getProperty("user.dir"));
	}
}
