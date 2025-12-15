package project.wallet.rate;

/**
 * 
 *用户汇率配置
 */
public interface UserRateConfigService {

	public void update(String rateId, String partyId);

	public UserRateConfig getByPartyId(String partyId);
	
	/**
	 * 查询用户计价方式，如果没有配置，则返回默认的计价方式
	 */
	public ExchangeRate findUserConfig(String partyId);

}
