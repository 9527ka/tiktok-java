package project.cms;



import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

import kernel.util.JsonUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import project.mall.seller.dto.MallLevelCondExpr;

/**
 * 读取Properties综合类,默认绑定到classpath下的config.properties文件。
 */
public class PropertiesUtilCms {
    private static Log log = LogFactory.getLog(PropertiesUtilCms.class);
    private static String CONFIG_FILENAME = "config/cms.properties";
    private static Properties prop = null;
    
    public PropertiesUtilCms() {
        if (prop == null) {
            loadProperties();
        }
    };
    
    private synchronized static void loadProperties() {
        byte buff[]=null;
        try {
            //Open the props file
            InputStream is=PropertiesUtilCms.class.getResourceAsStream("/" + CONFIG_FILENAME);
            prop = new Properties();
            //Read in the stored properties
            prop.load(is);
        }
        catch (Exception e) {
            System.err.println("读取配置文件失败！！！");
            prop = null;
            log.error(e.getMessage(), e);
        }
    }
    
    /**
     * 得到属性值
     * @param key
     * @return
     */
    public static String getProperty(String key) {
        if (prop == null) {
            loadProperties();
        }
        
        String value = prop.getProperty(key);
        if(value ==null){          
            return null;
        }    
        return value.trim();    
    }
    
    /**
     * 得到内容包含汉字的属性值
     * @param key
     * @return
     */
    public static String getGBKProperty(String key) {
        String value = getProperty(key);
        try {
            value = new String(value.getBytes("ISO8859-1"),"GBK");
        } catch (UnsupportedEncodingException e) {         
        }
        
        if(value ==null){          
            return null;
        }
        return value.trim();
    }
    
    /**
     * 得到属性值，
     * @param key
     * @param defaultValue
     * @return
     */
    public static String getProperty(String key, String defaultValue) {
        if (prop == null) {
            loadProperties();
        }
        
        String value = prop.getProperty(key, defaultValue);
        if(value ==null){          
            return null;
        }    
        return value.trim();   
    }
    
    /**
     * 得到内容包含汉字的属性值，如果不存在则使用默认值
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
			defaultValue = new String(defaultValue.getBytes("UTF-8"),
					"ISO8859-1");
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
    
    public static void main(String[] args) {

        String json = "{\"params\":[{\"code\":\"rechargeAmount\",\"title\":\"运行资金\",\"value\":5000},{\"code\":\"popularizeUserCount\",\"title\":\"分店数\",\"value\":3}],\"expression\":\"popularizeUserCount >= 3 || rechargeAmount >= 5000\"}";
        MallLevelCondExpr mallLevelCondExpr = JsonUtils.json2Object(json, MallLevelCondExpr.class);
    }
}