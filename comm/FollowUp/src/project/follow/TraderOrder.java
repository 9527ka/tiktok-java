package project.follow;

import java.io.Serializable;
import java.util.Date;

import kernel.bo.EntityObject;

/**
 * 交易员历史订单(保护虚假订单)
 */
public class TraderOrder extends EntityObject<String> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7332111188993920706L;

	private String partyId;
	
	/**
	 * 品种
	 */
	private String symbol;
	/**
	 * 订单 号
	 */
	private String order_no;
	
	/**
	 * "buy":买(多) "sell":卖(空)
	 */
	private String direction;
	
	/**
	 * 收益
	 */
	private double profit;
	
	private Date create_time;
	/**
	 * 平仓时间
	 */
	private Date close_time;

	/**
	 * 杠杆倍数[“开仓”若有10倍多单，就不能再下20倍多单]
	 */
	private Double lever_rate;
	
	/**
	 * 委托数量(张)
	 */
	private Double volume_open;
	/**
	 * 涨跌幅
	 */
	private double change_ratio;
	/**
	 * 状态。submitted 已提交（持仓）， created 完成（平仓）
	 */
	private String state = "created";
	/**
	 * 成交均价(成本)
	 */
	private Double trade_avg_price;

	/**
	 * 平仓均价
	 */
	private Double close_avg_price;
	
	public String getPartyId() {
		return partyId;
	}
	public String getSymbol() {
		return symbol;
	}
	public String getOrder_no() {
		return order_no;
	}
	public String getDirection() {
		return direction;
	}
	public double getProfit() {
		return profit;
	}
	public Date getCreate_time() {
		return create_time;
	}
	public Date getClose_time() {
		return close_time;
	}
	public Double getLever_rate() {
		return lever_rate;
	}
	public Double getVolume_open() {
		return volume_open;
	}
	public double getChange_ratio() {
		return change_ratio;
	}
	public void setPartyId(String partyId) {
		this.partyId = partyId;
	}
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	public void setOrder_no(String order_no) {
		this.order_no = order_no;
	}
	public void setDirection(String direction) {
		this.direction = direction;
	}
	public void setProfit(double profit) {
		this.profit = profit;
	}
	public void setCreate_time(Date create_time) {
		this.create_time = create_time;
	}
	public void setClose_time(Date close_time) {
		this.close_time = close_time;
	}
	public void setLever_rate(Double lever_rate) {
		this.lever_rate = lever_rate;
	}
	public void setVolume_open(Double volume_open) {
		this.volume_open = volume_open;
	}
	public void setChange_ratio(double change_ratio) {
		this.change_ratio = change_ratio;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public Double getTrade_avg_price() {
		return trade_avg_price;
	}
	public Double getClose_avg_price() {
		return close_avg_price;
	}
	public void setTrade_avg_price(Double trade_avg_price) {
		this.trade_avg_price = trade_avg_price;
	}
	public void setClose_avg_price(Double close_avg_price) {
		this.close_avg_price = close_avg_price;
	}
	
	
	
	
	

}
