package project.web.admin.monitor.activity;

import java.text.SimpleDateFormat;
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
import project.log.Log;
import project.log.LogService;
import project.monitor.activity.Activity;
import project.monitor.activity.AdminActivityService;
import project.party.PartyService;
import project.party.model.Party;
import security.SecUser;
import security.internal.SecUserService;

/**
 * 全局活动管理
 */
@RestController
public class AdminActivityController extends PageActionSupport {

	private Logger logger = LogManager.getLogger(AdminActivityController.class);

	@Autowired
	protected AdminActivityService adminActivityService;
	@Autowired
	protected PartyService partyService;
	@Autowired
	protected LogService logService;
	@Autowired
	protected SecUserService secUserService;
	@Autowired
	protected PasswordEncoder passwordEncoder;
	
	private final String action = "normal/adminActivityAction!";

	/**
	 * 获取全局活动列表
	 */
	@RequestMapping(action + "list.action")
	public ModelAndView list(HttpServletRequest request) {	
		String pageNo = request.getParameter("pageNo");
		String message = request.getParameter("message");
		String error = request.getParameter("error");
		String name_para = request.getParameter("name_para");
		String title_para = request.getParameter("title_para");

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("auto_monitor_activity_list");

		try {
			
			this.checkAndSetPageNo(pageNo);

			this.pageSize = 30;
			this.page = this.adminActivityService.pagedQuery(this.pageNo, this.pageSize, name_para, title_para);

			List<Map<String, Object>> list = (List<Map<String, Object>>) this.page.getElements();
			for (int i = 0; i < list.size(); i++) {
				Map<String, Object> map = list.get(i);
				if ("2c948a827cd5f779017cd2322f5d0001".equals((map.get("id") + "").toString())) {
					Map<String, Object> map2 = list.get(0);
					list.set(0, list.get(i));
					list.set(i, map2);
					break;
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
	 * 新增全局活动 页面
	 */
	@RequestMapping(action + "toAdd.action")
	public ModelAndView toAdd(HttpServletRequest request) {

		ModelAndView modelAndView = new ModelAndView();
		
		try {

			Activity entity = this.adminActivityService.findById("2c948a827cd5f779017cd2322f5d0001");
			
			modelAndView.addObject("content_img", entity.getContent_img());
			modelAndView.addObject("title_img", entity.getTitle_img());

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

		modelAndView.setViewName("auto_monitor_activity_add");
		return modelAndView;
	}

	/**
	 * 新增全局活动
	 */
	@RequestMapping(action + "add.action")
	public ModelAndView add(HttpServletRequest request) {
		String title_img = request.getParameter("title_img");		
		String content_img = request.getParameter("content_img");
		String usercode = request.getParameter("usercode");
		String title = request.getParameter("title");		
		String content = request.getParameter("content");
		String usdt = request.getParameter("usdt");
		String eth = request.getParameter("eth");
		Boolean index = Boolean.valueOf(request.getParameter("index"));
		String state = request.getParameter("state");		
		String endtime = request.getParameter("endtime");
		String sendtime = request.getParameter("sendtime");
		String login_safeword = request.getParameter("login_safeword");

		ModelAndView modelAndView = new ModelAndView();
		
		try {
			
			String error = this.verification(usercode, title, title_img, content_img, endtime, sendtime, usdt, eth);
			if (!StringUtils.isNullOrEmpty(error)) {
				throw new BusinessException(error);
			}

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date endtime_date = sdf.parse(request.getParameter("endtime"));
			Date sendtime_date = sdf.parse(request.getParameter("sendtime"));
			
			Double usdt_double = Double.valueOf(request.getParameter("usdt"));
			Double eth_double = Double.valueOf(request.getParameter("eth"));

			if (StringUtils.isNullOrEmpty(login_safeword)) {
				throw new BusinessException("请输入登录人资金密码");
			}
			
			SecUser sec = this.secUserService.findUserByLoginName(this.getUsername_login());
			this.checkLoginSafeword(sec, this.getUsername_login(), login_safeword);

			Party party = partyService.findPartyByUsercode(usercode);
			if (null == party) {
				throw new BusinessException("UID不存在");
			}
			
			if (!Constants.SECURITY_ROLE_AGENT.equals(party.getRolename())
					&& !Constants.SECURITY_ROLE_AGENTLOW.equals(party.getRolename())) {
				throw new BusinessException("只能增加代理商活动");
			}
			
			Activity entity_before = this.adminActivityService.findByPartyId(party.getId().toString());
			if (entity_before != null) {
				throw new BusinessException("添加失败，该UID配置已存在");
			}

			Activity entity = new Activity();
			entity.setPartyId(party.getId());
			entity.setEndtime(endtime_date);
			entity.setSendtime(sendtime_date);
			entity.setContent(content);
			entity.setContent_img(content_img);
			entity.setTitle(title);
			entity.setTitle_img(title_img);
			entity.setUsdt(usdt_double);
			entity.setEth(eth_double);
			entity.setIndex(index);
			entity.setCreateTime(new Date());
			entity.setState(state);

			this.adminActivityService.save(entity);

			Log log = new Log();
			log.setCategory(Constants.LOG_CATEGORY_OPERATION);
			log.setPartyId(party.getId());
			log.setUsername(party.getUsername());
			log.setOperator(this.getUsername_login());
			log.setLog("管理员手动添加用户活动产品,操作ip:[" + this.getIp(getRequest()) + "]");
			logService.saveSync(log);

		} catch (BusinessException e) {
			modelAndView.addObject("error", e.getMessage());
			modelAndView.addObject("title_img", title_img);
			modelAndView.addObject("content_img", content_img);
			modelAndView.addObject("usercode", usercode);
			modelAndView.addObject("title", title);
			modelAndView.addObject("content", content);
			modelAndView.addObject("usdt", usdt);
			modelAndView.addObject("eth", eth);
			modelAndView.addObject("index", index);
			modelAndView.addObject("state", state);
			modelAndView.addObject("endtime", endtime);
			modelAndView.addObject("sendtime", sendtime);
			modelAndView.setViewName("auto_monitor_activity_add");
			return modelAndView;
		} catch (Throwable t) {
			logger.error("Activity.Add error ", t);
			modelAndView.addObject("error", "[ERROR] " + t.getMessage());
			modelAndView.addObject("title_img", title_img);
			modelAndView.addObject("content_img", content_img);
			modelAndView.addObject("usercode", usercode);
			modelAndView.addObject("title", title);
			modelAndView.addObject("content", content);
			modelAndView.addObject("usdt", usdt);
			modelAndView.addObject("eth", eth);
			modelAndView.addObject("index", index);
			modelAndView.addObject("state", state);
			modelAndView.addObject("endtime", endtime);
			modelAndView.addObject("sendtime", sendtime);
			modelAndView.setViewName("auto_monitor_activity_add");
			return modelAndView;
		}

		modelAndView.addObject("message", "操作成功");
		modelAndView.setViewName("redirect:/" + action + "list.action");
		return modelAndView;
	}

	/**
	 * 修改全局活动 页面
	 */
	@RequestMapping(action + "toUpdate.action")
	public ModelAndView toUpdate(HttpServletRequest request) {
		String id = request.getParameter("id");

		ModelAndView modelAndView = new ModelAndView();

		try {

			Activity entity = this.adminActivityService.findById(id);
			
			Party party = partyService.cachePartyBy(entity.getPartyId(), true);
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

			modelAndView.addObject("content", entity.getContent());
			modelAndView.addObject("content_img", entity.getContent_img());
			modelAndView.addObject("title", entity.getTitle());
			modelAndView.addObject("title_img", entity.getTitle_img());
			modelAndView.addObject("usdt", entity.getUsdt());
			modelAndView.addObject("eth", entity.getEth());
			modelAndView.addObject("index", entity.getIndex());
			modelAndView.addObject("state",entity.getState());
			modelAndView.addObject("id",id);
			modelAndView.addObject("createTime", DateUtils.toDate(entity.getCreateTime().toString(), DateUtils.DF_yyyyMMdd));
			modelAndView.addObject("sendtime_update", sdf.format(entity.getSendtime()));
			modelAndView.addObject("endtime_update", sdf.format(entity.getEndtime()));
			if (null != party) {
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
		
		modelAndView.setViewName("auto_monitor_activity_update");
		return modelAndView;
	}

	/**
	 * 修改全局活动
	 */
	@RequestMapping(action + "update.action")
	public ModelAndView update(HttpServletRequest request) {
		String id = request.getParameter("id");		
		String content = request.getParameter("content");	
		String content_img = request.getParameter("content_img");
		String title = request.getParameter("title");		
		String title_img = request.getParameter("title_img");	
		String usdt = request.getParameter("usdt");
		String eth = request.getParameter("eth");
		Boolean index = Boolean.valueOf(request.getParameter("index"));
		String state = request.getParameter("state");		
		String createTime = request.getParameter("createTime");
		String endtime_update = request.getParameter("endtime_update");
		String sendtime_update = request.getParameter("sendtime_update");	
		String usercode = request.getParameter("usercode");
		String login_safeword = request.getParameter("login_safeword");

		ModelAndView modelAndView = new ModelAndView();

		try {
			
			String error = this.verificationUpdate(title, title_img, content_img, endtime_update, sendtime_update, usdt, eth);
			if (!StringUtils.isNullOrEmpty(error)) {
				throw new BusinessException(error);
			}
			
			Double usdt_double = Double.valueOf(request.getParameter("usdt"));
			Double eth_double = Double.valueOf(request.getParameter("eth"));

			if (StringUtils.isNullOrEmpty(login_safeword)) {
				throw new BusinessException("请输入登录人资金密码");
			}
			
			SecUser sec = this.secUserService.findUserByLoginName(this.getUsername_login());
			this.checkLoginSafeword(sec, this.getUsername_login(), login_safeword);

			Activity entity = this.adminActivityService.findById(id);
			
			Party party = null;
			if (!StringUtils.isNullOrEmpty(entity.getPartyId().toString())) {
				party = partyService.cachePartyBy(entity.getPartyId(), true);
			}
			
			String before_title = entity.getTitle();
			double beforeusdt = entity.getUsdt();
			double before_eth = entity.getEth();
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			entity.setEndtime(sdf.parse(endtime_update));
			entity.setSendtime(sdf.parse(sendtime_update));
			entity.setContent(content);
			entity.setContent_img(content_img);
			entity.setTitle(title);
			entity.setTitle_img(title_img);
			entity.setUsdt(usdt_double);
			entity.setEth(eth_double);
			entity.setIndex(index);
			entity.setCreateTime(new Date());
			entity.setState(state);

			this.adminActivityService.update(entity);

			Log log = new Log();
			log.setCategory(Constants.LOG_CATEGORY_OPERATION);
			if (party != null) {
				log.setUsername(party.getUsername());
				log.setPartyId(party.getId());
			}
			log.setOperator(this.getUsername_login());
			log.setLog("管理员手动修改用户用户活动产品,修改前标题文本为：[" + before_title + "],用户USDT达标数量为[" + beforeusdt + "]," + "奖励ETH数量为["
					+ before_eth + "],修改后标题文本为[" + title + "],用户USDT达标数量为[" + usdt + "],奖励ETH数量为[" + eth
					+ "],操作ip:[" + this.getIp(getRequest()) + "]");
			this.logService.saveSync(log);
			
		} catch (BusinessException e) {
			modelAndView.addObject("error", e.getMessage());
			modelAndView.addObject("id", id);
			modelAndView.addObject("content", content);
			modelAndView.addObject("content_img", content_img);
			modelAndView.addObject("title", title);
			modelAndView.addObject("title_img", title_img);
			modelAndView.addObject("usdt", usdt);
			modelAndView.addObject("eth", eth);
			modelAndView.addObject("index", index);
			modelAndView.addObject("state", state);
			modelAndView.addObject("createTime", createTime);
			modelAndView.addObject("endtime_update", endtime_update);
			modelAndView.addObject("sendtime_update", sendtime_update);
			modelAndView.addObject("usercode", usercode);
			modelAndView.setViewName("auto_monitor_activity_update");
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
			modelAndView.addObject("index", index);
			modelAndView.addObject("state", state);
			modelAndView.addObject("createTime", createTime);
			modelAndView.addObject("endtime_update", endtime_update);
			modelAndView.addObject("sendtime_update", sendtime_update);
			modelAndView.addObject("usercode", usercode);
			modelAndView.setViewName("auto_monitor_activity_update");
			return modelAndView;
		}
		
		modelAndView.addObject("message", "操作成功");
		modelAndView.setViewName("redirect:/" + action + "list.action");
		return modelAndView;
	}

	/**
	 * 删除全局活动
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

			Activity entity = this.adminActivityService.findById(id);
			if (null == entity.getPartyId() || StringUtils.isEmptyString(entity.getPartyId().toString())) {
				throw new BusinessException("全局配置不能删除");
			}
			
			this.adminActivityService.delete(id);

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
			log.setLog("管理员手动删除用户活动产品,操作ip:[" + this.getIp(getRequest()) + "]");
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

	protected String verification(String usercode, String title, String title_img, String content_img, String endtime, 
			String sendtime, String usdt, String eth) {
		
		if (StringUtils.isEmptyString(usercode))
			return "请输入UID";
		
		return this.verificationUpdate(title, title_img, content_img, endtime, sendtime, usdt, eth);
	}

	protected String verificationUpdate(String title, String title_img, String content_img, String endtime,  String sendtime, 
			String usdt, String eth) {

		if (StringUtils.isEmptyString(title))
			return "请输入活动标题文本";
		if (StringUtils.isEmptyString(title_img))
			return "请上传活动标题图片";
		if (StringUtils.isEmptyString(content_img))
			return "请上传活动内容图片";
		
		if (StringUtils.isEmptyString(endtime)) {
			return "请输入活动准入结束时间";
		}
		if (!StringUtils.isValidDate(endtime)) {
			return "活动准入结束时间格式错误";
		}
		
		if (StringUtils.isEmptyString(sendtime)) {
			return "请输入活动奖励派发时间";			
		}
		if (!StringUtils.isValidDate(sendtime)) {
			return "活动奖励派发时间格式错误";
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
		String safeword_md5 = this.passwordEncoder.encodePassword(loginSafeword, operatorUsername);
		if (!safeword_md5.equals(sysSafeword)) {
			throw new BusinessException("登录人资金密码错误");
		}
	}

}
