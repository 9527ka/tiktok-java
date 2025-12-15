package project;

public class RedisKeys {
	/**
	 * item杠杆倍数
	 */
	public final static String ITEM_LEVER_ID = "ITEM_LEVER_ID_";

	public final static String ITEM_MAP = "ITEM_MAP_";

	public final static String ITEM_SYMBOL = "ITEM_SYMBOL_";
	public final static String ITEM_SYMBOLDATA = "ITEM_SYMBOLDATA_";

	/**
	 * partyId查询party表
	 */
	public final static String PARTY_PARTYID = "PARTY_PARRTID_";
	/**
	 * party，在线用户，partyId做key
	 */
	public final static String PARTY_ONLINEUSER_PARTYID = "PARTY_ONLINEUSER_PARTYID_";
	/**
	 * party，获取到所有在线用户
	 */
	public final static String PARTY_ONLINEUSER = "PARTY_ONLINEUSER_";
	/**
	 * code查询系统参数Syspara表
	 */
	public final static String SYSPARA_CODE = "SYSPARA_CODE_";
	/**
	 * 查询系统参数Syspara表的Map
	 */
	public final static String SYSPARA_MAP = "SYSPARA_MAP_";

	/**
	 * 区块链充值地址
	 */
	public final static String CHANNEL_BLOCKCHAIN_ID = "CHANNEL_BLOCKCHAIN_ID_";
	/**
	 * 区块链充值地址 所有
	 */
	public final static String CHANNEL_BLOCKCHAIN_MAP = "CHANNEL_BLOCKCHAIN_MAP_";
	/**
	 * 区块链充值订单
	 */
	public final static String RECHARGE_BLOCKCHAIN_ORDERNO = "RECHARGE_BLOCKCHAIN_ORDERNO_";
	/**
	 * 提现订单
	 */
	public final static String WITHDRAW_ORDERNO = "WITHDRAW_ORDERNO_";

	/**
	 * 钱包 partyid 做key
	 */
	public final static String WALLET_PARTY_ID = "WALLET_PARTY_ID_";
	/**
	 * 钱包，异步更新
	 */
	public final static String WALLET = "WALLET_";
	/**
	 * 拓展钱包 partyId+walletType做key
	 */
	public final static String WALLET_EXTEND_PARTY_ID_WALLETTYPE = "WALLET_EXTEND_PARTY_ID_WALLETTYPE_";
	/**
	 * 拓展钱包 查询partyId的map
	 */
	public final static String WALLET_EXTEND_PARTY_ID = "WALLET_EXTEND_PARTY_ID_";
	/**
	 * 拓展钱包，异步更新
	 */
	public final static String WALLET_EXTEND = "WALLET_EXTEND_";

	/**
	 * 系统用户，username做key
	 */
	public final static String SECUSER_USERNAME = "SECUSER_USERNAME_";

//	/**
//	 * CMS，查询语言的map
//	 */
//	public final static String CMS_LANGUAGE = "CMS_LANGUAGE_";
//	/**
//	 * CMS,language+contentCode 做key
//	 */
//	public final static String CMS_LANGUAGE_CONTENTCODE = "CMS_LANGUAGE_CONTENTCODE_";

	/**
	 * 新闻，id做key
	 */
	public final static String NEWS_ID = "NEWS_ID_";

	/**
	 * 新闻，查询语言的map
	 */
	public final static String NEWS_LANGUAGE = "NEWS_LANGUAGE_";

	/**
	 * 充提日志，orderno做key
	 */
	public final static String WALLET_LOG_ORDERNO = "WALLET_LOG_ORDERNO_";
	/**
	 * 充提日志，查询分类map
	 */
	public final static String WALLET_LOG_CATEGORY = "WALLET_LOG_CATEGORY_";

	/**
	 * 汇率，CURRENCY做key
	 */
	public final static String EXCHANGE_RATE_CURRENCY = "EXCHANGE_RATE_CURRENCY_";
	/**
	 * 汇率，ID做key
	 */
	public final static String EXCHANGE_RATE_ID = "EXCHANGE_RATE_ID_";
	/**
	 * 汇率，查询out_or_in 的map
	 */
	public final static String EXCHANGE_RATE_OUTORIN = "EXCHANGE_RATE_OUTORIN_";
	/**
	 * 用户汇率配置，partyId做key
	 */
	public final static String USER_RATE_CONFIG_PARTY_ID = "USER_RATE_CONFIG_PARTY_ID_";

	/**
	 * 用户认证，partyId做key
	 */
	public final static String KYC_PARTY_ID = "KYC_PARTY_ID_";
	/**
	 * 高级认证，partyId做key
	 */
	public final static String KYC_HIGHLEVEL_PARTY_ID = "KYC_HIGHLEVEL_PARTY_ID_";
	/**
	 * 支付方式，partyId做key
	 */
	public final static String PAYMENT_METHOD_ID = "PAYMENT_METHOD_ID_";
	/**
	 * 支付方式，查询partyId的map
	 */
	public final static String PAYMENT_METHOD_PARTY_ID = "PAYMENT_METHOD_PARTY_ID_";

	/**
	 * 获取用户partyId，根据token
	 */
	public final static String TOKEN = "TOKEN_";
	/**
	 * 获取用户token，party做key
	 */
	public final static String TOKEN_PARTY_ID = "TOKEN_PARTY_ID_";
	/**
	 * 理财产品，id做key
	 */
	public final static String FINANCE_ID = "FINANCE_ID_";
	/**
	 * 理财产品，MAP
	 */
	public final static String FINANCE_MAP = "FINANCE_MAP_";
	/**
	 * 理财产品，map并且state=1
	 */
	public final static String FINANCE_MAP_STATE_1 = "FINANCE_MAP_STATE_1_";
	/**
	 * 理财产品订单，id做key
	 */
	public final static String FINANCE_ORDER_ID = "FINANCE_ORDER_ID_";

	/**
	 * 理财产品订单，map并且state=1
	 */
	public final static String FINANCE_ORDER_MAP_STATE_1 = "FINANCE_ORDER_MAP_STATE_1_";

	/**
	 * 币币交易 现货，order_no为key
	 */
	public final static String EXCHANGE_ORDER_NO = "EXCHANGE_ORDER_NO_";
	/**
	 * 币币交易 现货，所有已提交的订单
	 */
	public final static String EXCHANGE_ORDER_STATE_SUBMITTED_MAP_ORDER_NO = "EXCHANGE_ORDER_STATE_SUBMITTED_ORDER_NO_";
	/**
	 * 币币资产交易买入价格，PARTYID 和wallettype为key
	 */
	public final static String WALLETEXTENDCOSTUSDT_PARTYID_WALLETTYPE = "WALLETEXTENDCOSTUSDT_PARTYID_WALLETTYPE_";

	/**
	 * 用户提现密码输入次数限制 key ,eg: WITHDRAW_DEPOSIT_PASSWORD_FAILD_NUMBER_userId_1
	 */
	public final static String WITHDRAW_DEPOSIT_PASSWORD_FAILD_NUMBER = "WITHDRAW_DEPOSIT_PASSWORD_FAILD_NUMBER_";

	/**
	 * 店铺商品最近增量销量缓存，value : 店铺商品id， score : 最近新增销量
	 */
	public final static String SELLER_GOODS_PURCHASE_ACC = "seller_goods_purchase_acc";

	/**
	 * 订单相关商品被评论累计次数缓存，value : 店铺商品id， score : 最近新增评论次数
	 */
	public final static String ORDER_GOODS_EVALUATION_ACC = "order_goods_evaluation_acc";

	/**
	 * 商品收藏/取消收藏事件累计次数缓存，value : 店铺商品id， score : 最近新增事件次数
	 */
	public final static String SELLER_GOODS_KEEP_EVENT_ACC = "seller_goods_keep_event_acc";

	/**
	 * 商品流量变更事件累计次数缓存，value : 店铺商品id， score : 最近新增事件次数
	 */
	public final static String SELLER_GOODS_VIEW_EVENT_ACC = "seller_goods_view_event_acc";

	/**
	 * 折扣商品截止时间戳缓存，value : 店铺商品id， score : 截止时间戳，毫秒
	 */
	public final static String SELLER_GOODS_DISCOUNT_ENDTIME = "seller_goods_discount_endtime";

	/**
	 * 商品第一次上架时间，value : 店铺商品id， score : 时间戳，毫秒
	 */
	public final static String SELLER_GOODS_FIRST_SHELF_TIME = "seller_goods_first_shelf_time";

	/**
	 * 商品折扣状态变更缓存，value : 店铺商品id， score : 状态有变化 > 0, 相对于上次操作无变化 - 0
	 */
	public final static String SELLER_GOODS_DISCOUNT_STATE_CHANGED = "seller_goods_discount_state_changed";

	/**
	 * 用户充值通过时间缓存，value : partyId， score : 最近充值通过毫秒时间戳
	 */
	public final static String RECHARGE_PASS_TIME = "recharge_time";

	/**
	 * 用户充值通过时间缓存，value : partyId， score : 最近充值通过毫秒时间戳
	 */
	public final static String SELLER_RECHARGE_UPGRADE_TIME = "seller_recharge_upgrade_time";


	public static final String ORDER_LOCK = "order-lock_";


	/**
	 * 账变加钱通知
	 */
	public final static String SELLER_MONEY_ADD_NOTIFY = "seller_money_add_notify_";

}
