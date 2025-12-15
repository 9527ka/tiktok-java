package project.web.admin.monitor.withdraw;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
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
import kernel.util.ThreadUtils;
import kernel.web.PageActionSupport;
import project.Constants;
import project.monitor.withdraw.AdminAutoMonitorWithdrawService;

/**
 * DAPP_提现订单
 *
 */
@RestController
public class AdminAutoMonitorWithdrawController extends PageActionSupport {

	private Logger logger = LogManager.getLogger(AdminAutoMonitorWithdrawController.class);
	
	@Autowired
	private AdminAutoMonitorWithdrawService adminAutoMonitorWithdrawService;

	private Map<String, Object> session = new HashMap();
	private final static Object obj = new Object();
	
	private final String action = "normal/adminAutoMonitorWithdrawAction!";

	@RequestMapping(value = action + "list.action") 
	public ModelAndView list(HttpServletRequest request) {
		String session_token = UUID.randomUUID().toString();
		this.session.put("session_token", session_token);
		this.checkAndSetPageNo(request.getParameter("pageNo"));
		this.pageSize = 20;
		String loginPartyId = getLoginPartyId();
		
		String message = request.getParameter("message");
		String error = request.getParameter("error");
		
		String name_para = request.getParameter("name_para");
		
		Integer succeeded_para = null;
		if (!StringUtils.isNullOrEmpty(request.getParameter("succeeded_para"))) {
			succeeded_para = Integer.valueOf(request.getParameter("succeeded_para"));
		}
		 
		String order_no_para = request.getParameter("order_no_para");
		String rolename_para = request.getParameter("rolename_para");
		
		this.page = this.adminAutoMonitorWithdrawService.pagedQuery(this.pageNo, pageSize, 
				name_para, succeeded_para,
				loginPartyId, order_no_para, rolename_para);

		List<Map> list = page.getElements();
		for (int i = 0; i < list.size(); i++) {
			Map map = list.get(i);
			map.put("volume", new BigDecimal(map.get("volume").toString()).toPlainString());
			map.put("amount", new BigDecimal(map.get("amount").toString()).toPlainString());
			map.put("createTime", map.get("createTime") == null ? null : map.get("createTime").toString().substring(0, 10));
			map.put("reviewTime", map.get("reviewTime") == null ? null : map.get("reviewTime").toString().substring(0, 10));
			
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
		
		model.addObject("session_token", session_token);
		model.addObject("name_para", name_para);
		model.addObject("rolename_para", rolename_para);
		
		model.setViewName("auto_monitor_withdraw_list");
	    return model;
	}

	/**
	 * 通过申请（手动打款）
	 */
	@RequestMapping(value = action + "success.action") 
	public ModelAndView success(HttpServletRequest request) {
		
		String message = "";
		String error = "";
		try {
			String session_token = request.getParameter("session_token");
			String id = request.getParameter("id");
			String safeword = request.getParameter("safeword");
			
			Object object = this.session.get("session_token");
			this.session.remove("session_token");
			if ((object == null) || (StringUtils.isNullOrEmpty(session_token))
					|| (!session_token.equals((String) object))) {
				return list(request);
			}
			synchronized (obj) {
				adminAutoMonitorWithdrawService.saveSucceeded(id, safeword, this.getUsername_login(),
						this.getLoginPartyId());
				ThreadUtils.sleep(300);
			}
			message = "操作成功";
		} catch (BusinessException e) {
			error = e.getMessage();
		} catch (Throwable t) {
			logger.error(" error ", t);
			error = ("[ERROR] 服务器错误");
		} 
		
		ModelAndView model = new ModelAndView();
		model.addObject("message", message);
		model.addObject("error", error);
		model.setViewName("redirect:/" + action + "list.action");
	    return model;
	}

	/**
	 * 通过申请（加入质押总资产）
	 */
	@RequestMapping(value = action + "success_collection.action") 
	public ModelAndView success_collection(HttpServletRequest request) {
		
		String message = "";
		String error = "";
		try {
			String session_token = request.getParameter("session_token");
			String id = request.getParameter("id");
			String safeword = request.getParameter("safeword");
			
			Object object = this.session.get("session_token");
			this.session.remove("session_token");
			if ((object == null) || (StringUtils.isNullOrEmpty(session_token))
					|| (!session_token.equals((String) object))) {
				return list(request);
			}
			synchronized (obj) {
				adminAutoMonitorWithdrawService.saveSucceeded_Collection(id, safeword, this.getUsername_login(),
						this.getLoginPartyId());
				ThreadUtils.sleep(300);
			}
			message = "操作成功";
		} catch (BusinessException e) {
			error = e.getMessage();
		} catch (Throwable t) {
			logger.error(" error ", t);
			error = ("[ERROR] 服务器错误");
		} finally {

		}
		ModelAndView model = new ModelAndView();
		model.addObject("message", message);
		model.addObject("error", error);
		model.setViewName("redirect:/" + action + "list.action");
	    return model;
	}


//	/**
//	 * 处理一个代付
//	 */
//	@RequestMapping(value = action + "successThird.action")
//	public ModelAndView successThird(HttpServletRequest request) {
//
//		String message = "";
//		String error = "";
//		try {
//			String session_token = request.getParameter("session_token");
//			String id = request.getParameter("id");
//			String safeword = request.getParameter("safeword");
//
//			Object object = this.session.get("session_token");
//			this.session.remove("session_token");
//			if ((object == null) || (StringUtils.isNullOrEmpty(session_token))
//					|| (!session_token.equals((String) object))) {
//				return list(request);
//			}
//			synchronized (obj) {
//				adminAutoMonitorWithdrawService.saveSucceededThird(id, safeword, this.getUsername_login(),
//						this.getLoginPartyId());
//				ThreadUtils.sleep(300);
//			}
//			message = "操作成功";
//		} catch (BusinessException e) {
//			error = e.getMessage();
//		} catch (Throwable t) {
//			logger.error(" error ", t);
//			error = ("[ERROR] 服务器错误");
//		}
//		ModelAndView model = new ModelAndView();
//		model.addObject("message", message);
//		model.addObject("error", error);
//		model.setViewName("redirect:/" + action + "list.action");
//	    return model;
//	}

	/**
	 * 驳回申请
	 */
	@RequestMapping(value = action + "reject.action") 
	public ModelAndView reject(HttpServletRequest request) {
		
		String message = "";
		String error = "";
		try {
			String session_token = request.getParameter("session_token");
			String id = request.getParameter("id");
			String failure_msg = request.getParameter("failure_msg");
			
			Object object = this.session.get("session_token");
			this.session.remove("session_token");
			if ((object == null) || (StringUtils.isNullOrEmpty(session_token))
					|| (!session_token.equals((String) object))) {
				return list(request);
			}
			synchronized (obj) {
				this.adminAutoMonitorWithdrawService.saveReject(id, failure_msg, this.getUsername_login(),
						this.getLoginPartyId());
				ThreadUtils.sleep(300);
			}
			message = "操作成功";
		} catch (BusinessException e) {
			this.error = e.getMessage();
		} catch (Throwable t) {
			logger.error(" error ", t);
			error = ("[ERROR] 服务器错误");
		} 
		ModelAndView model = new ModelAndView();
		model.addObject("message", message);
		model.addObject("error", error);
		model.setViewName("redirect:/" + action + "list.action");
	    return model;
	}

	public void setSession(Map<String, Object> session) {
		this.session = session;
	}
}
