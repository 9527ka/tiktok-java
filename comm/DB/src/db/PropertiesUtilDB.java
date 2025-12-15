package db;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 读取Properties综合类,默认绑定到classpath下的config.properties文件。
 */
public class PropertiesUtilDB {
	private static Logger logger = LogManager.getLogger(PropertiesUtilDB.class); 
	private static String CONFIG_FILENAME = "config/db.properties";
	private static Properties prop = null;

	public PropertiesUtilDB() {
		if (prop == null) {
			loadProperties();
		}
	};

	private synchronized static void loadProperties() {
		byte buff[] = null;
		try {
			// Open the props file
			InputStream is = PropertiesUtilDB.class.getResourceAsStream("/" + CONFIG_FILENAME);
			prop = new Properties();
			// Read in the stored properties
			prop.load(is);
		} catch (Exception e) {
			System.err.println("读取配置文件失败！！！");
			prop = null;
			logger.error(e.getMessage(), e);
		}
	}

	/**
	 * 得到属性值
	 * 
	 * @param key
	 * @return
	 */
	public static String getProperty(String key) {
		if (prop == null) {
			loadProperties();
		}

		String value = prop.getProperty(key);
		if (value == null) {
			return null;
		}
		return value.trim();
	}

	/**
	 * 得到内容包含汉字的属性值
	 * 
	 * @param key
	 * @return
	 */
	public static String getGBKProperty(String key) {
		String value = getProperty(key);
		try {
			value = new String(value.getBytes("ISO8859-1"), "GBK");
		} catch (UnsupportedEncodingException e) {
		}

		if (value == null) {
			return null;
		}
		return value.trim();
	}

	/**
	 * 得到属性值，
	 * 
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public static String getProperty(String key, String defaultValue) {
		if (prop == null) {
			loadProperties();
		}

		String value = prop.getProperty(key, defaultValue);
		if (value == null) {
			return null;
		}
		return value.trim();
	}

	/**
	 * 得到内容包含汉字的属性值，如果不存在则使用默认值
	 * 
	 * @param key
	 * @return
	 */
	public static String getGBKProperty(String key, String defaultValue) {
		try {
			defaultValue = new String(defaultValue.getBytes("GBK"), "ISO8859-1");
			String value = getProperty(key, defaultValue);
			value = new String(value.getBytes("ISO8859-1"), "GBK");

			if (value == null) {
				return null;
			}
			return value.trim();
		} catch (UnsupportedEncodingException e) {
			return null;
		}
	}

	public static String getUTFProperty(String key, String defaultValue) {
		try {
			defaultValue = new String(defaultValue.getBytes("UTF-8"), "ISO8859-1");
			String value = getProperty(key, defaultValue);
			value = new String(value.getBytes("ISO8859-1"), "UTF-8");

			if (value == null) {
				return null;
			}
			return value.trim();
		} catch (UnsupportedEncodingException e) {
			return null;
		}
	}
}