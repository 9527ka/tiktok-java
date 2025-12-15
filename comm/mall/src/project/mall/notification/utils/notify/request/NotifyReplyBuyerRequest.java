package project.mall.notification.utils.notify.request;

public class NotifyReplyBuyerRequest extends BaseNotifyRequest {

    private String sellerId;

    // 在发送邮件场景需要用到
    private String sellerEmail;

    /**
     * 在发送短信场景需要用到，注意：需要携带国家编号
     * 示例：
     *     中国大陆手机号：8613311111111
     *
     */
    private String sellerMobileInfo;

    private String buyerId;

    private String lastImMessageId;

    private String lastImMessage;

    // img | text
    private String lastImMessageType;

    @Override
    public CommonNotifyRequest buildRequest() {
        CommonNotifyRequest request = new CommonNotifyRequest();
        request.setBizType(this.getBizType());
        request.setLanguageType(this.getLanguageType());
        request.setFromUserId(this.getFromUserId());
        request.setTargetUserId(this.getTargetUserId());
        request.setTargetTopic(this.getTargetTopic());
        // 跳转链接与消息id有关，可能还与消息内容类型有关
        //request.setLink();
        request.setRefType(this.getRefType());
        request.setRefId(this.getRefId());

        // 填充消息模板中的占位符 "content"
        //request.setVarValue("content", this.getLastImMessage());

        return request;
    }

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public String getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(String buyerId) {
        this.buyerId = buyerId;
    }

    public String getLastImMessageId() {
        return lastImMessageId;
    }

    public void setLastImMessageId(String lastImMessageId) {
        this.lastImMessageId = lastImMessageId;
    }

    public String getLastImMessage() {
        return lastImMessage;
    }

    public void setLastImMessage(String lastImMessage) {
        this.lastImMessage = lastImMessage;
    }

    public String getLastImMessageType() {
        return lastImMessageType;
    }

    public void setLastImMessageType(String lastImMessageType) {
        this.lastImMessageType = lastImMessageType;
    }

    public String getSellerEmail() {
        return sellerEmail;
    }

    public void setSellerEmail(String sellerEmail) {
        this.sellerEmail = sellerEmail;
    }

    public String getSellerMobileInfo() {
        return sellerMobileInfo;
    }

    public void setSellerMobileInfo(String sellerMobileInfo) {
        this.sellerMobileInfo = sellerMobileInfo;
    }
}
