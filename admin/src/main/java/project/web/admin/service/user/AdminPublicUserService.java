package project.web.admin.service.user;

/**
 *用户的公共服务，
 *
 */
public interface AdminPublicUserService {
	/**
	 * 修改密码，会验证旧密码
	 */
	public void saveChangePassword(String partyId,String oldpassword,String password,String username,String safeword,String code,String googleAuthCode);
	
	/**
	 * 修改资金密码，会验证旧密码
	 */
	public void saveChangeSafeword(String partyId,String oldpassword,String password,String username,String code,String googleAuthCode);
}