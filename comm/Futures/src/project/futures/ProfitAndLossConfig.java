package project.futures;

import java.io.Serializable;

import kernel.bo.EntityObject;

public class ProfitAndLossConfig extends EntityObject<String> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1367032133017670721L;
	/**
	 * 盈利
	 */
	public final static String TYPE_PROFIT = "profit";
	public final static String TYPE_LOSS = "loss";
	public final static String TYPE_BUY_PROFIT = "buy_profit";
	public final static String TYPE_SELL_PROFIT = "sell_profit";
	/**
	 * 买多盈利并且买空亏损
	 */
	public final static String TYPE_BUY_PROFIT_SELL_LOSS = "buy_profit_sell_loss";
	/**
	 * 买空盈利并且买多亏损
	 */
	public final static String TYPE_SELL_PROFIT_BUY_LOSS = "sell_profit_buy_loss";

	private String partyId;
	/**
	 * 见TYPE_* 1 盈利，2亏损，3 买多盈利，4买空盈利
	 */
	private String type;

	/**
	 * 备注
	 */
	private String remark;

	public String getPartyId() {
		return partyId;
	}

	public void setPartyId(String partyId) {
		this.partyId = partyId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

}
