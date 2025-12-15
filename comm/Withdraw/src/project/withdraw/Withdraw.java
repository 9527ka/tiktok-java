package project.withdraw;

import java.io.Serializable;
import java.util.Date;

import kernel.bo.EntityObject;

public class Withdraw extends EntityObject<String> {

	private static final long serialVersionUID = 5391586680142892251L;

	private String partyId;
	/**
	 * 商户订单号
	 */
	private String order_no;
	/**
	 * 订单总金额（必须大于 0），单位为对应币种的最小货币单位，人民币为元。
	 * 兑换成 USDT 后，并且扣除了手续费后的金额
	 */
	private double amount;

	/**
	 * 订单总金额（必须大于 0），单位为对应币种的最小货币单位，人民币为元。
	 * 原始金额对应的币种
	 */
	private double volume;

	/**
	 * 订单手续费，USDT。
	 */
	private double amount_fee;

	/**
	 * 状态 0 初始状态，未知 1 成功 2 失败，
	 */
	private int succeeded = 0;

	/**
	 * 提现货币 CNY USD
	 */
	private String currency;

	/**
	 * 错误信息
	 */
	private String failure_msg;
	/**
	 * 创建时间
	 */
	private Date createTime;

	/**
	 * 审核操作时间
	 */
	private Date reviewTime;

	private Date time_settle;

	/**
	 * 收款方式 bank 银行卡 alipay 支付宝 weixin 微信 paypal PayPal western 西联汇款 swift
	 * SWIFT国际汇款,USDT
	 * 
	 */
	private String method;

	/**
	 * 账号，银行卡号，支付宝账号，电汇地址等
	 */
	private String account;
	/**
	 * 二维码
	 */
	private String qdcode;

	/**
	 * 姓名
	 */
	private String username;

	/*
	 * 以下是银行卡专用
	 */
	/**
	 * 银行
	 */
	private String bank;
	/**
	 * 开户行
	 */
	private String deposit_bank;
	/**
	 * 路由号码
	 */
	private String routingNum;

	/**
	 * 账户地址
	 */
	private String accountAddress;

	/**
	 * 银行地址
	 */
	private String bankAddress;

	/**
	 * 国际代码
	 */
	private String swiftCode;


	/**
	 * 国籍
	 */
	private String countryName;

	/**
	 * USDT地址
	 */
	private String address;

	/**
	 * hash值
	 */
	private String tx;

	/**
	 * 真实客损
	 * @return
	 */
	/**
	 * 业务员提成
	 */
	private Double withdrawCommission = 0.0;

	/**
	 * 提现币种下，扣除手续费后的实际到账金额
	 */
	private Double arrivalAmount = 0.0;

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

	public int getSucceeded() {
		return succeeded;
	}

	public void setSucceeded(int succeeded) {
		this.succeeded = succeeded;
	}

	public String getFailure_msg() {
		return failure_msg;
	}

	public void setFailure_msg(String failure_msg) {
		this.failure_msg = failure_msg;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public double getAmount_fee() {
		return amount_fee;
	}

	public void setAmount_fee(double amount_fee) {
		this.amount_fee = amount_fee;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getTime_settle() {
		return time_settle;
	}

	public void setTime_settle(Date time_settle) {
		this.time_settle = time_settle;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getQdcode() {
		return qdcode;
	}

	public void setQdcode(String qdcode) {
		this.qdcode = qdcode;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getBank() {
		return bank;
	}

	public void setBank(String bank) {
		this.bank = bank;
	}

	public String getDeposit_bank() {
		return deposit_bank;
	}

	public void setDeposit_bank(String deposit_bank) {
		this.deposit_bank = deposit_bank;
	}

	public double getVolume() {
		return volume;
	}

	public void setVolume(double volume) {
		this.volume = volume;
	}

	public Date getReviewTime() {
		return reviewTime;
	}

	public void setReviewTime(Date reviewTime) {
		this.reviewTime = reviewTime;
	}

	public String getTx() {
		return this.tx;
	}

	public void setTx(String tx) {
		this.tx = tx;
	}

	public Double getWithdrawCommission() {
		return withdrawCommission;
	}

	public void setWithdrawCommission(Double withdrawCommission) {
		this.withdrawCommission = withdrawCommission;
	}

	public Double getArrivalAmount() {
		return arrivalAmount;
	}

	public void setArrivalAmount(Double arrivalAmount) {
		this.arrivalAmount = arrivalAmount;
	}

	public String getRoutingNum() {
		return routingNum;
	}

	public void setRoutingNum(String routingNum) {
		this.routingNum = routingNum;
	}

	public String getAccountAddress() {
		return accountAddress;
	}

	public void setAccountAddress(String accountAddress) {
		this.accountAddress = accountAddress;
	}

	public String getBankAddress() {
		return bankAddress;
	}

	public void setBankAddress(String bankAddress) {
		this.bankAddress = bankAddress;
	}

	public String getCountryName() {
		return countryName;
	}

	public void setCountryName(String countryName) {
		this.countryName = countryName;
	}

	public String getSwiftCode() {
		return swiftCode;
	}

	public void setSwiftCode(String swiftCode) {
		this.swiftCode = swiftCode;
	}
}
