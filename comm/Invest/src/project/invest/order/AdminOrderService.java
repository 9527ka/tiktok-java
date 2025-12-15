package project.invest.order;

import kernel.web.Page;
import project.invest.project.model.InvestOrders;

import java.util.Map;

public interface AdminOrderService {
    /**
     * 查询
     * @param pageNo
     * @param pageSize
     * @param id
     * @param userCode
     * @param userName
     * @param phone
     * @param roleName
     * @param startTime
     * @param endTime
     * @param status
     * @return
     */
    Page pagedQuery(int pageNo, int pageSize, String id, String userCode, String userName, String phone, String roleName, String startTime, String endTime, Integer status);

    /**
     * 订单取消
     * @param id
     */
    void updateCancel(String id);

    /**
     * 根据id查询
     * @param id
     * @return
     */
    InvestOrders findOrdersById(String id);

    /**
     * 订单关闭
     * @param order
     */
    void updateClosure(InvestOrders order);

    Map findDaySumData();
}