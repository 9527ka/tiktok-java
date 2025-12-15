package project.wallet;

import kernel.bo.EntityObject;

import java.util.Date;


/**
 * 用户钱包 每日统计
 *
 */
public class WalletDay extends EntityObject<String> {


	private static final long serialVersionUID = 8800518972776269909L;
	/**
	 * 金额
	 */
	private double amount = 0.0D;

	/**
	 * 创建时间
	 */
	private Date createTime;


	public double getAmount() {
		return this.amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
}
