package kernel.cache;

import kernel.annotation.CustomCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description: 在当前的应用下需要配置进 bean.xml 才会生效 TODO
 *     参考资料：
 *     https://blog.csdn.net/qq_30095631/article/details/103669089
 *
 * @author caster
 * @date 2023年4月26日 下午10:25:32
 *
 */
public class CustomCacheMethodProcessor implements BeanPostProcessor,
        ApplicationContextAware, SmartInitializingSingleton {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    private ConfigurableApplicationContext applicationContext;

	private List<Method> allCachedMethodList = new ArrayList<>();

    @Override
    public final void setApplicationContext(ApplicationContext applicationContext)
            throws BeansException {
        this.applicationContext = (ConfigurableApplicationContext) applicationContext;
    }

	/**
	 * 比 afterSingletonsInstantiated 先执行
	 *
	 * @param bean
	 * @param beanName
	 * @return
	 * @throws BeansException
	 */
    @Override
    public final Object postProcessAfterInitialization(Object bean, final String beanName)
            throws BeansException {
		Class<?> beanClass = AopUtils.isAopProxy(bean) ? AopUtils.getTargetClass(bean)
				: bean.getClass();

		Method[] uniqueDeclaredMethods = ReflectionUtils.getUniqueDeclaredMethods(beanClass);
		for (Method method : uniqueDeclaredMethods) {
			CustomCache cacheAnn = AnnotatedElementUtils.findMergedAnnotation(method, CustomCache.class);
			if (cacheAnn != null && !method.isBridge()) {
				allCachedMethodList.add(method);
			}
		}

        return bean;
    }

	/**
	 * 当系统中所有 bean 加载成功后才会执行
	 */
	@Override
    public final void afterSingletonsInstantiated() {
        if (allCachedMethodList.isEmpty()) {
            return;
        }

        // TODO

		allCachedMethodList.clear();
		allCachedMethodList = null;
    }


}