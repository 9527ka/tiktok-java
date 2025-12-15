package project.contract;

import java.io.Serializable;
import java.util.Date;

import kernel.bo.EntityObject;

/**
 * 委托单
 */
public class ContractApplyOrder extends EntityObject<String> {

	public final static String STATE_SUBMITTED = "submitted";
	public final static String STATE_CANCELED = "canceled";
	public final static String STATE_CREATED = "created";
	/**
	 * 多仓
	 */
	public final static String DIRECTION_BUY = "buy";
	/**
	 * 空仓
	 */
	public final static String DIRECTION_SELL = "sell";
	/**
	 * 开仓
	 */
	public final static String OFFSET_OPEN = "open";

	/**
	 * 平仓
	 */
	public final static String OFFSET_CLOSE = "close";

	/**
	 * 限价单
	 */
	public final static String ORDER_PRICE_TYPE_LIMIT = "limit";

	/**
	 * 对手价（市价）
	 */
	public final static String ORDER_PRICE_TYPE_OPPONENT = "opponent";

	private static final long serialVersionUID = 3005514385287413248L;

	private String partyId;

	/**
	 * 订单 号
	 */
	private String order_no;

	private String symbol;
	/**
	 * "buy":多 "sell":空
	 */
	private String direction;

	/**
	 * "open":开 "close":平
	 */
	private String offset;
	/**
	 * 委托数量(剩余)(张)
	 */
	private Double volume;
	/**
	 * 委托数量(张)
	 */
	private Double volume_open;
	/**
	 * 杠杆倍数[“开仓”若有10倍多单，就不能再下20倍多单]
	 */
	private Double lever_rate;

	/**
	 * limit order的交易价格
	 */
	private Double price;
	/**
	 * 止盈触发价格
	 */
	private Double stop_price_profit;
	/**
	 * 止损触发价格
	 */
	private Double stop_price_loss;
	/**
	 * 订单报价类型。 "limit":限价 "opponent":对手价（市价）
	 */
	private String order_price_type;
	/**
	 * 状态。submitted 已提交，canceled 已撤销， created 委托完成
	 */
	private String state = "submitted";

	private Date create_time;

	/**
	 * 手续费
	 */
	private double fee;

	/**
	 * 保证金
	 */
	private double deposit;

	/**
	 * 每手金额
	 */
	private double unit_amount;

	public String getPartyId() {
		return partyId;
	}

	public void setPartyId(String partyId) {
		this.partyId = partyId;
	}

	public String getOrder_no() {
		return order_no;
	}

	public void setOrder_no(String order_no) {
		this.order_no = order_no;
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public String getDirection() {
		return direction;
	}

	public void setDirection(String direction) {
		this.direction = direction;
	}

	public String getOffset() {
		return offset;
	}

	public void setOffset(String offset) {
		this.offset = offset;
	}

	public Double getVolume() {
		return volume;
	}

	public void setVolume(Double volume) {
		this.volume = volume;
	}

	public Double getLever_rate() {
		return lever_rate;
	}

	public void setLever_rate(Double lever_rate) {
		if (lever_rate != null && lever_rate == 1) {
			lever_rate = null;
		}
		this.lever_rate = lever_rate;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
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

	public String getOrder_price_type() {
		return order_price_type;
	}

	public void setOrder_price_type(String order_price_type) {
		this.order_price_type = order_price_type;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public Date getCreate_time() {
		return create_time;
	}

	public void setCreate_time(Date create_time) {
		this.create_time = create_time;
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

	public Double getVolume_open() {
		return volume_open;
	}

	public void setVolume_open(Double volume_open) {
		this.volume_open = volume_open;
	}

	public double getUnit_amount() {
		return unit_amount;
	}

	public void setUnit_amount(double unit_amount) {
		this.unit_amount = unit_amount;
	}

}
