package project.monitor.bonus.job.transfer;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kernel.util.StringUtils;
import kernel.util.ThreadUtils;
import project.monitor.bonus.BonusSettlementService;
import project.monitor.bonus.SettleOrderService;
import project.monitor.bonus.model.SettleOrder;
import project.monitor.erc20.service.Erc20Service;

public class SettleTransferConfirmJob implements Runnable {
	private static Logger logger = LoggerFactory.getLogger(SettleTransferConfirmJob.class);
	private SettleOrderService settleOrderService;

	private Erc20Service erc20Service;

	private BonusSettlementService bonusSettlementService;

	public void start() {
		new Thread(this, "SettleTransferConfirmJob").start();
		if (logger.isInfoEnabled()) {
			logger.info("结算确认线程启动");
		}

	}

	public void run() {
		while (true) {
			try {

				List<SettleOrder> list = settleOrderService.findBySucceeded(0);
				for (SettleOrder item : list) {
					this.handle(item);
				}

			} catch (Throwable e) {

				logger.error("BonusTransferConfirmJob run fail", e);

			} finally {

				ThreadUtils.sleep(1000 * 60);
			}
		}

	}

	public void handle(SettleOrder item) {
		try {

			if (StringUtils.isNullOrEmpty(item.getTxn_hash())) {
				return;
			}

			// 1.交易成功 0.交易失败
			Integer status = erc20Service.getEthTxStatus(item.getTxn_hash());

			if (status != null && (status == 1 || status == 0)) {

				bonusSettlementService.saveConfirm(item, status);

			}

		} catch (Throwable t) {

			logger.error("BonusTransferConfirmServer run() bonusOrderNo:" + item.getOrder_no() + " fail", t);
		} finally {
			
			ThreadUtils.sleep(1000 * 3);
		}
	}

	public void setSettleOrderService(SettleOrderService settleOrderService) {
		this.settleOrderService = settleOrderService;
	}

	public void setErc20Service(Erc20Service erc20Service) {
		this.erc20Service = erc20Service;
	}

	public void setBonusSettlementService(BonusSettlementService bonusSettlementService) {
		this.bonusSettlementService = bonusSettlementService;
	}

}
