package project.mall.seller;

import project.mall.seller.model.Seller;
import project.mall.utils.MallPageInfo;

import java.util.List;
import java.util.Map;


public interface FocusSellerService {

    MallPageInfo listFocusSeller(int pageNum, int pageSize, String partyId);

    Integer queryIsFocus(String sellerId, String partyId);

    Integer getFocusSellerCount(String partyId);

    Integer getFocusCount(String sellerId);

    Map<String, Integer> getFocusCounts(List<String> sellerIds);

    void addFocus(String partyId, Seller seller);

    void deleteFocus(String focusSellerId, String partyId);

    void deleteAllFocus(String sellerId);
}
