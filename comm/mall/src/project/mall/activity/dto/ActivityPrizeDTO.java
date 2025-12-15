package project.mall.activity.dto;


import lombok.Data;

import java.math.BigDecimal;


/**
 * 活动奖品池
 *
 * @author caster
 */
@Data
public class ActivityPrizeDTO {

	private String id;

	private String poolId;

	/**
	 * 奖品名称
	 */
	private String prizeName;

	/**
	 * 奖品类型， 1-实物、2-彩金
	 */
	private Integer prizeType;

	/**
	 * 奖品价值
	 */
	private BigDecimal prizeAmount;


	/**
	 * 当所有随机奖励都没有获取时，当前奖品是否是默认奖品 1、默认奖品（谢谢惠顾）
	 */
	private Integer defaultPrize;

	/**
	 * 奖品数量
	 */
	private int maxQuantity;

	/**
	 * 中奖几率
	 */
	private BigDecimal odds;

	/**
	 * 图片地址
	 */
	private String image;
}
