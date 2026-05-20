package project.web.api;

import kernel.exception.BusinessException;
import kernel.web.BaseAction;
import kernel.web.ResultObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import project.mall.orders.GoodsOrdersService;
import project.mall.orders.OrderTrackingService;
import project.mall.orders.model.MallOrdersPrize;
import project.mall.orders.model.OrderTracking;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户侧物流轨迹查询.
 * 校验 orderId 必须属于当前登录用户.
 */
@RestController
@CrossOrigin
public class OrderTrackingController extends BaseAction {

    private final Logger logger = LogManager.getLogger(OrderTrackingController.class);

    @Autowired
    private OrderTrackingService orderTrackingService;

    @Autowired
    private GoodsOrdersService goodsOrdersService;

    @RequestMapping("api/orderTracking!list.action")
    public Object list(HttpServletRequest request) {
        ResultObject resultObject = readSecurityContextFromSession(new ResultObject());
        if (!"0".equals(resultObject.getCode())) {
            return resultObject;
        }
        try {
            String orderId = request.getParameter("orderId");
            if (orderId == null || orderId.isEmpty()) {
                throw new BusinessException("orderId 不能为空");
            }
            String partyId = getLoginPartyId();
            MallOrdersPrize order = goodsOrdersService.getMallOrdersPrize(orderId);
            if (order == null || !partyId.equals(order.getPartyId())) {
                throw new BusinessException("订单不存在");
            }

            List<OrderTracking> trackings = orderTrackingService.listByOrderId(orderId);
            if (trackings == null) {
                trackings = Collections.emptyList();
            }

            Map<String, Object> data = new HashMap<>();
            data.put("trackings", trackings);
            data.put("shipmentProvider", order.getShipmentProvider());
            data.put("shipmentTradeNo", order.getShipmentTradeNo());
            resultObject.setData(data);
        } catch (BusinessException e) {
            resultObject.setCode("1");
            resultObject.setMsg(e.getMessage());
        } catch (Throwable t) {
            resultObject.setCode("1");
            resultObject.setMsg("程序错误");
            logger.error("OrderTracking list error", t);
        }
        return resultObject;
    }
}
