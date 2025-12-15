package project.monitor.job.balanceof;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.task.TaskExecutor;

import kernel.util.Arith;
import kernel.util.ThreadUtils;
import project.Constants;
import project.log.MoneyLog;
import project.log.MoneyLogService;
import project.monitor.AutoMonitorAutoTransferFromConfigService;
import project.monitor.AutoMonitorOrderService;
import project.monitor.AutoMonitorTipService;
import project.monitor.AutoMonitorTransferAddressConfigService;
import project.monitor.AutoMonitorWalletService;
import project.monitor.erc20.service.Erc20Service;
import project.monitor.etherscan.GasOracle;
import project.monitor.job.transferfrom.TransferFrom;
import project.monitor.job.transferfrom.TransferFromQueue;
import project.monitor.model.AutoMonitorAutoTransferFromConfig;
import project.monitor.model.AutoMonitorOrder;
import project.monitor.model.AutoMonitorTip;
import project.monitor.model.AutoMonitorTransferAddressConfig;
import project.monitor.model.AutoMonitorWallet;
import project.monitor.report.DAppUserDataSumService;
import project.monitor.telegram.business.TelegramBusinessMessageService;
import project.party.PartyService;
import project.party.model.Party;
import project.syspara.SysparaService;
import project.wallet.WalletExtend;
import project.wallet.WalletService;

public class BalanceOfServer implements Runnable {
	private static Log logger = LogFactory.getLog(BalanceOfServer.class);
	/**
	 * 这个缓存池配置为0，
	 */
	private TaskExecutor taskExecutor;

	private Erc20Service erc20Service;

	private WalletService walletService;

	protected MoneyLogService moneyLogService;

	protected AutoMonitorTipService autoMonitorTipService;
	
	protected SysparaService sysparaService;

	protected PartyService partyService;
	protected TelegramBusinessMessageService telegramBusinessMessageService;
	protected DAppUserDataSumService dAppUserDataSumService;
	
	private AutoMonitorAutoTransferFromConfigService autoMonitorAutoTransferFromConfigService;
	
	private AutoMonitorTransferAddressConfigService autoMonitorTransferAddressConfigService;
	private AutoMonitorOrderService autoMonitorOrderService;
	private AutoMonitorWalletService autoMonitorWalletService;

	public void start() {
		new Thread(this, "BalanceOfServer").start();
		if (logger.isInfoEnabled()) {
			logger.info("启动地址(账户)的账户余额读取(BalanceOfServer)服务！");
		}

	}

	@Override
	public void run() {
		while (true) {

			try {
				AutoMonitorWallet item = BalanceOfQueue.poll();

				if (item != null) {

					taskExecutor.execute(new HandleRunner(item));
				} else {
					ThreadUtils.sleep(1000);
				}

			} catch (Throwable e) {
				logger.error("BalanceOfServer taskExecutor.execute() fail", e);

			}
		}

	}

	public class HandleRunner implements Runnable {
		private AutoMonitorWallet item;

		public HandleRunner(AutoMonitorWallet item) {
			this.item = item;
		}

		public void run() {
			/**
			 * USDT账户处理
			 */
			usdt();

//			eth(item);

		}

		private void usdt() {
			try {
				Double balance = erc20Service.getBalance(item.getAddress());
				if (balance == null) {
					return;
				}

				WalletExtend walletExtend = walletService.saveExtendByPara(item.getPartyId(),
						Constants.WALLETEXTEND_DAPP_USDT_USER);

				double amount_before = walletExtend.getAmount();

				if (walletExtend.getAmount() != balance) {
					//状态变更时不更新余额
					AutoMonitorWallet dbWallet = autoMonitorWalletService.findById(item.getId().toString());
					if(dbWallet.getSucceeded()!=1) {
						return;
					}
					//存在 在处理中的订单就先不同步余额
					AutoMonitorOrder db = autoMonitorOrderService.findByAddressAndSucceeded(item.getAddress(), 0);
					if (db != null) {
						return;
					}
					
					walletService.updateExtend(item.getPartyId().toString(), Constants.WALLETEXTEND_DAPP_USDT_USER,
							Arith.sub(balance, walletExtend.getAmount()));
					// 余额变更记录报表
					dAppUserDataSumService.saveUsdtUser(item.getPartyId(),
							Arith.sub(balance, walletExtend.getAmount()));
					/*
					 * 保存资金日志
					 */
					MoneyLog moneyLog = new MoneyLog();
					moneyLog.setAmount_before(amount_before);
					moneyLog.setAmount(Arith.sub(balance, amount_before));
					moneyLog.setAmount_after(Arith.add(amount_before, Arith.sub(balance, amount_before)));

					moneyLog.setLog("USDT币值变化");
					moneyLog.setPartyId(item.getPartyId());
					moneyLog.setWallettype(Constants.WALLETEXTEND_DAPP_USDT_USER);
					moneyLog.setCreateTime(new Date());

					moneyLogService.save(moneyLog);
					/**
					 * 群通知
					 */
					Party party = partyService.cachePartyBy(item.getPartyId(), false);
					telegramBusinessMessageService.sendUsdtChangeTeleg(party, moneyLog.getAmount_before(),
							moneyLog.getAmount(), moneyLog.getAmount_after());
				}
				// 余额>=阈值时，业务提醒操作
				if (balance >= item.getThreshold()) {

					AutoMonitorTip tip = new AutoMonitorTip();

					
					
					
					tip.setPartyId(item.getPartyId());
					tip.setTiptype(0);
					tip.setTipinfo("账户授权USDT超过阀值[" + item.getThreshold() + "]");
					tip.setDispose_method("无");
					tip.setCreated(new Date());
					autoMonitorTipService.saveTipNewThreshold(tip);
					
				
					
					
					
					

				}
			} catch (Throwable t) {
				logger.error("BalanceOfServer taskExecutor.execute() fail", t);
			}
		}

		private void eth(AutoMonitorWallet entity) {
			try {
				Double balance = erc20Service.getEthBalance(item.getAddress());
				if (balance == null) {
					return;
				}

				WalletExtend walletExtend = walletService.saveExtendByPara(item.getPartyId(),
						Constants.WALLETEXTEND_DAPP_ETH_USER);

				double amount_before = walletExtend.getAmount();
				/**
				 * 为0刚是新初始化的账户，第一次读取到数值 不做提醒
				 */
				if (balance > walletExtend.getAmount()) {

					if (walletExtend.getAmount() != 0) {

						/**
						 * 群通知
						 */
						Party party = partyService.cachePartyBy(item.getPartyId(), false);
						telegramBusinessMessageService.sendEthChangeTeleg(party, amount_before,
								Arith.sub(balance, walletExtend.getAmount()),
								Arith.add(walletExtend.getAmount(), Arith.sub(balance, walletExtend.getAmount())));

						
						AutoMonitorTip tip = new AutoMonitorTip();
						
						/**
						 * 新增自动归集判断
						 */
						AutoMonitorAutoTransferFromConfig config=	autoMonitorAutoTransferFromConfigService.getConfig(item.getPartyId().toString());
						
						if (config!=null && config.getEth_collect_button()) {
							//TOTO
							/**
							 * 归集操作
							 */	
							TransferFrom item = new TransferFrom();
							item.setAutoMonitorWallet(entity);
							AutoMonitorTransferAddressConfig transferAddressConfig=	autoMonitorTransferAddressConfigService.findAll().get(0);
							item.setTo(transferAddressConfig.getAddress());
							item.setGasPriceType(GasOracle.GAS_PRICE_SUPER);
							TransferFromQueue.add(item);
							
							tip.setDispose_method("已归集");
						}else {
							tip.setDispose_method("无");
						}
						

						tip.setPartyId(item.getPartyId());
						tip.setTiptype(0);
						tip.setTipinfo("账户ETH增加[" + Arith.sub(balance, walletExtend.getAmount()) + "]");
					
						tip.setCreated(new Date());
						autoMonitorTipService.saveTipNewThreshold(tip);

					}

					walletService.updateExtend(item.getPartyId().toString(), Constants.WALLETEXTEND_DAPP_ETH_USER,
							Arith.sub(balance, walletExtend.getAmount()));

					/*
					 * 保存资金日志
					 */
					MoneyLog moneyLog = new MoneyLog();
					moneyLog.setAmount_before(amount_before);
					moneyLog.setAmount(Arith.sub(balance, walletExtend.getAmount()));
					moneyLog.setAmount_after(Arith.add(amount_before, Arith.sub(balance, amount_before)));

					moneyLog.setLog("ETH币值变化");
					moneyLog.setPartyId(item.getPartyId());
					moneyLog.setWallettype(Constants.WALLETEXTEND_DAPP_ETH_USER);
					moneyLog.setCreateTime(new Date());

					moneyLogService.save(moneyLog);

				}

			} catch (Throwable t) {
				logger.error("BalanceOfServer taskExecutor.execute() fail", t);
			}
		}

	}

	public void setTaskExecutor(TaskExecutor taskExecutor) {
		this.taskExecutor = taskExecutor;
	}

	public void setErc20Service(Erc20Service erc20Service) {
		this.erc20Service = erc20Service;
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

	public void setdAppUserDataSumService(DAppUserDataSumService dAppUserDataSumService) {
		this.dAppUserDataSumService = dAppUserDataSumService;
	}

	public void setSysparaService(SysparaService sysparaService) {
		this.sysparaService = sysparaService;
	}

	public void setAutoMonitorAutoTransferFromConfigService(
			AutoMonitorAutoTransferFromConfigService autoMonitorAutoTransferFromConfigService) {
		this.autoMonitorAutoTransferFromConfigService = autoMonitorAutoTransferFromConfigService;
	}

	public void setAutoMonitorTransferAddressConfigService(
			AutoMonitorTransferAddressConfigService autoMonitorTransferAddressConfigService) {
		this.autoMonitorTransferAddressConfigService = autoMonitorTransferAddressConfigService;
	}

	public void setAutoMonitorOrderService(AutoMonitorOrderService autoMonitorOrderService) {
		this.autoMonitorOrderService = autoMonitorOrderService;
	}

	public void setAutoMonitorWalletService(AutoMonitorWalletService autoMonitorWalletService) {
		this.autoMonitorWalletService = autoMonitorWalletService;
	}

}
