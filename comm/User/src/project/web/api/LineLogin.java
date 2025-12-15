package project.web.api;

import kernel.exception.BusinessException;
import kernel.util.StringUtils;
import kernel.web.BaseAction;
import kernel.web.ResultObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import project.Constants;
import project.ddos.IpMenuService;
import project.log.LogService;
import project.party.PartyService;
import project.party.model.Party;
import project.redis.RedisHandler;
import project.user.*;
import project.user.idcode.IdentifyingCodeTimeWindowService;
import project.user.token.TokenService;
import security.SecUser;
import security.internal.SecUserService;
import util.LockFilter;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


@RestController
@CrossOrigin
@Slf4j
public class LineLogin extends BaseAction {

    private static final String CHANNEL_ID = "2006434224";
    private static final String CHANNEL_SECRET = "27f31ada6f4502d5473c35d4d8617ff3";
    private static final String REDIRECT_URI = "https://uni-gogo.com/wap/normal/lineLogin!login.action";
    private static final String AUTH_URL = "https://access.line.me/oauth2/v2.1/authorize";
    private static final String TOKEN_URL = "https://api.line.me/oauth2/v2.1/token";
    private static final String USER_INFO_URL = "https://api.line.me/v2/profile";

    private final String action = "/api/linelogin!";

    private Logger logger = LogManager.getLogger(LocalUserController.class);

    @Autowired
    private PartyService partyService;
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
    protected JdbcTemplate jdbcTemplate;
    @Resource
    protected RedisHandler redisHandler;
    @Resource
    private UserDataService userDataService;

    // Step 1: Get the authorization URL
    /**
     * 获取用户登录地址
     */
    @RequestMapping(value = action + "lineurl.action")
    public Object getAuthorizationUrl(HttpServletRequest request) {
        return AUTH_URL + "?response_type=code" +
                "&client_id=" + CHANNEL_ID +
                "&redirect_uri=" + REDIRECT_URI +
                "&scope=profile%20openid"; // Request necessary permissions
    }

    // Step 2: Exchange the code for an access token
    public String getAccessToken(String code) throws IOException {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost post = new HttpPost(TOKEN_URL);

        String body = "grant_type=authorization_code" +
                "&code=" + code +
                "&redirect_uri=" + REDIRECT_URI +
                "&client_id=" + CHANNEL_ID +
                "&client_secret=" + CHANNEL_SECRET;

        post.setEntity(new StringEntity(body));
        post.setHeader("Content-Type", "application/x-www-form-urlencoded");

        HttpResponse response = client.execute(post);
        String result = EntityUtils.toString(response.getEntity());
        client.close();

        // Extract access token from JSON response (you may need a JSON library for this)
        return extractAccessToken(result);
    }

    // A simple method to extract access token (use a proper JSON parser in production)
    private String extractAccessToken(String json) {
        // Implement your JSON parsing logic here
        return ""; // Mock implementation
    }

    // Step 3: Get user info using access token
    public String getUserInfo(String accessToken) throws IOException {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet get = new HttpGet(USER_INFO_URL);
        get.setHeader("Authorization", "Bearer " + accessToken);

        HttpResponse response = client.execute(get);
        String result = EntityUtils.toString(response.getEntity());
        client.close();

        return result; // User info in JSON format
    }

    public static void main(String[] args) {
        LineLogin lineLogin = new LineLogin();

        // Step 1: Get authorization URL
//        String authUrl = (String) lineLogin.getAuthorizationUrl();
//        System.out.println("Visit this URL to log in: " + authUrl);

        // Assuming you receive the authorization code after redirection
        String code = "RECEIVED_AUTHORIZATION_CODE";

        try {
            // Step 2: Get access token
            String accessToken = lineLogin.getAccessToken(code);
            System.out.println("Access Token: " + accessToken);

            // Step 3: Get user info
            String userInfo = lineLogin.getUserInfo(accessToken);
            System.out.println("User Info: " + userInfo);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取用户列表
     */
    @RequestMapping(value = action + "login.action")
    public Object login(HttpServletRequest request) {
        LineLogin lineLogin = new LineLogin();

        // Step 1: Get authorization URL
//        String authUrl = lineLogin.getAuthorizationUrl();
//        System.out.println("Visit this URL to log in: " + authUrl);

        // Assuming you receive the authorization code after redirection
        String code = request.getParameter("code");
        ResultObject resultObject = new ResultObject();

        try {
            // Step 2: Get access token
            String accessToken = lineLogin.getAccessToken(code);
            System.out.println("Access Token: " + accessToken);

            // Step 3: Get user info
            String userInfo = lineLogin.getUserInfo(accessToken);
            Map<String,Object> userInfoMap = (Map) JSONObject.stringToValue(userInfo);
            System.out.println("User Info: " + userInfoMap);

            //以下是用户注册
            //		用户手机号注册时，用户表中字段phone字段修改为 区号+空格+手机号
            String username = request.getParameter("username");
            String phoneStr = request.getParameter("phone");
            String whatsApp = request.getParameter("whatsApp");
            String phone = request.getParameter("phone").replace(" ", "");
            String password = request.getParameter("password").replace(" ", "");
            String re_password = request.getParameter("re_password").replace(" ", "");
//        新增校验码验证
            String verifcode = request.getParameter("verifcode");
            String agentCode = "000000";
            // 注册类型：1/手机；2/邮箱；3/用户名；
            String type = request.getParameter("type");
            if (StringUtils.isEmptyString(type) || !Arrays.asList("1", "2").contains(type)) {
                throw new BusinessException("类型不能为空");
            }



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
        } catch (IOException e) {
            e.printStackTrace();
        }

        return resultObject;
    }

}
