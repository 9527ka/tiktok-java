package project.web.api;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
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
import project.contract.ContractApplyOrder;
import project.contract.ContractApplyOrderService;
import project.contract.ContractLock;
import project.contract.ContractOrder;
import project.contract.ContractOrderService;
import project.item.ItemService;
import project.item.model.Item;
import project.item.model.ItemLever;
import project.party.PartyService;
import project.party.model.Party;
import project.syspara.SysparaService;
import project.wallet.Wallet;
import project.wallet.WalletService;

/**
 * 永续合约委托单
 */
@RestController
@CrossOrigin
public class ContractApplyOrderController extends BaseAction {

	private Logger logger = LogManager.getLogger(ContractApplyOrderController.class);

	@Autowired
	private ContractApplyOrderService contractApplyOrderService;
	@Autowired
	private ContractOrderService contractOrderService;
	@Autowired
	private ItemService itemService;
	@Autowired
	private WalletService walletService;
	@Autowired
	private PartyService partyService;
	@Autowired
	private SessionTokenService sessionTokenService;
	@Autowired
	protected SysparaService sysparaService;

	private final String action = "/api/contractApplyOrder!";
	
	/**
	 * 开仓页面参数
	 * 
	 * symbol 币种
	 */
	@RequestMapping(action + "openview.action")
	public Object openview(HttpServletRequest request) throws IOException {
		String symbol = request.getParameter("symbol");

		ResultObject resultObject = new ResultObject();

		try {
			
			Map<String, Object> data = new HashMap<String, Object>();
			
			Item item = this.itemService.cacheBySymbol(symbol, false);			
			data.put("amount", item.getUnit_amount());
			data.put("fee", item.getUnit_fee());
			
			List<ItemLever> list = this.itemService.findLever(item.getId().toString());
			data.put("lever", list);
			
			String partyId = this.getLoginPartyId();
			if (!StringUtils.isNullOrEmpty(partyId)) {
				
				Wallet wallet = this.walletService.saveWalletByPartyId(this.getLoginPartyId());
				
				// 账户剩余资 金
				double use_amount = Arith.add(item.getUnit_amount(), item.getUnit_fee());
				double volume = Arith.div(wallet.getMoney(), use_amount);
				volume = new BigDecimal(volume).setScale(0, RoundingMode.DOWN).doubleValue();
				
				DecimalFormat df = new DecimalFormat("#");
				data.put("volume", df.format(volume));
				
				String session_token = this.sessionTokenService.savePut(partyId);
				data.put("session_token", session_token);
			} else {
				data.put("volume", 0.00D);
			}			

			double contract_open_limit_min = Double.valueOf(this.sysparaService.find("contract_open_limit_min").getValue());
			data.put("contract_open_limit_min", contract_open_limit_min);
			double contract_open_limit_max = Double.valueOf(this.sysparaService.find("contract_open_limit_max").getValue());
			data.put("contract_open_limit_max", contract_open_limit_max);

			resultObject.setData(data);
			
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
	 * 平仓页面参数
	 * 
	 * symbol 币种
	 * direction "buy":多 "sell":空
	 */
	@RequestMapping(action + "closeview.action")
	public Object closeview(HttpServletRequest request) throws IOException {
		String symbol = request.getParameter("symbol");
		String direction = request.getParameter("direction");

		ResultObject resultObject = new ResultObject();

		try {
			
			Map<String, Object> data = new HashMap<String, Object>();
			
			String partyId = this.getLoginPartyId();
			if (!StringUtils.isNullOrEmpty(partyId)) {

				List<ContractOrder> list = this.contractOrderService.findSubmitted(partyId, symbol, direction);
				
				double ordervolume = 0;
				for (int i = 0; i < list.size(); i++) {
					ordervolume = Arith.add(ordervolume, list.get(i).getVolume());
				}
				data.put("amount", ordervolume);
				
				String session_token = this.sessionTokenService.savePut(partyId);
				data.put("session_token", session_token);				
			} else {
				data.put("amount", 0);
			}			

			double contract_close_limit_min = Double.valueOf(this.sysparaService.find("contract_close_limit_min").getValue());
			data.put("contract_close_limit_min", contract_close_limit_min);
			double contract_close_limit_max = Double.valueOf(this.sysparaService.find("contract_close_limit_max").getValue());
			data.put("contract_close_limit_max", contract_close_limit_max);

			resultObject.setData(data);
			
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
	 * 开仓
	 * 
	 * symbol 币种
	 * direction "buy":多 "sell":空
	 * amount 委托数量(张)
	 * lever_rate 杠杆倍数
	 * price 交易价格
	 * stop_price_profit 止盈触发价格
	 * stop_price_loss 止损触发价格
	 * price_type 订单报价类型："limit":限价 "opponent":对手价（市价）
	 */
	@RequestMapping(action + "open.action")
	public Object open(HttpServletRequest request) throws IOException {
		String session_token = request.getParameter("session_token");
		String symbol = request.getParameter("symbol");
		String direction = request.getParameter("direction");
		String amount = request.getParameter("amount");
		String lever_rate = request.getParameter("lever_rate");
		String price = request.getParameter("price");
		String stop_price_profit = request.getParameter("stop_price_profit");
		String stop_price_loss = request.getParameter("stop_price_loss");
		String price_type = request.getParameter("price_type");

		ResultObject resultObject = new ResultObject();
		resultObject = this.readSecurityContextFromSession(resultObject);
		if (!"0".equals(resultObject.getCode())) {
			return resultObject;
		}
		
		String partyId = this.getLoginPartyId();
		boolean lock = false;
		
		try {

			if (!ContractLock.add(partyId)) {
				throw new BusinessException("请稍后再试");
			}
			
			lock = true;

			Object object = this.sessionTokenService.cacheGet(session_token);
			this.sessionTokenService.delete(session_token);
			if (null == object || !this.getLoginPartyId().equals((String) object)) {
				throw new BusinessException("请稍后再试");
			}
			
			if (StringUtils.isNullOrEmpty(lever_rate)) {
				lever_rate = "1";
			}
			if (StringUtils.isNullOrEmpty(stop_price_profit)) {
				stop_price_profit = "0";
			}
			if (StringUtils.isNullOrEmpty(stop_price_loss)) {
				stop_price_loss = "0";
			}
			
			String error = this.verifOpen(amount, lever_rate, price, stop_price_profit, stop_price_loss);
			if (!StringUtils.isNullOrEmpty(error)) {
				throw new BusinessException(error);
			}
			
			double amount_double = Double.valueOf(request.getParameter("amount")).doubleValue();
			double lever_rate_double = Double.valueOf(lever_rate).doubleValue();
			double price_double = Double.valueOf(request.getParameter("price")).doubleValue();
			double stop_price_profit_double = Double.valueOf(stop_price_profit).doubleValue();
			double stop_price_loss_double = Double.valueOf(stop_price_loss).doubleValue();
			
			Party party = this.partyService.cachePartyBy(partyId, false);
			if (!party.getEnabled()) {
				resultObject.setCode("506");
				resultObject.setMsg("用户已锁定");
				return resultObject;
			}

//			double contract_open_limit_min = Double.valueOf(this.sysparaService.find("contract_open_limit_min").getValue());
//			if (amount_double < contract_open_limit_min) {
//				throw new BusinessException("开仓金额不得小于开仓最小限额");
//			}
//			double contract_open_limit_max = Double.valueOf(this.sysparaService.find("contract_open_limit_max").getValue());
//			if (amount_double > contract_open_limit_max) {
//				throw new BusinessException("开仓金额不得大于开仓最大限额");
//			}

			ContractApplyOrder order = new ContractApplyOrder();
			order.setPartyId(partyId);
			order.setSymbol(symbol);
			order.setDirection(direction);
			order.setOffset(ContractApplyOrder.OFFSET_OPEN);
			order.setVolume(amount_double);
			order.setVolume_open(amount_double);
			order.setLever_rate(lever_rate_double);
			order.setPrice(price_double);
			order.setStop_price_profit(stop_price_profit_double);
			order.setStop_price_loss(stop_price_loss_double);
			order.setOrder_price_type(price_type);

			this.contractApplyOrderService.saveCreate(order);
			
		} catch (BusinessException e) {
			resultObject.setCode("1");
			resultObject.setMsg(e.getMessage());
		} catch (Throwable t) {
			resultObject.setCode("1");
			resultObject.setMsg("程序错误");
			logger.error("error:", t);
		} finally {
			if (lock) {
				ThreadUtils.sleep(100);
				ContractLock.remove(partyId);
			}
		}
		
		return resultObject;
	}

	/**
	 * 平仓
	 * 
	 * symbol 币种
	 * direction "buy":多 "sell":空
	 * amount 委托数量(张)
	 * price 交易价格
	 * order_price_type 订单报价类型："limit":限价 "opponent":对手价（市价）
	 */
	@RequestMapping(action + "close.action")
	public Object close(HttpServletRequest request) throws IOException {
		String session_token = request.getParameter("session_token");
		String symbol = request.getParameter("symbol");
		String direction = request.getParameter("direction");
		String amount = request.getParameter("amount");
		String price = request.getParameter("price");
		String price_type = request.getParameter("price_type");

		ResultObject resultObject = new ResultObject();
		resultObject = this.readSecurityContextFromSession(resultObject);
		if (!"0".equals(resultObject.getCode())) {
			return resultObject;
		}
		
		String partyId = this.getLoginPartyId();
		boolean lock = false;
		
		try {

			if (!ContractLock.add(partyId)) {
				throw new BusinessException("请稍后再试");
			}
			
			lock = true;

			Object object = this.sessionTokenService.cacheGet(session_token);
			this.sessionTokenService.delete(session_token);
			if (null == object || !this.getLoginPartyId().equals((String) object)) {
				throw new BusinessException("请稍后再试");
			}
			
			String error = this.verifClose(amount, price);
			if (!StringUtils.isNullOrEmpty(error)) {
				throw new BusinessException(error);
			}
			
			double amount_double = Double.valueOf(request.getParameter("amount")).doubleValue();
			double price_double = Double.valueOf(request.getParameter("price")).doubleValue();
			
			Party party = this.partyService.cachePartyBy(partyId, false);
			if (!party.getEnabled()) {
				resultObject.setCode("506");
				resultObject.setMsg("用户已锁定");
				return resultObject;
			}

//			double contract_close_limit_min = Double.valueOf(this.sysparaService.find("contract_close_limit_min").getValue());
//			if (amount_double < contract_close_limit_min) {
//				throw new BusinessException("平仓金额不得小于平仓最小限额");
//			}
//			double contract_close_limit_max = Double.valueOf(this.sysparaService.find("contract_close_limit_max").getValue());
//			if (amount_double > contract_close_limit_max) {
//				throw new BusinessException("平仓金额不得大于平仓最大限额");
//			}
			
			ContractApplyOrder order = new ContractApplyOrder();
			order.setPartyId(partyId);
			order.setSymbol(symbol);
			order.setDirection(direction);
			order.setOffset(ContractApplyOrder.OFFSET_CLOSE);
			order.setVolume(amount_double);
			order.setVolume_open(amount_double);
			order.setPrice(price_double);
			order.setOrder_price_type(price_type);

			this.contractApplyOrderService.saveCreate(order);
			
		} catch (BusinessException e) {
			resultObject.setCode("1");
			resultObject.setMsg(e.getMessage());
		} catch (Throwable t) {
			resultObject.setCode("1");
			resultObject.setMsg("程序错误");
			logger.error("error:", t);
		} finally {
			if (lock) {
				ThreadUtils.sleep(100);
				ContractLock.remove(partyId);
			}
		}
		
		return resultObject;
	}

	/**
	 * 撤单
	 * 
	 * order_no 订单号
	 */
	@RequestMapping(action + "cancel.action")
	public Object cancel(HttpServletRequest request) throws IOException {
		String order_no = request.getParameter("order_no");

		ResultObject resultObject = new ResultObject();
		resultObject = this.readSecurityContextFromSession(resultObject);
		if (!"0".equals(resultObject.getCode())) {
			return resultObject;
		}
		
		try {

			CancelDelayThread lockDelayThread = new CancelDelayThread(this.getLoginPartyId(), order_no, this.contractApplyOrderService, false);
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
	 * 一键撤单
	 */
	@RequestMapping(action + "cancelAll.action")
	public Object cancelAll(HttpServletRequest request) throws IOException {

		ResultObject resultObject = new ResultObject();
		resultObject = this.readSecurityContextFromSession(resultObject);
		if (!"0".equals(resultObject.getCode())) {
			return resultObject;
		}
		
		try {

			CancelDelayThread lockDelayThread = new CancelDelayThread(this.getLoginPartyId(), "", this.contractApplyOrderService, true);
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
	 * 查询订单详情
	 * 
	 * order_no 订单号
	 */
	@RequestMapping(action + "get.action")
	public Object get(HttpServletRequest request) throws IOException {
		String order_no = request.getParameter("order_no");

		ResultObject resultObject = new ResultObject();
		resultObject = this.readSecurityContextFromSession(resultObject);
		if (!"0".equals(resultObject.getCode())) {
			return resultObject;
		}

		try {
			
			if (StringUtils.isNullOrEmpty(order_no)) {
				logger.info("contractApplyOrder!get order_no null");
				throw new BusinessException("订单不存在");
			}
			
			ContractApplyOrder order = this.contractApplyOrderService.findByOrderNo(order_no);
			
			if (null == order) {
				logger.info("contractApplyOrder!get order_no:" + order_no + ", order null");
				throw new BusinessException("订单不存在");
			}
			
			resultObject.setData(this.bulidData(order));
			
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
	 * 查询订单列表
	 * 
	 * page_no 页码
	 * symbol 币种
	 * type 查询类型：orders 当前委托单 ，hisorders 历史委托单
	 */
	@RequestMapping(action + "list.action")
	public Object list(HttpServletRequest request) throws IOException {
		String page_no = request.getParameter("page_no");
		String symbol = request.getParameter("symbol");
		String type = request.getParameter("type");

		List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
		
		ResultObject resultObject = new ResultObject();
		resultObject = this.readSecurityContextFromSession(resultObject);
		if (!"0".equals(resultObject.getCode())) {
			resultObject.setData(data);
			return resultObject;
		}

		try {

			if (StringUtils.isNullOrEmpty(page_no)) {
				page_no = "1";
			}
			if (!StringUtils.isInteger(page_no)) {
				throw new BusinessException("页码不是整数");
			}
			if (Integer.valueOf(page_no).intValue() <= 0) {
				throw new BusinessException("页码不能小于等于0");
			}

			int page_no_int = Integer.valueOf(page_no).intValue();

			data = this.contractApplyOrderService.getPaged(page_no_int, 10, this.getLoginPartyId(), symbol, type);
			
			resultObject.setData(data);
			
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

	private Map<String, Object> bulidData(ContractApplyOrder order) {		
		Map<String, Object> map = new HashMap<String, Object>();		
		map.put("order_no", order.getOrder_no());
		map.put("name", this.itemService.cacheBySymbol(order.getSymbol(), false).getName());
		map.put("symbol", order.getSymbol());
		map.put("create_time", DateUtils.format(order.getCreate_time(), DateUtils.DF_yyyyMMddHHmmss));
		map.put("volume", order.getVolume());
		map.put("volume_open", order.getVolume_open());
		map.put("direction", order.getDirection());
		map.put("offset", order.getOffset());
		map.put("lever_rate", order.getLever_rate());
		map.put("price", order.getPrice());
		map.put("stop_price_profit", order.getStop_price_profit());
		map.put("stop_price_loss", order.getStop_price_loss());
		map.put("price_type", order.getOrder_price_type());
		map.put("state", order.getState());
		map.put("amount", Arith.mul(order.getVolume(), order.getUnit_amount()));
		map.put("amount_open", Arith.mul(order.getVolume_open(), order.getUnit_amount()));
		map.put("fee", order.getFee());
		map.put("deposit", order.getDeposit());
		return map;
	}

	private String verifOpen(String amount, String lever_rate, String price, String stop_price_profit, String stop_price_loss) {
		
		if (StringUtils.isNullOrEmpty(amount)) {
			return "委托金额必填";
		}
		if (!StringUtils.isDouble(amount)) {
			return "委托金额不是浮点数";
		}
		if (Double.valueOf(amount).doubleValue() <= 0) {
			return "委托金额不能小于等于0";
		}
		
//		if (StringUtils.isNullOrEmpty(lever_rate)) {
//			return "杠杆倍数必填";
//		}
		if (!StringUtils.isDouble(lever_rate)) {
			return "杠杆倍数不是浮点数";
		}
		if (Double.valueOf(lever_rate).doubleValue() <= 0) {
			return "杠杆倍数不能小于等于0";
		}
		
		if (StringUtils.isNullOrEmpty(price)) {
			return "交易价格必填";
		}
		if (!StringUtils.isDouble(price)) {
			return "交易价格不是浮点数";
		}
		if (Double.valueOf(price).doubleValue() <= 0) {
			return "交易价格不能小于等于0";
		}
		
//		if (StringUtils.isNullOrEmpty(stop_price_profit)) {
//			return "止盈触发价格必填";
//		}
//		if (!StringUtils.isDouble(stop_price_profit)) {
//			return "止盈触发价格不是浮点数";
//		}
//		if (Double.valueOf(stop_price_profit).doubleValue() < 0) {
//			return "止盈触发价格不能小于0";
//		}
//		
//		if (StringUtils.isNullOrEmpty(stop_price_loss)) {
//			return "止损触发价格必填";
//		}
//		if (!StringUtils.isDouble(stop_price_loss)) {
//			return "止损触发价格不是浮点数";
//		}
//		if (Double.valueOf(stop_price_loss).doubleValue() < 0) {
//			return "止损触发价格不能小于0";
//		}
		
		return null;
	}

	private String verifClose(String amount, String price) {
		
		if (StringUtils.isNullOrEmpty(amount)) {
			return "委托金额必填";
		}
		if (!StringUtils.isDouble(amount)) {
			return "委托金额不是浮点数";
		}
		if (Double.valueOf(amount).doubleValue() <= 0) {
			return "委托金额不能小于等于0";
		}
		
		if (StringUtils.isNullOrEmpty(price)) {
			return "交易价格必填";
		}
		if (!StringUtils.isDouble(price)) {
			return "交易价格不是浮点数";
		}
		if (Double.valueOf(price).doubleValue() <= 0) {
			return "交易价格不能小于等于0";
		}
		
		return null;
	}

	/**
	 * 新线程处理，直接拿到订单锁处理完成后退出
	 */
	public class CancelDelayThread implements Runnable {
		private String partyId;
		private String order_no;
		private ContractApplyOrderService contractApplyOrderService;
		private boolean all = false;

		public void run() {
			
			try {
				
				while (true) {
					if (true == all) {
						// 一键撤单
						if (ContractLock.add("all")) {
							this.contractApplyOrderService.saveCancelAllByPartyId(partyId);
							// 处理完退出
							break;
						}
						ThreadUtils.sleep(100);							
					} else {
						if (ContractLock.add(order_no)) {
							this.contractApplyOrderService.saveCancel(partyId, order_no);
							// 处理完退出
							break;
						}
						ThreadUtils.sleep(100);						
					}
				}

			} catch (Throwable t) {
				logger.error("error:", t);
			} finally {
				ThreadUtils.sleep(100);
				if (true == all) {
					ContractLock.remove("all");					
				} else {
					ContractLock.remove(order_no);					
				}
			}
		}

		public CancelDelayThread(String partyId, String order_no, ContractApplyOrderService contractApplyOrderService, boolean all) {
			this.partyId = partyId;
			this.order_no = order_no;
			this.contractApplyOrderService = contractApplyOrderService;
			this.all = all;
		}
	}

}
