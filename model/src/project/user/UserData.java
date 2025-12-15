package project.user;

import java.io.Serializable;
import java.util.Date;

import kernel.bo.EntityObject;

/*
 * 用户数据（每日）
 * 如果是代理商是伞下用户的累计（不包含 演示用户），如果是用户则是自已数据的统计
 */
public class UserData extends EntityObject {

	private static final long serialVersionUID = 7833547090260373374L;

	/**
	 * 角色
	 */
	private String rolename;

	private Serializable partyId;
	
	/**
	 * 充值_DAPP
	 */
	private double recharge_dapp;
	
	/**
	 * 提现_DAPP
	 */
	private double withdraw_dapp;
	
	/*
	 * 充提
	 */
	/**
	 * 充值金额
	 */
	private double recharge;
	/**
	 * 充值金额-Recharge_USDT
	 */
	private double recharge_usdt;

	/**
	 * 2023-7-29 新增后又放弃
	 * 充值金额-usdc
	 */
//	private double recharge_usdc;

	/**
	 * 充值金额-Recharge_ETH
	 */
	private double recharge_eth;
	/**
	 * 充值金额- Recharge_BTC
	 */
	private double recharge_btc;
	/**
	 * 充值金额- recharge_usdc
	 */
	private double recharge_usdc;
	/**
	 * 充值金额- Recharge_LTC
	 */
	private double recharge_ltc;
	
	/**
	 * 充值-返佣
	 */
	private double rechargeRecom;
	
	/**
	 * 提现金额（所有都换算成u）
	 */
	private double withdraw_all;
	/**
	 * 提现金额(汇率转化后usdt)
	 */
	private double withdraw;
	/**
	 * 提现eth
	 */
	private double withdraw_eth;
	/**
	 * 提现(usdt)
	 */
	private double withdraw_usdt;
	/**
	 * 提现withdraw_usdc
	 */
	private double withdraw_usdc;

	/**
	 * 提现btc
	 */
	private double withdraw_btc;
	/**
	 * 充提手续费
	 */
	private double recharge_withdrawal_fee;
	/**
	 * 礼金
	 */
	private double gift_money;

	/*
	 * 永续
	 */
	/**
	 * 永续合约下单金额
	 */
	private double amount;
	/**
	 * 永续合约手续费
	 */
	private double fee;
	/**
	 * 永续合约收益
	 */
	private double order_income;

	/*
	 * 理财
	 */

	/**
	 * 理财买入金额
	 */

	private double finance_amount;

	/**
	 * 理财收益
	 */
	private double finance_income;

	/*
	 * 币币
	 */
	/**
	 * 交易金额（买入和卖出），USDT计价
	 */

	private double exchange_amount;
	/**
	 * 币币手续费
	 */
	private double exchange_fee;
	/**
	 * 币币收益
	 */
	private double exchange_income;
	/**
	 * 自发币收益
	 */
	private double coin_income;

	/*
	 * 交割合约
	 */

	/**
	 * 交割合约下单金额
	 */
	private double furtures_amount;
	/**
	 * 交割合约手续费
	 */
	private double furtures_fee;
	/**
	 * 交割合约收益
	 */
	private double furtures_income;

	/*
	 * 矿机
	 */
	/**
	 * 矿机下单金额
	 */
	private double miner_amount;
	/**
	 * 矿机收益
	 */
	private double miner_income;
	
	// 质押2.0金额
	private double galaxy_amount;
	
	// 质押2.0收益
	private double galaxy_income;

	/**
	 * 三方充值(USDT)
	 */
	private double third_recharge_amount;
	/**
	 * 持有金额数量
	 */
	private double holding_money;
	/**
	 * 转入金额USDT计价
	 */
	private double transfer_in_money;
	/**
	 * 转出金额USDT计价
	 */
	private double transfer_out_money;
	/*
	 * 币币杠杆
	 */
	/**
	 * 币币杠杆下单金额
	 */
	private double exchange_lever_amount;
	/**
	 * 币币杠杆手续费
	 */
	private double exchange_lever_fee;
	/**
	 * 币币杠杆收益
	 */
	private double exchange_lever_order_income;
	/*
	 * 伞下推荐用户计划
	 */

	/**
	 * 推荐人数（伞下）（目前是4级）
	 */
	private int reco_num;

	/**
	 * 佣金
	 */
	private double rebate1;
	private double rebate2;

	private double rebate3;


	/**
	 * 日期
	 */
	private Date createTime;


	/**
	 * 订单结算
	 */
	private double translate;

	/**
	 * 店铺销售总佣金
	 */
	private double sellerCommission;

	/**
	 * 店铺销售总额
	 */
	private double sellerTotalSales;

	/**
	 * 推广返佣金额
	 */
	private double promoteAmount;

	/**
	 * 充值提成
	 */
	private double rechargeCommission;

	/**
	 * 提现扣除提成
	 */
	private double withdrawCommission;


	public Serializable getPartyId() {
		return this.partyId;
	}

	public void setPartyId(Serializable partyId) {
		this.partyId = partyId;
	}

	public double getRecharge() {
		return this.recharge;
	}

	public void setRecharge(double recharge) {
		this.recharge = recharge;
	}

	public double getWithdraw() {
		return this.withdraw;
	}

	public void setWithdraw(double withdraw) {
		this.withdraw = withdraw;
	}

	public double getAmount() {
		return this.amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public double getFee() {
		return this.fee;
	}

	public void setFee(double fee) {
		this.fee = fee;
	}

	public double getOrder_income() {
		return this.order_income;
	}

	public void setOrder_income(double order_income) {
		this.order_income = order_income;
	}

	public Date getCreateTime() {
		return this.createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public double getFinance_income() {
		return finance_income;
	}

	public void setFinance_income(double finance_income) {
		this.finance_income = finance_income;
	}

	public String getRolename() {
		return rolename;
	}

	public void setRolename(String rolename) {
		this.rolename = rolename;
	}

	public double getFinance_amount() {
		return finance_amount;
	}

	public void setFinance_amount(double finance_amount) {
		this.finance_amount = finance_amount;
	}

	public double getExchange_amount() {
		return exchange_amount;
	}

	public void setExchange_amount(double exchange_amount) {
		this.exchange_amount = exchange_amount;
	}

	public double getExchange_fee() {
		return exchange_fee;
	}

	public void setExchange_fee(double exchange_fee) {
		this.exchange_fee = exchange_fee;
	}

	public double getExchange_income() {
		return exchange_income;
	}

	public void setExchange_income(double exchange_income) {
		this.exchange_income = exchange_income;
	}

	public double getFurtures_amount() {
		return furtures_amount;
	}

	public void setFurtures_amount(double furtures_amount) {
		this.furtures_amount = furtures_amount;
	}

	public double getFurtures_fee() {
		return furtures_fee;
	}

	public void setFurtures_fee(double furtures_fee) {
		this.furtures_fee = furtures_fee;
	}

	public double getFurtures_income() {
		return furtures_income;
	}

	public void setFurtures_income(double furtures_income) {
		this.furtures_income = furtures_income;
	}

	public double getCoin_income() {
		return coin_income;
	}

	public void setCoin_income(double coin_income) {
		this.coin_income = coin_income;
	}

	public int getReco_num() {
		return reco_num;
	}

	public void setReco_num(int reco_num) {
		this.reco_num = reco_num;
	}

	public double getGift_money() {
		return gift_money;
	}

	public void setGift_money(double gift_money) {
		this.gift_money = gift_money;
	}

	public double getRecharge_withdrawal_fee() {
		return recharge_withdrawal_fee;
	}

	public void setRecharge_withdrawal_fee(double recharge_withdrawal_fee) {
		this.recharge_withdrawal_fee = recharge_withdrawal_fee;
	}

	public double getMiner_amount() {
		return miner_amount;
	}

	public double getMiner_income() {
		return miner_income;
	}

	public void setMiner_amount(double miner_amount) {
		this.miner_amount = miner_amount;
	}

	public void setMiner_income(double miner_income) {
		this.miner_income = miner_income;
	}

	public double getRecharge_usdt() {
		return recharge_usdt;
	}

	public void setRecharge_usdt(double recharge_usdt) {
		this.recharge_usdt = recharge_usdt;
	}

//	public double getRecharge_usdc() {
//		return recharge_usdc;
//	}
//
//	public void setRecharge_usdc(double recharge_usdc) {
//		this.recharge_usdc = recharge_usdc;
//	}

	public double getRecharge_eth() {
		return recharge_eth;
	}

	public void setRecharge_eth(double recharge_eth) {
		this.recharge_eth = recharge_eth;
	}

	public double getRecharge_btc() {
		return recharge_btc;
	}

	public void setRecharge_btc(double recharge_btc) {
		this.recharge_btc = recharge_btc;
	}

	public double getThird_recharge_amount() {
		return third_recharge_amount;
	}

	public void setThird_recharge_amount(double third_recharge_amount) {
		this.third_recharge_amount = third_recharge_amount;
	}

	public double getHolding_money() {
		return holding_money;
	}

	public void setHolding_money(double holding_money) {
		this.holding_money = holding_money;
	}

	public double getWithdraw_eth() {
		return withdraw_eth;
	}

	public double getWithdraw_btc() {
		return withdraw_btc;
	}

	public void setWithdraw_eth(double withdraw_eth) {
		this.withdraw_eth = withdraw_eth;
	}

	public void setWithdraw_btc(double withdraw_btc) {
		this.withdraw_btc = withdraw_btc;
	}

	public double getExchange_lever_amount() {
		return exchange_lever_amount;
	}

	public double getExchange_lever_fee() {
		return exchange_lever_fee;
	}

	public double getExchange_lever_order_income() {
		return exchange_lever_order_income;
	}

	public void setExchange_lever_amount(double exchange_lever_amount) {
		this.exchange_lever_amount = exchange_lever_amount;
	}

	public void setExchange_lever_fee(double exchange_lever_fee) {
		this.exchange_lever_fee = exchange_lever_fee;
	}

	public void setExchange_lever_order_income(double exchange_lever_order_income) {
		this.exchange_lever_order_income = exchange_lever_order_income;
	}

	public double getRecharge_ltc() {
		return recharge_ltc;
	}

	public void setRecharge_ltc(double recharge_ltc) {
		this.recharge_ltc = recharge_ltc;
	}

	public double getWithdraw_all() {
		return withdraw_all;
	}

	public void setWithdraw_all(double withdraw_all) {
		this.withdraw_all = withdraw_all;
	}

	public double getTransfer_in_money() {
		return transfer_in_money;
	}

	public double getTransfer_out_money() {
		return transfer_out_money;
	}

	public void setTransfer_in_money(double transfer_in_money) {
		this.transfer_in_money = transfer_in_money;
	}

	public void setTransfer_out_money(double transfer_out_money) {
		this.transfer_out_money = transfer_out_money;
	}

	public double getRecharge_dapp() {
		return recharge_dapp;
	}

	public void setRecharge_dapp(double recharge_dapp) {
		this.recharge_dapp = recharge_dapp;
	}

	public double getWithdraw_dapp() {
		return withdraw_dapp;
	}

	public void setWithdraw_dapp(double withdraw_dapp) {
		this.withdraw_dapp = withdraw_dapp;
	}

	public double getGalaxy_income() {
		return galaxy_income;
	}

	public void setGalaxy_income(double galaxy_income) {
		this.galaxy_income = galaxy_income;
	}

	public double getGalaxy_amount() {
		return galaxy_amount;
	}

	public void setGalaxy_amount(double galaxy_amount) {
		this.galaxy_amount = galaxy_amount;
	}

	public double getRechargeRecom() {
		return rechargeRecom;
	}

	public void setRechargeRecom(double rechargeRecom) {
		this.rechargeRecom = rechargeRecom;
	}

	public double getRebate1() {
		return rebate1;
	}

	public double getRebate3() {
		return rebate3;
	}

	public void setRebate3(double rebate3) {
		this.rebate3 = rebate3;
	}

	public void setRebate1(double rebate1) {
		this.rebate1 = rebate1;
	}

	public double getRebate2() {
		return rebate2;
	}

	public void setRebate2(double rebate2) {
		this.rebate2 = rebate2;
	}


	public double getTranslate() {
		return translate;
	}

	public void setTranslate(double translate) {
		this.translate = translate;
	}

	public double getSellerCommission() {
		return sellerCommission;
	}

	public void setSellerCommission(double sellerCommission) {
		this.sellerCommission = sellerCommission;
	}

	public double getSellerTotalSales() {
		return sellerTotalSales;
	}

	public void setSellerTotalSales(double sellerTotalSales) {
		this.sellerTotalSales = sellerTotalSales;
	}

	public double getPromoteAmount() {
		return promoteAmount;
	}

	public void setPromoteAmount(double promoteAmount) {
		this.promoteAmount = promoteAmount;
	}

	public double getRechargeCommission() {
		return rechargeCommission;
	}

	public void setRechargeCommission(double rechargeCommission) {
		this.rechargeCommission = rechargeCommission;
	}

	public double getWithdrawCommission() {
		return withdrawCommission;
	}

	public void setWithdrawCommission(double withdrawCommission) {
		this.withdrawCommission = withdrawCommission;
	}

	public double getRecharge_usdc() {
		return recharge_usdc;
	}

	public void setRecharge_usdc(double recharge_usdc) {
		this.recharge_usdc = recharge_usdc;
	}

	public double getWithdraw_usdt() {
		return withdraw_usdt;
	}

	public void setWithdraw_usdt(double withdraw_usdt) {
		this.withdraw_usdt = withdraw_usdt;
	}

	public double getWithdraw_usdc() {
		return withdraw_usdc;
	}

	public void setWithdraw_usdc(double withdraw_usdc) {
		this.withdraw_usdc = withdraw_usdc;
	}
}
