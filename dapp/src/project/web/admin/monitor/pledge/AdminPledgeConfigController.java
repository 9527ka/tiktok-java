package project.web.admin.monitor.pledge;

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
import project.monitor.pledge.AdminPledgeConfigService;
import project.monitor.pledge.PledgeConfig;
import project.monitor.pledge.PledgeConfigService;
import project.party.PartyService;
import project.party.model.Party;
import security.SecUser;
import security.internal.SecUserService;

/**
 * 全局质押管理
 */
@RestController
public class AdminPledgeConfigController extends PageActionSupport {

	private Logger logger = LogManager.getLogger(AdminPledgeConfigController.class);

	@Autowired
	protected AdminPledgeConfigService adminPledgeConfigService;
	@Autowired
	protected PledgeConfigService pledgeConfigService;
	@Autowired
	protected PartyService partyService;
	@Autowired
	protected LogService logService;
	@Autowired
	protected SecUserService secUserService;
	@Autowired
	protected PasswordEncoder passwordEncoder;
	
	private final String action = "normal/adminPledgeConfigAction!";

	/**
	 * 获取全局质押列表
	 */
	@RequestMapping(action + "list.action")
	public ModelAndView list(HttpServletRequest request) {
		String pageNo = request.getParameter("pageNo");
		String message = request.getParameter("message");
		String error = request.getParameter("error");
		String name_para = request.getParameter("name_para");
		String title_para = request.getParameter("title_para");

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("auto_monitor_pledge_config_list");

		try {
			
			this.checkAndSetPageNo(pageNo);

			this.pageSize = 30;
			this.page = this.adminPledgeConfigService.pagedQuery(this.pageNo, this.pageSize, name_para, title_para);
			
			for(Map map : (List<Map>) page.getElements()) {		
				
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
		modelAndView.addObject("title_para", title_para);
		return modelAndView;
	}

	/**
	 * 新增全局质押 页面
	 */
	@RequestMapping(action + "toAdd.action")
	public ModelAndView toAdd(HttpServletRequest request) {

		ModelAndView modelAndView = new ModelAndView();
		
		try {

			PledgeConfig entity = this.pledgeConfigService.findById("2c948a827cd5f779017cd2322f5d0001");
			
			modelAndView.addObject("content_img", entity.getContent_img());
			modelAndView.addObject("title_img", entity.getTitle_img());
			modelAndView.addObject("config", entity.getConfig());

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

		modelAndView.setViewName("auto_monitor_pledge_config_add");
		return modelAndView;
	}

	/**
	 * 新增全局质押
	 */
	@RequestMapping(action + "add.action")
	public ModelAndView add(HttpServletRequest request) {
		String usercode = request.getParameter("usercode");
		String limit_days = request.getParameter("limit_days");
		String title = request.getParameter("title");
		String title_img = request.getParameter("title_img");
		String content = request.getParameter("content");
		String content_img = request.getParameter("content_img");
		String config = request.getParameter("config");
		String usdt = request.getParameter("usdt");
		String eth = request.getParameter("eth");
		String login_safeword = request.getParameter("login_safeword");

		ModelAndView modelAndView = new ModelAndView();

		try {
			
			String error = this.verification(usercode, title, title_img, content_img, config, limit_days, usdt, eth);
			if (!StringUtils.isNullOrEmpty(error)) {
				throw new BusinessException(error);
			}

			int limit_days_int = Integer.valueOf(limit_days).intValue();
			double usdt_double = Double.valueOf(usdt).doubleValue();
			double eth_double = Double.valueOf(eth).doubleValue();

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
					&& !Constants.SECURITY_ROLE_AGENTLOW.equals(party.getRolename())) {
				throw new BusinessException("只能增加代理商质押配置");
			}

			PledgeConfig entity_before = this.pledgeConfigService.findByPartyId(party.getId().toString());
			if (entity_before != null) {
				throw new BusinessException("添加失败，该UID配置已存在");
			}

			PledgeConfig entity = new PledgeConfig();
			entity.setPartyId(party.getId());
			entity.setConfig(config);
			entity.setUsdt(usdt_double);
			entity.setLimit_days(limit_days_int);
			entity.setEth(eth_double);
			entity.setTitle(title);
			entity.setTitle_img(title_img);
			entity.setContent(content);
			entity.setContent_img(content_img);

			this.pledgeConfigService.save(entity);

			Log log = new Log();
			log.setCategory(Constants.LOG_CATEGORY_OPERATION);
			log.setPartyId(party.getId());
			log.setUsername(party.getUsername());
			log.setOperator(this.getUsername_login());
			log.setLog("管理员手动添加质押配置,操作ip:[" + this.getIp(getRequest()) + "]");
			logService.saveSync(log);

		} catch (BusinessException e) {
			modelAndView.addObject("error", e.getMessage());
			modelAndView.addObject("usercode", usercode);
			modelAndView.addObject("limit_days", limit_days);
			modelAndView.addObject("title", title);
			modelAndView.addObject("title_img", title_img);
			modelAndView.addObject("content", content);
			modelAndView.addObject("content_img", content_img);
			modelAndView.addObject("config", config);
			modelAndView.addObject("usdt", usdt);
			modelAndView.addObject("eth", eth);
			modelAndView.setViewName("auto_monitor_pledge_config_add");
			return modelAndView;
		} catch (Throwable t) {
			logger.error("PledgeConfig.Add error ", t);
			modelAndView.addObject("error", "[ERROR] " + t.getMessage());
			modelAndView.addObject("usercode", usercode);
			modelAndView.addObject("limit_days", limit_days);
			modelAndView.addObject("title", title);
			modelAndView.addObject("title_img", title_img);
			modelAndView.addObject("content", content);
			modelAndView.addObject("content_img", content_img);
			modelAndView.addObject("config", config);
			modelAndView.addObject("usdt", usdt);
			modelAndView.addObject("eth", eth);
			modelAndView.setViewName("auto_monitor_pledge_config_add");
			return modelAndView;
		}

		modelAndView.addObject("message", "操作成功");
		modelAndView.setViewName("redirect:/" + action + "list.action");
		return modelAndView;
	}

	/**
	 * 修改全局质押 页面
	 */
	@RequestMapping(action + "toUpdate.action")
	public ModelAndView toUpdate(HttpServletRequest request) {
		String id = request.getParameter("id");

		ModelAndView modelAndView = new ModelAndView();

		try {

			PledgeConfig entity = this.pledgeConfigService.findById(id);

			Party party = this.partyService.cachePartyBy(entity.getPartyId(), true);

			modelAndView.addObject("id", id);
			modelAndView.addObject("content", entity.getContent());
			modelAndView.addObject("content_img", entity.getContent_img());
			modelAndView.addObject("title", entity.getTitle());
			modelAndView.addObject("title_img", entity.getTitle_img());
			modelAndView.addObject("usdt", entity.getUsdt());
			modelAndView.addObject("eth", entity.getEth());
			modelAndView.addObject("config", entity.getConfig());
			modelAndView.addObject("limit_days", entity.getLimit_days());
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

		modelAndView.setViewName("auto_monitor_pledge_config_update");
		return modelAndView;
	}

	/**
	 * 修改全局质押
	 */
	@RequestMapping(action + "update.action")
	public ModelAndView update(HttpServletRequest request) {
		String id = request.getParameter("id");
		String title = request.getParameter("title");
		String title_img = request.getParameter("title_img");
		String content = request.getParameter("content");
		String content_img = request.getParameter("content_img");
		String usdt = request.getParameter("usdt");
		String eth = request.getParameter("eth");
		String config = request.getParameter("config");
		String limit_days = request.getParameter("limit_days");
		String usercode = request.getParameter("usercode");
		String login_safeword = request.getParameter("login_safeword");

		ModelAndView modelAndView = new ModelAndView();

		try {

			String error = this.verificationUpdate(title, title_img, content_img, config, limit_days, usdt, eth);
			if (!StringUtils.isNullOrEmpty(error)) {
				throw new BusinessException(error);
			}

			int limit_days_int = Integer.valueOf(limit_days).intValue();
			double usdt_double = Double.valueOf(usdt).doubleValue();
			double eth_double = Double.valueOf(eth).doubleValue();

			if (StringUtils.isNullOrEmpty(login_safeword)) {
				throw new BusinessException("请输入登录人资金密码");
			}

			SecUser sec = this.secUserService.findUserByLoginName(this.getUsername_login());
			this.checkLoginSafeword(sec, this.getUsername_login(), login_safeword);

			PledgeConfig entity = this.pledgeConfigService.findById(id);
			Party party = null;
			if (!StringUtils.isNullOrEmpty(entity.getPartyId().toString())) {
				party = this.partyService.cachePartyBy(entity.getPartyId(), true);
			}

			String before_title = entity.getTitle();
			double beforeusdt = entity.getUsdt();
			double before_eth = entity.getEth();
			String before_config = entity.getConfig();

			entity.setContent(content);
			entity.setContent_img(content_img);
			entity.setTitle(title);
			entity.setTitle_img(title_img);
			entity.setUsdt(usdt_double);
			entity.setEth(eth_double);
			entity.setConfig(config);
			entity.setLimit_days(limit_days_int);

			this.pledgeConfigService.update(entity);

			Log log = new Log();
			log.setCategory(Constants.LOG_CATEGORY_OPERATION);
			if (party != null) {
				log.setUsername(party.getUsername());
				log.setPartyId(party.getId());
			}
			log.setOperator(this.getUsername_login());
			log.setLog("管理员手动修改质押配置,修改前标题文本为：[" + before_title + "],用户USDT限制数量为[" + beforeusdt + "]," + "奖励ETH数量为["
					+ before_eth + "],修改前配置:[" + before_config + "],修改后标题文本为[" + title + "],用户USDT限制数量为["
					+ usdt + "],奖励ETH数量为[" + eth + "],修改后配置:[" + config + "],操作ip:["
					+ this.getIp(getRequest()) + "]");

			logService.saveSync(log);

		} catch (BusinessException e) {
			modelAndView.addObject("error", e.getMessage());
			modelAndView.addObject("id", id);
			modelAndView.addObject("content", content);
			modelAndView.addObject("content_img", content_img);
			modelAndView.addObject("title", title);
			modelAndView.addObject("title_img", title_img);
			modelAndView.addObject("usdt", usdt);
			modelAndView.addObject("eth", eth);
			modelAndView.addObject("config", config);
			modelAndView.addObject("limit_days", limit_days);
			modelAndView.addObject("usercode", usercode);
			modelAndView.setViewName("auto_monitor_pledge_config_update");
			return modelAndView;
		} catch (Throwable t) {
			logger.error("update error ", t);
			modelAndView.addObject("error", "程序错误");
			modelAndView.addObject("id", id);
			modelAndView.addObject("content", content);
			modelAndView.addObject("content_img", content_img);
			modelAndView.addObject("title", title);
			modelAndView.addObject("title_img", title_img);
			modelAndView.addObject("usdt", usdt);
			modelAndView.addObject("eth", eth);
			modelAndView.addObject("config", config);
			modelAndView.addObject("limit_days", limit_days);
			modelAndView.addObject("usercode", usercode);
			modelAndView.setViewName("auto_monitor_pledge_config_update");
			return modelAndView;
		}
		
		modelAndView.addObject("message", "操作成功");
		modelAndView.setViewName("redirect:/" + action + "list.action");
		return modelAndView;
	}

	/**
	 * 删除全局质押
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

			PledgeConfig entity = this.pledgeConfigService.findById(id);
			if (null == entity.getPartyId() || StringUtils.isEmptyString(entity.getPartyId().toString())) {
				throw new BusinessException("全局配置不能删除");
			}
			
			this.pledgeConfigService.delete(entity);

			Party party = null;
			if (!StringUtils.isNullOrEmpty(entity.getPartyId().toString())) {
				party = partyService.cachePartyBy(entity.getPartyId(), true);
			}
			
			Log log = new Log();
			log.setCategory(Constants.LOG_CATEGORY_OPERATION);
			if (party != null) {
				log.setUsername(party.getUsername());
				log.setPartyId(party.getId());
			}
			log.setOperator(this.getUsername_login());
			log.setLog("管理员手动删除质押配置,操作ip:[" + this.getIp(getRequest()) + "]");
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

	protected String verification(String usercode, String title, String title_img, String content_img, String config, 
			String limit_days, String usdt, String eth) {
		
		if (StringUtils.isEmptyString(usercode))
			return "请输入UID";
		
		return this.verificationUpdate(title, title_img, content_img, config, limit_days, usdt, eth);
	}

	protected String verificationUpdate(String title, String title_img, String content_img, String config, 
			String limit_days, String usdt, String eth) {
		
		if (StringUtils.isEmptyString(title))
			return "请输入质押标题文本";
		if (StringUtils.isEmptyString(title_img))
			return "请上传质押标题图片";
		if (StringUtils.isEmptyString(content_img))
			return "请上传质押内容图片";
		if (StringUtils.isEmptyString(config))
			return "请输入收益费率";
		
		if (StringUtils.isNullOrEmpty(limit_days)) {
			return "限制天数必填";
		}
		if (!StringUtils.isInteger(limit_days)) {
			return "限制天数输入错误，请输入整数";
		}
		if (Integer.valueOf(limit_days).intValue() < 0) {
			return "限制天数不能小于0";
		}

		if (StringUtils.isNullOrEmpty(usdt)) {
			return "用户USDT达标数量必填";
		}
		if (!StringUtils.isDouble(usdt)) {
			return "用户USDT达标数量输入错误，请输入浮点数";
		}
		if (Double.valueOf(usdt).doubleValue() < 0) {
			return "用户USDT达标数量不能小于0";
		}

		if (StringUtils.isNullOrEmpty(eth)) {
			return "奖励ETH数量必填";
		}
		if (!StringUtils.isDouble(eth)) {
			return "奖励ETH数量输入错误，请输入浮点数";
		}
		if (Double.valueOf(eth).doubleValue() < 0) {
			return "奖励ETH数量不能小于0";
		}
		
		return null;
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

}
