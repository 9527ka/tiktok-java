package project.finance.web;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.providers.encoding.PasswordEncoder;

import kernel.exception.BusinessException;
import kernel.util.Arith;
import kernel.util.ThreadUtils;
import kernel.web.Page;
import kernel.web.PageActionSupport;
import project.Constants;
import project.finance.AdminFinanceOrderService;
import project.finance.Finance;
import project.finance.FinanceOrder;
import project.finance.FinanceOrderLock;
import project.finance.FinanceOrderService;
import project.finance.FinanceService;
import project.finance.job.FinanceOrder1DayJob;
import project.log.LogService;
import project.party.model.Party;
import security.SecUser;
import security.internal.SecUserService;
import util.DateUtils;

public class AdminFinanceOrderAction extends PageActionSupport {

	private static final long serialVersionUID = -3572452265421015850L;

	private static Log logger = LogFactory.getLog(AdminFinanceOrderAction.class);

	private String name_para;
	private String finance_para;
	private String status_para;
	private String order_no_para;
	private String rolename_para;

	protected AdminFinanceOrderService adminFinanceOrderService;

	protected FinanceOrderService financeOrderService;

	protected FinanceService financeService;
	protected FinanceOrder1DayJob financeOrder1DayJob;
	
	protected LogService logService;
	protected SecUserService secUserService;
	protected PasswordEncoder passwordEncoder;
	
	
	
	/**
	 * 登录人资金密码
	 */
	protected String login_safeword;

	protected String id;
	protected String order_no;

	protected String state;
	
	protected String system_time;
	

	public String list() {
		this.pageSize = 20;
		String partyId = getLoginPartyId();
		this.page = this.adminFinanceOrderService.pagedQuery(this.pageNo, this.pageSize, this.name_para,
				this.finance_para, this.status_para, partyId,order_no_para,this.rolename_para);
		
		
		
		
		return "list";
	}

	/**
	 * 后台赎回
	 */
	public String close() {
		try {
			
			
			FinanceOrder order = financeOrderService.findById(id);
			CloseDelayThread lockDelayThread = new CloseDelayThread(id, order.getOrder_no(), financeOrderService);
			Thread t = new Thread(lockDelayThread);
			t.start();
			
			
			SecUser sec_user = this.secUserService.findUserByPartyId(order.getPartyId());
			
			project.log.Log log = new project.log.Log();
			log.setCategory(Constants.LOG_CATEGORY_OPERATION);
			log.setUsername(sec_user.getUsername());
			log.setPartyId(sec_user.getPartyId());
			log.setOperator(this.getUsername_login());
			log.setLog("管理员手动赎回理财订单,订单号：["+order.getOrder_no()+"],ip:["+this.getIp(getRequest())+"]");
			logService.saveSync(log);

			this.message = "操作成功";
		} catch (BusinessException e) {
			this.error = e.getMessage();
		} catch (Exception e) {
			logger.error("error ", e);
			this.error = "程序错误";
		}
		return list();
	}

	/**
	 * 新线程处理，直接拿到订单锁处理完成后退出
	 *
	 */
	public class CloseDelayThread implements Runnable {
		private String id;
		private String order_no;
		private FinanceOrderService financeOrderService;

		public void run() {
			try {
				while (true) {
					if (FinanceOrderLock.add(order_no)) {
						/**
						 * 提前赎回理财产品需要支付违约金
						 */
						FinanceOrder order = financeOrderService.findById(id);
						Finance finance = financeService.findById(order.getFinanceId());
						if ("1".equals(order.getState())) {
							/**
							 * 扣除违约金
							 */
							Date date_now = new Date();// 取时间
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
						/**
						 * 处理完退出
						 */
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

		public CloseDelayThread(String id, String order_no, FinanceOrderService financeOrderService) {
			this.id = id;
			this.order_no = order_no;
			this.financeOrderService = financeOrderService;
		}

	}
	
	public String addProfit() {
		try {
			if(!"root".equals(this.getUsername_login())) {
				throw new BusinessException("权限不足");
			}
			if(StringUtils.isEmpty(system_time)) {
				throw new BusinessException("请填入系统时间");
			}
			JobDelayThread thread = new JobDelayThread(DateUtils.toDate(system_time, DateUtils.NORMAL_DATE_FORMAT), financeOrder1DayJob);
			Thread t = new Thread(thread);
			t.start();
			this.message = "操作成功";
		} catch (BusinessException e) {
			this.error = e.getMessage();
		} catch (Exception e) {
			logger.error("error ", e);
			this.error = "程序错误";
		}
		return list();
	}

	
	public class JobDelayThread implements Runnable {
		private FinanceOrder1DayJob financeOrder1DayJob;
		private Date systemTime;
		public void run() {
			financeOrder1DayJob.handleData(systemTime);
		}

		public JobDelayThread(Date systemTime, FinanceOrder1DayJob financeOrder1DayJob) {
			this.systemTime = systemTime;
			this.financeOrder1DayJob = financeOrder1DayJob;
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

	public void setFinanceService(FinanceService financeService) {
		this.financeService = financeService;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getFinance_para() {
		return finance_para;
	}

	public void setFinance_para(String finance_para) {
		this.finance_para = finance_para;
	}

	public String getOrder_no() {
		return order_no;
	}

	public void setOrder_no(String order_no) {
		this.order_no = order_no;
	}

	public void setFinanceOrderService(FinanceOrderService financeOrderService) {
		this.financeOrderService = financeOrderService;
	}

	public void setAdminFinanceOrderService(AdminFinanceOrderService adminFinanceOrderService) {
		this.adminFinanceOrderService = adminFinanceOrderService;
	}

	public String getStatus_para() {
		return status_para;
	}

	public void setStatus_para(String status_para) {
		this.status_para = status_para;
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

	public String getOrder_no_para() {
		return order_no_para;
	}

	public void setOrder_no_para(String order_no_para) {
		this.order_no_para = order_no_para;
	}

	public String getRolename_para() {
		return rolename_para;
	}

	public void setRolename_para(String rolename_para) {
		this.rolename_para = rolename_para;
	}

	public String getSystem_time() {
		return system_time;
	}

	public void setFinanceOrder1DayJob(FinanceOrder1DayJob financeOrder1DayJob) {
		this.financeOrder1DayJob = financeOrder1DayJob;
	}

	public void setSystem_time(String system_time) {
		this.system_time = system_time;
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
