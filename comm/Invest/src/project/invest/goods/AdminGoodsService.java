package project.invest.goods;

import kernel.web.Page;
import project.invest.goods.model.Goods;
import project.invest.goods.model.GoodsLang;

import java.util.List;


public interface AdminGoodsService {

    /**
     * 分页查询
     * @param pageNo
     * @param pageSize
     * @param name
     * @param status
     * @param startTime
     * @param endTime
     * @return
     */
    Page pagedQuery(int pageNo, int pageSize, String name,  Integer status, String startTime, String endTime);


    /**
     * 新增
     * @param name
     */
    void save(String name);

    /**
     * 根据id查询
     * @param id
     * @return
     */
    Goods findById(String id);

    /**
     * 根据goodsId与语言查找
     * @param GoodsId
     * @param lang
     * @return
     */
    List<GoodsLang> findLanByGoodsId(String GoodsId, String lang);


    void delete(String id,List<GoodsLang> goodsLangs);

    /**
     * 更新
     * @param name
     * @param iconImg
     * @param prize
     * @param goodsId
     * @param goodsLanId
     * @param status
     * @param des
     * @param lang
     * @param sort
     * @param total
     * @param lastAmount
     */
    void update(String name, String iconImg, String prize, String goodsId, String goodsLanId, String status, String des, String lang, String sort, String total, String lastAmount);
}