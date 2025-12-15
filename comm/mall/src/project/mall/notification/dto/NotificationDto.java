package project.mall.notification.dto;

import lombok.Data;

@Data
public class NotificationDto {
    private String id;

    /**
     * 语言类型：zh_CN: 中文，en_US: 英文
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
     * 预约发送时间
     */
    private String reserveSendTime;

    /**
     * 正式发送时间
     */
    private String sendTime;

    /**
     * 用于支持屏幕滑动翻页
     */
    private Long location;

    private String fromUserId;

    private String targetUserId;

    private String targetTopic;

    // 目标用户的额外信息，例如 email 值，手机号等
    private String targetExtra;

    /**
     * 业务类型：
     * 参考枚举类型：NotificationBizTypeEnum
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
     * 通知关联的某类记录ID
     */
    private String refId;

    /**
     * 通知关联的某类业务类型，用于解释: ref_id
     */
    private Integer refType;

    /**
     * 通知标题
     */
    private String title;

    /**
     * 通知内容
     */
    private String content;

    /**
     * 变量值的json结构
     */
    private String varInfo;

    /**
     * 点击通知跳转链接
     */
    private String link;

    /**
     * 附件信息
     */
    private String attachment;

    /**
     * 通知状态：0-未发送，1-已发送未读，2-已发送已读，3-被撤销
     */
    private Integer status;

//    /**
//     * 对应解析类：ContentPlaceHolder
//     * 消息模板变量占位符信息，例如：[{"index":1, "code":"text", "format":"yyyy年MM月dd日","value":"10月20号"}]
//     */
//    private List<ContentPlaceHolder> varPlaceholders;

}
