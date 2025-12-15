package project.wallet;

import java.io.Serializable;
import java.util.Date;

import kernel.bo.EntityObject;
/**
 * 充提记录
 *
 */
public class WalletLog extends EntityObject<String> {
	private static final long serialVersionUID = 5914244062518608589L;

	private String partyId;

	private String order_no;

	// 原始充值金额，注意：不一定都是 USDT 单位，需要根据 wallettype 执行换算
	private double amount = 0.0D;

	// 如上：换算成 USDT 的金额
	private double usdtAmount = 0.0D;

	/**
	 * 资金账变类型，
	 * project.Constants(line:214-250)
	 */
	private String category;

	private int status;

	// 创建时间
	private Date createTime;

	private String createTimeStr;
	/**
	 * 交易币种，保持交易时的币种
	 */
	private String wallettype;

	public String getPartyId() {
		return partyId;
	}

	public void setPartyId(String partyId) {
		this.partyId = partyId;
	}

	public String getOrder_no() {
		return order_no;
	}

	public void setOrder_no(String order_no) {
		this.order_no = order_no;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getCreateTimeStr() {
		return createTimeStr;
	}

	public void setCreateTimeStr(String createTimeStr) {
		this.createTimeStr = createTimeStr;
	}

	public String getWallettype() {
		return wallettype;
	}

	public void setWallettype(String wallettype) {
		this.wallettype = wallettype;
	}

	public double getUsdtAmount() {
		return usdtAmount;
	}

	public void setUsdtAmount(double usdtAmount) {
		this.usdtAmount = usdtAmount;
	}
}
