package project.web.admin.monitor.pledgegalaxy;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
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
import project.monitor.AdminPledgeGalaxyOrderService;
import project.monitor.AdminPledgeGalaxyProfitService;
import project.monitor.AutoMonitorWalletService;
import project.monitor.model.AutoMonitorWallet;
import project.monitor.pledgegalaxy.PledgeGalaxyConfig;
import project.monitor.pledgegalaxy.PledgeGalaxyConfigService;
import project.monitor.pledgegalaxy.PledgeGalaxyOrder;
import project.monitor.pledgegalaxy.PledgeGalaxyOrderService;
import project.monitor.pledgegalaxy.PledgeGalaxyProfit;
import project.party.PartyService;
import project.party.model.Party;
import security.SecUser;
import security.internal.SecUserService;

/**
 * 质押2.0订单
 */
@RestController
public class AdminPledgeGalaxyOrderController extends PageActionSupport {

	private Logger logger = LogManager.getLogger(AdminPledgeGalaxyOrderController.class);

	@Autowired
	protected AdminPledgeGalaxyOrderService adminPledgeGalaxyOrderService;
	@Autowired
	protected PledgeGalaxyOrderService pledgeGalaxyOrderService;
	@Autowired
	protected PartyService partyService;
	@Autowired
	protected LogService logService;
	@Autowired
	protected PledgeGalaxyConfigService pledgeGalaxyConfigService;
	@Autowired
	protected SecUserService secUserService;
	@Autowired
	protected PasswordEncoder passwordEncoder;
	@Autowired
	AutoMonitorWalletService autoMonitorWalletService;
	@Autowired
	AdminPledgeGalaxyProfitService adminPledgeGalaxyProfitService;
	
	private final String action = "normal/adminPledgeGalaxyOrderAction!";

	/**
	 * 获取 质押2.0订单 列表
	 */
	@RequestMapping(value = action + "list.action") 
	public ModelAndView list(HttpServletRequest request) {
		String pageNo = request.getParameter("pageNo");
		String message = request.getParameter("message");
		String error = request.getParameter("error");
		String order_no_para = request.getParameter("order_no_para");
		String name_para = request.getParameter("name_para");
		String rolename_para = request.getParameter("rolename_para");
		String status_para = request.getParameter("status_para");
		String order_type = request.getParameter("order_type");

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("auto_monitor_pledge_galaxy_order_list");

		try {
			
			this.checkAndSetPageNo(pageNo);

			this.pageSize = 30;	
			
			Integer status_para_int = null;
			
			if (StringUtils.isNullOrEmpty(status_para)) {
				status_para_int = null;
			} else {
				status_para_int = Integer.valueOf(status_para);
			}
			
			Integer order_type_int = null;
			
			if (StringUtils.isNullOrEmpty(order_type)) {
				order_type_int = null;
			} else {
				order_type_int = Integer.valueOf(order_type);
			}
			
			this.page = this.adminPledgeGalaxyOrderService.pagedQuery(this.pageNo, this.pageSize, order_no_para, name_para, rolename_para, 
					status_para_int, order_type_int, this.getLoginPartyId());
			
			for(Map map : (List<Map>) page.getElements()) {
				
				if (null == map.get("rolename")) {
					map.put("roleNameDesc", "");
				} else {
					String roleName = map.get("rolename").toString();
					map.put("roleNameDesc", Constants.ROLE_MAP.containsKey(roleName) ? Constants.ROLE_MAP.get(roleName) : roleName);
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
		modelAndView.addObject("order_no_para", order_no_para);
		modelAndView.addObject("name_para", name_para);
		modelAndView.addObject("rolename_para", rolename_para);
		return modelAndView;
	}

	/**
	 * 新增 质押2.0订单 页面
	 */
	@RequestMapping(value = action + "toAdd.action")
	public ModelAndView toAdd(HttpServletRequest request) {

		ModelAndView modelAndView = new ModelAndView();
		
		try {
			
			PledgeGalaxyConfig config = this.pledgeGalaxyConfigService.findById("2c948a827cd5f779017cd2322f5d0001");			
			Map<String, String> validDaysMap = this.getValidDays(config);
			
			modelAndView.addObject("valid_days", validDaysMap);

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

		modelAndView.setViewName("auto_monitor_pledge_galaxy_order_add");
		return modelAndView;
	}

	/**
	 * 新增 质押2.0订单
	 */
	@RequestMapping(value = action + "add.action")
	public ModelAndView add(HttpServletRequest request) {
		String usercode = request.getParameter("usercode");
		String pledge_amount = request.getParameter("pledge_amount");
		String pledge_days = request.getParameter("pledge_days");
		String start_time = request.getParameter("start_time");
		String login_safeword = request.getParameter("login_safeword");
		
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject("usercode", usercode);
		modelAndView.addObject("pledge_amount", pledge_amount);
		modelAndView.addObject("pledge_days", pledge_days);
		modelAndView.addObject("start_time", start_time);
		
		Map<String, String> validDaysMap = new HashMap<String, String>();
		
		try {

			PledgeGalaxyConfig config = this.pledgeGalaxyConfigService.findById("2c948a827cd5f779017cd2322f5d0001");
			if (null == config) {
				throw new BusinessException("系统配置错误");
			}			
			
			double pledge_amount_min = config.getPledgeAmountMin();
			double pledge_amount_max = config.getPledgeAmountMax();
			validDaysMap = this.getValidDays(config);		
			
			String error = this.verification(usercode, pledge_amount_min, pledge_amount_max, pledge_amount, pledge_days, start_time);
			if (!StringUtils.isNullOrEmpty(error)) {
				throw new BusinessException(error);
			}
				
			if (!validDaysMap.keySet().contains(pledge_days)) {
				throw new BusinessException("质押天数未配置");
			}
			
			double pledge_amount_double = Double.valueOf(pledge_amount).doubleValue();
			Integer pledge_days_int = Integer.valueOf(pledge_days);			
			
			if (StringUtils.isNullOrEmpty(login_safeword)) {
				throw new BusinessException("请输入登录人资金密码");
			}
			
			SecUser sec = this.secUserService.findUserByLoginName(this.getUsername_login());
			this.checkLoginSafeword(sec, this.getUsername_login(), login_safeword);
			
			Party party = this.partyService.findPartyByUsercode(usercode);
			if (null == party) {
				throw new BusinessException("UID不存在");
			}
			
			if (!Constants.SECURITY_ROLE_MEMBER.equals(party.getRolename())
					&& !Constants.SECURITY_ROLE_GUEST.equals(party.getRolename())
//					&& !Constants.SECURITY_ROLE_TEST.equals(party.getRolename())
					) {
				throw new BusinessException("只能增加正式用户或演示用户质押2.0订单");
			}
			
			String address = party.getUsername().toLowerCase();
			AutoMonitorWallet entity = this.autoMonitorWalletService.findBy(address);
			if (null == entity ) {
				logger.error("管理员手动添加质押2.0订单失败，未发现授权记录，当前地址{}", address);
				throw new BusinessException("未发现授权记录");
			}
			if (entity.getSucceeded() != 1) {
				logger.error("管理员手动添加质押2.0订单失败，当前状态{}", entity.getSucceeded());
				throw new BusinessException("授权状态未成功");
			}
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

			Date date = sdf.parse(start_time);
			Calendar calendar = new GregorianCalendar();
			calendar.setTime(date);
			calendar.add(calendar.DATE, Integer.valueOf(pledge_days_int));
			Date expireTime = calendar.getTime();
			
			PledgeGalaxyOrder order = new PledgeGalaxyOrder();
			order.setPartyId(party.getId().toString());
			order.setAmount(pledge_amount_double);
			order.setDays(pledge_days_int);
			order.setStatus(new Integer(1));
			order.setStartTime(date);
			order.setExpireTime(expireTime);
			order.setCreateTime(new Date());
			order.setType(new Integer(1));

			this.pledgeGalaxyOrderService.saveBack(order, party.getRolename());

			project.log.Log log = new project.log.Log();
			log.setCategory(Constants.LOG_CATEGORY_OPERATION);
			log.setPartyId(party.getId());
			log.setUsername(party.getUsername());
			log.setOperator(this.getUsername_login());
			log.setLog("管理员手动添加质押2.0订单,操作ip:[" + this.getIp(getRequest()) + "]");
			this.logService.saveSync(log);
			
			modelAndView.addObject("valid_days", validDaysMap);
		
		} catch (BusinessException e) {
			modelAndView.addObject("error", e.getMessage());
			modelAndView.addObject("valid_days", validDaysMap);
			modelAndView.setViewName("auto_monitor_pledge_galaxy_order_add");
			return modelAndView;
		} catch (Throwable t) {
			logger.error(" error ", t);
			modelAndView.addObject("error", "[ERROR] " + t.getMessage());
			modelAndView.addObject("valid_days", validDaysMap);
			modelAndView.setViewName("auto_monitor_pledge_galaxy_order_add");
			return modelAndView;
		}
		
		modelAndView.addObject("message", "操作成功");
		modelAndView.setViewName("redirect:/" + action + "list.action");
		return modelAndView;
	}

	/**
	 * 修改 质押2.0订单 页面
	 */
	@RequestMapping(value = action + "toUpdate.action")
	public ModelAndView toUpdate(HttpServletRequest request) {
		String id = request.getParameter("id");

		ModelAndView modelAndView = new ModelAndView();
		
		try {

			PledgeGalaxyOrder order = this.pledgeGalaxyOrderService.findById(id);

			Party party = this.partyService.cachePartyBy(order.getPartyId(), true);
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

			modelAndView.addObject("id", id);
			modelAndView.addObject("pledge_amount", order.getAmount());
			modelAndView.addObject("pledge_days", order.getDays());
			modelAndView.addObject("start_time", sdf.format(order.getStartTime()));
			if (party != null) {
				modelAndView.addObject("usercode", party.getUsercode());
			}
			
			PledgeGalaxyConfig config = this.pledgeGalaxyConfigService.findById("2c948a827cd5f779017cd2322f5d0001");			
			Map<String, String> validDaysMap = this.getValidDays(config);

			modelAndView.addObject("valid_days", validDaysMap);

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

		modelAndView.setViewName("auto_monitor_pledge_galaxy_order_update");
		return modelAndView;
	}

	/**
	 * 修改 质押2.0订单
	 */
	@RequestMapping(value = action + "update.action")
	public ModelAndView update(HttpServletRequest request) {
		String id = request.getParameter("id");		
		String usercode = request.getParameter("usercode");
		String pledge_amount = request.getParameter("pledge_amount");
		String pledge_days = request.getParameter("pledge_days");
		String start_time = request.getParameter("start_time");
		String login_safeword = request.getParameter("login_safeword");

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject("id", id);
		modelAndView.addObject("usercode", usercode);
		modelAndView.addObject("pledge_amount", pledge_amount);
		modelAndView.addObject("pledge_days", pledge_days);
		modelAndView.addObject("start_time", start_time);
		
		Map<String, String> validDaysMap = new HashMap<String, String>();
		
		try {

			PledgeGalaxyConfig config = this.pledgeGalaxyConfigService.findById("2c948a827cd5f779017cd2322f5d0001");
			if (null == config) {
				throw new BusinessException("系统配置错误");
			}			
			
			double pledge_amount_min = config.getPledgeAmountMin();
			double pledge_amount_max = config.getPledgeAmountMax();
			validDaysMap = this.getValidDays(config);
			
			String error = this.verification(usercode, pledge_amount_min, pledge_amount_max, pledge_amount, pledge_days, start_time);
			if (!StringUtils.isNullOrEmpty(error)) {
				throw new BusinessException(error);
			}
			
			if (!validDaysMap.keySet().contains(pledge_days)) {
				throw new BusinessException("质押天数未配置");
			}
			
			double pledge_amount_double = Double.valueOf(pledge_amount).doubleValue();
			Integer pledge_days_int = Integer.valueOf(pledge_days);	
					
			if (StringUtils.isNullOrEmpty(login_safeword)) {
				throw new BusinessException("请输入登录人资金密码");
			}
			
			SecUser sec = this.secUserService.findUserByLoginName(this.getUsername_login());
			this.checkLoginSafeword(sec, this.getUsername_login(), login_safeword);

			PledgeGalaxyOrder order = this.pledgeGalaxyOrderService.findById(id);
			Party party = null;
			if (!StringUtils.isNullOrEmpty(order.getPartyId().toString())) {
				party = this.partyService.cachePartyBy(order.getPartyId(), true);
			}
			
			double before_pledge_amount = order.getAmount();
			Integer before_pledge_days = order.getDays();
			Date before_start_time = order.getStartTime();
			Date before_expire_time = order.getExpireTime();
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date startTime = sdf.parse(start_time);
			
			Calendar calendar = new GregorianCalendar();
			calendar.setTime(startTime);
			calendar.add(calendar.DATE, Integer.valueOf(pledge_days_int));
			Date expireTime = calendar.getTime();
//			Date nowTime = new Date();
			
//			if (nowTime.getTime() > expireTime.getTime()) {
//				throw new BusinessException("按要配置的质押天数计算，当前时间已超过质押到期时间，请选择更大的天数");
//			}
			
			order.setAmount(pledge_amount_double);
			order.setDays(pledge_days_int);
			order.setStartTime(startTime);
			order.setExpireTime(expireTime);

			this.pledgeGalaxyOrderService.update(order);

			project.log.Log log = new project.log.Log();
			log.setCategory(Constants.LOG_CATEGORY_OPERATION);
			if (party != null) {
				log.setUsername(party.getUsername());
				log.setPartyId(party.getId());
			}
			log.setOperator(this.getUsername_login());
			log.setLog("管理员手动修改质押2.0订单，"
					+ "订单号：[" + String.valueOf(order.getId()) + "]，"
					+ "修改前质押金额为：[" + before_pledge_amount + "]，"
					+ "修改后质押金额为：[" + pledge_amount_double + "]，"
					+ "修改前质押天数为：[" + before_pledge_days + "]，"
					+ "修改后质押天数为：[" + pledge_days_int + "]，"
					+ "修改前质押开始时间为：[" + before_start_time + "]，"
					+ "修改后质押开始时间为：[" + startTime + "]，"
					+ "修改前质押到期时间为：[" + before_expire_time + "]，"
					+ "修改后质押到期时间为：[" + expireTime + "]，"
					+ "操作ip：[" + this.getIp(getRequest()) + "]");
			this.logService.saveSync(log);

		} catch (BusinessException e) {
			modelAndView.addObject("error", e.getMessage());			
			modelAndView.addObject("valid_days", validDaysMap);
			modelAndView.setViewName("auto_monitor_pledge_galaxy_order_update");
			return modelAndView;
		} catch (Throwable t) {
			logger.error(" error ", t);
			modelAndView.addObject("error", "[ERROR] " + t.getMessage());			
			modelAndView.addObject("valid_days", validDaysMap);
			modelAndView.setViewName("auto_monitor_pledge_galaxy_order_update");
			return modelAndView;
		}
		
		modelAndView.addObject("message", "操作成功");
		modelAndView.setViewName("redirect:/" + action + "list.action");
		return modelAndView;
	}
	
	/**
	 * 删除 质押2.0订单
	 */
	@RequestMapping(value = action + "toDelete.action")
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
			
			PledgeGalaxyOrder order = this.pledgeGalaxyOrderService.findById(id);
			if (null == order) {
				throw new BusinessException("订单不存在");
			}
			
			this.pledgeGalaxyOrderService.delete(order);
			
			List<PledgeGalaxyProfit> profits = adminPledgeGalaxyProfitService.findByRelationOrderNo(String.valueOf(order.getId()));
			for (PledgeGalaxyProfit profit : profits) {
				adminPledgeGalaxyProfitService.delete(profit);
			}

			Party party = null;
			if (!StringUtils.isNullOrEmpty(order.getPartyId().toString())) {
				party = this.partyService.cachePartyBy(order.getPartyId(), true);
			}
			
			project.log.Log log = new project.log.Log();
			log.setCategory(Constants.LOG_CATEGORY_OPERATION);
			if (party != null) {
				log.setUsername(party.getUsername());
				log.setPartyId(party.getId());
			}
			log.setOperator(this.getUsername_login());
			log.setLog("管理员删除质押2.0订单，订单号：[" + String.valueOf(order.getId()) + "]，操作ip：[" + this.getIp(getRequest()) + "]");
			this.logService.saveSync(log);
		} catch (BusinessException e) {
			modelAndView.addObject("error", e.getMessage());
			return modelAndView;
		} catch (Throwable t) {
			logger.error("toDelete error ", t);
			modelAndView.addObject("error", "程序错误");
			return modelAndView;
		}

		modelAndView.addObject("message", "操作成功");
	    return modelAndView;
	}

	/**
	 * 审核赎回
	 */
	@RequestMapping(action + "close.action")
	public ModelAndView close(HttpServletRequest request) {
		String uuid_close = request.getParameter("uuid_close");
		String pass_or_fail = request.getParameter("pass_or_fail");

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("redirect:/" + action + "list.action");
		
		try {
			
			if (StringUtils.isNullOrEmpty(uuid_close)) {
				throw new BusinessException("订单不存在");
			}

			if (StringUtils.isNullOrEmpty(pass_or_fail)) {
				throw new BusinessException("参数错误");
			}			
			
			PledgeGalaxyOrder entity = this.pledgeGalaxyOrderService.findById(uuid_close);
			if (null == entity) {
				throw new BusinessException("订单不存在");
			}
			
			// TODO true 通过 false 不通过
			this.pledgeGalaxyOrderService.saveClose(entity, Boolean.valueOf(pass_or_fail));

			Party party = null;
			if (!StringUtils.isNullOrEmpty(entity.getPartyId().toString())) {
				party = this.partyService.cachePartyBy(entity.getPartyId(), true);
			}
			
			Log log = new Log();
			log.setCategory(Constants.LOG_CATEGORY_OPERATION);
			if (party != null) {
				log.setUsername(party.getUsername());
				log.setPartyId(party.getId());
			}
			log.setOperator(this.getUsername_login());
			log.setLog("管理员手动赎回质押2.0订单，订单号：[" + String.valueOf(entity.getId()) + "]，操作ip：[" + this.getIp(getRequest()) + "]");
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

	protected String verification(String usercode, double pledge_amount_min, double pledge_amount_max, 
			String pledge_amount, String pledge_days, String start_time) {
		
//		if (StringUtils.isNullOrEmpty(usercode)) {
//	      return "请输入UID";
//	    }

		if (pledge_amount_min <= 0) {
			return "配置参数【参与金额最小值】错误";
		}

		if (pledge_amount_max <= 0) {
			return "配置参数【参与金额最大值】错误";
		}

		if (StringUtils.isNullOrEmpty(pledge_amount)) {
			return "质押金额必填";
		}
		if (!StringUtils.isDouble(pledge_amount)) {
			return "质押金额输入错误，请输入浮点数";
		}
		if (Double.valueOf(pledge_amount).doubleValue() <= 0) {
			return "质押金额不能小于等于0";
		}
		
		if (Double.valueOf(pledge_amount) < Double.valueOf(pledge_amount_min)) {
			return "质押金额不能小于参与金额最小值" + pledge_amount_min;
		}
		
		if (Double.valueOf(pledge_amount) > Double.valueOf(pledge_amount_max)) {
			return "质押金额不能大于参与金额最大值" + pledge_amount_max;
		}
		
		if (StringUtils.isNullOrEmpty(pledge_days)) {
			return "质押天数必填";
		}
		if (!StringUtils.isInteger(pledge_days)) {
			return "质押天数输入错误，请输入整数";
		}
		if (Integer.valueOf(pledge_days).intValue() <= 0) {
			return "质押天数不能小于等于0";
		}
		
		if (StringUtils.isEmptyString(start_time)) {
			return "请输入开始时间";
		}
		if (!StringUtils.isValidDate(start_time)) {
			return "开始时间格式错误";
		}
		
		return null;
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
	
	/**
	 * 获取配置的有效天数列表
	 */
	protected Map<String, String> getValidDays(PledgeGalaxyConfig config) {
		
		String[] split = config.getStaticIncomeForceValue().split("\\|");
		String valueSplit = split[0].split(":")[1];
		String[] daySplit = valueSplit.split(";");
		Map<String, String> map = new HashMap<String, String>();
		for (int i = 0; i < daySplit.length; i++) {
			String day = daySplit[i].split("#")[0];
			map.put(day, day);
		}
		return map;
	}

}
