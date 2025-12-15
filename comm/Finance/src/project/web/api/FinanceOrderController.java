package project.web.api;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kernel.exception.BusinessException;
import kernel.sessiontoken.SessionTokenService;
import kernel.util.Arith;
import kernel.util.DateUtils;
import kernel.util.StringUtils;
import kernel.util.ThreadUtils;
import kernel.web.BaseAction;
import kernel.web.ResultObject;
import project.Constants;
import project.finance.Finance;
import project.finance.FinanceOrder;
import project.finance.FinanceOrderLock;
import project.finance.FinanceOrderService;
import project.finance.FinanceService;
import project.party.PartyService;
import project.party.model.Party;
import util.DateUtil;
import util.RandomUtil;

/**
 * 理财订单
 *
 */
@RestController
@CrossOrigin
public class FinanceOrderController extends BaseAction {

	private Logger logger = LogManager.getLogger(FinanceOrderController.class);
	
	@Autowired
	protected FinanceOrderService financeOrderService;
	@Autowired
	protected FinanceService financeService;
	@Autowired
	protected PartyService partyService;
	@Autowired
	protected SessionTokenService sessionTokenService;

	private final String action = "/api/financeOrder!";

	/**
	 * 托管订单
	 * 
	 */
	@RequestMapping(action + "list.action")
	public Object list(HttpServletRequest request) {
		ResultObject resultObject = new ResultObject();
		resultObject = readSecurityContextFromSession(resultObject);
		if (!"0".equals(resultObject.getCode())) {
			return resultObject;
		}
		try {
			List<FinanceOrder> data = null;
			String partyId = getLoginPartyId();
			
			if (StringUtils.isNullOrEmpty(partyId)) {
				resultObject.setData(data);
				resultObject.setCode("0");
				return resultObject;
			}
			String page_no = request.getParameter("page_no");
			int pageNo = 1;
			if (StringUtils.isNotEmpty(page_no)) {
				pageNo = Integer.valueOf(page_no);
			}
			// 状态。0.正常赎回， 1 托管中 ,2提前赎回 (违约)3.取消
			String state = request.getParameter("state");
			data = financeOrderService.pagedQuery(pageNo, 10, partyId, state).getElements();
			// 如果不为空 则从理财产品中获取日利率 图片
			if (data == null) {
				resultObject.setData(data);
				resultObject.setCode("0");
				return resultObject;
			}
			
			List<FinanceOrder> data1 = new ArrayList<>();
			FinanceOrder order;
			List<Finance> finances = financeService.findAll();
			Finance finance = new Finance();
			
			for (int i = 0; i < data.size(); i++) {
				order = data.get(i);
				for (int j = 0; j < finances.size(); j++) {
					finance = finances.get(j);
					if (finance.getId().equals(order.getFinanceId())) {
						break;
					}
				}
				// 取时间
				Date date_now = new Date();
				int days = daysBetween(date_now, order.getStop_time());
				if (days < 0) {
					days = 0;
				}
				order.setFinanceName(finance.getName());
				order.setFinanceName_cn(finance.getName_cn());
				order.setFinanceName_en(finance.getName_en());
				order.setDays(days);
				order.setClose_timeStr(DateUtils.format(order.getClose_time(), DateUtils.DF_yyyyMMdd));
				order.setCreate_timeStr(DateUtils.format(order.getCreate_time(), DateUtils.DF_yyyyMMdd));
				order.setDaily_rate(finance.getDaily_rate() + " ~ " + finance.getDaily_rate_max());
				data1.add(order);
			}
			
			resultObject.setData(data1);
			resultObject.setCode("0");
		} catch (BusinessException e) {
			resultObject.setCode("1");
			resultObject.setMsg(e.getMessage());
		} catch (Throwable t) {
			resultObject.setCode("1");
			resultObject.setMsg("程序错误");
			logger.error("error:", t);
		}

		return resultObject;

	}

	/**
	 * 收益统计
	 * 
	 */
	@RequestMapping(action + "listSum.action")
	public Object listSum() {

		ResultObject resultObject = new ResultObject();

		try {
			String partyId = getLoginPartyId();
			List<FinanceOrder> data = financeOrderService.findByState(partyId, "1");
			FinanceOrder order;
			List<Finance> finances = financeService.findAll();
			Finance finance = new Finance();
			Map<String, Object> map = new HashMap<String, Object>();

			// 金额总数
			double amount_sum = 0;
			// 预计今日盈利
			double today_profit = 0;
			// 已获收益
			double aready_profit = 0;
			// 订单数
			double order_sum = 0;

			if (data != null) {
				for (int i = 0; i < data.size(); i++) {
					order = data.get(i);
					order_sum = Arith.add(order_sum, 1);
					amount_sum = Arith.add(amount_sum, order.getAmount());
					aready_profit = Arith.add(aready_profit, order.getProfit());
					for (int j = 0; j < finances.size(); j++) {
						finance = finances.get(j);
						if (finance.getId().equals(order.getFinanceId())) {
							double finance_profit = Arith.mul(finance.getDaily_rate(), 0.01);
							double get_profit = Arith.mul(finance_profit, order.getAmount());
							today_profit = Arith.add(today_profit, get_profit);
							break;
						}
					}
				}
			}
			map.put("amount_sum", amount_sum);
			map.put("today_profit", today_profit);
			map.put("aready_profit", aready_profit);
			map.put("order_sum", order_sum);
			resultObject.setData(map);
			resultObject.setCode("0");
		} catch (BusinessException e) {
			resultObject.setCode("1");
			resultObject.setMsg(e.getMessage());
		} catch (Throwable t) {
			resultObject.setCode("1");
			resultObject.setMsg("程序错误");
			logger.error("error:", t);
		}

		return resultObject;
	}

	/**
	 * 理财托管订单-详情
	 */
	@RequestMapping(action + "get.action")
	public Object get(HttpServletRequest request) {

		ResultObject resultObject = new ResultObject();
		resultObject = readSecurityContextFromSession(resultObject);
		if (!"0".equals(resultObject.getCode())) {
			return resultObject;
		}
		try {
			String order_no = request.getParameter("order_no");
			FinanceOrder data = financeOrderService.findByOrder_no(order_no);

			// 如果不为空 则从理财产品中获取日利率 图片
			resultObject.setData(bulidData(data));
			resultObject.setCode("0");
		} catch (BusinessException e) {
			resultObject.setCode("1");
			resultObject.setMsg(e.getMessage());
		} catch (Throwable t) {
			resultObject.setCode("1");
			resultObject.setMsg("程序错误");
			logger.error("error:", t);
		}

		return resultObject;
	}

	/**
	 * 创建买入理财产品订单
	 * 
	 */
	@RequestMapping(action + "open.action")
	public Object open(HttpServletRequest request) {

		ResultObject resultObject = new ResultObject();
		resultObject = readSecurityContextFromSession(resultObject);
		if (!"0".equals(resultObject.getCode())) {
			return resultObject;
		}
		String partyId = this.getLoginPartyId();
		try {
			String session_token = request.getParameter("session_token");
			String financeId = request.getParameter("financeId");
			String amount = request.getParameter("amount");
			
			Object object = this.sessionTokenService.cacheGet(session_token);
			this.sessionTokenService.delete(session_token);
			if ((object == null) || (!this.getLoginPartyId().equals((String) object))) {
				resultObject.setCode("1");
				resultObject.setMsg("请稍后再试");
				return resultObject;
			}
			Party party = this.partyService.cachePartyBy(partyId, false);
			if (!party.getEnabled()) {
				resultObject.setCode("506");
				resultObject.setMsg(error);
				return resultObject;
			}

			FinanceOrder order = new FinanceOrder();
			order.setPartyId(partyId);
			order.setFinanceId(financeId);
			order.setAmount(Double.valueOf(amount));
			order.setOrder_no(DateUtil.getToday("yyMMddHHmmss") + RandomUtil.getRandomNum(8));
			order.setState("1");

			this.financeOrderService.saveCreate(order);
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("order_no", order.getOrder_no());
			resultObject.setData(map);
			resultObject.setCode("0");
		} catch (BusinessException e) {
			resultObject.setCode("1");
			resultObject.setMsg(e.getMessage());
		} catch (Throwable t) {
			resultObject.setCode("1");
			resultObject.setMsg("程序错误");
			logger.error("error:", t);
		}
		return resultObject;
	}

	/**
	 * 获取订单
	 * 
	 */
	@RequestMapping(action + "getOpen.action")
	public Object getOpen(HttpServletRequest request) {

		ResultObject resultObject = new ResultObject();
		resultObject = readSecurityContextFromSession(resultObject);
		if (!"0".equals(resultObject.getCode())) {
			return resultObject;
		}
		try {
			
			String financeId = request.getParameter("financeId");
			String amount_temp = request.getParameter("amount");
			
			Finance finance = financeService.findById(financeId);

			if (StringUtils.isNullOrEmpty(amount_temp) 
					|| !StringUtils.isDouble(amount_temp) 
					|| Double.valueOf(amount_temp) < 0) {
				throw new BusinessException("金额错误");
			}
			
			Double amount = Double.valueOf(amount_temp);

			Map<String, Object> map = new HashMap<String, Object>();
			if (finance != null) {
				String path = Constants.WEB_URL + "/public/showimg!showImg.action?imagePath=" + finance.getImg();
				map.put("img", path);
			}

			map.put("finance_name", finance.getName());
			map.put("finance_name_en", finance.getName_en());
			map.put("finance_name_cn", finance.getName_cn());
			map.put("finance_name_kn", finance.getName_kn());
			map.put("finance_name_jn", finance.getName_jn());
			map.put("cycle", finance.getCycle());
			String partyId = getLoginPartyId();
			if (!StringUtils.isNullOrEmpty(partyId)) {
				String session_token = sessionTokenService.savePut(partyId);
				map.put("session_token", session_token);
			}

			map.put("amount", amount);
			// 取时间
			Date date = new Date();

			map.put("create_time", DateUtils.format(date, DateUtils.DF_yyyyMMddHHmmss));
			Calendar calendar = new GregorianCalendar();
			calendar.setTime(date);
			calendar.add(calendar.DATE, 1);// 把日期往后增加一天.整数往后推
			date = calendar.getTime(); //

			// 起息时间
			map.put("earn_time", DateUtils.format(date, DateUtils.DF_yyyyMMdd));
			calendar.add(calendar.DATE, finance.getCycle());// 把日期往后增加周期.整数往后推
			date = calendar.getTime();

			// 截止时间
			map.put("stop_time", DateUtils.format(date, DateUtils.DF_yyyyMMdd));

			// 区间
			map.put("investment_min", finance.getInvestment_min());
			map.put("investment_max", finance.getInvestment_max());
			DecimalFormat df = new DecimalFormat("#.##");
			map.put("daily_rate", df.format(finance.getDaily_rate()) + "~" + df.format(finance.getDaily_rate_max()));
			map.put("daily_rate_max", finance.getDaily_rate_max());
			map.put("financeId", financeId);
			double rate = Arith.mul(finance.getDaily_rate(), 0.01);
			double rateMax = Arith.mul(finance.getDaily_rate_max(), 0.01);
			map.put("profit_may", Arith.mul(amount, Arith.mul(rate, finance.getCycle())) + "~"
					+ Arith.mul(amount, Arith.mul(rateMax, finance.getCycle())));
			map.put("order_no", DateUtil.getToday("yyMMddHHmmss") + RandomUtil.getRandomNum(8));

			resultObject.setData(map);
			resultObject.setCode("0");
		} catch (BusinessException e) {
			resultObject.setCode("1");
			resultObject.setMsg(e.getMessage());
		} catch (Throwable t) {
			resultObject.setCode("1");
			resultObject.setMsg("程序错误");
			logger.error("error:", t);
		} 
		return resultObject;
	}

	/**
	 * 赎回理财产品订单
	 * 
	 */
	@RequestMapping(action + "closOrder.action")
	public Object closOrder(HttpServletRequest request) {

		ResultObject resultObject = new ResultObject();
		resultObject = readSecurityContextFromSession(resultObject);
		if (!"0".equals(resultObject.getCode())) {
			return resultObject;
		}
		try {
			String id = request.getParameter("id");
			FinanceOrder order = financeOrderService.findById(id);
			CloseDelayThread lockDelayThread = new CloseDelayThread(id, order.getOrder_no(), financeOrderService);
			Thread t = new Thread(lockDelayThread);
			t.start();
		} catch (BusinessException e) {
			resultObject.setCode("1");
			resultObject.setMsg(e.getMessage());
		} catch (Throwable t) {
			resultObject.setCode("1");
			resultObject.setMsg("程序错误");
			logger.error("error:", t);
		}
		return resultObject;
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
						// order.setDays(daysBetween(order.getEarn_time(), order.getStop_time()));
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

			} catch (Throwable t) {
				logger.error("error:", t);
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

	public Map<String, Object> bulidData(FinanceOrder order) throws ParseException {
		Finance finance = new Finance();
		finance = financeService.findById(order.getFinanceId());

		DecimalFormat df = new DecimalFormat("#.##");
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("img", Constants.WEB_URL + "/public/showimg!showImg.action?imagePath=" + finance.getImg());
		map.put("daily_rate", df.format(finance.getDaily_rate()) + "~" + df.format(finance.getDaily_rate_max()));
		map.put("daily_rate_max", finance.getDaily_rate_max());
		map.put("create_timeStr", DateUtils.format(order.getCreate_time(), DateUtils.DF_yyyyMMddHHmmss));
		map.put("close_timeStr", DateUtils.format(order.getClose_time(), DateUtils.DF_yyyyMMddHHmmss));
		/**
		 * 此处将截止时间传回
		 */
		map.put("stop_timeStr", DateUtils.format(order.getClose_time(), DateUtils.DF_yyyyMMdd));
		map.put("earn_timeStr", DateUtils.format(order.getEarn_time(), DateUtils.DF_yyyyMMdd));
		Date date_now = new Date();// 取时间
		int days = daysBetween(date_now, order.getStop_time());
		if (days < 0) {
			days = 0;
		}
		map.put("days", days);
		double rate = Arith.mul(finance.getDaily_rate(), 0.01);
		double rateMax = Arith.mul(finance.getDaily_rate_max(), 0.01);
		map.put("profit_may", Arith.mul(order.getAmount(), Arith.mul(rate, finance.getCycle())) + "~"
				+ Arith.mul(order.getAmount(), Arith.mul(rateMax, finance.getCycle())));
		map.put("order_no", order.getOrder_no());
		map.put("amount", order.getAmount());
		map.put("cycle", order.getCycle());
		map.put("id", order.getId());
		map.put("name", finance.getName());
		map.put("name_en", finance.getName_en());
		map.put("name_cn", finance.getName_cn());
		map.put("name_kn", finance.getName_kn());
		map.put("name_jn", finance.getName_jn());

		map.put("state", order.getState());
		if ("1".equals(order.getState())) {

			map.put("profit", order.getProfit());

			double rate_order = Arith.mul(finance.getDefault_ratio(), 0.01);
			double last_days = daysBetween(date_now, order.getStop_time());
			if (last_days <= 0) {
				last_days = 1;
			}
			double default_amount = 0;
			default_amount = Arith.mul(order.getAmount(), Arith.mul(rate_order, last_days));
			map.put("default_amount", default_amount);
			double principal_amount = Arith.sub(order.getAmount(), default_amount);
			if (principal_amount < 0) {
				principal_amount = 0;
			}
			map.put("principal_amount", principal_amount);

		}
		if ("2".equals(order.getState())) {
			map.put("profit", 0);
			map.put("default_amount", df.format(Arith.sub(0, order.getProfit())));
			map.put("principal_amount", df.format(Arith.add(order.getAmount(), order.getProfit())));
		}
		if ("0".equals(order.getState())) {
			map.put("profit", order.getProfit());
			map.put("default_amount", 0);
			map.put("principal_amount", df.format(Arith.add(order.getAmount(), order.getProfit())));
		}

		return map;

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

}
