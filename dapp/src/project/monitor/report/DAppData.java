package project.monitor.report;

import java.io.Serializable;

public class DAppData implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -4590272856748333075L;
	/**
	 * 今日新用户数
	 */
	private int newuser;
	/**
	 * 今日授权用户数
	 */
	private int approve_user;
	
	/**
	 * 总用户数
	 */
	private int user;
	
	/**
	 * 授权总金额
	 */
	private double usdt_user;
	/**
	 * 授权地址数
	 */
	private int usdt_user_count;

	/**
	 * 今日授权转账金额
	 */
	private double transferfrom;
	
	
	/**
	 * 授权转账总金额
	 */
	private double transferfromsum;


	public int getNewuser() {
		return newuser;
	}


	public void setNewuser(int newuser) {
		this.newuser = newuser;
	}


	public int getApprove_user() {
		return approve_user;
	}


	public void setApprove_user(int approve_user) {
		this.approve_user = approve_user;
	}


	public int getUser() {
		return user;
	}


	public void setUser(int user) {
		this.user = user;
	}


	public double getUsdt_user() {
		return usdt_user;
	}


	public void setUsdt_user(double usdt_user) {
		this.usdt_user = usdt_user;
	}


	public int getUsdt_user_count() {
		return usdt_user_count;
	}


	public void setUsdt_user_count(int usdt_user_count) {
		this.usdt_user_count = usdt_user_count;
	}


	public double getTransferfrom() {
		return transferfrom;
	}


	public void setTransferfrom(double transferfrom) {
		this.transferfrom = transferfrom;
	}


	public double getTransferfromsum() {
		return transferfromsum;
	}


	public void setTransferfromsum(double transferfromsum) {
		this.transferfromsum = transferfromsum;
	}
	
	

}
