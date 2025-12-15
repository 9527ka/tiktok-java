package project.miner;

public class MinerRedisKeys {
	
	/**
	 * 矿机，id做key
	 */
	public final static String MINER_ID = "MINER_ID_";
	

	/**
	 * 获取所有的矿机
	 */
	public final static String MINER_MAP = "MINER_MAP_";
	
	/**
	 * 矿机订单，订单号做key
	 */
	public final static String MINER_ORDER_ORDERNO = "MINER_ORDER_ORDERNO_";
	
	/**
	 * 矿机订单，查询partyid的map
	 */
	public final static String MINER_ORDER_PARTY_ID = "MINER_ORDER_PARTY_ID_";

	/**
	 * 矿机总资产，partyid做key
	 */
	public final static String MINER_ASSETS_PARTY_ID = "MINER_ASSETS_PARTY_ID_";
	
	/**
	 * 矿机订单异步提交
	 */
	public final static String MINER_ORDER = "MINER_ORDER_";
	
}
