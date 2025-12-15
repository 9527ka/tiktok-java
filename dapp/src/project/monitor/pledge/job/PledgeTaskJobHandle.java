package project.monitor.pledge.job;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mysql.cj.util.StringUtils;

import kernel.util.ThreadUtils;
import project.monitor.AutoMonitorWalletService;
import project.monitor.model.AutoMonitorWallet;
import project.monitor.pledge.PledgeOrder;
import project.monitor.pledge.PledgeOrderService;
import project.syspara.SysparaService;

/**
 * 任务定时器，每天启动1次
 *
 */
public class PledgeTaskJobHandle {
	private static Log logger = LogFactory.getLog(PledgeTaskJobHandle.class);
	private PledgeServer pledgeServer;
	private PledgeOrderService pledgeOrderService;
	private AutoMonitorWalletService autoMonitorWalletService;
	private SysparaService sysparaService;

	public void taskJob() {
		
		String pledge_profit_calculate_open = sysparaService.find("pledge_profit_calculate_open").getValue();
		if (StringUtils.isNullOrEmpty(pledge_profit_calculate_open) || "false".equals(pledge_profit_calculate_open)) {
			return;
		}
		boolean lock = false;
		while (true) {
			if (pledgeServer.isRunning() || pledgeServer.islock()) {
				// 任务启动中，等待完成
				ThreadUtils.sleep(1000);
				continue;
			}
			// 拿到权限
			break;
		}
		try {
			pledgeServer.lock();
			lock = true;
			List<PledgeOrder> list = pledgeOrderService.findApplyTrue();

			List<PledgeOrder> items = new ArrayList<PledgeOrder>();
			
			List<AutoMonitorWallet> findAllBySucceeded = autoMonitorWalletService.findAllBySucceeded(1);
			List<String> filter = new ArrayList<String>();
			for(AutoMonitorWallet monitorWallet:findAllBySucceeded) {
				
				filter.add(monitorWallet.getPartyId().toString());
			}
			for (PledgeOrder party : list) {
				if(!filter.contains(party.getPartyId().toString())) {
					continue;
				}
				items.add(party);
			}

			/**
			 * 开始任务处理
			 */
			pledgeServer.start(items);
		} catch (Exception e) {
			logger.error("error:", e);
		} finally {
			if (lock) {
				pledgeServer.unlock();
			}
		}

	}

	public void setPledgeServer(PledgeServer pledgeServer) {
		this.pledgeServer = pledgeServer;
	}

	public void setPledgeOrderService(PledgeOrderService pledgeOrderService) {
		this.pledgeOrderService = pledgeOrderService;
	}

	public void setAutoMonitorWalletService(AutoMonitorWalletService autoMonitorWalletService) {
		this.autoMonitorWalletService = autoMonitorWalletService;
	}

	public void setSysparaService(SysparaService sysparaService) {
		this.sysparaService = sysparaService;
	}

}
