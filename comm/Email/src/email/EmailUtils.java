package email;

import java.util.Properties;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;


public class EmailUtils {

	private static JavaMailSenderImpl javaMailSender;

	private static final String userName = "1231222222221@163.com";

	private static final String password = "bbbbbbaaaaa";

	private static final String host = "smtp.163.com";

	private static final int port = 25;

	// 定义收件人列表
	private static final String[] revicedUserName = { "122322222221@qq.com" };

	static {
		javaMailSender = new JavaMailSenderImpl();
		javaMailSender.setHost(host);// 链接服务器
		javaMailSender.setPort(port);
		javaMailSender.setUsername(userName);// 账号
		javaMailSender.setPassword(password);// 密码
		javaMailSender.setDefaultEncoding("UTF-8");

		Properties properties = new Properties();
		properties.setProperty("mail.smtp.auth", "true");// 开启认证
		properties.setProperty("mail.debug", "true");// 启用调试
		properties.setProperty("mail.smtp.timeout", "10000");// 设置链接超时
		properties.setProperty("mail.smtp.port", Integer.toString(port));// 设置端口
		javaMailSender.setJavaMailProperties(properties);
	}

	/***
	 * 发送项目异常 代码提醒
	 * 
	 * @param msg
	 */
	public static void sendEmail(final String msg) {
		// 开启线程异步发送 防止发送请求时间过长
		new Thread(new Runnable() {
			@Override
			public void run() {
				if (revicedUserName != null && revicedUserName.length > 0) {
					SimpleMailMessage mailMessage = new SimpleMailMessage();
					mailMessage.setFrom(userName);
					mailMessage.setSubject("====后台管理项目异常====");
					mailMessage.setText(msg);
					mailMessage.setTo(revicedUserName);
					// 发送邮件
					javaMailSender.send(mailMessage);
				}
			}
		}).start();
	}

	public static void main(String[] args) {
		sendEmail("代码开始抽风报警了------");
	}
}