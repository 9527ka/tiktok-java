package project.mall.goods.dto;

import lombok.Data;

import java.util.Date;

@Data
public class GoodsAttributeCategoryDto {

    /**
     * 分类名称
     */
    private String name;


    /**
     * 排序
     */
    private  String sort;


    /**
     * 创建时间
     */
    private Date createTime;

    private  String id;


    /**
     *  规则总数
     */
    private  int attrCount=0;
}
