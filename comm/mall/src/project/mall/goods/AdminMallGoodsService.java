package project.mall.goods;

import jnr.ffi.annotations.In;
import kernel.web.Page;
import project.mall.goods.model.Evaluation;
import project.mall.goods.model.SellerGoods;
import project.mall.goods.model.SystemGoods;
import project.mall.goods.model.SystemGoodsLang;
import project.web.admin.dto.DeleteSku;
import project.web.admin.model.SystemGoodModel;

import java.util.List;

public interface AdminMallGoodsService {
    /**
     * 管理后台商品库列表
     * @param pageNo
     * @param pageSize
     * @param name
     * @param isShelf
     * @param categoryId
     * @param secondaryCategoryId
     * @param id
     * @param updateStatus
     * @return
     */
    Page pageQuery(int pageNo, int pageSize, String name, Integer isShelf, String categoryId, String secondaryCategoryId, String id, Integer updateStatus);

    Page pagedQuery(int pageNo, int pageSize, String name, Integer isShelf, String startTime, String endTime, String PName);
    Page pagedQuery1(int pageNo, int pageSize, String name, Integer isShelf, String startTime, String endTime, String PName);

    void save(String name, String categoryId);

    SystemGoods findById(String goodsId);

    SellerGoods findSellerGoodsById(String sellerGoodsId);

    List<SystemGoodsLang> findLanByGoodsId(String goodsId, String lang);

    void update(SystemGoods goods, String name, String lang, String content, String content1, String unit, String goodsLanId,String attributeId);

    void updateStatus(String id, int status, int type, int duration);

    void delete(String id, List<SystemGoodsLang> lanByGoodsId);

    /**
     * 管理后台店铺商品列表
     * @param pageNo
     * @param pageSize
     * @param goodId
     * @param goodName
     * @param pName
     * @param sellerName
     * @param categoryId
     * @param secondaryCategoryId
     * @return
     */
    Page pagedQuerySellerGoods(int pageNo, int pageSize, String goodId, String goodName, String pName, String sellerName,
                               String categoryId, String secondaryCategoryId, String loginPartyId, String sellerRoleName);

    Page pagedQueryEvaluation(int pageNo, int pageSize, String sellerGoodsId, String sellerId, String userName,
                              Integer evaluationType);

    Integer getSystemGoodsNum(String lang);

    Evaluation findEvaluationById(String id);

    void deleteEvaluation(Evaluation e);

    List<SystemGoods> findGoodsByCategoryId(String CategoryId);

    /**
     * 查看商品记录的一级分类或二级分类使用了该分类id的商品数量.
     * @param categoryId
     * @return
     */
    int getGoodsCountByCategoryId(String categoryId);

    void updateBuyMin(String sellerId, int buyMin);

    void updateEvaluationStatus(String id, int parseInt);

    void update(SystemGoodModel systemGoodModel);

    void updateShelf(String id, Integer isShelf);

    void updateUpdateStatus(String id, Integer updateStatus);

    void deleteSku(String skuId);

    void adminShelfBatch(List<String> sellerGoodsIdList, Integer isShelf);

    List<SellerGoods> findSellerGoodsBySystemGoodsId(String goodId);

}
