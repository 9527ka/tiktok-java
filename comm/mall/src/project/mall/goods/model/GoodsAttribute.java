package project.mall.goods.model;

import kernel.bo.EntityObject;
import lombok.Data;

import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import java.util.Date;
import java.util.List;


/**
 * 商品属性参数表
 */
@Data
public class GoodsAttribute extends EntityObject<String> {
    /**
     * 排序
     */
    private Integer sort;

    /**
     * 创建时间
     */
    private Date createTime;


    /**
     *  分类id
     */
    private String categoryId;

}