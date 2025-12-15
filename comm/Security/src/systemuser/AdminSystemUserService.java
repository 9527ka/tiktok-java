package systemuser;

import java.io.Serializable;
import java.util.Map;

import kernel.web.Page;
import security.SecUser;

public interface AdminSystemUserService {

	public void save(SecUser user, String operatorUsername, String loginSafeword, String code, String ip, String superGoogleAuthCode);

	public void saveAllUser(SecUser user);

	public SecUser get(String id);
	
	/**
	 * 更新系统用户，有密码则更新密码,否则更新用户
	 * @param user
	 * @param newPassword
	 * @param type			密码类型，	password：登录密码，safe_password：资金密码
	 */
	public void update(SecUser user,String newPassword,String type,String operatorUsername,String loginSafeword,String code,String ip,String superGoogleAuthCode) ;
	
	/**
	 * 删除系统用户
	 */
	public void delete(SecUser user, String operatorUsername, String loginSafeword, String ip, String superGoogleAuthCode);
	
	public Page pagedQuery(int pageNo,int pageSize ,String usernamePara);
	/**
	 * 获取可分配给系统用户的角色
	 * @return
	 */
	public Map<String,String> findRoleMap();
	
}
