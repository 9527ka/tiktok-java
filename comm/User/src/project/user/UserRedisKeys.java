package project.user;

public class UserRedisKeys {
	/**
	 * 用户认证
	 */
	public final static String KYC_PARTY_ID = "KYC_PARTY_ID_";
	/**
	 * 高级认证
	 */
	public final static String KYC_HIGHLEVEL_PARTY_ID = "KYC_HIGHLEVEL_PARTY_ID_";
	/**
	 * 支付方式
	 */
	public final static String PAYMENT_METHOD_ID = "PAYMENT_METHOD_ID_";
	public final static String PAYMENT_METHOD_PARTY_ID = "PAYMENT_METHOD_PARTY_ID_";

	/**
	 * token
	 */
	public final static String TOKEN = "TOKEN_";
	public final static String TOKEN_PARTY_ID = "TOKEN_PARTY_ID_";


	public final static String PLAT_FROM_TOKEN = "PLAT_FROM_TOKEN_";
	public final static String PLAT_FROM_TOKEN_PARTY_ID = "PLAT_FROM_TOKEN_PARTY_ID_";

	/**
	 * 在线用户
	 */
	public final static String ONLINEUSER_PARTYID = "ONLINEUSER_PARTYID_";
	public final static String ONLINEUSER = "ONLINEUSER_";
	/**
	 * 客服聊天列表在线聊天，离线状态设置    在线1，离开2，离线3
	 */
	public final static String ONLINE_USER_STATUS_PARTYID = "ONLINE_USER_STATUS_PARTYID:";
}
