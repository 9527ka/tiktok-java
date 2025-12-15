package project.mall.goods.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class GoodsAttributeDescDto {

    /**
     * 名称
     */
    private  String name;

    /**
     * 属性Id
     */
    private String  id;


    /**
     * 语言
     */
    private  String lang;

   

    /**
     * 分类id
     */
    private  String categoryId;

    /**
     * 排序
     */
    private  int sort;
}
