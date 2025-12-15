package project.mall.activity.dto;


import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 活动奖品池
 * 
 * @author caster
 */
@Data
public class ActivityPrizePoolShowDTO {
	/**
	 * 奖品池ID
	 */
	private String id;

	private Integer prizeType;

	/**
	 * 奖品价值
	 */
	private BigDecimal prizeAmount;

	private String image;

	/**
	 * 奖品名称
	 */
	private String prizeName;

	private String createTime;

	private String updateTime;

	private String createBy;

	private String remark;
}
