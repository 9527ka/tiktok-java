package project.mall.auto;

import ext.Strings;
import ext.Types;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import project.syspara.SysparaService;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.util.Properties;

public class AutoConfig {

    private static String CONFIG_FILENAME = "config.properties";
    private static Properties prop = null;
    private static SysparaService sysparaService;

    public static void configure(SysparaService service){
        if(sysparaService == null) {
            sysparaService = service;
        }
    }


    public static String getBaseUrl(){
        String domain = getDomain(getSysparaService());
        return String.format("https://%s", domain);
    }

    private static SysparaService getSysparaService() {
        if(sysparaService == null){
            throw new RuntimeException("not initialize syspara service");
        }
        return sysparaService;
    }



    private static String getDomain(SysparaService sysparaService) {
        String domain = sysparaService.findString("mall_site_domain");
        if(Strings.isNullOrEmpty(domain)){
            ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            HttpServletRequest req = servletRequestAttributes.getRequest();
            domain = req.getHeader("HOST");
        }
        return domain.replaceFirst("https*://", "");
    }


    public static String attribute(String key){
        try {
            //Open the props file
            InputStream is= AutoConfig.class.getClassLoader().getResourceAsStream(CONFIG_FILENAME);
            prop = new Properties();
            //Read in the stored properties
            prop.load(is);
        }
        catch (Exception e) {
            System.err.println("读取配置文件失败！！！");
            prop = null;
        }
        return prop.getProperty(key);
    }
    /**
     * 商品导入url
     */
    public static String goodsUrl = "https://qfushj.site/api/item/collect";

    /**
     * 分销接口
     */
    public static String distribution = "https://qfushj.site/api/promote/distribution";

    public static String getAdminUrl(){
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpServletRequest req = servletRequestAttributes.getRequest();
        String host = req.getHeader("HOST");
        if(host.contains("localhost") || host.contains("127.0.0.1")) {
            return Types.orValue(req.getContextPath(),"");
        }
        return String.format("https://%s/admin", host);
    }

    public static String joinAdminUrl(String path){
        String adminUrl = getAdminUrl();
        if(adminUrl.endsWith("/")){
            adminUrl = adminUrl.substring(0,adminUrl.length()-1);
        }
        return adminUrl+(!path.startsWith("/")?"/"+path:path);
    }

    public static String getBaseDir() {
        return System.getProperty("user.dir");
    }
}