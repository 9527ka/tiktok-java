package project.finance;

import kernel.bo.EntityObject;

public class Finance extends EntityObject<String> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1639941028310043811L;
	/**
	 * 产品名称
	 */
	private String name;

	/**
	 * 产品名称英文
	 */
	private String name_en;
	/**
	 * 产品名称繁体
	 */
	private String name_cn;
	/**
	 * 产品名称 韩语
	 */
	private String name_kn;
	/**
	 * 产品名称 日语
	 */
	private String name_jn;

	/**
	 * 产品图片
	 */
	private String img;
	/**
	 * 周期-天数
	 */
	private int cycle;

	/**
	 * 日利率最低(%)
	 */
	private double daily_rate;
	/**
	 * 日利率最高(%)
	 */
	private double daily_rate_max;
	/**
	 * 今日利率(%)
	 * 
	 */
	private double today_rate;

	/**
	 * 违约结算比例(%)
	 */
	private double default_ratio;

	/**
	 * 投资金额区间(USDT)
	 */
	private double investment_min;
	/**
	 * 投资金额区间(USDT)
	 */
	private double investment_max;

	/**
	 * 状态。0 停用， 1 启用
	 */
	private String state = "0";

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getImg() {
		return img;
	}

	public void setImg(String img) {
		this.img = img;
	}

	public int getCycle() {
		return cycle;
	}

	public void setCycle(int cycle) {
		this.cycle = cycle;
	}

	public void setDaily_rate(int daily_rate) {
		this.daily_rate = daily_rate;
	}

	public double getDefault_ratio() {
		return default_ratio;
	}

	public void setDefault_ratio(double default_ratio) {
		this.default_ratio = default_ratio;
	}

	public double getDaily_rate() {
		return daily_rate;
	}

	public void setDaily_rate(double daily_rate) {
		this.daily_rate = daily_rate;
	}

	public double getToday_rate() {
		return today_rate;
	}

	public void setToday_rate(double today_rate) {
		this.today_rate = today_rate;
	}

	public double getInvestment_min() {
		return investment_min;
	}

	public void setInvestment_min(double investment_min) {
		this.investment_min = investment_min;
	}

	public double getInvestment_max() {
		return investment_max;
	}

	public void setInvestment_max(double investment_max) {
		this.investment_max = investment_max;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getName_en() {
		return name_en;
	}

	public void setName_en(String name_en) {
		this.name_en = name_en;
	}

	public String getName_cn() {
		return name_cn;
	}

	public void setName_cn(String name_cn) {
		this.name_cn = name_cn;
	}

	public String getName_kn() {
		return name_kn;
	}

	public void setName_kn(String name_kn) {
		this.name_kn = name_kn;
	}

	public String getName_jn() {
		return name_jn;
	}

	public void setName_jn(String name_jn) {
		this.name_jn = name_jn;
	}

	public double getDaily_rate_max() {
		return daily_rate_max;
	}

	public void setDaily_rate_max(double daily_rate_max) {
		this.daily_rate_max = daily_rate_max;
	}

}
