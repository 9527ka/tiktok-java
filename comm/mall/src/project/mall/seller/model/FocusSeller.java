package project.mall.seller.model;

import kernel.bo.EntityObject;
import lombok.Data;

import java.util.Date;

@Data
public class FocusSeller extends EntityObject<String> {

    private static final long serialVersionUID = 8904357201753772888L;

//    @TableField("USERNAME")
    private String partyId;

//    @ApiModelProperty(value = "商品id")
//    @TableField("SELLER__ID")
    private String sellerId;

    //关注时间
    private Date createTime;
}
