package project.web.api;

import com.alibaba.fastjson.JSONObject;
import kernel.exception.BusinessException;
import kernel.web.BaseAction;
import kernel.web.ResultObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import project.mall.orders.GoodsOrdersService;
import project.redis.RedisHandler;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
@CrossOrigin
public class OrdersLocalController extends BaseAction {


    @Resource
    private GoodsOrdersService goodsOrdersService;

    @Resource
    protected RedisHandler redisHandler;

    private static Log logger = LogFactory.getLog(OrdersLocalController.class);
    private final String action = "/api/order-local!";

    /**
     * 添加
     * @return
     */
    @PostMapping( action+"submit.action")
    public Object add(HttpServletRequest request){
        ResultObject resultObject = new ResultObject();


        String uuid = request.getParameter("uuid");

        String num = request.getParameter("num");

        String partyId = request.getParameter("partyId");

        try {
            JSONObject object = new JSONObject();
            object.put("orderList", goodsOrdersService.saveGoodsBuy(partyId,uuid,num));
            resultObject.setData(object);
        }catch (BusinessException e){
            e.printStackTrace();
            resultObject.setCode("1");
            resultObject.setMsg(e.getMessage());
            return resultObject;
        }catch (Exception e1){
            e1.printStackTrace();
            resultObject.setCode("1");
            resultObject.setMsg("提交失败");
        }


        return resultObject;
    }


}
