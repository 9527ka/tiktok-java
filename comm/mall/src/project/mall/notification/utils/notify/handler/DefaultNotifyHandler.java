package project.mall.notification.utils.notify.handler;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSONArray;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import project.mall.notification.NotificationService;
import project.mall.notification.NotificationTemplateService;
import project.mall.notification.constant.VarPlaceHolderTypeEnum;
import project.mall.notification.model.ContentPlaceHolder;
import project.mall.notification.model.Notification;
import project.mall.notification.model.NotificationTemplate;
import project.mall.notification.utils.notify.model.ContentPlaceHolderVO;
import project.mall.notification.utils.notify.request.CommonNotifyRequest;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 发送消息通知的历史记录存储处理器，无论是短信、email还是纯站内信，都可以通过本方法将消息发送记录存入数据库。
 *
 */
@Service
public class DefaultNotifyHandler {
    protected org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(this.getClass());

    private NotificationService notificationService;
    private NotificationTemplateService notificationTemplateService;

    // 基于下标占位符的消息模板识别
    public static final String INDEX_VAR_HOLDER_REGEX = "(\\{\\s*(\\d+)\\s*\\})";
    public static final Pattern indexVarHolderPattern = Pattern.compile(INDEX_VAR_HOLDER_REGEX);

    // 基于变量名称占位符的消息模板识别
    public static final String CODE_BASE_VAR_HOLDER_REGEX = "(\\{\\s*(\\w+)\\s*\\})";
    public static final Pattern codeBaseVarHolderPattern = Pattern.compile(CODE_BASE_VAR_HOLDER_REGEX);

    public static void main(String[] args) {
        Pattern indexVarHolderPattern = Pattern.compile("(\\d+)(\\s)*(\\d+)");
        String script = "861";
        Matcher matcher1 = indexVarHolderPattern.matcher(script);
        if (matcher1.find()) {
            System.out.println("---> find, 1:" + matcher1.group(1) + ", 2:" + matcher1.group(3));
        } else {
            System.out.println("not find");
        }

//        Pattern indexVarHolderPattern = Pattern.compile("<.*script.*>");
//        String script = "<script src=>";
//        Matcher matcher1 = indexVarHolderPattern.matcher(script);
//        if (matcher1.find()) {
//            System.out.println("---> find");
//        } else {
//            System.out.println("not find");
//        }

//        String str = "你好:{  1},扎不违反:{1 }, sss{2     }";
//        Matcher matcher1 = indexVarHolderPattern.matcher(str);
//        while (matcher1.find()) {
//            String s1 = matcher1.group(1);
//            String idx = matcher1.group(2);
//            System.out.println("---> s1:" + s1 + ", idx:" + idx);
//        }
//
////        str = "你好:{ code1   },扎不违反:{ code1  }, sss{     code2}";
////        Matcher matcher2 = codeBaseVarHolderPattern.matcher(str);
////        while (matcher2.find()) {
////            String s1 = matcher2.group(1);
////            String idx = matcher2.group(2);
////            System.out.println("---> s1:" + s1 + ", idx:" + idx);
////        }
//
//        str = str.replaceAll("\\{\\s*1\\s*\\}", "下标1的值");
//        str = str.replaceAll("\\{\\s*2\\s*\\}", "下标2的值");
//        System.out.println("---> str:" + str);
//
////        String str = "{\"name\":'hellowzh\",\"orderNo\":\"no-1000\"}";
////        Map map = JSON.parseObject(str, Map.class);
////        System.out.println("---> map:" + map);
    }

    public void setNotificationService(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    public void setNotificationTemplateService(NotificationTemplateService notificationTemplateService) {
        this.notificationTemplateService = notificationTemplateService;
    }

    /**
     * 将模板消息中的占位符给提取出来
     * 注意：
     *     鉴于目前是简单的消息模板创建模式，本方法不能解析占位符的详细信息，只能使用保守属性值。
     *     当占位符类似：{1}, {2} 这种数字型的时候，认为是下标占位符，
     *     当占位符类似: {v1}, {code} 这种字符串类型的时候，认为是参数占位符；
     *     下标占位符约定从：1 开始
     *
     * @param templateContent
     * @return
     */
    public List<ContentPlaceHolder> parseTemplate(String templateContent) {
        if (templateContent == null || templateContent.trim().isEmpty()) {
            return null;
        }

        List<ContentPlaceHolder> varHolderList = new ArrayList();
        Matcher matcher1 = indexVarHolderPattern.matcher(templateContent);
        while (matcher1.find()) {
            String idxValue = matcher1.group(2);
            try {
                int idx = Integer.parseInt(idxValue);
                ContentPlaceHolder hodler = new ContentPlaceHolder();
                hodler.setCode("0");
                hodler.setIndex(idx);
                hodler.setMax(0);
                hodler.setMin(0);
                hodler.setVarType(VarPlaceHolderTypeEnum.THE_TXT.getCode());

                varHolderList.add(hodler);
            } catch (Exception e) {
                break;
            }
        }

        Matcher matcher2 = codeBaseVarHolderPattern.matcher(templateContent);
        while (matcher2.find()) {
            String codeValue = matcher2.group(2);
            try {
                Integer.parseInt(codeValue);
                // 如果是下标类型占位符，就不再处理直接跳出
                break;
            } catch (Exception e) {
            }

            ContentPlaceHolder hodler = new ContentPlaceHolder();
            hodler.setCode(codeValue);
            hodler.setIndex(0);
            hodler.setMax(0);
            hodler.setMin(0);
            hodler.setVarType(VarPlaceHolderTypeEnum.THE_TXT.getCode());

            varHolderList.add(hodler);
        }

        return varHolderList;
    }

    /**
     * 对消息内容做最后一次占位符的替换，产生最终消息内容
     *
     * @param request
     * @param messageContent
     * @return
     */
    public String replaceVarHolder(CommonNotifyRequest request, String messageContent) {
        List<ContentPlaceHolderVO> varList = request.getContentVarList();
        if (varList == null || varList.isEmpty()) {
            return messageContent;
        }

        for (ContentPlaceHolderVO oneVar : varList) {
            String varHolder = "";
            if (oneVar.getIndex() > 0) {
                // 通过下标方式替换内容
                varHolder = "\\{\\s*" + oneVar.getIndex() + "\\s*\\}";
            } else {
                // 通过变量名称方式替换占位符
                varHolder = "\\{\\s*" + oneVar.getCode() + "\\s*\\}";
            }

            if (oneVar.getVarType() == VarPlaceHolderTypeEnum.CURRENT_TIME.getCode()) {
                if (oneVar.getFormat() == null || oneVar.getFormat().trim().isEmpty()) {
                    // 不用特殊格式化
                    String timeStr = DateUtil.formatDateTime(new Date());
                    oneVar.setValue(timeStr);
                    messageContent = messageContent.replaceAll(varHolder, timeStr);
                } else {
                    // 特殊格式化 TODO
                    String timeStr = DateUtil.formatDateTime(new Date());
                    oneVar.setValue(timeStr);
                    messageContent = messageContent.replaceAll(varHolder, timeStr);
                }
            } else if (oneVar.getVarType() == VarPlaceHolderTypeEnum.THE_TIME.getCode()) {
                String timeStr = DateUtil.formatDateTime((Date)oneVar.getValue());
                oneVar.setValue(timeStr);
                messageContent = messageContent.replaceAll(varHolder, timeStr);
            } else if (oneVar.getVarType() == VarPlaceHolderTypeEnum.RANDOM_CAPTCH_CODE.getCode()) {
                // TODO
                if (oneVar.getMax() > 0) {

                } else {

                }
                String varValue = "";
                if (oneVar.getValue() != null && !oneVar.getValue().toString().trim().isEmpty()) {
                    varValue = oneVar.getValue().toString().trim();
                } else {
                    // TODO
                    varValue = "12345";
                }
                oneVar.setValue(varValue);
                messageContent = messageContent.replaceAll(varHolder, varValue);
            } else {
                messageContent = messageContent.replaceAll(varHolder, String.valueOf(oneVar.getValue()));
            }
        }

        return messageContent;
    }

    /**
     * 业务子类可以基于本方法完善额外信息的填充
     *
     * @param request
     * @return
     */
    protected Notification assembleNotifyEntity(CommonNotifyRequest request) {
        Date now = new Date();

        NotificationTemplate template = notificationTemplateService.getTemplateByBizType(request.getBizType(), request.getLanguageType());
        if (template == null) {

        }

        Notification notificationEntity = new Notification();
        notificationEntity.setSendTime(now);
        notificationEntity.setLocation(now.getTime());
        notificationEntity.setReserveSendTime(now);
        notificationEntity.setBizType(request.getBizType());
        notificationEntity.setHandler(template.getHandler());
        notificationEntity.setTitle(request.getMessageTitle());
        notificationEntity.setContent(request.getMessageContent());
        notificationEntity.setFromUserId(request.getFromUserId());
        notificationEntity.setTargetUserId(request.getTargetUserId());
        if (request.getTargetUserId() == null || request.getTargetUserId().isEmpty()) {
            notificationEntity.setTargetUserId("0");
        }
        notificationEntity.setTargetTopic(request.getTargetTopic());
        if (request.getTargetTopic() == null || request.getTargetTopic().isEmpty()) {
            notificationEntity.setTargetTopic("0");
        }
        notificationEntity.setLanguage(request.getLanguageType());
        notificationEntity.setTemplateCode(template.getTemplateCode());
//        notificationEntity.setTemplateGroupKey(template.getTemplateGroupKey());
        notificationEntity.setModule(template.getModule());
        notificationEntity.setLink("");
        notificationEntity.setRefId(StringUtils.isBlank(request.getRefId()) ? "0" : request.getRefId());
        notificationEntity.setRefType(request.getRefType());
        notificationEntity.setType(template.getType());
        notificationEntity.setStatus(1);
        notificationEntity.setLink(request.getLink());

        notificationEntity.setVarInfo(JSONArray.toJSON(request.getContentVarList()).toString());

        return notificationEntity;
    }

    /**
     * 正式发送站内消息
     *
     * @param request
     */
    public Notification handle(CommonNotifyRequest request) {
        Notification notificationEntity = assembleNotifyEntity(request);

        notificationService.save(notificationEntity);
        return notificationEntity;
    }
}
