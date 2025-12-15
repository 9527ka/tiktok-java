package project.web.admin.service.email;

public interface AdminEmailCodeService {
	/**
	 * 发送验证码
	 * @param ip
	 * @param operatorUsername	操作人
	 * @param context			操作内容
	 * @param isSuper			true:超级签,false:个人签
	 */
	public void sendCode(String ip,String operatorUsername,String context,boolean isSuper) ;
	/**
	 * 验证邮箱
	 * @param ip
	 * @param operatorUsername
	 * @param code
	 * @param uri
	 */
	public void updateCheckCode(String ip, String operatorUsername, String code, String uri);
	/**
	 * 谷歌验证
	 * @param ip
	 * @param operatorUsername
	 * @param googleAuthCode
	 * @param uri
	 */
	public void updateCheckGoogleAuthCode(String ip, String operatorUsername, String googleAuthCode, String uri);
}
