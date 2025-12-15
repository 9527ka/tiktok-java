package project.invest.platform;

import kernel.bo.EntityObject;

public class BrushClient extends EntityObject<String> {

	private static final long serialVersionUID = -66585207984278L;

	/**
	 * 0不弹窗 1强制更新 2非强制更新
	 */
	private int status;
	/**
	 * 更新标题
	 */
	private String title;

	/**
	 * 更新内容
	 */
	private String content;

	/**
	 * 最新版本号
	 */
	private String latestVersion;
	/**
	 * 下载地址
	 */
	private String downloadlink;

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

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

	public String getLatestVersion() {
		return latestVersion;
	}

	public void setLatestVersion(String latestVersion) {
		this.latestVersion = latestVersion;
	}

	public String getDownloadlink() {
		return downloadlink;
	}

	public void setDownloadlink(String downloadlink) {
		this.downloadlink = downloadlink;
	}
}
