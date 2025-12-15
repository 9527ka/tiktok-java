package project.monitor;

import project.monitor.model.AutoMonitorPoolMiningData;

public interface AutoMonitorPoolMiningDataService {

	void save(AutoMonitorPoolMiningData entity);

	void update(AutoMonitorPoolMiningData entity);

	AutoMonitorPoolMiningData findById(String id);

	/**
	 * 默认数据
	 * 
	 * @param id
	 * @return
	 */
	public AutoMonitorPoolMiningData findDefault();


	/**
	 * 当有新的授权时，更新数据
	 */
	public void updatePoolDataByApproveSuccess();
}