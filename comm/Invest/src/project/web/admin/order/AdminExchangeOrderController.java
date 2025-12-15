package project.web.admin.order;

import kernel.exception.BusinessException;
import kernel.util.StringUtils;
import kernel.util.ThreadUtils;
import kernel.web.PageActionSupport;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import project.Constants;
import project.invest.order.AdminExchangeOrderService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/exchange/order")
public class AdminExchangeOrderController extends PageActionSupport {

    private Logger logger = LogManager.getLogger(AdminExchangeOrderController.class);

    @Resource
    private AdminExchangeOrderService adminExchangeOrderService;

    private Map<String, Object> session = new HashMap<String, Object>();

    private final static Object obj = new Object();

    /**
     * 获取 otc订单 列表
     */
    @RequestMapping("/list.action")
    public ModelAndView list(HttpServletRequest request) {
        String pageNo = request.getParameter("pageNo");
        String message = request.getParameter("message");
        String error = request.getParameter("error");
        String name_para = request.getParameter("name_para");
        String id = request.getParameter("id");
        String phone = request.getParameter("phone");
        String roleName = request.getParameter("roleName");
        String succeeded_para = request.getParameter("succeeded_para");
        String startTime = request.getParameter("startTime");
        String endTime = request.getParameter("endTime");
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("order_exchange_list");

        try {

            this.checkAndSetPageNo(pageNo);

            this.pageSize = 20;

            String session_token = UUID.randomUUID().toString();
            this.session.put("session_token", session_token);

            Integer status = null;
            if (!StringUtils.isEmptyString(succeeded_para)) {
                status = Integer.valueOf(succeeded_para).intValue();
            }
            this.page = this.adminExchangeOrderService.pagedQuery(this.pageNo, this.pageSize,name_para,phone,id,roleName,status,startTime,endTime);
            List<Map> list = this.page.getElements();
            for (int i = 0; i < list.size(); i++) {
                Map map = list.get(i);
                if (null == map.get("roleName")) {
                    map.put("roleNameDesc", "");
                } else {
                    String rolename = map.get("roleName").toString();
                    map.put("roleNameDesc", Constants.ROLE_MAP.containsKey(rolename) ? Constants.ROLE_MAP.get(rolename) : rolename);
                }
                map.put("symbolValue", new BigDecimal(map.get("symbolValue").toString()).toPlainString());
            }
            modelAndView.addObject("session_token", session_token);

        } catch (BusinessException e) {
            modelAndView.addObject("error", e.getMessage());
            return modelAndView;
        } catch (Throwable t) {
            logger.error(" error ", t);
            modelAndView.addObject("error", "[ERROR] " + t.getMessage());
            return modelAndView;
        }

        modelAndView.addObject("pageNo", this.pageNo);
        modelAndView.addObject("pageSize", this.pageSize);
        modelAndView.addObject("page", this.page);
        modelAndView.addObject("message", message);
        modelAndView.addObject("error", error);
        modelAndView.addObject("succeeded_para", succeeded_para);
        modelAndView.addObject("roleName", roleName);
        modelAndView.addObject("phone", phone);
        modelAndView.addObject("startTime", startTime);
        modelAndView.addObject("endTime", endTime);
        modelAndView.addObject("id", id);
        modelAndView.addObject("name_para", name_para);
        return modelAndView;
    }

    /**
     * 处理一个代付
     */
    @RequestMapping( "/success.action")
    public ModelAndView success(HttpServletRequest request) {
        String session_token = request.getParameter("session_token");
        String id = request.getParameter("id");
        String safeword = request.getParameter("safeword");

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("redirect:/" + "exchange/order/list.action");

        try {

            Object object = this.session.get("session_token");
            this.session.remove("session_token");
            if (null == object || StringUtils.isNullOrEmpty(session_token) || !session_token.equals((String) object)) {
                throw new BusinessException("请稍后再试");
            }

            synchronized (obj) {
                // 统一处理成功接口
                this.adminExchangeOrderService.saveSucceeded(id, safeword, this.getUsername_login());
                ThreadUtils.sleep(300);
            }

        } catch (BusinessException e) {
            modelAndView.addObject("error", e.getMessage());
            return modelAndView;
        } catch (Throwable t) {
            logger.error("update error ", t);
            modelAndView.addObject("error", "程序错误");
            return modelAndView;
        }

        modelAndView.addObject("message", "操作成功");
        return modelAndView;
    }


    /**
     * 驳回
     */
    @RequestMapping("/reject.action")
    public ModelAndView reject(HttpServletRequest request) {
        String session_token = request.getParameter("session_token");
        String id = request.getParameter("id");
        String failure_msg = request.getParameter("failure_msg");

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("redirect:/" + "exchange/order/list.action");

        try {

            Object object = this.session.get("session_token");
            this.session.remove("session_token");
            if (null == object || StringUtils.isNullOrEmpty(session_token) || !session_token.equals((String) object)) {
                throw new BusinessException("请稍后再试");
            }

            synchronized (obj) {
                // 统一处理失败接口
                this.adminExchangeOrderService.saveReject(id, failure_msg, this.getUsername_login());
                ThreadUtils.sleep(300);
            }

        } catch (BusinessException e) {
            modelAndView.addObject("error", e.getMessage());
            return modelAndView;
        } catch (Throwable t) {
            logger.error("update error ", t);
            modelAndView.addObject("error", "程序错误");
            return modelAndView;
        }

        modelAndView.addObject("message", "操作成功");
        return modelAndView;
    }

}