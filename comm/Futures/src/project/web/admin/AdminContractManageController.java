package project.web.admin;

import java.util.ArrayList;
import java.util.HashMap;
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
import kernel.util.PropertiesUtil;
import kernel.util.StringUtils;
import kernel.web.PageActionSupport;
import project.futures.AdminContractManageService;
import project.futures.AdminFuturesParaService;
import project.futures.FuturesPara;
import project.futures.FuturesPara.TIMENUM;
import project.item.AdminItemService;
import project.item.model.Item;

/**
 * 交割合约管理
 */
@RestController
public class AdminContractManageController extends PageActionSupport {

	private Logger logger = LogManager.getLogger(AdminContractManageController.class);

	@Autowired
	private AdminContractManageService adminContractManageService;
	@Autowired
	private AdminItemService adminItemService;
	@Autowired
	private AdminFuturesParaService adminFuturesParaService;
//	@Autowired
//	private ItemService itemService;

	private final String action = "normal/adminContractManageAction!";

	/**
	 * 获取 交易对 列表
	 */
	@RequestMapping(action + "list.action")
	public ModelAndView list(HttpServletRequest request) {
		String pageNo = request.getParameter("pageNo");
		String message = request.getParameter("message");
		String error = request.getParameter("error");
		String para_symbol = request.getParameter("para_symbol");

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("contract_manage_list");
		
		Map<String, Object> contractResult = new HashMap<String, Object>();

		try {
			
			this.checkAndSetPageNo(pageNo);
			
			this.pageSize = 10;
			
			List<Item> itemsRet = new ArrayList<Item>();
			
			List<Item> items = this.adminItemService.getItems();
			
			if (StringUtils.isEmptyString(para_symbol)) {
				itemsRet = items;
			} else {
				for (int i = 0; i < items.size(); i++) {
					Item item = items.get(i);
					if (item.getSymbol().equalsIgnoreCase(para_symbol)) {
						itemsRet.add(item);
					}
				}				
			}
			
			contractResult.put("contractItems", itemsRet);
			
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
//		modelAndView.addObject("page", this.page);
		modelAndView.addObject("message", message);
		modelAndView.addObject("error", error);
		modelAndView.addObject("para_symbol", para_symbol);
		modelAndView.addObject("contractResult", contractResult);
		return modelAndView;
	}

	/**
	 * 获取 交易参数 列表
	 */
	@RequestMapping(action + "listPara.action")
	public ModelAndView listPara(HttpServletRequest request) {
		String pageNo = request.getParameter("pageNo");
		String message = request.getParameter("message");
		String error = request.getParameter("error");
		String query_symbol = request.getParameter("query_symbol");

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("contract_para_list");
		
		Map<String, Object> contractResult = new HashMap<String, Object>();

		try {
			
			this.checkAndSetPageNo(pageNo);
			
			this.pageSize = 10;
			
			contractResult.put("futures", this.adminFuturesParaService.pagedQuery(this.pageNo, this.pageSize, query_symbol));

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
//		modelAndView.addObject("page", this.page);
		modelAndView.addObject("message", message);
		modelAndView.addObject("error", error);
		modelAndView.addObject("query_symbol", query_symbol);
		modelAndView.addObject("contractResult", contractResult);
		return modelAndView;
	}
	
//	public String toAdd() {
//		basePath = getPath(ServletActionContext.getRequest());
//		if (StringUtils.isNotEmpty(itemId)) {
//			Item item = adminItemService.get(itemId);
//			this.itemId = item.getId().toString();
//			this.name = item.getName();
//			this.symbol = item.getSymbol();
//			this.decimals = item.getDecimals();
//			this.symbol_data = item.getSymbol_data();
//		}
//		return "add";
//	}
	
//	public String addContractItem() {
//		try {
//			this.error = verifyItem();
//			if (StringUtils.isNotEmpty(this.error))
//				return toAdd();
//			Item entity = new Item();
//			entity.setId(this.itemId);
//			entity.setName(name);
//			entity.setSymbol(symbol);
//			entity.setDecimals(decimals);
//			entity.setSymbol_data(symbol_data);
//			this.error = adminContractManageService.addContractItem(entity);
//		} catch (BusinessException e) {
//			this.error = e.getMessage();
//		}
//		return StringUtils.isNotEmpty(error) ? toAdd() : list();
//	}
	
	/**
	 * 新增/修改 交易参数 页面
	 */
	@RequestMapping(action + "toAddInstall.action")
	public ModelAndView toAddInstall(HttpServletRequest request) {
		String futuresId = request.getParameter("futuresId");
		String query_symbol = request.getParameter("query_symbol");

		ModelAndView modelAndView = new ModelAndView();
		
		try {
			
			String basePath = PropertiesUtil.getProperty("admin_url");
			basePath = this.getPath(request);
			
			Map<String, String> symbolMap = new HashMap<String, String>();
			symbolMap = this.adminContractManageService.getFuturesSymbols();
			
			FuturesPara futuresPara = new FuturesPara();
			if (StringUtils.isNotEmpty(futuresId)) {
				// 修改
				
				futuresPara = this.adminFuturesParaService.getById(futuresId);
				if (null == futuresPara) {
					throw new BusinessException("交易参数不存在");
				}
				
				futuresPara.setProfit_ratio(Arith.mul(futuresPara.getProfit_ratio(), 100));
				futuresPara.setProfit_ratio_max(Arith.mul(futuresPara.getProfit_ratio_max(), 100));
				futuresPara.setUnit_fee(Arith.mul(futuresPara.getUnit_fee(), 100));

				futuresPara.setTimeUnitCn(TIMENUM.valueOf(futuresPara.getTimeUnit()).getCn());

				modelAndView.addObject("futuresId", futuresId);
				modelAndView.addObject("query_symbol", query_symbol);
				modelAndView.addObject("symbolMap", symbolMap);
				
				modelAndView.addObject("futuresPara_id", futuresPara.getId());
				modelAndView.addObject("futuresPara_symbol", futuresPara.getSymbol());
				modelAndView.addObject("futuresPara_timeNum", futuresPara.getTimeNum());
				modelAndView.addObject("futuresPara_timeUnit", futuresPara.getTimeUnit());
				modelAndView.addObject("futuresPara_profit_ratio", futuresPara.getProfit_ratio());
				modelAndView.addObject("futuresPara_profit_ratio_max", futuresPara.getProfit_ratio_max());
				modelAndView.addObject("futuresPara_unit_amount", futuresPara.getUnit_amount());
				modelAndView.addObject("futuresPara_unit_fee", futuresPara.getUnit_fee());
				modelAndView.addObject("futuresPara_timeUnitCn", futuresPara.getTimeUnitCn());
				modelAndView.addObject("futuresPara_unit_max_amount", futuresPara.getUnit_max_amount());
				
			} else {
				// 新增
				
				if (StringUtils.isEmptyString(query_symbol)) {
					throw new BusinessException("请选择合约代码");
				}
				
				modelAndView.addObject("futuresId", futuresId);
				modelAndView.addObject("query_symbol", query_symbol);
				modelAndView.addObject("symbolMap", symbolMap);
			}
			
		} catch (BusinessException e) {
			modelAndView.addObject("error", e.getMessage());
			modelAndView.setViewName("redirect:/" + action + "list.action");
			return modelAndView;
		} catch (Throwable t) {
			logger.error(" error ", t);
			modelAndView.addObject("error", "[ERROR] " + t.getMessage());
			modelAndView.setViewName("redirect:/" + action + "list.action");
			return modelAndView;
		}
		
		modelAndView.setViewName("contract_install_add");
		return modelAndView;
	}
	
	/**
	 * 新增/修改 交易参数
	 */
	@RequestMapping(action + "addFutures.action")
	public ModelAndView addFutures(HttpServletRequest request) {
		String futuresId = request.getParameter("futuresId");
		String query_symbol = request.getParameter("query_symbol");
		String futuresPara_id = request.getParameter("futuresPara_id");
		String futuresPara_symbol = request.getParameter("futuresPara_symbol");
		String futuresPara_timeNum = request.getParameter("futuresPara_timeNum");
		String futuresPara_timeUnit = request.getParameter("futuresPara_timeUnit");
		String futuresPara_profit_ratio = request.getParameter("futuresPara_profit_ratio");
		String futuresPara_profit_ratio_max = request.getParameter("futuresPara_profit_ratio_max");
		String futuresPara_unit_amount = request.getParameter("futuresPara_unit_amount");
		String futuresPara_unit_fee = request.getParameter("futuresPara_unit_fee");
		String futuresPara_timeUnitCn = request.getParameter("futuresPara_timeUnitCn");
		String futuresPara_unit_max_amount = request.getParameter("futuresPara_unit_max_amount");
		String login_safeword = request.getParameter("login_safeword");

		ModelAndView modelAndView = new ModelAndView();
		
		Map<String, String> symbolMap = new HashMap<String, String>();
		symbolMap = this.adminContractManageService.getFuturesSymbols();

		try {
			
			if (StringUtils.isNullOrEmpty(futuresPara_unit_max_amount)) {
				futuresPara_unit_max_amount = "0";
			}

			String error = this.verification(query_symbol, futuresPara_timeNum, futuresPara_timeUnit, futuresPara_unit_amount, 
					futuresPara_unit_fee, futuresPara_profit_ratio, futuresPara_profit_ratio_max, futuresPara_unit_max_amount);
			if (StringUtils.isNotEmpty(error)) {
				throw new BusinessException(error);
			}
			
			int futuresPara_timeNum_int = Integer.valueOf(futuresPara_timeNum).intValue();
			double futuresPara_unit_amount_double = Double.valueOf(futuresPara_unit_amount).doubleValue();
			double futuresPara_unit_fee_double = Double.valueOf(futuresPara_unit_fee).doubleValue();
			double futuresPara_profit_ratio_double = Double.valueOf(futuresPara_profit_ratio).doubleValue();
			double futuresPara_profit_ratio_max_double = Double.valueOf(futuresPara_profit_ratio_max).doubleValue();
			double futuresPara_unit_max_amount_double = Double.valueOf(futuresPara_unit_max_amount).doubleValue();

			FuturesPara futuresPara = new FuturesPara();
			futuresPara.setId(futuresPara_id);
			futuresPara.setSymbol(futuresPara_symbol);
			futuresPara.setTimeNum(futuresPara_timeNum_int);
			futuresPara.setTimeUnit(futuresPara_timeUnit);
			futuresPara.setProfit_ratio(Arith.div(futuresPara_profit_ratio_double, 100));
			futuresPara.setProfit_ratio_max(Arith.div(futuresPara_profit_ratio_max_double, 100));
			futuresPara.setUnit_amount(futuresPara_unit_amount_double);
			futuresPara.setUnit_fee(Arith.div(futuresPara_unit_fee_double, 100));
			futuresPara.setTimeUnitCn(futuresPara_timeUnitCn);
			futuresPara.setUnit_max_amount(futuresPara_unit_max_amount_double);
						
			String error1 = this.adminContractManageService.addFutures(futuresPara, this.getIp(), this.getUsername_login(), login_safeword);
			if (StringUtils.isNotEmpty(error1)) {
				throw new BusinessException(error1);
			}

		} catch (BusinessException e) {
			modelAndView.addObject("error", e.getMessage());
			modelAndView.addObject("futuresId", futuresId);
			modelAndView.addObject("query_symbol", query_symbol);
			modelAndView.addObject("symbolMap", symbolMap);			
			modelAndView.addObject("futuresPara_id", futuresPara_id);
			modelAndView.addObject("futuresPara_symbol", futuresPara_symbol);
			modelAndView.addObject("futuresPara_timeNum", futuresPara_timeNum);
			modelAndView.addObject("futuresPara_timeUnit", futuresPara_timeUnit);
			modelAndView.addObject("futuresPara_profit_ratio", futuresPara_profit_ratio);
			modelAndView.addObject("futuresPara_profit_ratio_max", futuresPara_profit_ratio_max);
			modelAndView.addObject("futuresPara_unit_amount", futuresPara_unit_amount);
			modelAndView.addObject("futuresPara_unit_fee", futuresPara_unit_fee);
			modelAndView.addObject("futuresPara_timeUnitCn", futuresPara_timeUnitCn);
			modelAndView.addObject("futuresPara_unit_max_amount", futuresPara_unit_max_amount);
			modelAndView.setViewName("contract_install_add");
			return modelAndView;
		} catch (Throwable t) {
			logger.error(" error ", t);
			modelAndView.addObject("error", "[ERROR] " + t.getMessage());
			modelAndView.addObject("futuresId", futuresId);
			modelAndView.addObject("query_symbol", query_symbol);
			modelAndView.addObject("symbolMap", symbolMap);			
			modelAndView.addObject("futuresPara_id", futuresPara_id);
			modelAndView.addObject("futuresPara_symbol", futuresPara_symbol);
			modelAndView.addObject("futuresPara_timeNum", futuresPara_timeNum);
			modelAndView.addObject("futuresPara_timeUnit", futuresPara_timeUnit);
			modelAndView.addObject("futuresPara_profit_ratio", futuresPara_profit_ratio);
			modelAndView.addObject("futuresPara_profit_ratio_max", futuresPara_profit_ratio_max);
			modelAndView.addObject("futuresPara_unit_amount", futuresPara_unit_amount);
			modelAndView.addObject("futuresPara_unit_fee", futuresPara_unit_fee);
			modelAndView.addObject("futuresPara_timeUnitCn", futuresPara_timeUnitCn);
			modelAndView.addObject("futuresPara_unit_max_amount", futuresPara_unit_max_amount);
			modelAndView.setViewName("contract_install_add");
			return modelAndView;
		}
		
		modelAndView.addObject("message", "操作成功");
		modelAndView.addObject("query_symbol", query_symbol);
		modelAndView.setViewName("redirect:/" + action + "listPara.action");
		return modelAndView;
	}

	/**
	 * 删除 交易参数
	 */
	@RequestMapping(action + "toDeleteFuturesPara.action")
	public ModelAndView toDeleteFuturesPara(HttpServletRequest request) {
		String futuresId = request.getParameter("futuresId");
		String login_safeword = request.getParameter("login_safeword");
		String super_google_auth_code = request.getParameter("super_google_auth_code");

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("redirect:/" + action + "listPara.action");
		
		try {

			this.adminContractManageService.deleteFuturesPara(futuresId, this.getIp(), this.getUsername_login(), login_safeword, super_google_auth_code);

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

	private String verification(String symbol, String timeNum, String timeUnit, String unit_amount, String unit_fee, 
			String profit_ratio, String profit_ratio_max, String unit_max_amount) {
		
		if (StringUtils.isEmptyString(symbol)) {
			return "请选择合约代码";
		}
		
		if (StringUtils.isNullOrEmpty(timeNum)) {
			return "时间必填";
		}
		if (!StringUtils.isInteger(timeNum)) {
			return "时间不是整数";
		}
		if (Integer.valueOf(timeNum).intValue() <= 0) {
			return "时间不能小于等于0";
		}

		if (StringUtils.isEmptyString(timeUnit)) {
			return "请选择时间单位";
		}
		
		if (StringUtils.isNullOrEmpty(unit_amount)) {
			return "每手金额必填";
		}
		if (!StringUtils.isDouble(unit_amount)) {
			return "每手金额不是浮点数";
		}
		if (Double.valueOf(unit_amount).doubleValue() <= 0) {
			return "每手金额不能小于等于0";
		}
		
		if (StringUtils.isNullOrEmpty(unit_amount)) {
			return "手续费必填";
		}
		if (!StringUtils.isDouble(unit_amount)) {
			return "手续费不是浮点数";
		}
		if (Double.valueOf(unit_amount).doubleValue() <= 0) {
			return "手续费不能小于等于0";
		}
		
		if (StringUtils.isNullOrEmpty(profit_ratio)) {
			return "最小收益率必填";
		}
		if (!StringUtils.isDouble(profit_ratio)) {
			return "最小收益率不是浮点数";
		}
		if (Double.valueOf(profit_ratio).doubleValue() <= 0) {
			return "最小收益率不能小于等于0";
		}
		
		if (StringUtils.isNullOrEmpty(profit_ratio_max)) {
			return "最大收益率必填";
		}
		if (!StringUtils.isDouble(profit_ratio_max)) {
			return "最大收益率不是浮点数";
		}
		if (Double.valueOf(profit_ratio_max).doubleValue() <= 0) {
			return "最大收益率不能小于等于0";
		}
		
		if (StringUtils.isNullOrEmpty(unit_max_amount)) {
			return "最高购买金额必填";
		}
		if (!StringUtils.isDouble(unit_max_amount)) {
			return "最高购买金额不是浮点数";
		}
//		if (Double.valueOf(unit_max_amount).doubleValue() <= 0) {
//			return "最高购买金额不能小于等于0";
//		}
		double unit_amount_double = Double.valueOf(unit_amount).doubleValue();
		double unit_max_amount_double = Double.valueOf(unit_max_amount).doubleValue();
		if (unit_max_amount_double != 0 && unit_max_amount_double < unit_amount_double) {
			return "最高购买金额需大于最低购买金额";
		}
		
		return null;
	}

//	private String verifyItem() {
//		if (StringUtils.isEmpty(this.name))
//			return "合约名称不能为空";
//		if (StringUtils.isEmpty(this.symbol))
//			return "代码不能为空";
//		if (StringUtils.isEmpty(this.symbol_data))
//			return "请选择交易对";
//		return null;
//	}

	private String getPath(HttpServletRequest request) {
		return String.format("%s://%s:%s%s", request.getScheme(), request.getServerName(),
				(request.getServerPort() == 80?"":request.getServerPort()), request.getContextPath());
	}

}
