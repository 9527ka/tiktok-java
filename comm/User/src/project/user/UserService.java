package project.user;

import java.util.Map;

import project.event.model.UserChangeInfo;
import security.SecUser;

public interface UserService {
	
	/**
	 * 登录
	 */
	public SecUser addLogin(String username, String password);
	
	/**
	 * 验证码登录
	 */
	public SecUser addLogin_idcode(String username, String verifcode);
	
	/**
	 * 退出登录
	 */
	public void logout(String partyId);

	/**
	 * 图片验证码缓存
	 */
	public Map<String, String> getImageCodeCache();
	
	/**
	 * 设置玩家在线
	 */
	public void online(String partyId);

	/**
	 * 设置玩家下线
	 */
	public void offline(String partyId);

	public void putImageCode(String key, String value);

	public String cacheImageCode(String key);

	public void cacheRemoveImageCode(String key);

	/**
	 * 用户关键信息修改了，需要同步到 secUser 表
	 * @param changeInfo
	 */
	public void updateSyncUserInfo(UserChangeInfo changeInfo);

	void updateLogoffAccount(String partyId, String reason);

}
