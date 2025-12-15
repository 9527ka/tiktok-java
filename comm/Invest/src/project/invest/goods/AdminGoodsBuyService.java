package project.invest.goods;

import kernel.web.Page;
import project.invest.goods.model.GoodsBuy;

public interface AdminGoodsBuyService {

    Page pagedQuery(int pageNo, int pageSize, String id, String userCode, String userName, String phone, Integer status, String startTime, String endTime);

    /**
     * 发货或取消
     * @param id
     * @param type
     * @param remark
     */
    void updateStatus(String id, String type, String remark);

    /**
     * 根据id查询
     * @param id
     * @return
     */
    GoodsBuy findGoodsBuyById(String id);

    /**
     * 余额兑换记录
     * @param pageNo
     * @param pageSize
     * @param id
     * @param userCode
     * @param userName
     * @param phone
     * @param status
     * @param startTime
     * @param endTime
     * @return
     */
    Page pagedQueryExchange(int pageNo, int pageSize, String id, String userCode, String userName, String phone, String startTime, String endTime);
}