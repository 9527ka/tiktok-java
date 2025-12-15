package project.mall.type.model;

import kernel.bo.EntityObject;
import lombok.Data;

@Data
public class CategoryLang extends EntityObject<String> {
    /**
     * 语言
     */
    private String lang;

    /**
     * 名称
     */
    private String name;

    /**
     * 分类Id
     */
    private String categoryId;

    private String des;
}