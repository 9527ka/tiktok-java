
package project.mall.combo;

import kernel.web.Page;
import project.mall.combo.model.Combo;
import project.mall.combo.model.ComboLang;
import project.mall.type.model.Category;
import project.mall.type.model.CategoryLang;

import java.util.List;

/**
 * @Description:
 * @author: Winter
 * @date: 2022/11/2
 */
public interface AdminComboService {

    Page pagedQuery(int pageNo, int pageSize, String name, String startTime, String endTime);

    void save(String name);

    List<ComboLang> findLanByComboId(String categoryId, String lang);

    void update(Combo bean, String name, String lang, String comboId, String comboLanId, String content);

    Combo findById(String id);

    void delete(String id, List<ComboLang> comboLangs);

    Page pagedQueryRecordList(int pageNo, int pageSize, String userCode, String sellerName, String startTime, String endTime);

    Page pagedQueryRecordGoodsList(int pageNo, int pageSize, String partyId);
    //Page pagedQueryRecordGoodsList1(int pageNo, int pageSize, String partyId);

}