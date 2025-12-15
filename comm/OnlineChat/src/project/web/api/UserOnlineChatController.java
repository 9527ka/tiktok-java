package project.web.api;

import com.alibaba.fastjson.JSONObject;
import kernel.exception.BusinessException;
import kernel.util.DateUtils;
import kernel.util.JsonUtils;
import kernel.util.StringUtils;
import kernel.web.PageActionSupport;
import kernel.web.ResultObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;
import project.Constants;
import project.mall.seller.SellerService;
import project.mall.seller.model.Seller;
import project.onlinechat.*;
import project.onlinechat.event.message.ImSendEvent;
import project.party.PartyService;
import project.party.model.Party;
import project.syspara.SysparaService;
import project.user.token.TokenService;
import sun.net.util.IPAddressUtil;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

@RestController
@CrossOrigin
public class UserOnlineChatController extends PageActionSupport {
    private Logger logger = LogManager.getLogger(UserOnlineChatController.class);
    @Resource
    private OnlineChatUserMessageService onlineChatUserMessageService;
    @Resource
    private TokenService tokenService;
    @Resource
    private PartyService partyService;
    @Resource
    private SysparaService sysparaService;
    @Resource
    private SellerService sellerService;
    private final String action = "public/userOnlineChatController!";

    /**
     * 在线聊天-人员列表
     */
    @RequestMapping(value = action + "userlist.action", produces = "text/html;charset=UTF-8")
    public String userlist(HttpServletRequest request) {

        ResultObject resultObject = new ResultObject();
        if (checkVisitorIp()) {
            return JsonUtils.getJsonString(resultObject);
        }
        try {
            int pageno = 1;
            if (null != request.getParameter("page_no")) {
                pageno = Integer.valueOf(request.getParameter("page_no"));
            }
            int pageSize = 50;
            String token = request.getParameter("token");
            String currentLoginType = request.getParameter("loginType");
//            首先要从token中拿到这个用户id，当用户的id取不到说明这个是游客
            String currentUserPartyId = tokenService.cacheGet(token);
//            得到当前的用户的partId或者IP地址
            String currentUserId = StringUtils.isNullOrEmpty(currentUserPartyId) ? this.getIp() : currentUserPartyId;
//          检查当前用户是否在黑名单中
            if (checkIfBlack(currentUserId)) {
                return JsonUtils.getJsonString(resultObject);
            }
//            拿到所有与当前用户相关的会话关系对象
            List<ChatUser> chatUsers = onlineChatUserMessageService.cacheGetMessageUserPage(pageno, pageSize, currentUserId,currentLoginType);
            List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
            Party currentParty = this.partyService.cachePartyBy(currentUserId, true);
            boolean currentIsSellerFlag = (currentParty != null && sellerService.getSeller(currentUserId) != null) ? true : false;
            for (int i = 0; i < chatUsers.size(); i++) {
                Map<String, Object> map = new HashMap<String, Object>();
                ChatUser chatUser = chatUsers.get(i);
                map.put("id", chatUser.getId());
                String userId = "";
//                拿到与当前登录用户对话的用户，如果当前登录用户是发起会话用户，则要查找的用户为另一个用户,并根据会话用户类别设置未读数
                if (chatUser.getStart_id().equals(currentUserId)) {
                    userId = chatUser.getUser_id();
                    map.put("unreadmsg", chatUser.getStart_unread());
                } else {
                    userId = chatUser.getStart_id();
                    map.put("unreadmsg", chatUser.getUser_unread());
                }
                Party party = this.partyService.cachePartyBy(userId, true);
                if (party != null) {//联系人非游客
//                    默认显示用户的用户名和头像
                    map.put("username", party.getUsername());
                    map.put("useravatar", party.getAvatar());
                    map.put("uid", party.getUsercode());
                    map.put("partyid", party.getId().toString());
//                    如果联系人为商家，需要返回联系人的商家店铺名和店铺图像
                    if (party.getRoleType() == 1) {//联系人为商家
                        Seller seller = sellerService.getSeller(userId);
                        if (seller != null) {//联系人具有商家和用户双重身份时
//                            默认user身份登陆的或者当前用户不具商家身份，联系人取商家信息
                            map.put("username", seller.getName());
                            map.put("avatar", seller.getAvatar());
                            if (currentIsSellerFlag && "shop".equals(currentLoginType)) {//当前用户为商家，shop身份登陆的 联系人取用户信息
                                map.put("username", party.getUsername());
                                map.put("avatar", party.getAvatar());
                            }
                            /*if (currentIsSellerFlag && "user".equals(currentLoginType)) {}*///当前用户为商家，user身份登陆的 联系人取紧挨着的商户信息
//                            if(!currentIsSellerFlag){}//当前用户为游客或者用户时候 俩个用户没有一个是商家，没有这样的数据
                        }
                    } else {//联系人为用户的情况下
                        if (currentIsSellerFlag && "user".equals(currentLoginType)) {//当前用户为商家，user身份登陆时候，排除这个用户
                            continue;
                        }
                        /*if (currentIsSellerFlag && "shop".equals(currentLoginType)) {}*///当前用户为商家，shop身份登陆时候，按照默认取值
                    }
                } else {//联系人为游客
                    if (currentIsSellerFlag && "user".equals(currentLoginType)) {//当前用户为商家，user身份登陆时候，排除这个用户
                        continue;
                    }
                    /*if (currentIsSellerFlag && "shop".equals(currentLoginType)) {}*///当前用户为商家，shop身份登陆时，正常执行
                    if(!currentIsSellerFlag){//当前用户为游客或者用户时候 俩个用户没有一个是商家，没有这样的数据
                        continue;
                    }
                    map.put("username", userId);
                    map.put("partyid", userId);
                }
                map.put("remarks", chatUser.getRemarks());
                List<OnlineChatUserMessage> chats = onlineChatUserMessageService.cacheGetList(currentLoginType,null, 1, chatUser.getChat_id());
                String content = "";
                Date chatDate = null;
                if (chats.size() > 0) {
                    chatDate = chats.get(0).getCreateTime();
                    if ("img".equals(chats.get(0).getContentType())) {
                        content = "[picture]";
                    } else {
                        content = chats.get(0).getContent();
                    }
                }
                map.put("content", content);
                map.put("updatetime",
                        DateUtils.getISO8601TimestampFromDateStr(chatDate != null ? chatDate : chatUser.getUpdateTime()));

                map.put("order_updatetime", chatDate != null && chatDate.after(chatUser.getUpdateTime()) ? chatDate : chatUser.getUpdateTime());// 用作排序
                data.add(map);
                Collections.sort(data, new Comparator<Map<String, Object>>() {
                    @Override
                    public int compare(Map<String, Object> paramT1, Map<String, Object> paramT2) {
                        Date date1 = (Date) paramT1.get("order_updatetime");
                        Date date2 = (Date) paramT2.get("order_updatetime");
                        return -date1.compareTo(date2);
                    }
                });
                int start = 0;
                int end = start + pageSize;

                if (data.size() <= end) {
                    end = data.size();
                }

                List<Map<String, Object>> resultData = new ArrayList<Map<String, Object>>();
                resultData.addAll(data);
                List<Map<String, Object>> subList = resultData.subList(start, end);
                resultObject.setData(subList);
            }
        } catch (BusinessException e) {
            resultObject.setCode("1");
            resultObject.setMsg(e.getMessage());
        } catch (Exception e) {
            resultObject.setCode("1");
            resultObject.setMsg("程序错误");
            logger.error("error:", e);
        }
        return JsonUtils.getJsonString(resultObject);
    }

    /**
     * 聊天记录列表
     */
    @RequestMapping(value = action + "list.action", produces = "text/html;charset=UTF-8")
    public String list(HttpServletRequest request) {
        ResultObject resultObject = new ResultObject();
        if (checkVisitorIp()) {
            return JsonUtils.getJsonString(resultObject);
        }
        try {
            String message_id = request.getParameter("message_id");
//            被读取聊天记录的用户的id
            String beReadPartyId = request.getParameter("partyid");
//            登录类型
            String loginType = request.getParameter("loginType");
//            当前用户的id号
            String token = request.getParameter("token");
//            首先要从token中拿到这个用户id，当用户的id取不到说明这个是游客
            String currentUserPartyId = tokenService.cacheGet(token);
            currentUserPartyId = StringUtils.isNullOrEmpty(currentUserPartyId) ? this.getIp() : currentUserPartyId;
//            检查当前用户是否在黑名单
            if (checkIfBlack(currentUserPartyId)) {
                return JsonUtils.getJsonString(resultObject);
            }
            if (StringUtils.isNullOrEmpty(beReadPartyId)) {
                throw new BusinessException("暂无用户");
            }
            if (checkDoubleIp(currentUserPartyId, beReadPartyId)) {
                throw new BusinessException("游客无与游客的消息记录");
            }

            String chatId="";
            if ("user".equals(loginType)) {
                chatId=new StringBuffer().append(currentUserPartyId).append("-").append(beReadPartyId).toString();
            }else if ("shop".equals(loginType)){
                chatId=new StringBuffer().append(beReadPartyId).append("-").append(currentUserPartyId).toString();
            }
            List<OnlineChatUserMessage> messages = onlineChatUserMessageService.cacheGetList(loginType,message_id, 30, chatId);
            // 首页的时候才更新未读数
            if (StringUtils.isNullOrEmpty(message_id) && messages.size() > 0) {
//                String chatId = messages.get(0).getChatId();
                onlineChatUserMessageService.updateUnread(currentUserPartyId, chatId);

            }
            List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
            for (int i = 0; i < messages.size(); i++) {
                OnlineChatUserMessage message = messages.get(i);
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("id", message.getId().toString());
                map.put("send_receive", currentUserPartyId.equals(message.getSendId()) ? "send" : "receive");
                String contentType = message.getContentType();
                map.put("type", contentType);
                String content = message.getContent();
//                map.put("content", "img".equals(contentType) ? Constants.WEB_URL + "/public/showimg!showImg.action?imagePath=" + content : content);
                map.put("content", content);
//                map.put("createtime", DateUtils.format(message.getCreateTime(), "yyyy-MM-dd HH:mm"));
                map.put("createtime",DateUtils.getISO8601TimestampFromDateStr(message.getCreateTime()));
                map.put("delete_status", message.getDelete_status());
                data.add(map);
            }
            resultObject.setData(data);
        } catch (BusinessException e) {
            resultObject.setCode("1");
            resultObject.setMsg(e.getMessage());
        } catch (Exception e) {
            resultObject.setCode("1");
            resultObject.setMsg("程序错误");
            logger.error("error:", e);
        }
        return JsonUtils.getJsonString(resultObject);
    }

    /**
     * 发送消息
     */
    @RequestMapping(value = action + "send.action", produces = "text/html;charset=UTF-8")
    public Object send(HttpServletRequest request) {
        ResultObject resultObject = new ResultObject();
        String token = request.getParameter("token");
        String sendType = request.getParameter("sendType");
        String chatId = request.getParameter("chatId");
//        区分当前用户是商家还是用户
        String currentLoginType = request.getParameter("loginType");
        String currentUserPartyId = null;
        String otherPartyId = null;//
        if (StringUtils.isNotEmpty(sendType) && "backendSend".equals(sendType)) {//管理后台发送消息的时候以用户身份发送，因此为starId
            currentUserPartyId = Arrays.asList(chatId.split("-")).get(0);
            otherPartyId = Arrays.asList(chatId.split("-")).get(1);
        }else{
            currentUserPartyId = tokenService.cacheGet(token);
//            消息消息接收者用户id ，有可能是ip，也有可能是partyid
            otherPartyId = request.getParameter("partyid");
        }
        if (StringUtils.isNullOrEmpty(currentUserPartyId)) {
            if (checkVisitorIp()) {
                return resultObject;
            }
        } else {
            if (checkUserBlack(currentUserPartyId)) {
                return resultObject;
            }
        }
        currentUserPartyId = StringUtils.isNullOrEmpty(currentUserPartyId) ? this.getIp() : currentUserPartyId;
        try {
            String type = request.getParameter("type");
            String content = request.getParameter("content");
            if (StringUtils.isNullOrEmpty(otherPartyId)) {
                throw new BusinessException("暂无用户");
            }
            if (StringUtils.isNullOrEmpty(content.trim()) || StringUtils.isNullOrEmpty(type)) {
                throw new BusinessException("请输入内容");
            }
//            检测这个partyId从系统中是否可以取到用户，如果取不到用户则为游客
            Party party = partyService.cachePartyBy(otherPartyId, true);
            if (party == null) {
                if (IPAddressUtil.isIPv4LiteralAddress(otherPartyId) || IPAddressUtil.isIPv6LiteralAddress(otherPartyId)) {

                } else {
                    throw new BusinessException("系统无该用户，或者ip地址不正确");
                }
            }
            if (checkDoubleIp(currentUserPartyId, otherPartyId)) {
                throw new BusinessException("游客不能向游客发送消息");
            }
            content = StringUtils.replacer(new StringBuffer(content));
            OnlineChatUserMessage message = null;
            if ("shop".equals(currentLoginType)) {
                message = onlineChatUserMessageService.saveSend(otherPartyId,currentUserPartyId,type, "receive", content,0,currentLoginType);
            }else {
                message = onlineChatUserMessageService.saveSend(currentUserPartyId,otherPartyId,type, "send", content,0,currentLoginType);
            }
            // 发出 spring event 事件，通知消息发送
            try {
                WebApplicationContext wac = ContextLoader.getCurrentWebApplicationContext();
                wac.publishEvent(new ImSendEvent(this, message));
            } catch (Exception e) {
                logger.error("发布IM发送消息事件时报错:", e);
            }
            Map<String, Object> data = new HashMap<String, Object>();
            data.put("updatetime", DateUtils.format(message.getCreateTime(), "yyyy-MM-dd HH:mm"));
            data.put("delete_status", message.getDelete_status());
            data.put("chat_id", message.getId());
            data.put("type", message.getContentType());
            data.put("content", message.getContent());
            data.put("send_receive", message.getSendId().equals(currentUserPartyId) ? "send" : "receive");
            resultObject.setData(data);
        } catch (BusinessException e) {
            resultObject.setCode("1");
            resultObject.setMsg(e.getMessage());
        } catch (Exception e) {
            resultObject.setCode("1");
            resultObject.setMsg("程序错误");
            logger.error("error:", e);
        }

        return JsonUtils.getJsonString(resultObject);
    }

    /**
     * 当用户从商品进入，用户发起与商家聊天，或者商家从订单进入，商家发起与游客聊天时候，需要发送默认消息
     */
    @RequestMapping(value = action + "senddefault.action", produces = "text/html;charset=UTF-8")
    public Object sendDefaultMessage(HttpServletRequest request) {
        ResultObject resultObject = new ResultObject();
        String sellerId = request.getParameter("sellerId");
        String userId = request.getParameter("userId");
        String loginType = request.getParameter("loginType");
        String token = request.getParameter("token");
        String currentUserPartyId = tokenService.cacheGet(token);
        currentUserPartyId = StringUtils.isNullOrEmpty(currentUserPartyId) ? this.getIp() : currentUserPartyId;
        OnlineChatUserMessage message = null;
        try {
            if ("shop".equals(loginType)) {//商家主动从订单进入到与用户聊天时，用户发送默认消息给商家
                this.readSecurityContextFromSession(resultObject);
                if (Objects.isNull(this.partyService.cachePartyBy(userId,true)) || Objects.isNull(sellerService.getSeller(currentUserPartyId))) {
                    throw new BusinessException("暂无用户");//没有商家和用户时
                }
 //        不存在这个会话时候发送默认消息
                if (!onlineChatUserMessageService.checkChat(currentUserPartyId,sellerId)) {//发送删除消息
                    message = onlineChatUserMessageService.saveSend(userId, currentUserPartyId, "text", "send", "",-1,loginType);
                }
            }
            if ("user".equals(loginType)) {//用户进入，商家发送默认消息给用户
                if (StringUtils.isNullOrEmpty(sellerId) || sellerService.getSeller(sellerId)==null) {
                    throw new BusinessException("暂无用户");
                }
                if (StringUtils.isNullOrEmpty(currentUserPartyId)) {
                    if (checkVisitorIp()) {//判断ip黑名单
                        return resultObject;
                    }
                } else {
                    if (checkUserBlack(currentUserPartyId)) {//用户黑名单
                        return resultObject;
                    }
                }
    //        不存在这个会话时候发送默认消息
                if (!onlineChatUserMessageService.checkChat(currentUserPartyId,sellerId)) {
                    String defaultMessage =Optional.ofNullable(sellerService.getSeller(sellerId).getImInitMessage()).orElse("Hello, welcome!");
                    String content = StringUtils.replacer(new StringBuffer(defaultMessage));
//                    2023-11-07 最新需求 卖家未设置默认消息时，该消息不展示
                    int messageDeleteType = StringUtils.isEmptyString(sellerService.getSeller(sellerId).getImInitMessage()) ? -1 : 0;
                    message = onlineChatUserMessageService.saveSend(currentUserPartyId, sellerId,"text", "receive", content,messageDeleteType,"user");
                }
            }
            if (Objects.nonNull(message)) {
                JSONObject o =new JSONObject();
                o.put("updatetime", DateUtils.format(message.getCreateTime(), "yyyy-MM-dd HH:mm"));
                o.put("delete_status", message.getDelete_status());
                o.put("chat_id", message.getId());
                o.put("type", message.getContentType());
                o.put("content", message.getContent());
                o.put("send_receive", message.getSendId().equals(currentUserPartyId) ? "send" : "receive");
                resultObject.setData(o);
            }
        } catch (BusinessException e) {
            resultObject.setCode("1");
            resultObject.setMsg(e.getMessage());
        }catch (Exception e) {
            resultObject.setCode("1");
            resultObject.setMsg("程序错误");
            logger.error("error:", e);
        }
        return JsonUtils.getJsonString(resultObject);
    }

    /**
     * 查询未读消息
     */
    @RequestMapping(action + "unread.action")
    public Object unread(HttpServletRequest request) {
        ResultObject resultObject = new ResultObject();
        if (checkVisitorIp()) {
            return resultObject;
        }
        int unreadMsg = 0;
//        2023-04-19 确认用户未读数总数不计平台的未读总数
//        int unreadPlatformMsg = 0;
        String token = request.getParameter("token");
        String loginType = request.getParameter("loginType");
        String currentUserId = tokenService.cacheGet(token);
        if (StringUtils.isNullOrEmpty(currentUserId)) {
            currentUserId = this.getIp();
            logger.warn("======> api: userOnlineChatController!unread.action 基于 token:" + token + " 未能提取到 partyId 信息");
        }
        if (checkIfBlack(currentUserId)) {
            return JsonUtils.getJsonString(resultObject);
        }
        try {
            if ("shop".equals(loginType)) {//商家固定为userid栏位
                this.readSecurityContextFromSession(resultObject);//首先校验登陆
//                if (Objects.isNull(this.partyService.cachePartyBy(currentUserId,true)) || Objects.isNull(sellerService.getSeller(currentUserId))) {
//                    throw new BusinessException("暂无用户");//校验商家信息
//                }登录校验通过后肯定存在用户信息，不可再进行卖家身份校验，因为买家可以登录卖家端，这个时候卖家信息还未生成，校验不通过会报错，登录一次生成卖家信息后正常，这里不校验，正常去查，查询不到返回0
                unreadMsg = onlineChatUserMessageService.getUnreadMsg(currentUserId,"shop");//商家可以查看到所有的消息，联系人默认为用户
            }else if ("user".equals(loginType)){//用户身份登录的只取与商家的聊天数
                unreadMsg = onlineChatUserMessageService.getUnreadMsg(currentUserId,"user");
            }
            resultObject.setData(unreadMsg);
        } catch (BusinessException e) {
            resultObject.setCode("1");
            resultObject.setMsg(e.getMessage());
        } catch (Exception e) {
            resultObject.setCode("1");
            resultObject.setMsg("程序错误");
            logger.error("error:", e);
        }
        return resultObject;
    }

    /**
     * 查询买家未读消息
     */
    @RequestMapping(action + "buyunread.action")
    public Object buyunread(HttpServletRequest request) {
        ResultObject resultObject = new ResultObject();
        if (checkVisitorIp()) {
            return resultObject;
        }
        String loginPartyId = this.getLoginPartyId();
        try {
            Map<String, Object> resultMap = onlineChatUserMessageService.getBuyUnreadCount(loginPartyId);
            Integer count = Integer.parseInt(String.valueOf(resultMap.get("count")));
            resultObject.setData(count);
        } catch (Exception e) {
            resultObject.setCode("1");
            resultObject.setMsg("程序错误");
            logger.error("error:", e);
        }
        return resultObject;
    }

    /**
     * 商家端从订单给用户发起聊天前，检查会话是否存在  0：会话不存在  1：会话存在
     */
    @RequestMapping(value = action + "check.action", produces = "text/html;charset=UTF-8")
    public Object checkChat(HttpServletRequest request) {
        ResultObject resultObject = new ResultObject();
        try {
            String salePartyId = tokenService.cacheGet(request.getParameter("token"));
            if (StringUtils.isNullOrEmpty(salePartyId)) {
                throw new BusinessException("token失效,请重新登录");
            }
            String userPartyId = request.getParameter("partyid");
            Party party = partyService.cachePartyBy(userPartyId, true);
            if (party == null) {
                throw new BusinessException("partyid不正确，系统无该用户");
            }
            Map<String, Object> data = new HashMap<String, Object>();
            data.put("flag", "0");
            if (this.onlineChatUserMessageService.cacheChatUser(salePartyId + "-" + userPartyId) != null ||
                    this.onlineChatUserMessageService.cacheChatUser(userPartyId + "-" + salePartyId) != null) {
                data.put("flag", "1");
            }
            resultObject.setData(data);
        } catch (BusinessException e) {
            resultObject.setCode("1");
            resultObject.setMsg(e.getMessage());
        } catch (Exception e) {
            resultObject.setCode("1");
            resultObject.setMsg("程序错误");
            logger.error("error:", e);
        }
        return JsonUtils.getJsonString(resultObject);
    }


    /**
     * 管理后台查看对话
     */
    @RequestMapping(value = action + "onechat.action", produces = "text/html;charset=UTF-8")
    public String oneChats(HttpServletRequest request) {
        ResultObject resultObject = new ResultObject();
        try {
            String chatid_para = request.getParameter("chatid");
            String messageId = request.getParameter("messageid");
            if (StringUtils.isNullOrEmpty(chatid_para)) {
                throw new BusinessException("会话id不能为空");
            }
            ChatUser chatUser = this.onlineChatUserMessageService.cacheChatUser(chatid_para);
            String start_id = chatUser.getStart_id();
            List<OnlineChatUserMessage> messages = onlineChatUserMessageService.cacheGetList("back",messageId, 30, chatid_para);
            List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
            // 首页的时候才更新未读数
            if (StringUtils.isNullOrEmpty(messageId) && messages.size() > 0) {
                onlineChatUserMessageService.updateUnread(start_id, chatUser.getChat_id());
            }
            for (int i = 0; i < messages.size(); i++) {
                OnlineChatUserMessage message = messages.get(i);
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("message_id", message.getId().toString());
                map.put("send_receive", start_id.equals(message.getSendId()) ? "send" : "receive");
                String contentType = message.getContentType();
                map.put("type", contentType);
                String content = message.getContent();
                map.put("content", "img".equals(contentType) ? Constants.WEB_URL + "/public/showimg!showImg.action?imagePath=" + content : content);
                map.put("createtime", DateUtils.format(message.getCreateTime(), "yyyy-MM-dd HH:mm"));
                map.put("delete_status", message.getDelete_status());
                data.add(map);
            }
            resultObject.setData(data);
        } catch (BusinessException e) {
            resultObject.setCode("1");
            resultObject.setMsg(e.getMessage());
        } catch (Exception e) {
            resultObject.setCode("1");
            resultObject.setMsg("程序错误");
            logger.error("error:", e);
        }
        return JsonUtils.getJsonString(resultObject);
    }

    /**
     * 检验游客ip是否是黑名单，true：是，false：否
     */
    private boolean checkVisitorIp() {
        String blackMenu = sysparaService.find("online_visitor_black_ip_menu").getValue();
        if (StringUtils.isNullOrEmpty(blackMenu)) {
            return false;
        }
        List<String> list = new ArrayList<String>(Arrays.asList(blackMenu.split(",")));
        return list.contains(this.getIp());
    }

    private boolean checkUserBlack(String loginPartyId) {
        String blackMenu = sysparaService.find("online_username_black_menu").getValue();
        if (StringUtils.isNullOrEmpty(blackMenu)) {
            return false;
        }
        Party party = partyService.cachePartyBy(loginPartyId, true);
        if (party == null) {
            return false;
        }
        String username = party.getUsername();
        List<String> list = new ArrayList<String>(Arrays.asList(blackMenu.split(",")));
        return list.contains(username);
    }

    //    检验发送者和接收者是否都是游客
    private boolean checkDoubleIp(String partyId1, String partyId2) {
        return ((partyId1.contains(".") || partyId1.contains(":"))) && ((partyId2.contains(".") || partyId2.contains(":")));
    }

    //    检测当前用户是否在黑名单中
    private boolean checkIfBlack(String currentPartyId) {
        if (!(currentPartyId.contains(".") || currentPartyId.contains(":"))) {
            return checkUserBlack(currentPartyId);
        }
        return false;
    }

    public ResultObject readSecurityContextFromSession(ResultObject resultObject) {
        HttpServletRequest request = this.getRequest();
        String token = request.getParameter("token");
        if (StringUtils.isNullOrEmpty(token)) {
            resultObject.setCode("403");
            resultObject.setMsg("请重新登录");
            return resultObject;
        }
        String partyId = tokenService.cacheGet(token);
        if (StringUtils.isNullOrEmpty(partyId)) {
            resultObject.setCode("403");
            resultObject.setMsg("请重新登录");
            return resultObject;
        }
        return resultObject;
    }
}