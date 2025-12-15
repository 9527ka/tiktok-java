package project.log;

import java.io.Serializable;
import java.util.Date;

import kernel.bo.EntityObject;

public class Log extends EntityObject<String> implements AbstractLog {
	/**
	 * Member Description
	 */

	private static final long serialVersionUID = 1837652077217320806L;
	/**
	 * 日志归属
	 */
	private String partyId;
	/**
	 * 日志归属
	 */
	private String username;

	// 日志类型，见Constants
	private String category;
	/**
	 * 日志
	 */
	private String log;

	/**
	 * 扩展信息，统计分类时使用
	 */
	private String extra;
	
	private String operator;

	// 创建时间
	private Date createTime;

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Serializable getPartyId() {
		return partyId;
	}

	public void setPartyId(String partyId) {
		this.partyId = partyId;
	}

	public String getLog() {
		return log;
	}

	public void setLog(String log) {
		this.log = log;
	}


	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getExtra() {
		return extra;
	}

	public void setExtra(String extra) {
		this.extra = extra;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}


}
