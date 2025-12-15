package project.finance.job;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import project.log.SysLog;
import project.log.SysLogService;

public class FinanceOrderCreateRecomJob {
	private static Logger logger = LoggerFactory.getLogger(FinanceOrderCreateRecomJob.class);
	protected SysLogService sysLogService;
	private FinanceOrderCreateRecomService financeOrderCreateRecomService;
	
	public void taskJob() {
		try {
			financeOrderCreateRecomService.computeRecom();
		} catch (Exception e) {
			logger.error("FinanceOrderCreateRecomJob run fail e:", e);
			SysLog entity = new SysLog();
			entity.setLevel(SysLog.level_error);
			entity.setCreateTime(new Date());
			entity.setLog("FinanceOrderCreateRecomJob 理财购买奖励任务 执行失败 e:"+e);
			sysLogService.saveAsyn(entity);
		} 
	}

	public void setSysLogService(SysLogService sysLogService) {
		this.sysLogService = sysLogService;
	}

	public void setFinanceOrderCreateRecomService(FinanceOrderCreateRecomService financeOrderCreateRecomService) {
		this.financeOrderCreateRecomService = financeOrderCreateRecomService;
	}
	
}
