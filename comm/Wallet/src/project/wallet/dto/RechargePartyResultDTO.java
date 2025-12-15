package project.wallet.dto;

import lombok.Data;

import java.io.Serializable;


@Data
public class RechargePartyResultDTO implements Serializable {

    private Integer number;

    private String amount;
}
