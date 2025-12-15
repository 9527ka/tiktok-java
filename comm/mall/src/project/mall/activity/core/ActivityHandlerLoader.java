package project.mall.activity.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;
import project.mall.activity.ActivityTypeEnum;
import project.mall.activity.handler.ActivityHandler;

import java.util.*;

/**
 * 统一管理活动处理器
 *
 * @author caster
 *
 */
public class ActivityHandlerLoader {
    private final static Logger logger = LoggerFactory.getLogger(ActivityHandlerLoader.class);

	private static Map<ActivityTypeEnum, ActivityHandler> handlerMap = new HashMap();

    private final static ActivityHandlerLoader instance = new ActivityHandlerLoader();
    private ActivityHandlerLoader() {
    	
    }

	static {
		ActivityHandlerScanningProvider provider = new ActivityHandlerScanningProvider(false);
		// 改写实现
		provider.addIncludeFilter(new ActivityHandlerAssignableTypeFilter(ActivityHandler.class));
		// 没标注为 bean 的不设置
		// provider.addIncludeFilter(new AnnotationTypeFilter(Component.class));

		WebApplicationContext wac = ContextLoader.getCurrentWebApplicationContext();
		Set<BeanDefinition> components = provider.findCandidateComponents("project.mall.activity");
		logger.info("========> [ActivityHandlerDispatcher _init] components:" + components.size());
		// List<ActivityHandler> allActivityHandlers = new ArrayList();
		Set<ActivityTypeEnum> repeatCheck = new HashSet<>();
		for (BeanDefinition component : components) {
			// Object serviceObj = component.getSource(); // 拿到的不是 bean
			String handlerClazzName = component.getBeanClassName();
			Class<? extends ActivityHandler> apiServiceClazz = null;
			try {
				apiServiceClazz = (Class<? extends ActivityHandler>)Class.forName(handlerClazzName);
			} catch (Exception e) {
				logger.error("[ActivityHandlerDispatcher _init] 构建类:{} 的对象报错", handlerClazzName, e);
				continue;
			}

			ActivityHandler serviceBean = null;
			try {
				serviceBean = wac.getBean(apiServiceClazz);
			} catch (Exception e) {
				logger.warn("========> [ActivityHandlerDispatcher _init] handlerClazzName:{} 不是一个SpringBean，将创建给类的普通实例...", handlerClazzName);

				try {
					serviceBean = apiServiceClazz.newInstance();
				} catch (Exception e2) {
					logger.error("========> [ActivityHandlerDispatcher _init] 创建活动处理器类:{} 的实例对象失败", handlerClazzName, e2);
				}
			}

			if (repeatCheck.contains(serviceBean.supportActivityType())) {
				throw new RuntimeException("活动处理器:" + handlerClazzName + " 使用了重复的活动类型:" + serviceBean.supportActivityType());
			}

			handlerMap.put(serviceBean.supportActivityType(), serviceBean);
			repeatCheck.add(serviceBean.supportActivityType());
		}
	}

    public static ActivityHandlerLoader getInstance() {
    	return instance;
    }

    public ActivityHandler getHandler(ActivityTypeEnum activityType) {
    	if (activityType == null) {
    		throw new RuntimeException("未指定活动类型");
		}

		return handlerMap.get(activityType);
	}
}
