package project.invest.project.model;

import kernel.bo.EntityObject;
import lombok.Data;

@Data
public class InvestRebate extends EntityObject<String> {

    private static final long serialVersionUID = -324021713418425625L;

    private String partyId;

	private String orderPartyId;

	/**
	 * 佣金
	 */
	private double rebate;

	/**
	 * 投资金额
	 */
	private double amount;

	private String orderId;

	/**
	 * 0=已发放1=待发送
	 */
	private int status;

	private int level;

    private String createTime;

	private String realTime;


}