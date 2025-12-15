package project.mall.goods.model;

import kernel.bo.EntityObject;
import lombok.Data;

import javax.persistence.CascadeType;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
public class GoodsAttributeCategory extends EntityObject<String> {

    /**
     * 分类名称
     */
    private String name;


    /**
     * 排序
     */
    private  int sort;


    /**
     * 创建时间
     */
    private Date createTime;

}
