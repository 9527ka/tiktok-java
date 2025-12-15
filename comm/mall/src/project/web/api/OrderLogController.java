package project.web.api;

import kernel.exception.BusinessException;
import kernel.util.DateUtils;
import kernel.web.BaseAction;
import kernel.web.ResultObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import project.mall.log.model.OrderLog;
import project.mall.log.OrderLogService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@CrossOrigin
public class OrderLogController extends BaseAction {
    private Logger logger = LogManager.getLogger(OrderLogController.class);

    @Autowired
    protected OrderLogService logService;

    @RequestMapping("api/orderLog!list.action")
    public Object list(HttpServletRequest request) {

        ResultObject resultObject = readSecurityContextFromSession(new ResultObject());
        if (!"0".equals(resultObject.getCode())) {
            return resultObject;
        }

        try {
            String orderId = request.getParameter("orderId");

            List<OrderLog> orderLogs = logService.listByOrderId(orderId);
            orderLogs.stream().filter(orderLog -> orderLog.getCreateTime()!=null).forEach(orderLog -> orderLog.setCreateTimeStr(DateUtils.getLongDate(orderLog.getCreateTime())));
            resultObject.setData(orderLogs);
        } catch (BusinessException e) {
            resultObject.setCode("1");
            resultObject.setMsg(e.getMessage());
        } catch (Throwable t) {
            resultObject.setCode("1");
            resultObject.setMsg("程序错误");
            logger.error("error:", t);
        }
        return resultObject;
    }
}
