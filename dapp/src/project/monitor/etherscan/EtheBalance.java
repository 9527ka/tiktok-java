package project.monitor.etherscan;

import java.io.Serializable;

public class EtheBalance  implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1006723713904722210L;
	private String account;
	private Double balance;
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	public Double getBalance() {
		return balance;
	}
	public void setBalance(Double balance) {
		this.balance = balance;
	}
	
	
}
