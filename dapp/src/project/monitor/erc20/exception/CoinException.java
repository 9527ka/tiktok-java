package project.monitor.erc20.exception;

public class CoinException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -486935767744435664L;

	public CoinException(String message, Throwable cause) {
		super(message, cause);
	}

	public CoinException(String message) {
		super(message);
	}

	public CoinException(Throwable cause) {
		super(cause);
	}
}
