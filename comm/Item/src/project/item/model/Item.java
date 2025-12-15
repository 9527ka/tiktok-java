package project.item.model;

import kernel.bo.EntityObject;

/**
 * 产品
 *
 */
public class Item extends EntityObject<String> {

	private static final long serialVersionUID = 4857935723215615892L;
	/**
	 * 交割合约
	 */
	public static final String DELIVERY_CONTRACT = "DELIVERY";
	/**
	 * 永续合约
	 */
	public static final String FOREVER_CONTRACT = "FOREVER";

	/**
	 * 名称
	 */
	private String name;

	/**
	 * 代码
	 */
	private String symbol;

	/**
	 * 数据源编码
	 */
	private String symbol_data;

	/**
	 * 最小浮动
	 */
	private double pips;

	private String pips_str;

	/**
	 * 最小浮动金额（以交易金额计算）
	 */
	private double pips_amount;
	
	private String pips_amount_str;

	private Double adjustment_value = new Double(0);

	/**
	 * 每手金额
	 */
	private double unit_amount;

	/**
	 * 每手的手续费
	 */
	private double unit_fee;

	/**
	 * 市场
	 */
	private String market;
	/**
	 * 小数位精度
	 */
	private Integer decimals;
	/**
	 * 交易量放大倍数，如果为0或者空不进行操作，否则乘以倍数
	 */
	private double multiple = new Double(0);
	/**
	 * 借贷利率
	 */
	private double borrowing_rate;
	
	/**
	 * 币种全称
	 */
	private String symbolFullName;

	public String getSymbolFullName() {
		return symbolFullName;
	}

	public void setSymbolFullName(String symbolFullName) {
		this.symbolFullName = symbolFullName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
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

	public Double getAdjustment_value() {
		return adjustment_value;
	}

	public void setAdjustment_value(Double adjustment_value) {
		this.adjustment_value = adjustment_value;
	}

	public String getMarket() {
		return market;
	}

	public void setMarket(String market) {
		this.market = market;
	}

	public String getSymbol_data() {
		return symbol_data;
	}

	public void setSymbol_data(String symbol_data) {
		this.symbol_data = symbol_data;
	}

	public double getUnit_amount() {
		return unit_amount;
	}

	public void setUnit_amount(double unit_amount) {
		this.unit_amount = unit_amount;
	}

	public double getUnit_fee() {
		return unit_fee;
	}

	public void setUnit_fee(double unit_fee) {
		this.unit_fee = unit_fee;
	}

	public Integer getDecimals() {
		return decimals;
	}

	public void setDecimals(Integer decimals) {
		this.decimals = decimals;
	}

	public String getPips_str() {
		return pips_str;
	}

	public void setPips_str(String pips_str) {
		this.pips_str = pips_str;
	}

	public double getMultiple() {
		return multiple;
	}

	public void setMultiple(double multiple) {
		this.multiple = multiple;
	}

	public double getBorrowing_rate() {
		return borrowing_rate;
	}

	public void setBorrowing_rate(double borrowing_rate) {
		this.borrowing_rate = borrowing_rate;
	}

	public String getPips_amount_str() {
		return pips_amount_str;
	}

	public void setPips_amount_str(String pips_amount_str) {
		this.pips_amount_str = pips_amount_str;
	}
	
}
