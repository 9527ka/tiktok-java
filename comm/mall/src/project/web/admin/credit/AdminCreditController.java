package project.web.admin.credit;

import kernel.exception.BusinessException;
import kernel.util.JsonUtils;
import kernel.util.StringUtils;
import kernel.util.ThreadUtils;
import kernel.web.PageActionSupport;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import project.Constants;
import project.log.LogService;
import project.mall.area.AdminMallCountryService;
import project.mall.credit.CreditService;
import project.mall.credit.model.Credit;
import project.mall.loan.model.LoanConfig;
import project.party.PartyService;
import project.party.model.Party;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

@RestController
@RequestMapping("/credit")
public class AdminCreditController extends PageActionSupport {
    private Logger logger = LogManager.getLogger(AdminCreditController.class);

    @Resource
    private CreditService creditService;
    @Resource
    private LogService logService;
    @Resource
    private PartyService partyService;

    @Resource
    private AdminMallCountryService adminMallCountryService;

    private Map<String, Object> session = new HashMap<String, Object>();

    private final static Object obj = new Object();
    /**
     * 管理后台查询贷款配置
     */
    @RequestMapping(value = "/config.action", produces = "text/html;charset=UTF-8")
    public ModelAndView config(HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView();
        LoanConfig loanConfig = null;
        try {
            loanConfig = creditService.queryLoanConfig();
        } catch (Throwable t) {
            logger.error(" error ", t);
            modelAndView.addObject("error", "[ERROR] " + t.getMessage());
            return modelAndView;
        }
        modelAndView.addObject("loanConfig", loanConfig);
        modelAndView.addObject("error", error);
        return modelAndView;
    }

    /**
     * 管理后台保存贷款配置
     */
    @RequestMapping(value = "/editconfig.action", produces = "text/html;charset=UTF-8")
    public ModelAndView editconfig(HttpServletRequest request) {
        String uuid = request.getParameter("uuid");
        String amountMin = request.getParameter("amountMin");
        String amountMax = request.getParameter("amountMax");
        String rate = request.getParameter("rate");
        String defaultRate = request.getParameter("defaultRate");
        String lendableDays = request.getParameter("lendableDays");
        ModelAndView modelAndView = new ModelAndView();
        try {
            LoanConfig loanConfig = LoanConfig.builder()
                    .amountMin(Double.valueOf(amountMin))
                    .amountMax(Double.valueOf(amountMax))
                    .rate(Double.valueOf(rate))
                    .defaultRate(Double.valueOf(defaultRate))
                    .lendableDays(lendableDays)
                    .build();
            creditService.updateLoanConfig(loanConfig);
        } catch (Throwable t) {
            logger.error(" error ", t);
            modelAndView.addObject("error", "参数错误");
            return modelAndView;
        }
        modelAndView.addObject("message", message);
        modelAndView.addObject("error", error);
        return modelAndView;
    }

    /**
     * 管理后台查询借贷记录
     */
    @RequestMapping(value = "/history.action", produces = "text/html;charset=UTF-8")
    public ModelAndView chatsList(HttpServletRequest request) {
        String error = request.getParameter("error");
        String message = request.getParameter("message");
        String userCode_para = request.getParameter("userCode");
        String userName_para = request.getParameter("userName");
        String identification_para = request.getParameter("identification");
        String status_para = request.getParameter("status");
        String customerSubmitTime_start_para = request.getParameter("customerSubmitTime_start");
        String customerSubmitTime_end_para = request.getParameter("customerSubmitTime_end");
        String pageNo = request.getParameter("pageNo");
        ModelAndView modelAndView = new ModelAndView("admin_credit_list");
        try {
            this.checkAndSetPageNo(pageNo);
            this.pageSize = 20;

            String session_token = UUID.randomUUID().toString();
            this.session.put("session_token", session_token);

            String partyId = this.getLoginPartyId();
            this.page = this.creditService.pagedQuery(this.pageNo, this.pageSize, userCode_para, userName_para, identification_para, status_para, customerSubmitTime_start_para,
                    customerSubmitTime_end_para, partyId);

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
        modelAndView.addObject("userCode", userCode_para);
        modelAndView.addObject("userName", userName_para);
        modelAndView.addObject("identification", identification_para);
        modelAndView.addObject("status", status_para);
        modelAndView.addObject("customerSubmitTime_start", customerSubmitTime_start_para);
        modelAndView.addObject("customerSubmitTime_end", customerSubmitTime_end_para);
        return modelAndView;
    }

    /**
     * 管理通过审核
     */
    @RequestMapping(value = "/pass.action", produces = "text/html;charset=UTF-8")
    public ModelAndView pass(HttpServletRequest request) {

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("redirect:/"  + "credit/history.action");

        String creditId = request.getParameter("creditId");
        String safeword = request.getParameter("safeword");
        String session_token = request.getParameter("session_token");

        try {

            if(StringUtils.isNullOrEmpty(creditId)){
                logger.error(" error ", "贷款单号不存在");
                throw new BusinessException("贷款单号不存在");
            }

            if (StringUtils.isEmptyString(safeword)) {
                logger.error(" error ", "资金密码不能为空");
                throw new BusinessException("资金密码不能为空");
            }

            if (safeword.length() < 6 || safeword.length() > 12) {
                logger.error(" error ", "资金密码必须6-12位");
                throw new BusinessException("资金密码必须6-12位");
            }

            Object object = this.session.get("session_token");
            this.session.remove("session_token");
            if (null == object || StringUtils.isNullOrEmpty(session_token) || !session_token.equals((String) object)) {
                throw new BusinessException("请稍后再试");
            }

            synchronized (object) {
                // 统一处理成功接口
                String patyid = this.creditService.updateCreditPass(creditId,safeword,this.getUsername_login());
                Party party = partyService.cachePartyBy(patyid, false);
                project.log.Log log = new project.log.Log();
                log.setCategory(Constants.LOG_CATEGORY_OPERATION);
                log.setPartyId(party.getId());
                log.setUsername(party.getUsername());
                log.setOperator(this.getUsername_login());
                log.setLog("借贷记录管理员手动通过审核,ip:["+this.getIp(getRequest())+"]" + "会员id["+ patyid + "]");
                logService.saveSync(log);
                ThreadUtils.sleep(300);
            }

         } catch (BusinessException e) {
            modelAndView.addObject("error", e.getMessage());
            return modelAndView;
        } catch (Throwable t) {
            logger.error(" error ", t);
            modelAndView.addObject("error", "[ERROR] " + t.getMessage());
            return modelAndView;
        }
        modelAndView.addObject("message", "操作成功");
        return modelAndView;
    }
    /**
     * 管理后台操作驳回或者手动还款
     */
    @RequestMapping(value = "/operate.action", produces = "text/html;charset=UTF-8")
    public ModelAndView operate(HttpServletRequest request) {
        String creditId = request.getParameter("creditId");
        String operateType = request.getParameter("operateType");
        String rejectReason = request.getParameter("rejectReason");
        String manualRepay = request.getParameter("manualRepay");
        String safeword = request.getParameter("safeword");
        String session_token = request.getParameter("session_token");

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("redirect:/"  + "credit/history.action");

        try {
            Object object = this.session.get("session_token");
            this.session.remove("session_token");
            if (null == object || StringUtils.isNullOrEmpty(session_token) || !session_token.equals((String) object)) {
                throw new BusinessException("请稍后再试");
            }

            synchronized (object) {
                // 统一处理成功接口
                this.creditService.updateCreditStatus(creditId, operateType, rejectReason,manualRepay,safeword, this.getUsername_login());
                ThreadUtils.sleep(300);
            }

        } catch (BusinessException e) {
            modelAndView.addObject("error", e.getMessage());
            return modelAndView;
        } catch (Throwable t) {
            logger.error(" error ", t);
            modelAndView.addObject("error", "[ERROR] " + t.getMessage());
            return modelAndView;
        }
        modelAndView.addObject("message", "操作成功");
        return modelAndView;
    }


    /**
     * 修改认证图片
     */
    @RequestMapping("updateCreditPic.action")
    public ModelAndView updateCreditPic(HttpServletRequest request) {

        String creditId = request.getParameter("creditId_updateCreditPic");
        String imgId = request.getParameter("img_id_updateCreditPic");
        String img = request.getParameter("img_updateCreditPic");

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("redirect:/"  + "credit/history.action");

        try {

            this.creditService.saveCreditPic(creditId, imgId, img);

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

    @RequestMapping("/findCode.action")
    public String findCode(HttpServletRequest request) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        String code = request.getParameter("code");
        String countryNameCn = adminMallCountryService.findCountryById(Long.valueOf(code)).getCountryNameCn();
        resultMap.put("countryNameCn",countryNameCn);
        resultMap.put("code",200);
        return JsonUtils.getJsonString(resultMap);
    }

    @RequestMapping("/findCreditById.action")
    public String findCreditById(HttpServletRequest request) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        String creditId = request.getParameter("creditId");
        Credit credits = creditService.findCreditsById(creditId);

        if (Objects.isNull(credits)){
            resultMap.put("code",500);
            return JsonUtils.getJsonString(resultMap);
        }

        Map<String, Object> calculate = creditService.calculate(new Date(), credits);
        resultMap.put("code",200);
        resultMap.putAll(calculate);
        return JsonUtils.getJsonString(resultMap);
    }
}
