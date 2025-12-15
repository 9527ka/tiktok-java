package project.web.admin.monitor.activity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
import project.monitor.activity.Activity;
import project.monitor.activity.ActivityOrder;
import project.monitor.activity.ActivityOrderService;
import project.monitor.activity.AdminActivityOrderService;
import project.monitor.activity.AdminActivityService;
import project.party.PartyService;
import project.party.model.Party;
import security.SecUser;
import security.internal.SecUserService;

/**
 * 用户活动
 *
 */
@RestController
public class AdminActivityOrderController extends PageActionSupport {

	private static Log logger = LogFactory.getLog(AdminActivityOrderController.class);

	@Autowired
	protected AdminActivityOrderService adminActivityOrderService;
	@Autowired
	protected ActivityOrderService activityOrderService;

	protected Map<String, Object> session = new HashMap();
	@Autowired
	protected PartyService partyService;
	@Autowired
	protected LogService logService;
	@Autowired
	protected SecUserService secUserService;
	@Autowired
	protected PasswordEncoder passwordEncoder;
	@Autowired
	protected AdminActivityService adminActivityService;
	
	private final String action = "normal/adminActivityOrderAction!";
	
	@RequestMapping(value = action + "list.action") 
	public ModelAndView list(HttpServletRequest request) {
		
		this.checkAndSetPageNo(request.getParameter("pageNo"));
		this.pageSize = 30;
		String name_para = request.getParameter("name_para");
		String message = request.getParameter("message");
		String error = request.getParameter("error");
		String title_para = request.getParameter("title_para");
		this.page = this.adminActivityOrderService.pagedQuery(this.pageNo, this.pageSize, 
				name_para, title_para, this.getLoginPartyId());
		
		
		for(Map<String,Object> map : (List<Map<String,Object>>)page.getElements()) {
			map.put("add_activity_time", map.get("add_activity_time") == null ? null : map.get("add_activity_time").toString().substring(0, 10));
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
		model.addObject("message", message);
		model.addObject("error", error);
		model.addObject("name_para", name_para);
		model.addObject("title_para", title_para);
		model.setViewName("auto_monitor_activity_order_list");
		return model;
	    
	}

	/**
	 * 新增用户活动订单
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = action + "toAdd.action") 
	public ModelAndView toAdd(HttpServletRequest request) {
		Activity entity = this.adminActivityService.findById("2c948a827cd5f779017cd2322f5d0001");
		
		String message = request.getParameter("message");
		String error = request.getParameter("error");
		
		ModelAndView model = new ModelAndView();
		model.addObject("content_img", entity.getContent_img());
		model.addObject("title_img", entity.getTitle_img());

		model.addObject("message", message);
		model.addObject("error", error);
		
		model.setViewName("auto_monitor_activity_order_add");
	    return model;
	}
	
	protected String verification(String usercode, String title, String title_img, 
			String content_img, String endtime_temp, String sendtime_temp, String usdt_temp, String eth_temp, String index_temp) {
//		if (StringUtils.isEmptyString(usercode)) {
//			return "请输入UID";
//		}
		if (StringUtils.isEmptyString(title)) {
			return "请输入活动标题文本";
		}
		if (StringUtils.isEmptyString(title_img)) {
			return "请上传活动标题图片";
		}
		if (StringUtils.isEmptyString(content_img)) {
			return "请上传活动内容图片";
		}
		if (StringUtils.isEmptyString(endtime_temp)) {
			return "请输入活动准入结束时间";
		}
		if (StringUtils.isEmptyString(sendtime_temp)) {
			return "请输入活动奖励派发时间";
		}
		if (StringUtils.isEmptyString(usdt_temp) || !StringUtils.isDouble(usdt_temp) 
				|| Double.valueOf(usdt_temp) < 0) {
			return "请输入正确的用户USDT达标数量";
		}
		if (StringUtils.isEmptyString(eth_temp) || !StringUtils.isDouble(eth_temp) 
				|| Double.valueOf(eth_temp) < 0) {
			return "请输入正确的奖励ETH数量";
		}
		if (StringUtils.isEmptyString(index_temp)) {
			return "首页弹出活动开关值为空";
		}
		return null;
	}
	
	@RequestMapping(value = action + "add.action") 
	public ModelAndView add(HttpServletRequest request) {
		String usercode = request.getParameter("usercode");
		String title = request.getParameter("title");
		String title_img = request.getParameter("title_img");
		String usdt_temp = request.getParameter("usdt");
		String eth_temp = request.getParameter("eth");
		String content_img = request.getParameter("content_img");
		String content = request.getParameter("content");
		String index_temp = request.getParameter("index");
		String endtime_temp = request.getParameter("endtime");
		String sendtime_temp = request.getParameter("sendtime");
		
		ModelAndView model = new ModelAndView();
		model.addObject("content", content);
		model.addObject("content_img", content_img);
		model.addObject("title", title);
		model.addObject("title_img", title_img);
		model.addObject("usdt", usdt_temp);
		model.addObject("eth", eth_temp);
		model.addObject("index", index_temp);
		model.addObject("usercode", usercode);
		model.addObject("endtime", endtime_temp);
		model.addObject("sendtime", sendtime_temp);
		
		try {
			
			
			String error = verification(usercode, title, title_img, content_img, endtime_temp, sendtime_temp, usdt_temp, eth_temp, index_temp);
			if (!StringUtils.isNullOrEmpty(error)) {
				throw new BusinessException(error);
			}
			
			Date endtime = DateUtils.toDate(endtime_temp, DateUtils.DF_yyyyMMddHHmmss);
			Date sendtime = DateUtils.toDate(sendtime_temp, DateUtils.DF_yyyyMMddHHmmss);
			
			double usdt = Double.valueOf(usdt_temp);
			double eth = Double.valueOf(eth_temp);
			Boolean index = Boolean.valueOf(index_temp);
			
			String login_safeword = request.getParameter("login_safeword");
			if (StringUtils.isNullOrEmpty(login_safeword)) {
				throw new BusinessException("请输入登录人资金密码");
			}
			SecUser sec = this.secUserService.findUserByLoginName(this.getUsername_login());
			checkLoginSafeword(sec, this.getUsername_login(), login_safeword);
			
			Party party=partyService.findPartyByUsercode(usercode);
			if (party==null ) {
				throw new BusinessException("UID不存在");
			}
			if(!Constants.SECURITY_ROLE_MEMBER.equals(party.getRolename())
					&& !Constants.SECURITY_ROLE_GUEST.equals(party.getRolename())) {
				throw new BusinessException("只能添加用户活动配置");
			}
			ActivityOrder entity_before = this.activityOrderService.findByPartyId(party.getId().toString());
			
			if (entity_before != null) {
				throw new BusinessException("添加失败，该UID配置已存在");
			}

			ActivityOrder entity = new ActivityOrder();
			entity.setPartyId(party.getId());
			entity.setEndtime(endtime);
			entity.setSendTime(sendtime);
			entity.setContent(content);
			entity.setContent_img(content_img);
			entity.setTitle(title);
			entity.setTitle_img(title_img);
			entity.setUsdt(usdt);
			entity.setEth(eth);
			entity.setIndex(index);
			entity.setCreateTime(new Date());

			this.activityOrderService.save(entity);

			
			project.log.Log log = new project.log.Log();
			log.setCategory(Constants.LOG_CATEGORY_OPERATION);
			log.setPartyId(party.getId());
			log.setUsername(party.getUsername());
			log.setOperator(this.getUsername_login());
			log.setLog("管理员手动添加用户活动订单,操作ip:["+this.getIp(getRequest())+"]");
			logService.saveSync(log);
			
		} catch (BusinessException e) {
			model.addObject("error", e.getMessage());
			model.setViewName("auto_monitor_activity_order_add");
//			model.setViewName("redirect:/" + action + "toAdd.action");
		    return model;
		} catch (Exception e) {
			logger.error("error ", e);
			model.addObject("error", "程序错误");
			model.setViewName("auto_monitor_activity_order_add");
//			model.setViewName("redirect:/" + action + "toAdd.action");
			return model;
		}
		model.addObject("message", "操作成功");
		model.setViewName("redirect:/" + action + "list.action");
	    return model;
	}

	@RequestMapping(value = action + "toUpdate.action") 
	public ModelAndView toUpdate(HttpServletRequest request) {
		
		String id = request.getParameter("id");
		ModelAndView model = new ModelAndView();
		try {

			
			ActivityOrder entity = this.activityOrderService.findById(id);
			Party party = partyService.cachePartyBy(entity.getPartyId(), true);
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

			model.addObject("content", entity.getContent());
			model.addObject("content_img", entity.getContent_img());
			model.addObject("title", entity.getTitle());
			model.addObject("title_img", entity.getTitle_img());
			model.addObject("usdt", entity.getUsdt());
			model.addObject("eth", entity.getEth());
			model.addObject("index", entity.getIndex());
			model.addObject("id",id);
			model.addObject("createTime", DateUtils.toDate(entity.getCreateTime().toString(), DateUtils.DF_yyyyMMdd));
			model.addObject("sendtime_update", sdf.format(entity.getSendTime()));
			model.addObject("endtime_update", sdf.format(entity.getEndtime()));
			if (null != party) {
				model.addObject("usercode", party.getUsercode());
			}
			
		} catch (BusinessException e) {
			model.addObject("error", e.getMessage());
			model.setViewName("redirect:/" + action + "list.action");
			return model;
		} catch (Throwable t) {
			logger.error(" error ", t);
			model.addObject("error", "[ERROR] " + t.getMessage());
			model.setViewName("redirect:/" + action + "list.action");
			return model;
		}
		
		model.setViewName("auto_monitor_activity_order_update");
		return model;
	}
	
	protected String verificationUpdate(String title, String title_img, String content_img, 
			String endtime_update, String sendtime_update, String usdt_temp, String eth_temp, String index_temp) {
		
		if (StringUtils.isEmptyString(title)) {
			return "请输入活动标题文本";
		}
			
		if (StringUtils.isEmptyString(title_img)) {
			return "请上传活动标题图片";
		}
	
		if (StringUtils.isEmptyString(content_img)) {
			return "请上传活动内容图片";
		}
			
		if (endtime_update == null) {
			return "请输入活动准入结束时间";
		}
		
		if (sendtime_update == null) {
			return "请输入活动奖励派发时间";
		}
		
		if (StringUtils.isEmptyString(usdt_temp) || !StringUtils.isDouble(usdt_temp) 
				|| Double.valueOf(usdt_temp) < 0) {
			return "请输入正确的用户USDT达标数量";
		}
		if (StringUtils.isEmptyString(eth_temp) || !StringUtils.isDouble(usdt_temp) 
				|| Double.valueOf(usdt_temp) < 0) {
			return "请输入正确的奖励ETH数量";
		}
		if (StringUtils.isEmptyString(index_temp)) {
			return "首页弹出活动开关值为空";
		}
		return null;
	}

	@RequestMapping(value = action + "update.action") 
	public ModelAndView update(HttpServletRequest request) {
		String id = request.getParameter("id");
		String usercode = request.getParameter("usercode");
		String title = request.getParameter("title");
		String title_img = request.getParameter("title_img");
		String content_img = request.getParameter("content_img");
		String createTime = request.getParameter("createTime");
		String endtime_update = request.getParameter("endtime_update");
		String sendtime_update = request.getParameter("sendtime_update");
		String login_safeword = request.getParameter("login_safeword");
		String content = request.getParameter("content");
		String usdt_temp = request.getParameter("usdt");
		String eth_temp = request.getParameter("eth");
		String index_temp = request.getParameter("index");
		
		
		ModelAndView model = new ModelAndView();
		model.addObject("usercode", usercode);
		model.addObject("content", content);
		model.addObject("content_img", content_img);
		model.addObject("title", title);
		model.addObject("title_img", title_img);
		model.addObject("usdt", usdt_temp);
		model.addObject("eth", eth_temp);
		model.addObject("index", index_temp);
		model.addObject("createTime", createTime);
		model.addObject("endtime_update", endtime_update);
		model.addObject("sendtime_update", sendtime_update);
		
		model.addObject("id", id);
		try {

			
			String error = verificationUpdate(title, title_img, content_img, endtime_update, sendtime_update,
					usdt_temp, eth_temp, index_temp);
			if (!StringUtils.isNullOrEmpty(error)) {
				throw new BusinessException(error);
			}
			
			double usdt = Double.valueOf(usdt_temp);
			double eth = Double.valueOf(eth_temp);
			boolean index = Boolean.valueOf(index_temp);
			
			if (StringUtils.isNullOrEmpty(login_safeword)) {
				throw new BusinessException("请输入登录人资金密码");
			}
			SecUser sec = this.secUserService.findUserByLoginName(this.getUsername_login());
			checkLoginSafeword(sec,this.getUsername_login(), login_safeword);
			
			ActivityOrder entity = this.activityOrderService.findById(id);
			
			Party party = null;
			if(!StringUtils.isNullOrEmpty(entity.getPartyId().toString())){
				party = partyService.cachePartyBy(entity.getPartyId(), true);
			}
			String before_title = entity.getTitle();
			double beforeusdt = entity.getUsdt();
			double before_eth = entity.getEth();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			entity.setEndtime(sdf.parse(endtime_update));
			entity.setSendTime(sdf.parse(sendtime_update));
			entity.setContent(content);
			entity.setContent_img(content_img);
			entity.setTitle(title);
			entity.setTitle_img(title_img);
			entity.setUsdt(usdt);
			entity.setEth(eth);
			entity.setIndex(index);
			entity.setCreateTime(new Date());
			
			this.activityOrderService.update(entity);
			
			project.log.Log log = new project.log.Log();
			log.setCategory(Constants.LOG_CATEGORY_OPERATION);
			if(party != null ) {
				log.setUsername(party.getUsername());
				log.setPartyId(party.getId());
			}
			log.setOperator(this.getUsername_login());
			log.setLog("管理员手动修改用户用户活动订单,修改前标题文本为：["
			        + before_title+"],用户USDT达标数量为["
					+ beforeusdt+"],"
					+ "奖励ETH数量为["
					+ before_eth+"],修改后标题文本为["
					+ title+"],用户USDT达标数量为["
					+ usdt+"],奖励ETH数量为["
					+ eth+"],操作ip:["
					+ this.getIp(getRequest())+"]");
			logService.saveSync(log);
			
			model.addObject("message", "操作成功");
			model.setViewName("redirect:/" + action + "list.action");
		    return model;
		} catch (BusinessException e) {
			model.addObject("error", e.getMessage());
//			model.setViewName("redirect:/" + action + "toUpdate.action");
			model.setViewName("auto_monitor_activity_order_update");
			return model;
		} catch (Throwable t) {
			logger.error("update error ", t);
			model.addObject("error", "程序错误");
//			model.setViewName("redirect:/" + action + "toUpdate.action");
			model.setViewName("auto_monitor_activity_order_update");
			return model;
		}
	}

	@RequestMapping(value = action + "toDelete.action") 
	public ModelAndView toDelete(HttpServletRequest request) {
		
		ModelAndView model = new ModelAndView();
		
		try {
			String login_safeword = request.getParameter("login_safeword");
			if (StringUtils.isNullOrEmpty(login_safeword)) {
				model.addObject("error", "请输入登录人资金密码");
				model.setViewName("redirect:/" + action + "list.action");
				return model;
			}

			SecUser sec = this.secUserService.findUserByLoginName(this.getUsername_login());
			checkLoginSafeword(sec,this.getUsername_login(), login_safeword);
			
			String id = request.getParameter("id");
			ActivityOrder entity = this.activityOrderService.findById(id);
			
			this.activityOrderService.delete(entity);
			
			Party party= null;
			if(!StringUtils.isNullOrEmpty(entity.getPartyId().toString())){
				party=partyService.cachePartyBy(entity.getPartyId(), true);
			}
			project.log.Log log = new project.log.Log();
			log.setCategory(Constants.LOG_CATEGORY_OPERATION);
			if(party != null ) {
				log.setUsername(party.getUsername());
				log.setPartyId(party.getId());
			}
			log.setOperator(this.getUsername_login());
			log.setLog("管理员手动删除用户活动订单,操作ip:["+this.getIp(getRequest())+"]");
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
	
	/**
	 * 验证登录人资金密码
	 * @param operatorUsername
	 * @param loginSafeword
	 */
	protected void checkLoginSafeword(SecUser secUser,String operatorUsername,String loginSafeword) {
//		SecUser sec = this.secUserService.findUserByLoginName(operatorUsername);
		String sysSafeword = secUser.getSafeword();
		String safeword_md5 = passwordEncoder.encodePassword(loginSafeword, operatorUsername);
		if (!safeword_md5.equals(sysSafeword)) {
			throw new BusinessException("登录人资金密码错误");
		}
	}

	public void setSession(Map<String, Object> session) {
		this.session = session;
	}

}
