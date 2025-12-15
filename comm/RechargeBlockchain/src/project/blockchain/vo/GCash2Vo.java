package project.blockchain.vo;

import lombok.Data;

@Data
public class GCash2Vo {
    /**
     * 支付链接
     */
    private String order_data;
    /**
     * 错误信息
     */
    private String err_msg;
    /**
     * 订单提交状态
     * 成功：SUCCESS 失败：FAIL
     */
    private String status;
    /**
     * 交易金额
     */
    private String order_amount;
}
