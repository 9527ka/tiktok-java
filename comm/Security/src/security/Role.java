package security;

import java.util.HashSet;
import java.util.Set;

import kernel.bo.EntityObject;

/**
 * 角色
 * 
 */
public class Role extends EntityObject<String> {

    private static final long serialVersionUID = 4814486392359827577L;

	private String roleName;// 角色名

	private String descr;// 描述

	private String defaultUrl;// 角色登录成功后首页地址
	
	
	private Set<Resource> resources = new HashSet<Resource>();//资源
	

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public String getDescr() {
		return descr;
	}

	public void setDescr(String descr) {
		this.descr = descr;
	}

	public String getDefaultUrl() {
		return defaultUrl;
	}

	public void setDefaultUrl(String defaultUrl) {
		this.defaultUrl = defaultUrl;
	}

    public Set<Resource> getResources() {
        return resources;
    }

    public void setResources(Set<Resource> resources) {
        this.resources = resources;
    }
	
	

}
