package project.invest.goods;

import project.invest.goods.model.Goods;
import project.invest.goods.model.GoodsBuy;
import project.invest.goods.model.PointExchange;
import project.invest.goods.model.Useraddress;
import project.mall.goods.model.SystemGoodsLang;

import java.util.List;


public interface GoodsService {

    List<Goods>  listGoodsSell( int pageNum, int pageSize);


    List<GoodsBuy>  listGoodsBuy(String partyId, int pageNum, int pageSize);

    GoodsBuy findGoodsBuyById(String id);

    List<PointExchange>  listPointExchange(String partyId, int pageNum, int pageSize);

    PointExchange findPointExchangeById(String id);

    Goods findById(String goodsId);

    void updateBuyGoods(String partyId,String goodsId,int amount,String phone,String contacts,String address);

    void updateExchangeUsdt(String partyId,String goodsId,int amount,long scale);

    void saveAddress(String partyId,int use,String phone,String contacts,String address);

    void updateAddress(String id,String partyId,int use,String phone,String contacts,String address);

    void removeAddress(String id);

    List<Useraddress> listAddress(String partyId);

    List<Useraddress> getAddressUse(String partyId);

    /**
     * 查询商品多语言设置
     * @param goodsId 商品编号
     * @return
     */
    List<SystemGoodsLang> getSystemGoodsLang(String goodsId);
}
