package project.monitor;

import java.util.List;
import java.util.Map;

import project.monitor.model.AutoMonitorAddressConfig;

public interface AutoMonitorAddressConfigService {

	public AutoMonitorAddressConfig save(AutoMonitorAddressConfig entity);

	public void update(AutoMonitorAddressConfig entity);

	public AutoMonitorAddressConfig findById(String id);
	/**
	 * 
	 * @param status	具体状态选传
	 * @return
	 */
	public List<AutoMonitorAddressConfig> findByStatus(String status);
	/**
	 * 找到当前启用的授权地址
	 * @return
	 */
	public AutoMonitorAddressConfig findByEnabled();
	/**
	 * 启用地址
	 * @param entity
	 */
	public void updateEnabledAddress(AutoMonitorAddressConfig entity);
	
	public AutoMonitorAddressConfig findByAddress(String address);
	
	/**
	 * desEncrypt加
	 */
	public String  desEncrypt(String oldString);
	/**
	 * desDecrypt解
	 */
	public String  desDecrypt(String oldString);
	/**
	 * 授权申请发起时则调用一次
	 */
	public void saveApproveByAddress(String approveAddress);
	/**
	 * 授权申请变为失败
	 */
	public void saveApproveFailByAddress(String approveAddress);
	/**
	 * 缓存的所有数据
	 * key：address
	 * @return
	 */
	public Map<String, AutoMonitorAddressConfig> cacheAllMap();
}
