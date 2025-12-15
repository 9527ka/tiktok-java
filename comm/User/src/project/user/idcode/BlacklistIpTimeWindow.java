package project.user.idcode;

import org.springframework.beans.factory.InitializingBean;

import kernel.util.TimeWindow;

public class BlacklistIpTimeWindow implements InitializingBean {
	private TimeWindow timeWindow = new TimeWindow();

	public void afterPropertiesSet() throws Exception {
		/**
		 * 24小时
		 */
		this.timeWindow.setTimeSize(60 * 60 * 24);
		this.timeWindow.start();
	}

	public String getBlackIp(String key) {
		Object authcode = this.timeWindow.findObject(key);
		if (authcode != null) {
			return String.valueOf(authcode.toString());
		}
		return null;
	}

	public void putBlackIp(String key, String ip) {
		this.timeWindow.add(key, ip);
	}

	public void delBlackIp(String key) {
		this.timeWindow.remove(key);
	}
}
