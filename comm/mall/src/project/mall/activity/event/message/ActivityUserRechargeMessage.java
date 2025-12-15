package project.mall.activity.event.message;

import lombok.Data;

/**
 * 用户充值消息，专用于活动模块
 *
 */
@Data
public class ActivityUserRechargeMessage extends BaseActivityMessage {
	private String userName;

	private Double usdtAmount;

//	private int currency;
//
//	private Double rmbAmount;

	private String transId;

}
