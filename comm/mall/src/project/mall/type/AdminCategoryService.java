
package project.mall.type;

import kernel.web.Page;
import project.mall.type.model.Category;
import project.mall.type.model.CategoryLang;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @author: Winter
 * @date: 2022/11/2
 */
public interface AdminCategoryService {

    Page pagedQuery(int pageNo, int pageSize, String parentId, Integer level, String startTime, String endTime);


    void save(String name, int sort,String des, String parentId);

    List<CategoryLang> findLanByCategoryId(String categoryId, String lang);

    List<CategoryLang> findLanByCategoryIds(String categoryId, String lang);

    void update(Category bean, String name, String lang, String categoryId, String categoryLanId, String des);

    Category findById(String id);

    int count(String categoryId);

    void delete(List<CategoryLang> categoryLang);

    void update(Category entity);

    /**
     * 隐藏分类以及其下的所有子分类
     *
     * @param categoryId
     */
    void updateHideCategory(String categoryId);

    /**
     * 是否首页推荐
     * @param id
     * @param status
     */
    void updateStatus(String id, Integer status);

    List listSubCategory(String parentId);

    LinkedHashMap<Object, String> getParentCategory(String categoryId, Integer level);

}