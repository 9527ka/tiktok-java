package project.item.model;

import kernel.bo.EntityObject;

/**
 * 
 * 杠杆定义
 */
public class ItemLever extends EntityObject<String> {
	private static final long serialVersionUID = -6464213099743653537L;
	private String item_id;
	/**
	 * 杠杆倍数
	 */
	private double lever_rate;

	public String getItem_id() {
		return item_id;
	}

	public void setItem_id(String item_id) {
		this.item_id = item_id;
	}

	public double getLever_rate() {
		return lever_rate;
	}

	public void setLever_rate(double lever_rate) {
		this.lever_rate = lever_rate;
	}

}
