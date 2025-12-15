package project.monitor.pledgegalaxy;

import java.io.Serializable;
import java.util.Date;

/**
 * 质押收益记录实体
 */
public class PledgeGalaxyProfit implements Serializable {

	private static final long serialVersionUID = -5200991181835291185L;

	private String id;

	/**
	 * 玩家id
	 */
	private String partyId;

	/**
	 * 收益记录类型
	 */
	private int type;

	/**
	 * 收益金额
	 */
	private double amount;

	/**
	 * 0 质押确认中 1 质押 2 失败 3 赎回确认中 4 已赎回
	 */
	private int status;

	/**
	 * 审核时间
	 */
	private Date auditTime;

	/**
	 * 收益过期时间
	 */
	private Date expireTime;

	/**
	 * 创建时间
	 */
	private Date createTime;

	/**
	 * 关联质押订单号
	 */
	private String relationOrderNo;

	/**
	 * 驳回原因
	 */
	private String msg;

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getPartyId() {
		return partyId;
	}

	public void setPartyId(String partyId) {
		this.partyId = partyId;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public Date getAuditTime() {
		return auditTime;
	}

	public void setAuditTime(Date auditTime) {
		this.auditTime = auditTime;
	}

	public Date getExpireTime() {
		return expireTime;
	}

	public void setExpireTime(Date expireTime) {
		this.expireTime = expireTime;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getRelationOrderNo() {
		return relationOrderNo;
	}

	public void setRelationOrderNo(String relationOrderNo) {
		this.relationOrderNo = relationOrderNo;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

}
