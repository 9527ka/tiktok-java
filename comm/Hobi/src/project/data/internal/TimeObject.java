package project.data.internal;

import java.io.Serializable;
import java.util.Date;

public abstract class TimeObject implements Serializable {
	private static final long serialVersionUID = -7709770878909783696L;

	/**
	 * 最后读取远程数据时间
	 */
	private Date lastTime;

	public Date getLastTime() {
		return lastTime;
	}

	public void setLastTime(Date lastTime) {
		this.lastTime = lastTime;
	}
	
	
}
