package project.mall.seller;

import kernel.web.Page;
import project.mall.seller.model.Seller;
import project.mall.seller.model.SellerCredit;

import java.util.List;
import java.util.Map;

public interface SellerCreditService {

    SellerCredit addCredit(String sellerId, int accScore, Integer eventType, String eventKey, String reason);

}
