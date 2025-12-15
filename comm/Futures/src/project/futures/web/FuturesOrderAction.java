package project.futures.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
// import org.apache.struts2.ServletActionContext;
import org.springframework.beans.BeanUtils;

import kernel.exception.BusinessException;
import kernel.sessiontoken.SessionTokenService;
import kernel.util.JsonUtils;
import kernel.util.StringUtils;
import kernel.util.ThreadUtils;
import kernel.web.BaseAction;
import kernel.web.ResultObject;
import project.futures.FuturesLock;
import project.futures.FuturesOrder;
import project.futures.FuturesOrderLocalService;
import project.futures.FuturesOrderService;
import project.futures.FuturesPara;
import project.futures.FuturesParaService;
import project.party.PartyService;
import project.party.model.Party;
import project.wallet.Wallet;
import project.wallet.WalletService;

public class FuturesOrderAction extends BaseAction {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7282461392959209832L;
	private static Log logger = LogFactory.getLog(FuturesOrderAction.class);
	private String symbol;

	/**
	 * "buy":多 "sell":空
	 */
	private String direction;

	/**
	 * 委托数量(张)
	 */
	private double volume;

	private String order_no;

	private int page_no;

	/**
	 * 交割合约参数
	 */
	private String para_id;

	private WalletService walletService;
	private PartyService partyService;
	private FuturesOrderService futuresOrderService;
	private FuturesOrderLocalService futuresOrderLocalService;

	private FuturesParaService futuresParaService;
	private Map<String, Object> session = new HashMap<String, Object>();
	private String session_token;
	private final static Object obj = new Object();

	private SessionTokenService sessionTokenService;

	/**
	 * 开仓页面订单类型
	 */
	private String type;

	/**
	 * 开仓页面参数
	 */
//	public String openview() throws IOException {
//		HttpServletResponse response = ServletActionContext.getResponse();
//		response.setContentType("application/json;charset=UTF-8");
//		response.setHeader("Access-Control-Allow-Origin", "*");
//		ResultObject resultObject = new ResultObject();
//		PrintWriter out = response.getWriter();
//		try {
//			Map<String, Object> data = new HashMap<String, Object>();
////			List<FuturesPara> futuresPara = new ArrayList<FuturesPara>();
////			for (FuturesPara obj : this.futuresParaService.cacheGetBySymbolSort(symbol)) {
////				FuturesPara para = new FuturesPara();
////				BeanUtils.copyProperties(obj, para);
////				para.setProfit_ratio(Arith.mul(para.getProfit_ratio(), 100));// 转换格式显示,
////			}
//			List<Map> futuresPara = new ArrayList<Map>();
//			for (FuturesPara obj : this.futuresParaService.cacheGetBySymbolSort(symbol)) {
//				FuturesPara para = new FuturesPara();
//				BeanUtils.copyProperties(obj, para);
//				futuresPara.add(this.futuresParaService.bulidOne(para));
//			}
//
//			data.put("para", futuresPara);
//			String partyId = this.getLoginPartyId();
//			if (!StringUtils.isNullOrEmpty(partyId) && futuresPara != null) {
//				Wallet wallet = walletService.saveWalletByPartyId(this.getLoginPartyId());
//				/*
//				 * 账户剩余资 金
//				 */
//				DecimalFormat df = new DecimalFormat("#");
//				data.put("volume", df.format(wallet.getMoney()));
//				session_token = sessionTokenService.savePut(partyId);
//				data.put("session_token", session_token);
//			} else {
//				data.put("volume", 0);
//			}
//
//			resultObject.setData(data);
//		} catch (BusinessException e) {
//			resultObject.setCode("1");
//			resultObject.setMsg(e.getMessage());
//		} catch (Exception e) {
//			resultObject.setCode("1");
//			resultObject.setMsg("参数错误");
//			logger.error("error:", e);
//		}
//		this.result = JsonUtils.getJsonString(resultObject);
//		out.println(this.result);
//		return null;
//	}

	/**
	 * 开仓
	 * 
	 */
//	public String open() throws IOException {
//		HttpServletResponse response = ServletActionContext.getResponse();
//		response.setContentType("application/json;charset=UTF-8");
//		response.setHeader("Access-Control-Allow-Origin", "*");
//		ResultObject resultObject = new ResultObject();
//		resultObject = readSecurityContextFromSession(resultObject);
//		PrintWriter out = response.getWriter();
//		if (!"0".equals(resultObject.getCode())) {
//			this.result = JsonUtils.getJsonString(resultObject);
//			out.println(this.result);
//			return null;
//		}
//		String partyId = this.getLoginPartyId();
//		boolean lock = false;
//		try {
//			Map<String, String> data = new HashMap<String, String>();
//			if (!FuturesLock.add(partyId)) {
//				throw new BusinessException(1, "请稍后再试");
//			}
//			lock = true;
//
//			Object object = this.sessionTokenService.cacheGet(session_token);
//			this.sessionTokenService.del(session_token);
//			if ((object == null) || (!this.getLoginPartyId().equals((String) object))) {
//				resultObject.setCode("1");
//				resultObject.setMsg("请稍后再试");
//				this.result = JsonUtils.getJsonString(resultObject);
//				out.println(this.result);
//				return null;
//			}
//			Party party = this.partyService.cachePartyBy(partyId, false);
//			if (!party.getEnabled()) {
//				resultObject.setCode("506");
//				resultObject.setMsg(error);
//				this.result = JsonUtils.getJsonString(resultObject);
//				out.println(this.result);
//				return null;
//			}
////			if (!party.getKyc_authority()) {
////				resultObject.setCode("401");
////				resultObject.setMsg(error);
////				this.result = JsonUtils.getJsonString(resultObject);
////				out.println(this.result);
////				return null;
////			}
//			FuturesOrder order = new FuturesOrder();
//			order.setPartyId(partyId);
//			order.setSymbol(symbol);
//			order.setDirection(direction);
//			order.setVolume(volume);
//
//			order = this.futuresOrderService.saveOpen(order, para_id);
//
////			this.futuresOrderService.pushAsynRecom(order);
//			data.put("order_no", order.getOrder_no());
//			resultObject.setData(data);
//		} catch (BusinessException e) {
//			resultObject.setCode("1");
//			resultObject.setMsg(e.getMessage());
//		} catch (Exception e) {
//			resultObject.setCode("1");
//			resultObject.setMsg("程序错误");
//			logger.error("error:", e);
//		} finally {
//			if (lock) {
//				ThreadUtils.sleep(100);
//				FuturesLock.remove(partyId);
//			}
//
//		}
//		this.result = JsonUtils.getJsonString(resultObject);
//		out.println(this.result);
//		return null;
//	}

	/**
	 * 查询交割持仓列表
	 * 
	 * @return
	 * @throws IOException
	 */
//	public String list() throws IOException {
//		HttpServletResponse response = ServletActionContext.getResponse();
//		response.setContentType("application/json;charset=UTF-8");
//		response.setHeader("Access-Control-Allow-Origin", "*");
//		ResultObject resultObject = new ResultObject();
//		resultObject = readSecurityContextFromSession(resultObject);
//		PrintWriter out = response.getWriter();
//		List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
//		if (!"0".equals(resultObject.getCode())) {
//			resultObject.setCode("0");
//			resultObject.setData(data);
//			this.result = JsonUtils.getJsonString(resultObject);
//			out.println(this.result);
//			return null;
//		}
//
//		try {
//			String loginPartyId = this.getLoginPartyId();
//			if ("orders".equals(type)) {
//				List<FuturesOrder> list = futuresOrderService.cacheSubmitted();
//				List<FuturesOrder> result = new LinkedList<FuturesOrder>();
//
//				if (StringUtils.isNotEmpty(loginPartyId)) {// 登录才添加到列表
//					for (FuturesOrder order : list) {
//						if (StringUtils.isNotEmpty(symbol)) {// 有种类，满足登录人和种类 添加
//							if (symbol.equals(order.getSymbol()) && loginPartyId.equals(order.getPartyId()))
//								result.add(order);
//						} else {
//							if (loginPartyId.equals(order.getPartyId()))
//								result.add(order);
//						}
//					}
//				}
//
//				data = futuresOrderService.bulidData(result);
//
//			} else if ("hisorders".equals(type)) {
//				List<FuturesOrder> list = this.futuresOrderService
//						.getPaged(page_no, 10, this.getLoginPartyId(), symbol, type).getElements();
//				data = futuresOrderService.bulidData(list);
//			}
//
//		} catch (Exception e) {
//			resultObject.setCode("1");
//			resultObject.setMsg("参数错误");
//			logger.error("error:", e);
//		}
//
//		resultObject.setData(data);
//		this.result = JsonUtils.getJsonString(resultObject);
//		out.println(this.result);
//		return null;
//	}
//
//	/**
//	 * 查询交割持仓详情
//	 *
//	 * @return
//	 * @throws IOException
//	 */
//	public String get() throws IOException {
//		HttpServletResponse response = ServletActionContext.getResponse();
//		response.setContentType("application/json;charset=UTF-8");
//		response.setHeader("Access-Control-Allow-Origin", "*");
//		ResultObject resultObject = new ResultObject();
//		resultObject = readSecurityContextFromSession(resultObject);
//		PrintWriter out = response.getWriter();
//		if (!"0".equals(resultObject.getCode())) {
//			resultObject.setCode("0");
//			this.result = JsonUtils.getJsonString(resultObject);
//			out.println(this.result);
//			return null;
//		}
//
//		try {
//
//			FuturesOrder data = this.futuresOrderLocalService.cacheByOrderNo(order_no);
//			resultObject.setData(futuresOrderLocalService.bulidOne(data));
//		} catch (Exception e) {
//			resultObject.setCode("1");
//			resultObject.setMsg("参数错误");
//			logger.error("error:", e);
//		}
//
//		this.result = JsonUtils.getJsonString(resultObject);
//		out.println(this.result);
//		return null;
//	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public void setOrder_no(String order_no) {
		this.order_no = order_no;
	}

	public void setPage_no(int page_no) {
		this.page_no = page_no;
	}

	public void setDirection(String direction) {
		this.direction = direction;
	}

	public void setPara_id(String para_id) {
		this.para_id = para_id;
	}

	public void setWalletService(WalletService walletService) {
		this.walletService = walletService;
	}

	public void setFuturesOrderService(FuturesOrderService futuresOrderService) {
		this.futuresOrderService = futuresOrderService;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setFuturesParaService(FuturesParaService futuresParaService) {
		this.futuresParaService = futuresParaService;
	}

	public String getSession_token() {
		return session_token;
	}

	public void setSession_token(String session_token) {
		this.session_token = session_token;
	}

	public void setSession(Map<String, Object> session) {
		this.session = session;
	}

	public void setSessionTokenService(SessionTokenService sessionTokenService) {
		this.sessionTokenService = sessionTokenService;
	}

	public void setPartyService(PartyService partyService) {
		this.partyService = partyService;
	}

	public void setFuturesOrderLocalService(FuturesOrderLocalService futuresOrderLocalService) {
		this.futuresOrderLocalService = futuresOrderLocalService;
	}

	public void setVolume(double volume) {
		this.volume = volume;
	}

}
