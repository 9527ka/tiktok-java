package project.blockchain.vo;

import lombok.Data;

@Data
public class GCash2OrderQueryVo {
    /**
     *平台订单号
     */
    private String order_no;
    /**
     *商户号
     */
    private String mer_no;
    /**
     *订单错误信息
     */
    private String order_err_msg;
    /**
     *查询状态
     */
    private String query_status;
    /**
     *数字签名
     */
    private String sign;
    /**
     *订单时间
     */
    private String order_time;
    /**
     *订单状态
     */
    private String order_status;
    /**
     *订单错误码
     */
    private String order_err_code;
    /**
     *订单金额
     */
    private String order_amount;
    /**
     *商户订单号
     */
    private String mer_order_no;
    /**
     *查询错误码
     */
    private String query_err_code;
    /**
     *查询错误信息
     */
    private String query_err_msg;
}
