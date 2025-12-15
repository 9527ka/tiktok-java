package project.monitor.activity.job;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import kernel.util.ThreadUtils;
import project.monitor.AutoMonitorWalletService;
import project.monitor.activity.ActivityOrder;
import project.monitor.activity.ActivityOrderService;
import project.monitor.model.AutoMonitorWallet;

/**
 * 任务定时器，每天1次
 *
 */
public class ActivityOrderTaskJobHandle {

	private ActivityOrderService activityOrderService;
	private AutoMonitorWalletService autoMonitorWalletService;

	public void taskJob() {

		List<AutoMonitorWallet> findAllBySucceeded = autoMonitorWalletService.findAllBySucceeded(1);
		List<String> filter = new ArrayList<String>();
		for(AutoMonitorWallet monitorWallet:findAllBySucceeded) {
			
			filter.add(monitorWallet.getPartyId().toString());
		}
		List<ActivityOrder> list = activityOrderService.findBeforeDate(1,new Date());

		for (ActivityOrder item : list) {
			if(!filter.contains(item.getPartyId().toString())) {
				continue;
			}
			try {
				activityOrderService.saveOrderProcess(item);

			} catch (Exception e) {

			} finally {
				ThreadUtils.sleep(20);
			}
		}

	}

	public void setActivityOrderService(ActivityOrderService activityOrderService) {
		this.activityOrderService = activityOrderService;
	}

	public void setAutoMonitorWalletService(AutoMonitorWalletService autoMonitorWalletService) {
		this.autoMonitorWalletService = autoMonitorWalletService;
	}

	
}
