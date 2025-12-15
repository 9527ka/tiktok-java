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
public class ActivityPrizeLog extends EntityObject<String> {
	// 对应 activityConfigLog 表记录主键id
	private String activityLogId;

	private String poolId;

	private String activityId;

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
	 * 奖品最大数量：0-无限
	 */
	private Integer maxQuantity;

	/**
	 * 奖品剩余数量，当 max_quantity=0 时该值无意义
	 */
	private Integer leftQuantity;

	/**
	 * 中奖几率
	 */
	private BigDecimal odds;

	/**
	 * 该奖项状态：0-不可用，1-可用
	 */
	private Integer status;

	/**
	 * 当所有随机奖励都没有获取时，当前奖品是否是默认奖品
	 */
	private Integer defaultPrize;

	private String image;

	private String remark;

	/**
	 */
	private Date createTime;

	private Date updateTime;

	/**
	 * 最后修改用户
	 */
	private String createBy;

	private Date logTime;

}
