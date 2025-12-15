package project.finance.web;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.providers.encoding.PasswordEncoder;

import kernel.exception.BusinessException;
import kernel.util.StringUtils;
import kernel.web.PageActionSupport;
import project.Constants;
import project.finance.AdminFinanceService;
import project.finance.Finance;
import project.finance.FinanceService;
import project.log.LogService;
import project.party.PartyService;
import security.SecUser;
import security.internal.SecUserService;

public class AdminFinanceAction extends PageActionSupport {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3791060223523658395L;

	private static Log logger = LogFactory.getLog(AdminFinanceAction.class);

	protected String name_para;

	protected AdminFinanceService adminFinanceService;

	protected FinanceService financeService;

	protected PartyService partyService;
	
	protected LogService logService;
	protected SecUserService secUserService;
	protected PasswordEncoder passwordEncoder;
	
	
	
	/**
	 * 登录人资金密码
	 */
	protected String login_safeword;

	protected String id;

	protected String name;
	protected String name_en;
	protected String name_cn;
	protected String name_kn;
	protected String name_jn;

	protected String img;

	protected int cycle;

	protected double daily_rate;
	protected double daily_rate_max;

	protected double today_rate;

	protected double default_ratio;

	protected double investment_min;
	protected double investment_max;


	protected String state;

	public String list() {
		this.pageSize = 300;
		this.page = this.adminFinanceService.pagedQuery(this.pageNo, this.pageSize, this.name_para);
		return "list";
	}

	public String toAdd() {
		return "add";
	}

	protected String verification() {
		if (StringUtils.isEmptyString(this.name))
			return "请输入产品名称";
		if (StringUtils.isEmptyString(this.img))
			return "请上传产品图片";
		if (this.cycle <= 0) {
			return "周期不能小于等于0天";
		}
		if (this.daily_rate < 0.0D) {
			return "日利率不能小于0";
		}
		if (this.daily_rate_max < 0.0D) {
			return "日利率不能小于0";
		}
		if (this.today_rate < 0.0D) {
			return "今日利率不能小于0";
		}
		if (this.default_ratio < 0.0D) {
			return "违约结算比例不能小于0";
		}
		if (this.investment_min < 0.0D) {
			return "投资金额区间不能小于0";
		}
		if (this.investment_max <= investment_min) {
			return "投资金额区间错误";
		}

		return null;
	}

	public String add() {
		try {
			this.error = verification();
			if (!StringUtils.isNullOrEmpty(this.error))
				return toAdd();
			
			if (StringUtils.isNullOrEmpty(this.login_safeword)) {
				this.error = "请输入登录人资金密码";
				return toAdd();
			}
//      if (this.financeService.findByName(this.name) != null) {
//    	  this.error ="产品名称已存在";
//    	  return toAdd(); 
//		}
			Finance finance = new Finance();
			finance.setName(this.name);
			finance.setName_en(this.name_en);
			finance.setName_cn(this.name_cn);
			finance.setName_kn(this.name_kn);
			finance.setName_jn(this.name_jn);
			finance.setImg(this.img);
			finance.setCycle(this.cycle);
			finance.setDaily_rate(this.daily_rate);
			finance.setDaily_rate_max(this.daily_rate_max);
			finance.setToday_rate(this.today_rate);
			finance.setDefault_ratio(this.default_ratio);
			finance.setInvestment_min(this.investment_min);
			finance.setInvestment_max(this.investment_max);
			finance.setState(this.state);

			this.financeService.save(finance,this.login_safeword,this.getUsername_login());
			this.message = "操作成功";
			
			project.log.Log log = new project.log.Log();
			log.setCategory(Constants.LOG_CATEGORY_OPERATION);
			log.setUsername(this.getUsername_login());
			log.setOperator(this.getUsername_login());
			log.setLog("管理员手动添加理财产品,ip:["+this.getIp(getRequest())+"]");
			logService.saveSync(log);
			
		} catch (BusinessException e) {
			this.error = e.getMessage();
			return toAdd();
		} catch (Throwable t) {
			logger.error("UserAction.register error ", t);
			this.error = "[ERROR] " + t.getMessage();
			return toAdd();
		}
		return list();
	}

	protected String verificationUpdate() {
		if (StringUtils.isEmptyString(this.name))
			return "请输入产品名称";
		if (StringUtils.isEmptyString(this.img))
			return "请上传产品图片";
		if (this.cycle <= 0) {
			return "周期不能小于等于0天";
		}
		if (this.daily_rate < 0.0D) {
			return "日利率不能小于0";
		}
		if (this.daily_rate_max < 0.0D) {
			return "日利率不能小于0";
		}
		if (this.today_rate < 0.0D) {
			return "今日利率不能小于0";
		}
		if (this.default_ratio < 0.0D) {
			return "违约结算比例不能小于0";
		}
		if (this.investment_min < 0.0D) {
			return "投资金额区间不能小于0";
		}
		if (this.investment_max <= investment_min) {
			return "投资金额区间错误";
		}

		return null;
	}

	public String toUpdate() {
		Finance finance = this.financeService.findById(this.id);
		this.name = finance.getName();
		this.name_en = finance.getName_en();
		this.name_cn = finance.getName_cn();
		this.name_kn = finance.getName_kn();
		this.name_jn = finance.getName_jn();
		this.img = finance.getImg();
		this.cycle = finance.getCycle();
		this.daily_rate = finance.getDaily_rate();
		this.daily_rate_max = finance.getDaily_rate_max();
		this.today_rate = finance.getToday_rate();
		this.default_ratio = finance.getDefault_ratio();
		this.investment_min = finance.getInvestment_min();
		this.investment_max = finance.getInvestment_max();
		this.state = finance.getState();

		return "update";
	}

	public String update() {

		Finance finance = this.financeService.findById(this.id);
		try {
			this.error = verificationUpdate();
			if (!StringUtils.isNullOrEmpty(this.error))
				return toUpdate();
			
			if (StringUtils.isNullOrEmpty(this.login_safeword)) {
				this.error = "请输入登录人资金密码";
				return toUpdate();
			}
//		      if (this.financeService.findByName(this.name) != null) {
//		    	  this.error ="产品名称已存在";
//		    	  return toAdd(); 
//				}
			finance.setName(this.name);
			finance.setName_en(this.name_en);
			finance.setName_cn(this.name_cn);
			finance.setName_jn(this.name_jn);
			finance.setName_kn(this.name_kn);
			finance.setImg(this.img);
			finance.setCycle(this.cycle);
			finance.setDaily_rate(this.daily_rate);
			finance.setDaily_rate_max(this.daily_rate_max);
			finance.setToday_rate(this.today_rate);
			finance.setDefault_ratio(this.default_ratio);
			finance.setInvestment_min(this.investment_min);
			finance.setInvestment_max(this.investment_max);
			finance.setState(this.state);
			this.financeService.update(finance,this.login_safeword,this.getUsername_login());
			this.message = "操作成功";
			
			project.log.Log log = new project.log.Log();
			log.setCategory(Constants.LOG_CATEGORY_OPERATION);
			log.setUsername(this.getUsername_login());
			log.setOperator(this.getUsername_login());
			log.setLog("管理员手动修改里财产品配置,ip:["+this.getIp(getRequest())+"]");
			logService.saveSync(log);
			return list();
		} catch (BusinessException e) {
			this.error = e.getMessage();
			return "update";
		} catch (Throwable t) {
			logger.error("update error ", t);
			this.error = "程序错误";
			return "update";
		}
	}

	public String toDelete() {
		try {
			if (StringUtils.isNullOrEmpty(this.login_safeword)) {
				this.error = "请输入登录人资金密码";
				return list();
			}
			this.financeService.delete(this.id,this.login_safeword,this.getUsername_login());
			this.message = "操作成功";
			
			project.log.Log log = new project.log.Log();
			log.setCategory(Constants.LOG_CATEGORY_OPERATION);
			log.setUsername(this.getUsername_login());
			log.setOperator(this.getUsername_login());
			log.setLog("管理员手动删除理财产品,ip:["+this.getIp(getRequest())+"]");
			logService.saveSync(log);
			
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

	public String getName_para() {
		return this.name_para;
	}

	public void setName_para(String name_para) {
		this.name_para = name_para;
	}

	public void setAdminFinanceService(AdminFinanceService adminFinanceService) {
		this.adminFinanceService = adminFinanceService;
	}

	public void setFinanceService(FinanceService financeService) {
		this.financeService = financeService;
	}

	public void setPartyService(PartyService partyService) {
		this.partyService = partyService;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getImg() {
		return img;
	}

	public void setImg(String img) {
		this.img = img;
	}

	public int getCycle() {
		return cycle;
	}

	public void setCycle(int cycle) {
		this.cycle = cycle;
	}

	public double getDaily_rate() {
		return daily_rate;
	}

	public void setDaily_rate(double daily_rate) {
		this.daily_rate = daily_rate;
	}

	public double getToday_rate() {
		return today_rate;
	}

	public void setToday_rate(double today_rate) {
		this.today_rate = today_rate;
	}

	public double getDefault_ratio() {
		return default_ratio;
	}

	public void setDefault_ratio(double default_ratio) {
		this.default_ratio = default_ratio;
	}

	public double getInvestment_min() {
		return investment_min;
	}

	public void setInvestment_min(double investment_min) {
		this.investment_min = investment_min;
	}

	public double getInvestment_max() {
		return investment_max;
	}

	public void setInvestment_max(double investment_max) {
		this.investment_max = investment_max;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getName_en() {
		return name_en;
	}

	public void setName_en(String name_en) {
		this.name_en = name_en;
	}

	public String getName_cn() {
		return name_cn;
	}

	public void setName_cn(String name_cn) {
		this.name_cn = name_cn;
	}

	public String getName_kn() {
		return name_kn;
	}

	public void setName_kn(String name_kn) {
		this.name_kn = name_kn;
	}

	public String getName_jn() {
		return name_jn;
	}

	public void setName_jn(String name_jn) {
		this.name_jn = name_jn;
	}

	public double getDaily_rate_max() {
		return daily_rate_max;
	}

	public void setDaily_rate_max(double daily_rate_max) {
		this.daily_rate_max = daily_rate_max;
	}

	
	
	
	public void setLogService(LogService logService) {
		this.logService = logService;
	}

	public String getLogin_safeword() {
		return login_safeword;
	}

	public void setLogin_safeword(String login_safeword) {
		this.login_safeword = login_safeword;
	}

	public void setSecUserService(SecUserService secUserService) {
		this.secUserService = secUserService;
	}

	public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
		this.passwordEncoder = passwordEncoder;
	}


}
