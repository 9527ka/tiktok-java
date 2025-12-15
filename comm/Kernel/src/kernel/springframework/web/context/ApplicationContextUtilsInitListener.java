package kernel.springframework.web.context;

import javax.servlet.ServletContextEvent;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.WebApplicationContextUtils;

import kernel.springframework.ServiceLocator;


public class ApplicationContextUtilsInitListener extends ContextLoaderListener {
	
	private Logger logger = LogManager.getLogger(ApplicationContextUtilsInitListener.class);
	/**
	 * Method contextInitialized.
	 * 
	 * @param event
	 *            ServletContextEvent
	 * @see javax.servlet.ServletContextListener#contextInitialized(javax.servlet.ServletContextEvent)
	 */
	public void contextInitialized(ServletContextEvent event) {
		super.contextInitialized(event);
		ApplicationContext context = WebApplicationContextUtils
				.getWebApplicationContext(event.getServletContext());
		ServiceLocator.setApplicationContext(context);
	}
	
   @Override
   public void contextDestroyed(ServletContextEvent event) {
	    super.contextDestroyed(event);
	    logger.info("--销毁");
    }
}
