package project.ddos.model;

import java.util.Date;

import kernel.bo.EntityObject;

public class IpMenu extends EntityObject<String> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4196439149608747292L;
	/**
	 * 白名单
	 */
	public static final String IP_WHITE = "white";
	/**
	 * 黑名单
	 */
	public static final String IP_BLACK = "black";
	/**
	 * 锁定名单
	 */
	public static final String IP_LOCK = "lock";
	/**
	 * ip
	 */
	private String ip;
	/**
	 * 类型 ：black:黑名单，white：白名单
	 */
	private String type;
	/**
	 * -1:标记删除，0:正常
	 */
	private int delete_status;
	/**
	 * 创建时间
	 */
	private Date create_time;
	/**
	 * 最后处理时间
	 */
	private Date last_opera_time;

	/**
	 *创建人
	 */
	private String createName;

	/**
	 * 备注
	 */
	private String remark;

	public String getIp() {
		return ip;
	}

	public String getType() {
		return type;
	}

	public int getDelete_status() {
		return delete_status;
	}

	public Date getCreate_time() {
		return create_time;
	}

	public Date getLast_opera_time() {
		return last_opera_time;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setDelete_status(int delete_status) {
		this.delete_status = delete_status;
	}

	public void setCreate_time(Date create_time) {
		this.create_time = create_time;
	}

	public void setLast_opera_time(Date last_opera_time) {
		this.last_opera_time = last_opera_time;
	}


	public String getCreateName() {
		return createName;
	}

	public void setCreateName(String createName) {
		this.createName = createName;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}
}
