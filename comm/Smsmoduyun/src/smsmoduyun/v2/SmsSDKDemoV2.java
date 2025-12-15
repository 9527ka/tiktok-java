package smsmoduyun.v2;

import java.util.ArrayList;
import java.util.List;

import smsmoduyun.v2.yun.SmsSingleSender;
import smsmoduyun.v2.yun.SmsSingleSenderResult;

public class SmsSDKDemoV2 {
	public static void main(String[] args) {
		try {
			// 请根据实际 accesskey 和 secretkey 进行开发，以下只作为演示 sdk 使用
			// 请根据实际 accesskey 和 secretkey 进行开发，以下只作为演示 sdk 使用
			String accesskey = "5fcb6fe98e860971d34a4239";
			String secretkey = "64760a707a1547ce988d8bb2ba0ddd4e";

			// type:0普通短信 1营销短信
			int type = 0;
			// 国家区号
			String nationcode = "86";
			// 手机号码
			String phoneNumber = "16534084800";
			// 短信签名ID (登录后台页面获取)
			String signId = "5fcb833d8e860971d34a426f";
			// 模板ID(登录后台页面获取)
			String templateId = "5a9599c66fcafe461546bb04";
			// 短信模板的变量值 ，将短信模板中的变量{0},{1}替换为参数中的值，如果短信模板中没有变量，则这个值填null
			List<String> params = new ArrayList<String>();
			// 模板中存在多个可变参数，可以添加对应的值。
			params.add("135321");

			// 自定义字段，用户可以根据自己的需要来使用
			String ext = "";

			// 初始化单发
			SmsSingleSender singleSender ;
			SmsSingleSenderResult singleSenderResult;
			// 普通单发,注意前面必须为【】符号包含，置于头或者尾部。
//			singleSenderResult = singleSender.send(type, nationcode, phoneNumber, signId, templateId, params, ext,
//					accesskey, secretkey);
//			System.out.println(singleSenderResult);
//			int a = 0;
//			if (singleSenderResult.errMsg == "OK") {
//				a = 1;
//
//			} else {
//				a = 0;
//			}
//			System.out.println("a:" + a);

		} catch (Exception e) {
			e.printStackTrace();

		}
	}
}
