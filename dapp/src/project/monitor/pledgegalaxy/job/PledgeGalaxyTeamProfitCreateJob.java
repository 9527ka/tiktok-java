package project.monitor.pledgegalaxy.job;

import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.util.CollectionUtils;

import com.mysql.cj.util.StringUtils;

import kernel.web.Page;
import project.monitor.pledgegalaxy.PledgeGalaxyOrder;
import project.monitor.pledgegalaxy.PledgeGalaxyOrderService;
import project.syspara.SysparaService;

/**
 * 团队收益记录生成JOB
 *
 */
public class PledgeGalaxyTeamProfitCreateJob {

	private Logger logger = LogManager.getLogger(PledgeGalaxyTeamProfitCreateJob.class);
	
	protected PledgeGalaxyOrderService pledgeGalaxyOrderService;
	protected SysparaService sysparaService;
	
	public void taskJob() {
		String pledgeGalaxyOpen = sysparaService.find("pledge_galaxy_open").getValue();
		if (StringUtils.isNullOrEmpty(pledgeGalaxyOpen) || "false".equals(pledgeGalaxyOpen)) {
			return;
		}
		
		String projectType = this.sysparaService.find("project_type").getValue();
		
		try {
			int pageNo = 1;
			int pageSize = 300;
			
			pledgeGalaxyOrderService.cacheRecomProfitClear();
			Date date = new Date();
			while (true) {
				
				Page page = pledgeGalaxyOrderService.pagedQueryComputeOrder(pageNo, pageSize, date);
				List<PledgeGalaxyOrder> galaxyOrders = page.getElements();
				// 分页没数据时表示已经计算结束
				if (CollectionUtils.isEmpty(galaxyOrders)) {
					break;
				}
				
				try {
					pledgeGalaxyOrderService.saveTeamProfit(galaxyOrders, projectType);
				} catch (Throwable e) {
					logger.error("error:", e);
				}
				logger.info("miner profit finished ,count:" + galaxyOrders.size());
				pageNo++;
			}
			
			// 用户收益计算完，计算推荐人收益
			pledgeGalaxyOrderService.insertTeamProfit();			
		} catch (Throwable e) {
			logger.error("PledgeGalaxyOrderProfit run fail", e);
		}
	
	}

	public PledgeGalaxyOrderService getPledgeGalaxyOrderService() {
		return pledgeGalaxyOrderService;
	}

	public void setPledgeGalaxyOrderService(PledgeGalaxyOrderService pledgeGalaxyOrderService) {
		this.pledgeGalaxyOrderService = pledgeGalaxyOrderService;
	}
	
	public SysparaService getSysparaService() {
		return sysparaService;
	}

	public void setSysparaService(SysparaService sysparaService) {
		this.sysparaService = sysparaService;
	}
	
}
