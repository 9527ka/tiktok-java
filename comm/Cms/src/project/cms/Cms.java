package project.cms;

import java.util.Date;

import kernel.bo.EntityObject;
/**
 *
 */
public class Cms extends EntityObject<String> {

	private String id;
	//getset
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}

	private String storeAccount;
	public String getStoreAccount() {
		return storeAccount;
	}
	public void setStoreAccount(String storeAccount) {
		this.storeAccount = storeAccount;
	}

	private String storeUuid;
	public String getStoreUuid() {
		return storeUuid;
	}
	public void setStoreUuid(String storeUuid) {
		this.storeUuid = storeUuid;
	}
	/**
	 * 标题
	 */
	private String title;
	/**
	 * 内容
	 */
	private String content;
	
	private Date createTime;
	
	private String language;

	/**
	 * 模块所属类型 '类型 0-公告管理，1-文章管理'
	 */
	private Integer type;

	/**
	 *  '0-启用，1-禁用'
	 */
	private Integer status;


	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
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

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}
}
