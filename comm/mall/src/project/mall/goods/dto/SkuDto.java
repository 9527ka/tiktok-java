package project.mall.goods.dto;

import lombok.Data;

import java.util.List;

@Data
public class SkuDto {

    private List<SkuAttrDto> attrs;
    private Double price;
    private Double sellingPrice;
    private Double discountPrice;
    private String skuId;
    private String iconImg;
    private String coverImg;
    private List<String> img;
    private boolean hidden = true;

}
