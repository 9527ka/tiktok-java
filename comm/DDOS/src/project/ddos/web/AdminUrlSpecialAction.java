package project.ddos.web;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import kernel.exception.BusinessException;
import kernel.util.StringUtils;
import kernel.web.PageActionSupport;
import project.ddos.AdminUrlSpecialService;
import project.ddos.UrlSpecialService;
import project.ddos.model.UrlSpecial;

public class AdminUrlSpecialAction extends PageActionSupport {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3567372733940832371L;
	private static final Log logger = LogFactory.getLog(AdminUrlSpecialAction.class);
	private AdminUrlSpecialService adminUrlSpecialService;
	private UrlSpecialService urlSpecialService;

	private String url_para;

	private String id;
	private String url;
	private String remarks;
	/**
	 * 登录人资金密码
	 */
	private String login_safeword;

	public String list() {
		this.pageSize = 30;
		this.page = this.adminUrlSpecialService.pagedQuery(this.pageNo, this.pageSize, this.url_para);
		return "list";
	}

	public String toAdd() {
		return "add";
	}

	private void check() {
		String loginUserName = this.getUsername_login();
		if (!("root".equals(loginUserName))) {
			throw new BusinessException("权限不足，无法操作");
		}
	}

	private String verif_add() {

		if (StringUtils.isEmptyString(this.url)) {
			return "请输入[URL]";
		}

//		if (StringUtils.isEmptyString(this.menu_type)) {
//			return "请选择[名单]";
//		}

		return null;
	}

	public String add() {
		try {
			check();
			this.error = verif_add();
			if (!StringUtils.isNullOrEmpty(this.error)) {
				return toAdd();
			}
			if (StringUtils.isNullOrEmpty(this.login_safeword)) {
				this.error = "请输入登录人资金密码";
				return toAdd();
			}
			UrlSpecial urlSpecial = new UrlSpecial();
			urlSpecial.setCreate_time(new Date());
			urlSpecial.setUrl(url);
			urlSpecial.setRemarks(remarks);

			/**
			 * 补充设值
			 */
			this.adminUrlSpecialService.save(urlSpecial, this.getUsername_login(), login_safeword, this.getIp());
			this.message = "操作成功";

		} catch (BusinessException e) {
			this.error = e.getMessage();
			return toAdd();
		} catch (Throwable t) {
			logger.error(" error ", t);
			this.error = ("[ERROR] " + t.getMessage());
			return toAdd();
		}
		return list();
	}

	public String update() {
		try {
			check();
			this.error = verif_add();
			if (!StringUtils.isNullOrEmpty(this.error)) {
				return toUpdate();
			}
			UrlSpecial urlSpecial = new UrlSpecial();
			urlSpecial.setCreate_time(new Date());
			urlSpecial.setId(id);
			urlSpecial.setUrl(url);
			urlSpecial.setRemarks(remarks);

			this.adminUrlSpecialService.update(urlSpecial, this.getUsername_login(), login_safeword, this.getIp());
			this.message = "操作成功";
		} catch (BusinessException e) {
			this.error = e.getMessage();
			return toUpdate();
		} catch (Throwable t) {
			logger.error(" error ", t);
			this.error = ("[ERROR] " + t.getMessage());
			return toUpdate();
		}
		return list();
	}

	public String toUpdate() {
		check();
		UrlSpecial special = urlSpecialService.cacheById(id);
		url = special.getUrl();
		remarks = special.getRemarks();
		return "update";
	}

	public String toDelete() {
		try {
			check();
			this.adminUrlSpecialService.delete(id, this.getUsername_login(), login_safeword, this.getIp());
			this.message = "操作成功";
			return list();
		} catch (BusinessException e) {
			this.error = e.getMessage();
			return list();
		} catch (Throwable t) {
			logger.error("update error ", t);
			this.error = "程序错误";
			return list();
		}
	}

	public void setLogin_safeword(String login_safeword) {
		this.login_safeword = login_safeword;
	}

	public String getUrl_para() {
		return url_para;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setAdminUrlSpecialService(AdminUrlSpecialService adminUrlSpecialService) {
		this.adminUrlSpecialService = adminUrlSpecialService;
	}

	public void setUrl_para(String url_para) {
		this.url_para = url_para;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getUrl() {
		return url;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public void setUrlSpecialService(UrlSpecialService urlSpecialService) {
		this.urlSpecialService = urlSpecialService;
	}

}
