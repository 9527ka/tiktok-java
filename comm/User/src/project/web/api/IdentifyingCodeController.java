package project.web.api;

import com.mysql.cj.util.StringUtils;
import kernel.exception.BusinessException;
import kernel.web.BaseAction;
import kernel.web.ResultObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import project.mall.MallRedisKeys;
import project.party.PartyService;
import project.redis.RedisHandler;
import project.syspara.SysparaService;
import project.user.idcode.IdentifyingCodeService;
import project.user.token.TokenService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

/**
 * 邮箱或手机绑定
 * 发送验证码
 */
@RestController
@CrossOrigin
public class IdentifyingCodeController extends BaseAction {

    @Autowired
    private IdentifyingCodeService identifyingCodeService;
    @Autowired
    private SysparaService sysparaService;
    @Autowired
    private RedisHandler redisHandler;
    @Autowired
    private TokenService tokenService;
    @Resource
    private PartyService partyService;

    @RequestMapping("/api/idcode!execute.action")
    public Object execute(HttpServletRequest request) {
        ResultObject resultObject = new ResultObject();
        resultObject = this.readSecurityContextFromSession(resultObject);
        if (!"0".equals(resultObject.getCode())) {
            return resultObject;
        }
        try {
            // 邮件或手机号
            String target = request.getParameter("target");
            String token = request.getParameter("token");
            String partyId = tokenService.cacheGet(token);
            if (kernel.util.StringUtils.isNullOrEmpty(partyId)) {
                resultObject.setCode("403");
                resultObject.setMsg("请重新登录");
                return resultObject;
            }
            if (StringUtils.isNullOrEmpty(target)) {
                throw new BusinessException("不能为空");
            }
            String verifyTimes = "";
            String verifyTimesKey = "";
            boolean type = false;//默认false是邮箱，ture为电话类型
            if (target.contains("@")) {
                verifyTimes = sysparaService.find("mall_modify_email_verify_times").getValue();
                verifyTimesKey = MallRedisKeys.MALL_EMAIL_VERIFY_TIME_LOCK + partyId;
            } else {
                verifyTimes = sysparaService.find("mall_modify_phone_verify_times").getValue();
                verifyTimesKey = MallRedisKeys.MALL_PHONE_VERIFY_TIME_LOCK + partyId;
                type = true;
            }

            if (Objects.isNull(verifyTimes)) {
                logger.error("mall_modify_email_verify_times 或者 mall_modify_phone_verify_times 系统参数未配置！");
                throw new BusinessException("参数异常");
            }
            int needSeconds = util.DateUtils.getTomorrowStartSeconds();
            boolean exit = redisHandler.exists(verifyTimesKey);//是否已经发送过
            if (exit && ("true".equals(redisHandler.getString(verifyTimesKey)))) {//已发送过且次数已经超过verifyTimes配置的次数
                if (type) {
                    throw new BusinessException(1, "手机验证码发送次数过多，请明天再试");
                } else {
                    throw new BusinessException(1, "邮箱验证码发送次数过多，请明天再试");
                }
            } else if (exit && verifyTimes.equals(redisHandler.getString(verifyTimesKey))) {//已经发送过且次数刚好等于verifyTimes配置的次数
                redisHandler.setSyncStringEx(verifyTimesKey, "true", needSeconds);
                if (type) {
                    throw new BusinessException(1, "手机验证码发送次数过多，请明天再试");
                } else {
                    throw new BusinessException(1, "邮箱验证码发送次数过多，请明天再试");
                }
            } else {//发送次数小于配置次数
                identifyingCodeService.addSend(target, this.getIp());
                redisHandler.incr(verifyTimesKey);
            }
        } catch (BusinessException e) {
            resultObject.setCode("1");
            resultObject.setMsg(e.getMessage());
        }
        return resultObject;
    }

    /**
     * justShop Argos2商家入驻发送验证码
     *
     * @param request
     * @return
     */
    @RequestMapping("/api/jscode!execute.action")
    public Object executeJs(HttpServletRequest request) {
        ResultObject resultObject = new ResultObject();
        try {
            // 邮箱或手机号
            String target = request.getParameter("target");
            if (StringUtils.isNullOrEmpty(target)) {
                throw new BusinessException("不能为空");
            }
            String verifyTimes = "";
            String verifyTimesKey = "";
            boolean type = false;//默认false是邮箱，ture为电话类型
            if (target.contains("@")) {
                //校验邮箱是否有重复的
                if (Objects.nonNull(this.partyService.findPartyByUsername(target)) || Objects.nonNull(this.partyService.getPartyByEmail(target))) {
                    throw new BusinessException("该邮箱已被占用，请更换其他邮箱注册");
                }
                verifyTimes = sysparaService.find("mall_modify_email_verify_times").getValue();
                verifyTimesKey = MallRedisKeys.MALL_EMAIL_VERIFY_TIME_LOCK + target;
            } else {
                //校验手机号是否有重复的，注意手机号里面有空格
                if (Objects.nonNull(this.partyService.findPartyByUsername(target.replaceAll("\\s", ""))) || Objects.nonNull(this.partyService.findPartyByVerifiedPhone(target))) {
                    throw new BusinessException("该手机号已被占用，请绑定其他手机号");
                }
                verifyTimes = sysparaService.find("mall_modify_phone_verify_times").getValue();
                verifyTimesKey = MallRedisKeys.MALL_PHONE_VERIFY_TIME_LOCK + target.replaceAll("\\s", "");
                type = true;
            }

            if (Objects.isNull(verifyTimes)) {
                logger.error("mall_modify_email_verify_times 或者 mall_modify_phone_verify_times 系统参数未配置！");
                throw new BusinessException("参数异常");
            }
            int needSeconds = util.DateUtils.getTomorrowStartSeconds();
            boolean exit = redisHandler.exists(verifyTimesKey);//是否已经发送过
//            if (exit && ("true".equals(redisHandler.getString(verifyTimesKey)))) {//已发送过且次数已经超过verifyTimes配置的次数
//                if (type) {
//                    throw new BusinessException(1, "手机验证码发送次数过多，请明天再试");
//                } else {
//                    throw new BusinessException(1, "邮箱验证码发送次数过多，请明天再试");
//                }
//            }
//            if (exit && verifyTimes.equals(redisHandler.getString(verifyTimesKey))) {//已经发送过且次数刚好等于verifyTimes配置的次数
//                redisHandler.setSyncStringEx(verifyTimesKey, "true", needSeconds);
//                if (type) {
//                    throw new BusinessException(1, "手机验证码发送次数过多，请明天再试");
//                } else {
//                    throw new BusinessException(1, "邮箱验证码发送次数过多，请明天再试");
//                }
//            }
            //发送次数小于配置次数
            identifyingCodeService.addSend(target.replaceAll("\\s", ""), this.getIp());
            redisHandler.incr(verifyTimesKey);
        } catch (BusinessException e) {
            resultObject.setCode("1");
            resultObject.setMsg(e.getMessage());
        }
        return resultObject;
    }
}
