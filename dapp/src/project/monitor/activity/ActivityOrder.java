package project.monitor.activity;

import java.io.Serializable;
import java.util.Date;

import kernel.bo.EntityObject;

/**
 * 用户的活动订单
 *
 */
public class ActivityOrder extends EntityObject<String> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8661414599071807514L;
	private String partyId;
	private String activityid;
	
	private Date createTime;
	/**
	 * 派发时间
	 */
	private Date sendTime;

	/**
	 * 
	 * 
	 * 0:未领取
	 * 1:已领取
	 * 2:成功
	 * 3:失败
	 */
	private int succeeded = 0;
	
	/**
	 * 现金
	 */
	private double usdt = 0.0D;

	/**
	 * 送ETH
	 */
	private double eth = 0.0D;

	/**
	 * 结束时间
	 */
	private Date endtime;

	/**
	 * 标题
	 */
	private String title;
	/**
	 * 内容
	 */
	private String content;
	
	
	/**
	 * 标题
	 */
	private String title_img;
	/**
	 * 内容
	 */
	private String content_img;
	
	
	/**
	 * 首页弹出新闻，如果为true弹出
	 */
	private Boolean index = false;
	/**
	 * 加入活动时间
	 */
	private Date add_activity_time;
	

	public String getPartyId() {
		return partyId;
	}

	public void setPartyId(String partyId) {
		this.partyId = partyId;
	}

	public String getActivityid() {
		return activityid;
	}

	public void setActivityid(String activityid) {
		this.activityid = activityid;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public int getSucceeded() {
		return succeeded;
	}

	public void setSucceeded(int succeeded) {
		this.succeeded = succeeded;
	}

	public Date getSendTime() {
		return sendTime;
	}

	public void setSendTime(Date sendTime) {
		this.sendTime = sendTime;
	}

	public double getUsdt() {
		return usdt;
	}

	public double getEth() {
		return eth;
	}

	public Date getEndtime() {
		return endtime;
	}

	public String getTitle() {
		return title;
	}

	public String getContent() {
		return content;
	}

	public String getTitle_img() {
		return title_img;
	}

	public String getContent_img() {
		return content_img;
	}

	public Boolean getIndex() {
		return index;
	}

	public void setUsdt(double usdt) {
		this.usdt = usdt;
	}

	public void setEth(double eth) {
		this.eth = eth;
	}

	public void setEndtime(Date endtime) {
		this.endtime = endtime;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public void setTitle_img(String title_img) {
		this.title_img = title_img;
	}

	public void setContent_img(String content_img) {
		this.content_img = content_img;
	}

	public void setIndex(Boolean index) {
		this.index = index;
	}

	public Date getAdd_activity_time() {
		return add_activity_time;
	}

	public void setAdd_activity_time(Date add_activity_time) {
		this.add_activity_time = add_activity_time;
	}

}
