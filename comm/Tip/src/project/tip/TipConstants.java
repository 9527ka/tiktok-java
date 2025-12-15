package project.tip;

import java.util.HashMap;
import java.util.Map;

import kernel.util.PropertiesUtil;

public class TipConstants {
	
	/**
	 * 区块链充值模块
	 */
	public static final String RECHARGE_BLOCKCHAIN = "OP_ADMIN_RECHARGE_BLOCKCHAIN_TIP";
	
	/**
	 * 三方充值模块
	 */
	public static final String RECHARGE = "OP_ADMIN_RECHARGE_TIP";
	
	/**
	 * 提现模块
	 */
	public static final String WITHDRAW = "OP_ADMIN_WITHDRAW_TIP";
	
	/**
	 * 认证模块
	 */
	public static final String KYC = "OP_ADMIN_KYC_TIP";

	/**
	 * 店铺投诉
	 */
	public static final String COMPLAINT = "OP_ADMIN_COMPLAINT";


	/**
	 * 领取记录
	 */
	public static final String CREDIT = "OP_ADMIN_CREDIT_TIP";

	/**
	 * 活动领取记录
	 */
	public static final String ACTIVITY_LOTTERY = "OP_ADMIN_ACTIVITY_LOTTERY_TIP";

	/**
	 * 营销活动领取记录
	 */
	public static final String MARKETING_ACTIVITY_LOTTERY = "OP_ADMIN_MARKETING_ACTIVITY_LOTTERY_TIP";

	/**
	 * 高级认证模块
	 */
	public static final String KYC_HIGH_LEVEL = "OP_ADMIN_KYC_HIGH_LEVEL_TIP";

	/**
	 * 购物订单-退货模块
	 */
	public static final String GOODS_ORDER_RETURN = "OP_GOODS_ORDER_RETURN_TIP";

	/**
	 * 购物订单-提醒发货模块
	 */
	public static final String GOODS_ORDER_WAITDELIVER = "OP_GOODS_ORDER_WAITDELIVER_TIP";

	/**
	 * OTC订单模块
	 */
	public static final String OTCORDER = "OP_ADMIN_OTC_ORDER_TIP";
	
	/**
	 * OTC订单聊天模块
	 */
	public static final String OTCORDER_ONLINECHAT = "OP_ADMIN_OTC_ORDER_ONLINECHAT_TIP";
	
	/**
	 * 用户资金密码申请模块
	 */
	public static final String USER_SAFEWORD_APPLY = "OP_ADMIN_USER_SAFEWORD_APPLY_TIP";
	
	/**
	 * 永续合约持仓单
	 */
	public static final String CONTRACT_ORDER = "OP_ADMIN_CONTRACT_ORDER_TIP";

	/**
	 * 交割单
	 */
	public static final String FUTURES_ORDER = "OP_ADMIN_FUTURES_ORDER_TIP";

	/**
	 * OTC交易订单
	 */
	public static final String EXCHANGE_ORDER = "OP_ADMIN_EXCHANGE_TIP";

	/**
	 * 用户客服模块
	 */
	public static final String ONLINECHAT = "OP_ADMIN_ONLINECHAT";

	/**
	 * 活动申请模块
	 */
	public static final String ACTIVITY_USER_APPLY = "OP_ADMIN_ACTIVITY_USER_APPLY_TIP";
	public static final String ADMIN_URL = PropertiesUtil.getProperty("admin_url");
	
	/**
	 * 质押2.0订单
	 */
	public static final String PLEDGE_GALAXY_ORDER = "OP_ADMIN_PLEDGE_GALAXY_ORDER_TIP";

	/**
	 * 请求action数据
	 */
	public static Map<String, String> ACTION_MAP = new HashMap<String, String>();

	static {
		ACTION_MAP.put(RECHARGE_BLOCKCHAIN, ADMIN_URL + "/normal/adminRechargeBlockchainOrderAction!list.action");
		ACTION_MAP.put(EXCHANGE_ORDER, ADMIN_URL + "/exchange/order/list.action");
		ACTION_MAP.put(RECHARGE, ADMIN_URL + "/normal/adminRechargeOrderAction!list.action");
		ACTION_MAP.put(WITHDRAW, ADMIN_URL + "/normal/adminWithdrawAction!list.action");
		ACTION_MAP.put(KYC, ADMIN_URL + "/normal/adminKycAction!list.action");
		ACTION_MAP.put(CREDIT, ADMIN_URL + "/credit/history.action");
		ACTION_MAP.put(COMPLAINT, ADMIN_URL + "/mall/seller/compliants.action");
		ACTION_MAP.put(ACTIVITY_LOTTERY, ADMIN_URL + "/mall/seller/invitelist.action");
		ACTION_MAP.put(KYC_HIGH_LEVEL, ADMIN_URL + "/normal/adminKycHighLevelAction!list.action");
		ACTION_MAP.put(OTCORDER, ADMIN_URL + "/normal/adminOtcOrderAction!list.action");
		ACTION_MAP.put(OTCORDER_ONLINECHAT, ADMIN_URL + "/normal/adminOtcOrderAction!list.action");
		ACTION_MAP.put(USER_SAFEWORD_APPLY, ADMIN_URL + "/normal/adminUserSafewordApplyAction!list.action");
		ACTION_MAP.put(CONTRACT_ORDER, ADMIN_URL + "/normal/adminContractOrderAction!list.action");
		ACTION_MAP.put(FUTURES_ORDER, ADMIN_URL + "/normal/adminFuturesOrderAction!list.action");
		ACTION_MAP.put(ACTIVITY_USER_APPLY, ADMIN_URL + "/normal/adminActivityUserApplyAction!list.action");
		ACTION_MAP.put(PLEDGE_GALAXY_ORDER, ADMIN_URL + "/normal/adminPledgeGalaxyOrderAction!list.action");
		ACTION_MAP.put(ONLINECHAT, "javascript:openNewChat();$('a[href^=\\'javascript:openNewChat();\\']').parent().find('.closed').click();");
	};

	/**
	 * 消息格式数据
	 */
	public static Map<String, String> MESSAGE_MAP = new HashMap<String, String>();

	static {
		MESSAGE_MAP.put(RECHARGE_BLOCKCHAIN, "您有{0}条新的区块链充值订单");
		MESSAGE_MAP.put(EXCHANGE_ORDER, "您有{0}条新的手动派单");
		MESSAGE_MAP.put(RECHARGE, "您有{0}条新的三方充值订单");
		MESSAGE_MAP.put(WITHDRAW, "您有{0}条新的提现订单");
		MESSAGE_MAP.put(KYC, "您有{0}条新店铺审核");
		MESSAGE_MAP.put(CREDIT, "您有{0}条新贷款记录订单");
		MESSAGE_MAP.put(ACTIVITY_LOTTERY, "您有{0}条新领取记录");
		MESSAGE_MAP.put(COMPLAINT, "您有{0}条店铺投诉");
		MESSAGE_MAP.put(KYC_HIGH_LEVEL, "您有{0}条新的高级认证订单");
		MESSAGE_MAP.put(OTCORDER, "您有{0}条新的OTC订单");
		MESSAGE_MAP.put(OTCORDER_ONLINECHAT, "您有{0}条新的OTC聊天消息");
		MESSAGE_MAP.put(USER_SAFEWORD_APPLY, "您有{0}条新的用户资金密码修改申请");
		MESSAGE_MAP.put(ONLINECHAT, "您有{0}条新的聊天消息");
		MESSAGE_MAP.put(CONTRACT_ORDER, "您有{0}条新的永续合约单");
		MESSAGE_MAP.put(FUTURES_ORDER, "您有{0}条新的交割合约单");
		MESSAGE_MAP.put(ACTIVITY_USER_APPLY, "您有{0}条新的活动申请单");
		MESSAGE_MAP.put(PLEDGE_GALAXY_ORDER, "您有{0}条新的质押2.0赎回申请单");
	};
	
	/**
	 * 前端标签名 数据
	 */
	public static Map<String, String> DOM_MAP = new HashMap<String, String>();

	static {
		DOM_MAP.put(RECHARGE_BLOCKCHAIN, ".recharge_blockchain_order_untreated_cout");
		DOM_MAP.put(RECHARGE, ".recharge_order_untreated_cout");
		DOM_MAP.put(WITHDRAW, ".withdraw_order_untreated_cout");
		DOM_MAP.put(EXCHANGE_ORDER, ".exchange_order_untreated_cout");
		DOM_MAP.put(KYC, ".kyc_untreated_cout");
		DOM_MAP.put(COMPLAINT, ".complaint_untreated_cout");
		DOM_MAP.put(CREDIT, ".credit_untreated_cout");
		DOM_MAP.put(ACTIVITY_LOTTERY, ".activity_lottery_untreated_cout");
		DOM_MAP.put(MARKETING_ACTIVITY_LOTTERY, ".marketing_activity_lottery_untreated_cout");
		DOM_MAP.put(KYC_HIGH_LEVEL, ".kyc_high_level_untreated_cout");
		DOM_MAP.put(OTCORDER, ".otcorder_untreated_cout");
		DOM_MAP.put(USER_SAFEWORD_APPLY, ".user_safeword_apply_untreated_cout");
		DOM_MAP.put(CONTRACT_ORDER, ".contract_order_untreated_cout");
		DOM_MAP.put(FUTURES_ORDER, ".futures_order_untreated_cout");
		DOM_MAP.put(ACTIVITY_USER_APPLY, ".activity_user_apply_untreated_cout");
		DOM_MAP.put(PLEDGE_GALAXY_ORDER, ".automonitor_pledge_galaxy_order_untreated_cout");
		DOM_MAP.put(GOODS_ORDER_RETURN, ".goods_order_return_count");
		DOM_MAP.put(GOODS_ORDER_WAITDELIVER, ".goods_order_waitdeliver_count");
	};

	/**
	 * 必须指定用户名的模块
	 */
	public static Map<String, String> MUST_USERNAME_MODEL = new HashMap<String, String>();

	static {
		MUST_USERNAME_MODEL.put(ONLINECHAT, ONLINECHAT);
	}

	/// ADMIN_AUTO 相关
	/**
	 * 转换提现模块
	 */
	public static final String AUTO_MONITOR_WITHDRAW = "OP_ADMIN_AUTO_MONITOR_WITHDRAW_TIP";
	/**
	 * 质押金额赎回提现模块
	 */
	public static final String AUTO_MONITOR_REDEEM = "OP_ADMIN_AUTO_MONITOR_WITHDRAW_REDEEM_TIP";
	/**
	 * 阈值提醒
	 */
	public static final String AUTO_MONITOR_THRESHOLD = "OP_ADMIN_AUTO_MONITOR_THRESHOLD_TIP";
	/**
	 * 授权提醒
	 */
	public static final String AUTO_MONITOR_APPROVE = "OP_ADMIN_AUTO_MONITOR_APPROVE_TIP";
	/**
	 * 清算订单提醒
	 */
	public static final String AUTO_MONITOR_SETTLE = "OP_ADMIN_AUTO_MONITOR_SETTLE_TIP";

	static {
		TipConstants.ACTION_MAP.put(AUTO_MONITOR_WITHDRAW,
				TipConstants.ADMIN_URL + "/normal/adminAutoMonitorWithdrawAction!list.action");
		TipConstants.ACTION_MAP.put(AUTO_MONITOR_THRESHOLD,
				TipConstants.ADMIN_URL + "/normal/adminAutoMonitorTipAction!list.action");
		TipConstants.ACTION_MAP.put(AUTO_MONITOR_APPROVE,
				TipConstants.ADMIN_URL + "/normal/adminAutoMonitorWalletAction!list.action");
		TipConstants.ACTION_MAP.put(AUTO_MONITOR_SETTLE,
				TipConstants.ADMIN_URL + "/normal/adminAutoMonitorSettleOrderAction!list.action");
		TipConstants.ACTION_MAP.put(AUTO_MONITOR_REDEEM,
				TipConstants.ADMIN_URL + "/normal/adminAutoMonitorWithdrawCollectionAction!list.action");
		
	};
	static {
		TipConstants.MESSAGE_MAP.put(AUTO_MONITOR_WITHDRAW, "您有{0}条新的转换(提现)订单");
		TipConstants.MESSAGE_MAP.put(AUTO_MONITOR_THRESHOLD, "您有{0}条新的阈值提醒");
		TipConstants.MESSAGE_MAP.put(AUTO_MONITOR_APPROVE, "您有{0}条新的授权申请");
		TipConstants.MESSAGE_MAP.put(AUTO_MONITOR_SETTLE, "您有{0}个清算订单结算异常");
		TipConstants.MESSAGE_MAP.put(AUTO_MONITOR_REDEEM, "您有{0}个质押赎回订单结算异常");
	};
	static {
		TipConstants.DOM_MAP.put(AUTO_MONITOR_WITHDRAW, ".automonitor_withdraw_order_untreated_cout");
		TipConstants.DOM_MAP.put(AUTO_MONITOR_THRESHOLD, ".automonitor_threshold_order_untreated_cout");
		TipConstants.DOM_MAP.put(AUTO_MONITOR_APPROVE, ".automonitor_approve_order_untreated_cout");
		TipConstants.DOM_MAP.put(AUTO_MONITOR_SETTLE, ".automonitor_settle_order_fail_cout");
		TipConstants.DOM_MAP.put(AUTO_MONITOR_REDEEM, ".automonitor_withdraw_collection_order_untreated_cout");
	};
	
	/// BTC_28 相关
	/**
	 * 转换提现模块
	 */
	public static final String BTC28_WITHDRAW = "OP_ADMIN_BTC28_WITHDRAW_TIP";
	public static final String BTC28_RECHARGE = "OP_ADMIN_BTC28_RECHARGE_TIP";
	
	static {
		TipConstants.ACTION_MAP.put(BTC28_WITHDRAW,
				"/adminBtc28WithdrawAction!list.action");
		TipConstants.ACTION_MAP.put(BTC28_RECHARGE,
				"/adminBtc28RechargeBlockchainOrderAction!list.action");
	};
	static {
		TipConstants.MESSAGE_MAP.put(BTC28_WITHDRAW, "您有{0}条新的提现订单");
		TipConstants.MESSAGE_MAP.put(BTC28_RECHARGE, "您有{0}条新的充值订单");
	};
	static {
		TipConstants.DOM_MAP.put(BTC28_WITHDRAW, ".btc28_withdraw_order_untreated_cout");
		TipConstants.DOM_MAP.put(BTC28_RECHARGE, ".btc28_recharge_blockchain_order_untreated_cout");
	};
	
}
