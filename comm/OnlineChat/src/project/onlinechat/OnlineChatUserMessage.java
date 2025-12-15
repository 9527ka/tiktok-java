package project.onlinechat;

import kernel.bo.EntityObject;
import org.jetbrains.annotations.NotNull;

import java.util.Date;

/**
 * 非客服聊天记录表
 */
public class OnlineChatUserMessage extends EntityObject<String> implements Comparable<OnlineChatUserMessage> {

    private static final long serialVersionUID = 3355519867841512649L;

    private String chatId;// 会话id

    private String sendId;// 消息发送者的id

    private int sendType;//发送类型 1买家发送，0商家发送

    private String contentType;//消息类型 text img

    private String content;// 留言内容

    private Date createTime;

    private int delete_status = 0;//标记删除，-1:删除，0:正常

    private int auditStatus;//消息审核状态(-1审核不通过，0未审核 ，1审核通过)当审核不通过或者未审核时，卖家端聊天记录不展示


    @Override
    public int compareTo(@NotNull OnlineChatUserMessage onlineChatUserMessage) {
        if (this.createTime.after(onlineChatUserMessage.getCreateTime())) {
            return 1;
        } else if (this.createTime.before(onlineChatUserMessage.getCreateTime())) {
            return -1;
        }
        return 0;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public String getSendId() {
        return sendId;
    }

    public void setSendId(String sendId) {
        this.sendId = sendId;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public int getDelete_status() {
        return delete_status;
    }

    public void setDelete_status(int delete_status) {
        this.delete_status = delete_status;
    }

    public int getAuditStatus() {
        return auditStatus;
    }

    public void setAuditStatus(int auditStatus) {
        this.auditStatus = auditStatus;
    }

    public int getSendType() {
        return sendType;
    }

    public void setSendType(int sendType) {
        this.sendType = sendType;
    }
}
