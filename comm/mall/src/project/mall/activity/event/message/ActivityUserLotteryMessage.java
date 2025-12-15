package project.mall.activity.event.message;

import lombok.Data;

/**
 * 用户点击转盘抽奖消息，专用于活动模块
 *
 */
@Data
public class ActivityUserLotteryMessage extends BaseActivityMessage {
	private String lang;

}
