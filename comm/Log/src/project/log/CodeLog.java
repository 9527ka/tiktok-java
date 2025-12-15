package project.log;

import java.io.Serializable;
import java.util.Date;

import kernel.bo.EntityObject;

public class CodeLog extends EntityObject<String> implements AbstractLog {

	private static final long serialVersionUID = 7008440232393696319L;

	/**
	 * 日志归属
	 */
	private String partyId;
	/**
	 * 日志归属
	 */
	private String username;

	/**
	 * 日志
	 */
	private String log;

	/**
	 * TARGET
	 */
	private String target;
	

	// 创建时间
	private Date createTime;

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getPartyId() {
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




	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}



}


