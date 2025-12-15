package smsbao;

/**
 * 短信发送，异步发送，写入SmsMessageQueue队列返回
 * 
 */
public interface SmsSendService {

	/**
	 * 单发短信
	 * 
	 * @param mobile   手机号
	 * @param content 短信内容
	 */
	public void send(String mobile, String content);

}
