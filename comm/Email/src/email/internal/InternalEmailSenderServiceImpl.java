package email.internal;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;

import email.sender.EmailServer;
import ext.Strings;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import email.sender.EmailMessage;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import kernel.util.StringUtils;
import project.syspara.SysparaService;

public class InternalEmailSenderServiceImpl implements InternalEmailSenderService {

	@Autowired
	SysparaService sysparaService;
	//private static final Log logger = LogFactory.getLog(InternalEmailSenderServiceImpl.class);
	private static org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(InternalEmailSenderServiceImpl.class);

	private JavaMailSenderImpl mailSender;
	private SimpleMailMessage mailMessage;
	private FreeMarkerConfigurer freeMarkerConfigurer;
	private int currentIndex = 0; // 当前选择的邮箱账号索引

	private String hosts;
	private String ports;
	private String usernames;
	private String passwords;
	private String froms;

	private List<String> host;
	private List<Integer> port;
	private List<String> username;
	private List<String> password;
	private List<String> from;

	@PostConstruct
	public void init(){
		this.host = Arrays.asList(hosts.split("&&"));
		this.port = Arrays.stream(ports.split("&&"))
				.map(Integer::parseInt)
				.collect(Collectors.toList());
		this.username = Arrays.asList(usernames.split("&&"));
		this.password = Arrays.asList(passwords.split("&&"));
		this.from = Arrays.asList(froms.split("&&"));
	}


	public static void main(String[] args) throws Exception {
		JavaMailSenderImpl sender = new JavaMailSenderImpl();
		System.out.println("----  准备发送 email...");
		//服务器
		sender.setHost("smtp.titan.email");
		//协议
		sender.setProtocol("smtps");//"smtps"
		//端口号
		sender.setPort(465);
		//邮箱账号
		sender.setUsername("support@e-metashop.com");
		//邮箱授权码
		sender.setPassword("Gyuws;[1o3iu2u2Feisf");
		//编码
		sender.setDefaultEncoding("Utf-8");
		Properties p = new Properties();
		p.setProperty("mail.smtp.auth", "true");
		p.setProperty("mail.smtp.starttls.enable", "true");

		sender.setJavaMailProperties(p);

		MimeMessage mailMsg = sender.createMimeMessage();
		MimeMessageHelper messageHelper = new MimeMessageHelper(mailMsg, true, "UTF-8");
		messageHelper.setTo("tongyiwzh@qq.com");// 接收邮箱
		messageHelper.setFrom("support@e-metashop.com");// 发送邮箱
		messageHelper.setSentDate(new Date());// 发送时间
		messageHelper.setSubject("测试邮件发送");// 邮件标题
		messageHelper.setText("测试邮件内容4443333311111111");// 邮件内容

		sender.send(mailMsg);// 发送
		System.out.println("----  发送 email成功");
	}

	public static void test1() throws Exception {
		JavaMailSenderImpl sender = new JavaMailSenderImpl();
		//服务器
		sender.setHost("smtp.office365.com");//"smtp.qq.com"
		//协议
		//sender.setProtocol("smtps");//"smtps"
		//端口号
		sender.setPort(587);//465
		//邮箱账号
		sender.setUsername("sendtoautotocodesendto@outlook.com");//"*********@qq.com"
		//邮箱授权码
		sender.setPassword("1FW*VDZ#eN!");
		//编码
		sender.setDefaultEncoding("Utf-8");
		Properties p = new Properties();
		//p.setProperty("mail.smtp.ssl.enable", "true");// outlook不能用
		p.setProperty("mail.smtp.auth", "true");
		p.setProperty("mail.smtp.starttls.enable", "true");
//		p.setProperty("mail.smtp.host", "smtp.office365.com");// 可免
//		p.setProperty("mail.smtp.port", "587");// 可免

		sender.setJavaMailProperties(p);

		MimeMessage mailMsg = sender.createMimeMessage();
		MimeMessageHelper messageHelper = new MimeMessageHelper(mailMsg, true, "UTF-8");
		messageHelper.setTo("tongyiwzh@qq.com");// 接收邮箱
		messageHelper.setFrom("sendtoautotocodesendto@outlook.com");// 发送邮箱
		messageHelper.setSentDate(new Date());// 发送时间
		messageHelper.setSubject("测试邮件发送");// 邮件标题
		messageHelper.setText("测试邮件内容444333333");// 邮件内容

		sender.send(mailMsg);// 发送
		System.out.println("----  发送 email成功");
	}

	@Override
	public void send(EmailMessage emailMessage) throws Exception {

////			设置发送邮件的信息
			if (currentIndex >= this.host.size()) {
				currentIndex = 0; // 重置计数器
			}
			String username = this.username.get(currentIndex).trim();
			String password = this.password.get(currentIndex).trim();
			String host = this.host.get(currentIndex).trim();
			int port = this.port.get(currentIndex);
			String from = this.from.get(currentIndex).trim();
		try {
			// 测试163邮箱ok
			Properties properties = new Properties();
			properties.put("mail.smtp.ssl.enable", "true");
			properties.put("mail.smtp.ssl.protocols", "TLSv1.2");
			properties.put("mail.smtp.starttls.enable", "true"); //TLS

			this.mailSender.setPort(port);
			this.mailSender.setUsername(username);
			this.mailSender.setPassword(password);
			this.mailSender.setHost(host);
			this.mailSender.setJavaMailProperties(properties);

			if(Objects.requireNonNull(this.mailSender.getHost()).contains("office365.com")){
				throw new RuntimeException("微软邮箱不支持基本验证发送邮箱，请选择其他邮箱");
			}

			if(Objects.requireNonNull(this.mailSender.getHost()).contains("office365.com")){
				throw new RuntimeException("微软邮箱不支持基本验证发送邮箱，请选择其他邮箱");
			}

			MimeMessage mailMsg = this.mailSender.createMimeMessage();

			MimeMessageHelper messageHelper = new MimeMessageHelper(mailMsg, true, "UTF-8");
			messageHelper.setTo(emailMessage.getTomail());// 接收邮箱
			messageHelper.setFrom(from);// 发送邮箱
			if(!Strings.isNullOrEmpty(emailMessage.getSenderName())) {
				// 使用 InternetAddress 设置发件人邮箱和显示名称
				InternetAddress fromAddress = new InternetAddress(from, emailMessage.getSenderName(), "UTF-8");
				messageHelper.setFrom(fromAddress);
			}
			messageHelper.setSentDate(new Date());// 发送时间
			messageHelper.setSubject(emailMessage.getSubject());// 邮件标题
			currentIndex++;
			// 邮件内容
			String content = "";
			if (StringUtils.isNullOrEmpty(emailMessage.getFtlname())) {
				content = emailMessage.getContent();
			} else {
				content = this.getMailText(emailMessage.getFtlname(), emailMessage.getMap());
			}
			if (content == null || content.trim().isEmpty()) {
				logger.error("向目标邮箱:" + emailMessage.getTomail() + " 发送的标题为:" + emailMessage.getSubject() + " 的邮件内容解析为空，不发送该邮件！");
				return;
			}
			messageHelper.setText(content, true);

			// true 表示启动HTML格式的邮件
			if (emailMessage.getFile() != null) {
				// 添加邮件附件
				FileSystemResource rarfile = new FileSystemResource(emailMessage.getFile());

				// addAttachment addInline 两种附件添加方式
				// 以附件的形式添加到邮件
				// 使用MimeUtility.encodeWord 解决附件名中文乱码的问题
				messageHelper.addAttachment(MimeUtility.encodeWord(emailMessage.getFilename()), rarfile);
			}

			this.mailSender.send(mailMsg);// 发送
		} catch (Throwable ex) {
			logger.error("邮件发送失败, 目标邮箱:{},\n smtp server: {} \n user: {} \n password: {}",
					emailMessage.getTomail(),
					String.format("%s:%d", host, port),
					username,
					password,
					ex);
		}

	}

	/**
	 * 获取模板并将内容输出到模板
	 * 
	 * @param ftlname
	 * @param map
	 * @return
	 */
	private String getMailText(String ftlname, Map<String, Object> map) {
		String html = "";

		try {

			// 装载模板
			Template tpl = this.freeMarkerConfigurer.getConfiguration().getTemplate(ftlname);
			// 加入map到模板中 输出对应变量
			html = FreeMarkerTemplateUtils.processTemplateIntoString(tpl, map);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (TemplateException e) {
			e.printStackTrace();
		}
		return html;
	}

	// 选择下一个邮箱账号索引
	private synchronized int getNextMailSender() {
		if (currentIndex >= this.host.size()) {
			currentIndex = 0; // 重置计数器
		}
		this.mailSender.setPort(port.get(currentIndex));
		this.mailSender.setUsername(username.get(currentIndex));
		this.mailSender.setPassword(password.get(currentIndex));
		this.mailSender.setHost(host.get(currentIndex));
		currentIndex++;
		return currentIndex++;
	}

	public void setMailSender(JavaMailSenderImpl mailSender) {
		this.mailSender = mailSender;
	}

	public void setMailMessage(SimpleMailMessage mailMessage) {
		this.mailMessage = mailMessage;
	}

	public void setFreeMarkerConfigurer(FreeMarkerConfigurer freeMarkerConfigurer) {
		this.freeMarkerConfigurer = freeMarkerConfigurer;
	}

	public void setHosts(String hosts) {
		this.hosts = hosts;
	}

	public void setPorts(String ports) {
		this.ports = ports;
	}

	public void setUsernames(String usernames) {
		this.usernames = usernames;
	}

	public void setPasswords(String passwords) {
		this.passwords = passwords;
	}

	public void setFroms(String froms) {
		this.froms = froms;
	}
}
