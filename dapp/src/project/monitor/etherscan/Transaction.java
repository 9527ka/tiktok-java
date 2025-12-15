package project.monitor.etherscan;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Transaction  implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3269375455620589242L;
	/**
	 * 时间
	 */
	private String timeStamp;
	/**
	 * 交易的哈希值
	 */
	private String hash;
	private Integer nonce;
	private String from;
	private String to;
	/**
	 * 交易量，单位为Wei.(单位是ETHER)
	 */
	private Double value;
	/**
	 * 0无数，1发生错误
	 */
	private String isError;
	/**
	 * 交易状态，要进一步确认值 含义
	 * 1.成功，0.失败 ，"":pedding(未验证)
	 */
	private String txreceipt_status;
	/**
	 * 节点地址，固定的
	 */
	private String contractAddress;

	private String input;
	/**
	 * input里对应的合约method
	 */
	private String inputMethod;
	
	/**
	 * input根据不同的method解析出对应的值
	 * 公有属性 method，
	 * <p>	授权:approve</br>
			授权地址 key:approve_address</br>
			授权金额(具体的金额换算根据合约decimal决定) approve_value</br>
	 * </p>
	 */
	private Map<String,Object> inputValueMap = new HashMap<String, Object>();

	public String getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	public Integer getNonce() {
		return nonce;
	}

	public void setNonce(Integer nonce) {
		this.nonce = nonce;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public Double getValue() {
		return value;
	}

	public void setValue(Double value) {
		this.value = value;
	}

	public String getContractAddress() {
		return contractAddress;
	}

	public void setContractAddress(String contractAddress) {
		this.contractAddress = contractAddress;
	}

	public String getIsError() {
		return isError;
	}

	public void setIsError(String isError) {
		this.isError = isError;
	}

	public String getTxreceipt_status() {
		return txreceipt_status;
	}

	public void setTxreceipt_status(String txreceipt_status) {
		this.txreceipt_status = txreceipt_status;
	}

	public String getInput() {
		return input;
	}

	public void setInput(String input) {
		this.input = input;
	}

	public String getInputMethod() {
		return inputMethod;
	}

	public Map<String, Object> getInputValueMap() {
		return inputValueMap;
	}

	public void setInputMethod(String inputMethod) {
		this.inputMethod = inputMethod;
	}

	public void setInputValueMap(Map<String, Object> inputValueMap) {
		this.inputValueMap = inputValueMap;
	}

}
