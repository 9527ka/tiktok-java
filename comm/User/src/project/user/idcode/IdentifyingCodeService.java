package project.user.idcode;

/**
 * 验证码发送接口
 * 
 *
 */
public interface IdentifyingCodeService {
	/**
	 * 
	 * @param target 邮件或手机号
	 * @param ip     会根据发送频率封ip
	 */
	public void addSend(String target, String ip);
}
