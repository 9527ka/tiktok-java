package project.user;

import java.util.Date;

import kernel.bo.EntityObject;

public class ImageQr extends EntityObject<String> {

	/**
	 * 图片名称IMG_NAME
	 */
	private String img_name;
	/**
	 * 图片内容 base64 CONTENT
	 */
	private String content;
	/**
	 * 图片类型
	 */
	private String img_type;
	/**
	 * 图片语言
	 */
	private String img_language;

	/**
	 * 归属用户
	 * 
	 * @return
	 */
	private String usercode;
	
	/**
	 * 创建时间----CREATE_TIME
	 */
	private Date create_time;

	public String getImg_name() {
		return img_name;
	}

	public void setImg_name(String img_name) {
		this.img_name = img_name;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getImg_type() {
		return img_type;
	}

	public void setImg_type(String img_type) {
		this.img_type = img_type;
	}

	public String getImg_language() {
		return img_language;
	}

	public void setImg_language(String img_language) {
		this.img_language = img_language;
	}

	public String getUsercode() {
		return usercode;
	}

	public void setUsercode(String usercode) {
		this.usercode = usercode;
	}

	public Date getCreate_time() {
		return create_time;
	}

	public void setCreate_time(Date create_time) {
		this.create_time = create_time;
	}

	
}
