package project.blockchain;

import java.io.Serializable;
import java.util.Date;

import kernel.bo.EntityObject;

/**
 * 区块链充值订单
 */
public class RechargeBlockchain extends EntityObject<String> {

    /**
     *
     */
    private static final long serialVersionUID = -4483090797419171871L;

    /**
     * 订单号
     */
    private String order_no;

    private String partyId;

    /**
     * 充值数量，原始币种的金额
     */
    private Double volume;

    /**
     * 充值币种
     */
    private String symbol;

    /**
     * 充值状态 0 初始状态，未知或处理中 1 成功 2 失败
     */
    private int succeeded = 0;
    /**
     * 创建时间
     */
    private Date created;
    /**
     * 审核操作时间
     */
    private Date reviewTime;

    /**
     * 备注说明，管理员操作
     */
    private String description;

    /**
     * 区块链充值地址
     */
    private String blockchain_name;
    /**
     * 已充值的上传图片
     */
    private String img;

    /**
     * 客户自己的区块链地址
     */
    private String address;
    /**
     * 通道充值地址
     */
    private String channel_address;

    /**
     * 转账hash
     */
    private String tx;

    /**
     * 实际到账金额，换算成 USDT 单位的金额
     */
    private Double amount;

    /**
     * 业务员提成
     */
    private Double rechargeCommission = 0.0;

    /**
     * 是否第三方订单，0-否,1-是
     */
    private int isThirdParty;

    /**
     * 第三方支付页面地址
     */
    private String payUrl;


    public String getOrder_no() {
        return order_no;
    }

    public void setOrder_no(String order_no) {
        this.order_no = order_no;
    }

    public String getPartyId() {
        return partyId;
    }

    public void setPartyId(String partyId) {
        this.partyId = partyId;
    }

    public int getSucceeded() {
        return succeeded;
    }

    public void setSucceeded(int succeeded) {
        this.succeeded = succeeded;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBlockchain_name() {
        return blockchain_name;
    }

    public void setBlockchain_name(String blockchain_name) {
        this.blockchain_name = blockchain_name;
    }

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

    public Double getVolume() {
        return volume;
    }

    public void setVolume(Double volume) {
        this.volume = volume;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public Date getReviewTime() {
        return reviewTime;
    }

    public void setReviewTime(Date reviewTime) {
        this.reviewTime = reviewTime;
    }

    public String getChannel_address() {
        return channel_address;
    }

    public void setChannel_address(String channel_address) {
        this.channel_address = channel_address;
    }

    public String getTx() {
        return tx;
    }

    public void setTx(String tx) {
        this.tx = tx;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Double getRechargeCommission() {
        return rechargeCommission;
    }

    public void setRechargeCommission(Double rechargeCommission) {
        this.rechargeCommission = rechargeCommission;
    }

	public int getIsThirdParty() {
		return isThirdParty;
	}

	public void setIsThirdParty(int isThirdParty) {
		this.isThirdParty = isThirdParty;
	}

    public String getPayUrl() {
        return payUrl;
    }

    public void setPayUrl(String payUrl) {
        this.payUrl = payUrl;
    }
}
