package security;

public abstract class Constants {
    /**
     * Regex或Ant，Regex支持正则表达式
     */
    public static final String URLMATCHER_PATH_TYPE = "Ant";

    /**
     * 使用Regex或Ant，是否转小写后再验证
     */
    public static final boolean LOWERCASECOMPARISONS = true;

    /**
     * 是否保护所有资源，true，则所有资源默认为受保护， false则只有声明了并且与权限挂钩了的资源才会受保护
     */
    public static final boolean ISPROTECTALLRESOURCE = false;
    
    public static final String RESTYPE_URL = "URL";
    
    public static final String RESTYPE_OPERATION = "OPERATION";
    
    
    public static final String ROLE_ADMIN_NAME = "ADMIN";
    
    public static final String ROLE_TENANT_NAME = "TENANT";

}
