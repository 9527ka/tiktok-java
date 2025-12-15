package project.mall.goods.model;

import lombok.Data;

import java.util.List;

@Data
public class GoodsAttrsValuesVo {
    // 属性名，国际化好的
    private String attrName;
    // 属性id
    private String attrNameId;

    //属性值名称，国家化好的
    private List<String> attrValues;
    // 属性值id
    private List<String> attrValueIds;
}
