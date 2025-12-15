package project.web.api;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mysql.cj.util.StringUtils;

import kernel.exception.BusinessException;
import kernel.web.BaseAction;
import kernel.web.ResultObject;
import project.party.PartyService;
import project.party.model.Party;
import project.user.googleauth.GoogleAuthService;
import security.SecUser;
import security.internal.SecUserService;
import util.GoogleAuthenticator;

/**
 * 谷歌身份认证器
 *
 */
@RestController
@CrossOrigin
public class GoogleAuthController extends BaseAction {
	
	private Logger logger = LogManager.getLogger(GoogleAuthController.class);
	
	@Autowired
	private GoogleAuthService googleAuthService;
	@Autowired
	private SecUserService secUserService;
	@Autowired
	private PartyService partyService;
	
	private final String action = "/api/googleauth!";
	
	/**
	 * 谷歌身份验证器 获取密钥及二维码
	 */
	@RequestMapping(action + "get.action")
	public Object get() {

		ResultObject resultObject = new ResultObject();
		resultObject = readSecurityContextFromSession(resultObject);
		if (!"0".equals(resultObject.getCode())) {
			return resultObject;
		}
		try {
			Map<String, Object> data = new HashMap<String, Object>();
			String partyId = getLoginPartyId();
			SecUser secUser = secUserService.findUserByPartyId(partyId);
			// 未绑定则
			if (!secUser.isGoogle_auth_bind()) {
				String secretKey = GoogleAuthenticator.generateSecretKey();
				data.put("google_auth_secret", secretKey);
				data.put("google_auth_url", googleAuthService.getGoogleAuthUrl(secUser.getUsername(), secretKey));
			}
			data.put("google_auth_bind", secUser.isGoogle_auth_bind());
			resultObject.setData(data);
			resultObject.setCode("0");
		} catch (BusinessException e) {
			resultObject.setCode("1");
			resultObject.setMsg(e.getMessage());
		} catch (Exception e) {
			resultObject.setCode("1");
			resultObject.setMsg("程序错误");
			logger.error("error:", e);
		}

		return resultObject;
	}

	/**
	 * 谷歌身份绑定
	 */
	@RequestMapping(action + "bind.action")
	public Object bind(HttpServletRequest request) {

		ResultObject resultObject = new ResultObject();
		resultObject = readSecurityContextFromSession(resultObject);
		if (!"0".equals(resultObject.getCode())) {
			return resultObject;
		}
		
		try {
			
			String secret = request.getParameter("secret");
			String code = request.getParameter("code");
			if (StringUtils.isNullOrEmpty(secret)) {
				throw new BusinessException("secret is null");
			}
			if (StringUtils.isNullOrEmpty(code)) {
				throw new BusinessException("code is null");
			}
			
			Map<String, Object> data = new HashMap<String, Object>();
			String partyId = getLoginPartyId();
			Party party = this.partyService.cachePartyBy(partyId, false);			

            // 绑定结果
            boolean binded = this.googleAuthService.saveGoogleAuthBind(party.getUsername(), secret, code);
            if (binded) {
    			
    			// 获取用户系统等级：1/新注册；2/邮箱谷歌手机其中有一个已验证；3/用户实名认证； 4/用户高级认证；
    			int userLevelSystem = this.partyService.getUserLevelByAuth(party);

    			// 十进制个位表示系统级别：1/新注册；2/邮箱谷歌手机其中有一个已验证；3/用户实名认证；4/用户高级认证；
    			// 十进制十位表示自定义级别：对应在前端显示为如VIP1 VIP2等级、黄金 白银等级；
    			// 如：级别11表示：新注册的前端显示为VIP1；
    			int userLevel = party.getUser_level();
    			party.setUser_level(((int) Math.floor(userLevel / 10)) * 10 + userLevelSystem);
    			
    			this.partyService.update(party);
            }

            data.put("google_auth_bind", binded);
			
			resultObject.setData(data);
			resultObject.setCode("0");
			
		} catch (BusinessException e) {
			resultObject.setCode("1");
			resultObject.setMsg(e.getMessage());
		} catch (Exception e) {
			resultObject.setCode("1");
			resultObject.setMsg("程序错误");
			logger.error("error:", e);
		}

		return resultObject;
	}

	public Object checkCode(HttpServletRequest request) {

		ResultObject resultObject = new ResultObject();
		resultObject = readSecurityContextFromSession(resultObject);
		if (!"0".equals(resultObject.getCode())) {
			return resultObject;
		}
		String code = request.getParameter("code");
		try {
			Map<String, Object> data = new HashMap<String, Object>();
			String partyId = getLoginPartyId();
			SecUser secUser = secUserService.findUserByPartyId(partyId);
			if (!secUser.isGoogle_auth_bind()) {// 未绑定则
				throw new BusinessException("请先绑定谷歌验证器");
			}
			data.put("check_result", googleAuthService.checkCode(secUser.getGoogle_auth_secret(), code));
			resultObject.setData(data);
			resultObject.setCode("0");
		} catch (BusinessException e) {
			resultObject.setCode("1");
			resultObject.setMsg(e.getMessage());
		} catch (Exception e) {
			resultObject.setCode("1");
			resultObject.setMsg("程序错误");
			logger.error("error:", e);
		}

		return resultObject;
	}

}
