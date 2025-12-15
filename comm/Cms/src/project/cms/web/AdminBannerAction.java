package project.cms.web;

import java.io.File;
import java.text.MessageFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.providers.encoding.PasswordEncoder;

import kernel.exception.BusinessException;
import kernel.util.ImageUtils;
import kernel.util.StringUtils;
import kernel.web.PageActionSupport;
import project.Constants;
import project.cms.AdminBannerService;
import project.cms.Banner;
import project.cms.BannerService;
import project.cms.PropertiesUtilCms;
import project.log.LogService;
import security.Role;
import security.SecUser;
import security.internal.SecUserService;

public class AdminBannerAction extends PageActionSupport {
	private static final Log logger = LogFactory.getLog(AdminBannerAction.class);
	private static final long serialVersionUID = 5666899648868642390L;
	private AdminBannerService adminBannerService;
	private BannerService bannerService;
	private SecUserService secUserService;
	private LogService logService;
	private PasswordEncoder passwordEncoder;
	private String id;
	/**
	 * 类型，top:顶部展示，other:其他地方展示
	 */
	private String model;
	/**
	 * 语言
	 */
	private String language;

	/**
	 * 业务代码
	 */
	private String content_code;
	/**
	 * 展示图片
	 */
	private String img;
	/**
	 * 访问路径
	 */
	private String url;
	/**
	 * 是否展示
	 */
	private int on_show;
	/**
	 * 排列顺序（数字相同按时间排，越小排越前）
	 */
	private int sort_index;

	/**
	 * 是否可以点击跳转
	 */
	private int click;
	/**
	 * 
	 */
	private String para_language;

	private String para_title;
	
	private File fileName;

	private Map<String, String> modelMap = Constants.BANNER_MODEL;
	private Map<String, String> languageMap = Constants.LANGUAGE;
	private String login_safeword;
	
	public String list() {
		this.pageSize = 30;
		this.page = this.adminBannerService.pagedQuery(this.pageNo, this.pageSize, this.para_language);
		for (Banner banner : (List<Banner>) this.page.getElements()) {
			banner.setLanguage(Constants.LANGUAGE.get(banner.getLanguage()));
			banner.setModel(Constants.BANNER_MODEL.get(banner.getModel()));
		}
		return "list";

	}

	public String toAdd() {
//		this.modelMap = this.adminBannerService.getModelDatasDictionary();
//		this.modelMap = Constants.CMS_MODEL;
//		this.languageMap = Constants.LANGUAGE;
		if (!checkIsRoot()) {
			this.error = "root 权限下才可添加";
			return list();
		}
//		if (!StringUtils.isNullOrEmpty(this.id)) {
//			Banner entity = this.adminBannerService.findById(this.id);
//			if (null == entity) {
//				this.error = "内容不存在或已删除";
//				return "add";
//			}
//			this.title = entity.getTitle();
//			this.content = entity.getContent();
//			this.model = entity.getModel();
//			this.language = entity.getLanguage();
//			this.content_code = entity.getContent_code();
//		} else {
		language = PropertiesUtilCms.getProperty("system_cms_language");
//		}
		return "add";
	}

	public String add() {
		try {
			this.error = verif();
			if (!StringUtils.isNullOrEmpty(this.error)) {
				return "add";
			}
			if (!checkIsRoot()) {
				throw new BusinessException("root 权限下才可添加");
			}
			SecUser sec = this.secUserService.findUserByLoginName(this.getUsername_login());
			checkLoginSafeword(sec, this.getUsername_login(), this.login_safeword);
			Banner entity = new Banner();
//			if (!StringUtils.isNullOrEmpty(this.id)) {
//				entity = this.adminBannerService.findById(this.id);
//
//			} else {
//				entity.setCreateTime(new Date());
//			}
			if (StringUtils.isEmptyString(this.model))
				throw new BusinessException("请选择模块");
			if (StringUtils.isEmptyString(this.language))
				throw new BusinessException("请选择语言");
			if (StringUtils.isEmptyString(this.content_code))
				throw new BusinessException("请填写业务码");
			entity.setCreateTime(new Date());
			entity.setModel(this.model);
			entity.setLanguage(this.language);
			entity.setContent_code(this.content_code);
			entity.setUrl(url);
			entity.setClick(click);
			entity.setOn_show(on_show);
			entity.setSort_index(sort_index);
			entity.setImage(ImageUtils.reduceImg(fileName, 1f));
//			ImageUtils.
			this.bannerService.save(entity);
			String log = MessageFormat.format(
					"ip:" + this.getIp() + ",管理员新增banner，id:{0},路径:{1},语言:{2},模块:{3},业务代码:{4},索引:{5},可否点击:{6},是否展示:{7}", entity.getId(),
					entity.getUrl(), entity.getLanguage(), entity.getModel(), entity.getContent_code(),
					entity.getSort_index(),entity.getClick(),entity.getOn_show());
			saveLog(sec, this.username_login, log);
			this.message = "操作成功";
		} catch (BusinessException e) {
			this.error = e.getMessage();
			return toAdd();
		} catch (Throwable t) {
			logger.error(" error ", t);
			this.error = ("[ERROR] 服务器错误");
			return toAdd();
		}
		return list();
	}

	public String toUpdate() {
		if (!StringUtils.isNullOrEmpty(this.id)) {
			Banner entity = this.bannerService.cacheById(this.id);
			if (null == entity) {
				this.error = "内容不存在或已删除";
				return list();
			}
//			this.title = entity.getTitle();
//			this.content = entity.getContent();
			this.img = entity.getImage();
			this.on_show = entity.getOn_show();
			this.click = entity.getClick();
			this.sort_index = entity.getSort_index();
			if (this.checkIsRoot()) {
				this.model = entity.getModel();
				this.language = entity.getLanguage();
				this.content_code = entity.getContent_code();
				this.url = entity.getUrl();
			}
		} else {
			this.error = "内容不存在或已删除";
			return list();
		}
		return "update";
	}

	public String update() {
		try {
			this.error = verif();
			if (!StringUtils.isNullOrEmpty(this.error)) {
				return "update";
			}
			SecUser sec = this.secUserService.findUserByLoginName(this.getUsername_login());
			checkLoginSafeword(sec, this.getUsername_login(), this.login_safeword);

			Banner entity = new Banner();
			if (!StringUtils.isNullOrEmpty(this.id)) {
				entity = this.bannerService.cacheById(this.id);
				if (null == entity)
					throw new BusinessException("内容不存在或已删除");
			} else {
				throw new BusinessException("内容不存在或已删除");
			}
			String log = MessageFormat.format(
					"ip:" + this.getIp() + ",管理员修改banner，id:{0},原路径:{1},原语言:{2},原模块:{3},原业务代码:{4},原索引:{5},原可否点击:{6},原是否展示:{7}",
					entity.getId(), entity.getUrl(), entity.getLanguage(), entity.getModel(), entity.getContent_code(),
					entity.getSort_index(),entity.getClick(),entity.getOn_show());
//			entity.setTitle(this.title);
//			entity.setContent(this.content);
			entity.setClick(click);
			entity.setOn_show(on_show);
			entity.setSort_index(sort_index);
			if(fileName!=null) {
				entity.setImage(ImageUtils.reduceImg(fileName, 1f));
			}
			if (this.checkIsRoot()) {
				if (StringUtils.isEmptyString(this.model))
					throw new BusinessException("请选择模块");
				if (StringUtils.isEmptyString(this.language))
					throw new BusinessException("请选择语言");
				if (StringUtils.isEmptyString(this.content_code))
					throw new BusinessException("请填写业务码");
				entity.setModel(this.model);
				entity.setLanguage(this.language);
				entity.setContent_code(this.content_code);
				entity.setUrl(url);
			}
			this.bannerService.update(entity);
			log += MessageFormat.format(",新路径:{0},新语言:{1},新模块:{2},新业务代码:{3},新索引:{4},新可否点击:{5},新是否展示:{6}", entity.getUrl(),
					entity.getLanguage(), entity.getModel(), entity.getContent_code(), entity.getSort_index(),entity.getClick(),entity.getOn_show());
			saveLog(sec, this.username_login, log);
			this.message = "操作成功";
		} catch (BusinessException e) {
			this.error = e.getMessage();
			return toUpdate();
		} catch (Throwable t) {
			logger.error(" error ", t);
			this.error = ("[ERROR] 服务器错误");
			return toUpdate();
		}
		return list();
	}

	private boolean checkIsRoot() {
		String username = this.getUsername_login();
		// root才可以改动
		SecUser secUser = secUserService.findUserByLoginName(username);
		for (Role role : secUser.getRoles()) {
			if (Constants.SECURITY_ROLE_ROOT.equals(role.getRoleName())) {
				return Boolean.TRUE;
			}
		}
		return Boolean.FALSE;
	}
	public String delete() {
		try {
			if (!checkIsRoot()) {
				throw new BusinessException("权限不足");
			}
			if (StringUtils.isNullOrEmpty(this.id)) {
				throw new BusinessException("请传入id");
			}
			SecUser sec = this.secUserService.findUserByLoginName(this.getUsername_login());
			checkLoginSafeword(sec, this.getUsername_login(), this.login_safeword);
			Banner entity = this.bannerService.cacheById(this.id);
			if (null == entity) {
				throw new BusinessException("内容不存在或已删除");
			}
			this.bannerService.delete(this.id);
			String log = MessageFormat.format(
					"ip:" + this.getIp() + ",管理员删除banner，id:{0},路径:{1},语言:{2},模块:{3},业务代码:{4},索引:{5},可否点击:{6},是否展示:{7}", entity.getId(),
					entity.getUrl(), entity.getLanguage(), entity.getModel(), entity.getContent_code(),
					entity.getSort_index(),entity.getClick(),entity.getOn_show());
			saveLog(sec, this.username_login, log);
			this.message = "操作成功";
		} catch (BusinessException e) {
			this.error = e.getMessage();
		} catch (Throwable t) {
			logger.error(" error ", t);
			this.error = ("[ERROR] 服务器错误");
		}
		return list();
	}

	public String verif() {
		if (StringUtils.isNullOrEmpty(this.img)) {
			return "请上传图片！";
		}
//		if (this.index < 0) {
//			return "请输入正确的排序索引！";
//		}
		return "";
	}

	/**
	 * 验证登录人资金密码
	 * 
	 * @param operatorUsername
	 * @param loginSafeword
	 */
	protected void checkLoginSafeword(SecUser secUser, String operatorUsername, String loginSafeword) {
//		SecUser sec = this.secUserService.findUserByLoginName(operatorUsername);
		String sysSafeword = secUser.getSafeword();
		String safeword_md5 = passwordEncoder.encodePassword(loginSafeword, operatorUsername);
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

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setAdminBannerService(AdminBannerService adminBannerService) {
		this.adminBannerService = adminBannerService;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getModel() {
		return model;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public Map<String, String> getModelMap() {
		return modelMap;
	}

	public void setModelMap(Map<String, String> modelMap) {
		this.modelMap = modelMap;
	}

	public String getPara_language() {
		return para_language;
	}

	public void setPara_language(String para_language) {
		this.para_language = para_language;
	}

	public String getPara_title() {
		return para_title;
	}

	public void setPara_title(String para_title) {
		this.para_title = para_title;
	}

	public Map<String, String> getLanguageMap() {
		return languageMap;
	}

	public void setLanguageMap(Map<String, String> languageMap) {
		this.languageMap = languageMap;
	}

	public String getContent_code() {
		return content_code;
	}

	public void setContent_code(String content_code) {
		this.content_code = content_code;
	}

	public void setSecUserService(SecUserService secUserService) {
		this.secUserService = secUserService;
	}

	public void setLogService(LogService logService) {
		this.logService = logService;
	}

	public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
		this.passwordEncoder = passwordEncoder;
	}

	public void setLogin_safeword(String login_safeword) {
		this.login_safeword = login_safeword;
	}

	public String getUrl() {
		return url;
	}

	public int getClick() {
		return click;
	}

	public void setUrl(String url) {
		this.url = url;
	}


	public void setClick(int click) {
		this.click = click;
	}

	public void setBannerService(BannerService bannerService) {
		this.bannerService = bannerService;
	}

	public void setFileName(File fileName) {
		this.fileName = fileName;
	}

	public String getImg() {
		return img;
	}

	public void setImg(String img) {
		this.img = img;
	}

	public int getOn_show() {
		return on_show;
	}

	public void setOn_show(int on_show) {
		this.on_show = on_show;
	}

	public int getSort_index() {
		return sort_index;
	}

	public void setSort_index(int sort_index) {
		this.sort_index = sort_index;
	}

//	public void setFile(File file) {
//		this.file = file;
//	}
	
	
}
