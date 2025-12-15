package project.web.admin.systemuser;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import kernel.exception.BusinessException;
import kernel.util.DateUtils;
import kernel.web.PageActionSupport;
import systemuser.AdminCustomerService;
import systemuser.CustomerService;
import systemuser.model.Customer;

/**
 * 客服个人中心
 *
 */
@RestController
public class AdminPersonalCustomerController extends PageActionSupport {
	
	private Logger logger = LogManager.getLogger(AdminPersonalCustomerController.class);
	
	@Autowired
	private AdminCustomerService adminCustomerService;
	
	@Autowired
	private CustomerService customerService;
	
	private final String action = "normal/adminPersonalCustomerAction!";
	
	/**
	 * 点击客服中心
	 */
	@RequestMapping(action + "personalCustomer.action")
	public ModelAndView personalCustomer(HttpServletRequest request) {
		
		String message = request.getParameter("message");
		String error = request.getParameter("error");
		
		boolean off_to_online = true;
		if("/normal/adminPersonalCustomerAction!personalCustomer.action".equals(request.getServletPath())) {
			off_to_online = false;
		}
		
		String username = null;
		String last_online_time = null;
		String last_offline_time = null;
		String auto_answer = null;
		Integer online_state = null;
		ModelAndView model = new ModelAndView();
		
		try {
			Customer customer = this.customerService.cacheByUsername(this.getUsername_login());
		    if (null != customer) {
		    	last_online_time = DateUtils.format(customer.getLast_online_time(), DateUtils.DF_yyyyMMddHHmmss);
				last_offline_time = DateUtils.format(customer.getLast_offline_time(), DateUtils.DF_yyyyMMddHHmmss);
				auto_answer = customer.getAuto_answer();
				online_state = customer.getOnline_state();
		    }
			username = this.getUsername_login();
		} catch (BusinessException e) {
			error = e.getMessage();
		} catch (Throwable t) {
			logger.error(" error ", t);
			error = ("[ERROR] " + t.getMessage());
		}
		
		model.addObject("off_to_online", off_to_online);
		model.addObject("username", username);
		model.addObject("last_online_time", last_online_time);
		model.addObject("last_offline_time", last_offline_time);
		model.addObject("auto_answer", auto_answer);
		model.addObject("online_state", online_state);
		
		model.addObject("message", message);
		model.addObject("error", error);
		model.setViewName("personal_customer");
		return model;
	}
	
	/**
	 * 上线
	 */
	@RequestMapping(action + "personalOnline.action")
	public ModelAndView personalOnline() {
		
		String message = "";
		String error = "";
		boolean off_to_online = false;
		try {
			this.adminCustomerService.online(this.getUsername_login());
		    off_to_online = true;
			message = "操作成功";
		} catch (BusinessException e) {
			error = e.getMessage();
		} catch (Throwable t) {
			logger.error(" error ", t);
			error = ("[ERROR] " + t.getMessage());
		}
		ModelAndView model = new ModelAndView();
		model.addObject("off_to_online", off_to_online);
		model.addObject("message", message);
		model.addObject("error", error);
		model.setViewName("redirect:/" + action + "personalCustomer.action");
		return model;
	}
	
	/**
	 * 下线
	 */
	@RequestMapping(action + "personalOffline.action")
	public ModelAndView personalOffline() {
		String message = "";
		String error = "";
		boolean off_to_online = true;
		try {
			this.adminCustomerService.offline(this.getUsername_login());
			off_to_online = false;
			message = "操作成功";
		} catch (BusinessException e) {
			error = e.getMessage();
		} catch (Throwable t) {
			logger.error(" error ", t);
			error = ("[ERROR] " + t.getMessage());
		}
		ModelAndView model = new ModelAndView();
		model.addObject("off_to_online", off_to_online);
		model.addObject("message", message);
		model.addObject("error", error);
		model.setViewName("redirect:/" + action + "personalCustomer.action");
		return model;
	}

	@RequestMapping(action + "personalUpdateAutoAnswer.action")
	public ModelAndView personalUpdateAutoAnswer(HttpServletRequest request) {
		String message = "";
		String error = "";
		String login_safeword = request.getParameter("login_safeword");
		String auto_answer = request.getParameter("auto_answer");
		boolean off_to_online = true;
		try {
			adminCustomerService.updatePersonalAutoAnswer(this.getUsername_login(), login_safeword, 
					this.getIp(), auto_answer);
			off_to_online = false;
			message = "操作成功";
		} catch (BusinessException e) {
			error = e.getMessage();
		} catch (Throwable t) {
			logger.error(" error ", t);
			error = ("[ERROR] " + t.getMessage());
		}
		ModelAndView model = new ModelAndView();
		model.addObject("off_to_online", off_to_online);
		model.addObject("message", message);
		model.addObject("error", error);
		model.setViewName("redirect:/" + action + "personalCustomer.action");
		return model;
	}

	public Integer customerOnlineState() {
		Customer customer = customerService.cacheByUsername(this.getUsername_login());
		if(null == customer) {
			return null;
		}
		return customer.getOnline_state();
	}

}
