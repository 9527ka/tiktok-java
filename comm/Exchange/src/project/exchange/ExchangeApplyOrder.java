package project.exchange;

import java.io.Serializable;
import java.util.Date;

import kernel.bo.EntityObject;

/**
 * 委托单
 */
public class ExchangeApplyOrder extends EntityObject<String> {

	private static final long serialVersionUID = -7263336511778693149L;
	public final static String STATE_SUBMITTED = "submitted";
	public final static String STATE_CREATED = "created";
	public final static String STATE_CANCELED = "canceled";

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

	private String partyId;
	
	/**
	 * 订单 号
	 */
	private String relation_order_no;

	/**
	 * 订单 号
	 */
	private String order_no;

	private String symbol;
	
	/**
	 * 币种数量
	 */
	private Double symbol_value;

	/**
	 * "open":买入 "close":卖出
	 */
	private String offset;
	/**
	 * 委托数量
	 */
	private Double volume;

	/**
	 * 手续费
	 */
	private double fee;

	/**
	 * 金额（USDT计价）
	 */
	private Double amount;

	/**
	 * 手续费(USDT计价)
	 */
	private Double wallet_fee;

	/**
	 * limit order的交易价格
	 */
	private Double price;

	/**
	 * 订单报价类型。 "limit":限价 "opponent":对手价（市价）
	 */
	private String order_price_type;
	/**
	 * 状态。submitted 已提交，canceled 已撤销， created 委托完成
	 */
	private String state = "submitted";
	/**
	 * 创建时间
	 */
	private Date create_time;
	/**
	 * 成交时行情点位
	 */
	private Double close_price;
	/**
	 * 成交时间
	 */
	private Date close_time;
	/**
	 * 是否计划委托
	 */
	private boolean is_trigger_order = false;
	/**
	 * 计划委托的触发价
	 */
	private Double trigger_price;

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

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
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

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public Double getWallet_fee() {
		return wallet_fee;
	}

	public void setWallet_fee(Double wallet_fee) {
		this.wallet_fee = wallet_fee;
	}

	public Double getClose_price() {
		return close_price;
	}

	public void setClose_price(Double close_price) {
		this.close_price = close_price;
	}

	public Date getClose_time() {
		return close_time;
	}

	public void setClose_time(Date close_time) {
		this.close_time = close_time;
	}

	public boolean isIs_trigger_order() {
		return is_trigger_order;
	}

	public void setIs_trigger_order(boolean is_trigger_order) {
		this.is_trigger_order = is_trigger_order;
	}

	public Double getTrigger_price() {
		return trigger_price;
	}

	public void setTrigger_price(Double trigger_price) {
		this.trigger_price = trigger_price;
	}
	
	public String getRelation_order_no() {
		return relation_order_no;
	}

	public void setRelation_order_no(String relation_order_no) {
		this.relation_order_no = relation_order_no;
	}

	public Double getSymbol_value() {
		return symbol_value;
	}

	public void setSymbol_value(Double symbol_value) {
		this.symbol_value = symbol_value;
	}

}
