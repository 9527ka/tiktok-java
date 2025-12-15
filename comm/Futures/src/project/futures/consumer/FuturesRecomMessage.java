package project.futures.consumer;

import java.util.Date;

public class FuturesRecomMessage {

	private String orderNo;
	
	private String partyId;
	
	private double volume;
	
	private Date createTime;

	public String getOrderNo() {
		return orderNo;
	}

	public String getPartyId() {
		return partyId;
	}

	public double getVolume() {
		return volume;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public void setPartyId(String partyId) {
		this.partyId = partyId;
	}

	public void setVolume(double volume) {
		this.volume = volume;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public FuturesRecomMessage(String orderNo, String partyId, double volume,Date createTime) {
		this.orderNo = orderNo;
		this.partyId = partyId;
		this.volume = volume;
		this.createTime = createTime;
	}
	
	public FuturesRecomMessage() {
	}
}
