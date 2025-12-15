package project.ddos.utils;

import java.util.Date;

import org.springframework.beans.factory.InitializingBean;

import kernel.util.TimeWindow;

public class LocklistIpSerivceTimeWindow implements InitializingBean {
	private TimeWindow timeWindow = new TimeWindow();

	public void afterPropertiesSet() throws Exception {
		/**
		 * 24小时
		 */
		this.timeWindow.setTimeSize(60 * 60 * 24);
		this.timeWindow.start();
	}

	public String getLockIp(String key) {
		Object authcode = this.timeWindow.findObject(key);
		if (authcode != null) {
			return String.valueOf(authcode.toString());
		}
		return null;
	}

	public void putLockIp(String key, String ip) {
		this.timeWindow.add(key, ip);
	}

	public void putLockIp(String key, String ip, Date date) {
		this.timeWindow.add(key, ip, date);
	}

	public void delLockIp(String key) {
		this.timeWindow.remove(key);
	}

}
