package project.web.api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kernel.exception.BusinessException;
import kernel.util.JsonUtils;
import kernel.util.StringUtils;
import kernel.util.ThreadUtils;
import kernel.web.BaseAction;
import kernel.web.ResultObject;
import project.Constants;
import project.contract.ContractLock;
import project.contract.ContractOrder;
import project.contract.ContractOrderService;
import project.data.DataService;
import project.data.model.Realtime;

/**
 * 永续合约持仓单
 */
@RestController
@CrossOrigin
public class ContractOrderController extends BaseAction {

	private Logger logger = LogManager.getLogger(ContractOrderController.class);

	@Autowired
	private ContractOrderService contractOrderService;
	@Autowired
	private DataService dataService;

	private final String action = "/api/contractOrder!";

	/**
	 * 平仓
	 * 
	 * order_no 订单号
	 */
	@RequestMapping(action + "close.action")
	public Object close(HttpServletRequest request) throws IOException {
		String order_no = request.getParameter("order_no");

		ResultObject resultObject = new ResultObject();
		resultObject = this.readSecurityContextFromSession(resultObject);
		if (!"0".equals(resultObject.getCode())) {
			return resultObject;
		}
		
		try {

			CloseDelayThread lockDelayThread = new CloseDelayThread(this.getLoginPartyId(), order_no, this.contractOrderService, false);
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
	 * 一键平仓
	 */
	@RequestMapping(action + "closeAll.action")
	public Object closeAll(HttpServletRequest request) throws IOException {

		ResultObject resultObject = new ResultObject();
		resultObject = this.readSecurityContextFromSession(resultObject);
		if (!"0".equals(resultObject.getCode())) {
			return resultObject;
		}
		
		try {

			CloseDelayThread lockDelayThread = new CloseDelayThread(this.getLoginPartyId(), "", this.contractOrderService, true);
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
	 * 订单列表
	 * 
	 * page_no 页码
	 * symbol 币种
	 * type 查询类型：orders 当前持仓单；hisorders 历史持仓单；
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

			data = this.contractOrderService.getPaged(page_no_int, 10, this.getLoginPartyId(), symbol, type);
			
			String symbolsStr = "";
			Set<String> symbols = new HashSet<String>();
			for (int i = 0; i < data.size(); i++) {
				String sym = data.get(i).get("symbol").toString();
				if (!symbols.contains(sym)) {
					symbols.add(sym);
					if (i != 0) {
						symbolsStr = symbolsStr + "," + sym;
					} else {
						symbolsStr = sym;
					}
				}			
			}
			
			List<Realtime> realtime_all = this.dataService.realtime(symbolsStr);
			if (realtime_all.size() <= 0) {
				realtime_all = new ArrayList<Realtime>();
//				throw new BusinessException("系统错误，请稍后重试");
			}
			
			Map<String, Realtime> realtimeMap = new HashMap<String, Realtime>();
			for (int i = 0; i < realtime_all.size(); i++) {
				realtimeMap.put(realtime_all.get(i).getSymbol(), realtime_all.get(i));
			}
			
			for (int i = 0; i < data.size(); i++) {
				Map<String, Object> map = data.get(i);

				// 标记价格
				Realtime realtime = realtimeMap.get(map.get("symbol"));
				if (null == realtime) {
					map.put("mark_price", 0);
				} else {
					map.put("mark_price", realtime.getClose());
				}
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
	 * 订单详情
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
				logger.info("contractOrder!get order_no null");
				throw new BusinessException("订单不存在");
			}

			ContractOrder order = this.contractOrderService.findByOrderNo(order_no);
						
			if (null == order) {
				logger.info("contractOrder!get order_no:" + order_no + ", order null");
				throw new BusinessException("订单不存在");
			}
						
			resultObject.setData(this.contractOrderService.bulidOne(order));
		
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
	 */
	public class CloseDelayThread implements Runnable {
		private String partyId;
		private String order_no;
		private ContractOrderService contractOrderService;
		private boolean all = false;

		public void run() {
			
			try {
				
				while (true) {
					if (true == all) {
						// 一键平仓
						if (ContractLock.add("all")) {
							this.contractOrderService.saveCloseRemoveAllByPartyId(partyId);
							// 处理完退出
							break;
						}
						ThreadUtils.sleep(500);	
					} else {
						if (ContractLock.add(order_no)) {
							this.contractOrderService.saveClose(partyId, order_no);
							// 处理完退出
							break;
						}
						ThreadUtils.sleep(500);						
					}
				}

			} catch (Throwable t) {
				logger.error("error:", t);
			} finally {
				if (true == all) {
					ContractLock.remove("all");
				} else {
					ContractLock.remove(order_no);
				}
			}
		}

		public CloseDelayThread(String partyId, String order_no, ContractOrderService contractOrderService, boolean all) {
			this.partyId = partyId;
			this.order_no = order_no;
			this.contractOrderService = contractOrderService;
			this.all = all;
		}
	}

}
