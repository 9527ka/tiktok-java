package project.web.api.websocket;

/**
 * 枚举
 * 
 * 不同页面区分webSocket
 */
public enum WebSocketEnum {
	/**
	 * 行情实时价格
	 */
	SOCKET_ENUM_REALTIME("1", "行情实时价格"),

	/**
	 * 近期交易记录
	 */
	SOCKET_ENUM_TRADE("2", "近期交易记录"),

	/**
	 * 市场深度数据
	 */
	SOCKET_ENUM_DEPTH("3", "市场深度数据");

	private String code;
	private String msg;

	WebSocketEnum(String code, String msg) {
		this.code = code;
		this.msg = msg;
	}

	public String getCode() {
		return code;
	}

	public String getMsg() {
		return msg;
	}
}