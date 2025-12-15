package project.mall.orders.model;

import kernel.bo.EntityObject;
import lombok.Data;

import java.util.Date;

@Data
public class MallOrderRebate extends EntityObject<String> {
    private static final long serialVersionUID = -7425736371338824089L;

    private String orderId;

    private String partyId;

    private String orderPartyId;

    private Double rebate;

    private Integer level;

    private Date createTime;

}
