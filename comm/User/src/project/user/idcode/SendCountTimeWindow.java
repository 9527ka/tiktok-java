package project.user.idcode;

import org.springframework.beans.factory.InitializingBean;

import kernel.util.TimeWindow;

public class SendCountTimeWindow implements InitializingBean {
	private TimeWindow timeWindow = new TimeWindow();

	public void afterPropertiesSet() throws Exception {
		/**
		 * 10分钟
		 */
		this.timeWindow.setTimeSize(60 * 10);
		this.timeWindow.start();
	}

	public String getIpSend(String key) {
		Object authcode = this.timeWindow.findObject(key);
		if (authcode != null) {
			return String.valueOf(authcode.toString());
		}
		return null;
	}

	public void putIpSend(String key, String ip) {
		this.timeWindow.add(key, ip);
	}

	public void delIpSend(String key) {
		this.timeWindow.remove(key);
	}
}
