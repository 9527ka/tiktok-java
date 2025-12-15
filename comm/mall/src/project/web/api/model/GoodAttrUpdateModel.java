package project.web.api.model;

import lombok.Data;
import project.mall.goods.dto.GoodsAttributeDescDto;

import java.util.Map;

@Data
public class GoodAttrUpdateModel {

    private String id;

    private Map<String, GoodsAttributeDescDto> attributes;


}
