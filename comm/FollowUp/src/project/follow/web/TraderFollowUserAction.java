package project.follow.web;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
// import org.apache.struts2.ServletActionContext;

import kernel.exception.BusinessException;
import kernel.sessiontoken.SessionTokenService;
import kernel.util.JsonUtils;
import kernel.web.BaseAction;
import kernel.web.ResultObject;
import project.follow.Trader;
import project.follow.TraderFollowUser;
import project.follow.TraderFollowUserService;
import project.follow.TraderService;
import project.party.PartyService;
import project.party.model.Party;

/**
 * 用户准备跟随交易员api接口
 */
public class TraderFollowUserAction extends BaseAction {
	private static final long serialVersionUID = 623416500874018208L;
	/**
	 * 交易员api接口
	 */
	private static Log logger = LogFactory.getLog(TraderFollowUserAction.class);
	private TraderFollowUserService traderFollowUserService;
	private TraderService traderService;
	private PartyService partyService;

	private String session_token;
	private SessionTokenService sessionTokenService;

	private String trader_id;

	private String trader_name;

	/**
	 * 跟随购买品种 symbol
	 */
	private String symbol;
	/**
	 * 跟单固定张数/固定比例---选择 1,固定张数，2，固定比例
	 */
	private String follow_type;

	/**
	 * 跟单张数或比例---具体值
	 */
	private double volume;
	/**
	 * 最大持仓张数
	 */
	private double volume_max;
	/**
	 * 止盈百分比
	 */
	private double stop_profit;
	/**
	 * 止损百分比
	 */
	private double stop_loss;

	/**
	 * 用户跟随交易员
	 */
//	public String saveCreate() throws IOException {
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
//		try {
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
//
//			Party party = this.partyService.cachePartyBy(partyId, false);
//			if (!party.getEnabled()) {
//				throw new BusinessException(506, error);
//			}
////			if (!party.getKyc_authority()) {
////				resultObject.setCode("401");
////				resultObject.setMsg(error);
////				this.result = JsonUtils.getJsonString(resultObject);
////				out.println(this.result);
////				return null;
////			}
//			TraderFollowUser entity = new TraderFollowUser();
//			entity.setPartyId(partyId);
//			entity.setUsername(party.getUsername());
//			/**
//			 * 跟单固定张数/固定比例---选择 1,固定张数，2，固定比例
//			 */
//			entity.setFollow_type(follow_type);
//			entity.setStop_loss(stop_loss);
//			entity.setStop_profit(stop_profit);
//			entity.setSymbol(symbol);
//			entity.setVolume(volume);
//			entity.setVolume_max(volume_max);
//			/**
//			 * 状态 是否还在跟随状态 1,跟随，2，取消跟随
//			 */
//			entity.setState("1");
//
//			this.traderFollowUserService.save(entity, trader_id);
//			resultObject.setCode("0");
//		} catch (BusinessException e) {
//			resultObject.setCode(e.getSign() + "");
//			resultObject.setMsg(e.getMessage());
//		} catch (Exception e) {
//			resultObject.setCode("1");
//			resultObject.setMsg("程序错误");
//			logger.error("error:", e.fillInStackTrace());
//		} finally {
//
//		}
//		this.result = JsonUtils.getJsonString(resultObject);
//		out.println(this.result);
//		return null;
//	}
//
//	public String changeFollow() throws IOException {
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
//		try {
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
//
//			Party party = this.partyService.cachePartyBy(partyId, false);
//			if (!party.getEnabled()) {
//				throw new BusinessException(506, error);
//			}
////			if (!party.getKyc_authority()) {
////				resultObject.setCode("401");
////				resultObject.setMsg(error);
////				this.result = JsonUtils.getJsonString(resultObject);
////				out.println(this.result);
////				return null;
////			}
//			Trader trader = this.traderService.findById(trader_id);
//			TraderFollowUser entity = this.traderFollowUserService.findByPartyIdAndTrader_partyId(partyId,
//					trader.getPartyId().toString());
//			/**
//			 * 跟单固定张数/固定比例---选择 1,固定张数，2，固定比例
//			 */
//			entity.setFollow_type(follow_type);
//			entity.setStop_loss(stop_loss);
//			entity.setStop_profit(stop_profit);
//			entity.setSymbol(symbol);
//			entity.setVolume(volume);
//			entity.setVolume_max(volume_max);
//			/**
//			 * 状态 是否还在跟随状态 1,跟随，2，取消跟随
//			 */
//			entity.setState("1");
//
//			this.traderFollowUserService.update(entity);
//			resultObject.setCode("0");
//		} catch (BusinessException e) {
//			resultObject.setCode(e.getSign() + "");
//			resultObject.setMsg(e.getMessage());
//		} catch (Exception e) {
//			resultObject.setCode("1");
//			resultObject.setMsg("程序错误");
//			logger.error("error:", e.fillInStackTrace());
//		} finally {
//
//		}
//		this.result = JsonUtils.getJsonString(resultObject);
//		out.println(this.result);
//		return null;
//	}
//
//	/**
//	 * 取消跟随
//	 */
//	public String cancelFollow() throws IOException {
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
//		try {
//			Trader trader = this.traderService.findById(this.trader_id);
//			TraderFollowUser traderFoolowUser = this.traderFollowUserService.findByPartyIdAndTrader_partyId(partyId,
//					trader.getPartyId().toString());
//			if (traderFoolowUser != null) {
//				this.traderFollowUserService.deleteCancel(traderFoolowUser.getId().toString());
//			}
//
//			resultObject.setCode("0");
//		} catch (BusinessException e) {
//			resultObject.setCode(e.getSign() + "");
//			resultObject.setMsg(e.getMessage());
//		} catch (Exception e) {
//			resultObject.setCode("1");
//			resultObject.setMsg("程序错误");
//			logger.error("error:", e.fillInStackTrace());
//		} finally {
//
//		}
//		this.result = JsonUtils.getJsonString(resultObject);
//		out.println(this.result);
//		return null;
//	}

	public String getTrader_id() {
		return trader_id;
	}

	public void setTrader_id(String trader_id) {
		this.trader_id = trader_id;
	}

	public String getTrader_name() {
		return trader_name;
	}

	public void setTrader_name(String trader_name) {
		this.trader_name = trader_name;
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public String getFollow_type() {
		return follow_type;
	}

	public void setFollow_type(String follow_type) {
		this.follow_type = follow_type;
	}

	public double getVolume() {
		return volume;
	}

	public void setVolume(double volume) {
		this.volume = volume;
	}

	public double getVolume_max() {
		return volume_max;
	}

	public void setVolume_max(double volume_max) {
		this.volume_max = volume_max;
	}

	public double getStop_profit() {
		return stop_profit;
	}

	public void setStop_profit(double stop_profit) {
		this.stop_profit = stop_profit;
	}

	public double getStop_loss() {
		return stop_loss;
	}

	public void setStop_loss(double stop_loss) {
		this.stop_loss = stop_loss;
	}

	public void setTraderFollowUserService(TraderFollowUserService traderFollowUserService) {
		this.traderFollowUserService = traderFollowUserService;
	}

	public void setPartyService(PartyService partyService) {
		this.partyService = partyService;
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

	public void setTraderService(TraderService traderService) {
		this.traderService = traderService;
	}

}
