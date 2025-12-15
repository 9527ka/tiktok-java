package project.follow.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
// import org.apache.struts2.ServletActionContext;

import kernel.exception.BusinessException;
import kernel.util.JsonUtils;
import kernel.web.BaseAction;
import kernel.web.ResultObject;
import project.Constants;
import project.contract.ContractOrderService;
import project.follow.Trader;
import project.follow.TraderFollowUser;
import project.follow.TraderFollowUserOrderService;
import project.follow.TraderFollowUserService;
import project.follow.TraderService;
import project.follow.TraderUser;
import project.follow.TraderUserService;

/**
 * 用户api接口，累计数据
 */
public class TraderUserAction extends BaseAction {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3217562997540384508L;
	/**
	 * 交易员api接口
	 */
	private static Log logger = LogFactory.getLog(TraderUserAction.class);
	private TraderService traderService;
	private TraderUserService traderUserService;
	private TraderFollowUserService traderFollowUserService;
	private TraderFollowUserOrderService traderFollowUserOrderService;
	private ContractOrderService contractOrderService;
	private String id;
	private String orderBy_type;
	private String symbol;

	/**
	 * 查询类型。 orders 我的跟单 ，trader 我的交易员
	 */
	private String type;
	/**
	 * 我的跟单查询类型。 orders 当前委托单 ，hisorders 历史委托单
	 */
	private String type_order;

	private String name;

	private int page_no;

//	public String get() throws IOException {
//		HttpServletResponse response = ServletActionContext.getResponse();
//		response.setContentType("application/json;charset=UTF-8");
//		response.setHeader("Access-Control-Allow-Origin", "*");
//		ResultObject resultObject = new ResultObject();
//		PrintWriter out = response.getWriter();
//		resultObject = readSecurityContextFromSession(resultObject);
//		if (!"0".equals(resultObject.getCode())) {
//			this.result = JsonUtils.getJsonString(resultObject);
//			out.println(this.result);
//			return null;
//		}
//		try {
//
//			TraderUser data = traderUserService.saveTraderUserByPartyId(this.getLoginPartyId());
//
//			resultObject.setData(bulidData(data));
//			resultObject.setCode("0");
//		} catch (BusinessException e) {
//			resultObject.setCode("1");
//			resultObject.setMsg(e.getMessage());
//		} catch (Exception e) {
//			resultObject.setCode("1");
//			resultObject.setMsg("程序错误");
//			logger.error("error:", e);
//		}
//
//		this.result = JsonUtils.getJsonString(resultObject);
//		out.println(this.result);
//		return null;
//
//	}

	private Map<String, Object> bulidData(TraderUser entity) throws ParseException {

		List<Map<String, Object>> trader_order = new ArrayList<Map<String, Object>>();
		List<TraderFollowUser> follow_users = new ArrayList<TraderFollowUser>();

		List<Map<String, Object>> follow_traders = new ArrayList<Map<String, Object>>();
		follow_users = traderFollowUserService.findByPartyId(entity.getPartyId().toString());
		double folllow_trader_num = 0;
		if (follow_users != null) {
			folllow_trader_num = follow_users.size();
		}
		/**
		 * 跟随的交易员
		 */
		if ("trader".equals(type)) {

			if (follow_users != null) {
				for (TraderFollowUser user : follow_users) {
					Trader trader = traderService.findByPartyId(user.getTrader_partyId().toString());
					Map<String, Object> follow_trader = new HashMap<String, Object>();
					follow_trader.put("profit", user.getProfit());
					follow_trader.put("amount_sum", user.getAmount_sum());
					follow_trader.put("username", trader.getName());
					String path = Constants.WEB_URL + "/public/showimg!showImg.action?imagePath=" + trader.getImg();
					follow_trader.put("img", path);
					follow_trader.put("id", trader.getId());
					follow_trader.put("follow_state", "1");
					follow_traders.add(follow_trader);
				}
			}
		}
		if ("orders".equals(type)) {

			trader_order = this.traderFollowUserOrderService.getPaged(page_no, 10, entity.getPartyId().toString());
		}

		Map<String, Object> map = new HashMap<String, Object>();

		map.put("orders", trader_order);
		map.put("traders", follow_traders);
		map.put("folllow_trader_num", folllow_trader_num);

		map.put("id", entity.getId());

		map.put("name", entity.getName());
		map.put("profit", entity.getProfit());
		map.put("amount_sum", entity.getAmount_sum());
		Date date_now = new Date();// 取时间
		int days = daysBetween(entity.getCreate_time(), date_now);
		if (days < 0) {
			days = 0;
		}
		map.put("create_days", days);

		return map;

	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setTraderService(TraderService traderService) {
		this.traderService = traderService;
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

	public int getPage_no() {
		return page_no;
	}

	public void setPage_no(int page_no) {
		this.page_no = page_no;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getOrderBy_type() {
		return orderBy_type;
	}

	public void setOrderBy_type(String orderBy_type) {
		this.orderBy_type = orderBy_type;
	}

	public void setContractOrderService(ContractOrderService contractOrderService) {
		this.contractOrderService = contractOrderService;
	}

	public String getSymbol() {
		return symbol;
	}

	public String getType() {
		return type;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setTraderUserService(TraderUserService traderUserService) {
		this.traderUserService = traderUserService;
	}

	public void setTraderFollowUserService(TraderFollowUserService traderFollowUserService) {
		this.traderFollowUserService = traderFollowUserService;
	}

	public String getType_order() {
		return type_order;
	}

	public void setType_order(String type_order) {
		this.type_order = type_order;
	}

	public void setTraderFollowUserOrderService(TraderFollowUserOrderService traderFollowUserOrderService) {
		this.traderFollowUserOrderService = traderFollowUserOrderService;
	}

}
