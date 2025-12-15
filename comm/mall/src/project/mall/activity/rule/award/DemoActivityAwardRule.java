package project.mall.activity.rule.award;

import lombok.Data;

/**
 * demo活动奖励规则
 */
@Data
public class DemoActivityAwardRule extends BaseActivityAwardRule {

	@Override
	public String showTitle() {
		return "奖励彩金 100 USDT";
	}
}
