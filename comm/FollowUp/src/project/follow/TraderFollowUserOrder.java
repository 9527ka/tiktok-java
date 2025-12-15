package project.follow;

import java.io.Serializable;
import java.util.Date;

import kernel.bo.EntityObject;

/**
 * 用户跟随交易员详情表
 */
public class TraderFollowUserOrder extends EntityObject<String> {

	private static final long serialVersionUID = -1617033543659508052L;
	private String partyId;

	/**
	 * 交易员partyId TRADER_PARTYID
	 */
	private String trader_partyId;


	/**
	 * 用户合约持仓单号订单号 USER_ORDER_NO
	 */
	private String user_order_no;
	/**
	 * 当前订单张数
	 */
	private double volume;
	
	/**
	 * 跟随的交易员合约持仓单号 TRADER_ORDER_NO
	 */
	private String trader_order_no;

	/**
	 * 状态。submitted 已提交（持仓），canceled 已撤销， created 完成（平仓）
	 */
	private String state = "submitted";

	/**
	 * 下单时间----CREATE_TIME
	 */
	private Date create_time;

	public String getPartyId() {
		return partyId;
	}

	public void setPartyId(String partyId) {
		this.partyId = partyId;
	}

	public Date getCreate_time() {
		return create_time;
	}

	public void setCreate_time(Date create_time) {
		this.create_time = create_time;
	}

	public String getTrader_partyId() {
		return trader_partyId;
	}

	public void setTrader_partyId(String trader_partyId) {
		this.trader_partyId = trader_partyId;
	}

	public String getUser_order_no() {
		return user_order_no;
	}

	public void setUser_order_no(String user_order_no) {
		this.user_order_no = user_order_no;
	}

	public String getTrader_order_no() {
		return trader_order_no;
	}

	public void setTrader_order_no(String trader_order_no) {
		this.trader_order_no = trader_order_no;
	}

	public double getVolume() {
		return volume;
	}

	public void setVolume(double volume) {
		this.volume = volume;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}




}
