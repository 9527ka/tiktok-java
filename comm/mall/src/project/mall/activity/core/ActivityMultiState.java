package project.mall.activity.core;


import lombok.Data;

/**
 * 活动处理相关的双状态
 *
 */
@Data
public class ActivityMultiState {
	/**
	 * 是否可以
	 */
	private ThreeStateEnum can;

	/**
	 * 是否已执行
	 */
	private ThreeStateEnum has;

	// 对应次数
	private int times;

	private String description;

	public ThreeStateEnum can() {
		return this.can;
	}

	public void can(ThreeStateEnum can) {
		this.can = can;
	}

	public ThreeStateEnum has() {
		return this.has;
	}

	public void has(ThreeStateEnum has) {
		this.has = has;
	}

	public int getTimes() {
		return this.times;
	}

	public void setTimes(int times) {
		this.times = times;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
