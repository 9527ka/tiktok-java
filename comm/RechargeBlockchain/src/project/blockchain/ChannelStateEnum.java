package project.blockchain;

import java.io.Serializable;

public enum ChannelStateEnum implements Serializable {
	STATE_SUCCESS("0", "成功"), STATE_FAIL("1", "失败"), STATE_UNKNOWN("3", "状态未知"), STATE_OTHER("4", "其它");

	private String code;
	private String msg;

	ChannelStateEnum(String code, String msg) {
		this.code = code;
		this.msg = msg;
	}

	public static String getMsgByCode(String code) {

		for (ChannelStateEnum rspMsgEnum : ChannelStateEnum.values()) {
			if (rspMsgEnum.getCode().equals(code)) {
				return rspMsgEnum.getMsg();
			}
		}
		return "未知错误";
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}
}
