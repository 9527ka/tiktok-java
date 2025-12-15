package project.mall.goods;

import project.mall.goods.dto.*;
import project.mall.goods.model.GoodsAttribute;
import project.mall.goods.model.GoodsAttributeLang;

import java.util.List;
import java.util.Map;

public interface GoodsAttributeService {


    /**
     * 属性分类列表
     *
     * @param pageNum
     * @param pageSize
     * @return
     */
    List<GoodsAttributeDto> list(int pageNum, int pageSize, String categoryId);

    int getCount(String categoryId);

    /**
     * 保存属性
     */
    void save(Map<String, GoodsAttributeDescDto> map,GoodsAttributeDescDto goodsAttributeDescDto);


    void saveAttr(GoodsAttributeDescDto goodsAttributeDescDto);

    /**
     * 更新属性
     *
     * @param id
     * @param map
     */
    void updateById(String id, Map<String, GoodsAttributeDescDto> map, int sort);


    /**
     * 删除属性
     *
     * @param id
     */
    void removeById(String id);

    /**
     * 获取规则详情
     *
     * @param id
     * @return
     */
    GoodsAttributeDescDto getById(String id,String lang);


    /**
     * 通过分类id生成Sku
     */
    void saveGenerateSku(String goodId, String categoryId);

    /**
     * 判断 排序是否存在
     *
     * @param sort
     * @param categoryId
     * @return
     */
    boolean queryExistBySortAndCategoryId(int sort, String categoryId);

    void updateAttrById(GoodsAttributeDescDto goodsAttributeDescDto);

    List<GoodAttrDto> findByCategoryId(String categoryId, String lang);

    String saveAttrValue(String attrId, String name, String lang);

    /**
     *
     * @param attrId 属性id
     * @param name  属性名称
     * @param lang  国际化
     * @param categoryId 属性分类id
     */
    void saveAndUpdate(String attrId,String name, String lang,String categoryId,int sort);

    GoodsAttribute findGoodsAttributeById(String id);

    GoodsAttributeLang findAttributeLangById(String attributeId, String lang);
}
