package project.web.admin.notification;

import com.alibaba.fastjson.JSON;
import kernel.exception.BusinessException;
import kernel.web.PageActionSupport;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import project.mall.notification.utils.notify.client.CommonNotifyService;
import project.mall.notification.NotificationService;
import project.mall.notification.NotificationTemplateService;
import project.mall.notification.model.NotificationTemplate;
import project.mall.notification.utils.notify.client.NotificationHelperClient;
import project.mall.notification.utils.notify.request.DefaultNotifyRequest;
import project.mall.seller.SellerService;
import project.party.PartyService;
import project.party.recom.UserRecomService;
import project.syspara.SysparaService;
import project.user.UserDataService;
import security.SecUser;
import security.internal.SecUserService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 消息通知服务
 *
 */
@RestController
@CrossOrigin
@RequestMapping("/mall/notification")
public class AdminNotificationController extends PageActionSupport {
	
	private Logger logger = LogManager.getLogger(AdminNotificationController.class);
	
	@Autowired
	protected UserRecomService userRecomService;
	@Autowired
	protected UserDataService userDataService;
	@Autowired
	protected PartyService partyService;
	@Autowired
	protected SysparaService sysparaService;
	@Autowired
	protected SellerService sellerService;

	@Autowired
	protected NotificationTemplateService notificationTemplateService;
	@Autowired
	protected NotificationService notificationService;
	@Autowired
    protected CommonNotifyService commonNotifyService;
	//protected CommonNotifyManager inboxNotifyManager;
    @Autowired
    protected SecUserService secUserService;
//    @Autowired
//    private NotificationHelperClient notificationHelperClient;

    /**
     * 管理员查看信息模板列表
     * 支持查看短信、email，站内信模板
     *
     * @param request
     * @return
     */
    @GetMapping("/listTemplate.action")
    public ModelAndView listNotificationTemplate(HttpServletRequest request) {
        SecUser sec = this.secUserService.findUserByLoginName(this.getUsername_login());
        String partyId = sec.getPartyId();

        ModelAndView model = new ModelAndView();

        String typeStr = request.getParameter("type");
        String statusStr = request.getParameter("status");
        String moduleStr = request.getParameter("module");
        String language = request.getParameter("language");

        int type = Integer.parseInt(typeStr);
        int module = 0;
        int status = 0;
        if (moduleStr != null && !moduleStr.trim().isEmpty()) {
            module = Integer.parseInt(moduleStr);
        }
        if (statusStr != null && !statusStr.trim().isEmpty()) {
            status = Integer.parseInt(statusStr);
        }

        try {
            List<NotificationTemplate> templateEntityList = notificationTemplateService.listNotificationTemplate(type, module, language, status);

        } catch (BusinessException e) {
            model.addObject("error", e.getMessage());
            model.setViewName("...");// TODO
            return model;
        } catch (Throwable t) {
            logger.error("update error ", t);
            model.addObject("error", "程序错误");
            model.setViewName("...");// TODO
            return model;
        }
        model.addObject("message", "操作成功");
        model.setViewName("...");// TODO
        return model;
    }

	@PostMapping("/addTemplate.action")
    public ModelAndView addNotificationTemplate(HttpServletRequest request) {
        SecUser sec = this.secUserService.findUserByLoginName(this.getUsername_login());
        String partyId = sec.getPartyId();

        ModelAndView model = new ModelAndView();

        String id = request.getParameter("id");
        String title = request.getParameter("title");
        String content = request.getParameter("content");
        String typeStr = request.getParameter("type");
        String bizType = request.getParameter("bizType");
        String templateCode = request.getParameter("templateCode");
        String statusStr = request.getParameter("status");
        String moduleStr = request.getParameter("module");
        String language = request.getParameter("language");

        NotificationTemplate baseData = new NotificationTemplate();
        baseData.setBizType(bizType);
        baseData.setContent(content);
        baseData.setLanguage(language);
        int type = Integer.parseInt(typeStr);
        baseData.setType(type);
        if (type == 3) {
            baseData.setPlatform("inbox");
        }
        int status = Integer.parseInt(statusStr.trim());
        baseData.setStatus(status);
        baseData.setId(id);
        baseData.setTitle(title);
        baseData.setTemplateCode(templateCode);
        int module = Integer.parseInt(moduleStr.trim());
        baseData.setModule(module);
        baseData.setHandler("default");

        try {
            NotificationTemplate templateEntity = commonNotifyService.saveNotifyTemplate(baseData);
        } catch (BusinessException e) {
            model.addObject("error", e.getMessage());
            model.setViewName("...");// TODO
            return model;
        } catch (Throwable t) {
            logger.error("update error ", t);
            model.addObject("error", "程序错误");
            model.setViewName("...");// TODO
            return model;
        }
        model.addObject("message", "操作成功");
        model.setViewName("...");// TODO
        return model;
    }

    @PostMapping("/updateTemplate.action")
    public ModelAndView updateNotificationTemplate(HttpServletRequest request) {
        SecUser sec = this.secUserService.findUserByLoginName(this.getUsername_login());
        String partyId = sec.getPartyId();

        ModelAndView model = new ModelAndView();

        String id = request.getParameter("id");
        String title = request.getParameter("title");
        String content = request.getParameter("content");
        String bizType = request.getParameter("bizType");
        String templateCode = request.getParameter("templateCode");
        String statusStr = request.getParameter("status");
        String moduleStr = request.getParameter("module");
        String language = request.getParameter("language");

        NotificationTemplate existEntity = notificationTemplateService.getById(id);
        if (existEntity == null) {
            model.addObject("消息模板:" + id + " 记录不存在");
            model.setViewName("...");// TODO
            return model;
        }

        existEntity.setBizType(bizType);
        existEntity.setContent(content);
        existEntity.setLanguage(language);
        int status = Integer.parseInt(statusStr.trim());
        existEntity.setStatus(status);
        existEntity.setTitle(title);
        existEntity.setTemplateCode(templateCode);
        int module = Integer.parseInt(moduleStr.trim());
        existEntity.setModule(module);
        existEntity.setHandler("default");

        try {
            NotificationTemplate templateEntity = commonNotifyService.saveNotifyTemplate(existEntity);
        } catch (BusinessException e) {
            model.addObject("error", e.getMessage());
            model.setViewName("...");// TODO
            return model;
        } catch (Throwable t) {
            logger.error("update error ", t);
            model.addObject("error", "程序错误");
            model.setViewName("...");// TODO
            return model;
        }
        model.addObject("message", "操作成功");
        model.setViewName("...");// TODO
        return model;
    }

    @GetMapping("/loadTemplate.action")
    public ModelAndView getTemplateDetail(HttpServletRequest request) {
        SecUser sec = this.secUserService.findUserByLoginName(this.getUsername_login());
        String partyId = sec.getPartyId();

        ModelAndView model = new ModelAndView();

        String id = request.getParameter("id");
        NotificationTemplate existEntity = notificationTemplateService.getById(id);
        if (existEntity == null) {
            model.addObject("消息模板:" + id + " 记录不存在");
            model.setViewName("...");// TODO
            return model;
        }

        model.addObject("message", "操作成功");
        model.setViewName("...");// TODO
        return model;
    }

    @PostMapping("/sendInbox.action")
    public ModelAndView sendInboxNotify(HttpServletRequest request) {
        SecUser sec = this.secUserService.findUserByLoginName(this.getUsername_login());
        String partyId = sec.getPartyId();

        ModelAndView model = new ModelAndView();

        String bizType = request.getParameter("bizType");
        String targetUserIds = request.getParameter("targetUserIds");
        String targetTopic = request.getParameter("targetTopic");
        String language = request.getParameter("language");
        String varInfo = request.getParameter("varInfo");

        DefaultNotifyRequest notifyRequest = new DefaultNotifyRequest();
        notifyRequest.setBizType(bizType);
        notifyRequest.setFromUserId("0");
        notifyRequest.setLanguageType(language);
        notifyRequest.setTargetTopic("0");
        if (targetTopic == null || targetTopic.trim().isEmpty()) {
            notifyRequest.setTargetTopic("0");
        } else {
            notifyRequest.setTargetTopic(targetTopic.trim());
        }

        logger.info("---> sendInboxNotify 方法收到的消息变量信息为:{}", varInfo);
        Map<String, Object> varMap = null;
        if (varInfo != null && !varInfo.trim().isEmpty()) {
            varMap = JSON.parseObject(varInfo, Map.class);
        }
        if (varMap != null && varMap.size() > 0) {
            Set<Map.Entry<String, Object>> entrySets = varMap.entrySet();
            for (Map.Entry<String, Object> oneEntry : entrySets) {
                notifyRequest.setValue(oneEntry.getKey(), oneEntry.getValue());
            }
        }

        try {
            if (targetUserIds == null || targetUserIds.trim().isEmpty()) {
                notifyRequest.setTargetUserId("0");
                // 广播消息
                commonNotifyService.sendNotify(notifyRequest);
            } else {
                // 多个目标用户
                String[] multiUserIdArr = targetUserIds.split(",");
                for (String targetUserId : multiUserIdArr) {
                    if (targetUserId.trim().isEmpty()) {
                        continue;
                    }
                    notifyRequest.setTargetUserId(targetUserId.trim());

                    // 针对具体用户的消息
                    commonNotifyService.sendNotify(notifyRequest);
                }
            }

            model.addObject("message", "操作成功");
            model.setViewName("...");// TODO
            return model;
        } catch (Exception e) {
            model.addObject("message", "操作失败");
            model.setViewName("...");// TODO
            return model;
        }
    }

//    /**
//     * 站内信分页查询
//     *
//     * @param request
//     * @return
//     */
//    @GetMapping(action + "message.pagelist")
//    public Object pageListInboxNotify(HttpServletRequest request) {
//        ResultObject resultObject = readSecurityContextFromSession(new ResultObject());
//        if (!"0".equals(resultObject.getCode())) {
//            return resultObject;
//        }
//        String partyId = this.getLoginPartyId();
////        Seller pl = sellerService.getSeller(partyId);
//
//        String typeStr = request.getParameter("type");
//        String statusStr = request.getParameter("status");
//        String pageNumStr = request.getParameter("pageNum");
//        String pageSizeStr = request.getParameter("pageSize");
//        String moduleStr = request.getParameter("module");
//        String language = request.getParameter("language");
//
//        int type = Integer.parseInt(typeStr);
//        int module = 0;
//        int status = 0;
//        int pageNum = -1;
//        int pageSize = 20;
//        if (moduleStr != null && !moduleStr.trim().isEmpty()) {
//            module = Integer.parseInt(moduleStr);
//        }
//        if (statusStr != null && !statusStr.trim().isEmpty()) {
//            status = Integer.parseInt(statusStr);
//        }
//        if (pageNumStr != null && !pageNumStr.trim().isEmpty()) {
//            pageNum = Integer.parseInt(pageNumStr);
//        }
//        if (pageSizeStr != null && !pageSizeStr.trim().isEmpty()) {
//            pageSize = Integer.parseInt(pageSizeStr);
//        }
//
//        Page pageInfo = notificationService.pageListUserNotification(pageNum, pageSize, partyId, type, module, null, language, status);
//
//        resultObject.setData(pageInfo);
//        return resultObject;
//    }
//
//    @GetMapping(action + "message.slidelist")
//    public Object slideListInboxNotify(HttpServletRequest request) {
//        ResultObject resultObject = readSecurityContextFromSession(new ResultObject());
//        if (!"0".equals(resultObject.getCode())) {
//            return resultObject;
//        }
//        String partyId = this.getLoginPartyId();
////        Seller pl = sellerService.getSeller(partyId);
//
//        String typeStr = request.getParameter("type");
//        String statusStr = request.getParameter("status");
//        String lastLocationStr = request.getParameter("lastLocation");
//        String pageSizeStr = request.getParameter("pageSize");
//        String moduleStr = request.getParameter("module");
//        String language = request.getParameter("language");
//
//        int type = Integer.parseInt(typeStr);
//        int module = 0;
//        int status = 0;
//        int pageSize = 20;
//        long lastLocation = 0L;
//        if (moduleStr != null && !moduleStr.trim().isEmpty()) {
//            module = Integer.parseInt(moduleStr);
//        }
//        if (statusStr != null && !statusStr.trim().isEmpty()) {
//            status = Integer.parseInt(statusStr);
//        }
//        if (pageSizeStr != null && !pageSizeStr.trim().isEmpty()) {
//            pageSize = Integer.parseInt(pageSizeStr);
//        }
//        if (lastLocationStr != null && !lastLocationStr.trim().isEmpty()) {
//            lastLocation = Integer.parseInt(lastLocationStr);
//        }
//
//        List<Notification> list = notificationService.slideListUserNotification(lastLocation, pageSize, partyId, type, module, null, language, status);
//
//        resultObject.setData(list);
//        return resultObject;
//    }
//
//    @PostMapping(action + "message.read")
//    public Object readNotify(HttpServletRequest request) {
//        ResultObject resultObject = readSecurityContextFromSession(new ResultObject());
//        if (!"0".equals(resultObject.getCode())) {
//            return resultObject;
//        }
//
//        String id = request.getParameter("id");
//        notificationService.updateStatus(id, 2);
//
//        return resultObject;
//    }
//
//    @GetMapping(action + "count.unread")
//    public Object getUnReadCount(HttpServletRequest request) {
//        ResultObject resultObject = readSecurityContextFromSession(new ResultObject());
//        if (!"0".equals(resultObject.getCode())) {
//            return resultObject;
//        }
//        String partyId = this.getLoginPartyId();
//
//        String typeStr = request.getParameter("type");
//        String moduleStr = request.getParameter("module");
//        String language = request.getParameter("language");
//
//        int type = Integer.parseInt(typeStr);
//        int module = 0;
//        if (moduleStr != null && !moduleStr.trim().isEmpty()) {
//            module = Integer.parseInt(moduleStr);
//        }
//
//        int count = notificationService.unReadCount(partyId, type, module, null, language);
//        Map<String, Integer> data = new HashMap();
//        data.put("count", count);
//
//        resultObject.setData(data);
//        return resultObject;
//    }
//
//    @GetMapping(action + "message.detail")
//    public Object getNotifyDetail(HttpServletRequest request) {
//        ResultObject resultObject = readSecurityContextFromSession(new ResultObject());
//        if (!"0".equals(resultObject.getCode())) {
//            return resultObject;
//        }
//        String partyId = this.getLoginPartyId();
//        String id = request.getParameter("id");
//
//        Notification existEntity = notificationService.getById(id);
//
//        resultObject.setData(existEntity);
//        return resultObject;
//    }

}
