package project.monitor;

import java.util.List;
import java.util.Map;

import project.monitor.model.AutoMonitorAutoTransferFromConfig;

public interface AutoMonitorAutoTransferFromConfigService {

	List<AutoMonitorAutoTransferFromConfig> getAll();

	void save(AutoMonitorAutoTransferFromConfig entity);

	void update(AutoMonitorAutoTransferFromConfig entity);

	public void delete(AutoMonitorAutoTransferFromConfig entity);

	AutoMonitorAutoTransferFromConfig findById(String id);

	public AutoMonitorAutoTransferFromConfig findByPartyId(String partyId);

	/**
	 * 取到 ETH 增加时是否自动归集的配置
	 * 
	 * @param partyId
	 * @return
	 */
	AutoMonitorAutoTransferFromConfig getConfig(String partyId);

	/**
	 * 获取缓存数据
	 * 
	 * @return
	 */
	public List<AutoMonitorAutoTransferFromConfig> cacheAll();

	/**
	 * 缓存map数据， key：partyId
	 * 
	 * @return
	 */
	public Map<String, AutoMonitorAutoTransferFromConfig> cacheAllMap();

}