package kernel.util;

import java.io.IOException;
import java.util.Properties;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;


public class PropertiesLoaderUtils extends org.springframework.core.io.support.PropertiesLoaderUtils {
    
    public static void merge(Properties target, Properties source) {
        target.putAll(source);
    }

    public static Properties loadProperties(String path){
        Properties props = new Properties();
        Resource resource = new ClassPathResource(path);
        try {
			PropertiesLoaderUtils.fillProperties(props, resource);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return props;
    }
}
