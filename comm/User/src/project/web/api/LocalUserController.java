package project.web.api;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import kernel.util.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.providers.encoding.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kernel.exception.BusinessException;
import kernel.web.BaseAction;
import kernel.web.ResultObject;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;
import project.Constants;
import project.ddos.IpMenuService;
import project.event.message.ModifyUserInfoEvent;
import project.event.model.UserChangeInfo;
import project.log.Log;
import project.log.LogService;
import project.mall.MallRedisKeys;
import project.mall.seller.SellerService;
import project.mall.seller.model.Seller;
import project.party.PartyService;
import project.party.model.Party;
import project.party.recom.UserRecomService;
import project.redis.RedisHandler;
import project.syspara.Syspara;
import project.syspara.SysparaService;
import project.user.LocalNormalReg;
import project.user.LocalUserService;
import project.user.QRGenerateService;
import project.user.UserDataService;
import project.user.UserService;
import project.user.captcha.GeetestService;
import project.user.idcode.IdentifyingCodeTimeWindowService;
import project.user.kyc.Kyc;
import project.user.kyc.KycHighLevel;
import project.user.kyc.KycHighLevelService;
import project.user.kyc.KycService;
import project.user.token.TokenService;
import security.SecUser;
import security.internal.SecUserService;
import util.LockFilter;
import util.RegexUtil;
import util.Strings;

@RestController
@CrossOrigin
public class LocalUserController extends BaseAction {

    private Logger logger = LogManager.getLogger(LocalUserController.class);

    @Autowired
    private PartyService partyService;
    @Autowired
    private QRGenerateService qRGenerateService;
    @Autowired
    private KycService kycService;
    @Autowired
    private KycHighLevelService kycHighLevelService;
    @Autowired
    private SysparaService sysparaService;
    @Autowired
    private UserService userService;
    @Autowired
    private LocalUserService localUserService;
    @Autowired
    private SecUserService secUserService;
    @Autowired
    private LogService logService;
    @Autowired
    protected TokenService tokenService;
    @Autowired
    private IpMenuService ipMenuService;
    @Autowired
    private IdentifyingCodeTimeWindowService identifyingCodeTimeWindowService;
    @Autowired
    private GeetestService geetestService;
    @Autowired
    private UserRecomService userRecomService;
    @Autowired
    protected JdbcTemplate jdbcTemplate;
    @Autowired
    private SellerService sellerService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Resource
    protected RedisHandler redisHandler;
    @Resource
    private UserDataService userDataService;

    private final String action = "api/localuser!";

    /**
     * Geetest二次验证：返回验证结果, request表单中必须包含challenge, validate, seccode
     */
    public HashMap<String, String> geetestVerif(String challenge, String validate, String seccode, String gt_server_status) throws UnsupportedEncodingException {

        if (StringUtils.isEmptyString(challenge) || StringUtils.isEmptyString(validate)
                || StringUtils.isEmptyString(seccode) || StringUtils.isEmptyString(gt_server_status)) {
            logger.info("ip:{" + this.getIp() + "},图形验证错误: "
                    + "challenge:{" + challenge + "},validate:{" + validate + "},seccode:{" + seccode + "},gt_server_status:{" + gt_server_status + "}");
            throw new BusinessException("图形验证错误，请重新再试");
        }

        // 自定义参数，可选择添加
        HashMap<String, String> param = new HashMap<String, String>();

        // 网站用户id
        param.put("user_id", "test");

        param.put("challenge", challenge);
        param.put("validate", validate);
        param.put("seccode", seccode);

        String geetest_id = this.sysparaService.find("geetest_id").getValue();
        String geetest_key = this.sysparaService.find("geetest_key").getValue();
        String new_failback = this.sysparaService.find("geetest_new_failback").getValue();
        if (StringUtils.isEmptyString(geetest_id) || StringUtils.isEmptyString(geetest_key) || StringUtils.isEmptyString(new_failback)) {
            throw new BusinessException("系统参数错误");
        }

        param.put("geetest_id", geetest_id);
        param.put("geetest_key", geetest_key);
        param.put("new_failback", new_failback);

        int gtResult = 0;
        if ("1".equals(gt_server_status)) {
            // gt-server正常，向gt-server进行二次验证
            gtResult = this.geetestService.enhencedValidateRequest(param);
            System.out.println(gtResult);
        } else {
            // gt-server非正常情况下，进行failback模式验证
            System.out.println("failback:use your own server captcha validate");
            gtResult = this.geetestService.failbackValidateRequest(param);
            System.out.println(gtResult);
        }

        HashMap<String, String> retMap = this.geetestService.preProcess(param);

        if (1 == gtResult) {
            // 验证成功
            retMap.put("status", "success");
            retMap.put("version", this.geetestService.getVersionInfo());
        } else {
            // 验证失败
            retMap.put("status", "fail");
            retMap.put("version", this.geetestService.getVersionInfo());
        }

        return retMap;
    }

    /**
     * 交易所用户注册（Geetest滑动图片验证方式）
     */
    @RequestMapping(action + "register_username_geetest.action")
    public Object register_username_geetest(HttpServletRequest request) {

        String username = request.getParameter("username").replace(" ", "");
        String password = request.getParameter("password").replace(" ", "");
        String safeword = request.getParameter("safeword").replace(" ", "");
        String usercode = request.getParameter("usercode");

        String challenge = request.getParameter("geetest_challenge");
        String validate = request.getParameter("geetest_validate");
        String seccode = request.getParameter("geetest_seccode");
        String gt_server_status = request.getParameter("gt_server_status");

        ResultObject resultObject = new ResultObject();

        boolean lock = false;

        try {

            if (!LockFilter.add(username)) {
                throw new BusinessException("重复提交");
            }

            lock = true;

            String error = this.validateParamUsername(username, password);
            if (!StringUtils.isNullOrEmpty(error)) {
                throw new BusinessException(error);
            }

            if (StringUtils.isEmptyString(safeword)) {
                throw new BusinessException("资金密码不能为空");
            }
            if (safeword.length() != 6 || !Strings.isNumber(safeword)) {
                throw new BusinessException("资金密码不符合设定");
            }

            Syspara geetest_open = this.sysparaService.find("geetest_open");
            if (null != geetest_open && true == geetest_open.getBoolean()) {

                HashMap<String, String> retGeetest = this.geetestVerif(challenge, validate, seccode, gt_server_status);
                if ("fail".equals(retGeetest.get("status"))) {
                    throw new BusinessException("图形验证错误，请重新再试");
                }
            }

            LocalNormalReg reg = new LocalNormalReg();
            reg.setUsername(username);
            reg.setPassword(password);
            reg.setReco_usercode(usercode);
            reg.setSafeword(safeword);
            this.localUserService.saveRegisterUsername(reg);

            SecUser secUser = this.secUserService.findUserByLoginName(username);

            Log log = new Log();
            log.setCategory(Constants.LOG_CATEGORY_SECURITY);
            log.setLog("用户无验证码注册,ip[" + this.getIp(getRequest()) + "]");
            log.setPartyId(secUser.getPartyId());
            log.setUsername(username);
            this.logService.saveAsyn(log);

            // 注册完直接登录返回token
            String token = this.tokenService.savePut(secUser.getPartyId());
            this.userService.online(secUser.getPartyId());
            this.ipMenuService.saveIpMenuWhite(this.getIp());

            Party party = this.partyService.cachePartyBy(secUser.getPartyId(), true);

            Map<String, Object> data = new HashMap<String, Object>();
            data.put("token", token);
            data.put("username", secUser.getUsername());
            data.put("usercode", party.getUsercode());

            party.setLogin_ip(this.getIp(getRequest()));
            this.partyService.update(party);
			this.userDataService.saveRegister(party.getId());
            resultObject.setData(data);

        } catch (BusinessException e) {
            resultObject.setCode("1");
            resultObject.setMsg(e.getMessage());
        } catch (Throwable t) {
            logger.error("UserAction.register error ", t);
            this.error = ("[ERROR] " + t.getMessage());
            resultObject.setCode("1");
            resultObject.setMsg(this.error);
        } finally {
            if (lock) {
                LockFilter.remove(username);
            }
        }

        return resultObject;
    }

    /**
     * 交易所用户注册（验证码方式）
     */
    @RequestMapping(action + "register_username.action")
    public Object register_username(HttpServletRequest request) {

        String username = request.getParameter("username").replace(" ", "");
        String password = request.getParameter("password").replace(" ", "");
        String safeword = request.getParameter("safeword").replace(" ", "");
        String usercode = request.getParameter("usercode");
        String code = request.getParameter("code");
        String key = request.getParameter("key");

        ResultObject resultObject = new ResultObject();
        String error = "";
        boolean lock = false;
        try {

            if (!LockFilter.add(username)) {
                resultObject.setCode("1");
                resultObject.setMsg("重复提交");
                return resultObject;
            }
            lock = true;
            error = validateParamUsername(username, password);
            if (!StringUtils.isNullOrEmpty(error)) {
                resultObject.setCode("1");
                resultObject.setMsg(error);
                return resultObject;
            }

            if (StringUtils.isEmptyString(safeword)) {
                throw new BusinessException("资金密码不能为空");
            }
            if (safeword.length() != 6 || !Strings.isNumber(safeword)) {
                throw new BusinessException("资金密码不符合设定");
            }

            boolean register_image_code_button = sysparaService.find("register_image_code_button").getBoolean();
            if (register_image_code_button) {
                if (StringUtils.isEmptyString(code) || StringUtils.isEmptyString(key)) {
                    throw new BusinessException("验证码不能为空");
                } else {
                    String decryptCode = ImageVerificationEndecrypt.decryptDES(code, key + "key");
                    if (!decryptCode.equalsIgnoreCase(userService.cacheImageCode(key))) {
                        logger.info("ip:{" + this.getIp() + "},图片验证码不正确,paramcode:{" + decryptCode + "},truecode:{"
                                + userService.cacheImageCode(key) + "}");
                        throw new BusinessException("验证码错误");
                    }
                }
            }
            LocalNormalReg reg = new LocalNormalReg();
            reg.setUsername(username);
            reg.setPassword(password);
            reg.setReco_usercode(usercode);
            reg.setSafeword(safeword);

            this.localUserService.saveRegisterUsername(reg);

            SecUser secUser = secUserService.findUserByLoginName(username);
            Log log = new Log();

            log.setCategory(Constants.LOG_CATEGORY_SECURITY);
            log.setLog("用户无验证码注册,ip[" + this.getIp(getRequest()) + "]");
            log.setPartyId(secUser.getPartyId());
            log.setUsername(username);
            logService.saveAsyn(log);

            // 注册完直接登录返回token
            String token = tokenService.savePut(secUser.getPartyId());
            userService.online(secUser.getPartyId());
            ipMenuService.saveIpMenuWhite(this.getIp());
            Party party = this.partyService.cachePartyBy(secUser.getPartyId(), true);
            Map<String, Object> data = new HashMap<String, Object>();
            data.put("token", token);
            data.put("username", secUser.getUsername());
            data.put("usercode", party.getUsercode());
            party.setLogin_ip(this.getIp(getRequest()));
            this.partyService.update(party);

			this.userDataService.saveRegister(party.getId());

            resultObject.setData(data);
        } catch (BusinessException e) {
            resultObject.setCode("1");
            resultObject.setMsg(e.getMessage());
        } catch (Throwable t) {
            logger.error("UserAction.register error ", t);
            this.error = ("[ERROR] " + t.getMessage());
            resultObject.setCode("1");
            resultObject.setMsg(this.error);
        } finally {
            if (lock) {
                LockFilter.remove(username);
            }
        }

        return resultObject;
    }

    private String validateParamUsername(String username, String password) {

        if (StringUtils.isNullOrEmpty(username)) {
            return "用户名不能为空";
        }

        if (StringUtils.isNullOrEmpty(password)) {
            return "登录密码不能为空";
        }
        if (!RegexUtil.isUSername(username)) {
            return "用户名必须由数字和英文字母组成";
        }

        int min = 6;
        int max = 12;
        int max_name = 24;
        if (!RegexUtil.length(username, min, max_name)) {
            return "用户名不符合设定";
        }
        if (!RegexUtil.length(password, min, max)) {
            return "登陆密码长度不符合设定";
        }
//		if (!RegexUtil.isDigits(this.password)) {
//			// 只能输入数字
//			return "登陆密码不符合设定";
//		}
//		if (StringUtils.isEmptyString(this.safeword)) {
//			return "资金密码不能为空";
//		}
//		if (!StringUtils.isEmptyString(this.safeword) && !RegexUtil.length(this.safeword, min, max)) {
//			// return "资金密码长度限制" + min + "-" + max + "个字符";
//			return "资金密码长度不符合设定";
//		}
//		if (StringUtils.isEmptyString(this.safeword) && !RegexUtil.isDigits(this.safeword)) {
//			// 只能输入数字
//			return "资金密码不符合设定";
//		}

        return null;
    }

    /**
     * 手机/邮箱注册接口
     */
    @RequestMapping(action + "register.action")
    public Object register(HttpServletRequest request) {
        String username = request.getParameter("username").replace(" ", "");
        String password = request.getParameter("password").replace(" ", "");
        String safeword = request.getParameter("safeword").replace(" ", "");
        String verifcode = request.getParameter("verifcode");
        String usercode = request.getParameter("usercode");
        // 注册类型：1/手机；2/邮箱；
        String type = request.getParameter("type");

        ResultObject resultObject = new ResultObject();

        boolean lock = false;

        try {

            if (!LockFilter.add(username)) {
                throw new BusinessException("重复提交");
            }

            lock = true;

            String error = this.validateParam(username, verifcode, password, type);
            if (!StringUtils.isNullOrEmpty(error)) {
                throw new BusinessException(error);
            }

            if (StringUtils.isEmptyString(safeword)) {
                throw new BusinessException("资金密码不能为空");
            }
            if (safeword.length() != 6 || !Strings.isNumber(safeword)) {
                throw new BusinessException("资金密码不符合设定");
            }

            LocalNormalReg reg = new LocalNormalReg();
            reg.setUsername(username);
            reg.setPassword(password);
            reg.setSafeword(safeword);
            reg.setReco_usercode(usercode);
            reg.setIdentifying_code(verifcode);

            this.localUserService.saveRegister(reg, type);

            SecUser secUser = this.secUserService.findUserByLoginName(username);

            project.log.Log log = new project.log.Log();
            log.setCategory(Constants.LOG_CATEGORY_SECURITY);
            log.setLog("用户注册,ip[" + this.getIp(getRequest()) + "]");
            log.setPartyId(secUser.getPartyId());
            log.setUsername(username);
            this.logService.saveAsyn(log);

            // 注册完直接登录返回token
            String token = this.tokenService.savePut(secUser.getPartyId());

            this.userService.online(secUser.getPartyId());
            this.ipMenuService.saveIpMenuWhite(this.getIp());

            Party party = this.partyService.cachePartyBy(secUser.getPartyId(), true);

            Map<String, Object> data = new HashMap<String, Object>();
            data.put("token", token);
            data.put("username", secUser.getUsername());
            data.put("usercode", party.getUsercode());

            party.setLogin_ip(this.getIp(getRequest()));
            this.partyService.update(party);

			this.userDataService.saveRegister(party.getId());

            resultObject.setData(data);

        } catch (BusinessException e) {
            resultObject.setCode("1");
            resultObject.setMsg(e.getMessage());
        } catch (Throwable t) {
            logger.error("UserAction.register error ", t);
            resultObject.setCode("1");
            resultObject.setMsg("[ERROR] " + t.getMessage());
        } finally {
            if (lock) {
                LockFilter.remove(username);
            }
        }

        return resultObject;
    }

    /**
     * 手机/邮箱/用户名注册（无验证码）
     */
    @RequestMapping(action + "registerNoVerifcode.action")
    public Object registerNoVerifcode(HttpServletRequest request) {
//		用户手机号注册时，用户表中字段phone字段修改为 区号+空格+手机号
        String usernameStr = request.getParameter("username");
        String username = request.getParameter("username").replace(" ", "");
        String password = request.getParameter("password").replace(" ", "");
        String re_password = request.getParameter("re_password").replace(" ", "");
        //String usercode = request.getParameter("usercode");
        // 数据挂在哪个代理商下
        String agentCode = request.getParameter("agentCode");
        if (StrUtil.isBlank(agentCode)) {
            agentCode = "000000";
        }

        // 注册类型：1/手机；2/邮箱；3/用户名；
        String type = request.getParameter("type");

        ResultObject resultObject = new ResultObject();

        boolean lock = false;

        try {
            if (!LockFilter.add(username)) {
                throw new BusinessException("重复提交");
            }

            lock = true;
            if (StringUtils.isEmptyString(username)) {
                throw new BusinessException("用户名不能为空");
            }

            if (StringUtils.isEmptyString(password)) {
                throw new BusinessException("登录密码不能为空");
            }

            if (StringUtils.isEmptyString(re_password)) {
                throw new BusinessException("密码确认不能为空");
            }
            password = URLDecoder.decode(password, StandardCharsets.UTF_8.toString());
            re_password = URLDecoder.decode(re_password, StandardCharsets.UTF_8.toString());
            if (password.length() < 6 || password.length() > 20 || re_password.length() < 6 || re_password.length() > 20) {
                throw new BusinessException("密码必须6-20位");
            }

            if (!password.equals(re_password)) {
                throw new BusinessException("两次输入的密码不相同");
            }

            if (StringUtils.isEmptyString(type) || !Arrays.asList("1", "2").contains(type)) {
                throw new BusinessException("类型不能为空");
            }

            LocalNormalReg reg = new LocalNormalReg();
            reg.setUsername(username);
            reg.setPassword(password);
            //reg.setSafeword("000000");
            reg.setRoleType(0);
            //reg.setReco_usercode(usercode);
            reg.setReco_usercode(agentCode);

            this.localUserService.saveRegisterNoVerifcode(reg, type);

            SecUser secUser = this.secUserService.findUserByLoginName(username);

            project.log.Log log = new project.log.Log();
            log.setCategory(Constants.LOG_CATEGORY_SECURITY);
            log.setLog("用户注册,ip[" + this.getIp(getRequest()) + "]");
            log.setPartyId(secUser.getPartyId());
            log.setUsername(username);
            this.logService.saveAsyn(log);

            // 注册完直接登录返回token
            String token = this.tokenService.savePut(secUser.getPartyId());

            this.userService.online(secUser.getPartyId());
            this.ipMenuService.saveIpMenuWhite(this.getIp());

            Party party = this.partyService.cachePartyBy(secUser.getPartyId(), true);

            Map<String, Object> data = new HashMap<String, Object>();
            data.put("token", token);
            data.put("username", secUser.getUsername());
            data.put("usercode", party.getUsercode());

            party.setLogin_ip(this.getIp(getRequest()));
            if ("1".equals(type)) {//若为手机号注册，手机号和区号中间加入空格
                party.setPhone(usernameStr.trim());
            }
            this.partyService.update(party);
			this.userDataService.saveRegister(party.getId());
            resultObject.setData(data);

        } catch (BusinessException e) {
            logger.error("UserAction.register error ", e);
            resultObject.setCode("1");
            resultObject.setMsg(e.getMessage());
        } catch (Throwable t) {
            logger.error("UserAction.register error ", t);
            resultObject.setCode("1");
            resultObject.setMsg("[ERROR] " + t.getMessage());
        } finally {
            if (lock) {
                LockFilter.remove(username);
            }
        }

        return resultObject;
    }

    /**
     * 手机/邮箱/用户名注册（需验证码）  2023-09-27 由于邮箱校验经常被封 因此邮箱校验去掉校验流程
     */
    @RequestMapping(action + "registerWithVerifcode.action")
    public Object registerWithVerifcode(HttpServletRequest request) {
//		用户手机号注册时，用户表中字段phone字段修改为 区号+空格+手机号
        String username = request.getParameter("username");
        String phoneStr = request.getParameter("phone");
        String whatsApp = request.getParameter("whatsApp");
        String phone = request.getParameter("phone").replace(" ", "");
        String password = request.getParameter("password").replace(" ", "");
        String re_password = request.getParameter("re_password").replace(" ", "");
//        新增校验码验证
        String verifcode = request.getParameter("verifcode");
        String agentCode = request.getParameter("agentCode");
        if (StrUtil.isBlank(agentCode)) {
            agentCode = "000000";
        }
        // 注册类型：1/手机；2/邮箱；3/用户名；
        String type = request.getParameter("type");

        if (StringUtils.isEmptyString(type) || !Arrays.asList("1", "2").contains(type)) {
            throw new BusinessException("类型不能为空");
        }

        ResultObject resultObject = new ResultObject();
//            校验验证码
//        String authcode = "1".equals(type) ? this.identifyingCodeTimeWindowService.getAuthCode(phone):this.identifyingCodeTimeWindowService.getAuthCode(username);
//        if ((null == authcode) || (!authcode.equals(verifcode))) {
//            resultObject.setCode("1");
//            resultObject.setMsg("验证码不正确");
//            return resultObject;
//        }
//        this.identifyingCodeTimeWindowService.delAuthCode("1".equals(type)?phone:username);
//        2023-09-27 此处修改为邮箱去掉校验，如果为手机号注册只校验手机号
        if ("1".equals(type)) {
            String authcode = this.identifyingCodeTimeWindowService.getAuthCode(phone);
            if ((null == authcode) || (!authcode.equals(verifcode))) {
                resultObject.setCode("1");
                resultObject.setMsg("验证码不正确");
                return resultObject;
            }
            this.identifyingCodeTimeWindowService.delAuthCode(phone);
        }

        boolean lock = false;
        try {
            if (!LockFilter.add(username)) {
                throw new BusinessException("重复提交");
            }

            lock = true;
            if (StringUtils.isEmptyString(username)) {
                throw new BusinessException("用户名不能为空");
            }

            if (StringUtils.isEmptyString(password)) {
                throw new BusinessException("登录密码不能为空");
            }

            if (StringUtils.isEmptyString(re_password)) {
                throw new BusinessException("密码确认不能为空");
            }
            password = URLDecoder.decode(password, StandardCharsets.UTF_8.toString());
            re_password = URLDecoder.decode(re_password, StandardCharsets.UTF_8.toString());
            if (password.length() < 6 || password.length() > 20 || re_password.length() < 6 || re_password.length() > 20) {
                throw new BusinessException("密码必须6-20位");
            }

            if (!password.equals(re_password)) {
                throw new BusinessException("两次输入的密码不相同");
            }
            //校验邮箱是否有重复的
            if (Objects.nonNull(this.partyService.findPartyByUsername(username)) || Objects.nonNull(this.partyService.getPartyByEmail(username))) {
                throw new BusinessException("该邮箱已被占用，请更换其他邮箱注册");
            }
            //校验手机号是否有重复的，注意手机号里面有空格
            if (Objects.nonNull(this.partyService.findPartyByUsername(phoneStr.replaceAll("\\s",""))) || Objects.nonNull(this.partyService.findPartyByVerifiedPhone(phoneStr))) {
                throw new BusinessException("该手机号已被占用，请绑定其他手机号");
            }

            LocalNormalReg reg = new LocalNormalReg();
            reg.setUsername(username);
            reg.setPassword(password);
            reg.setRoleType(0);
            reg.setPhone(phoneStr);
            reg.setReco_usercode(agentCode);
            reg.setWhatsApp(whatsApp);

            this.localUserService.saveRegisterWithVerifcode(reg, type);

            SecUser secUser = this.secUserService.findUserByLoginName(username);

            project.log.Log log = new project.log.Log();
            log.setCategory(Constants.LOG_CATEGORY_SECURITY);
            log.setLog("用户注册,ip[" + this.getIp(getRequest()) + "]");
            log.setPartyId(secUser.getPartyId());
            log.setUsername(username);
            this.logService.saveAsyn(log);

            // 注册完直接登录返回token
            String token = this.tokenService.savePut(secUser.getPartyId());

            this.userService.online(secUser.getPartyId());
            this.ipMenuService.saveIpMenuWhite(this.getIp());

            Party party = this.partyService.cachePartyBy(secUser.getPartyId(), true);

            Map<String, Object> data = new HashMap<String, Object>();
            data.put("token", token);
            data.put("username", secUser.getUsername());
            data.put("usercode", party.getUsercode());

            party.setLogin_ip(this.getIp(getRequest()));
            if ("1".equals(type)) {//若为手机号注册，手机号和区号中间加入空格
                party.setPhone(username);
            }
            this.partyService.update(party);
            this.userDataService.saveRegister(party.getId());
            resultObject.setData(data);

        } catch (BusinessException e) {
            logger.error("UserAction.register error ", e);
            resultObject.setCode("1");
            resultObject.setMsg(e.getMessage());
        } catch (Throwable t) {
            logger.error("UserAction.register error ", t);
            resultObject.setCode("1");
            resultObject.setMsg("[ERROR] " + t.getMessage());
        } finally {
            if (lock) {
                LockFilter.remove(username);
            }
        }

        return resultObject;
    }

    /**
     * 手机/邮箱/用户名注册（无验证码，填资金密码）
     */
    @RequestMapping(action + "registerNoVerifcodeSafeword.action")
    public Object registerNoVerifcodeSafeword(HttpServletRequest request) {
        String username = request.getParameter("username").replace(" ", "");
        String password = request.getParameter("password").replace(" ", "");
        String re_password = request.getParameter("re_password").replace(" ", "");
        String usercode = request.getParameter("usercode");
        // 注册类型：1/手机；2/邮箱；3/用户名；
        String type = request.getParameter("type");
        // 资金密码选填，不填默认 000000
        String safeword = request.getParameter("safeword").replace(" ", "");
        String re_safeword = request.getParameter("re_safeword").replace(" ", "");

        ResultObject resultObject = new ResultObject();

        boolean lock = false;

        try {

            if (!LockFilter.add(username)) {
                throw new BusinessException("重复提交");
            }

            lock = true;

            if (StringUtils.isEmptyString(username)) {
                throw new BusinessException("用户名不能为空");
            }

            if (StringUtils.isEmptyString(password)) {
                throw new BusinessException("登录密码不能为空");
            }

            if (StringUtils.isEmptyString(re_password)) {
                throw new BusinessException("密码确认不能为空");
            }
            password = URLDecoder.decode(password, StandardCharsets.UTF_8.toString());
            re_password = URLDecoder.decode(re_password, StandardCharsets.UTF_8.toString());
            if (password.length() < 6 || password.length() > 20 || re_password.length() < 6 || re_password.length() > 20) {
                throw new BusinessException("密码必须6-20位");
            }

            if (!password.equals(re_password)) {
                throw new BusinessException("两次输入的密码不相同");
            }

            if (StringUtils.isEmptyString(safeword)) {
                safeword = "000000";
            }
            if (StringUtils.isEmptyString(re_safeword)) {
                re_safeword = "000000";
            }
            if (safeword.length() != 6 || !Strings.isNumber(safeword)) {
                throw new BusinessException("资金密码不符合设定");
            }

            if (!safeword.equals(re_safeword)) {
                throw new BusinessException("两次输入的资金密码不相同");
            }

            if (StringUtils.isEmptyString(type) || !Arrays.asList("1", "2", "3").contains(type)) {
                throw new BusinessException("类型不能为空");
            }

            LocalNormalReg reg = new LocalNormalReg();
            reg.setUsername(username);
            reg.setPassword(password);
            reg.setSafeword(safeword);
            reg.setReco_usercode(usercode);

            this.localUserService.saveRegisterNoVerifcode(reg, type);

            SecUser secUser = this.secUserService.findUserByLoginName(username);

            project.log.Log log = new project.log.Log();
            log.setCategory(Constants.LOG_CATEGORY_SECURITY);
            log.setLog("用户注册,ip[" + this.getIp(getRequest()) + "]");
            log.setPartyId(secUser.getPartyId());
            log.setUsername(username);
            this.logService.saveAsyn(log);

            // 注册完直接登录返回token
            String token = this.tokenService.savePut(secUser.getPartyId());

            this.userService.online(secUser.getPartyId());
            this.ipMenuService.saveIpMenuWhite(this.getIp());

            Party party = this.partyService.cachePartyBy(secUser.getPartyId(), true);

            Map<String, Object> data = new HashMap<String, Object>();
            data.put("token", token);
            data.put("username", secUser.getUsername());
            data.put("usercode", party.getUsercode());

            party.setLogin_ip(this.getIp(getRequest()));
            this.partyService.update(party);
			this.userDataService.saveRegister(party.getId());
            resultObject.setData(data);

        } catch (BusinessException e) {
            resultObject.setCode("1");
            resultObject.setMsg(e.getMessage());
        } catch (Throwable t) {
            logger.error("UserAction.register error ", t);
            resultObject.setCode("1");
            resultObject.setMsg("[ERROR] " + t.getMessage());
        } finally {
            if (lock) {
                LockFilter.remove(username);
            }
        }

        return resultObject;
    }

    private String validateParam(String username, String verifcode, String password, String type) {

        if (StringUtils.isEmptyString(username)) {
            return "用户名不能为空";
        }

//		if (StringUtils.isEmptyString(verifcode)) {
//			return "验证码不能为空";
//		}
        if (StringUtils.isEmptyString(password)) {
            return "登录密码不能为空";
        }
        int min = 6;
        int max = 12;
        if (!RegexUtil.length(password, min, max)) {
            return "登陆密码长度不符合设定";
        }
//		if (!RegexUtil.isDigits(this.password)) {
//			// 只能输入数字
//			return "登陆密码不符合设定";
//		}

//		if (StringUtils.isEmptyString(this.usercode)) {
//			return "推荐码不能为空";
//		}

        if (StringUtils.isEmptyString(type) || !Arrays.asList("1", "2").contains(type)) {
            return "类型不能为空";
        }

        return null;
    }

    /**
     * 绑定邮箱or手机-确认
     */
    @RequestMapping(action + "bindEmailOrPhone.action")
    public Object bindEmailOrPhone(HttpServletRequest request) {
        String target = request.getParameter("target");
        String verifcode = request.getParameter("verifcode");
        String phone = request.getParameter("phone");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        // 注意区分大小写
        String verifyCode = request.getParameter("verifyCode");

        ResultObject resultObject = new ResultObject();
        resultObject = this.readSecurityContextFromSession(resultObject);
        if (!"0".equals(resultObject.getCode())) {
            return resultObject;
        }

        try {
            // 验证码
            Party party = this.partyService.cachePartyBy(this.getLoginPartyId(), true);
            SecUser user = secUserService.findUserByPartyId(this.getLoginPartyId());
            if (party == null || user == null) {
                throw new BusinessException("用户不存在");
            }
            // 如果本接口调用不是用于校验手机/邮箱，则需要校验密码
            String password_encoder = passwordEncoder.encodePassword(password, user.getUsername());
            if (!password_encoder.equals(user.getPassword())) {
                throw new BusinessException("密码不正确");
            }

            String authcode = this.identifyingCodeTimeWindowService.getAuthCode(target);

            String bind_phone_email_ver = this.sysparaService.find("bind_phone_email_ver").getValue();

            // 不是演示用户 && 绑定手机邮箱号是否需要验证：1需要，2不需要
            if (!"GUEST".contentEquals(party.getRolename()) && "1".equals(bind_phone_email_ver)) {
                if ((null == authcode) || (!authcode.equals(verifcode))) {
                    resultObject.setCode("1");
                    resultObject.setMsg("验证码不正确");
                    return resultObject;
                }
                this.identifyingCodeTimeWindowService.delAuthCode(target);
            }
            // 必须通过原始校验
            String verifyCode_inRedis = this.redisHandler.getString(MallRedisKeys.MALL_MODIFY_BEFORE_VERIFY_TOKEN + this.getLoginPartyId());
            if (StringUtils.isEmptyString(verifyCode_inRedis) || !verifyCode_inRedis.equals(verifyCode)) {
                throw new BusinessException("请先校验原始账号");
            }
            this.redisHandler.remove(MallRedisKeys.MALL_MODIFY_BEFORE_VERIFY_TOKEN + this.getLoginPartyId());

            String userName = party.getUsername();
            UserChangeInfo changeInfo = UserChangeInfo.create();
            changeInfo.withPartyId(party.getId().toString());
            changeInfo.withPassword(password);
            if (target.indexOf("@") == -1) {
                // 绑定手机
//				this.localUserService.savePhone(target, String.valueOf(party.getId()));
                // 检查手机号冲突
                Party existParty = this.partyService.findPartyByVerifiedPhone(target);
                String newUserName = target.replace(" ", "");
//				2023-03-31 新增需求，修改电话号码时，账号同步修改(电话和邮箱都可以登录)，因为如果有手机号必定和账号字段一致
//				if (existParty != null && !existParty.getId().toString().equals(party.getId().toString())) {
                if (!(Objects.isNull(existParty) && Objects.isNull(this.partyService.findPartyByUsername(newUserName)))) {
                    throw new BusinessException("该手机号码已被占用，绑定新手机号码失败");
                }

                // 电话绑定成功
                party.setPhone(phone);
                party.setPhone_authority(true);

                // 2023-3-28 新增需求，更改手机号，则账号也改
                changeInfo.withOldPhone(party.getPhone()).withNewPhone(target);
				/*if (userName.indexOf("@") == -1) {
					// 旧账号使用的是手机，手机号变更需要同步账号变更
					String newUserName = target.replace(" ", "");
					party.setUsername(newUserName);
					// 账号修改事件
					changeInfo.withOldUserName(userName).withNewUserName(newUserName);
				}*/

                // 旧账号使用的不管是手机还是邮箱，手机号变更需要同步账号变更
                party.setUsername(newUserName);
                // 账号修改事件
                changeInfo.withOldUserName(userName).withNewUserName(newUserName);

                // 获取用户系统等级：1/新注册；2/邮箱谷歌手机其中有一个已验证；3/用户实名认证； 4/用户高级认证；
                int userLevelSystem = this.partyService.getUserLevelByAuth(party);

                // 十进制个位表示系统级别：1/新注册；2/邮箱谷歌手机其中有一个已验证；3/用户实名认证；4/用户高级认证；
                // 十进制十位表示自定义级别：对应在前端显示为如VIP1 VIP2等级、黄金 白银等级；
                // 如：级别11表示：新注册的前端显示为VIP1；
                int userLevel = party.getUser_level();
                party.setUser_level(((int) Math.floor(userLevel / 10)) * 10 + userLevelSystem);

                this.partyService.update(party);
            } else {
                // 绑定邮箱
//				this.localUserService.saveEmail(target, String.valueOf(party.getId()));
                // 检查邮箱冲突
                Party existParty = this.partyService.getPartyByEmail(target);
//				2023-03-31新增需求，修改邮箱时候，账号不同步修改，因为此时邮箱和手机号都可以登录，必须保证邮箱及邮箱与用户名称的唯一性
				/*if (existParty != null && !existParty.getId().toString().equals(party.getId().toString())) {
					throw new BusinessException("该邮箱已被占用，绑定新邮箱失败");
				}*/
//				新邮箱必须未在EMAIL和USERNAME字段中使用过
                if (!(Objects.isNull(existParty) && Objects.isNull(this.partyService.findPartyByUsername(target)))) {
                    throw new BusinessException("该邮箱已被占用，绑定新邮箱失败");
                }

                // 邮箱绑定成功
                party.setEmail(email);
                party.setEmail_authority(true);

                changeInfo.withOldEmail(party.getEmail()).withNewEmail(target);

                // 旧账号使用的不管是手机还是邮箱，手机号变更需要同步账号变更
                party.setUsername(email);
                // 账号修改事件
                changeInfo.withOldUserName(userName).withNewUserName(email);

                // 获取用户等级 1/新注册；2/邮箱谷歌手机其中有一个已验证；3/用户实名认证； 4/用户高级认证；
                party.setUser_level(this.partyService.getUserLevelByAuth(party));

                this.partyService.update(party);
            }

            // 发布事件，可能其他业务也需要修改相关的字段，例如监听器：ModifyUserInfoEventListener
            WebApplicationContext wac = ContextLoader.getCurrentWebApplicationContext();
            wac.publishEvent(new ModifyUserInfoEvent(this, changeInfo));
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
     * 绑定邮箱or手机-确认 2023-12-13 新需求SM、familyshop 绑定手机号不需要验证码
     */
    @RequestMapping(action + "bindEmailOrPhoneSm.action")
    public Object bindEmailOrPhoneSm(HttpServletRequest request) {
        String target = request.getParameter("target");
        String verifcode = request.getParameter("verifcode");
        String phone = request.getParameter("phone");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        // 注意区分大小写
        String verifyCode = request.getParameter("verifyCode");

        ResultObject resultObject = new ResultObject();
        resultObject = this.readSecurityContextFromSession(resultObject);
        if (!"0".equals(resultObject.getCode())) {
            return resultObject;
        }

        try {
            // 验证码
            Party party = this.partyService.cachePartyBy(this.getLoginPartyId(), true);
            SecUser user = secUserService.findUserByPartyId(this.getLoginPartyId());
            if (party == null || user == null) {
                throw new BusinessException("用户不存在");
            }
            // 如果本接口调用不是用于校验手机/邮箱，则需要校验密码
            String password_encoder = passwordEncoder.encodePassword(password, user.getUsername());
            if (!password_encoder.equals(user.getPassword())) {
                throw new BusinessException("密码不正确");
            }

            String userName = party.getUsername();
            UserChangeInfo changeInfo = UserChangeInfo.create();
            changeInfo.withPartyId(party.getId().toString());
            changeInfo.withPassword(password);
            if (target.indexOf("@") == -1) {
                // 绑定手机
//				this.localUserService.savePhone(target, String.valueOf(party.getId()));
                // 检查手机号冲突
                Party existParty = this.partyService.findPartyByVerifiedPhone(target);
                String newUserName = target.replace(" ", "");
//				2023-03-31 新增需求，修改电话号码时，账号同步修改(电话和邮箱都可以登录)，因为如果有手机号必定和账号字段一致
//				if (existParty != null && !existParty.getId().toString().equals(party.getId().toString())) {
                if (!(Objects.isNull(existParty) && Objects.isNull(this.partyService.findPartyByUsername(newUserName)))) {
                    throw new BusinessException("该手机号码已被占用，绑定新手机号码失败");
                }

                // 电话绑定成功
                party.setPhone(phone);
                party.setPhone_authority(true);

                // 2023-3-28 新增需求，更改手机号，则账号也改
                changeInfo.withOldPhone(party.getPhone()).withNewPhone(target);
				/*if (userName.indexOf("@") == -1) {
					// 旧账号使用的是手机，手机号变更需要同步账号变更
					String newUserName = target.replace(" ", "");
					party.setUsername(newUserName);
					// 账号修改事件
					changeInfo.withOldUserName(userName).withNewUserName(newUserName);
				}*/

                // 旧账号使用的不管是手机还是邮箱，手机号变更需要同步账号变更
                party.setUsername(newUserName);
                // 账号修改事件
                changeInfo.withOldUserName(userName).withNewUserName(newUserName);

                // 获取用户系统等级：1/新注册；2/邮箱谷歌手机其中有一个已验证；3/用户实名认证； 4/用户高级认证；
                int userLevelSystem = this.partyService.getUserLevelByAuth(party);

                // 十进制个位表示系统级别：1/新注册；2/邮箱谷歌手机其中有一个已验证；3/用户实名认证；4/用户高级认证；
                // 十进制十位表示自定义级别：对应在前端显示为如VIP1 VIP2等级、黄金 白银等级；
                // 如：级别11表示：新注册的前端显示为VIP1；
                int userLevel = party.getUser_level();
                party.setUser_level(((int) Math.floor(userLevel / 10)) * 10 + userLevelSystem);

                this.partyService.update(party);
            } else {//此处的邮箱修改需要之前的校验
                String authcode = this.identifyingCodeTimeWindowService.getAuthCode(target);
                String bind_phone_email_ver = this.sysparaService.find("bind_phone_email_ver").getValue();
                // 不是演示用户 && 绑定手机邮箱号是否需要验证：1需要，2不需要
                if (!"GUEST".contentEquals(party.getRolename()) && "1".equals(bind_phone_email_ver)) {
                    if ((null == authcode) || (!authcode.equals(verifcode))) {
                        resultObject.setCode("1");
                        resultObject.setMsg("验证码不正确");
                        return resultObject;
                    }
                    this.identifyingCodeTimeWindowService.delAuthCode(target);
                    // 必须通过原始校验
                    String verifyCode_inRedis = this.redisHandler.getString(MallRedisKeys.MALL_MODIFY_BEFORE_VERIFY_TOKEN + this.getLoginPartyId());
                    if (StringUtils.isEmptyString(verifyCode_inRedis) || !verifyCode_inRedis.equals(verifyCode)) {
                        throw new BusinessException("请先校验原始账号");
                    }
                    this.redisHandler.remove(MallRedisKeys.MALL_MODIFY_BEFORE_VERIFY_TOKEN + this.getLoginPartyId());
                }
                Party existParty = this.partyService.getPartyByEmail(target);
                if (!(Objects.isNull(existParty) && Objects.isNull(this.partyService.findPartyByUsername(target)))) {
                    throw new BusinessException("该邮箱已被占用，绑定新邮箱失败");
                }
                // 邮箱绑定成功
                party.setEmail(email);
                party.setEmail_authority(true);

                changeInfo.withOldEmail(party.getEmail()).withNewEmail(target);

                // 旧账号使用的不管是手机还是邮箱，手机号变更需要同步账号变更
                party.setUsername(email);
                // 账号修改事件
                changeInfo.withOldUserName(userName).withNewUserName(email);

                // 获取用户等级 1/新注册；2/邮箱谷歌手机其中有一个已验证；3/用户实名认证； 4/用户高级认证；
                party.setUser_level(this.partyService.getUserLevelByAuth(party));

                this.partyService.update(party);
            }

            // 发布事件，可能其他业务也需要修改相关的字段，例如监听器：ModifyUserInfoEventListener
            WebApplicationContext wac = ContextLoader.getCurrentWebApplicationContext();
            wac.publishEvent(new ModifyUserInfoEvent(this, changeInfo));
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
     * Check whether mobile or email is right or not before binding the new mobile or email.
     */
    @RequestMapping(action + "beforeBind.action")
    public Object beforeBind(HttpServletRequest request) {
        ResultObject resultObject = new ResultObject();
        resultObject = this.readSecurityContextFromSession(resultObject);
        if (!"0".equals(resultObject.getCode())) {
            return resultObject;
        }
        String verifcode = request.getParameter("verifcode");
        verifcode = verifcode == null ? "" : verifcode.trim();
        String target = request.getParameter("target");

        try {
            // 验证码
            Party party = this.partyService.cachePartyBy(this.getLoginPartyId(), true);
            SecUser user = secUserService.findUserByPartyId(this.getLoginPartyId());
            if (party == null || user == null) {
                throw new BusinessException("用户不存在");
            }

            String key = target;
            String authcode = this.identifyingCodeTimeWindowService.getAuthCode(key);
            authcode = authcode == null ? "" : authcode;
            String bind_phone_email_ver = this.sysparaService.find("bind_phone_email_ver").getValue();

            // 不是演示用户 && 绑定手机邮箱号是否需要验证：1需要，2不需要
            if (!"GUEST".contentEquals(party.getRolename()) && "1".equals(bind_phone_email_ver)) {
                if (StrUtil.isBlank(authcode) || (!authcode.equals(verifcode))) {
                    resultObject.setCode("1");
                    resultObject.setMsg("验证码不正确");
                    return resultObject;
                }
                this.identifyingCodeTimeWindowService.delAuthCode(key);
            }

//			设置校验token
            String verifyCode = UUIDGenerator.getUUID();
//			设置校验码,并设置失效时间600s
            redisHandler.setSyncStringEx(MallRedisKeys.MALL_MODIFY_BEFORE_VERIFY_TOKEN + this.getLoginPartyId(), verifyCode, 600);
            JSONObject o = new JSONObject();
            o.put("verifyCode", verifyCode);
            resultObject.setData(o);
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
     * 校验手机号或邮箱
     */
    @RequestMapping(action + "checkEmailOrPhone.action")
    public Object checkEmailOrPhone(HttpServletRequest request) {
        String target = request.getParameter("target");
        String verifcode = request.getParameter("verifcode");
        String phone = request.getParameter("phone");
        String email = request.getParameter("email");

        ResultObject resultObject = new ResultObject();
        resultObject = this.readSecurityContextFromSession(resultObject);
        if (!"0".equals(resultObject.getCode())) {
            return resultObject;
        }

        try {
            // 提前当前用户信息
            Party party = this.partyService.cachePartyBy(this.getLoginPartyId(), false);
            logger.info("---> 获取当前party :{}", JSON.toJSONString(party));

            SecUser user = secUserService.findUserByPartyId(this.getLoginPartyId());
            logger.info("---> 获取当前user :{}", JSON.toJSONString(user));
            if (party == null || user == null) {
                throw new BusinessException("用户不存在");
            }
            // 拿到调用接口：/api/idcode!execute.action 产生的验证码
            String key = target;
            String authcode = this.identifyingCodeTimeWindowService.getAuthCode(key);

            String bind_phone_email_ver = this.sysparaService.find("bind_phone_email_ver").getValue();

            // 不是演示用户 && 绑定手机邮箱号是否需要验证：1需要，2不需要
            if (!"GUEST".contentEquals(party.getRolename()) && "1".equals(bind_phone_email_ver)) {
                if ((null == authcode) || (!authcode.equals(verifcode))) {
                    resultObject.setCode("1");
                    resultObject.setMsg("验证码不正确");
                    return resultObject;
                }
            }
            this.identifyingCodeTimeWindowService.delAuthCode(key);

            if (target.indexOf("@") == -1) {
                // 标记为通过验证状态
                party.setPhone_authority(true);

                // 获取用户系统等级：1/新注册；2/邮箱谷歌手机其中有一个已验证；3/用户实名认证； 4/用户高级认证；
                int userLevelSystem = this.partyService.getUserLevelByAuth(party);

                // 十进制个位表示系统级别：1/新注册；2/邮箱谷歌手机其中有一个已验证；3/用户实名认证；4/用户高级认证；
                // 十进制十位表示自定义级别：对应在前端显示为如VIP1 VIP2等级、黄金 白银等级；
                // 如：级别11表示：新注册的前端显示为VIP1；
                int userLevel = party.getUser_level();
                party.setUser_level(((int) Math.floor(userLevel / 10)) * 10 + userLevelSystem);

                this.partyService.update(party);
            } else {
                // 邮箱校验成功
                party.setEmail_authority(true);

                // 获取用户等级 1/新注册；2/邮箱谷歌手机其中有一个已验证；3/用户实名认证； 4/用户高级认证；
                party.setUser_level(this.partyService.getUserLevelByAuth(party));

                this.partyService.update(party);
            }
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
     * 电话绑定
     */
    @RequestMapping(action + "save_phone.action")
    public Object save_phone(HttpServletRequest request) {
        String phone = request.getParameter("phone");
        String verifcode = request.getParameter("verifcode");
        String usercode = request.getParameter("usercode");

        ResultObject resultObject = new ResultObject();
        resultObject = this.readSecurityContextFromSession(resultObject);
        if (!"0".equals(resultObject.getCode())) {
            return resultObject;
        }

        try {

//			if (StringUtils.isEmptyString(phone) || !Strings.isNumber(phone) || phone.length() > 15) {
            if (StringUtils.isEmptyString(phone) || phone.length() > 20) {
                throw new BusinessException("请填写正确的电话号码");
            }

            String loginPartyId = this.getLoginPartyId();
            Party party = this.partyService.cachePartyBy(loginPartyId, false);

            if (null != party.getPhone() && party.getPhone().equals(phone) && true == party.getPhone_authority()) {
                throw new BusinessException("电话号码已绑定");
            }

            Party partyPhone = this.partyService.findPartyByVerifiedPhone(phone);
            if (null != partyPhone && !partyPhone.getId().toString().equals(loginPartyId)) {
                throw new BusinessException("电话号码已绑定其他用户");
            }

            String authcode = this.identifyingCodeTimeWindowService.getAuthCode(phone);

            //String bind_phone_email_ver = this.sysparaService.find("bind_phone_email_ver").getValue();
            //String bind_usercode = this.sysparaService.find("bind_usercode").getValue();

            // 如果是演示用户，则不判断验证码
            if (!"GUEST".contentEquals(party.getRolename())) {

                if (StringUtils.isEmptyString(verifcode)) {
                    throw new BusinessException("请填写正确的验证码");
                }

                if ((null == authcode) || (!authcode.equals(verifcode))) {
                    throw new BusinessException("验证码不正确");
                }

//				if ("1".contentEquals(bind_phone_email_ver)) {
//
//					if (StringUtils.isEmptyString(verifcode)) {
//						throw new BusinessException("请填写正确的验证码");
//					}
//
//					if ((null == authcode) || (!authcode.equals(verifcode))) {
//						throw new BusinessException("验证码不正确");
//					}
//				}
//
//				if ("1".contentEquals(bind_usercode)) {
//
//					if (StringUtils.isEmptyString(usercode)) {
//						throw new BusinessException("推荐码不正确");
//					}
//
//					Party party_reco = this.partyService.findPartyByUsercode(usercode);
//					if (null == party_reco || !party_reco.getEnabled()) {
//						throw new BusinessException("推荐人无权限推荐");
//					}
//
//					UserRecom userRecom = this.userRecomService.findByPartyId(party.getId());
//					if (null == userRecom) {
//						userRecom = new UserRecom();
//						userRecom.setPartyId(party.getId());
//						userRecom.setReco_id(party_reco.getId());
//						this.userRecomService.save(userRecom);
//					} else {
////						this.userRecomService.update(party.getId(), party_reco.getId());
//					}
//				}
            }

            // 电话绑定成功
            party.setPhone(phone);
            party.setPhone_authority(true);

            // 获取用户系统等级：1/新注册；2/邮箱谷歌手机其中有一个已验证；3/用户实名认证； 4/用户高级认证；
            int userLevelSystem = this.partyService.getUserLevelByAuth(party);

            // 十进制个位表示系统级别：1/新注册；2/邮箱谷歌手机其中有一个已验证；3/用户实名认证；4/用户高级认证；
            // 十进制十位表示自定义级别：对应在前端显示为如VIP1 VIP2等级、黄金 白银等级；
            // 如：级别11表示：新注册的前端显示为VIP1；
            int userLevel = party.getUser_level();
            party.setUser_level(((int) Math.floor(userLevel / 10)) * 10 + userLevelSystem);

            this.partyService.update(party);

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
     * 邮箱绑定
     */
    @RequestMapping(action + "save_email.action")
    public Object save_email(HttpServletRequest request) {
        String email = request.getParameter("email");
        String verifcode = request.getParameter("verifcode");

        ResultObject resultObject = new ResultObject();
        resultObject = this.readSecurityContextFromSession(resultObject);
        if (!"0".equals(resultObject.getCode())) {
            return resultObject;
        }

        try {

            if (StringUtils.isEmptyString(email) || !Strings.isEmail(email)) {
                throw new BusinessException("请填写正确的邮箱地址");
            }

            String loginPartyId = this.getLoginPartyId();
            Party party = this.partyService.cachePartyBy(loginPartyId, false);

            if (null != party.getEmail() && party.getEmail().equals(email) && true == party.getEmail_authority()) {
                throw new BusinessException("邮箱已绑定");
            }

            Party partyEmail = this.partyService.findPartyByVerifiedEmail(email);
            if (null != partyEmail && !partyEmail.getId().toString().equals(loginPartyId)) {
                throw new BusinessException("邮箱已绑定其他用户");
            }

            String authcode = this.identifyingCodeTimeWindowService.getAuthCode(email);

            //String bind_phone_email_ver = sysparaService.find("bind_phone_email_ver").getValue();

            // 如果是演示用户，则不判断验证码
            if (!"GUEST".contentEquals(party.getRolename())) {
                if (StringUtils.isEmptyString(verifcode)) {
                    throw new BusinessException("请填写正确的验证码");
                }

                if ((null == authcode) || (!authcode.equals(verifcode))) {
                    resultObject.setCode("1");
                    resultObject.setMsg("验证码不正确");
                    return resultObject;
                }
            }

            // 邮箱绑定成功
            party.setEmail(email);
            party.setEmail_authority(true);

            // 获取用户系统等级：1/新注册；2/邮箱谷歌手机其中有一个已验证；3/用户实名认证； 4/用户高级认证；
            int userLevelSystem = this.partyService.getUserLevelByAuth(party);

            // 十进制个位表示系统级别：1/新注册；2/邮箱谷歌手机其中有一个已验证；3/用户实名认证；4/用户高级认证；
            // 十进制十位表示自定义级别：对应在前端显示为如VIP1 VIP2等级、黄金 白银等级；
            // 如：级别11表示：新注册的前端显示为VIP1；
            int userLevel = party.getUser_level();
            party.setUser_level(((int) Math.floor(userLevel / 10)) * 10 + userLevelSystem);

            this.partyService.update(party);

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
     * 获取我的分享信息
     */
    @RequestMapping(action + "getShare.action")
    public Object getShare() {

        ResultObject resultObject = new ResultObject();
        resultObject = readSecurityContextFromSession(resultObject);

        if (!"0".equals(resultObject.getCode())) {
            return resultObject;
        }
        try {
            Party party = this.partyService.cachePartyBy(getLoginPartyId(), false);
            // 关闭后，正式用户进入推广页面的时候，接口就不返回内容
            boolean member_promote_button = sysparaService.find("member_promote_button").getBoolean();

            Kyc kyc = kycService.get(party.getId().toString());
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("username", party.getUsername());
            map.put("userrole", party.getRolename());
            map.put("usercode", party.getUsercode());

            // 十进制个位表示系统级别：1/新注册；2/邮箱谷歌手机其中有一个已验证；3/用户实名认证；4/用户高级认证；
            // 十进制十位表示自定义级别：对应在前端显示为如VIP1 VIP2等级、黄金 白银等级；
            // 如：级别11表示：新注册的前端显示为VIP1；
            map.put("user_level", (int) (party.getUser_level() % 10));
            map.put("user_level_custom", (int) Math.floor(party.getUser_level() / 10));
            map.put("user_level_custom_display", "VIP");
            String user_level_custom_config = this.sysparaService.find("user_level_custom_config").getValue();
            String[] levelArray = user_level_custom_config.split(",");
            for (int i = 0; i < levelArray.length; i++) {
                String[] level = levelArray[i].split("-");
                if (level[0].equals(map.get("user_level_custom").toString())) {
                    map.put("user_level_custom_display", level[1]);
                    break;
                }
            }

            String str = Constants.WEB_URL.substring(0, Constants.WEB_URL.indexOf("/wap"));
            if (party.getKyc_authority())
                map.put("name", kyc.getName());
            if (Constants.SECURITY_ROLE_TEST.equals(party.getRolename())) {
                map.put("test", true);

            } else {
                if (Constants.SECURITY_ROLE_MEMBER.equals(party.getRolename()) && !member_promote_button) {
                    map.put("url", "");
                    map.put("usercode_qr", "");
                } else {

                    map.put("url", str + "/?usercode=" + party.getUsercode());
                    /**
                     * 生成二维码图片
                     */
                    qRGenerateService.generate(party.getUsercode());

                    map.put("usercode_qr", Constants.WEB_URL + "/public/showimg!showImg.action?imagePath=/qr/"
                            + party.getUsercode() + ".png");
                }
            }

            resultObject.setData(map);
        } catch (Exception e) {
            resultObject.setCode("1");
            resultObject.setMsg("程序错误");
        }
        return resultObject;
    }

    @RequestMapping(action + "getImageCode.action")
    public Object getImageCode() {
        ResultObject resultObject = new ResultObject();
        try {
            Map<String, Object> data = new HashMap<String, Object>();
            String key = UUIDGenerator.getUUID();
            ImageVerificationCodeUtil iv = new ImageVerificationCodeUtil();
            data.put("code", iv.getBase64());
            data.put("key", key);
            resultObject.setData(data);
            userService.putImageCode(key, iv.getText());
        } catch (BusinessException e) {
            resultObject.setCode("1");
            resultObject.setMsg(e.getMessage());
        } catch (Throwable t) {
            logger.error("UserAction.register error ", t);
            resultObject.setCode("1");
            resultObject.setMsg("[ERROR] " + t.getMessage());
        }

        return resultObject;
    }

    /**
     * 获取个人信息
     */
    @RequestMapping(action + "get.action")
    public Object get(HttpServletRequest request) {

        ResultObject resultObject = new ResultObject();
        resultObject = this.readSecurityContextFromSession(resultObject);
        if (!"0".equals(resultObject.getCode())) {
            return resultObject;
        }

        try {

            String loginPartyId = this.getLoginPartyId();
            Party party = this.partyService.cachePartyBy(loginPartyId, false);
            SecUser secUser = this.secUserService.findUserByPartyId(loginPartyId);
            Kyc kyc = this.kycService.get(party.getId().toString());
            KycHighLevel kycHighLevel = this.kycHighLevelService.get(party.getId().toString());

            Seller seller = sellerService.getSeller(party.getId().toString());

            Map<String, Object> map = new HashMap<String, Object>();

            // 十进制个位表示系统级别：1/新注册；2/邮箱谷歌手机其中有一个已验证；3/用户实名认证；4/用户高级认证；
            // 十进制十位表示自定义级别：对应在前端显示为如VIP1 VIP2等级、黄金 白银等级；
            // 如：级别11表示：新注册的前端显示为VIP1；
            //map.put("user_level", (int) (party.getUser_level() % 10));
            //map.put("user_level_custom", (int) Math.floor(party.getUser_level() / 10));

//			String projectType = this.sysparaService.find("project_type").getValue();
//			if (StringUtils.isEmptyString(projectType)) {
//				throw new BusinessException("系统参数错误");
//			}
//			if (projectType.equals("DAPP_EXCHANGE_BINANCE")) {
//				map.put("user_level_custom_display", map.get("user_level_custom"));
//			} else {
//				map.put("user_level_custom_display", "VIP");
//				String user_level_custom_config = this.sysparaService.find("user_level_custom_config").getValue();
//				String[] levelArray = user_level_custom_config.split(",");
//				for (int i = 0; i < levelArray.length; i++) {
//					String[] level = levelArray[i].split("-");
//					if (level[0].equals(map.get("user_level_custom").toString())) {
//						map.put("user_level_custom_display", level[1]);
//						break;
//					}
//				}
//			}
            if(!party.getUsername().contains("@") && !party.getUsername().startsWith("+")){
                party.setUsername("+"+party.getUsername());
            }
            map.put("username", party.getUsername());
            map.put("userrole", party.getRolename());
            map.put("roletype", party.getRoleType());
            map.put("usercode", party.getUsercode());
            map.put("phone", party.getPhone());
            map.put("phoneverif", party.getPhone_authority());
            map.put("email", party.getEmail());
            map.put("emailverif", party.getEmail_authority());
            map.put("google_auth_secret", secUser.getGoogle_auth_secret());
            map.put("googleverif", secUser.isGoogle_auth_bind());
            map.put("identityverif", party.getKyc_authority());
            map.put("advancedverif", party.isKyc_highlevel_authority());
            map.put("lastlogintime", party.getLast_loginTime());
            map.put("lastloginip", party.getLogin_ip());
            map.put("avatar", party.getAvatar());
            if (StrUtil.isBlank(party.getAvatar())) {
                map.put("avatar", "1");
            }

            // 实名认证通过返回真实姓名
            if (party.getKyc_authority()) {
                map.put("name", kyc.getName());
            }

            if (null != kyc) {
                map.put("nationality", kyc.getNationality());
                map.put("kyc_status", kyc.getStatus());
            }

            if (Objects.nonNull(seller)) {
                map.put("signPdfUrl", seller.getSignPdfUrl());
            }

            if (null != kycHighLevel) {
                map.put("kyc_high_level_status", kycHighLevel.getStatus());
            }

            if (Constants.SECURITY_ROLE_TEST.equals(party.getRolename())) {
                map.put("test", true);
            } else {

                // 关闭后，正式用户进入推广页面的时候，接口就不返回内容
                boolean member_promote_button = this.sysparaService.find("member_promote_button").getBoolean();

                if (Constants.SECURITY_ROLE_MEMBER.equals(party.getRolename()) && !member_promote_button) {
                    map.put("url", "");
                    map.put("usercode_qr", "");
                } else {
                    map.put("url", Constants.WEB_URL + "/register.html?usercode=" + party.getUsercode());
                    // 生成二维码图片
                    qRGenerateService.generate(party.getUsercode());
                    map.put("usercode_qr", Constants.WEB_URL + "/public/showimg!showImg.action?imagePath=/qr/" + party.getUsercode() + ".png");
                }
            }

            String partySafeword = party.getSafeword();
            int safeword = 0;
            if (StringUtils.isNotEmpty(partySafeword)) {
                safeword = 1;
            }
            map.put("safeword", safeword);

//			UserRecom userRecom = this.userRecomService.findByPartyId(party.getId());
//			if (null == userRecom || null == userRecom.getReco_id()) {
//				map.put("usercode_parent", "");
//			} else {
//				Party party_reco = this.partyService.cachePartyBy(userRecom.getReco_id(), false);
//				if (null == party_reco || null == party_reco.getUsercode() || StringUtils.isEmptyString(party_reco.getUsercode().toString())) {
//					map.put("usercode_parent", "");
//				} else {
//					map.put("usercode_parent", party_reco.getUsercode());
//				}
//			}
//
//			// 时间分隔点：新用户需要注册后填手机号和邀请码
//			map.put("register_need_phone_usercode", false);
//			String register_need_phone_usercode_time = this.sysparaService.find("register_need_phone_usercode_time").getValue();
//			if (!StringUtils.isEmptyString(register_need_phone_usercode_time)) {
//
//				// 结合盘：只有新用户需要注册后填手机号和邀请码
//				Date dateFixed = DateUtils.toDate(register_need_phone_usercode_time, DateUtils.NORMAL_DATE_FORMAT);
//				if (party.getCreateTime().getTime() > dateFixed.getTime()) {
//					map.put("register_need_phone_usercode", true);
//				} else {
//					map.put("register_need_phone_usercode", false);
//				}
//			}
//
//			//承兑商类型：0不是承兑商/1后台承兑商/2用户承兑商
//			map.put("c2c_user_type", party.getC2cUserType());

            resultObject.setData(map);

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
     * 电话绑定
     */
    @PostMapping(action + "refreshAvatar.action")
    public Object refreshAvatar(HttpServletRequest request) {
        String idxStr = request.getParameter("idx");

        ResultObject resultObject = new ResultObject();
        resultObject = this.readSecurityContextFromSession(resultObject);
        if (!"0".equals(resultObject.getCode())) {
            return resultObject;
        }

        try {
            int idx = 0;
            try {
                idx = Integer.parseInt(idxStr);
            } catch (Exception e) {
                throw new BusinessException("图片下标应该是数字");
            }

            String loginPartyId = this.getLoginPartyId();
            Party party = this.partyService.cachePartyBy(loginPartyId, false);

            // 更新图标
            party.setAvatar(idxStr);
            this.partyService.update(party);
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

}
