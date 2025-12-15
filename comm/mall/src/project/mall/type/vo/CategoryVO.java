package project.mall.type.vo;

import kernel.bo.EntityObject;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class CategoryVO {

    private String id;

    private String parentId;

    /**
     * 时间
     */
    private Date createTime;

    /**
     * 排序，取代sort字段，值越小越靠前
     * 未排序使用初始值：9999
     */
    private Integer rank;

    private Integer sort;

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

    // 对外对语言展示分类名称
    private String name;

    // 兼容旧数据格式，等效 id
    private String categoryId;

    private String des;

    public List<CategoryVO> subList;
    
}