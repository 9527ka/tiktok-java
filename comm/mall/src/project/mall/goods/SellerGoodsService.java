package project.mall.goods;

import cn.hutool.core.util.StrUtil;
import kernel.util.PageInfo;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.transaction.annotation.Transactional;
import project.mall.goods.dto.CategoryGoodCountDto;
import project.mall.goods.dto.GoodsSellerSalesDto;
import project.mall.goods.dto.SellerTopNDto;
import project.mall.goods.model.*;
import project.mall.goods.vo.*;
import project.mall.utils.MallPageInfo;
import project.web.api.SellerGoodsQuery;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public interface SellerGoodsService {
    /**
     * 查看商家所有商品的一级分类id集合.
     *
     * @param sellerId
     * @param onlyOnShelf
     * @return
     */
    List<String> getSellerCategoryList(String sellerId, boolean onlyOnShelf);

    /**
     * 查看商家所有商品二级分类id集合.
     *
     * @param sellerId
     * @param onlyOnShelf
     * @return
     */
    List<String> getSellerSecondaryCategoryList(String sellerId, boolean onlyOnShelf);

    /**
     * 查看商家所有商品所有分类id信息集合(一级分类id + 二级分类id)
     *
     * @param sellerId
     * @param onlyOnShelf
     * @return
     */
    List<String> getSellerAllCategoryList(String sellerId, boolean onlyOnShelf);

    long getNumberOfVisitorsByDate(String sellerId, Date startTime, Date endTime);

    List<CategoryGoodCountDto> getCategoryGoodCount(String sellerId, String lang);

    List<SellerGoods> getCategoryGoodList(int pageNum, int pageSize, String sellerId, String categoryId);

    MallPageInfo listGoodsSell(int pageNum, int pageSize, String sellerId, String categoryId, String secondaryCategoryId, Integer isNew,
                               Integer rec, Integer isRec, Integer isHot, Integer isPrice, String lang, Integer discount);

    MallPageInfo listGoodsSellAdmin(int pageNum, int pageSize, SellerGoodsQuery sellerGoodsQuery, Integer isNew, Integer isRec, Integer isHot, Integer isPrice);
//    MallPageInfo listGoodsSellAdmin1(int pageNum, int pageSize, SellerGoodsQuery sellerGoodsQuery, Integer isNew, Integer isRec, Integer isHot, Integer isPrice);

    MallPageInfo listSystemGoods(int pageNum, int pageSize, String categoryId, String secondaryCategoryId, String lang, String sellerId, String goodsName, String id);
//    MallPageInfo listSystemGoods1(int pageNum, int pageSize, String categoryId, String secondaryCategoryId, String lang, String sellerId, String goodsName, String id);

    List<SystemGoods> querySearchKeyword(String lang, String keyword, int isHot, int isNew, int isPrice);

    List<SellerGoods> querySearchGoods(int pageNum, int pageSize, String goodsId, int isPrice, int isNew, int isHot);

    List<SystemGoods> queryAdminSearchGoods(int pageNum, int pageSize, String keywords, String lang);

    List<SellerGoods> querySearchsellerGoods(int pageNum, int pageSize, String sellerId, String keywords, String lang);
//    List<SellerGoods> querySearchsellerGoods1(int pageNum, int pageSize, String sellerId, String keywords, String lang);

//    List<SellerGoods> querySearchsellerGoods(int pageNum, int pageSize, String keywords, String lang,
//                                             Integer isNew, Integer isRec, Integer isHot, Integer isPrice, Integer is_discount);

    PageInfo pagedSearchsellerGoods(int pageNum, int pageSize, String keywords, String lang, Integer isNew,
                                    Integer isRec, Integer isHot, Integer isPrice, Integer is_discount);
//    PageInfo pagedSearchsellerGoods1(int pageNum, int pageSize, String keywords, String lang, Integer isNew,
//                                    Integer isRec, Integer isHot, Integer isPrice, Integer is_discount);

    void saveSellerGoods(SellerGoods sellerGoods);

    void updateSellerGoods(SellerGoods sellerGoods);

    SellerGoods getSellerGoods(String sellerGoodsId);

    SystemGoods getSystemGoods(String systemGoodsId);

    List<SystemGoods> getSystemGoodsBatch(List<String> systemGoodsIdList);

    List<SellerGoods> getSellerGoodsBatch(List<String> sellerGoodsId);

    List<SellerGoods> listBySystemGoodsIds(List<String> systemGoodsIdList);

    SellerGoods getSellerGoods(String goodsId, String sellerId);

    Integer getSellerGoodsNum(String sellerId);

    Long getSoldNumBySellerId(String sellerId);

    Map<String, Long> getSoldNumsBySellerIds(List<String> sellerIds);

    /**
     * 批量统计多个店铺的销量
     *
     * @param sellerIdList
     * @return
     */
    List<SoldGoodsCount> getSoldNumBySellerIds(List<String> sellerIdList);

    Long getSoldNumByGoodsId(String sellerId);

    Long getOnSelfGoodsNumBySellerId(String sellerId);

    Long getGoodsNumBySellerId(String sellerId);

    Map<String, Long> getGoodsNumBySellerIds(List<String> sellerIds);

    /**
     * 根据商户id和语言查询商品数
     * @param sellerId 商户id
     * @param lang 语言
     * @return 商品数
     */
    Long getGoodsNumBySellerIdAndLang(String sellerId,String lang);

    List<SellerGoodsCount> getGoodsNumBySellersAndLang(List<String> sellerIdList, String lang);

    Long getViewsNumBySellerId(String sellerId);

    List<SellerViewCount> getViewsNumBySellerIds(List<String> sellerIdList);

    Long getSellerGoodsNumBySellerId(String sellerId, String lang);

    void deleteSellerGoodsBySystemGoodId(String systemGoodIds);

    void deleteSellerGoods(String sellerGoodsId, String sellerId);

    void deleteAllSellerGoods(String sellerId);

    @Transactional
    void shelfBatch(List<String> sellerGoodsIdList, String sellerId, Integer isShelf);

    SellerGoods getRandomSellerGoods(String sellerId);

    // 获取当前卖家当前处于激活状态的直通车商品
    List<SellerGoods> getSellerComboActiveGoods(String sellerId);

    // 获取所有商家上架的商品,isCombo为null查询所有的，1是查询直通车激活的,0是查询上架，但是直通车没有激活
    List<String> getSellerGoodsId(String sellerId, Integer isCombo);

    // 对批量对商品新增浏览
    void updateVirtualViewsBatch(String sellerId, Map<String, Long> goodsViewAdd);

    void addRealViews(String sellerId, String goodsId, String viewerId,Integer viewsNum);

    Long getViewNums(String goodsId);

    Long getRealViewNums(String goodsId);

    Map<String, Long> getViewNums(List<String> goodsId);

    Map<String, Long> getRealViewNums(List<String> goodsId);

    List<GoodsSellerSalesDto> listGoodsSellerSales(String sellerId, String lang);

    /**
     * 优化商铺销售 topN 统计逻辑（真实处理方法：topNSellers），允许复用最近的统计结果.
     *
     * @param fromTime
     * @param toTime
     * @param topN
     * @return
     */
    List<SellerTopNDto> cacheTopNSellers(String fromTime, String toTime, int topN);

    /**
     * 列出指定时间区间的店铺销量排行记录.
     * 剔除演示店铺和演示账号产生的订单记录
     *
     * @param fromTime
     * @param toTime
     * @param topN
     * @return
     */
    List<SellerTopNDto> getTopNSellers(String fromTime, String toTime, int topN);

    /**
     * 统计指定时间的销售总额数据
     * 剔除演示店铺和演示账号产生的订单记录
     *
     * @param fromTime
     * @param toTime
     * @return
     */
    Map<String, Object> querySumSellerOrdersPrize(String fromTime, String toTime);

    /**
     * 统计指定时间的销售总额数据
     * 剔除演示店铺和演示账号产生的订单记录
     *
     * @param fromTime
     * @param toTime
     * @return
     */
    Map<String, Object> queryCacheSumSellerOrdersPrize(String fromTime, String toTime);

    /**
     * 计算店铺商品数量
     *
     * @param sellerId
     * @param shelfState
     * @return
     */
    int getCountGoods(String sellerId, int shelfState);

    /**
     * 商品上架商品的时候，根据sku自动计算每个goodsSku的价格等信息
     *
     * @param sellerGoods
     */
    void saveSellerGoodsSkus(SellerGoods sellerGoods);

    /**
     * 商品上架商品的时候，根据sku自动计算每个goodsSku的价格等信息
     *
     * @param sellerGoods
     */
    void updateSellerGoodsSkus(SellerGoods sellerGoods);

    void fillSellerGoodsSkus(SellerGoods sellerGoods);

    /**
     * 通过sellerGoods 和skuId找 SellerGoodsSku，用于计算价格
     *
     * @param sellerGoods
     * @param skuId
     * @return
     */
//    SellerGoodsSku findSellerGoodSku(SellerGoods sellerGoods, String skuId);

    SellerGoodsSkuInfoVO findSellerGoodSku(SellerGoods sellerGoods, String skuId);

    SellerGoodsSkuInfoVO findCachedSellerGoodSku(SellerGoods sellerGoods, String skuId);

    List<SellerGoods> listRecommendAndNewGoods(int type, PageInfo pageInfo);

    List<SellerGoods> listAutoSellerGoodsList(int type);

    List<SellerGoods> listRecommendAndLikeGoods(String partyId, String sellerId, int type);

    void insertBrowsHistory(String userId, String sellerGoodsId, String sellerId);

    /**
     * 根据系统商品ID更新所有商户的商品系统价格
     *
     * @param goodsId
     * @param systemPrice
     * @return
     */
    void updateSellerPriceByGoodsId(String goodsId, Double systemPrice);

    /**
     * @param goodsId
     * @param isValid 商品是有效 1、有效 0、无效
     */
    void updateSellerGoodsValid(String goodsId, Integer isValid);

    void updateSellerGoodsCategory(String goodsId, String newCategoryId, String newSecondaryCategoryId);

    SystemGoodsLang selectGoodsLang(String lang, String goodsId);

    /**
     * 你没有看错
     *
     * @param dataList
     * @return
     */
    int[] updateBatchShowWeight1(final List<GoodsShowWeight> dataList);

    void updateBatchShowWeight2(final List<GoodsShowWeight> dataList);

    List<SellerGoods> listDiscountSellerGoods(int pageNum, int pageSize);


    List<SellerGoods> pagedAllSellerGoods(int pageNum, int pageSize);

    /**
     * 提取第一次上架时间大于指定临界时间的商品记录
     * 注意： 支持 limitTime 值为0
     *
     * @param limitTime
     * @param pageNum
     * @param pageSize
     * @return
     */
    List<SellerGoods> pagedNewSellerGoods(long limitTime, int pageNum, int pageSize);

    /**
     * 提取已过新手保护期的商品记录
     *
     * @param limitTime
     * @param pageNum
     * @param pageSize
     * @return
     */
    List<SellerGoods> pagedOldSellerGoods(long limitTime, int pageNum, int pageSize);

//    /**
//     * 更新商品属性缓存
//     *
//     * @param sellerGoodsSkuList 商品属性集合
//     */
//    void updateCachedSellerGoodSku(List<SellerGoodsSku> sellerGoodsSkuList);

    //    /**
//     * 删除商品属性缓存
//     *
//     * @param sellerGoodsSkuList 商品属性集合
//     */
//    void deleteCachedSellerGoodSku(List<SellerGoodsSku> sellerGoodsSkuList);
    void deleteCachedSellerGoodSku1(SellerGoods sellerGoods);

    /**
     * 批量更新利润率，折扣率
     * @param goodsIds
     * @param partyId
     * @param has_discount
     * @param discountStartTime
     * @param discountEndTime
     * @param discount_ratio
     * @param profit_ratio
     */
    void updateDisProBatchBatch(List<String> goodsIds, String partyId, boolean has_discount, Date discountStartTime, Date discountEndTime, double discount_ratio, double profit_ratio);


//    /**
//     * 根据商家商品id查询
//     * @param sellerGoodsId
//     * @return
//     */
//    List<SellerGoodsSku> listSellerGoodsSku(String sellerGoodsId);

    List<SellerGoodsSkuInfoVO> listSellerGoodsSku(String systemGoodsId);

    /**
     * 清理浏览量记录
     */
    int updateCleanView(String partyId,String sellerGoodsId,String SellerId,Date limitTime);

    int[] updateBatchGoodsTitle(final List<SystemGoods> dataList);

    int[] updateBatchGoodsSku(final List<SellerGoods> dataList);

    MallPageInfo tmpPageListAllSystemGoods(int pageNum, int pageSize, String categoryId, String secondaryCategoryId);

    List<SystemGoodsLang> listSystemGoodsLang(List<String> goodsIdList, String lang);

//    List<SellerGoodsSku> listSellerGoodsSku(List<String> sellerGoodsIdList);

//    List<SellerGoods> listSellerGoodsBySku(String systemGoodsId, String skuId);

}
