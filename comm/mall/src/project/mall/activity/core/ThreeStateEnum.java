package project.mall.activity.core;


import cn.hutool.core.util.StrUtil;

public enum ThreeStateEnum {

	TRUE("true", "是"),

	FALSE("false", "否"),

	UNKNOW("unknow", "未知"),

	;

	private String state;

	private String stateName;


	ThreeStateEnum(String state, String stateName) {
		this.state = state;
		this.stateName = stateName;
	}

	public static ThreeStateEnum typeOf(String state) {
		if (StrUtil.isBlank(state)) {
			return UNKNOW;
		}
		ThreeStateEnum values[] = ThreeStateEnum.values();
		for (ThreeStateEnum one : values) {
			if (one.getState().equalsIgnoreCase(state)) {
				return one;
			}
		}

		return UNKNOW;
	}

	public String getState() {
		return this.state;
	}

	public String getStateName() {
		return stateName;
	}
}
