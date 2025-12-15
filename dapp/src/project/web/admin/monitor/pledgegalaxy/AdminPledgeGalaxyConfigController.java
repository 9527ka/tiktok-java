package project.web.admin.monitor.pledgegalaxy;

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
import kernel.util.StringUtils;
import kernel.web.PageActionSupport;
import project.Constants;
import project.log.Log;
import project.log.LogService;
import project.monitor.pledgegalaxy.AdminPledgeGalaxyConfigService;
import project.monitor.pledgegalaxy.PledgeGalaxyConfig;
import project.monitor.pledgegalaxy.PledgeGalaxyConfigService;
import project.monitor.pledgegalaxy.PledgeGalaxyRedisKeys;
import project.party.PartyService;
import project.party.model.Party;
import project.redis.RedisHandler;
import project.syspara.SysparaService;
import security.SecUser;
import security.internal.SecUserService;

/**
 * 质押2.0配置
 */
@RestController
public class AdminPledgeGalaxyConfigController extends PageActionSupport {

	private Logger logger = LogManager.getLogger(AdminPledgeGalaxyConfigController.class);

	@Autowired
	protected AdminPledgeGalaxyConfigService adminPledgeGalaxyConfigService;
	@Autowired
	protected PledgeGalaxyConfigService pledgeGalaxyConfigService;
	@Autowired
	protected PartyService partyService;
	@Autowired
	protected LogService logService;
	@Autowired
	protected SecUserService secUserService;
	@Autowired
	protected PasswordEncoder passwordEncoder;
	@Autowired	
	protected RedisHandler redisHandler;
	@Autowired	
	protected SysparaService sysparaService;
	
	private final String action = "normal/adminPledgeGalaxyConfigAction!";

	/**
	 * 获取 质押2.0配置 列表
	 */
	@RequestMapping(action + "list.action")
	public ModelAndView list(HttpServletRequest request) {
		String pageNo = request.getParameter("pageNo");
		String message = request.getParameter("message");
		String error = request.getParameter("error");
		String name_para = request.getParameter("name_para");
		String rolename_para = request.getParameter("rolename_para");

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("auto_monitor_pledge_galaxy_config_list");

		try {
			
			this.checkAndSetPageNo(pageNo);

			this.pageSize = 30;
			this.page = this.adminPledgeGalaxyConfigService.pagedQuery(this.pageNo, this.pageSize, name_para, rolename_para);
			
			for(Map map : (List<Map>) page.getElements()) {
				
				String staticIncomeForceValue = (String) map.get("static_income_force_value");
				staticIncomeForceValue = staticIncomeForceValue.replace("|", "<br/>|");
				staticIncomeForceValue = staticIncomeForceValue.replace("&", "<br />&");
				map.put("static_income_force_value", staticIncomeForceValue);
				
				String dynamicIncomeAssistValue = (String) map.get("dynamic_income_assist_value");
				dynamicIncomeAssistValue = dynamicIncomeAssistValue.replace("|", "<br/>|");
				map.put("dynamic_income_assist_value", dynamicIncomeAssistValue);
				
				String teamIncomeProfitRatio = (String) map.get("team_income_profit_ratio");
				teamIncomeProfitRatio = teamIncomeProfitRatio.replace("#", "<br/>#");
				map.put("team_income_profit_ratio", teamIncomeProfitRatio);
				
				if (null == map.get("rolename")) {
					map.put("roleNameDesc", "");
				} else {
					String roleName = map.get("rolename").toString();
					map.put("roleNameDesc", Constants.ROLE_MAP.containsKey(roleName) ? Constants.ROLE_MAP.get(roleName) : roleName);
				}
			}

			modelAndView.addObject("result", this.result);
			
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
		modelAndView.addObject("rolename_para", rolename_para);
		return modelAndView;
	}

	/**
	 * 新增 质押2.0配置 页面
	 */
	@RequestMapping(action + "toAdd.action")
	public ModelAndView toAdd(HttpServletRequest request) {

		ModelAndView modelAndView = new ModelAndView();
		
		String addView = "auto_monitor_pledge_galaxy_config_add";
		
		try {
			
			String projectType = this.sysparaService.find("project_type").getValue();
			if (StringUtils.isEmptyString(projectType)) {
				throw new BusinessException("系统参数错误");
			}
			if (projectType.equals("DAPP_EXCHANGE_SAFEPAL5")) {
				addView = "auto_monitor_pledge_galaxy_config_add_safepal5";
			} else if (projectType.equals("DAPP_EXCHANGE_IOEAI")) {
				addView = "auto_monitor_pledge_galaxy_config_add_ioeai";
			}

			PledgeGalaxyConfig entity = this.pledgeGalaxyConfigService.findById("2c948a827cd5f779017cd2322f5d0001");

			modelAndView.addObject("pledge_amount_min", entity.getPledgeAmountMin());
			modelAndView.addObject("pledge_amount_max", entity.getPledgeAmountMax());
			modelAndView.addObject("valid_recom_pledge_amount_min", entity.getValidRecomPledgeAmountMin());
			modelAndView.addObject("static_income_force_value", entity.getStaticIncomeForceValue());
			modelAndView.addObject("dynamic_income_assist_value", entity.getDynamicIncomeAssistValue());
			modelAndView.addObject("team_income_profit_ratio", entity.getTeamIncomeProfitRatio());

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

		modelAndView.setViewName(addView);
		return modelAndView;
	}

	/**
	 * 新增 质押2.0配置
	 */
	@RequestMapping(action + "add.action")
	public ModelAndView add(HttpServletRequest request) {
		String usercode = request.getParameter("usercode");
		String pledge_amount_min = request.getParameter("pledge_amount_min");
		String pledge_amount_max = request.getParameter("pledge_amount_max");
		String valid_recom_pledge_amount_min = request.getParameter("valid_recom_pledge_amount_min");
		String static_income_force_value = request.getParameter("static_income_force_value");
		String dynamic_income_assist_value = request.getParameter("dynamic_income_assist_value");
		String team_income_profit_ratio = request.getParameter("team_income_profit_ratio");
		String login_safeword = request.getParameter("login_safeword");

		ModelAndView modelAndView = new ModelAndView();
		
		String addView = "auto_monitor_pledge_galaxy_config_add";

		try {
			
			String projectType = this.sysparaService.find("project_type").getValue();
			if (StringUtils.isEmptyString(projectType)) {
				throw new BusinessException("系统参数错误");
			}
			if (projectType.equals("DAPP_EXCHANGE_SAFEPAL5")) {
				addView = "auto_monitor_pledge_galaxy_config_add_safepal5";
			} else if (projectType.equals("DAPP_EXCHANGE_IOEAI")) {
				addView = "auto_monitor_pledge_galaxy_config_add_ioeai";
			}
			
			String error = this.verification(usercode, pledge_amount_min, pledge_amount_max, valid_recom_pledge_amount_min, 
					static_income_force_value, dynamic_income_assist_value, team_income_profit_ratio);
			if (!StringUtils.isNullOrEmpty(error)) {
				throw new BusinessException(error);
			}

			double pledge_amount_min_double = Double.valueOf(pledge_amount_min).doubleValue();
			double pledge_amount_max_double = Double.valueOf(pledge_amount_max).doubleValue();
			double valid_recom_pledge_amount_min_double = Double.valueOf(valid_recom_pledge_amount_min).doubleValue();

			if (StringUtils.isNullOrEmpty(login_safeword)) {
				throw new BusinessException("请输入登录人资金密码");
			}

			SecUser sec = this.secUserService.findUserByLoginName(this.getUsername_login());
			this.checkLoginSafeword(sec, this.getUsername_login(), login_safeword);

			Party party = this.partyService.findPartyByUsercode(usercode);
			if (null == party) {
				throw new BusinessException("UID不存在");
			}

			if (!Constants.SECURITY_ROLE_AGENT.equals(party.getRolename()) 
					&& !Constants.SECURITY_ROLE_AGENTLOW.equals(party.getRolename())
					&& !Constants.SECURITY_ROLE_MEMBER.equals(party.getRolename())
					&& !Constants.SECURITY_ROLE_GUEST.equals(party.getRolename())
//					&& !Constants.SECURITY_ROLE_TEST.equals(party.getRolename())
					) {
				throw new BusinessException("只能增加代理商、正式用户或演示用户质押2.0配置");
			}

			PledgeGalaxyConfig entity_before = this.pledgeGalaxyConfigService.findByPartyId(party.getId().toString());
			if (entity_before != null) {
				throw new BusinessException("添加失败，该UID配置已存在");
			}

			PledgeGalaxyConfig entity = new PledgeGalaxyConfig();
			entity.setPartyId(party.getId().toString());
			entity.setPledgeAmountMin(pledge_amount_min_double);
			entity.setPledgeAmountMax(pledge_amount_max_double);
			entity.setValidRecomPledgeAmountMin(valid_recom_pledge_amount_min_double);
			entity.setStaticIncomeForceValue(static_income_force_value);
			entity.setDynamicIncomeAssistValue(dynamic_income_assist_value);
			entity.setTeamIncomeProfitRatio(team_income_profit_ratio);
			Date date = new Date();
			entity.setCreated(date);
			entity.setUpdated(date);

			this.pledgeGalaxyConfigService.save(entity);
			
			this.redisHandler.setSync(PledgeGalaxyRedisKeys.PLEDGE_GALAXY_CONFIG + party.getId().toString(), entity);

			Log log = new Log();
			log.setCategory(Constants.LOG_CATEGORY_OPERATION);
			log.setPartyId(party.getId());
			log.setUsername(party.getUsername());
			log.setOperator(this.getUsername_login());
			log.setLog("管理员手动添加质押2.0配置,操作ip:[" + this.getIp(getRequest()) + "]");
			this.logService.saveSync(log);

		} catch (BusinessException e) {
			modelAndView.addObject("error", e.getMessage());
			modelAndView.addObject("usercode", usercode);
			modelAndView.addObject("pledge_amount_min", pledge_amount_min);
			modelAndView.addObject("pledge_amount_max", pledge_amount_max);
			modelAndView.addObject("valid_recom_pledge_amount_min", valid_recom_pledge_amount_min);
			modelAndView.addObject("static_income_force_value", static_income_force_value);
			modelAndView.addObject("dynamic_income_assist_value", dynamic_income_assist_value);
			modelAndView.addObject("team_income_profit_ratio", team_income_profit_ratio);
			modelAndView.setViewName(addView);
			return modelAndView;
		} catch (Throwable t) {
			logger.error("PledgeConfig.Add error ", t);
			modelAndView.addObject("error", "[ERROR] " + t.getMessage());
			modelAndView.addObject("usercode", usercode);
			modelAndView.addObject("pledge_amount_min", pledge_amount_min);
			modelAndView.addObject("pledge_amount_max", pledge_amount_max);
			modelAndView.addObject("valid_recom_pledge_amount_min", valid_recom_pledge_amount_min);
			modelAndView.addObject("static_income_force_value", static_income_force_value);
			modelAndView.addObject("dynamic_income_assist_value", dynamic_income_assist_value);
			modelAndView.addObject("team_income_profit_ratio", team_income_profit_ratio);
			modelAndView.setViewName(addView);
			return modelAndView;
		}

		modelAndView.addObject("message", "操作成功");
		modelAndView.setViewName("redirect:/" + action + "list.action");
		return modelAndView;
	}

	/**
	 * 修改 质押2.0配置 页面
	 */
	@RequestMapping(action + "toUpdate.action")
	public ModelAndView toUpdate(HttpServletRequest request) {
		String id = request.getParameter("id");

		ModelAndView modelAndView = new ModelAndView();
		
		String updateView = "auto_monitor_pledge_galaxy_config_update";

		try {
			
			String projectType = this.sysparaService.find("project_type").getValue();
			if (StringUtils.isEmptyString(projectType)) {
				throw new BusinessException("系统参数错误");
			}
			if (projectType.equals("DAPP_EXCHANGE_SAFEPAL5")) {
				updateView = "auto_monitor_pledge_galaxy_config_update_safepal5";
			} else if (projectType.equals("DAPP_EXCHANGE_IOEAI")) {
				updateView = "auto_monitor_pledge_galaxy_config_update_ioeai";
			}

			PledgeGalaxyConfig entity = this.pledgeGalaxyConfigService.findById(id);

			Party party = this.partyService.cachePartyBy(entity.getPartyId(), true);

			modelAndView.addObject("id", id);
			modelAndView.addObject("pledge_amount_min", entity.getPledgeAmountMin());
			modelAndView.addObject("pledge_amount_max", entity.getPledgeAmountMax());
			modelAndView.addObject("valid_recom_pledge_amount_min", entity.getValidRecomPledgeAmountMin());
			modelAndView.addObject("static_income_force_value", entity.getStaticIncomeForceValue());
			modelAndView.addObject("dynamic_income_assist_value", entity.getDynamicIncomeAssistValue());
			modelAndView.addObject("team_income_profit_ratio", entity.getTeamIncomeProfitRatio());
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

		modelAndView.setViewName(updateView);
		return modelAndView;
	}

	/**
	 * 修改 质押2.0配置
	 */
	@RequestMapping(action + "update.action")
	public ModelAndView update(HttpServletRequest request) {
		String id = request.getParameter("id");
		String usercode = request.getParameter("usercode");
		String pledge_amount_min = request.getParameter("pledge_amount_min");
		String pledge_amount_max = request.getParameter("pledge_amount_max");
		String valid_recom_pledge_amount_min = request.getParameter("valid_recom_pledge_amount_min");
		String static_income_force_value = request.getParameter("static_income_force_value");
		String dynamic_income_assist_value = request.getParameter("dynamic_income_assist_value");
		String team_income_profit_ratio = request.getParameter("team_income_profit_ratio");
		String login_safeword = request.getParameter("login_safeword");

		ModelAndView modelAndView = new ModelAndView();
		
		String updateView = "auto_monitor_pledge_galaxy_config_update";

		try {
			
			String projectType = this.sysparaService.find("project_type").getValue();
			if (StringUtils.isEmptyString(projectType)) {
				throw new BusinessException("系统参数错误");
			}
			if (projectType.equals("DAPP_EXCHANGE_SAFEPAL5")) {
				updateView = "auto_monitor_pledge_galaxy_config_update_safepal5";
			} else if (projectType.equals("DAPP_EXCHANGE_IOEAI")) {
				updateView = "auto_monitor_pledge_galaxy_config_update_ioeai";
			}

			String error = this.verificationUpdate(pledge_amount_min, pledge_amount_max, valid_recom_pledge_amount_min, 
					static_income_force_value, dynamic_income_assist_value, team_income_profit_ratio);
			if (!StringUtils.isNullOrEmpty(error)) {
				throw new BusinessException(error);
			}

			double pledge_amount_min_double = Double.valueOf(pledge_amount_min).doubleValue();
			double pledge_amount_max_double = Double.valueOf(pledge_amount_max).doubleValue();
			double valid_recom_pledge_amount_min_double = Double.valueOf(valid_recom_pledge_amount_min).doubleValue();

			if (StringUtils.isNullOrEmpty(login_safeword)) {
				throw new BusinessException("请输入登录人资金密码");
			}

			SecUser sec = this.secUserService.findUserByLoginName(this.getUsername_login());
			this.checkLoginSafeword(sec, this.getUsername_login(), login_safeword);

			PledgeGalaxyConfig entity = this.pledgeGalaxyConfigService.findById(id);
			Party party = null;
			if (!StringUtils.isNullOrEmpty(entity.getPartyId().toString())) {
				party = this.partyService.cachePartyBy(entity.getPartyId(), true);
			}

			double before_pledge_amount_min = entity.getPledgeAmountMin();
			double before_pledge_amount_max = entity.getPledgeAmountMax();
			double before_valid_recom_pledge_amount_min = entity.getValidRecomPledgeAmountMin();
			String before_static_income_force_value = entity.getStaticIncomeForceValue();
			String before_dynamic_income_assist_value = entity.getDynamicIncomeAssistValue();
			String before_team_income_profit_ratio = entity.getTeamIncomeProfitRatio();

			entity.setPledgeAmountMin(pledge_amount_min_double);
			entity.setPledgeAmountMax(pledge_amount_max_double);
			entity.setValidRecomPledgeAmountMin(valid_recom_pledge_amount_min_double);
			entity.setStaticIncomeForceValue(static_income_force_value);
			entity.setDynamicIncomeAssistValue(dynamic_income_assist_value);
			entity.setTeamIncomeProfitRatio(team_income_profit_ratio);
			entity.setUpdated(new Date());

			this.pledgeGalaxyConfigService.update(entity);
			
			String partyId = entity.getPartyId();
			if (StringUtils.isNullOrEmpty(partyId)) {
				partyId = "";
			}
			
			this.redisHandler.setSync(PledgeGalaxyRedisKeys.PLEDGE_GALAXY_CONFIG + partyId, entity);

			Log log = new Log();
			log.setCategory(Constants.LOG_CATEGORY_OPERATION);
			if (party != null) {
				log.setUsername(party.getUsername());
				log.setPartyId(party.getId());
			}
			log.setOperator(this.getUsername_login());
			log.setLog("管理员手动修改质押2.0配置,修改前参与金额最小值为：[" + before_pledge_amount_min 
					+ "],修改前参与金额最大值为[" + before_pledge_amount_max  
					+ "],修改前有效下级质押金额最小值为[" + before_valid_recom_pledge_amount_min 
					+ "],修改前静态收益原力值为[" + before_static_income_force_value 
					+ "],修改前动态收益助力值为[" + before_dynamic_income_assist_value 
					+ "],修改前团队收益利润率为[" + before_team_income_profit_ratio 
					+ "],修改后参与金额最小值为[" + pledge_amount_min_double 
					+ "],修改后参与金额最大值为[" + pledge_amount_max_double 
					+ "],修改后有效下级质押金额最小值为[" + valid_recom_pledge_amount_min_double 
					+ "],修改后静态收益原力值为[" + static_income_force_value 
					+ "],修改后动态收益助力值为[" + dynamic_income_assist_value 
					+ "],修改后团队收益利润率为[" + team_income_profit_ratio 
					+ "],操作ip:[" + this.getIp(getRequest()) 
					+ "]");

			logService.saveSync(log);

		} catch (BusinessException e) {
			modelAndView.addObject("error", e.getMessage());
			modelAndView.addObject("id", id);
			modelAndView.addObject("usercode", usercode);			
			modelAndView.addObject("pledge_amount_min", pledge_amount_min);
			modelAndView.addObject("pledge_amount_max", pledge_amount_max);
			modelAndView.addObject("valid_recom_pledge_amount_min", valid_recom_pledge_amount_min);
			modelAndView.addObject("static_income_force_value", static_income_force_value);
			modelAndView.addObject("dynamic_income_assist_value", dynamic_income_assist_value);
			modelAndView.addObject("team_income_profit_ratio", team_income_profit_ratio);
			modelAndView.setViewName(updateView);
			return modelAndView;
		} catch (Throwable t) {
			logger.error("update error ", t);
			modelAndView.addObject("error", "程序错误");
			modelAndView.addObject("id", id);
			modelAndView.addObject("usercode", usercode);			
			modelAndView.addObject("pledge_amount_min", pledge_amount_min);		
			modelAndView.addObject("pledge_amount_max", pledge_amount_max);
			modelAndView.addObject("valid_recom_pledge_amount_min", valid_recom_pledge_amount_min);
			modelAndView.addObject("static_income_force_value", static_income_force_value);
			modelAndView.addObject("dynamic_income_assist_value", dynamic_income_assist_value);
			modelAndView.addObject("team_income_profit_ratio", team_income_profit_ratio);
			modelAndView.setViewName(updateView);
			return modelAndView;
		}
		
		modelAndView.addObject("message", "操作成功");
		modelAndView.setViewName("redirect:/" + action + "list.action");
		return modelAndView;
	}

	/**
	 * 删除 质押2.0配置
	 */
	@RequestMapping(action + "toDelete.action")
	public ModelAndView toDelete(HttpServletRequest request) {
		String id = request.getParameter("id");
		String login_safeword = request.getParameter("login_safeword");

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("redirect:/" + action + "list.action");
		
		try {
			
			if (StringUtils.isNullOrEmpty(login_safeword)) {
				throw new BusinessException("请输入登录人资金密码");
			}

			SecUser sec = this.secUserService.findUserByLoginName(this.getUsername_login());
			this.checkLoginSafeword(sec, this.getUsername_login(), login_safeword);

			PledgeGalaxyConfig entity = this.pledgeGalaxyConfigService.findById(id);
			if (null == entity.getPartyId() || StringUtils.isEmptyString(entity.getPartyId().toString())) {
				throw new BusinessException("全局配置不能删除");
			}
			
			this.pledgeGalaxyConfigService.delete(entity);
			
			this.redisHandler.remove(PledgeGalaxyRedisKeys.PLEDGE_GALAXY_CONFIG + entity.getPartyId());

			Party party = null;
			if (!StringUtils.isNullOrEmpty(entity.getPartyId())) {
				party = this.partyService.cachePartyBy(entity.getPartyId(), true);
			}
			
			Log log = new Log();
			log.setCategory(Constants.LOG_CATEGORY_OPERATION);
			if (party != null) {
				log.setUsername(party.getUsername());
				log.setPartyId(party.getId());
			}
			log.setOperator(this.getUsername_login());
			log.setLog("管理员手动删除质押2.0配置,操作ip:[" + this.getIp(getRequest()) + "]");
			logService.saveSync(log);

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

	protected String verification(String usercode, String pledge_amount_min, String pledge_amount_max, String valid_recom_pledge_amount_min, 
			String static_income_force_value, String dynamic_income_assist_value,  String team_income_profit_ratio) {
		
		if (StringUtils.isEmptyString(usercode))
			return "请输入UID";
		
		return this.verificationUpdate(pledge_amount_min, pledge_amount_max, valid_recom_pledge_amount_min, static_income_force_value, 
				dynamic_income_assist_value, team_income_profit_ratio);
	}

	protected String verificationUpdate(String pledge_amount_min, String pledge_amount_max, String valid_recom_pledge_amount_min, 
			String static_income_force_value, String dynamic_income_assist_value, String team_income_profit_ratio) {

		if (StringUtils.isNullOrEmpty(pledge_amount_min)) {
			return "参与金额最小值必填";
		}
		if (!StringUtils.isDouble(pledge_amount_min)) {
			return "参与金额最小值输入错误，请输入浮点数";
		}
		if (Double.valueOf(pledge_amount_min).doubleValue() <= 0) {
			return "参与金额最小值不能小于等于0";
		}

		if (StringUtils.isNullOrEmpty(pledge_amount_max)) {
			return "参与金额最大值必填";
		}
		if (!StringUtils.isDouble(pledge_amount_max)) {
			return "参与金额最大值输入错误，请输入浮点数";
		}
		if (Double.valueOf(pledge_amount_max).doubleValue() <= 0) {
			return "参与金额最大值不能小于等于0";
		}

		if (StringUtils.isNullOrEmpty(valid_recom_pledge_amount_min)) {
			return "有效下级质押金额最小值必填";
		}
		if (!StringUtils.isDouble(valid_recom_pledge_amount_min)) {
			return "有效下级质押金额最小值输入错误，请输入浮点数";
		}
		if (Double.valueOf(valid_recom_pledge_amount_min).doubleValue() <= 0) {
			return "有效下级质押金额最小值不能小于等于0";
		}
		
		if (Double.valueOf(pledge_amount_max).doubleValue() <= Double.valueOf(pledge_amount_min).doubleValue()) {
			return "参与金额最大值不能小于等于参与金额最小值";
		}
		
		if (StringUtils.isEmptyString(static_income_force_value)) {
			return "请输入静态收益原力值";
		}
		
		if (StringUtils.isEmptyString(dynamic_income_assist_value)) {
			return "请输入动态收益助力值";
		}
		
		if (StringUtils.isEmptyString(team_income_profit_ratio)) {
			return "请输入团队收益利润率";
		}
		
		return null;
	}

	/**
	 * 验证登录人资金密码
	 */
	protected void checkLoginSafeword(SecUser secUser, String operatorUsername, String loginSafeword) {
		String sysSafeword = secUser.getSafeword();
		String safeword_md5 = this.passwordEncoder.encodePassword(loginSafeword, operatorUsername);
		if (!safeword_md5.equals(sysSafeword)) {
			throw new BusinessException("登录人资金密码错误");
		}
	}
	
}
