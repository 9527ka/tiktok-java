package project.blockchain;

import kernel.bo.EntityObject;

import java.util.Date;

/**
 * 个人区块链充值地址
 *
 */
public class PartyBlockchain extends EntityObject<Integer> {

    private static final long serialVersionUID = 8087327604778650102L;

    /**
     * 用户名
     */
    private String userName;
    /**
     * 链名称
     */
    private String chainName;
    /**
     *
     */
    private String coinSymbol;
    /**
     * 区块链地址图片,不带链接
     */
    private String qrImage;
    /**
     * 区块链地址图片,不带链接
     */
    private String address;
    /**
     * 自动/手动到账
     */
    private String auto;

    private Date createTime;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getChainName() {
        return chainName;
    }

    public void setChainName(String chainName) {
        this.chainName = chainName;
    }

    public String getCoinSymbol() {
        return coinSymbol;
    }

    public void setCoinSymbol(String coinSymbol) {
        this.coinSymbol = coinSymbol;
    }

    public String getQrImage() {
        return qrImage;
    }

    public void setQrImage(String qrImage) {
        this.qrImage = qrImage;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAuto() {
        return auto;
    }

    public void setAuto(String auto) {
        this.auto = auto;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
