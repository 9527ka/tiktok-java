package project.monitor.pledgegalaxy.job;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import kernel.util.StringUtils;
import kernel.util.ThreadUtils;
import project.monitor.pledgegalaxy.PledgeGalaxyProfit;
import project.monitor.pledgegalaxy.PledgeGalaxyProfitService;
import project.monitor.pledgegalaxy.PledgeGalaxyStatusConstants;
import project.syspara.Syspara;
import project.syspara.SysparaService;
import util.LockFilter;

/**
 * 质押收益状态修改
 *
 */
public class PledgeGalaxyProfitStatusUpdateJob implements Runnable {

	private Logger logger = LogManager.getLogger(PledgeGalaxyProfitStatusUpdateJob.class);
	
	PledgeGalaxyProfitService pledgeGalaxyProfitService;
	SysparaService sysparaService;
	
	public void start() {
		
		// 是否开启质押2.0功能：true/开启；false/关闭；
		Syspara syspara = this.sysparaService.find("pledge_galaxy_open");
		if (null == syspara) {
			return;
		}
		
		String pledge_galaxy_open = syspara.getValue();
		if(StringUtils.isEmptyString(pledge_galaxy_open)) {
			return;
		}

		if ("true".equals(pledge_galaxy_open)) {
			new Thread(this, "PledgeGalaxyProfitStatusUpdateJob").start();
			if (logger.isInfoEnabled()) {
				logger.info("启动 更新质押收益状态(PledgeGalaxyProfitStatusUpdateJob)服务！");
			}
		}
	}
	
	public void run() {
		while (true) {
			try {
				List<PledgeGalaxyProfit> all = pledgeGalaxyProfitService.findByStatus(PledgeGalaxyStatusConstants.PROFIT_PENDING);
				if (null != all && all.size() > 0) {
					for (PledgeGalaxyProfit item : all) {
						handleRunner(item);
						ThreadUtils.sleep(10);
					}
				}
			} catch (Throwable e) {
				logger.error("PledgeGalaxyOrderStatusUpdateJob taskExecutor.execute() fail", e);
			}finally {
				ThreadUtils.sleep(1000 * 10);
			}
		}
	}
	
	public void handleRunner(PledgeGalaxyProfit item) {
		boolean lock = false;
		try {
			if (!LockFilter.add(item.getId().toString())) {
				return;
			}
			lock = true;
			item.setStatus(PledgeGalaxyStatusConstants.PROFIT_EXPIRED);
			pledgeGalaxyProfitService.update(item);
		} catch (Throwable t) {
			logger.error("PledgeGalaxyOrderStatusUpdateJob taskExecutor.execute() fail", t);
		} finally {
			if (lock) {
				ThreadUtils.sleep(200);
				LockFilter.remove(item.getId().toString());
			}
		}
	}

	public PledgeGalaxyProfitService getPledgeGalaxyProfitService() {
		return pledgeGalaxyProfitService;
	}

	public void setPledgeGalaxyProfitService(PledgeGalaxyProfitService pledgeGalaxyProfitService) {
		this.pledgeGalaxyProfitService = pledgeGalaxyProfitService;
	}

	public void setSysparaService(SysparaService sysparaService) {
		this.sysparaService = sysparaService;
	}

}
