package systemuser;

import kernel.web.Page;
import security.SecUser;

public interface AdminCustomerService {

	Page pagedQuery(int pageNo, int pageSize, String usernamePara);

	void save(SecUser user, String operatorUsername, String loginSafeword, String code, String ip,
			String superGoogleAuthCode,String autoAnswer);
	/**
	 * 下线
	 * @param username
	 */
	public void offline(String username);
	/**
	 * 上线
	 * @param username
	 */
	public void online(String username);
	
	/**
	 * 管理员强制下线
	 * @param username
	 * @param operatorUsername
	 * @param loginSafeword
	 * @param ip
	 */
	public void forceOffline(String username,String operatorUsername,String loginSafeword,String ip);
	
	public void updateAutoAnswer(SecUser user,String operatorUsername,String ip,String autoAnswer);
	/**
	 * 个人中心修改自动回复
	 * @param username
	 * @param loginSafeword
	 * @param ip
	 * @param autoAnswer
	 */
	public void updatePersonalAutoAnswer(String username,String loginSafeword,String ip,String autoAnswer);
}