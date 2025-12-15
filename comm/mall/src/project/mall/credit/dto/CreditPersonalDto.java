package project.mall.credit.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class CreditPersonalDto {
    private String id;
    private BigDecimal applyAmount;
    private String customerSubmitTime;
    private Integer creditPeriod;
    private BigDecimal creditRate;
    private Integer status;
    private String statusStr;
    private String rejectReason;
}
