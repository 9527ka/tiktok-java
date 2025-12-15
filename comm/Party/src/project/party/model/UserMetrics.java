package project.party.model;

import kernel.bo.EntityObject;
import lombok.Data;

import java.util.Date;

@Data
public class UserMetrics extends EntityObject<String> {
    private static final long serialVersionUID = -6010777602205862201L;

//    @ApiModelProperty(value = "用户ID")
    private String partyId;

//    @ApiModelProperty(value = "累计有效充值金额")
    private Double moneyRechargeAcc =0d;

//    @ApiModelProperty(value = "店铺升级级累计有效充值金额")
    private Double storeMoneyRechargeAcc = 0d;

    // 暂未启用该指标
//    @ApiModelProperty(value = "累计有效提现金额")
    private Double moneyWithdrawAcc;

    // 暂未启用该指标
//    @ApiModelProperty(value = "账户余额")
    private Double accountBalance;

    // 暂未启用该指标
//    @ApiModelProperty(value = "累计收入金额")
    private Double totleIncome;


//    @ApiModelProperty(value = "0-禁用 1-启用")
    private Integer status;

    private Date createTime;

    private Date updateTime;
}
