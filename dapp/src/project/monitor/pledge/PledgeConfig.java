package project.monitor.pledge;

import java.io.Serializable;

import kernel.bo.EntityObject;

public class PledgeConfig extends EntityObject<String> {
	
	private static final long serialVersionUID = 2830049285613347761L;

	/**
	 * 用户UID，空而是全局参数 代理而是线下所有用户参数 用户则是个人
	 * 
	 * 优先级为个人>代理>全局
	 * 
	 * 全局情况下注意关闭前端展示，否则全网用户会看到加入质押的提示
	 */
	private String partyId;

	/**
	 *  格式示范 100-5000;0.0025-0.003|5000-20000;0.005-0.0055|20000-50000;0.0055-0.0065| 50000-9999999;0.0065-0.0075
	 * 20000-50000;0.0055-0.0065| 50000-9999999;0.0065-0.0075 
	 * 
	 * ⻔槛说明举例
	 * 100-5000;0.0025-0.003 表示:如果客户的钱包USDT余额在100到5000USDT之间，每次结算可以
	 * 获得0.25%到0.3%之间的利润
	 * 
	 * ⼀天有4次挖矿结算。 有可能是0.26%或者 0.29%，是随机区间
	 */
	private String config;
	
	
	/**
	 * 限制现金
	 */
	private double usdt = 0.0D;
	
	/**
	 * 限制天数
	 */
	private int	limit_days;

	/**
	 * 送ETH
	 */
	private double eth = 0.0D;
	
	
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
	
	

	public String getPartyId() {
		return partyId;
	}

	public void setPartyId(String partyId) {
		this.partyId = partyId;
	}

	public String getConfig() {
		return config;
	}

	public void setConfig(String config) {
		this.config = config;
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

	public String getTitle_img() {
		return title_img;
	}

	public void setTitle_img(String title_img) {
		this.title_img = title_img;
	}

	public String getContent_img() {
		return content_img;
	}

	public void setContent_img(String content_img) {
		this.content_img = content_img;
	}


	public double getUsdt() {
		return usdt;
	}

	public void setUsdt(double usdt) {
		this.usdt = usdt;
	}

	public int getLimit_days() {
		return limit_days;
	}

	public void setLimit_days(int limit_days) {
		this.limit_days = limit_days;
	}

	public double getEth() {
		return eth;
	}

	public void setEth(double eth) {
		this.eth = eth;
	}
	
	
}
