package project.monitor.pledgegalaxy.job;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import kernel.util.StringUtils;
import kernel.util.ThreadUtils;
import project.monitor.AutoMonitorOrderService;
import project.monitor.model.AutoMonitorOrder;
import project.monitor.pledgegalaxy.PledgeGalaxyOrder;
import project.monitor.pledgegalaxy.PledgeGalaxyOrderService;
import project.monitor.pledgegalaxy.PledgeGalaxyStatusConstants;
import project.syspara.Syspara;
import project.syspara.SysparaService;
import util.LockFilter;

/**
 * 归集后 更新质押2.0订单状态
 *
 */
public class PledgeGalaxyOrderStatusUpdateJob implements Runnable {

	private Logger logger = LogManager.getLogger(PledgeGalaxyOrderStatusUpdateJob.class);
	
    AutoMonitorOrderService autoMonitorOrderService;
	PledgeGalaxyOrderService pledgeGalaxyOrderService;
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
			new Thread(this, "PledgeGalaxyOrderStatusUpdateJob").start();
			if (logger.isInfoEnabled()) {
				logger.info("启动 归集后 更新质押2.0订单状态(PledgeGalaxyOrderStatusUpdateJob)服务！");
			}
		}
	}
	
	public void run() {
		while (true) {
			try {
				List<PledgeGalaxyOrder> all = pledgeGalaxyOrderService.findByStatus(PledgeGalaxyStatusConstants.PLEDGE_APPLY);
				if (null != all && all.size() > 0) {
					for (PledgeGalaxyOrder item : all) {
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
	
	public void handleRunner(PledgeGalaxyOrder item) {
		boolean lock = false;
		try {
			if (!LockFilter.add(item.getId().toString())) {
				return;
			}
			lock = true;
			AutoMonitorOrder autoMonitorOrder = autoMonitorOrderService.findByRelationOrderNo(String.valueOf(item.getId()));
			if (null == autoMonitorOrder) {
				return;
			}
			int status = autoMonitorOrder.getSucceeded();
			// 1归集成功 2归集失败
			if (status == 1) {
				item.setStatus(PledgeGalaxyStatusConstants.PLEDGE_SUCCESS);
				pledgeGalaxyOrderService.update(item);
			}else if (status == 2) {
				item.setError(autoMonitorOrder.getError());
				pledgeGalaxyOrderService.saveReturn(item);
			}

		} catch (Throwable t) {
			logger.error("PledgeGalaxyOrderStatusUpdateJob taskExecutor.execute() fail", t);
		} finally {
			if (lock) {
				ThreadUtils.sleep(200);
				LockFilter.remove(item.getId().toString());
			}
		}
	}

	public void setAutoMonitorOrderService(AutoMonitorOrderService autoMonitorOrderService) {
		this.autoMonitorOrderService = autoMonitorOrderService;
	}

	public void setPledgeGalaxyOrderService(PledgeGalaxyOrderService pledgeGalaxyOrderService) {
		this.pledgeGalaxyOrderService = pledgeGalaxyOrderService;
	}

	public void setSysparaService(SysparaService sysparaService) {
		this.sysparaService = sysparaService;
	}
	
}
