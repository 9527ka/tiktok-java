package project.log;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import kernel.bo.EntityObject;
import project.mall.orders.model.MallOrderRebate;

public class MoneyLog extends EntityObject<String> {
	private static final long serialVersionUID = -5914896022101327097L;
	// 提现时，无论什么币种，都以 USDT 标记
	private String wallettype;
	private String category;
	private Date createTime;
	private Serializable partyId;

	private String createTimeStr;
	private double amount = 0.0D;

	// 存的是 USDT 的金额
	private double amount_before = 0.0D;

	// 存的是 USDT 的金额
	private double amount_after = 0.0D;
	private int freeze = 0 ;

	private int notify = 0 ;
	private Date notifyTime;
	private String log;
	private String oderNum;
	private List<MallOrderRebate> detail;
	/**
	 * 资金日志提供的内容 ：提币 充币 永续建仓 永续平仓 手续费
	 */
	private String content_type;

	public int getFreeze() {
		return freeze;
	}

	public void setFreeze(int freeze) {
		this.freeze = freeze;
	}

	/**
	 * 充值提现备注
	 */
	private String remarks;

	public List<MallOrderRebate> getDetail() {
		return detail;
	}

	public void setDetail(List<MallOrderRebate> detail) {
		this.detail = detail;
	}

	public String getWallettype() {
		return this.wallettype;
	}

	public void setWallettype(String wallettype) {
		this.wallettype = wallettype;
	}

	public Date getCreateTime() {
		return this.createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Serializable getPartyId() {
		return this.partyId;
	}

	public void setPartyId(Serializable partyId) {
		this.partyId = partyId;
	}

	public double getAmount() {
		return this.amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public double getAmount_before() {
		return this.amount_before;
	}

	public void setAmount_before(double amount_before) {
		this.amount_before = amount_before;
	}

	public double getAmount_after() {
		return this.amount_after;
	}

	public void setAmount_after(double amount_after) {
		this.amount_after = amount_after;
	}

	public String getLog() {
		return this.log;
	}

	public void setLog(String log) {
		this.log = log;
	}

	public String getCategory() {
		return this.category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getCreateTimeStr() {
		return createTimeStr;
	}

	public void setCreateTimeStr(String createTimeStr) {
		this.createTimeStr = createTimeStr;
	}

	public String getContent_type() {
		return content_type;
	}

	public void setContent_type(String content_type) {
		this.content_type = content_type;
	}


	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public int getNotify() {
		return notify;
	}

	public void setNotify(int notify) {
		this.notify = notify;
	}

	public Date getNotifyTime() {
		return notifyTime;
	}

	public void setNotifyTime(Date notifyTime) {
		this.notifyTime = notifyTime;
	}

	public String getOderNum() {
		return oderNum;
	}

	public void setOderNum(String oderNum) {
		this.oderNum = oderNum;
	}
}
