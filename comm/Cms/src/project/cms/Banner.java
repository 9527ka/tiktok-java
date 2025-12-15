package project.cms;

import java.util.Date;

import kernel.bo.EntityObject;

public class Banner extends EntityObject<String> implements Comparable<Banner> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8252272898082376329L;

	/**
	 * 语言
	 */
	private String language;

	/**
	 * 业务代码， 同种内容 不同语言下的code相同
	 */
	private String content_code;
	/**
	 * 展示图片
	 */
	private String image;
	/**
	 * 访问路径
	 */
	private String url;
	/**
	 * 是否展示
	 */
	private int on_show;
	/**
	 * 排列顺序（数字相同按时间排，越小排越前）
	 */
	private int sort_index;
	/**
	 * 类型，top:顶部展示，other:其他地方展示,poster:弹窗海报
	 */
	private String model;
	/**
	 * 是否可以点击跳转
	 */
	private int click;

	private Date createTime;

	public String getLanguage() {
		return language;
	}

	public String getContent_code() {
		return content_code;
	}

	public String getUrl() {
		return url;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public void setContent_code(String content_code) {
		this.content_code = content_code;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public int getClick() {
		return click;
	}

	public void setClick(int click) {
		this.click = click;
	}

	public int getSort_index() {
		return sort_index;
	}

	public void setSort_index(int sort_index) {
		this.sort_index = sort_index;
	}

	public int getOn_show() {
		return on_show;
	}

	public void setOn_show(int on_show) {
		this.on_show = on_show;
	}

	@Override
	public int compareTo(Banner paramT) {
		// TODO Auto-generated method stub
		return this.sort_index - paramT.getSort_index();
	}

}
