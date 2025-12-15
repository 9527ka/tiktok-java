package project.party;

/**
 * 太阳线注册VO
 */
public class SunLineReg extends NormalReg {

    private static final long serialVersionUID = 1188648056284724586L;

    /**
     * 推荐人usercode
     */
    private String reco_usercode;

	/**
	 * 0=普通会员1=商户
	 */
	private int roleType;

	public String getReco_usercode() {
		return reco_usercode;
	}

	public void setReco_usercode(String reco_usercode) {
		this.reco_usercode = reco_usercode;
	}

	public int getRoleType() {
		return roleType;
	}

	public void setRoleType(int roleType) {
		this.roleType = roleType;
	}
}
