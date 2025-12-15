package project.web.admin;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.providers.encoding.PasswordEncoder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import kernel.exception.BusinessException;
import kernel.util.StringUtils;
import kernel.web.PageActionSupport;
import project.Constants;
import project.log.AdminLogService;
import project.log.Log;
import project.log.LogService;
import security.SecUser;
import security.internal.SecUserService;

@RestController
public class AdminCodeLogController extends PageActionSupport {

	@Autowired
	private AdminLogService adminLogService;
	@Autowired
	private LogService logService;
	@Autowired
	private SecUserService secUserService;
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	private final String action = "normal/adminCodeLogAction!";
	
	@RequestMapping(action + "list.action")
	public ModelAndView list(HttpServletRequest request) {
		this.checkAndSetPageNo(request.getParameter("pageNo"));
		this.pageSize = 30;
		String loginPartyId = getLoginPartyId();
	
		String date_para = request.getParameter("date_para");
		String log_para = request.getParameter("log_para");
		String name_para = request.getParameter("name_para");
		String target = request.getParameter("target");
		
		List<Date> date_range = toRangeDate(date_para);
		this.page = this.adminLogService.pagedQueryCodeLog(this.pageNo, pageSize, log_para, name_para, target, 
				date_range.get(0), date_range.get(1), loginPartyId, this.getUsername_login(),null);
		
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject("pageNo", this.pageNo);
		modelAndView.addObject("pageSize", this.pageSize);
		modelAndView.addObject("page", this.page);
		modelAndView.addObject("message", message);
		modelAndView.addObject("error", error);
		modelAndView.addObject("date_para", date_para);
		modelAndView.addObject("log_para", log_para);
		modelAndView.addObject("name_para", name_para);
		modelAndView.addObject("target", target);
		modelAndView.setViewName("code_log_list");
		return modelAndView;
	}
	
	@RequestMapping(action + "get_code.action")
	public ModelAndView get_code(HttpServletRequest request) {
		
		String login_safeword = request.getParameter("login_safeword");
		String log_id = request.getParameter("log_id");
		
		String error = "";
		ModelAndView modelAndView = new ModelAndView();
		
		try {
			if (StringUtils.isEmptyString(login_safeword)) {
				throw new BusinessException("资金密码不能为空");
			}
			SecUser sec = this.secUserService.findUserByLoginName(this.getUsername_login());

			checkLoginSafeword(sec, this.getUsername_login(), login_safeword);
			
			this.page = this.adminLogService.pagedQueryCodeLog(pageNo, pageSize, null, null, null, 
					null, null, getLoginPartyId(), this.getUsername_login(), log_id);
			List<Map> list = page.getElements();
			Map map = list.get(0);
			Log log = new Log();
			log.setCategory(Constants.LOG_CATEGORY_OPERATION);
			log.setUsername("");
			log.setOperator(this.getUsername_login());
			log.setLog("管理员查看手机号/邮箱号["+String.valueOf(map.get("target"))+"]的验证码，管理员ip["+this.getIp(getRequest())+"]");
			logService.saveSync(log);
			
			modelAndView.addObject("page", this.page);
			modelAndView.addObject("message", "操作成功");
			modelAndView.setViewName("code_log_get");
			return modelAndView;
		} catch (BusinessException e) {
			modelAndView.addObject("error", e.getMessage());
			modelAndView.setViewName("code_log_list");
			return modelAndView;
		} catch (Throwable t) {
			error = ("[ERROR] " + t.getMessage());
			modelAndView.addObject("error", error);
			modelAndView.setViewName("code_log_list");
			return modelAndView;
		}
	}
	
	
	/**
	 * 验证登录人资金密码
	 */
	private void checkLoginSafeword(SecUser secUser,String operatorUsername,String loginSafeword) {
		String sysSafeword = secUser.getSafeword();
		String safeword_md5 = passwordEncoder.encodePassword(loginSafeword, operatorUsername);
		if (!safeword_md5.equals(sysSafeword)) {
			throw new BusinessException("登录人资金密码错误");
		}
	}
}
