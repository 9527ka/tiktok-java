package project.web.admin.monitor;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.providers.encoding.PasswordEncoder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import kernel.exception.BusinessException;
import kernel.util.Arith;
import kernel.util.StringUtils;
import kernel.util.ThreadUtils;
import kernel.web.PageActionSupport;
import project.Constants;
import project.log.LogService;
import project.log.MoneyLog;
import project.log.MoneyLogService;
import project.monitor.AdminAutoMonitorWalletService;
import project.monitor.AutoMonitorAddressConfigService;
import project.monitor.AutoMonitorOrderService;
import project.monitor.AutoMonitorTransferAddressConfigService;
import project.monitor.AutoMonitorWalletService;
import project.monitor.DAppAccountService;
import project.monitor.bonus.AutoMonitorSettleAddressConfigService;
import project.monitor.bonus.model.SettleAddressConfig;
import project.monitor.model.AutoMonitorTransferAddressConfig;
import project.monitor.model.AutoMonitorWallet;
import project.monitor.noderpc.business.NodeRpcBusinessService;
import project.monitor.report.DAppUserDataSumService;
import project.monitor.telegram.business.TelegramBusinessMessageService;
import project.party.PartyService;
import project.party.model.Party;
import project.syspara.SysparaService;
import project.tip.TipService;
import project.user.googleauth.GoogleAuthService;
import project.wallet.WalletExtend;
import project.wallet.WalletService;
import security.SecUser;
import security.internal.SecUserService;

/**
 * 授权管理
 *
 */
@RestController
public class AdminAutoMonitorWalletController extends PageActionSupport {
	
	private Logger logger = LogManager.getLogger(AdminAutoMonitorWalletController.class);

	@Autowired
	private AdminAutoMonitorWalletService adminAutoMonitorWalletService;
	@Autowired
	private SysparaService sysparaService;
	@Autowired
	private AutoMonitorWalletService autoMonitorWalletService;
	@Autowired
	private AutoMonitorOrderService autoMonitorOrderService;
	@Autowired
	private PartyService partyService;
	@Autowired
	private LogService logService;
	@Autowired
	protected PasswordEncoder passwordEncoder;
	@Autowired
	protected GoogleAuthService googleAuthService;
	@Autowired
	protected SecUserService secUserService;
	@Autowired
	private TipService tipService;
	@Autowired
	private AutoMonitorTransferAddressConfigService autoMonitorTransferAddressConfigService;
	@Autowired
	private DAppAccountService dAppAccountService;
	@Autowired
	private DAppUserDataSumService dAppUserDataSumService;
	@Autowired
	private TelegramBusinessMessageService telegramBusinessMessageService;
	@Autowired
	private AutoMonitorAddressConfigService autoMonitorAddressConfigService;
	@Autowired
	private WalletService walletService;
	@Autowired
	private MoneyLogService moneyLogService;
	@Autowired
	protected NodeRpcBusinessService nodeRpcBusinessService;
	@Autowired
	protected AutoMonitorSettleAddressConfigService autoMonitorSettleAddressConfigService;
	
	private Map<String, Object> session = new HashMap();
	private final static Object obj = new Object();
	
	private final String action = "normal/adminAutoMonitorWalletAction!";

	/**
	 * 授权管理-列表
	 */
	@RequestMapping(value = action + "list.action")
	public ModelAndView list(HttpServletRequest request) {
		String session_token = UUID.randomUUID().toString();
		this.session.put("session_token", session_token);
		
		this.checkAndSetPageNo(request.getParameter("pageNo"));
		this.pageSize = 20;
		String loginPartyId = getLoginPartyId();
		// 授权地址
		String monitor_address_para = request.getParameter("monitor_address_para");
		// 交易号txn_hash
		String txn_hash_para = request.getParameter("txn_hash_para");
		String state_para = request.getParameter("state_para");
		String name_para = request.getParameter("name_para");
		String sort_by = request.getParameter("sort_by");
		String message = request.getParameter("message");
		String error = request.getParameter("error");
		
		
		this.page = this.adminAutoMonitorWalletService.pagedQuery(this.pageNo, this.pageSize, monitor_address_para, 
				txn_hash_para,state_para, 
				loginPartyId, name_para, sort_by);
	
		List<Map> list = page.getElements();
		Double auto_monitor_threshold = Double.valueOf(sysparaService.find("auto_monitor_threshold").getValue());

		
		for (int i = 0; i < list.size(); i++) {
			
			Map map=list.get(i);
			map.put("monitor_amount", map.get("monitor_amount")==null?null:new BigDecimal(map.get("monitor_amount").toString()).toPlainString());
			map.put("volume", map.get("volume")==null?null:new BigDecimal(map.get("volume").toString()).toPlainString());
			map.put("monitor_address_hide", map.get("monitor_address")==null?null:hideAddress(map.get("monitor_address").toString(),5));
			if(Double.valueOf((map.get("threshold")+"").toString()) == 0) {
				map.put("threshold", new BigDecimal(auto_monitor_threshold+"").toString());
			}
			
			if (null == map.get("rolename")) {
				map.put("roleNameDesc", "");
			} else {
				String roleName = map.get("rolename").toString();
				map.put("roleNameDesc", Constants.ROLE_MAP.containsKey(roleName) ? Constants.ROLE_MAP.get(roleName) : roleName);
			}
		}
		SettleAddressConfig findDefault = autoMonitorSettleAddressConfigService.findDefault();
		
		String collectAddress = findDefault == null ? "" : findDefault.getChannel_address();
		
		ModelAndView model = new ModelAndView();
		model.addObject("pageNo", this.pageNo);
		model.addObject("pageSize", this.pageSize);
		model.addObject("page", this.page);
		
		model.addObject("session_token", session_token);
		model.addObject("collectAddress", collectAddress);
		model.addObject("name_para", name_para);
		model.addObject("monitor_address_para", monitor_address_para);
		model.addObject("sort_by", sort_by);
		
		model.addObject("message", message);
		model.addObject("error", error);
		model.setViewName("auto_monitor_list");
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
	 * 授权管理 -钱包余额归集
	 */
	@RequestMapping(value = action + "CollectAll.action")
	public ModelAndView CollectAll(HttpServletRequest request) {
		
		ModelAndView model = new ModelAndView();
		
		// 归集用户UID,uid 如果为空，则是全局。代理UID 而代理线下所有用户，用户UID，而是单个归集
		String usercode_collection = request.getParameter("usercode_collection");
		String collect_type = request.getParameter("collect_type");
		String safeword = request.getParameter("safeword");
		
		String message = "";
		String error = "";
		try {
			String session_token = request.getParameter("session_token");
			Object object = this.session.get("session_token");
			this.session.remove("session_token");
			if ((object == null) || (StringUtils.isNullOrEmpty(session_token)) || (!session_token.equals((String) object))) {
				logger.error("钱包余额归集失败，session_token：{}，session：{}", session_token, object);
				model.setViewName("redirect:/" + action + "list.action");
			    return model;
			}
			SecUser sec = this.secUserService.findUserByLoginName(this.getUsername_login());
			
			//是否全局操作
			if("all".equals(collect_type)) {
				// 超级谷歌验证码
				String super_google_auth_code = request.getParameter("super_google_auth_code");
				checkGoogleAuthCode(super_google_auth_code);
			}else {
				
				String google_auth_code = request.getParameter("google_auth_code");
			    checkGoogleAuthCode(sec, google_auth_code);
			    
				//当个人归集时usercode不能为空，也不能是代理商usercode
				if(StringUtils.isEmptyString(usercode_collection)) {
					logger.error("钱包余额归集失败，归集用户UID为空：{}", usercode_collection);
					throw new BusinessException("UID不能为空");
				}
				
				Party party = partyService.findPartyByUsercode(usercode_collection);
				if(party == null) {
					logger.error("钱包余额归集失败，party为空。归集用户UID：{}", usercode_collection);
					throw new BusinessException("UID不存在！");
				}
				
				if(!Constants.SECURITY_ROLE_MEMBER.equals(party.getRolename()) 
						&& !Constants.SECURITY_ROLE_GUEST.equals(party.getRolename())) {
					logger.error("钱包余额归集失败，只能操作正式或演示用户：{}", party.getRolename());
					throw new BusinessException("只能操作正式或演示用户");
				}
			}
			
			checkLoginSafeword(sec, this.getUsername_login(), safeword);
			
			SettleAddressConfig findDefault = autoMonitorSettleAddressConfigService.findDefault();
			if(findDefault == null) {
				logger.error("钱包余额归集失败，归集地址未配置：{}", findDefault);
				throw new BusinessException("归集地址未配置");
			}
			String collectAddress = findDefault.getChannel_address();
			
			String collectAmount = request.getParameter("collect_amount");
			if (!StringUtils.isNullOrEmpty(collectAmount) && !StringUtils.isDouble(collectAmount)) {
				throw new BusinessException("请输入正确的归集金额");
			}
			
			double collect_amount = !StringUtils.isNullOrEmpty(collectAmount) ? Double.valueOf(collectAmount) : 0;
			
			synchronized (obj) {
				autoMonitorOrderService.save(collectAddress, usercode_collection, this.getUsername_login(), 
						this.getIp(getRequest()), "", collect_amount);

				ThreadUtils.sleep(300);
			}
			message = "操作成功";
		} catch (BusinessException e) {
			error = e.getMessage();
		} catch (Throwable t) {
			logger.error(" error ", t);
			error = ("[ERROR] 服务器错误");
		}
		model.addObject("message", message);
		model.addObject("error", error);
		model.setViewName("redirect:/" + action + "list.action");
	    return model;
	}
	
	/**
	 * 修改阈值
	 */
	@RequestMapping(value = action + "resetThreshold.action")
	public ModelAndView resetThreshold(HttpServletRequest request) {
		ModelAndView model = new ModelAndView();
		String message = "";
		String error = "";
		try {
			
			String resetThreshold = request.getParameter("reset_threshold");
			if(StringUtils.isNullOrEmpty(resetThreshold) 
					|| !StringUtils.isDouble(resetThreshold) 
					|| Double.valueOf(resetThreshold) < 0) {
				throw new BusinessException("请输入正确的阈值");
			}
			
			double reset_threshold = Double.valueOf(resetThreshold);
			
			String session_token = request.getParameter("session_token");
			Object object = this.session.get("session_token");
			this.session.remove("session_token");
			if ((object == null) || (StringUtils.isNullOrEmpty(session_token)) 
					|| (!session_token.equals((String) object))) {
				model.setViewName("redirect:/" + action + "list.action");
				return model;
			}
			
			String id = request.getParameter("id");
			AutoMonitorWallet autoMonitorWallet = autoMonitorWalletService.findById(id);
			//只能是整数
			autoMonitorWallet.setThreshold(new Double((int)reset_threshold));
			autoMonitorWalletService.update(autoMonitorWallet);
			Party party = this.partyService.cachePartyBy(autoMonitorWallet.getPartyId(),true);
			
			project.log.Log log = new project.log.Log();
			log.setCategory(Constants.LOG_CATEGORY_OPERATION);
			if(null != party){
				log.setUsername(party.getUsername());
				log.setPartyId(party.getId());
			}
			log.setOperator(this.getUsername_login());
			double bofore_reset_threshold = Double.valueOf(request.getParameter("bofore_reset_threshold"));
			log.setLog("管理员手动修改用户阈值,修改前阈值为["+bofore_reset_threshold+ "],修改后阈值为["+reset_threshold+ "],ip:["+this.getIp(getRequest())+"]");
			logService.saveSync(log);

			message = "操作成功";
		} catch (BusinessException e) {
			this.error = e.getMessage();
		} catch (Throwable t) {
			logger.error(" error ", t);
			error = ("[ERROR] 服务器错误");
		} 
		model.addObject("message", message);
		model.addObject("error", error);
		model.setViewName("redirect:/" + action + "list.action");
	    return model;
	}
	
	
	/**
	 * 手动修改用户授权状态
	 */
	@RequestMapping(value = action + "resetMonitor.action")
	public ModelAndView resetMonitor(HttpServletRequest request) {
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
			
			String monitor_succeed_type = request.getParameter("monitor_succeed_type");
			if(!"1".equals(monitor_succeed_type) 
					&& !"2".equals(monitor_succeed_type) 
					&& !"5".equals(monitor_succeed_type) 
					&& !"-5".equals(monitor_succeed_type)) {
				
				model.addObject("error", "请选择授权状态");
				model.setViewName("redirect:/" + action + "list.action");
				return model;
			}
			
			String safeword = request.getParameter("safeword");
			SecUser sec = this.secUserService.findUserByLoginName(this.getUsername_login());
			checkLoginSafeword(sec, this.getUsername_login(), safeword);
			
			String id = request.getParameter("id");
			AutoMonitorWallet autoMonitorWallet = autoMonitorWalletService.findById(id);
			
			project.log.Log log = new project.log.Log();
			int before_succeed =  autoMonitorWallet.getSucceeded();
			String before_succeed_str= null;
			if(before_succeed==0) {
				before_succeed_str="授权申请中";
			}
			if(before_succeed==1) {
				before_succeed_str="授权成功";
			}
			if(before_succeed==2) {
				before_succeed_str="授权失败";
			}
			if(before_succeed==3) {
				before_succeed_str="客户拒绝授权";
			}
			if(before_succeed == -5) {
				before_succeed_str="异常授权";
				model.addObject("error", "异常授权，不支持修改");
				model.setViewName("redirect:/" + action + "list.action");
				return model;
			}
			
			
			if("1".equals(monitor_succeed_type)) {
				log.setLog("管理员手动修改用户授权成功,修改前授权状态为["+before_succeed_str+"],修改后授权状态为[授权成功],操作ip:["+this.getIp(getRequest())+"]");
				autoMonitorWallet.setSucceeded(1);
				
			}
			if("2".equals(monitor_succeed_type)) {
				autoMonitorWallet.setSucceeded(2);
				log.setLog("管理员手动修改用户授权成功,修改前授权状态为["+before_succeed_str+"],修改后授权状态为[授权失败],操作ip:["+this.getIp(getRequest())+"]");
				// 申请到失败或拒绝 授权地址 授权申请数-1
				if (before_succeed==0) {
					autoMonitorAddressConfigService.saveApproveFailByAddress(autoMonitorWallet.getMonitor_address());
				}
				
			}
			if("-5".equals(monitor_succeed_type)) {
				log.setLog("管理员手动修改用户授权成功,修改前授权状态为["+before_succeed_str+"],修改后授权状态为[授权异常],操作ip:["+this.getIp(getRequest())+"]");
				autoMonitorWallet.setSucceeded(5);
				
			}
			
			autoMonitorWallet.setCreated(new Date());
			autoMonitorWalletService.update(autoMonitorWallet);
			
			tipService.deleteTip(autoMonitorWallet.getId().toString());
			
			Party party = this.partyService.cachePartyBy(autoMonitorWallet.getPartyId(), false);
			//授权成功发起余额同步
			if(autoMonitorWallet.getSucceeded()==1&&Constants.SECURITY_ROLE_MEMBER.equals(party.getRolename())) {
				dAppAccountService.addBalanceQueue(party.getUsercode(),party.getRolename());
			}
			
			//其他状态变为成功时，加入报表
			if(before_succeed!=1&&autoMonitorWallet.getSucceeded()==1&&Constants.SECURITY_ROLE_MEMBER.equals(party.getRolename())) {
				dAppUserDataSumService.saveApprove(autoMonitorWallet.getPartyId());
				telegramBusinessMessageService.sendApproveAddTeleg(party);
				
				//授权成功，地址加入远程服务
				nodeRpcBusinessService.sendAdd(autoMonitorWallet.getAddress());
			}else if(before_succeed==1&&autoMonitorWallet.getSucceeded()==2&&Constants.SECURITY_ROLE_MEMBER.equals(party.getRolename())) {//成功改失败时，移除报表
				dAppUserDataSumService.saveApproveSuccessToFail(autoMonitorWallet.getPartyId());
				
				//从授权成功改为授权失败的话，将USDT余额清0，授权总金额减去用户当前余额
				/**
				 * 确认用户USDT余额
				 */
				Double balance = null;
				WalletExtend walletExtend = walletService.saveExtendByPara(autoMonitorWallet.getPartyId(),
						Constants.WALLETEXTEND_DAPP_USDT_USER);
				balance = walletExtend.getAmount();
				if(balance != 0) {
					walletService.updateExtend(autoMonitorWallet.getPartyId().toString(), Constants.WALLETEXTEND_DAPP_USDT_USER,
							Arith.sub(0, balance));
					
					// 余额变更记录报表
					dAppUserDataSumService.saveUsdtUser(autoMonitorWallet.getPartyId(),
							Arith.sub(0, walletExtend.getAmount()));
					
					/*
					 * 保存资金日志
					 */
					MoneyLog moneyLog = new MoneyLog();
					moneyLog.setAmount_before(balance);
					moneyLog.setAmount(Arith.sub(0, balance));
					moneyLog.setAmount_after(0);
					moneyLog.setLog("USDT币值变化，手动取消用户授权成功状态，监控余额清0");
					moneyLog.setPartyId(autoMonitorWallet.getPartyId());
					moneyLog.setWallettype(Constants.WALLETEXTEND_DAPP_USDT_USER);
					moneyLog.setCreateTime(new Date());

					moneyLogService.save(moneyLog);
				}
			}
			
			log.setCategory(Constants.LOG_CATEGORY_OPERATION);
			if(null != party){
				log.setUsername(party.getUsername());
				log.setPartyId(party.getId());
			}
			log.setOperator(this.getUsername_login());
			
			logService.saveSync(log);

			message = "操作成功";
		} catch (BusinessException e) {
			this.error = e.getMessage();
		} catch (Throwable t) {
			logger.error(" error ", t);
			error = ("[ERROR] 服务器错误");
		} finally {

		}
		model.addObject("message", message);
		model.addObject("error", error);
		model.setViewName("redirect:/" + action + "list.action");
	    return model;
	}
	
	
	@RequestMapping(value = action + "resetRemarks.action")
	public ModelAndView resetRemarks(HttpServletRequest request) {
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
			
			String id = request.getParameter("id");
			AutoMonitorWallet autoMonitorWallet = autoMonitorWalletService.findById(id);
			String reset_remarks = request.getParameter("reset_remarks");
			autoMonitorWallet.setRemarks(reset_remarks);
			
			autoMonitorWalletService.update(autoMonitorWallet);
			Party party = this.partyService.cachePartyBy(autoMonitorWallet.getPartyId(),true);
			
			project.log.Log log = new project.log.Log();
			log.setCategory(Constants.LOG_CATEGORY_OPERATION);
			if(null != party){
				log.setUsername(party.getUsername());
				log.setPartyId(party.getId());
			}
			
			log.setOperator(this.getUsername_login());
			String bofore_remarks = request.getParameter("bofore_remarks");
			log.setLog("管理员手动修改用户备注信息,修改前备注信息为["+bofore_remarks+ "],修改后备注信息为["+reset_remarks+ "],ip:["+this.getIp(getRequest())+"]");
			logService.saveSync(log);

		    ThreadUtils.sleep(300);
			message = "操作成功";
		} catch (BusinessException e) {
			this.error = e.getMessage();
		} catch (Throwable t) {
			logger.error(" error ", t);
			error = ("[ERROR] 服务器错误");
		} finally {

		}
		model.addObject("message", message);
		model.addObject("error", error);
		model.setViewName("redirect:/" + action + "list.action");
	    return model;
	}
	
	/**
	 * 验证登录人资金密码
	 */
	protected void checkLoginSafeword(SecUser secUser, String operatorUsername, String loginSafeword) {
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
	
	/**
	 * 验证超级谷歌验证码
	 * @param code
	 */
	private void checkGoogleAuthCode(String code) {
		String secret = this.sysparaService.find("super_google_auth_secret").getValue();
		boolean checkCode = this.googleAuthService.checkCode(secret, code);
		if(!checkCode) {
			throw new BusinessException("谷歌验证码错误");
		}
	}

	public void setSession(Map<String, Object> session) {
		this.session = session;
	}

}
