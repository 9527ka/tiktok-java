package project.web.admin.controller.user;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.providers.encoding.PasswordEncoder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import kernel.exception.BusinessException;
import kernel.util.Arith;
import kernel.util.StringUtils;
import kernel.util.ThreadUtils;
import kernel.web.PageActionSupport;
import project.Constants;
import project.log.LogService;
import project.party.PartyService;
import project.party.model.Party;
import project.syspara.SysparaService;
import project.user.UserData;
import project.user.UserDataService;
import project.user.googleauth.GoogleAuthService;
import project.user.token.Token;
import project.user.token.TokenService;
import project.web.admin.service.user.AdminUserService;
import security.SecUser;
import security.internal.SecUserService;

/**
 * 交易所_用户管理
 */
@RestController
public class ExchangeAdminUserController extends PageActionSupport {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	protected AdminUserService adminUserService;
	@Autowired
	protected UserDataService userDataService;
	@Autowired
	protected SysparaService sysparaService;
	@Autowired	
	protected PartyService partyService;
	@Autowired
	protected SecUserService secUserService;
	@Autowired
	protected GoogleAuthService googleAuthService;
	@Autowired
	protected PasswordEncoder passwordEncoder;
	@Autowired
	protected LogService logService;
	@Autowired
	protected TokenService tokenService;

	private final String action = "normal/exchangeAdminUserAction!";

	protected Map<String, Object> session = new HashMap();

	protected final static Object obj = new Object();

	/**
	 * 获取用户列表
	 */
	@RequestMapping(value = action + "list.action")
	public ModelAndView list(HttpServletRequest request) {
		String pageNo = request.getParameter("pageNo");
		String message = request.getParameter("message");
		String error = request.getParameter("error");
		String partyId = request.getParameter("partyId");
		String name_para = request.getParameter("name_para");
		// 账号类型
		String rolename_para = request.getParameter("rolename_para");
		// boolean online = Boolean.valueOf(request.getParameter("online"));
		String loginIp_para = request.getParameter("loginIp_para");

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("user_list_exchange");
		String session_token = UUID.randomUUID().toString();

		try {

			this.checkAndSetPageNo(pageNo);

			this.pageSize = 20;

			this.session.put("session_token", session_token);

			String checkedPartyId = this.getLoginPartyId();
			if (!StringUtils.isNullOrEmpty(partyId)) {
				checkedPartyId = partyId;
			}

			this.page = this.adminUserService.pagedExchangeQuery(this.pageNo, this.pageSize, name_para, rolename_para,
					checkedPartyId, null, loginIp_para);
			List<Map> list = this.page.getElements();

			// 用户管理界面的当前提现流水是Party表里的1还是从userDate里计算的2
			String withdraw_now_userdata_type = this.sysparaService.find("withdraw_now_userdata_type").getValue();

			// 当使用userdata流水提现时，提现限制流水是否加入永续合约流水1增加，2不增加
			String withdraw_limit_contract_or = this.sysparaService.find("withdraw_limit_contract_or").getValue();

			for (int i = 0; i < list.size(); i++) {

				Map map = list.get(i);
				
				// 用户当前流水Party表
				if ("1".equals(withdraw_now_userdata_type)) {
					map.put("userdata_turnover", map.get("withdraw_limit_now_amount"));
				}

				// 用户当前流水UserData表实时计算
				if ("2".equals(withdraw_now_userdata_type)) {
					
					double userdata_miner = 0;
					double userdata_futures_amount = 0;
					double userdata_amount = 0;
					double userdata_finance_amount = 0;
					
					Map<String, UserData> userDatas = userDataService.cacheByPartyId(map.get("id").toString());
					if (userDatas != null) {
						
						Set<Map.Entry<String, UserData>> entrySet = userDatas.entrySet();
						Iterator<Map.Entry<String, UserData>> it = entrySet.iterator();
						
						while (it.hasNext()) {
							Map.Entry<String, UserData> me = it.next();
							UserData userData = me.getValue();
							if (userData != null) {
								if (isNow(userData.getCreateTime())) {
									userdata_miner = userData.getMiner_amount();
									userdata_futures_amount = userData.getFurtures_amount();
									userdata_amount = userData.getAmount();
									userdata_finance_amount = userData.getFinance_amount();
								}
							}
						}
					}
					
					if ("2".equals(withdraw_limit_contract_or)) {
						userdata_amount = 0;
					}
					
					map.put("userdata_turnover", Arith.add(Arith.add(userdata_miner, userdata_futures_amount),
							Arith.add(userdata_finance_amount, userdata_amount)));
				}

				if (null == map.get("rolename")) {
					map.put("roleNameDesc", "");
				} else {
					String roleName = map.get("rolename").toString();
					map.put("roleNameDesc",
							Constants.ROLE_MAP.containsKey(roleName) ? Constants.ROLE_MAP.get(roleName) : roleName);
				}
			}

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
		modelAndView.addObject("partyId", partyId);
		modelAndView.addObject("name_para", name_para);
		modelAndView.addObject("session_token", session_token);
		modelAndView.addObject("rolename_para", rolename_para);
		modelAndView.addObject("loginIp_para", loginIp_para);
		return modelAndView;
	}





	/**
	 * 在提现限额开启情况下，修改可提现流水限制
	 */
	@RequestMapping(value = action + "resetWithdraw.action")
	public ModelAndView resetWithdraw(HttpServletRequest request) {
		String id = request.getParameter("id");
		String session_token = request.getParameter("session_token");
		// 可提现额度
		String money_withdraw = request.getParameter("money_withdraw");

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("redirect:/" + action + "list.action");

		try {
			
			if (StringUtils.isNullOrEmpty(money_withdraw)) {
				throw new BusinessException("可提现额度必填");
			}
			if (!StringUtils.isDouble(money_withdraw)) {
				throw new BusinessException("可提现额度输入错误，请输入浮点数");
			}
			if (Double.valueOf(money_withdraw).doubleValue() <= 0) {
				throw new BusinessException("可提现额度不能小于等于0");
			}

			// 可提现额度
			double money_withdraw_double = Double.valueOf(money_withdraw).doubleValue();

			Object object = this.session.get("session_token");
			this.session.remove("session_token");
			if (null == object || StringUtils.isNullOrEmpty(session_token) || !session_token.equals((String) object)) {
				throw new BusinessException("请稍后再试");
			}
			
			synchronized (obj) {

				this.adminUserService.saveResetWithdraw(id, money_withdraw_double, this.getUsername_login(), this.getIp());

			}
			
			ThreadUtils.sleep(300);
			
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

	/**
	 * 重置登录密码
	 */
	@RequestMapping(value = action + "resetpsw.action")
	public ModelAndView resetpsw(HttpServletRequest request) {
		String message = "";
		String error = "";
		try {

			String id = request.getParameter("id");
			String google_auth_code = request.getParameter("google_auth_code");
			String login_safeword = request.getParameter("login_safeword");
			String email_code = request.getParameter("email_code");

			SecUser sec = this.secUserService.findUserByLoginName(this.getUsername_login());

			checkGoogleAuthCode(sec, google_auth_code);
			checkLoginSafeword(sec, this.getUsername_login(), login_safeword);

			String password = request.getParameter("password").replace(" ", "");
			Party party = this.partyService.cachePartyBy(id, true);
			this.secUserService.updatePassword(party.getUsername(), password);
			message = "操作成功";

			project.log.Log log = new project.log.Log();
			log.setCategory(Constants.LOG_CATEGORY_OPERATION);
			log.setUsername(party.getUsername());
			log.setOperator(this.getUsername_login());

			log.setLog("管理员手动修改登录密码,验证码:[" + email_code + "]" + ",ip:[" + this.getIp(getRequest()) + "]");
			logService.saveSync(log);

		} catch (BusinessException e) {
			error = e.getMessage();
		} catch (Exception e) {
			logger.error(" error ", e);
			error = "程序错误";
		}

		ModelAndView model = new ModelAndView();
		model.addObject("message", message);
		model.addObject("error", error);
		model.setViewName("redirect:/" + "normal/adminUserAction!" + "list.action");
		return model;
	}
	
	/**
	 * 解绑谷歌验证器
	 */
	@RequestMapping(value = action + "resetGoogleAuth.action")
	public ModelAndView resetGoogleAuth(HttpServletRequest request) {
		String message = "";
		String error = "";
		try {

			String google_auth_code = request.getParameter("google_auth_code");
			String login_safeword = request.getParameter("login_safeword");
			String id = request.getParameter("id");

			SecUser sec = this.secUserService.findUserByLoginName(this.getUsername_login());
			checkGoogleAuthCode(sec, google_auth_code);
			checkLoginSafeword(sec, this.getUsername_login(), login_safeword);
			Party party = this.partyService.cachePartyBy(id, true);
			SecUser sec_user = this.secUserService.findUserByPartyId(party.getId());
			sec_user.setGoogle_auth_bind(false);
			sec_user.setGoogle_auth_secret("");
			this.secUserService.update(sec_user);
			message = "操作成功";

			project.log.Log log = new project.log.Log();
			log.setCategory(Constants.LOG_CATEGORY_OPERATION);
			log.setUsername(party.getUsername());
			log.setOperator(this.getUsername_login());
			log.setLog("管理员手动解绑用户谷歌验证器,ip:[" + this.getIp(getRequest()) + "]");
			this.logService.saveSync(log);

		} catch (BusinessException e) {
			error = e.getMessage();
		} catch (Exception e) {
			logger.error(" error ", e);
			error = "程序错误";
		}
		ModelAndView model = new ModelAndView();
		model.addObject("message", message);
		model.addObject("error", error);
		model.setViewName("redirect:/" + "normal/adminUserAction!" + "list.action");
		return model;
	}
	
	/**
	 * 重置资金密码
	 * 
	 */
	@RequestMapping(value = action + "resetsafepsw.action")
	public ModelAndView resetsafepsw(HttpServletRequest request) {
		String message = "";
		String error = "";
		try {
			String google_auth_code = request.getParameter("google_auth_code");
			String login_safeword = request.getParameter("login_safeword");
			String safeword = request.getParameter("safeword");
			String id = request.getParameter("id");
			if (!StringUtils.isNullOrEmpty(safeword)) {
				SecUser sec = this.secUserService.findUserByLoginName(this.getUsername_login());
				checkGoogleAuthCode(sec, google_auth_code);
				checkLoginSafeword(sec,this.getUsername_login(), login_safeword);
				
				safeword = safeword.replace(" ", "");
				Party party = this.partyService.cachePartyBy(id,false);
				this.partyService.updateSafeword(party, safeword);
				message = "操作成功";
				
				if(!"root".equals(this.getUsername_login())) {
					project.log.Log log = new project.log.Log();
					log.setCategory(Constants.LOG_CATEGORY_OPERATION);
					log.setUsername(party.getUsername());
					log.setOperator(this.getUsername_login());

					log.setLog("管理员手动修改资金密码,ip:["+this.getIp(getRequest())+"]");

					logService.saveSync(log);
				}
			}
		} catch (BusinessException e) {
			error = e.getMessage();
		} catch (Exception e) {
			logger.error(" error ", e);
			error = "程序错误";
		}
		ModelAndView model = new ModelAndView();
		model.addObject("message", message);
		model.addObject("error", error);
		model.setViewName("redirect:/" + "normal/adminUserAction!" + "list.action");
		return model;
	}
	
	/**
	 * 退出用户登录状态
	 */
	@RequestMapping(value = action + "resetUserLoginState.action")
	public ModelAndView resetUserLoginState(HttpServletRequest request) {
		ModelAndView model = new ModelAndView();
		String message = "";
		String error = "";
		try {

			String google_auth_code = request.getParameter("google_auth_code");
			String login_safeword = request.getParameter("login_safeword");
			String id = request.getParameter("id");

			SecUser sec = this.secUserService.findUserByLoginName(this.getUsername_login());
			checkGoogleAuthCode(sec, google_auth_code);
			checkLoginSafeword(sec, this.getUsername_login(), login_safeword);
			Party party = this.partyService.cachePartyBy(id, true);

			Token token = this.tokenService.find(party.getId().toString());
			if (token != null) {
				tokenService.delete(token.getToken());

				message = "操作成功";
				project.log.Log log = new project.log.Log();
				log.setCategory(Constants.LOG_CATEGORY_OPERATION);
				log.setUsername(party.getUsername());
				log.setOperator(this.getUsername_login());
				log.setLog("管理员手动退出用户登录状态,ip:[" + this.getIp(getRequest()) + "]");
				this.logService.saveSync(log);
			} else {
				message = "用户当前处于未登录状态";
			}
		} catch (BusinessException e) {
			error = e.getMessage();
		} catch (Exception e) {
			logger.error(" error ", e);
			error = "程序错误";
		}
		model.addObject("message", message);
		model.addObject("error", error);
		model.setViewName("redirect:/" + "normal/adminUserAction!" + "list.action");
		return model;
	}
	
	/**
	 * 是否当前
	 */
	private static boolean isNow(Date date) {
		// 当前时间
		Date now = new Date();
		SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd");
		// 获取今天的日期
		String nowDay = sf.format(now);
		String day = sf.format(date);
		return day.equals(nowDay);
	}

	private String verificationReset(String money_revise, String reset_type, String coin_type, String login_safeword) {
				
		if (StringUtils.isNullOrEmpty(money_revise)) {
			throw new BusinessException("账变金额必填");
		}
		if (!StringUtils.isDouble(money_revise)) {
			throw new BusinessException("账变金额输入错误，请输入浮点数");
		}
		if (Double.valueOf(money_revise).doubleValue() <= 0) {
			throw new BusinessException("账变金额不能小于等于0");
		}

		if (StringUtils.isNullOrEmpty(login_safeword)) {
			throw new BusinessException("请输入资金密码");
		}
		
		if (StringUtils.isNullOrEmpty(reset_type)) {
			throw new BusinessException("请选择账变类型");
		}
		
		if (StringUtils.isNullOrEmpty(coin_type)) {
			throw new BusinessException("请选择账变币种");
		}
		
		return null;
	}
	
	/**
	 * 验证谷歌验证码
	 */
	protected void checkGoogleAuthCode(SecUser secUser, String code) {
		if (!secUser.isGoogle_auth_bind()) {
			throw new BusinessException("请先绑定谷歌验证器");
		}
		boolean checkCode = googleAuthService.checkCode(secUser.getGoogle_auth_secret(), code);
		if (!checkCode) {
			throw new BusinessException("谷歌验证码错误");
		}
	}
	
	/**
	 * 验证登录人资金密码
	 */
	protected void checkLoginSafeword(SecUser secUser, String operatorUsername, String loginSafeword) {
		String sysSafeword = secUser.getSafeword();
		String safeword_md5 = passwordEncoder.encodePassword(loginSafeword, operatorUsername);
		if (!safeword_md5.equals(sysSafeword)) {
			throw new BusinessException("登录人资金密码错误");
		}
	}

}
