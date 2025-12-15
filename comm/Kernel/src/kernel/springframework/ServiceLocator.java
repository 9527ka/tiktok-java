package kernel.springframework;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ServiceLocator {
	/**
	 * spring应用上下文
	 */
	private static ApplicationContext CONTEXT;

	/**
	 * Method getApplicationContext.
	 *
	 * @return ApplicationContext
	 */
	public static ApplicationContext getApplicationContext() {
		if (CONTEXT == null) {
			CONTEXT = new ClassPathXmlApplicationContext(
					new String[] { "classpath*:spring/*.xml" });
		}
		return CONTEXT;

	}

	/**
	 * Method setApplicationContext.
	 *
	 * @param outcontext ApplicationContext
	 */
	public static void setApplicationContext(ApplicationContext outcontext) {
		CONTEXT = outcontext;
	}

	/**
	 * 获得一个Spring的Bean对象
	 *
	 * @param name
	 * @return
	 */
	public static Object getBean(String name) {
		return getApplicationContext().getBean(name);
	}

	/**
	 * 通过calss 获得一个Spring的Bean对象
	 *
	 * @param T
	 * @return
	 */
	public static <T> T getBean(Class<T> T) {
		return getApplicationContext().getBean(T);
	}
	
	/**
	 * 
	 * @param key ：properties定义的值
	 * @param defaultMessage：取不到值,默认的值
	 * @return
	 */
	public static String getMessage(String key,String defaultMessage){
		return CONTEXT.getMessage(key, null, defaultMessage,null);
	}
	
	/**
	 * 
	 * @param key ：properties定义的值
	 * @return  如果key值 没有，将返回"",
	 */
	public static String getMessage(String key){
		return CONTEXT.getMessage(key, null,"",null);
	}
	
	
	
}
