package project.web.admin;

import java.text.MessageFormat;
import java.util.Date;
import java.util.List;

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
import project.cms.AdminCmsService;
import project.cms.Cms;
import project.cms.PropertiesUtilCms;
import project.log.LogService;
import project.news.News;
import security.Role;
import security.SecUser;
import security.internal.SecUserService;

/**
 * 用户端内容管理
 */
@RestController
public class AdminCmsController extends PageActionSupport {

	private Logger logger = LogManager.getLogger(AdminCmsController.class);

	@Autowired
	private AdminCmsService adminCmsService;
	@Autowired
	private SecUserService secUserService;
	@Autowired
	private LogService logService;
	@Autowired
	private PasswordEncoder passwordEncoder;

	private final String action = "normal/adminCmsAction!";

	/**
	 * 获取 用户端内容管理 列表
	 */
	@RequestMapping(action + "list.action")
	public ModelAndView list(HttpServletRequest request) {
		String pageNo = request.getParameter("pageNo");
		String message = request.getParameter("message");
		String error = request.getParameter("error");
		String language = request.getParameter("language");
		String title = request.getParameter("title");
		String startTime = request.getParameter("startTime");
		String endTime = request.getParameter("endTime");
		Integer status = request.getParameter("status") == null ? -2 : Integer.parseInt(request.getParameter("status"));
		Integer type = request.getParameter("type") == null ? -2 : Integer.parseInt(request.getParameter("type"));
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("cms_list");

		try {

			this.checkAndSetPageNo(pageNo);

			this.pageSize = 20;

			this.page = this.adminCmsService.pagedQuery(this.pageNo, this.pageSize, language, title, startTime, endTime, type, status);


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
		modelAndView.addObject("title", title);
		modelAndView.addObject("language",language);
		modelAndView.addObject("startTime", startTime);
		modelAndView.addObject("endTime", endTime);
		modelAndView.addObject("type", type);
		modelAndView.addObject("status", status);
		return modelAndView;
	}
	
	/**
	 * 新增 用户端内容管理 页面
	 */
	@RequestMapping(action + "toAdd.action")
	public ModelAndView toAdd(HttpServletRequest request) {

		ModelAndView modelAndView = new ModelAndView();
		
		try {

//			if (!this.checkIsRoot()) {
//				throw new BusinessException("root 权限下才可添加");
//			}
			
//			modelAndView.addObject("language", PropertiesUtilCms.getProperty("system_cms_language"));
//			modelAndView.addObject("modelMap", Constants.CMS_MODEL);
//			modelAndView.addObject("languageMap", Constants.LANGUAGE);

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

		modelAndView.setViewName("cms_add");
		return modelAndView;
	}

	/**
	 * 新增 用户端内容管理
	 * 
	 * title 标题
	 * content 内容
	 * model 模块
	 * language 语言
	 * content_code 业务代码
	 */
	@RequestMapping(action + "add.action")
	public ModelAndView add(HttpServletRequest request, Cms cms) {
		String login_safeword = request.getParameter("login_safeword");


		ModelAndView modelAndView = new ModelAndView();
		
		try {
			
			String error = this.verif(cms.getTitle(), cms.getContent());
			if (!StringUtils.isNullOrEmpty(error)) {
				throw new BusinessException(error);
			}
			
//			if (!this.checkIsRoot()) {
//				throw new BusinessException("root 权限下才可添加");
//			}
			
			String username_login = this.getUsername_login();
			
			SecUser sec = this.secUserService.findUserByLoginName(username_login);

			cms.setCreateTime(new Date());
			this.adminCmsService.saveOrUpdate(cms);

			String log = null;
				log = MessageFormat.format("ip:" + this.getIp() + ",管理员新增cms，id:{0},标题:{1},语言:{2},模块:{3}",
						cms.getId(), cms.getTitle(), cms.getLanguage());
			this.saveLog(sec, username_login, log);
			
		} catch (BusinessException e) {
			modelAndView.addObject("error", e.getMessage());
//			modelAndView.addObject("model", model);
//			modelAndView.addObject("language", language);
//			modelAndView.addObject("content_code", content_code);
//			modelAndView.addObject("title", title);
//			modelAndView.addObject("content", content);
//			modelAndView.addObject("modelMap", Constants.CMS_MODEL);
//			modelAndView.addObject("languageMap", Constants.LANGUAGE);
			modelAndView.setViewName("cms_add");
			return modelAndView;
		} catch (Throwable t) {
			logger.error(" error ", t);
			modelAndView.addObject("error", "[ERROR] " + t.getMessage());
//			modelAndView.addObject("model", model);
//			modelAndView.addObject("language", language);
//			modelAndView.addObject("content_code", content_code);
//			modelAndView.addObject("title", title);
//			modelAndView.addObject("content", content);
//			modelAndView.addObject("modelMap", Constants.CMS_MODEL);
//			modelAndView.addObject("languageMap", Constants.LANGUAGE);
			modelAndView.setViewName("cms_add");
			return modelAndView;
		}
		
		modelAndView.addObject("message", "操作成功");
		modelAndView.setViewName("redirect:/" + action + "list.action");
		return modelAndView;
	}

	/**
	 * 修改 用户端内容管理 页面
	 * 
	 * title 标题
	 * content 内容
	 * model 模块
	 * language 语言
	 * content_code 业务代码
	 */
	@RequestMapping(action + "toUpdate.action")
	public ModelAndView toUpdate(HttpServletRequest request) {
		String id = request.getParameter("id");

		ModelAndView modelAndView = new ModelAndView();

		try {
			
			if (StringUtils.isNullOrEmpty(id)) {
				throw new BusinessException("内容不存在或已删除");
			}
						
			Cms cms = this.adminCmsService.findById(id);
			if (null == cms) {
				throw new BusinessException("内容不存在或已删除");
			}

			modelAndView.addObject("cms", cms);

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
		
		modelAndView.setViewName("cms_update");
		return modelAndView;
	}
	
	/**
	 * 修改 用户端内容管理
	 * 
	 * title 标题
	 * content 内容
	 * model 模块
	 * language 语言
	 * content_code 业务代码
	 */
	@RequestMapping(action + "update.action")
	public ModelAndView update(HttpServletRequest request, Cms cms) {
		String login_safeword = request.getParameter("login_safeword");

		ModelAndView modelAndView = new ModelAndView();

		try {

			String error = this.verif(cms.getTitle(), cms.getContent());
			if (!StringUtils.isNullOrEmpty(error)) {
				throw new BusinessException(error);
			}

			String username_login = this.getUsername_login();

			SecUser sec = this.secUserService.findUserByLoginName(username_login);

			this.checkLoginSafeword(sec, username_login, login_safeword);

			if (StringUtils.isNullOrEmpty(cms.getId().toString())) {
				throw new BusinessException("内容不存在或已删除");
			}

			Cms entity = this.adminCmsService.findById(cms.getId().toString());
			if (null == entity) {
				throw new BusinessException("内容不存在或已删除");
			}
			cms.setCreateTime(entity.getCreateTime());

			String log = null;
			log = MessageFormat.format("ip:" + this.getIp() + ",管理员修改cms，id:{0},原标题:{1},原语言:{2},原模块:{3}",
						entity.getId(), entity.getTitle(), entity.getLanguage());

			this.adminCmsService.saveOrUpdate(cms);
			saveLog(sec, username_login, log);

		} catch (BusinessException e) {
			modelAndView.addObject("cms", cms);
			modelAndView.addObject("error", e.getMessage());
			modelAndView.setViewName("cms_update");
			return modelAndView;
		} catch (Throwable t) {
			logger.error(" error ", t);
			modelAndView.addObject("cms", cms);
			modelAndView.addObject("error", "[ERROR] " + t.getMessage());
			modelAndView.setViewName("cms_update");
			return modelAndView;
		}
		
		modelAndView.addObject("message", "操作成功");
		modelAndView.setViewName("redirect:/" + action + "list.action");
		return modelAndView;
	}

	/**
	 * 删除 新闻管理
	 */
	@RequestMapping(action + "delete.action")
	public ModelAndView delete(HttpServletRequest request) {
		String id = request.getParameter("id");
		String login_safeword = request.getParameter("login_safeword");

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("redirect:/" + action + "list.action");

		try {

			String userNameLogin = this.getUsername_login();

			SecUser sec = this.secUserService.findUserByLoginName(userNameLogin);
			this.checkLoginSafeword(sec, userNameLogin, login_safeword);

			Cms cms = this.adminCmsService.findById(id);

			String log = MessageFormat.format("ip:" + this.getIp() + ",管理员删除公告,id:{0},原标题:{1},原语言:{2},原模块:{3}",
					cms.getId(), cms.getTitle(), cms.getLanguage(),  cms.getType());
			this.saveLog(sec, userNameLogin, log);
			this.adminCmsService.delete(cms);

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

	private boolean checkIsRoot() {
		String username = this.getUsername_login();
		// root才可以改动
		SecUser secUser = this.secUserService.findUserByLoginName(username);
		for (Role role : secUser.getRoles()) {
			if (Constants.SECURITY_ROLE_ROOT.equals(role.getRoleName())) {
				return Boolean.TRUE;
			}
		}
		return Boolean.FALSE;
	}

	public String verif(String title, String content) {
		if (StringUtils.isNullOrEmpty(title)) {
			return "请输入标题！";
		}
		if (StringUtils.isNullOrEmpty(content)) {
			return "请输入内容！";
		}
		return "";
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

	public void saveLog(SecUser secUser, String operator, String context) {
		project.log.Log log = new project.log.Log();
		log.setCategory(Constants.LOG_CATEGORY_OPERATION);
		log.setOperator(operator);
		log.setUsername(secUser.getUsername());
		log.setPartyId(secUser.getPartyId());
		log.setLog(context);
		log.setCreateTime(new Date());
		logService.saveSync(log);
	}

}
