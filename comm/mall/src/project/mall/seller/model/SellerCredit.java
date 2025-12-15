package project.mall.seller.model;

import kernel.bo.EntityObject;
import lombok.Data;

import java.util.Date;

@Data
public class SellerCredit extends EntityObject<String> {
    private static final long serialVersionUID = -6010777602205862201L;
//    @ApiModelProperty(value = "店铺ID")
//    @TableField("SELLER_ID")
    private String sellerId;

//    @ApiModelProperty(value = "信誉分变更值")
//    @TableField("ACC_SCORE")
    private Integer accScore;

//    @ApiModelProperty(value = "变更时间")
//    @TableField("OPERATION_TIME")
    private Date operationTime;

//    @ApiModelProperty(value = "变更原因描述")
//    @TableField("REASON")
    private String reason;

//    @ApiModelProperty(value = "信誉分时间区间")
//    @TableField("TIME_REGION")
    private String timeRegion;

//    @ApiModelProperty(value = "积分变更事件类型")
//    @TableField("EVENT_TYPE")
    private Integer eventType;

//    @ApiModelProperty(value = "事件标记")
//    @TableField("EVENT_KEY")
    private String eventKey;


}
