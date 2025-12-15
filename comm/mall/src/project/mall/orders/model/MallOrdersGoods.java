package project.mall.orders.model;

import kernel.bo.EntityObject;
import lombok.Data;

import java.util.Date;

@Data
public class MallOrdersGoods extends EntityObject<String> {
	private static final long serialVersionUID = -6585270719884278L;
	/**
	 * 订单ID
	 */
	private String orderId;


	/**
	 * 商品ID
	 */
	private String goodsId;

	/**
	 * 商品数量
	 */
	private int goodsNum = 0;


	/**
	 * 商品单价，商品原价
	 */
	private double goodsPrize;


	/**
	 * 实际价格，折扣期间, GoodsReal 填写的是折扣价，如果折扣过期，填写的是原价
	 */
	private double goodsReal;


	private double systemPrice;

	/**
	 * 运费
	 */
	private double fees;


	/**
	 * 税收
	 */
	private double tax;


	/**
	 * 序号
	 */
	private int goodsSort = 0;

	/**
	 * 平台商品ID
	 */
	private String systemGoodsId;

	/**
	 * skuId
	 */
	private String skuId;



}
