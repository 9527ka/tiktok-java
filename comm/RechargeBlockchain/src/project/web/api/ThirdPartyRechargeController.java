package project.web.api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import ext.TypeConv;
import ext.jdk8.Maps;
import kernel.exception.BusinessException;
import kernel.sessiontoken.SessionTokenService;
import kernel.util.Arith;
import kernel.util.ObjectTools;
import kernel.util.StringUtils;
import kernel.web.BaseAction;
import kernel.web.ResultObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;
import project.blockchain.ThirdPartyRechargeService;
import project.blockchain.event.message.RechargeSuccessEvent;
import project.blockchain.event.model.RechargeInfo;
import project.blockchain.manager.ThirdPartyManager;
import project.blockchain.vo.GCash2NotifyVo;
import project.blockchain.vo.GCash3NotifyVo;
import project.blockchain.vo.GCashPayNotifyVo;
import project.blockchain.vo.ThirdPartyCommonVo;
import project.blockchain.vo.ThirdPartyPayRespVo;
import project.blockchain.vo.ThirdPartyProductVo;
import project.blockchain.vo.ThirdPartyRangeVo;
import project.pay.PayCore;
import project.syspara.Syspara;
import project.syspara.SysparaService;
import project.wallet.rate.ExchangeRate;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@CrossOrigin
public class ThirdPartyRechargeController extends BaseAction {
    private static final Logger LOGGER = LoggerFactory.getLogger(ThirdPartyRechargeController.class);

    private static final String ACTION = "/api/thirdPartyRecharge!";

    @Autowired
    private SessionTokenService sessionTokenService;

    @Autowired
    private ThirdPartyManager thirdPartyManager;

    @Autowired
    private ThirdPartyRechargeService thirdPartyRechargeService;
    @Autowired
    private SysparaService sysparaService;


    /**
     * 第三方银行卡充值申请
     * frenchCurrency 法币代码
     * amount 金额
     */
    @RequestMapping(ACTION + "recharge.action")
    public Object thirdPartyRecharge(HttpServletRequest request) throws IOException {
        String session_token = request.getParameter("session_token");
        String amount = request.getParameter("amount");
        String frenchCurrency = request.getParameter("frenchCurrency");

        ResultObject resultObject = new ResultObject();
        resultObject = this.readSecurityContextFromSession(resultObject);
        if (!"0".equals(resultObject.getCode())) {
            return resultObject;
        }
        try {
            String error = this.verif(amount, frenchCurrency);
            if (!StringUtils.isNullOrEmpty(error)) {
                throw new BusinessException(error);
            }
            Object object = this.sessionTokenService.cacheGet(session_token);
            this.sessionTokenService.delete(session_token);
            if (null == object || !this.getLoginPartyId().equals(object)) {
                throw new BusinessException("请稍后再试");
            }

            String payUrl = thirdPartyRechargeService.saveApplyRecharge(getLoginPartyId(), amount, frenchCurrency);
            resultObject.setData(payUrl);
        } catch (BusinessException e) {
            resultObject.setCode("1");
            resultObject.setMsg(e.getMessage());
        } catch (Throwable t) {
            resultObject.setCode("1");
            resultObject.setMsg("程序错误");
            logger.error("error:", t);
        }

        return resultObject;
    }

    private String verif(String amount, String frenchCurrency) {

        if (StringUtils.isNullOrEmpty(amount)) {
            return "充值数量必填";
        }
        if (!StringUtils.isDouble(amount)) {
            return "充值数量输入错误，请输入浮点数";
        }
        double amountNum = Double.parseDouble(amount);
        if (amountNum <= 0) {
            return "充值数量不能小于等于0";
        }
        if (StringUtils.isEmptyString(frenchCurrency)) {
            return "请选择法币类型";
        }
        ThirdPartyProductVo productVo = queryThirdPartyList();
        List<ThirdPartyRangeVo> thirdPartyRangeVos = productVo.getRange();
        boolean flag = true;
        for (ThirdPartyRangeVo thirdPartyRangeVo : thirdPartyRangeVos) {
            String bankCode = thirdPartyRangeVo.getBank_code();
            if (frenchCurrency.equals(bankCode)) {
                Double max_amount = thirdPartyRangeVo.getMax_amount();
                if (amountNum > max_amount) {
                    return "充值金额超过最大金额";
                }
                Double min_amount = thirdPartyRangeVo.getMin_amount();
                if (amountNum < min_amount) {
                    return "充值金额小于最小金额";
                }
                flag = false;
            }
        }
        if (flag) {
            return "请选择正确的法币类型";
        }
        return null;
    }

    private ThirdPartyProductVo queryThirdPartyList() {
        Map<String, Object> params = new HashMap<>();
        params.put("product", "TRC20Buy");
        ThirdPartyCommonVo resp = thirdPartyManager.sendPost(params, "/buy/coin/amount/range", Instant.now().getEpochSecond());
        ThirdPartyProductVo productVo = new ThirdPartyProductVo();
        productVo.setProductType("Bank");
        productVo.setRange(JSONObject.parseArray(resp.getParams(), ThirdPartyRangeVo.class));
        return productVo;
    }

    /**
     * 异步通知
     */
    @PostMapping(ACTION + "onSucceeded.action")
    public String onsucceeded(ThirdPartyCommonVo request) {
        int timestamp = request.getTimestamp();
        String params = request.getParams();
        String sign = request.getSign();
        String mySign = thirdPartyManager.sign(params, timestamp);
        if (!mySign.equals(sign)) {
            LOGGER.error("第三方异步通知签名验证不通过");
            return "error sign";
        }
        RechargeInfo rechargeInfo = null;
        try {
            synchronized (this) {
                ThirdPartyPayRespVo payRespVo = JSONObject.parseObject(params, ThirdPartyPayRespVo.class);
                rechargeInfo = this.thirdPartyRechargeService.saveSucceeded(payRespVo);
            }
        } catch (BusinessException e) {
            logger.error("第三方回调失败：", e);
            return "error";
        } catch (Throwable t) {
            logger.error("update error ", t);
            return "error";
        }
        if (Objects.nonNull(rechargeInfo)) {
            WebApplicationContext wac = ContextLoader.getCurrentWebApplicationContext();
            wac.publishEvent(new RechargeSuccessEvent(this, rechargeInfo));
        }

        return "SUCCESS";
    }

    /**
     * 查询币种与限额
     *
     * @return
     */
    @RequestMapping(ACTION + "getCoinList.action")
    public Object getCoinList() {
        ResultObject resultObject = new ResultObject();
        resultObject = this.readSecurityContextFromSession(resultObject);
        if (!"0".equals(resultObject.getCode())) {
            return resultObject;
        }
        List<ThirdPartyProductVo> thirdPartyProductVos = new ArrayList<>();

        Syspara bankSwitch = sysparaService.find("third_party_bank_switch");
        if (bankSwitch != null && Boolean.parseBoolean(bankSwitch.getValue())) {
            try {
                ThirdPartyProductVo Bank = queryThirdPartyList();
                thirdPartyProductVos.add(Bank);
            } catch (Exception e) {
                logger.error("查询第三方bank限额异常", e);
            }
        }

        Syspara gcashPaySwitch = sysparaService.find("third_party_gcashPay_switch");
        if (gcashPaySwitch != null && Boolean.parseBoolean(gcashPaySwitch.getValue())) {
            try {
                ThirdPartyProductVo GCashPay = thirdPartyManager.getCoin5();
                thirdPartyProductVos.add(GCashPay);
            } catch (Exception e) {
                logger.error("查询第三方gcashPay限额异常", e);
            }
        }
        try {
            Syspara gcashSwitch = sysparaService.find("third_party_gcash_switch");
            if (gcashSwitch != null && Boolean.parseBoolean(gcashSwitch.getValue())) {
                try {
                    ThirdPartyProductVo GCash = thirdPartyManager.getCoin();
                    thirdPartyProductVos.add(GCash);
                } catch (Exception e) {
                    logger.error("查询第三方gcash限额异常", e);
                }
            }
            Syspara gcash2Switch = sysparaService.find("third_party_gcash2_switch");
            if (gcash2Switch != null && Boolean.parseBoolean(gcash2Switch.getValue())) {
                try {
                    ThirdPartyProductVo GCash2 = thirdPartyManager.getCoin2();
                    thirdPartyProductVos.add(GCash2);
                } catch (Exception e) {
                    logger.error("查询第三方gcash2限额异常", e);
                }
            }
            Syspara gcash3Switch = sysparaService.find("third_party_gcash3_switch");
            if (gcash3Switch != null && Boolean.parseBoolean(gcash3Switch.getValue())) {
                try {
                    ThirdPartyProductVo GCash3 = thirdPartyManager.getCoin3();
                    thirdPartyProductVos.add(GCash3);
                } catch (Exception e) {
                    logger.error("查询第三方gcash3限额异常", e);
                }
            }
            Syspara mayaSwitch = sysparaService.find("third_party_maya_switch");
            if (mayaSwitch !=null &&  Boolean.parseBoolean(mayaSwitch.getValue())) {
                try {
                    ThirdPartyProductVo Maya = thirdPartyManager.getCoin4();
                    thirdPartyProductVos.add(Maya);
                } catch (Exception e) {
                    logger.error("查询第三方maya限额异常", e);
                }
            }

            resultObject.setData(thirdPartyProductVos);
        } catch (BusinessException e) {
            resultObject.setCode("1");
            resultObject.setMsg(e.getMessage());
        } catch (Exception t) {
            resultObject.setCode("1");
            resultObject.setMsg("程序错误");
            logger.error("error:", t);
        }
        return resultObject;
    }

    /**
     * 第三方PHP充值申请
     * frenchCurrency 法币代码
     * amount 金额
     */
    @RequestMapping(ACTION + "PHP_recharge.action")
    public Object thirdPartyPHPRecharge(HttpServletRequest request) throws IOException {
        String session_token = request.getParameter("session_token");
        String amount = request.getParameter("amount");
//        String frenchCurrency = request.getParameter("frenchCurrency");

        ResultObject resultObject = new ResultObject();
        resultObject = this.readSecurityContextFromSession(resultObject);
        if (!"0".equals(resultObject.getCode())) {
            return resultObject;
        }
        try {
            String error = this.verif(amount);
            if (!StringUtils.isNullOrEmpty(error)) {
                throw new BusinessException(error);
            }
            Object object = this.sessionTokenService.cacheGet(session_token);
            this.sessionTokenService.delete(session_token);
            if (null == object || !this.getLoginPartyId().equals(object)) {
                throw new BusinessException("请稍后再试");
            }

            String payUrl = thirdPartyRechargeService.saveApplyRecharge(getLoginPartyId(), amount);
            resultObject.setData(payUrl);
        } catch (BusinessException e) {
            resultObject.setCode("1");
            resultObject.setMsg(e.getMessage());
        } catch (Throwable t) {
            resultObject.setCode("1");
            resultObject.setMsg("程序错误");
            logger.error("error:", t);
        }

        return resultObject;
    }

    /**
     * 第三方PHP充值申请2.0
     * pageUrl 支付成功回跳页面
     * amount 金额
     */
    @RequestMapping(ACTION + "PHP_recharge2.action")
    public Object thirdPartyPHPRecharge2(HttpServletRequest request) throws IOException {
        String session_token = request.getParameter("session_token");
        String amount = request.getParameter("amount");
        String pageUrl = request.getParameter("pageUrl");
//        String frenchCurrency = request.getParameter("frenchCurrency");

        ResultObject resultObject = new ResultObject();
        resultObject = this.readSecurityContextFromSession(resultObject);
        if (!"0".equals(resultObject.getCode())) {
            return resultObject;
        }
        try {
            String error = this.verif2(amount, pageUrl);
            if (!StringUtils.isNullOrEmpty(error)) {
                throw new BusinessException(error);
            }
            Object object = this.sessionTokenService.cacheGet(session_token);
            this.sessionTokenService.delete(session_token);
            if (null == object || !this.getLoginPartyId().equals(object)) {
                throw new BusinessException("请稍后再试");
            }

            String payUrl = thirdPartyRechargeService.saveApplyRecharge2(getLoginPartyId(), amount, pageUrl);
            resultObject.setData(payUrl);
        } catch (BusinessException e) {
            resultObject.setCode("1");
            resultObject.setMsg(e.getMessage());
        } catch (Throwable t) {
            resultObject.setCode("1");
            resultObject.setMsg("程序错误");
            logger.error("error:", t);
        }

        return resultObject;
    }

    /**
     * 2.0异步通知
     */
    @PostMapping(ACTION + "onSucceeded2.action")
    public String onsucceeded2(GCash2NotifyVo notifyVo) {
        String sign = notifyVo.getSign();
        Map<String, Object> params = (Map<String, Object>) JSON.toJSON(notifyVo);
        String key = sysparaService.find("third_party_api_key_2").getValue();
        String mySign = thirdPartyManager.sign2MD5(params, key);
        if (!mySign.equals(sign)) {
            LOGGER.error("第三方2.0异步通知签名验证不通过");
            return "error sign";
        }
        RechargeInfo rechargeInfo = null;
        try {
            synchronized (this) {
                rechargeInfo = this.thirdPartyRechargeService.saveSucceeded2(notifyVo);
            }
        } catch (BusinessException e) {
            logger.error("第三方2.0回调失败：", e);
            return "error";
        } catch (Throwable t) {
            logger.error("2.0 update error ", t);
            return "error";
        }

        if (Objects.nonNull(rechargeInfo)) {
            WebApplicationContext wac = ContextLoader.getCurrentWebApplicationContext();
            wac.publishEvent(new RechargeSuccessEvent(this, rechargeInfo));
        }
        return "SUCCESS";
    }

    /**
     * 第三方PHP充值申请3.0
     * pageUrl 支付成功回跳页面
     * amount 金额
     */
    @RequestMapping(ACTION + "PHP_recharge3.action")
    public Object thirdPartyPHPRecharge3(HttpServletRequest request) throws IOException {
        String session_token = request.getParameter("session_token");
        String amount = request.getParameter("amount");
        String callBackUrl = request.getParameter("pageUrl");
//        String frenchCurrency = request.getParameter("frenchCurrency");

        ResultObject resultObject = new ResultObject();
        resultObject = this.readSecurityContextFromSession(resultObject);
        if (!"0".equals(resultObject.getCode())) {
            return resultObject;
        }
        try {
            String error = this.verif2(amount, callBackUrl);
            if (!StringUtils.isNullOrEmpty(error)) {
                throw new BusinessException(error);
            }
            Object object = this.sessionTokenService.cacheGet(session_token);
            this.sessionTokenService.delete(session_token);
            if (null == object || !this.getLoginPartyId().equals(object)) {
                throw new BusinessException("请稍后再试");
            }

            String payUrl = thirdPartyRechargeService.saveApplyRecharge3(getLoginPartyId(), amount, callBackUrl);
            resultObject.setData(payUrl);
        } catch (BusinessException e) {
            resultObject.setCode("1");
            resultObject.setMsg(e.getMessage());
        } catch (Throwable t) {
            resultObject.setCode("1");
            resultObject.setMsg("程序错误");
            logger.error("error:", t);
        }

        return resultObject;
    }

    /**
     * 第三方PHP充值申请PayMaya
     * pageUrl 支付成功回跳页面
     * amount 金额
     */
    @RequestMapping(ACTION + "PHP_recharge4.action")
    public Object thirdPartyPHPRecharge4(HttpServletRequest request) throws IOException {
        String session_token = request.getParameter("session_token");
        String amount = request.getParameter("amount");
        String callBackUrl = request.getParameter("pageUrl");
//        String frenchCurrency = request.getParameter("frenchCurrency");

        ResultObject resultObject = new ResultObject();
        resultObject = this.readSecurityContextFromSession(resultObject);
        if (!"0".equals(resultObject.getCode())) {
            return resultObject;
        }
        try {
            String error = this.verif2(amount, callBackUrl);
            if (!StringUtils.isNullOrEmpty(error)) {
                throw new BusinessException(error);
            }
            Object object = this.sessionTokenService.cacheGet(session_token);
            this.sessionTokenService.delete(session_token);
            if (null == object || !this.getLoginPartyId().equals(object)) {
                throw new BusinessException("请稍后再试");
            }

            String payUrl = thirdPartyRechargeService.saveApplyRecharge4(getLoginPartyId(), amount, callBackUrl);
            resultObject.setData(payUrl);
        } catch (BusinessException e) {
            resultObject.setCode("1");
            resultObject.setMsg(e.getMessage());
        } catch (Throwable t) {
            resultObject.setCode("1");
            resultObject.setMsg("程序错误");
            logger.error("error:", t);
        }

        return resultObject;
    }

    /**
     * 3.0异步通知
     */
    @PostMapping(ACTION + "onSucceeded3.action")
    public String onsucceeded3(@RequestBody GCash3NotifyVo notifyVo) {
        String sign = notifyVo.getSign();
        Map<String, Object> params = (Map<String, Object>) JSON.toJSON(notifyVo);
        String key = sysparaService.find("third_party_api_key_3").getValue();
        String mySign = thirdPartyManager.sign2MD5(params, key);
        if (!mySign.equals(sign)) {
            LOGGER.error("第三方3.0异步通知签名验证不通过");
            return "error sign";
        }
        RechargeInfo rechargeInfo = null;
        try {
            synchronized (this) {
                rechargeInfo = this.thirdPartyRechargeService.saveSucceeded3(notifyVo);
            }
        } catch (BusinessException e) {
            logger.error("第三方3.0回调失败：", e);
            return "error";
        } catch (Throwable t) {
            logger.error("3.0 update error ", t);
            return "error";
        }

        if (Objects.nonNull(rechargeInfo)) {
            WebApplicationContext wac = ContextLoader.getCurrentWebApplicationContext();
            wac.publishEvent(new RechargeSuccessEvent(this, rechargeInfo));
        }

        return "SUCCESS";
    }

    /**
     * 第三方PHP充值申请GCash pay
     * pageUrl 支付成功回跳页面
     * amount 金额
     */
    @RequestMapping(ACTION + "PHP_recharge5.action")
    public Object thirdPartyPHPRecharge5(HttpServletRequest request) throws IOException {
        String session_token = request.getParameter("session_token");
        String amount = request.getParameter("amount");
        String callBackUrl = request.getParameter("pageUrl");
//        String frenchCurrency = request.getParameter("frenchCurrency");

        ResultObject resultObject = new ResultObject();
        resultObject = this.readSecurityContextFromSession(resultObject);
        if (!"0".equals(resultObject.getCode())) {
            return resultObject;
        }
        try {
            String error = this.verif2(amount, callBackUrl);
            if (!StringUtils.isNullOrEmpty(error)) {
                throw new BusinessException(error);
            }
            Object object = this.sessionTokenService.cacheGet(session_token);
            this.sessionTokenService.delete(session_token);
            if (null == object || !this.getLoginPartyId().equals(object)) {
                throw new BusinessException("请稍后再试");
            }

            String payUrl = thirdPartyRechargeService.saveApplyRecharge5(getLoginPartyId(), amount, callBackUrl);
            resultObject.setData(payUrl);
        } catch (BusinessException e) {
            resultObject.setCode("1");
            resultObject.setMsg(e.getMessage());
        } catch (Throwable t) {
            resultObject.setCode("1");
            resultObject.setMsg("程序错误");
            logger.error("error:", t);
        }

        return resultObject;
    }

    /**
     * GCash pay异步通知
     */
    @PostMapping(ACTION + "onSucceeded4.action")
    public String onsucceeded4(GCashPayNotifyVo notifyVo) {
        String sign = notifyVo.getSign();
        Map<String, Object> params = objectToMapWithOriginalPropertyNames(notifyVo);
        String key = sysparaService.find("third_party_api_key_5").getValue();
        String mySign;
        try {
            mySign = thirdPartyManager.getSign(params, key);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            LOGGER.error("第三方GCash pay异步通知签名异常", e);
            return "sign error";
        }
        if (!mySign.equals(sign)) {
            LOGGER.error("第三方GCash pay异步通知签名验证不通过");
            return "error sign";
        }
        RechargeInfo rechargeInfo = null;
        try {
            synchronized (this) {
                rechargeInfo = this.thirdPartyRechargeService.saveSucceeded4(notifyVo);
            }
        } catch (BusinessException e) {
            logger.error("第三方GCash pay回调失败：", e);
            return "error";
        } catch (Throwable t) {
            logger.error("GCash pay update error ", t);
            return "error";
        }
        if (Objects.nonNull(rechargeInfo)) {
            WebApplicationContext wac = ContextLoader.getCurrentWebApplicationContext();
            wac.publishEvent(new RechargeSuccessEvent(this, rechargeInfo));
        }

        return "SUCCESS";
    }

    private String verif(String amount) {

        if (StringUtils.isNullOrEmpty(amount)) {
            return "充值数量必填";
        }
        if (!StringUtils.isDouble(amount)) {
            return "充值数量输入错误，请输入浮点数";
        }
        return null;
    }

    private String verif2(String amount, String pageUrl) {

        if (StringUtils.isNullOrEmpty(amount)) {
            return "充值数量必填";
        }
        if (StringUtils.isNullOrEmpty(pageUrl)) {
            return "参数缺失";
        }
        if (!StringUtils.isDouble(amount)) {
            return "充值数量输入错误，请输入浮点数";
        }
        return null;
    }

    public static Map<String, Object> objectToMapWithOriginalPropertyNames(Object obj) {
        Map<String, Object> map = new HashMap<>();
        Class<?> clazz = obj.getClass();

        // 遍历对象的字段
        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true); // 允许访问私有字段
            try {
                // 将字段名（首字母保持不变）和字段值存储到 Map 中
                map.put(field.getName(), field.get(obj));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        return map;
    }

    /**
     * 微信支付宝充值申请
     * amount 金额
     */
    @RequestMapping(ACTION + "top_up_v11.action")
    public Object thirdPartTopUpV11(HttpServletRequest request) throws IOException {
        String session_token = request.getParameter("session_token");
        String amount = request.getParameter("amount");
        String channel = request.getParameter("channel");
        ResultObject resultObject = new ResultObject();
        resultObject = this.readSecurityContextFromSession(resultObject);
        if (!"0".equals(resultObject.getCode())) {
            return resultObject;
        }
        try {
            String error = this.verif(amount);
            if (!StringUtils.isNullOrEmpty(error)) {
                throw new BusinessException(error);
            }
            Object object = this.sessionTokenService.cacheGet(session_token);
            this.sessionTokenService.delete(session_token);
            String partyId = this.getLoginPartyId();
            if (!partyId.equals(object)) {
                throw new BusinessException("请稍后再试");
            }

            String payUrl = thirdPartyRechargeService.submitTopUpRequest(request, partyId,channel, amount);
            resultObject.setData(Maps.of("payUrl", payUrl));
        } catch (BusinessException e) {
            resultObject.setCode("1");
            resultObject.setMsg(e.getMessage());
        } catch (Throwable t) {
            resultObject.setCode("1");
            resultObject.setMsg(t.getMessage());
            logger.error("error:", t);
        }
        return resultObject;
    }


    /**
     * 异步通知
     */
    @PostMapping(ACTION + "payment_v11_notify.action")
    public String thirdPartTopUpNotify(@RequestBody Map<String,Object> data) {
        LOGGER.info("data:{}", JSON.toJSONString(data));
        //String mockData = "{\"mchId\":\"M1733726846\",\"tradeNo\":\"SM20241212150607362323271\",\"outTradeNo\":\"24121215060716195591\",\"originTradeNo\":1,\"amount\":12000,\"subject\":\"用户充值\",\"notifyTime\":1733987390981,\"state\":1,\"extParam\":\"{\\\"orderNo\\\":\\\"24121215060716195591\\\"}\",\"sign\":\"6bbf7f6c9935b268242b700d9e32e969\"}";
       // Map<String, Object> map = JSON.parseObject(mockData, Map.class);
        if(!PayCore.checkSign(data,data.get("sign").toString())){
            return "Sign Not Match";
        }
        String orderNo = TypeConv.toString(data.get("outTradeNo"));
//        mchId 	商户号 	是 	string 	M1623984572 	商户号
//        tradeNo 	支付订单号 	是 	string 	P202109052329398641190 	返回支付系统订单号
//        outTradeNo 	商户订单号 	是 	string 	20210905000702675466 	返回商户传入的订单号
//        originTradeNo 	通道订单号 	否 	int 	1 	返回 1
//        amount 	订单金额 (单位: 分) 	是 	long 	10000 	订单金额 (单位: 分)，例如: 10000 即为 100.00 元
//        subject 	商品标题 	是 	string 	商品标题测试 	商品标题
//        body 	商品描述 	否 	string 	商品描述测试 	商品描述
//        extParam 	扩展参数 	否 	string 	134586944573118714 	商户扩展参数，回调时会原样返回
//        state 	订单状态 	是 	int 	1 	订单状态：0=待支付，1=支付成功，2=支付失败
//        notifyTime 	通知时间 	是 	long 	1622016572190 	通知时间，13位时间戳
//        sign 	签名 	是 	string 	694da7a446ab4b1d9ceea7e5614694f4 	签名值，详见 签名算法


        RechargeInfo rechargeInfo = null;
        try {
            synchronized (this) {
                rechargeInfo = this.thirdPartyRechargeService.updatePaymentSuccessStatus(orderNo);
            }
        } catch (BusinessException e) {
            logger.error("第三方回调失败：", e);
            return "error";
        } catch (Throwable t) {
            logger.error("update error ", t);
            return "error";
        }
        if (Objects.nonNull(rechargeInfo)) {
            WebApplicationContext wac = ContextLoader.getCurrentWebApplicationContext();
            wac.publishEvent(new RechargeSuccessEvent(this, rechargeInfo));
        }
        return "SUCCESS";
    }

    /**
     * 微信支付宝充值申请
     * amount 金额
     */
    @RequestMapping(ACTION + "top_up_manual.action")
    public Object submitManualTopUpRequest(HttpServletRequest request) throws IOException {
        String session_token = request.getParameter("session_token");
        String amount = request.getParameter("amount");
        String channel = request.getParameter("channel");
        ResultObject resultObject = new ResultObject();
        resultObject = this.readSecurityContextFromSession(resultObject);
        if (!"0".equals(resultObject.getCode())) {
            return resultObject;
        }
        try {
            String error = this.verif(amount);
            if (!StringUtils.isNullOrEmpty(error)) {
                throw new BusinessException(error);
            }
            Object object = this.sessionTokenService.cacheGet(session_token);
            this.sessionTokenService.delete(session_token);
            String partyId = this.getLoginPartyId();
            if (!partyId.equals(object)) {
                throw new BusinessException("请稍后再试");
            }

            thirdPartyRechargeService.submitManualRequest( partyId,channel, amount);
            resultObject.setData(Maps.of("payUrl",String.format( "/customerService?type=manualTopUp&channel=%s&amount=%s",channel,amount)));
        } catch (BusinessException e) {
            resultObject.setCode("1");
            resultObject.setMsg(e.getMessage());
        } catch (Throwable t) {
            resultObject.setCode("1");
            resultObject.setMsg(t.getMessage());
            logger.error("error:", t);
        }
        return resultObject;
    }

}
