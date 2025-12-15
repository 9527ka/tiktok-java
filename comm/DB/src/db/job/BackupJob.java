package db.job;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import db.DBBackupLock;
import db.util.BackupUtil;
import kernel.util.DateUtils;
import project.log.SysLogService;
import project.syspara.SysparaService;

public class BackupJob {
	private Logger log = LoggerFactory.getLogger(BackupJob.class);
	protected SysLogService sysLogService;
	protected SysparaService sysparaService;
	
	public void taskJob() {
		try {
//			log.info("BackupJob taskJob start,time:"+DateUtils.dateToStr(new Date(), DateUtils.DF_yyyyMMddHHmmss)+",isBackup:"+isBackup()+",lock:"+DBBackupLock.getLock(DBBackupLock.ALL_DB_LOCK));
			//未被锁
			if(isBackup() && !DBBackupLock.getLock(DBBackupLock.ALL_DB_LOCK)) {
				BackupUtil.backupPart(sysLogService,sysparaService);
			}
			log.info("BackupJob taskJob end");
		}catch (Throwable t) {
			log.error("BackupJob taskJob fail t:"+t);
		}
	
	}
	
	/**
	 * 当前是否执行局部备份，true：开启，false：不开启
	 * @return
	 */
	private boolean isBackup() {
		//例如， 01:55-02:35,04:00-05:08,13:55-14:45  表示多个时间段内不备份
		try {
			String not_part_backup_times = sysparaService.find("not_part_backup_times").getValue();
			String[] times = not_part_backup_times.split(",");
			Date now = new Date();
			String nowDate = DateUtils.getDateStr(now);
			for (int i = 0; i < times.length; i++) {
				String[] timePart = times[i].split("-");
				Date startTime = DateUtils.strToDate(nowDate+" "+timePart[0]);
				Date endTime = DateUtils.strToDate(nowDate+" "+timePart[1]);
				if(now.after(startTime)&&now.before(endTime)) {
					return false;
				}
			}
		}catch (Exception e) {
			// TODO: handle exception
			log.error("BackupJob isBackup fail,e:",e);
		}
		return true;
	}
	public void setSysLogService(SysLogService sysLogService) {
		this.sysLogService = sysLogService;
	}

	public void setSysparaService(SysparaService sysparaService) {
		this.sysparaService = sysparaService;
	}
	
	
}
