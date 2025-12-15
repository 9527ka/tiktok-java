package project.mall.notification.utils.notify.request;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import project.mall.notification.utils.notify.model.ContentPlaceHolderVO;
import project.mall.notification.utils.notify.CommonNotifyManager;
import project.mall.notification.utils.notify.model.NotificationTemplateVO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommonNotifyRequest {
    private String messageContent;

    private String messageTitle;

    private String bizType;

    private String languageType;

    private String fromUserId;

    // 非必填，特殊场景可使用该属性携带值
    private String fromExtra;

    private String targetUserId;

    private String targetTopic;

    private String targetExtra;

    private String link;

    /**
     * 非必填字段，有些特殊业务如果需要，可以填充该值方便维护
     * 1 - MallOrdersPrize 记录
     * 2 - OnlineChatUserMessage 记录
     */
    private int refType;

    private String refId;

    // 基于通知模板中的动态变量，构建当前请求对应的变量实例集合
    private List<ContentPlaceHolderVO> contentVarList = new ArrayList();

    /**
     *
     * @Description: 通过下标方式设置变量参数
     * @time 2023年1月14日 上午11:32:29
     * @param value
     * void
     * @throws
     */
    public void setVarValue(int index, Object value) {
        if (value == null) {
            throw new RuntimeException("参数不规范");
        }
        if (StringUtils.isBlank(this.getBizType())) {
            throw new RuntimeException("未指定业务类型");
        }

        // 通知模板，多语言版本情况下随意选择一个，主要用于校验动态参数
        NotificationTemplateVO notificationTemplate = CommonNotifyManager.getInstance().getTemplate(this.getBizType(), null);
        List<ContentPlaceHolderVO> varPlaceholderList = notificationTemplate.getVarPlaceholderList();
        if (CollectionUtils.isEmpty(varPlaceholderList)) {
            throw new RuntimeException("当前通知模板未使用该变量");
        }
        // 查找当前输入参数对应的模板内容占位符信息，先确认模板中有该变量
        ContentPlaceHolderVO varPlaceHolder = null;
        for (ContentPlaceHolderVO oneVarPlaceHolder : varPlaceholderList) {
            if (oneVarPlaceHolder.getIndex() == index) {
                varPlaceHolder = oneVarPlaceHolder;
                break;
            }
        }
        if (varPlaceHolder == null) {
            //throw new RuntimeException("当前消息模板未使用该变量");
            System.out.println("当前业务下的消息模板:" + this.getBizType() + " 没有该下标:" + index);
            return;
        }

        // 基于请求信息，构建消息内容中的动态变量值信息
        ContentPlaceHolderVO currentParam = null;
        for (ContentPlaceHolderVO oneParam : this.contentVarList) {
            if (oneParam.getIndex() == index) {
                currentParam = oneParam;
                break;
            }
        }
        if (currentParam == null) {
            // 尚未设置该动态参数值
            currentParam = new ContentPlaceHolderVO();
            this.contentVarList.add(currentParam);
        }
        BeanUtil.copyProperties(varPlaceHolder, currentParam);
        currentParam.setValue(value);
    }

    /**
     *
     * @Description: 阿里平台设置变量参数
     * @time 2020年11月3日 上午11:32:00
     * @param code
     * @param value
     * void
     * @throws
     */
    public void setVarValue(String code, Object value) {
        if (StringUtils.isBlank(code) || value == null) {
            throw new RuntimeException("参数不规范");
        }
        if (StringUtils.isBlank(this.getBizType())) {
            throw new RuntimeException("未指定业务类型");
        }

        // 通知模板，多语言版本情况下随意选择一个，主要用于校验动态参数
        NotificationTemplateVO notificationTemplate = CommonNotifyManager.getInstance().getTemplate(this.getBizType(), null);
        List<ContentPlaceHolderVO> varPlaceholderList = notificationTemplate.getVarPlaceholderList();
        if (CollectionUtils.isEmpty(varPlaceholderList)) {
            throw new RuntimeException("当前通知模板未使用该变量");
        }
        // 查找当前输入参数对应的模板内容占位符信息，先确认模板中有该变量
        ContentPlaceHolderVO varPlaceHolder = null;
        for (ContentPlaceHolderVO oneVarPlaceHolder : varPlaceholderList) {
            if (code.equals(oneVarPlaceHolder.getCode())) {
                varPlaceHolder = oneVarPlaceHolder;
                break;
            }
        }
        if (varPlaceHolder == null) {
            //throw new RuntimeException("当前消息模板未使用该变量");
            System.out.println("当前业务下的消息模板:" + this.getBizType() + " 未使用该变量:" + code);
            return;
        }

        // 基于请求信息，构建消息内容中的动态变量值信息
        ContentPlaceHolderVO currentParam = null;
        for (ContentPlaceHolderVO oneParam : this.contentVarList) {
            if (code.equals(oneParam.getCode())) {
                currentParam = oneParam;
                break;
            }
        }
        if (currentParam == null) {
            // 尚未设置该动态参数值
            currentParam = new ContentPlaceHolderVO();
            this.contentVarList.add(currentParam);
        }

        BeanUtil.copyProperties(varPlaceHolder, currentParam);
        currentParam.setValue(value);
    }

    /**
     * 针对特定变量占位符的值设置：验证码
     *
     * @param value
     */
    public void setCaptchCodeValue(String value) {
        if (StringUtils.isBlank(value)) {
            throw new RuntimeException("验证码值不能为空");
        }
        if (StringUtils.isBlank(this.getBizType())) {
            throw new RuntimeException("未指定业务类型");
        }

        // 通知模板，多语言版本情况下随意选择一个，主要用于校验动态参数
        NotificationTemplateVO notificationTemplate = CommonNotifyManager.getInstance().getTemplate(this.getBizType(), null);
        List<ContentPlaceHolderVO> varPlaceholderList = notificationTemplate.getVarPlaceholderList();
        if (CollectionUtils.isEmpty(varPlaceholderList)) {
            throw new RuntimeException("当前通知模板未使用验证码");
        }
        // 查找当前输入参数对应的模板内容占位符信息，先确认模板中有该变量
        ContentPlaceHolderVO varPlaceHolder = null;
        for (ContentPlaceHolderVO oneVarPlaceHolder : varPlaceholderList) {
            if (oneVarPlaceHolder.getVarType() == 1) {
                // 验证码类型
                varPlaceHolder = oneVarPlaceHolder;
                break;
            }
        }
        if (varPlaceHolder == null) {
            throw new RuntimeException("当前通知模板未使用验证码变量");
        }

        // 基于请求信息，构建消息内容中的动态变量值信息
        ContentPlaceHolderVO currentParam = null;
        for (ContentPlaceHolderVO oneParam : this.contentVarList) {
            if (varPlaceHolder.getCode().equals(oneParam.getCode())) {
                currentParam = oneParam;
                break;
            }
        }
        if (currentParam == null) {
            // 尚未设置该动态参数值
            currentParam = new ContentPlaceHolderVO();
            this.contentVarList.add(currentParam);
        }

        BeanUtil.copyProperties(varPlaceHolder, currentParam);
        currentParam.setValue(value);
    }

    /**
     * 在通过 request 实例完成通知模板的变量填充后，校验请求是否可用
     *
     */
    public void checkRequestParams() {
        NotificationTemplateVO notificationTemplate = CommonNotifyManager.getInstance().getTemplate(this.getBizType(), null);
        List<ContentPlaceHolderVO> varPlaceholderList = notificationTemplate.getVarPlaceholderList();
        if (CollectionUtils.isEmpty(varPlaceholderList)) {
            // 该通知模板没有变量占位符
            return;
        }

        Map<Integer, ContentPlaceHolderVO> indexParamMap = new HashMap();
        Map<String, ContentPlaceHolderVO> varParamMap = new HashMap();
        for (ContentPlaceHolderVO oneParam : this.contentVarList) {
            System.out.println("-----> oneParam:" + JSONObject.toJSON(oneParam));
            if (oneParam.getIndex() > 0) {
                // 基于下标模式替换变量
                indexParamMap.put(oneParam.getIndex(), oneParam);
            } else {
                varParamMap.put(oneParam.getCode(), oneParam);
            }
        }

        for (ContentPlaceHolderVO oneVarPlaceHolder : varPlaceholderList) {
            if (oneVarPlaceHolder.getIndex() > 0) {
                // 基于下标模式替换变量
                ContentPlaceHolderVO currentParam = indexParamMap.get(oneVarPlaceHolder.getIndex());
                if (currentParam == null) {
                    throw new RuntimeException("未设置通知模板中下标为:" + oneVarPlaceHolder.getIndex() + " 的变量值");
                }
            } else {
                ContentPlaceHolderVO currentParam = varParamMap.get(oneVarPlaceHolder.getCode());
                if (currentParam == null) {
                    throw new RuntimeException("未设置通知模板中变量为:" + oneVarPlaceHolder.getCode() + " 的变量值");
                }
            }
        }
    }

    public ContentPlaceHolderVO getVarHolder(String bizType, int index) {
        if (StringUtils.isBlank(bizType) || index < 1) {
            throw new RuntimeException("参数不规范");
        }

        // 通知模板，多语言版本情况下随意选择一个，主要用于校验动态参数
        NotificationTemplateVO notificationTemplate = CommonNotifyManager.getInstance().getTemplate(this.getBizType(), null);
        List<ContentPlaceHolderVO> varPlaceholderList = notificationTemplate.getVarPlaceholderList();
        if (CollectionUtils.isEmpty(varPlaceholderList)) {
            throw new RuntimeException("当前通知模板未使用该变量");
        }
        // 查找当前输入参数对应的模板内容占位符信息，先确认模板中有该变量
        ContentPlaceHolderVO varPlaceHolder = null;
        for (ContentPlaceHolderVO oneVarPlaceHolder : varPlaceholderList) {
            if (oneVarPlaceHolder.getIndex() == index) {
                varPlaceHolder = oneVarPlaceHolder;
                break;
            }
        }

        return varPlaceHolder;
    }

    public ContentPlaceHolderVO getVarHolder(String bizType, String varCode) {
        if (StringUtils.isBlank(bizType) || StringUtils.isBlank(varCode)) {
            throw new RuntimeException("参数不规范");
        }

        // 通知模板，多语言版本情况下随意选择一个，主要用于校验动态参数
        NotificationTemplateVO notificationTemplate = CommonNotifyManager.getInstance().getTemplate(bizType, null);
        List<ContentPlaceHolderVO> varPlaceholderList = notificationTemplate.getVarPlaceholderList();
        if (CollectionUtils.isEmpty(varPlaceholderList)) {
            throw new RuntimeException("当前通知模板未使用该变量");
        }
        // 查找当前输入参数对应的模板内容占位符信息，先确认模板中有该变量
        ContentPlaceHolderVO varPlaceHolder = null;
        for (ContentPlaceHolderVO oneVarPlaceHolder : varPlaceholderList) {
            if (oneVarPlaceHolder.getCode().equals(varCode)) {
                varPlaceHolder = oneVarPlaceHolder;
                break;
            }
        }

        return varPlaceHolder;
    }


    public String getLanguageType() {
        return languageType;
    }

    public void setLanguageType(String languageType) {
        this.languageType = languageType;
    }

    public List<ContentPlaceHolderVO> getContentVarList() {
        return contentVarList;
    }

    public String getBizType() {
        return bizType;
    }

    public void setBizType(String bizType) {
        this.bizType = bizType;
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

    public String getMessageContent() {
        return messageContent;
    }

    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
    }

    public String getMessageTitle() {
        return messageTitle;
    }

    public void setMessageTitle(String messageTitle) {
        this.messageTitle = messageTitle;
    }

    public String getTargetExtra() {
        return targetExtra;
    }

    public void setTargetExtra(String targetExtra) {
        this.targetExtra = targetExtra;
    }

    public String getFromExtra() {
        return fromExtra;
    }

    public void setFromExtra(String fromExtra) {
        this.fromExtra = fromExtra;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
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
