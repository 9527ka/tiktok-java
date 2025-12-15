package project.mall.combo;

import project.mall.combo.model.Combo;
import project.mall.combo.model.ComboUser;
import project.mall.utils.MallPageInfo;

import java.util.List;

public interface ComboService {

    ComboUser findComboUserByPartyId(String partyId);

    Combo findComboByPartyId(String partyId);

    void updateComboUser(ComboUser comboUser);

    List<Combo> listCombo();


    void updateBuy(String partyId,String id,String name);


    MallPageInfo listComboRecord(String partyId, String begin , String end, int pageNum, int pageSize);


}
