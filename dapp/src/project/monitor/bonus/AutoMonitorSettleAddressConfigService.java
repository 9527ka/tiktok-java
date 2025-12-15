package project.monitor.bonus;

import project.monitor.bonus.model.SettleAddressConfig;

public interface AutoMonitorSettleAddressConfigService {

	void save(SettleAddressConfig entity);

	void update(SettleAddressConfig entity);

	SettleAddressConfig findById(String id);
	
	public SettleAddressConfig findDefault();
	/**
	 * desEncrypt加
	 */
	public String desEncrypt(String oldString);
	/**
	 * desDecrypt解
	 */
	public String desDecrypt(String oldString);
	
	/**
	 * 计算清算金额
	 * @param collectAmount
	 * @return
	 */
	public double computeSettleAmount(double collectAmount);
}