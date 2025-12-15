package project.mall.notification.utils.notify.request;

import kernel.util.ObjectTools;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;
import project.mall.notification.utils.notify.CommonNotifyManager;
import project.mall.notification.utils.notify.handler.DefaultEmailNotifyHandler;

import java.util.HashMap;
import java.util.Map;

public abstract class BaseNotifyRequest implements java.io.Serializable {
    // 参考枚举类型：NotificationBizTypeEnum
    private String bizType;

    private String languageType;

    /**
     * 针对固定消息模板对应的业务，此处不需要填值，组件会通过消息模板组装最终的消息内容；
     * 针对站内信、email 类型的动态自定义消息，如果填写了该字段值，则使用该字段值代表的
     * 临时消息模板内容取代之前配置的静态模板内容，组装动态消息。
     */
    private String customeMessage;
    private String customeTitle;

    private String fromUserId;

    private String targetUserId;

    private String targetTopic;

    /**
     * 非必填字段，有些特殊业务如果需要，可以填充该值方便维护
     * 1 - MallOrdersPrize 记录
     * 2 - OnlineChatUserMessage 记录
     */
    private int refType;

    private String refId;

    /**
     * 业务子类根据对应的业务特征，生成标准的 CommonNotifyRequest 对象。
     * 可以在此处调用 CommonNotifyRequest 的 setValue 方法初始化占位符信息
     *
     * @return
     */
    public abstract CommonNotifyRequest buildRequest();

    /**
     * 返回所有的业务数据，可用于支持基于 freemaker 的动态变量填充
     *
     * @return
     */
    protected Map<String, Object> listBizAttrbuteValue() {
        Map<String, Object> bizAttrDataMap = ObjectTools.beanToMap(this);
        bizAttrDataMap.remove("bizType");
        bizAttrDataMap.remove("languageType");
        bizAttrDataMap.remove("customeMessage");
        bizAttrDataMap.remove("customeTitle");
        bizAttrDataMap.remove("fromUserId");
        bizAttrDataMap.remove("targetUserId");
        bizAttrDataMap.remove("targetTopic");

        return bizAttrDataMap;
    }

    /**
     * 初始化消息内容并返回，可以在此方法中更新原始的 messageContent 内容。
     * 例如消息模板中如果存在 freemaker 的语法做复杂的内容组装，可以在此处做特殊的处理
     *
     * @return
     */
    public String initContent(String templateContent) {
        if (templateContent == null || templateContent.trim().isEmpty()) {
            return "";
        }
        templateContent = templateContent.trim();

        //WebApplicationContext wac = ContextLoader.getCurrentWebApplicationContext();
        //String handlerSimpleName = DefaultEmailNotifyHandler.class.getSimpleName();
        //handlerSimpleName = handlerSimpleName.substring(0, 1).toLowerCase() + handlerSimpleName.substring(1);
        if (this instanceof DefaultEmailNotifyRequest) {
            // 替换掉邮件模板中的 freemaker 相关的动态变量
            Map<String, Object> bizDataMap = listBizAttrbuteValue();

            DefaultEmailNotifyHandler handler = CommonNotifyManager.getInstance().getNotifyBean(DefaultEmailNotifyHandler.class);
            if (templateContent.startsWith("ftlFilePath:")) {
                String ftlFilePath = templateContent.substring("ftlFilePath:".length()).trim();
                return handler.getMailTextFromFtlFile(ftlFilePath, bizDataMap);
            } else {
                return handler.getMailTextFromContent(templateContent, bizDataMap);
            }
        }

        return templateContent;
    }

    public String getBizType() {
        return bizType;
    }

    public void setBizType(String bizType) {
        this.bizType = bizType;
    }

    public String getLanguageType() {
        return languageType;
    }

    public void setLanguageType(String languageType) {
        this.languageType = languageType;
    }

    public String getCustomeTitle() {
        return customeTitle;
    }

    public void setCustomeTitle(String customeTitle) {
        this.customeTitle = customeTitle;
    }

    public String getCustomeMessage() {
        return customeMessage;
    }

    public void setCustomeMessage(String customeMessageContent) {
        this.customeMessage = customeMessageContent;
    }

    public String getFromUserId() {
        return fromUserId;
    }

    public void setFromUserId(String fromUserId) {
        this.fromUserId = fromUserId;
    }

    public String getTargetUserId() {
        return targetUserId;
    }

    public void setTargetUserId(String targetUserId) {
        this.targetUserId = targetUserId;
    }

    public String getTargetTopic() {
        return targetTopic;
    }

    public void setTargetTopic(String targetTopic) {
        this.targetTopic = targetTopic;
    }

    public int getRefType() {
        return refType;
    }

    public void setRefType(int refType) {
        this.refType = refType;
    }

    public String getRefId() {
        return refId;
    }

    public void setRefId(String refId) {
        this.refId = refId;
    }

}
