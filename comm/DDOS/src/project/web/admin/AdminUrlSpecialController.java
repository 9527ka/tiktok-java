package project.web.admin;

import java.util.Date;

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
import project.ddos.AdminUrlSpecialService;
import project.ddos.UrlSpecialService;
import project.ddos.model.UrlSpecial;

/**
 * 特殊URL管理
 */
@RestController
public class AdminUrlSpecialController extends PageActionSupport {

	private Logger logger = LogManager.getLogger(AdminUrlSpecialController.class);

	@Autowired
	private AdminUrlSpecialService adminUrlSpecialService;
	@Autowired
	private UrlSpecialService urlSpecialService;
	
	private final String action = "normal/adminUrlSpecialAction!";

	/**
	 * 获取 特殊URL 列表
	 */
	@RequestMapping(action + "list.action")
	public ModelAndView list(HttpServletRequest request) {
		String pageNo = request.getParameter("pageNo");
		String message = request.getParameter("message");
		String error = request.getParameter("error");
		String url_para = request.getParameter("url_para");

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("url_special_list");

		try {
			
			this.checkAndSetPageNo(pageNo);
			
			this.pageSize = 30;
			this.page = this.adminUrlSpecialService.pagedQuery(this.pageNo, this.pageSize, url_para);

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
		modelAndView.addObject("url_para", url_para);
		return modelAndView;
	}

	/**
	 * 新增 特殊URL 页面
	 */
	@RequestMapping(action + "toAdd.action")
	public ModelAndView toAdd(HttpServletRequest request) {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("url_special_add");
		return modelAndView;
	}

	/**
	 * 新增 特殊URL
	 */
	@RequestMapping(action + "add.action")
	public ModelAndView add(HttpServletRequest request) {		
		String login_safeword = request.getParameter("login_safeword");
		String url = request.getParameter("url");
		String remarks = request.getParameter("remarks");

		ModelAndView modelAndView = new ModelAndView();
		
		try {
			
			this.check();
			
			String error = verif_add(url);
			if (!StringUtils.isNullOrEmpty(error)) {
				throw new BusinessException(error);
			}
			
			if (StringUtils.isNullOrEmpty(login_safeword)) {
				throw new BusinessException("请输入登录人资金密码");
			}
			
			UrlSpecial urlSpecial = new UrlSpecial();
			urlSpecial.setCreate_time(new Date());
			urlSpecial.setUrl(url);
			urlSpecial.setRemarks(remarks);

			// 补充设值
			this.adminUrlSpecialService.save(urlSpecial, this.getUsername_login(), login_safeword, this.getIp());
			
		} catch (BusinessException e) {
			modelAndView.addObject("error", e.getMessage());
			modelAndView.addObject("url", url);
			modelAndView.addObject("remarks", remarks);
			modelAndView.setViewName("url_special_add");
			return modelAndView;
		} catch (Throwable t) {
			logger.error(" error ", t);
			modelAndView.addObject("error", "[ERROR] " + t.getMessage());
			modelAndView.addObject("url", url);
			modelAndView.addObject("remarks", remarks);
			modelAndView.setViewName("url_special_add");
			return modelAndView;
		}

		modelAndView.addObject("message", "操作成功");
		modelAndView.setViewName("redirect:/" + action + "list.action");
		return modelAndView;
	}

	/**
	 * 修改 特殊URL 页面
	 */
	@RequestMapping(action + "toUpdate.action")
	public ModelAndView toUpdate(HttpServletRequest request) {
		String id = request.getParameter("id");

		ModelAndView modelAndView = new ModelAndView();

		try {			

			this.check();
			
			UrlSpecial special = this.urlSpecialService.cacheById(id);

			modelAndView.addObject("id", id);
			modelAndView.addObject("url", special.getUrl());
			modelAndView.addObject("remarks", special.getRemarks());
			
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
		
		modelAndView.setViewName("url_special_update");
		return modelAndView;
	}

	/**
	 * 修改 特殊URL
	 */
	@RequestMapping(action + "update.action")
	public ModelAndView update(HttpServletRequest request) {
		String id = request.getParameter("id");
		String url = request.getParameter("url");		
		String remarks = request.getParameter("remarks");
		String login_safeword = request.getParameter("login_safeword");

		ModelAndView modelAndView = new ModelAndView();
		
		try {
			
			this.check();
			
			String error = verif_add(url);
			if (!StringUtils.isNullOrEmpty(error)) {
				throw new BusinessException(error);
			}
			
			UrlSpecial urlSpecial = new UrlSpecial();
			urlSpecial.setCreate_time(new Date());
			urlSpecial.setId(id);
			urlSpecial.setUrl(url);
			urlSpecial.setRemarks(remarks);

			this.adminUrlSpecialService.update(urlSpecial, this.getUsername_login(), login_safeword, this.getIp());
						
		} catch (BusinessException e) {
			modelAndView.addObject("error", e.getMessage());
			modelAndView.addObject("id", id);
			modelAndView.addObject("url", url);
			modelAndView.addObject("remarks", remarks);
			modelAndView.setViewName("url_special_update");
			return modelAndView;
		} catch (Throwable t) {
			logger.error(" error ", t);
			modelAndView.addObject("error", "[ERROR] " + t.getMessage());
			modelAndView.addObject("id", id);
			modelAndView.addObject("url", url);
			modelAndView.addObject("remarks", remarks);
			modelAndView.setViewName("url_special_update");
			return modelAndView;
		}

		modelAndView.addObject("message", "操作成功");
		modelAndView.setViewName("redirect:/" + action + "list.action");
		return modelAndView;
	}

	/**
	 * 修改 特殊URL
	 */
	@RequestMapping(action + "toDelete.action")
	public ModelAndView toDelete(HttpServletRequest request) {
		String id = request.getParameter("id");
		String login_safeword = request.getParameter("login_safeword");

		ModelAndView modelAndView = new ModelAndView();

		try {
			
			this.check();
			
			this.adminUrlSpecialService.delete(id, this.getUsername_login(), login_safeword, this.getIp());
			
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

	private void check() {
		String loginUserName = this.getUsername_login();
		if (!("root".equals(loginUserName))) {
			throw new BusinessException("权限不足，无法操作");
		}
	}

	private String verif_add(String url) {
		if (StringUtils.isEmptyString(url)) {
			return "请输入[URL]";
		}
//		if (StringUtils.isEmptyString(this.menu_type)) {
//			return "请选择[名单]";
//		}
		return null;
	}

}
