package project.web.admin.loan;

import kernel.exception.BusinessException;
import kernel.util.Arith;
import kernel.web.PageActionSupport;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.providers.encoding.PasswordEncoder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import project.Constants;
import project.log.LogService;
import project.mall.loan.AdminMallLoanConfigService;
import project.mall.loan.model.LoanConfig;
import project.user.googleauth.GoogleAuthService;
import security.SecUser;
import security.internal.SecUserService;
import util.DateUtil;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * 借贷配置
 */

@RestController
@RequestMapping("/mall/loan/config/")
public class AdminMallLoanConfigController extends PageActionSupport {

    private static Log logger = LogFactory.getLog(AdminMallLoanConfigController.class);

    @Resource
    AdminMallLoanConfigService adminMallLoanConfigService;


    @Resource
    private SecUserService secUserService;

    @Resource
    protected LogService logService;

    @Resource
    protected PasswordEncoder passwordEncoder;

    @Resource
    protected GoogleAuthService googleAuthService;

    /**
     * 跳转修改页面
     */
    @RequestMapping(value = "/toUpdate.action")
    public ModelAndView toUpdate(HttpServletRequest request) {

        ModelAndView model = new ModelAndView();

        LoanConfig loanConfig;
        model.setViewName("admin_loan_config_update");
        try {
            loanConfig = adminMallLoanConfigService.findLoanConfig();

            loanConfig.setRate(Arith.mul(loanConfig.getRate(),100));
            loanConfig.setDefaultRate(Arith.mul(loanConfig.getDefaultRate(),100));

            if(loanConfig == null) {
                throw new BusinessException("请初始化数据");
            }
        } catch (BusinessException e) {
            model.addObject("error", e.getMessage());
            return model;
        } catch (Throwable t) {
            logger.error(" error ", t);
            model.addObject("error", "[ERROR] " + t.getMessage());
            return model;
        }
        model.addObject("loanConfig",loanConfig);
        model.addObject("error",request.getParameter("error"));
        model.addObject("message",request.getParameter("message"));
        return model;
    }


    /**
     * 修改借贷配置
     *
     * @param
     * @return
     */
    @RequestMapping("updateLoanConfig.action")
    public ModelAndView update(HttpServletRequest request, LoanConfig model) {

        String pageNo = request.getParameter("pageNo");
        String[] day = request.getParameterValues("day");
        String days = StringUtils.join(day, ",");
        String login_safeword = request.getParameter("login_safeword");

        ModelAndView m = new ModelAndView();
        m.addObject("pageNo", pageNo);
        try {
            if (null == days){
                throw new BusinessException("贷款天数不可以空选");
            }
            LoanConfig loanConfig = adminMallLoanConfigService.findLoanConfig();

            SecUser sec = this.secUserService.findUserByLoginName(this.getUsername_login());
//            checkGoogleAuthCode(sec,google_auth_code);
            checkLoginSafeword(sec,this.getUsername_login(),login_safeword);

            model.setRate(Arith.div(model.getRate(),100));
            model.setDefaultRate(Arith.div(model.getDefaultRate(),100));
            model.setLendableDays(days);
            model.setAllLendableDays(loanConfig.getAllLendableDays());
            adminMallLoanConfigService.updateById(model);
            project.log.Log log = new project.log.Log();
            log.setCategory(Constants.LOG_CATEGORY_OPERATION);
            log.setUsername(sec.getUsername());
            log.setOperator(this.getUsername_login());

            log.setLog("管理员手动修改借贷配置 , ip:["+this.getIp(getRequest())+"]," + "修改时间[" + DateUtil.DatetoString(new Date(),"yyyy-MM-dd HH:mm:ss") + "]");
            logService.saveSync(log);
        } catch (BusinessException e) {
            m.addObject("error", e.getMessage());
            m.setViewName("redirect:/" + "mall/loan/config/toUpdate.action");
            return m;
        } catch (Exception e) {
            logger.error("error ", e);
            m.setViewName("redirect:/" + "mall/loan/config/toUpdate.action");
            return m;
        }
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("message", "操作成功");
        modelAndView.addObject("PageNo", pageNo);
        modelAndView.setViewName("redirect:/" + "mall/loan/config/toUpdate.action");
        return modelAndView;

    }


    /**
     * 验证登录人资金密码
     * @param operatorUsername
     * @param loginSafeword
     */
    protected void checkLoginSafeword(SecUser secUser, String operatorUsername, String loginSafeword) {
//		SecUser sec = this.secUserService.findUserByLoginName(operatorUsername);
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
