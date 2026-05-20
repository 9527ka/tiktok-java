package project.web.admin.config;

import ext.translate.Locales;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import project.mall.auto.AutoConfig;
import project.syspara.SysparaService;

/**
 * Admin module init: 在 Spring context 就绪后立即初始化 AutoConfig 静态字段,
 * 这样 JSP 调 AutoConfig.getBaseUrl() / getAdminUrl() 不会因为没登录而 NPE.
 *
 * 原来只在 LocalLoginSuccessController 登录成功后才 configure, 重启 tomcat 后
 * 没登录过的请求会触发 "not initialize syspara service" 错误.
 *
 * 通过 applicationContext-mall_admin.xml 声明为 bean (root context),
 * 而不是 @Configuration 自动扫描 (会被放进 child mvc context).
 */
public class AdminInit implements ApplicationListener<ContextRefreshedEvent> {

    private final Logger logger = LoggerFactory.getLogger(AdminInit.class);

    private SysparaService sysparaService;

    public void setSysparaService(SysparaService sysparaService) {
        this.sysparaService = sysparaService;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        // 多次刷新只配置一次
        if (sysparaService == null) {
            logger.warn("AdminInit: sysparaService not injected, skip");
            return;
        }
        logger.info("Admin server is init...");
        try {
            Locales.setLocalDir("./locale");
            logger.info("locale path: {}/locale", System.getProperty("user.dir"));
        } catch (Throwable t) {
            logger.warn("locale init failed: {}", t.getMessage());
        }
        AutoConfig.configure(sysparaService);
        logger.info("AutoConfig configured with sysparaService");
    }
}
