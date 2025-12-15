package project.web.api;

import kernel.exception.BusinessException;
import kernel.util.Arith;
import kernel.util.JsonUtils;
import kernel.util.PageInfo;
import kernel.util.StringUtils;
import kernel.web.Page;
import kernel.web.PageActionSupport;
import kernel.web.ResultObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import project.Constants;
import project.log.LogService;
import project.mall.credit.CreditService;
import project.mall.credit.model.Credit;
import project.mall.loan.model.LoanConfig;
import project.party.PartyService;
import project.party.model.Party;
import project.tip.TipConstants;
import project.tip.TipService;
import project.user.token.TokenService;
import project.wallet.Wallet;
import project.wallet.WalletService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@CrossOrigin
public class CreditController extends PageActionSupport {

    private static Log logger = LogFactory.getLog(CreditController.class);

    @Resource
    private TokenService tokenService;
    @Resource
    private CreditService creditService;
    @Resource
    private WalletService walletService;
    @Resource
    private PartyService partyService;
    @Resource
    private LogService logService;

    @Resource
    private TipService tipService;


    private final String action = "/api/credit!";

    /**
     * 查询是否可以贷款，返回false 申请贷款不可用，true 申请贷款可用
     */
    @RequestMapping(value = action + "check.action", produces = "text/html;charset=UTF-8")
    public Object check(HttpServletRequest request) {
        ResultObject resultObject = readSecurityContextFromSession(new ResultObject());
        if (!resultObject.getCode().equals("0")) {
            return JsonUtils.getJsonString(resultObject);
        }
        String partyId = tokenService.cacheGet(request.getParameter("token"));
        resultObject.setData("true");
        try {
            List<Credit> credits = creditService.getOnUseCreditById(partyId);
            if (credits.size() > 0) {//有正在使用的贷款，不展示申请贷款页面
                resultObject.setData("false");
            }
            Wallet wallet = walletService.saveWalletByPartyId(partyId);
            if (wallet.getFrozenState() == 1){//资金冻结后不让借贷
                resultObject.setData("false");
            }
        } catch (Exception e) {
            resultObject.setCode("1");
            resultObject.setMsg("程序错误");
            logger.error("error:", e);
        }
        return JsonUtils.getJsonString(resultObject);
    }

    @RequestMapping(value = action + "config.action", produces = "text/html;charset=UTF-8")
    public Object config(HttpServletRequest request) {
        ResultObject resultObject = readSecurityContextFromSession(new ResultObject());
        if (!resultObject.getCode().equals("0")) {
            return resultObject;
        }
        try {
            LoanConfig loanConfig = creditService.queryLoanConfig();
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("rate",new BigDecimal(String.valueOf(loanConfig.getRate())).setScale(4,BigDecimal.ROUND_DOWN));
            resultMap.put("defaultRate",new BigDecimal(String.valueOf(loanConfig.getDefaultRate())).setScale(4,BigDecimal.ROUND_DOWN));
            resultMap.put("lendableDays",Arrays.asList(loanConfig.getLendableDays().split(",")));
            resultMap.put("allLendableDays",Arrays.asList(loanConfig.getAllLendableDays().split(",")));
            resultMap.put("amountMin",new BigDecimal(String.valueOf(loanConfig.getAmountMin())).setScale(2,BigDecimal.ROUND_DOWN));
            resultMap.put("amountMax",new BigDecimal(String.valueOf(loanConfig.getAmountMax())).setScale(2,BigDecimal.ROUND_DOWN));
            resultObject.setData(resultMap);
        } catch (Exception e) {
            resultObject.setCode("1");
            resultObject.setMsg("程序错误");
            logger.error("error:", e);
        }
        return JsonUtils.getJsonString(resultObject);
    }

    /**
     * 在线申请贷款或者确认重新申请
     */
    @RequestMapping(value = action + "apply.action", produces = "text/html;charset=UTF-8")
    public Object apply(HttpServletRequest request) {
        ResultObject resultObject = readSecurityContextFromSession(new ResultObject());
        if (!resultObject.getCode().equals("0")) {
            return resultObject;
        }
        String partyId = tokenService.cacheGet(request.getParameter("token"));
        String realName = request.getParameter("realName");
        String creditPeriod = request.getParameter("creditPeriod");
        String applyAmount = request.getParameter("applyAmount");
        String countryId = request.getParameter("countryId");
        String identification = request.getParameter("identification");
        String imgCertificateFace = request.getParameter("imgCertificateFace");
        String imgCertificateBack = request.getParameter("imgCertificateBack");
        String imgCertificateHand = request.getParameter("imgCertificateHand");
        String creditid = request.getParameter("creditId");
        LoanConfig loanConfig = creditService.queryLoanConfig();
        Integer applyAmount_para = 0;
        try {
            applyAmount_para = Integer.valueOf(applyAmount);
        } catch (NumberFormatException e) {
            resultObject.setCode("1");
            resultObject.setMsg("参数异常");
            return JsonUtils.getJsonString(resultObject);
        }
        if(!(applyAmount_para >= loanConfig.getAmountMin() && applyAmount_para <= loanConfig.getAmountMax())){
            resultObject.setCode("1");
            resultObject.setMsg("申请的贷款金额，不在允许的范围内");
            return JsonUtils.getJsonString(resultObject);
        }
        try {
            double totalInterest = Arith.roundDown(Arith.mul(loanConfig.getRate(), Arith.mul(Double.valueOf(applyAmount), Double.valueOf(creditPeriod))), 2);
            double totalRepayment = Arith.roundDown(Arith.add(totalInterest, Double.valueOf(applyAmount)), 2);
            Credit credit = Credit.builder().partyId(partyId)
                    .realName(realName)
                    .creditPeriod(Integer.valueOf(creditPeriod))
                    .applyAmount(Double.valueOf(applyAmount))
                    .creditRate(loanConfig.getRate())
                    .defaultRate(loanConfig.getDefaultRate())
                    .countryId(Integer.valueOf(countryId))
                    .identification(identification)
                    .imgCertificateFace(imgCertificateFace)
                    .imgCertificateBack(imgCertificateBack)
                    .imgCertificateHand(imgCertificateHand)
                    .totalInterest(totalInterest)
                    .totalRepayment(totalRepayment)
                    .customerSubmitTime(new Date())
                    .status(1).build();//待审核状态
            if (StringUtils.isNotEmpty(creditid)) {
                credit.setId(creditid);
            }
            creditService.saveCreate(credit);

            Party party = this.partyService.cachePartyBy(partyId, false);
            if (Constants.SECURITY_ROLE_MEMBER.equals(party.getRolename())) {
                this.tipService.saveTip(credit.getId().toString(), TipConstants.CREDIT);
            }
        } catch (BusinessException e) {
            resultObject.setCode("1");
            resultObject.setMsg(e.getMessage());
            logger.error("error:", e);
        } catch (Exception e) {
            resultObject.setCode("1");
            resultObject.setMsg("程序异常");
            logger.error("error:", e);
        }
        return JsonUtils.getJsonString(resultObject);
    }

    /**
     * 正在计费的贷款
     */
    @RequestMapping(value = action + "bill.action", produces = "text/html;charset=UTF-8")
    public Object bill(HttpServletRequest request) {
        ResultObject resultObject = this.readSecurityContextFromSession(new ResultObject());
        if (!resultObject.getCode().equals("0")) {
            return JsonUtils.getJsonString(resultObject);
        }
        String partyId = tokenService.cacheGet(request.getParameter("token"));
        try {
            long start = System.currentTimeMillis();
            logger.info("**********设置贷款单逾期开始**********");
            Integer c = creditService.updateOverDue();
            logger.info("设置贷款单逾期数:" + c + ",花费时间:" + (System.currentTimeMillis() - start) + "ms");
            List<Credit> credits = creditService.queryBillCredit(partyId);
            Map<String, Object> map = new HashMap<String, Object>();
//            map.put("accountAmount",wallet.getMoney());
            if (Objects.isNull(credits) || credits.size() == 0) {
                map.put("applyAmount", 0);
                map.put("alreadyCreditDays", 0);
                map.put("interest", 0);
                map.put("estimatePayment", 0);
                resultObject.setData(map);
                return JsonUtils.getJsonString(resultObject);
            }
            Credit credit = credits.get(0);
            map.put("realName", credit.getRealName());
            map.put("applyAmount", new BigDecimal(String.valueOf(credit.getApplyAmount())).setScale(2, BigDecimal.ROUND_DOWN));
            Date date = new Date();
//            计算贷款的方法
            Map<String, Object> calculate = this.creditService.calculate(date, credit);
            map.put("interest", calculate.get("interest"));
            map.put("alreadyCreditDays", calculate.get("alreadyCreditDays"));
            map.put("estimatePayment", calculate.get("estimatePayment"));
            resultObject.setData(map);
        } catch (BusinessException e) {
            resultObject.setCode("1");
            resultObject.setMsg(e.getMessage());
            logger.error("error:", e);
        } catch (Exception e) {
            resultObject.setCode("1");
            resultObject.setMsg("程序异常");
            logger.error("error:", e);
        }
        return JsonUtils.getJsonString(resultObject);
    }

    /**
     * 贷款记录
     */
    @RequestMapping(value = action + "histroy.action")
    public Object histroy(HttpServletRequest request) {
        ResultObject resultObject = this.readSecurityContextFromSession(new ResultObject());
        if (!resultObject.getCode().equals("0")) {
            return resultObject;
        }
        String partyId = tokenService.cacheGet(request.getParameter("token"));
        PageInfo pageInfo = getPageInfo(request);
        try {
            long start = System.currentTimeMillis();
            logger.info("**********设置贷款逾期开始**********");
            Integer c = creditService.updateOverDue();
            logger.info("设置贷款逾期数:" + c + ",花费时间:" + (System.currentTimeMillis() - start) + "ms");
            Page page = creditService.pagedQuery(pageInfo.getPageNum(), pageInfo.getPageSize(), partyId);
            resultObject.setData(page);
        } catch (BusinessException e) {
            resultObject.setCode("1");
            resultObject.setMsg(e.getMessage());
            logger.error("error:", e);
        } catch (Exception e) {
            resultObject.setCode("1");
            resultObject.setMsg("程序异常");
            logger.error("error:", e);
        }
        return resultObject;
    }

    /**
     * 支付前展示
     */
    @RequestMapping(value = action + "beforepay.action")
    public Object beforePay(HttpServletRequest request) {
        ResultObject resultObject = readSecurityContextFromSession(new ResultObject());
        if (!resultObject.getCode().equals("0")) {
            return JsonUtils.getJsonString(resultObject);
        }
        String partyId = tokenService.cacheGet(request.getParameter("token"));
        Wallet wallet = this.walletService.saveWalletByPartyId(partyId);
        String creditId = request.getParameter("creditId");
        Credit credit = creditService.findCreditsById(creditId);
        if (Objects.isNull(credit)){
            resultObject.setCode("1");
            new BusinessException("该贷款单号不存在");
            return resultObject;

        }
        Map<String, Object> calculate = null;
        try {
            calculate = creditService.calculate(new Date(), credit);
        } catch (BusinessException e) {
            resultObject.setCode("1");
            resultObject.setMsg(e.getMessage());
            logger.error("error:", e);
            return resultObject;
        } catch (Exception e) {
            resultObject.setCode("1");
            resultObject.setMsg("程序异常");
            logger.error("error:", e);
            return resultObject;
        }
        Map<String, Object> result = new HashMap<>();
        result.put("realName", credit.getRealName());
        result.put("alreadyCreditDays", calculate.get("alreadyCreditDays"));
        result.put("estimatePayment", calculate.get("estimatePayment"));
        result.put("creditRate", credit.getCreditRate());
        result.put("accountAmount", new BigDecimal(wallet.getMoney()).setScale(2, BigDecimal.ROUND_DOWN));
        resultObject.setData(result);
        return resultObject;
    }

    /**
     * 确认还款,支付
     */
    @RequestMapping(value = action + "pay.action", produces = "text/html;charset=UTF-8")
    public Object pay(HttpServletRequest request) {
        ResultObject resultObject = readSecurityContextFromSession(new ResultObject());
        if (!resultObject.getCode().equals("0")) {
            return JsonUtils.getJsonString(resultObject);
        }
        String creditId = request.getParameter("creditId");
        String partyId = tokenService.cacheGet(request.getParameter("token"));
        String safeword = request.getParameter("safeword");
        if (StringUtils.isNullOrEmpty(creditId)) {
            resultObject.setCode("1");
            resultObject.setMsg("该贷款单号不存在");
            return JsonUtils.getJsonString(resultObject);
        }
        if (StringUtils.isEmptyString(safeword)) {
            resultObject.setCode("1");
            resultObject.setMsg("资金密码不能为空");
            return JsonUtils.getJsonString(resultObject);
        }

        if (safeword.length() < 6 || safeword.length() > 12) {
            resultObject.setCode("1");
            resultObject.setMsg("资金密码必须6-12位");
            return JsonUtils.getJsonString(resultObject);
        }
        try {
            long start = System.currentTimeMillis();
            logger.info("**********设置贷款单逾期开始**********");
            Integer c = creditService.updateOverDue();
            logger.info("设置贷款单逾期数:" + c + ",花费时间:" + (System.currentTimeMillis() - start) + "ms");
            creditService.updateCreditOrder(partyId, creditId, safeword);

            Party party = partyService.cachePartyBy(partyId, false);
            project.log.Log log = new project.log.Log();
            log.setCategory(Constants.LOG_CATEGORY_OPERATION);
            log.setPartyId(party.getId());
            log.setUsername(party.getUsername());
            log.setOperator(this.getUsername_login());
            log.setLog("用户偿还贷款,ip:[" + this.getIp(getRequest()) + "]");
            logService.saveSync(log);
        } catch (BusinessException e) {
            resultObject.setCode("1");
            resultObject.setMsg(e.getMessage());
            logger.error("error:", e);
        } catch (Exception e) {
            resultObject.setCode("1");
            resultObject.setMsg("程序异常");
            logger.error("error:", e);
        }
        return JsonUtils.getJsonString(resultObject);
    }


    /**
     * 重新申请
     */
    @RequestMapping(value = action + "beforereapply.action", produces = "text/html;charset=UTF-8")
    public Object reapply(HttpServletRequest request) {
        ResultObject resultObject = readSecurityContextFromSession(new ResultObject());
        if (!resultObject.getCode().equals("0")) {
            return JsonUtils.getJsonString(resultObject);
        }
        String creditId = request.getParameter("creditId");
        try {
            Credit credit = creditService.findCreditsById(creditId);
            resultObject.setData(credit);
        } catch (BusinessException e) {
            resultObject.setCode("1");
            resultObject.setMsg(e.getMessage());
            logger.error("error:", e);
        } catch (Exception e) {
            resultObject.setCode("1");
            resultObject.setMsg("程序异常");
            logger.error("error:", e);
        }
        return JsonUtils.getJsonString(resultObject);
    }

    public ResultObject readSecurityContextFromSession(ResultObject resultObject) {
        HttpServletRequest request = this.getRequest();
        String token = request.getParameter("token");
        if (StringUtils.isNullOrEmpty(token)) {
            resultObject.setCode("403");
            resultObject.setMsg("请重新登录");
            return resultObject;
        }
        String partyId = tokenService.cacheGet(token);
        if (StringUtils.isNullOrEmpty(partyId)) {
            resultObject.setCode("403");
            resultObject.setMsg("请重新登录");
            return resultObject;
        }
        return resultObject;
    }


}
