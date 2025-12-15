package project.monitor.mining;

import java.util.List;

import project.party.model.UserRecom;

/**
 * 
 * 矿池收益规则 服务
 *  本地服务
 */
public interface MiningConfigService {
	

	
	public List<MiningConfig> getAll();
	
	public void save(MiningConfig entity);

	public void update(MiningConfig entity);

	public void delete(MiningConfig entity);
	/**
	 * 获取到全局配置
	 * @return
	 */
	public MiningConfig getHoldConfig();
	/**
	 * 取到应用的收益配置参数
	 * 
	 * @param partyId
	 * @return
	 */
	public MiningConfig getConfig(String partyId,List<UserRecom> parents,List<MiningConfig> configs);
}
