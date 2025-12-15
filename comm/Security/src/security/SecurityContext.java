package security;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SecurityContext implements Serializable {

	private static final long serialVersionUID = 2225996820449948264L;
	
	/**
	 * SecUser
	 */
	private Object principal;

	private String username;

	private String partyId;

	private List<String> roles = new ArrayList<String>();

	public Object getPrincipal() {
		return principal;
	}

	public void setPrincipal(Object principal) {
		this.principal = principal;
	}

	public String getUsername() {
		return username;
	}

	public String getPartyId() {
		return partyId;
	}


	public void setUsername(String username) {
		this.username = username;
	}

	public void setPartyId(String partyId) {
		this.partyId = partyId;
	}

	public List<String> getRoles() {
		return roles;
	}

	public void setRoles(List<String> roles) {
		this.roles = roles;
	}

}
