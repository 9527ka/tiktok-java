package project.web.admin.monitor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.providers.encoding.PasswordEncoder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import kernel.exception.BusinessException;
import kernel.web.PageActionSupport;
import project.Constants;
import project.monitor.AdminAutoMonitorTipService;
import project.monitor.model.AutoMonitorTip;
import project.tip.TipService;
import project.user.googleauth.GoogleAuthService;
import security.internal.SecUserService;

/**
 * 异常提醒
 */
@RestController
public class AdminAutoMonitorTipController extends PageActionSupport {

	private Logger logger = LogManager.getLogger(AdminAutoMonitorTipController.class);
	
	@Autowired
	private AdminAutoMonitorTipService adminAutoMonitorTipService;
	@Autowired
	protected PasswordEncoder passwordEncoder;
	@Autowired
	protected GoogleAuthService googleAuthService;
	@Autowired
	protected SecUserService secUserService;
	@Autowired
	private TipService tipService;
	
	private Map<String, Object> session = new HashMap();
	private final static Object obj = new Object();
	
	private final String action = "normal/adminAutoMonitorTipAction!";

	@RequestMapping(value = action + "list.action")
	public ModelAndView list(HttpServletRequest request) {
		
		String session_token = UUID.randomUUID().toString();
		this.session.put("session_token", session_token);
		this.checkAndSetPageNo(request.getParameter("pageNo"));
		this.pageSize = 20;
		String loginPartyId = getLoginPartyId();
		
		String message = request.getParameter("message");
		String name_para = request.getParameter("name_para");
		// 0 阀值 提醒 1 ETH充值 
		Integer tiptype_para = null;
		if (null != request.getParameter("tiptype_para") && !"".equals(request.getParameter("tiptype_para"))) {
			
			tiptype_para = Integer.valueOf(request.getParameter("tiptype_para"));
		}
		// 是否已查看确认知道这条消息， 0 初始状态，未知 1  已确认
		Integer is_confirmed_para = null;
		if (null != request.getParameter("is_confirmed_para") && !"".equals(request.getParameter("is_confirmed_para"))) {
			
			is_confirmed_para = Integer.valueOf(request.getParameter("is_confirmed_para"));
		}

		this.page = this.adminAutoMonitorTipService.pagedQuery(this.pageNo, this.pageSize, name_para,
				tiptype_para, is_confirmed_para, loginPartyId);

		List<Map> list = this.page.getElements();
		for (int i = 0; i < list.size(); i++) {
			Map map = list.get(i);			
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
		
		model.addObject("session_token", session_token);
		model.addObject("name_para", name_para);
		model.addObject("tiptype_para", tiptype_para);
		model.addObject("is_confirmed_para", is_confirmed_para);
		
		model.addObject("message", message);
		model.setViewName("auto_monitor_remind");
		return model;
	}
	
	@RequestMapping(value = action + "confirmed.action")
	public ModelAndView confirmed(HttpServletRequest request) {
		String message = "";
		String error = "";
		try {
			String id = request.getParameter("id");
			AutoMonitorTip entity= adminAutoMonitorTipService.findById(id);
			if (entity!=null) {
				entity.setIs_confirmed(1);
				adminAutoMonitorTipService.update(entity);
				tipService.deleteTip(entity.getId().toString());
				
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

}
