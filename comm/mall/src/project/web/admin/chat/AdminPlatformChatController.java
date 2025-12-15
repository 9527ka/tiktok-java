package project.web.admin.chat;

import kernel.exception.BusinessException;
import kernel.util.DateUtils;
import kernel.util.JsonUtils;
import kernel.util.StringUtils;
import kernel.web.PageActionSupport;
import kernel.web.ResultObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import project.Constants;
import project.onlinechat.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/platform")
public class AdminPlatformChatController extends PageActionSupport {

    private Logger logger = LogManager.getLogger(AdminPlatformChatController.class);

    @Resource
    private OnlineChatMessageService onlineChatMessageService;

    @Resource
    private OnlineChatVisitorMessageService onlineChatVisitorMessageService;

    /**
     * 管理后台查询所有平台客服聊天记录
     */
    @RequestMapping(value = "/chatsList.action", produces = "text/html;charset=UTF-8")
    public ModelAndView chatsList(HttpServletRequest request) {
        String pageNo = request.getParameter("pageNo");
        String userCode_para = request.getParameter("userCode");
        String email_para = request.getParameter("email");
        String phone_para = request.getParameter("phone");
        String roleName_para = request.getParameter("roleName");
        String sellerName_para = request.getParameter("sellerName");
        String targetUserName_para = request.getParameter("targetUserName");
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("platform_chat_list");
        try {
            this.checkAndSetPageNo(pageNo);
            this.pageSize = 20;
            this.page = this.onlineChatMessageService.pagedQuery(this.pageNo, this.pageSize, userCode_para, email_para,
                    phone_para, roleName_para, sellerName_para,targetUserName_para);
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
        modelAndView.addObject("targetUserName", targetUserName_para);
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
            if (StringUtils.isNullOrEmpty(chatid_para)) {
                throw new BusinessException("用户id或者ip不能为空");
            }
            List<OnlineChatMessage> messages = onlineChatMessageService.cacheGetList(null, 1,
                    chatid_para);
            List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
            for (int i = 0; i < messages.size(); i++) {
                OnlineChatMessage message = messages.get(i);
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("message_id", message.getId().toString());
                map.put("send_receive",message.getSend_receive());
                String contentType = message.getType();
                map.put("type", contentType);
                String content = message.getContent();
                map.put("content", "img".equals(contentType) ? Constants.WEB_URL + "/public/showimg!showImg.action?imagePath=" + content : content);
                map.put("createtime", DateUtils.format(message.getCreateTime(), "yyyy-MM-dd HH:mm"));
                map.put("targetUsername", message.getTarget_username());
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
        String partyid = request.getParameter("partyid");
        String remarks = request.getParameter("remarks");
        ModelAndView modelAndView = new ModelAndView();
        try {
            if (partyid.indexOf(".") != -1 || partyid.indexOf(":") != -1) {// ip，表示游客
                onlineChatVisitorMessageService.updateResetRemarks(partyid, remarks);
            } else {
                onlineChatMessageService.updateResetRemarks(partyid, remarks);
            }
        } catch (BusinessException e) {
            modelAndView.addObject("error", e.getMessage());
            modelAndView.setViewName("redirect:/" +  "platform/chatsList.action");
            return modelAndView;
        } catch (Throwable t) {
            logger.error(" error ", t);
            modelAndView.addObject("error", "[ERROR] " + t.getMessage());
            modelAndView.setViewName("redirect:/" +  "platform/chatsList.action");
            return modelAndView;
        }
        modelAndView.addObject("message", message);
        modelAndView.addObject("error", error);
        modelAndView.setViewName("redirect:/" +  "platform/chatsList.action");
        return modelAndView;
    }
}
