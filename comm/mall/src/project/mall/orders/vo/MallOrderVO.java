package project.mall.orders.vo;

import lombok.Data;

import java.util.List;

@Data
public class MallOrderVO {
    private String id;

    private String partyId;
    
    private String sellerId;

    private Integer countryId;

    private String buyerAddress;

    private String lang;

    private List<OrderGoodsVO> goodsList;
}
