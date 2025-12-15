package project.follow;

import java.io.Serializable;
import java.util.Date;

import kernel.bo.EntityObject;

/**
 * 交易员
 */
public class Trader extends EntityObject<String> {

	private static final long serialVersionUID = -1617033543659508052L;
	private String partyId;

	/**
	 * 交易员名称
	 */
	private String name;
	/**
	 * 交易员简介
	 */
	private String remarks;
	/**
	 * 带单币种（多品种的话用;隔开）
	 */
	private String symbols;

	/**
	 * 利润分成比例---PROFIT_SHARE_RATIO
	 */
	private double profit_share_ratio;

	/**
	 * 状态（是否开启跟单）---STATE,0为未开启，1为开启
	 */
	private String state;
	/**
	 * 此次跟单最多跟随人数---FOLLOWER_MAX
	 */
	private int follower_max;

	/**
	 * 入驻时间----CREATE_TIME
	 */
	private Date create_time;
	/**
	 * 头像图片---IMG
	 */
	private String img;

	/**
	 * 近3周
	 */
	/**
	 * 近3周收益
	 */
	private double week_3_profit;
	/**
	 * 近3周累计金额
	 */
	private double week_3_order_amount;

	/**
	 * 近3周收益率
	 */
	private double week_3_profit_ratio;
	/**
	 * 近3周盈利笔数
	 */
	private int week_3_order_profit;
	/**
	 * 近3周交易笔数
	 */
	private int week_3_order_sum;
	/**
	 * 累计金额
	 */
	private double order_amount;

	/**
	 * 累计收益
	 */
	private double profit;

	/**
	 * 累计收益率
	 */
	private double profit_ratio;

	/**
	 * 累计盈利笔数
	 */
	private int order_profit;

	/**
	 * 累计亏损笔数
	 */
	private int order_loss;
	/**
	 * 累计交易笔数
	 */
	private int order_sum;

	/**
	 * 累计跟随人数
	 */
	private int follower_sum;

	/**
	 * 当前跟随人数---FOLLOWER_NOW
	 */
	private int follower_now;

	/**
	 * 近3周收益--------------------偏差值
	 */
	private double deviation_week_3_profit;
	/**
	 * 近3周累计金额-偏差值
	 */
	private double deviation_week_3_order_amount;

	/**
	 * 近3周收益率-偏差值
	 */
	private double deviation_week_3_profit_ratio;
	/**
	 * 近3周盈利笔数-偏差值
	 */
	private Integer deviation_week_3_order_profit;
	/**
	 * 近3周交易笔数-偏差值
	 */
	private Integer deviation_week_3_order_sum;
	/**
	 * 累计金额-偏差值
	 */
	private double deviation_order_amount;

	/**
	 * 累计收益-偏差值
	 */
	private double deviation_profit;

	/**
	 * 累计收益率-偏差值
	 */
	private double deviation_profit_ratio;

	/**
	 * 累计盈利笔数-偏差值
	 */
	private int deviation_order_profit;

	/**
	 * 累计亏损笔数-偏差值
	 */
	private int deviation_order_loss;
	/**
	 * 累计交易笔数-偏差值
	 */
	private int deviation_order_sum;

	/**
	 * 累计跟随人数-偏差值
	 */
	private int deviation_follower_sum;

	/**
	 * 当前跟随人数-偏差值---DEVIATION_FOLLOWER_NOW
	 */
	private int deviation_follower_now;
	/**
	 * 跟单最小下单数
	 */
	private int follow_volumn_min;

	public String getPartyId() {
		return partyId;
	}

	public String getName() {
		return name;
	}

	public String getRemarks() {
		return remarks;
	}

	public String getSymbols() {
		return symbols;
	}

	public double getProfit_share_ratio() {
		return profit_share_ratio;
	}

	public String getState() {
		return state;
	}

	public int getFollower_max() {
		return follower_max;
	}

	public Date getCreate_time() {
		return create_time;
	}

	public String getImg() {
		return img;
	}

	public double getWeek_3_profit() {
		return week_3_profit;
	}

	public double getWeek_3_order_amount() {
		return week_3_order_amount;
	}

	public double getWeek_3_profit_ratio() {
		return week_3_profit_ratio;
	}

	public int getWeek_3_order_profit() {
		return week_3_order_profit;
	}

	public int getWeek_3_order_sum() {
		return week_3_order_sum;
	}

	public double getOrder_amount() {
		return order_amount;
	}

	public double getProfit() {
		return profit;
	}

	public double getProfit_ratio() {
		return profit_ratio;
	}

	public int getOrder_profit() {
		return order_profit;
	}

	public int getOrder_loss() {
		return order_loss;
	}

	public int getOrder_sum() {
		return order_sum;
	}

	public int getFollower_sum() {
		return follower_sum;
	}

	public int getFollower_now() {
		return follower_now;
	}

	public double getDeviation_week_3_profit() {
		return deviation_week_3_profit;
	}

	public double getDeviation_week_3_order_amount() {
		return deviation_week_3_order_amount;
	}

	public double getDeviation_week_3_profit_ratio() {
		return deviation_week_3_profit_ratio;
	}

	public Integer getDeviation_week_3_order_profit() {
		return deviation_week_3_order_profit;
	}

	public Integer getDeviation_week_3_order_sum() {
		return deviation_week_3_order_sum;
	}

	public double getDeviation_order_amount() {
		return deviation_order_amount;
	}

	public double getDeviation_profit() {
		return deviation_profit;
	}

	public double getDeviation_profit_ratio() {
		return deviation_profit_ratio;
	}

	public int getDeviation_order_profit() {
		return deviation_order_profit;
	}

	public int getDeviation_order_loss() {
		return deviation_order_loss;
	}

	public int getDeviation_order_sum() {
		return deviation_order_sum;
	}

	public int getDeviation_follower_sum() {
		return deviation_follower_sum;
	}

	public int getDeviation_follower_now() {
		return deviation_follower_now;
	}

	public void setPartyId(String partyId) {
		this.partyId = partyId;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public void setSymbols(String symbols) {
		this.symbols = symbols;
	}

	public void setProfit_share_ratio(double profit_share_ratio) {
		this.profit_share_ratio = profit_share_ratio;
	}

	public void setState(String state) {
		this.state = state;
	}

	public void setFollower_max(int follower_max) {
		this.follower_max = follower_max;
	}

	public void setCreate_time(Date create_time) {
		this.create_time = create_time;
	}

	public void setImg(String img) {
		this.img = img;
	}

	public void setWeek_3_profit(double week_3_profit) {
		this.week_3_profit = week_3_profit;
	}

	public void setWeek_3_order_amount(double week_3_order_amount) {
		this.week_3_order_amount = week_3_order_amount;
	}

	public void setWeek_3_profit_ratio(double week_3_profit_ratio) {
		this.week_3_profit_ratio = week_3_profit_ratio;
	}

	public void setWeek_3_order_profit(int week_3_order_profit) {
		this.week_3_order_profit = week_3_order_profit;
	}

	public void setWeek_3_order_sum(int week_3_order_sum) {
		this.week_3_order_sum = week_3_order_sum;
	}

	public void setOrder_amount(double order_amount) {
		this.order_amount = order_amount;
	}

	public void setProfit(double profit) {
		this.profit = profit;
	}

	public void setProfit_ratio(double profit_ratio) {
		this.profit_ratio = profit_ratio;
	}

	public void setOrder_profit(int order_profit) {
		this.order_profit = order_profit;
	}

	public void setOrder_loss(int order_loss) {
		this.order_loss = order_loss;
	}

	public void setOrder_sum(int order_sum) {
		this.order_sum = order_sum;
	}

	public void setFollower_sum(int follower_sum) {
		this.follower_sum = follower_sum;
	}

	public void setFollower_now(int follower_now) {
		this.follower_now = follower_now;
	}

	public void setDeviation_week_3_profit(double deviation_week_3_profit) {
		this.deviation_week_3_profit = deviation_week_3_profit;
	}

	public void setDeviation_week_3_order_amount(double deviation_week_3_order_amount) {
		this.deviation_week_3_order_amount = deviation_week_3_order_amount;
	}

	public void setDeviation_week_3_profit_ratio(double deviation_week_3_profit_ratio) {
		this.deviation_week_3_profit_ratio = deviation_week_3_profit_ratio;
	}

	public void setDeviation_week_3_order_profit(Integer deviation_week_3_order_profit) {
		this.deviation_week_3_order_profit = deviation_week_3_order_profit;
	}

	public void setDeviation_week_3_order_sum(Integer deviation_week_3_order_sum) {
		this.deviation_week_3_order_sum = deviation_week_3_order_sum;
	}

	public void setDeviation_order_amount(double deviation_order_amount) {
		this.deviation_order_amount = deviation_order_amount;
	}

	public void setDeviation_profit(double deviation_profit) {
		this.deviation_profit = deviation_profit;
	}

	public void setDeviation_profit_ratio(double deviation_profit_ratio) {
		this.deviation_profit_ratio = deviation_profit_ratio;
	}

	public void setDeviation_order_profit(int deviation_order_profit) {
		this.deviation_order_profit = deviation_order_profit;
	}

	public void setDeviation_order_loss(int deviation_order_loss) {
		this.deviation_order_loss = deviation_order_loss;
	}

	public void setDeviation_order_sum(int deviation_order_sum) {
		this.deviation_order_sum = deviation_order_sum;
	}

	public void setDeviation_follower_sum(int deviation_follower_sum) {
		this.deviation_follower_sum = deviation_follower_sum;
	}

	public void setDeviation_follower_now(int deviation_follower_now) {
		this.deviation_follower_now = deviation_follower_now;
	}

	public int getFollow_volumn_min() {
		return follow_volumn_min;
	}

	public void setFollow_volumn_min(int follow_volumn_min) {
		this.follow_volumn_min = follow_volumn_min;
	}

}
