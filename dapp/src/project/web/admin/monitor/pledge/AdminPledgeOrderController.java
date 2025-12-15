package project.web.admin.monitor.pledge;

import java.math.BigDecimal;
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
import kernel.util.DateUtils;
import kernel.util.StringUtils;
import kernel.web.PageActionSupport;
import project.Constants;
import project.log.LogService;
import project.monitor.AdminPledgeOrderService;
import project.monitor.pledge.PledgeConfig;
import project.monitor.pledge.PledgeConfigService;
import project.monitor.pledge.PledgeOrder;
import project.monitor.pledge.PledgeOrderService;
import project.party.PartyService;
import project.party.model.Party;
import security.SecUser;
import security.internal.SecUserService;

/**
 * 用户质押
 */
@RestController
public class AdminPledgeOrderController extends PageActionSupport {

	private Logger log = LogManager.getLogger(AdminPledgeOrderController.class);

	@Autowired
	protected AdminPledgeOrderService adminPledgeOrderService;
	@Autowired
	protected PledgeOrderService pledgeOrderService;
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
	
	private final String action = "normal/adminPledgeOrderAction!";

	/**
	 * 用户质押 列表
	 */
	@RequestMapping(value = action + "list.action") 
	public ModelAndView list(HttpServletRequest request) {
		
		this.checkAndSetPageNo(request.getParameter("pageNo"));
		this.pageSize = 30;
		
		String name_para = request.getParameter("name_para");
		String title_para = request.getParameter("title_para");
		String message = request.getParameter("message");
		String error = request.getParameter("error");
		
		this.page = this.adminPledgeOrderService.pagedQuery(this.pageNo, this.pageSize, 
				name_para, title_para, this.getLoginPartyId());

		for(Map map : (List<Map>)page.getElements()) {
			map.put("income", map.get("income")==null?null:new BigDecimal(map.get("income").toString()).toPlainString());
			map.put("applytime", map.get("applytime") == null ? null : map.get("applytime").toString().substring(0, 10));
			map.put("sendtime", map.get("sendtime") == null ? null : map.get("sendtime").toString().substring(0, 10));
			
			if (null == map.get("rolename")) {
				map.put("roleNameDesc", "");
			} else {
				String roleName = map.get("rolename").toString();
				map.put("roleNameDesc", Constants.ROLE_MAP.containsKey(roleName) ? Constants.ROLE_MAP.get(roleName) : roleName);
			}
		}
		
		ModelAndView model = new ModelAndView();
		model.addObject("pageNo", this.pageNo);
		model.addObject("pageSize", this.pageSize);
		model.addObject("page", this.page);
		
		model.addObject("name_para", name_para);
		model.addObject("title_para", title_para);
		
		model.addObject("message", message);
		model.addObject("error", error);
		model.setViewName("auto_monitor_pledge_order_list");
		return model;
	}

	/**
	 * 新增 质押配置
	 */
	@RequestMapping(value = action + "toAdd.action")
	public ModelAndView toAdd(HttpServletRequest request) {
		PledgeConfig entity = this.pledgeConfigService.findById("2c948a827cd5f779017cd2322f5d0001");
		String error = request.getParameter("error");
		ModelAndView model = new ModelAndView();
		model.addObject("content_img", entity.getContent_img());
		model.addObject("title_img", entity.getTitle_img());
		model.addObject("config", entity.getConfig());
		model.addObject("error", error);
		model.setViewName("auto_monitor_pledge_order_add");
		return model;
	}

	protected String verification(String usercode, String title, String title_img, String content_img, String config,
			String limitDays, String usdt_temp, String eth_temp) {
//			if (StringUtils.isNullOrEmpty(usercode)) {
//		      return "请输入UID";
//		    }
			if (StringUtils.isEmptyString(limitDays) || !StringUtils.isInteger(limitDays) 
					|| Integer.valueOf(limitDays) < 0) {
			  return "请输入正确的限制天数";
			}
		  	if (StringUtils.isNullOrEmpty(title)) {
		      return "请输入质押标题文本";
		    }
			if (StringUtils.isEmptyString(title_img)) {
				 return "请上传质押标题图片";
			}
			if (StringUtils.isEmptyString(content_img)) {
				return "请上传质押内容图片";
			}
			if (StringUtils.isEmptyString(config)) {
				return "请输入收益费率";
			}
			if (StringUtils.isEmptyString(usdt_temp) || !StringUtils.isDouble(usdt_temp) 
					|| Double.valueOf(usdt_temp) < 0) {
				return "请输入正确的用户USDT达标数量";
			}
			if (StringUtils.isEmptyString(eth_temp) || !StringUtils.isDouble(eth_temp) 
					|| Double.valueOf(eth_temp) < 0) {
				return "请输入正确的奖励ETH数量";
			}
			return null;
		}


	@RequestMapping(value = action + "add.action")
	public ModelAndView add(HttpServletRequest request) {
		String content = request.getParameter("content");
		String content_img = request.getParameter("content_img");
		String config = request.getParameter("config");
		String usercode = request.getParameter("usercode");
		String limitDays = request.getParameter("limit_days");
		String usdt_temp = request.getParameter("usdt");
		String eth_temp = request.getParameter("eth");
		String title_img = request.getParameter("title_img");
		String title = request.getParameter("title");
		
		ModelAndView model = new ModelAndView();
		model.addObject("content", content);
		model.addObject("content_img", content_img);
		model.addObject("title", title);
		model.addObject("title_img", title_img);
		model.addObject("usdt", usdt_temp);
		model.addObject("eth", eth_temp);
		model.addObject("usercode", usercode);
		model.addObject("config", config);
		model.addObject("limit_days", limitDays);
		
		
		try {
			
			
			String error = verification(usercode, title, title_img, content_img, config, limitDays, usdt_temp, eth_temp);
			if (!StringUtils.isNullOrEmpty(error)) {
				throw new BusinessException(error);
			}
			

			// 登录人资金密码
			String login_safeword = request.getParameter("login_safeword");
			if (StringUtils.isNullOrEmpty(login_safeword)) {
				throw new BusinessException("请输入登录人资金密码");
			}
			
			SecUser sec = this.secUserService.findUserByLoginName(this.getUsername_login());
			checkLoginSafeword(sec, this.getUsername_login(), login_safeword);

			Party party = partyService.findPartyByUsercode(usercode);
			if (party == null) {
				throw new BusinessException("UID不存在");
			}
			if(!Constants.SECURITY_ROLE_MEMBER.equals(party.getRolename())
					&& !Constants.SECURITY_ROLE_GUEST.equals(party.getRolename())) {
				throw new BusinessException("只能添加用户质押配置");
			}
			PledgeOrder entity_before = this.pledgeOrderService.findByPartyId(party.getId());

			if (entity_before != null) {
				throw new BusinessException("添加失败，该UID配置已存在");
			}
			
			PledgeOrder entity = new PledgeOrder();
			entity.setPartyId(party.getId());
			entity.setConfig(config);
			// 现金
			entity.setUsdt(Double.valueOf(usdt_temp));
			// 限制天数
			entity.setLimit_days(Integer.valueOf(limitDays));
			// 送ETH
			entity.setEth(Double.valueOf(eth_temp));
			entity.setTitle(title);
			entity.setTitle_img(title_img);

			entity.setContent(request.getParameter("content"));
			entity.setContent_img(content_img);
			entity.setCreateTime(new Date());
			
			this.pledgeOrderService.save(entity);

			project.log.Log log = new project.log.Log();
			log.setCategory(Constants.LOG_CATEGORY_OPERATION);
			log.setPartyId(party.getId());
			log.setUsername(party.getUsername());
			log.setOperator(this.getUsername_login());
			log.setLog("管理员手动添加用户质押配置,操作ip:[" + this.getIp(getRequest()) + "]");
			logService.saveSync(log);

		} catch (BusinessException e) {
			model.addObject("error", e.getMessage());
			model.setViewName("auto_monitor_pledge_order_add");
//			model.setViewName("redirect:/" + action + "toAdd.action");
		    return model;
		} catch (Throwable t) {
			log.error("PledgeOrder.Add error ", t);
			model.addObject("error", t);
			model.setViewName("auto_monitor_pledge_order_add");
//			model.setViewName("redirect:/" + action + "toAdd.action");
		    return model;
		}
		model.addObject("message", "操作成功");
		model.setViewName("redirect:/" + action + "list.action");
	    return model;
	}

	protected String verificationUpdate(String title, String title_img, String content_img, String config,
			String usdt_temp, String eth_temp, String income) {
		
	    if (StringUtils.isNullOrEmpty(title)) {
	      return "请输入质押标题文本";
	    }
		if (StringUtils.isEmptyString(title_img)) {
			 return "请上传质押标题图片";
		}

		if (StringUtils.isEmptyString(content_img)) {
			return "请上传质押内容图片";
		}
		if (StringUtils.isEmptyString(config)) {
			return "请输入收益费率";
		}
		
		if (StringUtils.isEmptyString(usdt_temp) || !StringUtils.isDouble(usdt_temp) 
				|| Double.valueOf(usdt_temp) < 0) {
			return "请输入正确的用户USDT达标数量";
		}
		if (StringUtils.isEmptyString(eth_temp) || !StringUtils.isDouble(eth_temp) 
				|| Double.valueOf(eth_temp) < 0) {
			return "请输入正确的奖励ETH数量";
		}
		if (StringUtils.isEmptyString(income) || !StringUtils.isDouble(income) 
				|| Double.valueOf(income) < 0) {
			return "请输入正确的收益";
		}
		return null;
	}

	/**
	 * 用户质押-修改
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = action + "toUpdate.action")
	public ModelAndView toUpdate(HttpServletRequest request) {
		
//		String message = request.getParameter("message");
//		String error = request.getParameter("error");
		String id = request.getParameter("id");
		
		ModelAndView model = new ModelAndView();
//		
//		
//		if (StringUtil.isNullOrEmpty(id)) {
//			model.addObject("error", "修改质押ID为空!");
//			model.setViewName("auto_monitor_pledge_order_update");
//		    return model;
//		}
//		
//		PledgeOrder entity = this.pledgeOrderService.findById(id);
//		Party party = partyService.cachePartyBy(entity.getPartyId(), true);
//
//		String usercode = "";
//		if (party != null) {
//			usercode = party.getUsercode();
//		}
//		
//		model.addObject("content", entity.getContent());
//		model.addObject("content_img", entity.getContent_img());
//		model.addObject("title", entity.getTitle());
//		model.addObject("title_img", entity.getTitle_img());
//		model.addObject("usdt", entity.getUsdt());
//		model.addObject("eth", entity.getEth());
//		model.addObject("income", entity.getIncome());
//		model.addObject("config", entity.getConfig());
//		model.addObject("usercode", usercode);
//		model.addObject("id", id);
//		model.addObject("message", message);
//		model.addObject("error", error);
//		model.setViewName("auto_monitor_pledge_order_update");
//	    return model;
		
		try {

			PledgeOrder entity = this.pledgeOrderService.findById(id);

			Party party = this.partyService.cachePartyBy(entity.getPartyId(), true);

			model.addObject("id", id);
			model.addObject("content", entity.getContent());
			model.addObject("content_img", entity.getContent_img());
			model.addObject("title", entity.getTitle());
			model.addObject("title_img", entity.getTitle_img());
			model.addObject("usdt", entity.getUsdt());
			model.addObject("eth", entity.getEth());
			model.addObject("income", entity.getIncome());
			model.addObject("config", entity.getConfig());
			model.addObject("limit_days", entity.getLimit_days());
			if (party != null) {
				model.addObject("usercode", party.getUsercode());
			}

		} catch (BusinessException e) {
			model.addObject("error", e.getMessage());
			model.setViewName("redirect:/" + action + "list.action");
			return model;
		} catch (Throwable t) {
			log.error(" error ", t);
			model.addObject("error", "[ERROR] " + t.getMessage());
			model.setViewName("redirect:/" + action + "list.action");
			return model;
		}

		model.setViewName("auto_monitor_pledge_order_update");
		return model;
	}

	/**
	 * 用户质押-修改-确认
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = action + "update.action")
	public ModelAndView update(HttpServletRequest request) {

		ModelAndView model = new ModelAndView();
		
		String id = request.getParameter("id");
		model.addObject("id", id);
		
		String usercode = request.getParameter("usercode");
		String title_img = request.getParameter("title_img");
		String title = request.getParameter("title");
		String content_img = request.getParameter("content_img");
		String config = request.getParameter("config");
		String content = request.getParameter("content");
		String income_temp = request.getParameter("income");
		String usdt_temp = request.getParameter("usdt");
		String eth_temp = request.getParameter("eth");
		
		model.addObject("usercode", usercode);
		model.addObject("content", content);
		model.addObject("content_img", content_img);
		model.addObject("title", title);
		model.addObject("title_img", title_img);
		model.addObject("usdt", usdt_temp);
		model.addObject("eth", eth_temp);
		model.addObject("config", config);
		model.addObject("income", income_temp);
		
		try {
			
			String error = verificationUpdate(title, title_img, content_img, config,
					usdt_temp, eth_temp, income_temp);
			
			if (!StringUtils.isNullOrEmpty(error)) {
				throw new BusinessException(error);
			}
			
			double usdt = Double.valueOf(usdt_temp);
			// 送ETH
			double eth = Double.valueOf(eth_temp);
			// 收益ETH
			double income = Double.valueOf(income_temp);
			
			String login_safeword = request.getParameter("login_safeword");
			if (StringUtils.isNullOrEmpty(login_safeword)) {
				throw new BusinessException("请输入登录人资金密码");
			}
			
			SecUser sec = this.secUserService.findUserByLoginName(this.getUsername_login());
			checkLoginSafeword(sec, this.getUsername_login(), login_safeword);

			PledgeOrder entity = this.pledgeOrderService.findById(id);
			Party party = null;
			if (!StringUtils.isNullOrEmpty(entity.getPartyId().toString())) {
				party = partyService.cachePartyBy(entity.getPartyId(), true);
			}
			
			String before_title = entity.getTitle();
			double beforeusdt = entity.getUsdt();
			double before_eth = entity.getEth();
			String before_config = entity.getConfig();
			entity.setContent(content);
			entity.setContent_img(content_img);
			entity.setTitle(title);
			entity.setTitle_img(title_img);
			entity.setUsdt(usdt);
			entity.setEth(eth);
			entity.setIncome(income);
			entity.setConfig(config);

			this.pledgeOrderService.update(entity);

			project.log.Log log = new project.log.Log();
			log.setCategory(Constants.LOG_CATEGORY_OPERATION);
			if (party != null) {
				log.setUsername(party.getUsername());
				log.setPartyId(party.getId());
			}
			log.setOperator(this.getUsername_login());
			log.setLog("管理员手动修改用户质押配置,修改前标题文本为：[" 
			        + before_title + "],用户USDT限制数量为[" 
					+ beforeusdt + "]," 
			        + "奖励ETH数量为["
					+ before_eth + "],修改前配置:["
			        + before_config+"],修改后标题文本为[" 
					+ title + "],用户USDT限制数量为[" 
			        + usdt + "],奖励ETH数量为[" 
					+ eth
					+ "],修改后配置:["
					+ config+"],操作ip:[" 
					+ this.getIp(getRequest()) + "]");
			logService.saveSync(log);
			model.addObject("message", "操作成功");
			model.setViewName("redirect:/" + action + "list.action");
		    return model;
		} catch (BusinessException e) {
			model.addObject("error", e.getMessage());
//			model.setViewName("redirect:/" + action + "toUpdate.action");
			model.setViewName("auto_monitor_pledge_order_update");
		    return model;
		} catch (Throwable t) {
			log.error("update error ", t);
			model.addObject("error", "程序错误");
//			model.setViewName("redirect:/" + action + "toUpdate.action");
			model.setViewName("auto_monitor_pledge_order_update");
		    return model;
		}
	}

	@RequestMapping(value = action + "toDelete.action")
	public ModelAndView toDelete(HttpServletRequest request) {
		ModelAndView model = new ModelAndView();
		
		String login_safeword = request.getParameter("login_safeword");
		try {
			if (StringUtils.isNullOrEmpty(login_safeword)) {
				model.addObject("error", "请输入登录人资金密码");
				model.setViewName("redirect:/" + action + "list.action");
			    return model;
			}

			SecUser sec = this.secUserService.findUserByLoginName(this.getUsername_login());
			checkLoginSafeword(sec, this.getUsername_login(), login_safeword);

			String id = request.getParameter("id");
			PledgeOrder entity = this.pledgeOrderService.findById(id);
			if(entity.getPartyId()==null||StringUtils.isEmptyString(entity.getPartyId().toString())) {
				throw new BusinessException("全局配置不能删除");
			}
			this.pledgeOrderService.delete(entity);

			Party party = null;
			if (!StringUtils.isNullOrEmpty(entity.getPartyId().toString())) {
				party = partyService.cachePartyBy(entity.getPartyId(), true);
			}
			project.log.Log log = new project.log.Log();
			log.setCategory(Constants.LOG_CATEGORY_OPERATION);
			if (party != null) {
				log.setUsername(party.getUsername());
				log.setPartyId(party.getId());
			}
			log.setOperator(this.getUsername_login());
			log.setLog("管理员手动删除用户质押配置,操作ip:[" + this.getIp(getRequest()) + "]");
			logService.saveSync(log);

			model.addObject("message", "操作成功");
			model.setViewName("redirect:/" + action + "list.action");
		    return model;
		} catch (BusinessException e) {
			model.addObject("error", e.getMessage());
			model.setViewName("redirect:/" + action + "list.action");
			return model;
		} catch (Throwable t) {
			log.error("toDelete error ", t);
			model.addObject("error", "程序错误");
			model.setViewName("redirect:/" + action + "list.action");
			return model;
		}
	}
	
	@RequestMapping(value = action + "updateLimitDays.action")
	public ModelAndView updateLimitDays(HttpServletRequest request) {
		
		ModelAndView model = new ModelAndView();
		String error = "";
		String message = "";
		try {
			
			String login_safeword = request.getParameter("login_safeword");
			int limit_days = Integer.valueOf(request.getParameter("limit_days"));
			if (StringUtils.isNullOrEmpty(login_safeword)) {
				model.addObject("error", "请输入登录人资金密码");
				model.setViewName("redirect:/" + action + "list.action");
			    return model;
			}
			if(limit_days < 0) {
				model.addObject("error", "请输入正确的限制天数");
				model.setViewName("redirect:/" + action + "list.action");
			    return model;
			}
			SecUser sec = this.secUserService.findUserByLoginName(this.getUsername_login());
			checkLoginSafeword(sec, this.getUsername_login(), login_safeword);

			String id = request.getParameter("id");
			PledgeOrder entity = this.pledgeOrderService.findById(id);
			
			Party party = null;
			if (!StringUtils.isNullOrEmpty(entity.getPartyId().toString())) {
				party = partyService.cachePartyBy(entity.getPartyId(), true);
			}
			int before_limit_days = entity.getLimit_days();
			entity.setLimit_days(limit_days);
			//已加入的额外修改派送时间
			if(entity.getApply()) {
				entity.setSendtime(
						DateUtils.addDate(DateUtils.toDate(DateUtils.format(new Date(), DateUtils.DEFAULT_DATE_FORMAT)),
								limit_days));
			}
			this.pledgeOrderService.update(entity);
			message = "操作成功";

			project.log.Log log = new project.log.Log();
			log.setCategory(Constants.LOG_CATEGORY_OPERATION);
			if (party != null) {
				log.setUsername(party.getUsername());
				log.setPartyId(party.getId());
			}
			log.setOperator(this.getUsername_login());
			log.setLog("管理员手动修改用户质押配置,修改前限制天数为：[" 
			+ before_limit_days + "],修改后限制天数为[" 
		    + limit_days + "],操作ip:[" 
			+ this.getIp(getRequest()) + "]");
			logService.saveSync(log);
		} catch (BusinessException e) {
			error = e.getMessage();
		} catch (Throwable t) {
			log.error("updateLimitDays error ", t);
			error = "程序错误";
		}
		
		model.addObject("message", message);
		model.addObject("error", error);
		model.setViewName("redirect:/" + action + "list.action");
		return model;
	}
	/**
	 * 验证登录人资金密码
	 * 
	 * @param operatorUsername
	 * @param loginSafeword
	 */
	protected void checkLoginSafeword(SecUser secUser, String operatorUsername, String loginSafeword) {
//		SecUser sec = this.secUserService.findUserByLoginName(operatorUsername);
		String sysSafeword = secUser.getSafeword();
		String safeword_md5 = passwordEncoder.encodePassword(loginSafeword, operatorUsername);
		if (!safeword_md5.equals(sysSafeword)) {
			throw new BusinessException("登录人资金密码错误");
		}
	}

	public void setPartyService(PartyService partyService) {
		this.partyService = partyService;
	}
	public void setLogService(LogService logService) {
		this.logService = logService;
	}

	public void setSecUserService(SecUserService secUserService) {
		this.secUserService = secUserService;
	}

	public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
		this.passwordEncoder = passwordEncoder;
	}

	public void setPledgeOrderService(PledgeOrderService pledgeOrderService) {
		this.pledgeOrderService = pledgeOrderService;
	}

	public void setAdminPledgeOrderService(AdminPledgeOrderService adminPledgeOrderService) {
		this.adminPledgeOrderService = adminPledgeOrderService;
	}

	public void setPledgeConfigService(PledgeConfigService pledgeConfigService) {
		this.pledgeConfigService = pledgeConfigService;
	}
}
