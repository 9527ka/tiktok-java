package project.futures.job;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import project.log.SysLog;
import project.log.SysLogService;

public class FuturesOrderCreateRecomJob {
	private static Logger logger = LoggerFactory.getLogger(FuturesOrderCreateRecomJob.class);
	protected SysLogService sysLogService;
	private FuturesOrderCreateRecomService futuresOrderCreateRecomService;

	public void taskJob() {
		try {
			futuresOrderCreateRecomService.computeRecom();
		} catch (Exception e) {
			logger.error("FuturesOrderCreateRecomJob run fail e:", e);
			SysLog entity = new SysLog();
			entity.setLevel(SysLog.level_error);
			entity.setCreateTime(new Date());
			entity.setLog("FuturesOrderCreateRecomJob 交割购买奖励任务 执行失败 e:" + e);
			sysLogService.saveAsyn(entity);
		}
	}

	public void setSysLogService(SysLogService sysLogService) {
		this.sysLogService = sysLogService;
	}

	public void setFuturesOrderCreateRecomService(FuturesOrderCreateRecomService futuresOrderCreateRecomService) {
		this.futuresOrderCreateRecomService = futuresOrderCreateRecomService;
	}

}
