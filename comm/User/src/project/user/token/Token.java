package project.user.token;

import java.io.Serializable;

import kernel.bo.EntityObject;

public class Token  extends EntityObject<String> {
	private static final long serialVersionUID = -5132505045848059321L;

	private String partyId;
	
	private String token;

	public String getPartyId() {
		return partyId;
	}

	public void setPartyId(String partyId) {
		this.partyId = partyId;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
	
	
}
