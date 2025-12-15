package project.invest.goods.model;

import kernel.bo.EntityObject;

import java.util.Date;
public class Goods extends EntityObject<String> {
	private static final long serialVersionUID = -66585270719884278L;
	/**
	 * 图标
	 */
	private String iconImg;

	/**
	 * 单价
	 */
	private double prize;

	/**
	 * 创建时间
	 */
	private Date createTime;

	/**
	 * 状态：0-启用 1-禁用
	 */
	private int status;

	/**
	 * 排序
	 */
	private int sort;

	/**
	 * '支付方式 0-全积分（预留字段）',
	 */
	private int payWay;

	/**
	 * 商品总数
	 */
	private int total;

	/**
	 * 剩余数量
	 */
	private int lastAmount;

	/**
	 * 已兑换总数
	 */
	private int exchangeAmount;

	/**
	 * 最后保存时间
	 */
	private long upTime;

	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	public String getIconImg() {
		return iconImg;
	}

	public void setIconImg(String iconImg) {
		this.iconImg = iconImg;
	}


	public void setPrize(int prize) {
		this.prize = prize;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getSort() {
		return sort;
	}

	public void setSort(int sort) {
		this.sort = sort;
	}

	public int getPayWay() {
		return payWay;
	}

	public void setPayWay(int payWay) {
		this.payWay = payWay;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public int getLastAmount() {
		if(lastAmount<0){
			return 0;
		}
		return lastAmount;
	}

	public void setLastAmount(int lastAmount) {
		this.lastAmount = lastAmount;
	}

	public int getExchangeAmount() {
		return exchangeAmount;
	}

	public void setExchangeAmount(int exchangeAmount) {
		this.exchangeAmount = exchangeAmount;
	}

	public long getUpTime() {
		return upTime;
	}

	public void setUpTime(long upTime) {
		this.upTime = upTime;
	}

	public double getPrize() {
		return prize;
	}

	public void setPrize(double prize) {
		this.prize = prize;
	}
}
