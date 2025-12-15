package project.web.admin;

import java.text.MessageFormat;
import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.providers.encoding.PasswordEncoder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import kernel.exception.BusinessException;
import kernel.util.StringUtils;
import kernel.web.PageActionSupport;
import project.Constants;
import project.log.LogService;
import project.news.AdminNewsService;
import project.news.News;
import security.SecUser;
import security.internal.SecUserService;

/**
 * 新闻管理
 */
@RestController
public class AdminNewsController extends PageActionSupport {

	private Logger logger = LogManager.getLogger(AdminNewsController.class);

	@Autowired
	private AdminNewsService adminNewsService;
	@Autowired
	private SecUserService secUserService;
	@Autowired
	private LogService logService;
	@Autowired
	private PasswordEncoder passwordEncoder;

	private final String action = "normal/adminNewsAction!";
	
	/**
	 * 获取 新闻管理 列表
	 */
	@RequestMapping(action + "list.action")
	public ModelAndView list(HttpServletRequest request) {
		String pageNo = request.getParameter("pageNo");
		String message = request.getParameter("message");
		String error = request.getParameter("error");
		String title = request.getParameter("title");
		String lang = request.getParameter("lang");
		String startTime = request.getParameter("startTime");
		String endTime = request.getParameter("endTime");
		Integer status = request.getParameter("status") == null ? -2 : Integer.parseInt(request.getParameter("status"));
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("news_list");

		try {
			this.checkAndSetPageNo(pageNo);
			this.pageSize = 20;
			this.page = this.adminNewsService.pagedQuery(this.pageNo, this.pageSize, title, lang,startTime,endTime,status);

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
		modelAndView.addObject("lang", lang);
//		modelAndView.addObject("languageMap", Constants.LANGUAGE);
		return modelAndView;
	}
	
	/**
	 * 新增 新闻管理 页面
	 */
	@RequestMapping(action + "toAdd.action")
	public ModelAndView toAdd(HttpServletRequest request) {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject("languageMap", Constants.LANGUAGE);
		modelAndView.setViewName("news_add");
		return modelAndView;
	}

	/**
	 * 新增 新闻管理
	 * 
	 * title 标题
	 * content 内容
	 * index 是否置顶
	 * language 语言
	 */
	@RequestMapping(action + "add.action")
	public ModelAndView add(HttpServletRequest request) {
		String pageNo = request.getParameter("pageNo");
		String title = request.getParameter("title");
		String content_text = request.getParameter("content");
		String lang = request.getParameter("lang");
		String iconImg = request.getParameter("iconImg");
		String status = request.getParameter("status");
		String sort = request.getParameter("sort");
		String releaseTime = request.getParameter("releaseTime");

		ModelAndView modelAndView = new ModelAndView();

		modelAndView.addObject("pageNo", pageNo);
		modelAndView.addObject("title", title);
		modelAndView.addObject("content", content_text);
		modelAndView.addObject("lang", lang);
		modelAndView.addObject("status", status);
		modelAndView.addObject("sort", sort);
		modelAndView.addObject("iconImg", iconImg);
		modelAndView.addObject("releaseTime", releaseTime);
		try {
			
			String error = this.verif(title, content_text,iconImg,sort,releaseTime);
			if (!StringUtils.isNullOrEmpty(error)) {
				throw new BusinessException(error);
			}
			String userNameLogin = this.getUsername_login();

			News news = new News();
			news.setTitle(title);
			news.setCreateTime(new Date());
			news.setIconImg(iconImg);
			news.setStatus(Integer.parseInt(status));
			news.setContent(content_text);
			news.setLang(lang);
			news.setReleaseTime(releaseTime);
			news.setSort(Integer.parseInt(sort));
			this.adminNewsService.save(news);
			SecUser sec = this.secUserService.findUserByLoginName(userNameLogin);
			String log = MessageFormat.format("ip:" + this.getIp() + ",管理员新增新闻,id:{0},标题:{1},语言:{2},内容:{3}",
					news.getId(), news.getTitle(), news.getLang(),news.getContent());
			this.saveLog(sec, userNameLogin, log);
			
		} catch (BusinessException e) {
			modelAndView.addObject("error", e.getMessage());			
			modelAndView.setViewName("news_add");
			return modelAndView;
		} catch (Throwable t) {
			logger.error(" error ", t);
			modelAndView.addObject("error", "[ERROR] " + t.getMessage());		
			modelAndView.setViewName("news_add");
			return modelAndView;
		}
		
		modelAndView.addObject("message", "操作成功");
		modelAndView.setViewName("redirect:/" + action + "list.action");
		return modelAndView;
	}

	private String verif(String title, String content_text, String iconImg, String sort, String releaseTime) {
		if(StringUtils.isEmptyString(title)){
			throw new BusinessException("标题不能为空");
		}
		if(StringUtils.isEmptyString(content_text)){
			throw new BusinessException("内容不能为空");
		}
		if(StringUtils.isEmptyString(iconImg)){
			throw new BusinessException("请选择照片");
		}
		if(StringUtils.isEmptyString(sort)){
			throw new BusinessException("请输入排序");
		}
		if(StringUtils.isEmptyString(releaseTime)){
			throw new BusinessException("请输入发布时间");
		}
		return null;
	}

	/**
	 * 修改 新闻管理 页面
	 * 
	 * title 标题
	 * content 内容
	 * index 是否置顶
	 * language 语言
	 */
	@RequestMapping(action + "toUpdate.action")
	public ModelAndView toUpdate(HttpServletRequest request) {
		String id = request.getParameter("id");

		ModelAndView modelAndView = new ModelAndView();

		try {
			
			News news = this.adminNewsService.findById(id);

			modelAndView.addObject("news", news);
//			modelAndView.addObject("title", news.getTitle());
//			modelAndView.addObject("content", news.getContent());
//			modelAndView.addObject("lang", news.getLang());
//			modelAndView.addObject("status", news.getStatus());
//			modelAndView.addObject("sort", news.getSort());
//			modelAndView.addObject("iconImg", iconImg);
//			modelAndView.addObject("releaseTime", releaseTime);

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
		
		modelAndView.setViewName("news_update");
		return modelAndView;
	}
	
	/**
	 * 修改 新闻管理
	 * 
	 * title 标题
	 * content 内容
	 * index 是否置顶
	 * language 语言
	 */
	@RequestMapping(action + "update.action")
	public ModelAndView update(HttpServletRequest request, News news, @RequestParam Map<String, String> paramMap) {

		ModelAndView modelAndView = new ModelAndView();
		String content_text = request.getParameter("content");
		try {
			String error = this.verif(news.getTitle(),content_text,news.getIconImg(),news.getSort().toString(),news.getReleaseTime());
			if (!StringUtils.isNullOrEmpty(error)) {
				throw new BusinessException(error);
			}

			String userNameLogin = this.getUsername_login();
			
			SecUser sec = this.secUserService.findUserByLoginName(userNameLogin);

			News bean = this.adminNewsService.findById(news.getId());
			news.setCreateTime(bean.getCreateTime());

			this.adminNewsService.update(news);
			String log = MessageFormat.format(",新标题:{0},新语言:{1},新内容:{3}",
					news.getTitle(), news.getLang(), content_text);
			this.saveLog(sec, userNameLogin, log);

		} catch (BusinessException e) {
			modelAndView.addObject("error", e.getMessage());			
			modelAndView.addObject("news", news);
			modelAndView.setViewName("news_update");
			return modelAndView;
		} catch (Throwable t) {
			logger.error(" error ", t);
			modelAndView.addObject("error", "[ERROR] " + t.getMessage());
			modelAndView.addObject("news", news);
			modelAndView.setViewName("news_update");
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
			
			News news = this.adminNewsService.findById(id);
			
			String log = MessageFormat.format("ip:" + this.getIp() + ",管理员删除新闻,id:{0},原标题:{1},原语言:{2},原内容:{3}",
					news.getId(), news.getTitle(), news.getLang(),  news.getContent());
			this.saveLog(sec, userNameLogin, log);
			this.adminNewsService.delete(news);
			
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
		this.logService.saveSync(log);
	}

}
