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
import kernel.util.JsonUtils;
import kernel.util.StringUtils;
import kernel.web.PageActionSupport;
import project.Constants;
import project.futures.AdminFuturesOrderService;
import project.futures.FuturesOrderService;
import project.item.ItemService;
import project.item.model.Item;

/**
 * 交割合约单
 */
@RestController
public class AdminFuturesOrderController extends PageActionSupport {

	private Logger logger = LogManager.getLogger(AdminFuturesOrderController.class);

	@Autowired
	private AdminFuturesOrderService adminFuturesOrderService;
	@Autowired
	private ItemService itemService;
	@Autowired
	private FuturesOrderService futuresOrderService;

	private final String action = "normal/adminFuturesOrderAction!";

	/**
	 * 获取 交割合约单 列表
	 * 
	 * symbol_para 币种
	 * direction_para 方向
	 * volume_para 下单金额
	 * symbol_map 上线币种
	 */
	@RequestMapping(action + "list.action")
	public ModelAndView list(HttpServletRequest request) {
		String pageNo = request.getParameter("pageNo");
		String message = request.getParameter("message");
		String error = request.getParameter("error");
		String status_para = request.getParameter("status_para");
		String rolename_para = request.getParameter("rolename_para");
		String name_para = request.getParameter("name_para");		
		String order_no_para = request.getParameter("order_no_para");
		String symbol_para = request.getParameter("symbol_para");
		String direction_para = request.getParameter("direction_para");
		String volume_para = request.getParameter("volume_para");

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("futures_order_list");
		
		Map<String, String> symbol_map = new HashMap<String, String>();

		try {
			
			List<Item> items = this.itemService.cacheGetAll();			
			for (Item item : items) {
				symbol_map.put(item.getSymbol(), item.getSymbol());
			}

			this.checkAndSetPageNo(pageNo);

			this.pageSize = 30;
			
			Double volume_para_double = null;
			if (StringUtils.isNullOrEmpty(volume_para)) {
				volume_para_double = null;
			} else {
				if (!StringUtils.isDouble(volume_para)) {
					throw new BusinessException("下单金额不是浮点数");
				}
				if (Double.valueOf(volume_para).doubleValue() < 0) {
					throw new BusinessException("下单金额不能小于0");
				}
				
				volume_para_double = Double.valueOf(volume_para).doubleValue();
			}
			
			String loginPartyId = this.getLoginPartyId();
			this.page = this.adminFuturesOrderService.pagedQuery(this.pageNo, this.pageSize, status_para,
					rolename_para, loginPartyId, name_para, order_no_para, symbol_para, direction_para, volume_para_double);

			List<Map> list = this.page.getElements();
			for (int i = 0; i < list.size(); i++) {
				Map map = list.get(i);
				if (null == map.get("rolename")) {
					map.put("roleNameDesc", "");
				} else {
					String roleName = map.get("rolename").toString();
					map.put("roleNameDesc",
							Constants.ROLE_MAP.containsKey(roleName) ? Constants.ROLE_MAP.get(roleName) : roleName);
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
		modelAndView.addObject("status_para", status_para);
		modelAndView.addObject("rolename_para", rolename_para);
		modelAndView.addObject("name_para", name_para);
		modelAndView.addObject("order_no_para", order_no_para);
		modelAndView.addObject("symbol_para", symbol_para);
		modelAndView.addObject("direction_para", direction_para);
		modelAndView.addObject("volume_para", volume_para);
		modelAndView.addObject("symbol_map", symbol_map);
		return modelAndView;
	}

	/**
	 * 获取 交割合约单 列表
	 * 
	 * symbol_para 币种
	 * direction_para 方向
	 * volume_para 下单金额
	 */
	@RequestMapping(action + "holdings_list.action")
	public ModelAndView holdings_list(HttpServletRequest request) {
		String pageNo = request.getParameter("pageNo");
		String message = request.getParameter("message");
		String error = request.getParameter("error");
		String rolename_para = request.getParameter("rolename_para");
		String name_para = request.getParameter("name_para");		
		String order_no_para = request.getParameter("order_no_para");
		String symbol_para = request.getParameter("symbol_para");
		String direction_para = request.getParameter("direction_para");
		String volume_para = request.getParameter("volume_para");

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("futures_holdings_list");

		try {

			this.checkAndSetPageNo(pageNo);

			this.pageSize = 30;
			
			if (StringUtils.isNullOrEmpty(volume_para)) {
				throw new BusinessException("下单金额必填");
			}
			if (!StringUtils.isDouble(volume_para)) {
				throw new BusinessException("下单金额不是浮点数");
			}
			if (Double.valueOf(volume_para).doubleValue() <= 0) {
				throw new BusinessException("下单金额不能小于等于0");
			}
			
			double volume_para_double = Double.valueOf(volume_para).doubleValue();
			
			String loginPartyId = this.getLoginPartyId();
			this.page = this.adminFuturesOrderService.pagedQuery(this.pageNo, this.pageSize, "submitted",
					rolename_para, loginPartyId, name_para, order_no_para, symbol_para, direction_para, volume_para_double);

			List<Map> list = this.page.getElements();
			for (int i = 0; i < list.size(); i++) {
				Map map = list.get(i);
				if (null == map.get("rolename")) {
					map.put("roleNameDesc", "");
				} else {
					String roleName = map.get("rolename").toString();
					map.put("roleNameDesc",
							Constants.ROLE_MAP.containsKey(roleName) ? Constants.ROLE_MAP.get(roleName) : roleName);
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
		modelAndView.addObject("rolename_para", rolename_para);
		modelAndView.addObject("name_para", name_para);
		modelAndView.addObject("order_no_para", order_no_para);
		modelAndView.addObject("symbol_para", symbol_para);
		modelAndView.addObject("direction_para", direction_para);
		modelAndView.addObject("volume_para", volume_para);
		return modelAndView;
	}

	/**
	 * ajax定时获取表
	 * 
	 * symbol_para 币种
	 * direction_para 方向
	 * volume_para 下单金额
	 */
	@RequestMapping(action + "getValue.action")
	public String getValue(HttpServletRequest request) {
		String pageNo = request.getParameter("pageNo");
		String rolename_para = request.getParameter("rolename_para");
		String name_para = request.getParameter("name_para");		
		String order_no_para = request.getParameter("order_no_para");
		String symbol_para = request.getParameter("symbol_para");
		String direction_para = request.getParameter("direction_para");
		String volume_para = request.getParameter("volume_para");
		
		try {

			this.checkAndSetPageNo(pageNo);

			this.pageSize = 30;
			
			if (StringUtils.isNullOrEmpty(volume_para)) {
				throw new BusinessException("下单金额必填");
			}
			if (!StringUtils.isDouble(volume_para)) {
				throw new BusinessException("下单金额不是浮点数");
			}
			if (Double.valueOf(volume_para).doubleValue() <= 0) {
				throw new BusinessException("下单金额不能小于等于0");
			}
			
			double volume_para_double = Double.valueOf(volume_para).doubleValue();
			
			String loginPartyId = this.getLoginPartyId();
			this.page = this.adminFuturesOrderService.pagedQuery(this.pageNo, this.pageSize, "submitted",
					rolename_para, loginPartyId, name_para, order_no_para, symbol_para, direction_para, volume_para_double);

		} catch (BusinessException e) {
			return JsonUtils.getJsonString(new ArrayList());
		} catch (Throwable t) {
			logger.error(" error ", t);
			return JsonUtils.getJsonString(new ArrayList());
		}

		return JsonUtils.getJsonString(this.page.getElements());
	}

	/**
	 * orderProfitLoss
	 * 
	 * profit_loss 盈利还是亏损
	 */
	@RequestMapping(action + "orderProfitLoss.action")
	public ModelAndView orderProfitLoss(HttpServletRequest request) {
		String order_no = request.getParameter("order_no");
		String profit_loss = request.getParameter("profit_loss");

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("redirect:/" + action + "list.action");
		
		try {
			
			String error = this.futuresOrderService.saveOrderPorfitOrLoss(order_no, profit_loss, this.getUsername_login());
			if (StringUtils.isNotEmpty(error)) {
				throw new BusinessException(error);
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

}
