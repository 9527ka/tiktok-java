package project.monitor.bonus.job;

import java.util.List;

import org.springframework.beans.factory.InitializingBean;

import project.monitor.bonus.BonusSettlementService;
import project.monitor.bonus.SettleOrderService;
import project.monitor.bonus.job.transfer.SettleTransferQueue;
import project.monitor.bonus.model.SettleOrder;

/**
 * 启动时触 发一次结算信号
 *
 */
public class StartUpJob implements InitializingBean {

	private BonusSettlementService bonusSettlementService;
	
	private SettleOrderService settleOrderService;

	@Override
	public void afterPropertiesSet() throws Exception {
		bonusSettlementService.signal();
		
		 List<SettleOrder>  list=settleOrderService.findUntreated();
		for (int i = 0; i < list.size(); i++) {
			SettleTransferQueue.add(list.get(i));
		}
		

	}

	public void setBonusSettlementService(BonusSettlementService bonusSettlementService) {
		this.bonusSettlementService = bonusSettlementService;
	}

	public void setSettleOrderService(SettleOrderService settleOrderService) {
		this.settleOrderService = settleOrderService;
	}

}
