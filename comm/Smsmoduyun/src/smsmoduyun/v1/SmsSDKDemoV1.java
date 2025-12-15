package smsmoduyun.v1;

import smsmoduyun.v1.yun.SmsSingleSender;
import smsmoduyun.v1.yun.SmsSingleSenderResult;

/**
 * 发送短信
 */
public class SmsSDKDemoV1 {
	public static void main(String[] args) {
		try {
			// 请根据实际 accesskey 和 secretkey 进行开发，以下只作为演示 sdk 使用
			// 请根据实际 accesskey 和 secretkey 进行开发，以下只作为演示 sdk 使用
			String accesskey = "xxx";
			String secretkey = "xxxxx";
			// 手机号码
			String phoneNumber = "xxxxxxx";
			int type = 0;
			// 初始化单发
			SmsSingleSender singleSender = new SmsSingleSender(accesskey, secretkey);
			SmsSingleSenderResult singleSenderResult;
			String rangdom = "xxxxxxxxxx"; // 你的随机数
			String msg = "【xxx】680956是您的验证码，2分钟内有效。（请勿向任何人泄露您收到的验证码）";

			int a = 0;
			// 普通单发,注意前面必须为【】符号包含，置于头或者尾部。
			singleSenderResult = singleSender.send(type, "86", phoneNumber, msg, "", "");
			System.out.println(singleSenderResult);

		} catch (Exception e) {
			e.printStackTrace();

		}
	}
}
