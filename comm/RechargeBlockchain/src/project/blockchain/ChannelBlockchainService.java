package project.blockchain;

import java.util.List;

/**
 * 链入地址
 */
public interface ChannelBlockchainService {

	/**
	 * 地址添加
	 */
	public void save(ChannelBlockchain channelBlockchain, String userName, String safeword, String login_ip,
			String code, String superGoogleAuthCode);

	/**
	 * 地址更新
	 */
	public void update(ChannelBlockchain old, ChannelBlockchain channelBlockchain, String userName, String partyId,
			String safeword, String login_ip, String code, String superGoogleAuthCode);

	public ChannelBlockchain findById(String id);

	public ChannelBlockchain findByNameAndCoinAndAdd(String blockchain_name, String coin,String address);

	/**
	 * 查询区块链个人充值地址
	 * @param blockchain_name
	 * @param coin
	 * @param address
	 * @param username
	 * @return
	 */
	public PartyBlockchain findByPersonNameAndCoinAndAdd(String blockchain_name, String coin, String address,String username);

	/**
	 * 地址删除
	 * 
	 * @param id
	 * @param safeword 资金密码
	 * @param userName 登录人
	 * @param loginIp  ip
	 * @param code     验证码
	 */
	public void delete(String id, String safeword, String userName, String loginIp, String code,
			String superGoogleAuthCode);

	public List<ChannelBlockchain> findAll();

	/**
	 * @param coin
	 * @return
	 */
	public List<ChannelBlockchain> findByCoin(String coin);
	
	/**
	 * @param coin
	 * @param blockchain_name
	 * @return
	 */
	public List<ChannelBlockchain> findByCoinAndName(String coin, String blockchain_name);

	/**
	 * 过滤充值地址链，随机获取
	 * 
	 * @param list
	 * @return
	 */
	public List<ChannelBlockchain> filterBlockchain(List<ChannelBlockchain> list);

	PartyBlockchain findPersonBlockchain(String username, String coinSymbol, String blockchainName);

}
