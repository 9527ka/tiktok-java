package project.web.api;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
import kernel.web.BaseAction;
import kernel.web.ResultObject;
import project.miner.MinerOrderService;
import project.miner.MinerService;
import project.miner.model.Miner;
import project.miner.model.MinerOrder;
import project.party.PartyService;
import project.party.model.Party;
import project.syspara.SysparaService;
import util.DateUtil;
import util.RandomUtil;

/**
 * 矿池订单
 *
 */
@RestController
@CrossOrigin
public class MinerOrderController extends BaseAction {

	private Logger logger = LogManager.getLogger(MinerOrderController.class);
	@Autowired
	protected MinerOrderService minerOrderService;
	@Autowired
	protected MinerService minerService;
	@Autowired
	protected PartyService partyService;
	@Autowired
	protected SessionTokenService sessionTokenService;
	@Autowired
	protected SysparaService sysparaService;
	
	private final String action = "api/minerOrder!";

	/**
	 * 矿池订单 列表
	 */
	@RequestMapping(action + "list.action")
	public Object list(HttpServletRequest request) {

		ResultObject resultObject = new ResultObject();
		resultObject = readSecurityContextFromSession(resultObject);
		if (!"0".equals(resultObject.getCode())) {
			return resultObject;
		}
		try {
			List<Map<String, Object>> datas = null;
			String partyId = getLoginPartyId();
			DecimalFormat df = new DecimalFormat("#.##");
			if (StringUtils.isNullOrEmpty(partyId)) {
				resultObject.setData(datas);
				resultObject.setCode("0");
				return resultObject;
			}
			
			// 状态。0.正常赎回， 1 托管中 ,2提前赎回 (违约)3.取消
			String state = request.getParameter("state");
			String page_no = request.getParameter("page_no");
			int pageNo = 1;
			if (StringUtils.isNotEmpty(page_no)) {
				pageNo = Integer.valueOf(page_no);
			}
			
			datas = minerOrderService.pagedQuery(pageNo, 40, partyId, state).getElements();
			Double miner_test_profit = sysparaService.find("miner_test_profit").getDouble();
			for (Map<String, Object> data : datas) {
				int intervalDaysByTwoDate = 0;
				if (null == data.get("stop_time")) {
					if ("0" != data.get("cycle_close")) {
						String can_close_time = DateUtils
								.format(DateUtils.addDay(DateUtils.toDate(data.get("create_time").toString()),
										new Double(data.get("cycle_close").toString()).intValue()),
										DateUtils.DF_yyyyMMdd);
						intervalDaysByTwoDate = DateUtils.getIntervalDaysByTwoDate(DateUtils.toDate(can_close_time),
								new Date());
					}
				} else {
					intervalDaysByTwoDate = DateUtils.getIntervalDaysByTwoDate(
							DateUtils.toDate(data.get("stop_time").toString()), new Date());
				}
				if (intervalDaysByTwoDate < 0) {
					intervalDaysByTwoDate = 0;
				}
				data.put("days", intervalDaysByTwoDate);
				data.put("profit", df.format(data.get("profit")));
				data.put("test", null != data.get("test") && "Y".equals(data.get("test").toString()));
				data.put("can_close", intervalDaysByTwoDate <= 0);
				/**
				 * 当日收益
				 */
				data.put("daily_profit",
						(boolean) data.get("test") ? miner_test_profit
								: df.format(Arith.mul(
										Arith.mul(Double.valueOf(data.get("daily_rate").toString()), 0.01),
										Double.valueOf(data.get("amount").toString()))));

				data.put("daily_rate", (boolean) data.get("test") ? miner_test_profit : data.get("daily_rate"));

				// 这里的周期，体验矿机是周期，其他矿机是解锁周期(到期后需手动解锁)
				int cycle = (boolean) data.get("test") ? Integer.valueOf(data.get("cycle").toString())
						: Integer.valueOf(data.get("cycle_close").toString());
//				int cycle = Integer.valueOf(data.get("cycle_close").toString()) ;
				data.put("cycle", cycle);

				double all_rate = Arith.mul(30, Double.valueOf(data.get("daily_rate").toString()));

				data.put("all_rate",
						(boolean) data.get("test") ? Arith.mul(miner_test_profit, cycle) : df.format(all_rate));
			}
			resultObject.setData(datas);
		} catch (BusinessException e) {
			resultObject.setCode("1");
			resultObject.setMsg(e.getMessage());
		} catch (Exception e) {
			resultObject.setCode("1");
			resultObject.setMsg("程序错误");
			logger.error("error:", e);
		}
		return resultObject;
	}

	/**
	 * 矿机收益统计
	 */
	@RequestMapping(action + "listSum.action")
	public Object listSum() {
		ResultObject resultObject = new ResultObject();
		try {
			String partyId = getLoginPartyId();
			List<MinerOrder> data = minerOrderService.findByState(partyId, null);
			MinerOrder order;
			List<Miner> miners = minerService.findAll();
			Miner miner = new Miner();
			Map<String, Object> map = new HashMap<String, Object>();

			// 金额总数
			double amount_sum = 0;
			// 预计今日盈利
			double today_profit = 0;
			// 已获收益
			double aready_profit = 0;
			// 订单数
			double order_sum = 0;
			Double miner_test_profit = sysparaService.find("miner_test_profit").getDouble();

			if (data != null) {
				for (int i = 0; i < data.size(); i++) {
					order = data.get(i);
					if ("1".equals(order.getState())) {
						order_sum = Arith.add(order_sum, 1);
						amount_sum = Arith.add(amount_sum, order.getAmount());
					}
					aready_profit = Arith.add(aready_profit, order.getProfit());
					for (int j = 0; j < miners.size(); j++) {
						miner = miners.get(j);
						if (miner.getId().equals(order.getMinerId()) && "1".equals(order.getState())) {
							if (miner.getTest()) {
								today_profit = Arith.add(today_profit, miner_test_profit);
							} else {
								double miner_profit = Arith.mul(miner.getDaily_rate(), 0.01);
								double get_profit = Arith.mul(miner_profit, order.getAmount());
								today_profit = Arith.add(today_profit, get_profit);
								break;
							}
						}
					}
				}
			}
			DecimalFormat df = new DecimalFormat("#.##");
			map.put("amount_sum", df.format(amount_sum));
			map.put("today_profit", df.format(today_profit));
			map.put("aready_profit", df.format(aready_profit));
			map.put("order_sum", order_sum);
			resultObject.setData(map);
			resultObject.setCode("0");
		} catch (BusinessException e) {
			resultObject.setCode("1");
			resultObject.setMsg(e.getMessage());
		} catch (Exception e) {
			resultObject.setCode("1");
			resultObject.setMsg("程序错误");
			logger.error("error:", e);
		}
		return resultObject;
	}

	/**
	 * 矿池订单 详情
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
			MinerOrder data = minerOrderService.findByOrder_no(order_no);

			resultObject.setData(bulidData(data));
			resultObject.setCode("0");
		} catch (BusinessException e) {
			resultObject.setCode("1");
			resultObject.setMsg(e.getMessage());
		} catch (Exception e) {
			resultObject.setCode("1");
			resultObject.setMsg("程序错误");
			logger.error("error:", e);
		}
		return resultObject;
	}

	/**
	 * 创建买入矿机订单
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
			String minerId = request.getParameter("minerId");
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

			MinerOrder order = new MinerOrder();
			order.setPartyId(partyId);
			order.setMinerId(minerId);
			order.setAmount(Double.valueOf(amount));
			order.setOrder_no(DateUtil.getToday("yyMMddHHmmss") + RandomUtil.getRandomNum(8));
			order.setState("1");

			this.minerOrderService.saveCreate(order, false);

			Map<String, Object> map = new HashMap<String, Object>();
			map.put("order_no", order.getOrder_no());
			resultObject.setData(map);
			resultObject.setCode("0");
		} catch (BusinessException e) {
			resultObject.setCode("1");
			resultObject.setMsg(e.getMessage());
		} catch (Exception e) {
			resultObject.setCode("1");
			resultObject.setMsg("程序错误");
			logger.error("error:", e);
		}
		return resultObject;
	}

	/**
	 * 获取订单
	 */
	@RequestMapping(action + "getOpen.action")
	public Object getOpen(HttpServletRequest request) throws IOException {
		ResultObject resultObject = new ResultObject();
		resultObject = readSecurityContextFromSession(resultObject);
		if (!"0".equals(resultObject.getCode())) {
			return resultObject;
		}
		try {
			String minerId = request.getParameter("minerId");
			String amount_temp = request.getParameter("amount");
			Miner miner = minerService.findById(minerId);
			if (StringUtils.isNullOrEmpty(amount_temp) 
					|| !StringUtils.isDouble(amount_temp) 
					|| Double.valueOf(amount_temp) < 0) {
				throw new BusinessException("金额错误");
			}
			
			double amount = Double.valueOf(amount_temp);
			
			DecimalFormat df = new DecimalFormat("#.##");
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("miner_test", miner.getTest());
			map.put("miner_name", miner.getName());
			map.put("miner_name_en", miner.getName_en());
			map.put("miner_name_cn", miner.getName_cn());
			String partyId = getLoginPartyId();
			if (!StringUtils.isNullOrEmpty(partyId)) {
				String session_token = sessionTokenService.savePut(partyId);
				map.put("session_token", session_token);
			}
			map.put("amount", miner.getTest() ? 0 : amount);
			Date date = new Date();// 取时间

			map.put("create_time", DateUtils.format(date, DateUtils.DF_yyyyMMddHHmmss));
			Calendar calendar = new GregorianCalendar();
			calendar.setTime(date);
			// 把日期往后增加一天.整数往后推
			calendar.add(calendar.DATE, 1);
			date = calendar.getTime();

			// 起息时间
			map.put("earn_time", DateUtils.format(date, DateUtils.DF_yyyyMMdd));
			// 把日期往后增加周期.整数往后推
			calendar.add(calendar.DATE, miner.getCycle());
			date = calendar.getTime();

			// 截止时间
			map.put("stop_time",
					miner.getTest()
							? DateUtils.format(DateUtils.addDay(new Date(), miner.getCycle()), DateUtils.DF_yyyyMMdd)
							: null);

			// 区间
			map.put("investment_min", miner.getInvestment_min());
			Double miner_test_profit = sysparaService.find("miner_test_profit").getDouble();

			map.put("daily_rate", miner.getTest() ? miner_test_profit : miner.getDaily_rate());
			map.put("minerId", minerId);
			double rate = Arith.mul(miner.getDaily_rate(), 0.01);
			if (miner.getTest()) {
				map.put("profit_may", String.valueOf(Arith.mul(miner_test_profit, miner.getCycle())));
			} else {
				map.put("profit_may", String.valueOf(df.format(Arith.mul(amount, Arith.mul(rate, 30)))));

			}
			map.put("order_no", DateUtil.getToday("yyMMddHHmmss") + RandomUtil.getRandomNum(8));

			// 判断是否体验矿机
			int cycle = miner.getTest() ? miner.getCycle() : miner.getCycle_close();

			map.put("cycle", cycle);
			double all_rate = Arith.mul(30, miner.getDaily_rate());
			map.put("all_rate", miner.getTest() ? String.valueOf(Arith.mul(miner_test_profit, miner.getCycle())) 
					: String.valueOf(df.format(all_rate)));

			map.put("test", miner.getTest());

			resultObject.setData(map);
			resultObject.setCode("0");
		} catch (BusinessException e) {
			resultObject.setCode("1");
			resultObject.setMsg(e.getMessage());
		} catch (Exception e) {
			resultObject.setCode("1");
			resultObject.setMsg("程序错误");
			logger.error("error:", e.fillInStackTrace());
		} 
		return resultObject;
	}


	/**
	 * 赎回订单
	 * 
	 */
	@RequestMapping(action + "closOrder.action")
	public Object closOrder(HttpServletRequest request) throws IOException {
		ResultObject resultObject = new ResultObject();
		resultObject = readSecurityContextFromSession(resultObject);
		if (!"0".equals(resultObject.getCode())) {
			return resultObject;
		}
		try {
			String order_no = request.getParameter("order_no");
			CloseDelayThread lockDelayThread = new CloseDelayThread(order_no, minerOrderService);
			Thread t = new Thread(lockDelayThread);
			t.start();

		} catch (BusinessException e) {
			resultObject.setCode("1");
			resultObject.setMsg(e.getMessage());
		} catch (Exception e) {
			resultObject.setCode("1");
			resultObject.setMsg("程序错误");
			logger.error("error:", e.fillInStackTrace());
		}
		return resultObject;
	}

	/**
	 * 新线程处理，直接拿到订单锁处理完成后退出
	 *
	 */
	public class CloseDelayThread implements Runnable {
		private String orderNo;
		private MinerOrderService minerOrderService;

		public void run() {
			try {
				// 提前赎回理财产品需要支付违约金
				MinerOrder order = minerOrderService.findByOrder_no(orderNo);

				Miner miner = minerService.findById(order.getMinerId());

				// 取时间
				Date date_now = new Date();
				double last_days = daysBetween(order.getCreate_time(), date_now);
				if ("1".equals(order.getState()) && last_days >= miner.getCycle_close()) {

					// 解锁，不扣违约金
					double default_money = 0;
					order.setState("2");
					order.setProfit(Arith.sub(order.getProfit(), default_money));
					this.minerOrderService.saveClose(order);
				}

			} catch (Exception e) {
				logger.error("error:", e);
			}

		}

		public CloseDelayThread(String orderNo, MinerOrderService minerOrderService) {
			this.orderNo = orderNo;
			this.minerOrderService = minerOrderService;
		}

	}

	protected Map<String, Object> bulidData(MinerOrder order) throws ParseException {
		Miner miner = new Miner();
		miner = minerService.findById(order.getMinerId());

		DecimalFormat df = new DecimalFormat("#.##");
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("miner_name", miner.getName());
		map.put("miner_name_en", miner.getName_en());
		map.put("miner_name_cn", miner.getName_cn());
		Double miner_test_profit = sysparaService.find("miner_test_profit").getDouble();

		map.put("daily_rate", miner.getTest() ? miner_test_profit : miner.getDaily_rate());
		map.put("create_timeStr", DateUtils.format(order.getCreate_time(), DateUtils.DF_yyyyMMddHHmmss));
		map.put("close_timeStr", DateUtils.format(order.getClose_time(), DateUtils.DF_yyyyMMddHHmmss));
		map.put("stop_timeStr", miner.getTest() ? DateUtils.format(order.getStop_time(), DateUtils.DF_yyyyMMdd) : null);
		map.put("earn_timeStr", DateUtils.format(order.getEarn_time(), DateUtils.DF_yyyyMMdd));
		Date date_now = new Date();// 取时间
		int daysBetween = order.getStop_time() == null ? 0 : daysBetween(date_now, order.getStop_time());
		daysBetween = daysBetween < 0 ? 0 : daysBetween;
		map.put("days", daysBetween);
		double rate = Arith.mul(miner.getDaily_rate(), 0.01);
		map.put("profit_may", miner.getTest() ? String.valueOf(Arith.mul(miner_test_profit, miner.getCycle()))
				: String.valueOf(Arith.mul(order.getAmount(), Arith.mul(rate, 30))));
		map.put("order_no", order.getOrder_no());
		map.put("amount", order.getAmount());
		map.put("cycle", miner.getCycle());
		map.put("id", order.getId());

		map.put("state", order.getState());
		map.put("profit", order.getProfit());

		int cycle = miner.getTest() ? miner.getCycle() : miner.getCycle_close();
		map.put("cycle", cycle);
		double all_rate = Arith.mul(30, miner.getDaily_rate());
		map.put("all_rate", miner.getTest() ? Arith.mul(miner_test_profit, miner.getCycle()) : df.format(all_rate));

		map.put("test", miner.getTest());

		if ("1".equals(order.getState())) {

			map.put("profit", order.getProfit());

			map.put("default_amount", 0);
			map.put("principal_amount", 0);
		}
		if ("2".equals(order.getState())) {
			map.put("profit", 0);
			map.put("default_amount", 0);
			map.put("principal_amount", df.format(order.getAmount()));
		}
		if ("0".equals(order.getState())) {// 正常
			map.put("profit", order.getProfit());
			map.put("default_amount", 0);
			map.put("principal_amount", df.format(order.getAmount()));
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
