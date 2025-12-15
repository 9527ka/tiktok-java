package project.web.admin.monitor.mining;

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
import kernel.util.StringUtils;
import kernel.web.PageActionSupport;
import project.Constants;
import project.log.Log;
import project.log.LogService;
import project.monitor.mining.AdminMiningConfigService;
import project.monitor.mining.MiningConfig;
import project.party.PartyService;
import project.party.model.Party;
import project.syspara.SysparaService;
import project.user.googleauth.GoogleAuthService;
import security.SecUser;
import security.internal.SecUserService;

/**
 * 矿池收益规则
 */
@RestController
public class AdminMiningConfigController extends PageActionSupport {

	private Logger logger = LogManager.getLogger(AdminMiningConfigController.class);

	@Autowired
	private AdminMiningConfigService adminMiningConfigService;
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private GoogleAuthService googleAuthService;
	@Autowired
	private SecUserService secUserService;
	@Autowired
	private SysparaService sysparaService;
	@Autowired
	private PartyService partyService;
	@Autowired
	private LogService logService;
	
	private final String action = "normal/adminMiningConfigAction!";
	
	private Map<String, Object> session = new HashMap<String, Object>();

	/**
	 * 获取矿池空投收益规则列表
	 */
	@RequestMapping(action + "list.action")
	public ModelAndView list(HttpServletRequest request) {	
		String pageNo = request.getParameter("pageNo");
		String message = request.getParameter("message");
		String error = request.getParameter("error");
		String name_para = request.getParameter("name_para");

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("auto_mining_config_list");
		
		if (this.session.size() > 500) {
			this.session = new HashMap<String, Object>();			
		}

		try {
			
			this.checkAndSetPageNo(pageNo);

			String session_token = UUID.randomUUID().toString();
			this.session.put("session_token", session_token);
			
			this.pageSize = 20;
			this.page = this.adminMiningConfigService.pagedQuery(this.pageNo, this.pageSize, name_para);

			List<Map<String, Object>> list = (List<Map<String, Object>>) page.getElements();

			modelAndView.addObject("session_token", session_token);
			modelAndView.addObject("result", this.result);

		} catch (BusinessException e) {
			this.error = e.getMessage();
			modelAndView.addObject("error", this.error);
			return modelAndView;
		} catch (Throwable t) {
			logger.error(" error ", t);
			this.error = ("[ERROR] " + t.getMessage());
			modelAndView.addObject("error", this.error);
			return modelAndView;
		}

		modelAndView.addObject("pageNo", this.pageNo);
		modelAndView.addObject("pageSize", this.pageSize);
		modelAndView.addObject("page", this.page);
		modelAndView.addObject("message", message);
		modelAndView.addObject("error", error);
		modelAndView.addObject("name_para", name_para);
		return modelAndView;
	}

	/**
	 * 新增矿池空投收益规则 页面
	 */
	@RequestMapping(action + "toAdd.action")
	public ModelAndView toAdd(HttpServletRequest request) {
		
		ModelAndView modelAndView = new ModelAndView();

		try {

			MiningConfig entity = this.adminMiningConfigService.findById("2c948a827cccd850017cccde6f220001");
			
			modelAndView.addObject("config", entity.getConfig());
			modelAndView.addObject("config_recom", entity.getConfig_recom());

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

		modelAndView.setViewName("auto_mining_config_add");
		return modelAndView;
	}

	/**
	 * 新增矿池空投收益规则
	 */
	@RequestMapping(action + "add.action")
	public ModelAndView add(HttpServletRequest request) {
		String config = request.getParameter("config");
		String config_recom = request.getParameter("config_recom");
		String usercode = request.getParameter("usercode");
		String safeword = request.getParameter("safeword");

		ModelAndView modelAndView = new ModelAndView();

		try {

			String error = this.verification(config, config_recom, usercode);
			if (!StringUtils.isNullOrEmpty(error)) {
				throw new BusinessException(error);
			}

			if (StringUtils.isNullOrEmpty(safeword)) {
				throw new BusinessException("请输入登录人资金密码");
			}
			
			SecUser sec = this.secUserService.findUserByLoginName(this.getUsername_login());
			this.checkLoginSafeword(sec, this.getUsername_login(), safeword);

			Party party = partyService.findPartyByUsercode(usercode);
			if (null == party) {
				throw new BusinessException("UID不存在");
			}
			
			MiningConfig entity_before = this.adminMiningConfigService.findByPartyId(party.getId().toString());
			if (entity_before != null) {
				throw new BusinessException("添加失败，该UID配置已存在");
			}

			MiningConfig entity = new MiningConfig();
			entity.setPartyId(party.getId());
			entity.setConfig(config);
			entity.setConfig_recom(config_recom);

			this.adminMiningConfigService.save(entity);

			Log log = new Log();
			log.setCategory(Constants.LOG_CATEGORY_OPERATION);
			log.setUsername(party.getUsername());
			log.setPartyId(party.getId());
			log.setOperator(this.getUsername_login());
			log.setLog("管理员手动添加用户矿池收益规则,操作ip:[" + this.getIp(getRequest()) + "]");
			logService.saveSync(log);

		} catch (BusinessException e) {
			modelAndView.addObject("error", e.getMessage());
			modelAndView.addObject("config", config);
			modelAndView.addObject("config_recom", config_recom);
			modelAndView.addObject("usercode", usercode);
			modelAndView.setViewName("auto_mining_config_add");
			return modelAndView;
		} catch (Throwable t) {
			logger.error("AdminMiningConfigAction.Add error ", t);
			modelAndView.addObject("error", "[ERROR] " + t.getMessage());
			modelAndView.addObject("config", config);
			modelAndView.addObject("config_recom", config_recom);
			modelAndView.addObject("usercode", usercode);
			modelAndView.setViewName("auto_mining_config_add");
			return modelAndView;
		}

		modelAndView.addObject("message", "操作成功");
		modelAndView.setViewName("redirect:/" + action + "list.action");
		return modelAndView;
	}

	/**
	 * 修改矿池空投收益规则 页面
	 */
	@RequestMapping(action + "toUpdate.action")
	public ModelAndView toUpdate(HttpServletRequest request) {
		String id = request.getParameter("id");

		ModelAndView modelAndView = new ModelAndView();

		try {

			MiningConfig entity = this.adminMiningConfigService.findById(id);
			
			Party party = this.partyService.cachePartyBy(entity.getPartyId(), true);

			modelAndView.addObject("id", id);
			modelAndView.addObject("config", entity.getConfig());
			modelAndView.addObject("config_recom", entity.getConfig_recom());
			if (party != null) {
				modelAndView.addObject("usercode", party.getUsercode());				
			}
			
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
		
		modelAndView.setViewName("auto_mining_config_update");
		return modelAndView;
	}

	/**
	 * 修改矿池空投收益规则
	 */
	@RequestMapping(action + "update.action")
	public ModelAndView update(HttpServletRequest request) {
		String id = request.getParameter("id");
		String config = request.getParameter("config");
		String config_recom = request.getParameter("config_recom");
		String usercode = request.getParameter("usercode");
		String safeword = request.getParameter("safeword");

		ModelAndView modelAndView = new ModelAndView();

		try {
			
			String error = this.verificationUpdate(config, config_recom);
			if (!StringUtils.isNullOrEmpty(error))
				throw new BusinessException(error);

			if (StringUtils.isNullOrEmpty(safeword)) {
				throw new BusinessException("请输入登录人资金密码");
			}
			
			SecUser sec = this.secUserService.findUserByLoginName(this.getUsername_login());
			this.checkLoginSafeword(sec, this.getUsername_login(), safeword);

			MiningConfig entity = this.adminMiningConfigService.findById(id);
			Party party = null;
			if (!StringUtils.isNullOrEmpty(entity.getPartyId().toString())) {
				party = this.partyService.cachePartyBy(entity.getPartyId(), true);
			}

			String config_before = entity.getConfig();
			String config_recom_before = entity.getConfig_recom();

			entity.setConfig(config);
			entity.setConfig_recom(config_recom);

			this.adminMiningConfigService.update(entity);

			project.log.Log log = new project.log.Log();
			log.setCategory(Constants.LOG_CATEGORY_OPERATION);
			if (party != null) {
				log.setUsername(party.getUsername());
				log.setPartyId(party.getId());
			}
			log.setOperator(this.getUsername_login());
			log.setLog("管理员手动修改用户矿池收益规则,修改前收益费率为：[" + config_before + "]," + "上级返佣费率为[" + config_recom_before
					+ "],修改后收益费率为[" + config + "],上级返佣费率为[" + config_recom + "],操作ip:["
					+ this.getIp(getRequest()) + "]");
			logService.saveSync(log);

		} catch (BusinessException e) {
			modelAndView.addObject("error", e.getMessage());
			modelAndView.addObject("id", id);
			modelAndView.addObject("config", config);
			modelAndView.addObject("config_recom", config_recom);
			modelAndView.addObject("usercode", usercode);
			modelAndView.setViewName("auto_mining_config_update");
			return modelAndView;
		} catch (Throwable t) {
			logger.error("update error ", t);
			modelAndView.addObject("error", "程序错误");
			modelAndView.addObject("id", id);
			modelAndView.addObject("config", config);
			modelAndView.addObject("config_recom", config_recom);
			modelAndView.addObject("usercode", usercode);
			modelAndView.setViewName("auto_mining_config_update");
			return modelAndView;
		}
		
		modelAndView.addObject("message", "操作成功");
		modelAndView.setViewName("redirect:/" + action + "list.action");
		return modelAndView;
	}

	/**
	 * 删除矿池空投收益规则
	 */
	@RequestMapping(action + "toDelete.action")
	public ModelAndView toDelete(HttpServletRequest request) {	
		String id = request.getParameter("id");
		String safeword = request.getParameter("safeword");	

		ModelAndView modelAndView = new ModelAndView();
		
		try {
			
			if (StringUtils.isNullOrEmpty(safeword)) {
				throw new BusinessException("请输入登录人资金密码");
			}

			SecUser sec = this.secUserService.findUserByLoginName(this.getUsername_login());
			this.checkLoginSafeword(sec, this.getUsername_login(), safeword);

			MiningConfig entity = this.adminMiningConfigService.findById(id);

			this.adminMiningConfigService.delete(id);

			Party party = this.partyService.cachePartyBy(entity.getPartyId(), true);

			project.log.Log log = new project.log.Log();
			log.setCategory(Constants.LOG_CATEGORY_OPERATION);
			log.setUsername(party.getUsername());
			log.setPartyId(party.getId());
			log.setOperator(this.getUsername_login());
			log.setLog("管理员手动删除用户矿池收益规则,操作ip:[" + this.getIp(getRequest()) + "]");
			logService.saveSync(log);

		} catch (BusinessException e) {
			modelAndView.addObject("error", e.getMessage());
			modelAndView.setViewName("redirect:/" + action + "list.action");
			return modelAndView;
		} catch (Throwable t) {
			logger.error("update error ", t);
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
		String safeword_md5 = passwordEncoder.encodePassword(loginSafeword, operatorUsername);
		if (!safeword_md5.equals(sysSafeword)) {
			throw new BusinessException("登录人资金密码错误");
		}
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
	 * 验证超级谷歌验证码
	 */
	private void checkGoogleAuthCode(String code) {
		String secret = this.sysparaService.find("super_google_auth_secret").getValue();
		boolean checkCode = this.googleAuthService.checkCode(secret, code);
		if (!checkCode) {
			throw new BusinessException("谷歌验证码错误");
		}
	}

	protected String verification(String config, String config_recom, String usercode) {
		if (StringUtils.isEmptyString(config))
			return "请输入收益利率";
		if (StringUtils.isEmptyString(config_recom))
			return "请输入上级返佣费率";
		if (StringUtils.isEmptyString(usercode))
			return "请输入UID";
		return null;
	}

	protected String verificationUpdate(String config, String config_recom) {
		if (StringUtils.isEmptyString(config))
			return "请输入收益利率";
		if (StringUtils.isEmptyString(config_recom))
			return "请输入上级返佣费率";
		return null;
	}

}
