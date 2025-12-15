package project.miner.model;


import kernel.bo.EntityObject;

/**
 * 矿机配置参数
 *
 */
public class MinerPara extends EntityObject<String> {

	private static final long serialVersionUID = 1639941028310043811L;

	/**
	 * 矿机id
	 */
	private String miner_id;
	/**
	 * 购买周期
	 */
	private int cycle;

	/**
	 * 购买价格
	 */
	private double amount;

	public int getCycle() {
		return cycle;
	}

	public double getAmount() {
		return amount;
	}

	public void setCycle(int cycle) {
		this.cycle = cycle;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public String getMiner_id() {
		return miner_id;
	}

	public void setMiner_id(String miner_id) {
		this.miner_id = miner_id;
	}

	
	
	


	
	
}
