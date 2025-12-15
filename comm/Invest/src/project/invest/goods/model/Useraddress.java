package project.invest.goods.model;

import kernel.bo.EntityObject;

import java.util.Date;

public class Useraddress extends EntityObject<String> {

	private static final long serialVersionUID = -6658527079884278L;

	/**
	 * 会员ID
	 */
	private String partyId;
	/**
	 * 是否默认
	 */
	private int status;
	/**
	 * 手机号
	 */
	private String phone;

	/**
	 * 联系人
	 */
	private String contacts;

	/**
	 * 地址
	 */
	private String address;

	private Date createTime;

	public String getPartyId() {
		return partyId;
	}

	public void setPartyId(String partyId) {
		this.partyId = partyId;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getContacts() {
		return contacts;
	}

	public void setContacts(String contacts) {
		this.contacts = contacts;
	}
}
