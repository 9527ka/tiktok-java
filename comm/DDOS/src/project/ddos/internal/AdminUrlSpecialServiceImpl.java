package project.ddos.internal;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.orm.hibernate5.support.HibernateDaoSupport;
import org.springframework.security.providers.encoding.PasswordEncoder;

import kernel.exception.BusinessException;
import kernel.util.StringUtils;
import kernel.web.Page;
import kernel.web.PagedQueryDao;
import project.Constants;
import project.ddos.AdminUrlSpecialService;
import project.ddos.UrlSpecialService;
import project.ddos.model.UrlSpecial;
import project.log.Log;
import project.log.LogService;
import project.syspara.SysparaService;
import security.SecUser;
import security.internal.SecUserService;

public class AdminUrlSpecialServiceImpl extends HibernateDaoSupport implements AdminUrlSpecialService {

	private PagedQueryDao pagedQueryDao;
	private SecUserService secUserService;
	private LogService logService;
	private SysparaService sysparaService;
	private PasswordEncoder passwordEncoder;
	private UrlSpecialService urlSpecialService;

	@Override
	public Page pagedQuery(int pageNo, int pageSize, String url_para) {
		StringBuffer queryString = new StringBuffer(" SELECT urls.UUID id,urls.URL url,urls.REMARKS remarks ");
		queryString.append(" FROM T_URL_SPECIAL urls WHERE 1 = 1 ");
		Map<String, Object> parameters = new HashMap<>();
		if (!StringUtils.isNullOrEmpty(url_para)) {
			queryString.append(" and  urls.URL like:url_para ");
			parameters.put("url_para", "%" + url_para + "%");
		}
		Page page = this.pagedQueryDao.pagedQuerySQL(pageNo, pageSize, queryString.toString(), parameters);
		return page;
	}

	@Override
	public void save(UrlSpecial entity, String operatorUsername, String loginSafeword, String ip) {
		if (urlSpecialService.cacheAllUrls().contains(entity.getUrl())) {
			throw new BusinessException("url已经存在");
		}
		checkLoginSafeword(operatorUsername, loginSafeword);
		SecUser user = this.secUserService.findUserByLoginName(operatorUsername);
		saveLog(user, operatorUsername,
				"ip:" + ip + "管理员新增特殊Url,url为[" + entity.getUrl() + "],备注为[" + entity.getRemarks() + "]");
		urlSpecialService.save(entity);
	}

	@Override
	public void update(UrlSpecial entity, String operatorUsername, String loginSafeword, String ip) {
		UrlSpecial urlSpecial = urlSpecialService.cacheById(entity.getId().toString());
		if (urlSpecial == null) {
			throw new BusinessException("url不存在");
		}
		if (!urlSpecial.getUrl().equals(entity.getUrl())
				&& urlSpecialService.cacheAllUrls().contains(entity.getUrl())) {
			throw new BusinessException("修改后的url已存在");
		}
		checkLoginSafeword(operatorUsername, loginSafeword);
		SecUser user = this.secUserService.findUserByLoginName(operatorUsername);
		saveLog(user, operatorUsername, "ip:" + ip + "管理员新增特殊Url,原url为[" + urlSpecial.getUrl() + "],原备注为["
				+ urlSpecial.getRemarks() + "],新url为[" + entity.getUrl() + "],新备注为[" + entity.getRemarks() + "]");
		urlSpecial.setUrl(entity.getUrl());
		urlSpecial.setRemarks(entity.getRemarks());
		urlSpecialService.update(urlSpecial);
	}

	@Override
	public void delete(String id, String operatorUsername, String loginSafeword, String ip) {
		UrlSpecial urlSpecial = urlSpecialService.cacheById(id);
		if (urlSpecial == null) {
			throw new BusinessException("url不存在");
		}
		checkLoginSafeword(operatorUsername, loginSafeword);
		SecUser user = this.secUserService.findUserByLoginName(operatorUsername);
		saveLog(user, operatorUsername,
				"ip:" + ip + "管理员删除特殊Url,url为[" + urlSpecial.getUrl() + "],备注为[" + urlSpecial.getRemarks() + "]");

		this.urlSpecialService.delete(urlSpecial);
	}

	/**
	 * 验证登录人资金密码
	 * 
	 * @param operatorUsername
	 * @param loginSafeword
	 */
	private void checkLoginSafeword(String operatorUsername, String loginSafeword) {
		SecUser sec = this.secUserService.findUserByLoginName(operatorUsername);
		String sysSafeword = sec.getSafeword();
		String safeword_md5 = passwordEncoder.encodePassword(loginSafeword, operatorUsername);
		if (!safeword_md5.equals(sysSafeword)) {
			throw new BusinessException("登录人资金密码错误");
		}

	}

	public void saveLog(SecUser secUser, String operator, String context) {
		Log log = new Log();
		log.setCategory(Constants.LOG_CATEGORY_OPERATION);
		log.setOperator(operator);
		log.setUsername(secUser.getUsername());
		log.setPartyId(secUser.getPartyId());
		log.setLog(context);
		log.setCreateTime(new Date());
		logService.saveSync(log);
	}

	public void setPagedQueryDao(PagedQueryDao pagedQueryDao) {
		this.pagedQueryDao = pagedQueryDao;
	}

	public void setSecUserService(SecUserService secUserService) {
		this.secUserService = secUserService;
	}

	public void setLogService(LogService logService) {
		this.logService = logService;
	}

	public void setSysparaService(SysparaService sysparaService) {
		this.sysparaService = sysparaService;
	}

	public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
		this.passwordEncoder = passwordEncoder;
	}

	public void setUrlSpecialService(UrlSpecialService urlSpecialService) {
		this.urlSpecialService = urlSpecialService;
	}

}
