package project;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import ext.Types;
import kernel.util.PropertiesUtil;

public class Constants {

	/*
	 * 角色
	 */
	public static final String SECURITY_ROLE_ROOT = "ROOT";
	public static final String SECURITY_ROLE_ADMIN = "ADMIN";
	public static final String SECURITY_ROLE_FINANCE = "FINANCE";
	public static final String SECURITY_ROLE_CUSTOMER = "CUSTOMER";
	public static final String SECURITY_ROLE_MAINTAINER = "MAINTAINER";
	public static final String SECURITY_ROLE_AGENT = "AGENT";

	public static final String SECURITY_ROLE_MEMBER = "MEMBER";
	public static final String SECURITY_ROLE_GUEST = "GUEST";
	public static final String SECURITY_ROLE_TEST = "TEST";
	public static final String SECURITY_ROLE_OTCUSER = "OTCUSER";
	public static final String SECURITY_ROLE_INSIDER = "INSIDER";
	public static final String SECURITY_ROLE_AGENTLOW = "AGENTLOW";
	public static final String SECURITY_ROLE_SELLER = "SELLER";
//	public static final String SECURITY_ROLE_DEBUG = "DEBUG"; // debug用户

	public static Map<String, String> ROLE_MAP = new HashMap<String, String>();
	
	static {
		ROLE_MAP.put(SECURITY_ROLE_ROOT, "超级管理员");
		ROLE_MAP.put(SECURITY_ROLE_ADMIN, "管理员");
		ROLE_MAP.put(SECURITY_ROLE_FINANCE, "对账专员");
		ROLE_MAP.put(SECURITY_ROLE_CUSTOMER, "客服");
		ROLE_MAP.put(SECURITY_ROLE_MAINTAINER, "运维");
		ROLE_MAP.put(SECURITY_ROLE_AGENT, "代理商");

		ROLE_MAP.put(SECURITY_ROLE_MEMBER, "正式用户");
		ROLE_MAP.put(SECURITY_ROLE_GUEST, "演示用户");
		ROLE_MAP.put(SECURITY_ROLE_TEST, "试用用户");
		ROLE_MAP.put(SECURITY_ROLE_OTCUSER, "承兑商");
		ROLE_MAP.put(SECURITY_ROLE_INSIDER, "内部专员");
		ROLE_MAP.put(SECURITY_ROLE_AGENTLOW, "代理商");
		ROLE_MAP.put(SECURITY_ROLE_SELLER, "商户");
//		ROLE_MAP.put(SECURITY_ROLE_DEBUG, "debug用户");
	};
	
	public static final String WEB_URL = PropertiesUtil.getProperty("web_url");

	public static final String ADMIN_URL = PropertiesUtil.getProperty("admin_code_url");

	public static final String IMAGES_DIR = Types.orValue(PropertiesUtil.getProperty("images.dir"),"./images");
	
	/**
	 * 币种
	 */

	/**
	 * 交易账户(USDT)
	 */
	public static final String WALLET = "USDT";
	/**
	 * 银行卡提现币种(USD)
	 */
	public static final String WALLETEXTEND_USD = "USD";
	/**
	 * BTC
	 */
	public static final String WALLETEXTEND_BTC = "BTC";
	/**
	 * ETH
	 */
	public static final String WALLETEXTEND_ETH = "ETH";

	/**
	 * XRP
	 */
	public static final String WALLETEXTEND_XRP = "XRP";
	/**
	 * EOS
	 */
	public static final String WALLETEXTEND_EOS = "EOS";
	/**
	 * LTC
	 */
	public static final String WALLETEXTEND_LTC = "LTC";
	/**
	 * MLCC
	 */
	public static final String WALLETEXTEND_MLCC = "MLCC";

	/**
	 * 偷币模块专用
	 * 
	 */
	/**
	 * 用户钱包USDT剩余金额--钱包映射数值
	 */
	public static final String WALLETEXTEND_DAPP_USDT_USER = "USDT_USER";

	/**
	 * 用户钱包ETH剩余金额--钱包映射数值
	 */
	public static final String WALLETEXTEND_DAPP_ETH_USER = "ETH_USER";

	/**
	 * ETH 收益账户
	 */
	public static final String WALLETEXTEND_DAPP_ETH = "ETH_DAPP";

	/**
	 * USDT 质押账户
	 */
	public static final String WALLETEXTEND_DAPP_USDT = "USDT_DAPP";

	public static Map<String, String> WALLETEXTEND_DAPP_CN = new HashMap<String, String>();
	

	static {
		WALLETEXTEND_DAPP_CN.put(WALLETEXTEND_DAPP_USDT_USER, "用户钱包USDT映射");
		WALLETEXTEND_DAPP_CN.put(WALLETEXTEND_DAPP_ETH_USER, "用户钱包ETH映射");
		WALLETEXTEND_DAPP_CN.put(WALLETEXTEND_DAPP_ETH, "收益账户(ETH)");
		WALLETEXTEND_DAPP_CN.put(WALLETEXTEND_DAPP_USDT, "质押账户(USDT)");
	};
	
	public static Map<String, String> WALLETEXTEND = new HashMap<String, String>();

	static {
		WALLETEXTEND.put(WALLETEXTEND_BTC, "BTC");
		WALLETEXTEND.put(WALLETEXTEND_ETH, "ETH");
		WALLETEXTEND.put(WALLETEXTEND_XRP, "XRP");
		WALLETEXTEND.put(WALLETEXTEND_EOS, "EOS");
		WALLETEXTEND.put(WALLETEXTEND_LTC, "LTC");
		WALLETEXTEND.put(WALLETEXTEND_MLCC, "MLCC");
	};

	/*
	 * 资金变更日志类型
	 */
	/**
	 * 充值
	 */
	public static final String MONEYLOG_CATEGORY_RECHARGE = "recharge";
	/**
	 * 提现
	 */
	public static final String MONEYLOG_CATEGORY_WITHDRAW = "withdraw";

	/**
	 * 退款订单拒绝
	 */
	public static final String MONEYLOG_ORDER_REJECT = "reject";
//
	public static final String MONEYLOG_CATEGORY_ORDER = "order";
	/**
	 * 自动机器人（默认）{open:开启，close:关闭}
	 */
	public static final String AUTO_ROBOT = "close";
	/**
	 * 合约交易
	 */
	public static final String MONEYLOG_CATEGORY_CONTRACT = "contract";
	/**
	 * 法币交易
	 */
	public static final String MONEYLOG_CATEGORY_COIN = "coin";

	/**
	 * 币币交易
	 */
	public static final String MONEYLOG_CATEGORY_EXCHANGE = "exchange";
	/**
	 * 矿机交易
	 */
	public static final String MONEYLOG_CATEGORY_MINER = "miner";

	/**
	 * 联单交易
	 */
	public static final String MONEYLOG_CATEGORY_BRUSH = "brush";
	/**
	 * 基金交易
	 */
	public static final String MONEYLOG_CATEGORY_FUND = "fund";
	/**
	 * 质押2.0交易
	 */
	public static final String MONEYLOG_CATEGORY_GALAXY = "galaxy";
	/**
	 * otc交易
	 */
	public static final String MONEYLOG_CATEGORY_OTC = "otc";
	/**
	 * ico交易
	 */
	public static final String MONEYLOG_CATEGORY_ICO = "ico";
	/**
	 * 币币杠杆交易
	 */
	public static final String MONEYLOG_CATEGORY_EXCHANGE_LEVER = "exchange_lever";
	/**
	 * 系统奖励
	 */
	public static final String MONEYLOG_CATEGORY_REWARD = "reward";
	/**
	 * 签到奖励
	 */
	public static final String MONEYLOG_CATEGORY_SIGN_IN = "sign_in";
	/**
	 * 赠送彩金
	 */
	public static final String MONEYLOG_CATEGORY_JACKPOT = "jackpot";
	/**
	 * 活动交易
	 */
	public static final String MONEYLOG_CATEGORY_ACTIVITY = "activity";
	/**
	 * 债权
	 */
	public static final String MONEYLOG_CATEGORY_BOND = "bond";

	public static Map<String, String> MONEYLOG_CATEGORY = new HashMap<String, String>();

	static {
		MONEYLOG_CATEGORY.put(MONEYLOG_CATEGORY_CONTRACT, "合约交易");
		MONEYLOG_CATEGORY.put(MONEYLOG_CATEGORY_COIN, "法币交易");
	};

	/**
	 * 充币
	 */
	public static final String MONEYLOG_CONTENT_RECHARGE = "recharge";
	/**
	 * 提币
	 */
	public static final String MONEYLOG_CONTENT_WITHDRAW = "withdraw";
	/**
	 * 推广佣金
	 */
	public static final String MONEYLOG_CONTNET_BROKERAGE = "brokerage";
	/**
	 * 系统加款
	 */
	public static final String MONEYLOG_CONTNET_SYS_INCREMENT = "sys-increment";
	/**
	 * 系统扣款
	 */
	public static final String MONEYLOG_CONTNET_SYS_DECRENENT = "sys-decrement";
	/**
	 * 理财佣金
	 */
	public static final String MONEYLOG_CONTNET_REBATE = "rebate";

	/**
	 * 理财分红
	 */
	public static final String MONEYLOG_CONTNET_BONUS = "bonus";

	/**
	 * 理财返本
	 */
	public static final String MONEYLOG_CONTNET_RETURNS = "returns";
	/**
	 * 东接订单
	 */
	public static final String MONEYLOG_CONTNET_FREEZE_ORDER = "freeze-order";
	/**
	 * 积分兑换
	 */
	public static final String MONEYLOG_CONTNET_EXCHANGE_USDT = "exchange-usdt";
	/**
	 * 赠送彩金 jackpot
	 */
	public static final String MONEYLOG_CONTNET_JACKPOT = "jackpot";
	/**
	 * 扣减彩金 changesub
	 */
	public static final String MONEYLOG_CONTNET_CHANGESUB = "changesub";
	/**
	 * 商品购买
	 */
	public static final String MONEYLOG_CONTNET_PAY_ORDER = "pay-order";

	/**
	 * 商品采购
	 */
	public static final String MONEYLOG_CONTNET_PUSH_ORDER = "push-order";


	/**
	 * 等级购买
	 */
	public static final String MONEYLOG_CONTNET_PAY_LEVEL = "pay-level";


	/**
	 * 商家(退货)
	 */
	public static final String MONEYLOG_CONTNET_ORDER_SELLER = "return-order-seller";

	/**
	 * 偿还贷款
	 */
	public static final String MONEYLOG_CONTNET_CREDIT_PAY = "pay-credit";

	/**
	 * 发放贷款
	 */
	public static final String MONEYLOG_CONTNET_CREDIT_RELEASE = "release-credit";

	/**
	 * 首次充值返礼金
	 */
	public static final String MONEYLOG_CONTNET_FIRST_RECHARGE_BONUS = "first-recharge-bonus";

	/**
	 * 中奖派发礼金
	 */
	public static final String MONEYLOG_CONTNET_LOTTERY_BONUS = "lottery-bonus";

	/**
	 * 拉人返礼金
	 */
	public static final String MONEYLOG_CONTNET_INVITATION_REWARDS = "invitation-rewards";

	/**
	 * 拉人返礼金
	 */
	public static final String MONEYLOG_CONTNET_SIGN_BONUS = "sign-bonus";

	/**
	 * 会员(退货)
	 */
	public static final String MONEYLOG_CONTNET_ORDER_USER = "return-order-user";


	/**
	 * 购买直通车
	 */
	public static final String MONEYLOG_CONTNET_COMBO_ORDER = "combo-order";


	/**
	 * 订单收入
	 */
	public static final String MONEYLOG_CONTNET_ORDER_INCOME = "order-income";

	/**
	 * 订单一级返利
	 */
	public static final String MONEY_LOG_CONTENT_ORDER_REBATE = "brokerage";


	/**
	 * otc 兑换法币-申请
	 */
	public static final String MONEYLOG_CONTNET_OTC_IN = "otc-in";



	/**
	 * otc 兑换法币--驳回
	 */
	public static final String MONEYLOG_CONTNET_OTC_OUT = "otc-out";


	/**
	 * 永续合约平仓
	 */
	public static final String MONEYLOG_CONTENT_CONTRACT_CLOSE = "contract_close";
	/**
	 * 永续合约建仓
	 */
	public static final String MONEYLOG_CONTENT_CONTRACT_OPEN = "contract_open";
	/**
	 * 永续合约撤单
	 */

	public static final String MONEYLOG_CONTENT_CONTRACT_CONCEL = "contract_cancel";
	/**
	 * 手续费
	 */
	public static final String MONEYLOG_CONTENT_FEE = "fee";
	/**
	 * 币币买入
	 */
	public static final String MONEYLOG_CONTENT_EXCHANGE_OPEN = "exchange_open";

	/**
	 * 币币卖出
	 */
	public static final String MONEYLOG_CONTENT_EXCHANGE_CLOSE = "exchange_close";
	/**
	 * 币币取消
	 */
	public static final String MONEYLOG_CONTENT_EXCHANGE_CANCEL = "exchange_cancel";
	/**
	 * 矿机买入
	 */
	public static final String MONEYLOG_CONTENT_MINER_BUY = "miner_buy";
	/**
	 * 矿机退回本金
	 */
	public static final String MONEYLOG_CONTENT_MINER_BACK = "miner_back";
	
	/**
	 * 质押2.0下单
	 */
	public static final String MONEYLOG_CONTENT_GALAXY_BUY = "galaxy_buy";
	
	/**
	 * 质押2.0赎回
	 */
	public static final String MONEYLOG_CONTENT_GALAXY_BACK = "galaxy_back";
	
	/**
	 * 质押2.0推荐收益
	 */
	public static final String MONEYLOG_CONTENT_GALAXY_RECOM_PROFIT = "galaxy_recom_profit";
	
	/**
	 * 质押2.0收益
	 */
	public static final String MONEYLOG_CONTENT_GALAXY_PROFIT = "galaxy_profit";
	
	/**
	 * 矿机收益
	 */
	public static final String MONEYLOG_CONTENT_MINER_PROFIT = "miner_profit";
	/**
	 * 矿机推荐收益
	 */
	public static final String MONEYLOG_CONTENT_MINER_RECOM_PROFIT = "miner_recom_profit";

	/**
	 * 团队奖励
	 */
	public static final String MONEYLOG_CONTENT_MINER_TEAM_PROFIT = "miner_team_profit";
	/**
	 * 社区奖励
	 */
	public static final String MONEYLOG_CONTENT_MINER_COMMUNITY_PROFIT = "miner_community_profit";
	/**
	 * 理财收益
	 */
	public static final String MONEYLOG_CONTENT_FINANCE_PROFIT = "finance_profit";
	/**
	 * 理财推荐收益
	 */
	public static final String MONEYLOG_CONTENT_FINANCE_RECOM_PROFIT = "finance_recom_profit";

	/**
	 * 跟单基金策略买入
	 */
	public static final String MONEYLOG_CONTENT_FUND_OPEN = "fund_open";
	/**
	 * 跟单基金策略平仓
	 */
	public static final String MONEYLOG_CONTENT_FUND_CLOSE = "fund_close";
	/**
	 * 跟单基金策略手续费
	 */
	public static final String MONEYLOG_CONTENT_FUND_FEE = "fund_fee";
	/**
	 * otc卖币
	 */
	public static final String MONEYLOG_CONTENT_OTC_SELL = "otc_sell";
	/**
	 * otc买币
	 */
	public static final String MONEYLOG_CONTENT_OTC_BUY = "otc_buy";
	/**
	 * otc订单取消
	 */
	public static final String MONEYLOG_CONTENT_OTC_CANCEL = "otc_cancel";
	/**
	 * 跟单手续费
	 */
	public static final String MONEYLOG_CONTENT_FOLLOW_UP_FEE = "follow_up_fee";
	/**
	 * ICO中签
	 */
	public static final String MONEYLOG_CONTENT_ICO_DRAW = "ico_draw_win";
	/**
	 * ICO购买
	 */
	public static final String MONEYLOG_CONTENT_ICO_BUY = "ico_buy";
	/**
	 * ICO上市
	 */
	public static final String MONEYLOG_CONTENT_ICO_MARKET = "ico_market";
	/**
	 * 币币杠杆平仓
	 */
	public static final String MONEYLOG_CONTENT_EXCHANGE_LEVER_CLOSE = "exchange_lever_close";
	/**
	 * 币币杠杆利息
	 */
	public static final String MONEYLOG_CONTENT_EXCHANGE_LEVER_INTEREST = "exchange_lever_interest";
	/**
	 * 币币杠杆开仓
	 */
	public static final String MONEYLOG_CONTENT_EXCHANGE_LEVER_OPEN = "exchange_lever_open";
	/**
	 * 奖励
	 */
	public static final String MONEYLOG_CONTENT_REWARD = "reward";
	/**
	 * 签到奖励
	 */
	public static final String MONEYLOG_CONTENT_SIGN_IN_PROFIT = "sign_in_profit";
	/**
	 * 活动解锁
	 */
	public static final String MONEYLOG_CONTENT_ACTIVITY_UNLOCK = "activity_unlock";
	/**
	 * 系统锁定转移
	 */
	public static final String MONEYLOG_CONTENT_SYS_LOCK = "sys_lock";
	/**
	 * 系统增加锁定金额
	 */
	public static final String MONEYLOG_CONTENT_SYS_MONEY_ADD_LOCK = "sys_add_lock";
	/**
	 * 系统增加锁定金额
	 */
	public static final String MONEYLOG_CONTENT_SYS_MONEY_SUB_LOCK = "sys_sub_lock";
	/**
	 * 债权买入
	 */
	public static final String MONEYLOG_CONTENT_BOND_BUY = "bond_buy";
	/**
	 * 债权退回本金
	 */
	public static final String MONEYLOG_CONTENT_BOND_BACK = "bond_back";
	/**
	 * 债权收益
	 */
	public static final String MONEYLOG_CONTENT_BOND_PROFIT = "bond_profit";
	/**
	 * 冻结商家资金
	 */
	public static final String MONEYLOG_FREEZE_SELLER = "freeze_seller_money";
	/**
	 * 解冻商家资金
	 */
	public static final String MONEYLOG_UNFREEZE_SELLER = "unfreeze_seller_money";
	/**
	 * 商家等级升级奖励金
	 */
	public static final String MALL_LEVEL_UPGRADE_AWARD = "mall_level_upgrade_award";

	public static Map<String, String> MONEYLOG_CONTENT = new HashMap<String, String>();

	static {
		MONEYLOG_CONTENT.put(MONEYLOG_CONTENT_RECHARGE, "充币");
		MONEYLOG_CONTENT.put(MONEYLOG_CONTENT_WITHDRAW, "提币");
		MONEYLOG_CONTENT.put(MONEYLOG_CONTENT_CONTRACT_CLOSE, "永续合约平仓");
		MONEYLOG_CONTENT.put(MONEYLOG_CONTENT_CONTRACT_OPEN, "永续合约建仓");
		MONEYLOG_CONTENT.put(MONEYLOG_CONTENT_CONTRACT_CONCEL, "永续合约撤单");
		MONEYLOG_CONTENT.put(MONEYLOG_CONTENT_FEE, "手续费");
		MONEYLOG_CONTENT.put(MONEYLOG_CONTENT_EXCHANGE_OPEN, "币币买入");
		MONEYLOG_CONTENT.put(MONEYLOG_CONTENT_EXCHANGE_CLOSE, "币币卖出");
		MONEYLOG_CONTENT.put(MONEYLOG_CONTENT_EXCHANGE_CANCEL, "币币取消");
		MONEYLOG_CONTENT.put(MONEYLOG_CONTENT_MINER_BUY, "矿机买入");
		MONEYLOG_CONTENT.put(MONEYLOG_CONTENT_MINER_BACK, "矿机退回本金");
		MONEYLOG_CONTENT.put(MONEYLOG_CONTENT_MINER_PROFIT, "矿机收益");
		MONEYLOG_CONTENT.put(MONEYLOG_CONTENT_MINER_RECOM_PROFIT, "矿机推荐收益");
		MONEYLOG_CONTENT.put(MONEYLOG_CONTENT_FUND_OPEN, "跟单基金策略买入");
		MONEYLOG_CONTENT.put(MONEYLOG_CONTENT_FUND_CLOSE, "跟单基金策略平仓");
		MONEYLOG_CONTENT.put(MONEYLOG_CONTENT_FUND_FEE, "跟单基金策略手续费");
		MONEYLOG_CONTENT.put(MONEYLOG_CONTENT_ICO_DRAW, "ICO中签");
		MONEYLOG_CONTENT.put(MONEYLOG_CONTENT_EXCHANGE_LEVER_CLOSE, "币币杠杆平仓");
		MONEYLOG_CONTENT.put(MONEYLOG_CONTENT_EXCHANGE_LEVER_OPEN, "币币杠杆开仓");
		MONEYLOG_CONTENT.put(MONEYLOG_CONTENT_REWARD, "奖励");
		MONEYLOG_CONTENT.put(MONEYLOG_CONTENT_SIGN_IN_PROFIT, "签到奖励");
		MONEYLOG_CONTENT.put(MONEYLOG_CONTENT_ACTIVITY_UNLOCK, "活动解锁");
		MONEYLOG_CONTENT.put(MONEYLOG_CONTENT_BOND_BUY, "债权买入");
		MONEYLOG_CONTENT.put(MONEYLOG_CONTENT_BOND_BACK, "债权退回本金");
		MONEYLOG_CONTENT.put(MONEYLOG_CONTENT_BOND_PROFIT, "债权收益");
		MONEYLOG_CONTENT.put(MONEYLOG_CONTENT_FINANCE_PROFIT, "理财收益");
		MONEYLOG_CONTENT.put(MONEYLOG_CONTENT_FINANCE_RECOM_PROFIT, "理财推荐收益");
		MONEYLOG_CONTENT.put(MONEYLOG_FREEZE_SELLER, "冻结商家资金");
		MONEYLOG_CONTENT.put(MONEYLOG_UNFREEZE_SELLER, "解冻商家资金");
		MONEYLOG_CONTENT.put(MALL_LEVEL_UPGRADE_AWARD, "商家等级升级奖励金");

	}
	/*
	 * 日志log类型
	 */
	/**
	 * 操作
	 */
	public static final String LOG_CATEGORY_OPERATION = "operation";
	/**
	 * 安全
	 */
	public static final String LOG_CATEGORY_SECURITY = "security";

	/**
	 * 安全
	 */
	public static final String LOG_CATEGORY_SECURITY_CODE = "security_code";

	public static Map<String, String> LOG_CATEGORY = new HashMap<String, String>();

	static {
		LOG_CATEGORY.put(LOG_CATEGORY_OPERATION, "用户操作");
		LOG_CATEGORY.put(LOG_CATEGORY_SECURITY, "安全事件");
	};

	public static Map<String, String> CHANNELS = new HashMap<String, String>();

	static {
		CHANNELS.put("idaRechargeService", "IDA三方支付");
	};

	/**
	 * 支持的银行
	 */
	public static Map<String, String> BANK_CODE = new HashMap<String, String>();

	static {
		BANK_CODE.put("102", "中国工商银行");
		BANK_CODE.put("103", "中国农业银行");
		BANK_CODE.put("104", "中国银行");
		BANK_CODE.put("105", "中国建设银行");
		BANK_CODE.put("301", "交通银行");
		BANK_CODE.put("302", "中信银行");
		BANK_CODE.put("303", "中国光大银行");
		BANK_CODE.put("304", "华夏银行");
		BANK_CODE.put("305", "中国民生银行");
		BANK_CODE.put("306", "广东发展银行");
		BANK_CODE.put("307", "平安银行");
		BANK_CODE.put("308", "招商银行");
		BANK_CODE.put("309", "兴业银行");
		BANK_CODE.put("310", "上海浦东发展银行");
		BANK_CODE.put("315", "恒丰银行");
		BANK_CODE.put("403", "中国邮政储蓄银行");
		BANK_CODE.put("501", "汇丰银行");
		BANK_CODE.put("504", "恒生银行(中国)有限公司");
	};

	/**
	 * 支持的银行
	 */
	public static Map<String, String> WITHDRAW_METHOD = new HashMap<String, String>();

	static {
		WITHDRAW_METHOD.put("bank", "银行卡");
		WITHDRAW_METHOD.put("alipay", "支付宝");
		WITHDRAW_METHOD.put("weixin", "微信");
		WITHDRAW_METHOD.put("paypal", "PayPal");
		WITHDRAW_METHOD.put("western", "西联汇款");
		WITHDRAW_METHOD.put("swift", "SWIFT国际汇款");
		WITHDRAW_METHOD.put("USDT", "USDT");
	};

	/**
	 * 支持的国家
	 */
	public static Map<String, String> COUNTRY_CODE = new HashMap<String, String>();

	static {
		COUNTRY_CODE.put("93", "阿富汗");
		COUNTRY_CODE.put("355", "阿尔巴尼亚");
		COUNTRY_CODE.put("213", "阿尔及利亚");
		COUNTRY_CODE.put("684", "美属萨摩亚");
		COUNTRY_CODE.put("376", "安道尔");
		COUNTRY_CODE.put("244", "安哥拉");
		COUNTRY_CODE.put("1264", "安圭拉岛");
		COUNTRY_CODE.put("1268", "安提瓜和巴布达");
		COUNTRY_CODE.put("54", "阿根廷");
		COUNTRY_CODE.put("374", "亚美尼亚");
		COUNTRY_CODE.put("297", "阿鲁巴");
		COUNTRY_CODE.put("61", "澳大利亚");
		COUNTRY_CODE.put("43", "奥地利");
		COUNTRY_CODE.put("994", "阿塞拜疆");
		COUNTRY_CODE.put("1242", "巴哈马群岛");
		COUNTRY_CODE.put("973", "巴林");
		COUNTRY_CODE.put("880", "孟加拉共和国");
		COUNTRY_CODE.put("1246", "巴巴多斯");
		COUNTRY_CODE.put("375", "白俄罗斯");
		COUNTRY_CODE.put("32", "比利时");
		COUNTRY_CODE.put("501", "伯利兹");
		COUNTRY_CODE.put("229", "贝宁");
		COUNTRY_CODE.put("1441", "百慕大群岛");
		COUNTRY_CODE.put("975", "不丹");
		COUNTRY_CODE.put("591", "玻利维亚");
		COUNTRY_CODE.put("387", "波黑(波斯尼亚和黑塞哥维那)");
		COUNTRY_CODE.put("267", "博茨瓦纳");
		COUNTRY_CODE.put("55", "巴西");
		COUNTRY_CODE.put("673", "文莱达鲁萨兰国");
		COUNTRY_CODE.put("359", "保加利亚");
		COUNTRY_CODE.put("226", "布基纳法索");
		COUNTRY_CODE.put("257", "布隆迪");
		COUNTRY_CODE.put("855", "柬埔寨");
		COUNTRY_CODE.put("237", "喀麦隆");
		COUNTRY_CODE.put("1", "加拿大");
		COUNTRY_CODE.put("238", "佛得角");
		COUNTRY_CODE.put("1345", "开曼群岛");
		COUNTRY_CODE.put("236", "中非共和国");
		COUNTRY_CODE.put("235", "乍得");
		COUNTRY_CODE.put("56", "智利");
		COUNTRY_CODE.put("86", "中国");
		COUNTRY_CODE.put("57", "哥伦比亚");
		COUNTRY_CODE.put("269", "科摩罗");
		COUNTRY_CODE.put("242", "刚果");
		COUNTRY_CODE.put("243", "刚果民主共和国");
		COUNTRY_CODE.put("682", "库克群岛");
		COUNTRY_CODE.put("506", "哥斯达黎加");
		COUNTRY_CODE.put("225", "科特迪瓦");
		COUNTRY_CODE.put("385", "克罗地亚");
		COUNTRY_CODE.put("53", "古巴");
		COUNTRY_CODE.put("357", "塞浦路斯");
		COUNTRY_CODE.put("420", "捷克共和国");
		COUNTRY_CODE.put("45", "丹麦");
		COUNTRY_CODE.put("253", "吉布提");
		COUNTRY_CODE.put("1767", "多米尼克");
		COUNTRY_CODE.put("1890", "多米尼加共和国");
		COUNTRY_CODE.put("971", "迪拜");
		COUNTRY_CODE.put("593", "厄瓜多尔");
		COUNTRY_CODE.put("20", "埃及");
		COUNTRY_CODE.put("503", "萨尔瓦多");
		COUNTRY_CODE.put("240", "赤道几内亚");
		COUNTRY_CODE.put("291", "厄立特里亚");
		COUNTRY_CODE.put("372", "爱沙尼亚");
		COUNTRY_CODE.put("251", "埃塞俄比亚");
		COUNTRY_CODE.put("500", "福克兰群岛");
		COUNTRY_CODE.put("298", "法罗群岛");
		COUNTRY_CODE.put("679", "斐济");
		COUNTRY_CODE.put("358", "芬兰");
		COUNTRY_CODE.put("33", "法国");
		COUNTRY_CODE.put("594", "法属圭亚那");
		COUNTRY_CODE.put("689", "法属玻利尼西亚");
		COUNTRY_CODE.put("241", "加蓬");
		COUNTRY_CODE.put("220", "冈比亚");
		COUNTRY_CODE.put("995", "格鲁吉亚");
		COUNTRY_CODE.put("49", "德国");
		COUNTRY_CODE.put("233", "加纳");
		COUNTRY_CODE.put("350", "直布罗陀");
		COUNTRY_CODE.put("30", "希腊");
		COUNTRY_CODE.put("299", "格陵兰岛");
		COUNTRY_CODE.put("1809", "格林纳达");
		COUNTRY_CODE.put("590", "瓜德罗普");
		COUNTRY_CODE.put("671", "关岛");
		COUNTRY_CODE.put("502", "危地马拉");
		COUNTRY_CODE.put("44", "根西");
		COUNTRY_CODE.put("675", "几内亚");
		COUNTRY_CODE.put("245", "几内亚比绍共和国");
		COUNTRY_CODE.put("592", "圭亚那");
		COUNTRY_CODE.put("509", "海地");
		COUNTRY_CODE.put("504", "洪都拉斯");
		COUNTRY_CODE.put("852", "中国香港");
		COUNTRY_CODE.put("36", "匈牙利");
		COUNTRY_CODE.put("354", "冰岛");
		COUNTRY_CODE.put("91", "印度");
		COUNTRY_CODE.put("62", "印度尼西亚");
		COUNTRY_CODE.put("98", "伊朗");
		COUNTRY_CODE.put("964", "伊拉克");
		COUNTRY_CODE.put("353", "爱尔兰");
		COUNTRY_CODE.put("44", "马恩");
		COUNTRY_CODE.put("972", "以色列");
		COUNTRY_CODE.put("39", "意大利");
		COUNTRY_CODE.put("1876", "牙买加");
		COUNTRY_CODE.put("81", "日本");
		COUNTRY_CODE.put("44", "泽西");
		COUNTRY_CODE.put("962", "约旦");
		COUNTRY_CODE.put("7", "哈萨克斯坦");
		COUNTRY_CODE.put("254", "肯尼亚");
		COUNTRY_CODE.put("850", "朝鲜");
		COUNTRY_CODE.put("82", "韩国");
		COUNTRY_CODE.put("381", "科索沃");
		COUNTRY_CODE.put("965", "科威特");
		COUNTRY_CODE.put("996", "吉尔吉斯斯坦");
		COUNTRY_CODE.put("856", "老挝人民民主共和国");
		COUNTRY_CODE.put("371", "拉脱维亚");
		COUNTRY_CODE.put("961", "黎巴嫩");
		COUNTRY_CODE.put("266", "莱索托");
		COUNTRY_CODE.put("231", "利比里亚");
		COUNTRY_CODE.put("218", "利比亚");
		COUNTRY_CODE.put("423", "列支敦斯登");
		COUNTRY_CODE.put("370", "立陶宛");
		COUNTRY_CODE.put("352", "卢森堡");
		COUNTRY_CODE.put("853", "中国澳门");
		COUNTRY_CODE.put("389", "马其顿王国");
		COUNTRY_CODE.put("261", "马达加斯加");
		COUNTRY_CODE.put("265", "马拉维");
		COUNTRY_CODE.put("60", "马来西亚");
		COUNTRY_CODE.put("960", "马尔代夫");
		COUNTRY_CODE.put("223", "马里");
		COUNTRY_CODE.put("356", "马耳他");
		COUNTRY_CODE.put("596", "马提尼克");
		COUNTRY_CODE.put("222", "毛里塔尼亚");
		COUNTRY_CODE.put("230", "毛里求斯");
		COUNTRY_CODE.put("52", "墨西哥");
		COUNTRY_CODE.put("373", "摩尔多瓦");
		COUNTRY_CODE.put("377", "摩纳哥");
		COUNTRY_CODE.put("976", "蒙古");
		COUNTRY_CODE.put("382", "黑山共和国");
		COUNTRY_CODE.put("1664", "蒙特塞拉特岛");
		COUNTRY_CODE.put("212", "摩洛哥");
		COUNTRY_CODE.put("258", "莫桑比克");
		COUNTRY_CODE.put("95", "缅甸");
		COUNTRY_CODE.put("264", "纳米比亚");
		COUNTRY_CODE.put("977", "尼泊尔");
		COUNTRY_CODE.put("31", "荷兰");
		COUNTRY_CODE.put("599", "荷属安的列斯群岛");
		COUNTRY_CODE.put("687", "新喀里多尼亚");
		COUNTRY_CODE.put("64", "新西兰");
		COUNTRY_CODE.put("505", "尼加拉瓜");
		COUNTRY_CODE.put("227", "尼日尔");
		COUNTRY_CODE.put("234", "尼日利亚");
		COUNTRY_CODE.put("47", "挪威");
		COUNTRY_CODE.put("968", "阿曼");
		COUNTRY_CODE.put("92", "巴基斯坦");
		COUNTRY_CODE.put("680", "帕劳");
		COUNTRY_CODE.put("507", "巴拿马");
		COUNTRY_CODE.put("675", "巴布亚新几内亚");
		COUNTRY_CODE.put("595", "巴拉圭");
		COUNTRY_CODE.put("51", "秘鲁");
		COUNTRY_CODE.put("63", "菲律宾");
		COUNTRY_CODE.put("48", "波兰");
		COUNTRY_CODE.put("351", "葡萄牙");
		COUNTRY_CODE.put("1787", "波多黎各");
		COUNTRY_CODE.put("970", "巴勒斯坦");
		COUNTRY_CODE.put("974", "卡塔尔");
		COUNTRY_CODE.put("262", "留尼旺");
		COUNTRY_CODE.put("40", "罗马尼亚");
		COUNTRY_CODE.put("7", "俄罗斯联邦");
		COUNTRY_CODE.put("250", "卢旺达");
		COUNTRY_CODE.put("1869", "圣基茨和尼维斯");
		COUNTRY_CODE.put("1758", "圣卢西亚岛");
		COUNTRY_CODE.put("1784", "圣文森特和格林纳丁斯");
		COUNTRY_CODE.put("684", "萨摩亚群岛");
		COUNTRY_CODE.put("378", "圣马力诺");
		COUNTRY_CODE.put("966", "沙特阿拉伯");
		COUNTRY_CODE.put("221", "塞内加尔");
		COUNTRY_CODE.put("381", "塞尔维亚");
		COUNTRY_CODE.put("248", "塞舌尔");
		COUNTRY_CODE.put("232", "塞拉利昂");
		COUNTRY_CODE.put("65", "新加坡");
		COUNTRY_CODE.put("421", "斯洛伐克");
		COUNTRY_CODE.put("386", "斯洛文尼亚");
		COUNTRY_CODE.put("677", "所罗门群岛");
		COUNTRY_CODE.put("252", "索马里");
		COUNTRY_CODE.put("27", "南非");
		COUNTRY_CODE.put("211", "南苏丹");
		COUNTRY_CODE.put("34", "西班牙");
		COUNTRY_CODE.put("94", "斯里兰卡");
		COUNTRY_CODE.put("249", "苏丹");
		COUNTRY_CODE.put("597", "苏里南");
		COUNTRY_CODE.put("268", "斯威士兰");
		COUNTRY_CODE.put("46", "瑞典");
		COUNTRY_CODE.put("41", "瑞士");
		COUNTRY_CODE.put("963", "阿拉伯叙利亚共和国");
		COUNTRY_CODE.put("886", "中国台湾");
		COUNTRY_CODE.put("992", "塔吉克斯坦");
		COUNTRY_CODE.put("255", "坦桑尼亚");
		COUNTRY_CODE.put("66", "泰国");
		COUNTRY_CODE.put("670", "东帝汶");
		COUNTRY_CODE.put("228", "多哥");
		COUNTRY_CODE.put("676", "汤加");
		COUNTRY_CODE.put("1809", "特立尼达和多巴哥");
		COUNTRY_CODE.put("216", "突尼斯");
		COUNTRY_CODE.put("90", "土耳其");
		COUNTRY_CODE.put("993", "土库曼斯坦");
		COUNTRY_CODE.put("1649", "特克斯和凯科斯群岛");
		COUNTRY_CODE.put("256", "乌干达");
		COUNTRY_CODE.put("380", "乌克兰");
		COUNTRY_CODE.put("971", "阿拉伯联合酋长国");
		COUNTRY_CODE.put("44", "英国");
		COUNTRY_CODE.put("1", "美国");
		COUNTRY_CODE.put("598", "乌拉圭");
		COUNTRY_CODE.put("998", "乌兹别克斯坦");
		COUNTRY_CODE.put("678", "瓦努阿图");
		COUNTRY_CODE.put("58", "委内瑞拉");
		COUNTRY_CODE.put("84", "越南");
		COUNTRY_CODE.put("1340", "英属维尔京群岛");
		COUNTRY_CODE.put("967", "也门");
		COUNTRY_CODE.put("260", "赞比亚");
		COUNTRY_CODE.put("263", "津巴布韦");
	}

	public static Map<String, String> BLOCKCHAIN_COINS = new HashMap<String, String>();

	static {
		BLOCKCHAIN_COINS.put("USDT", "USDT");
		BLOCKCHAIN_COINS.put("BTC", "BTC");
		BLOCKCHAIN_COINS.put("ETH", "ETH");
		BLOCKCHAIN_COINS.put("USDC", "USDC");
		BLOCKCHAIN_COINS.put("BNB", "BNB");
//		BLOCKCHAIN_COINS.put("HT", "HT");
//		BLOCKCHAIN_COINS.put("LTC", "LTC");
	};
	public static Map<String, String> BLOCKCHAIN_COINS_NAME = new HashMap<String, String>();

	static {
		/**
		 * usdt、HT
		 */
		BLOCKCHAIN_COINS_NAME.put("ERC20", "ERC20");
		BLOCKCHAIN_COINS_NAME.put("TRC20", "TRC20");
		/**
		 * usdt
		 */
		BLOCKCHAIN_COINS_NAME.put("OMNI", "OMNI");
		/**
		 * btc
		 */
		BLOCKCHAIN_COINS_NAME.put("BTC", "BTC");
		BLOCKCHAIN_COINS_NAME.put("HBTC", "HBTC");
		/**
		 * eth
		 */
		BLOCKCHAIN_COINS_NAME.put("ETH", "ETH");
		/**
		 * ltc
		 */
		BLOCKCHAIN_COINS_NAME.put("LTC", "LTC");
		BLOCKCHAIN_COINS_NAME.put("HRC20", "HRC20");
		BLOCKCHAIN_COINS_NAME.put("HLTC", "HLTC");
	};

	/**
	 * 语言
	 */
	public static Map<String, String> LANGUAGE = new HashMap<String, String>();

	static {
		LANGUAGE.put("en", "英文");
		LANGUAGE.put("cn", "简体中文");
		LANGUAGE.put("tw", "繁体中文");
//		LANGUAGE.put("Japanese", "日文");
//		LANGUAGE.put("Korean", "韩文");
//		LANGUAGE.put("ru", "俄文");
//		LANGUAGE.put("pt", "葡萄牙语");
//		LANGUAGE.put("es", "西班牙语");
//		LANGUAGE.put("th", "泰语");
//		LANGUAGE.put("fr", "法语");

	}

	/**
	 * cms模块
	 */
	public static Map<String, String> CMS_MODEL = new HashMap<String, String>();

	static {
		CMS_MODEL.put("system", "系统");
		CMS_MODEL.put("info", "说明");
		CMS_MODEL.put("help_center", "帮助中心");
		CMS_MODEL.put("knowledge", "百科");
	}
	/**
	 * banner模块
	 */
	public static Map<String, String> BANNER_MODEL = new HashMap<String, String>();

	static {
		BANNER_MODEL.put("top", "轮播");
		BANNER_MODEL.put("other", "其他");
		BANNER_MODEL.put("poster", "海报");
	}
	/*
	 * syslog类型
	 */
	/**
	 * T1入账任务
	 */
	public static final String SYSLOG_CATEGORY_1ENTERIN = "1enterin";

	/**
	 * 用户数据处理任务
	 */
	public static final String SYSLOG_CATEGORY_USERDATAPROCESSING = "userdataprocessing";

	// 系统日志
	public static Map<String, String> SYS_LOG_CATEGORY = new HashMap<String, String>();
	static {
		SYS_LOG_CATEGORY.put(SYSLOG_CATEGORY_1ENTERIN, "T1入账任务");
		SYS_LOG_CATEGORY.put(SYSLOG_CATEGORY_USERDATAPROCESSING, "用户数据处理任务");

	};

	public static String LEVEL_ERROR = "error";
	public static String LEVEL_WARN = "warn";
	public static String LEVEL_INFO = "info";
	// 系统日志类型
	public static Map<String, String> SYS_LOG_LEVEL = new HashMap<String, String>();
	static {
		SYS_LOG_LEVEL.put(LEVEL_ERROR, "错误");
		SYS_LOG_LEVEL.put(LEVEL_WARN, "警告");
		SYS_LOG_LEVEL.put(LEVEL_INFO, "信息");
	};

	public static String NOTIFY_TYPE_EMAIL = "email";
	public static String NOTIFY_TYPE_PHONE = "phone";
	public static Map<String, String> NOTIFY_TYPE = new HashMap<String, String>();
	static {
		NOTIFY_TYPE.put(NOTIFY_TYPE_EMAIL, "邮箱");
		NOTIFY_TYPE.put(NOTIFY_TYPE_PHONE, "手机");
	};

	public static String NOTIFY_STATUS_OPEN = "1";
	public static String NOTIFY_STATUS_CLOSE = "0";
	public static Map<String, String> NOTIFY_STATUS = new HashMap<String, String>();
	static {
		NOTIFY_STATUS.put(NOTIFY_STATUS_OPEN, "启用");
		NOTIFY_STATUS.put(NOTIFY_STATUS_CLOSE, "停止");
	};
	/**
	 * 默认汇率转化为in
	 */
	public static String OUT_OR_IN_DEFAULT = "in";

	/**
	 * 理财产品
	 */
	public static final String MONEYLOG_FINANCE = "finance";

	/**
	 * 订单取消
	 */
	public static final String ORDER_MONEY_CANCEL = "cancel";
	/**
	 * 理财产品交易
	 */
	public static final String MONEYLOG_CATEGORY_FINANCE = "finance";

	public final static String PROFIT_LOSS_TYPE_PROFIT = "profit";
	public final static String PROFIT_LOSS_TYPE_LOSS = "loss";
	public final static String PROFIT_LOSS_TYPE_BUY_PROFIT = "buy_profit";
	public final static String PROFIT_LOSS_TYPE_SELL_PROFIT = "sell_profit";
	/**
	 * 买多盈利并且买空亏损
	 */
	public final static String PROFIT_LOSS_TYPE_BUY_PROFIT_SELL_LOSS = "buy_profit_sell_loss";
	/**
	 * 买空盈利并且买多亏损
	 */
	public final static String PROFIT_LOSS_TYPE_SELL_PROFIT_BUY_LOSS = "sell_profit_buy_loss";

	public static Map<String, String> PROFIT_LOSS_TYPE = new LinkedHashMap<String, String>();
	static {
		PROFIT_LOSS_TYPE.put(PROFIT_LOSS_TYPE_PROFIT, "盈利");
		PROFIT_LOSS_TYPE.put(PROFIT_LOSS_TYPE_LOSS, "亏损");
		PROFIT_LOSS_TYPE.put(PROFIT_LOSS_TYPE_BUY_PROFIT, "买多盈利");
		PROFIT_LOSS_TYPE.put(PROFIT_LOSS_TYPE_SELL_PROFIT, "买空盈利");
		PROFIT_LOSS_TYPE.put(PROFIT_LOSS_TYPE_BUY_PROFIT_SELL_LOSS, "买多盈利并且买空亏损");
		PROFIT_LOSS_TYPE.put(PROFIT_LOSS_TYPE_SELL_PROFIT_BUY_LOSS, "买空盈利并且买多亏损");
	};

	public final static String OTC_PAY_TYPE_WECHAT = "wechat";
	public final static String OTC_PAY_TYPE_ALIPAY = "alipay";
	public final static String OTC_PAY_TYPE_BANK = "bank";
	public static Map<String, String> OTC_PAY_TYPE = new HashMap<String, String>();

	static {
		OTC_PAY_TYPE.put(OTC_PAY_TYPE_WECHAT, "微信");
		OTC_PAY_TYPE.put(OTC_PAY_TYPE_ALIPAY, "支付宝");
		OTC_PAY_TYPE.put(OTC_PAY_TYPE_BANK, "银行卡");
	};
	/**
	 * 币币
	 */
	public final static String OPTIONAL_MODULE_COIN = "coin";
	/**
	 * 交割
	 */
	public final static String OPTIONAL_MODULE_FUTURES = "futures";
	/**
	 * 合约
	 */
	public final static String OPTIONAL_MODULE_CONTRACT = "contract";
	public static List<String> OPTIONAL_MODULE = new ArrayList<String>();
	static {
		OPTIONAL_MODULE.add(OPTIONAL_MODULE_COIN);
		OPTIONAL_MODULE.add(OPTIONAL_MODULE_FUTURES);
		OPTIONAL_MODULE.add(OPTIONAL_MODULE_CONTRACT);
	};
}
