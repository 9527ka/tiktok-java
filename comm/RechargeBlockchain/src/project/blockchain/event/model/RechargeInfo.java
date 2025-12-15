package project.blockchain.event.model;

import lombok.Data;

import java.util.Date;

@Data
public class RechargeInfo {
    // 该值可能为空，给用户直接打钱时
    private String orderNo;

    private double amount;

    private String applyUserId;

    private String walletLogId;

    private Date eventTime;
}
