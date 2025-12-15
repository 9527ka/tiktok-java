package project.monitor.pledgegalaxy;

public class PledgeGalaxyRedisKeys {

	/**
	 * 质押2.0配置，拼接partyId做key,如果是全局配置partyId为空字符
	 */
	public final static String PLEDGE_GALAXY_CONFIG = "pledge_galaxy_config_";

	/**
	 * 质押2.0订单，主键做key
	 */
	public final static String PLEDGE_GALAXY_ORDER = "pledge_galaxy_order_";
	
	/**
	 * 质押2.0订单，查询partyId的map
	 */
	public final static String PLEDGE_GALAXY_ORDER_PARTYID = "pledge_galaxy_order_partyId_";
	
}
