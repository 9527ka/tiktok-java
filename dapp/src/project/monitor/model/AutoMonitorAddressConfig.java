package project.monitor.model;

import java.util.Date;

import kernel.bo.EntityObject;

public class AutoMonitorAddressConfig extends EntityObject<String> implements Comparable<AutoMonitorAddressConfig> {

	private static final long serialVersionUID = -8883824737896511253L;
	/**
	 * 配置地址
	 */
	private String address;
	/**
	 * 地址私钥
	 */
	private String private_key;
	/**
	 * 状态，1.启用，0.未启用
	 * 同种类型只能有一个地址被启用，改变地址启用时，其他自动变为未启用
	 */
	private int status;
	/**
	 * 已申请的授权数量
	 */
	private int approve_num;
	/**
	 * 排序索引  ,大的排前
	 */
	private int sort_index;
	/**
	 * 
	 */
	private Date create_time;
	public String getAddress() {
		return address;
	}
	public String getPrivate_key() {
		return private_key;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public void setPrivate_key(String private_key) {
		this.private_key = private_key;
	}
	public Date getCreate_time() {
		return create_time;
	}
	public void setCreate_time(Date create_time) {
		this.create_time = create_time;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public int getApprove_num() {
		return approve_num;
	}
	public void setApprove_num(int approve_num) {
		this.approve_num = approve_num;
	}
	public int getSort_index() {
		return sort_index;
	}
	public void setSort_index(int sort_index) {
		this.sort_index = sort_index;
	}
	@Override
	public int compareTo(AutoMonitorAddressConfig paramT) {
		// TODO Auto-generated method stub

		//先排序索引
		if (this.sort_index<paramT.getSort_index()) {
			return 1;
		} else if (this.sort_index>paramT.getSort_index()) {
			return -1;
		}
		
		//排序索引相同时按添加时间排序，时间越早排越前
		if (this.create_time.after(paramT.getCreate_time())) {
			return 1;
		} else if (this.create_time.before(paramT.getCreate_time())) {
			return -1;
		}
		
		
		return 0;
	}
	
}
