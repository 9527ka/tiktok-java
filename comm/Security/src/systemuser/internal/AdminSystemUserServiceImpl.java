package systemuser.internal;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;
import org.springframework.security.providers.encoding.PasswordEncoder;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

import kernel.exception.BusinessException;
import kernel.util.StringUtils;
import kernel.web.Page;
import kernel.web.PagedQueryDao;
import project.Constants;
import project.ddos.CheckIpRequestCountService;
import project.log.Log;
import project.log.LogService;
import project.syspara.SysparaService;
import project.user.googleauth.GoogleAuthService;
import security.Role;
import security.RoleService;
import security.SecUser;
import security.internal.SecUserService;
import systemuser.AdminSystemUserService;

public class AdminSystemUserServiceImpl extends HibernateDaoSupport implements AdminSystemUserService {

	private SecUserService secUserService;
	private PagedQueryDao pagedQueryDao;
	private RoleService roleService;
	private PasswordEncoder passwordEncoder;
	protected LogService logService;
//	protected IdentifyingCodeService identifyingCodeService;
//	protected IdentifyingCodeTimeWindowService identifyingCodeTimeWindowService;

	private SysparaService sysparaService;
	@Autowired
	private GoogleAuthService googleAuthService;
	
	public void save(SecUser user,String operatorUsername,String loginSafeword,String code,String ip,String superGoogleAuthCode) {
//		checkEmailCode(code);
		checkGoogleAuthCode(superGoogleAuthCode);
		String enable = "";
		if(user.getEnabled()) {
			enable= "开启";
		}else {
			enable = "未开启";
		}
		checkLoginSafeword(operatorUsername,loginSafeword);
		saveLog(user,operatorUsername,"ip:"+ip+"管理员新增系统用户,角色为["+user.getId()+"],登录权限为["+enable+"],邮箱为["+user.getEmail()+"],验证码:["+code+"]");
		user.setSafeword(passwordEncoder.encodePassword(user.getSafeword(), user.getUsername()));
		secUserService.saveUser(user);
	}

	public void saveAllUser(SecUser user){
		user.setSafeword(passwordEncoder.encodePassword(user.getSafeword(), user.getUsername()));
		secUserService.saveUser(user);
	}

	public void saveAllUser(SecUser user,String operatorUsername,String loginSafeword,String code,String ip,String superGoogleAuthCode) {
//		checkEmailCode(code);
		checkGoogleAuthCode(superGoogleAuthCode);
		String enable = "";
		if(user.getEnabled()) {
			enable= "开启";
		}else {
			enable = "未开启";
		}
		checkLoginSafeword(operatorUsername,loginSafeword);
		saveLog(user,operatorUsername,"ip:"+ip+"管理员新增系统用户,角色为["+user.getId()+"],登录权限为["+enable+"],邮箱为["+user.getEmail()+"],验证码:["+code+"]");
		user.setSafeword(passwordEncoder.encodePassword(user.getSafeword(), user.getUsername()));
		secUserService.saveUser(user);
	}

	@Override
	public SecUser get(String id) {
		return secUserService.findUserById(id);
	}
	
	/**
	 * 验证登录人资金密码
	 * @param operatorUsername
	 * @param loginSafeword
	 */
	private void checkLoginSafeword(String operatorUsername,String loginSafeword) {
		SecUser sec = this.secUserService.findUserByLoginName(operatorUsername);
		String sysSafeword = sec.getSafeword();
		String safeword_md5 = passwordEncoder.encodePassword(loginSafeword, operatorUsername);
		if (!safeword_md5.equals(sysSafeword)) {
			throw new BusinessException("登录人资金密码错误");
		}		
	}
	
	/**
	 * 验证谷歌验证码
	 * @param code
	 */
	private void checkGoogleAuthCode(String code) {
		if(!this.googleAuthService.isEnableGoogleAuth()){
			// 关闭谷歌验证码
			return;
		}
		WebApplicationContext wac = ContextLoader.getCurrentWebApplicationContext();
		GoogleAuthService googleAuthService = (GoogleAuthService) wac.getBean("googleAuthService");
		
		String secret = sysparaService.find("super_google_auth_secret").getValue();
		boolean checkCode = googleAuthService.checkCode(secret, code);
		if(!checkCode) {
			throw new BusinessException("谷歌验证码错误");
		}
	}
	
	/**
	 * 验证管理员唯一邮箱
	 * @param code
	 */
	private void checkEmailCode(String code) {
//		String value = sysparaService.find("admin_verify_email").getValue();
//		String authCode = identifyingCodeTimeWindowService.getAuthCode(value);
//		if(StringUtils.isEmpty(authCode)||!authCode.equals(code)) {
//			throw new BusinessException("验证码错误");
//		}
//		identifyingCodeTimeWindowService.delAuthCode(value);
	}
	
	public void saveLog(SecUser secUser, String operator,String context) {
		Log log = new Log();
		log.setCategory(Constants.LOG_CATEGORY_OPERATION);
		log.setOperator(operator);
		log.setUsername(secUser.getUsername());
		log.setPartyId(secUser.getPartyId());
		log.setLog(context);
		log.setCreateTime(new Date());
		logService.saveSync(log);
	}
	
	/**
	 * 更新系统用户，有密码则更新密码,否则更新用户
	 * @param user
	 * @param newPassword
	 * @param type			密码类型，	password：登录密码，safe_password：资金密码
	 */
	public void update(SecUser user,String newPassword,String type,String operatorUsername,String loginSafeword,String code,String ip,String superGoogleAuthCode) {
		if(StringUtils.isEmpty(newPassword)) {
			checkLoginSafeword(operatorUsername,loginSafeword);
			SecUser userDB = this.get(user.getId());
			String enableDB = "";
			if(userDB.getEnabled()) {
				enableDB= "开启";
			}else {
				enableDB = "未开启";
			}
			String emailDB = user.getEmail();
			this.secUserService.update(user);
			String enable = "";
			if(user.getEnabled()) {
				enable= "开启";
			}else {
				enable = "未开启";
			}
			
			saveLog(user,operatorUsername,"ip:"+ip+"管理员修改系统用户,修改前角色为["+userDB.getRoles().toArray(new Role[0])[0].getRoleName()+"],登录权限["+enableDB+"],邮箱为["+emailDB+"],"
					+ "修改后角色为["+user.getRoles().toArray(new Role[0])[0].getRoleName()+"],登录权限["+enable+"],邮箱为["+user.getEmail()+"]");
		}else {
//			checkEmailCode(code);
			checkGoogleAuthCode(superGoogleAuthCode);
			checkLoginSafeword(operatorUsername,loginSafeword);
			switch(type) {
			case "password":this.secUserService.updatePassword(user.getUsername(), newPassword);
						saveLog(user,operatorUsername,"ip:"+ip+"管理员修改系统用户登录密码,验证码:["+code+"]");break;
			case "safe_password":this.secUserService.updateSafeword(user.getUsername(), newPassword);
						saveLog(user,operatorUsername,"ip:"+ip+"管理员修改系统用户资金密码,验证码:["+code+"]");break;
			default:break;
			}
		}
	}
	
	/**
	 * 删除系统用户
	 */
	public void delete(SecUser user, String operatorUsername, String loginSafeword, String ip, String superGoogleAuthCode) {

		this.checkGoogleAuthCode(superGoogleAuthCode);
		
		this.checkLoginSafeword(operatorUsername, loginSafeword);
		
		SecUser userDB = this.get(user.getId());
		String enableDB = "";
		if(userDB.getEnabled()) {
			enableDB= "开启";
		}else {
			enableDB = "未开启";
		}
		String emailDB = user.getEmail();
		
		this.secUserService.deleteUser(user);
		
		this.saveLog(user, operatorUsername, "ip:"+ip+"管理员删除系统用户,系统用户角色为["+userDB.getRoles().toArray(new Role[0])[0].getRoleName()+"],登录权限["+enableDB+"],邮箱为["+emailDB+"]");
	}	
	
	public Page pagedQuery(int pageNo,int pageSize ,String usernamePara) {
		StringBuffer queryString = new StringBuffer();
		queryString.append(" FROM SecUser ");
		queryString.append("WHERE 1=1 ");
		queryString.append("AND (partyId is null OR partyId='') ");
		Map<String,Object> parameters = new HashMap<String,Object>();
		queryString.append("AND id not in(:roles)  ");
		parameters.put("roles", new String[] {"SROOT"});
		
		if(StringUtils.isNotEmpty(usernamePara)) {
			queryString.append("AND username like:username ");
			parameters.put("username", "%"+usernamePara+"%");
		}
		queryString.append("ORDER BY createTime asc ");
		Page page = pagedQueryDao.pagedQueryHql(pageNo, pageSize, queryString.toString(), parameters);
		for(SecUser user:(List<SecUser>)page.getElements()) {
			user.setRoleName(((Role)(Arrays.asList(user.getRoles().toArray(new Role[0])).get(0))).getRoleName());
		}
		return page;
	}
	
	public Map<String,String> findRoleMap() {
		Map<String,String> map = new HashMap<>();
		List<Role> all = roleService.getAll();
		for(Role role:all) {
			if(
//				!Constants.SECURITY_ROLE_CUSTOMER.equals(role.getRoleName())&& //排除客服
					Constants.ROLE_MAP.containsKey(role.getRoleName()))
				continue;
			map.put(role.getRoleName(), Constants.ROLE_MAP.containsKey(role.getRoleName())?Constants.ROLE_MAP.get(role.getRoleName()):role.getRoleName());
		}
		return map;
	}
	
	public PagedQueryDao getPagedQueryDao() {
		return pagedQueryDao;
	}

	public void setPagedQueryDao(PagedQueryDao pagedQueryDao) {
		this.pagedQueryDao = pagedQueryDao;
	}

	public void setRoleService(RoleService roleService) {
		this.roleService = roleService;
	}
	public SecUserService getSecUserService() {
		return secUserService;
	}

	public void setSecUserService(SecUserService secUserService) {
		this.secUserService = secUserService;
	}

	public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
		this.passwordEncoder = passwordEncoder;
	}

	public void setLogService(LogService logService) {
		this.logService = logService;
	}

//	public void setIdentifyingCodeService(IdentifyingCodeService identifyingCodeService) {
//		this.identifyingCodeService = identifyingCodeService;
//	}

	public void setSysparaService(SysparaService sysparaService) {
		this.sysparaService = sysparaService;
	}

//	public void setIdentifyingCodeTimeWindowService(IdentifyingCodeTimeWindowService identifyingCodeTimeWindowService) {
//		this.identifyingCodeTimeWindowService = identifyingCodeTimeWindowService;
//	}

//	public void setGoogleAuthService(GoogleAuthService googleAuthService) {
//		this.googleAuthService = googleAuthService;
//	}
	
}
