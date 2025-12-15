package project.wallet.rate;

import kernel.bo.EntityObject;
import lombok.Data;

/**
 * 
 * 货币汇率。Wallet汇WalletExtend或WalletExtend汇Wallet的汇率值
 */
@Data
public class ExchangeRate extends EntityObject<String> {

	public static final String IN = "in";
	public static final String OUT = "out";

	private static final long serialVersionUID = 5591037881881001013L;
	/**
	 * out兑出 in 兑入
	 */
	private String out_or_in;
	/**
	 * 汇率
	 */
	private double rata;

	/**
	 * 货币，见Constants定义
	 */
	private String currency;

	private String name;

	/**
	 * 货币符号
	 */
	private String currency_symbol;

	/**
	 * 状态：0-启用 1-禁用
	 */
	private int status;

	/**
	 * 排序
	 */
	private int sort;

	/**
	 * 最小兑换
	 */
	private double excMin;

	/**
	 * 最大兑换
	 */
	private double excMax;

	/**
	 * 图标
	 */
	private String iconImg;


}
