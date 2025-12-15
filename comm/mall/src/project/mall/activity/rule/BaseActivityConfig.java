package project.mall.activity.rule;

import lombok.Data;

/**
 * 活动配置信息，难以区分分类时可以直接取代 jonRule 和 awardRule
 */
@Data
public class BaseActivityConfig {

	public String showTitle() {
		return "-";
	}
}
