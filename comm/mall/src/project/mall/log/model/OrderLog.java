package project.mall.log.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import kernel.bo.EntityObject;
import project.log.AbstractLog;

import java.io.Serializable;
import java.util.Date;

public class OrderLog extends EntityObject<String> implements AbstractLog {

    /**
     * Member Description
     */
    private static final long serialVersionUID = 1837652077217320809L;
    /**
     * 日志归属用户ID
     */
    private String partyId;
    /**
     * 日志归属用户名
     */
    private String username;

    /**
     * 类型,目前都是订单，写死
     */
    private String category = "order";

    /**
     * 下单成功，未支付 1 <br/>
     * 下单成功，已支付 2 <br/>
     * 手动发起取消订单 3 <br/>
     * 超时自动取消订单 4 <br/>
     * 商家采购成功  5 <br/>
     * 系统发货成功 6 <br/>
     * 订单自动收货、订单手动收货 7 <br/>
     * 发起退款申请 8 <br/>
     * 退款成功 9 <br/>
     * 退款失败 10 <br/>
     * <p>
     *
     * 原来的状态如下：
     * 订单状态（-1=已取消）（0=待付款）（1=待发货）（2=已确认）（3=待收货）（4=已收获）（5=已评价）（6=退款)<br/>
     * 退货状态（0=未退款）（1=退款中）（2=退款成功）（3=退款失败）<br/>
     * 商家采购状态 0-未采购，1已经采购 <br/>
     */
    private Integer state;

    /**
     * 日志，例如： <br/>
     * 1、订单#202312323#下单成功，等待付款 -> 下单成功，未支付 <br/>
     * 2、订单#202312323#下单成功，付款成功 -> 下单成功，已支付  <br/>
     * 3、订单#202312323#订单已取消，原因：不想要了 -> 手动发起取消订单  <br/>
     * 4、订单#202312323#长时间未付款，系统自动取消订单 -> 超时自动取消订单  <br/>
     * 5、订单#202312323#商家已确认，已进入备货状态 -> 商家采购成功  <br/>
     * 6、订单#202312323#订单已发货，正在运送 -> 系统发货成功  <br/>
     * 7、订单#202312323#订单已签收 -> 订单自动收货、订单手动收货  <br/>
     * 8、订单#202312323#订单发起退款申请 -> 发起退款申请  <br/>
     * 9、订单#202312323#订单退款成功 -> 退款成功  <br/>
     * 10、订单#202312323#订单退款失败 -> 退款失败  <br/>
     *
     */
    private String log;

    /**
     * 订单Id
     */
    private String orderId;

    /**
     * 创建时间
     */
//    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8") 取服务器设置的默认时区，不显式设置
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    private String createTimeStr;

    public String getCreateTimeStr() {
        return createTimeStr;
    }

    public void setCreateTimeStr(String createTimeStr) {
        this.createTimeStr = createTimeStr;
    }

    /**
     * 修改时间
     */
//    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8") 取服务器设置的默认时区，不显式设置
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getPartyId() {
        return partyId;
    }

    public void setPartyId(String partyId) {
        this.partyId = partyId;
    }

    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getOrderId() {
        return orderId;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return "OrderLog{" +
                "partyId=" + partyId +
                ", username='" + username + '\'' +
                ", category='" + category + '\'' +
                ", state=" + state +
                ", log='" + log + '\'' +
                ", orderId='" + orderId + '\'' +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}
