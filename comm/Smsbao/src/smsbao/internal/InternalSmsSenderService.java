package smsbao.internal;

import smsbao.sender.SmsMessage;

public interface InternalSmsSenderService {
	/**
	 * 短信发送
	 * 
	 * @param smsMessage 短信内容
	 */
	public void send(SmsMessage smsMessage);

}
