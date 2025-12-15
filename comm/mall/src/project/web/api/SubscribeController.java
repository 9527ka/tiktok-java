package project.web.api;

import kernel.exception.BusinessException;
import kernel.util.StringUtils;
import kernel.web.BaseAction;
import kernel.web.ResultObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import project.mall.subscribe.SubscribeService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 *订阅
 */

@Slf4j
@RestController
@CrossOrigin
public class SubscribeController extends BaseAction {

    @Resource
    private SubscribeService subscribeService;


    private static Log logger = LogFactory.getLog(SubscribeController.class);
    private final String action = "/api/subscribe!";


    /**
     * 添加
     *
     * @return
     */
    @PostMapping(action + "add.action")
    public Object add(HttpServletRequest request) {

        ResultObject resultObject = new ResultObject();

        String email = request.getParameter("email");

        if (StringUtils.isEmptyString(email)) {
            resultObject.setCode("1");
            resultObject.setMsg("邮箱不能为空");
            return resultObject;
        }

        try {
            subscribeService.saveSubscribe(email);
        } catch (BusinessException e) {
            e.printStackTrace();
            resultObject.setCode("1");
            resultObject.setMsg(e.getMessage());
            return resultObject;
        } catch (Exception e1) {
            e1.printStackTrace();
            resultObject.setCode("1");
            resultObject.setMsg("添加失败");
        }

        return resultObject;
    }
}
