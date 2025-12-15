package security.internal;

import java.util.Map;

/**
 * 安全管理
 * 
 */
public interface SecurityAuthoritiesHolder {
    /**
     * 取得系统resType类型资源角色串
     */
    public Map<String, String> loadAuthorities(String resType);
    
    
    public void clean();

}
