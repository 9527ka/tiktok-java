package project.web.admin.goods;

import kernel.exception.BusinessException;
import kernel.util.StringUtils;
import kernel.web.PageActionSupport;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.providers.encoding.PasswordEncoder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import project.Constants;
import project.invest.goods.AdminGoodsBuyService;
import project.log.LogService;
import project.party.PartyService;
import security.SecUser;
import security.internal.SecUserService;
import util.DateUtil;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * 积分兑换记录
 */
@Slf4j
@RestController
@RequestMapping("/invest/goodsBuy")
public class AdminGoodsBuyController extends PageActionSupport {

    private static Log logger = LogFactory.getLog(AdminGoodsBuyController.class);

    @Resource
    protected PartyService partyService;
    @Resource
    protected LogService logService;
    @Resource
    protected SecUserService secUserService;
    @Resource
    protected PasswordEncoder passwordEncoder;

    @Resource
    private AdminGoodsBuyService adminGoodsBuyService;

    /**
     * 积分兑换记录列表
     * @param request
     * @return
     */
    @RequestMapping("/list.action")
    public ModelAndView list(HttpServletRequest request) {
        this.pageSize = 20;
        String error = request.getParameter("error");
        String id = request.getParameter("id");
        String userCode = request.getParameter("userCode");
        String phone = request.getParameter("phone");
        String userName = request.getParameter("userName");
        String startTime = request.getParameter("startTime");
        String endTime = request.getParameter("endTime");
        Integer status = request.getParameter("status") == null ? -2 : Integer.parseInt(request.getParameter("status"));
        String message = request.getParameter("message");
        ModelAndView model = new ModelAndView("goods_buy_list");

        model.addObject("pageNo",pageNo);
        model.addObject("id",id);
        model.addObject("userCode",userCode);
        model.addObject("phone",phone);
        model.addObject("userName",userName);
        model.addObject("startTime",startTime);
        model.addObject("endTime",endTime);
        model.addObject("message",message);
        model.addObject("status",status);
        model.addObject("error",error);
        try {
            this.checkAndSetPageNo(request.getParameter("pageNo"));
            this.page = adminGoodsBuyService.pagedQuery(this.pageNo, this.pageSize, id, userCode, userName, phone, status, startTime, endTime);
            model.addObject("page",this.page);
            return model;
        } catch (BusinessException e) {
            model.addObject("error", error);
            return model;
        } catch (Throwable t) {
            logger.error(" error ", t);
            model.addObject("error", "[ERROR] " + t.getMessage());
            return model;
        }
    }

    /**
     * 余额兑换记录列表
     * @param request
     * @return
     */
    @RequestMapping("/point/exchange/list.action")
    public ModelAndView exchangeList(HttpServletRequest request) {
        this.pageSize = 20;
        String error = request.getParameter("error");
        String id = request.getParameter("id");
        String userCode = request.getParameter("userCode");
        String phone = request.getParameter("phone");
        String userName = request.getParameter("userName");
        String startTime = request.getParameter("startTime");
        String endTime = request.getParameter("endTime");
//        Integer status = request.getParameter("status") == null ? -2 : Integer.parseInt(request.getParameter("status"));
        String message = request.getParameter("message");
        ModelAndView model = new ModelAndView("point_exchange_list");

        model.addObject("pageNo",pageNo);
        model.addObject("id",id);
        model.addObject("userCode",userCode);
        model.addObject("phone",phone);
        model.addObject("userName",userName);
        model.addObject("startTime",startTime);
        model.addObject("endTime",endTime);
        model.addObject("message",message);
//        model.addObject("status",status);
        model.addObject("error",error);
        try {
            this.checkAndSetPageNo(request.getParameter("pageNo"));
            this.page = adminGoodsBuyService.pagedQueryExchange(this.pageNo, this.pageSize, id, userCode, userName, phone,  startTime, endTime);
            model.addObject("page",this.page);
            return model;
        } catch (BusinessException e) {
            model.addObject("error", error);
            return model;
        } catch (Throwable t) {
            logger.error(" error ", t);
            model.addObject("error", "[ERROR] " + t.getMessage());
            return model;
        }
    }

    /**
     * 发货或取消
     */
    @RequestMapping(value = "/updateStatus.action")
    public ModelAndView updateStatus(HttpServletRequest request) {
        String pageNo = request.getParameter("pageNo");
        String message = request.getParameter("message");
        String error = request.getParameter("error");
        String id = request.getParameter("sid");
        String login_safeword = request.getParameter("login_safeword");
        String type = request.getParameter("type");
        String remark = request.getParameter("remark");

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("pageNo", pageNo);
        try {
            if(StringUtils.isEmptyString(id)){
                throw new BusinessException("系统错误");
            }
            SecUser sec = this.secUserService.findUserByLoginName(this.getUsername_login());
            checkLoginSafeword(sec,this.getUsername_login(),login_safeword);
            this.adminGoodsBuyService.updateStatus(id,type,remark);
            this.message = "操作成功";
            project.log.Log log = new project.log.Log();
            log.setCategory(Constants.LOG_CATEGORY_OPERATION);
            log.setUsername(sec.getUsername());
            log.setPartyId(sec.getPartyId());
            log.setOperator(this.getUsername_login());
            log.setLog("管理员手动兑换["+ id +"] 操作ip:["+this.getIp(getRequest())+"]" + " 操作时间 [" + DateUtil.DatetoString(new Date(),"yyyy-MM-dd HH:mm:ss")+ "]");
            logService.saveSync(log);
        } catch (BusinessException e) {
            modelAndView.addObject("error", e.getMessage());
            modelAndView.setViewName("redirect:/" +  "invest/goodsBuy/list.action");
            return modelAndView;
        } catch (Throwable t) {
            logger.error(" error ", t);
            modelAndView.addObject("error", "[ERROR] " + t.getMessage());
            modelAndView.setViewName("redirect:/" +  "invest/goodsBuy/list.action");
            return modelAndView;
        }
        modelAndView.addObject("message", message);
        modelAndView.addObject("error", error);
        modelAndView.setViewName("redirect:/" +  "invest/goodsBuy/list.action");
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
}