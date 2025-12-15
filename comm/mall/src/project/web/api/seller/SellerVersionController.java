package project.web.api.seller;

import com.alibaba.fastjson.JSONObject;
import ext.Types;
import kernel.exception.BusinessException;
import kernel.util.StringUtils;
import kernel.web.BaseAction;
import kernel.web.ResultObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import project.Constants;
import project.ddos.IpMenuService;
import project.log.LogService;
import project.mall.orders.GoodsOrdersService;
import project.mall.seller.SellerService;
import project.mall.seller.model.Seller;
import project.mall.version.MallClientSeller;
import project.party.PartyService;
import project.party.model.Party;
import project.syspara.Syspara;
import project.syspara.SysparaService;
import project.tip.TipConstants;
import project.tip.TipService;
import project.user.LocalNormalReg;
import project.user.LocalUserService;
import project.user.UserDataService;
import project.user.UserService;
import project.user.idcode.IdentifyingCodeTimeWindowService;
import project.user.kyc.Kyc;
import project.user.kyc.KycService;
import project.user.token.TokenService;
import security.SecUser;
import security.internal.SecUserService;
import util.LockFilter;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin
public class SellerVersionController extends BaseAction {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final String action = "/seller/version!";

    @Resource
    private GoodsOrdersService goodsOrdersService;

    @Resource
    private LocalUserService localUserService;
    @Resource
    private SecUserService secUserService;
    @Resource
    private LogService logService;

    @Resource
    private UserService userService;

    @Resource
    protected TokenService tokenService;
    @Resource
    private IpMenuService ipMenuService;
    @Resource
    private KycService kycService;
    @Resource
    private TipService tipService;
    @Resource
    private PartyService partyService;

    @Resource
    protected SellerService sellerService;
    @Resource
    private UserDataService userDataService;
    @Resource
    private IdentifyingCodeTimeWindowService identifyingCodeTimeWindowService;

    @Resource
    private SysparaService sysparaService;
    /**
     * 商户客户端信息
     *
     * @return
     */
    @PostMapping(action + "client.action")
    public Object client(HttpServletRequest request) {

        ResultObject resultObject = new ResultObject();
        if (!"0".equals(resultObject.getCode())) {
            return resultObject;
        }

        JSONObject object = new JSONObject();

        object.put("plantform", "0");

        String id = request.getParameter("plantform");
        if (id == null || !id.equals("1") && !id.equals("2")) {
            resultObject.setData(object);
            return resultObject;
        }

        String lang = request.getParameter("lang");
        if (lang == null) {
            lang = "en";
        }

        MallClientSeller client = goodsOrdersService.getMallClientSeller(id + lang);
        if (client == null) {
            resultObject.setData(object);
            return resultObject;
        }

        object.put("plantform", id);
        object.put("latestVersion", client.getLatestVersion());
        object.put("title", client.getTitle());
        object.put("content", client.getContent());
        object.put("downloadlink", client.getDownloadlink());
        object.put("status", client.getStatus());
        resultObject.setData(object);
        return resultObject;

    }

    /**
     * 店铺注册
     */
    @RequestMapping(action + "register.action")
    public Object apply(HttpServletRequest request) throws IOException {
        String action = Types.orValue(request.getParameter("action"),"");
        if(action.equals("check")){
            return this.checkRegister(request);
        }
        String idimg_1 = request.getParameter("idimg_1");
        String idimg_2 = request.getParameter("idimg_2");
        String idimg_3 = request.getParameter("idimg_3");
//        String idimg_1 = "";
//                String idimg_2 = "";
//                String idimg_3 = "";
        String idname = request.getParameter("idname");//ID名称，如身份证等
        String name = request.getParameter("name");//实名姓名
        String idnumber = request.getParameter("idnumber");//证件号码
        String nationality = request.getParameter("nationality");//国际
        String sellerImg = request.getParameter("sellerImg");//店铺  logo
        String sellerName = request.getParameter("sellerName");//店铺  名字
        String sellerAddress = request.getParameter("sellerAddress");//店铺  地址
//        String whatsApp = request.getParameter("whatsApp");//whatsApp
        String whatsApp = "";
        String signPdfUrl = request.getParameter("signPdfUrl");

//        手机号注册时，如果有区号，要回显时修改用户表中phone字段为 区号+空格+手机号
        String usernameStr = request.getParameter("username");
        String username = request.getParameter("username").replace(" ", "");
        String password = request.getParameter("password").replace(" ", "");
        String re_password = request.getParameter("re_password").replace(" ", "");
        // 邀请码
        String usercode = request.getParameter("usercode");
        String verifyCode = request.getParameter("verifyCode");
        // 注册类型：1/手机；2/邮箱；3/用户名；
        String type = request.getParameter("type");


        String phoneStr = request.getParameter("phone");//注册时用户表中手机号字段保存空格


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
            if (StringUtils.isEmptyString(name)) {
                throw new BusinessException("实名姓名不能为空");
            }

            if (StringUtils.isEmptyString(usercode)) {
                throw new BusinessException("邀请码不能为空");
            }

//            idname = URLDecoder.decode(idname, "utf-8");
//            name = URLDecoder.decode(name, "utf-8");


            // 校验验证码
            String authcode = null;
            if ("1".equals(type)) {
                Syspara sp = sysparaService.find("mall_seller_require_phone_code");
                if(sp != null && sp.getValue().equals("1")) {
                    authcode = this.identifyingCodeTimeWindowService.getAuthCode(phoneStr.replaceAll("\\s", ""));
                    if ((null == authcode) || (!authcode.equals(verifyCode))) {
                        resultObject.setCode("1");
                        resultObject.setMsg("验证码不正确");
                        return resultObject;
                    }
                    this.identifyingCodeTimeWindowService.delAuthCode(usernameStr);
                }
            } else {
                Syspara sp = sysparaService.find("mall_seller_require_email_code");
                if(sp != null && sp.getValue().equals("1")) {
                    authcode = this.identifyingCodeTimeWindowService.getAuthCode(username.replaceAll("\\s", ""));
                    if ((null == authcode) || (!authcode.equals(verifyCode))) {
                        resultObject.setCode("1");
                        resultObject.setMsg("验证码不正确");
                        return resultObject;
                    }
                    this.identifyingCodeTimeWindowService.delAuthCode(usernameStr);
                }
            }

            Seller existSeller = sellerService.getByName(sellerName);
            if (existSeller != null) {
                throw new BusinessException("已存在同名商铺");
            }
            if(this.kycService.isExistsIdNumber(idnumber, "")){
                throw new BusinessException("身份信息已经存在");
            }

            LocalNormalReg reg = new LocalNormalReg();
            reg.setUsername(username);
            reg.setPassword(password);
            //reg.setSafeword("000000");
            reg.setReco_usercode(usercode);
            reg.setRoleType(1);

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
            party.setRoleType(1);
            if ("1".equals(type)) {//如果手机号注册，将手机号码设置成 区号+空格+手机号
                party.setPhone(usernameStr.trim());
            }
            if (StringUtils.isNotEmpty(whatsApp)) {
                party.setWhatsApp(whatsApp);
            }
            this.partyService.update(party);

            this.userDataService.saveRegister(party.getId());

            Kyc entity = new Kyc();
            entity.setPartyId(secUser.getPartyId());
            entity.setStatus(1);
            entity.setIdimg_1(idimg_1);
            entity.setIdimg_2(idimg_2);
            entity.setIdimg_3(idimg_3);
            entity.setIdname(idname);
            entity.setIdnumber(idnumber);
            entity.setName(name);
            entity.setNationality(nationality);
            entity.setSex("");
            entity.setBorth_date("");
            entity.setSellerImg(sellerImg);
            entity.setInvitationCode(usercode);
            entity.setSellerName(sellerName);
            entity.setSellerAddress(sellerAddress);
            this.kycService.save(entity);


            Seller seller = new Seller();
            seller.setId(entity.getPartyId());
            seller.setName(entity.getSellerName());
            seller.setAvatar(entity.getSellerImg());
            seller.setCreateTime(new Date());
            seller.setRecTime(0L);
            seller.setStatus(0);
            seller.setCreditScore(0);
            seller.setSignPdfUrl(signPdfUrl);

            //店铺流量默认值
            seller.setAutoStart(0);
            seller.setAutoEnd(5);
            seller.setBaseTraffic(1);
            seller.setAutoValid(1);
            // 设置商家默认banner
            seller.setBanner1("");
            seller.setBanner2("");
            seller.setBanner3("");
            sellerService.saveSeller(seller);

            if (Constants.SECURITY_ROLE_MEMBER.equals(party.getRolename())) {
                this.tipService.saveTip(entity.getId().toString(), TipConstants.KYC);
            }

            resultObject.setData(data);

        } catch (BusinessException e) {
            resultObject.setCode("1");
            resultObject.setMsg(e.getMessage());
        } catch (Throwable t) {
            logger.error("UserAction.register error ", t);
            resultObject.setCode("1");
            resultObject.setMsg("程序错误");
        } finally {
            if (lock) {
                LockFilter.remove(username);
            }
        }

        return resultObject;

    }

    private Object checkRegister(HttpServletRequest request) {
//      手机号注册时，如果有区号，要回显时修改用户表中phone字段为 区号+空格+手机号
        String usernameStr = request.getParameter("username");
        String username = request.getParameter("username").replace(" ", "");
        String password = request.getParameter("password").replace(" ", "");
        String re_password = request.getParameter("re_password").replace(" ", "");
        // 邀请码
        String usercode = request.getParameter("usercode");
        String verifyCode = request.getParameter("verifyCode");
        // 注册类型：1/手机；2/邮箱；3/用户名；
        String type = request.getParameter("type");
        String phoneStr = request.getParameter("phone");//注册时用户表中手机号字段保存空格
        ResultObject resultObject = new ResultObject();

        try {

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

            if (StringUtils.isEmptyString(usercode)) {
                throw new BusinessException("邀请码不能为空");
            }

            // 校验验证码
            String authcode = null;
            if ("1".equals(type)) {
                Syspara sp = sysparaService.find("mall_seller_require_phone_code");
                if (sp != null && sp.getValue().equals("1")) {
                    authcode = this.identifyingCodeTimeWindowService.getAuthCode(phoneStr.replaceAll("\\s", ""));
                    if ((null == authcode) || (!authcode.equals(verifyCode))) {
                        resultObject.setCode("1");
                        resultObject.setMsg("验证码不正确");
                        return resultObject;
                    }
                }
            } else {
                Syspara sp = sysparaService.find("mall_seller_require_email_code");
                if (sp != null && sp.getValue().equals("1")) {
                    authcode = this.identifyingCodeTimeWindowService.getAuthCode(username.replaceAll("\\s", ""));
                    if ((null == authcode) || (!authcode.equals(verifyCode))) {
                        resultObject.setCode("1");
                        resultObject.setMsg("验证码不正确");
                        return resultObject;
                    }
                }
            }
            this.localUserService.checkRegister(type,username,usercode);
        } catch (Exception e) {
           resultObject.setCode("1");
           resultObject.setMsg(e.getMessage());
        }
        return resultObject;
    }

    /**
     * justShop Argos2商家入驻店铺注册
     */
    @RequestMapping(action + "registerjs.action")
    public Object applyjs(HttpServletRequest request) throws IOException {
        String idimg_1 = request.getParameter("idimg_1");
        String idimg_2 = request.getParameter("idimg_2");
        String idimg_3 = request.getParameter("idimg_3");
//        String idimg_1 = "";
//        String idimg_2 = "";
//        String idimg_3 = "";
        String idname = request.getParameter("idname");//ID名称，如身份证等
        String name = request.getParameter("name");//实名姓名
        String idnumber = request.getParameter("idnumber");//证件号码
        String nationality = request.getParameter("nationality");//国际
        String sellerImg = request.getParameter("sellerImg");//店铺  logo
        String sellerName = request.getParameter("sellerName");//店铺  名字
        String sellerAddress = request.getParameter("sellerAddress");//店铺  地址
        String signPdfUrl = request.getParameter("signPdfUrl");
        String verifcode = request.getParameter("verifcode");
//        String whatsApp = request.getParameter("whatsApp");//whatsApp
        String whatsApp = "";

//        justShop的注册修改为username固定为邮箱，手机号也是必填的 Argos也是如此，只不过校验使用手机号校验
        String usernameStr = request.getParameter("username");
        String phoneStr = request.getParameter("phone");//注册时用户表中手机号字段保存空格
        String username = request.getParameter("username").replace(" ", "");
        String password = request.getParameter("password").replace(" ", "");
        String re_password = request.getParameter("re_password").replace(" ", "");
        String usercode = request.getParameter("usercode");
        // 校验类型：1/手机；2/邮箱；
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
            if (StringUtils.isEmptyString(name)) {
                throw new BusinessException("实名姓名不能为空");
            }

            if (StringUtils.isEmptyString(usercode)) {
                throw new BusinessException("邀请码不能为空");
            }

            idname = URLDecoder.decode(idname, "utf-8");
            name = URLDecoder.decode(name, "utf-8");

//            校验验证码
            String authcode = null;
            if ("1".equals(type)) {
//                Argos2商家入驻使用手机号校验
                authcode = this.identifyingCodeTimeWindowService.getAuthCode(phoneStr.replaceAll("\\s", ""));
                if ((null == authcode) || (!authcode.equals(verifcode))) {
                    resultObject.setCode("1");
                    resultObject.setMsg("验证码不正确");
                    return resultObject;
                }
                this.identifyingCodeTimeWindowService.delAuthCode(usernameStr);
            } else {
//                JustShop商家入驻使用邮箱校验  2023-09-27 新增需求去除邮箱校验
//                authcode = this.identifyingCodeTimeWindowService.getAuthCode(usernameStr);
            }


            Seller existSeller = sellerService.getByName(sellerName);
            if (existSeller != null) {
                throw new BusinessException("已存在同名商铺");
            }

            LocalNormalReg reg = new LocalNormalReg();
            reg.setUsername(username);
            reg.setPassword(password);
            reg.setPhone(phoneStr);
            reg.setReco_usercode(usercode);
            reg.setRoleType(1);
            reg.setWhatsApp(whatsApp);
            this.localUserService.saveRegisterNoVerifcodeJs(reg, type);

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
            party.setRoleType(1);
//            if ("1".equals(type)) {//如果手机号注册，将手机号码设置成 区号+空格+手机号
//                party.setPhone(usernameStr.trim());
//            }
            this.partyService.update(party);

            this.userDataService.saveRegister(party.getId());

            Kyc entity = new Kyc();
            entity.setPartyId(secUser.getPartyId());
            entity.setStatus(1);
            entity.setIdimg_1(idimg_1);
            entity.setIdimg_2(idimg_2);
            entity.setIdimg_3(idimg_3);
            entity.setIdname(idname);
            entity.setIdnumber(idnumber);
            entity.setName(name);
            entity.setNationality(nationality);
            entity.setSex("");
            entity.setBorth_date("");
            entity.setSellerImg(sellerImg);
            entity.setInvitationCode(usercode);
            entity.setSellerName(sellerName);
            entity.setSellerAddress(sellerAddress);
            this.kycService.save(entity);


            Seller seller = new Seller();
            seller.setId(entity.getPartyId());
            seller.setName(entity.getSellerName());
            seller.setAvatar(entity.getSellerImg());
            seller.setCreateTime(new Date());
            seller.setRecTime(0L);
            seller.setStatus(0);
            seller.setCreditScore(0);
            seller.setSignPdfUrl(signPdfUrl);

            //店铺流量默认值
            seller.setAutoStart(0);
            seller.setAutoEnd(5);
            seller.setBaseTraffic(1);
            seller.setAutoValid(1);
            // 设置商家默认banner
            seller.setBanner1("");
            seller.setBanner2("");
            seller.setBanner3("");
            sellerService.saveSeller(seller);

            if (Constants.SECURITY_ROLE_MEMBER.equals(party.getRolename())) {
                this.tipService.saveTip(entity.getId().toString(), TipConstants.KYC);
            }

            resultObject.setData(data);

        } catch (BusinessException e) {
            resultObject.setCode("1");
            resultObject.setMsg(e.getMessage());
        } catch (Throwable t) {
            logger.error("UserAction.register error ", t);
            resultObject.setCode("1");
            resultObject.setMsg("程序错误");
        } finally {
            if (lock) {
                LockFilter.remove(username);
            }
        }

        return resultObject;

    }

    /**
     * 更新店铺签名信息
     */
    @RequestMapping(action + "updateSignPdf.action")
    public Object updateSignPdf(HttpServletRequest request) {

        ResultObject resultObject = readSecurityContextFromSession(new ResultObject());
        String signPdfUrl = request.getParameter("signPdfUrl");

        try {
            String loginPartyId = super.getLoginPartyId();
            Seller seller = sellerService.getSeller(loginPartyId);
            seller.setSignPdfUrl(signPdfUrl);
            sellerService.updateSeller(seller);
            resultObject.setData("success");
        } catch (BusinessException e) {
            resultObject.setCode("1");
            resultObject.setMsg(e.getMessage());
        } catch (Throwable t) {
            logger.error("seller action sign error ", t);
            resultObject.setCode("1");
            resultObject.setMsg("商户签名错误");
        }
        return resultObject;
    }

}
