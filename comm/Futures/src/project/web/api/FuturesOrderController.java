package project.web.api;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kernel.exception.BusinessException;
import kernel.sessiontoken.SessionTokenService;
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

/**
 * 交割合约订单
 */
@RestController
@CrossOrigin
public class FuturesOrderController extends BaseAction {

	private Logger logger = LogManager.getLogger(FuturesOrderController.class);

	@Autowired
	private WalletService walletService;
	@Autowired
	private PartyService partyService;
	@Autowired
	private FuturesOrderService futuresOrderService;
	@Autowired
	private FuturesOrderLocalService futuresOrderLocalService;
	@Autowired
	private FuturesParaService futuresParaService;
	@Autowired
	private SessionTokenService sessionTokenService;

	private final String action = "/api/futuresOrder!";

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

			List<Map> futuresPara = new ArrayList<Map>();
			for (FuturesPara obj : this.futuresParaService.cacheGetBySymbolSort(symbol)) {
				FuturesPara para = new FuturesPara();
				BeanUtils.copyProperties(obj, para);
				futuresPara.add(this.futuresParaService.bulidOne(para));
			}
			data.put("para", futuresPara);

			String partyId = this.getLoginPartyId();
			if (!StringUtils.isNullOrEmpty(partyId) && futuresPara != null) {
				Wallet wallet = this.walletService.saveWalletByPartyId(this.getLoginPartyId());

				// 账户剩余资金
				DecimalFormat df = new DecimalFormat("#");
				data.put("amount", df.format(wallet.getMoney()));

				String session_token = this.sessionTokenService.savePut(partyId);
				data.put("session_token", session_token);				
			} else {
				data.put("amount", 0);
			}

			resultObject.setData(data);

		} catch (BusinessException e) {
			resultObject.setCode("1");
			resultObject.setMsg(e.getMessage());
		} catch (Throwable t) {
			resultObject.setCode("1");
			resultObject.setMsg("参数错误");
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
	 * para_id 交割合约参数
	 */
	@RequestMapping(action + "open.action")
	public Object open(HttpServletRequest request) throws IOException {
		String session_token = request.getParameter("session_token");
		String symbol = request.getParameter("symbol");
		String direction = request.getParameter("direction");
		String amount = request.getParameter("amount");
		String para_id = request.getParameter("para_id");

		ResultObject resultObject = new ResultObject();
		resultObject = this.readSecurityContextFromSession(resultObject);
		if (!"0".equals(resultObject.getCode())) {
			return resultObject;
		}
		
		String partyId = this.getLoginPartyId();
		boolean lock = false;
		
		try {
			
			Map<String, String> data = new HashMap<String, String>();
			
			if (!FuturesLock.add(partyId)) {
				throw new BusinessException("请稍后再试");
			}
			
			lock = true;

			Object object = this.sessionTokenService.cacheGet(session_token);
			this.sessionTokenService.delete(session_token);
			if (null == object || !this.getLoginPartyId().equals((String) object)) {
				throw new BusinessException("请稍后再试");
			}
			
			if (StringUtils.isNullOrEmpty(amount)) {
				return "委托数量(张)必填";
			}
			if (!StringUtils.isDouble(amount)) {
				return "委托数量(张)不是浮点数";
			}
			if (Double.valueOf(amount).doubleValue() <= 0) {
				return "委托数量(张)不能小于等于0";
			}

			double amount_double = Double.valueOf(request.getParameter("amount")).doubleValue();
						
			Party party = this.partyService.cachePartyBy(partyId, false);
			if (!party.getEnabled()) {
				resultObject.setCode("506");
				resultObject.setMsg("用户已锁定");
				return resultObject;
			}

			FuturesOrder order = new FuturesOrder();
			order.setPartyId(partyId);
			order.setSymbol(symbol);
			order.setDirection(direction);
			order.setVolume(amount_double);

			order = this.futuresOrderService.saveOpen(order, para_id);
			data.put("order_no", order.getOrder_no());
			data.put("open_price", order.getTrade_avg_price().toString());
			
			resultObject.setData(data);
			
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
				FuturesLock.remove(partyId);
			}
		}
		
		return resultObject;
	}

	/**
	 * 查询交割持仓列表
	 * 
	 * page_no 页码
	 * symbol 币种
	 * type 开仓页面订单类型
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
			
			String loginPartyId = this.getLoginPartyId();
			
			if ("orders".equals(type)) {
				
				List<FuturesOrder> list = this.futuresOrderService.cacheSubmitted();
				List<FuturesOrder> result = new LinkedList<FuturesOrder>();
				
				if (StringUtils.isNotEmpty(loginPartyId)) {
					// 登录才添加到列表
					
					for (FuturesOrder order : list) {
						if (StringUtils.isNotEmpty(symbol)) {
							// 有种类，满足登录人和种类添加
							if (symbol.equals(order.getSymbol()) && loginPartyId.equals(order.getPartyId())) {
								result.add(order);								
							}
						} else {
							if (loginPartyId.equals(order.getPartyId())) {
								result.add(order);								
							}
						}
					}
				}

				data = this.futuresOrderService.bulidData(result);

			} else if ("hisorders".equals(type)) {
				
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
				
				List<FuturesOrder> list = this.futuresOrderService.getPaged(page_no_int, 10, this.getLoginPartyId(), symbol, type).getElements();
				data = this.futuresOrderService.bulidData(list);
			}

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
	 * 查询交割持仓详情
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
				logger.info("futuresOrder!get order_no null");
				throw new BusinessException("订单不存在");
			}

			FuturesOrder order = this.futuresOrderLocalService.cacheByOrderNo(order_no);
			
			if (null == order) {
				logger.info("futuresOrder!get order_no:" + order_no + ", order null");
				throw new BusinessException("订单不存在");
			}
			
			resultObject.setData(this.futuresOrderLocalService.bulidOne(order));
			
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

}
