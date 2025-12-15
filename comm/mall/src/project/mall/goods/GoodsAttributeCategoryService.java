package project.mall.goods;

import project.mall.goods.dto.GoodsAttributeCategoryDto;
import project.mall.goods.model.Evaluation;
import project.mall.goods.model.GoodsAttributeCategory;

import java.util.List;

public interface GoodsAttributeCategoryService {



    /**
     * 属性分类列表
     * @param pageNum
     * @param pageSize
     * @return
     */
    List<GoodsAttributeCategoryDto> list(int pageNum, int pageSize, String names);


    int getCount();



    /**
     * 新增属性分类
     * @param name
     * @param sort
     */
    public void save(String name,int sort);


    void  updateById(String id,String name,int sort);

    void  deleteById(String id);

    List<GoodsAttributeCategory> findAllAttributeCategory();

}
