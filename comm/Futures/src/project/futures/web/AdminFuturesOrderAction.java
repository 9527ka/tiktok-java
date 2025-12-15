package project.futures.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import kernel.exception.BusinessException;
import kernel.util.JsonUtils;
import kernel.web.PageActionSupport;
import project.Constants;
import project.futures.AdminFuturesOrderService;
import project.futures.FuturesOrder;
import project.futures.FuturesOrderService;
import project.item.ItemService;
import project.item.model.Item;

public class AdminFuturesOrderAction extends PageActionSupport {

	private static final long serialVersionUID = -2826063509608077114L;

	private static Log logger = LogFactory.getLog(AdminFuturesOrderAction.class);

	/**
	 * 所有item
	 */
	private List<Item> items;
	private String symbol;
	private String order_no;

	/**
	 * 查询参数
	 */
	private String rolename_para = Constants.SECURITY_ROLE_MEMBER;
	private String name_para;
	private String status_para;
	private String order_no_para;

	private AdminFuturesOrderService adminFuturesOrderService;
	private ItemService itemService;

	private FuturesOrderService futuresOrderService;
	/*
	 * 页面json返回值
	 */
	private String result_make;
	/**
	 * 盈利还是亏损
	 */
	private String profit_loss;
	/**
	 * 币种
	 */
	private String symbol_para;
	/**
	 * 下单金额
	 */
	private Double volume_para;
	/**
	 * 方向
	 */
	private String direction_para;
	
	/**
	 * 上线币种
	 */
	private Map<String, String> symbol_map = new HashMap<String, String>();

	public String list() {
		for(Item item:itemService.cacheGetAll()) {
			symbol_map.put(item.getSymbol(), item.getSymbol());
		}
		
		this.pageSize = 30;
		String loginPartyId = getLoginPartyId();
		this.page = this.adminFuturesOrderService.pagedQuery(this.pageNo, this.pageSize, this.status_para,
				this.rolename_para, loginPartyId, this.name_para,order_no_para,symbol_para,direction_para,volume_para);
		this.items = this.itemService.cacheGetAll();

		return "list";
	}

	public String holdings_list() {
		this.pageSize = 30;
		String loginPartyId = getLoginPartyId();
		this.page = this.adminFuturesOrderService.pagedQuery(this.pageNo, this.pageSize, "submitted",
				this.rolename_para, loginPartyId, this.name_para,order_no_para,symbol_para,direction_para,volume_para);
		this.items = this.itemService.cacheGetAll();

		return "holdings";
	}

	/**
	 * 
	 * ajax定时获取表
	 */
	public String getValue() {

		this.pageSize = 30;
		String loginPartyId = getLoginPartyId();
		this.page = this.adminFuturesOrderService.pagedQuery(this.pageNo, this.pageSize, "submitted",
				this.rolename_para, loginPartyId, this.name_para,order_no_para,symbol_para,direction_para,volume_para);
		this.items = this.itemService.cacheGetAll();

		this.result_make = JsonUtils.getJsonString(this.page.getElements());
		return "result_make";
	}

	/**
	 * 平仓或撤单
	 */
	public String close() {
		try {
//			FuturesOrder order = futuresOrderService.findByOrderNo(order_no);

//			this.message = "操作成功";
		} catch (BusinessException e) {
			this.error = e.getMessage();
		} catch (Exception e) {
			logger.error("error ", e);
			this.error = "程序错误";
		}
		return list();
	}

	public String orderProfitLoss() {
		try {
			this.error = futuresOrderService.saveOrderPorfitOrLoss(order_no, profit_loss,this.getUsername_login());
			if(StringUtils.isEmpty(error)) {
				this.message = "操作成功";
			}
		} catch (BusinessException e) {
			this.error = e.getMessage();
		} catch (Exception e) {
			logger.error("error ", e);
			this.error = "程序错误";
		}
		return list();
	}
	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public String getOrder_no() {
		return order_no;
	}

	public void setOrder_no(String order_no) {
		this.order_no = order_no;
	}

	public String getRolename_para() {
		return rolename_para;
	}

	public void setRolename_para(String rolename_para) {
		this.rolename_para = rolename_para;
	}

	public List<Item> getItems() {
		return items;
	}

	public String getResult_make() {
		return result_make;
	}

	public void setItemService(ItemService itemService) {
		this.itemService = itemService;
	}

	public void setFuturesOrderService(FuturesOrderService futuresOrderService) {
		this.futuresOrderService = futuresOrderService;
	}

	public String getStatus_para() {
		return status_para;
	}

	public void setStatus_para(String status_para) {
		this.status_para = status_para;
	}

	public void setAdminFuturesOrderService(AdminFuturesOrderService adminFuturesOrderService) {
		this.adminFuturesOrderService = adminFuturesOrderService;
	}

	public String getName_para() {
		return name_para;
	}

	public void setName_para(String name_para) {
		this.name_para = name_para;
	}

	public String getOrder_no_para() {
		return order_no_para;
	}

	public void setOrder_no_para(String order_no_para) {
		this.order_no_para = order_no_para;
	}

	public String getProfit_loss() {
		return profit_loss;
	}

	public void setProfit_loss(String profit_loss) {
		this.profit_loss = profit_loss;
	}

	public String getSymbol_para() {
		return symbol_para;
	}

	public Map<String, String> getSymbol_map() {
		return symbol_map;
	}

	public void setSymbol_para(String symbol_para) {
		this.symbol_para = symbol_para;
	}


	public void setSymbol_map(Map<String, String> symbol_map) {
		this.symbol_map = symbol_map;
	}

	public String getDirection_para() {
		return direction_para;
	}

	public void setDirection_para(String direction_para) {
		this.direction_para = direction_para;
	}

	public Double getVolume_para() {
		return volume_para;
	}

	public void setVolume_para(Double volume_para) {
		this.volume_para = volume_para;
	}

	
}
