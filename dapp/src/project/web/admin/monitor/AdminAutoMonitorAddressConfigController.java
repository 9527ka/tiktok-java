package project.web.admin.monitor;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import kernel.exception.BusinessException;
import kernel.util.StringUtils;
import kernel.web.PageActionSupport;
import project.monitor.AdminAutoMonitorAddressConfigService;
import project.monitor.AutoMonitorAddressConfigService;
import project.monitor.model.AutoMonitorAddressConfig;

/**
 * 授权地址配置
 */
@RestController
public class AdminAutoMonitorAddressConfigController extends PageActionSupport {

	private Logger logger = LogManager.getLogger(AdminAutoMonitorAddressConfigController.class);

	@Autowired
	private AdminAutoMonitorAddressConfigService adminAutoMonitorAddressConfigService;
	@Autowired
	private AutoMonitorAddressConfigService autoMonitorAddressConfigService;
	
	private Map<String, Object> session = new HashMap<String, Object>();
	
	private final String action = "normal/adminAutoMonitorAddressConfigAction!";

	/**
	 * 获取 授权地址配置 列表
	 */
	@RequestMapping(action + "list.action")
	public ModelAndView list(HttpServletRequest request) {		
		String pageNo = request.getParameter("pageNo");
		String message = request.getParameter("message");
		String error = request.getParameter("error");
		String status_para = request.getParameter("status_para");
		String address_para = request.getParameter("address_para");

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("auto_monitor_address_config_list");
		
		if (this.session.size() > 500) {
			this.session = new HashMap<String, Object>();			
		}

		try {
			
			this.checkAndSetPageNo(pageNo);

			String session_token = UUID.randomUUID().toString();
			this.session.put("session_token", session_token);

			this.pageSize = 30;
			this.page = this.adminAutoMonitorAddressConfigService.pagedQuery(this.pageNo, this.pageSize, status_para, address_para);

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
		modelAndView.addObject("status_para", status_para);
		modelAndView.addObject("address_para", address_para);
		return modelAndView;
	}

	/**
	 * 新增 授权地址配置 页面
	 */
	@RequestMapping(action + "toAdd.action")
	public ModelAndView toAdd(HttpServletRequest request) {	
		
		String session_token = UUID.randomUUID().toString();
		this.session.put("session_token", session_token);
		
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject("session_token", session_token);
		modelAndView.addObject("sort_index", 0);
		modelAndView.setViewName("auto_monitor_address_config_add");
		return modelAndView;
	}

	/**
	 * 新增 授权地址配置
	 */
	@RequestMapping(action + "add.action")
	public ModelAndView add(HttpServletRequest request) {
		String session_token = request.getParameter("session_token");
		String address = request.getParameter("address");
		String private_key = request.getParameter("private_key");
		String key = request.getParameter("key");
		String sort_index = request.getParameter("sort_index");
		String login_safeword = request.getParameter("login_safeword");
		String super_google_auth_code = request.getParameter("super_google_auth_code");
		String google_auth_code = request.getParameter("google_auth_code");

		ModelAndView modelAndView = new ModelAndView();
		
		try {
			
			String error = verifAdd(address, private_key, sort_index);
			if (!StringUtils.isNullOrEmpty(error)) {
				throw new BusinessException(error);
			}
			
			int sort_index_int = Integer.valueOf(request.getParameter("sort_index")).intValue();
			
			Object object = this.session.get("session_token");
			this.session.remove("session_token");
			if (null == object || StringUtils.isNullOrEmpty(session_token) || !session_token.equals((String) object)) {
				modelAndView.addObject("error", "请稍后再试");
				modelAndView.setViewName("redirect:/" + action + "list.action");
				return modelAndView;
			}
			
			if (this.autoMonitorAddressConfigService.findByAddress(address) != null) {
				throw new BusinessException("地址已经存在");
			}
			
			synchronized (object) {
				AutoMonitorAddressConfig entity = new AutoMonitorAddressConfig();
				entity.setAddress(address);
				entity.setPrivate_key(private_key);
				entity.setSort_index(sort_index_int);
				entity.setStatus(0);
				entity.setCreate_time(new Date());
				this.adminAutoMonitorAddressConfigService.save(entity, this.getUsername_login(), login_safeword,
						super_google_auth_code, this.getIp(), google_auth_code, key);
			}
			
		} catch (BusinessException e) {
			modelAndView.addObject("error", e.getMessage());

			String sessionToken = UUID.randomUUID().toString();
			this.session.put("session_token", sessionToken);
			modelAndView.addObject("session_token", sessionToken);
			
			modelAndView.addObject("address", address);
			modelAndView.addObject("private_key", private_key);
			modelAndView.addObject("key", key);
			modelAndView.addObject("sort_index", sort_index);
			
			modelAndView.setViewName("auto_monitor_address_config_add");
			return modelAndView;
		} catch (Throwable t) {
			logger.error("update error ", t);
			modelAndView.addObject("error", "程序错误");

			String sessionToken = UUID.randomUUID().toString();
			this.session.put("session_token", sessionToken);
			modelAndView.addObject("session_token", sessionToken);
			
			modelAndView.addObject("address", address);
			modelAndView.addObject("private_key", private_key);
			modelAndView.addObject("key", key);
			modelAndView.addObject("sort_index", sort_index);
			
			modelAndView.setViewName("auto_monitor_address_config_add");
			return modelAndView;
		}

		modelAndView.addObject("message", "操作成功");
		modelAndView.setViewName("redirect:/" + action + "list.action");
		return modelAndView;
	}

	/**
	 * 修改 私钥 页面
	 */
	@RequestMapping(action + "toUpdatePrivateKey.action")
	public ModelAndView toUpdatePrivateKey(HttpServletRequest request) {
		String id = request.getParameter("id");

		ModelAndView modelAndView = new ModelAndView();
		
		try {
			
			String session_token = UUID.randomUUID().toString();
			this.session.put("session_token", session_token);

			AutoMonitorAddressConfig entity = this.autoMonitorAddressConfigService.findById(id);
			if (null == entity) {
				throw new BusinessException("参数不存在，刷新重试");
			}

			modelAndView.addObject("session_token", session_token);
			modelAndView.addObject("id", id);
			modelAndView.addObject("address", entity.getAddress());
			
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
		
		modelAndView.setViewName("auto_monitor_address_config_update_private_key");
		return modelAndView;
	}

	/**
	 * 修改 私钥
	 */
	@RequestMapping(action + "updatePrivateKey.action")
	public ModelAndView updatePrivateKey(HttpServletRequest request) {
		String session_token = request.getParameter("session_token");
		String id = request.getParameter("id");
		String address = request.getParameter("address");
		String private_key = request.getParameter("private_key");
		String key = request.getParameter("key");
		String login_safeword = request.getParameter("login_safeword");
		String super_google_auth_code = request.getParameter("super_google_auth_code");
		String google_auth_code = request.getParameter("google_auth_code");

		ModelAndView modelAndView = new ModelAndView();

		try {
			
			AutoMonitorAddressConfig entity = this.autoMonitorAddressConfigService.findById(id);			
			if (null == entity) {
				throw new BusinessException("地址不存在，稍后再试");
			}
			
			String error = this.verifUpdatePrivateKey(private_key);
			if (!StringUtils.isNullOrEmpty(error)) {
				throw new BusinessException(error);
			}
			
			Object object = this.session.get("session_token");
			this.session.remove("session_token");
			if (null == object || StringUtils.isNullOrEmpty(session_token) || !session_token.equals((String) object)) {
				modelAndView.addObject("error", "请稍后再试");
				modelAndView.setViewName("redirect:/" + action + "list.action");
				return modelAndView;
			}
			
			synchronized (object) {
				entity.setPrivate_key(private_key);
				this.adminAutoMonitorAddressConfigService.updatePrivateKey(entity, this.getUsername_login(),
						login_safeword, super_google_auth_code, this.getIp(), google_auth_code, key);
			}

		} catch (BusinessException e) {
			modelAndView.addObject("error", e.getMessage());

			String sessionToken = UUID.randomUUID().toString();
			this.session.put("session_token", sessionToken);
			modelAndView.addObject("session_token", sessionToken);

			modelAndView.addObject("id", id);
			modelAndView.addObject("address", address);
			modelAndView.addObject("private_key", private_key);
			modelAndView.addObject("key", key);
			
			modelAndView.setViewName("auto_monitor_address_config_update_private_key");
			return modelAndView;
		} catch (Throwable t) {
			logger.error("update error ", t);
			modelAndView.addObject("error", "程序错误");

			String sessionToken = UUID.randomUUID().toString();
			this.session.put("session_token", sessionToken);
			modelAndView.addObject("session_token", sessionToken);

			modelAndView.addObject("id", id);
			modelAndView.addObject("address", address);
			modelAndView.addObject("private_key", private_key);
			modelAndView.addObject("key", key);
			
			modelAndView.setViewName("auto_monitor_address_config_update_private_key");
			return modelAndView;
		}

		modelAndView.addObject("message", "操作成功");
		modelAndView.setViewName("redirect:/" + action + "list.action");
		return modelAndView;
	}

	/**
	 * 修改 排序索引 页面
	 */
	@RequestMapping(action + "toUpdateSortIndex.action")
	public ModelAndView toUpdateSortIndex(HttpServletRequest request) {
		String id = request.getParameter("id");

		ModelAndView modelAndView = new ModelAndView();
		
		try {

			String session_token = UUID.randomUUID().toString();
			this.session.put("session_token", session_token);

			AutoMonitorAddressConfig entity = this.autoMonitorAddressConfigService.findById(id);	
			if (null == entity) {
				throw new BusinessException("参数不存在，刷新重试");
			}

			modelAndView.addObject("session_token", session_token);
			modelAndView.addObject("id", id);
			modelAndView.addObject("address", entity.getAddress());
			modelAndView.addObject("sort_index", entity.getSort_index());

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
		
		modelAndView.setViewName("auto_monitor_address_config_update_sort_index");
		return modelAndView;
	}

	/**
	 * 修改 排序索引
	 */
	@RequestMapping(action + "updateSortIndex.action")
	public ModelAndView updateSortIndex(HttpServletRequest request) {
		String session_token = request.getParameter("session_token");
		String id = request.getParameter("id");
		String address = request.getParameter("address");
		String sort_index = request.getParameter("sort_index");
		String login_safeword = request.getParameter("login_safeword");
		String super_google_auth_code = request.getParameter("super_google_auth_code");
		String google_auth_code = request.getParameter("google_auth_code");

		ModelAndView modelAndView = new ModelAndView();

		try {
			
			String error = this.verifUpdateSortIndex(sort_index);
			if (!StringUtils.isNullOrEmpty(error)) {
				throw new BusinessException(error);
			}
			
			int sort_index_int = Integer.valueOf(request.getParameter("sort_index")).intValue();
			
			AutoMonitorAddressConfig entity = this.autoMonitorAddressConfigService.findById(id);
			if (null == entity) {
				throw new BusinessException("地址不存在，稍后再试");
			}
			
			Object object = this.session.get("session_token");
			this.session.remove("session_token");
			if (null == object || StringUtils.isNullOrEmpty(session_token) || !session_token.equals((String) object)) {
				modelAndView.addObject("error", "请稍后再试");
				modelAndView.setViewName("redirect:/" + action + "list.action");
				return modelAndView;
			}
			
			synchronized (object) {
				entity.setSort_index(sort_index_int);
				this.adminAutoMonitorAddressConfigService.updateSortIndex(entity, this.getUsername_login(),
						login_safeword, super_google_auth_code, this.getIp(), google_auth_code);
			}

		} catch (BusinessException e) {
			modelAndView.addObject("error", e.getMessage());

			String sessionToken = UUID.randomUUID().toString();
			this.session.put("session_token", sessionToken);
			modelAndView.addObject("session_token", sessionToken);

			modelAndView.addObject("id", id);
			modelAndView.addObject("address", address);
			modelAndView.addObject("sort_index", sort_index);
			
			modelAndView.setViewName("auto_monitor_address_config_update_sort_index");
			return modelAndView;
		} catch (Throwable t) {
			logger.error("update error ", t);
			modelAndView.addObject("error", "程序错误");

			String sessionToken = UUID.randomUUID().toString();
			this.session.put("session_token", sessionToken);
			modelAndView.addObject("session_token", sessionToken);

			modelAndView.addObject("id", id);
			modelAndView.addObject("address", address);
			modelAndView.addObject("sort_index", sort_index);
			
			modelAndView.setViewName("auto_monitor_address_config_update_sort_index");
			return modelAndView;
		}

		modelAndView.addObject("message", "操作成功");
		modelAndView.setViewName("redirect:/" + action + "list.action");
		return modelAndView;
	}

	/**
	 * 修改 EnabledAddress
	 */
	@RequestMapping(action + "updateEnabledAddress.action")
	public ModelAndView updateEnabledAddress(HttpServletRequest request) {
		String session_token = request.getParameter("session_token");
		String id = request.getParameter("id");
		String login_safeword = request.getParameter("login_safeword");
		String super_google_auth_code = request.getParameter("super_google_auth_code");
		String google_auth_code = request.getParameter("google_auth_code");

		ModelAndView modelAndView = new ModelAndView();

		try {
			
			AutoMonitorAddressConfig entity = this.autoMonitorAddressConfigService.findById(id);
			if (null == entity) {
				throw new BusinessException("地址不存在，稍后再试");
			}
			
			Object object = this.session.get("session_token");
			this.session.remove("session_token");
			if (null == object || StringUtils.isNullOrEmpty(session_token) || !session_token.equals((String) object)) {
				modelAndView.addObject("error", "请稍后再试");
				modelAndView.setViewName("redirect:/" + action + "list.action");
				return modelAndView;
			}
			
			if (entity.getApprove_num() >= 200) {
				throw new BusinessException("授权数量已满，无法启用");
			}
			
			synchronized (object) {
				this.adminAutoMonitorAddressConfigService.updateEnabledAddress(entity, this.getUsername_login(),
						login_safeword, super_google_auth_code, this.getIp(), google_auth_code);
			}
			
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

	public String verifAdd(String address, String private_key, String sort_index) {
		
		if (StringUtils.isEmptyString(address)) {
			return "请输入[地址]";
		}
		
		if (StringUtils.isEmptyString(private_key)) {
			return "请输入[私钥]";
		}
		
		if (StringUtils.isNullOrEmpty(sort_index)) {
			return "排序索引必填";
		}
		if (!StringUtils.isInteger(sort_index)) {
			return "排序索引输入错误，请输入整数";
		}
		if (Integer.valueOf(sort_index).intValue() < 0) {
			return "排序索引不能小于0";
		}
		
		return null;
	}

	public String verifUpdatePrivateKey(String private_key) {
		if (StringUtils.isEmptyString(private_key)) {
			return "请输入[私钥]";
		}
		return null;
	}

	public String verifUpdateSortIndex(String sort_index) {
		
		if (StringUtils.isNullOrEmpty(sort_index)) {
			return "排序索引必填";
		}
		if (!StringUtils.isInteger(sort_index)) {
			return "排序索引输入错误，请输入整数";
		}
		if (Integer.valueOf(sort_index).intValue() < 0) {
			return "排序索引不能小于0";
		}
		
		return null;
	}

}
