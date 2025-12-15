package kernel.web;

import java.io.Serializable;

public class ResultObject implements Serializable {
	private static final long serialVersionUID = 6952247764513362272L;
	private String code = "0";
	private String msg;
	private Object data;

	public String getCode() {
		return this.code;
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

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

}
