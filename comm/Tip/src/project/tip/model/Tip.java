package project.tip.model;

import java.util.Date;

import kernel.bo.EntityObject;

public class Tip extends EntityObject<String> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2944570190286781603L;
	/**
	 * 模块
	 */
	private String model;
	/**
	 * 业务id
	 */
	private String business_id;
	/**
	 * 创建时间
	 */
	private Date create_time;
	/**
	 * 时间戳
	 */
	private Long time_stamp;
	/**
	 * 指定用户
	 */
	private String target_username;

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getBusiness_id() {
		return business_id;
	}

	public Date getCreate_time() {
		return create_time;
	}

	public void setBusiness_id(String business_id) {
		this.business_id = business_id;
	}

	public void setCreate_time(Date create_time) {
		this.create_time = create_time;
	}

	public Long getTime_stamp() {
		return time_stamp;
	}

	public void setTime_stamp(Long time_stamp) {
		this.time_stamp = time_stamp;
	}

	public String getTarget_username() {
		return target_username;
	}

	public void setTarget_username(String target_username) {
		this.target_username = target_username;
	}

}
