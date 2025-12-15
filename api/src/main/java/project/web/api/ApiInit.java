package project.web.api;

import ext.translate.Locales;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import project.mall.auto.AutoConfig;
import project.syspara.SysparaService;

@Configuration
public class ApiInit implements ApplicationListener<ContextRefreshedEvent> {
    Logger logger = LoggerFactory.getLogger(ApiInit.class);
    @Autowired
    protected SysparaService sysparaService;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        logger.info("Api server is init...");
        Locales.setLocalDir("./locale");
        logger.info("locale has init. locale path: {}/locale",System.getProperty("user.dir"));

        AutoConfig.configure(sysparaService);

    }
}
