package project.futures;

public class FuturesRedisKeys {
	
	/**
	 * 交割合约参数，id做key
	 */
	public final static String FUTURES_PARA_ID = "FUTURES_PARA_ID_";
	
	/**
	 * 交割合约参数，查询 map ，symbol做key
	 */
	public final static String FUTURES_PARA_SYMBOL = "FUTURES_PARA_SYMBOL_";
	
	/**
	 * 交割持仓订单，orderno做key
	 */
	public final static String FUTURES_SUBMITTED_ORDERNO = "FUTURES_SUBMITTED_ORDERNO_";
	
	/**
	 * 交割持仓单所有
	 */
//	public final static String FUTURES_SUBMITTED_MAP = "FUTURES_SUBMITTED_MAP_";
	
	/**
	 * 交割场控，partyId做key
	 */
	public final static String FUTURES_PROFIT_LOSS_PARTY_ID = "FUTURES_PROFIT_LOSS_PARTY_ID_";	
	
	/**
	 * 交割推荐奖励 update队列
	 */
	public final static String FUTURES_RECOM_QUEUE_UPDATE = "FUTURES_RECOM_QUEUE_UPDATE_";

	/**
	 * 永续合约，总资产，partyid做key
	 */
	public final static String FUTURES_ASSETS_PARTY_ID = "FUTURES_ASSETS_PARTY_ID_";

	/**
	 * 永续合约，总未实现盈利，partyid做key
	 */
	public final static String FUTURES_ASSETS_PROFIT_PARTY_ID = "FUTURES_ASSETS_PROFIT_PARTY_ID_";
	
}
