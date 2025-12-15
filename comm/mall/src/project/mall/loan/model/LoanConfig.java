package project.mall.loan.model;

import kernel.bo.EntityObject;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoanConfig extends EntityObject<String> {

    private static final long serialVersionUID = 6391352094952122335L;

    private double amountMin;

    private double amountMax;

    private double rate;

    private double defaultRate;

    private String lendableDays;

    private String allLendableDays;
}