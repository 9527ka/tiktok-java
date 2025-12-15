package project.mall.combo.model;

import kernel.bo.EntityObject;
import lombok.Data;

import java.util.Date;

@Data
public class Combo extends EntityObject<String> {

	private static final long serialVersionUID = -4344448622645620405L;

//	@ApiModelProperty(value = "封面图")
	private String iconImg;

	/***
	 * 表示的总的可推广数量，不会变化的
	 */
//	@ApiModelProperty(value = "可推广数")
	private int promoteNum;

//	@ApiModelProperty(value = "价格")
	private Double amount;

//	@ApiModelProperty(value = "有效天数")
	private int day;

	//	@ApiModelProperty(value = "创建时间")
	private Date createTime;

	/**
	 * 基础访问量
	 */
	private int baseAccessNum;

	/**
	 * 自增范围最小值
	 */
	private int autoAccMin;

	/**
	 * 自增范围最大值
	 */
	private int autoAccMax;

	/**
	 * 自增间隔，单位秒，默认：3600
	 */
	private int accInterval;

}
