package project.web.admin;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.providers.encoding.PasswordEncoder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import kernel.exception.BusinessException;
import kernel.util.Arith;
import kernel.util.ThreadUtils;
import kernel.web.PageActionSupport;
import project.Constants;
import project.finance.AdminFinanceOrderService;
import project.finance.Finance;
import project.finance.FinanceOrder;
import project.finance.FinanceOrderLock;
import project.finance.FinanceOrderService;
import project.finance.FinanceService;
import project.finance.job.FinanceOrder1DayJob;
import project.log.Log;
import project.log.LogService;
import security.SecUser;
import security.internal.SecUserService;
import util.DateUtils;

/**
 * 理财产品订单
 */
@RestController
public class AdminFinanceOrderController extends PageActionSupport {

	private Logger logger = LogManager.getLogger(AdminFinanceOrderController.class);

	@Autowired
	protected AdminFinanceOrderService adminFinanceOrderService;
	@Autowired
	protected FinanceOrderService financeOrderService;
	@Autowired
	protected FinanceService financeService;
	@Autowired
	protected FinanceOrder1DayJob financeOrder1DayJob;
	@Autowired
	protected LogService logService;
	@Autowired
	protected SecUserService secUserService;
	@Autowired
	protected PasswordEncoder passwordEncoder;

	private final String action = "normal/adminFinanceOrderAction!";

	/**
	 * 获取 理财产品订单 列表
	 */
	@RequestMapping(action + "list.action")
	public ModelAndView list(HttpServletRequest request) {
		String pageNo = request.getParameter("pageNo");
		String message = request.getParameter("message");
		String error = request.getParameter("error");
		String name_para = request.getParameter("name_para");
		String finance_para = request.getParameter("finance_para");
		String status_para = request.getParameter("status_para");
		String order_no_para = request.getParameter("order_no_para");
		String rolename_para = request.getParameter("rolename_para");

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("finance_order_list");

		try {

			this.checkAndSetPageNo(pageNo);

			this.pageSize = 20;

			String partyId = this.getLoginPartyId();

			this.page = this.adminFinanceOrderService.pagedQuery(this.pageNo, this.pageSize, name_para, finance_para,
					status_para, partyId, order_no_para, rolename_para);

			List<Map> list = this.page.getElements();
			for (int i = 0; i < list.size(); i++) {
				Map map = list.get(i);
				if (null == map.get("rolename")) {
					map.put("roleNameDesc", "");
				} else {
					String roleName = map.get("rolename").toString();
					map.put("roleNameDesc", Constants.ROLE_MAP.containsKey(roleName) ? Constants.ROLE_MAP.get(roleName) : roleName);
				}
			}

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
		modelAndView.addObject("finance_para", finance_para);
		modelAndView.addObject("status_para", status_para);
		modelAndView.addObject("order_no_para", order_no_para);
		modelAndView.addObject("rolename_para", rolename_para);
		return modelAndView;
	}
	
	/**
	 * 后台赎回
	 */
	@RequestMapping(action + "close.action")
	public ModelAndView close(HttpServletRequest request) {
		String id = request.getParameter("id");

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("redirect:/" + action + "list.action");
		
		try {

			FinanceOrder order = this.financeOrderService.findById(id);
			
			CloseDelayThread lockDelayThread = new CloseDelayThread(id, order.getOrder_no(), this.financeService, this.financeOrderService);
			
			Thread t = new Thread(lockDelayThread);
			t.start();

			SecUser sec_user = this.secUserService.findUserByPartyId(order.getPartyId());

			Log log = new Log();
			log.setCategory(Constants.LOG_CATEGORY_OPERATION);
			log.setUsername(sec_user.getUsername());
			log.setPartyId(sec_user.getPartyId());
			log.setOperator(this.getUsername_login());
			log.setLog("管理员手动赎回理财订单,订单号：[" + order.getOrder_no() + "],ip:[" + this.getIp(getRequest()) + "]");
			logService.saveSync(log);

		} catch (BusinessException e) {
			modelAndView.addObject("error", e.getMessage());
			return modelAndView;
		} catch (Throwable t) {
			logger.error(" error ", t);
			modelAndView.addObject("error", "[ERROR] " + t.getMessage());
			return modelAndView;
		}

		modelAndView.addObject("message", "操作成功");
		return modelAndView;
	}
	
	/**
	 * 利息重计
	 */
	@RequestMapping(action + "addProfit.action")
	public ModelAndView addProfit(HttpServletRequest request) {
		String system_time = request.getParameter("system_time");

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("redirect:/" + action + "list.action");
		
		try {
			
			if (!"root".equals(this.getUsername_login())) {
				throw new BusinessException("权限不足");
			}
			
			if (StringUtils.isEmpty(system_time)) {
				throw new BusinessException("请填入系统时间");
			}
			
			JobDelayThread thread = new JobDelayThread(DateUtils.toDate(system_time, DateUtils.NORMAL_DATE_FORMAT), this.financeOrder1DayJob);
			Thread t = new Thread(thread);
			t.start();

		} catch (BusinessException e) {
			modelAndView.addObject("error", e.getMessage());
			return modelAndView;
		} catch (Throwable t) {
			logger.error(" error ", t);
			modelAndView.addObject("error", "[ERROR] " + t.getMessage());
			return modelAndView;
		}

		modelAndView.addObject("message", "操作成功");
		return modelAndView;
	}

	public static int daysBetween(Date smdate, Date bdate) throws ParseException {		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		smdate = sdf.parse(sdf.format(smdate));
		bdate = sdf.parse(sdf.format(bdate));		
		Calendar cal = Calendar.getInstance();
		cal.setTime(smdate);
		long time1 = cal.getTimeInMillis();
		cal.setTime(bdate);
		long time2 = cal.getTimeInMillis();
		long between_days = (time2 - time1) / (1000 * 3600 * 24);
		return Integer.parseInt(String.valueOf(between_days));
	}

	/**
	 * 新线程处理，直接拿到订单锁处理完成后退出
	 */
	public class CloseDelayThread implements Runnable {
		private String id;
		private String order_no;
		private FinanceService financeService;
		private FinanceOrderService financeOrderService;

		public void run() {
			
			try {
				
				while (true) {
					
					if (FinanceOrderLock.add(order_no)) {
						
						// 提前赎回理财产品需要支付违约金
						FinanceOrder order = this.financeOrderService.findById(id);
						Finance finance = this.financeService.findById(order.getFinanceId());
						
						if ("1".equals(order.getState())) {
							
							// 取时间
							Date date_now = new Date();
							
							// 扣除违约金
							double last_days = daysBetween(date_now, order.getStop_time());
							if (last_days <= 0) {
								last_days = 1;
							}
							
							double default_ratio = Arith.mul(finance.getDefault_ratio(), 0.01);
							default_ratio = Arith.mul(default_ratio, last_days);
							double breach_amount = Arith.mul(order.getAmount(), default_ratio);
							order.setProfit(Arith.sub(0, breach_amount));
							order.setState("2");

							this.financeOrderService.saveClose(order);
						}
						
						// 处理完退出
						break;
					}
					
					ThreadUtils.sleep(500);
				}

			} catch (Exception e) {
				logger.error("error:", e);
			} finally {
				FinanceOrderLock.remove(order_no);
			}
		}

		public CloseDelayThread(String id, String order_no, FinanceService financeService, FinanceOrderService financeOrderService) {
			this.id = id;
			this.order_no = order_no;
			this.financeService = financeService;
			this.financeOrderService = financeOrderService;
		}
		
	}

	public class JobDelayThread implements Runnable {
		private FinanceOrder1DayJob financeOrder1DayJob;
		private Date systemTime;

		public void run() {
			this.financeOrder1DayJob.handleData(systemTime);
		}

		public JobDelayThread(Date systemTime, FinanceOrder1DayJob financeOrder1DayJob) {
			this.systemTime = systemTime;
			this.financeOrder1DayJob = financeOrder1DayJob;
		}

	}

}
