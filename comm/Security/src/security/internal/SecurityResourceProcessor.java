package security.internal;

import java.util.List;

public interface SecurityResourceProcessor {

    public boolean isResourceAccessible(String resource,List<String> roles);
    
    public boolean isResourceAccessible(String resource, String type,List<String> roles);
    
    public boolean isRolesAccessible(String verifyroles, List<String> roles);
    
    
    public boolean isUrlAccessible(String servletPath, List<String> roles);

}
