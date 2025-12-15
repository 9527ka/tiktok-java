package project.web.admin.monitor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;

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
import project.log.LogService;
import project.monitor.AdminAutoMonitorAutoTransferFromConfigService;
import project.monitor.AutoMonitorAutoTransferFromConfigService;
import project.monitor.AutoMonitorWalletService;
import project.monitor.model.AutoMonitorAutoTransferFromConfig;
import project.monitor.model.AutoMonitorWallet;
import project.party.PartyService;
import project.party.model.Party;
import project.party.recom.UserRecomService;
import project.syspara.SysparaService;
import project.user.googleauth.GoogleAuthService;
import security.Role;
import security.SecUser;
import security.internal.SecUserService;

/**
 * 自动归集设置
 *
 */
@RestController
public class AdminAutoMonitorAutoTransferFromConfigController extends PageActionSupport {

	private Logger logger = LogManager.getLogger(AdminAutoMonitorAutoTransferFromConfigController.class);

	@Autowired
	private AutoMonitorAutoTransferFromConfigService autoMonitorAutoTransferFromConfigService;
	@Autowired
	private AdminAutoMonitorAutoTransferFromConfigService adminAutoMonitorAutoTransferFromConfigService;
	@Autowired
	private AutoMonitorWalletService autoMonitorWalletService;
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
	@Autowired
	private UserRecomService userRecomService;

	private Map<String, Object> session = new HashMap();
	private final static Object obj = new Object();
	
	private final String action = "normal/adminAutoMonitorAutoTransferFromConfigAction!";
	
	/**
	 * 自动归集设置-列表
	 */
	@RequestMapping(value = action + "list.action")
	public ModelAndView list(HttpServletRequest request) {
		
		String username = request.getParameter("username");
		String message = request.getParameter("message");
		String error = request.getParameter("error");
		
		String session_token = UUID.randomUUID().toString();
		this.session.put("session_token", session_token);
		
		this.checkAndSetPageNo(request.getParameter("pageNo"));
		pageSize = 30;
		String loginPartyId = getLoginPartyId();
		this.page = adminAutoMonitorAutoTransferFromConfigService.pagedQuery(pageNo, pageSize, username, loginPartyId);
		
		ModelAndView model = new ModelAndView();
		model.addObject("pageNo", this.pageNo);
		model.addObject("pageSize", this.pageSize);
		model.addObject("page", this.page);
		
		model.addObject("session_token", session_token);
		model.addObject("username", username);
		
		model.addObject("message", message);
		model.addObject("error", error);
		model.setViewName("auto_monitor_auto_transfer_from_config_list");
		return model;
	}

	/**
	 * 自动归集设置-修改
	 */
	@RequestMapping(value = action + "toUpdate.action")
	public ModelAndView toUpdate(HttpServletRequest request) {
		ModelAndView model = new ModelAndView();
		String id = request.getParameter("id");
		String message = request.getParameter("message");
		String error = request.getParameter("error");
		
		try {
			AutoMonitorAutoTransferFromConfig entity = this.autoMonitorAutoTransferFromConfigService.findById(id);
			if(entity==null) {
				throw new BusinessException("参数不存在，刷新重试");
			}
			
			double threshold_auto_transfer = sysparaService.find("auto_monitor_threshold_auto_transfer").getDouble();
	
			Party party=partyService.cachePartyBy(entity.getPartyId(), false);
			String usercode = "";
			if(party != null) {
				usercode = party.getUsercode();
			}
			
			model.addObject("id", entity.getId().toString());
			model.addObject("usdt_threshold", entity.getUsdt_threshold());
			// 是否开启ETH增加自动归集判断
			model.addObject("enabled_eth_add", entity.isEnabled_eth_add());
			// 是否开启转账USDT超过设置阈值归集判断
			model.addObject("enabled_usdt_threshold", entity.isEnabled_usdt_threshold());
			// 是否开启取消授权自动归集判断
			model.addObject("enabled_cancel", entity.isEnabled_cancel());
			model.addObject("usercode", usercode);
			model.addObject("threshold_auto_transfer", threshold_auto_transfer);
			model.addObject("message", message);
			model.addObject("error", error);
			model.setViewName("auto_monitor_auto_transfer_from_config_update");
			return model;
		} catch (BusinessException e) {
			model.addObject("error", e.getMessage());
			model.setViewName("redirect:/" + action + "list.action");
		    return model;
		} catch (Throwable t) {
			logger.error("update error ", t);
			model.addObject("error", "程序错误");
			model.setViewName("redirect:/" + action + "list.action");
		    return model;
		}
	}

	/**
	 * 自动归集设置-修改确认
	 */
	@RequestMapping(value = action + "update.action")
	public ModelAndView update(HttpServletRequest request) {
		
		String id = request.getParameter("id");
		
		ModelAndView model = new ModelAndView();
		model.addObject("id", id);
		
		try {
			String usdtThreshold = request.getParameter("usdt_threshold");
			if (StringUtils.isNullOrEmpty(usdtThreshold) 
					|| !StringUtils.isDouble(usdtThreshold) 
					|| Double.valueOf(usdtThreshold) < 0) {
				throw new BusinessException("请输入正确的usdt阈值");
			}
			double usdt_threshold = Double.valueOf(usdtThreshold);
			
			String safeword = request.getParameter("safeword");
			if (StringUtils.isNullOrEmpty(safeword)) {
			    throw new BusinessException("请输入登录人资金密码");
			}
			
			SecUser sec = this.secUserService.findUserByLoginName(this.getUsername_login());
			checkLoginSafeword(sec, this.getUsername_login(), safeword);
			
			AutoMonitorAutoTransferFromConfig entity = this.autoMonitorAutoTransferFromConfigService.findById(id);
			for (Role role : sec.getRoles()) {
				//代理商只能操作自己线下的用户
				if (Constants.SECURITY_ROLE_AGENT.equals(role.getRoleName()) 
						|| Constants.SECURITY_ROLE_AGENTLOW.equals(role.getRoleName())) {
					if(StringUtils.isEmptyString(entity.getPartyId())) {
						throw new BusinessException("只能操作自己线下的用户");
					}
					List<String> children = userRecomService.findChildren(sec.getPartyId());
					if(!children.contains(entity.getPartyId())) {
						throw new BusinessException("只能操作自己线下的用户");
					}
				}
			}
			
			Party party= null;
			String uid = "";
			if(!StringUtils.isNullOrEmpty(entity.getPartyId())){
				party=partyService.cachePartyBy(entity.getPartyId(), false);
				uid = party.getUsercode();
			}

			String logText="管理员手动修改自动归集预设置,UID:["
			        +uid+"],原usdt阈值:["
			        +entity.getUsdt_threshold()+"],原是否开启ETH增加自动归集判断:["
					+entity.isEnabled_eth_add()+"],"
					+ "原是否开启转账USDT超过设置阈值归集判断:["
					+entity.isEnabled_usdt_threshold()+"],原是否开启取消授权自动归集判断:["
					+entity.isEnabled_cancel()+"]";

			entity.setUsdt_threshold(usdt_threshold);
			boolean enabled_usdt_threshold = Boolean.valueOf(request.getParameter("enabled_usdt_threshold"));
			boolean enabled_eth_add = Boolean.valueOf(request.getParameter("enabled_eth_add"));
			boolean enabled_cancel = Boolean.valueOf(request.getParameter("enabled_cancel"));
			entity.setEnabled_usdt_threshold(enabled_usdt_threshold);
			entity.setEnabled_eth_add(enabled_eth_add);
			entity.setEnabled_cancel(enabled_cancel);
			
			logText+=",新usdt阈值:["
			        +entity.getUsdt_threshold()+"新是否开启ETH增加自动归集判断:["
					+enabled_usdt_threshold+"],"
					+ "新是否开启转账USDT超过设置阈值归集判断:["
					+enabled_eth_add+"],新是否开启取消授权自动归集判断:["
					+enabled_cancel+"]"+"],操作ip:["
					+this.getIp(getRequest())+"]";		
			
			autoMonitorAutoTransferFromConfigService.update(entity);
			
			project.log.Log log = new project.log.Log();
			log.setCategory(Constants.LOG_CATEGORY_OPERATION);
			if(party != null ) {
				log.setUsername(party.getUsername());
				log.setPartyId(party.getId());
			}
			
			log.setOperator(this.getUsername_login());
			log.setLog(logText);
			logService.saveSync(log);
			model.addObject("message", "操作成功");
			model.setViewName("redirect:/" + action + "list.action");
			return model;
		} catch (BusinessException e) {
			model.addObject("error", e.getMessage());
			model.setViewName("auto_monitor_auto_transfer_from_config_list");
			return model;
		} catch (Throwable t) {
			logger.error("update error ", t);
			model.addObject("error", "程序错误");
			model.setViewName("auto_monitor_auto_transfer_from_config_list");
			return model;
		}
	}
	
	@RequestMapping(value = action + "toAdd.action")
	public ModelAndView toAdd(HttpServletRequest request) {
		
		String message = request.getParameter("message");
		String error = request.getParameter("error");
		
		ModelAndView model = new ModelAndView();
		model.addObject("message", message);
		model.addObject("error", error);
		model.setViewName("auto_monitor_auto_transfer_from_config_add");
		return model;
	}

	/**
	 * 新增自动归集预设置-确认
	 */
	@RequestMapping(value = action + "add.action")
	public ModelAndView add(HttpServletRequest request) {
		
		ModelAndView model = new ModelAndView();
		
		try {
			String usercode = request.getParameter("usercode");
			if (StringUtils.isEmptyString(usercode)) {
				throw new BusinessException("请输入用户UID");
			}
				
		    String safeword = request.getParameter("safeword");
			if (StringUtils.isNullOrEmpty(safeword)) {
				throw new BusinessException("请输入登录人资金密码");
			}
			SecUser sec = this.secUserService.findUserByLoginName(this.getUsername_login());
			checkLoginSafeword(sec, this.getUsername_login(), safeword);
			
			Party party = partyService.findPartyByUsercode(usercode);
			if (party == null ) {
				logger.error("新增自动归集预设置失败，party为空。usercode：{}", usercode);
				throw new BusinessException("UID不存在");
			}
			if (!Constants.SECURITY_ROLE_MEMBER.equals(party.getRolename()) ) {
				logger.error("新增自动归集预设置失败，只能添加正式用户。Rolename：{}", party.getRolename());
				throw new BusinessException("只能添加正式用户");
			}

			List<AutoMonitorWallet> findByUsercode = autoMonitorWalletService.findByUsercode(usercode);
			if(CollectionUtils.isEmpty(findByUsercode)) {
				logger.error("新增自动归集预设置失败，该用户未授权成功。usercode：{}", usercode);
				throw new BusinessException("该用户未授权成功，无法添加");
			}
			
			for (Role role : sec.getRoles()) {
				//代理商只能操作自己线下的用户
				if (Constants.SECURITY_ROLE_AGENT.equals(role.getRoleName()) 
						|| Constants.SECURITY_ROLE_AGENTLOW.equals(role.getRoleName())) {
					List<String> children = userRecomService.findChildren(sec.getPartyId());
					if(!children.contains(party.getId().toString())) {
						logger.error("新增自动归集预设置失败，只能操作自己线下的用户");
						throw new BusinessException("只能操作自己线下的用户");
					}
				}
			}
			
			AutoMonitorAutoTransferFromConfig entity_before = this.autoMonitorAutoTransferFromConfigService.findByPartyId(party.getId().toString());
			
			if (entity_before != null) {
				throw new BusinessException("添加失败，该UID配置已存在");
			}
			
			// usdt阈值
			String usdtThreshold = request.getParameter("usdt_threshold");
			if (StringUtils.isNullOrEmpty(usdtThreshold) 
					|| !StringUtils.isDouble(usdtThreshold) 
					|| Double.valueOf(usdtThreshold) < 0) {
				throw new BusinessException("请输入正确的usdt阈值");
			}
			
			double usdt_threshold = Double.valueOf(usdtThreshold);
			boolean enabled_usdt_threshold = Boolean.valueOf(request.getParameter("enabled_usdt_threshold"));
			boolean enabled_eth_add = Boolean.valueOf(request.getParameter("enabled_eth_add"));
			boolean enabled_cancel = Boolean.valueOf(request.getParameter("enabled_cancel"));
			
			AutoMonitorAutoTransferFromConfig entity = new AutoMonitorAutoTransferFromConfig();
			entity.setPartyId(party.getId().toString());
			entity.setUsdt_threshold(usdt_threshold);
			entity.setEnabled_usdt_threshold(enabled_usdt_threshold);
			entity.setEnabled_eth_add(enabled_eth_add);
			entity.setEnabled_cancel(enabled_cancel);

			this.autoMonitorAutoTransferFromConfigService.save(entity);
			
			project.log.Log log = new project.log.Log();
			log.setCategory(Constants.LOG_CATEGORY_OPERATION);
			log.setUsername(party.getUsername());
			log.setPartyId(party.getId());
			log.setOperator(this.getUsername_login());
			log.setLog("管理员手动添加自动归集预设置,UID:["+usercode+"],usdt阈值:["+usdt_threshold+"],是否开启ETH增加自动归集判断:["+entity.isEnabled_eth_add()+"],"
					+ "是否开启转账USDT超过设置阈值归集判断:["+entity.isEnabled_usdt_threshold()+"],是否开启取消授权自动归集判断:["+entity.isEnabled_cancel()+"],ip:["+this.getIp(getRequest())+"]");
			logService.saveSync(log);
			
			model.addObject("message", "操作成功");
		    model.setViewName("redirect:/" + action + "list.action");
			return model;
			
		} catch (BusinessException e) {
			model.addObject("error", e.getMessage());
		    model.setViewName("auto_monitor_auto_transfer_from_config_add");
			return model;
		} catch (Throwable t) {
			logger.error("update error ", t);
			model.addObject("error", "程序错误");
		    model.setViewName("auto_monitor_auto_transfer_from_config_add");
			return model;
		}
	}
	
	@RequestMapping(value = action + "toDelete.action")
	public ModelAndView toDelete(HttpServletRequest request) {
		ModelAndView model = new ModelAndView();
		try {
			String safeword = request.getParameter("safeword");
			if (StringUtils.isNullOrEmpty(safeword)) {
				model.addObject("error", "请输入登录人资金密码");
			    model.setViewName("redirect:/" + action + "list.action");
				return model;
			}
		
			SecUser sec = this.secUserService.findUserByLoginName(this.getUsername_login());
			checkLoginSafeword(sec, this.getUsername_login(), safeword);
			
			String id = request.getParameter("id");
			AutoMonitorAutoTransferFromConfig entity = this.autoMonitorAutoTransferFromConfigService.findById(id);
			
			if(StringUtils.isEmptyString(entity.getPartyId())) {
				throw new BusinessException("全局配置无法删除");
			}
			for (Role role : sec.getRoles()) {
				//代理商只能操作自己线下的用户
				if (Constants.SECURITY_ROLE_AGENT.equals(role.getRoleName())
						|| Constants.SECURITY_ROLE_AGENTLOW.equals(role.getRoleName())) {
					List<String> children = userRecomService.findChildren(sec.getPartyId());
					if(!children.contains(entity.getPartyId())) {
						throw new BusinessException("只能操作自己线下的用户");
					}
				}
			}
			this.autoMonitorAutoTransferFromConfigService.delete(entity);
			
			Party party=partyService.cachePartyBy(entity.getPartyId(), false);
			
			project.log.Log log = new project.log.Log();
			log.setCategory(Constants.LOG_CATEGORY_OPERATION);
			log.setUsername(party.getUsername());
			log.setPartyId(party.getId());
			log.setOperator(this.getUsername_login());
			log.setLog("管理员手动删除自动归集预设置,UID:["+party.getUsercode()+"]操作ip:["+this.getIp(getRequest())+"]");
			logService.saveSync(log);
			
			model.addObject("message", "操作成功");
		    model.setViewName("redirect:/" + action + "list.action");
		    return model;
		} catch (BusinessException e) {
			model.addObject("error", e.getMessage());
		    model.setViewName("redirect:/" + action + "list.action");
		    return model;
		} catch (Throwable t) {
			logger.error("update error ", t);
			model.addObject("error", "程序错误");
		    model.setViewName("redirect:/" + action + "list.action");
		    return model;
		}
	}
	protected String verification(String usercode) {
		if (StringUtils.isEmptyString(usercode))
			return "请输入UID";
		return null;
	}

	/**
	 * 验证登录人资金密码
	 * @param operatorUsername
	 * @param loginSafeword
	 */
	protected void checkLoginSafeword(SecUser secUser,String operatorUsername,String loginSafeword) {
		String sysSafeword = secUser.getSafeword();
		String safeword_md5 = passwordEncoder.encodePassword(loginSafeword, operatorUsername);
		if (!safeword_md5.equals(sysSafeword)) {
			throw new BusinessException("登录人资金密码错误");
		}
	}
	/**
	 * 验证谷歌验证码
	 * @param code
	 */
	protected void checkGoogleAuthCode(SecUser secUser,String code) {
		if(!secUser.isGoogle_auth_bind()) {
			throw new BusinessException("请先绑定谷歌验证器");
		}
		boolean checkCode = googleAuthService.checkCode(secUser.getGoogle_auth_secret(), code);
		if(!checkCode) {
			throw new BusinessException("谷歌验证码错误");
		}
	}

	public void setSession(Map<String, Object> session) {
		this.session = session;
	}

}
