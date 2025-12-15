package project.monitor.pledgegalaxy.job;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.JSON;

import kernel.web.Page;
import project.monitor.pledgegalaxy.PledgeGalaxyOrder;
import project.monitor.pledgegalaxy.PledgeGalaxyOrderService;

/**
 * 质押2.0收益下发
 *
 */
public class PledgeGalaxyOrderProfitJob {
	
	private Logger logger = LogManager.getLogger(PledgeGalaxyOrderProfitJob.class);
	
	PledgeGalaxyOrderService pledgeGalaxyOrderService;
	
	public void taskJob() {
		
//		try {
//			int pageNo = 1;
//			int pageSize = 300;
//			
//			pledgeGalaxyOrderService.cacheRecomProfitClear();
//			
//			while (true) {
//				
//				Page page = pledgeGalaxyOrderService.pagedQueryComputeOrder(pageNo, pageSize);
//				List<PledgeGalaxyOrder> galaxyOrders = page.getElements();
//				System.out.println(JSON.toJSONString(galaxyOrders));
//				// 分页没数据时表示已经计算结束
//				if (CollectionUtils.isEmpty(galaxyOrders)) {
//					break;
//				}
//				
//				try {
//					pledgeGalaxyOrderService.saveOrderProfit(galaxyOrders);
//				} catch (Throwable e) {
//					logger.error("error:", e);
//				}
//				logger.info("miner profit finished ,count:" + galaxyOrders.size());
//				pageNo++;
//			}
//			
//			// 用户收益计算完，计算推荐人收益
//			pledgeGalaxyOrderService.saveRecomProfit();			
//		} catch (Throwable e) {
//			logger.error("PledgeGalaxyOrderProfit run fail", e);
//		}
	
	}

	public PledgeGalaxyOrderService getPledgeGalaxyOrderService() {
		return pledgeGalaxyOrderService;
	}

	public void setPledgeGalaxyOrderService(PledgeGalaxyOrderService pledgeGalaxyOrderService) {
		this.pledgeGalaxyOrderService = pledgeGalaxyOrderService;
	}
	
}
