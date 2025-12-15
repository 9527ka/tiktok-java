package project.data.job;

import project.item.model.Item;

public class HandleObject {

	public static String type_depth = "depth";
	public static String type_trade = "trade";
	/**
	 */
	private String type;
	private Item item;

	/**
	 * K线图的参数line
	 */
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Item getItem() {
		return item;
	}

	public void setItem(Item item) {
		this.item = item;
	}

}
