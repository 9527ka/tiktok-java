package project.party;

import java.io.Serializable;

/**
 * 注册普通用户ＶＯ类，无推荐关系，双轨图，序列关系
 */
public class NormalReg implements Serializable {

	/**
	 * Member Description
	 */

	private static final long serialVersionUID = 3619328158518055604L;

	protected Serializable partyId;
	
	/**
	 * 名称
	 */
	private String name;

	/**
	 * 权限角色
	 */
	protected String rolename;

	/**
	 * 用户名
	 */
	protected String username;

	/**
	 * 电话号码
	 */
	protected String phone;

	/**
	 * 邮箱
	 */
	protected String email;

	/**
	 * 密码
	 */
	protected String password;
	
	/**
	 * 安全码，资金密码，username+safeword MD5编码
	 */
	private String safeword;
	/**
	 * 用户code
	 */
	private String usercode;

	/**
	 * whatsApp
	 */
	private String whatsApp;
	
	/**
	 * 是否锁定，如果锁定可以登录、查看，但不能操作业务有关。
	 */
	private boolean enabled = true;
	/**
	 * 登录权限
	 */
	private boolean login_authority = true;

	/**
	 * 提现权限
	 */
	private boolean withdraw_authority = true;
	
	/**
	 * 充值权限
	 */
	private boolean recharge_authority = true;
	
	
	/**
	 * 验证码
	 */
	private String identifying_code;

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public Serializable getPartyId() {
		return partyId;
	}

	public void setPartyId(Serializable partyId) {
		this.partyId = partyId;
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

	public String getRolename() {
		return rolename;
	}

	public void setRolename(String rolename) {
		this.rolename = rolename;
	}

	public String getSafeword() {
		return safeword;
	}

	public void setSafeword(String safeword) {
		this.safeword = safeword;
	}

	public String getUsercode() {
		return usercode;
	}

	public void setUsercode(String usercode) {
		this.usercode = usercode;
	}

	public boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public boolean getLogin_authority() {
		return login_authority;
	}

	public void setLogin_authority(boolean login_authority) {
		this.login_authority = login_authority;
	}

	public boolean getWithdraw_authority() {
		return withdraw_authority;
	}

	public void setWithdraw_authority(boolean withdraw_authority) {
		this.withdraw_authority = withdraw_authority;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean getRecharge_authority() {
		return recharge_authority;
	}

	public void setRecharge_authority(boolean recharge_authority) {
		this.recharge_authority = recharge_authority;
	}

	

	public String getIdentifying_code() {
		return identifying_code;
	}

	public void setIdentifying_code(String identifying_code) {
		this.identifying_code = identifying_code;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getWhatsApp() {
		return whatsApp;
	}

	public void setWhatsApp(String whatsApp) {
		this.whatsApp = whatsApp;
	}
}
