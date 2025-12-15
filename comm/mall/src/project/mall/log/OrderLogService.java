package project.mall.log;

import project.mall.log.model.OrderLog;

import java.util.List;

public interface OrderLogService {

    List<OrderLog> listByOrderId(String orderId);

    void saveSync(OrderLog orderLog);

}
