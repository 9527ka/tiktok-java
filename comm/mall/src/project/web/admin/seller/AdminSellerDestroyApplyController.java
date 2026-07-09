package project.web.admin.seller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import kernel.exception.BusinessException;
import kernel.util.StringUtils;
import kernel.util.ThreadUtils;
import kernel.web.PageActionSupport;
import project.mall.seller.AdminSellerDestroyApplyService;
import project.mall.utils.CsrfTokenUtil;

/**
 * 商家注销申请审核(后台)
 */
@Slf4j
@RestController
public class AdminSellerDestroyApplyController extends PageActionSupport {

    @Autowired
    private AdminSellerDestroyApplyService adminSellerDestroyApplyService;

    @Autowired
    private HttpSession httpSession;

    private final static Object obj = new Object();

    private final String action = "normal/adminSellerDestroyApplyAction!";

    /**
     * 注销申请列表
     */
    @RequestMapping(action + "list.action")
    public ModelAndView list(HttpServletRequest request) {
        String pageNo = request.getParameter("pageNo");
        String message = request.getParameter("message");
        String error = request.getParameter("error");
        String name_para = request.getParameter("name_para");
        String status_para = request.getParameter("status_para");
        String start_time = request.getParameter("start_time");
        String end_time = request.getParameter("end_time");

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("admin_seller_destroy_apply_list");

        try {
            this.checkAndSetPageNo(pageNo);
            this.pageSize = 20;

            String session_token = CsrfTokenUtil.generateToken();
            CsrfTokenUtil.saveTokenInSession(httpSession, session_token);

            Integer status_para_int = null;
            if (!StringUtils.isEmptyString(status_para)) {
                status_para_int = Integer.valueOf(status_para).intValue();
            }

            this.page = this.adminSellerDestroyApplyService.pagedQuery(this.pageNo, this.pageSize, name_para,
                    status_para_int, start_time, end_time);

            modelAndView.addObject("session_token", session_token);
        } catch (BusinessException e) {
            modelAndView.addObject("error", e.getMessage());
            return modelAndView;
        } catch (Throwable t) {
            log.error(" error ", t);
            modelAndView.addObject("error", "[ERROR] " + t.getMessage());
            return modelAndView;
        }

        modelAndView.addObject("pageNo", this.pageNo);
        modelAndView.addObject("pageSize", this.pageSize);
        modelAndView.addObject("page", this.page);
        modelAndView.addObject("message", message);
        modelAndView.addObject("error", error);
        modelAndView.addObject("name_para", name_para);
        modelAndView.addObject("status_para", status_para);
        modelAndView.addObject("start_time", start_time);
        modelAndView.addObject("end_time", end_time);
        return modelAndView;
    }

    /**
     * 审核通过(真正注销账号)
     */
    @RequestMapping(action + "approve.action")
    public ModelAndView approve(HttpServletRequest request) {
        String session_token = request.getParameter("session_token");
        String id = request.getParameter("id");
        String status_para = request.getParameter("status_para");
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("redirect:/" + action + "list.action");
        modelAndView.addObject("status_para", status_para);

        try {
            String sessionToken = (String) httpSession.getAttribute("session_token");
            CsrfTokenUtil.removeTokenFromSession(httpSession);
            if (!CsrfTokenUtil.isTokenValid(sessionToken, session_token)) {
                throw new BusinessException("操作成功，请勿重复点击");
            }
            synchronized (obj) {
                this.adminSellerDestroyApplyService.saveApprove(id, this.getUsername_login());
                ThreadUtils.sleep(300);
            }
        } catch (BusinessException e) {
            modelAndView.addObject("error", e.getMessage());
            return modelAndView;
        } catch (Throwable t) {
            log.error("approve error ", t);
            modelAndView.addObject("error", "程序错误");
            return modelAndView;
        }
        modelAndView.addObject("message", "操作成功");
        return modelAndView;
    }

    /**
     * 审核拒绝
     */
    @RequestMapping(action + "reject.action")
    public ModelAndView reject(HttpServletRequest request) {
        String session_token = request.getParameter("session_token");
        String id = request.getParameter("id");
        String review_remark = request.getParameter("review_remark");
        String status_para = request.getParameter("status_para");
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("redirect:/" + action + "list.action");
        modelAndView.addObject("status_para", status_para);

        try {
            String sessionToken = (String) httpSession.getAttribute("session_token");
            CsrfTokenUtil.removeTokenFromSession(httpSession);
            if (!CsrfTokenUtil.isTokenValid(sessionToken, session_token)) {
                throw new BusinessException("操作成功，请勿重复点击");
            }
            synchronized (obj) {
                this.adminSellerDestroyApplyService.saveReject(id, review_remark, this.getUsername_login());
                ThreadUtils.sleep(300);
            }
        } catch (BusinessException e) {
            modelAndView.addObject("error", e.getMessage());
            return modelAndView;
        } catch (Throwable t) {
            log.error("reject error ", t);
            modelAndView.addObject("error", "程序错误");
            return modelAndView;
        }
        modelAndView.addObject("message", "操作成功");
        return modelAndView;
    }
}
