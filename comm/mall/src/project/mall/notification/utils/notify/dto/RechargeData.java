package project.mall.notification.utils.notify.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class RechargeData implements Serializable {


    //充值订单号
    private String orderNo;


    // 充值金额
    private double amount;

    // 充值用户ID
    private String rechargeUserId;

    //驳回原因
    private String reason;

}
