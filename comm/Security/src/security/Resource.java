package security;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import kernel.bo.EntityObject;

/**
 * 资源
 * 
 */
public class Resource extends EntityObject<String> {

    private static final long serialVersionUID = 1L;
    
    public static final String RESOURCE_TYPE_URL = "URL";
    public static final String RESOURCE_TYPE_OPERATION = "OPERATION";

    private String resString;// 资源串

    private String resType;// 资源类型

    private Set<Role> roles = new HashSet<Role>(0);// 资源关联的角色

    /**
     * 获取资源对应的色名
     */
    public String getRoleAuthorities() {
        List<String> roleAuthorities = new ArrayList<String>();
        boolean sign = true;// 标志是否已存在角色
            // 遍历复合资源关联的角色
            for (Role role : roles) {
                // 判断是否存在角色
                for (String roleName : roleAuthorities) {
                    if (roleName.equals("ROLE_" + role.getRoleName())) {
                        sign = false;
                        break;
                    }
                }
                if (sign) {
                    roleAuthorities.add("ROLE_" + role.getRoleName());
                }
        }
        return StringUtils.join(roleAuthorities.iterator(), ",");
    }

    public String getResString() {
        return this.resString;
    }

    public void setResString(String resString) {
        this.resString = resString;
    }

    public String getResType() {
        return this.resType;
    }

    public void setResType(String resType) {
        this.resType = resType;
    }

	public Set<Role> getRoles() {
		return roles;
	}

	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}

}