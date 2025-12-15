package project.monitor.erc20.dto;

public class TransactionResponseDto {

	/**
	 * 自定义200为成功
	 */
	public static final String CODE_LOCAL_SUCCESS ="200";
	/**
	 * 自定义-1为本地失败
	 */
	public static final String CODE_LOCAL_FAIL ="-1";
	/**
	 * 交易返回的code，
	 * 自定义200为成功
	 * 自定义-1为本地失败
	 * 其他为失败code
	 */
	private String code;
	/**
	 * 交易发起时产生hash码
	 */
	private String hash;
	/**
	 * 错误时会返回错误信息
	 */
	private String error;
	
	public TransactionResponseDto() {
	}
	
	public TransactionResponseDto(String code, String hash, String error) {
		this.code = code;
		this.hash = hash;
		this.error = error;
	}
	public TransactionResponseDto(String hash) {
		this.code = CODE_LOCAL_SUCCESS;
		this.hash = hash;
	}
	public TransactionResponseDto(String code, String error) {
		this.code = code;
		this.error = error;
	}
	public String getCode() {
		return code;
	}
	public String getHash() {
		return hash;
	}
	public String getError() {
		return error;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public void setHash(String hash) {
		this.hash = hash;
	}
	public void setError(String error) {
		this.error = error;
	}
	
	
}
