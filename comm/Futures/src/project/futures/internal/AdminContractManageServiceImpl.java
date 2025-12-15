package project.futures.internal;

import java.text.MessageFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;
import org.springframework.security.providers.encoding.PasswordEncoder;

import kernel.exception.BusinessException;
import kernel.util.StringUtils;
import kernel.web.PagedQueryDao;
import project.Constants;
import project.futures.AdminContractManageService;
import project.futures.AdminFuturesParaService;
import project.futures.FuturesPara;
import project.item.AdminItemService;
import project.item.model.Item;
import project.log.LogService;
import project.syspara.SysparaService;
import project.user.googleauth.GoogleAuthService;
import security.SecUser;
import security.internal.SecUserService;

public class AdminContractManageServiceImpl extends HibernateDaoSupport implements AdminContractManageService {
	private Logger log = LoggerFactory.getLogger(getClass());
	private PagedQueryDao pagedQueryDao;
	private JdbcTemplate jdbcTemplate;
	private AdminItemService adminItemService;
	private AdminFuturesParaService adminFuturesParaService;
	private SecUserService secUserService;
	private LogService logService;
	private PasswordEncoder passwordEncoder;
	private SysparaService sysparaService;
	protected GoogleAuthService googleAuthService;

	public Map<String, String> getFuturesSymbols() {
		List<String> list = this.adminItemService.getSymbols();
		Map<String,String> result = new HashMap<String,String>();
		for(String str:list) {
			result.put(str, str);
		}
		return result;
	}

	public String addContractItem(Item entity) {
		if (entity.getId()!=null&&StringUtils.isNotEmpty(entity.getId().toString())) {
			Item item = adminItemService.get(entity.getId().toString());
			if (null == item) {
				log.info("item is null ,id:{}", entity.getId().toString());
				return "合约产品不存在";
			}
			if (null != entity.getDecimals())
				item.setDecimals(entity.getDecimals());
			item.setName(entity.getName());
			this.adminItemService.update(item);
			return "";
		}
		if (this.adminItemService.checkSymbolExit(entity.getSymbol()))
			return "合约代码已经存在";
		entity.setMarket(Item.DELIVERY_CONTRACT);// 合约
		this.adminItemService.save(entity);			
		return "";
	}

	public String addFutures(FuturesPara entity,String ip,String operaUsername,String loginSafeword) {
		SecUser sec = this.secUserService.findUserByLoginName(operaUsername);
		checkLoginSafeword(sec,operaUsername,loginSafeword);
		String logContent = "ip:"+ip;
		if (entity.getId()!=null && StringUtils.isNotEmpty(entity.getId().toString())) {
			FuturesPara futuresById = this.adminFuturesParaService.getById(entity.getId());
			if (null == futuresById) {
				log.info("futures is null ,id:{}", entity.getId());
				return "合约参数不存在";
			}
			logContent += MessageFormat.format(",管理员修改交割参数，币种:{0},原时间长度:{1},原时间单位:{2},原最低购买金额:{3},原手续费:{4},原浮动最小收益率:{5},原浮动最大收益率:{6}", 
					futuresById.getSymbol(),futuresById.getTimeNum(),futuresById.getTimeUnit(),futuresById.getUnit_amount(),futuresById.getUnit_fee(),futuresById.getProfit_ratio(),futuresById.getProfit_ratio_max());
			BeanUtils.copyProperties(entity, futuresById);// 是否做用户控制
			this.adminFuturesParaService.update(futuresById);
			logContent += MessageFormat.format(",新时间长度:{0},新时间单位:{1},新最低购买金额:{2},新手续费:{3},新浮动最小收益率:{4},新浮动最大收益率:{5}", 
					futuresById.getTimeNum(),futuresById.getTimeUnit(),futuresById.getUnit_amount(),futuresById.getUnit_fee(),futuresById.getProfit_ratio(),futuresById.getProfit_ratio_max());
			saveLog(sec,operaUsername,logContent);
			return "";
		}
		logContent += MessageFormat.format(",管理员新增交割参数，币种:{0},时间长度:{1},时间单位:{2},最低购买金额:{3},手续费:{4},浮动最小收益率:{5},浮动最大收益率:{6}", 
				entity.getSymbol(),entity.getTimeNum(),entity.getTimeUnit(),entity.getUnit_amount(),entity.getUnit_fee(),entity.getProfit_ratio(),entity.getProfit_ratio_max());
		this.adminFuturesParaService.add(entity);
		saveLog(sec,operaUsername,logContent);
		return "";
	}

	public void deleteFuturesPara(String id,String ip,String operaUsername,String loginSafeword,String superGoogleAuthCode) {
		checkGoogleAuthCode(superGoogleAuthCode);
		SecUser sec = this.secUserService.findUserByLoginName(operaUsername);
		checkLoginSafeword(sec,operaUsername,loginSafeword);
		
		FuturesPara entity = this.adminFuturesParaService.getById(id);
		if(null == entity ) {
			throw new BusinessException("交易参数不存在");
		}
		String logContent = "ip:"+ip;
		logContent += MessageFormat.format(",管理员删除交割参数，币种:{0},时间长度:{1},时间单位:{2},最低购买金额:{3},手续费:{4},浮动最小收益率:{5},浮动最大收益率:{6}", 
				entity.getSymbol(),entity.getTimeNum(),entity.getTimeUnit(),entity.getUnit_amount(),entity.getUnit_fee(),entity.getProfit_ratio(),entity.getProfit_ratio_max());
		this.adminFuturesParaService.delete(entity);
		saveLog(sec,operaUsername,logContent);
	}
	/**
	 * 验证谷歌验证码
	 * @param code
	 */
	private void checkGoogleAuthCode(String code) {
		String secret = sysparaService.find("super_google_auth_secret").getValue();
		boolean checkCode = googleAuthService.checkCode(secret, code);
		if(!checkCode) {
			throw new BusinessException("谷歌验证码错误");
		}
	}
	/**
	 * 验证登录人资金密码
	 * @param operatorUsername
	 * @param loginSafeword
	 */
	protected void checkLoginSafeword(SecUser secUser,String operatorUsername,String loginSafeword) {
//		SecUser sec = this.secUserService.findUserByLoginName(operatorUsername);
		String sysSafeword = secUser.getSafeword();
		String safeword_md5 = passwordEncoder.encodePassword(loginSafeword, operatorUsername);
		if (!safeword_md5.equals(sysSafeword)) {
			throw new BusinessException("登录人资金密码错误");
		}
		
	}
	public void saveLog(SecUser secUser, String operator,String context) {
		project.log.Log log = new project.log.Log();
		log.setCategory(Constants.LOG_CATEGORY_OPERATION);
		log.setOperator(operator);
		log.setUsername(secUser.getUsername());
		log.setPartyId(secUser.getPartyId());
		log.setLog(context);
		log.setCreateTime(new Date());
		logService.saveSync(log);
	}	
	public PagedQueryDao getPagedQueryDao() {
		return pagedQueryDao;
	}

	public void setPagedQueryDao(PagedQueryDao pagedQueryDao) {
		this.pagedQueryDao = pagedQueryDao;
	}

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public AdminItemService getAdminItemService() {
		return adminItemService;
	}

	public void setAdminItemService(AdminItemService adminItemService) {
		this.adminItemService = adminItemService;
	}
	
	public void setAdminFuturesParaService(AdminFuturesParaService adminFuturesParaService) {
		this.adminFuturesParaService = adminFuturesParaService;
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

	public void setSysparaService(SysparaService sysparaService) {
		this.sysparaService = sysparaService;
	}

	public void setGoogleAuthService(GoogleAuthService googleAuthService) {
		this.googleAuthService = googleAuthService;
	}
	
	
}
