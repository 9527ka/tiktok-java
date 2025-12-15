/**
 * 
 */
package kernel.context;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


public abstract class ApplicationContextHolder {
    

    public static AbstractApplicationContext applicationContext = null;

    public static void launcher() {
        
        applicationContext = new ClassPathXmlApplicationContext("spring/applicationContext*.xml");
        applicationContext.registerShutdownHook();
    }

    public static Object getBean(String name) {
        if (applicationContext == null) {
            launcher();
        }
        return applicationContext.getBean(name);
    }

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

}
