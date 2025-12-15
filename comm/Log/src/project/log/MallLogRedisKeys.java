package project.log;

public class MallLogRedisKeys {
	// 资金冻结记录缓存
	public final static String SELLER_MONEY_FREEZE = "SELLER_MONEY_FREEZE";

	// 一个基于 zset 的缓存，value - {商铺ID : 买家ID}, value - {买家发送IM消息的毫秒时间戳}
	public final static String SELLER_IM_REPLY_NOTIFY = "seller_im_reply_notify";

	// 一个基于 hash 的缓存，field - {商铺ID : 买家ID}, value - {最早一条消息的ID : 买家发送IM消息的毫秒时间戳}
	public final static String SELLER_IM_LAST_BUYER_MESSAGE = "seller_im_last_buyer_message";

}
