package project.invest.vip;

import kernel.web.Page;
import project.invest.vip.model.Vip;
import project.mall.seller.model.MallLevel;

public interface AdminVipService {

    Page pagedQuery();

    MallLevel findById(String id);

    void update(MallLevel mallLevel);
}