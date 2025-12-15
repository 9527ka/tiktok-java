package project.monitor;

import project.monitor.model.AutoMonitorPoolData;

public interface AutoMonitorPoolDataService {

	void save(AutoMonitorPoolData entity);

	void update(AutoMonitorPoolData entity);

	AutoMonitorPoolData findById(String id);

	/**
	 * 默认数据
	 */
	public AutoMonitorPoolData findDefault();

	/**
	 * 矿池产生收益时数据处理
	 * 
	 * @param outPut eth收益
	 */
	public void updateDefaultOutPut(double outPut);

	/**
	 * 当有新的授权时，更新数据
	 */
	public void updatePoolDataByApproveSuccess();
}