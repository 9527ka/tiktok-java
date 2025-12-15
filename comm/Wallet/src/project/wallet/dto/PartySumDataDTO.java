package project.wallet.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author axing
 * @since 2023/8/15
 **/
@Data
public class PartySumDataDTO implements Serializable {

    private Double totalRecharge;

    private Double totalWithdraw;

    private Integer rechargeNum;

    private Integer withdrawNum;
}
