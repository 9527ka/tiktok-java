package project.web.api;

import com.alibaba.fastjson.JSONObject;
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

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@CrossOrigin
public class SysParaProductController extends BaseAction {

    private Logger logger = LogManager.getLogger(SysParaProductController.class);

    @Autowired
    protected GoodsOrdersService ordersService;

    @RequestMapping("api/sysParaProduct!info.action")
    public Object paraInfo(HttpServletRequest request) {

        ResultObject resultObject = readSecurityContextFromSession(new ResultObject());
        if (!"0".equals(resultObject.getCode())) {
            return resultObject;
        }

        JSONObject o = new JSONObject();

        String loginPartyId = getLoginPartyId();

        try {
            Map<String ,String> results  = ordersService.queryProductProfit(loginPartyId);
            o.put("sysParaMax", results.get("sysParaMax"));
            o.put("sysParaMin", results.get("sysParaMin"));
            resultObject.setData(o);

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

    @RequestMapping("api/sysParaSign!info.action")
    public Object sellerSign() {

        ResultObject resultObject = new ResultObject();
        JSONObject o = new JSONObject();
        try {
            Map<String ,String> results  = ordersService.querySellerSign();
            o.putAll(results);
            resultObject.setData(o);

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
