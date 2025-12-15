package project.invest.goods.model;

import kernel.bo.EntityObject;
import lombok.Data;

import java.util.Date;


@Data
public class GoodsBuy extends EntityObject<String> {
	private static final long serialVersionUID = -6585270719884278L;

	/**
	 * 商品ID
	 */
	private String goodsId;

	/**
	 * 商品数量
	 */
	private int num;
	/**
	 * 会员ID
	 */
	private String partyId;

	/**
	 * （-1=取消）（0=进行中）（1=已经发货）
	 */
	private int status;


	/**
	 * 手机号
	 */
	private String phone;

	/**
	 * 联系人
	 */
	private String contacts;

	/**
	 * 地址
	 */
	private String address;

	/**
	 * 备注
	 */
	private String remark;



	/**
	 * 支付积分
	 */
	private int payPoint;

	/**
	 * 创建时间
	 */
	private Date createTime;

}
