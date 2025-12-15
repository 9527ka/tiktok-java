package project.web.admin.subscribe;

import com.alibaba.druid.support.logging.Log;
import com.alibaba.druid.support.logging.LogFactory;
import kernel.exception.BusinessException;
import kernel.util.StringUtils;
import kernel.web.PageActionSupport;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import project.mall.subscribe.AdminSubscribeService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 后台订阅
 */
@RestController
@RequestMapping("/mall/subscribe")
public class AdminSubscribeController extends PageActionSupport {

    private static Log logger = LogFactory.getLog(AdminSubscribeController.class);


    @Resource
    protected AdminSubscribeService adminSubscribeService;

    /**
     *
     * 列表查询
     */
    @RequestMapping(value = "/list.action")
    public ModelAndView list(HttpServletRequest request) {
        String pageNo = request.getParameter("pageNo");
        String message = request.getParameter("message");
        String error = request.getParameter("error");
        String email = request.getParameter("email");
        String startTime = request.getParameter("startTime");
        String endTime = request.getParameter("endTime");


        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("admin_subscribe_list");


        try {
            this.checkAndSetPageNo(pageNo);
            this.pageSize = 20;
            this.page = this.adminSubscribeService.pagedQuery(this.pageNo, this.pageSize,startTime,endTime,email);
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
        modelAndView.addObject("startTime", startTime);
        modelAndView.addObject("endTime", endTime);
        modelAndView.addObject("email", email);
        return modelAndView;
    }

    @RequestMapping(value =  "/delete.action")
    public ModelAndView delete(HttpServletRequest request) {
        ModelAndView model = new ModelAndView();

        try {
            String login_safeword = request.getParameter("login_safeword");
            if (StringUtils.isNullOrEmpty(login_safeword)) {
                model.addObject("error", "请输入登录人资金密码");
                model.setViewName("redirect:/" +  "invest/subscribe/list.action");
                return model;
            }

            String id = request.getParameter("baseId");
            this.adminSubscribeService.delete(id);

            model.addObject("message", "操作成功");
            model.setViewName("redirect:/" +  "mall/subscribe/list.action");
            return model;
        } catch (BusinessException e) {
            model.addObject("error", e.getMessage());
            model.setViewName("redirect:/" +  "mall/subscribe/list.action");
            return model;
        } catch (Throwable t) {
            logger.error("update error ", t);
            model.addObject("error", "程序错误");
            model.setViewName("redirect:/" +  "mall/subscribe/list.action");
            return model;
        }
    }

}
