package project.mall.notification.utils.notify.handler;

import email.EmailSendService;
import freemarker.core.BugException;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import kernel.util.JsonUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;
import project.mall.notification.model.Notification;
import project.mall.notification.utils.notify.request.CommonNotifyRequest;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;


public class DefaultEmailNotifyHandler extends DefaultNotifyHandler {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private EmailSendService emailSendService;
    private FreeMarkerConfigurer freeMarkerConfigurer;

    @Override
    protected Notification assembleNotifyEntity(CommonNotifyRequest request) {
        Notification notifyEntity = super.assembleNotifyEntity(request);
        notifyEntity.setTargetExtra(request.getTargetExtra());

        return notifyEntity;
    }

    public Configuration getTplConfig() {
        return this.freeMarkerConfigurer.getConfiguration();
    }

    @Override
    public Notification handle(CommonNotifyRequest request) {
        // 先存消息发送记录
        Notification notifyEntity = super.handle(request);

        String fromEmail = request.getFromExtra();
        String toEmail = notifyEntity.getTargetExtra();
        String content = notifyEntity.getContent();

        // 然后发送真实的 email 消息
        try {
            logger.info("---> DefaultEmailNotifyHandler.handle 准备向目标:{} 发送消息通知:{}", toEmail, content);
            emailSendService.sendEmail(toEmail, notifyEntity.getTitle(),"", content);
//            MimeMessage mailMsg = this.mailSender.createMimeMessage();
//
//            MimeMessageHelper messageHelper = new MimeMessageHelper(mailMsg, true, "UTF-8");
//            messageHelper.setTo(toEmail); // 接收邮箱
//            messageHelper.setFrom(fromEmail);// 发送邮箱
//            messageHelper.setSentDate(new Date());// 发送时间
//            messageHelper.setSubject(notifyEntity.getTitle());// 邮件标题
//            messageHelper.setText(message);// 邮件内容
//
//            this.mailSender.send(mailMsg);// 发送
        } catch (Exception e) {
            logger.error("向目标用户:" + notifyEntity.getTargetUserId() + " 对应的邮箱:" + toEmail + " 发送邮件内容:" + content + " 报错:", e);
        }

        return notifyEntity;
    }

    /**
     * 获取模板并将内容输出到模板
     *
     * @param ftlname
     * @param map
     * @return
     */
    public String getMailTextFromFtlFile(String ftlname, Map<String, Object> map) {
        if (ftlname == null || ftlname.trim().isEmpty()) {
            return "";
        }
        if (map == null) {
            map = new HashMap<>();
        }

        String html = "";
        try {
            // 装载模板
            Template tpl = this.freeMarkerConfigurer.getConfiguration().getTemplate(ftlname);
            // 加入map到模板中 输出对应变量
            html = FreeMarkerTemplateUtils.processTemplateIntoString(tpl, map);
        } catch (IOException e) {
            logger.error("解析 email 模板文件:" + ftlname + " 时报错", e);
        } catch (TemplateException e) {
            logger.error("解析 email 模板文件:" + ftlname + " 时报错", e);
        }

        return html;
    }

    /**
     * 获取模板并将内容输出到模板
     *
     * @param oriContent
     * @param map
     * @return
     */
    public String getMailTextFromContent(String oriContent, Map<String, Object> map) {
        if (oriContent == null || oriContent.trim().isEmpty()) {
            return "";
        }

        String html = "";
        try {
            // 装载模板
            Template tpl = null;
            Reader reader = new StringReader(oriContent);
            try {
                tpl = new Template("", new StringReader(oriContent), getTplConfig());
                tpl.setEncoding("UTF-8");
            } catch (IOException e) {
                throw new BugException("Plain text template creation failed", e);
            } finally {
                reader.close();
            }

            // 加入map到模板中 输出对应变量
            StringWriter out = new StringWriter(1024);
            tpl.process(map, out);
            html = out.toString();

            out.close();
        } catch (IOException e) {
            logger.error("解析 email 模板内容:" + oriContent + " 时报错", e);
        } catch (TemplateException e) {
            logger.error("解析 email 模板内容:" + oriContent + " 时报错", e);
        }

        return html;
    }

//    public void setMailSender(JavaMailSenderImpl mailSender) {
//        this.mailSender = mailSender;
//    }


    public void setEmailSendService(EmailSendService emailSendService) {
        this.emailSendService = emailSendService;
    }

    public void setFreeMarkerConfigurer(FreeMarkerConfigurer freeMarkerConfigurer) {
        this.freeMarkerConfigurer = freeMarkerConfigurer;
    }
}
