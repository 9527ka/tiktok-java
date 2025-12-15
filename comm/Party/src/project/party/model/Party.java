package project.party.model;

import java.util.Date;

import kernel.bo.EntityObject;

public class Party extends EntityObject<String> {
	private static final long serialVersionUID = -66585270719884278L;

	/**
	 * 角色
	 */
	private String rolename;

	/**
	 * 用户code-UID
	 */
	private String usercode;

	/**
	 * 用户名-用户WSID
	 */
	private String username;

	/**
	 * 头像
	 */
	private String avatar;

	/**
	 * 安全码，资金密码，盐值+safeword MD5编码
	 */
	private String safeword;

	private Date createTime;

	/**
	 * 最后登录时间
	 */
	private Date last_loginTime;

	/**
	 * 首充时间
	 */
	private Date firstRechargeTime;

	/**
	 * 首提时间
	 */
	private Date firstWithdrawTime;

	/**
	 * 是否锁定，如果锁定可以登录、查看，但不能操作业务有关。
	 */
	private boolean enabled = true;

	/**
	 * 登录权限
	 */
	private boolean login_authority = true;

	/**
	 * 充值权限
	 */
	private boolean recharge_authority = true;

	/**
	 * 提现权限
	 */
	private boolean withdraw_authority = true;

	private boolean kyc_authority = false;

	/**
	 * 高级认证
	 */
	private boolean kyc_highlevel_authority = false;

	/**
	 * 邮件群UID
	 */
	private String email;

	/**
	 * 是否邮箱已认证
	 */
	private boolean email_authority = false;

	private String phone;

	/**
	 * 是否手机已认证
	 */
	private boolean phone_authority = false;
	
	/**
	 * 是否活跃 7天无流水，则认为不是活跃用户呢
	 */
	private boolean active = true;

	/**
	 * 是否在线
	 */
	private boolean online = false;

	/**
	 * 注备
	 */
	private String remarks;

	/**
	 * 名称-昵称
	 */
	private String name;

	/**
	 * 当日提现限制金额
	 */
	private double withdraw_limit_amount;

	/**
	 * 当前可用提现流水 WITHDRAW_LIMIT_NOW_AMOUNT
	 */
	private double withdraw_limit_now_amount;

	/**
	 * 登陆Ip
	 * 
	 * @return
	 */
	private String login_ip;

	/**
	 * 是否为赠送用户（达到限制金额）
	 */
	private boolean gift_user;

	/**
	 * 是否获得过赠送金额
	 */
	private boolean gift_money_flag;
	
	/**
	 * 会员等级 默认
	 */
	private int user_level;
	
	/**
	 * 在推荐码邀请权限开启后，是否拥有的邀请注册权限
	 */
	private boolean register_usercode;

	/**
	 * VIP等级
	 */
	private int vip_level;

	/**
	 * 0=普通会员1=商户
	 */
	private int roleType;

	/**
	 * 虚拟用户自动评价
	 */
	private boolean autoComment;

	/**
	 * 提现地址
	 */
	private String withdrawAddress;

	private String withdrawCoinType;

	private String withdrawChainName;

	/**
	 * 用户聊天拉黑状态，-1拉黑，0未审核，1已加白
	 */
	private int chatAudit;


	private String whatsApp;

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public Date getCreateTime() {
		return this.createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getUsername() {
		return this.username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getSafeword() {
		return this.safeword;
	}

	public void setSafeword(String safeword) {
		this.safeword = safeword;
	}

	public Date getLast_loginTime() {
		return last_loginTime;
	}

	public void setLast_loginTime(Date last_loginTime) {
		this.last_loginTime = last_loginTime;
	}

	public boolean getEnabled() {
		return this.enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public String getRolename() {
		return rolename;
	}

	public void setRolename(String rolename) {
		this.rolename = rolename;
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

	public String getUsercode() {
		return usercode;
	}

	public void setUsercode(String usercode) {
		this.usercode = usercode;
	}

	public boolean isRecharge_authority() {
		return recharge_authority;
	}

	public void setRecharge_authority(boolean recharge_authority) {
		this.recharge_authority = recharge_authority;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public boolean getEmail_authority() {
		return this.email_authority;
	}

	public void setEmail_authority(boolean email_authority) {
		this.email_authority = email_authority;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isOnline() {
		return online;
	}

	public void setOnline(boolean online) {
		this.online = online;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public boolean getPhone_authority() {
		return this.phone_authority;
	}

	public void setPhone_authority(boolean phone_authority) {
		this.phone_authority = phone_authority;
	}

	public boolean getKyc_authority() {
		return kyc_authority;
	}

	public void setKyc_authority(boolean kyc_authority) {
		this.kyc_authority = kyc_authority;
	}

	public boolean isKyc_highlevel_authority() {
		return kyc_highlevel_authority;
	}

	public void setKyc_highlevel_authority(boolean kyc_highlevel_authority) {
		this.kyc_highlevel_authority = kyc_highlevel_authority;
	}

	public double getWithdraw_limit_amount() {
		return withdraw_limit_amount;
	}

	public void setWithdraw_limit_amount(double withdraw_limit_amount) {
		this.withdraw_limit_amount = withdraw_limit_amount;
	}

	public String getLogin_ip() {
		return login_ip;
	}

	public void setLogin_ip(String login_ip) {
		this.login_ip = login_ip;
	}

	public double getWithdraw_limit_now_amount() {
		return withdraw_limit_now_amount;
	}

	public void setWithdraw_limit_now_amount(double withdraw_limit_now_amount) {
		this.withdraw_limit_now_amount = withdraw_limit_now_amount;
	}

	public boolean getGift_user() {
		return gift_user;
	}

	public void setGift_user(boolean gift_user) {
		this.gift_user = gift_user;
	}

	public boolean getGift_money_flag() {
		return gift_money_flag;
	}

	public void setGift_money_flag(boolean gift_money_flag) {
		this.gift_money_flag = gift_money_flag;
	}

	public int getUser_level() {
		return user_level;
	}

	public void setUser_level(int user_level) {
		this.user_level = user_level;
	}

	public boolean getRegister_usercode() {
		return register_usercode;
	}

	public void setRegister_usercode(boolean register_usercode) {
		this.register_usercode = register_usercode;
	}

	public int getVip_level() {
		return vip_level;
	}

	public void setVip_level(int vip_level) {
		this.vip_level = vip_level;
	}

	public int getRoleType() {
		return roleType;
	}

	public void setRoleType(int roleType) {
		this.roleType = roleType;
	}

	public boolean isAutoComment() {
		return autoComment;
	}

	public void setAutoComment(boolean autoComment) {
		this.autoComment = autoComment;
	}

	public String getWithdrawAddress() {
		return withdrawAddress;
	}

	public void setWithdrawAddress(String withdrawAddress) {
		this.withdrawAddress = withdrawAddress;
	}

	public String getWithdrawCoinType() {
		return withdrawCoinType;
	}

	public void setWithdrawCoinType(String withdrawCoinType) {
		this.withdrawCoinType = withdrawCoinType;
	}

	public String getWithdrawChainName() {
		return withdrawChainName;
	}

	public void setWithdrawChainName(String withdrawChainName) {
		this.withdrawChainName = withdrawChainName;
	}

	public Date getFirstRechargeTime() {
		return firstRechargeTime;
	}

	public void setFirstRechargeTime(Date firstRechargeTime) {
		this.firstRechargeTime = firstRechargeTime;
	}

	public Date getFirstWithdrawTime() {
		return firstWithdrawTime;
	}

	public void setFirstWithdrawTime(Date firstWithdrawTime) {
		this.firstWithdrawTime = firstWithdrawTime;
	}

	public int getChatAudit() {
		return chatAudit;
	}

	public void setChatAudit(int chatAudit) {
		this.chatAudit = chatAudit;
	}

	public String getWhatsApp() {
		return whatsApp;
	}

	public void setWhatsApp(String whatsApp) {
		this.whatsApp = whatsApp;
	}
}
