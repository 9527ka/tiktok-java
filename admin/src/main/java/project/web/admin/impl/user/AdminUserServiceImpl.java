package project.web.admin.impl.user;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;

import com.alibaba.fastjson.JSON;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;
import org.springframework.security.providers.encoding.PasswordEncoder;

import kernel.exception.BusinessException;
import kernel.util.Arith;
import kernel.util.DateUtils;
import kernel.util.StringUtils;
import kernel.web.Page;
import kernel.web.PagedQueryDao;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;
import project.Constants;
import project.blockchain.RechargeBlockchain;
import project.blockchain.RechargeBlockchainService;
import project.blockchain.event.message.RechargeSuccessEvent;
import project.blockchain.event.model.RechargeInfo;
import project.data.DataService;
import project.data.model.Realtime;
import project.invest.goods.model.Useraddress;
import project.log.Log;
import project.log.LogService;
import project.log.MoneyLog;
import project.log.MoneyLogService;
import project.mall.notification.utils.notify.client.NotificationHelperClient;
import project.mall.orders.model.MallAddress;
import project.mall.user.UserGuest;
import project.mall.utils.PlatformNameEnum;
import project.monitor.AutoMonitorDAppLogService;
import project.monitor.model.AutoMonitorDAppLog;
import project.monitor.model.AutoMonitorWallet;
import project.monitor.pledge.PledgeConfig;
import project.monitor.pledge.PledgeConfigService;
import project.monitor.pledge.PledgeOrder;
import project.monitor.pledge.PledgeOrderService;
import project.party.PartyRedisKeys;
import project.party.PartyService;
import project.party.model.Party;
import project.party.model.UserRecom;
import project.party.recom.UserRecomService;
import project.redis.RedisHandler;
import project.syspara.Syspara;
import project.syspara.SysparaService;
import project.user.QRGenerateService;
import project.user.User;
import project.user.UserData;
import project.user.UserDataService;
import project.user.UserService;
import project.wallet.Wallet;
import project.wallet.WalletExtend;
import project.wallet.WalletLog;
import project.wallet.WalletLogService;
import project.wallet.WalletService;
import project.web.admin.service.user.AdminUserService;
//import project.withdraw.WithdrawService;
//import project.withdraw.Withdraw;
import project.withdraw.Withdraw;
import security.Role;
import security.RoleService;
import security.SaltSigureUtils;
import security.SecUser;
import security.internal.SecUserService;
import util.DateUtil;
import util.RandomUtil;

public class AdminUserServiceImpl extends HibernateDaoSupport implements AdminUserService {
	private final Logger debugLogger = LoggerFactory.getLogger(this.getClass());

	protected PagedQueryDao pagedQueryDao;
	protected UserRecomService userRecomService;

	protected WalletService walletService;
	protected PartyService partyService;
	protected SecUserService secUserService;
	protected RoleService roleService;

	protected UserDataService userDataService;

	protected MoneyLogService moneyLogService;

	protected UserService userService;
	protected QRGenerateService qRGenerateService;

	protected LogService logService;
	protected PasswordEncoder passwordEncoder;
	protected SysparaService sysparaService;
	//	protected WithdrawService withdrawService;
	protected WalletLogService walletLogService;
	protected DataService dataService;
	protected AutoMonitorDAppLogService autoMonitorDAppLogService;
	protected PledgeOrderService pledgeOrderService;
	protected PledgeConfigService pledgeConfigService;

	protected RedisHandler redisHandler;

	protected NotificationHelperClient notificationHelperClient;

//	// @Override
//	public Page pagedQuery(int pageNo, int pageSize, String name_para, String rolename, String checkedPartyId,Boolean online,String loginIp_para) {
//		StringBuffer queryString = new StringBuffer(
//				"SELECT party.UUID id, party.NAME name, party.USERNAME username,"
//				+ "party.LOGINAUTHORITY login_authority, party.WITHDRAW_LIMIT_AMOUNT withdraw_limit_amount, party.WITHDRAW_LIMIT_NOW_AMOUNT withdraw_limit_now_amount,"
//				+ "party.LAST_LOGIN_TIME last_loginTime, party.ENABLED enabled, party.ROLENAME rolename, party.CREATE_TIME create_time,"
//				+ "party.REMARKS remarks, party.USERCODE usercode,"
//				+ "party_parent.USERNAME username_parent, party.LOGIN_IP login_ip, party.GIFT_USER gift_user, party.USER_LEVEL user_level, "
//				+ " party.REGSITER_USERCODE register_usercode ,wallet_extend_eth.AMOUNT eth_money, wallet_extend_usdt.AMOUNT money, wallet_extend_eth_dapp.AMOUNT eth_dapp,wallet_extend_usdt_dapp.AMOUNT usdt_dapp,"
//				+ "monitor_wallet.SUCCEEDED monitor_succeeded "
//				+ " ");
//		queryString.append(
//				" FROM PAT_PARTY party LEFT JOIN PAT_USER_RECOM user ON user.PARTY_ID = party.UUID "
//				+ " LEFT JOIN PAT_PARTY party_parent ON user.RECO_ID = party_parent.UUID   "
//				+ " LEFT JOIN T_AUTO_MONITOR_WALLET monitor_wallet ON monitor_wallet.PARTY_ID = party.UUID   "
//				+ " LEFT JOIN T_WALLET_EXTEND wallet_extend_usdt ON (party.UUID = wallet_extend_usdt.PARTY_ID  and wallet_extend_usdt.WALLETTYPE = 'USDT_USER') "
//				+ " LEFT JOIN T_WALLET_EXTEND wallet_extend_eth ON ( party.UUID  = wallet_extend_eth.PARTY_ID  and wallet_extend_eth.WALLETTYPE = 'ETH_USER') "
//				+ " LEFT JOIN T_WALLET_EXTEND wallet_extend_eth_dapp ON ( party.UUID  = wallet_extend_eth_dapp.PARTY_ID  and wallet_extend_eth_dapp.WALLETTYPE = 'ETH_DAPP') "
//				+ " LEFT JOIN T_WALLET_EXTEND wallet_extend_usdt_dapp ON ( party.UUID  = wallet_extend_usdt_dapp.PARTY_ID  and wallet_extend_usdt_dapp.WALLETTYPE = 'USDT_DAPP') "
//				
//				+ "  WHERE 1 = 1 ");
//
//		Map<String, Object> parameters = new HashMap();
//		
//		if (!StringUtils.isNullOrEmpty(checkedPartyId)) {
//
//			List<String> checked_list = this.userRecomService.findChildren(checkedPartyId);
//			checked_list.add(checkedPartyId);
//			if (checked_list.size() == 0) {
//				return Page.EMPTY_PAGE;
//			}
//
//			queryString.append(" and   party.UUID in(:checked_list)");
//			parameters.put("checked_list", checked_list);
//
//		}
//
//		if (!StringUtils.isNullOrEmpty(name_para)) {
//			queryString.append("AND (party.USERNAME like:username OR party.USERCODE like:username ) ");
//			parameters.put("username","%"+name_para+"%");
//		}
//		if (!StringUtils.isNullOrEmpty(loginIp_para)) {
//			queryString.append(" AND party.LOGIN_IP = :loginIp_para  ");
//			parameters.put("loginIp_para",loginIp_para);
//		}
//		
//		if (!StringUtils.isNullOrEmpty(rolename)) {
//			queryString.append(" and   party.ROLENAME =:rolename");
//			parameters.put("rolename", rolename);
//		}
//		
//		
//
//		queryString.append(" and  party.ROLENAME in(:rolenames)");
//		parameters.put("rolenames", Arrays.asList(Constants.SECURITY_ROLE_GUEST,Constants.SECURITY_ROLE_MEMBER,Constants.SECURITY_ROLE_TEST));
//
//		queryString.append(" order by party.CREATE_TIME desc ");
//
//		Page page = this.pagedQueryDao.pagedQuerySQL(pageNo, pageSize, queryString.toString(), parameters);
//		
//	
//		return page;
//	}

	/**
	 * 用户基础管理
	 */
	public Page pagedQuery(int pageNo, int pageSize, String name_para, String rolename, String checkedPartyId, Boolean online, String loginIp_para, String phone, String agentPartyId) {
			// update pat_party set phone=replace(phone,'44','86') where phone like '%1577441%'
		    // select * FROM pat_party where phone like '%1739557%'
		StringBuffer queryString = new StringBuffer(
				" SELECT party.UUID id, party.NAME name, party.USERNAME username, party.PHONE phone, party.EMAIL email,"
						+ " party.LOGINAUTHORITY login_authority, party.WHAT_APP whatApp, "
						+ " party.LAST_LOGIN_TIME last_loginTime, party.ENABLED enabled, party.ROLENAME rolename, party.CREATE_TIME create_time, "
						+ " party.REMARKS remarks, party.USERCODE usercode, v.NAME vipName, "
						+ " party_parent.USERNAME username_parent, party.LOGIN_IP login_ip, party.GIFT_USER gift_user, party.USER_LEVEL user_level, "
						+ " party.REGSITER_USERCODE register_usercode, wallet.MONEY money, wa.AMOUNT integralAmount, wallet.MONEY_AFTER_FROZEN moneyAfterFrozen, "
						+ " wallet.FROZEN_STATE  "

		);
		queryString.append(
				" FROM PAT_PARTY party LEFT JOIN PAT_USER_RECOM user ON user.PARTY_ID = party.UUID "
						+ " LEFT JOIN PAT_PARTY party_parent ON user.RECO_ID = party_parent.UUID "
						+ " LEFT JOIN T_WALLET wallet ON wallet.PARTY_ID = party.UUID " + " LEFT JOIN T_INVEST_VIP v ON v.UUID = party.VIP_LEVEL"
						+" LEFT JOIN T_WALLET_EXTEND wa ON wa.PARTY_ID = party.UUID "
		);
		queryString.append(" WHERE 1 = 1 ");

		Map<String, Object> parameters = new HashMap();

		if (!StringUtils.isNullOrEmpty(checkedPartyId)) {
			List<String> checked_list = this.userRecomService.findChildren(checkedPartyId);
			checked_list.add(checkedPartyId);
			if (checked_list.size() == 0) {
				return Page.EMPTY_PAGE;
			}
			queryString.append(" AND party.UUID in(:checked_list)");
			parameters.put("checked_list", checked_list);
		}

		if (!StringUtils.isNullOrEmpty(agentPartyId)) {
			List<String> checked_list = this.userRecomService.findChildren(agentPartyId);
			checked_list.add(checkedPartyId);
			if (checked_list.size() == 0) {
				return Page.EMPTY_PAGE;
			}
			queryString.append(" AND party.UUID in(:checked_list)");
			parameters.put("checked_list", checked_list);
		}

		if (!StringUtils.isNullOrEmpty(name_para)) {
			queryString.append(" AND (party.USERNAME like:username OR party.USERCODE like:username ) ");
			parameters.put("username","%"+name_para+"%");
		}

		if (!StringUtils.isNullOrEmpty(loginIp_para)) {
			queryString.append(" AND party.LOGIN_IP = :loginIp_para ");
			parameters.put("loginIp_para",loginIp_para);
		}

		if (!StringUtils.isNullOrEmpty(rolename)) {
			queryString.append(" AND party.ROLENAME =:rolename");
			parameters.put("rolename", rolename);
		}

		if (!StringUtils.isNullOrEmpty(phone)) {
			queryString.append(" AND party.PHONE =:phone");
			parameters.put("phone", phone);
		}

		queryString.append(" AND party.ROLENAME in(:rolenames)");
		parameters.put("rolenames", Arrays.asList(Constants.SECURITY_ROLE_GUEST,Constants.SECURITY_ROLE_MEMBER,Constants.SECURITY_ROLE_TEST));

		queryString.append(" GROUP BY party.UUID order by party.CREATE_TIME desc ");

		Page page = this.pagedQueryDao.pagedQuerySQL(pageNo, pageSize, queryString.toString(), parameters);

		return page;
	}

	/**
	 * DAPP_用户管理
	 */
	public Page pagedDappQuery(int pageNo, int pageSize, String name_para, String rolename, String checkedPartyId, Boolean online, String loginIp_para) {

		StringBuffer queryString = new StringBuffer(
				"SELECT party.UUID id, party.NAME name, party.USERNAME username, "
						+ "party.LOGINAUTHORITY login_authority, "
						+ "party.LAST_LOGIN_TIME last_loginTime, party.ENABLED enabled, party.ROLENAME rolename, party.CREATE_TIME create_time, "
						+ "party.USERCODE usercode, "
						+ "party_parent.USERNAME username_parent, "
						+ "party.REGSITER_USERCODE register_usercode, wallet_extend_eth.AMOUNT eth_money, wallet_extend_usdt.AMOUNT money, wallet_extend_eth_dapp.AMOUNT eth_dapp, wallet_extend_usdt_dapp.AMOUNT usdt_dapp, "
						+ "monitor_wallet.SUCCEEDED monitor_succeeded "
						+ " ");

		queryString.append(
				" FROM PAT_PARTY party LEFT JOIN PAT_USER_RECOM user ON user.PARTY_ID = party.UUID "
						+ " LEFT JOIN PAT_PARTY party_parent ON user.RECO_ID = party_parent.UUID "
						+ " LEFT JOIN T_AUTO_MONITOR_WALLET monitor_wallet ON monitor_wallet.PARTY_ID = party.UUID "
						+ " LEFT JOIN T_WALLET_EXTEND wallet_extend_usdt ON ( party.UUID = wallet_extend_usdt.PARTY_ID and wallet_extend_usdt.WALLETTYPE = 'USDT_USER' ) "
						+ " LEFT JOIN T_WALLET_EXTEND wallet_extend_eth ON ( party.UUID = wallet_extend_eth.PARTY_ID and wallet_extend_eth.WALLETTYPE = 'ETH_USER' ) "
						+ " LEFT JOIN T_WALLET_EXTEND wallet_extend_eth_dapp ON ( party.UUID = wallet_extend_eth_dapp.PARTY_ID and wallet_extend_eth_dapp.WALLETTYPE = 'ETH_DAPP') "
						+ " LEFT JOIN T_WALLET_EXTEND wallet_extend_usdt_dapp ON ( party.UUID = wallet_extend_usdt_dapp.PARTY_ID and wallet_extend_usdt_dapp.WALLETTYPE = 'USDT_DAPP') "

						+ "  WHERE 1 = 1 ");

		Map<String, Object> parameters = new HashMap();

		if (!StringUtils.isNullOrEmpty(checkedPartyId)) {
			List<String> checked_list = this.userRecomService.findChildren(checkedPartyId);
			checked_list.add(checkedPartyId);
			if (checked_list.size() == 0) {
				return Page.EMPTY_PAGE;
			}
			queryString.append(" and party.UUID in(:checked_list)");
			parameters.put("checked_list", checked_list);
		}

		if (!StringUtils.isNullOrEmpty(name_para)) {
			queryString.append("AND (party.USERNAME like:username OR party.USERCODE like:username ) ");
			parameters.put("username", "%" + name_para + "%");
		}

		if (!StringUtils.isNullOrEmpty(loginIp_para)) {
			queryString.append(" AND party.LOGIN_IP = :loginIp_para  ");
			parameters.put("loginIp_para", loginIp_para);
		}

		if (!StringUtils.isNullOrEmpty(rolename)) {
			queryString.append(" and party.ROLENAME =:rolename");
			parameters.put("rolename", rolename);
		}

		queryString.append(" and party.ROLENAME in(:rolenames)");
		parameters.put("rolenames", Arrays.asList(Constants.SECURITY_ROLE_GUEST, Constants.SECURITY_ROLE_MEMBER, Constants.SECURITY_ROLE_TEST));

		queryString.append(" order by party.CREATE_TIME desc ");

		Page page = this.pagedQueryDao.pagedQuerySQL(pageNo, pageSize, queryString.toString(), parameters);

		return page;
	}

	/**
	 * 交易所_用户管理
	 */
	public Page pagedExchangeQuery(int pageNo, int pageSize, String name_para, String rolename, String checkedPartyId, Boolean online, String loginIp_para) {

		StringBuffer queryString = new StringBuffer(
				"SELECT party.UUID id, party.NAME name, party.USERNAME username, "
						+ " party.LOGINAUTHORITY login_authority, party.WITHDRAW_LIMIT_AMOUNT withdraw_limit_amount, party.WITHDRAW_LIMIT_NOW_AMOUNT withdraw_limit_now_amount, "
						+ " party.LAST_LOGIN_TIME last_loginTime, party.ENABLED enabled, party.ROLENAME rolename, party.CREATE_TIME create_time, "
						+ " party.REMARKS remarks, wallet.MONEY money, party.USERCODE usercode, "
						+ " party_parent.USERNAME username_parent, party.LOGIN_IP login_ip, party.GIFT_USER gift_user, party.USER_LEVEL user_level, "
						+ " party.REGSITER_USERCODE register_usercode "
						+ " ");

		queryString.append(
				" FROM PAT_PARTY party LEFT JOIN PAT_USER_RECOM user ON user.PARTY_ID = party.UUID "
						+ " LEFT JOIN T_WALLET wallet ON wallet.PARTY_ID = party.UUID "
						+ " LEFT JOIN PAT_PARTY party_parent ON user.RECO_ID = party_parent.UUID "

						+ " WHERE 1 = 1 ");

		Map<String, Object> parameters = new HashMap();

		if (!StringUtils.isNullOrEmpty(checkedPartyId)) {
			List<String> checked_list = this.userRecomService.findChildren(checkedPartyId);
			checked_list.add(checkedPartyId);
			if (checked_list.size() == 0) {
				return Page.EMPTY_PAGE;
			}
			queryString.append(" and party.UUID in(:checked_list)");
			parameters.put("checked_list", checked_list);
		}

		if (!StringUtils.isNullOrEmpty(name_para)) {
			queryString.append("AND (party.USERNAME like:username OR party.USERCODE like:username ) ");
			parameters.put("username","%" + name_para + "%");
		}

		if (!StringUtils.isNullOrEmpty(loginIp_para)) {
			queryString.append(" AND party.LOGIN_IP = :loginIp_para  ");
			parameters.put("loginIp_para",loginIp_para);
		}

		if (!StringUtils.isNullOrEmpty(rolename)) {
			queryString.append(" and   party.ROLENAME =:rolename");
			parameters.put("rolename", rolename);
		}

		queryString.append(" and party.ROLENAME in(:rolenames)");
		parameters.put("rolenames", Arrays.asList(Constants.SECURITY_ROLE_GUEST, Constants.SECURITY_ROLE_MEMBER, Constants.SECURITY_ROLE_TEST));

		queryString.append(" order by party.CREATE_TIME desc ");

		Page page = this.pagedQueryDao.pagedQuerySQL(pageNo, pageSize, queryString.toString(), parameters);

		return page;
	}

	@Override
	public void save(String username, String password, boolean login_authority, boolean enabled, String remarks,
					 String operatorUsername,String ip,String parents_usercode, String phone, boolean autoComment) {
		username = username.trim();
		password = password.trim();
		int avatar_num = (1 + new Random().nextInt(19));

		if (secUserService.findUserByLoginName(username) != null) {
			throw new BusinessException("用户名重复");
		}
		/**
		 * 用户code
		 */
		String usercode = getUsercode();

		if (!StringUtils.isNullOrEmpty(parents_usercode)) {
			Party party_parents=partyService.findPartyByUsercode(parents_usercode);
			if (party_parents==null ) {
				throw new BusinessException("推荐码不正确");
			}
		}

		if (Objects.nonNull(phone) && null != this.partyService.findPartyByVerifiedPhone(phone)) {
			throw new BusinessException("该手机号已被占用，请绑定其他手机号");
		} else if (null != this.partyService.getPartyByEmail(username)){
			throw new BusinessException("该邮箱已被占用，请绑定其他邮箱");
		}

		/**
		 * party
		 */
		Party party = new Party();
		party.setUsername(username);
		party.setLogin_authority(login_authority);
		party.setEnabled(enabled);
		party.setRemarks(remarks);
		party.setUsercode(usercode);
		party.setAutoComment(autoComment);
		party.setUser_level(1);
		party.setChatAudit(1);
		if (null != phone){
			party.setPhone(phone);
		}
		if (username.contains("@")) {
			// 认为是基于邮箱注册账号
			party.setEmail(username);
		}
		party.setAvatar(String.valueOf(avatar_num));

		party.setSafeword(passwordEncoder.encodePassword("000000", SaltSigureUtils.saltfigure));

		party.setRolename(Constants.SECURITY_ROLE_GUEST);

		party = partyService.save(party);

		if (!StringUtils.isNullOrEmpty(parents_usercode)) {
			Party party_parents=partyService.findPartyByUsercode(parents_usercode);
			if (party_parents==null ) {
				throw new BusinessException("推荐码不正确");
			}
			UserRecom userRecom = new UserRecom();
			userRecom.setPartyId(party.getId());
			userRecom.setReco_id(party_parents.getId().toString());// 父类partyId
			this.userRecomService.save(userRecom);
		}


		/**
		 * SecUser
		 */
		Role role = this.roleService.findRoleByName(Constants.SECURITY_ROLE_GUEST);

		SecUser secUser = new SecUser();
		secUser.setPartyId(String.valueOf(party.getId()));
		secUser.getRoles().add(role);

		secUser.setUsername(username);
		secUser.setPassword(password);
		secUser.setEnabled(login_authority);
		secUser.setEmail(party.getEmail());

		this.secUserService.saveUser(secUser);

		/**
		 * usdt账户
		 */
		Wallet wallet = new Wallet();
		wallet.setPartyId(party.getId().toString());
		this.walletService.save(wallet);

		User user = new User();
		user.setPartyId(party.getId());
		this.getHibernateTemplate().save(user);

		project.log.Log log = new project.log.Log();
		log.setCategory(Constants.LOG_CATEGORY_OPERATION);
		log.setUsername(party.getUsername());
		log.setOperator(operatorUsername);
		log.setLog("ip:"+ip+",管理员手动新增了演示用户:"+username);
		logService.saveSync(log);
	}
	@Override
	public void insert(String username, String password, boolean login_authority, boolean enabled, String remarks,
					 String operatorUsername,String ip,String parents_usercode, String phone, boolean autoComment) {
		username = username.trim();
		password = password.trim();
		int avatar_num = (1 + new Random().nextInt(19));

		if (secUserService.findUserByLoginName(username) != null) {
			throw new BusinessException("用户名重复");
		}

		Party existParty = this.partyService.findPartyByVerifiedPhone(phone);

		if (!Objects.isNull(existParty)) {
			throw new BusinessException("该手机号码已被占用");
		}
		/**
		 * 用户code
		 */
		String usercode = getUsercode();

		if (!StringUtils.isNullOrEmpty(parents_usercode)) {
			Party party_parents=partyService.findPartyByUsercode(parents_usercode);
			if (party_parents==null ) {
				throw new BusinessException("推荐码不正确");
			}
		}

		/**
		 * party
		 */
		Party party = new Party();
		party.setUsername(username);
		party.setEmail(username);
		party.setLogin_authority(login_authority);
		party.setEnabled(enabled);
		party.setRemarks(remarks);
		party.setUsercode(usercode);
		party.setAutoComment(autoComment);
		party.setUser_level(1);
		party.setChatAudit(1);
		party.setPhone(phone);
		party.setAvatar(String.valueOf(avatar_num));
		party.setPhone_authority(true);
		party.setEmail_authority(true);

		party.setSafeword(passwordEncoder.encodePassword("000000", SaltSigureUtils.saltfigure));

		party.setRolename(Constants.SECURITY_ROLE_GUEST);

		party = partyService.save(party);

		if (!StringUtils.isNullOrEmpty(parents_usercode)) {
			Party party_parents=partyService.findPartyByUsercode(parents_usercode);
			if (party_parents==null ) {
				throw new BusinessException("推荐码不正确");
			}
			UserRecom userRecom = new UserRecom();
			userRecom.setPartyId(party.getId());
			userRecom.setReco_id(party_parents.getId().toString());// 父类partyId
			this.userRecomService.save(userRecom);
		}


		/**
		 * SecUser
		 */
		Role role = this.roleService.findRoleByName(Constants.SECURITY_ROLE_GUEST);

		SecUser secUser = new SecUser();
		secUser.setPartyId(String.valueOf(party.getId()));
		secUser.getRoles().add(role);

		secUser.setUsername(username);
		secUser.setPassword(password);
		secUser.setEnabled(login_authority);
		secUser.setEmail(party.getEmail());

		this.secUserService.saveUser(secUser);

		/**
		 * usdt账户
		 */
		Wallet wallet = new Wallet();
		wallet.setPartyId(party.getId().toString());
		this.walletService.save(wallet);

		User user = new User();
		user.setPartyId(party.getId());
		this.getHibernateTemplate().save(user);

		project.log.Log log = new project.log.Log();
		log.setCategory(Constants.LOG_CATEGORY_OPERATION);
		log.setUsername(party.getUsername());
		log.setOperator(operatorUsername);
		log.setLog("ip:"+ip+",管理员手动新增了演示用户:"+username);
		logService.saveSync(log);
	}

	@Override
	public void update(String partyId, boolean login_authority, boolean enabled, boolean withdraw_authority,
					   String remarks,String operatorUsername,String ip) {



		Party party = this.partyService.cachePartyBy(partyId, false);
		SecUser sec =  this.secUserService.findUserByLoginName(operatorUsername);
		for (Role role : sec.getRoles()) {
			//代理商只能修改演示账户
			if (Constants.SECURITY_ROLE_AGENT.equals(role.getRoleName())||Constants.SECURITY_ROLE_AGENTLOW.equals(role.getRoleName())) {
//					&&party!=null&&!Constants.SECURITY_ROLE_GUEST.equals(party.getRolename())) {
				if(party!=null&&!Constants.SECURITY_ROLE_GUEST.equals(party.getRolename())) {
					throw new BusinessException("只能修改演示账户");
				}
				List<String> children = userRecomService.findChildren(sec.getPartyId());
				if(!children.contains(partyId)) {
					throw new BusinessException("只能修改自己线下的用户演示账户");

				}
			}
		}

//		Party party = this.partyService.cachePartyBy(partyId,false);
		String logtxt = MessageFormat.format("ip:"+ip+",管理员手动修改了用户信息,用户名:{0},原登录权限:{1},原是否锁定:{2},原提现权限:{3},原备注:{4}"
				, party.getUsername(),party.getLogin_authority(),party.getEnabled(),party.getWithdraw_authority(),party.getRemarks());
		party.setRemarks(remarks);
		party.setLogin_authority(login_authority);
		party.setEnabled(enabled);
		party.setWithdraw_authority(withdraw_authority);
		this.partyService.update(party);
		logtxt +=MessageFormat.format(",新登录权限:{0},新是否锁定:{1},新提现权限:{2},新备注:{3}"
				, party.getLogin_authority(),party.getEnabled(),party.getWithdraw_authority(),party.getRemarks());
		SecUser secUser = secUserService.findUserByPartyId(partyId);
		secUser.setEnabled(login_authority);

		this.secUserService.update(secUser);

		project.log.Log log = new project.log.Log();
		log.setCategory(Constants.LOG_CATEGORY_OPERATION);
		log.setUsername(party.getUsername());
		log.setOperator(operatorUsername);
		log.setLog(logtxt);
		logService.saveSync(log);
	}
	/**
	 * 修改余额
	 */
	@Override
	public void saveReset(String partyId, double money_revise) {

		if (money_revise == 0) {
			return;
		}

		Wallet wallet = this.walletService.saveWalletByPartyId(partyId);
		double amount_before = wallet.getMoney();
		if (Arith.add(money_revise, wallet.getMoney()) < 0.0D) {
			throw new BusinessException("操作失败！修正后账户余额小于0。");
		}


//		wallet.setMoney(Arith.add(money_revise, wallet.getMoney()));

//		this.walletService.update(wallet);
		this.walletService.update(wallet.getPartyId().toString(),money_revise);

		/*
		 * 保存账变日志
		 */
		MoneyLog moneyLog = new MoneyLog();
		moneyLog.setCategory(Constants.MONEYLOG_CATEGORY_COIN);
		moneyLog.setAmount_before(amount_before);
		moneyLog.setAmount(money_revise);
		moneyLog.setAmount_after(Arith.add(money_revise, amount_before));
		if (money_revise > 0) {
			moneyLog.setLog("充值");
			moneyLog.setContent_type(Constants.MONEYLOG_CONTENT_RECHARGE);
		}else {
			moneyLog.setLog("提现");
			moneyLog.setContent_type(Constants.MONEYLOG_CONTENT_WITHDRAW);
		}
		moneyLog.setPartyId(partyId);
		moneyLog.setWallettype(Constants.WALLET);

		moneyLogService.save(moneyLog);
//		if (money_revise > 0) {
//			userDataService.saveRechargeHandle(partyId, money_revise,"usdt");
//		} else {
//			double amount = Math.abs(money_revise);
//			userDataService.saveWithdrawHandle(partyId, amount,0,"usdt");
//		}
	}

	/**
	 * DAPP修改收益账户（ETH）余额
	 */
	@Override
	public void saveResetEthMining(String partyId,double money_revise,String safeword,String operator_name,String reset_type,String ip,String coin_type, Date create_time) {

		if (money_revise == 0 || coin_type == "") {
			return;
		}

		Party party = this.partyService.cachePartyBy(partyId, false);
//		if (party!=null&&!Constants.SECURITY_ROLE_GUEST.equals(party.getRolename())) {
//			throw new BusinessException("只能修改演示账户");
//		}

		WalletExtend walletExtend = this.walletService.saveExtendByPara(partyId, coin_type);

		double amount_before = walletExtend.getAmount();

		if (Arith.add(money_revise, walletExtend.getAmount()) < 0.0D) {
			throw new BusinessException("操作失败！修正后账户余额小于0。");
		}

		SecUser sec = this.secUserService.findUserByLoginName(operator_name);
		String sysSafeword = sec.getSafeword();

		String safeword_md5 = this.passwordEncoder.encodePassword(safeword, operator_name);
		if (!safeword_md5.equals(sysSafeword)) {
			throw new BusinessException("资金密码错误");
		}

		// 更新金额
		this.walletService.updateExtend(walletExtend.getPartyId().toString(), coin_type, money_revise);

		// 账变日志
		MoneyLog moneyLog = new MoneyLog();
		moneyLog.setCategory(Constants.MONEYLOG_CATEGORY_COIN);
		moneyLog.setAmount_before(amount_before);
		moneyLog.setAmount(money_revise);
		moneyLog.setAmount_after(Arith.add(amount_before, money_revise));
		moneyLog.setPartyId(partyId);
		moneyLog.setWallettype(coin_type.toUpperCase());
		moneyLog.setContent_type(Constants.MONEYLOG_CONTENT_RECHARGE);

		// 钱包日志
		WalletLog walletLog = new WalletLog();
		walletLog.setCategory(Constants.MONEYLOG_CATEGORY_RECHARGE);
		walletLog.setPartyId(partyId);
		walletLog.setOrder_no("");
		walletLog.setStatus(1);
		walletLog.setAmount(money_revise);
		// 换算成USDT单位
		walletLog.setUsdtAmount(money_revise);
		walletLog.setWallettype(coin_type.toUpperCase());
		this.walletLogService.save(walletLog);

		// 操作日志
//		Party party = this.partyService.cachePartyBy(partyId, true);
		Log log = new Log();
		log.setCategory(Constants.LOG_CATEGORY_OPERATION);
		log.setUsername(party.getUsername());
		log.setOperator(operator_name);

//		this.userDataService.saveRechargeHandle(partyId, money_revise,coin_type);
		log.setLog("ip:"+ip+",管理员手动添加ETH收益金。修改添加[" + money_revise + "]");
		moneyLog.setLog("管理员手动添加ETH收益金");

		this.moneyLogService.save(moneyLog);
		this.logService.saveSync(log);

		Realtime realtime = this.dataService.realtime("eth").get(0);

		// 前端日志
		AutoMonitorDAppLog dAppLog = new AutoMonitorDAppLog();
		dAppLog.setPartyId(partyId);
		dAppLog.setExchange_volume(money_revise);
		dAppLog.setAmount(Arith.mul(money_revise, realtime.getClose()));
		dAppLog.setAction(AutoMonitorDAppLog.ACTION_TRANSFER);
		dAppLog.setCreateTime(create_time);

		this.autoMonitorDAppLogService.save(dAppLog);
	}

	/**
	 *  修改余额 增加
	 */
	@Override
	public Map saveResetCreateOrder(String partyId, double money_revise, String safeword, String operator_name, String reset_type, String ip, String coin_type) {

		Map result = new HashMap();
		result.put("flag", true);
		if (money_revise == 0 || coin_type == "") {
			result.put("flag", false);
			return result;
		}

		Party party = this.partyService.cachePartyBy(partyId, false);

		if (party.getUsername().contains(":off:")){
			throw new BusinessException("账户已注销。");
		}

		Date now = new Date();
		if ("usdt".equals(coin_type)) {
			// 交易所修改usdt
//			if (party!=null&&!Constants.SECURITY_ROLE_GUEST.equals(party.getRolename())) {
//				throw new BusinessException("只能修改演示账户");
//			}

			Wallet wallet = this.walletService.saveWalletByPartyId(partyId);

			int frozenState = wallet.getFrozenState();

			double amount_before = wallet.getMoney();

			// 账变日志
			MoneyLog moneyLog = new MoneyLog();

			if (frozenState == 1 && reset_type.equals("recharge")){
				amount_before = wallet.getMoneyAfterFrozen();
				moneyLog.setFreeze(1);
			}

			if (Arith.add(money_revise, amount_before) < 0.0D) {
				throw new BusinessException("操作失败！修正后账户余额小于0。");
			}

			SecUser sec = this.secUserService.findUserByLoginName(operator_name);
			String sysSafeword = sec.getSafeword();
			String safeword_md5 = this.passwordEncoder.encodePassword(safeword, operator_name);
			if (!safeword_md5.equals(sysSafeword)) {
				throw new BusinessException("资金密码错误");
			}

			//除充值提现 其他都走冻结钱包money
			if (frozenState == 1 && reset_type.equals("recharge")){
				amount_before = wallet.getMoneyAfterFrozen();
				debugLogger.info("---> AdminUserServiceImpl saveResetCreateOrder 发起更新钱包余额的的请求, partyId:{}, amount:{} ...", wallet.getPartyId(), money_revise);
				this.walletService.update(wallet.getPartyId().toString(), money_revise);
				debugLogger.info("---> AdminUserServiceImpl saveResetCreateOrder 已提交请求更新钱包余额, partyId:{}, amount:{}", wallet.getPartyId(), money_revise);
			} else {
				moneyLog.setFreeze(0);
				debugLogger.info("---> AdminUserServiceImpl saveResetCreateOrder 发起更新钱包余额的的请求, partyId:{}, amount:{} ...", wallet.getPartyId(), money_revise);
				this.walletService.updateMoeny(wallet.getPartyId().toString(), money_revise);
				debugLogger.info("---> AdminUserServiceImpl saveResetCreateOrder 已提交请求更新钱包余额, partyId:{}, amount:{}", wallet.getPartyId(), money_revise);
			}


			Boolean produceRechargeBlockchain = !"change".equals(reset_type);//赠送彩金不生成充值订单
			result.put("flag", produceRechargeBlockchain);//赠送发生不生成充值订单，不发布事件
			String rechargeOrderNo = DateUtil.getToday("yyMMddHHmmss") + RandomUtil.getRandomNum(8);
			if (produceRechargeBlockchain) {
				//生成充值订单
				RechargeBlockchain recharge = new RechargeBlockchain();
				recharge.setAddress("-");
				recharge.setBlockchain_name("-");
				recharge.setVolume(money_revise);
				recharge.setSymbol("usdt");
				recharge.setPartyId(partyId);
				recharge.setSucceeded(1);
				recharge.setChannel_address("-");
				recharge.setTx("-");
				recharge.setAmount(Arith.roundDown(money_revise, 2));
				recharge.setRechargeCommission(0D);
				recharge.setOrder_no(rechargeOrderNo);
				recharge.setCreated(new Date());
				recharge.setReviewTime(new Date());
				this.getHibernateTemplate().save(recharge);
				result.put("orderNo",rechargeOrderNo);
			}

			moneyLog.setCategory(Constants.MONEYLOG_CATEGORY_COIN);
			moneyLog.setAmount_before(amount_before);
			moneyLog.setAmount(money_revise);
			moneyLog.setAmount_after(Arith.add(amount_before, money_revise));
			moneyLog.setPartyId(partyId);
			moneyLog.setWallettype(Constants.WALLET);
			moneyLog.setContent_type(Constants.MONEYLOG_CONTENT_RECHARGE);

			// 钱包日志
			WalletLog walletLog = new WalletLog();
			walletLog.setCategory(Constants.MONEYLOG_CATEGORY_COIN);
			if ("change".equals(reset_type)) {
				walletLog.setCategory(Constants.MONEYLOG_CATEGORY_JACKPOT);
			} else if ("recharge".equals(reset_type)) {
				walletLog.setCategory(Constants.MONEYLOG_CATEGORY_RECHARGE);
			}
			walletLog.setPartyId(partyId);
			walletLog.setOrder_no(produceRechargeBlockchain?rechargeOrderNo:"");
			walletLog.setStatus(1);
			walletLog.setAmount(money_revise);
			// 换算成USDT单位
			walletLog.setUsdtAmount(money_revise);
			walletLog.setWallettype(Constants.WALLET);
			this.walletLogService.save(walletLog);

			if (produceRechargeBlockchain) {//有充值订单时，生成事件信息
				RechargeInfo info = new RechargeInfo();
				info.setApplyUserId(partyId);
				info.setOrderNo("");
				info.setWalletLogId(walletLog.getId().toString());
				info.setAmount(money_revise);
				info.setEventTime(now);
				result.put("info",info);
			}

			// 操作日志			
			Log log = new Log();
			log.setCategory(Constants.LOG_CATEGORY_OPERATION);
			log.setUsername(party.getUsername());
			log.setOperator(operator_name);

			// change----添加赠送金额
			if("change".equals(reset_type)) {
				// 只有正式用户才需要记录报表
				if (null != party && Constants.SECURITY_ROLE_MEMBER.equals(party.getRolename())) {
					this.userDataService.saveGiftMoneyHandle(partyId, money_revise);
				}

				log.setLog("ip:"+ip+",管理员手动添加赠送金额。修改币种[usdt]，修改数量[" + money_revise + "]");
				moneyLog.setLog(produceRechargeBlockchain?"管理员手动添加赠送金额"+"["+rechargeOrderNo+"]":"管理员手动添加赠送金额");
				moneyLog.setCategory(Constants.MONEYLOG_CATEGORY_JACKPOT);
				moneyLog.setContent_type(Constants.MONEYLOG_CONTNET_JACKPOT);

				this.checkGiftUserLine(party);
			}

			// recharge--添加充值金额
			if("recharge".equals(reset_type)) {
				//记录第一次充值时间
				if (Constants.SECURITY_ROLE_MEMBER.equals(party.getRolename())) {
					if (Objects.isNull(party.getFirstRechargeTime())){
						debugLogger.info("->>>>>>>首充用户id:{}", party.getUsercode());
						party.setFirstRechargeTime(new Date());
					}
					// 只有正式用户才需要记录报表
					this.userDataService.saveRechargeHandle(partyId, money_revise, "usdt",money_revise);
				}
				log.setLog("ip:"+ip+",管理员手动添加充值金额。修改币种[usdt]，修改数量[" + money_revise + "]");
				moneyLog.setLog(produceRechargeBlockchain?"管理员手动添加充值金额"+"["+rechargeOrderNo+"]":"管理员手动添加充值金额");
				moneyLog.setCategory(Constants.MONEYLOG_CATEGORY_RECHARGE);
				moneyLog.setContent_type(Constants.MONEYLOG_CONTENT_RECHARGE);
			}

			this.moneyLogService.save(moneyLog);
			this.logService.saveSync(log);

			// 充值到账后给他增加提现流水限制金额
			party.setWithdraw_limit_amount(Arith.add(party.getWithdraw_limit_amount(), money_revise));
			this.partyService.update(party);
			if("change".equals(reset_type)) {
				try {
					notificationHelperClient.notifyGiveBonusByInbox(moneyLog);
				} catch (Exception e) {
					logger.error("发送通知消息赠送彩金事件报错:", e);
				}
			}

		} else {
//			if (party!=null&&!Constants.SECURITY_ROLE_GUEST.equals(party.getRolename())) {
//				throw new BusinessException("只能修改演示账户");
//			}

			WalletExtend walletExtend = this.walletService.saveExtendByPara(partyId, coin_type);
			double amount_before = walletExtend.getAmount();
			if (Arith.add(money_revise, walletExtend.getAmount()) < 0.0D) {
				throw new BusinessException("操作失败！修正后账户余额小于0。");
			}

			SecUser sec =  this.secUserService.findUserByLoginName(operator_name);
			String sysSafeword =sec.getSafeword();

			String safeword_md5 = this.passwordEncoder.encodePassword(safeword, operator_name);
			if (!safeword_md5.equals(sysSafeword)) {
				throw new BusinessException("资金密码错误");
			}

			this.walletService.updateExtend(walletExtend.getPartyId().toString(), coin_type, money_revise);

			// 账变日志
			MoneyLog moneyLog = new MoneyLog();
			moneyLog.setCategory(Constants.MONEYLOG_CATEGORY_COIN);
			moneyLog.setAmount_before(amount_before);
			moneyLog.setAmount(money_revise);
			moneyLog.setAmount_after(Arith.add(amount_before, money_revise));
			moneyLog.setPartyId(partyId);
			moneyLog.setWallettype(coin_type.toUpperCase());
			moneyLog.setContent_type(Constants.MONEYLOG_CONTENT_RECHARGE);

			// 钱包日志
			WalletLog walletLog = new WalletLog();
			walletLog.setCategory(Constants.MONEYLOG_CATEGORY_RECHARGE);
			walletLog.setPartyId(partyId);
			walletLog.setOrder_no("");
			walletLog.setStatus(1);
			walletLog.setAmount(money_revise);
			// 换算成USDT单位
			walletLog.setUsdtAmount(money_revise);
			walletLog.setWallettype(coin_type.toUpperCase());
			walletLogService.save(walletLog);

			// 发布一个充值审核成功的事件
//			WebApplicationContext wac = ContextLoader.getCurrentWebApplicationContext();
			RechargeInfo info = new RechargeInfo();
			info.setApplyUserId(partyId);
			info.setOrderNo("");
			info.setWalletLogId(walletLog.getId().toString());
			info.setAmount(money_revise);
			info.setEventTime(now);
//			wac.publishEvent(new RechargeSuccessEvent(this, info));
			result.put("info",info);

			// 操作日志
//			Party party = this.partyService.cachePartyBy(partyId, true);
			Log log = new Log();
			log.setCategory(Constants.LOG_CATEGORY_OPERATION);
			log.setUsername(party.getUsername());
			log.setOperator(operator_name);

			// recharge--添加充值金额
			if ("recharge".equals(reset_type)) {
				String coin_str= "";

				if("USDT_DAPP".equals(coin_type)) {
					coin_str="[质押账户(USDT)]";

					PledgeOrder entity_before = this.pledgeOrderService.findByPartyId(partyId);

					if (entity_before == null) {
						// TODO, ID是个写死的值合适吗？
						PledgeConfig entity_config = this.pledgeConfigService.findById("2c948a827cd5f779017cd2322f5d0001");
						PledgeOrder entity = new PledgeOrder();

						entity.setPartyId(partyId);
						entity.setConfig(entity_config.getConfig());
						entity.setUsdt(entity_config.getUsdt());
						entity.setLimit_days(entity_config.getLimit_days());
						entity.setEth(entity_config.getEth());
						entity.setTitle(entity_config.getTitle());
						entity.setTitle_img(entity_config.getTitle_img());

						entity.setContent(entity_config.getContent());
						entity.setContent_img(entity_config.getContent_img());
						entity.setApply(true);
						entity.setSendtime( DateUtils.addDate(DateUtils.toDate(DateUtils.format(new Date(), DateUtils.DEFAULT_DATE_FORMAT)), entity.getLimit_days()));
						entity.setApplyTime(new Date());

//						entity.setSendtime(this.sendtime);
						entity.setCreateTime(new Date());

						this.pledgeOrderService.save(entity);
					}
				}

				if("USDT_USER".equals(coin_type)) {
					coin_str="[用户钱包USDT映射]";
				}

				// 只有正式用户才需要记录报表
				if (null != party && Constants.SECURITY_ROLE_MEMBER.equals(party.getRolename())) {
					this.userDataService.saveRechargeHandle(partyId, money_revise, coin_type,money_revise);
				}

				log.setLog("ip:"+ip+",管理员手动添加充值金额。修改币种["+coin_type+"]"+coin_str+"，修改数量[" + money_revise + "]");
				moneyLog.setLog("管理员手动添加充值金额");
			}

			this.moneyLogService.save(moneyLog);
			this.logService.saveSync(log);
		}
		return result;
	}

	/**
	 *  修改余额 减少
	 */
	public void saveResetCreateWithdraw(String partyId, double money_revise, String safeword, String operator_name, String reset_type, String ip, String coin_type) {

		// money_revise为负值
		if (money_revise == 0 || coin_type == "") {
			return;
		}

		if ("usdt".equals(coin_type)) {
			// 交易所修改usdt

			Party party = this.partyService.cachePartyBy(partyId, false);
//			if (party!=null&&!Constants.SECURITY_ROLE_GUEST.equals(party.getRolename())) {
//				throw new BusinessException("只能修改演示账户");
//			}

			Wallet wallet = this.walletService.saveWalletByPartyId(partyId);

			int frozenState = wallet.getFrozenState();
			double amount_before = wallet.getMoney();

			if (frozenState == 1 && reset_type.equals("withdraw")){
				amount_before = wallet.getMoneyAfterFrozen();
			}

			if (wallet.getFrozenState() == 1 && reset_type.equals("withdraw")){
				 amount_before = wallet.getMoneyAfterFrozen();
			}
			if (amount_before < money_revise) {
				throw new BusinessException("余额不足");
			}
			if (Arith.add(money_revise, amount_before) < 0.0D) {
				throw new BusinessException("操作失败！修正后账户余额小于0。");
			}

			// 判断资金密码
			SecUser sec =  this.secUserService.findUserByLoginName(operator_name);
			String sysSafeword =sec.getSafeword();
			String safeword_md5 = this.passwordEncoder.encodePassword(safeword, operator_name);
			if (!safeword_md5.equals(sysSafeword)) {
				throw new BusinessException("资金密码错误");
			}

			// 账变日志
			MoneyLog moneyLog = new MoneyLog();

			if (frozenState == 1 && reset_type.equals("withdraw")){
				moneyLog.setFreeze(1);
				this.walletService.update(wallet.getPartyId().toString(), money_revise);
			} else {
				this.walletService.updateMoeny(wallet.getPartyId().toString(), money_revise);
			}

			Boolean produceWithdraw = !"changesub".equals(reset_type);
			String orderNo = DateUtil.getToday("yyMMddHHmmss") + RandomUtil.getRandomNum(8);
			if (produceWithdraw) {//扣减彩金不生成提款订单
				//生成提现订单
				Withdraw withdraw = new Withdraw();
				withdraw.setOrder_no(orderNo);
				withdraw.setPartyId(partyId);
				withdraw.setVolume(Math.abs(money_revise));
				withdraw.setAddress("-");
				withdraw.setCurrency("USDT");
				withdraw.setTx("");
				withdraw.setWithdrawCommission(0D);
				withdraw.setAmount(Math.abs(money_revise));
				withdraw.setArrivalAmount(Math.abs(money_revise));
				withdraw.setCreateTime(new Date());
				withdraw.setReviewTime(new Date());
				withdraw.setSucceeded(1);
				this.getHibernateTemplate().save(withdraw);
			}

			moneyLog.setCategory(Constants.MONEYLOG_CATEGORY_COIN);
			moneyLog.setAmount_before(amount_before);
			moneyLog.setAmount(money_revise);
			moneyLog.setAmount_after(Arith.add(amount_before, money_revise));
			moneyLog.setPartyId(partyId);
			moneyLog.setWallettype(Constants.WALLET);
			moneyLog.setContent_type(produceWithdraw?Constants.MONEYLOG_CONTENT_WITHDRAW:Constants.MONEYLOG_CONTNET_CHANGESUB);

			// 钱包日志
			WalletLog walletLog = new WalletLog();
			walletLog.setCategory(produceWithdraw?Constants.MONEYLOG_CONTENT_WITHDRAW:Constants.MONEYLOG_CONTNET_CHANGESUB);
			walletLog.setPartyId(partyId);
			walletLog.setStatus(1);
			walletLog.setAmount(money_revise);
			walletLog.setOrder_no(produceWithdraw?orderNo:"");
			// 换算成USDT单位
			walletLog.setUsdtAmount(money_revise);
			walletLog.setWallettype(Constants.WALLET);
			this.walletLogService.save(walletLog);

			// 操作日志
			Log log = new Log();
			log.setCategory(Constants.LOG_CATEGORY_OPERATION);
			log.setUsername(party.getUsername());
			log.setOperator(operator_name);

			// changesub----扣彩金
			if("changesub".equals(reset_type)) {
				// 只有正式用户才需要记录报表
				if (null != party && Constants.SECURITY_ROLE_MEMBER.equals(party.getRolename())) {
					this.userDataService.saveGiftMoneyHandle(partyId, money_revise);
				}

				log.setLog("ip:"+ip+",管理员手动扣减彩金。修改币种[usdt]，修改数量[" + money_revise + "]");
				moneyLog.setLog("管理员手动扣减彩金");
				moneyLog.setCategory(Constants.MONEYLOG_CONTNET_CHANGESUB);
				moneyLog.setContent_type(Constants.MONEYLOG_CONTNET_CHANGESUB);
				this.checkGiftUserLine(party);
			}
			if("withdraw".equals(reset_type)) {
				if (null != party && Objects.isNull(party.getFirstWithdrawTime()) && party.getRolename().equals(Constants.SECURITY_ROLE_MEMBER)){
					debugLogger.info("->>>>>>>首次提现用户id:{}", party.getUsercode());
					party.setFirstWithdrawTime(new Date());
					partyService.update(party);
				}
				this.userDataService.saveWithdrawHandle(partyId,  Arith.sub(0, money_revise),0,"usdt",Arith.sub(0, money_revise));
				log.setLog("ip:"+ip+",管理员后台手动提现金额，修改币种[usdt]，" + "提现数量["+ money_revise + "]");
				moneyLog.setLog("管理员后台手动提现金额"+"["+orderNo+"]");
			}

			this.moneyLogService.save(moneyLog);
			this.logService.saveSync(log);

		} else {
			// 交易所修改btc、eth；DAPP修改质押账户（USDT）【USDT_DAPP】；DAPP演示用户修改DAPP余额【USDT_USER】；

			Party party = this.partyService.cachePartyBy(partyId, false);
//			if (party!=null&&!Constants.SECURITY_ROLE_GUEST.equals(party.getRolename())) {
//				throw new BusinessException("只能修改演示账户");
//			}

			WalletExtend walletExtend = this.walletService.saveExtendByPara(partyId, coin_type);
			double amount_before = walletExtend.getAmount();
			if (walletExtend.getAmount() < money_revise) {
				throw new BusinessException("余额不足");
			}
			if (Arith.add(money_revise, walletExtend.getAmount()) < 0.0D) {
				throw new BusinessException("操作失败！修正后["+coin_type.toUpperCase()+"]账户余额小于0。");
			}

			// 判断资金密码
			SecUser sec =  this.secUserService.findUserByLoginName(operator_name);
			String sysSafeword =sec.getSafeword();
			String safeword_md5 = this.passwordEncoder.encodePassword(safeword, operator_name);
			if (!safeword_md5.equals(sysSafeword)) {
				throw new BusinessException("资金密码错误");
			}

			// 更新金额
			this.walletService.updateExtend(walletExtend.getPartyId().toString(), coin_type, money_revise);

			// 账变日志
			MoneyLog moneyLog = new MoneyLog();
			moneyLog.setCategory(Constants.MONEYLOG_CATEGORY_COIN);
			moneyLog.setAmount_before(amount_before);
			moneyLog.setAmount(money_revise);
			moneyLog.setAmount_after(Arith.add(amount_before, money_revise));
			moneyLog.setPartyId(partyId);
			moneyLog.setWallettype(coin_type.toUpperCase());
			moneyLog.setContent_type(Constants.MONEYLOG_CONTENT_WITHDRAW);

			// 钱包日志
			WalletLog walletLog = new WalletLog();
			walletLog.setCategory("withdraw");
			walletLog.setPartyId(partyId);
			walletLog.setStatus(1);
			walletLog.setAmount(money_revise);
			// 换算成USDT单位
			walletLog.setUsdtAmount(money_revise);
			walletLog.setWallettype(coin_type.toUpperCase());
			this.walletLogService.save(walletLog);

			SecUser SecUser =  secUserService.findUserByPartyId(partyId);

			Log log = new Log();
			log.setCategory(Constants.LOG_CATEGORY_OPERATION);
			log.setUsername(SecUser.getUsername());
			log.setOperator(operator_name);

			String coin_str= "";
			if("USDT_DAPP".equals(coin_type)) {
				coin_str="[质押账户(USDT)]";
			}
			if("USDT_USER".equals(coin_type)) {
				coin_str="[用户钱包USDT映射]";
			}

			log.setLog("ip:"+ip+",管理员手动减少充值金额，修改币种["+coin_type+"]"+coin_str+"，" + "修改数量["+ money_revise + "]");
			moneyLog.setLog("管理员手动减少充值金额");

			this.moneyLogService.save(moneyLog);
			this.logService.saveSync(log);
		}
	}

	/**
	 * 检验是否达到赠送用户的达标线
	 * @param party
	 */
	public void checkGiftUserLine(Party party) {
//		Party party = this.partyService.cachePartyBy(partyId, false);
		if(!party.getGift_money_flag()) {
			party.setGift_money_flag(true);
		}
		if(party.getGift_user()) {//已经是赠送用户则无需判定
			return;
		}
		Map<String, UserData> datas = userDataService.cacheByPartyId(party.getId().toString());
		Double giftUserLine = sysparaService.find("gift_user_line").getDouble();
		String gift_user_date_start = sysparaService.find("gift_user_date_start").getValue();
		double giftMoney = giftMoney(datas, gift_user_date_start, null);
		if(giftMoney>=giftUserLine) {
			party.setGift_user(true);
		}
	}

	/**
	 * 时间范围内的充值总额
	 *
	 * @param datas
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	private double giftMoney(Map<String, UserData> datas, String startTime, String endTime) {
		if (datas == null || datas.isEmpty())
			return 0;
		double giftMoney = 0;
		for (Entry<String, UserData> valueEntry : datas.entrySet()) {
			UserData userdata = valueEntry.getValue();
			Date time = userdata.getCreateTime();
			if (!StringUtils.isNullOrEmpty(startTime)) {
				Date startDate = DateUtils.toDate(startTime, DateUtils.DF_yyyyMMdd);
				int intervalDays = DateUtils.getIntervalDaysByTwoDate(startDate, time);// 开始-数据时间
				if (intervalDays > 0) // 开始>数据时间 ，则过滤
					continue;
			}
			if (!StringUtils.isNullOrEmpty(endTime)) {
				Date endDate = DateUtils.toDate(endTime, DateUtils.DF_yyyyMMdd);
				int intervalDays = DateUtils.getIntervalDaysByTwoDate(endDate, time);// 结束-数据时间
				if (intervalDays < 0) // 结束<数据时间
					continue;
			}
			giftMoney = Arith.add(userdata.getGift_money(), giftMoney);
		}

		return giftMoney;
	}

	public void saveResetWithdraw(String partyId,double money_withdraw,String operator_username,String ip) {

		Party party = this.partyService.cachePartyBy(partyId,false);
		double last_amount = party.getWithdraw_limit_amount();

		party.setWithdraw_limit_amount(Arith.add(party.getWithdraw_limit_amount(), money_withdraw));

		double after_party = party.getWithdraw_limit_amount();
		this.partyService.update(party);

		/**
		 * 操作日志 
		 */
		SecUser SecUser =  secUserService.findUserByPartyId(partyId);
		Log log = new Log();
		log.setCategory(Constants.LOG_CATEGORY_OPERATION);
		log.setUsername(SecUser.getUsername());
		log.setOperator(operator_username);
		log.setLog("ip:"+ip+",管理员手动修改提现限制流水。修改前数量为["+last_amount+"]，"
				+ "修改数量为["+money_withdraw+"]，修改后数量为["
				+ after_party + "]。");

		logService.saveSync(log);
	}

	public void setPagedQueryDao(PagedQueryDao pagedQueryDao) {
		this.pagedQueryDao = pagedQueryDao;
	}

	public void setUserRecomService(UserRecomService userRecomService) {
		this.userRecomService = userRecomService;
	}

	public void setWalletService(WalletService walletService) {
		this.walletService = walletService;
	}

	public void setPartyService(PartyService partyService) {
		this.partyService = partyService;
	}

	public void setSecUserService(SecUserService secUserService) {
		this.secUserService = secUserService;
	}

	public void setRoleService(RoleService roleService) {
		this.roleService = roleService;
	}

	public void setMoneyLogService(MoneyLogService moneyLogService) {
		this.moneyLogService = moneyLogService;
	}

	public void setUserDataService(UserDataService userDataService) {
		this.userDataService = userDataService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}
	private String getUsercode() {
		Syspara syspara = sysparaService.find("user_uid_sequence");
		int random = (int) (Math.random() * 3 + 1);
		int user_uid_sequence = syspara.getInteger() + random;
		syspara.setValue(user_uid_sequence);
		sysparaService.update(syspara);

		String usercode = String.valueOf(user_uid_sequence);
//		Party party = this.partyService.findPartyByUsercode(usercode);
//		if (party != null) {
//			usercode = getUsercode();
//		}

		return usercode;
	}

	public void setqRGenerateService(QRGenerateService qRGenerateService) {
		this.qRGenerateService = qRGenerateService;
	}

	public class PosterThread implements Runnable {
		String image_name;
		String usercode;

		public void run() {
			try {
				qRGenerateService.generate_poster(image_name, usercode);

			} catch (Exception e) {
				logger.error("error:", e);
			}

		}

		public PosterThread(String image_name, String usercode) {
			this.image_name = image_name;
			this.usercode = usercode;
		}

	}

	public int getUserCount(String isMember, String startTime, String endTime, String loginPartyId) {
		StringBuffer queryString = new StringBuffer();
		queryString.append("SELECT COUNT(id) FROM Party WHERE 1=1 ");
		Map<String,Object> parameters = new HashMap<String,Object>();

		if (!StringUtils.isNullOrEmpty(loginPartyId)) {
			String childrensId = childrensId(loginPartyId);
			if (StringUtils.isEmptyString(childrensId)) {
				return 0;
			}
			queryString.append(" and id in("+childrensId+") ");
		}
		if(Constants.SECURITY_ROLE_MEMBER.equals(isMember)) {
//			queryString.append("AND partyId is not null AND LENGTH(trim(partyId))!=0");
			queryString.append("AND rolename ='"+Constants.SECURITY_ROLE_MEMBER+"' ");
		}
		if (!StringUtils.isNullOrEmpty(startTime)) {
			queryString.append(" and DATE(createTime) >=DATE(?)  ");
			parameters.put("startTime", startTime);

		}
		if (!StringUtils.isNullOrEmpty(endTime)) {
			queryString.append(" and DATE(createTime) <=DATE(?)  ");
			parameters.put("endTime", endTime);
		}

		List find = this.getHibernateTemplate().find(queryString.toString(),parameters.values().toArray());
		return CollectionUtils.isEmpty(find)?0:find.get(0)==null?0:Integer.valueOf(find.get(0).toString());
	}

	/**
	 * 父类网络
	 * @param partyId
	 * @return
	 */
	public List<Map<String,Object>> getParentsNet(String partyId){
		List<Map<String,Object>> result = new ArrayList<Map<String,Object>>();
		Map<String,Object> data = new HashMap<String, Object>();
		Party party = partyService.cachePartyBy(partyId, false);
		data.put("username", party.getUsername());
		data.put("usercode", party.getUsercode());
		data.put("rolename", party.getRolename());
		data.put("kyc_authority", party.getKyc_authority());
		result.add(data);
		List<UserRecom> parents = userRecomService.getParents(partyId);
		for(UserRecom recom:parents) {
			data = new HashMap<String, Object>();
			Party parent = partyService.cachePartyBy(recom.getReco_id().toString(), false);
			data.put("username", parent.getUsername());
			data.put("usercode", parent.getUsercode());
			data.put("rolename", parent.getRolename());
			data.put("kyc_authority", party.getKyc_authority());
			result.add(data);
		}
		Collections.reverse(result);
		return result;
	}

	@Override
	public void saveUserAddress(MallAddress mallAddress) {
		getHibernateTemplate().save(mallAddress);
	}

	@Override
	public void updateUserAddress(MallAddress mallAddress) {
		getHibernateTemplate().update(mallAddress);
	}

	@Override
	public void saveImport(String username, String password, boolean login_authority, boolean enabled, String remarks,
						   String operatorUsername,String ip,String parents_usercode,double money) {
		/**
		 * 用户code
		 */
		String usercode = getUsercode();

		if (!StringUtils.isNullOrEmpty(parents_usercode)) {
			Party party_parents=partyService.findPartyByUsercode(parents_usercode);
			if (party_parents==null ) {
				throw new BusinessException("推荐码不正确");
			}
		}


		/**
		 * party
		 */
		Party party = new Party();
		if (username.contains("@")) {
			// 认为是基于邮箱注册账号
			party.setEmail(username);
		} else {
			party.setPhone(username);
		}
		int avatar_num = (1 + new Random().nextInt(19));

		party.setUsername(username.replace(" ", "").trim());
		party.setLogin_authority(login_authority);
		party.setEnabled(enabled);
		party.setRemarks(remarks);
		party.setUsercode(usercode);
		party.setUser_level(1);
		party.setAvatar(String.valueOf(avatar_num));
		party.setChatAudit(1);

		party.setSafeword(passwordEncoder.encodePassword("000000", SaltSigureUtils.saltfigure));

		party.setRolename(Constants.SECURITY_ROLE_GUEST);

		party.setAutoComment(true);

		party = partyService.save(party);

		if (!StringUtils.isNullOrEmpty(parents_usercode)) {
			Party party_parents=partyService.findPartyByUsercode(parents_usercode);
			if (party_parents==null ) {
				throw new BusinessException("推荐码不正确");
			}
			UserRecom userRecom = new UserRecom();
			userRecom.setPartyId(party.getId());
			userRecom.setReco_id(party_parents.getId().toString());// 父类partyId
			this.userRecomService.save(userRecom);
		}


		/**
		 * SecUser
		 */
		Role role = this.roleService.findRoleByName(Constants.SECURITY_ROLE_GUEST);

		SecUser secUser = new SecUser();
		secUser.setPartyId(String.valueOf(party.getId()));
		secUser.getRoles().add(role);

		if (username.contains("@")) {
			// 认为是基于邮箱注册账号
			secUser.setUsername(username);
		} else {
			secUser.setUsername(username.replace(" ", "").trim());
		}
		secUser.setPassword(password.trim());
		secUser.setEnabled(login_authority);

		this.secUserService.saveUser(secUser);

		/**
		 * usdt账户
		 */
		Wallet wallet = new Wallet();
		wallet.setPartyId(party.getId().toString());
		wallet.setMoney(money);
		this.walletService.save(wallet);

		User user = new User();
		user.setPartyId(party.getId());
		this.getHibernateTemplate().save(user);

		project.log.Log log = new project.log.Log();
		log.setCategory(Constants.LOG_CATEGORY_OPERATION);
		log.setUsername(party.getUsername());
		log.setOperator(operatorUsername);
		log.setLog("ip:"+ip+",管理员手动新增了演示用户:"+username);
		logService.saveSync(log);
	}

	@Override
	@Transactional
	public void updateUserName(String partyId, String userName, String password, String registerType, String phone, String usernameLogin, String loginSafeword) {
		Party party = this.partyService.cachePartyBy(partyId, false);
		String username = party.getUsername();
		if (null == party){
			throw new BusinessException("用户不存在");
		}

		SecUser sec =  this.secUserService.findUserByLoginName(usernameLogin);
		String sysSafeword =sec.getSafeword();

		String safeword_md5 = this.passwordEncoder.encodePassword(loginSafeword, usernameLogin);
		if (!safeword_md5.equals(sysSafeword)) {
			throw new BusinessException("登录人资金密码错误");
		}

		if (Objects.equals(registerType,"phone") && null != this.partyService.findPartyByVerifiedPhone(phone)) {
			throw new BusinessException("该手机号已被占用，请绑定其他手机号");
		} else if (null != this.partyService.getPartyByEmail(userName)){
			throw new BusinessException("该邮箱已被占用，请绑定其他邮箱");
		}
		SecUser db = secUserService.findUserByLoginName(userName);
		if (null != db) {
			throw new BusinessException("用户名称已存在");
		}

		String platformName = sysparaService.find("platform_name").getValue();
		party.setUsername(userName);
		if (Objects.equals(registerType,"phone")){
			party.setPhone(phone);
			party.setPhone_authority(false);
		} else {
			party.setEmail(userName);
			party.setEmail_authority(false);
		}

		//定制盘 9/6 手机号与邮箱都是认证状态
		if (null != platformName && (Objects.equals(platformName, PlatformNameEnum.SM.getDescription()) || Objects.equals(platformName, PlatformNameEnum.FAMILY_SHOP.getDescription()))){
			party.setPhone_authority(true);
			party.setEmail_authority(true);
		}

		party.setSafeword(passwordEncoder.encodePassword("000000", SaltSigureUtils.saltfigure));
		debugLogger.info("->>>>>>>party:{}", JSON.toJSONString(party));
		partyService.update(party);
		redisHandler.remove(PartyRedisKeys.PARTY_USERNAME + username);

		secUserService.updateSecUser(partyId,userName,password);

		project.log.Log log = new project.log.Log();
		log.setCategory(Constants.LOG_CATEGORY_OPERATION);
		log.setUsername(party.getUsername());
		log.setOperator(usernameLogin);
		String content = MessageFormat.format("管理员手动修改用户名称，修改类型:{0},修改前账号:{1},修改后账号:{2}", registerType, username, userName);
		log.setLog(content);
		logService.saveSync(log);
	}

	@Override
	public MallAddress findUserAddressById(String id) {
		return (MallAddress) getHibernateTemplate().get(MallAddress.class, id);
	}
//	public Withdraw get(String id) {
//		return this.getHibernateTemplate().get(Withdraw.class, id);
//	}

	public void setLogService(LogService logService) {
		this.logService = logService;
	}

	public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
		this.passwordEncoder = passwordEncoder;
	}

	public void setSysparaService(SysparaService sysparaService) {
		this.sysparaService = sysparaService;
	}

//
//	public void setWithdrawService(WithdrawService withdrawService) {
//		this.withdrawService = withdrawService;
//	}

	public void setWalletLogService(WalletLogService walletLogService) {
		this.walletLogService = walletLogService;
	}


	private String childrensId(String loginPartyId) {
		String childrensId = "";
		if (!StringUtils.isNullOrEmpty(loginPartyId)) {
			List<String> children = this.userRecomService.findChildren(loginPartyId);
			if (children.size() == 0) {
				return null;
			}
			List<String> ids = new LinkedList<String>();
			for(String p:children) {
				ids.add("'"+p+"'");
			}
			childrensId = String.join(",", ids);
		}
		return childrensId;
	}



	public void setDataService(DataService dataService) {
		this.dataService = dataService;
	}



	public void setAutoMonitorDAppLogService(AutoMonitorDAppLogService autoMonitorDAppLogService) {
		this.autoMonitorDAppLogService = autoMonitorDAppLogService;
	}



	public void setPledgeOrderService(PledgeOrderService pledgeOrderService) {
		this.pledgeOrderService = pledgeOrderService;
	}



	public void setPledgeConfigService(PledgeConfigService pledgeConfigService) {
		this.pledgeConfigService = pledgeConfigService;
	}

	public void setRedisHandler(RedisHandler redisHandler) {
		this.redisHandler = redisHandler;
	}

	public void setNotificationHelperClient(NotificationHelperClient notificationHelperClient) {
		this.notificationHelperClient = notificationHelperClient;
	}
}
