package project.contract;

public class ContractRedisKeys {

	/**
	 * 永续合约，orderNo做key
	 */
	public final static String CONTRACT_ORDERNO = "CONTRACT_ORDERNO_";
	
	/**
	 * 永续合约，查询订单map，partyid做key
	 */
	public final static String CONTRACT_SUBMITTED_ORDER_PARTY_ID = "CONTRACT_SUBMITTED_ORDER_PARTY_ID_";

	/**
	 * 永续合约，总资产，partyid做key
	 */
	public final static String CONTRACT_ASSETS_PARTY_ID = "CONTRACT_ASSETS_PARTY_ID_";

	/**
	 * 永续合约，总保证金，partyid做key
	 */
	public final static String CONTRACT_ASSETS_DEPOSIT_PARTY_ID = "CONTRACT_ASSETS_DEPOSIT_PARTY_ID_";

	/**
	 * 永续合约，总未实现盈利，partyid做key
	 */
	public final static String CONTRACT_ASSETS_PROFIT_PARTY_ID = "CONTRACT_ASSETS_PROFIT_PARTY_ID_";
	
}
