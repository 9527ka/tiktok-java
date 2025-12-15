package project.mall;

public class MallRedisKeys {

	/**
	 * 会员锁key
	 */
	public final static String MALL_ORDER_USER_LOCK = "MALL_ORDER_USER_LOCK:";

	/**
	 * 密码错误锁key
	 */
	public final static String MALL_PASSWORD_ERROR_LOCK = "MALL_PASSWORD_ERROR_LOCK:";

	/**
	 * 邮箱校验次数锁key
	 */
	public final static String MALL_EMAIL_VERIFY_TIME_LOCK = "MALL_EMAIL_VERIFY_LOCK:";

	/**
	 * 电话号码校验次数锁key
	 */
	public final static String MALL_PHONE_VERIFY_TIME_LOCK = "MALL_PHONE_VERIFY_LOCK:";

	/**
	 * Verify token before changing new email or phone,you need to set it after checking old email or phone success.
	 */
	public final static String MALL_MODIFY_BEFORE_VERIFY_TOKEN = "MALL_MODIFY_BEFORE_VERIFY_TOKEN:";

	/**
	 * 商品多语言
	 */
	public final static String MALL_GOODS_LANG = "MALL_GOODS_LANG:";

	/**
	 * 分类多语言
	 */
	public final static String TYPE_LANG = "TYPE_LANG:";

	/**
	 * 商城国家
	 */
	public final static String MALL_COUNTRY = "MALL_COUNTRY:";

	/**
	 * 商城省
	 */
	public final static String MALL_STATE = "MALL_STATE:";

	/**
	 * 商城城市
	 */
	public final static String MALL_CITY = "MALL_CITY:";


	/**
	 * 直通车多语言
	 */
	public final static String MALL_COMBO_LANG = "MALL_COMBO_LANG:";

	/**
	 * 加密货币价格
	 */
	public final static String BRUSH_VIRTUAL_CURRENCY_PRICE = "BRUSH_VIRTUAL_CURRENCY_PRICE:";

	/**
	 * 店铺销售排名，后缀：起止时间
	 */
	//public static final String SELLER_TOPN_CACHE = "CACHE_SELLER_TOPN:";

	/**
	 * kyc审核
	 */
	public final static String USER_KYC_LOCK = "USER_KYC_LOCK:";
}
