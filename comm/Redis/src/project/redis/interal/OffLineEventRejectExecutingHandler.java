package project.redis.interal;

import java.util.Date;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

import project.log.SysLog;
import project.log.SysLogService;

public class OffLineEventRejectExecutingHandler implements RejectedExecutionHandler {

	private SysLogService sysLogService;

	public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
		SysLog entity = new SysLog();
		entity.setLevel(SysLog.level_error);
		entity.setCreateTime(new Date());
		entity.setLog("RedisHandlerImpl处理线程池溢出，数据被丢弃，请调整线程参数。");
		sysLogService.saveAsyn(entity);
	}

	public void setSysLogService(SysLogService sysLogService) {
		this.sysLogService = sysLogService;
	}

}
