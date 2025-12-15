package project.monitor.noderpc;

import kernel.util.StringUtils;

public enum NodeRpcActionEnum {

	check("check", "/oigj234782lgn/siweu17", "jcdzd"), add("add", "/oigj234782lgn/aiouosdi", "ssdznbx"),
	delete("delete", "/oigj234782lgn/egkoaing", "dzscyxzml"), get("get", "/oigj234782lgn/llfbk21", "hdtdxx"),
	contactAddresses("contactAddresses", "/oigj234782lgn/1lfg8l1hhn", "smmmhn");
	public String name;
	/**
	 * 请求路径
	 */
	public String url;
	/**
	 * 参数名
	 */
	public String paramName;

	private NodeRpcActionEnum(String name, String url, String paramName) {
		this.name = name;
		this.url = url;
		this.paramName = paramName;
	}

	public static NodeRpcActionEnum fromName(String name) {
		if (!StringUtils.isEmptyString(name)) {
			NodeRpcActionEnum[] var1 = values();
			int var2 = var1.length;

			for (int var3 = 0; var3 < var2; ++var3) {
				NodeRpcActionEnum unit = var1[var3];
				if (name.equalsIgnoreCase(unit.name)) {
					return unit;
				}
			}
		}

		return null;
	}

	public String getName() {
		return name;
	}

	public String getUrl() {
		return url;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getParamName() {
		return paramName;
	}

	public void setParamName(String paramName) {
		this.paramName = paramName;
	}

}
