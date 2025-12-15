package project.mall.notification.utils.notify;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSONArray;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;
import project.mall.notification.utils.notify.client.CommonNotifyService;
import project.mall.notification.NotificationService;
import project.mall.notification.NotificationTemplateService;
import project.mall.notification.model.ContentPlaceHolder;
import project.mall.notification.model.Notification;
import project.mall.notification.model.NotificationTemplate;
import project.mall.notification.utils.notify.handler.DefaultEmailNotifyHandler;
import project.mall.notification.utils.notify.handler.DefaultNotifyHandler;
import project.mall.notification.utils.notify.handler.SmsBaoNotifyHandler;
import project.mall.notification.utils.notify.model.ContentPlaceHolderVO;
import project.mall.notification.utils.notify.model.NotificationTemplateVO;
import project.mall.notification.utils.notify.request.BaseNotifyRequest;
import project.mall.notification.utils.notify.request.CommonNotifyRequest;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 站内信管理工具
 */
@Service
public class CommonNotifyManager implements CommonNotifyService {
    protected static org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(CommonNotifyManager.class);

    private List<NotificationTemplateVO> templateList;
    private boolean inited = false;
    private boolean isReady = false;

    private NotificationService notificationService;
    private NotificationTemplateService notificationTemplateService;
    private DefaultEmailNotifyHandler defaultEmailNotifyHandler;
    private DefaultNotifyHandler defaultNotifyHandler;
    private SmsBaoNotifyHandler smsBaoNotifyHandler;

    private static CommonNotifyManager instance;

    private ConcurrentHashMap<Class<?>, Object> notifyBeanMap = new ConcurrentHashMap<>();

    /**
     * @Description: 在使用站内信发送组件之前，必须通过 init 方法加载消息模板
     */
    @PostConstruct
    public synchronized boolean init() {
        if (this.inited) {
            return this.isReady;
        }
        instance = this;
        logger.info("---> 准备初始化 InboxNotifyManager ...");

        // 为防止在线程池中 ContextLoader.getCurrentWebApplicationContext() 操作返回的对象是空的，此处提前将相关的几个 bean 加载到缓存中
        cacheNotifyBean();

        reloadSmsTemplate();
        this.inited = true;
        this.isReady = true;
        return isReady;
    }

    public boolean isReady() {
        return this.isReady;
    }

    public boolean isInited() {
        return this.inited;
    }

    public static CommonNotifyManager getInstance() {
        return instance;
    }

    public <T> T getNotifyBean(Class<T> typeClass) {
        Object bean = notifyBeanMap.get(typeClass);
        if (bean == null) {
            WebApplicationContext wac = ContextLoader.getCurrentWebApplicationContext();
            bean = wac.getBean(typeClass);
            if (bean != null) {
                notifyBeanMap.put(typeClass, bean);
                return (T)bean;
            } else {
                return null;
            }
        }

        return (T)bean;
    }

    /**
     *
     * @Description: 管理后台更新了短信模板的配置后，重新加载到内存（慎用）
     */
    public void reloadSmsTemplate() {
        List<NotificationTemplateVO> newTemplateList = new ArrayList();
        List<NotificationTemplate> allTemplateList = notificationTemplateService.listNotificationTemplate(0, 0, null, 2);
        logger.info("---> 加载了:{} 条站内信模板", allTemplateList.size());

        for (NotificationTemplate oneTemplate : allTemplateList) {
            NotificationTemplateVO templateVo = new NotificationTemplateVO();
            BeanUtil.copyProperties(oneTemplate, templateVo);
            newTemplateList.add(templateVo);

            templateVo.setVarPlaceholderList(new ArrayList());
            List<ContentPlaceHolder> varPlaceholderList = oneTemplate.getVarPlaceholders();
            if (CollectionUtils.isNotEmpty(varPlaceholderList)) {
                for (ContentPlaceHolder onePlaceHolderInDB : varPlaceholderList) {
                    ContentPlaceHolderVO placeHolderVo = new ContentPlaceHolderVO();
                    BeanUtil.copyProperties(onePlaceHolderInDB, placeHolderVo);
                    templateVo.getVarPlaceholderList().add(placeHolderVo);
                }
            }
        }

        this.templateList = newTemplateList;
    }

    public NotificationTemplate saveNotifyTemplate(NotificationTemplate baseData) {
        String bizType = baseData.getBizType();
        NotificationBizTypeEnum bizTypeEnum = NotificationBizTypeEnum.bizTypeOf(bizType);
        NotificationHandlerEnum handlerType = NotificationHandlerEnum.handlerNameOf(baseData.getHandler());

        WebApplicationContext wac = ContextLoader.getCurrentWebApplicationContext();
        logger.info("[CommonNotifyManager saveNotifyTemplate] wac:{}", wac);
        String handlerSimpleName = handlerType.getHandler().getSimpleName();
        handlerSimpleName = handlerSimpleName.substring(0, 1).toLowerCase() + handlerSimpleName.substring(1);
        DefaultNotifyHandler handler = (DefaultNotifyHandler) wac.getBean(handlerSimpleName);
        logger.info("[CommonNotifyManager saveNotifyTemplate] 通知业务:{} 对应的处理器 bean 为:{}", bizType, handler);

        // 对基于 code 或 index 模式的变量占位符最后进行一次变量替换
        List<ContentPlaceHolder> varHolderList = handler.parseTemplate(baseData.getContent());
        if (varHolderList != null && !varHolderList.isEmpty()) {
            baseData.setVarPlaceholdersJson(JSONArray.toJSON(varHolderList).toString());
        }
        if (baseData.getTemplateCode() == null || baseData.getTemplateCode().trim().isEmpty()) {
            baseData.setTemplateCode(UUID.randomUUID().toString());
        }

        if (baseData.getId() == null || baseData.getId().toString().isEmpty()) {
            baseData.setId(null);
            notificationTemplateService.save(baseData);
        } else {
            notificationTemplateService.update(baseData);
        }

        this.reloadSmsTemplate();
        return baseData;
    }

    /**
     * 统一的发送消息通知入口；
     * 支持短信发送，email发送，站内信发送
     *
     * @param bizRequestData
     * @return
     */
    public Notification sendNotify(BaseNotifyRequest bizRequestData) {
        if (!isReady()) {
            throw new RuntimeException("通知管理器未能正常启动");
        }

        String bizType = bizRequestData.getBizType();
        String language = bizRequestData.getLanguageType();
        NotificationBizTypeEnum bizTypeEnum = NotificationBizTypeEnum.bizTypeOf(bizType);
        NotificationTemplateVO template = getTemplate(bizType, language);
        if (template == null) {
            throw new RuntimeException("当前业务:" + bizType + " - " + language + " 下没有对应的通知模板");
        }

        // 将业务相关的 request 对象加工成统一的消息通知对象，此处将处理消息模板中的动态占位符
        CommonNotifyRequest request = bizRequestData.buildRequest();
        String messageTemplateContent = template.getContent();
        String notifyTitle = template.getTitle();
        if (bizRequestData.getCustomeMessage() != null && !bizRequestData.getCustomeMessage().trim().isEmpty()) {
            // 针对临时动态的自定义消息模板
            messageTemplateContent = bizRequestData.getCustomeMessage();
            if (bizTypeEnum.getType() == 3 && bizTypeEnum != NotificationBizTypeEnum.INBOX_CUSTOME_MESSAGE) {
                throw new RuntimeException("当前业务:" + bizType + " - " + language + " 不能使用自定义消息");
            }
            if (bizTypeEnum.getType() == 2 && bizTypeEnum != NotificationBizTypeEnum.EMAIL_CUSTOME_MESSAGE) {
                throw new RuntimeException("当前业务:" + bizType + " - " + language + " 不能使用自定义消息");
            }
            if (bizTypeEnum.getType() == 1) {
                throw new RuntimeException("短信不支持自定义消息");
            }
        }
        if (bizRequestData.getCustomeTitle() != null && !bizRequestData.getCustomeTitle().trim().isEmpty()) {
            notifyTitle = bizRequestData.getCustomeTitle();
        }

        //bizRequestData.setMessageContent(messageTemplateContent);
        // 初始化消息内容，针对消息模板中的特殊情况进行初步加工
        String initMessageContent = bizRequestData.initContent(messageTemplateContent);

        if (StringUtils.isBlank(bizType) || StringUtils.isBlank(language)) {
            throw new RuntimeException("请求未指定必要参数");
        }
        request.checkRequestParams();

        try {
            NotificationHandlerEnum handlerType = NotificationHandlerEnum.handlerNameOf(template.getHandler());

            //WebApplicationContext wac = ContextLoader.getCurrentWebApplicationContext();
            //String handlerSimpleName = handlerType.getHandler().getSimpleName();
            //handlerSimpleName = handlerSimpleName.substring(0, 1).toLowerCase() + handlerSimpleName.substring(1);
            //DefaultNotifyHandler handler = (DefaultNotifyHandler) wac.getBean(handlerSimpleName);
            DefaultNotifyHandler handler = this.getNotifyBean(handlerType.getHandler());
            logger.info("[CommonNotifyManager sendNotify] 通知业务:{} 对应的处理器 bean 为:{}", bizType, handler);

            // 对基于 code 或 index 模式的变量占位符最后进行一次变量替换
            initMessageContent = handler.replaceVarHolder(request, initMessageContent);
            // 针对自定义 title 和 content 的支持
            request.setMessageTitle(notifyTitle);
            request.setMessageContent(initMessageContent);

            // 发送最终文本消息
            Notification notify = handler.handle(request);
            return notify;
        } catch (Exception e) {
            logger.error("[CommonNotifyManager sendNotify] 基于业务请求:{} 发送通知消息报错", bizType, e);
        }

        return null;
    }

    /**
     *
     */
    public NotificationTemplateVO getTemplate(String bizType, String languageType) {
        if (StringUtils.isBlank(bizType)) {
            throw new RuntimeException("缺乏必须参数");
        }

        for (NotificationTemplateVO oneTemplate : this.templateList) {
            if (oneTemplate.getBizType().equals(bizType)) {
                if (StringUtils.isBlank(languageType)) {
                    return oneTemplate;
                }
                if (oneTemplate.getLanguage().equals(languageType)) {
                    return oneTemplate;
                }
            }
        }

        return null;
    }

    private void cacheNotifyBean() {
//        // 会出现 wac 为空值的情况
//        WebApplicationContext wac = ContextLoader.getCurrentWebApplicationContext();
//
//        NotificationHandlerEnum[] handlerTypes = NotificationHandlerEnum.values();
//        for (NotificationHandlerEnum oneHandlerType : handlerTypes) {
//            Object handlerBean = wac.getBean(oneHandlerType.getHandler());
//            notifyBeanMap.put(oneHandlerType.getHandler(), handlerBean);
//        }

        // 用笨办法处理
        notifyBeanMap.put(DefaultEmailNotifyHandler.class, defaultEmailNotifyHandler);
        notifyBeanMap.put(DefaultNotifyHandler.class, defaultNotifyHandler);
        notifyBeanMap.put(SmsBaoNotifyHandler.class, smsBaoNotifyHandler);
    }

    public void setNotificationService(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    public void setNotificationTemplateService(NotificationTemplateService notificationTemplateService) {
        this.notificationTemplateService = notificationTemplateService;
    }

    public void setDefaultEmailNotifyHandler(DefaultEmailNotifyHandler defaultEmailNotifyHandler) {
        this.defaultEmailNotifyHandler = defaultEmailNotifyHandler;
    }

    public void setDefaultNotifyHandler(DefaultNotifyHandler defaultNotifyHandler) {
        this.defaultNotifyHandler = defaultNotifyHandler;
    }

    public void setSmsBaoNotifyHandler(SmsBaoNotifyHandler smsBaoNotifyHandler) {
        this.smsBaoNotifyHandler = smsBaoNotifyHandler;
    }

}
