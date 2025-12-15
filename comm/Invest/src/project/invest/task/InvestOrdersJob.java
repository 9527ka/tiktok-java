package project.invest.task;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import project.invest.project.ProjectService;
import project.invest.project.model.InvestOrders;
import project.invest.project.model.InvestRebate;

import java.util.List;


/**
 * 60秒
 */
public class InvestOrdersJob {

	protected ProjectService projectService;
	private static Log logger = LogFactory.getLog(InvestOrdersJob.class);

	/**
	 * 分红
	 */
	public void taskSettlementsJob() {
		long start = System.currentTimeMillis();
		logger.error("**********分红订单开始**********");
		List<InvestOrders> list = projectService.listWaiteSettlements();
		for(InvestOrders brushOrders:list){

			//异步
			projectService.updateSettlementsOrders(brushOrders.getId().toString());
		}
		logger.error("分红单数:"+list.size()+",花费时间:"+(System.currentTimeMillis()-start)+"ms");
	}

	/**
	 * 推广佣金(1.2级)
	 */
	public void taskPromoteJob() {
		long start = System.currentTimeMillis();
		logger.error("**********佣金发送开始**********");
		List<InvestRebate> list = projectService.listWaiteRebate();
		for(InvestRebate rebate:list){

			try {
				projectService.updateRebate(rebate.getId().toString());

			}catch (Exception e){
				logger.error("定时分佣异常",e);
			}

			//异步
		}
		logger.error("佣金发送单数:"+list.size()+",花费时间:"+(System.currentTimeMillis()-start)+"ms");
	}

	public void setProjectService(ProjectService projectService) {
		this.projectService = projectService;
	}
}
