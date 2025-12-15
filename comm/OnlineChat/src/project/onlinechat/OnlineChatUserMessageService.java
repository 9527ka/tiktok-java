package project.onlinechat;


import kernel.web.Page;

import java.util.List;
import java.util.Map;

/*
* 非客服聊天管理
* */
public interface OnlineChatUserMessageService {

    /**
     * 分页读取会话用户列表
     */
    public List<ChatUser> cacheGetMessageUserPage(int pageNo, int pageSize, String userId,String currentLoginType);

    /**
     * 根据消息id为起始索引，获取分页数据
     * @param messageId
     */
    public List<OnlineChatUserMessage> cacheGetList(String loginType,String messageId, int pageSize, String... chatId);

    /**
     * 根据俩个用户的partyId 来发送这个消息
     */
    OnlineChatUserMessage saveSend(String partyId, String sellerId, String type, String send, String content,int messageDeleteType,String loginType);

    /**
     *
     * @param currentUserId
     * @param loginType 根据登录类型来查找未读数字
     * @return
     */
    int getUnreadMsg(String currentUserId, String loginType);

    long getTotalCountMsg(String partyId);

    Map<String, Object> getBuyUnreadCount(String loginPartyId);

    void updateUnread(String currentUserPartyId, String beReadPartyId);

    /**
     * 获取聊天用户
     */
    public ChatUser cacheChatUser(String key);

    public ChatUser findChatUserById(String id);

    public ChatUser saveCreate(String chatId);

    /**
     * 后台全部聊天分页查询
     */
    Page pagedQuery(int pageNo, int pageSize, String userCode_para, String email_para, String phone_para, String roleName_para,
                    String sellerName_para ,String partyId,String sellerCode, String sellerRoleName);

    /**
     * 后台审查消息记录分页查询
     */
    Page pagedQueryAudit(int pageNo, int pageSize, String userCode_para, String email_para, String phone_para,String chatAudit_para,String loginPartyId);

    /*
    * 根据chatid跟新chatUser
    */
    public void updateChatUserByChatId(ChatUser chatUser);

    OnlineChatUserMessage getLastImMessage(String senderId, String receiverId, boolean includeImg, Long optionLimitTime);

    boolean checkChat(String currentUserPartyId,String sellerId);

    /**
     * 自动清除历史聊天记录
     */
    void updateAutoClearChatHistory();

    void addWhite(String partyId);

    void addBlack(String partyId);
}
