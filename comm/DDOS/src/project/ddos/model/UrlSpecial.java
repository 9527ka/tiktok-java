package project.ddos.model;

import java.util.Date;

import kernel.bo.EntityObject;

public class UrlSpecial extends EntityObject<String> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4196439149608747292L;
	/**
	 * url
	 */
	private String url;
	/**
	 * 备注
	 */
	private String remarks;
	/**
	 * 创建时间
	 */
	private Date create_time;

	public void setUrl(String url) {
		this.url = url;
	}

	public void setCreate_time(Date create_time) {
		this.create_time = create_time;
	}

	public String getUrl() {
		return url;
	}

	public Date getCreate_time() {
		return create_time;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

}
