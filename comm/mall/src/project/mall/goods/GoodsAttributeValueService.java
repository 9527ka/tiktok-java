package project.mall.goods;
import project.mall.goods.dto.GoodsAttributeValueDto;
import project.mall.goods.model.GoodsAttributeValueLang;

import java.util.List;

public interface GoodsAttributeValueService {

    /**
     * 参数列表
     * @param pageNum
     * @param pageSize
     * @param attrId
     * @return
     */
    List<GoodsAttributeValueDto> list(int pageNum, int pageSize, String attrId);

    /**
     *
     * @param name 参数名称
     * @param lang 国际化
     * @param attrId 属性id
     * @param id 参数id
     */
    void  saveOrUpdate(String name,String lang,String attrId,String id);

    /**
     * 删除
     * @param   id 参数id
     */
    void  delete(String id);

    GoodsAttributeValueLang findLangData(String attrValueId,String lang);

}
