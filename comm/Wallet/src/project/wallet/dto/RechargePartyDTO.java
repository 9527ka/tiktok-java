package project.wallet.dto;

import lombok.Data;

import java.io.Serializable;


@Data
public class RechargePartyDTO implements Serializable {

    private Double amount;

    private String sellerId;

}
