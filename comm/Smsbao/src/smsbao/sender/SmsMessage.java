package smsbao.sender;

/**
 * 
 * <p>
 * Description: 短信消息类
 * </p>
 */
public class SmsMessage {
	/**
	 * 要发送的电话号码
	 */
	private String mobile;

	/**
	 * 要发送的短信内容
	 */
	private String content;

	// 是否是国外（中国大陆外）手机号
	private boolean inter = true;

	/**
	 * 无参构造函数
	 */
	public SmsMessage() {
	}

	/**
	 * 构造函数
	 * 
	 * @param phones  电话号码
	 * @param content 短信内容
	 */
	public SmsMessage(String mobile, String content) {
		this.mobile = mobile;
		this.content = content;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public boolean getInter() {
		return inter;
	}

	public void setInter(boolean inter) {
		this.inter = inter;
	}

}
