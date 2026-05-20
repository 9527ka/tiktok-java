package project.web.api;

import ext.Result;
import kernel.exception.BusinessException;
import kernel.web.PageActionSupport;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import project.mall.orders.OrderTrackingService;
import project.mall.orders.model.OrderTracking;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 后台管理员/商家录入订单物流轨迹.
 */
@RestController
@Slf4j
public class AdminOrderTrackingController extends PageActionSupport {

    @Resource
    private OrderTrackingService orderTrackingService;

    @RequestMapping("admin/orderTracking!list.action")
    public Object list(HttpServletRequest request) {
        try {
            String orderId = request.getParameter("orderId");
            if (orderId == null || orderId.isEmpty()) {
                return Result.error(1, "orderId 不能为空");
            }
            List<OrderTracking> list = orderTrackingService.listByOrderId(orderId);
            Map<String, Object> data = new HashMap<>();
            data.put("list", list);
            return Result.success(data);
        } catch (BusinessException e) {
            return Result.error(1, e.getMessage());
        } catch (Throwable t) {
            log.error("orderTracking list error", t);
            return Result.error(1, "查询失败");
        }
    }

    @RequestMapping("admin/orderTracking!add.action")
    public Object add(HttpServletRequest request) {
        try {
            String orderId = request.getParameter("orderId");
            String eventTimeStr = request.getParameter("eventTime");
            String location = request.getParameter("location");
            String status = request.getParameter("status");
            String description = request.getParameter("description");

            if (orderId == null || orderId.isEmpty()) {
                return Result.error(1, "orderId 不能为空");
            }
            OrderTracking tracking = new OrderTracking();
            tracking.setOrderId(orderId);
            tracking.setEventTime(parseDate(eventTimeStr));
            tracking.setLocation(location);
            tracking.setStatus(status);
            tracking.setDescription(description);
            try {
                tracking.setCreator(this.getLoginPartyId());
            } catch (Exception ignored) {
            }
            orderTrackingService.add(tracking);
            return Result.success(tracking);
        } catch (BusinessException e) {
            return Result.error(1, e.getMessage());
        } catch (Throwable t) {
            log.error("orderTracking add error", t);
            return Result.error(1, "新增失败");
        }
    }

    @RequestMapping("admin/orderTracking!update.action")
    public Object update(HttpServletRequest request) {
        try {
            String id = request.getParameter("id");
            if (id == null || id.isEmpty()) {
                return Result.error(1, "id 不能为空");
            }
            OrderTracking tracking = new OrderTracking();
            tracking.setId(id);
            tracking.setEventTime(parseDate(request.getParameter("eventTime")));
            tracking.setLocation(request.getParameter("location"));
            tracking.setStatus(request.getParameter("status"));
            tracking.setDescription(request.getParameter("description"));
            orderTrackingService.update(tracking);
            return Result.success();
        } catch (BusinessException e) {
            return Result.error(1, e.getMessage());
        } catch (Throwable t) {
            log.error("orderTracking update error", t);
            return Result.error(1, "修改失败");
        }
    }

    @RequestMapping("admin/orderTracking!delete.action")
    public Object delete(HttpServletRequest request) {
        try {
            String id = request.getParameter("id");
            if (id == null || id.isEmpty()) {
                return Result.error(1, "id 不能为空");
            }
            orderTrackingService.delete(id);
            return Result.success();
        } catch (BusinessException e) {
            return Result.error(1, e.getMessage());
        } catch (Throwable t) {
            log.error("orderTracking delete error", t);
            return Result.error(1, "删除失败");
        }
    }

    private Date parseDate(String s) {
        if (s == null || s.isEmpty()) {
            return null;
        }
        String[] patterns = {"yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm", "yyyy-MM-dd"};
        for (String p : patterns) {
            try {
                return new SimpleDateFormat(p).parse(s);
            } catch (ParseException ignored) {
            }
        }
        return null;
    }

}
