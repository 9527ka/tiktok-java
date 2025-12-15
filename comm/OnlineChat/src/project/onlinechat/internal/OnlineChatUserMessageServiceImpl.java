package project.onlinechat.internal;

import kernel.exception.BusinessException;
import kernel.util.DateUtils;
import kernel.util.StringUtils;
import kernel.web.Page;
import kernel.web.PagedQueryDao;
import org.apache.commons.collections.Predicate;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.*;
import org.hibernate.query.NativeQuery;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;
import project.log.MoneyLog;
import project.mall.seller.SellerService;
import project.onlinechat.ChatUser;
import project.onlinechat.OnlineChatUserMessage;
import project.onlinechat.OnlineChatUserMessageService;
import project.party.PartyService;
import project.party.model.Party;
import project.party.recom.UserRecomService;
import project.syspara.SysparaService;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Root;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class OnlineChatUserMessageServiceImpl extends HibernateDaoSupport implements OnlineChatUserMessageService {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private PagedQueryDao pagedQueryDao;
    private UserRecomService userRecomService;
    private SellerService sellerService;
    private JdbcTemplate jdbcTemplate;
    private SysparaService sysparaService;
    private PartyService partyService;
    private Map<String, ChatUser> cache_chatUser = new ConcurrentHashMap<String, ChatUser>();
    private Map<String, List<OnlineChatUserMessage>> cache_messages = new ConcurrentHashMap<String, List<OnlineChatUserMessage>>();

    @Override
    public List<ChatUser> cacheGetMessageUserPage(int pageNo, int pageSize, String currentUserId,String currentLoginType) {
        List<ChatUser> list = new ArrayList<ChatUser>(cache_chatUser.values());
        ArrayList<ChatUser> result = new ArrayList<>();
//        list.stream().filter(s->!(s.getDelete_status()==-1))
        for (ChatUser chatUser : list) {
//            过滤已删除的会话
            if (chatUser.getDelete_status() == -1) {
                continue;
            }
//            过滤不含当前用户的会话id
            if (!chatUser.getChat_id().contains(currentUserId)) {
                continue;
            }
//            商家端过滤掉未通过审核和拉黑的买家
            if ("shop".equals(currentLoginType)) {
                Party start = partyService.getById(chatUser.getStart_id());
                if (Objects.isNull(start) || start.getChatAudit()==0 || start.getChatAudit()==-1) {
                    continue;
                }
            }
//            商家身份登录 并且跟商家id 相等
            if ("shop".equals(currentLoginType) && chatUser.getUser_id().equals(currentUserId)) {
                result.add(chatUser);
            }else if ("user".equals(currentLoginType) && chatUser.getStart_id().equals(currentUserId)){//用户身份登录 并且跟用户id相等
                result.add(chatUser);
            }
        }
        Collections.sort(result);
        return result;
    }

    @Override
    public List<OnlineChatUserMessage> cacheGetList(String loginType,String messageId, int pageSize, String... chatIds) {
//        根据参数个数判断是直接传入的chatId，还是俩个userId
        String chatId = "";
        List<OnlineChatUserMessage> cache = null;
//        当传入的是俩个userId 会话id有可能是userId1-userId2 , 也有可能是userId2-userId1
        if (chatIds.length > 1) {
            chatId = chatIds[0] + "-" + chatIds[1];
            cache = cache_messages.get(chatId);
            if (cache == null) {
                chatId = chatIds[1] + "-" + chatIds[0];
                cache = cache_messages.get(chatId);
                if (cache == null) {
                    return new LinkedList<>();
                }
            }
        } else {//当传入的是chatId
            chatId = chatIds[0];
            cache = cache_messages.get(chatId);
            if (cache == null) {
                return new LinkedList<>();
            }
        }

        List<OnlineChatUserMessage> result = new ArrayList<>();
        result.addAll(cache);

        // 过滤只展示通过审核的消息 商家端只展示已通过审核的消息
        if ("shop".equals(loginType)) {
            org.apache.commons.collections.CollectionUtils.filter(result, new Predicate() {
                @Override
                public boolean evaluate(Object arg0) {
                    OnlineChatUserMessage msg = (OnlineChatUserMessage) arg0;
                    return msg.getAuditStatus() == 1 && msg.getDelete_status()==0;
                }
            });
        }else {
            org.apache.commons.collections.CollectionUtils.filter(result, new Predicate() {
                @Override
                public boolean evaluate(Object arg0) {
                    OnlineChatUserMessage msg = (OnlineChatUserMessage) arg0;
                    return msg.getDelete_status()==0;
                }
            });
        }
        int start = cacheIndex(messageId, result);
//		start = start == 0 ? start : start + 1;// 空消息则表示首页，消息索引的后一条为起始
        int end = start + pageSize;

        if (start >= result.size()) {// 起始数据大于总量，返回空
            return new LinkedList<OnlineChatUserMessage>();
        }
        if (cache.size() <= end)
            end = result.size();
        List<OnlineChatUserMessage> list = result.subList(start, end>=result.size()?result.size():end);
        return list;
    }

    @Override
    public OnlineChatUserMessage saveSend(String startId, String sellerId, String type, String sendType, String content ,int messageDeleteType,String loginType) {
//        会话关系在这里根据登录类型确定为，第一个id为用户id或者游客ip ，后一个id必须为商家id
        OnlineChatUserMessage onlineChatUserMessage = new OnlineChatUserMessage();
        Party sender = partyService.getById(startId);
        if (Objects.isNull(sender)) {
            throw new BusinessException("请重新登录");
        }
        String chatId = startId + "-" + sellerId;
        onlineChatUserMessage.setChatId(chatId);
        if ("user".equals(loginType) && "send".equals(sendType)) {//用户端登录并且是发送类型的才是买家消息发送
            onlineChatUserMessage.setSendId(startId);
            onlineChatUserMessage.setAuditStatus(sender.getChatAudit());
            onlineChatUserMessage.setSendType(1);//标记用户发送消息类型为1
        }else {
            onlineChatUserMessage.setSendId(sellerId);
            onlineChatUserMessage.setAuditStatus(1);
            onlineChatUserMessage.setSendType(0);//标记商家用户消息类型为0
        }
        onlineChatUserMessage.setContentType(type);
        onlineChatUserMessage.setContent(content);
        onlineChatUserMessage.setCreateTime(new Date());
        onlineChatUserMessage.setDelete_status(messageDeleteType);

        this.getHibernateTemplate().save(onlineChatUserMessage);

        List<OnlineChatUserMessage> list = cache_messages.get(chatId);
        if (list == null) {
            list = new LinkedList<OnlineChatUserMessage>();
        }
        list.add(onlineChatUserMessage);
        Collections.sort(list);
        Collections.reverse(list);// 添加完后，时间倒叙排序加回
        this.cache_messages.put(chatId, list);
        if (!cache_chatUser.containsKey(chatId)) {// 不存在该会话则新建该会话
            ChatUser chatUser = cache_chatUser.get(chatId);
            if (chatUser == null) {
                chatUser = new ChatUser();
                chatUser.setChat_id(chatId);
                chatUser.setStart_id(startId);
                chatUser.setUser_id(sellerId);
            }
//            会话创建的时候才给更新时间
            chatUser.setUpdateTime(new Date());
            chatUser.setDelete_status(0);
            this.getHibernateTemplate().saveOrUpdate(chatUser);
            cache_chatUser.put(chatId, chatUser);
        }
        ChatUser currentChatUser = cache_chatUser.get(chatId);
        if ("send".equals(sendType) && messageDeleteType==0) {//买家是消息发送者
            if (onlineChatUserMessage.getAuditStatus()==1) {
                currentChatUser.setUser_unread(currentChatUser.getUser_unread() + 1);
            }
            if (onlineChatUserMessage.getAuditStatus()==0) {
                currentChatUser.setUnAuditUnread(currentChatUser.getUnAuditUnread()+1);
            }
        }else if(messageDeleteType==0 && "receive".equals(sendType)){//买家是消息发送者
            currentChatUser.setStart_unread(currentChatUser.getStart_unread() + 1);
        }
        currentChatUser.setDelete_status(0);
//        2023-06-15 新增需求 发送消息更新会话关系表的会话更新时间
        currentChatUser.setUpdateTime(new Date());
        updateChatUserByChatId(currentChatUser);
        return onlineChatUserMessage;
    }

    @Override
    public int getUnreadMsg(String currentUserId, String loginType) {
        int unreadmsg = 0;
        Iterator<Map.Entry<String, ChatUser>> it = cache_chatUser.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, ChatUser> entry = it.next();
            ChatUser chatUser = entry.getValue();
            if ("shop".equals(loginType)) {//当前用户以商家身份登录时，当前用户id为User_id
                if (chatUser.getUser_id().contains(currentUserId)) {//会话中包含当前用户
                    unreadmsg += chatUser.getUser_unread();
                }
            }else if ("user".equals(loginType)){
                if (chatUser.getStart_id().contains(currentUserId)) {
                    unreadmsg += chatUser.getStart_unread();
                }
            }
        }
        return unreadmsg;
    }

    @Override
    public long getTotalCountMsg(String loginPartyId) {
//        Criteria criteria = this.getHibernateTemplate().getSessionFactory().getCurrentSession().createCriteria(ChatUser.class);
//        ProjectionList projectionList = Projections.projectionList()
//                .add(Projections.sum("user_unread"))
//                .add(Projections.sum("unAuditUnread"));
//        criteria.setProjection(projectionList);
//        Object[] results = (Object[]) criteria.uniqueResult();
//        Long totalUnreadCount = (Long) results[0] + (Long) results[1];
//        2023-01-04 需求修改为只查询未审核状态会员发送的消息 2023-09-08 修改为只查询代理下的用户
        String hql = "SELECT SUM(u.unAuditUnread) FROM ChatUser u ";
        List children = this.userRecomService.findChildren(loginPartyId);
        // 使用布尔变量简化条件判断
        boolean hasChildren = children != null && !children.isEmpty();
        if (hasChildren) {
            hql += " WHERE u.start_id IN (:userIds)";
        }
        Query<Long> query = this.getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(hql);
        // if (!StringUtils.isNullOrEmpty(loginPartyId)) {
        if (hasChildren) {
            query.setParameterList("userIds", children);
        }
        // }
        Long totalUnreadCount = query.uniqueResult();
        return totalUnreadCount != null ? totalUnreadCount : 0L;
    }

    @Override
    public Map<String, Object> getBuyUnreadCount(String loginPartyId) {
        //查询虚拟用户与登录的代理下的商家的未读总数
        Map<String, Object> countResult = new HashMap<>();
        if (!StringUtils.isNullOrEmpty(loginPartyId)) {
            Map<String, Object> parameters = new HashMap<>();
            List children = this.userRecomService.findChildren(loginPartyId);
            if (children.size() == 0) {
                countResult.put("count", new Integer(0));
                return countResult;
            }
            log.info("---> OnlineChatUserMessageServiceImpl getBuyUnreadCount 当前用户:{} 有下级用户数量为:{} 个", loginPartyId, children.size());

            String sql = "SELECT IFNULL(SUM(START_UNREAD),0)  FROM T_CHAT_USER chat, PAT_PARTY p WHERE chat.START_ID=p.UUID AND p.ROLENAME='GUEST' AND chat.USER_ID IN (?0)";
            log.info("---> OnlineChatUserMessageServiceImpl getBuyUnreadCount sql:{}, children:{}", sql, children);

            NativeQuery<Object[]> nativeQuery = this.getHibernateTemplate().getSessionFactory().getCurrentSession().createNativeQuery(sql);
            nativeQuery = this.getHibernateTemplate().getSessionFactory().getCurrentSession().createNativeQuery(sql);
            nativeQuery.setParameter(0, children);
            countResult.put("count", nativeQuery.getSingleResult());

            return countResult;
        }

        StringBuffer sql = new StringBuffer(" SELECT IFNULL(SUM(START_UNREAD),0) FROM T_CHAT_USER chat,PAT_PARTY p WHERE chat.START_ID=p.UUID AND p.ROLENAME='GUEST' ");
        log.info("---> OnlineChatUserMessageServiceImpl getBuyUnreadCount sql:{}", sql);

        NativeQuery<Object[]> nativeQuery = this.getHibernateTemplate().getSessionFactory().getCurrentSession()
                .createNativeQuery(sql.toString());
        Object result = nativeQuery.getSingleResult();
        countResult.put("count", result);
        return countResult;
    }

    //    调用消息记录接口时候，更新未读数
    @Override
    public void updateUnread(String currentUserPartyId, String chatId) {
        ChatUser chatUser = cache_chatUser.get(chatId);
        if (chatUser == null) {
            saveCreate(chatId);
            chatUser = cache_chatUser.get(chatId);
        }//更新当前用户未读数字
        if (chatUser.getStart_id().equals(currentUserPartyId)) {
            chatUser.setStart_unread(0);
        }
        if (chatUser.getUser_id().equals(currentUserPartyId)){
            chatUser.setUser_unread(0);
        }
        updateChatUserByChatId(chatUser);
    }

    /**
     * 获取消息的索引
     */
    private int cacheIndex(String messageId, List<OnlineChatUserMessage> list) {
        if (StringUtils.isEmptyString(messageId))
            return 0;
        int index = -1;
        for (int i = 0; i < list.size(); i++) {
            OnlineChatUserMessage message = list.get(i);
            if (messageId.equals(message.getId().toString())) {
                index = i;
            }
        }
        if (index == -1) {
            throw new BusinessException("参数异常，消息获取失败");
        }
        return index + 1;
    }

    public ChatUser cacheChatUser(String key) {
        return cache_chatUser.get(key);
    }

    public ChatUser findChatUserById(String id) {
        return getHibernateTemplate().get(ChatUser.class, id);
    }

    @Override
    public ChatUser saveCreate(String chatId) {
        ChatUser chatUser = cache_chatUser.get(chatId);
        if (chatUser == null) {
            chatUser = new ChatUser();
            chatUser.setChat_id(chatId);
            String[] split = chatId.split("-");
            chatUser.setStart_id(split[0]);
            chatUser.setUser_id(split[1]);
        }
        chatUser.setUpdateTime(new Date());
        chatUser.setDelete_status(0);
        this.getHibernateTemplate().saveOrUpdate(chatUser);
//		入库的时候就需要更新缓存
        cache_chatUser.put(chatId, chatUser);
        return chatUser;
    }

    @Override
    public Page pagedQuery(int pageNo, int pageSize, String userCode_para, String email_para, String phone_para, String roleName_para,
                           String sellerName_para,String loginPartyId, String sellerCode, String sellerRoleName) {
        Map<String, Object> parameters = new HashMap<>();
        StringBuffer queryString = new StringBuffer(
                "SELECT buy.USERCODE AS USERCODE, buy.EMAIL, buy.PHONE, buy.ROLENAME, sale.USERCODE AS SALECODE ");
        queryString.append(" 	, s.NAME, sale.REMARKS remarks, sale.UUID partyId, chat.UPDATETIME, chat.CHAT_ID, chat.START_UNREAD AS UNREAD,chat.UUID id ");
        queryString.append(" 	,  sale.ROLENAME sellerRoleName, sale.USERCODE sellerCode ");
        queryString.append(" FROM T_CHAT_USER chat   ");
        queryString.append(" 	INNER JOIN PAT_PARTY buy ON buy.UUID = chat.START_ID AND buy.ROLENAME='GUEST'  ");
        queryString.append(" 	LEFT JOIN PAT_PARTY sale ON sale.UUID = chat.USER_ID AND sale.ROLE_TYPE = '1' ");
        queryString.append(" 	LEFT JOIN T_MALL_SELLER s ON s.UUID = sale.UUID ");
        queryString.append(" WHERE 1 = 1 ");
        if (!StringUtils.isNullOrEmpty(loginPartyId)) {
            List children = this.userRecomService.findChildren(loginPartyId);
            if (children.size() == 0) {
                return new Page();
            }
            queryString.append(" and sale.UUID in (:children) ");
            parameters.put("children", children);
        }
        if (!StringUtils.isNullOrEmpty(userCode_para)) {
            queryString.append(" AND buy.USERCODE =:usercode ");
            parameters.put("usercode", userCode_para);
        }
        if (!StringUtils.isNullOrEmpty(email_para)) {
            queryString.append(" AND buy.EMAIL =:email ");
            parameters.put("email", email_para);
        }
        if (!StringUtils.isNullOrEmpty(phone_para)) {
            queryString.append(" AND buy.PHONE =:phone ");
            parameters.put("phone", phone_para);
        }
        if (!StringUtils.isNullOrEmpty(roleName_para)) {
            queryString.append(" AND buy.ROLENAME =:rolename ");
            parameters.put("rolename", roleName_para);
        }
        if (!StringUtils.isNullOrEmpty(sellerName_para)) {
            queryString.append(" AND s.NAME =:sellername ");
            parameters.put("sellername", sellerName_para);
        }
        if (!StringUtils.isNullOrEmpty(sellerCode)) {
            queryString.append(" AND sale.USERCODE =:sellerCode ");
            parameters.put("sellerCode", sellerCode);
        }
        if (!StringUtils.isNullOrEmpty(sellerRoleName)) {
            queryString.append(" AND sale.ROLENAME =:sellerRoleName ");
            parameters.put("sellerRoleName", sellerRoleName);
        }
        queryString.append(" ORDER BY chat.UPDATETIME desc ");
        Page page = this.pagedQueryDao.pagedQuerySQL(pageNo, pageSize, queryString.toString(), parameters);
        return page;
    }

    @Override
    public Page pagedQueryAudit(int pageNo, int pageSize, String userCode_para, String email_para, String phone_para,String chatAudit_para,String loginPartyId) {
        Map<String, Object> parameters = new HashMap<>();
        StringBuffer queryString = new StringBuffer(
                " SELECT sender.UUID,sender.USERCODE,sender.EMAIL,sender.PHONE,sender.ROLENAME,msg.CONTENT,msg.CREATE_TIME,sender.CHAT_AUDIT, "+
                " seller.NAME AS sellerName, sellerP.USERCODE AS sellerCode "+
                " FROM T_ONLINECHAT_USERMESSAGE msg "+
                " LEFT JOIN PAT_PARTY sender ON sender.UUID = msg.SEND_ID "+
                " LEFT JOIN T_CHAT_USER user ON user.CHAT_ID = msg.CHAT_ID "+
                " LEFT JOIN T_MALL_SELLER seller ON user.USER_ID = seller.UUID "+
                " LEFT JOIN PAT_PARTY sellerP ON sellerP.UUID = seller.UUID "+
                " WHERE SEND_TYPE = 1 AND sender.ROLENAME ='MEMBER' ");
        if (!StringUtils.isNullOrEmpty(loginPartyId)) {
            List children = this.userRecomService.findChildren(loginPartyId);
            if (children.size() == 0) {
                return new Page();
            }
            queryString.append(" AND user.START_ID in (:children) ");
            parameters.put("children", children);
        }
        if (!StringUtils.isNullOrEmpty(userCode_para)) {
            queryString.append(" AND sender.USERCODE =:usercode ");
            parameters.put("usercode", userCode_para);
        }
        if (!StringUtils.isNullOrEmpty(email_para)) {
            queryString.append(" AND sender.EMAIL =:email ");
            parameters.put("email", email_para);
        }
        if (!StringUtils.isNullOrEmpty(phone_para)) {
            queryString.append(" AND sender.PHONE =:phone ");
            parameters.put("phone", phone_para);
        }
        if (!StringUtils.isNullOrEmpty(chatAudit_para)) {
            queryString.append(" AND sender.CHAT_AUDIT =:chatAudit ");
            parameters.put("chatAudit", chatAudit_para);
        }
        queryString.append(" ORDER BY msg.CREATE_TIME desc ");
        Page page = this.pagedQueryDao.pagedQuerySQL(pageNo, pageSize, queryString.toString(), parameters);
        return page;
    }

    public void updateChatUserByChatId(ChatUser chatUser) {
        this.getHibernateTemplate().saveOrUpdate(chatUser);
//        this.getHibernateTemplate().flush();
        cache_chatUser.put(chatUser.getChat_id(), chatUser);
    }

    public void init() {

        StringBuffer queryString = new StringBuffer(" FROM ChatUser ");
        List<ChatUser> list_user = (List<ChatUser>) this.getHibernateTemplate().find(queryString.toString());

        for (int i = 0; i < list_user.size(); i++) {
            ChatUser item = list_user.get(i);
            cache_chatUser.put(item.getChat_id(), item);
        }

        queryString = new StringBuffer(" FROM OnlineChatUserMessage order by createTime desc ");

        List<OnlineChatUserMessage> list_chat = (List<OnlineChatUserMessage>) this.getHibernateTemplate().find(queryString.toString());

        for (int i = 0; i < list_chat.size(); i++) {

            OnlineChatUserMessage item = list_chat.get(i);
            List<OnlineChatUserMessage> list = null;
            list = cache_messages.get(item.getChatId());
            if (list == null) {
                list = new LinkedList<>();
            }
            list.add(item);
            this.cache_messages.put(item.getChatId(), list);
        }
    }

    public OnlineChatUserMessage getLastImMessage(String senderId, String receiverId, boolean includeImg, Long optionLimitTime) {
        if (StringUtils.isNullOrEmpty(senderId) || StringUtils.isNullOrEmpty(receiverId)) {
            throw new RuntimeException("缺少必须参数");
        }

        DetachedCriteria query = DetachedCriteria.forClass(OnlineChatUserMessage.class);
        query.add(Property.forName("chatId").eq(senderId + "-" + receiverId));
        if (optionLimitTime != null && optionLimitTime > 0) {
            Date limitSendTime = new Date(optionLimitTime);
            query.add(Property.forName("createTime").ge(limitSendTime));
        }
        if (!includeImg) {
            query.add(Property.forName("contentType").eq("text"));
        }
        query.addOrder(Order.desc("createTime"));

        List<OnlineChatUserMessage> list = (List<OnlineChatUserMessage>) getHibernateTemplate().findByCriteria(query,0,1);
        if (list == null || list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    @Override
    public boolean checkChat(String currentUserPartyId, String sellerId) {
        return this.cache_chatUser.containsKey(currentUserPartyId+"-"+sellerId) || this.cache_chatUser.containsKey(sellerId+"-"+currentUserPartyId);
    }

    @Override
    public void updateAutoClearChatHistory() {
        String mallChatHistoryDeleteDays = this.sysparaService.find("mall_chat_history_delete").getValue();
        if (Objects.isNull(mallChatHistoryDeleteDays)) {
            logger.error("mall_chat_history_delete参数未配置");
            return;
        }
        String needToClearTime = DateUtils.toString(DateUtils.addDate(new Date(), -Integer.valueOf(mallChatHistoryDeleteDays)));
        jdbcTemplate.execute("DELETE FROM T_ONLINECHAT_MESSAGE WHERE CREATE_TIME<'" + needToClearTime + "'");
        jdbcTemplate.execute("DELETE FROM T_ONLINECHAT_USERMESSAGE WHERE CREATE_TIME<'" + needToClearTime + "'");
    }

    @Override
    public void addWhite(String partyId) {
        Set<String> chatIds = cache_chatUser.keySet();
        for (String chatId : chatIds) {
            if (chatId.startsWith(partyId)) {
//                将这个未审核的未读数字加到已审核未读数上
                ChatUser chatUser = cache_chatUser.get(chatId);
                int unAuditUnread = chatUser.getUnAuditUnread();
                int user_unread = chatUser.getUser_unread();
                chatUser.setUser_unread(user_unread+unAuditUnread);
                chatUser.setUnAuditUnread(0);
                this.updateChatUserByChatId(chatUser);
                cache_chatUser.put(chatId,chatUser);

//                将这个已通过审核的聊天消息由未审核状态变成已审核状态
                List<OnlineChatUserMessage> onlineChatUserMessages = cache_messages.get(chatId);
                for (OnlineChatUserMessage onlineChatUserMessage : onlineChatUserMessages) {
                    if (onlineChatUserMessage.getAuditStatus()==0) {
                        onlineChatUserMessage.setAuditStatus(1);
                        this.getHibernateTemplate().saveOrUpdate(onlineChatUserMessage);
                    }
                }
                cache_messages.put(chatId,onlineChatUserMessages);
            }
        }
    }

    @Override
    public void addBlack(String partyId) {
        Set<String> chatIds = cache_chatUser.keySet();
        for (String chatId : chatIds) {
            if (chatId.startsWith(partyId)) {
//                将这个已拉黑用户的未审核的消息未读数字设置成0
                ChatUser chatUser = cache_chatUser.get(chatId);
                chatUser.setUnAuditUnread(0);
                chatUser.setUser_unread(0);
                this.updateChatUserByChatId(chatUser);
                cache_chatUser.put(chatId,chatUser);
//                将这个已拉黑的用户的未审核的聊天消息由未审核状态变成了拉黑状态
                List<OnlineChatUserMessage> onlineChatUserMessages = cache_messages.get(chatId);
                for (OnlineChatUserMessage onlineChatUserMessage : onlineChatUserMessages) {
                    if (onlineChatUserMessage.getAuditStatus()==0) {
                        onlineChatUserMessage.setAuditStatus(-1);
                        this.getHibernateTemplate().saveOrUpdate(onlineChatUserMessage);
                    }
                }
                cache_messages.put(chatId,onlineChatUserMessages);
            }
        }
    }

    public void setPagedQueryDao(PagedQueryDao pagedQueryDao) {
        this.pagedQueryDao = pagedQueryDao;
    }

    public void setUserRecomService(UserRecomService userRecomService) {
        this.userRecomService = userRecomService;
    }

    public void setSellerService(SellerService sellerService) {
        this.sellerService = sellerService;
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void setSysparaService(SysparaService sysparaService) {
        this.sysparaService = sysparaService;
    }

    public void setPartyService(PartyService partyService) {
        this.partyService = partyService;
    }
}
