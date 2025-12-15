package project.data.model;

import java.io.Serializable;

/**
 * An depth entry consisting of price and amount.
 */
public class DepthEntry  implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2177789072570310524L;
	
	private Double price;
	private Double amount;

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

}
