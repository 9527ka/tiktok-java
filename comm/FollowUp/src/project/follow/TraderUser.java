package project.follow;

import java.io.Serializable;
import java.util.Date;

import kernel.bo.EntityObject;

/**
 * 用户跟随交易员累计收益表
 */
public class TraderUser extends EntityObject<String> {

	private static final long serialVersionUID = -1617033543659508052L;

	private String partyId;

	/**
	 * 用户账号 邮箱号或手机号
	 */
	private String name;

	/**
	 * 累计跟单收益 PROFIT
	 */
	private double profit;
	/**
	 * 累计跟单本金 AMOUNT_SUM
	 */
	private double amount_sum;

	/**
	 * 入驻时间----CREATE_TIME
	 */
	private Date create_time;

	public String getPartyId() {
		return partyId;
	}

	public void setPartyId(String partyId) {
		this.partyId = partyId;
	}

	public double getProfit() {
		return profit;
	}

	public void setProfit(double profit) {
		this.profit = profit;
	}

	

	public double getAmount_sum() {
		return amount_sum;
	}

	public void setAmount_sum(double amount_sum) {
		this.amount_sum = amount_sum;
	}


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getCreate_time() {
		return create_time;
	}

	public void setCreate_time(Date create_time) {
		this.create_time = create_time;
	}

}
