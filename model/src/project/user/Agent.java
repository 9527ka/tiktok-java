package project.user;

import java.io.Serializable;

import kernel.bo.EntityObject;

public class Agent extends EntityObject<String> {
	private static final long serialVersionUID = 8554751796911238962L;

	private String partyId;

	/**
	 * 父节点
	 */
	private String parent_partyId;

	public String getPartyId() {
		return partyId;
	}

	public void setPartyId(String partyId) {
		this.partyId = partyId;
	}

	public String getParent_partyId() {
		return parent_partyId;
	}

	public void setParent_partyId(String parent_partyId) {
		this.parent_partyId = parent_partyId;
	}



}
