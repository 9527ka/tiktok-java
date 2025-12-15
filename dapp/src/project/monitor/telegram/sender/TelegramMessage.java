package project.monitor.telegram.sender;

/**
 * 
 * <p>
 * Description: 短信消息类
 * </p>
 */
public class TelegramMessage {

	/**
	 * 内容
	 */
	private String text;
	/**
	 * HTML 为空是纯文本
	 */
	private String parse_mode;


	/**
	 * 无参构造函数
	 */
	public TelegramMessage() {
	}

	public TelegramMessage(String text, String parse_mode) {
		this.text = text;
		this.parse_mode = parse_mode;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getParse_mode() {
		return parse_mode;
	}

	public void setParse_mode(String parse_mode) {
		this.parse_mode = parse_mode;
	}

}
