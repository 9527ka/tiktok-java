package project.party.model;

import java.io.Serializable;

import kernel.bo.EntityObject;

public class UserRecom extends EntityObject<String> {
	private static final long serialVersionUID = 4306215956505507789L;
	private String partyId;
	/**
	 * 推荐人
	 */
	private String reco_id;

	public String getPartyId() {
		return this.partyId;
	}

	public void setPartyId(String partyId) {
		this.partyId = partyId;
	}

	public String getReco_id() {
		return this.reco_id;
	}

	public void setReco_id(String reco_id) {
		this.reco_id = reco_id;
	}
}
