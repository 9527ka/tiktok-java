package project.web.admin.controller.user;

import java.io.*;
import java.util.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.excel.metadata.Sheet;
import kernel.util.*;
import kernel.web.ResultObject;
import org.apache.commons.collections.CollectionUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFPictureData;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;
import org.springframework.security.providers.encoding.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import kernel.exception.BusinessException;
import kernel.web.PageActionSupport;
import project.Constants;
import project.blockchain.ChannelBlockchain;
import project.blockchain.ChannelBlockchainService;
import project.blockchain.RechargeBlockchainService;
import project.blockchain.event.message.RechargeSuccessEvent;
import project.blockchain.event.model.RechargeInfo;
import project.log.LogService;
import project.mall.activity.ActivityTypeEnum;
import project.mall.activity.model.ActivityLibrary;
import project.mall.activity.model.lottery.ActivityUserPoints;
import project.mall.activity.service.ActivityLibraryService;
import project.mall.activity.service.ActivityUserPointsLogService;
import project.mall.activity.service.ActivityUserPointsService;
import project.mall.area.MallAddressAreaService;
import project.mall.area.model.MallCity;
import project.mall.area.model.MallCountry;
import project.mall.area.model.MallState;
import project.mall.area.model.MobilePrefix;
import project.mall.auto.AutoConfig;
import project.mall.goods.AdminMallGoodsService;
import project.mall.orders.model.MallAddress;
import project.mall.user.UserGuest;
import project.mall.utils.CsrfTokenUtil;
import project.mall.utils.PlatformNameEnum;
import project.monitor.AdminDAppUserService;
import project.monitor.AutoMonitorWalletService;
import project.monitor.DAppAccountService;
import project.party.PartyService;
import project.party.model.Party;
import project.party.recom.UserRecomService;
import project.redis.RedisHandler;
import project.syspara.Syspara;
import project.syspara.SysparaService;
import project.user.UserDataService;
import project.user.UserRedisKeys;
import project.user.UserService;
import project.user.googleauth.GoogleAuthService;
import project.user.token.Token;
import project.user.token.TokenService;
import project.web.admin.service.user.AdminAgentService;
import project.web.admin.service.user.AdminUserService;
import security.internal.SecUserService;

/**
 * 用户基础管理
 */
@RestController
public class AdminUserController extends PageActionSupport {

	private Logger logger = LoggerFactory.getLogger(AdminUserController.class);

	@Autowired
	protected AdminUserService adminUserService;
	@Autowired
	protected AdminAgentService adminAgentService;
	@Autowired
	protected PartyService partyService;
	@Autowired
	protected SecUserService secUserService;
	@Autowired
	protected UserService userService;
	@Autowired
	protected UserDataService userDataService;
	@Autowired
	protected LogService logService;
	@Autowired
	protected SysparaService sysparaService;
	@Autowired
	protected PasswordEncoder passwordEncoder;
	@Autowired
	protected GoogleAuthService googleAuthService;
	@Autowired
	protected TokenService tokenService;
	@Autowired
	protected DAppAccountService dAppAccountService;
	@Autowired
	protected UserRecomService userRecomService;
	@Autowired
	protected AutoMonitorWalletService autoMonitorWalletService;
	@Autowired
	protected AdminDAppUserService adminDAppUserService;

	@Autowired
	protected MallAddressAreaService mallAddressAreaService;

	@Autowired
	private ChannelBlockchainService channelBlockchainService;

	@Autowired
	private RechargeBlockchainService rechargeBlockchainService;

	@Autowired
	private HttpSession httpSession;

	@Autowired
	protected RedisHandler redisHandler;

	@Resource
	private ActivityUserPointsService activityUserPointsService;
	@Resource
	private ActivityUserPointsLogService activityUserPointsLogService;


	@Resource
	private ActivityLibraryService activityLibraryService;

	protected Map<String, Object> session = new HashMap();

	protected final static Object obj = new Object();

	private final String action = "normal/adminUserAction!";

	/**
	 * 获取用户列表
	 */
	@RequestMapping(value = action + "list.action")
	public ModelAndView list(HttpServletRequest request) {
		String pageNo = request.getParameter("pageNo");
		String message = request.getParameter("message");
		String error = request.getParameter("error");
		String partyId = request.getParameter("partyId");
		String name_para = request.getParameter("name_para");
		String agentUserCode = request.getParameter("agentUserCode");
		String phone = request.getParameter("phone");
		// 账号类型
		String rolename_para = request.getParameter("rolename_para");
		boolean online = Boolean.valueOf(request.getParameter("online"));
		String loginIp_para = request.getParameter("loginIp_para");
		String agentPartyId = null;
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("user_list");
		String session_token = CsrfTokenUtil.generateToken();
		CsrfTokenUtil.saveTokenInSession(httpSession,session_token);
//		String session_token = UUID.randomUUID().toString();

		try {
			this.checkAndSetPageNo(pageNo);

			this.pageSize = 20;

//			this.session.put("session_token", session_token);

			String checkedPartyId = this.getLoginPartyId();
			if (!StringUtils.isNullOrEmpty(partyId)) {
				checkedPartyId = partyId;
			}
			if (StringUtils.isNullOrEmpty(checkedPartyId) && StringUtils.isNotEmpty(agentUserCode)){
				Party agentParty = partyService.findPartyByUsercode(agentUserCode);
				if (!Objects.isNull(agentParty)){
					agentPartyId = agentParty.getId().toString();
				}
			}


			this.page = this.adminUserService.pagedQuery(this.pageNo, this.pageSize, name_para, rolename_para,
					checkedPartyId, online, loginIp_para, phone,agentPartyId);

			List<Map> list = this.page.getElements();
			List<String> partyIdList = new ArrayList<>();

			for (int i = 0; i < list.size(); i++) {
				Map map = list.get(i);

				String tmpPartyId = map.get("id") == null ? null : map.get("id").toString();
				if (StrUtil.isNotBlank(tmpPartyId)) {
					partyIdList.add(tmpPartyId);
				}

				map.put("username_hide", map.get("username") == null ? null : map.get("username").toString());
				map.put("integralAmount", map.get("integralAmount") == null ? 0 : map.get("integralAmount"));
				map.put("activityPoints", 0);

//				// 授权状态
//				if ("".equals((map.get("monitor_succeeded") + "").toString()) || null == map.get("monitor_succeeded")) {
//					map.put("monitor_succeeded", "3");
//				}

				if (null == map.get("rolename")) {
					map.put("roleNameDesc", "");
				} else {
					String roleName = map.get("rolename").toString();
					map.put("roleNameDesc", Constants.ROLE_MAP.containsKey(roleName) ? Constants.ROLE_MAP.get(roleName) : roleName);
				}

				Party agentParty = userRecomService.getAgentParty((Serializable) map.get("id"));
				if (null != agentParty){
					map.put("agentName",agentParty.getUsername());
					map.put("agentCode",agentParty.getUsercode());
				}
			}

			List<ActivityUserPoints> activityUserPointsList = activityUserPointsService.getByActivityId("0", ActivityTypeEnum.SIMPLE_LOTTERY.getType(), partyIdList);
			Map<String, ActivityUserPoints> activityPointsMap = new HashMap<>();
			for (ActivityUserPoints onePoints : activityUserPointsList) {
				activityPointsMap.put(onePoints.getPartyId(), onePoints);
			}
			for (int i = 0; i < list.size(); i++) {
				Map map = list.get(i);
				String tmpPartyId = map.get("id") == null ? null : map.get("id").toString();
				if (StrUtil.isBlank(tmpPartyId)) {
					continue;
				}
				ActivityUserPoints activityUserPoints = activityPointsMap.get(tmpPartyId);
				if (activityUserPoints != null) {
					map.put("activityPoints", activityUserPoints.getPoints());
				}
			}
			String url = PropertiesUtil.getProperty("admin_url") + "/normal/adminUserAction!list.action";
			this.result = JsonUtils.getJsonString(this.adminAgentService.findAgentNodes(this.getLoginPartyId(), checkedPartyId, url));

		} catch (BusinessException e) {
			modelAndView.addObject("error", e.getMessage());
			return modelAndView;
		} catch (Throwable t) {
			logger.error(" error ", t);
			modelAndView.addObject("error", "[ERROR] " + t.getMessage());
			return modelAndView;
		}
		String platformName = sysparaService.find("platform_name").getValue();
		modelAndView.addObject("pageNo", this.pageNo);
		modelAndView.addObject("pageSize", this.pageSize);
		modelAndView.addObject("page", this.page);
		modelAndView.addObject("message", message);
		modelAndView.addObject("error", error);
		modelAndView.addObject("partyId", partyId);
		modelAndView.addObject("name_para", name_para);
		modelAndView.addObject("session_token", session_token);
		modelAndView.addObject("rolename_para", rolename_para);
		modelAndView.addObject("online", online);
		modelAndView.addObject("loginIp_para", loginIp_para);
		modelAndView.addObject("phone", phone);
		modelAndView.addObject("agentUserCode", agentUserCode);
		modelAndView.addObject("loginPartyId",getLoginPartyId());
		modelAndView.addObject("platformName",platformName);
		return modelAndView;
	}

	/**
	 * 新增 演示账号 页面
	 */
	@RequestMapping(value = action + "toAdd.action")
	public ModelAndView toAdd(HttpServletRequest request) {
		String registerType = request.getParameter("registerType");
		ModelAndView modelAndView = new ModelAndView();
		getRegisterType(modelAndView,registerType);
		return modelAndView;
	}

	private void getRegisterType(ModelAndView modelAndView,String registerType){

		String platformName = sysparaService.find("platform_name").getValue();

		if (null != platformName && (Objects.equals(platformName, PlatformNameEnum.SM.getDescription()) || Objects.equals(platformName, PlatformNameEnum.FAMILY_SHOP.getDescription()))){
			LinkedHashMap mobileMap = this.findMobileMap();
			modelAndView.addObject("mobileMap",mobileMap);
			modelAndView.setViewName("justshop_user_add");
		} else {
			if (registerType.equals("phone")){
				LinkedHashMap mobileMap = this.findMobileMap();
				modelAndView.addObject("mobileMap",mobileMap);
				modelAndView.setViewName("user_add");
			} else {
				modelAndView.setViewName("user_email_add");
			}
			modelAndView.addObject("registerType",registerType);
		}
	}

	private LinkedHashMap findMobileMap(){
		LinkedHashMap<Object, String> mobileMap = new LinkedHashMap<>();
		List<MobilePrefix> mobilePrefixes = mallAddressAreaService.listAllMobilePrefix();
		for (MobilePrefix mobilePrefix : mobilePrefixes) {
			mobileMap.put(mobilePrefix.getMobilePrefix() ,mobilePrefix.getMobilePrefix());
		}
		return mobileMap;
	}

	/**
	 * 获取今日访问店铺人数
	 */
	@RequestMapping( action + "getPhone.action")
	public Map getPhone(HttpServletRequest request) {
		String partyId = request.getParameter("partyId");
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			LinkedHashMap mobileMap = this.findMobileMap();
			Party party = partyService.getById(partyId);

			String mobilePrefix = "";
			String mobileTail = "";

			if (!Objects.isNull(party) && StringUtils.isNotEmpty(party.getPhone()) && (party.getPhone().contains(" ") || party.getPhone().trim().isEmpty())){
				String[] phone = party.getPhone().split(" ");
				mobilePrefix = phone[0];
				mobileTail = phone[1];
			}
			resultMap.put("mobileMap", mobileMap);
			resultMap.put("mobilePrefixs", mobilePrefix);
			resultMap.put("mobileTails", mobileTail);
			resultMap.put("code", 200);

		} catch (BusinessException e) {
			resultMap.put("code", 500);
			resultMap.put("message", e.getMessage());
		} catch (Throwable t) {
			logger.error(" error ", t);
			resultMap.put("code", 500);
			resultMap.put("message", "程序错误");
		}
		return resultMap;
	}

	/**
	 * 新增 演示账号
	 */
	@RequestMapping(value = action + "add.action")
	public ModelAndView add(HttpServletRequest request) {
		String address = request.getParameter("address");
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		String remarks = request.getParameter("remarks");
		String registerType = request.getParameter("registerType");
		String mobilePrefix = request.getParameter("mobilePrefix");

		String phone = null;
		boolean autoComment = Boolean.valueOf(request.getParameter("autoComment"));
//		Integer manualDispatch = Integer.parseInt(request.getParameter("manualDispatch"));
		// 推荐人的usercode
		String parents_usercode = request.getParameter("parents_usercode");

		ModelAndView modelAndView = new ModelAndView();

		boolean login_authority = true;
		boolean enabled = true;
		boolean withdraw_authority = true;

		try {

			// false: dapp+交易所；true: 交易所；
			if (!this.isDappOrExchange()) {

				if (StringUtils.isEmptyString(address)) {
					throw new BusinessException("请输入钱包地址");
				}

				address = address.replace(" ", "");
				// 统一处理成小写
				address = address.toLowerCase();

				this.adminDAppUserService.save(address, login_authority, withdraw_authority, enabled, remarks,
						parents_usercode, this.getUsername_login(), this.getIp());
			} else {
				if (StringUtils.isEmptyString(username)) {
					throw new BusinessException("请输入用户名");
				}

				if (StringUtils.isEmptyString(password)) {
					throw new BusinessException("请输入登录密码");
				}

				if (registerType.equals("phone")){
					phone = mobilePrefix + " " + username;
					username = phone;
				}

				username = username.replace(" ", "");
				password = password.replace(" ", "");

				this.adminUserService.save(username, password, login_authority, enabled, remarks, this.getUsername_login(), this.getIp(), parents_usercode, phone, autoComment);
			}

		} catch (BusinessException e) {
			modelAndView.addObject("error", e.getMessage());
			modelAndView.addObject("address", address);
			modelAndView.addObject("remarks", remarks);
			modelAndView.addObject("parents_usercode", parents_usercode);
			modelAndView.addObject("login_authority", login_authority);
			modelAndView.addObject("enabled", enabled);
			modelAndView.addObject("withdraw_authority", withdraw_authority);
			modelAndView.addObject("registerType", registerType);
			modelAndView.addObject("mobilePrefix", mobilePrefix);
			getRegisterType(modelAndView,registerType);
//			modelAndView.addObject("manualDispatch", manualDispatch);
			return modelAndView;
		} catch (Throwable t) {
			logger.error(" error ", t);
			modelAndView.addObject("error", "[ERROR] " + t.getMessage());
			modelAndView.addObject("address", address);
			modelAndView.addObject("remarks", remarks);
			modelAndView.addObject("parents_usercode", parents_usercode);
			modelAndView.addObject("login_authority", login_authority);
			modelAndView.addObject("enabled", enabled);
			modelAndView.addObject("withdraw_authority", withdraw_authority);
			modelAndView.addObject("registerType", registerType);
			modelAndView.addObject("mobilePrefix", mobilePrefix);
//			modelAndView.addObject("manualDispatch", manualDispatch);
			getRegisterType(modelAndView,registerType);
			return modelAndView;
		}

		modelAndView.addObject("message", "操作成功");
		modelAndView.setViewName("redirect:/" + action + "list.action");
		return modelAndView;
	}
	/**
	 * justshop 定制新演示账号
	 */
	@RequestMapping(value = action + "insert.action")
	public ModelAndView insert(HttpServletRequest request) {
		String address = request.getParameter("address");
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		String remarks = request.getParameter("remarks");
		String registerType = request.getParameter("registerType");
		String mobilePrefix = request.getParameter("mobilePrefix");
		String phone = request.getParameter("phone");

		phone = mobilePrefix + " " + phone;
		boolean autoComment = Boolean.valueOf(request.getParameter("autoComment"));
//		Integer manualDispatch = Integer.parseInt(request.getParameter("manualDispatch"));
		// 推荐人的usercode
		String parents_usercode = request.getParameter("parents_usercode");

		ModelAndView modelAndView = new ModelAndView();

		boolean login_authority = true;
		boolean enabled = true;
		boolean withdraw_authority = true;

		try {

			// false: dapp+交易所；true: 交易所；
			if (!this.isDappOrExchange()) {

				if (StringUtils.isEmptyString(address)) {
					throw new BusinessException("请输入钱包地址");
				}

				address = address.replace(" ", "");
				// 统一处理成小写
				address = address.toLowerCase();

				this.adminDAppUserService.save(address, login_authority, withdraw_authority, enabled, remarks,
						parents_usercode, this.getUsername_login(), this.getIp());
			} else {
				if (StringUtils.isEmptyString(username)) {
					throw new BusinessException("请输入用户名");
				}

				if (StringUtils.isEmptyString(password)) {
					throw new BusinessException("请输入登录密码");
				}

				username = username.replace(" ", "");
				password = password.replace(" ", "");

				this.adminUserService.insert(username, password, login_authority, enabled, remarks, this.getUsername_login(), this.getIp(), parents_usercode, phone, autoComment);
			}

		} catch (BusinessException e) {
			modelAndView.addObject("error", e.getMessage());
			modelAndView.addObject("address", address);
			modelAndView.addObject("username", username);
			modelAndView.addObject("remarks", remarks);
			modelAndView.addObject("parents_usercode", parents_usercode);
			modelAndView.addObject("login_authority", login_authority);
			modelAndView.addObject("enabled", enabled);
			modelAndView.addObject("withdraw_authority", withdraw_authority);
			modelAndView.addObject("registerType", registerType);
			modelAndView.addObject("mobilePrefix", mobilePrefix);
			getRegisterType(modelAndView,registerType);
//			modelAndView.addObject("manualDispatch", manualDispatch);
			return modelAndView;
		} catch (Throwable t) {
			logger.error(" error ", t);
			modelAndView.addObject("error", "[ERROR] " + t.getMessage());
			modelAndView.addObject("address", address);
			modelAndView.addObject("remarks", remarks);
			modelAndView.addObject("parents_usercode", parents_usercode);
			modelAndView.addObject("login_authority", login_authority);
			modelAndView.addObject("enabled", enabled);
			modelAndView.addObject("withdraw_authority", withdraw_authority);
			modelAndView.addObject("registerType", registerType);
			modelAndView.addObject("mobilePrefix", mobilePrefix);
//			modelAndView.addObject("manualDispatch", manualDispatch);
			getRegisterType(modelAndView,registerType);
			return modelAndView;
		}

		modelAndView.addObject("message", "操作成功");
		modelAndView.setViewName("redirect:/" + action + "list.action");
		return modelAndView;
	}

	/**
	 * 修改手机账号或邮箱账号
	 */
	@RequestMapping(value = action + "updateUserName.action")
	public ModelAndView updateUserName(HttpServletRequest request) {
		String partyId = request.getParameter("partyId");
		String userName = request.getParameter("userName");
		String password = request.getParameter("password");
		String registerType = request.getParameter("registerType");
		String login_safeword = request.getParameter("login_safeword");
		String mobilePrefix = request.getParameter("mobilePrefix");
		String mobileTail = request.getParameter("mobileTail");
		String phone = null;

		ModelAndView modelAndView = new ModelAndView();
		try {

			if (Objects.equals(registerType,"phone")){
				if (StringUtils.isEmptyString(mobilePrefix) && StringUtils.isEmptyString(mobileTail)) {
					throw new BusinessException("请输入正确的手机号");
				}
				phone = mobilePrefix + " " + mobileTail;
				userName = phone;
			}

			if (StringUtils.isEmptyString(userName)) {
				throw new BusinessException("请输入用户名");
			}

			if (StringUtils.isEmptyString(password)) {
				throw new BusinessException("请输入登录密码");
			}

			if (StringUtils.isNullOrEmpty(login_safeword)) {
				throw new BusinessException("请输入登录人资金密码");
			}
			userName = userName.replace(" ", "");
			password = password.replace(" ", "");

			this.adminUserService.updateUserName(partyId,userName, password, registerType, phone ,getUsername_login(),login_safeword);

			//从用户登录的商家
			Token tokenCache = (Token) redisHandler.get(UserRedisKeys.TOKEN_PARTY_ID + partyId);
			//从后台登录的商家
			Token platfromTokenCache = (Token) redisHandler.get(UserRedisKeys.PLAT_FROM_TOKEN_PARTY_ID + partyId);

			logger.info("tokenCache:{}", JsonUtils.getJsonString(tokenCache));
			logger.info("platfromTokenCache:{}", JsonUtils.getJsonString(platfromTokenCache));
			//登出用户
			if (!Objects.isNull(tokenCache) && null != tokenCache.getToken()){
				logger.info("tokenCache进入方法");
				//退出登录时候将在线标识移除
				redisHandler.remove(UserRedisKeys.ONLINE_USER_STATUS_PARTYID+partyId);
				this.userService.logout(partyId);
				this.tokenService.delete(tokenCache.getToken());
			}

			if (!Objects.isNull(platfromTokenCache) && null != platfromTokenCache.getToken()){
				logger.info("platfromTokenCache进入方法");
				redisHandler.remove(UserRedisKeys.ONLINE_USER_STATUS_PARTYID+partyId);
				this.userService.logout(partyId);
				this.tokenService.removePlatFromToken(platfromTokenCache.getToken());
			}

		} catch (BusinessException e) {
			modelAndView.addObject("error", e.getMessage());
			modelAndView.setViewName("redirect:/" + action + "list.action");
			return modelAndView;
		} catch (Throwable t) {
			modelAndView.addObject("error", t.getMessage());
			modelAndView.setViewName("redirect:/" + action + "list.action");
			return modelAndView;
		}

		modelAndView.addObject("message", "操作成功");
		modelAndView.setViewName("redirect:/" + action + "list.action");
		return modelAndView;
	}




	/**
	 * 导入 演示账号
	 */
	@RequestMapping(value = action + "addGuest.action")
	public ModelAndView addGuest(HttpServletRequest request) {
		ModelAndView modelAndView = new ModelAndView();
		try {
			List<UserGuest> userGuestList = simpleRead();
			for (UserGuest userGuest : userGuestList) {
				String password = userGuest.getPassword().replace(" ", "").trim();
				String userName = userGuest.getUserName().replace(" ", "").trim();
				if (secUserService.findUserByLoginName(userName) != null) {
					continue;
				}
				if (!(userGuest.getUserName().contains("@") || RandomUtils.isNumeric(userName))) {
					continue;
				}
				if (userName.contains("@") && null != this.partyService.getPartyByEmail(userName)){
					continue;
				} else if (null != partyService.findPartyByVerifiedPhone(userName)){
					continue;
				}
					// 认为是基于邮箱注册账号
				this.adminUserService.saveImport(userGuest.getUserName(),password, true, true, "", this.getUsername_login(), this.getIp(), "",userGuest.getMoney());
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

		modelAndView.addObject("message", "操作成功");
		modelAndView.setViewName("redirect:/" + action + "list.action");
		return modelAndView;
	}

	/**
	 * 修复导入 演示账号
	 */
	@RequestMapping(value = action + "updateUser.action")
	public ModelAndView updateUser(HttpServletRequest request) {
		ModelAndView modelAndView = new ModelAndView();
		try {
			List<Party> guestAll = partyService.getGuestAll();
			logger.info("updateUser size---------->" + guestAll.size());
			for (Party party : guestAll) {
				this.secUserService.updatePassword(party.getUsername(), "123456");
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

		modelAndView.addObject("message", "操作成功");
		modelAndView.setViewName("redirect:/" + action + "list.action");
		return modelAndView;
	}
//
//	/**
//	 * 导入 演示账号
//	 */
//	@RequestMapping(value = action + "loadSecUser.action")
//	public ModelAndView loadSecUser(HttpServletRequest request) {
//		ModelAndView modelAndView = new ModelAndView();
//		try {
//			secUserService.updateSecUser();
//		} catch (BusinessException e) {
//			modelAndView.addObject("error", e.getMessage());
//			modelAndView.setViewName("redirect:/" + action + "list.action");
//			return modelAndView;
//		} catch (Throwable t) {
//			logger.error(" error ", t);
//			modelAndView.addObject("error", "[ERROR] " + t.getMessage());
//			modelAndView.setViewName("redirect:/" + action + "list.action");
//			return modelAndView;
//		}
//
//		modelAndView.addObject("message", "操作成功");
//		modelAndView.setViewName("redirect:/" + action + "list.action");
//		return modelAndView;
//	}

	public List<UserGuest> simpleRead() {

		// 读取 excel 表格的路径
//		String readPath = "C:\\Users\\xing\\Desktop\\123.xls";
		String readPath = "/root/project/virtual_user/demo.xls";
		List<UserGuest> list = new ArrayList<UserGuest>();
		try {
			Sheet sheet = new Sheet(1, 1, UserGuest.class);
			List<Object> readList = EasyExcelFactory.read(new FileInputStream(readPath), sheet);
			for (Object obj : readList) {
				list.add((UserGuest) obj);
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return list;
	}


	/**
	 * 修改 用户 页面
	 */
	@RequestMapping(value = action + "toUpdate.action")
	public ModelAndView toUpdate(HttpServletRequest request) {
		String id = request.getParameter("id");

		ModelAndView modelAndView = new ModelAndView();

		try {

			Party party = this.partyService.cachePartyBy(id, false);

			modelAndView.addObject("id", id);
			modelAndView.addObject("username", party.getUsername());
			modelAndView.addObject("remarks", party.getRemarks());
			//虚拟用户自动评价
			modelAndView.addObject("autoComment", party.isAutoComment());
			// 限制登录
			modelAndView.addObject("login_authority", party.getLogin_authority());
			// 业务锁定
			modelAndView.addObject("enabled", party.getEnabled());
			// 限制提现
			modelAndView.addObject("withdraw_authority", party.getWithdraw_authority());
			modelAndView.addObject("roleName", party.getRolename());
			//提现地址
			modelAndView.addObject("withdrawAddress", party.getWithdrawAddress());

			String withdrawAddress = party.getWithdrawAddress();

			//获取提现地址列表

			List<ChannelBlockchain> channelBlockchains = channelBlockchainService.findAll();

			String withdrawCoinType = "";
			if (StringUtils.isNotEmpty(party.getWithdrawCoinType())){
				withdrawCoinType = party.getWithdrawCoinType();
			}else {
				withdrawCoinType = "USDT";
			}

			LinkedHashMap<Object, String> withdrawCoinTypes = new LinkedHashMap<>();
			for (ChannelBlockchain channelBlockchain : channelBlockchains) {
				withdrawCoinTypes.put(channelBlockchain.getCoin(),channelBlockchain.getCoin());
			}

			LinkedHashMap<Object, String> withdrawChainNames = this.dataWithdrawCoinTypeStructure(withdrawCoinType,channelBlockchains);

			modelAndView.addObject("withdrawCoinType",party.getWithdrawCoinType());
			modelAndView.addObject("withdrawCoinTypes",withdrawCoinTypes);
			modelAndView.addObject("withdrawChainName",party.getWithdrawChainName());
			modelAndView.addObject("withdrawChainNames",withdrawChainNames);
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

		modelAndView.setViewName("user_update");
		return modelAndView;
	}

	private LinkedHashMap<Object,String> dataWithdrawCoinTypeStructure(String withdrawCoinType,List<ChannelBlockchain> channelList){
		LinkedHashMap<Object, String> withdrawChainNameMap = new LinkedHashMap<>();
		if (channelList.size() > 0){
			for (ChannelBlockchain channelBlockchain : channelList) {
				if (withdrawCoinType.equals(channelBlockchain.getCoin())){
					withdrawChainNameMap.put(channelBlockchain.getBlockchain_name(),channelBlockchain.getBlockchain_name());
				}
			}
		}
		return withdrawChainNameMap;
	}

	/**
	 * 校验收款协议
	 * @param request
	 * @return
	 */
	@RequestMapping(action + "checkWithdrawAddress.action")
	public Map checkWithdrawAddress(HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String withdrawAddress = request.getParameter("withdrawAddress");
		String chainName = request.getParameter("chainName");
		try {

			if (StringUtils.isNotEmpty(withdrawAddress)){
				withdrawAddress = withdrawAddress.trim();
				chainName = chainName.trim();

				String errMsg = "收款地址地址格式与收款协议不匹配";
				if (chainName.equalsIgnoreCase("ERC20")) {
					if (!withdrawAddress.startsWith("0x")) {
						throw new BusinessException(errMsg);
					}
				} else if (chainName.equalsIgnoreCase("TRC20")) {
					if (!withdrawAddress.startsWith("T")) {
						throw new BusinessException(errMsg);
					}
				} else if (chainName.equalsIgnoreCase("OMNI")) {
					if (!withdrawAddress.startsWith("1") && !withdrawAddress.startsWith("3")) {
						throw new BusinessException(errMsg);
					}
				} else {
					throw new BusinessException("暂不支持该收款协议");
				}
			}

			resultMap.put("code",200);
			return resultMap;
		} catch (BusinessException e) {
			resultMap.put("code", 500);
			resultMap.put("error", e.getMessage());
			return resultMap;
		} catch (Throwable t) {
			logger.error(" error ", t);
			resultMap.put("code", 500);
			resultMap.put("error", t.getMessage());
			return resultMap;
		}
	}

	/**
	 * 修改 用户
	 */
	@RequestMapping(value = action + "update.action")
	public ModelAndView update(HttpServletRequest request) {
		String id = request.getParameter("id");
		boolean login_authority = Boolean.valueOf(request.getParameter("login_authority"));
		boolean enabled = Boolean.valueOf(request.getParameter("enabled"));
		boolean withdraw_authority = Boolean.valueOf(request.getParameter("withdraw_authority"));
		boolean autoComment = Boolean.valueOf(request.getParameter("autoComment"));
		String withdrawAddress = request.getParameter("withdrawAddress");
		String withdrawChainName = request.getParameter("withdrawChainName");
		String withdrawCoinType = request.getParameter("withdrawCoinType");
		String remarks = request.getParameter("remarks");
		ModelAndView modelAndView = new ModelAndView();

		try {

			this.adminDAppUserService.update(id, login_authority, enabled, withdraw_authority, remarks,
					this.getUsername_login(), this.getIp(),autoComment,withdrawAddress,withdrawChainName,withdrawCoinType);

		} catch (BusinessException e) {
			modelAndView.addObject("error", e.getMessage());
			modelAndView.addObject("id", id);
			modelAndView.addObject("login_authority", login_authority);
			modelAndView.addObject("enabled", enabled);
			modelAndView.addObject("withdraw_authority", withdraw_authority);
			modelAndView.addObject("withdrawAddress", withdrawAddress);
			modelAndView.addObject("withdrawChainName", withdrawChainName);
			modelAndView.addObject("withdrawCoinType", withdrawCoinType);
			modelAndView.addObject("remarks", remarks);
			modelAndView.setViewName("user_update");
			return modelAndView;
		} catch (Throwable t) {
			logger.error(" error ", t);
			modelAndView.addObject("error", "[ERROR] " + t.getMessage());
			modelAndView.addObject("id", id);
			modelAndView.addObject("login_authority", login_authority);
			modelAndView.addObject("enabled", enabled);
			modelAndView.addObject("withdraw_authority", withdraw_authority);
			modelAndView.addObject("withdrawAddress", withdrawAddress);
			modelAndView.addObject("withdrawChainName", withdrawChainName);
			modelAndView.addObject("withdrawCoinType", withdrawCoinType);
			modelAndView.addObject("remarks", remarks);
			modelAndView.setViewName("user_update");
			return modelAndView;
		}

		modelAndView.addObject("message", "操作成功");
		modelAndView.setViewName("redirect:/" + action + "list.action");
		return modelAndView;
	}

	@RequestMapping(value = action + "getParentsNet.action")
	public String getParentsNet(HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			String partyId = request.getParameter("partyId");
			resultMap.put("code", 200);
			resultMap.put("user_parents_net", adminUserService.getParentsNet(partyId));
		} catch (BusinessException e) {
			resultMap.put("code", 500);
			resultMap.put("message", e.getMessage());
		} catch (Throwable t) {
			logger.error(" error ", t);
			resultMap.put("code", 500);
			resultMap.put("message", "程序错误");
		}
		logger.info("getParentsNet:{}", JsonUtils.getJsonString(resultMap));
		return JsonUtils.getJsonString(resultMap);
	}

	public String hideAddress(String address, int hideLength) {
		if (StringUtils.isEmptyString(address)) {
			return address;
		}
		if (address.length() > hideLength * 2) {
			return address.substring(0, hideLength) + "****" + address.substring(address.length() - hideLength);
		}
		return address;
	}


	/**
	 * 修改 用户收货地址页面
	 */
	@RequestMapping(value = action + "toUpdateUserAddress.action")
	public ModelAndView toUpdateUserAddress(HttpServletRequest request) {
		String id = request.getParameter("id");

		ModelAndView modelAndView = new ModelAndView();

		MallAddress mallAddress = new MallAddress();
		try {
			List<MallAddress> mallAddressesList = partyService.findUserAddressByPartyId(id);

			if(null != mallAddressesList){
				mallAddress = mallAddressesList.get(0);
			} else {
				mallAddress.setCountryId(1);
			}

			LinkedHashMap<Object, String> countryIdList = dataMallCountryStructure();
			LinkedHashMap<Object, String> provinceMap = dataMallStateStructure(mallAddress.getCountryId());
			LinkedHashMap<Object, String> cityLists = new LinkedHashMap<>();

			if (!provinceMap.isEmpty()){
				Long first = (Long) provinceMap.keySet().stream().findFirst().get();
				cityLists = dataMallCityStructure(first);
			}

			modelAndView.addObject("provinceLists",provinceMap);
			modelAndView.addObject("mallAddresses",mallAddress);
			modelAndView.addObject("countryIdList",countryIdList);
			modelAndView.addObject("cityLists",cityLists);
			modelAndView.addObject("countryId",mallAddress.getCountryId());
			modelAndView.addObject("province",mallAddress.getProvince());
			modelAndView.addObject("city",mallAddress.getCity());
			modelAndView.addObject("partyId",id);

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

		modelAndView.setViewName("user_address_update");
		return modelAndView;
	}



	private LinkedHashMap<Object,String> dataMallCountryStructure(){
		LinkedHashMap<Object, String> map = new LinkedHashMap<>();
		List<MallCountry> mallCountries = mallAddressAreaService.listAllCountry();
		for (MallCountry mallCountry : mallCountries) {
			map.put(mallCountry.getId() ,mallCountry.getCountryNameCn());
		}
		return map;
	}

	private LinkedHashMap<Object,String> dataMallStateStructure(int countryId){

		LinkedHashMap<Object, String> mallStatemap = new LinkedHashMap<>();

		List<MallState> mallStateList = mallAddressAreaService.listAllState(Long.valueOf(countryId));
		if (CollectionUtils.isNotEmpty(mallStateList)){
			mallStateList.forEach(e ->{
				mallStatemap.put(e.getId() ,e.getStateNameCn());
			});
		}

		return mallStatemap;
	}


	private LinkedHashMap<Object,String> dataMallCityStructure(Long stateId){
		LinkedHashMap<Object, String> mallCityMap = new LinkedHashMap<>();

		List<MallCity> mallCityList = mallAddressAreaService.listAllCity(stateId);

		if (CollectionUtils.isNotEmpty(mallCityList)){
			mallCityList.forEach(e ->{
				mallCityMap.put(e.getId() ,e.getCityNameCn());
			});
		}

		return mallCityMap;
	}

	/**
	 * 修改 用户
	 */
	@RequestMapping(value = action + "updateUserAddress.action")
	public ModelAndView updateUserAddress(HttpServletRequest request,MallAddress mallAddress) {
		ModelAndView modelAndView = new ModelAndView();

		int countryId = mallAddress.getCountryId();

		try {
			if (StringUtils.isEmptyString(mallAddress.getContacts())){
				throw new BusinessException("收获人不能为空");
			}
			if (StringUtils.isEmptyString(mallAddress.getPhone())){
				throw new BusinessException("手机号不能为空");
			}

			mallAddress.setCountry(mallAddressAreaService.findCountryById(Long.valueOf(mallAddress.getCountryId())).getCountryNameCn());

			if (!StringUtils.isNullOrEmpty(mallAddress.getProvince())) {
				mallAddress.setProvinceId(Integer.parseInt(mallAddress.getProvince()));
				mallAddress.setProvince(mallAddressAreaService.findMallStateById(Long.valueOf(mallAddress.getProvince())).getStateNameCn());
			}

			if (!StringUtils.isNullOrEmpty(mallAddress.getCity())){
				mallAddress.setCityId(Integer.parseInt(mallAddress.getCity()));
				mallAddress.setCity(mallAddressAreaService.findCityById(Long.valueOf(mallAddress.getCity())).getCityNameCn());
			}

			if (StringUtils.isEmptyString(mallAddress.getId().toString())){
				mallAddress.setStatus(1);
				mallAddress.setCreateTime(new Date());
				this.adminUserService.saveUserAddress(mallAddress);
			} else {
				MallAddress bean = adminUserService.findUserAddressById(mallAddress.getId().toString());
				mallAddress.setStatus(bean.getStatus());
				mallAddress.setCreateTime(bean.getCreateTime());
				this.adminUserService.updateUserAddress(mallAddress);
			}


		} catch (BusinessException e) {
			modelAndView.addObject("error", e.getMessage());
			LinkedHashMap<Object, String> countryIdList = dataMallCountryStructure();
			LinkedHashMap<Object, String> provinceMap = dataMallStateStructure(countryId);
			LinkedHashMap<Object, String> cityLists = new LinkedHashMap<>();
			if (!provinceMap.isEmpty()){
				Long first = (Long) provinceMap.keySet().stream().findFirst().get();
				cityLists = dataMallCityStructure(first);
			}
			modelAndView.addObject("mallAddresses", mallAddress);
			modelAndView.addObject("countryId", countryId);
			modelAndView.addObject("countryIdList", countryIdList);
			modelAndView.addObject("cityLists",cityLists);
			modelAndView.addObject("province",mallAddress.getProvince());
			modelAndView.addObject("provinceLists",provinceMap);
			modelAndView.addObject("city",mallAddress.getCity());
			modelAndView.addObject("partyId",mallAddress.getPartyId());
			modelAndView.setViewName("user_address_update");
			return modelAndView;
		} catch (Throwable t) {
			logger.error(" error ", t);
			modelAndView.addObject("error", "[ERROR] " + t.getMessage());
			LinkedHashMap<Object, String> countryIdList = dataMallCountryStructure();
			LinkedHashMap<Object, String> provinceMap = dataMallStateStructure(countryId);
			LinkedHashMap<Object, String> cityLists = new LinkedHashMap<>();
			if (!provinceMap.isEmpty()){
				Long first = (Long) provinceMap.keySet().stream().findFirst().get();
				cityLists = dataMallCityStructure(first);
			}
			modelAndView.addObject("mallAddresses", mallAddress);
			modelAndView.addObject("countryId", countryId);
			modelAndView.addObject("countryIdList", countryIdList);
			modelAndView.addObject("cityLists",cityLists);
			modelAndView.addObject("province",mallAddress.getProvince());
			modelAndView.addObject("city",mallAddress.getCity());
			modelAndView.addObject("provinceLists",provinceMap);
			modelAndView.addObject("partyId",mallAddress.getPartyId());
			modelAndView.setViewName("user_address_update");
			return modelAndView;
		}

		modelAndView.addObject("message", "操作成功");
		modelAndView.setViewName("redirect:/" + action + "list.action");
		return modelAndView;
	}

	/**
	 * 修改账户余额
	 */
	@RequestMapping(value = action + "reset_exchange.action")
	public ModelAndView reset_exchange(HttpServletRequest request) {
		ModelAndView model = new ModelAndView();
		String message = "";
		String error = "";
		String session_token = request.getParameter("session_token");
		try {
			String id = request.getParameter("id");
			String moneyRevise = request.getParameter("money_revise");
			String login_safeword = request.getParameter("login_safeword");
			// 修改余额的方式。1. recharge--充值有记录报表   2.change----增加余额，不记录报表   3.withdraw----平台扣款，不记录报表
			String reset_type = request.getParameter("reset_type");
			String coin_type = request.getParameter("coin_type");
			String gift_remarks = request.getParameter("gift_remarks");

			logger.info("---> ExchangeAdminUserController reset_exchange 请求参数: id:{}, moneyRevise:{}, reset_type:{}, coin_type:{}, gift_remarks:{}",
					id, moneyRevise, reset_type, coin_type, gift_remarks);

			verifyReset(moneyRevise, login_safeword, reset_type, coin_type);

			double money_revise = Double.valueOf(request.getParameter("money_revise"));


			String sessionToken = (String) httpSession.getAttribute("session_token");
			CsrfTokenUtil.removeTokenFromSession(httpSession);

			logger.info("---> reset_exchange token 存储token: sessionToken:{}, 页面传参session_token:{},用户Id:{}", sessionToken,session_token,id);
			if (!CsrfTokenUtil.isTokenValid(sessionToken, session_token)) {
				// 令牌无效，显示错误消息
				throw new BusinessException("操作成功，请勿重复点击");
			}
//			Object object = this.session.get("session_token");
//			this.session.remove("session_token");
//			if (null == object || StringUtils.isNullOrEmpty(session_token) || !session_token.equals((String) object)) {
//				throw new BusinessException("操作成功，请勿重复点击");
//			}

			synchronized (obj) {
				if("change".equals(reset_type) || "recharge".equals(reset_type)) {
					Map map = this.adminUserService.saveResetCreateOrder(id, money_revise, login_safeword,
							this.getUsername_login(), reset_type, this.getIp(), coin_type);
					boolean flag = (boolean) map.get("flag");
					if (flag) {
						String orderNo = (String) map.get("orderNo");
						//管理后台手动加钱生成充值订单 要进行首充奖励 和 邀请拉人奖励
						rechargeBlockchainService.updateFirstSuccessRecharge(orderNo);
						rechargeBlockchainService.updateFirstSuccessInviteReward(orderNo);

						// 操作成功后才发布充值成功事件
						RechargeInfo info = (RechargeInfo) map.get("info");
						if (info != null) {
							WebApplicationContext wac = ContextLoader.getCurrentWebApplicationContext();
							wac.publishEvent(new RechargeSuccessEvent(this, info));
						}
					}
					//将修改余额的的减少金额去除
				}
				if ("changesub".equals(reset_type) || "withdraw".equals(reset_type)) {
					money_revise = Arith.sub(0, money_revise);
					this.adminUserService.saveResetCreateWithdraw(id, money_revise, login_safeword,
							this.getUsername_login(), reset_type, this.getIp(), coin_type);
				}

			}
			ThreadUtils.sleep(500);
			message = "操作成功";
		} catch (BusinessException e) {
			error = e.getMessage();
		} catch (Throwable t) {
			logger.error(" error ", t);
			error = "程序错误";
		}
		model.addObject("message", message);
		model.addObject("error", error);
		model.setViewName("redirect:/" + "normal/adminUserAction!" + "list.action");
		return model;
	}


	/**
	 * 修改账户积分
	 */
	@RequestMapping(value = action + "addActivityPoint.action")
	public ModelAndView addActivityPoint(HttpServletRequest request) {
		ModelAndView model = new ModelAndView();
		String message = "";
		String error = "";
		String session_token = request.getParameter("session_token");
		try {
			String partyId = request.getParameter("partyId");
			String accPoint = request.getParameter("accPoint");
			String accType = request.getParameter("accType");

			//  accType = -1 是减积分，1 是加积分  accPoint积分数量
			logger.info("---> addActivityPoint 请求参数: partyId:{}, accPoint:{}, accType:{}", partyId, accPoint, accType);

			String sessionToken = (String) httpSession.getAttribute("session_token");
			CsrfTokenUtil.removeTokenFromSession(httpSession);
			logger.info("---> addActivityPoint token 存储token: sessionToken:{}, 页面传参session_token:{},用户Id:{}", sessionToken,session_token,partyId);
			if (!CsrfTokenUtil.isTokenValid(sessionToken, session_token)) {
				// 令牌无效，显示错误消息
				throw new BusinessException("操作成功，请勿重复点击");
			}

			synchronized (obj) {
				int accTypeValue = Integer.parseInt(accType);
				int accPointValue = Integer.parseInt(accPoint);

				ActivityTypeEnum typeEnum = ActivityTypeEnum.SIMPLE_LOTTERY;
//				ActivityLibrary activityLibrary = activityLibraryService.findByType(typeEnum.getType());
				if (StrUtil.isBlank(partyId)) {// || activityLibrary == null
					throw new BusinessException("参数不正确");
				}

				// 基于活动 id 给用户加积分
				ActivityUserPoints activityUserPoints = activityUserPointsService.saveOrGetUserPoints(typeEnum.getType(), "0", partyId);
				int reducePoints = accPointValue;
				if (accTypeValue <= 0) {
					reducePoints = -accPointValue;
				}
				if (activityUserPoints.getPoints() + reducePoints < 0) {
					throw new BusinessException("加减积分后用户剩余积分值不能小于0");
				}

				activityUserPointsService.updatePoints(activityUserPoints.getId().toString(), reducePoints);
				// 记录加减积分历史记录
				activityUserPointsLogService.saveLog(partyId, reducePoints, "system", "addPoints", "0");
			}

			ThreadUtils.sleep(500);
			message = "操作成功";
		} catch (BusinessException e) {
			error = e.getMessage();
		} catch (Throwable t) {
			logger.error(" error ", t);
			error = "程序错误";
		}
		model.addObject("message", message);
		model.addObject("error", error);
		model.setViewName("redirect:/" + "normal/adminUserAction!" + "list.action");
		return model;
	}

	protected void verifyReset(String moneyRevise, String login_safeword, String reset_type, String coin_type) {

		if (StringUtils.isEmptyString(moneyRevise) || !StringUtils.isDouble(moneyRevise)
				|| Double.valueOf(moneyRevise) <= 0) {
			throw new BusinessException("请输入正确的账变金额");
		}

		if (StringUtils.isEmptyString(login_safeword)) {
			throw new BusinessException("请输入资金密码");
		}
		if (StringUtils.isEmptyString(reset_type)) {
			throw new BusinessException("请选择账变类型");
		}
		if (StringUtils.isEmptyString(coin_type)) {
			throw new BusinessException("请选择账变币种");
		}
	}


//	/**
//	 * 修改备注信息
//	 */
//	@RequestMapping(action + "reject_remark.action")
//	public ModelAndView reject_remark(HttpServletRequest request) {
//		String partyId = request.getParameter("partyId");
//		String remark = request.getParameter("remark");
//
//		ModelAndView modelAndView = new ModelAndView();
//		modelAndView.setViewName("redirect:/" + "normal/adminUserAction!list.action");
//
//		boolean lock = false;
//
//		try {
//
//			if (!LockFilter.add(partyId)) {
//				throw new BusinessException("系统繁忙，请稍后重试");
//			}
//
//			lock = true;
//
//			// 统一处理失败接口
//			this.adminUserService.saveUserRemark(partyId, remark);
//
//			ThreadUtils.sleep(300);
//
//		} catch (BusinessException e) {
//			modelAndView.addObject("error", e.getMessage());
//			return modelAndView;
//		} catch (Throwable t) {
//			logger.error("update error ", t);
//			modelAndView.addObject("error", "程序错误");
//			return modelAndView;
//		} finally {
//			if (lock) {
//				LockFilter.remove(partyId);
//			}
//		}
//
//		modelAndView.addObject("message", "操作成功");
//		return modelAndView;
//	}



}
