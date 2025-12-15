package project.mall.credit.model;

import kernel.bo.EntityObject;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Credit extends EntityObject<String> {

    private static final long serialVersionUID = 5747129038668729443L;

    //	@ApiModelProperty(value = "用户id")
    private String partyId;

    //	@ApiModelProperty(value = "贷款状态(1待审核,2审核通过,3已逾期,4未通过,5已还款)")
    private int status;

    //	@ApiModelProperty(value = "真实姓名")
    private String realName;

    //	@ApiModelProperty(value = "证件号码")
    private String identification;

    //	@ApiModelProperty(value = "国籍")
    private Integer countryId;

    //	@ApiModelProperty(value = "证件照正面")
    private String imgCertificateFace;

    //	@ApiModelProperty(value = "证件照反面")
    private String imgCertificateBack;

    //	@ApiModelProperty(value = "手持证件照")
    private String imgCertificateHand;

    //	@ApiModelProperty(value = "贷款期限,单位天")
    private Integer creditPeriod;

    //	@ApiModelProperty(value = "申请金额")
    private double applyAmount;

    //	@ApiModelProperty(value = "贷款利率")
    private double creditRate;

    //	@ApiModelProperty(value = "逾期利率")
    private double defaultRate;

    //	@ApiModelProperty(value = "总利息")
    private double totalInterest;

    //	@ApiModelProperty(value = "总还款金额")
    private double totalRepayment;

    //	@ApiModelProperty(value = "实际还款")
    private double actualRepayment;

    //	@ApiModelProperty(value = "驳回原因")
    private String rejectReason;

    //	@ApiModelProperty(value = "客户提交时间")
    private Date customerSubmitTime;

    //	@ApiModelProperty(value = "系统审核时间")
    private Date systemAuditTime;

    //	@ApiModelProperty(value = "最后还款时间 ")
    private Date finalRepayTime;

    //	@ApiModelProperty(value = "逾期时间 ")
    private Date expireTime;
}
