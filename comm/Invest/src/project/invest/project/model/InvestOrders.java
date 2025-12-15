package project.invest.project.model;

import kernel.bo.EntityObject;
import lombok.Data;

import java.util.Date;

@Data
public class InvestOrders extends EntityObject<String> {
	private static final long serialVersionUID = -6585270719884278L;

	/**
	 * 项目ID
	 */
	private String projectId;
	/**
	 * 会员ID
	 */
	private String partyId;


	/**
	 * 投资金额
	 */
	private double amount;

	/**
	 * 创建时间
	 */
	private Date createTime;

	/**
	 * 最后审批时间
	 */
	private Date auditsTime;


	/**
	 * 状态：（-1=取消）（0=进行中）（1=关闭）（2=已完成）
	 */
	private int status;

	/**
	 * 分红比例
	 */
	private double bonusRate;

	/**
	 * 分红比例+Vip
	 */
	private double bonusRateVip;

	/**
	 * 锁仓期限
	 */
	private Integer bonus;

	/**
	 * 1=按小时付收益，到期返本；
	 * 2=按小时算收益，到期返本+分红；
	 * 3=按天付收益，到期返本；
	 * 4=按天算收益，到期返本+分红
	 */
	private Integer type;

	/**
	 * 收益
	 */
	private double income;

	/**
	 * 预计产值
	 */
	private double incomeWill;

	/**
	 * 结束时间
	 */
	private Long sucessWill;

	/**
	 * 下次分红时间（可能尾付）
	 */
	private Long nextWill;

	/**
	 * 最后分红时间
	 */
	private long upTime;

}
