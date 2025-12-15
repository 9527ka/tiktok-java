package project.monitor.model;

import java.util.Date;

import kernel.bo.EntityObject;

/**
 * 转账地址配置
 *
 */
public class AutoMonitorTransferAddressConfig extends EntityObject {

	/**
	 * 
	 */

	/**
	 * 
	 */
	private static final long serialVersionUID = -8883824737896511253L;
	/**
	 * 配置地址
	 */
	private String address;

	
	private Date create_time;
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public Date getCreate_time() {
		return create_time;
	}
	public void setCreate_time(Date create_time) {
		this.create_time = create_time;
	}
	
}
