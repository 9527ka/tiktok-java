package project.contract;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.Date;

import kernel.bo.EntityObject;
import kernel.util.Arith;

public class ContractOrder extends EntityObject<String> {
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
	 * 每手金额
	 */
	private double unit_amount;

	/**
	 * 平仓退回金额
	 */
	private double amount_close;

	/**
	 * 手续费
	 */
	private double fee;

	/**
	 * 保证金(剩余)
	 */
	private double deposit;

	/**
	 * 保证金
	 */
	private double deposit_open;

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
	 * 止盈触发价格
	 */
	private Double stop_price_profit;
	/**
	 * 止损触发价格
	 */
	private Double stop_price_loss;

	/**
	 * 最小浮动
	 */
	private double pips;
	/**
	 * 最小浮动金额（以交易金额计算）
	 */
	private double pips_amount;

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
	 * 杠杆倍数[“开仓”若有10倍多单，就不能再下20倍多单]
	 */
	private Double lever_rate;

	/**
	 * 涨跌幅
	 */
	private double change_ratio;

	/**
	 * 委托数量(剩余)(张)
	 */
	private Double volume;
	/**
	 * 委托数量(张)
	 */
	private Double volume_open;

	public Double getChange_ratio() {
		if (STATE_SUBMITTED.equals(state)) {
			change_ratio = Arith.div(Arith.sub(Arith.add(Arith.add(amount_close, profit), deposit), deposit_open),
					deposit_open);
		} else {
			change_ratio = Arith.div(Arith.sub(Arith.add(amount_close, deposit), deposit_open), deposit_open);

		}

		change_ratio = Arith.mul(change_ratio, 100);
		DecimalFormat df = new DecimalFormat("#.##");
		return Double.valueOf(df.format(change_ratio));
	}

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

	public double getDeposit() {
		return deposit;
	}

	public void setDeposit(double deposit) {
		this.deposit = deposit;
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

	public Double getStop_price_profit() {
		return stop_price_profit;
	}

	public void setStop_price_profit(Double stop_price_profit) {
		this.stop_price_profit = stop_price_profit;
	}

	public Double getStop_price_loss() {
		return stop_price_loss;
	}

	public void setStop_price_loss(Double stop_price_loss) {
		this.stop_price_loss = stop_price_loss;
	}

	public double getPips() {
		return pips;
	}

	public void setPips(double pips) {
		this.pips = pips;
	}

	public double getPips_amount() {
		return pips_amount;
	}

	public void setPips_amount(double pips_amount) {
		this.pips_amount = pips_amount;
	}

	public Date getCreate_time() {
		return create_time;
	}

	public void setCreate_time(Date create_time) {
		this.create_time = create_time;
	}

	public Double getLever_rate() {
		return lever_rate;
	}

	public void setLever_rate(Double lever_rate) {
		this.lever_rate = lever_rate;
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

	public double getAmount_close() {
		return amount_close;
	}

	public void setAmount_close(double amount_close) {
		this.amount_close = amount_close;
	}

	public double getDeposit_open() {
		return deposit_open;
	}

	public void setDeposit_open(double deposit_open) {
		this.deposit_open = deposit_open;
	}

	public double getUnit_amount() {
		return unit_amount;
	}

	public void setUnit_amount(double unit_amount) {
		this.unit_amount = unit_amount;
	}

	public Double getVolume() {
		return volume;
	}

	public void setVolume(Double volume) {
		this.volume = volume;
	}

	public Double getVolume_open() {
		return volume_open;
	}

	public void setVolume_open(Double volume_open) {
		this.volume_open = volume_open;
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

}
