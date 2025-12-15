package project.web.admin.chat;

import kernel.exception.BusinessException;
import kernel.util.DateUtils;
import kernel.util.JsonUtils;
import kernel.util.StringUtils;
import kernel.web.PageActionSupport;
import kernel.web.ResultObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import project.Constants;
import project.onlinechat.ChatUser;
import project.onlinechat.OnlineChatUserMessage;
import project.onlinechat.OnlineChatUserMessageService;
import project.party.PartyService;
import project.party.model.Party;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/chat")
@Slf4j
public class AdminChatController extends PageActionSupport {

    private Logger logger = LogManager.getLogger(AdminChatController.class);

    @Resource
    private OnlineChatUserMessageService onlineChatUserMessageService;

    @Resource
    protected PartyService partyService;

    /**
     * 管理后台查询所有聊天记录
     */
    @RequestMapping(value = "/chatsList.action", produces = "text/html;charset=UTF-8")
    public ModelAndView chatsList(HttpServletRequest request) {
        String pageNo = request.getParameter("pageNo");
        String userCode_para = request.getParameter("userCode");
        String email_para = request.getParameter("email");
        String phone_para = request.getParameter("phone");
        String roleName_para = request.getParameter("roleName");
        String sellerName_para = request.getParameter("sellerName");
        String sellerCode = request.getParameter("sellerCode");
        String sellerRoleName = request.getParameter("sellerRoleName");
        String message = request.getParameter("message");
        ModelAndView modelAndView = new ModelAndView();
        try {
            this.checkAndSetPageNo(pageNo);
            this.pageSize = 20;
            String loginPartyId = this.getLoginPartyId();
            this.page = this.onlineChatUserMessageService.pagedQuery(this.pageNo, this.pageSize, userCode_para, email_para,
                    phone_para, roleName_para, sellerName_para,loginPartyId,sellerCode,sellerRoleName);
        } catch (BusinessException e) {
            modelAndView.addObject("error", e.getMessage());
            return modelAndView;
        } catch (Throwable t) {
            logger.error(" error ", t);
            modelAndView.addObject("error", "[ERROR] " + t.getMessage());
            return modelAndView;
        }
        modelAndView.addObject("pageNo", this.pageNo);
        modelAndView.addObject("pageSize", this.pageSize);
        modelAndView.addObject("page", this.page);
        modelAndView.addObject("message", message);
        modelAndView.addObject("error", error);
        modelAndView.addObject("userCode", userCode_para);
        modelAndView.addObject("email", email_para);
        modelAndView.addObject("phone", phone_para);
        modelAndView.addObject("roleName", roleName_para);
        modelAndView.addObject("sellerName", sellerName_para);
        modelAndView.addObject("sellerCode", sellerCode);
        modelAndView.addObject("sellerRoleName", sellerRoleName);
        modelAndView.setViewName("chat_list");
        return modelAndView;
    }

    /**
     * 管理后台查看对话
     */
    @RequestMapping(value ="/onechat.action", produces = "text/html;charset=UTF-8")
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
//                String chatId = messages.get(0).getChatId();
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
     * 管理后台更新备注
     */
    @RequestMapping("/upremarks.action")
    public ModelAndView upremarks(HttpServletRequest request) {
        String partyId = request.getParameter("partyId");
        String remarks = request.getParameter("remarks");
        String pageNo = request.getParameter("pageNo");
        ModelAndView modelAndView = new ModelAndView();
        try {
            log.info("partyId->>>>>>>{},remark ->>>>>>{}",partyId,remarks);
            partyService.updateUserRemark(partyId,remarks);
        } catch (BusinessException e) {
            modelAndView.addObject("error", e.getMessage());
            modelAndView.setViewName("redirect:/" +  "chat/chatsList.action");
            return modelAndView;
        } catch (Throwable t) {
            logger.error(" error ", t);
            modelAndView.addObject("error", "[ERROR] " + t.getMessage());
            modelAndView.setViewName("redirect:/" +  "chat/chatsList.action");
            return modelAndView;
        }
        modelAndView.addObject("message", "操作成功");
        modelAndView.addObject("error", error);
        modelAndView.addObject("pageNo", pageNo);
        modelAndView.setViewName("redirect:/" +  "chat/chatsList.action");
        return modelAndView;
    }

    /**
     * 管理后台审核用户聊天记录
     */
    @RequestMapping("/auditList.action")
    public ModelAndView auditList(HttpServletRequest request) {
        String pageNo = request.getParameter("pageNo");
        String userCode_para = request.getParameter("userCode");
        String email_para = request.getParameter("email");
        String phone_para = request.getParameter("phone");
        String message = request.getParameter("message");
        String error = request.getParameter("error");
        String auditStatus_para = request.getParameter("auditStatus");

        ModelAndView modelAndView = new ModelAndView();
        String loginPartyId = this.getLoginPartyId();
        try {
            this.checkAndSetPageNo(pageNo);
            this.pageSize = 20;
            this.page = onlineChatUserMessageService.pagedQueryAudit(this.pageNo, this.pageSize, userCode_para, email_para,phone_para,auditStatus_para,loginPartyId);
        } catch (BusinessException e) {
            modelAndView.addObject("error", e.getMessage());
            return modelAndView;
        } catch (Throwable t) {
            logger.error(" error ", t);
            modelAndView.addObject("error", "[ERROR] " + t.getMessage());
            return modelAndView;
        }
        modelAndView.addObject("pageNo", this.pageNo);
        modelAndView.addObject("page", this.page);
        modelAndView.addObject("userCode",userCode_para);
        modelAndView.addObject("email",email_para);
        modelAndView.addObject("phone",phone_para);
        modelAndView.addObject("message",message);
        modelAndView.addObject("error",error);
        modelAndView.addObject("auditStatus",auditStatus_para);
        modelAndView.setViewName("chat_check_list");
        return modelAndView;
    }

    /**
     * 聊天用户审核，加入白名单或者黑名单
     */
    @RequestMapping("/auditchat.action")
    public ModelAndView auditTOWhiteOrBlack(HttpServletRequest request) {
        String partyId = request.getParameter("uuid");
        String chatAuditPara = request.getParameter("chatAudit");
        String pageNo = request.getParameter("pageNo");
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("pageNo", pageNo);
        try {
            int chatAudit = Integer.valueOf(chatAuditPara);
            Party party = partyService.cachePartyBy(partyId, true);
            party.setChatAudit(chatAudit);
            partyService.update(party);
            switch (chatAudit){
                case 1:
                    onlineChatUserMessageService.addWhite(partyId);
                    break;
                case -1:
                    onlineChatUserMessageService.addBlack(partyId);
                    break;
                default:
            }

        } catch (BusinessException e) {
            modelAndView.addObject("error", e.getMessage());
            modelAndView.setViewName("redirect:/" +  "chat/auditList.action");
            return modelAndView;
        } catch (Throwable t) {
            logger.error(" error ", t);
            modelAndView.addObject("error", "[ERROR] " + t.getMessage());
            modelAndView.setViewName("redirect:/" +  "chat/auditList.action");
            return modelAndView;
        }
        modelAndView.setViewName("redirect:/" +  "chat/auditList.action");
        return modelAndView;
    }
}
