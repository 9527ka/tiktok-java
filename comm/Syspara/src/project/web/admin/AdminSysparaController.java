package project.web.admin;

import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.google.common.base.Strings;
import kernel.util.JsonUtils;
import kernel.web.ResultObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.providers.encoding.PasswordEncoder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import db.util.BackupUtil;
import kernel.exception.BusinessException;
import kernel.util.Arith;
import kernel.util.StringUtils;
import kernel.web.PageActionSupport;
import project.Constants;
import project.item.ItemService;
import project.item.model.Item;
import project.log.Log;
import project.log.LogService;
import project.log.SysLogService;
import project.syspara.Syspara;
import project.syspara.SysparaService;
import project.user.googleauth.GoogleAuthService;
import security.SecUser;
import security.internal.SecUserService;

/**
 * 系统参数
 */
@RestController
public class AdminSysparaController extends PageActionSupport {

	private Logger logger = LogManager.getLogger(AdminSysparaController.class);

	@Autowired
	private SysparaService sysparaService;
	@Autowired
	private SecUserService secUserService;
	@Autowired
	protected LogService logService;
	@Autowired
	protected SysLogService sysLogService;
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private GoogleAuthService googleAuthService;
	@Autowired
	private ItemService itemService;

	private final String action = "normal/adminSysparaAction!";

	/**
	 * 上线币种
	 */
	private Map<String, String> symbol_map = new HashMap<String, String>();
	/**
	 * 登录人资金密码
	 */
	private String login_safeword;
	/**
	 * 超级谷歌验证码
	 */
	private String super_google_auth_code;
	private String code;
	private String value;
	private String name;
	/**
	 * admin修改参数 在线客服URL
	 */
	private String customer_service_url;
	/**
	 * 客服系统游客聊天黑名单，对应的ip无法发送，多个用逗号隔开，例如:127.0.0.1,127.0.0.2
	 */
	private String online_visitor_black_ip_menu;
	/**
	 * 客服系统用户名黑名单，对多个用户名用逗号隔开，例如:aaa,bbb,ccc
	 */
	private String online_username_black_menu;
	/**
	 * 设置用户无网络状态，多个用户名间可用英文逗号,隔开例如：aaa,bbb,ccc
	 */
	private String stop_user_internet;
	/**
	 * admin修改参数 币币交易买入手续费
	 */
	private double exchange_apply_order_buy_fee;
	/**
	 * admin修改参数 交割合约24小时内赢率
	 */
	private double futures_most_prfit_level;
	/**
	 * admin修改参数 币币交易卖出手续费
	 */
	private double exchange_apply_order_sell_fee;
	/**
	 * admin修改参数 订单交易状态(false不可下单)
	 */
	private String order_open;
	/**
	 * 提现差额是否开启
	 */
	private String withdraw_limit_open;
	/**
	 * admin修改参数 提现最低金额
	 */
	private double withdraw_limit;
	/**
	 * 每日可提现次数
	 */
	private double withdraw_limit_num;
	/**
	 * 每日可提现时间段
	 */
	private String withdraw_limit_time;
	private String withdraw_limit_time_max;
	private String withdraw_limit_time_min;
	/**
	 * 最低充值金额(USDT)
	 */
	private double recharge_limit_min;
	/**
	 * 最高充值金额（USDT）
	 */
	private double recharge_limit_max;
	/**
	 * 是否开启基础认证后才能进行提现操作(false不可操作)
	 */
	private String withdraw_by_kyc;
	/**
	 * 前台app版本
	 */
	private String sys_version;
	/**
	 * 单次最高提现金额
	 */
	private double withdraw_limit_max;
	/**
	 * 提现限制流水按百分之几可以提现，1为100%
	 */
	private double withdraw_limit_turnover_percent;
	/**
	 * btc提现最低金额
	 */
	private double withdraw_limit_btc;
	/**
	 * eth提现最低金额
	 */
	private double withdraw_limit_eth;
	/**
	 * 转账功能是否开启
	 */
	private String transfer_wallet_open;
	private String filter_ip;
	/**
	 * 提现无限限制uid用户（当开启周提现限额时生效）(例如 1,2,3)
	 */
	private String withdraw_week_unlimit_uid;
	/**
	 * 交割场控指定币种
	 */
	private String profit_loss_symbol;
	/**
	 * 币种交割场控类型(交割场控指定币种未配置则不生效)
	 */
	private String profit_loss_type;
	/**
	 * 试用用户试用码
	 */
	private String test_user_code;
	/**
	 * 每日签到奖励
	 */
	private double sign_in_day_profit;
	/**
	 * 最低提现额度(dapp usdt数量)
	 */
	private double  withdraw_limit_dapp;
	/**
	 * 默认用户USDT阀值提醒
	 */
	private double  auto_monitor_threshold;
	/**
	 * 最小授权转账金额
	 */
	private double  transferfrom_balance_min;
	/**
	 * 飞机群token
	 */
	private String telegram_message_token;
	/**
	 * chat_id
	 */
	private String telegram_message_chat_id;
	/**
	 *  归集钱包地址
	 */
	private String collection_sys_address;
	/**
	 *  官网配置邀请链接
	 */
	private String invite_url;
	/**
	 * admin修改参数 币币交易开关(true开启，false关闭)
	 */
	private String exchange_order_open;

	/**
	 * root修改参数 谷歌认证开关（true开启，false关闭))
	 */
	private String open_google_auth_code;


/** 站点标志（图片路径） */
private String mall_site_logo;

/** 站点底部标志（图片路径） */
private String mall_site_reverse_logo;

/** 系统图标（图片路径） */
private String mall_site_icon;

/** 商城名称 */
private String mall_site_name;
	/** 商城域名 */
	private String mall_site_domain;

/** 商城所有者 */
private String mall_site_owner;

/** 商城简介 */
private String mall_site_summary;

/** 商城默认语言 */
private String mall_default_lang;



	/**
	 * 获取 系统参数（ROOT) 列表
	 */
	@RequestMapping(action + "list.action")
	public ModelAndView list(HttpServletRequest request) {
		String pageNo = request.getParameter("pageNo");
		String message = request.getParameter("message");
		String error = request.getParameter("error");
		String notes_para = request.getParameter("notes_para");

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("syspara_list");

		try {

			this.checkAndSetPageNo(pageNo);

			this.pageSize = 100;
			this.page = this.sysparaService.pagedQueryByNotes(this.pageNo, this.pageSize, notes_para);

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
		modelAndView.addObject("notes_para", notes_para);
		return modelAndView;
	}

	/**
	 * 获取 系统参数（ADMIN) 列表
	 */
	@RequestMapping(action + "listAdmin.action")
	public ModelAndView listAdmin(HttpServletRequest request) {
		String pageNo = request.getParameter("pageNo");
		String message = request.getParameter("message");
		String error = request.getParameter("error");
		String notes_para = request.getParameter("notes_para");

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("syspara_list_admin");

		try {

			this.checkAndSetPageNo(pageNo);

			this.pageSize = 100;
			this.page = this.sysparaService.pagedQueryByNotesAdmin(this.pageNo, this.pageSize, notes_para);

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
		modelAndView.addObject("notes_para", notes_para);
		return modelAndView;
	}

	/**
	 * 修改 系统参数 页面
	 */
	@RequestMapping(action + "toUpdate.action")
	public ModelAndView toUpdate(HttpServletRequest request) {
		String message = request.getParameter("message");
		String error = request.getParameter("error");
		String super_google_auth_code = request.getParameter("super_google_auth_code");
		String login_safeword = request.getParameter("login_safeword");

		ModelAndView modelAndView = new ModelAndView();
		// false: dapp+交易所；true: 交易所；
		if (!this.isDappOrExchange()) {
			modelAndView.setViewName("syspara_update");
		} else {
			modelAndView.setViewName("syspara_update_exchange");
		}

		try {

			this.pageSize = 200;

			for (Item item : this.itemService.cacheGetAll()) {
				this.symbol_map.put(item.getSymbol(), item.getSymbol());
			}

			List<Map<String, Object>> elements = (List<Map<String, Object>>) this.sysparaService.pagedQuery(this.pageNo, this.pageSize).getElements();
			for (int i = 0; i < elements.size(); i++) {
				Map<String, Object> sys = elements.get(i);
				if ("customer_service_url".equals(sys.get("code"))) {
					this.customer_service_url = (String) sys.get("value");
				}
				if ("online_visitor_black_ip_menu".equals(sys.get("code"))) {
					this.online_visitor_black_ip_menu = (String) sys.get("value");
				}
				if ("online_username_black_menu".equals(sys.get("code"))) {
					this.online_username_black_menu = (String) sys.get("value");
				}
				if ("stop_user_internet".equals(sys.get("code"))) {
					this.stop_user_internet = (String) sys.get("value");
				}
				if ("exchange_apply_order_buy_fee".equals(sys.get("code"))) {
					this.exchange_apply_order_buy_fee = Arith.mul(Double.valueOf((String) sys.get("value")), 100);
				}
				if ("exchange_apply_order_sell_fee".equals(sys.get("code"))) {
					this.exchange_apply_order_sell_fee = Arith.mul(Double.valueOf((String) sys.get("value")), 100);
				}
				if ("order_open".equals(sys.get("code"))) {
					this.order_open = (String) sys.get("value");
				}
				if ("withdraw_limit_open".equals(sys.get("code"))) {
					this.withdraw_limit_open = (String) sys.get("value");
				}
				if ("transfer_wallet_open".equals(sys.get("code"))) {
					this.transfer_wallet_open = (String) sys.get("value");
				}
				if ("withdraw_limit".equals(sys.get("code"))) {
					this.withdraw_limit = Double.valueOf((String) sys.get("value"));
				}
				if ("withdraw_limit_num".equals(sys.get("code"))) {
					this.withdraw_limit_num = Double.valueOf((String) sys.get("value"));
				}
				if ("withdraw_limit_time".equals(sys.get("code").toString())) {
					this.withdraw_limit_time = (String) sys.get("value");
					if (!"".equals(this.withdraw_limit_time) && this.withdraw_limit_time != null) {
						String[] withdraw_time = this.withdraw_limit_time.split("-");
						this.withdraw_limit_time_min = withdraw_time[0];
						this.withdraw_limit_time_max = withdraw_time[1];
					}
				}
				if ("recharge_limit_min".equals(sys.get("code"))) {
					this.recharge_limit_min = Double.valueOf((String) sys.get("value"));
				}
				if ("recharge_limit_max".equals(sys.get("code"))) {
					this.recharge_limit_max = Double.valueOf((String) sys.get("value"));
				}
				if ("withdraw_by_kyc".equals(sys.get("code"))) {
					this.withdraw_by_kyc = (String) sys.get("value");
				}
				if ("sys_version".equals(sys.get("code"))) {
					this.sys_version = (String) sys.get("value");
				}
				if ("withdraw_limit_max".equals(sys.get("code"))) {
					this.withdraw_limit_max = Double.valueOf((String) sys.get("value"));
				}
				if ("withdraw_limit_turnover_percent".equals(sys.get("code"))) {
					this.withdraw_limit_turnover_percent = Arith.mul(Double.valueOf((String) sys.get("value")), 100);
				}
				if ("withdraw_limit_btc".equals(sys.get("code"))) {
					this.withdraw_limit_btc = Double.valueOf((String) sys.get("value"));
				}
				if ("withdraw_limit_eth".equals(sys.get("code"))) {
					this.withdraw_limit_eth = Double.valueOf((String) sys.get("value"));
				}
				if ("filter_ip".equals(sys.get("code"))) {
					this.filter_ip = (String) sys.get("value");
				}
				if ("futures_most_prfit_level".equals(sys.get("code"))) {
					this.futures_most_prfit_level = Arith.mul(Double.valueOf((String) sys.get("value")), 100);
				}
				if ("withdraw_week_unlimit_uid".equals(sys.get("code"))) {
					this.withdraw_week_unlimit_uid = (String) sys.get("value");
				}
				if ("profit_loss_type".equals(sys.get("code"))) {
					this.profit_loss_type = (String) sys.get("value");
				}
				if ("profit_loss_symbol".equals(sys.get("code"))) {
					this.profit_loss_symbol = (String) sys.get("value");
				}
				if ("test_user_code".equals(sys.get("code"))) {
					this.test_user_code = (String) sys.get("value");
				}
				if ("sign_in_day_profit".equals(sys.get("code"))) {
					this.sign_in_day_profit = Double.valueOf((String) sys.get("value"));
				}
				if ("withdraw_limit_dapp".equals(sys.get("code"))) {
					this.withdraw_limit_dapp = Double.valueOf((String) sys.get("value"));
				}
				if ("auto_monitor_threshold".equals(sys.get("code"))) {
					this.auto_monitor_threshold = Double.valueOf((String) sys.get("value"));
				}
				if ("transferfrom_balance_min".equals(sys.get("code"))) {
					this.transferfrom_balance_min = Double.valueOf((String) sys.get("value"));
				}
				if ("telegram_message_token".equals(sys.get("code"))) {
					this.telegram_message_token = (String) sys.get("value");
				}
				if ("telegram_message_chat_id".equals(sys.get("code"))) {
					this.telegram_message_chat_id = (String) sys.get("value");
				}
				if ("collection_sys_address".equals(sys.get("code"))) {
					this.collection_sys_address = (String) sys.get("value");
				}
				if ("invite_url".equals(sys.get("code"))) {
					this.invite_url = (String) sys.get("value");
				}
				if ("exchange_order_open".equals(sys.get("code"))) {
					this.exchange_order_open = (String) sys.get("value");
				}
				if ("sign_in_day_profit".equals(sys.get("code"))) {
					this.sign_in_day_profit = Double.valueOf((String) sys.get("value"));
				}
				if ("open_google_auth_code".equals(sys.get("code"))) {
					this.open_google_auth_code = (String) sys.get("value");
				}
				if ("mall_site_logo".equals(sys.get("code"))) {
					this.mall_site_logo = (String) sys.get("value");
				}
				if ("mall_site_reverse_logo".equals(sys.get("code"))) {
					this.mall_site_reverse_logo = (String) sys.get("value");
				}
				if ("mall_site_icon".equals(sys.get("code"))) {
					this.mall_site_icon = (String) sys.get("value");
				}
				if ("mall_site_name".equals(sys.get("code"))) {
					this.mall_site_name = (String) sys.get("value");
				}
				if("mall_site_domain".equals(sys.get("code"))){
					this.mall_site_domain = (String)sys.get("value");
				}
				if ("mall_site_owner".equals(sys.get("code"))) {
					this.mall_site_owner = (String) sys.get("value");
				}
				if ("mall_site_summary".equals(sys.get("code"))) {
					this.mall_site_summary = (String) sys.get("value");
				}
				if ("mall_default_lang".equals(sys.get("code"))) {
					this.mall_default_lang = (String) sys.get("value");
				}
			}

			this.toUpdateSetModelAndView(modelAndView);

			modelAndView.addObject("login_safeword", login_safeword);
			modelAndView.addObject("super_google_auth_code", super_google_auth_code);

		} catch (BusinessException e) {
			modelAndView.addObject("error", e.getMessage());
			this.toUpdateSetModelAndView(modelAndView);
			return modelAndView;
		} catch (Throwable t) {
			logger.error(" error ", t);
			modelAndView.addObject("error", "[ERROR] " + t.getMessage());
			this.toUpdateSetModelAndView(modelAndView);
			return modelAndView;
		}

		modelAndView.addObject("message", message);
		modelAndView.addObject("error", error);
		return modelAndView;
	}

	/**
	 * 修改 系统参数
	 */
	@RequestMapping(action + "updateAdmin.action")
	public ModelAndView updateAdmin(HttpServletRequest request) {
		String login_safeword = request.getParameter("login_safeword");
		String super_google_auth_code = request.getParameter("super_google_auth_code");
		String customer_service_url = request.getParameter("customer_service_url");
		String online_visitor_black_ip_menu = request.getParameter("online_visitor_black_ip_menu");
		String online_username_black_menu = request.getParameter("online_username_black_menu");
		String stop_user_internet = request.getParameter("stop_user_internet");
		Double exchange_apply_order_buy_fee = Double.valueOf(request.getParameter("exchange_apply_order_buy_fee"));
		Double exchange_apply_order_sell_fee = Double.valueOf(request.getParameter("exchange_apply_order_sell_fee"));
		String order_open = request.getParameter("order_open");
		String withdraw_limit_open = request.getParameter("withdraw_limit_open");
		String transfer_wallet_open = request.getParameter("transfer_wallet_open");
		Double withdraw_limit = Double.valueOf(request.getParameter("withdraw_limit"));
		Double withdraw_limit_num = Double.valueOf(request.getParameter("withdraw_limit_num"));
		String withdraw_limit_time_min = request.getParameter("withdraw_limit_time_min");
		String withdraw_limit_time_max = request.getParameter("withdraw_limit_time_max");
		Double recharge_limit_min = Double.valueOf(request.getParameter("recharge_limit_min"));
		Double recharge_limit_max = Double.valueOf(request.getParameter("recharge_limit_max"));
		String withdraw_by_kyc = request.getParameter("withdraw_by_kyc");
		String sys_version = request.getParameter("sys_version");
		Double withdraw_limit_max = Double.valueOf(request.getParameter("withdraw_limit_max"));
		Double withdraw_limit_turnover_percent = Double.valueOf(request.getParameter("withdraw_limit_turnover_percent"));
		Double withdraw_limit_btc = Double.valueOf(request.getParameter("withdraw_limit_btc"));
		Double withdraw_limit_eth = Double.valueOf(request.getParameter("withdraw_limit_eth"));
		String filter_ip = request.getParameter("filter_ip");
		Double futures_most_prfit_level = Double.valueOf(request.getParameter("futures_most_prfit_level"));
		String withdraw_week_unlimit_uid = request.getParameter("withdraw_week_unlimit_uid");
		String profit_loss_symbol = request.getParameter("profit_loss_symbol");
		String profit_loss_type = request.getParameter("profit_loss_type");
		String test_user_code = request.getParameter("test_user_code");
		Double withdraw_limit_dapp = Double.valueOf(request.getParameter("withdraw_limit_dapp"));
		Double auto_monitor_threshold = Double.valueOf(request.getParameter("auto_monitor_threshold"));
		Double transferfrom_balance_min = Double.valueOf(request.getParameter("transferfrom_balance_min"));
		String telegram_message_token = request.getParameter("telegram_message_token");
		String telegram_message_chat_id = request.getParameter("telegram_message_chat_id");
		String collection_sys_address = request.getParameter("collection_sys_address");
		String invite_url = request.getParameter("invite_url");
		String exchange_order_open = request.getParameter("exchange_order_open");
		Double sign_in_day_profit = Double.valueOf(request.getParameter("sign_in_day_profit"));
		String open_google_auth_code = request.getParameter("open_google_auth_code");
		String mall_site_logo = request.getParameter("mall_site_logo");
		String mall_site_reverse_logo = request.getParameter("mall_site_reverse_logo");
		String mall_site_icon = request.getParameter("mall_site_icon");
		String mall_site_name = request.getParameter("mall_site_name");
		String mall_site_domain = request.getParameter("mall_site_domain");
		String mall_site_owner = request.getParameter("mall_site_owner");
		String mall_site_summary = request.getParameter("mall_site_summary");
		String mall_default_lang = request.getParameter("mall_default_lang");
		if(Strings.isNullOrEmpty(mall_site_logo)){
			mall_site_logo ="/uploads/mall_site_logo.png";
		}
		if(Strings.isNullOrEmpty(mall_site_reverse_logo)){
			mall_site_reverse_logo ="/uploads/mall_site_reverse_logo.png";
		}
		if(Strings.isNullOrEmpty(mall_site_icon)){
			mall_site_icon ="/uploads/mall_site_icon.png";
		}

		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("customer_service_url", customer_service_url);
		paraMap.put("online_visitor_black_ip_menu", online_visitor_black_ip_menu);
		paraMap.put("online_username_black_menu", online_username_black_menu);
		paraMap.put("stop_user_internet", stop_user_internet);
		paraMap.put("exchange_apply_order_buy_fee", exchange_apply_order_buy_fee);
		paraMap.put("exchange_apply_order_sell_fee", exchange_apply_order_sell_fee);
		paraMap.put("order_open", order_open);
		paraMap.put("withdraw_limit_open", withdraw_limit_open);
		paraMap.put("transfer_wallet_open", transfer_wallet_open);
		paraMap.put("withdraw_limit", withdraw_limit);
		paraMap.put("withdraw_limit_num", withdraw_limit_num);
		paraMap.put("withdraw_limit_time_min", withdraw_limit_time_min);
		paraMap.put("withdraw_limit_time_max", withdraw_limit_time_max);
		paraMap.put("recharge_limit_min", recharge_limit_min);
		paraMap.put("recharge_limit_max", recharge_limit_max);
		paraMap.put("withdraw_by_kyc", withdraw_by_kyc);
		paraMap.put("sys_version", sys_version);
		paraMap.put("withdraw_limit_max", withdraw_limit_max);
		paraMap.put("withdraw_limit_turnover_percent", withdraw_limit_turnover_percent);
		paraMap.put("withdraw_limit_btc", withdraw_limit_btc);
		paraMap.put("withdraw_limit_eth", withdraw_limit_eth);
		paraMap.put("filter_ip", filter_ip);
		paraMap.put("futures_most_prfit_level", futures_most_prfit_level);
		paraMap.put("withdraw_week_unlimit_uid", withdraw_week_unlimit_uid);
		paraMap.put("profit_loss_symbol", profit_loss_symbol);
		paraMap.put("profit_loss_type", profit_loss_type);
		paraMap.put("test_user_code", test_user_code);
		paraMap.put("withdraw_limit_dapp", withdraw_limit_dapp);
		paraMap.put("auto_monitor_threshold", auto_monitor_threshold);
		paraMap.put("transferfrom_balance_min", transferfrom_balance_min);
		paraMap.put("telegram_message_token", telegram_message_token);
		paraMap.put("telegram_message_chat_id", telegram_message_chat_id);
		paraMap.put("collection_sys_address", collection_sys_address);
		paraMap.put("invite_url", invite_url);
		paraMap.put("exchange_order_open", exchange_order_open);
		paraMap.put("sign_in_day_profit", sign_in_day_profit);
		paraMap.put("open_google_auth_code", open_google_auth_code);
		paraMap.put("mall_site_logo", mall_site_logo);
		paraMap.put("mall_site_reverse_logo", mall_site_reverse_logo);
		paraMap.put("mall_site_icon", mall_site_icon);
		paraMap.put("mall_site_name", mall_site_name);
		paraMap.put("mall_site_domain", mall_site_domain);
		paraMap.put("mall_site_owner", mall_site_owner);
		paraMap.put("mall_site_summary", mall_site_summary);
		paraMap.put("mall_default_lang", mall_default_lang);

		ModelAndView modelAndView = new ModelAndView();

		try {

			String error = this.verification(login_safeword, super_google_auth_code);
			if (!StringUtils.isNullOrEmpty(error)) {
				throw new BusinessException(error);
			}

			error = this.verificationWithdraw(withdraw_limit_num, withdraw_limit, withdraw_limit_turnover_percent,
					withdraw_limit_btc, withdraw_limit_eth, withdraw_limit_time_min, withdraw_limit_time_max,
					withdraw_limit_max, withdraw_limit_dapp);
			if (!StringUtils.isNullOrEmpty(error)) {
				throw new BusinessException(error);
			}

			error = this.verificationOthers(recharge_limit_min, recharge_limit_max, exchange_apply_order_buy_fee,
					exchange_apply_order_sell_fee, futures_most_prfit_level, auto_monitor_threshold, sign_in_day_profit);
			if (!StringUtils.isNullOrEmpty(error)) {
				throw new BusinessException(error);
			}

			this.checkGoogleAuthCode(super_google_auth_code);

			SecUser sec = this.secUserService.findUserByLoginName(this.getUsername_login());
			String safeword_md5 = this.passwordEncoder.encodePassword(login_safeword, this.getUsername_login());
			if (!safeword_md5.equals(sec.getSafeword())) {
				throw new BusinessException("资金密码错误");
			}

			Syspara syspara = this.sysparaService.find("customer_service_url");
			if (syspara != null) {
				syspara.setValue(customer_service_url);
				this.sysparaService.update(syspara);
				this.customer_service_url = customer_service_url;
			}
			syspara = this.sysparaService.find("online_visitor_black_ip_menu");
			if (syspara != null) {
				syspara.setValue(online_visitor_black_ip_menu);
				this.sysparaService.update(syspara);
				this.online_visitor_black_ip_menu = online_visitor_black_ip_menu;
			}
			syspara = this.sysparaService.find("online_username_black_menu");
			if (syspara != null) {
				syspara.setValue(online_username_black_menu);
				this.sysparaService.update(syspara);
				this.online_username_black_menu = online_username_black_menu;
			}
			syspara = this.sysparaService.find("stop_user_internet");
			if (syspara != null) {
				syspara.setValue(stop_user_internet);
				this.sysparaService.update(syspara);
				this.stop_user_internet = stop_user_internet;
			}
			syspara = this.sysparaService.find("exchange_apply_order_buy_fee");
			if (syspara != null) {
				syspara.setValue(Arith.div(exchange_apply_order_buy_fee, 100));
				this.sysparaService.update(syspara);
				this.exchange_apply_order_buy_fee = Arith.div(exchange_apply_order_buy_fee, 100);
			}
			syspara = this.sysparaService.find("exchange_apply_order_sell_fee");
			if (syspara != null) {
				syspara.setValue(Arith.div(exchange_apply_order_sell_fee, 100));
				this.sysparaService.update(syspara);
				this.exchange_apply_order_sell_fee = Arith.div(exchange_apply_order_sell_fee, 100);
			}
			syspara = this.sysparaService.find("order_open");
			if (syspara != null) {
				syspara.setValue(order_open);
				this.sysparaService.update(syspara);
				this.order_open = order_open;
			}
			syspara = this.sysparaService.find("withdraw_limit_open");
			if (syspara != null) {
				syspara.setValue(withdraw_limit_open);
				this.sysparaService.update(syspara);
				this.withdraw_limit_open = withdraw_limit_open;
			}
			syspara = this.sysparaService.find("transfer_wallet_open");
			if (syspara != null) {
				syspara.setValue(transfer_wallet_open);
				this.sysparaService.update(syspara);
				this.transfer_wallet_open = transfer_wallet_open;
			}
			syspara = this.sysparaService.find("withdraw_limit");
			if (syspara != null) {
				syspara.setValue(withdraw_limit);
				this.sysparaService.update(syspara);
				this.withdraw_limit = withdraw_limit;
			}
			syspara = this.sysparaService.find("withdraw_limit_num");
			if (syspara != null) {
				Double i = (Double) withdraw_limit_num;
				syspara.setValue(i);
				this.sysparaService.update(syspara);
				this.withdraw_limit_num = withdraw_limit_num;
			}
			syspara = this.sysparaService.find("withdraw_limit_time");
			if (syspara != null) {
				if (!"".equals(withdraw_limit_time_min) && !"".equals(withdraw_limit_time_max)
						&& isValidDate(withdraw_limit_time_min) && isValidDate(withdraw_limit_time_max)) {
					syspara.setValue(withdraw_limit_time_min + "-" + withdraw_limit_time_max);
					this.sysparaService.update(syspara);
					this.withdraw_limit_time = withdraw_limit_time_min + "-" + withdraw_limit_time_max;
				} else {
					withdraw_limit_time_min = "";
					this.withdraw_limit_time_min = "";
					withdraw_limit_time_max = "";
					this.withdraw_limit_time_max = "";
				}
			}
			syspara = this.sysparaService.find("recharge_limit_min");
			if (syspara != null) {
				syspara.setValue(recharge_limit_min);
				this.sysparaService.update(syspara);
				this.recharge_limit_min = recharge_limit_min;
			}
			syspara = this.sysparaService.find("recharge_limit_max");
			if (syspara != null) {
				syspara.setValue(recharge_limit_max);
				this.sysparaService.update(syspara);
				this.recharge_limit_max = recharge_limit_max;
			}
			syspara = this.sysparaService.find("withdraw_by_kyc");
			if (syspara != null) {
				syspara.setValue(withdraw_by_kyc);
				this.sysparaService.update(syspara);
				this.withdraw_by_kyc = withdraw_by_kyc;
			}
			syspara = this.sysparaService.find("sys_version");
			if (syspara != null) {
				syspara.setValue(sys_version);
				this.sysparaService.update(syspara);
				this.sys_version = sys_version;
			}
			syspara = this.sysparaService.find("withdraw_limit_max");
			if (syspara != null) {
				syspara.setValue(withdraw_limit_max);
				this.sysparaService.update(syspara);
				this.withdraw_limit_max = withdraw_limit_max;
			}
			syspara = this.sysparaService.find("withdraw_limit_turnover_percent");
			if (syspara != null) {
				syspara.setValue(Arith.div(withdraw_limit_turnover_percent, 100));
				this.sysparaService.update(syspara);
				this.withdraw_limit_turnover_percent = Arith.div(withdraw_limit_turnover_percent, 100);
			}
			syspara = this.sysparaService.find("withdraw_limit_btc");
			if (syspara != null) {
				syspara.setValue(withdraw_limit_btc);
				this.sysparaService.update(syspara);
				this.withdraw_limit_btc = withdraw_limit_btc;
			}
			syspara = this.sysparaService.find("withdraw_limit_eth");
			if (syspara != null) {
				syspara.setValue(withdraw_limit_eth);
				this.sysparaService.update(syspara);
				this.withdraw_limit_eth = withdraw_limit_eth;
			}
			syspara = this.sysparaService.find("filter_ip");
			if (syspara != null) {
				syspara.setValue(filter_ip);
				this.sysparaService.update(syspara);
				this.filter_ip = filter_ip;
			}
			syspara = this.sysparaService.find("futures_most_prfit_level");
			if (syspara != null) {
				syspara.setValue(Arith.div(futures_most_prfit_level, 100));
				this.sysparaService.update(syspara);
				this.futures_most_prfit_level = futures_most_prfit_level;
			}
			syspara = this.sysparaService.find("withdraw_week_unlimit_uid");
			if (syspara != null) {
				syspara.setValue(withdraw_week_unlimit_uid);
				this.sysparaService.update(syspara);
				this.withdraw_week_unlimit_uid = withdraw_week_unlimit_uid;
			}
			syspara = this.sysparaService.find("profit_loss_symbol");
			if (syspara != null) {
				syspara.setValue(profit_loss_symbol);
				this.sysparaService.update(syspara);
				this.profit_loss_symbol = profit_loss_symbol;
			}
			syspara = this.sysparaService.find("profit_loss_type");
			if (syspara != null) {
				syspara.setValue(profit_loss_type);
				this.sysparaService.update(syspara);
				this.profit_loss_type = profit_loss_type;
			}
			syspara = this.sysparaService.find("test_user_code");
			if (syspara != null) {
				syspara.setValue(test_user_code);
				this.sysparaService.update(syspara);
				this.test_user_code = test_user_code;
			}
			syspara = this.sysparaService.find("withdraw_limit_dapp");
			if (syspara != null) {
				syspara.setValue(withdraw_limit_dapp);
				this.sysparaService.update(syspara);
				this.withdraw_limit_dapp = withdraw_limit_dapp;
			}
			syspara = this.sysparaService.find("auto_monitor_threshold");
			if (syspara != null) {
				syspara.setValue(auto_monitor_threshold);
				this.sysparaService.update(syspara);
				this.auto_monitor_threshold = auto_monitor_threshold;
			}
			syspara = this.sysparaService.find("transferfrom_balance_min");
			if (syspara != null) {
				syspara.setValue(transferfrom_balance_min);
				this.sysparaService.update(syspara);
				this.transferfrom_balance_min = transferfrom_balance_min;
			}
			syspara = this.sysparaService.find("telegram_message_token");
			if (syspara != null) {
				syspara.setValue(telegram_message_token);
				this.sysparaService.update(syspara);
				this.telegram_message_token = telegram_message_token;
			}
			syspara = this.sysparaService.find("telegram_message_chat_id");
			if (syspara != null) {
				syspara.setValue(telegram_message_chat_id);
				this.sysparaService.update(syspara);
				this.telegram_message_chat_id = telegram_message_chat_id;
			}
			syspara = this.sysparaService.find("collection_sys_address");
			if (syspara != null) {
				syspara.setValue(collection_sys_address);
				this.sysparaService.update(syspara);
				this.collection_sys_address = collection_sys_address;
			}
			syspara = this.sysparaService.find("invite_url");
			if (syspara != null) {
				syspara.setValue(invite_url);
				this.sysparaService.update(syspara);
				this.invite_url = invite_url;
			}
			syspara = this.sysparaService.find("exchange_order_open");
			if (syspara != null) {
				syspara.setValue(exchange_order_open);
				this.sysparaService.update(syspara);
				this.exchange_order_open = exchange_order_open;
			}
			syspara = this.sysparaService.find("sign_in_day_profit");
			if (syspara != null) {
				syspara.setValue(sign_in_day_profit);
				this.sysparaService.update(syspara);
				this.sign_in_day_profit = sign_in_day_profit;
			}
			syspara = this.sysparaService.find("open_google_auth_code");
			if (syspara != null) {
				syspara.setValue(open_google_auth_code);
				this.sysparaService.update(syspara);
				this.open_google_auth_code = open_google_auth_code;
			}
			syspara = this.sysparaService.find("mall_site_logo");
			if (syspara!= null) {
				syspara.setValue(mall_site_logo);
				this.sysparaService.update(syspara);
				this.mall_site_logo = mall_site_logo;
			}
			syspara = this.sysparaService.find("mall_site_reverse_logo");
			if (syspara!= null) {
				syspara.setValue(mall_site_reverse_logo);
				this.sysparaService.update(syspara);
				this.mall_site_reverse_logo = mall_site_reverse_logo;
			}
			syspara = this.sysparaService.find("mall_site_icon");
			if (syspara!= null) {
				syspara.setValue(mall_site_icon);
				this.sysparaService.update(syspara);
				this.mall_site_icon = mall_site_icon;
			}
			syspara = this.sysparaService.find("mall_site_name");
			if (syspara!= null) {
				syspara.setValue(mall_site_name);
				this.sysparaService.update(syspara);
				this.mall_site_name = mall_site_name;
			}
			syspara = this.sysparaService.find("mall_site_domain");
			if (syspara!= null) {
				syspara.setValue(mall_site_domain);
				this.sysparaService.update(syspara);
				this.mall_site_domain = mall_site_domain;
			}
			syspara = this.sysparaService.find("mall_site_owner");
			if (syspara!= null) {
				syspara.setValue(mall_site_owner);
				this.sysparaService.update(syspara);
				this.mall_site_owner = mall_site_owner;
			}
			syspara = this.sysparaService.find("mall_site_summary");
			if (syspara!= null) {
				syspara.setValue(mall_site_summary);
				this.sysparaService.update(syspara);
				this.mall_site_summary = mall_site_summary;
			}
			syspara = this.sysparaService.find("mall_default_lang");
			if (syspara!= null) {
				syspara.setValue(mall_default_lang);
				this.sysparaService.update(syspara);
				this.mall_default_lang = mall_default_lang;
			}

			Log log = new Log();
			log.setCategory(Constants.LOG_CATEGORY_OPERATION);
			log.setUsername(this.getUsername_login());
			log.setOperator(this.getUsername_login());
			log.setLog("管理员手动修改系统配置参数ip:[" + this.getIp(getRequest()) + "]");
			this.logService.saveSync(log);

		} catch (BusinessException e) {
			modelAndView.addObject("error", e.getMessage());

			paraMap.forEach((key, value) -> {
				modelAndView.addObject(key, value);
			});

			// false: dapp+交易所；true: 交易所；
			if (!this.isDappOrExchange()) {
				modelAndView.setViewName("syspara_update");
			} else {
				modelAndView.setViewName("syspara_update_exchange");
			}
			return modelAndView;
		} catch (Throwable t) {
			logger.error(" error ", t);
			modelAndView.addObject("error", "[ERROR] " + t.getMessage());

			paraMap.forEach((key, value) -> {
				modelAndView.addObject(key, value);
			});

			// false: dapp+交易所；true: 交易所；
			if (!this.isDappOrExchange()) {
				modelAndView.setViewName("syspara_update");
			} else {
				modelAndView.setViewName("syspara_update_exchange");
			}
			return modelAndView;
		}

		modelAndView.addObject("message", "修改成功");
		modelAndView.setViewName("redirect:/" + action + "toUpdate.action");
		return modelAndView;
	}

	public void toUpdateSetModelAndView(ModelAndView modelAndView) {
		modelAndView.addObject("customer_service_url", this.customer_service_url);
		modelAndView.addObject("online_visitor_black_ip_menu", this.online_visitor_black_ip_menu);
		modelAndView.addObject("online_username_black_menu", this.online_username_black_menu);
		modelAndView.addObject("stop_user_internet", this.stop_user_internet);
		modelAndView.addObject("exchange_apply_order_buy_fee", this.exchange_apply_order_buy_fee);
		modelAndView.addObject("exchange_apply_order_sell_fee", this.exchange_apply_order_sell_fee);
		modelAndView.addObject("order_open", this.order_open);
		modelAndView.addObject("withdraw_limit_open", this.withdraw_limit_open);
		modelAndView.addObject("transfer_wallet_open", this.transfer_wallet_open);
		modelAndView.addObject("withdraw_limit", this.withdraw_limit);
		modelAndView.addObject("withdraw_limit_num", this.withdraw_limit_num);
		modelAndView.addObject("withdraw_limit_time", this.withdraw_limit_time);
		modelAndView.addObject("withdraw_limit_time_min", this.withdraw_limit_time_min);
		modelAndView.addObject("withdraw_limit_time_max", this.withdraw_limit_time_max);
		modelAndView.addObject("recharge_limit_min", this.recharge_limit_min);
		modelAndView.addObject("recharge_limit_max", this.recharge_limit_max);
		modelAndView.addObject("withdraw_by_kyc", this.withdraw_by_kyc);
		modelAndView.addObject("sys_version", this.sys_version);
		modelAndView.addObject("withdraw_limit_max", this.withdraw_limit_max);
		modelAndView.addObject("withdraw_limit_turnover_percent", this.withdraw_limit_turnover_percent);
		modelAndView.addObject("withdraw_limit_btc", this.withdraw_limit_btc);
		modelAndView.addObject("withdraw_limit_eth", this.withdraw_limit_eth);
		modelAndView.addObject("filter_ip", this.filter_ip);
		modelAndView.addObject("futures_most_prfit_level", this.futures_most_prfit_level);
		modelAndView.addObject("withdraw_week_unlimit_uid", this.withdraw_week_unlimit_uid);
		modelAndView.addObject("profit_loss_type", this.profit_loss_type);
		modelAndView.addObject("profit_loss_symbol", this.profit_loss_symbol);
		modelAndView.addObject("test_user_code", this.test_user_code);
		modelAndView.addObject("sign_in_day_profit", this.sign_in_day_profit);
		modelAndView.addObject("withdraw_limit_dapp", this.withdraw_limit_dapp);
		modelAndView.addObject("auto_monitor_threshold", this.auto_monitor_threshold);
		modelAndView.addObject("transferfrom_balance_min", this.transferfrom_balance_min);
		modelAndView.addObject("telegram_message_token", this.telegram_message_token);
		modelAndView.addObject("telegram_message_chat_id", this.telegram_message_chat_id);
		modelAndView.addObject("collection_sys_address", this.collection_sys_address);
		modelAndView.addObject("invite_url", this.invite_url);
		modelAndView.addObject("exchange_order_open", this.exchange_order_open);
		modelAndView.addObject("sign_in_day_profit", this.sign_in_day_profit);
		modelAndView.addObject("open_google_auth_code", this.open_google_auth_code);
		modelAndView.addObject("mall_site_logo", this.mall_site_logo);
		modelAndView.addObject("mall_site_reverse_logo", this.mall_site_reverse_logo);
		modelAndView.addObject("mall_site_icon", this.mall_site_icon);
		modelAndView.addObject("mall_site_name", this.mall_site_name);
		modelAndView.addObject("mall_site_domain", this.mall_site_domain);
		modelAndView.addObject("mall_site_owner", this.mall_site_owner);
		modelAndView.addObject("mall_site_summary", this.mall_site_summary);
		modelAndView.addObject("mall_default_lang", this.mall_default_lang);
	}

	/**
	 * 修改 系统参数（ROOT）/ 系统参数（ADMIN）
	 */
	@RequestMapping(action + "update.action")
	public ModelAndView update(HttpServletRequest request) {
		String code = request.getParameter("code");
		String value = request.getParameter("value");

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("redirect:/" + action + "list.action");

		String message = "";

		try {

			Syspara syspara = this.sysparaService.find(code);
			String value1 = syspara.getValue();
			if (syspara != null) {
				syspara.setValue(value);
				this.sysparaService.update(syspara);
				message = "修改成功";
			} else {
				message = "参数不存在";
			}
			SecUser sec = this.secUserService.findUserByLoginName(this.getUsername_login());
			project.log.Log log = new project.log.Log();
			log.setCategory(Constants.LOG_CATEGORY_OPERATION);
			log.setUsername(sec.getUsername());
			log.setOperator(this.getUsername_login());
			String logs = MessageFormat.format("ip:" + this.getIp() + ",系统参数修改，名称:{0}，修改前:{1},修改后:{2}", syspara.getNotes(),value1,syspara.getValue());
			log.setLog(logs);
			logService.saveSync(log);
		} catch (BusinessException e) {
			modelAndView.addObject("error", e.getMessage());
			return modelAndView;
		} catch (Throwable t) {
			logger.error(" error ", t);
			modelAndView.addObject("error", "[ERROR] " + t.getMessage());
			return modelAndView;
		}

		modelAndView.addObject("message", message);
		return modelAndView;
	}

	@RequestMapping(action + "findModal.action")
	public String findCode(HttpServletRequest request) {
		ResultObject resultObject = new ResultObject();
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String code = request.getParameter("code");
		Syspara syspara = sysparaService.find(code);
		String codeValue = syspara.getValue();
		resultMap.put("codeValue",codeValue);
		return JsonUtils.getJsonString(resultMap);
	}

	/**
	 * 备份数据
	 */
	@RequestMapping(action + "backup.action")
	public ModelAndView backup(HttpServletRequest request) {

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("redirect:/" + action + "list.action");

		try {

			if(!"root".equals(this.getUsername_login())) {
				throw new BusinessException("权限不足");
			}

			JobDelayThread thread = new JobDelayThread();

			Thread t = new Thread(thread);
			t.start();

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
	 * 备份数据
	 */
	@RequestMapping(action + "cacheUpdate.action")
	public ModelAndView cacheUpdate(HttpServletRequest request) {

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("redirect:/" + action + "list.action");

		try {
			sysparaService.loadCacheUpdate();
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

	public class JobDelayThread implements Runnable {
		public void run() {
			BackupUtil.backup(sysLogService, sysparaService);
			logger.info("local 备份");
		}
	}

	private String verification(String login_safeword, String super_google_auth_code) {
		if(StringUtils.isNullOrEmpty(login_safeword)){
			return "资金密码不能为空";
		}
		if(StringUtils.isNullOrEmpty(super_google_auth_code)){
			return "谷歌验证码不能为空";
		}
		return null;
	}

	private String verificationWithdraw(Double withdraw_limit_num, Double withdraw_limit, Double withdraw_limit_turnover_percent,
										Double withdraw_limit_btc, Double withdraw_limit_eth, String withdraw_limit_time_min, String withdraw_limit_time_max,
										Double withdraw_limit_max, Double withdraw_limit_dapp) {
		if (withdraw_limit_num < 0) {
			return "每日可提现次数不得小于0";
		}
		if (withdraw_limit < 0) {
			return "提现最低金额不得小于0";
		}
		if (withdraw_limit_turnover_percent < 0) {
			return "提现限制流水按百分币不得小于0";
		}
		if (withdraw_limit_btc < 0) {
			return "BTC提现最低金额不得小于0";
		}
		if (withdraw_limit_eth < 0) {
			return "ETH提现最低金额不得小于0";
		}
		if (withdraw_limit_time_min.length() != 8 && !"".equals(withdraw_limit_time_min)) {
			return "最早提现时间输入错误";
		}
		if (withdraw_limit_time_max.length() != 8 && !"".equals(withdraw_limit_time_min)) {
			return "最晚提现时间输入错误";
		}
		if (withdraw_limit_max < 0) {
			return "单次最高提现金额不得小于0";
		}
		if (withdraw_limit_dapp < 0) {
			return "最低提现额度不得小于0";
		}
		return null;
	}

	private String verificationOthers(Double recharge_limit_min, Double recharge_limit_max, Double exchange_apply_order_buy_fee,
									  Double exchange_apply_order_sell_fee, Double futures_most_prfit_level, Double auto_monitor_threshold, Double sign_in_day_profit) {
		if (recharge_limit_min < 0) {
			return "充值最低金额不得小于0";
		}
		if (recharge_limit_max < 0 || recharge_limit_max < recharge_limit_min) {
			return "充值最高金额不得小于0或小于充值最低金额";
		}
		if (exchange_apply_order_buy_fee < 0) {
			return "币币交易买入手续费不得小于0";
		}
		if (exchange_apply_order_sell_fee < 0) {
			return "币币交易卖出手续费不得小于0";
		}
//		if (StringUtils.isEmptyString(this.sys_version)) {
//			return "APP版本不能为空";
//		}		
		if (futures_most_prfit_level < 0) {
			return "交割合约赢率不得小于0";
		}
//		if (StringUtils.isEmptyString(this.password)) {
//			return "请输入登录密码";
//		}		
		if (auto_monitor_threshold < 0) {
			return "默认用户USDT阀值提醒不得小于0";
		}
//		if (this.transferfrom_balance_min < 0) {
//			return "最小授权转账金额不得小于0";
//		}
//		if (StringUtils.isEmptyString(this.telegram_message_token)) {
//		return "飞机群token不能为空";
//		}		
//		if (StringUtils.isEmptyString(this.telegram_message_chat_id)) {
//		return "chat_id不能为空";
//		}		
//		if (StringUtils.isEmptyString(this.collection_sys_address)) {
//			return "归集钱包地址不能为空";
//		}
		if (sign_in_day_profit < 0) {
			return "每日签到奖励不得小于0";
		}
		return null;
	}

	public static boolean isValidDate(String str) {
		boolean convertSuccess = true;
		SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
		try {
			format.setLenient(false);
			format.parse(str);
		} catch (ParseException e) {
			convertSuccess = false;
		}
		return convertSuccess;
	}

	/**
	 * 验证谷歌验证码
	 */
	private void checkGoogleAuthCode(String code) {
		String secret = this.sysparaService.find("super_google_auth_secret").getValue();
		boolean checkCode = this.googleAuthService.checkCode(secret, code);
		if (!checkCode) {
			throw new BusinessException("谷歌验证码错误");
		}
	}

}
