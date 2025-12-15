package project.monitor.bonus.internal;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kernel.util.Arith;
import project.monitor.AutoMonitorOrderService;
import project.monitor.bonus.AutoMonitorSettleAddressConfigService;
import project.monitor.bonus.BonusSettlementService;
import project.monitor.bonus.SettleOrderService;
import project.monitor.bonus.job.Signal;
import project.monitor.bonus.job.TriggerQueue;
import project.monitor.bonus.job.transfer.SettleTransferQueue;
import project.monitor.bonus.model.SettleAddressConfig;
import project.monitor.bonus.model.SettleOrder;
import project.monitor.erc20.dto.TransactionResponseDto;
import project.monitor.erc20.service.Erc20Service;
import project.monitor.etherscan.GasOracle;
import project.monitor.model.AutoMonitorAddressConfig;
import project.monitor.model.AutoMonitorOrder;
import project.monitor.report.DAppUserDataSumService;
import project.monitor.telegram.business.TelegramBusinessMessageService;
import project.syspara.SysparaService;
import project.tip.TipConstants;
import project.tip.TipService;
import util.DateUtil;
import util.RandomUtil;

public class BonusSettlementServiceImpl implements BonusSettlementService {
	protected AutoMonitorOrderService autoMonitorOrderService;
	protected SysparaService sysparaService;
	protected SettleOrderService settleOrderService;
	protected Erc20Service erc20Service;
	protected AutoMonitorSettleAddressConfigService autoMonitorSettleAddressConfigService;
	protected DAppUserDataSumService dAppUserDataSumService;

	protected TelegramBusinessMessageService telegramBusinessMessageService;
	protected TipService tipService;
	
	@Override
	public void signal() {
		TriggerQueue.add(new Signal());
	}

	@Override
	public void saveHandle(Signal item) {
//		List<AutoMonitorOrder> list = autoMonitorOrderService.findBySucceeded(1);
		//转账完成且未结算的订单
		List<AutoMonitorOrder> list = autoMonitorOrderService.findBySucceededAndSettleState(1,0);

		if (list.size() <= 0) {
			return;
		}
		
		SettleAddressConfig settleAddressConfig = autoMonitorSettleAddressConfigService.findDefault();
		if(settleAddressConfig.getSettle_rate()<=0d) {
			//收益率为0不生成清算订单处理
			return;
		}
		if(item.isSettleLast()) {
			single(list, settleAddressConfig);
			return;
		}
		switch (settleAddressConfig.getSettle_type()) {
		case 1:// 1.每笔都分成
			single(list,settleAddressConfig);
			break;
		case 2:// 2.达标后一起分成
			batch(list,settleAddressConfig);
			break;
		default:
			break;
		}
	}

	public void single(List<AutoMonitorOrder> list,SettleAddressConfig settleAddressConfig) {

		SettleOrder item = this.build(list,settleAddressConfig);
		SettleTransferQueue.add(item);
	}

	public void batch(List<AutoMonitorOrder> list,SettleAddressConfig settleAddressConfig) {
		// 一起分成的达标线，例如达到1w后才开始分成
		Double settlement_limit = settleAddressConfig.getSettle_limit_amount();

		double amount = 0;
		for (int i = 0; i < list.size(); i++) {
			amount = Arith.add(amount, list.get(i).getVolume());
		}

		if (amount < settlement_limit) {
			return;
		}

		SettleOrder item = this.build(list,settleAddressConfig);
		SettleTransferQueue.add(item);
	}

	private SettleOrder build(List<AutoMonitorOrder> list,SettleAddressConfig settleAddressConfig) {

		SettleOrder settleOrder =  new SettleOrder();
		settleOrder.setOrder_no(DateUtil.getToday("yyMMddHHmmss") + RandomUtil.getRandomNum(8));
		settleOrder.setFrom_address(settleAddressConfig.getChannel_address());
		settleOrder.setTo_address(settleAddressConfig.getSettle_address());
		settleOrder.setCreated(new Date());
		settleOrder.setVolume(0d);
		for (int i = 0; i < list.size(); i++) {
			AutoMonitorOrder order = list.get(i);

			settleOrder.setVolume(Arith.add(settleOrder.getVolume(), order.getSettle_amount()));

			order.setSettle_state(1);
			order.setSettle_order_no(settleOrder.getOrder_no());
			order.setSettle_time(new Date());

			autoMonitorOrderService.update(order);

		}

		this.settleOrderService.save(settleOrder);

		return settleOrder;
	}

	@Override
	public void saveTransfer(SettleOrder settleOrder) {

		SettleAddressConfig settleAddressConfig = autoMonitorSettleAddressConfigService.findDefault();
		if (!settleOrder.getFrom_address().equals(settleAddressConfig.getChannel_address())) {
			settleOrder.setSucceeded(2);
			settleOrder.setError("地址鉴权错误");
			settleOrderService.update(settleOrder);
			
			this.recover(settleOrder);
			
			return;
		}

		TransactionResponseDto transactionResponseDto = erc20Service.tokenTrans(settleOrder.getFrom_address(),
				settleOrder.getTo_address(), String.valueOf(settleOrder.getVolume()),
				autoMonitorSettleAddressConfigService.desDecrypt(settleAddressConfig.getChannel_private_key()), GasOracle.GAS_PRICE_NORMAL);

		// 网络请求成功会返回一个hash
		if (TransactionResponseDto.CODE_LOCAL_SUCCESS.equals(transactionResponseDto.getCode())) {
			settleOrder.setTxn_hash(transactionResponseDto.getHash());
			settleOrderService.update(settleOrder);
		} else {
			settleOrder.setSucceeded(2);
			settleOrder.setError(transactionResponseDto.getError());
			
			settleOrderService.update(settleOrder);
			
			this.recover(settleOrder);
			/**
			 * 补通知
			 */
			telegramBusinessMessageService.sendSettleTransferErrorTeleg(settleOrder);
		}

		settleOrderService.update(settleOrder);

	}

	@Override
	public void saveConfirm(SettleOrder settleOrder, Integer status) {
		
		if (status == 0) {
			/**
			 * 交易失败
			 */

			settleOrder.setSucceeded(2);
			
			settleOrderService.update(settleOrder);
			
			this.recover(settleOrder);
			
			/**
			 * 补通知
			 */
			telegramBusinessMessageService.sendSettleTransferErrorTeleg(settleOrder);
		} else if (status == 1) {
			/**
			 * 交易成功
			 */

			/*
			 * 保存订单状态
			 */

			settleOrder.setSucceeded(1);
			
			settleOrderService.update(settleOrder);
			
			autoMonitorOrderService.updateSucceedByBonusOrderNo(settleOrder.getOrder_no());

			/*
			 * 报表处理
			 */
			dAppUserDataSumService.saveSettle(settleOrder.getVolume());

		}

	}
	
	
	private void recover(SettleOrder settleOrder) {
		SettleOrder rebirth = new SettleOrder();
		rebirth.setCreated(new Date());
		rebirth.setFrom_address(settleOrder.getFrom_address());
		rebirth.setTo_address(settleOrder.getTo_address());
		rebirth.setVolume(settleOrder.getVolume());
		rebirth.setOrder_no(settleOrder.getOrder_no());
		rebirth.setSucceeded(-1);
		
		this.settleOrderService.save(rebirth);
		
		tipService.saveTip(rebirth.getId().toString(), TipConstants.AUTO_MONITOR_SETTLE);
	}

	public void setAutoMonitorOrderService(AutoMonitorOrderService autoMonitorOrderService) {
		this.autoMonitorOrderService = autoMonitorOrderService;
	}

	public void setSysparaService(SysparaService sysparaService) {
		this.sysparaService = sysparaService;
	}

	public void setSettleOrderService(SettleOrderService settleOrderService) {
		this.settleOrderService = settleOrderService;
	}

	public void setErc20Service(Erc20Service erc20Service) {
		this.erc20Service = erc20Service;
	}

	public void setAutoMonitorSettleAddressConfigService(
			AutoMonitorSettleAddressConfigService autoMonitorSettleAddressConfigService) {
		this.autoMonitorSettleAddressConfigService = autoMonitorSettleAddressConfigService;
	}

	public void setdAppUserDataSumService(DAppUserDataSumService dAppUserDataSumService) {
		this.dAppUserDataSumService = dAppUserDataSumService;
	}

	public void setTelegramBusinessMessageService(TelegramBusinessMessageService telegramBusinessMessageService) {
		this.telegramBusinessMessageService = telegramBusinessMessageService;
	}

	public void setTipService(TipService tipService) {
		this.tipService = tipService;
	}

}
