package project.mall.combo.model;

import kernel.bo.EntityObject;
import lombok.Data;

import java.util.Date;

@Data
public class ComboRecord extends EntityObject<String> {
	private String comboId;
	private String partyId;
	private String name;
	private int promoteNum;
	private double amount;
	private long stopTime;
	private int day;
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
