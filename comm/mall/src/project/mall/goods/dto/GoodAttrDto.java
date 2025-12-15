package project.mall.goods.dto;

import lombok.Data;

import java.util.List;

@Data
public class GoodAttrDto {

    private String attrId;


    private String attrName;

    private List<GoodAttrValueDto> attrValues;

}
