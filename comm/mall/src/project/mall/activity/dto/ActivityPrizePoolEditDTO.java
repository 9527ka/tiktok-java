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
public class ActivityPrizePoolEditDTO {
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

	private String remark;

	/**
	 * 奖品名称
	 */
	private List<MultiLanguageField> i18nNames;

	/**
	 * 操作用户账号
	 */
	private String createBy;
}
