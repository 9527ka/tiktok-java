package project.blockchain;

import kernel.bo.EntityObject;

/**
 * 区块链充值地址
 *
 */
public class ChannelBlockchain extends EntityObject<String> {

	private static final long serialVersionUID = 8611350151193561992L;

	/**
	 * 币种名称 BTC ETH USDT
	 */
	private String coin;
	/**
	 * 链名称
	 */
	private String blockchain_name;
	/**
	 * 区块链地址图片
	 */
	private String img;
	/**
	 * 区块链地址图片,不带链接
	 */
	private String img_str;
	/**
	 * 区块链地址
	 */
	private String address;

	/**
	 * 汇率
	 */
	private double fee = 1;

	private double recharge_limit_min = 1;

	private double recharge_limit_max = 1;
	
	/**
	 * 手动/自动到账
	 */
	private boolean auto = false;

	public String getImg() {
		return img;
	}

	public void setImg(String img) {
		this.img = img;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getCoin() {
		return coin;
	}

	public void setCoin(String coin) {
		this.coin = coin;
	}

	public String getBlockchain_name() {
		return blockchain_name;
	}

	public void setBlockchain_name(String blockchain_name) {
		this.blockchain_name = blockchain_name;
	}

	public String getImg_str() {
		return img_str;
	}

	public void setImg_str(String img_str) {
		this.img_str = img_str;
	}

	public boolean getAuto() {
		return this.auto;
	}

	public void setAuto(boolean auto) {
		this.auto = auto;
	}

	public double getFee() {
		return fee;
	}

	public void setFee(double fee) {
		this.fee = fee;
	}

	public double getRecharge_limit_min() {
		return recharge_limit_min;
	}

	public void setRecharge_limit_min(double recharge_limit_min) {
		this.recharge_limit_min = recharge_limit_min;
	}

	public double getRecharge_limit_max() {
		return recharge_limit_max;
	}

	public void setRecharge_limit_max(double recharge_limit_max) {
		this.recharge_limit_max = recharge_limit_max;
	}
}
