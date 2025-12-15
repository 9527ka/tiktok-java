package project.onlinechat;

import java.util.Date;

import kernel.bo.EntityObject;
import  redis.clients.util.Pool;

public class MessageUser extends EntityObject<String> implements Comparable<MessageUser> {

	private static final long serialVersionUID = -7768174302895619763L;

	private String partyId;
	/**
	 * 用户未读
	 */
	private int user_unreadmsg;
	/**
	 * 客服未读
	 */
	private int customer_unreadmsg;

	private Date updateTime;
	/**
	 * 标记删除，-1:删除，0:正常
	 */
	private int delete_status;
	/**
	 * 备注
	 */
	private String remarks;

	private String ip;
	/**
	 * 发给指定用户
	 */
	private String target_username;

	public String getPartyId() {
		return partyId;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public void setPartyId(String partyId) {
		this.partyId = partyId;
	}

	@Override
	public int compareTo(MessageUser messageUser) {
		if (this.updateTime.after(messageUser.getUpdateTime())) {
			return -1;
		} else if (this.updateTime.before(messageUser.getUpdateTime())) {
			return 1;
		}
		return 0;
	}

	public int getUser_unreadmsg() {
		return user_unreadmsg;
	}

	public int getCustomer_unreadmsg() {
		return customer_unreadmsg;
	}

	public void setUser_unreadmsg(int user_unreadmsg) {
		this.user_unreadmsg = user_unreadmsg;
	}

	public void setCustomer_unreadmsg(int customer_unreadmsg) {
		this.customer_unreadmsg = customer_unreadmsg;
	}

	public int getDelete_status() {
		return delete_status;
	}

	public void setDelete_status(int delete_status) {
		this.delete_status = delete_status;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getTarget_username() {
		return target_username;
	}

	public void setTarget_username(String target_username) {
		this.target_username = target_username;
	}

}
