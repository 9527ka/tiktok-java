package project.mall.activity.rule.join;

import lombok.Data;

/**
 * demo活动参加规则
 */
@Data
public class DemoActivityJoinRule extends BaseActivityJoinRule {

	@Override
	public String showTitle() {
		return "用户活动积分满 10 分";
	}
}
