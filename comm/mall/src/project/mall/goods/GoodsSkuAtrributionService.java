package project.mall.goods;

import project.mall.goods.dto.GoodSkuAttrDto;
import project.mall.goods.model.GoodsAttributeVo;
import project.mall.goods.model.GoodsAttrsSkuVo;
import project.mall.goods.model.SellerGoods;

import java.util.List;
import java.util.Map;

/**
 *
 */
public interface GoodsSkuAtrributionService {

    List<GoodsAttributeVo> findCachedGoodsAttributeBySkuId(String skuId, String lang);

    List<GoodsAttributeVo> findGoodsAttributeBySkuId(String skuId, String lang);


    Map<String,List<GoodsAttributeVo>> listGoodsAttributeBySkuIds(List<String> skuIds, String lang);

    /**
     * 传入系统商品id，获取改商品可以选择的所有属性值，和所有属性值和skuId的关联关系。
     * 前端通过选择属性值，可以映射出商品的skuId
     *
     * @param systemGoodsId
     * @param lang
     * @return
     */
    GoodsAttrsSkuVo findGoodsAttributeByGoodsId(String systemGoodsId, String lang);

    Map<String, GoodsAttrsSkuVo> findGoodsAttributeByGoodsIds(List<String>  systemGoodsIds, String lang);

    GoodSkuAttrDto getCachedGoodsAttrListSku(String goodId, String lang);

    GoodSkuAttrDto getGoodsAttrListSku(String goodId, String lang);


    GoodSkuAttrDto getGoodsAttrListSkuBySellerGoods(SellerGoods goodId, String lang);

    GoodSkuAttrDto getCachedGoodsAttrListSkuBySellerGoods(SellerGoods goodId, String lang);

    String selectSkuCoverImg(String skuId);

}
