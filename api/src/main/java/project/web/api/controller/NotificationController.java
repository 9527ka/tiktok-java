package project.web.api.controller;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSON;
import kernel.web.BaseAction;
import kernel.web.Page;
import kernel.web.ResultObject;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import project.mall.notification.NotificationService;
import project.mall.notification.NotificationTemplateService;
import project.mall.notification.dto.NotificationDto;
import project.mall.notification.model.Notification;
import project.mall.notification.model.NotificationTemplate;
import project.mall.notification.utils.notify.client.CommonNotifyService;
import project.mall.notification.utils.notify.request.DefaultEmailNotifyRequest;
import project.mall.notification.utils.notify.request.DefaultNotifyRequest;
import project.mall.notification.utils.notify.request.DefaultSmsNotifyRequest;
import project.mall.seller.SellerService;
import project.party.PartyService;
import project.party.model.Party;
import project.party.recom.UserRecomService;
import project.syspara.SysparaService;
import project.user.UserDataService;
import util.DateUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * 消息通知服务
 *
 */
@RestController
@CrossOrigin
public class NotificationController extends BaseAction {
	private Logger logger = LoggerFactory.getLogger(NotificationController.class);
	
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

	private final String action = "api/notification!";

    @GetMapping(action + "template.list")
    public Object listNotificationTemplate(HttpServletRequest request) {
        ResultObject resultObject = readSecurityContextFromSession(new ResultObject());
        if (!"0".equals(resultObject.getCode())) {
            return resultObject;
        }

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

        List<NotificationTemplate> templateEntityList = notificationTemplateService.listNotificationTemplate(type, module, language, status);
        if (templateEntityList != null && templateEntityList.size() > 0) {
            for (NotificationTemplate oneTemplate : templateEntityList) {
                // 触发一下加载 varPlaceholders 值
                oneTemplate.getVarPlaceholders();
            }
        }

        resultObject.setData(templateEntityList);
        return resultObject;
    }

	@PostMapping(action + "template.add")
    public Object addNotificationTemplate(HttpServletRequest request) {
        ResultObject resultObject = readSecurityContextFromSession(new ResultObject());
        if (!"0".equals(resultObject.getCode())) {
            return resultObject;
        }

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

        NotificationTemplate templateEntity = commonNotifyService.saveNotifyTemplate(baseData);

        resultObject.setData(templateEntity);
        return resultObject;
    }

    @PostMapping(action + "template.update")
    public Object updateNotificationTemplate(HttpServletRequest request) {
        ResultObject resultObject = readSecurityContextFromSession(new ResultObject());
        if (!"0".equals(resultObject.getCode())) {
            return resultObject;
        }

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
            resultObject.setCode("1");
            resultObject.setMsg("消息模板:" + id + " 记录不存在");
            return resultObject;
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

        NotificationTemplate templateEntity = commonNotifyService.saveNotifyTemplate(existEntity);

        resultObject.setData(templateEntity);
        return resultObject;
    }

    @GetMapping(action + "template.detail")
    public Object getTemplateDetail(HttpServletRequest request) {
//        ResultObject resultObject = readSecurityContextFromSession(new ResultObject());
//        if (!"0".equals(resultObject.getCode())) {
//            return resultObject;
//        }
//        String partyId = this.getLoginPartyId();
//        Seller pl = sellerService.getSeller(partyId);

        ResultObject resultObject = new ResultObject();

        String id = request.getParameter("id");
        NotificationTemplate existEntity = notificationTemplateService.getById(id);
        if (existEntity == null) {
            resultObject.setCode("1");
            resultObject.setMsg("消息模板:" + id + " 记录不存在");
            return resultObject;
        }

        // 将数据库中的占位符 json 值，构建 list 对象
        existEntity.getVarPlaceholders();

        resultObject.setData(existEntity);
        return resultObject;
    }

    @PostMapping(action + "inbox.send")
    public Object sendInboxNotify(HttpServletRequest request) {
        String bizType = request.getParameter("bizType");
        String targetUserIds = request.getParameter("targetUserIds");
        String targetTopic = request.getParameter("targetTopic");
        String language = request.getParameter("language");
        language = "en_US";// TODO
        String varInfo = request.getParameter("varInfo");
        String customeTitle = request.getParameter("customeTitle");
        String customeMessage = request.getParameter("customeMessage");

        DefaultNotifyRequest notifyRequest = new DefaultNotifyRequest();
        notifyRequest.setBizType(bizType);
        notifyRequest.setFromUserId("0");
        notifyRequest.setLanguageType(language);
        notifyRequest.setTargetTopic("0");
        notifyRequest.setCustomeTitle(customeTitle);
        notifyRequest.setCustomeMessage(customeMessage);
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

        if (targetUserIds == null || targetUserIds.trim().isEmpty()) {
            notifyRequest.setTargetUserId("0");
            // 广播消息
            commonNotifyService.sendNotify(notifyRequest);
        } else {
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

        ResultObject resultObject = new ResultObject();

        return resultObject;
    }

    @PostMapping(action + "email.send")
    public Object sendEmailNotify(HttpServletRequest request) {
        String bizType = request.getParameter("bizType");
        String targetUserIds = request.getParameter("targetUserIds");
        String targetTopic = request.getParameter("targetTopic");
        String language = request.getParameter("language");
        language = "en_US";// TODO
        String varInfo = request.getParameter("varInfo");
        String customeTitle = request.getParameter("customeTitle");
        String customeMessage = request.getParameter("customeMessage");

        DefaultEmailNotifyRequest notifyRequest = new DefaultEmailNotifyRequest();
        notifyRequest.setBizType(bizType);
        notifyRequest.setFromUserId("0");
        notifyRequest.setLanguageType(language);
        notifyRequest.setTargetTopic("0");
        notifyRequest.setCustomeTitle(customeTitle);
        notifyRequest.setCustomeMessage(customeMessage);
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

        if (targetUserIds == null || targetUserIds.trim().isEmpty()) {
            notifyRequest.setTargetUserId("0");
            // 广播消息
            //commonNotifyService.sendNotify(notifyRequest);
            throw new RuntimeException("暂不支持群发邮件！");
        } else {
            String[] multiUserIdArr = targetUserIds.split(",");
            for (String targetUserId : multiUserIdArr) {
                if (targetUserId.trim().isEmpty()) {
                    continue;
                }
                notifyRequest.setTargetUserId(targetUserId.trim());

                Party partyEntity = partyService.cachePartyBy(targetUserId.trim(), true);
                if (partyEntity == null) {
                    logger.error("---> sendEmailNotify 目标用户:" + targetUserId + " 对应的数据记录不存在，无法发送邮件提醒");
                    continue;
                }
                if (StringUtils.isBlank(partyEntity.getEmail())) {
                    logger.error("---> sendEmailNotify 目标用户:" + targetUserId + " 对应的邮件不存在，无法发送邮件提醒");
                    continue;
                }
                notifyRequest.setTargetEmail(partyEntity.getEmail());

                // 针对具体用户的消息
                commonNotifyService.sendNotify(notifyRequest);
            }
        }

        ResultObject resultObject = new ResultObject();

        return resultObject;
    }

    @PostMapping(action + "sms.send")
    public Object sendSmsNotify(HttpServletRequest request) {
        String bizType = request.getParameter("bizType");
        String targetUserIds = request.getParameter("targetUserIds");
        String targetTopic = request.getParameter("targetTopic");
        String language = request.getParameter("language");
        language = "en_US";// TODO
        String varInfo = request.getParameter("varInfo");
        String customeTitle = request.getParameter("customeTitle");
        String customeMessage = request.getParameter("customeMessage");

        DefaultSmsNotifyRequest notifyRequest = new DefaultSmsNotifyRequest();
        notifyRequest.setBizType(bizType);
        notifyRequest.setFromUserId("0");
        notifyRequest.setLanguageType(language);
        notifyRequest.setTargetTopic("0");
        notifyRequest.setCustomeTitle(customeTitle);
        notifyRequest.setCustomeMessage(customeMessage);
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

        if (targetUserIds == null || targetUserIds.trim().isEmpty()) {
            notifyRequest.setTargetUserId("0");
            // 广播消息
            //commonNotifyService.sendNotify(notifyRequest);
            throw new RuntimeException("暂不支持群发短信！");
        } else {
            String[] multiUserIdArr = targetUserIds.split(",");
            for (String targetUserId : multiUserIdArr) {
                if (targetUserId.trim().isEmpty()) {
                    continue;
                }
                notifyRequest.setTargetUserId(targetUserId.trim());

                Party partyEntity = partyService.cachePartyBy(targetUserId.trim(), true);
                if (partyEntity == null) {
                    logger.error("---> sendSmsNotify 目标用户:" + targetUserId + " 对应的数据记录不存在，无法发送短信提醒");
                    continue;
                }
                if (StringUtils.isBlank(partyEntity.getPhone())) {
                    logger.error("---> sendSmsNotify 目标用户:" + targetUserId + " 对应的手机号码不存在，无法发送短信提醒");
                    continue;
                }
                // TODO
                String mobileInfo = partyEntity.getPhone().trim();
                notifyRequest.setMobileInfo(mobileInfo);

                // 针对具体用户的消息
                commonNotifyService.sendNotify(notifyRequest);
            }
        }

        ResultObject resultObject = new ResultObject();

        return resultObject;
    }

    /**
     * 站内信分页查询
     *
     * @param request
     * @return
     */
    @GetMapping(action + "message.pagelist")
    public Object pageListInboxNotify(HttpServletRequest request) {
        ResultObject resultObject = readSecurityContextFromSession(new ResultObject());
        if (!"0".equals(resultObject.getCode())) {
            return resultObject;
        }
        String partyId = this.getLoginPartyId();
//        Seller pl = sellerService.getSeller(partyId);

        String typeStr = request.getParameter("type");
        String statusStr = request.getParameter("status");
        String pageNumStr = request.getParameter("pageNum");
        String pageSizeStr = request.getParameter("pageSize");
        String moduleStr = request.getParameter("module");
        String language = request.getParameter("language");
        language = "en_US";// TODO

        int type = 3;
        int module = 0;
        int status = 0;
        int pageNum = -1;
        int pageSize = 20;
        try {
            type = Integer.parseInt(typeStr);

            if (moduleStr != null && !moduleStr.trim().isEmpty()) {
                module = Integer.parseInt(moduleStr);
            }
            if (statusStr != null && !statusStr.trim().isEmpty()) {
                status = Integer.parseInt(statusStr);
            }
            if (pageNumStr != null && !pageNumStr.trim().isEmpty()) {
                pageNum = Integer.parseInt(pageNumStr);
            }
            if (pageSizeStr != null && !pageSizeStr.trim().isEmpty()) {
                pageSize = Integer.parseInt(pageSizeStr);
            }
        } catch (Exception e) {
            resultObject.setCode("1");
            resultObject.setMsg("参数格式错误！");
            return resultObject;
        }

        Page pageInfo = notificationService.pagedListUserNotification(pageNum, pageSize, partyId, type, module, null, language, status);

        Page retData = new Page(pageInfo.getThisPageNumber(), pageInfo.getPageSize(), pageInfo.getTotalElements());
        List recordList = pageInfo.getElements();
        if (CollectionUtils.isNotEmpty(recordList)) {
            List<NotificationDto> pageList = new ArrayList();
            for (Object oneObj : recordList) {
                Notification oneEntity = (Notification)oneObj;
                NotificationDto dto = new NotificationDto();
                pageList.add(dto);

                BeanUtil.copyProperties(oneEntity, dto);
                dto.setId(oneEntity.getId().toString());
                dto.setSendTime(DateUtils.formatOfDateTime(oneEntity.getSendTime()));
                dto.setReserveSendTime(DateUtils.formatOfDateTime(oneEntity.getReserveSendTime()));
            }

            retData.setElements(pageList);
        }

        resultObject.setData(retData);
        return resultObject;
    }

    @GetMapping(action + "message.slidelist")
    public Object slideListInboxNotify(HttpServletRequest request) {
        ResultObject resultObject = readSecurityContextFromSession(new ResultObject());
        if (!"0".equals(resultObject.getCode())) {
            return resultObject;
        }
        String partyId = this.getLoginPartyId();

        String typeStr = request.getParameter("type");
        String statusStr = request.getParameter("status");
        String lastLocationStr = request.getParameter("lastLocation");
        String pageSizeStr = request.getParameter("pageSize");
        String moduleStr = request.getParameter("module");
        String language = request.getParameter("language");
        language = "en_US";// TODO

        int type = 3;
        int module = 0;
        int status = 0;
        int pageSize = 20;
        long lastLocation = 0L;
        try {
            type = Integer.parseInt(typeStr);

            if (moduleStr != null && !moduleStr.trim().isEmpty()) {
                module = Integer.parseInt(moduleStr);
            }
            if (statusStr != null && !statusStr.trim().isEmpty()) {
                status = Integer.parseInt(statusStr);
            }
            if (pageSizeStr != null && !pageSizeStr.trim().isEmpty()) {
                pageSize = Integer.parseInt(pageSizeStr);
            }
            if (lastLocationStr != null && !lastLocationStr.trim().isEmpty()) {
                lastLocation = Long.parseLong(lastLocationStr);
            }
        } catch (Exception e) {
            resultObject.setCode("1");
            resultObject.setMsg("参数格式错误！");
            return resultObject;
        }

        List<Notification> list = notificationService.getSlideListUserNotification(lastLocation, pageSize, partyId, type, module, null, language, status);

        if (CollectionUtils.isNotEmpty(list)) {
            List<NotificationDto> pageList = new ArrayList();
            for (Object oneObj : list) {
                Notification oneEntity = (Notification)oneObj;
                NotificationDto dto = new NotificationDto();
                pageList.add(dto);

                BeanUtil.copyProperties(oneEntity, dto);
                dto.setId(oneEntity.getId().toString());
                dto.setSendTime(DateUtils.formatOfDateTime(oneEntity.getSendTime()));
                dto.setReserveSendTime(DateUtils.formatOfDateTime(oneEntity.getReserveSendTime()));
            }

            resultObject.setData(pageList);
        }

        return resultObject;
    }

    @PostMapping(action + "message.read")
    public Object readNotify(HttpServletRequest request) {
        ResultObject resultObject = readSecurityContextFromSession(new ResultObject());
        if (!"0".equals(resultObject.getCode())) {
            return resultObject;
        }

        String partyId = this.getLoginPartyId();
        String ids = request.getParameter("ids");
        logger.info("---> NotificationController readNotify ids: {} ", ids);
        if (StringUtils.isBlank(ids)) {
            return resultObject;
        }

        try {
            Set<String> idList = new HashSet<>();
            String[] idArr = ids.split(",");
            for (String oneId : idArr) {
                if (StringUtils.isBlank(oneId)) {
                    continue;
                }
                idList.add(oneId.trim());
            }
            notificationService.updateBatchRead(idList, partyId);

            return resultObject;
        } catch (Exception e) {
            logger.error("---> NotificationController readNotify 报错: ", e);
            resultObject.setMsg("fail");
            resultObject.setCode("1");
            return resultObject;
        }
    }

    @GetMapping(action + "count.unread")
    public Object getUnReadCount(HttpServletRequest request) {
        ResultObject resultObject = readSecurityContextFromSession(new ResultObject());
        if (!"0".equals(resultObject.getCode())) {
            return resultObject;
        }
        String partyId = this.getLoginPartyId();

        String typeStr = request.getParameter("type");
        String moduleStr = request.getParameter("module");
        String language = request.getParameter("language");
        language = "en_US";// TODO

        int type = Integer.parseInt(typeStr);
        int module = 0;
        if (moduleStr != null && !moduleStr.trim().isEmpty()) {
            module = Integer.parseInt(moduleStr);
        }

        int count = notificationService.getUnReadCount(partyId, type, module, null, language);
        Map<String, Integer> data = new HashMap();
        data.put("count", count);

        resultObject.setData(data);
        return resultObject;
    }

    @GetMapping(action + "message.detail")
    public Object getNotifyDetail(HttpServletRequest request) {
        ResultObject resultObject = readSecurityContextFromSession(new ResultObject());
        if (!"0".equals(resultObject.getCode())) {
            return resultObject;
        }
        String partyId = this.getLoginPartyId();
        String id = request.getParameter("id");

        Notification existEntity = notificationService.getById(id);
        existEntity.getVarPlaceholders();

        resultObject.setData(existEntity);
        return resultObject;
    }

    /**
     * 如果修改了消息模板的关键部分，忘了重新加载相关工具的缓存，可以执行该方法进行刷新
     *
     * @param request
     * @return
     */
    @GetMapping(action + "tool.refresh")
    public Object refreshNotifyTool(HttpServletRequest request) {
        ResultObject resultObject = readSecurityContextFromSession(new ResultObject());
//        if (!"0".equals(resultObject.getCode())) {
//            return resultObject;
//        }
//        String partyId = this.getLoginPartyId();
//        Seller pl = sellerService.getSeller(partyId);

        commonNotifyService.reloadSmsTemplate();

        return resultObject;
    }

}
