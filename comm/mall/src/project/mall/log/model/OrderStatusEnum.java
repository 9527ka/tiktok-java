package project.mall.log.model;

import java.io.Serializable;

public enum OrderStatusEnum implements Serializable {


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
     * 订单状态（-1=已取消）（0=待付款）（1=待发货）（2=已确认）（3=待收货）（4=已收获）（5=已评价）（6=退款)<br/>
     * 退货状态（0=未退款）（1=退款中）（2=退款成功）（3=退款失败）<br/>
     * 商家采购状态 0-未采购，1已经采购 <br/>
     **/

    ORDER_SUCCESS(1, "下单成功，未支付"),
    ORDER_PAY_SUCCESS(2, "下单成功，已支付"),
    ORDER_CANCEL(3, "手动发起取消订单"),
    ORDER_CANCEL_TIME(4, "超时自动取消订单"),
    PURCH_SUCESS(5, "商家采购成功"),
    ORDER_SEND(6, "系统发货成功"),
    ORDER_SEND_CONFIRM(7, "订单自动收货、订单手动收货"),
    REFUND(8, "发起退款申请"),
    REFUND_SUCCESS(9, "退款成功"),
    REFUND_FAIL(10, "退款失败"),
    ;

    private Integer code;
    private String msg;

    OrderStatusEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

}
