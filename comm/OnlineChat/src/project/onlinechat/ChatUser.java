package project.onlinechat;

import kernel.bo.EntityObject;
import org.jetbrains.annotations.NotNull;

import java.util.Date;

public class ChatUser extends EntityObject<String> implements Comparable<ChatUser> {

    private static final long serialVersionUID = 6632900428820490181L;

    /**
     * 会话id  生成规则为对话的用户id组合而成  若为游客则为ip地址
     */
    private String chat_id;
    /**
     * 发起会话用户id 2023-06-15新增需求：将此处id固定位  买家id
     */
    private String start_id;
    /**
     * 用户id  2023-06-15 新增需求：将此处id固定位  商家id
     */
    private String user_id;
    /**
     * 发起会话用户未读数
     */
    private int start_unread;
    /**
     * 客服未读
     */
    private int user_unread;

    /**
     * 商家接收到的买家未被审核消息的未读数字
     */
    private int unAuditUnread;

    private Date updateTime;
    /**
     * 标记删除，-1:删除，0:正常
     */
    private int delete_status;
    /**
     * 备注
     */
    private String remarks;

    @Override
    public int compareTo(@NotNull ChatUser chatUser) {
        if (this.updateTime.after(chatUser.getUpdateTime())) {
            return -1;
        } else if (this.updateTime.before(chatUser.getUpdateTime())) {
            return 1;
        }
        return 0;
    }

    public int getUnAuditUnread() {
        return unAuditUnread;
    }

    public void setUnAuditUnread(int unAuditUnread) {
        this.unAuditUnread = unAuditUnread;
    }

    public String getChat_id() {
        return chat_id;
    }

    public void setChat_id(String chat_id) {
        this.chat_id = chat_id;
    }

    public String getStart_id() {
        return start_id;
    }

    public void setStart_id(String start_id) {
        this.start_id = start_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public int getStart_unread() {
        return start_unread;
    }

    public void setStart_unread(int start_unread) {
        this.start_unread = start_unread;
    }

    public int getUser_unread() {
        return user_unread;
    }

    public void setUser_unread(int user_unread) {
        this.user_unread = user_unread;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public int getDelete_status() {
        return delete_status;
    }

    public void setDelete_status(int delete_status) {
        this.delete_status = delete_status;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}