package project.mall.activity.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.env.Environment;

/**
 * 注意：本扫描器只提取 ActivityHandler 类型的实现类
 * 
 * @author caster
 */
public class ActivityHandlerScanningProvider extends ClassPathScanningCandidateComponentProvider {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * Protected constructor for flexible subclass initialization.
     * @since 4.3.6
     */
    protected ActivityHandlerScanningProvider() {
    }

    /**
     * Create a ClassPathScanningCandidateComponentProvider with a {@link StandardEnvironment}.
     * @param useDefaultFilters whether to register the default filters for the
     * {@link Component @Component}, {@link Repository @Repository},
     * {@link Service @Service}, and {@link Controller @Controller}
     * stereotype annotations
     * @see #registerDefaultFilters()
     */
    public ActivityHandlerScanningProvider(boolean useDefaultFilters) {
        super(useDefaultFilters);
    }

    /**
     * Create a ClassPathScanningCandidateComponentProvider with the given {@link Environment}.
     * @param useDefaultFilters whether to register the default filters for the
     * {@link Component @Component}, {@link Repository @Repository},
     * {@link Service @Service}, and {@link Controller @Controller}
     * stereotype annotations
     * @param environment the Environment to use
     * @see #registerDefaultFilters()
     */
    public ActivityHandlerScanningProvider(boolean useDefaultFilters, Environment environment) {
        super(useDefaultFilters, environment);
    }

//    /**
//     * 只要接口类
//     * @param beanDefinition
//     * @return
//     */
//    protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
//        AnnotationMetadata metadata = beanDefinition.getMetadata();
//        return (metadata.isIndependent() && metadata.isInterface());
//    }

}