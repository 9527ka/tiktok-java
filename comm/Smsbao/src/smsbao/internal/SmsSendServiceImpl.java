package smsbao.internal;

import cn.hutool.core.util.StrUtil;
import smsbao.SmsSendService;
import smsbao.sender.SmsMessage;
import smsbao.sender.SmsMessageQueue;

public class SmsSendServiceImpl implements SmsSendService {

	@Override
	public void send(String mobile, String content) {
		if (StrUtil.isBlank(mobile) || mobile.trim().length() < 3) {
			System.out.println("---> SmsSendServiceImpl.send 传入的手机号信息不合规:" + mobile);;
			throw new RuntimeException("手机号信息不正确:" + mobile);
		}

		SmsMessage smsMessage = new SmsMessage(mobile, content);
		String strh = "";
		strh = mobile.substring(0, 2);
		if ("86".equals(strh)) {
			smsMessage.setInter(false);
		}

		SmsMessageQueue.add(smsMessage);
	}

}
