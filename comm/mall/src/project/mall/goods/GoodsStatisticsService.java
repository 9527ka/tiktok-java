package project.mall.goods;

import kernel.util.PageInfo;
import org.springframework.transaction.annotation.Transactional;
import project.mall.goods.dto.CategoryGoodCountDto;
import project.mall.goods.dto.GoodsSellerSalesDto;
import project.mall.goods.model.SellerGoods;
import project.mall.goods.model.SystemGoods;
import project.mall.goods.model.SystemGoodsLang;
import project.mall.utils.MallPageInfo;
import project.web.api.SellerGoodsQuery;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 用于商品相关数据统计的服务类.
 */
public interface GoodsStatisticsService {
    /**
     * 重新计算指定商品的 showWeight 信息并更新数据库.
     *
     * @param goodsIdList
     * @return
     */
    void updateRefreshSellerGoodsShowWeight(List<String> goodsIdList);


}
