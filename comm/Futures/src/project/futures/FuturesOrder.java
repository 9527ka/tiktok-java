package project.futures;

import java.io.Serializable;
import java.util.Date;

import kernel.bo.EntityObject;

public class FuturesOrder extends EntityObject<String> {
	public final static String STATE_SUBMITTED = "submitted";
	public final static String STATE_CREATED = "created";
	/**
	 * 多仓
	 */
	public final static String DIRECTION_BUY = "buy";
	/**
	 * 空仓
	 */
	public final static String DIRECTION_SELL = "sell";
	private static final long serialVersionUID = 8847718625460348172L;

	private String partyId;
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
	 * 时间
	 */
	private int timeNum;
	/**
	 * 时间单位
	 * 
	 */
	private String timeUnit;

//	/**
//	 * 每手金额
//	 */
//	private double unit_amount;

	/**
	 * 委托数量
	 */
	private Double volume;

	/**
	 * 手续费
	 */
	private double fee;

	/**
	 * 收益率
	 */
	private double profit_ratio;

	/**
	 * 收益
	 */
	private double profit;

	/**
	 * 成交均价(成本)
	 */
	private Double trade_avg_price;

	/**
	 * 平仓均价
	 */
	private Double close_avg_price;

	/**
	 * 状态。submitted 已提交（持仓）， created 完成（平仓）
	 */
	private String state = "submitted";

	private Date create_time;
	/**
	 * 平仓时间
	 */
	private Date close_time;

	/**
	 * 结算时间
	 */
	private Date settlement_time;

	/**
	 * 剩余时间 h:m:s
	 */
	private String remain_time;
	/**
	 * 购买时控制场控
	 */
	private String profit_loss;

	public String getPartyId() {
		return partyId;
	}

	public void setPartyId(String partyId) {
		this.partyId = partyId;
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public String getOrder_no() {
		return order_no;
	}

	public void setOrder_no(String order_no) {
		this.order_no = order_no;
	}

	public String getDirection() {
		return direction;
	}

	public void setDirection(String direction) {
		this.direction = direction;
	}

	public double getFee() {
		return fee;
	}

	public void setFee(double fee) {
		this.fee = fee;
	}

	public double getProfit() {
		return profit;
	}

	public void setProfit(double profit) {
		this.profit = profit;
	}

	public Double getTrade_avg_price() {
		return trade_avg_price;
	}

	public void setTrade_avg_price(Double trade_avg_price) {
		this.trade_avg_price = trade_avg_price;
	}

	public Date getCreate_time() {
		return create_time;
	}

	public void setCreate_time(Date create_time) {
		this.create_time = create_time;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

//	public double getUnit_amount() {
//		return unit_amount;
//	}
//
//	public void setUnit_amount(double unit_amount) {
//		this.unit_amount = unit_amount;
//	}

	public Double getVolume() {
		return volume;
	}

	public void setVolume(Double volume) {
		this.volume = volume;
	}

	public Double getClose_avg_price() {
		return close_avg_price;
	}

	public void setClose_avg_price(Double close_avg_price) {
		this.close_avg_price = close_avg_price;
	}

	public Date getClose_time() {
		return close_time;
	}

	public void setClose_time(Date close_time) {
		this.close_time = close_time;
	}

	public int getTimeNum() {
		return timeNum;
	}

	public void setTimeNum(int timeNum) {
		this.timeNum = timeNum;
	}

	public String getTimeUnit() {
		return timeUnit;
	}

	public void setTimeUnit(String timeUnit) {
		this.timeUnit = timeUnit;
	}

	public double getProfit_ratio() {
		return profit_ratio;
	}

	public void setProfit_ratio(double profit_ratio) {
		this.profit_ratio = profit_ratio;
	}

	public Date getSettlement_time() {
		return settlement_time;
	}

	public void setSettlement_time(Date settlement_time) {
		this.settlement_time = settlement_time;
	}

	public String getRemain_time() {
		return remain_time;
	}

	public void setRemain_time(String remain_time) {
		this.remain_time = remain_time;
	}

	public String getProfit_loss() {
		return profit_loss;
	}

	public void setProfit_loss(String profit_loss) {
		this.profit_loss = profit_loss;
	}

}
