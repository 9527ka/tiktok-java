package security.internal;

import java.io.Serializable;
import java.util.List;

import project.party.model.Party;
import security.SecUser;

public interface SecUserService {

	/**
	 * 根据登陆用户名查询用户
	 */
	public SecUser findUserByLoginName(String loginName);

	/**
	 * 根据手机号
	 */
	Party findUserByPhone(String phone);

	/**
	 * 根据用户名和角色查询用户
	 */
	public SecUser findValidUserByLoginName(String loginName, String[] roles);

	public void saveUser(SecUser user);

	public void deleteUser(SecUser user);

	/**
	 * 修改密码，会验证旧密码
	 * 
	 * @param partyId
	 * @param oldpassword
	 * @param password
	 */
	public void updatePassword(String username, String oldpassword, String password);

	/**
	 *  修改密码，不验证旧密码，管理员后台重置密码使用
	 * 
	 * @param loginName
	 * @param password
	 */
	public void updatePassword(String loginName, String password);

	/**
	 * 根据partyId查询用户
	 */
	public SecUser findUserByPartyId(String partyId);

	public void update(SecUser user);

	public SecUser findUserById(String id);

	public  SecUser findUserByLoginCode(String loginCode,String loginFlag);
	/**
	 * 修改资金密码，会验证旧密码
	 *
	 * @param username
	 * @param oldpassword
	 * @param password
	 */
	public void updateSafeword(String username, String oldpassword, String password);

	/**
	 * 修改用户名和密码
	 * @param partyId
	 * @param userName
	 * @param password
	 */
	public void updateSecUser(String partyId, String userName, String password);

	/**
	 *  修改资金密码，不验证旧密码，管理员后台重置密码使用
	 * 
	 * @param loginName
	 * @param password
	 */
	public void updateSafeword(String loginName, String password);
	
	/**
	 * 所有系统用户
	 * @return
	 */
	public List<SecUser> findAllSysUsers();
	
	public String test();

	boolean queryCheckGuestAccount(String partyId);
}
