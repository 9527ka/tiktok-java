package project.web.admin;

import java.math.BigDecimal;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.providers.encoding.PasswordEncoder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import kernel.exception.BusinessException;
import kernel.util.Arith;
import kernel.util.PropertiesUtil;
import kernel.util.StringUtils;
import kernel.web.PageActionSupport;
import project.Constants;
import project.data.internal.KlineInitService;
import project.item.AdminItemService;
import project.item.ItemService;
import project.item.model.Item;
import project.log.Log;
import project.log.LogService;
import project.syspara.Syspara;
import project.syspara.SysparaService;
import security.SecUser;
import security.internal.SecUserService;

/**
 * 永续合约管理
 */
@RestController
public class AdminItemController extends PageActionSupport {

	private Logger logger = LogManager.getLogger(AdminItemController.class);

	@Autowired
	private AdminItemService adminItemService;
	@Autowired
	private ItemService itemService;
	@Autowired
	private KlineInitService klineInitService;
	@Autowired
	private SysparaService sysparaService;
	@Autowired
	protected LogService logService;
	@Autowired
	protected SecUserService secUserService;
	@Autowired
	protected PasswordEncoder passwordEncoder;

	private final String action = "normal/adminItemAction!";

	/**
	 * 获取 列表
	 */
	@RequestMapping(value = action + "list.action")
	public ModelAndView list(HttpServletRequest request) {
		String pageNo = request.getParameter("pageNo");
		String message = request.getParameter("message");
		String error = request.getParameter("error");
		String para_symbol = request.getParameter("para_symbol");

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("item_list");

		try {

			this.checkAndSetPageNo(pageNo);

			this.pageSize = 20;

			this.page = this.adminItemService.pagedQuery(this.pageNo, this.pageSize, Item.FOREVER_CONTRACT,
					para_symbol);
			for (Item item : (List<Item>) this.page.getElements()) {
				item.setPips_str(new BigDecimal(String.valueOf(item.getPips())).toPlainString());
				item.setPips_amount_str(new BigDecimal(String.valueOf(item.getPips_amount())).toPlainString());
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
		modelAndView.addObject("para_symbol", para_symbol);
		return modelAndView;
	}

	/**
	 * 新增 页面
	 */
	@RequestMapping(value = action + "toAdd.action")
	public ModelAndView toAdd(HttpServletRequest request) {

		String basePath = PropertiesUtil.getProperty("admin_url");
		basePath = this.getPath(request);

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject(basePath);
		modelAndView.setViewName("item_add");
		return modelAndView;
	}

	/**
	 * 新增
	 * name 名称
	 * symbol 代码
	 * symbol_data 数据源编码
	 * pips 最小浮动
	 * pips_amount 最小浮动金额（以交易金额计算）
	 * unit_amount 每手金额
	 * decimals 精度
	 * unit_fee 每手的手续费
	 */
	@RequestMapping(value = action + "add.action")
	public ModelAndView add(HttpServletRequest request) {
		String name = request.getParameter("name");
		String symbol = request.getParameter("symbol");
		String symbol_data = request.getParameter("symbol_data");
		String pips = request.getParameter("pips");
		String pips_amount = request.getParameter("pips_amount");
		String unit_amount = request.getParameter("unit_amount");
		String decimals = request.getParameter("decimals");
		String unit_fee = request.getParameter("unit_fee");

		ModelAndView modelAndView = new ModelAndView();

		try {

			String error = this.validateAdd(name, symbol, symbol_data, pips, pips_amount, unit_amount, decimals, unit_fee);
			if (!StringUtils.isNullOrEmpty(error)) {
				throw new BusinessException(error);
			}

			Item item = new Item();
			item.setName(name);
			item.setSymbol(symbol);
			item.setSymbol_data(symbol_data);
			item.setPips(Double.valueOf(pips));
			item.setPips_amount(Double.valueOf(pips_amount));
			item.setUnit_amount(Double.valueOf(unit_amount));
			item.setDecimals(Integer.valueOf(decimals));
			item.setUnit_fee(Double.valueOf(unit_fee));

		} catch (BusinessException e) {
			modelAndView.addObject("error", e.getMessage());
			modelAndView.addObject("name", name);
			modelAndView.addObject("symbol", symbol);
			modelAndView.addObject("symbol_data", symbol_data);
			modelAndView.addObject("pips", pips);
			modelAndView.addObject("pips_amount", pips_amount);
			modelAndView.addObject("unit_amount", unit_amount);
			modelAndView.addObject("decimals", decimals);
			modelAndView.addObject("unit_fee", unit_fee);
			modelAndView.setViewName("item_add");
			return modelAndView;
		} catch (Throwable t) {
			logger.error(" error ", t);
			modelAndView.addObject("error", "[ERROR] " + t.getMessage());
			modelAndView.addObject("name", name);
			modelAndView.addObject("symbol", symbol);
			modelAndView.addObject("symbol_data", symbol_data);
			modelAndView.addObject("pips", pips);
			modelAndView.addObject("pips_amount", pips_amount);
			modelAndView.addObject("unit_amount", unit_amount);
			modelAndView.addObject("decimals", decimals);
			modelAndView.addObject("unit_fee", unit_fee);
			modelAndView.setViewName("item_add");
			return modelAndView;
		}

		modelAndView.addObject("message", "操作成功");
		modelAndView.setViewName("redirect:/" + action + "list.action");
		return modelAndView;
	}

	/**
	 * 更新 页面
	 */
	@RequestMapping(value = action + "toUpdate.action")
	public ModelAndView toUpdate(HttpServletRequest request) {
		String id = request.getParameter("id");

		ModelAndView modelAndView = new ModelAndView();

		try {

			Item item = this.adminItemService.get(id);

			modelAndView.addObject("id", id);
			modelAndView.addObject("name", item.getName());
			modelAndView.addObject("symbol", item.getSymbol());
			modelAndView.addObject("symbol_data", item.getSymbol_data());
			modelAndView.addObject("pips", item.getPips());
			modelAndView.addObject("pips_str", new BigDecimal(String.valueOf(item.getPips())).toPlainString());
			modelAndView.addObject("pips_amount", item.getPips_amount());
			modelAndView.addObject("unit_amount", item.getUnit_amount());
			modelAndView.addObject("unit_fee", item.getUnit_fee());
			modelAndView.addObject("decimals", null == item.getDecimals() ? 0 : item.getDecimals());

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

		modelAndView.setViewName("item_update");
		return modelAndView;
	}

	/**
	 * 更新
	 * name 名称
	 * symbol 代码
	 * symbol_data 数据源编码
	 * pips 最小浮动
	 * pips_amount 最小浮动金额（以交易金额计算）
	 * unit_amount 每手金额
	 * decimals 精度
	 * unit_fee 每手的手续费
	 * multiple 交易量放大倍数，如果为0或者空不进行操作，否则乘以倍数
	 * login_safeword 登录人资金密码
	 */
	@RequestMapping(value = action + "update.action")
	public ModelAndView update(HttpServletRequest request) {
		String id = request.getParameter("id");
		String name = request.getParameter("name");
		String symbol = request.getParameter("symbol");
		String symbol_data = request.getParameter("symbol_data");
		String pips = request.getParameter("pips");
		String pips_str = request.getParameter("pips_str");
		String pips_amount = request.getParameter("pips_amount");
		String unit_amount = request.getParameter("unit_amount");
		String unit_fee = request.getParameter("unit_fee");
		String decimals = request.getParameter("decimals");
		String multiple = request.getParameter("multiple");
		String login_safeword = request.getParameter("login_safeword");

		ModelAndView modelAndView = new ModelAndView();

		try {
			
			if (StringUtils.isEmptyString(multiple)) {
				multiple = "0";				
			}

			String error = this.validate(unit_fee, unit_amount, pips, pips_amount, multiple, decimals);
			if (!StringUtils.isNullOrEmpty(error)) {
				throw new BusinessException(error);
			}
			
			double unit_fee_double = Double.valueOf(unit_fee).doubleValue();
			double unit_amount_double = Double.valueOf(unit_amount).doubleValue();
			double pips_double = Double.valueOf(pips).doubleValue();
			double pips_amount_double = Double.valueOf(pips_amount).doubleValue();
			double multiple_double = Double.valueOf(multiple).doubleValue();
			int decimals_int = Integer.valueOf(decimals).intValue();

			Item item = this.adminItemService.get(id);
			String old_item_name = item.getName();
			String old_item_symbol = item.getSymbol();
			double old_multiple = item.getMultiple();
			double old_unit_amount = item.getUnit_amount();
			double old_unit_fee = item.getUnit_fee();
			// pips 最小变动单位
			double old_pips = item.getPips();
			// pips_amount最小变动单位的盈亏金额
			double old_pips_amount = item.getPips_amount();

			String username_login = this.getUsername_login();

			SecUser sec = this.secUserService.findUserByLoginName(this.getUsername_login());
			this.checkLoginSafeword(sec, username_login, login_safeword);

			item.setName(name);
			item.setUnit_fee(unit_fee_double);
			item.setUnit_amount(unit_amount_double);
			item.setPips(pips_double);
			item.setDecimals(decimals_int);
			item.setPips_amount(pips_amount_double);
			this.adminItemService.update(item);

			Log log = new Log();
			log.setCategory(Constants.LOG_CATEGORY_OPERATION);
			log.setUsername(username_login);
			log.setOperator(username_login);
			log.setLog("管理员手动修改永续合约配置," + "原名称[" + old_item_name + "],原代码[" + old_item_symbol + "]," + "原交易量倍数["
					+ old_multiple + "],原每手价格[" + old_unit_amount + "]," + "原手续费[" + old_unit_fee + "],原最小变动单位["
					+ old_pips + "]," + "原最小变动单位的盈亏金额[" + old_pips_amount + "]," + "修改后名称[" + name + "],修改后代码[" + symbol
					+ "]," + "修改后交易量倍数[" + multiple_double + "],原每手价格[" + unit_amount + "]," + "修改后手续费[" + unit_fee
					+ "],修改后最小变动单位[" + pips + "]," + "修改后最小变动单位的盈亏金额[" + pips_amount + "]," + "ip:["
					+ this.getIp(getRequest()) + "]");
			logService.saveSync(log);
		
		} catch (BusinessException e) {
			modelAndView.addObject("error", e.getMessage());
			modelAndView.addObject("id", id);
			modelAndView.addObject("name", name);
			modelAndView.addObject("symbol", symbol);
			modelAndView.addObject("symbol_data", symbol_data);
			modelAndView.addObject("pips", pips);
			modelAndView.addObject("pips_str", pips_str);
			modelAndView.addObject("pips_amount", pips_amount);
			modelAndView.addObject("unit_amount", unit_amount);
			modelAndView.addObject("unit_fee", unit_fee);
			modelAndView.addObject("decimals", decimals);
			modelAndView.addObject("multiple", multiple);
			modelAndView.setViewName("item_update");
			return modelAndView;
		} catch (Throwable t) {
			logger.error(" error ", t);
			modelAndView.addObject("error", "[ERROR] " + t.getMessage());
			modelAndView.addObject("id", id);
			modelAndView.addObject("name", name);
			modelAndView.addObject("symbol", symbol);
			modelAndView.addObject("symbol_data", symbol_data);
			modelAndView.addObject("pips", pips);
			modelAndView.addObject("pips_str", pips_str);
			modelAndView.addObject("pips_amount", pips_amount);
			modelAndView.addObject("unit_amount", unit_amount);
			modelAndView.addObject("unit_fee", unit_fee);
			modelAndView.addObject("decimals", decimals);
			modelAndView.addObject("multiple", multiple);
			modelAndView.setViewName("item_update");
			return modelAndView;
		}

		modelAndView.addObject("message", "操作成功");
		modelAndView.setViewName("redirect:/" + action + "list.action");
		return modelAndView;
	}

	/**
	 * 配置 列表
	 */
	@RequestMapping(value = action + "listConfig.action")
	public ModelAndView listConfig(HttpServletRequest request) {
		String pageNo = request.getParameter("pageNo");
		String message = request.getParameter("message");
		String error = request.getParameter("error");
		String para_symbol = request.getParameter("para_symbol");

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("item_config_list");

		try {
			
			this.checkAndSetPageNo(pageNo);

			this.pageSize = 20;
			
			this.page = this.adminItemService.pagedQuery(this.pageNo, this.pageSize, Item.FOREVER_CONTRACT, para_symbol);
			
			for (Item item : (List<Item>) this.page.getElements()) {
				item.setPips_str(new BigDecimal(String.valueOf(item.getPips())).toPlainString());
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
		modelAndView.addObject("para_symbol", para_symbol);
		return modelAndView;
	}

	/**
	 * 新增 配置 页面
	 */
	@RequestMapping(value = action + "toAddConfig.action")
	public ModelAndView toAddConfig(HttpServletRequest request) {
		
		String basePath = PropertiesUtil.getProperty("admin_url");
		basePath = this.getPath(request);
		
		ModelAndView model = new ModelAndView();
		model.addObject(basePath);
		model.setViewName("item_config_add");
		return model;
	}

	/**
	 * 新增 配置
	 * name 名称
	 * symbol 代码
	 * symbol_data 数据源编码
	 * decimals 精度
	 * multiple 交易量放大倍数，如果为0或者空不进行操作，否则乘以倍数
	 * login_safeword 登录人资金密码
	 * borrowing_rate 借贷利率
	 */
	@RequestMapping(value = action + "addConfig.action")
	public ModelAndView addConfig(HttpServletRequest request) {
		String name = request.getParameter("name");
		String symbol = request.getParameter("symbol");
		String symbol_data = request.getParameter("symbol_data");
		String decimals = request.getParameter("decimals");
		String multiple = request.getParameter("multiple");
		String borrowing_rate = request.getParameter("borrowing_rate");
		String login_safeword = request.getParameter("login_safeword");

		ModelAndView modelAndView = new ModelAndView();

		try {
			
			if (StringUtils.isEmptyString(multiple)) {
				multiple = "0";				
			}
			
			String error = this.validateAddConfig(name, symbol, symbol_data, decimals, multiple, borrowing_rate);
			if (!StringUtils.isNullOrEmpty(error)) {
				throw new BusinessException(error);
			}
			
			int decimals_int = Integer.valueOf(decimals).intValue();
			double multiple_double = Double.valueOf(multiple).doubleValue();
			double borrowing_rate_double = Double.valueOf(borrowing_rate).doubleValue();

			String username_login = this.getUsername_login();

			SecUser sec = this.secUserService.findUserByLoginName(this.getUsername_login());
			this.checkLoginSafeword(sec, username_login, login_safeword);

			Item item = new Item();
			item.setName(name);
			item.setSymbol(symbol);
			item.setSymbol_data(symbol_data);
			item.setDecimals(decimals_int);
			item.setMultiple(multiple_double);
			item.setBorrowing_rate(Arith.div(borrowing_rate_double, 100));
			this.adminItemService.save(item);

			project.log.Log log = new project.log.Log();
			log.setCategory(Constants.LOG_CATEGORY_OPERATION);
			log.setUsername(username_login);
			log.setOperator(username_login);
			log.setLog("管理员手动添加行情品种,品种名称[" + name + "],品种代码[" + symbol + "],ip:[" + this.getIp(getRequest()) + "]");
			logService.saveSync(log);

		} catch (BusinessException e) {
			modelAndView.addObject("error", e.getMessage());			
			modelAndView.addObject("name", name);
			modelAndView.addObject("symbol", symbol);
			modelAndView.addObject("symbol_data", symbol_data);
			modelAndView.addObject("decimals", decimals);
			modelAndView.addObject("multiple", multiple);
			modelAndView.addObject("borrowing_rate", borrowing_rate);
			modelAndView.setViewName("item_config_add");
			return modelAndView;
		} catch (Throwable t) {
			logger.error(" error ", t);
			modelAndView.addObject("error", "[ERROR] " + t.getMessage());			
			modelAndView.addObject("name", name);
			modelAndView.addObject("symbol", symbol);
			modelAndView.addObject("symbol_data", symbol_data);
			modelAndView.addObject("decimals", decimals);
			modelAndView.addObject("multiple", multiple);
			modelAndView.addObject("borrowing_rate", borrowing_rate);
			modelAndView.setViewName("item_config_add");
			return modelAndView;
		}

		modelAndView.addObject("message", "操作成功");
		modelAndView.setViewName("redirect:/" + action + "listConfig.action");
		return modelAndView;
	}

	/**
	 * 更新 配置 页面
	 */
	@RequestMapping(value = action + "toUpdateConfig.action")
	public ModelAndView toUpdateConfig(HttpServletRequest request) {
		String id = request.getParameter("id");
		
		Item item = this.adminItemService.get(id);

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject("id", id);
		modelAndView.addObject("name", item.getName());
		modelAndView.addObject("symbol", item.getSymbol());
		modelAndView.addObject("symbol_data", item.getSymbol_data());
		modelAndView.addObject("decimals", null == item.getDecimals() ? 0 : item.getDecimals());
		modelAndView.addObject("multiple", item.getMultiple());
		modelAndView.addObject("borrowing_rate", Arith.mul(item.getBorrowing_rate(), 100));
		modelAndView.setViewName("item_config_update");
		return modelAndView;
	}

	/**
	 * 更新 配置
	 * name 名称
	 * symbol 代码
	 * decimals 精度
	 * multiple 交易量放大倍数，如果为0或者空不进行操作，否则乘以倍数
	 * login_safeword 登录人资金密码
	 * borrowing_rate 借贷利率
	 */
	@RequestMapping(value = action + "updateConfig.action")
	public ModelAndView updateConfig(HttpServletRequest request) {
		String id = request.getParameter("id");
		String name = request.getParameter("name");
		String symbol = request.getParameter("symbol");
		String decimals = request.getParameter("decimals");
		String multiple = request.getParameter("multiple");
		String borrowing_rate = request.getParameter("borrowing_rate");
		String login_safeword = request.getParameter("login_safeword");

		ModelAndView modelAndView = new ModelAndView();
		
		try {

			Item item = this.adminItemService.get(id);
			String old_item_name = item.getName();
			String old_item_symbol = item.getSymbol();
			double old_multiple = item.getMultiple();
			double old_borrowing_rate = item.getBorrowing_rate();

			String error = this.validateUpdateConfig(name, symbol, "null", decimals, multiple, borrowing_rate);
			if (!StringUtils.isNullOrEmpty(error)) {
				throw new BusinessException(error);
			}
			
			int decimals_int = Integer.valueOf(decimals).intValue();
			double multiple_double = Double.valueOf(multiple).doubleValue();
			double borrowing_rate_double = Double.valueOf(borrowing_rate).doubleValue();

			String username_login = this.getUsername_login();
			
			SecUser sec = this.secUserService.findUserByLoginName(username_login);
			this.checkLoginSafeword(sec, username_login, login_safeword);

			item.setName(name);
			item.setDecimals(decimals_int);
			item.setMultiple(multiple_double);
			item.setBorrowing_rate(Arith.div(borrowing_rate_double, 100));
			
			this.adminItemService.update(item);

			Log log = new Log();
			log.setCategory(Constants.LOG_CATEGORY_OPERATION);
			log.setUsername(username_login);
			log.setOperator(username_login);
			log.setLog("管理员手动修改行情品种," + "原品种名称[" + old_item_name + "],原品种代码[" + old_item_symbol + "],原品种交易量倍数["
					+ old_multiple + "]," + "原借贷利率[" + old_borrowing_rate + "]," + "修改后品种名称[" + name + "],修改后品种代码["
					+ symbol + "],修改后品种交易量倍数[" + multiple + "]," + "修改后借贷利率[" + item.getBorrowing_rate() + "]," + "ip:["
					+ this.getIp(getRequest()) + "]");
			this.logService.saveSync(log);
			
		} catch (BusinessException e) {
			modelAndView.addObject("error", e.getMessage());
			modelAndView.addObject("id", id);
			modelAndView.addObject("name", name);
			modelAndView.addObject("symbol", symbol);
			modelAndView.addObject("decimals", decimals);
			modelAndView.addObject("multiple", multiple);
			modelAndView.addObject("borrowing_rate", borrowing_rate);
			modelAndView.setViewName("item_config_update");
			return modelAndView;
		} catch (Throwable t) {
			logger.error(" error ", t);
			modelAndView.addObject("error", "[ERROR] " + t.getMessage());
			modelAndView.addObject("id", id);
			modelAndView.addObject("name", name);
			modelAndView.addObject("symbol", symbol);
			modelAndView.addObject("decimals", decimals);
			modelAndView.addObject("multiple", multiple);
			modelAndView.addObject("borrowing_rate", borrowing_rate);
			modelAndView.setViewName("item_config_update");
			return modelAndView;
		}

		modelAndView.addObject("message", "操作成功");
		modelAndView.setViewName("redirect:/" + action + "listConfig.action");
		return modelAndView;
	}

	/**
	 * order_open
	 */
	@RequestMapping(value = action + "order_open.action")
	public ModelAndView order_open(HttpServletRequest request) {
		String order_open = request.getParameter("order_open");

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("redirect:/" + action + "list.action");

		try {
			
			Syspara syspara = this.sysparaService.find("order_open");
			syspara.setValue(order_open);
			this.sysparaService.update(syspara);
			
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
	 * kline初始化
	 */
	@RequestMapping(value = action + "klineInit.action")
	public ModelAndView klineInit(HttpServletRequest request) {
		String para_init_symbol = request.getParameter("para_init_symbol");

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("redirect:/" + action + "listConfig.action");

		try {
			
			String symbols = para_init_symbol;			
			if (StringUtils.isEmptyString(symbols)) {
				
				List<Item> items = this.itemService.cacheGetAll();
				for (int i = 0; i < items.size(); i++) {
					
					String symbol = items.get(i).getSymbol();
					if (items.size() - i == 1) {
						symbols += symbol;
					} else {
						symbols += symbol + ",";
					}
				}
			}
			
			this.klineInitService.klineInit(symbols);
		
		} catch (BusinessException e) {
			modelAndView.addObject("error", e.getMessage());
			return modelAndView;
		} catch (Throwable t) {
			logger.error(" error ", t);
			modelAndView.addObject("error", "[ERROR] " + t.getMessage());
			return modelAndView;
		}

		modelAndView.addObject("message", "K线图初始化完成");
		return modelAndView;
	}

	/**
	 * kline初始化配置
	 */
	@RequestMapping(value = action + "klineInitConfig.action")
	public ModelAndView klineInitConfig(HttpServletRequest request) {
		String para_init_symbol = request.getParameter("para_init_symbol");

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("redirect:/" + action + "listConfig.action");

		try {
			
			String symbols = para_init_symbol;
			if (StringUtils.isEmptyString(symbols)) {
				
				List<Item> items = this.itemService.cacheGetByMarket("");
				for (int i = 0; i < items.size(); i++) {
					
					String symbol = items.get(i).getSymbol();
					if (items.size() - i == 1) {
						symbols += symbol;
					} else {
						symbols += symbol + ",";
					}
				}
			}
			
			this.klineInitService.klineInit(symbols);
		
		} catch (BusinessException e) {
			modelAndView.addObject("error", e.getMessage());
			return modelAndView;
		} catch (Throwable t) {
			logger.error(" error ", t);
			modelAndView.addObject("error", "[ERROR] " + t.getMessage());
			return modelAndView;
		}

		modelAndView.addObject("message", "K线图初始化完成");
		return modelAndView;
	}

	/**
	 * 验证登录人资金密码
	 */
	private void checkLoginSafeword(SecUser secUser, String operatorUsername, String loginSafeword) {
		String sysSafeword = secUser.getSafeword();
		String safeword_md5 = passwordEncoder.encodePassword(loginSafeword, operatorUsername);
		if (!safeword_md5.equals(sysSafeword)) {
			throw new BusinessException("登录人资金密码错误");
		}
	}

	private String getPath(HttpServletRequest request) {
		return String.format("%s://%s:%s%s", request.getScheme(), request.getServerName(),
				(request.getServerPort() == 80?"":request.getServerPort()),
				request.getContextPath());
	}

	public String validateAdd(String name, String symbol, String symbol_data, String pips, String pips_amount,
			String unit_amount, String decimals, String unit_fee) {
		
		if (StringUtils.isNullOrEmpty(name)) {
			return "名称不能为空";
		}
		if (StringUtils.isNullOrEmpty(symbol)) {
			return "代码不能为空";
		}
		if (this.adminItemService.checkSymbolExit(symbol)) {
			return "代码已经存在";
		}
		
		return this.validate(unit_fee, unit_amount, pips, pips_amount, decimals, "0");
	}

	public String validate(String unit_fee, String unit_amount, String pips, String pips_amount, String multiple, String decimals) {
		
		if (StringUtils.isNullOrEmpty(unit_fee)) {
			return "每手的手续费必填";
		}
		if (!StringUtils.isDouble(unit_fee)) {
			return "每手的手续费不是浮点数";
		}
		if (Double.valueOf(unit_fee).doubleValue() < 0) {
			return "每手的手续费不能小于0";
		}
		
		if (StringUtils.isNullOrEmpty(unit_amount)) {
			return "每手金额必填";
		}
		if (!StringUtils.isDouble(unit_amount)) {
			return "每手金额不是浮点数";
		}
		if (Double.valueOf(unit_amount).doubleValue() < 0) {
			return "每手金额不能小于0";
		}
		
		if (StringUtils.isNullOrEmpty(pips)) {
			return "最小浮动必填";
		}
		if (!StringUtils.isDouble(pips)) {
			return "最小浮动不是浮点数";
		}
		if (Double.valueOf(pips).doubleValue() <= 0) {
			return "最小浮动不能小于等于0";
		}
		
		if (StringUtils.isNullOrEmpty(pips_amount)) {
			return "最小浮动金额必填";
		}
		if (!StringUtils.isDouble(pips_amount)) {
			return "最小浮动金额不是浮点数";
		}
		if (Double.valueOf(pips_amount).doubleValue() < 0) {
			return "最小浮动金额不能小于0";
		}
		
		if (StringUtils.isNullOrEmpty(multiple)) {
			return "交易量放大倍数必填";
		}
		if (!StringUtils.isDouble(multiple)) {
			return "交易量放大倍数不是浮点数";
		}
		if (Double.valueOf(multiple).doubleValue() < 0) {
			return "交易量放大倍数不能小于0";
		}
		
		if (StringUtils.isNullOrEmpty(decimals)) {
			return "精度必填";
		}
		if (!StringUtils.isInteger(decimals)) {
			return "精度不是整数";
		}
		if (Integer.valueOf(decimals).intValue() < 0) {
			return "精度不能小于0";
		}

		return null;
	}

	public String validateAddConfig(String name, String symbol, String symbol_data, String decimals, String multiple, String borrowing_rate) {
		
		if (StringUtils.isNullOrEmpty(name)) {
			return "名称不能为空";
		}
		if (StringUtils.isNullOrEmpty(symbol)) {
			return "代码不能为空";
		}
		if (this.adminItemService.checkSymbolExit(symbol)) {
			return "代码已经存在";
		}
		
		if (StringUtils.isNullOrEmpty(decimals)) {
			return "精度必填";
		}
		if (!StringUtils.isInteger(decimals)) {
			return "精度不是整数";
		}
		if (Integer.valueOf(decimals).intValue() < 0) {
			return "精度不能小于0";
		}
		
		if (StringUtils.isNullOrEmpty(multiple)) {
			return "交易量放大倍数必填";
		}
		if (!StringUtils.isDouble(multiple)) {
			return "交易量放大倍数不是浮点数";
		}
		if (Double.valueOf(multiple).doubleValue() < 0) {
			return "交易量放大倍数不能小于0";
		}
		
		if (StringUtils.isNullOrEmpty(borrowing_rate)) {
			return "借贷利率必填";
		}
		if (!StringUtils.isDouble(borrowing_rate)) {
			return "借贷利率不是浮点数";
		}
		if (Double.valueOf(borrowing_rate).doubleValue() < 0) {
			return "借贷利率不能小于0";
		}

		return null;
	}

	public String validateUpdateConfig(String name, String symbol, String symbol_data, String decimals, String multiple, String borrowing_rate) {
		
		if (StringUtils.isNullOrEmpty(name)) {
			return "名称不能为空";
		}
		if (StringUtils.isNullOrEmpty(symbol)) {
			return "代码不能为空";
		}
		
		if (StringUtils.isNullOrEmpty(decimals)) {
			return "精度必填";
		}
		if (!StringUtils.isInteger(decimals)) {
			return "精度不是整数";
		}
		if (Integer.valueOf(decimals).intValue() < 0) {
			return "精度不能小于0";
		}
		
		if (StringUtils.isNullOrEmpty(multiple)) {
			return "交易量放大倍数必填";
		}
		if (!StringUtils.isDouble(multiple)) {
			return "交易量放大倍数不是浮点数";
		}
		if (Double.valueOf(multiple).doubleValue() < 0) {
			return "交易量放大倍数不能小于0";
		}
		
		if (StringUtils.isNullOrEmpty(borrowing_rate)) {
			return "借贷利率必填";
		}
		if (!StringUtils.isDouble(borrowing_rate)) {
			return "借贷利率不是浮点数";
		}
		if (Double.valueOf(borrowing_rate).doubleValue() < 0) {
			return "借贷利率不能小于0";
		}

		return null;
	}
	
}
