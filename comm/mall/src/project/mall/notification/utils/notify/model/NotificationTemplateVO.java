package project.mall.notification.utils.notify.model;

import lombok.Data;

import java.util.List;

@Data
public class NotificationTemplateVO {
//    /**
//     * 用于支持同条通知多语言，逻辑上同条通知该值相同
//     */
//    private String templateGroupKey;

    /**
     * 语言类型：zh: 中文，en: 英文
     */
    private String language;

    // 模板编码，全局唯一
    private String templateCode;

    /**
     * 模板大类：
     * 1 - 短信模板
     * 2 - email模板
     * 3 - 站内消息模板
     */
    private Integer type;

    /**
     * 模板标题，说明
     */
    private String title;

    /**
     * 模板状态：0-废弃，1-审核中，2-正常，3-下架锁定（提前一天强制下架锁定，该状态的模板不能发起短信，针对并发情况）
     */
    private Integer status;


    /**
     * 模板平台：
     * Inbox - 站内信
     * aliSms - 阿里短信
     * googleEmail - 谷歌邮箱
     *
     */
    private String platform;

    /**
     * 业务类型：
     */
    private String bizType;

    /**
     * 消息处理器名称
     */
    private String handler;

    /**
     * 模块：
     */
    private Integer module;


    /**
     * 模板内容
     */
    private String content;

    // 短信模板动态变量占位符信息
    private List<ContentPlaceHolderVO> varPlaceholderList;
}
