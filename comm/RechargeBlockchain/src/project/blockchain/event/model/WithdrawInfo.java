package project.blockchain.event.model;

import lombok.Data;

import java.util.Date;

@Data
public class WithdrawInfo {
    // 该值可能为空
    private String orderNo;

    private double amount;

    private String applyUserId;

    private String walletLogId;

    private Date eventTime;
}
