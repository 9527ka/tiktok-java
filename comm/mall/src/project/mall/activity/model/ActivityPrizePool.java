package project.mall.activity.model;


import kernel.bo.EntityObject;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 活动奖品池
 *
 * @author caster
 */
@Data
public class ActivityPrizePool extends EntityObject<String> {
	/**
	 *
	 */
	private String prizeNameCn;

	private String prizeNameEn;

	/**
	 * 奖品类型， 1-实物、2-彩金, 3-谢谢惠顾
	 */
	private Integer prizeType;

	/**
	 * 奖品价值
	 */
	private BigDecimal prizeAmount;

	/**
	 * 奖品启用状态：0-未启用，1-启用
	 */
	private Integer status;

	private String image;

	private String remark;

	/**
	 */
	private Date createTime;

	private Date updateTime;

	/**
	 */
	private String createBy;

	/**
	 * 逻辑删除标记： 0-正常，1-删除
	 */
	private Integer deleted;
}
