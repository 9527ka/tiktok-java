package project.onlinechat;

import java.util.Date;

import kernel.bo.EntityObject;

/**
 * 客服留言表
 * 
 */

public class OnlineChatMessage extends EntityObject<String> implements Comparable<OnlineChatMessage> {

	private static final long serialVersionUID = -4999012202564084751L;

	private String partyId;// varchar(32),
	/**
	 * send 发送 receive 接收
	 */
	private String send_receive;// 1 用户发送 2 用户接收

	/**
	 * text img
	 */
	private String type;

	private String content;// 留言内容

	private Date createTime;

	private String username;// 用户名(管理员发送时纪录)

	private String target_username;// 发送到指定用户

	private String ip;
	/**
	 * 标记删除，-1:删除，0:正常
	 */
	private int delete_status=0;

	public String getPartyId() {
		return partyId;
	}

	public void setPartyId(String partyId) {
		this.partyId = partyId;
	}

	public String getSend_receive() {
		return send_receive;
	}

	public void setSend_receive(String send_receive) {
		this.send_receive = send_receive;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	@Override
	public int compareTo(OnlineChatMessage onlineChatMessage) {

		if (this.createTime.after(onlineChatMessage.getCreateTime())) {
			return 1;
		} else if (this.createTime.before(onlineChatMessage.getCreateTime())) {
			return -1;
		}
		return 0;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
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

	public int getDelete_status() {
		return delete_status;
	}

	public void setDelete_status(int delete_status) {
		this.delete_status = delete_status;
	}
	
	

}
