package security;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.springframework.security.GrantedAuthority;
import org.springframework.security.GrantedAuthorityImpl;
import org.springframework.security.userdetails.UserDetails;

import kernel.bo.EntityObject;

/**
 * 系统用户.
 * 
 */
public class SecUser extends EntityObject<String> implements UserDetails {

	private static final long serialVersionUID = 8585484879324416599L;

	private String username;// 登陆用户名

	private String password;// 密码

	private String safeword;// 资金密码

	private String partyId;// 关联 party entity

	private Date createTime;

	private String name;

	private String email;

	private boolean accountNonExpired = true;// 账户是否过期。暂默认值为true，后期根据业务修改

	private boolean accountNonLocked = true;;// 账户是否锁定。暂默认值为true，后期根据业务修改

	private boolean credentialsNonExpired = true;;// 账户密码是否过期。暂默认值为true，后期根据业务修改

	private boolean enabled = true;// 账户是否有效。暂默认值为true，后期根据业务修改

	private String defaultLocale;// 默认Locale

	private boolean isdel = false;

	private Set<Role> roles = new HashSet<Role>();// 角色

	private String remarks;

	private String roleName;// 角色名
	/**
	 * 最后登录时间
	 */
	private Date last_loginTime;
	/**
	 * 登陆Ip
	 * 
	 * @return
	 */
	private String login_ip;

	/**
	 * 谷歌验证器
	 */
	private String google_auth_secret;
	/**
	 * 谷歌验证器是否绑定
	 */
	private boolean google_auth_bind = false;

	private String linecode;

	private String tiktokcode;
	private String facebookcode;

	public String getLinecode() {
		return linecode;
	}

	public void setLinecode(String linecode) {
		this.linecode = linecode;
	}

	public String getTiktokcode() {
		return tiktokcode;
	}

	public void setTiktokcode(String tiktokcode) {
		this.tiktokcode = tiktokcode;
	}

	public String getFacebookcode() {
		return facebookcode;
	}

	public void setFacebookcode(String facebookcode) {
		this.facebookcode = facebookcode;
	}

	public GrantedAuthority[] getAuthorities() {
		List<GrantedAuthority> grantedAuthorities = new ArrayList<GrantedAuthority>(roles.size());
		for (Role role : roles) {
			grantedAuthorities.add(new GrantedAuthorityImpl("ROLE_" + role.getRoleName()));
		}

		return grantedAuthorities.toArray(new GrantedAuthority[roles.size()]);

	}

	public String getRoleAuthorities() {
		List<String> roleName = new ArrayList<String>();
		for (Role role : roles) {
			roleName.add(role.getRoleName());
		}
		return StringUtils.join(roleName.iterator(), ",");
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Set<Role> getRoles() {
		return roles;
	}

	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public boolean getEnabled() {
		return isEnabled();
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public boolean isAccountNonExpired() {
		return accountNonExpired;
	}

	public void setAccountNonExpired(boolean accountNonExpired) {
		this.accountNonExpired = accountNonExpired;
	}

	public boolean isAccountNonLocked() {
		return accountNonLocked;
	}

	public void setAccountNonLocked(boolean accountNonLocked) {
		this.accountNonLocked = accountNonLocked;
	}

	public boolean isCredentialsNonExpired() {
		return credentialsNonExpired;
	}

	public void setCredentialsNonExpired(boolean credentialsNonExpired) {
		this.credentialsNonExpired = credentialsNonExpired;
	}

	public String getDefaultLocale() {
		return defaultLocale;
	}

	public void setDefaultLocale(String defaultLocale) {
		this.defaultLocale = defaultLocale;
	}

	public Locale getLocale() {
		if (this.getDefaultLocale() != null) {
			return new Locale(this.getDefaultLocale());
		}
		return null;
	}

	public String getPartyId() {
		return partyId;
	}

	public void setPartyId(String partyId) {
		this.partyId = partyId;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public boolean getIsdel() {
		return isdel;
	}

	public void setIsdel(boolean isdel) {
		this.isdel = isdel;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public String getSafeword() {
		return safeword;
	}

	public void setSafeword(String safeword) {
		this.safeword = safeword;
	}

	public Date getLast_loginTime() {
		return last_loginTime;
	}

	public String getLogin_ip() {
		return login_ip;
	}

	public void setLast_loginTime(Date last_loginTime) {
		this.last_loginTime = last_loginTime;
	}

	public void setLogin_ip(String login_ip) {
		this.login_ip = login_ip;
	}

	public String getGoogle_auth_secret() {
		return google_auth_secret;
	}

	public boolean isGoogle_auth_bind() {
		return google_auth_bind;
	}

	public void setGoogle_auth_secret(String google_auth_secret) {
		this.google_auth_secret = google_auth_secret;
	}

	public void setGoogle_auth_bind(boolean google_auth_bind) {
		this.google_auth_bind = google_auth_bind;
	}

}
