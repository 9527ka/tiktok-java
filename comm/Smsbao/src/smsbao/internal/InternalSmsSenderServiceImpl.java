package smsbao.internal;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import kernel.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import project.log.SysLogService;
import project.syspara.SysparaService;
import smsbao.exception.InvalidMobileException;
import smsbao.exception.InvalidSmsContentException;
import smsbao.sender.SmsMessage;

public class InternalSmsSenderServiceImpl implements InternalSmsSenderService {
	private Logger log = LoggerFactory.getLogger(InternalSmsSenderServiceImpl.class);
	private SysparaService sysparaService;

	private SysLogService sysLogService;

	private static final Pattern MobileFetchPattern = Pattern.compile("(\\d+)(\\s)*(\\d+)");


	@Override
	public void send(SmsMessage smsMessage) {
		// 目的号码，注意：拼接的号码之间可能有空格，需要剔除
		String dest = smsMessage.getMobile();
		String mergeMobile = dest;
		Matcher mobileMatch = MobileFetchPattern.matcher(dest);
		if (mobileMatch.find()) {
			mergeMobile = mobileMatch.group(1) + mobileMatch.group(3);
		}

		/**
		 * 发送的短信接口类型 tiantian---天天---smsSendService--->>>>--
		 * moduyun---摩杜云---smsSingleSender
		 */
		String send_code_type = this.sysparaService.find("send_code_type").getValue();

		if ("tiantian".equals(send_code_type)) {
			// 用户名
			String user = sysparaService.find("smsbao_u").getValue();

			// 密码：
			String pwd = sysparaService.find("smsbao_p").getValue();

			String ip = "";
			String sendResult = "";
			if (smsMessage.getInter()) {
				ip = "m.isms360.com";
				int port = 8085;
				HttpClientUtil util = new HttpClientUtil(ip, port, "/mt/MT3.ashx");
				String ServiceID = "SEND";

				// 原号码
				String sender = "";

				// 短信内容
				String msg = smsMessage.getContent();

				// UTF-16BE
				String hex = WebNetEncode.encodeHexStr(8, msg);
				hex = hex.trim() + "&codec=8";
				sendResult = util.sendPostMessage(user, pwd, ServiceID, mergeMobile, sender, hex);
				log.info("tiantian--" + mergeMobile + ",短信内容：" + smsMessage.getContent() + "--验证码发送返回信息 = "
						+ sendResult);
				System.out.println("验证码发送返回信息 = " + sendResult);
			} else {
				ip = "m.isms360.com";
				int port = 8085;
				HttpClientUtil util = new HttpClientUtil(ip, port, "/mt/MT3.ashx");
				String ServiceID = "SEND";
				// 原号码
				String sender = "";

				// 短信内容
				String msg = smsMessage.getContent();

				// UTF-16BE
				String hex = WebNetEncode.encodeHexStr(8, msg);
				hex = hex.trim() + "&codec=8";

				sendResult = util.sendPostMessage(user, pwd, ServiceID, mergeMobile, sender, hex);
				log.info("tiantian--" + mergeMobile + ",短信内容：" + smsMessage.getContent() + "--验证码发送返回信息 = "
						+ sendResult);
				System.out.println("验证码发送返回信息 = " + sendResult);
			}

			// 错误码规范：http://www.isms360.com/channel.aspx?id=25
			if (StringUtils.isNullOrEmpty(sendResult)) {
				return;
			} else if (!sendResult.trim().startsWith("-")) {
				// 不是错误码
				return;
			} else if (sendResult.trim().equals("-4")) {
				// 目的号码运营商不在服务覆盖范围
				throw new InvalidMobileException();
			} else if (sendResult.trim().equals("-10")) {
				// DEST参数格式错误
				throw new InvalidMobileException();
			} else if (sendResult.trim().equals("-15")) {
				// 非法手机号码，手机号码格式不对
				throw new InvalidMobileException();
			} else if (sendResult.trim().equals("-18")) {
				// 目的手机号码限制
				throw new InvalidMobileException();
			} else if (sendResult.trim().equals("-16")) {
				// 短信内容超长！（UNICODE最大70个字符，Alphabet编码（英文即以此方式传输）最大160字符）
				throw new InvalidSmsContentException();
			} else if (sendResult.trim().equals("-17")) {
				// 短信内容含有非法字符
				throw new InvalidSmsContentException();
			} else if (sendResult.trim().equals("-19")) {
				// 短信内容编码不对（比如发中文、韩文、日文而用Alphabet编码方式）
				throw new InvalidSmsContentException();
			}
		} else if ("smsbao".equals(send_code_type)) {
			String username = sysparaService.find("smsbao_u").getValue(); // 在短信宝注册的用户名
			String password = sysparaService.find("smsbao_p").getValue(); // 在短信宝注册的密码
			String httpUrl = null;
			if (smsMessage.getInter()) {
				// 国际
				httpUrl = "http://api.smsbao.com/wsms";

			} else {
				httpUrl = "http://api.smsbao.com/sms";
			}

			StringBuffer httpArg = new StringBuffer();
			httpArg.append("u=").append(username).append("&");
			httpArg.append("p=").append(md5(password)).append("&");

			if (smsMessage.getInter()) {
				// 国际
				httpArg.append("m=").append(encodeUrlString("+", "UTF-8") + mergeMobile).append("&");
			} else {
				httpArg.append("m=").append(mergeMobile.substring(2, mergeMobile.length()))
						.append("&");
			}
			httpArg.append("c=").append(encodeUrlString(smsMessage.getContent(), "UTF-8"));

			String result = request(httpUrl, httpArg.toString());

			if (!"0".equals(result)) {
				log.info("Smsbao--" + mergeMobile + ",短信内容：" + smsMessage.getContent() + "--验证码发送失败 ");

			} else {
				log.info("Smsbao--" + mergeMobile + ",短信内容：" + smsMessage.getContent() + "--验证码发送成功 ");
			}
		}
	}

	public static void main(String[] args) {
		String httpUrl = "http://api.smsbao.com/wsms";
		// 区域编号+手机号：66 939462175
		String mobile = "662939462175";
		String message = "手机号没空格，能收到短信吗";
		String username = "duanxin19";
		String password = "#ynwt|1u6Ngw";

//		StringBuffer httpArg = new StringBuffer();
//		httpArg.append("u=").append("duanxin19").append("&");
//		httpArg.append("p=").append(md5("#ynwt|1u6Ngw")).append("&");
//		httpArg.append("m=").append(encodeUrlString("+", "UTF-8") + mobile).append("&");
//		httpArg.append("c=").append(encodeUrlString(message, "UTF-8"));
//		String result = request(httpUrl, httpArg.toString());
//		System.out.println("==========> result = " + result);

//		String str = "test++test!"; // 746573742B2B7465737421
//		str = "测试++！"; // 6D4B8BD5002B002BFF01
//		String hex = WebNetEncode.encodeHexStr(8, str);
//		System.out.println("---> hex:" + hex); // 746573742B2B7465737421

		String ip = "m.isms360.com";
		int port = 8085;
		HttpClientUtil util = new HttpClientUtil(ip, port, "/mt/MT3.ashx");
		String ServiceID = "SEND";
		// UTF-16BE
		String hex = WebNetEncode.encodeHexStr(8, message);
		hex = hex.trim() + "&codec=8";
		String sendResult = util.sendPostMessage(username, password, ServiceID, mobile, "", hex);
		System.out.println("验证码发送返回信息 = " + sendResult);
	}

	public static String request(String httpUrl, String httpArg) {
		BufferedReader reader = null;
		String result = null;
		StringBuffer sbf = new StringBuffer();
		httpUrl = httpUrl + "?" + httpArg;
		System.out.println("=====> 短信发送完整请求地址：" + httpUrl);
		try {
			URL url = new URL(httpUrl);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			connection.connect();
			InputStream is = connection.getInputStream();
			reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			String strRead = reader.readLine();
			if (strRead != null) {
				sbf.append(strRead);
				while ((strRead = reader.readLine()) != null) {
					sbf.append("\n");
					sbf.append(strRead);
				}
			}
			reader.close();
			result = sbf.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public static String md5(String plainText) {
		StringBuffer buf = null;
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(plainText.getBytes());
			byte b[] = md.digest();
			int i;
			buf = new StringBuffer("");
			for (int offset = 0; offset < b.length; offset++) {
				i = b[offset];
				if (i < 0)
					i += 256;
				if (i < 16)
					buf.append("0");
				buf.append(Integer.toHexString(i));
			}
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return buf.toString();
	}

	public static String encodeUrlString(String str, String charset) {
		String strret = null;
		if (str == null)
			return str;
		try {
			strret = java.net.URLEncoder.encode(str, charset);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return strret;
	}

	public void setSysparaService(SysparaService sysparaService) {
		this.sysparaService = sysparaService;
	}

	public void setSysLogService(SysLogService sysLogService) {
		this.sysLogService = sysLogService;
	}

}
