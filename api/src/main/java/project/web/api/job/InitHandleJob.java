package project.web.api.job;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class InitHandleJob implements InitializingBean {
	
	private Logger logger = LogManager.getLogger(InitHandleJob.class);

	private RealtimePushJob realtimePushJob;
	private TradePushJob tradePushJob;
	private DepthPushJob depthPushJob;
	
	public void afterPropertiesSet() throws Exception {
		realtimePushJob.start();
		tradePushJob.start();
		depthPushJob.start();
	}
	
	public TradePushJob getTradePushJob() {
		return tradePushJob;
	}

	public void setTradePushJob(TradePushJob tradePushJob) {
		this.tradePushJob = tradePushJob;
	}

	public DepthPushJob getDepthPushJob() {
		return depthPushJob;
	}

	public void setDepthPushJob(DepthPushJob depthPushJob) {
		this.depthPushJob = depthPushJob;
	}

	public RealtimePushJob getRealtimePushJob() {
		return realtimePushJob;
	}

	public void setRealtimePushJob(RealtimePushJob realtimePushJob) {
		this.realtimePushJob = realtimePushJob;
	}

}
