package project.web.admin.controller.user;

import java.text.MessageFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.providers.encoding.PasswordEncoder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import kernel.exception.BusinessException;
import kernel.util.JsonUtils;
import kernel.util.PropertiesUtil;
import kernel.util.StringUtils;
import kernel.web.PageActionSupport;
import project.Constants;
import project.log.Log;
import project.log.LogService;
import project.mall.auto.AutoConfig;
import project.party.PartyService;
import project.party.model.Party;
import project.syspara.SysparaService;
import project.user.Agent;
import project.web.admin.service.user.AdminAgentService;
import security.SecUser;
import security.internal.SecUserService;

/**
 * 代理商
 */
@RestController
public class AdminAgentController extends PageActionSupport {

	private Logger logger = LogManager.getLogger(AdminAgentController.class);

	@Autowired
	private AdminAgentService adminAgentService;
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private PartyService partyService;
	@Autowired
	private LogService logService;
	@Autowired
	private SecUserService secUserService;

	@Autowired
	private SysparaService sysparaService;

	private final String action = "normal/adminAgentAction!";

	/**
	 * 获取代理商列表
	 */
	@RequestMapping(action + "list.action")
	public ModelAndView list(HttpServletRequest request) {
		String pageNo = request.getParameter("pageNo");
		String message = request.getParameter("message");
		String error = request.getParameter("error");
		String partyId = request.getParameter("partyId");
		String view_para = request.getParameter("view_para");
		String name_para = request.getParameter("name_para");
		String para_party_id = request.getParameter("para_party_id");

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("agent_list");

		try {

			this.checkAndSetPageNo(pageNo);

			this.pageSize = 20;

			String checkedPartyId = this.getLoginPartyId();
			if (!StringUtils.isNullOrEmpty(partyId)) {
				checkedPartyId = partyId;
			}

			if ("list".equals(view_para)) {
				this.page = this.adminAgentService.pagedQuery(this.pageNo, this.pageSize, name_para, checkedPartyId);
			} else {
				if (!"".equals(name_para)) {
					para_party_id = "";
				}

				this.page = this.adminAgentService.pagedQueryNetwork(this.pageNo, this.pageSize, this.getLoginPartyId(), name_para,
						Constants.SECURITY_ROLE_AGENT, para_party_id);
			}

//			String webUrl = Constants.WEB_URL;
//			webUrl = webUrl.substring(0, webUrl.length() - 4);
			String webUrl = AutoConfig.getBaseUrl();

			List<Map<String, Object>> list = (List<Map<String, Object>>) this.page.getElements();
			for (Map<String, Object> map : list) {
				map.put("share_url", webUrl + "#/?code=" + map.get("usercode").toString());
			}

			String url = PropertiesUtil.getProperty("admin_url") + "/adminAgent/list";
			String result = JsonUtils.getJsonString(this.adminAgentService.findAgentNodes(this.getLoginPartyId(), checkedPartyId, url));

			modelAndView.addObject("result", result);

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
		modelAndView.addObject("name_para", name_para);
		modelAndView.addObject("view_para", view_para);
		return modelAndView;
	}

	/**
	 * 新增代理商 页面
	 */
	@RequestMapping(action + "toAdd.action")
	public ModelAndView toAdd(HttpServletRequest request) {

		ModelAndView modelAndView = new ModelAndView();

		try {

			String parents_usercode = "";

			if (!StringUtils.isNullOrEmpty(this.getLoginPartyId())) {
				String loginPartyId = this.getLoginPartyId();
				Party party = this.partyService.cachePartyBy(loginPartyId, true);
				parents_usercode = party.getUsercode();
			}

			modelAndView.addObject("parents_usercode", parents_usercode);

		} catch (BusinessException e) {
			modelAndView.addObject("error", e.getMessage());
			modelAndView.setViewName("redirect:/" + action + "list.action");
			return modelAndView;
		} catch (Throwable t) {
			logger.error(" error ", t);
			modelAndView.addObject("error", "[ERROR] " + t.getMessage());
			modelAndView.setViewName("redirect:/" + action + "list.action");
			return modelAndView;
		}

		modelAndView.setViewName("agent_add");
		return modelAndView;
	}

	/**
	 * 新增代理商
	 */
	@RequestMapping(action + "add.action")
	public ModelAndView add(HttpServletRequest request) {
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		String login_safeword = request.getParameter("login_safeword");
		String name = request.getParameter("name");
		String remarks = request.getParameter("remarks");
		String parents_usercode = request.getParameter("parents_usercode");
		boolean login_authority = Boolean.valueOf(request.getParameter("login_authority")).booleanValue();
		boolean opera_authority = Boolean.valueOf(request.getParameter("opera_authority")).booleanValue();
		//skyxx
		boolean call_flag = Boolean.valueOf(request.getParameter("call_flag")).booleanValue();

		ModelAndView modelAndView = new ModelAndView();

		try {

			String error = this.verification(username, password);
			if (!StringUtils.isNullOrEmpty(error)) {
				throw new BusinessException(error);
			}

			String username_login = this.getUsername_login();

			SecUser sec = this.secUserService.findUserByLoginName(username_login);
			this.checkLoginSafeword(sec, this.getUsername_login(), login_safeword);

			username = username.replace(" ", "");
			password = password.replace(" ", "");

			this.adminAgentService.save(name, username, password, login_authority, call_flag,remarks, parents_usercode, opera_authority);

			String log = MessageFormat.format(
					"ip:" + this.getIp() + ",管理员新增代理商，名称:{0},用户名:{1},登录权限:{2},备注:{3},推荐人uid:{4},操作权限:{5}", name,
					username, login_authority, remarks, parents_usercode, opera_authority);
			this.saveLog(sec, username_login, log);

		} catch (BusinessException e) {
			modelAndView.addObject("error", e.getMessage());
			modelAndView.addObject("username", username);
			modelAndView.addObject("password", password);
			modelAndView.addObject("remarks", remarks);
			modelAndView.addObject("parents_usercode", parents_usercode);
			modelAndView.addObject("login_authority", login_authority);
			modelAndView.addObject("opera_authority", opera_authority);
			modelAndView.setViewName("agent_add");
			return modelAndView;
		} catch (Throwable t) {
			logger.error("UserAction.register error ", t);
			modelAndView.addObject("error", "[ERROR] " + t.getMessage());
			modelAndView.addObject("username", username);
			modelAndView.addObject("password", password);
			modelAndView.addObject("remarks", remarks);
			modelAndView.addObject("parents_usercode", parents_usercode);
			modelAndView.addObject("login_authority", login_authority);
			modelAndView.addObject("opera_authority", opera_authority);
			modelAndView.setViewName("agent_add");
			return modelAndView;
		}

		modelAndView.addObject("message", "操作成功");
		modelAndView.setViewName("redirect:/" + action + "list.action");
		return modelAndView;
	}

	/**
	 * 修改代理商 页面
	 */
	@RequestMapping(action + "toUpdate.action")
	public ModelAndView toUpdate(HttpServletRequest request) {
		String id = request.getParameter("id");

		ModelAndView modelAndView = new ModelAndView();

		try {

			Agent agent = adminAgentService.get(id);

			Party party = this.partyService.cachePartyBy(agent.getPartyId(), false);

			modelAndView.addObject("id", id);
			modelAndView.addObject("name", party.getName());
			modelAndView.addObject("remarks", party.getRemarks());
			modelAndView.addObject("login_authority", party.getLogin_authority());
			modelAndView.addObject("opera_authority", Constants.SECURITY_ROLE_AGENT.equals(party.getRolename()));

		} catch (BusinessException e) {
			modelAndView.addObject("error", e.getMessage());
			modelAndView.setViewName("redirect:/" + action + "list.action");
			return modelAndView;
		} catch (Throwable t) {
			logger.error(" error ", t);
			modelAndView.addObject("error", "[ERROR] " + t.getMessage());
			modelAndView.setViewName("redirect:/" + action + "list.action");
			return modelAndView;
		}

		modelAndView.setViewName("agent_update");
		return modelAndView;
	}

	/**
	 * 修改代理商
	 */
	@RequestMapping(action + "update.action")
	public ModelAndView update(HttpServletRequest request) {
		String id = request.getParameter("id");
		String name = request.getParameter("name");
		String remarks = request.getParameter("remarks");
		boolean login_authority = Boolean.valueOf(request.getParameter("login_authority")).booleanValue();
		boolean opera_authority = Boolean.valueOf(request.getParameter("opera_authority")).booleanValue();
		boolean call_flag = Boolean.valueOf(request.getParameter("call_flag")).booleanValue();

		ModelAndView modelAndView = new ModelAndView();

		try {

			Agent agent = this.adminAgentService.get(id);

			Party party = this.partyService.cachePartyBy(agent.getPartyId(), false);

			SecUser secUser = this.secUserService.findUserByPartyId(agent.getPartyId());
			//String log = MessageFormat.format("ip:" + this.getIp() + ",管理员修改代理商，用户名:{0},原登录权限:{1},原备注:{2},原操作权限:{3}",
			//		secUser.getUsername(), secUser.getEnabled(), party.getRemarks(),
			//		Constants.SECURITY_ROLE_AGENT.equals(party.getRolename()));

			this.adminAgentService.update(id, name, login_authority, call_flag,remarks, opera_authority);

			//log += MessageFormat.format(",新登录权限:{0},新备注:{1},新操作权限:{2}", login_authority, remarks, opera_authority);
			//this.saveLog(secUser, this.getUsername_login(), log);

		} catch (BusinessException e) {
			modelAndView.addObject("error", e.getMessage());
			modelAndView.addObject("id", id);
			modelAndView.addObject("name", name);
			modelAndView.addObject("remarks", remarks);
			modelAndView.addObject("login_authority", login_authority);
			modelAndView.addObject("opera_authority", opera_authority);
			modelAndView.setViewName("agent_update");
			return modelAndView;
		} catch (Throwable t) {
			logger.error(" error ", t);
			modelAndView.addObject("error", "[ERROR] " + t.getMessage());
			modelAndView.addObject("id", id);
			modelAndView.addObject("name", name);
			modelAndView.addObject("remarks", remarks);
			modelAndView.addObject("login_authority", login_authority);
			modelAndView.addObject("opera_authority", opera_authority);
			modelAndView.setViewName("agent_update");
			return modelAndView;
		}

		modelAndView.addObject("message", "操作成功");
		modelAndView.setViewName("redirect:/" + action + "list.action");
		return modelAndView;
	}

	/**
	 * 重置登录密码
	 */
	@RequestMapping(action + "resetpsw.action")
	public ModelAndView resetpsw(HttpServletRequest request) {
		String id = request.getParameter("id");
		String password = request.getParameter("password");
		String safeword = request.getParameter("safeword");

		ModelAndView modelAndView = new ModelAndView();

		try {

			if (!StringUtils.isNullOrEmpty(password) || !StringUtils.isNullOrEmpty(safeword)) {

				SecUser sec = this.secUserService.findUserByLoginName(this.getUsername_login());
				String sysSafeword = sec.getSafeword();
				String safeword_md5 = passwordEncoder.encodePassword(safeword, this.getUsername_login());
				if (!safeword_md5.equals(sysSafeword)) {
					throw new BusinessException("资金密码错误");
				}

				password = password.replace(" ", "");

				Agent agent = this.adminAgentService.get(id);
				Party party = this.partyService.cachePartyBy(agent.getPartyId(), true);

				this.secUserService.updatePassword(party.getUsername(), password);

				Log log = new Log();
				log.setCategory(Constants.LOG_CATEGORY_OPERATION);
				log.setUsername(party.getUsername());
				log.setOperator(this.getUsername_login());
				log.setLog("ip:" + this.getIp() + ",管理员手动代理商修改登录密码");
				this.logService.saveSync(log);
			}
		} catch (BusinessException e) {
			modelAndView.addObject("error", e.getMessage());
			modelAndView.setViewName("redirect:/" + action + "list.action");
			return modelAndView;
		} catch (Exception e) {
			logger.error(" error ", e);
			modelAndView.addObject("error", "程序错误");
			modelAndView.setViewName("redirect:/" + action + "list.action");
			return modelAndView;
		}

		modelAndView.addObject("message", "操作成功");
		modelAndView.setViewName("redirect:/" + action + "list.action");
		return modelAndView;
	}

	/**
	 * 验证登录人资金密码
	 */
	protected void checkLoginSafeword(SecUser secUser, String operatorUsername, String loginSafeword) {
//		SecUser sec = this.secUserService.findUserByLoginName(operatorUsername);
		String sysSafeword = secUser.getSafeword();
		String safeword_md5 = this.passwordEncoder.encodePassword(loginSafeword, operatorUsername);
		if (!safeword_md5.equals(sysSafeword)) {
			throw new BusinessException("登录人资金密码错误");
		}
	}

	public void saveLog(SecUser secUser, String operator, String context) {
		Log log = new Log();
		log.setCategory(Constants.LOG_CATEGORY_OPERATION);
		log.setOperator(operator);
		log.setUsername(secUser.getUsername());
		log.setPartyId(secUser.getPartyId());
		log.setLog(context);
		log.setCreateTime(new Date());
		this.logService.saveSync(log);
	}

	private String verification(String username, String password) {
//		if (StringUtils.isEmptyString(this.name)) {
//			return "请输入姓名";
//		}
		if (StringUtils.isEmptyString(username)) {
			return "请输入用户名";
		}
		if (StringUtils.isEmptyString(password)) {
			return "请输入登录密码";
		}
		return null;
	}

}
