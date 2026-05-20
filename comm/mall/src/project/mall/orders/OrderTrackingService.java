package project.mall.orders;

import project.mall.orders.model.OrderTracking;

import java.util.List;

public interface OrderTrackingService {

    /**
     * 新增一条物流轨迹.
     */
    OrderTracking add(OrderTracking tracking);

    /**
     * 修改一条物流轨迹.
     */
    void update(OrderTracking tracking);

    /**
     * 删除一条物流轨迹.
     */
    void delete(String id);

    /**
     * 根据 ID 查询一条.
     */
    OrderTracking getById(String id);

    /**
     * 列出某订单所有物流轨迹, 按 eventTime 倒序 (最新在前).
     */
    List<OrderTracking> listByOrderId(String orderId);
}
