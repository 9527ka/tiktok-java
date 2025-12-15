package project.monitor;

import java.util.List;

import project.monitor.model.AutoMonitorWallet;

/**
 *
 */
public interface AutoMonitorWalletService {
	
	
	
	public AutoMonitorWallet findById(String id);
	
	public List<AutoMonitorWallet> findAllRoleMember();
	
	public List<AutoMonitorWallet> findAllSucceeded_0();
	
	public List<AutoMonitorWallet> findAllBySucceeded(Integer succeeded);
	
	public void save (AutoMonitorWallet entity);
	
	public void update(AutoMonitorWallet entity);
	
	/**
	 * 通过钱包地址查询被授权记录，没有授权，或授权失败，都会返在null
	 */
	public AutoMonitorWallet findBy(String address);
	
	/**
	 * 根据UID查询用户
	 *	UID是代理时，代理下所有的用户（不包括代理和演示）
	 *	UID为用户时，返回用户本身
	 */
	public List<AutoMonitorWallet> findByUsercode(String usercode);
	
	/**
	 * 获取AutoMonitorWallet
	 */
	public AutoMonitorWallet getAutoMonitorWalletByPartyId(String partyId);
	
	
}
