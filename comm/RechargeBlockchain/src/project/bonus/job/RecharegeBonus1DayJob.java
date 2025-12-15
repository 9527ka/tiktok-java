package project.bonus.job;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


import kernel.util.ThreadUtils;
import project.bonus.RechargeBonusService;

/**
 * 每日定时返佣前一天的 充值
 * 
 */
public class RecharegeBonus1DayJob {
	private static Log logger = LogFactory.getLog(RecharegeBonus1DayJob.class);

	private RechargeBonusService rechargeBonusService;

	public void taskJob() {
		try {
			rechargeBonusService.saveDailyBounsHandle();
			logger.info("DailyBounsHandle end" );
		} catch (Throwable e) {
			logger.error("DailyBounsHandle run fail", e);
		} finally {
			// 暂停0.1秒
			ThreadUtils.sleep(1000);
		}
	}

	public void setRechargeBonusService(RechargeBonusService rechargeBonusService) {
		this.rechargeBonusService = rechargeBonusService;
	}

}
