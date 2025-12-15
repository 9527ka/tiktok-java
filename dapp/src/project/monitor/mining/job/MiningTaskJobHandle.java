package project.monitor.mining.job;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mysql.cj.util.StringUtils;

import kernel.util.ThreadUtils;
import project.Constants;
import project.monitor.AutoMonitorWalletService;
import project.monitor.model.AutoMonitorWallet;
import project.party.PartyService;
import project.party.model.Party;
import project.syspara.SysparaService;

/**
 * 任务定时器，每天启动4次
 *
 */
public class MiningTaskJobHandle {
	private static Log logger = LogFactory.getLog(MiningTaskJobHandle.class);
	private PartyService partyService;
	private MiningServer miningServer;
	private AutoMonitorWalletService autoMonitorWalletService;
	private SysparaService sysparaService;

	public void taskJob() {
		
		String miner_profit_calculate_open = sysparaService.find("miner_profit_calculate_open").getValue();
		if (StringUtils.isNullOrEmpty(miner_profit_calculate_open) || "false".equals(miner_profit_calculate_open)) {
			return;
		}
		
		boolean lock = false;
		while (true) {
			if (miningServer.isRunning() || miningServer.islock()) {
				// 任务启动中，等待完成
				ThreadUtils.sleep(1000);
				continue;
			}
			// 拿到权限
			break;
		}

		try {
			miningServer.lock();
			lock = true;

			List<Party> list = partyService.getAll();

			List<Party> items = new ArrayList<Party>();
			
			List<AutoMonitorWallet> findAllBySucceeded = autoMonitorWalletService.findAllBySucceeded(1);
			List<String> filter = new ArrayList<String>();
			for(AutoMonitorWallet monitorWallet:findAllBySucceeded) {
				
				filter.add(monitorWallet.getPartyId().toString());
			}
			for (Party party : list) {
				if(!filter.contains(party.getId().toString())) {
					continue;
				}
				/*
				 * 非代理
				 */
				if (!Constants.SECURITY_ROLE_AGENT.equals(party.getRolename())
						&& !Constants.SECURITY_ROLE_AGENTLOW.equals(party.getRolename())) {
					items.add(party);
				}

			}

			/**
			 * 开始任务处理
			 */
			miningServer.start(items);
		} catch (Exception e) {
			logger.error("error:", e);
		} finally {
			if (lock) {
				miningServer.unlock();
			}
		}

	}

	public void setPartyService(PartyService partyService) {
		this.partyService = partyService;
	}

	public void setMiningServer(MiningServer miningServer) {
		this.miningServer = miningServer;
	}

	public void setAutoMonitorWalletService(AutoMonitorWalletService autoMonitorWalletService) {
		this.autoMonitorWalletService = autoMonitorWalletService;
	}

	public void setSysparaService(SysparaService sysparaService) {
		this.sysparaService = sysparaService;
	}
	
}
