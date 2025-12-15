package project.log;

import java.io.Serializable;
import java.util.Date;

import kernel.bo.EntityObject;

public class ApiLog extends EntityObject<String> implements AbstractLog {
	/**
	 * Member Description
	 */

	private static final long serialVersionUID = 1837652077217320806L;
	/**
	 * 日志归属
	 */
	private String partyId;
	/**
	 * ip
	 */
	private String ip;

	/**
	 * 接口名
	 */
	private String api;
	/**
	 * 日志
	 */
	private String log;

	/**
	 * 扩展信息，统计分类时使用
	 */
	private String extra;

	// 创建时间
	private Date createTime;

	public String getPartyId() {
		return partyId;
	}

	public String getIp() {
		return ip;
	}

	public String getLog() {
		return log;
	}

	public String getExtra() {
		return extra;
	}

	public String getApi() {
		return api;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setPartyId(String partyId) {
		this.partyId = partyId;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public void setLog(String log) {
		this.log = log;
	}

	public void setExtra(String extra) {
		this.extra = extra;
	}

	public void setApi(String api) {
		this.api = api;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

}
