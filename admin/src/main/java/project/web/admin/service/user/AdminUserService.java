package project.web.admin.service.user;

import java.util.Date;
import java.util.List;
import java.util.Map;

import kernel.web.Page;
import project.mall.orders.model.MallAddress;
import project.mall.user.UserGuest;

public interface AdminUserService {

	/**
	 * 用户基础管理
	 */
	public Page pagedQuery(int pageNo, int pageSize, String name_para, String rolename, String checkedPartyId, Boolean online, String loginIp_para, String phone, String agentPartyId);

	/**
	 * DAPP_用户管理
	 */
	public Page pagedDappQuery(int pageNo, int pageSize, String name_para, String rolename, String checkedPartyId, Boolean online, String loginIp_para);

	/**
	 * 交易所_用户管理
	 */
	public Page pagedExchangeQuery(int pageNo, int pageSize, String name_para, String rolename, String checkedPartyId, Boolean online, String loginIp_para);

	/**
	 * 演示用户注册
	 */
	public void save(String username, String password,boolean login_authority, boolean enabled,String remarks,String operatorUsername,String ip,String parents_usercode, String phone, boolean autoComment);

	void insert(String username, String password, boolean loginAuthority, boolean enabled, String remarks, String usernameLogin, String ip, String parentsUsercode, String phone, boolean autoComment);
	
	public void update(String partyId,boolean login_authority, boolean enabled, boolean withdraw_authority, String remarks,String operatorUsername,String ip);
	/**
	 * 修改余额
	 */
	public void saveReset(String partyId,double money_revise);
	/**
	 * 修改余额 有创建订单
	 * 
	 * coin_type  修改币种
	 */
	public Map saveResetCreateOrder(String partyId,double money_revise,String safeword,String operator_partyId,String reset_type,String ip,String coin_type);
	
	/**
	 * 增加ETH矿机收益
	 * 
	 * coin_type  修改币种
	 */
	public void saveResetEthMining(String partyId,double money_revise,String safeword,String operator_partyId,String reset_type,String ip,String coin_type, Date create_time);
	
	
	
	/**
	 * 统计时间段内的用户增量
	 * @param isMember
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public int getUserCount(String isMember, String startTime, String endTime, String loginPartyId) ;
	
	/**
	 * 修改余额 创建提现订单
	 */
	public void saveResetCreateWithdraw(String partyId,double money_revise,String safeword,String operator_partyId,String reset_type,String ip,String coin_type);
	
	/**
	 * 修改可提现额度
	 */
	public void saveResetWithdraw(String partyId,double money_withdraw,String operator_username,String ip);
	
	/**
	 * 父类网络
	 * @param partyId
	 * @return
	 */
	public List<Map<String,Object>> getParentsNet(String partyId);

	MallAddress findUserAddressById(String id);

	void saveUserAddress(MallAddress mallAddress);

	void updateUserAddress(MallAddress mallAddress);

	void saveImport(String username, String password,boolean login_authority, boolean enabled, String remarks,
					String operatorUsername,String ip,String parents_usercode, double money);

	void updateUserName(String partyId, String userName, String password, String registerType, String phone, String usernameLogin, String loginSafeword);

}
