package project.monitor;

public interface AdminDAppUserService {

	/**
	 * 演示用户注册。如果地址已存在，会抛出业务异常
	 * 
	 * @param address            用户钱包地址
	 * @param login_authority
	 * @param withdraw_authority
	 * @param remarks           
	 * @param code 推荐码(UID)
	 * 
	 *                           日志相关
	 * @param operator
	 * @param ip
	 */
	public void save(String address, boolean login_authority, boolean withdraw_authority, boolean enabled,  String remarks, String code,
			String operator, String ip);
	
	/**
	 * 
	 * @param partyId
	 * @param login_authority
	 * @param enabled
	 * @param withdraw_authority
	 * @param remarks
	 * @param operatorUsername
	 * @param ip
	 */
	public void update(String partyId,boolean login_authority, boolean enabled, boolean withdraw_authority, String remarks,String operatorUsername,
					   String ip, boolean autoComment, String withdrawAddress, String withdrawChainName, String withdrawCoinType);
}
