package project.mall.type;

import kernel.web.Page;
import project.mall.type.model.Category;
import project.mall.type.model.CategoryLang;
import project.mall.type.vo.CategoryVO;

import java.util.List;

public interface CategoryLangService {

    public List<CategoryLang> listCategoryLang(List<String> categoryIdList, String lang);

}
