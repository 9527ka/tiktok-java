package project.follow.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.text.DecimalFormat;
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
import kernel.sessiontoken.SessionTokenService;
import kernel.util.Arith;
import kernel.util.DateUtils;
import kernel.util.JsonUtils;
import kernel.util.StringUtils;
import kernel.web.BaseAction;
import kernel.web.ResultObject;
import project.Constants;
import project.contract.ContractOrderService;
import project.follow.Trader;
import project.follow.TraderFollowUser;
import project.follow.TraderFollowUserOrderService;
import project.follow.TraderFollowUserService;
import project.follow.TraderOrderService;
import project.follow.TraderService;

/**
 * 交易员api接口
 */
public class TraderAction extends BaseAction {
	/**
	 * 
	 */
	private static final long serialVersionUID = -816097638237895124L;
	/**
	 * 交易员api接口
	 */
	private static Log logger = LogFactory.getLog(TraderAction.class);
	private TraderService traderService;
	private TraderFollowUserService traderFollowUserService;
	private TraderFollowUserOrderService traderFollowUserOrderService;

	private TraderOrderService traderOrderService;

	private ContractOrderService contractOrderService;

	private String session_token;

	private SessionTokenService sessionTokenService;

	private String id;

	private String orderBy_type;

	private String symbol;

	/**
	 * 查询类型。 orders 当前委托单 ，hisorders 历史委托单 ，user 跟随者
	 */
	private String type;

	private String name;

	private int page_no;

//	public String list() throws IOException {
//		HttpServletResponse response = ServletActionContext.getResponse();
//		response.setContentType("application/json;charset=UTF-8");
//		response.setHeader("Access-Control-Allow-Origin", "*");
//		ResultObject resultObject = new ResultObject();
//		PrintWriter out = response.getWriter();
//		List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
//
//		try {
//			if (!StringUtils.isEmptyString(this.name)) {
//				this.name = URLDecoder.decode(this.name, "utf-8");
//			}
//
//			data = this.traderService.getPaged(this.page_no, 10, this.name, "1", this.orderBy_type);
//			if (data != null) {
//				for (int i = 0; i < data.size(); i++) {
//					Map<String, Object> map = data.get(i);
//					if (this.getLoginPartyId() != null) {
//						TraderFollowUser user = this.traderFollowUserService.findByPartyIdAndTrader_partyId(
//								this.getLoginPartyId().toString(), map.get("partyId").toString());
//						if (user != null) {
//							/**
//							 * 1跟随，2未跟随
//							 */
//							map.put("follow_state", "1");
//							map.remove("partyId");
//						} else {
//							map.put("follow_state", "2");
//							map.remove("partyId");
//						}
//
//					} else {
//						map.put("follow_state", "2");
//						map.remove("partyId");
//					}
//
//				}
//
//			}
//
//			// List<Trader> data = traderService.findAllState_1();
//
////			resultObject.setData(bulidData(data));
//			resultObject.setData(data);
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
//
//	public String get() throws IOException {
//		HttpServletResponse response = ServletActionContext.getResponse();
//		response.setContentType("application/json;charset=UTF-8");
//		response.setHeader("Access-Control-Allow-Origin", "*");
//		ResultObject resultObject = new ResultObject();
//		PrintWriter out = response.getWriter();
//
//		try {
//
//			Trader data = traderService.findById(id);
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

	private Map<String, Object> bulidData(Trader entity) throws ParseException {
		DecimalFormat df2 = new DecimalFormat("#.##");
		List<Map<String, Object>> trader_order = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> trader_user = new ArrayList<Map<String, Object>>();
		/**
		 * 跟随用户
		 */
		if ("user".equals(this.type)) {
			trader_user = traderFollowUserService.getPaged(page_no, 10, entity.getPartyId().toString(), "profit");
			if (trader_user != null) {
				for (Map<String, Object> follow_user : trader_user) {
					String username = follow_user.get("name").toString();
					char[] username_char = username.toCharArray();
					StringBuffer sb = new StringBuffer();
					if (username_char.length > 6) {
						for (int i = 0; i < username_char.length; i++) {
							String aa = "";
							if (i < 6) {
								aa = String.valueOf(username_char[i]).replaceAll(String.valueOf(username_char[i]), "*");
							} else {
								aa = String.valueOf(username_char[i]);
							}
							sb.append(aa);
						}
					} else {
						for (int i = 0; i < username_char.length; i++) {
							String aa = "";
							if (i < username_char.length) {
								aa = String.valueOf(username_char[i]).replaceAll(String.valueOf(username_char[i]), "*");
							} else {
								aa = String.valueOf(username_char[i]);
							}
							sb.append(aa);
						}
					}
					follow_user.put("name", sb);
				}
			}
		} else {
			/**
			 * 查询类型。 orders 当前委托单 ，hisorders 历史委托单 ，user 跟随者
			 */
			if ("hisorders".equals(type)) {
				trader_order = this.traderOrderService.getPaged(page_no, 10, entity.getPartyId().toString());

			} else {
				trader_order = this.contractOrderService.getPaged(page_no, 10, entity.getPartyId().toString(), symbol,
						type);
			}

		}
		if (trader_order != null) {
			for (Map<String, Object> order : trader_order) {
				order.put("create_time",
						DateUtils.format(
								DateUtils.toDate(order.get("create_time").toString(), DateUtils.DF_yyyyMMddHHmmss),
								"MM-dd HH:mm:ss"));

				if (order.get("close_time") != null && !"".equals(order.get("close_time"))) {
					order.put("close_time",
							DateUtils.format(
									DateUtils.toDate(order.get("close_time").toString(), DateUtils.DF_yyyyMMddHHmmss),
									"MM-dd HH:mm:ss"));
				} else {
					order.put("close_time", "");
				}
			}

		}

		Map<String, Object> map = new HashMap<String, Object>();
		String path = Constants.WEB_URL + "/public/showimg!showImg.action?imagePath=" + entity.getImg();
		map.put("img", path);
		map.put("trader_order", trader_order);
		map.put("trader_user", trader_user);
		map.put("id", entity.getId());

		map.put("name", entity.getName());
		map.put("remarks", entity.getRemarks());
		/**
		 * 累计金额order_amount
		 */
		map.put("order_amount", df2.format(Arith.add(entity.getOrder_amount(), entity.getDeviation_order_amount())));

//		map.put("symbol_name", "BTC/USDT;ETH/USDT");
		map.put("profit", df2.format(Arith.add(entity.getProfit(), entity.getDeviation_profit())));

		map.put("order_profit", (int) Arith.add(entity.getOrder_profit(), entity.getDeviation_order_profit()));

		map.put("order_loss", (int) Arith.add(entity.getOrder_loss(), entity.getDeviation_order_loss()));
		map.put("order_sum", (int) Arith.add(Arith.add(entity.getOrder_profit(), entity.getOrder_loss()),
				Arith.add(entity.getDeviation_order_profit(), entity.getDeviation_order_loss())));
		map.put("follower_sum", (int) Arith.add(entity.getFollower_sum(), entity.getDeviation_follower_sum()));

		map.put("follower_now", (int) Arith.add(entity.getFollower_now(), entity.getDeviation_follower_now()));
		/**
		 * 累计收益率
		 */
		map.put("profit_ratio", df2.format(Arith.add(Arith.mul(entity.getDeviation_profit_ratio(), 100),
				Arith.mul(entity.getProfit_ratio(), 100))));

		map.put("profit_share_ratio", Arith.mul(entity.getProfit_share_ratio(), 100));
		map.put("follower_max", entity.getFollower_max());
		Date date_now = new Date();// 取时间
		int days = daysBetween(entity.getCreate_time(), date_now);
		if (days < 0) {
			days = 0;
		}
		map.put("create_days", days);

		String partyId = this.getLoginPartyId();
		if (!StringUtils.isNullOrEmpty(partyId)) {
			session_token = sessionTokenService.savePut(partyId);
			map.put("session_token", session_token);
		}

		if (this.getLoginPartyId() != null) {
			TraderFollowUser user = this.traderFollowUserService
					.findByPartyIdAndTrader_partyId(this.getLoginPartyId().toString(), entity.getPartyId().toString());
			if (user != null) {

				map.put("follow_volume", user.getVolume());
				map.put("follow_volume_max", user.getVolume_max());
				/**
				 * 跟单固定张数/固定比例---选择 1,固定张数，2，固定比例
				 */
				map.put("follow_type", user.getFollow_type());
				map.put("follow_state", "1");
			} else {

				map.put("follow_state", "2");
			}

		} else {
			map.put("follow_state", "2");
			map.remove("partyId");
		}
		map.put("follow_volumn_min", entity.getFollow_volumn_min());
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

	public String getSession_token() {
		return session_token;
	}

	public void setSession_token(String session_token) {
		this.session_token = session_token;
	}

	public void setSessionTokenService(SessionTokenService sessionTokenService) {
		this.sessionTokenService = sessionTokenService;
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

	public void setTraderFollowUserService(TraderFollowUserService traderFollowUserService) {
		this.traderFollowUserService = traderFollowUserService;
	}

	public void setTraderFollowUserOrderService(TraderFollowUserOrderService traderFollowUserOrderService) {
		this.traderFollowUserOrderService = traderFollowUserOrderService;
	}

	public void setTraderOrderService(TraderOrderService traderOrderService) {
		this.traderOrderService = traderOrderService;
	}

}
