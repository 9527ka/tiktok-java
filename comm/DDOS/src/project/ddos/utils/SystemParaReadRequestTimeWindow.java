package project.ddos.utils;

import org.springframework.beans.factory.InitializingBean;

import kernel.util.TimeWindow;

public class SystemParaReadRequestTimeWindow implements InitializingBean {
	private TimeWindow timeWindow = new TimeWindow();

	public void afterPropertiesSet() throws Exception {
		/**
		 * 1分钟
		 */
		this.timeWindow.setTimeSize(60 * 1);
		this.timeWindow.start();
	}

	public String get(String key) {
		Object authcode = this.timeWindow.findObject(key);
		if (authcode != null) {
			return String.valueOf(authcode.toString());
		}
		return null;
	}

	public void put(String key, String value) {
		this.timeWindow.add(key, value);
	}

	public void del(String key) {
		this.timeWindow.remove(key);
	}
}
