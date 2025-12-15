package project.user.googleauth;

import security.SecUser;

public interface GoogleAuthService {
	
	/**
	 * 用户绑定谷歌验证器
	 * @param username
	 */
	public boolean saveGoogleAuthBind(String username,String secret,String code);
	/**
	 * 用户解绑谷歌验证器
	 * @param username
	 */
	public void saveGoogleAuthUnBind(String username);
	/**
	 * 验证用户的谷歌验证码
	 * @param secret
	 * @param code
	 * @return
	 */
	public boolean checkCode(String secret,String code);
	
	/**
	 * 生成谷歌验证码二维码，并返回地址
	 * @param username
	 * @param secret
	 * @return	二维码地址
	 */
	public String getGoogleAuthUrl(String username,String secret);
	
	/**
	 * 校验超级谷歌验证码
	 */
//	public void queryCheckSuperGoogleAuthCode(String code);
	
	/**
	 * 校验谷歌验证码
	 */
	public void checkGoogleAuthCode(SecUser secUser,String code);

    boolean isEnableGoogleAuth();

    /**
	 * 登录时 校验谷歌验证码
	 */
	public void updateGoogleAuthCodeForLogin(String ip, String operatorUsername, String googleAuthCode, String uri);
}
