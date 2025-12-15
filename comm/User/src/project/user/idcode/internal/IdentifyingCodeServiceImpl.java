package project.user.idcode.internal;

import java.text.MessageFormat;
import java.util.Date;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import ext.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mysql.cj.util.StringUtils;

import email.EmailSendService;
import project.log.CodeLog;
import project.log.CodeLogService;
import project.syspara.SysParaCode;
import project.syspara.Syspara;
import project.syspara.SysparaService;
import project.user.idcode.BlacklistIpTimeWindow;
import project.user.idcode.IdentifyingCodeService;
import project.user.idcode.IdentifyingCodeTimeWindowService;
import project.user.idcode.SendCountTimeWindow;
import smsbao.SmsSendService;

public class IdentifyingCodeServiceImpl implements IdentifyingCodeService {
	private Logger log = LoggerFactory.getLogger(IdentifyingCodeServiceImpl.class);
	private SmsSendService smsSendService;
	private EmailSendService emailSendService;
	private IdentifyingCodeTimeWindowService identifyingCodeTimeWindowService;
	private SendCountTimeWindow sendCountTimeWindow;
	private BlacklistIpTimeWindow blacklistIpTimeWindow;
	private SysparaService sysparaService;
	private CodeLogService codeLogService;

	private Map<String, Integer> ipCache = new ConcurrentHashMap<String, Integer>();

	@Override
	public void addSend(String target, String ip) {
		String code = null;
		if (chcekIp(ip)) {// 被封的ip直接返回 不操作
			return;
		}
		/**
		 * 短信发送签名
		 */
//		String smsbao_sign = sysparaService.find("smsbao_sign").getValue();
		
		// 短信发送文本[TEST]code is ：{0}
		String send_code_text = "";
		String title = "";
		if (target.indexOf("@") == -1) {
			// 短信
			Syspara contentParam = this.sysparaService.find(SysParaCode.SEND_CODE_TEXT.getCode());
			if (contentParam == null || StringUtils.isNullOrEmpty(contentParam.getValue())) {
				log.error("send_code_text 未配置");
				return;
			}
			send_code_text = contentParam.getValue();
		} else {
			// 邮件
			Syspara contentParam = this.sysparaService.find(SysParaCode.SEND_EMAIL_CODE_TEXT.getCode());
			Syspara titleParam = this.sysparaService.find(SysParaCode.SEND_EMAIL_CODE_TITLE.getCode());
			if (contentParam == null || StringUtils.isNullOrEmpty(contentParam.getValue())) {
				log.error("send_email_code_text 未配置");
				return;
			}
			send_code_text = contentParam.getValue();
			if (titleParam == null || StringUtils.isNullOrEmpty(titleParam.getValue())) {
				log.error("send_email_code_title 未配置");
				title = "Email captchaCode";
			} else {
				title = titleParam.getValue();
			}
		}

		String mailSenderName = "";
		// 替换Shop2U
		String mallSiteName = sysparaService.findString("mall_site_name");
		if (!Strings.isNullOrEmpty(mallSiteName)) {
			send_code_text = send_code_text.replace("Shop2U", mallSiteName);
			mailSenderName = mallSiteName;
		}


		/**
		 * 是否每次发送的code都不一样
		 */
		boolean send_code_always_new = this.sysparaService.find("send_code_always_new").getBoolean();

		Object object = this.identifyingCodeTimeWindowService.getAuthCode(target);
		if (object == null || send_code_always_new) {
			Random random = new Random();
			code = String.valueOf(random.nextInt(999999) % 900000 + 100000);
		} else {
			code = String.valueOf(object);
		}
//		log.info(MessageFormat.format("target:{0},code:{1},ip:{2}", target, code, ip));

		String content = MessageFormat.format(send_code_text, new Object[] { code });
		if (target.indexOf("@") == -1) {
			/**
			 * 发送的短信接口类型 tiantian---天天---smsSendService--->>>>--
			 * moduyun---摩杜云---smsSingleSender
			 */
			String send_code_type = this.sysparaService.find("send_code_type").getValue();
			if (StringUtils.isNullOrEmpty(send_code_type)) {
				log.error("send_code_type 未配置");
				return;
			}
			
			if ("tiantian".equals(send_code_type)) {
				smsSendService.send(target, content);
				log.info(MessageFormat.format("tiangtian--target:{0},code:{1},ip:{2}", target, code, ip));
			} 
			
//			else if ("moduyun".equals(send_code_type)) {
//				// -- 摩杜云短信签名的Id--accesskey,secretkey,signId,templateId
//				String send_code_moduyun = this.sysparaService.find("send_code_moduyun").getValue();
//				String[] send_code_moduyun_parts = send_code_moduyun.split(",");
//				List<String> params = new ArrayList<String>();
//				params.add(code);
//				String strh_code = "";
//				strh_code = target.substring(0, 2);
//				if ("86".equals(strh_code)) {
//					try {
//						smsSingleSender.send(0, "86", target.substring(2, target.length()), send_code_moduyun_parts[2],
//								send_code_moduyun_parts[3], params, "", send_code_moduyun_parts[0],
//								send_code_moduyun_parts[1]);
//						log.info(MessageFormat.format("moduyun--target:{0},code:{1},ip:{2}", target, code, ip));
//					} catch (Exception e) {
//					}
//				}
//			}
			 else if ("smsbao".equals(send_code_type)) {
					smsSendService.send(target, content);
					log.info(MessageFormat.format("smsbao--target:{0},code:{1},ip:{2}", target, code, ip));
			 }
		} else {
			/**
			 * 邮件
			 */
			emailSendService.sendEmail(target, title,mailSenderName, content);
			log.info(MessageFormat.format("email--target:{0},code:{1},ip:{2}", target, code, ip));
		}
		
		this.identifyingCodeTimeWindowService.putAuthCode(target, code);
		System.out.println("获取验证码：" + target + "---" + code);
		CodeLog codeLog = new CodeLog();
		codeLog.setTarget(target);
		codeLog.setLog("发送地址：" + target + ",验证码：" + code + ",ip地址：" + ip);
		codeLog.setCreateTime(new Date());
		codeLogService.saveSync(codeLog);
	}

	/**
	 * 返回true:ip已被封， false：ip正常
	 * 
	 * @param ip
	 * @return
	 */
	private boolean chcekIp(String ip) {
		String check_send_count = sysparaService.find("send_code_check_ip").getValue();
		if (!"true".equals(check_send_count))
			return false;// 不为1时 未开启，直接返回false不做处理
		if (blacklistIpTimeWindow.getBlackIp(ip) != null)
			return true;// ip被封，不发送

		if (sendCountTimeWindow.getIpSend(ip) != null) {
			Integer count = ipCache.get(ip);
			count++;
			if (count >= 30) {// 从ip发送第一条开始
				blacklistIpTimeWindow.putBlackIp(ip, ip);
				ipCache.remove(ip);
				sendCountTimeWindow.delIpSend(ip);
				return true;
			} else {
				ipCache.put(ip, count++);
			}

		} else {
			ipCache.put(ip, 1);
			sendCountTimeWindow.putIpSend(ip, ip);
		}
		return false;

	}

	public void setSmsSendService(SmsSendService smsSendService) {
		this.smsSendService = smsSendService;
	}

	public void setEmailSendService(EmailSendService emailSendService) {
		this.emailSendService = emailSendService;
	}

//	public void setIdentifyingCodeTimeWindow(IdentifyingCodeTimeWindow identifyingCodeTimeWindow) {
//		this.identifyingCodeTimeWindow = identifyingCodeTimeWindow;
//	}

	public void setSysparaService(SysparaService sysparaService) {
		this.sysparaService = sysparaService;
	}

	public void setSendCountTimeWindow(SendCountTimeWindow sendCountTimeWindow) {
		this.sendCountTimeWindow = sendCountTimeWindow;
	}

	public void setBlacklistIpTimeWindow(BlacklistIpTimeWindow blacklistIpTimeWindow) {
		this.blacklistIpTimeWindow = blacklistIpTimeWindow;
	}

	public void setIdentifyingCodeTimeWindowService(IdentifyingCodeTimeWindowService identifyingCodeTimeWindowService) {
		this.identifyingCodeTimeWindowService = identifyingCodeTimeWindowService;
	}

	public void setCodeLogService(CodeLogService codeLogService) {
		this.codeLogService = codeLogService;
	}
}
