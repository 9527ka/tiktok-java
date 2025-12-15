package project.web.api;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
import project.data.DataService;
import project.data.model.Realtime;
import project.exchange.ExchangeApplyOrder;
import project.exchange.ExchangeApplyOrderService;
import project.exchange.ExchangeLock;
import project.item.ItemService;
import project.party.PartyService;
import project.party.model.Party;
import project.syspara.SysparaService;
import project.user.kyc.Kyc;
import project.user.kyc.KycService;
import project.wallet.Wallet;
import project.wallet.WalletExtend;
import project.wallet.WalletService;

/**
 * 兑换
 * 币币交易 低买高卖
 *
 */
@RestController
@CrossOrigin
public class ExchangeApplyOrderController extends BaseAction {

	private Logger logger = LogManager.getLogger(ExchangeApplyOrderController.class);
	
	@Autowired
	private ExchangeApplyOrderService exchangeApplyOrderService;
	@Autowired
	private SysparaService sysparaService;
	@Autowired
	private DataService dataService;
	@Autowired
	private PartyService partyService;
	@Autowired
	private SessionTokenService sessionTokenService;
	@Autowired
	private WalletService walletService;
	@Autowired
	private KycService kycService;
	@Autowired
	private ItemService itemService;
	
	private final String action = "/api/exchangeapplyorder!";

	/**
	 * 兑换币 如果是使用usdt兑换其他币种，则直接执行正常买币open流程 如果是其他币种--》usdt 则是直接卖币流程
	 * 如果是其他币种到另一个币种，则需要先卖出，然后再买入
	 * 
	 * 兑换接口
	 */
	@RequestMapping(action + "buy_and_sell.action")
	public Object buy_and_sell(HttpServletRequest request) {

		ResultObject resultObject = new ResultObject();
		resultObject = readSecurityContextFromSession(resultObject);
		
		if (!"0".equals(resultObject.getCode())) {
			return resultObject;
		}
		
		String volume_temp = request.getParameter("volume");
		if (StringUtils.isNullOrEmpty(volume_temp)
				|| !StringUtils.isDouble(volume_temp) || Double.valueOf(volume_temp) <= 0) {
			throw new BusinessException("请输入正确的货币数量");
		}
		
		double volume = Double.valueOf(volume_temp);
		
		String symbol = request.getParameter("symbol");
		String symbol_to = request.getParameter("symbol_to");
		if (symbol.equals(symbol_to)) {
			throw new BusinessException("请选择正确的币种");
		}
		
		String session_token = request.getParameter("session_token");
		
		String partyId = this.getLoginPartyId();
		boolean lock = false;
		try {
			
			if (!ExchangeLock.add(partyId)) {
				logger.error("ExchangeLock{}", partyId);
				System.out.println("ExchangeLock " + partyId);
				throw new BusinessException(1, "请稍后再试");
			}
			lock = true;
			
			Object object = this.sessionTokenService.cacheGet(session_token);
			this.sessionTokenService.delete(session_token);
			if ((object == null) || (!this.getLoginPartyId().equals((String) object))) {
				logger.error("sessionToken{}", object);
				System.out.println("sessionToken " + object);
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

			symbol = symbol.toLowerCase();
			symbol_to = symbol_to.toLowerCase();
			
			String relation_order_no = UUID.randomUUID().toString();
			
			// 如果是使用usdt兑换其他币种，则直接执行正常买币open流程
			if ("usdt".equals(symbol) || "usdt".equals(symbol_to)) {
				ExchangeApplyOrder order = new ExchangeApplyOrder();
				order.setPartyId(partyId);
				order.setVolume(volume);
				order.setOrder_price_type("opponent");
				order.setRelation_order_no(relation_order_no);
				
				if ("usdt".equals(symbol)) {
					order.setSymbol(symbol_to);
					order.setOffset(ExchangeApplyOrder.OFFSET_OPEN);
				} 
				// 兑换功能 目前仅有 第三方币种 --> usdt
				else if ("usdt".equals(symbol_to)) {
					order.setSymbol(symbol);
					order.setOffset(ExchangeApplyOrder.OFFSET_CLOSE);
				}
				this.exchangeApplyOrderService.saveCreate(order);
			} 
			
			else {

				// 非usdt则需要进行一次卖出，再买入
				ExchangeApplyOrder order_sell = new ExchangeApplyOrder();
				order_sell.setPartyId(partyId);
				order_sell.setSymbol(symbol);
				order_sell.setOffset(ExchangeApplyOrder.OFFSET_CLOSE);
				order_sell.setVolume(volume);
				order_sell.setOrder_price_type("opponent");
				order_sell.setRelation_order_no(relation_order_no);
				
				this.exchangeApplyOrderService.saveCreate(order_sell);
				
				List<Realtime> realtimes = this.dataService.realtime(symbol);
				double close = 1;
				if (realtimes != null && realtimes.size() > 0) {
					close = realtimes.get(0).getClose();
				}

				double sub = Arith.sub(volume,
						Arith.mul(volume, sysparaService.find("exchange_apply_order_sell_fee").getDouble()));
				double amount = Arith.mul(sub, close);

				ThreadUtils.sleep(1000);

				// 再买入币种
				ExchangeApplyOrder order_buy = new ExchangeApplyOrder();
				order_buy.setPartyId(partyId);
				order_buy.setSymbol(symbol_to);
				order_buy.setOffset(ExchangeApplyOrder.OFFSET_OPEN);
				order_buy.setVolume(amount);
				order_buy.setRelation_order_no(relation_order_no);
				order_buy.setOrder_price_type("opponent");

				this.exchangeApplyOrderService.saveCreate(order_buy);
			}

		} catch (BusinessException e) {
			resultObject.setCode("1");
			resultObject.setMsg(e.getMessage());
		} catch (Exception e) {
			resultObject.setCode("1");
			resultObject.setMsg("程序错误");
			logger.error("error:", e.fillInStackTrace());
		} finally {
			if (lock) {
				ThreadUtils.sleep(100);
				ExchangeLock.remove(partyId);
			}
		}
		return resultObject;
	}

	/**
	 * 首次进入页面，传递session_token
	 */
	@RequestMapping(action + "view.action")
	public Object view() {

		ResultObject resultObject = new ResultObject();
		resultObject = readSecurityContextFromSession(resultObject);

		if (!"0".equals(resultObject.getCode())) {
			return resultObject;
		}
		
		try {

			String partyId = getLoginPartyId();
			if (!StringUtils.isNullOrEmpty(partyId)) {
				Map<String, Object> session = new HashMap<String, Object>();
				String session_token = sessionTokenService.savePut(partyId);
				session.put("session_token", session_token);
				System.out.println("兑换设置session_token: " + session_token);
				resultObject.setData(session);
			}

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
	 * 兑换汇率
	 */
	@RequestMapping(action + "buy_and_sell_fee.action")
	public Object buy_and_sell_fee(HttpServletRequest request) {

		ResultObject resultObject = new ResultObject();
		try {
			
			// 需兑换币种
			String symbol = request.getParameter("symbol");
			// 兑换后的币种
			String symbol_to = request.getParameter("symbol_to");
			if (symbol.equals(symbol_to)) {
				throw new BusinessException("请选择正确的币种");
			}
			
			// 委托数量
			String volume_temp = request.getParameter("volume");
			if (StringUtils.isNullOrEmpty(volume_temp) 
					|| !StringUtils.isDouble(volume_temp) || Double.valueOf(volume_temp) < 0) {
				throw new BusinessException("请输入正确的兑换数量");
			}
			
			Map<String, Object> data = new HashMap<String, Object>();

			symbol = symbol.toLowerCase();
			symbol_to = symbol_to.toLowerCase();
			double buy_fee = Double.valueOf(sysparaService.find("exchange_apply_order_buy_fee").getValue());
			double sell_fee = Double.valueOf(sysparaService.find("exchange_apply_order_sell_fee").getValue());
			double volume = Double.valueOf(volume_temp);
			
			if ("usdt".equals(symbol) || "usdt".equals(symbol_to)) {
				if ("usdt".equals(symbol)) {
					// 如果是使用Usdt 则计算收益
					List<Realtime> realtime_list = this.dataService.realtime(symbol_to);
					Realtime realtime = null;
					if (realtime_list.size() > 0) {
						realtime = realtime_list.get(0);
					} else {
						throw new BusinessException("系统错误，请稍后重试");
					}
					double symbol_to_price = realtime.getClose();
					// usdt除以的数量
					data.put("get_rate", Arith.div(1, symbol_to_price));

					// 实际兑换数量= 兑换数量-手续费数量
					double fact_volume = Arith.sub(volume, Arith.mul(volume, buy_fee));
					
					// 实际价值 = 实际兑换数量 * 被兑换品种价格
					double fact_price = Arith.mul(fact_volume, 1);

					// 实际获取数量 = 实际价值 / 将要兑换的品种的价值
					data.put("get_volume", Arith.div(fact_price, symbol_to_price));
				}

				if ("usdt".equals(symbol_to)) {
					/**
					 * 如果是转成Usdt 则计算收益
					 */
					List<Realtime> realtime_list = this.dataService.realtime(symbol);
					Realtime realtime = null;
					if (realtime_list.size() > 0) {
						realtime = realtime_list.get(0);
					} else {
						throw new BusinessException("系统错误，请稍后重试");
					}
					double symbol_price = realtime.getClose();
					// 对应usdt数量
					data.put("get_rate", Arith.div(symbol_price, 1));

					// 实际兑换数量= 兑换数量-手续费数量
					double fact_volume = Arith.sub(volume, Arith.mul(volume, sell_fee));

					// 实际价值 = 实际兑换数量 * 被兑换品种价格
					double fact_price = Arith.mul(Arith.div(symbol_price, 1), fact_volume);

					// 实际获取数量 = 实际价值 / 将要兑换的品种的价值
					data.put("get_volume", Arith.div(fact_price, 1));
				}
			} else {
				double symbol_price = 0;
				double symbol_to_price = 0;

				// 获取币种最新价格
				List<Realtime> realtime_list = this.dataService.realtime(symbol);
				Realtime realtime = null;
				if (realtime_list.size() > 0) {
					realtime = realtime_list.get(0);
					symbol_price = realtime.getClose();
				} else {
					throw new BusinessException("系统错误，请稍后重试");
				}

				// 获取币种最新价格
				List<Realtime> realtime_list_to = this.dataService.realtime(symbol_to);
				Realtime realtime_to = null;
				if (realtime_list_to.size() > 0) {
					realtime_to = realtime_list_to.get(0);
					symbol_to_price = realtime_to.getClose();
				} else {
					throw new BusinessException("系统错误，请稍后重试");
				}
				if (symbol_to_price == 0) {
					symbol_to_price = 1;
				}
				if (symbol_price == 0) {
					symbol_price = 1;
				}

				data.put("get_rate", Arith.div(symbol_price, symbol_to_price));

				// 总手续费比例
				double all_fee = Arith.add(buy_fee, sell_fee);

				// 实际兑换数量= 兑换数量-手续费数量
				double fact_volume = Arith.sub(volume, Arith.mul(volume, all_fee));

				// 实际价值 = 实际兑换数量 * 被兑换品种价格
				double fact_price = Arith.mul(fact_volume, symbol_price);

				// 实际获取数量 = 实际价值 / 将要兑换的品种的价值
				data.put("get_volume", Arith.div(fact_price, symbol_to_price));
			}
			resultObject.setData(data);
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
	 * 兑换记录
	 * 委托单记录
	 * 
	 */
	@RequestMapping(action + "list.action")
	public Object list(HttpServletRequest request) {
		
		ResultObject resultObject = new ResultObject();
		resultObject = readSecurityContextFromSession(resultObject);
		List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
		if (!"0".equals(resultObject.getCode())) {
			resultObject.setData(data);
			return resultObject;
		}
		
		String symbol = request.getParameter("symbol");
		String type = request.getParameter("type");
		String isAll = request.getParameter("isAll");
		try {
			
			String page_no = request.getParameter("page_no");
			int pageNo = Integer.valueOf(page_no);
			data = this.exchangeApplyOrderService.getPaged(pageNo, 10, this.getLoginPartyId(), symbol, type, isAll);
			resultObject.setData(data);
		} catch (Exception e) {
			resultObject.setCode("1");
			resultObject.setMsg("程序错误");
			logger.error("error:", e.fillInStackTrace());
		}
		return resultObject;
	}
	
	/**
	 * 币币交易-买入
	 * 开仓页面参数
	 */
	@RequestMapping(action + "openview.action")
	public Object openview() {

		ResultObject resultObject = new ResultObject();
		try {
			Map<String, Object> data = new HashMap<String, Object>();
			String partyId = this.getLoginPartyId();
			if (!StringUtils.isNullOrEmpty(partyId)) {
				Wallet wallet = walletService.saveWalletByPartyId(this.getLoginPartyId());

				// 账户剩余资金
				DecimalFormat df = new DecimalFormat("#.##");
				df.setRoundingMode(RoundingMode.FLOOR);// 向下取整
				data.put("volume", df.format(wallet.getMoney()));
				String session_token = sessionTokenService.savePut(partyId);
				data.put("session_token", session_token);
				data.put("fee", sysparaService.find("exchange_apply_order_buy_fee").getValue());
			}

			resultObject.setData(data);
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
	 * 币币交易-卖出
	 * 平仓页面参数
	 */
	@RequestMapping(action + "closeview.action")
	public Object closeview(HttpServletRequest request) {

		ResultObject resultObject = new ResultObject();
		try {
			Map<String, Object> data = new HashMap<String, Object>();
			String partyId = this.getLoginPartyId();
			String symbol = request.getParameter("symbol");
			if (!StringUtils.isNullOrEmpty(partyId)) {
				WalletExtend walletExtend = walletService.saveExtendByPara(partyId, symbol);
				data.put("volume", null == walletExtend ? 0
						: new BigDecimal(String.valueOf(walletExtend.getAmount())).toPlainString());
				String session_token = sessionTokenService.savePut(partyId);
				data.put("session_token", session_token);
				data.put("fee", sysparaService.find("exchange_apply_order_sell_fee").getValue());
			}
			resultObject.setData(data);
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
	 * 币币交易-买入
	 * 
	 */
	@RequestMapping(action + "open.action")
	public Object open(HttpServletRequest request) {

		ResultObject resultObject = new ResultObject();
		resultObject = readSecurityContextFromSession(resultObject);
		if (!"0".equals(resultObject.getCode())) {
			return resultObject;
		}
		// 委托数量
		String volume = request.getParameter("volume");
		String session_token = request.getParameter("session_token");
		
		String symbol = request.getParameter("symbol");
		// limit order的交易价格
		String price = request.getParameter("price");
		// 计划委托 是之前火币那边拷贝学过来的一个功能 只是只有一个盘在用，暂时注释不用
		// 是否计划委托
		String is_trigger_order = request.getParameter("is_trigger_order");
		// 计划委托的触发价
		String trigger_price = request.getParameter("trigger_price");
		// 订单报价类型。 "limit":限价 "opponent":对手价（市价）
		String order_price_type = request.getParameter("order_price_type");
		
		String partyId = this.getLoginPartyId();
		boolean lock = false;
		try {
			
			if (StringUtils.isNullOrEmpty(volume) 
					|| !StringUtils.isDouble(volume) 
					|| Double.valueOf(volume) <= 0) {
				throw new BusinessException("请输入正确的货币数量");
			}
				
			if (!ExchangeLock.add(partyId)) {
				throw new BusinessException(1, "请稍后再试");
			}

			lock = true;
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

			Kyc party_kyc = kycService.get(party.getId().toString());
			if (!(party_kyc.getStatus() == 2) && "true".equals(sysparaService.find("exchange_by_kyc").getValue())) {
				throw new BusinessException(401, "无权限");
			}
			
			List<Realtime> realtimes = this.dataService.realtime(symbol);
			double close = 1;
			if (realtimes != null) {
				close = realtimes.get(0).getClose();
			} else {
				throw new BusinessException("参数错误");
			}
			
			ExchangeApplyOrder order = new ExchangeApplyOrder();
			order.setPartyId(partyId);
			order.setSymbol(symbol);
			order.setOffset(ExchangeApplyOrder.OFFSET_OPEN);
			order.setVolume(Double.valueOf(volume));
			order.setPrice(StringUtils.isNullOrEmpty(price) ? 0 : Double.valueOf(price));
			order.setIs_trigger_order(StringUtils.isNullOrEmpty(is_trigger_order) ? false : Boolean.valueOf(is_trigger_order));
			order.setTrigger_price(StringUtils.isNullOrEmpty(trigger_price) ? 0 : Double.valueOf(trigger_price));
			order.setOrder_price_type(order_price_type);
			order.setRelation_order_no(UUID.randomUUID().toString());
			order.setClose_price(close);
			
			// 限价单 && limit order的交易价格 为空
			if ("limit".equals(order.getOrder_price_type()) && order.getPrice() == null) {
				order.setPrice(close);
			}

			exchangeApplyOrderService.saveCreate(order);
		} catch (BusinessException e) {
			resultObject.setCode("1");
			resultObject.setMsg(e.getMessage());
		} catch (Exception e) {
			resultObject.setCode("1");
			resultObject.setMsg("程序错误");
			logger.error("error:", e.fillInStackTrace());
		} finally {
			if (lock) {
				ThreadUtils.sleep(100);
				ExchangeLock.remove(partyId);
			}
		}
		return resultObject;
	}

	/**
	 * 币币交易-卖出
	 */
	@RequestMapping(action + "close.action")
	public Object close(HttpServletRequest request) {

		ResultObject resultObject = new ResultObject();
		resultObject = readSecurityContextFromSession(resultObject);
		if (!"0".equals(resultObject.getCode())) {
			return resultObject;
		}
		
		// 委托数量
		String volume = request.getParameter("volume");
		String session_token = request.getParameter("session_token");
		
		String symbol = request.getParameter("symbol");
		// limit order的交易价格
		String price = request.getParameter("price");
		// 计划委托 是之前火币那边拷贝学过来的一个功能 只是只有一个盘在用，暂时注释不用
		// 是否计划委托
		String is_trigger_order = request.getParameter("is_trigger_order");
		// 计划委托的触发价
		String trigger_price = request.getParameter("trigger_price");
		// 订单报价类型。 "limit":限价 "opponent":市价
		String order_price_type = request.getParameter("order_price_type");
		
		String partyId = this.getLoginPartyId();
		boolean lock = false;
		try {
			if (StringUtils.isNullOrEmpty(volume) 
					|| !StringUtils.isDouble(volume) 
					|| Double.valueOf(volume) <= 0) {
				throw new BusinessException("请输入正确的货币数量");
			}
			if (!ExchangeLock.add(partyId)) {
				throw new BusinessException(1, "请稍后再试");
			}
			lock = true;
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
			
			List<Realtime> realtimes = this.dataService.realtime(symbol);
			double close = 1;
			if (realtimes != null) {
				close = realtimes.get(0).getClose();
			} else {
				throw new BusinessException("参数错误");
			}

			ExchangeApplyOrder order = new ExchangeApplyOrder();
			order.setPartyId(partyId);
			order.setSymbol(symbol);
			order.setOffset(ExchangeApplyOrder.OFFSET_CLOSE);
			order.setVolume(Double.valueOf(volume));
			order.setPrice(StringUtils.isNullOrEmpty(price) ? 0 : Double.valueOf(price));
			order.setIs_trigger_order(StringUtils.isNullOrEmpty(is_trigger_order) ? false : Boolean.valueOf(is_trigger_order));
			order.setTrigger_price(StringUtils.isNullOrEmpty(trigger_price) ? 0 : Double.valueOf(trigger_price));
			order.setOrder_price_type(order_price_type);
			order.setRelation_order_no(UUID.randomUUID().toString());
			order.setClose_price(close);
			
			// 限价单 && limit order的交易价格 为空
			if ("limit".equals(order.getOrder_price_type()) && order.getPrice() == null) {
				order.setPrice(close);
			}

			this.exchangeApplyOrderService.saveCreate(order);
		} catch (BusinessException e) {
			resultObject.setCode("1");
			resultObject.setMsg(e.getMessage());
		} catch (Exception e) {
			resultObject.setCode("1");
			resultObject.setMsg("程序错误");
			logger.error("error:", e.fillInStackTrace());
		} finally {
			if (lock) {
				ThreadUtils.sleep(100);
				ExchangeLock.remove(partyId);
			}
		}
		return resultObject;
	}
	
	/**
	 * 撤单
	 */
	@RequestMapping(action + "cancel.action")
	public Object cancel(HttpServletRequest request) {

		ResultObject resultObject = new ResultObject();
		resultObject = readSecurityContextFromSession(resultObject);
		if (!"0".equals(resultObject.getCode())) {
			return resultObject;
		}
		try {
			String order_no = request.getParameter("order_no");
			CancelDelayThread lockDelayThread = new CancelDelayThread(this.getLoginPartyId(), order_no,
					exchangeApplyOrderService);
			Thread t = new Thread(lockDelayThread);
			t.start();

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
	 * 新线程处理，直接拿到订单锁处理完成后退出
	 *
	 */
	public class CancelDelayThread implements Runnable {
		private String partyId;
		private String order_no;
		private ExchangeApplyOrderService exchangeApplyOrderService;

		public void run() {
			try {
				while (true) {
					if (ExchangeLock.add(order_no)) {
						this.exchangeApplyOrderService.saveCancel(partyId, order_no);
						/**
						 * 处理完退出
						 */
						break;
					}
					ThreadUtils.sleep(100);

				}

			} catch (Exception e) {
				logger.error("error:", e);
			} finally {
				ThreadUtils.sleep(100);
				ExchangeLock.remove(order_no);
			}

		}

		public CancelDelayThread(String partyId, String order_no, ExchangeApplyOrderService exchangeApplyOrderService) {
			this.partyId = partyId;
			this.order_no = order_no;
			this.exchangeApplyOrderService = exchangeApplyOrderService;
		}

	}

	/**
	 * 详情接口
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
			ExchangeApplyOrder order = this.exchangeApplyOrderService.findByOrderNoAndPartyId(order_no,
					this.getLoginPartyId());
			resultObject.setData(bulidData(order));
		} catch (Exception e) {
			resultObject.setCode("1");
			resultObject.setMsg("程序错误");
			logger.error("error:", e);
		}
		return resultObject;
	}
	
	private Map<String, Object> bulidData(ExchangeApplyOrder order) {
		DecimalFormat df = new DecimalFormat("#.##");
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("order_no", order.getOrder_no());
		map.put("name", itemService.cacheBySymbol(order.getSymbol(), false).getName());
		map.put("symbol", order.getSymbol());
		map.put("create_time", DateUtils.format(order.getCreate_time(), DateUtils.DF_yyyyMMddHHmmss));
		map.put("volume", order.getVolume());
		map.put("offset", order.getOffset());
		map.put("price", order.getPrice());
		map.put("order_price_type", order.getOrder_price_type());
		map.put("state", order.getState());
		map.put("fee", order.getFee());
		map.put("amount", order.getAmount());
		map.put("close_price", order.getClose_price());
		map.put("close_time", DateUtils.format(order.getClose_time(), DateUtils.DF_yyyyMMddHHmmss));
		map.put("trigger_price", order.getTrigger_price());
		map.put("is_trigger_order", order.isIs_trigger_order());
		return map;
	}
}
