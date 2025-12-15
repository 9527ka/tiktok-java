package project.monitor.pledgegalaxy.job;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.util.CollectionUtils;

import com.mysql.cj.util.StringUtils;

import kernel.util.DateUtils;
import kernel.web.Page;
import project.monitor.pledgegalaxy.PledgeGalaxyOrder;
import project.monitor.pledgegalaxy.PledgeGalaxyOrderService;
import project.syspara.SysparaService;

/**
 * 生成质押收益记录
 *
 */
public class PledgeGalaxyProfitCreateJob {

	private Logger logger = LogManager.getLogger(PledgeGalaxyProfitCreateJob.class);
	
	protected PledgeGalaxyOrderService pledgeGalaxyOrderService;
	protected SysparaService sysparaService;
	
	public void taskJob() {
		String pledgeGalaxyOpen = sysparaService.find("pledge_galaxy_open").getValue();
		if (StringUtils.isNullOrEmpty(pledgeGalaxyOpen) || "false".equals(pledgeGalaxyOpen)) {
			return;
		}
		try {
			int pageNo = 1;
			int pageSize = 300;
			// 小于当前时间，新增的订单都不查
			Date date = new Date();
			List<PledgeGalaxyOrder> closeList = new ArrayList<>();
			while (true) {
				
				Page page = pledgeGalaxyOrderService.pagedQueryComputeOrder(pageNo, pageSize, date);
				List<PledgeGalaxyOrder> galaxyOrders = page.getElements();
				// 分页没数据时表示已经计算结束
				if (CollectionUtils.isEmpty(galaxyOrders)) {
					break;
				}
				try {
					pledgeGalaxyOrderService.saveGalaxyProfit(galaxyOrders, closeList);
				} catch (Throwable e) {
					logger.error("error:", e);
				}
				logger.info("miner profit finished ,count:" + galaxyOrders.size());
				pageNo++;
			}
			
			for (PledgeGalaxyOrder close : closeList) {
				pledgeGalaxyOrderService.saveClose(close, true);
			}
			
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
