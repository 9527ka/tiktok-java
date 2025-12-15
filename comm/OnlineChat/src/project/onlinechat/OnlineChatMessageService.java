package project.onlinechat;

import kernel.web.Page;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 客服中心管理
 */

public interface OnlineChatMessageService {

//	public List<OnlineChatMessage> cacheGetList(int pageNo, int pageSize, String partyId);

	/**
	 * 分页读取在线客服用户列表（管理员界面）
	 */
	public List<MessageUser> cacheGetMessageUserPage(int pageNo, int pageSize, String username);

	public OnlineChatMessage saveSend(String partyId, String type, String send_receive, String content,
			String username);

	/*
	 * 创建一个对话，如果已经存在则将对话更新到首位
	 */
	public MessageUser saveCreate(String uid, String username);

	public void delete(String partyId);

	/**
	 * 未读消息数，不输入参数，则返回所有的未读消息数
	 * 
	 * @param partyId
	 * @param type           user:用户未读数，customer:客服未读数
	 * @param targetUsername 当为客服时，指定用户的未读数
	 * @return
	 */
	public int getUnreadMsg(String partyId, String type, String targetUsername);

	/**
	 * 更新未读数
	 * 
	 * @param partyId
	 * @param user_customer 更新对象，用户，客服
	 * @param type          read:读，write：写
	 */
	public void updateUnread(String partyId, String user_customer, String type);

	/**
	 * 根据消息id为起始索引，获取翻页数据
	 * 
	 * @param messageId
	 * @param pageSize
	 * @param partyId
	 * @param clicentType 请求的客户端类型，用户端user，客服端 不传
	 * @return
	 */
	public List<OnlineChatMessage> cacheGetList(String messageId, int pageSize, String partyId, String... clicentType);

	/**
	 * 获取聊天用户
	 * 
	 * @param key
	 * @return
	 */
	public MessageUser cacheMessageUser(String key);

	/**
	 * 设置备注
	 * 
	 * @param partyId
	 * @param remarks
	 */
	public String updateResetRemarks(String partyId, String remarks) throws Exception;

	/**
	 * 获取用户信息
	 * 
	 * @param partyId
	 * @return
	 */
	public Map<String, Object> getUserInfo(String partyId);

	public Map<String, List<OnlineChatMessage>> cacheMessageAll();

	public Map<String, MessageUser> cacheMessageUserAll();

	public void putMessage(String key, List<OnlineChatMessage> value);

	public void putMessageUser(String key, MessageUser value);

	public List<OnlineChatMessage> cacheMessage(String key);

	public void updateMessageUserByIp(MessageUser messageUser);

	public void deleteByIp(String ip);

	/**
	 * 移除通知
	 * 
	 * @param partyId
	 * @param removeTipNum
	 */
	public void removeTips(String partyId, int removeTipNum);

	/**
	 * 未分配到客服的用户，分配客服
	 * 
	 * @return
	 */
	public void updateNoAnwserUser(String username);

	/**
	 * 用户发送客服获取
	 * 
	 * @param partyId
	 * @param sendTime
	 * @param targetUsername
	 * @return
	 */
	public String userSendTarget(String partyId, Date sendTime, String targetUsername);

	public OnlineChatMessage getMessageById(String messageId);

	/**
	 * 后台客服撤回消息操作
	 * 
	 * @param messageId
	 * @param targetUserName
	 */
	public void updateMessageDelete(String messageId, String targetUserName);

	/**
	 * 后台平台客服聊天分页查询
	 */
	Page pagedQuery(int pageNo, int pageSize, String userCode_para, String email_para, String phone_para, String roleName_para, String sellerName_para ,String targetUserNamePara_para);
}
