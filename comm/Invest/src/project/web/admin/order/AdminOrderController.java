package project.web.admin.order;

import com.alibaba.druid.support.logging.Log;
import com.alibaba.druid.support.logging.LogFactory;
import kernel.exception.BusinessException;
import kernel.util.Arith;
import kernel.util.StringUtils;
import kernel.web.PageActionSupport;
import org.springframework.security.providers.encoding.PasswordEncoder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import project.Constants;
import project.invest.order.AdminOrderService;
import project.invest.project.model.InvestOrders;
import project.log.LogService;
import project.user.googleauth.GoogleAuthService;
import security.SecUser;
import security.internal.SecUserService;
import util.DateUtil;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/adminOrder")
public class AdminOrderController extends PageActionSupport {

    private static Log logger = LogFactory.getLog(AdminOrderController.class);

    @Resource
    protected LogService logService;

    @Resource
    private AdminOrderService adminOrderService;

    @Resource
    private SecUserService secUserService;

    @Resource
    private PasswordEncoder passwordEncoder;

    @Resource
    private GoogleAuthService googleAuthService;


    /**
     *
     * 列表查询
     */
    @RequestMapping(value = "/list.action")
    public ModelAndView list(HttpServletRequest request) {
        String pageNo = request.getParameter("pageNo");
        String message = request.getParameter("message");
        String error = request.getParameter("error");
        String id = request.getParameter("id");
        String userCode = request.getParameter("userCode");
        String phone = request.getParameter("phone");
        String userName = request.getParameter("userName");
        String roleName = request.getParameter("roleName");
        String startTime = request.getParameter("startTime");
        String endTime = request.getParameter("endTime");

        Integer status = request.getParameter("status") == null ? -2 : Integer.parseInt(request.getParameter("status"));
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("admin_order_list");
        try {
            this.checkAndSetPageNo(pageNo);
            this.pageSize = 20;
            this.page = this.adminOrderService.pagedQuery(this.pageNo, this.pageSize,id,userCode,
                    userName,phone,roleName,startTime,endTime,status);
            List<Map> list = page.getElements();
            for (int i = 0; i < list.size(); i++) {
                Map map=list.get(i);
                Double bonusRate = (Double) map.get("bonusRate");
                Double bonusRateVip = (Double) map.get("bonusRateVip");
                map.put("sucessWill", DateUtil.DatetoString(new Date((long)map.get("sucessWill")),"yyyy-MM-dd HH:mm:ss") );
                map.put("bonusRate", Arith.mul(bonusRate,100));
                map.put("bonusRateVip", Arith.mul(bonusRateVip,100));
            }
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
        modelAndView.addObject("id", id);
        modelAndView.addObject("userCode", userCode);
        modelAndView.addObject("phone", phone);
        modelAndView.addObject("userName", userName);
        modelAndView.addObject("roleName", roleName);
        modelAndView.addObject("startTime", startTime);
        modelAndView.addObject("endTime", endTime);
        modelAndView.addObject("status", status);
        return modelAndView;
    }

    /**
     * 订单取消
     */
    @RequestMapping(value = "/cancel.action")
    public ModelAndView cancel(HttpServletRequest request) {
        String pageNo = request.getParameter("pageNo");
        String message = request.getParameter("message");
        String error = request.getParameter("error");
        String id = request.getParameter("sid");
        String login_safeword = request.getParameter("login_safeword");

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("pageNo", pageNo);
        try {
            if(StringUtils.isEmptyString(id)){
                throw new BusinessException("系统错误");
            }
            SecUser sec = this.secUserService.findUserByLoginName(this.getUsername_login());
            checkLoginSafeword(sec,this.getUsername_login(),login_safeword);
            this.adminOrderService.updateCancel(id);
            this.message = "订单取消成功";
            project.log.Log log = new project.log.Log();
            log.setCategory(Constants.LOG_CATEGORY_OPERATION);
            log.setUsername(sec.getUsername());
            log.setPartyId(sec.getPartyId());
            log.setOperator(this.getUsername_login());
            log.setLog("管理员手动取消任务订单["+ id +"] 操作ip:["+this.getIp(getRequest())+"]" + " 操作时间 [" + DateUtil.DatetoString(new Date(),"yyyy-MM-dd HH:mm:ss")+ "]");
            logService.saveSync(log);
        } catch (BusinessException e) {
            modelAndView.addObject("error", e.getMessage());
            modelAndView.setViewName("redirect:/" +  "adminOrder/list.action");
            return modelAndView;
        } catch (Throwable t) {
            logger.error(" error ", t);
            modelAndView.addObject("error", "[ERROR] " + t.getMessage());
            modelAndView.setViewName("redirect:/" +  "adminOrder/list.action");
            return modelAndView;
        }
        modelAndView.addObject("message", message);
        modelAndView.addObject("error", error);
        modelAndView.setViewName("redirect:/" +  "adminOrder/list.action");
        return modelAndView;
    }

    /**
     * 订单关闭
     */
    @RequestMapping(value = "/closure.action")
    public ModelAndView closure(HttpServletRequest request) {
        String pageNo = request.getParameter("pageNo");
        String error = request.getParameter("error");
        String id = request.getParameter("sid");
        String login_safeword = request.getParameter("login_safeword");

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("pageNo", pageNo);
        try {
            if(StringUtils.isEmptyString(id)){
                throw new BusinessException("系统错误");
            }
            SecUser sec = this.secUserService.findUserByLoginName(this.getUsername_login());
            checkLoginSafeword(sec,this.getUsername_login(),login_safeword);
            project.log.Log log = new project.log.Log();
            InvestOrders order = adminOrderService.findOrdersById(id);
            if(Objects.isNull(order)) {
                throw new BusinessException("订单不存在");
            }
            adminOrderService.updateClosure(order);
            this.message = "操作成功";
            log.setCategory(Constants.LOG_CATEGORY_OPERATION);
            log.setUsername(sec.getUsername());
            log.setPartyId(sec.getPartyId());
            log.setOperator(this.getUsername_login());
            log.setLog("管理员手动关闭订单["+ id +"] 操作ip:["+this.getIp(getRequest())+"]" + " 操作时间 [" + DateUtil.DatetoString(new Date(),"yyyy-MM-dd HH:mm:ss")+ "]");
            logService.saveSync(log);
        } catch (BusinessException e) {
            modelAndView.addObject("error", e.getMessage());
            modelAndView.setViewName("redirect:/" +  "adminOrder/list.action");
            return modelAndView;
        } catch (Throwable t) {
            logger.error(" error ", t);
            modelAndView.addObject("error", "[ERROR] " + t.getMessage());
            modelAndView.setViewName("redirect:/" +  "adminOrder/list.action");
            return modelAndView;
        }
        modelAndView.addObject("message", this.message);
        modelAndView.addObject("error", error);
        modelAndView.setViewName("redirect:/" +  "adminOrder/list.action");
        return modelAndView;
    }

    /**
     * 验证登录人资金密码
     * @param operatorUsername
     * @param loginSafeword
     */
    protected void checkLoginSafeword(SecUser secUser, String operatorUsername, String loginSafeword) {
        String sysSafeword = secUser.getSafeword();
        String safeword_md5 = passwordEncoder.encodePassword(loginSafeword, operatorUsername);
        if (!safeword_md5.equals(sysSafeword)) {
            throw new BusinessException("登录人资金密码错误");
        }
    }
    /**
     * 验证谷歌验证码
     * @param code
     */
    protected void checkGoogleAuthCode(SecUser secUser,String code) {
        if(!secUser.isGoogle_auth_bind()) {
            throw new BusinessException("请先绑定谷歌验证器");
        }
        boolean checkCode = googleAuthService.checkCode(secUser.getGoogle_auth_secret(), code);
        if(!checkCode) {
            throw new BusinessException("谷歌验证码错误");
        }
    }


}