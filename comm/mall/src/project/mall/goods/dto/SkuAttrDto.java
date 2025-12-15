package project.mall.goods.dto;

import lombok.Data;

@Data
public class SkuAttrDto {

    private  String attrId;

    private  String attrValueId;

    private  String attrName;

    private  String attrValueName;

    private boolean isIcon;

    private String iconImg;

}
