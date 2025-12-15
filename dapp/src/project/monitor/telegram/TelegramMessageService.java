package project.monitor.telegram;

public interface TelegramMessageService {

	/**
	 * HTML text
	 * @param text
	 */
	public void send(String text);
	
	/**
	 * 
	 * Telegram消息发送，token和chat_id配置在系统参数里，支持固定机器人的特定群消息推送
	 * 
	 * @param text       内容
	 * @param parse_mode HTML 为空是纯文本
	 */
	public void send(String text, String parse_mode);
	
	

}
