package project.mall.type.model;

import kernel.bo.EntityObject;
import lombok.Data;

import java.util.Date;

@Data
public class Category extends EntityObject<String> {

    private String parentId;

    /**
     * 时间
     */
    private Date createTime;

    /**
     * 序号
     */
    private Integer sort;

    /**
     * 排序，取代sort字段，值越小越靠前
     * 未排序使用初始值：9999
     */
    private Integer rank;

    /**
     * 推荐时间（0=不推荐）
     */
    private Long recTime;

    /**
     * 是否启用 0-禁用 1-启用
     */
    private Integer status;

    /**
     * 图标
     */
    private String iconImg;

    /**
     * 商品分类层级，1-第一级，2-第二级
     */
    private Integer level;

    /**
     * 删除标记：0-删除，1-有效
     */
    private int type;
}