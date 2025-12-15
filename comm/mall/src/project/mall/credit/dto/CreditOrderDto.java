package project.mall.credit.dto;

import lombok.Builder;
import lombok.Data;
import project.mall.credit.model.Credit;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Builder
public class CreditOrderDto {
    private String creditId;
    private String usercode;
    private String username_parent;
    private String rolename;
    private String username;
    private Integer status;
    private Integer creditPeriod;
    private BigDecimal applyAmount;
    private BigDecimal creditRate;
    private BigDecimal defaultRate;
    private BigDecimal totalInterest;
    private BigDecimal totalRepayment;
    private BigDecimal actualRepayment;
    private String rejectReason;
    private String customerSubmitTime;
    private String systemAuditTime;
    private String finalRepayTime;
    private String realName;
    private String identification;
    private Integer countryId;
    private String imgCertificateFace;
    private String imgCertificateBack;
    private String imgCertificateHand;


}
