package project.mall.seller;

import project.mall.seller.model.MallLevel;
import project.mall.seller.model.SellerCredit;

import java.util.List;

public interface MallLevelService {

    List<MallLevel> listLevel();

    MallLevel findByLevel(String level);

}
