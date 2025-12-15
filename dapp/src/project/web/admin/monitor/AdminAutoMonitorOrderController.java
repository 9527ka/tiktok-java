package project.web.admin.monitor;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.providers.encoding.PasswordEncoder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import kernel.exception.BusinessException;
import kernel.util.StringUtils;
import kernel.util.ThreadUtils;
import kernel.web.PageActionSupport;
import project.Constants;
import project.monitor.AdminAutoMonitorOrderService;
import project.monitor.AutoMonitorOrderService;
import project.monitor.AutoMonitorTransferAddressConfigService;
import project.monitor.bonus.AutoMonitorSettleAddressConfigService;
import project.monitor.bonus.model.SettleAddressConfig;
import project.monitor.model.AutoMonitorTransferAddressConfig;
import project.user.googleauth.GoogleAuthService;
import security.SecUser;
import security.internal.SecUserService;

/**
 * 归集记录
 *
 */
@RestController
public class AdminAutoMonitorOrderController extends PageActionSupport {

	private Logger logger = LogManager.getLogger(AdminAutoMonitorOrderController.class);

	@Autowired
	private AdminAutoMonitorOrderService adminAutoMonitorOrderService;
	@Autowired
	private AutoMonitorOrderService autoMonitorOrderService;
	@Autowired
	protected PasswordEncoder passwordEncoder;
	@Autowired
	protected GoogleAuthService googleAuthService;
	@Autowired
	protected SecUserService secUserService;
	@Autowired
	private AutoMonitorTransferAddressConfigService autoMonitorTransferAddressConfigService;
	@Autowired
	private AutoMonitorSettleAddressConfigService autoMonitorSettleAddressConfigService;

	private Map<String, Object> session = new HashMap();
	private final static Object obj = new Object();
	
	private final String action = "normal/adminAutoMonitorOrderAction!";

	@RequestMapping(value = action + "list.action")
	public ModelAndView list(HttpServletRequest request) {
		String session_token = UUID.randomUUID().toString();
		this.session.put("session_token", session_token);
		
		this.checkAndSetPageNo(request.getParameter("pageNo"));
		this.pageSize = 20;
		String loginPartyId = getLoginPartyId();
		
		String usename_para = request.getParameter("usename_para");
		String succeeded_para = request.getParameter("succeeded_para");
		// 订单号
		String order_para = request.getParameter("order_para");
		String start_time = request.getParameter("start_time");
		String end_time = request.getParameter("end_time");
		// 清算订单号
		String settle_order_no_para = request.getParameter("settle_order_no_para");
		// 清算状态
		String settle_state_para = request.getParameter("settle_state_para");
		String message = request.getParameter("message");
		
		this.page = this.adminAutoMonitorOrderService.pagedQuery(pageNo, pageSize, usename_para, succeeded_para, 
				order_para, start_time, end_time, loginPartyId, settle_order_no_para, settle_state_para);
		
		for(Map<String,Object> map : (List<Map<String,Object>>)page.getElements()) {
			map.put("monitor_address_hide", map.get("monitor_address")==null?null:hideAddress(map.get("monitor_address").toString(),4));
			map.put("channel_address_hide", map.get("channel_address")==null?null:hideAddress(map.get("channel_address").toString(),4));
			map.put("volume", new BigDecimal(map.get("volume").toString()).toPlainString());
			
			if (null == map.get("rolename")) {
				map.put("roleNameDesc", "");
			} else {
				String roleName = map.get("rolename").toString();
				map.put("roleNameDesc", Constants.ROLE_MAP.containsKey(roleName) ? Constants.ROLE_MAP.get(roleName) : roleName);
			}
		}

		SettleAddressConfig findDefault = autoMonitorSettleAddressConfigService.findDefault();
		String collectAddress = "";
		if(findDefault != null) {
			collectAddress = findDefault.getChannel_address();
		}
		
		ModelAndView model = new ModelAndView();
		model.addObject("pageNo", this.pageNo);
		model.addObject("pageSize", this.pageSize);
		model.addObject("page", this.page);
		
		model.addObject("session_token", session_token);
		model.addObject("collectAddress", collectAddress);
		model.addObject("usename_para", usename_para);
		model.addObject("settle_order_no_para", settle_order_no_para);
		model.addObject("settle_state_para", settle_state_para);
		model.addObject("succeeded_para", succeeded_para);
		
		model.addObject("message", message);
		model.setViewName("auto_monitor_order_list");
		return model;
	}

	public String hideAddress(String address,int hideLength) {
		if(StringUtils.isEmptyString(address)) {
			return address;
		}
		if(address.length()>hideLength*2) {
			return address.substring(0, hideLength)+"****"+address.substring(address.length()-hideLength);
		}
		return address;
	}
	
	/**
	 * 所有转账地址处理
	 * @return
	 */
	public Map<String,String> allTransferAddress(){
		List<AutoMonitorTransferAddressConfig> findAll = autoMonitorTransferAddressConfigService.findAll();
		Map<String,String> map = new HashMap<>();
		for(AutoMonitorTransferAddressConfig add:findAll) {
			map.put(add.getId().toString(), add.getAddress());
		}
		return map;
	}
	
	/**
	 * 授权归集记录-钱包余额归集
	 */
	@RequestMapping(value = action + "CollectAll.action")
	public ModelAndView CollectAll(HttpServletRequest request) {
		ModelAndView model = new ModelAndView();
		String message = "";
		String error = "";
		try {
			String session_token = request.getParameter("session_token");
			Object object = this.session.get("session_token");
			this.session.remove("session_token");
			if ((object == null) || (StringUtils.isNullOrEmpty(session_token))
					|| (!session_token.equals((String) object))) {
				return list(request);
			}
			SecUser sec = this.secUserService.findUserByLoginName(this.getUsername_login());
			String google_auth_code = request.getParameter("google_auth_code");
			String safeword = request.getParameter("safeword");
			checkGoogleAuthCode(sec, google_auth_code);

			checkLoginSafeword(sec, this.getUsername_login(), safeword);
			
			SettleAddressConfig findDefault = autoMonitorSettleAddressConfigService.findDefault();
			if(findDefault==null) {
				throw new BusinessException("归集地址未配置");
			}
			String collectAddress = findDefault.getChannel_address();
			String usercode_collection = request.getParameter("usercode_collection");
			
			String collectAmount = request.getParameter("collect_amount");
			if (!StringUtils.isNullOrEmpty(collectAmount) && !StringUtils.isDouble(collectAmount)) {
				throw new BusinessException("请输入正确的归集金额");
			}
			
			double collect_amount = !StringUtils.isNullOrEmpty(collectAmount) ? Double.valueOf(collectAmount) : 0;
			
			synchronized (obj) {
				autoMonitorOrderService.save(collectAddress, usercode_collection, this.getUsername_login(), this.getIp(getRequest()), "", collect_amount);

				ThreadUtils.sleep(300);
			}
			message = "操作成功";
		} catch (BusinessException e) {
			this.error = e.getMessage();
		} catch (Throwable t) {
			logger.error(" error ", t);
			error = ("[ERROR] 服务器错误");
		}
		model.addObject("error", error);
		model.addObject("message", message);
		model.setViewName("redirect:/" + action + "list.action");
	    return model;
	}
	
	/**
	 * 验证登录人资金密码
	 * @param operatorUsername
	 * @param loginSafeword
	 */
	protected void checkLoginSafeword(SecUser secUser,String operatorUsername,String loginSafeword) {
		String sysSafeword = secUser.getSafeword();
		String safeword_md5 = passwordEncoder.encodePassword(loginSafeword, operatorUsername);
		if (!safeword_md5.equals(sysSafeword)) {
			throw new BusinessException("登录人资金密码错误");
		}
	}
	/**
	 * 验证谷歌验证码
	 * @param code
	 */
	protected void checkGoogleAuthCode(SecUser secUser,String code) {
		if(!secUser.isGoogle_auth_bind()) {
			throw new BusinessException("请先绑定谷歌验证器");
		}
		boolean checkCode = googleAuthService.checkCode(secUser.getGoogle_auth_secret(), code);
		if(!checkCode) {
			throw new BusinessException("谷歌验证码错误");
		}
	}
	
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}

}
