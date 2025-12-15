
package project.futures.web;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
// import org.apache.struts2.ServletActionContext;

import kernel.exception.BusinessException;
import kernel.util.Arith;
import kernel.util.PropertiesUtil;
import kernel.web.PageActionSupport;
//import project.data.internal.KlineInitService;
import project.futures.AdminContractManageService;
import project.futures.AdminFuturesParaService;
import project.futures.FuturesPara;
import project.futures.FuturesPara.TIMENUM;
//import project.futures.utils.ValidResult;
//import project.futures.utils.ValidationUtil;
import project.item.AdminItemService;
//import project.log.AdminLogService;
import project.item.ItemService;
import project.item.model.Item;

/**
 * 合约产品
 *
 */
public class AdminContractManageAction extends PageActionSupport {
	private static final long serialVersionUID = 1749530978470763030L;
	private static final Log logger = LogFactory.getLog(AdminContractManageAction.class);
	private AdminContractManageService adminContractManageService;
	private AdminItemService adminItemService;
	private AdminFuturesParaService adminFuturesParaService;
	private ItemService itemService;

//	private KlineInitService klineInitService;

	private Map<String, Object> contractResult = new HashMap<String, Object>();
	/**
	 * 代码列表
	 */
	private Map<String, String> symbolMap = new HashMap<String, String>();

	private FuturesPara futuresPara = new FuturesPara();
	/**
	 * 查询
	 */

	private String itemId;

	private String futuresId;

	private String basePath = PropertiesUtil.getProperty("admin_url");

	private String query_symbol;

	/**
	 * 名称
	 */
	private String name;
	/**
	 * 代码
	 */
	private String symbol;
	/**
	 * 交易对
	 */
	private String symbol_data;
	/**
	 * 精度
	 */
	private Integer decimals;
	/**
	 * 初始化k线图代码
	 */
	private String para_init_symbol;
	private String login_safeword;

	private String super_google_auth_code;

//	public String list() {
//		HttpServletResponse response = ServletActionContext.getResponse();
//		response.setContentType("application/json;charset=UTF-8");
//		response.setHeader("Access-Control-Allow-Origin", "*");
//		response.setHeader("Cache-Control", "no-cache");
//
//		this.pageSize = 10;
//		contractResult.put("contractItems", this.adminItemService.getItems());
//		return "list";
//	}
//
//	public String listPara() {
//		HttpServletResponse response = ServletActionContext.getResponse();
//		response.setContentType("application/json;charset=UTF-8");
//		response.setHeader("Access-Control-Allow-Origin", "*");
//		response.setHeader("Cache-Control", "no-cache");
//
//		this.pageSize = 10;
//		contractResult.put("futures", adminFuturesParaService.pagedQuery(pageNo, pageSize, query_symbol));
//		return "listPara";
//	}
//
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

	private String getPath(HttpServletRequest request) {
		return String.format("%s://%s:%s%s", request.getScheme(), request.getServerName(),
				(request.getServerPort() == 80?"":request.getServerPort()),
				request.getContextPath());
	}

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

//	public String toAddInstall() {
//		basePath = getPath(ServletActionContext.getRequest());
//		symbolMap = this.adminContractManageService.getFuturesSymbols();
//		if (StringUtils.isNotEmpty(futuresId)) {
//			futuresPara = adminFuturesParaService.getById(futuresId);
//			if (null == futuresPara) {
//				this.error = "交易参数不存在";
//				return list();
//			}
//			futuresPara.setProfit_ratio(Arith.mul(futuresPara.getProfit_ratio(), 100));
//			futuresPara.setProfit_ratio_max(Arith.mul(futuresPara.getProfit_ratio_max(), 100));
//			futuresPara.setUnit_fee(Arith.mul(futuresPara.getUnit_fee(), 100));
//
//			futuresPara.setTimeUnitCn(TIMENUM.valueOf(futuresPara.getTimeUnit()).getCn());
//		} else if (StringUtils.isEmpty(verification(futuresPara))) {
//			futuresPara.setProfit_ratio(Arith.mul(futuresPara.getProfit_ratio(), 100));
//			futuresPara.setProfit_ratio_max(Arith.mul(futuresPara.getProfit_ratio_max(), 100));
//			futuresPara.setUnit_fee(Arith.mul(futuresPara.getUnit_fee(), 100));
//		}
//		return "toAddInstall";
//	}

//	public String addFutures() {
//		try {
//			this.error = verification(futuresPara);
//			if (StringUtils.isNotEmpty(error))
//				return toAddInstall();
//
//			// 序列号的id接收会转变为数组
//			if (futuresPara != null && futuresPara.getId() != null
//					&& StringUtils.isNotEmpty(futuresPara.getId().toString()))
//				futuresPara.setId(((String[]) futuresPara.getId())[0]);
//			futuresPara.setProfit_ratio(Arith.div(futuresPara.getProfit_ratio(), 100));
//			futuresPara.setProfit_ratio_max(Arith.div(futuresPara.getProfit_ratio_max(), 100));
//			futuresPara.setUnit_fee(Arith.div(futuresPara.getUnit_fee(), 100));
//			this.error = adminContractManageService.addFutures(futuresPara, this.getIp(), this.getUsername_login(),
//					this.login_safeword);
//			this.message = "操作成功";
//		} catch (BusinessException e) {
//			this.error = e.getMessage();
//		} catch (Exception e) {
//			this.error = e.getMessage();
//		}
//		return StringUtils.isNotEmpty(error) ? toAddInstall() : listPara();
//	}
//
//	public String toDeleteFuturesPara() {
//		try {
////			if(!"root".equals(this.getUsername_login())) {
////				throw new BusinessException("权限不足");
////			}
//			this.adminContractManageService.deleteFuturesPara(this.futuresId, this.getIp(), this.getUsername_login(),
//					this.login_safeword, this.super_google_auth_code);
//			this.message = "操作成功";
//		} catch (BusinessException e) {
//			this.error = e.getMessage();
//		} catch (Exception t) {
//			logger.error("delete error ", t);
//			this.error = "程序错误";
//		}
//		return listPara();
//	}

	private String verification(FuturesPara futuresPara) {
		if (StringUtils.isEmpty(futuresPara.getSymbol()))
			return "请选择合约代码";
		if (futuresPara.getTimeNum() < 0)
			return "请填写正确的时间";
		if (StringUtils.isEmpty(futuresPara.getTimeUnit()))
			return "请选择时间单位";
		if (futuresPara.getUnit_amount() < 0)
			return "请填写正确每手金额";
		if (futuresPara.getUnit_fee() < 0)
			return "请填写正确手续费";
		if (futuresPara.getProfit_ratio() < 0)
			return "请填写正确收益率";
		if (futuresPara.getUnit_max_amount() != 0 && futuresPara.getUnit_max_amount() < futuresPara.getUnit_amount())
			return "最高购买金额需大于最低购买金额";
		return null;
	}

	private String verifyItem() {
		if (StringUtils.isEmpty(this.name))
			return "合约名称不能为空";
		if (StringUtils.isEmpty(this.symbol))
			return "代码不能为空";
		if (StringUtils.isEmpty(this.symbol_data))
			return "请选择交易对";
		return null;
	}

	public String getItemId() {
		return itemId;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}

	public AdminContractManageService getAdminContractManageService() {
		return adminContractManageService;
	}

	public void setAdminContractManageService(AdminContractManageService adminContractManageService) {
		this.adminContractManageService = adminContractManageService;
	}

	public void setBasePath(String basePath) {
		this.basePath = basePath;
	}

	public String getBasePath() {
		return basePath;
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public Map<String, Object> getContractResult() {
		return contractResult;
	}

	public void setContractResult(Map<String, Object> contractResult) {
		this.contractResult = contractResult;
	}

	public AdminItemService getAdminItemService() {
		return adminItemService;
	}

	public void setAdminItemService(AdminItemService adminItemService) {
		this.adminItemService = adminItemService;
	}

	public String getFuturesId() {
		return futuresId;
	}

	public void setFuturesId(String futuresId) {
		this.futuresId = futuresId;
	}

	public AdminFuturesParaService getAdminFuturesParaService() {
		return adminFuturesParaService;
	}

	public void setAdminFuturesParaService(AdminFuturesParaService adminFuturesParaService) {
		this.adminFuturesParaService = adminFuturesParaService;
	}

	public Map<String, String> getSymbolMap() {
		return symbolMap;
	}

	public void setSymbolMap(Map<String, String> symbolMap) {
		this.symbolMap = symbolMap;
	}

	public FuturesPara getFuturesPara() {
		return futuresPara;
	}

	public void setFuturesPara(FuturesPara futuresPara) {
		this.futuresPara = futuresPara;
	}

	public String getQuery_symbol() {
		return query_symbol;
	}

	public void setQuery_symbol(String query_symbol) {
		this.query_symbol = query_symbol;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSymbol_data() {
		return symbol_data;
	}

	public void setSymbol_data(String symbol_data) {
		this.symbol_data = symbol_data;
	}

	public Integer getDecimals() {
		return decimals;
	}

	public void setDecimals(Integer decimals) {
		this.decimals = decimals;
	}

	public void setItemService(ItemService itemService) {
		this.itemService = itemService;
	}

	public void setLogin_safeword(String login_safeword) {
		this.login_safeword = login_safeword;
	}

	public void setSuper_google_auth_code(String super_google_auth_code) {
		this.super_google_auth_code = super_google_auth_code;
	}

}
