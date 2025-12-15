package project.mall.activity.event.source;

import project.mall.activity.event.message.ActivityUserRechargeMessage;

/**
 * 用户充值事件，专用于活动模块
 *
 */
public class ActivityPlayerRechargeEvent extends ActivityTriggerEvent<ActivityUserRechargeMessage> {
	private ActivityUserRechargeMessage info;

	public ActivityPlayerRechargeEvent(Object source, ActivityUserRechargeMessage info) {
		super(source, info);
	}
}
