package project.item.model;

import kernel.bo.EntityObject;

/**
 * 产品
 *
 */
public class ItemUserOptional extends EntityObject<String> {

	private static final long serialVersionUID = 4857935723215615892L;

	private String partyId;

	/**
	 * 代码
	 */
	private String symbol;

	public String getPartyId() {
		return partyId;
	}

	public String getSymbol() {
		return symbol;
	}

	public void setPartyId(String partyId) {
		this.partyId = partyId;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

}
