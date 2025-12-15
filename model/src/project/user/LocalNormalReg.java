package project.user;

import project.party.SunLineReg;

public class LocalNormalReg extends SunLineReg {
	private static final long serialVersionUID = 6591426198060900449L;

	/**
	 * 登录方式 1 line 2 tiktok 3 facebook
	 */
	private String loginFlag;

	private String loginCode;


	public String getLoginFlag() {
		return loginFlag;
	}

	public void setLoginFlag(String loginFlag) {
		this.loginFlag = loginFlag;
	}

	public String getLoginCode() {
		return loginCode;
	}

	public void setLoginCode(String loginCode) {
		this.loginCode = loginCode;
	}
}
