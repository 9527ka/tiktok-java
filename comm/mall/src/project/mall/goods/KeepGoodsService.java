package project.mall.goods;

import project.mall.utils.MallPageInfo;

import java.util.List;
import java.util.Map;


public interface KeepGoodsService {
    MallPageInfo  listKeepGoods(int pageNum, int pageSize, String partyId);

    Integer getKeepCount(String sellerGoodsId);

    Map<String, Integer> getSellerGoodsKeepCount(List<String> sellerGoodsIdList);

    void addKeep(String partyId, String sellerGoodsId);


    void deleteKeep(String sellerGoodsId, String partyId);

    Integer queryIsKeep(String sellerGoodsId, String partyId);

    Integer getKeepGoodsCount(String partyId);

}
