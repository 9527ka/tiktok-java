package project.web.admin;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import kernel.exception.BusinessException;
import kernel.util.Arith;
import kernel.util.JsonUtils;
import kernel.util.StringUtils;
import kernel.util.ThreadUtils;
import kernel.web.PageActionSupport;
import project.Constants;
import project.contract.AdminContractOrderService;
import project.contract.ContractOrder;
import project.contract.ContractOrderService;
import project.data.AdjustmentValue;
import project.data.AdjustmentValueService;
import project.data.DataService;
import project.data.model.Realtime;
import project.item.ItemService;
import project.item.model.Item;
import project.log.Log;
import project.log.LogService;
import security.SecUser;
import security.internal.SecUserService;

/**
 * 永续持仓单 当前单 
 */
@RestController
public class AdminContractOrderController extends PageActionSupport {

	private Logger logger = LogManager.getLogger(AdminContractOrderController.class);
	
	@Autowired
	private AdminContractOrderService adminContractOrderService;
	@Autowired
	private ItemService itemService;
	@Autowired
	private AdjustmentValueService adjustmentValueService;
	@Autowired
	private DataService dataService;
	@Autowired
	private ContractOrderService contractOrderService;
	@Autowired
	private LogService logService;
	@Autowired
	private SecUserService secUserService;

	private final String action = "normal/adminContractOrderAction!";

	/**
	 * 获取 永续合约持仓单列表
	 */
	@RequestMapping(action + "list.action")
	public ModelAndView list(HttpServletRequest request) {
		String pageNo = request.getParameter("pageNo");
		String message = request.getParameter("message");
		String error = request.getParameter("error");
		String order_no_para = request.getParameter("order_no_para");
		String name_para = request.getParameter("name_para");
		String rolename_para = request.getParameter("rolename_para");
		String start_time = request.getParameter("start_time");
		String end_time = request.getParameter("end_time");

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("contract_order_list");
		
		try {
			
			this.checkAndSetPageNo(pageNo);

			this.pageSize = 30;
			
			String loginPartyId = this.getLoginPartyId();
			this.page = this.adminContractOrderService.pagedQuery(this.pageNo, this.pageSize, ContractOrder.STATE_SUBMITTED,
					rolename_para, loginPartyId, start_time, end_time, name_para, order_no_para);

			List<Map> list = this.page.getElements();
			for (int i = 0; i < list.size(); i++) {
				Map map = list.get(i);				
				if (null == map.get("rolename")) {
					map.put("roleNameDesc", "");
				} else {
					String roleName = map.get("rolename").toString();
					map.put("roleNameDesc", Constants.ROLE_MAP.containsKey(roleName) ? Constants.ROLE_MAP.get(roleName) : roleName);
				}
			}

			List<Item> items = this.itemService.cacheGetAll();
			Item item_turn = null;
			for (int i = 0; i < items.size(); i++) {
				item_turn = items.get(i);

				if ("knc".equals(item_turn.getSymbol())) {
					Item item_3 = items.get(1);
					if (item_3.getSymbol().equals(item_turn.getSymbol())) {
						continue;
					}
					items.remove(1);
					items.add(1, item_turn);
					items.remove(i);
					items.add(i, item_3);
					continue;
				}
				
				if ("abcoin".equals(item_turn.getSymbol())) {
					Item item_3 = items.get(0);
					if (item_3.getSymbol().equals(item_turn.getSymbol())) {
						continue;
					}
					items.remove(0);
					items.add(0, item_turn);
					items.remove(i);
					items.add(i, item_3);
					continue;
				}
				
				if ("oxtrx".equals(item_turn.getSymbol())) {
					Item item_3 = items.get(0);
					if (item_3.getSymbol().equals(item_turn.getSymbol())) {
						continue;
					}
					items.remove(0);
					items.add(0, item_turn);
					items.remove(i);
					items.add(i, item_3);
					continue;
				}
			}

			Map<String, String> symbols = new LinkedHashMap<String, String>();
			for (Item item : items) {
				symbols.put(item.getSymbol(), item.getName());
			}
			
			modelAndView.addObject("symbols", symbols);

		} catch (BusinessException e) {
			modelAndView.addObject("error", e.getMessage());
			return modelAndView;
		} catch (Throwable t) {
			logger.error(" error ", t);
			modelAndView.addObject("error", "[ERROR] " + t.getMessage());
			return modelAndView;
		}

		modelAndView.addObject("pageNo", this.pageNo);
		modelAndView.addObject("pageSize", this.pageSize);
		modelAndView.addObject("page", this.page);
		modelAndView.addObject("message", message);
		modelAndView.addObject("error", error);
		modelAndView.addObject("order_no_para", order_no_para);
		modelAndView.addObject("name_para", name_para);
		modelAndView.addObject("rolename_para", rolename_para);
		modelAndView.addObject("start_time", start_time);
		modelAndView.addObject("end_time", end_time);
		return modelAndView;
	}

	/**
	 * 获取 永续合约持仓单列表内容
	 */
	@RequestMapping(action + "content.action")
	public ModelAndView content(HttpServletRequest request) {
		String pageNo = request.getParameter("pageNo");
		String message = request.getParameter("message");
		String error = request.getParameter("error");
		String order_no_para = request.getParameter("order_no_para");
		String name_para = request.getParameter("name_para");
		String rolename_para = request.getParameter("rolename_para");
		String start_time = request.getParameter("start_time");
		String end_time = request.getParameter("end_time");

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("contract_order_list_content");
		
		try {
			
			this.checkAndSetPageNo(pageNo);
			
			this.pageSize = 30;
			
			String loginPartyId = getLoginPartyId();
			this.page = this.adminContractOrderService.pagedQuery(this.pageNo, this.pageSize, ContractOrder.STATE_SUBMITTED,
					rolename_para, loginPartyId, start_time, end_time, name_para, order_no_para);

			List<Map> list = this.page.getElements();
			for (int i = 0; i < list.size(); i++) {
				Map map = list.get(i);				
				if (null == map.get("rolename")) {
					map.put("roleNameDesc", "");
				} else {
					String roleName = map.get("rolename").toString();
					map.put("roleNameDesc", Constants.ROLE_MAP.containsKey(roleName) ? Constants.ROLE_MAP.get(roleName) : roleName);
				}
			}

		} catch (BusinessException e) {
			modelAndView.addObject("error", e.getMessage());
			return modelAndView;
		} catch (Throwable t) {
			logger.error(" error ", t);
			modelAndView.addObject("error", "[ERROR] " + t.getMessage());
			return modelAndView;
		}

		modelAndView.addObject("pageNo", this.pageNo);
		modelAndView.addObject("pageSize", this.pageSize);
		modelAndView.addObject("page", this.page);
		modelAndView.addObject("message", message);
		modelAndView.addObject("error", error);
		modelAndView.addObject("order_no_para", order_no_para);
		modelAndView.addObject("name_para", name_para);
		modelAndView.addObject("rolename_para", rolename_para);
		modelAndView.addObject("start_time", start_time);
		modelAndView.addObject("end_time", end_time);
		return modelAndView;
	}

	/**
	 * 显示 调整页面
	 */
	@RequestMapping(action + "showModal.action")
	public String showModal(HttpServletRequest request) {
		String symbol = request.getParameter("symbol");
		
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		try {
			
			if (!StringUtils.isNullOrEmpty(this.getLoginPartyId())) {
				throw new BusinessException("无权限");
			}		
			
			Realtime realtime = this.dataService.realtime(symbol).get(0);

			Item item = this.itemService.cacheBySymbol(symbol, false);
			
			Double currentValue = this.adjustmentValueService.getCurrentValue(symbol);
			if (null == currentValue) {
				resultMap.put("adjust_value", 0D);
				resultMap.put("new_price", realtime.getClose());
			} else {
				resultMap.put("adjust_value", currentValue);
				resultMap.put("new_price", Arith.sub(realtime.getClose(), currentValue));
			}

			resultMap.put("pips", item.getPips());

			AdjustmentValue delayValue = this.adjustmentValueService.getDelayValue(symbol);
			if (delayValue != null) {
				resultMap.put("delay_value", delayValue.getValue());
				resultMap.put("delay_second", delayValue.getSecond());
			}

		} catch (BusinessException e) {
			resultMap.put("code", 500);
			resultMap.put("message", e.getMessage());
		} catch (Throwable t) {
			logger.error(" error ", t);
			resultMap.put("code", 500);
			resultMap.put("message", "程序错误");
		}

		return JsonUtils.getJsonString(resultMap);
	}

	/**
	 * 调整 页面计算
	 * 
	 * type 0增加一个pips；1减少一个pips；2直接修改调整值；
	 * value 调整值
	 */
	@RequestMapping(action + "getValue.action")
	public String getValue(HttpServletRequest request) {
		String symbol = request.getParameter("symbol");
		String type = request.getParameter("type");
		String value = request.getParameter("value");

		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		try {
			
			if (!StringUtils.isNullOrEmpty(this.getLoginPartyId())) {
				throw new BusinessException("无权限");
			}

			String error = this.verif(type, value);
			if (!StringUtils.isNullOrEmpty(error)) {
				throw new BusinessException(error);
			}
			
			int type_int = Integer.valueOf(request.getParameter("type")).intValue();
			double value_double = Double.valueOf(request.getParameter("value")).doubleValue();
			
			Realtime realtime = this.dataService.realtime(symbol).get(0);

			Item item = this.itemService.cacheBySymbol(symbol, false);
			
			Double currentValue = this.adjustmentValueService.getCurrentValue(symbol);
			if (null == currentValue) {
				resultMap.put("new_price", realtime.getClose());
			} else {
				resultMap.put("new_price", Arith.sub(realtime.getClose(), currentValue));
			}

			double temp;
			
			if (0 == type_int) {
				temp = Arith.add(value_double, item.getPips());
				// 调整量
				resultMap.put("adjust_current_value", Double.valueOf(temp));
				// 调整后的值
				resultMap.put("adjust_value_after", Double.valueOf(Arith.add(realtime.getClose(), temp)));
			} else if (1 == type_int) {
				temp = Arith.sub(value_double, item.getPips());
				resultMap.put("adjust_current_value", Double.valueOf(temp));
				resultMap.put("adjust_value_after", Arith.add(realtime.getClose(), temp));
			} else {
				temp = value_double;
				resultMap.put("adjust_current_value", Double.valueOf(temp));
				resultMap.put("adjust_value_after", Arith.add(realtime.getClose(), temp));
			}

			if (null == currentValue) {
				resultMap.put("adjust_value", Double.valueOf(item.getPips()));
			} else {
				resultMap.put("adjust_value", Arith.add(temp, currentValue));
			}
			
			AdjustmentValue delayValue = this.adjustmentValueService.getDelayValue(symbol);
			if (delayValue != null) {
				resultMap.put("delay_value", delayValue.getValue());
				resultMap.put("delay_second", delayValue.getSecond());
			}

		} catch (BusinessException e) {
			resultMap.put("code", 500);
			resultMap.put("message", e.getMessage());
		} catch (Throwable t) {
			logger.error(" error ", t);
			resultMap.put("code", 500);
			resultMap.put("message", "程序错误");
		}

		return JsonUtils.getJsonString(resultMap);
	}

	/**
	 * 调整
	 * 
	 * value 调整值
	 * second 延迟秒
	 */
	@RequestMapping(action + "adjust.action")
	public ModelAndView adjust(HttpServletRequest request) {
		String symbol = request.getParameter("symbol");
		String value = request.getParameter("value");
		String second = request.getParameter("second");
		String username_login = request.getParameter("username_login");
		
		String order_no_para = request.getParameter("order_no_para");
		String name_para = request.getParameter("name_para");
		String rolename_para = request.getParameter("rolename_para");
		String start_time = request.getParameter("start_time");
		String end_time = request.getParameter("end_time");

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("redirect:/" + action + "list.action");
		
		try {

			String error = this.verifAdjust(second, value);
			if (!StringUtils.isNullOrEmpty(error)) {
				throw new BusinessException(error);
			}
			
			double value_double = Double.valueOf(request.getParameter("value"));
			double second_double = Double.valueOf(request.getParameter("second"));
			
			Double currentValue = this.adjustmentValueService.getCurrentValue(symbol);
			if (null == currentValue) {
				Realtime realtime = this.dataService.realtime(symbol).get(0);
				currentValue = realtime.getClose();
			}
			
			SecUser sec = this.secUserService.findUserByLoginName(this.getUsername_login());
			
			String log = MessageFormat.format("ip:" + this.getIp() + ",管理员调整行情,币种:{0},原值:{1},调整值:{2},调整时间:{3}", symbol,
					new BigDecimal(currentValue).toPlainString(), new BigDecimal(value_double).toPlainString(), second_double);

			this.adjustmentValueService.adjust(symbol, value_double, second_double);

			saveLog(sec, username_login, log);
			ThreadUtils.sleep(1000);
			
			modelAndView.addObject("order_no_para", order_no_para);
			modelAndView.addObject("name_para", name_para);
			modelAndView.addObject("rolename_para", rolename_para);
			modelAndView.addObject("start_time", start_time);
			modelAndView.addObject("end_time", end_time);

		} catch (BusinessException e) {
			modelAndView.addObject("error", e.getMessage());
			return modelAndView;
		} catch (Throwable t) {
			logger.error(" error ", t);
			modelAndView.addObject("error", "[ERROR] " + t.getMessage());
			return modelAndView;
		}

		modelAndView.addObject("message", "操作成功");
		return modelAndView;
	}

	/**
	 * 平仓或撤单
	 */
	@RequestMapping(action + "close.action")
	public ModelAndView close(HttpServletRequest request) {
		String order_no = request.getParameter("order_no");

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("redirect:/" + action + "list.action");

		try {
			
			ContractOrder order = this.contractOrderService.findByOrderNo(order_no);
			if (order != null) {
				CloseDelayThread lockDelayThread = new CloseDelayThread(order.getPartyId().toString(), order_no, this.contractOrderService);
				Thread t = new Thread(lockDelayThread);
				t.start();
			}
			
		} catch (BusinessException e) {
			modelAndView.addObject("error", e.getMessage());
			return modelAndView;
		} catch (Throwable t) {
			logger.error(" error ", t);
			modelAndView.addObject("error", "[ERROR] " + t.getMessage());
			return modelAndView;
		}

		modelAndView.addObject("message", "操作成功");
		return modelAndView;
	}

	public void saveLog(SecUser secUser, String operator, String context) {
		Log log = new Log();
		log.setCategory(Constants.LOG_CATEGORY_OPERATION);
		log.setOperator(operator);
		log.setUsername(secUser.getUsername());
		log.setPartyId(secUser.getPartyId());
		log.setLog(context);
		log.setCreateTime(new Date());
		this.logService.saveSync(log);
	}

	protected String verif(String type, String value) {
		
		if (StringUtils.isNullOrEmpty(type)) {
			return "限制天数必填";
		}
		if (!StringUtils.isInteger(type)) {
			return "限制天数输入错误，请输入整数";
		}
		if (Integer.valueOf(type).intValue() < 0) {
			return "限制天数不能小于0";
		}

		if (StringUtils.isNullOrEmpty(value)) {
			return "调整值必填";
		}
		if (!StringUtils.isDouble(value)) {
			return "调整值不是浮点数";
		}
//		if (Double.valueOf(value).doubleValue() < 0) {
//			return "调整值不能小于0";
//		}
		
		return null;
	}

	protected String verifAdjust(String second, String value) {

		if (StringUtils.isNullOrEmpty(second)) {
			return "调整时间必填";
		}
		if (!StringUtils.isDouble(second)) {
			return "调整时间不是浮点数";
		}

		if (StringUtils.isNullOrEmpty(value)) {
			return "调整值必填";
		}
		if (!StringUtils.isDouble(value)) {
			return "调整值不是浮点数";
		}
//		if (Double.valueOf(value).doubleValue() <= 0) {
//			return "调整值不能小于等于0";
//		}
		
		return null;
	}

	/**
	 * 新线程处理，直接拿到订单锁处理完成后退出
	 */
	public class CloseDelayThread implements Runnable {
		private String partyId;
		private String order_no;
		private ContractOrderService contractOrderService;

		public void run() {
			
			try {
				
				while (true) {
					
					if (this.contractOrderService.lock(order_no)) {
						this.contractOrderService.saveClose(partyId, order_no);
						// 处理完退出
						break;
					}
					ThreadUtils.sleep(500);
				}
				
			} catch (Throwable t) {
				logger.error("error:", t);
			} finally {
				this.contractOrderService.unlock(order_no);
			}
		}

		public CloseDelayThread(String partyId, String order_no, ContractOrderService contractOrderService) {
			this.partyId = partyId;
			this.order_no = order_no;
			this.contractOrderService = contractOrderService;
		}
	}

}
