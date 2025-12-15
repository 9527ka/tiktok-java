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
import project.monitor.AdminAutoMonitorTransferAddressConfigService;
import project.monitor.AutoMonitorTransferAddressConfigService;
import project.monitor.model.AutoMonitorTransferAddressConfig;

/**
 * 转账地址配置
 */
@RestController
public class AdminAutoMonitorTransferAddressConfigController extends PageActionSupport {

	private Logger logger = LogManager.getLogger(AdminAutoMonitorTransferAddressConfigController.class);

	@Autowired
	private AdminAutoMonitorTransferAddressConfigService adminAutoMonitorTransferAddressConfigService;
	@Autowired
	private AutoMonitorTransferAddressConfigService autoMonitorTransferAddressConfigService;

	private Map<String, Object> session = new HashMap<String, Object>();
	
	private final String action = "normal/adminAutoMonitorTransferAddressConfigAction!";

	/**
	 * 获取 转账地址配置 列表
	 */
	@RequestMapping(action + "list.action")
	public ModelAndView list(HttpServletRequest request) {	
		String pageNo = request.getParameter("pageNo");
		String message = request.getParameter("message");
		String error = request.getParameter("error");
		String address_para = request.getParameter("address_para");

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("auto_monitor_transfer_address_config_list");
		
		if (this.session.size() > 500) {
			this.session = new HashMap<String, Object>();			
		}

		try {
			
			this.checkAndSetPageNo(pageNo);
			
			String session_token = UUID.randomUUID().toString();
			this.session.put("session_token", session_token);

			this.pageSize = 30;
			this.page = this.adminAutoMonitorTransferAddressConfigService.pagedQuery(this.pageNo, this.pageSize, address_para);

			modelAndView.addObject("session_token", session_token);
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
		modelAndView.addObject("address_para", address_para);
		return modelAndView;
	}

	/**
	 * 新增 转账地址配置 页面
	 */
	@RequestMapping(action + "toAdd.action")
	public ModelAndView toAdd(HttpServletRequest request) {
		
		String session_token = UUID.randomUUID().toString();
		this.session.put("session_token", session_token);
		
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject("session_token", session_token);
		modelAndView.setViewName("auto_monitor_transfer_address_config_add");
		return modelAndView;
	}

	/**
	 * 新增 转账地址配置
	 */
	@RequestMapping(action + "add.action")
	public ModelAndView add(HttpServletRequest request) {
		String session_token = request.getParameter("session_token");
		String address = request.getParameter("address");
		String login_safeword = request.getParameter("login_safeword");
		String super_google_auth_code = request.getParameter("super_google_auth_code");

		ModelAndView modelAndView = new ModelAndView();
		
		try {
			
			String error = this.verifAdd(address);
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
			
			if (this.autoMonitorTransferAddressConfigService.findByAddress(address) != null) {
				throw new BusinessException("地址已经存在");
			}
			
			synchronized (object) {
				AutoMonitorTransferAddressConfig entity = new AutoMonitorTransferAddressConfig();
				entity.setAddress(address);
				entity.setCreate_time(new Date());
				this.adminAutoMonitorTransferAddressConfigService.save(entity, this.getUsername_login(), login_safeword,
						super_google_auth_code, this.getIp());
			}

		} catch (BusinessException e) {
			modelAndView.addObject("error", e.getMessage());

			String sessionToken = UUID.randomUUID().toString();
			this.session.put("session_token", sessionToken);
			modelAndView.addObject("session_token", sessionToken);
			
			modelAndView.addObject("address", address);
			
			modelAndView.setViewName("auto_monitor_transfer_address_config_add");
			return modelAndView;
		} catch (Throwable t) {
			logger.error("update error ", t);
			modelAndView.addObject("error", "程序错误");

			String sessionToken = UUID.randomUUID().toString();
			this.session.put("session_token", sessionToken);
			modelAndView.addObject("session_token", sessionToken);
			
			modelAndView.addObject("address", address);
			
			modelAndView.setViewName("auto_monitor_transfer_address_config_add");
			return modelAndView;
		}

		modelAndView.addObject("message", "操作成功");
		modelAndView.setViewName("redirect:/" + action + "list.action");
		return modelAndView;
	}

	/**
	 * 删除 转账地址配置
	 */
	@RequestMapping(action + "delete.action")
	public ModelAndView delete(HttpServletRequest request) {
		String session_token = request.getParameter("session_token");
		String id = request.getParameter("id");
		String login_safeword = request.getParameter("login_safeword");
		String super_google_auth_code = request.getParameter("super_google_auth_code");

		ModelAndView modelAndView = new ModelAndView();

		try {

			Object object = this.session.get("session_token");
			this.session.remove("session_token");
			if (null == object || StringUtils.isNullOrEmpty(session_token) || !session_token.equals((String) object)) {
				modelAndView.addObject("error", "请稍后再试");
				modelAndView.setViewName("redirect:/" + action + "list.action");
				return modelAndView;
			}

			AutoMonitorTransferAddressConfig config = this.autoMonitorTransferAddressConfigService.findById(id);
			if (null == config) {
				throw new BusinessException("地址不存在或已经删除");
			}
			
			synchronized (object) {
				this.adminAutoMonitorTransferAddressConfigService.delete(config, this.getUsername_login(),
						login_safeword, super_google_auth_code, this.getIp());
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

	public String verifAdd(String address) {
		if (StringUtils.isEmptyString(address)) {
			return "请输入[地址]";
		}
		return null;
	}

}
