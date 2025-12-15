package project.follow;

import java.io.Serializable;
import java.util.Date;

import kernel.bo.EntityObject;

/**
 * 用户跟随交易员详情表
 */
public class TraderFollowUser extends EntityObject<String> {

	private static final long serialVersionUID = -1617033543659508052L;
	private String partyId;
	/**
	 * 用户名
	 */
	private String username;

	/**
	 * 交易员partyId TRADER_PARTYID
	 */
	private String trader_partyId;

	/**
	 * 跟随购买品种 symbol
	 */
	private String symbol;

	/**
	 * 跟单固定张数/固定比例---选择 1,固定张数，2，固定比例
	 */
	private String follow_type;
	/**
	 * 状态 是否还在跟随状态 1,跟随，2，取消跟随
	 */
	private String state;

	/**
	 * 跟单张数或比例---具体值
	 */
	private double volume;
	/**
	 * 最大持仓张数
	 */
	private double volume_max;
	
	/**
	 * 累计跟单收益 PROFIT
	 */
	private double profit;
	/**
	 * 累计跟单本金 AMOUNT_SUM
	 */
	private double amount_sum;

	/**
	 * 止盈百分比
	 */
	private double stop_profit;
	/**
	 * 止损百分比
	 */
	private double stop_loss;

	/**
	 * 跟随时间----CREATE_TIME
	 */
	private Date create_time;

	public String getPartyId() {
		return partyId;
	}

	public void setPartyId(String partyId) {
		this.partyId = partyId;
	}

	public Date getCreate_time() {
		return create_time;
	}

	public void setCreate_time(Date create_time) {
		this.create_time = create_time;
	}

	public String getTrader_partyId() {
		return trader_partyId;
	}

	public void setTrader_partyId(String trader_partyId) {
		this.trader_partyId = trader_partyId;
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public double getVolume() {
		return volume;
	}

	public void setVolume(double volume) {
		this.volume = volume;
	}

	public double getVolume_max() {
		return volume_max;
	}

	public void setVolume_max(double volume_max) {
		this.volume_max = volume_max;
	}

	public double getStop_profit() {
		return stop_profit;
	}

	public void setStop_profit(double stop_profit) {
		this.stop_profit = stop_profit;
	}

	public double getStop_loss() {
		return stop_loss;
	}

	public void setStop_loss(double stop_loss) {
		this.stop_loss = stop_loss;
	}

	public String getFollow_type() {
		return follow_type;
	}

	public void setFollow_type(String follow_type) {
		this.follow_type = follow_type;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public double getProfit() {
		return profit;
	}

	public double getAmount_sum() {
		return amount_sum;
	}

	public void setProfit(double profit) {
		this.profit = profit;
	}

	public void setAmount_sum(double amount_sum) {
		this.amount_sum = amount_sum;
	}
	
	

}
