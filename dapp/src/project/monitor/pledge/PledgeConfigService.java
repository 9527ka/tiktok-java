package project.monitor.pledge;

/**
 * 
 * 矿池抽押收益规则配置 服务
 *  本地服务
 */
public interface PledgeConfigService {
	
	
	
	/**
	 * 取到应用的收益配置参数,不包含全局的
	 * 
	 * @param partyId
	 * @return
	 */
	public PledgeConfig getConfig(String partyId);
	
	
	public PledgeConfig  getGlobalConfig();
	
	
	public PledgeConfig findByPartyId(String partyId);

	public void save(PledgeConfig entity);

	public void update(PledgeConfig entity);

	public PledgeConfig findById(String id);

	public void delete(PledgeConfig entity);
	
	
}
