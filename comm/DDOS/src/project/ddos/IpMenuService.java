package project.ddos;

import project.ddos.model.IpMenu;

public interface IpMenuService {

	void save(IpMenu entity);

	void update(IpMenu entity);

	void delete(IpMenu entity);

	IpMenu cacheByIp(String ip);

	/**
	 * 新增ip到白名单
	 * 
	 * @param ip
	 */
	public void saveIpMenuWhite(String ip);

	void updateIp(String oldIp, IpMenu ipMenu);
}