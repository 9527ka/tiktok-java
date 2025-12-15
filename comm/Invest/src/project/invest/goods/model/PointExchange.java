package project.invest.goods.model;

import kernel.bo.EntityObject;
import lombok.Data;

import java.util.Date;

@Data
public class PointExchange extends EntityObject<String> {
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
	 * 兑换倍数（SCALE:1）
	 */
	private String scale;

	/**
	 *获得usdt
	 */
	private double usdt;


	/**
	 * 支付积分
	 */
	private int payPoint;


	/**
	 * 创建时间
	 */
	private Date createTime;

}
