package project.web.admin;

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
import project.finance.AdminFinanceService;
import project.finance.Finance;
import project.finance.FinanceService;
import project.log.Log;
import project.log.LogService;
import project.party.PartyService;
import security.internal.SecUserService;

/**
 * 理财配置
 */
@RestController
public class AdminFinanceController extends PageActionSupport {
	
	private Logger logger = LogManager.getLogger(AdminFinanceController.class);

	@Autowired
	protected AdminFinanceService adminFinanceService;
	@Autowired
	protected FinanceService financeService;
	@Autowired
	protected PartyService partyService;
	@Autowired
	protected LogService logService;
	@Autowired
	protected SecUserService secUserService;
	@Autowired
	protected PasswordEncoder passwordEncoder;
	
	private final String action = "/normal/adminFinanceAction!";

	/**
	 * 获取 理财配置 列表
	 */
	@RequestMapping(action + "list.action")
	public ModelAndView list(HttpServletRequest request) {
		String pageNo = request.getParameter("pageNo");
		String message = request.getParameter("message");
		String error = request.getParameter("error");
		String name_para = request.getParameter("name_para");

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("finance_list");

		try {

			this.checkAndSetPageNo(pageNo);
			
			this.pageSize = 300;
			
			this.page = this.adminFinanceService.pagedQuery(this.pageNo, this.pageSize, name_para);

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
		modelAndView.addObject("name_para", name_para);
		return modelAndView;
	}

	/**
	 * 新增 理财配置 页面
	 */
	@RequestMapping(action + "toAdd.action")
	public ModelAndView toAdd() {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("finance_add");
		return modelAndView;
	}

	/**
	 * 新增 理财配置
	 */
	@RequestMapping(action + "add.action")
	public ModelAndView add(HttpServletRequest request) {		
		// 产品名称
		String name = request.getParameter("name");
		// 产品图片
		String img = request.getParameter("img");
		// 周期
		String cycle = request.getParameter("cycle");
		// 日利率
		String daily_rate = request.getParameter("daily_rate");
		String daily_rate_max = request.getParameter("daily_rate_max");
		// 今日利率
		String today_rate = request.getParameter("today_rate");
		// 违约结算比例
		String default_ratio = request.getParameter("default_ratio");
		// 投资金额区间min
		String investment_min = request.getParameter("investment_min");
		// 投资金额区间max
		String investment_max = request.getParameter("investment_max");
		// 资金密码
		String login_safeword = request.getParameter("login_safeword");
		String name_en = request.getParameter("name_en");
		String name_cn = request.getParameter("name_cn");
		String name_kn = request.getParameter("name_kn");
		String name_jn = request.getParameter("name_jn");
		String state = request.getParameter("state");
		
		ModelAndView model = new ModelAndView();
		model.addObject("name", name);
		model.addObject("img", img);
		model.addObject("cycle", cycle);
		model.addObject("daily_rate", daily_rate);
		model.addObject("daily_rate_max", daily_rate_max);
		model.addObject("today_rate", today_rate);
		model.addObject("default_ratio", default_ratio);
		model.addObject("investment_min", investment_min);
		model.addObject("investment_max", investment_max);
		model.addObject("name_en", name_en);
		model.addObject("name_cn", name_cn);
		model.addObject("name_kn", name_kn);
		model.addObject("name_jn", name_jn);
		model.addObject("state", state);
		
		try {

			String error = this.verification(name, img, cycle, daily_rate, daily_rate_max, today_rate, 
					default_ratio, investment_min, investment_max, login_safeword);
			if (!StringUtils.isNullOrEmpty(error)) {
				throw new BusinessException(error);
			}
			
			Finance finance = new Finance();
			finance.setName(name);
			finance.setName_en(name_en);
			finance.setName_cn(name_cn);
			finance.setName_kn(name_kn);
			finance.setName_jn(name_jn);
			finance.setImg(img);
			finance.setCycle(Integer.valueOf(cycle));
			finance.setDaily_rate(Double.valueOf(daily_rate));
			finance.setDaily_rate_max(Double.valueOf(daily_rate_max));
			finance.setToday_rate(Double.valueOf(today_rate));
			finance.setDefault_ratio(Double.valueOf(default_ratio));
			finance.setInvestment_min(Double.valueOf(investment_min));
			finance.setInvestment_max(Double.valueOf(investment_max));
			finance.setState(state);

			this.financeService.save(finance, login_safeword, this.getUsername_login());
			
			Log log = new Log();
			log.setCategory(Constants.LOG_CATEGORY_OPERATION);
			log.setUsername(this.getUsername_login());
			log.setOperator(this.getUsername_login());
			log.setLog("管理员手动添加理财配置,ip:["+this.getIp(request)+"]");
			logService.saveSync(log);
			
		} catch (BusinessException e) {
			model.addObject("error", e.getMessage());
			model.setViewName("finance_add");
			return model;
		} catch (Throwable t) {
			logger.error("UserAction.register error ", t);
			model.addObject("error", error);
			model.setViewName("finance_add");
			return model;
		}
		
		model.addObject("message", "操作成功");
		model.setViewName("redirect:/" + action + "list.action");
		return model;
	}
	
	/**
	 * 修改 理财配置 页面
	 */
	@RequestMapping(action + "toUpdate.action")
	public ModelAndView toUpdate(HttpServletRequest request) {
		String id = request.getParameter("id");

		ModelAndView modelAndView = new ModelAndView();
		
		try {
		
			Finance finance = this.financeService.findById(id);

			modelAndView.addObject("id", id);
			modelAndView.addObject("name", finance.getName());
			modelAndView.addObject("img", finance.getImg());
			modelAndView.addObject("cycle", finance.getCycle());
			modelAndView.addObject("daily_rate", finance.getDaily_rate());
			modelAndView.addObject("daily_rate_max", finance.getDaily_rate_max());
			modelAndView.addObject("today_rate", finance.getToday_rate());
			modelAndView.addObject("default_ratio", finance.getDefault_ratio());
			modelAndView.addObject("investment_min", finance.getInvestment_min());
			modelAndView.addObject("investment_max", finance.getInvestment_max());
			modelAndView.addObject("name_en", finance.getName_en());
			modelAndView.addObject("name_cn", finance.getName_cn());
			modelAndView.addObject("name_kn", finance.getName_kn());
			modelAndView.addObject("name_jn", finance.getName_jn());
			modelAndView.addObject("state", finance.getState());
		
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
		
		modelAndView.setViewName("finance_update");
		return modelAndView;
	}

	/**
	 * 修改 理财配置
	 */
	@RequestMapping(action + "update.action")
	public ModelAndView update(HttpServletRequest request) {
		String id = request.getParameter("id");
		// 产品名称
		String name = request.getParameter("name");
		// 产品图片
		String img = request.getParameter("img");
		// 周期
		String cycle = request.getParameter("cycle");
		// 日利率
		String daily_rate = request.getParameter("daily_rate");
		String daily_rate_max = request.getParameter("daily_rate_max");
		// 今日利率
		String today_rate = request.getParameter("today_rate");
		// 违约结算比例
		String default_ratio = request.getParameter("default_ratio");
		// 投资金额区间min
		String investment_min = request.getParameter("investment_min");
		// 投资金额区间max
		String investment_max = request.getParameter("investment_max");
		String name_en = request.getParameter("name_en");
		String name_cn = request.getParameter("name_cn");
		String name_kn = request.getParameter("name_kn");
		String name_jn = request.getParameter("name_jn");
		String state = request.getParameter("state");
		// 资金密码
		String login_safeword = request.getParameter("login_safeword");
		
		ModelAndView model = new ModelAndView();
		model.addObject("id", id);
		model.addObject("name", name);
		model.addObject("img", img);
		model.addObject("cycle", cycle);
		model.addObject("daily_rate", daily_rate);
		model.addObject("daily_rate_max", daily_rate_max);
		model.addObject("today_rate", today_rate);
		model.addObject("default_ratio", default_ratio);
		model.addObject("investment_min", investment_min);
		model.addObject("investment_max", investment_max);
		model.addObject("name_en", name_en);
		model.addObject("name_cn", name_cn);
		model.addObject("name_kn", name_kn);
		model.addObject("name_jn", name_jn);
		model.addObject("state", state);
		
		try {
			
			String error = this.verification(name, img, cycle, daily_rate, daily_rate_max, today_rate, 
					default_ratio, investment_min, investment_max, login_safeword);
			if (!StringUtils.isNullOrEmpty(error)) {
				throw new BusinessException(error);
			}
			
			Finance finance = this.financeService.findById(id);

			finance.setName(name);
			finance.setName_en(name_en);
			finance.setName_cn(name_cn);
			finance.setName_kn(name_kn);
			finance.setName_jn(name_jn);
			finance.setImg(img);
			finance.setCycle(Integer.valueOf(cycle));
			finance.setDaily_rate(Double.valueOf(daily_rate));
			finance.setDaily_rate_max(Double.valueOf(daily_rate_max));
			finance.setToday_rate(Double.valueOf(today_rate));
			finance.setDefault_ratio(Double.valueOf(default_ratio));
			finance.setInvestment_min(Double.valueOf(investment_min));
			finance.setInvestment_max(Double.valueOf(investment_max));
			finance.setState(state);
			
			this.financeService.update(finance, login_safeword, this.getUsername_login());
			
			Log log = new Log();
			log.setCategory(Constants.LOG_CATEGORY_OPERATION);
			log.setUsername(this.getUsername_login());
			log.setOperator(this.getUsername_login());
			log.setLog("管理员手动修改理财配置,ip:["+this.getIp(getRequest())+"]");
			logService.saveSync(log);
			
		} catch (BusinessException e) {
			model.addObject("error", e.getMessage());
			model.setViewName("finance_update");
			return model;
		} catch (Throwable t) {
			logger.error("update error ", t);
			model.addObject("error", "程序错误");
			model.setViewName("finance_update");
			return model;
		}
		
		model.addObject("message", "操作成功");
		model.setViewName("redirect:/" + action + "list.action");
		return model;
	}
	
	/**
	 * 删除 理财配置
	 */
	@RequestMapping(action + "toDelete.action")
	public ModelAndView toDelete(HttpServletRequest request) {			
		String id = request.getParameter("id");
		String login_safeword = request.getParameter("login_safeword");

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("redirect:/" + action + "list.action");

		try {
			
			if (StringUtils.isNullOrEmpty(login_safeword)) {
				throw new BusinessException("请输入登录人资金密码");
			}
			
			this.financeService.delete(id, login_safeword, this.getUsername_login());
			
			Log log = new Log();
			log.setCategory(Constants.LOG_CATEGORY_OPERATION);
			log.setUsername(this.getUsername_login());
			log.setOperator(this.getUsername_login());
			log.setLog("管理员手动删除理财配置,ip:["+this.getIp(getRequest())+"]");
			logService.saveSync(log);

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

	protected String verification(String name, String img, String cycle, String daily_rate, String daily_rate_max, String today_rate, 
			String default_ratio, String investment_min, String investment_max, String login_safeword) {
		if (StringUtils.isEmptyString(name)) {
			return "请输入产品名称";
		}			
		if (StringUtils.isEmptyString(img)) {
			return "请上传产品图片";
		}		
		if (StringUtils.isEmptyString(login_safeword)) {
			return "请输入登录人资金密码";
		}			
		if (StringUtils.isNullOrEmpty(cycle) 
				|| !StringUtils.isInteger(cycle) 
				|| Integer.valueOf(cycle) <= 0) {
			return "周期不能小于等于0天";
		}
		if (StringUtils.isNullOrEmpty(daily_rate) 
				|| !StringUtils.isDouble(daily_rate) 
				|| Double.valueOf(daily_rate) < 0) {
			return "日利率不能小于0";
		}
		if (StringUtils.isNullOrEmpty(daily_rate_max) 
				|| !StringUtils.isDouble(daily_rate_max) 
				|| Double.valueOf(daily_rate_max) < 0) {
			return "日利率不能小于0";
		}
		if (StringUtils.isNullOrEmpty(today_rate) 
				|| !StringUtils.isDouble(today_rate) 
				|| Double.valueOf(today_rate) < 0) {
			return "今日利率不能小于0";
		}
		if (StringUtils.isNullOrEmpty(default_ratio) 
				|| !StringUtils.isDouble(default_ratio) 
				|| Double.valueOf(default_ratio) < 0) {
			return "违约结算比例不能小于0";
		}
		if (StringUtils.isNullOrEmpty(investment_min) 
				|| !StringUtils.isDouble(investment_min) 
				|| Double.valueOf(investment_min) < 0) {
			return "投资金额区间不能小于0";
		}
		if (StringUtils.isNullOrEmpty(investment_max) 
				|| !StringUtils.isDouble(investment_max) 
				|| Double.valueOf(investment_max) < 0) {
			return "投资金额区间不能小于0";
		}
		if (Double.valueOf(investment_max) <= Double.valueOf(investment_min)) {
			return "投资金额区间错误";
		}
		return null;
	}

}
