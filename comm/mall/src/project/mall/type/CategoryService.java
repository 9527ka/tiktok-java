package project.mall.type;

import kernel.web.Page;
import project.mall.goods.model.SellerGoods;
import project.mall.type.model.Category;
import project.mall.type.model.CategoryLang;
import project.mall.type.vo.CategoryVO;

import java.util.List;

public interface CategoryService {
    Category getById(String id);

    List<Category> listByIds(List<String> idList);

    /**
     * 分页列出商品分类列表，禁用标记的记录也展示，管理后台专用.
     *
     * @param pageNum
     * @param pageSize
     * @return
     */
    public Page pageListCategory(int pageNum, int pageSize);

    List<Category> listCategory(int pageNum, int pageSize);

    List<Category> listRecommendCategory(int pageNum, int pageSize);

    public List<Category> listTopLevelCategorys();

    public List<Category> listSubCategorys(String parentId);

    /**
     * 将有效的商品分类以树形结构展示出来。
     *
     * @return
     */
    public List<CategoryVO> getCategoryTree(boolean showHidden);

    List<CategoryVO> loadBuildCategoryTree(List<String> categoryIdList);

    CategoryLang selectLang(String lang, String categoryId);

    /**
     * 级联修改可见性
     *
     * @param categoryId
     * @param status
     */
    void updateStatus(String categoryId, int status);

}
