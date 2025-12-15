package project.web.api;

import java.io.IOException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import ext.Strings;
import kernel.web.Page;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kernel.exception.BusinessException;
import kernel.sessiontoken.SessionTokenService;
import kernel.util.Arith;
import kernel.util.DateUtils;
import kernel.util.StringUtils;
import kernel.util.ThreadUtils;
import kernel.web.BaseAction;
import kernel.web.ResultObject;
import project.Constants;
import project.invest.vip.VipService;
import project.log.MoneyFreeze;
import project.log.MoneyFreezeService;
import project.mall.utils.PlatformNameEnum;
import project.party.PartyService;
import project.party.model.Party;
import project.syspara.SysParaCode;
import project.syspara.Syspara;
import project.syspara.SysparaService;
import project.wallet.WalletLogService;
import project.wallet.rate.ExchangeRateService;
import project.withdraw.Withdraw;
import project.withdraw.WithdrawService;
import util.LockFilter;

/**
 * 提现
 */
@RestController
@CrossOrigin
public class WithdrawController extends BaseAction {

	private Logger logger = LogManager.getLogger(WithdrawController.class);

	@Autowired
	private WithdrawService withdrawService;
	@Autowired
	private PartyService partyService;
	@Autowired
	private SessionTokenService sessionTokenService;
	@Autowired
	private SysparaService sysparaService;
	@Autowired
	protected WalletLogService walletLogService;
	@Resource
	VipService vipService;

	@Autowired
	private MoneyFreezeService moneyFreezeService;

	@Autowired
	private ExchangeRateService exchangeRateService;

	private final String action = "/api/withdraw!";

	/**
	 * 首次进入页面，传递session_token
	 */
	@RequestMapping(action + "withdraw_open.action")
	public Object withdraw_open() throws IOException {

		ResultObject resultObject = new ResultObject();
		resultObject = this.readSecurityContextFromSession(resultObject);
		if (!"0".equals(resultObject.getCode())) {
			return resultObject;
		}
		
		try {
			String partyId = getLoginPartyId();
			String session_token = this.sessionTokenService.savePut(partyId);
			
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("session_token", session_token);

			// 是否需要提醒用户绑定提现地址
			int openWithdrawAddressBinding = 0;
			String existWithdrawAddress = "";
			String coinType = "";
			String chainName = "";
			Syspara syspara = sysparaService.find(SysParaCode.MUST_BIND_WITHDRAW_ADDRESS.getCode());
			Party party = this.partyService.cachePartyBy(partyId, false);
			if (syspara != null) {
				String needBindInfo = syspara.getValue().trim();
				if (StrUtil.isNotBlank(needBindInfo) && needBindInfo.trim().equalsIgnoreCase("true")) {
					// 强制要求用户绑定提现地址
					openWithdrawAddressBinding = 1;
				} else {
					// 已有地址
					openWithdrawAddressBinding = 0;
				}
			}
			if (StrUtil.isNotBlank(party.getWithdrawAddress()) && !Objects.equals(party.getWithdrawAddress(), "0")) {
				existWithdrawAddress = party.getWithdrawAddress().trim();
				coinType = party.getWithdrawCoinType().trim();
				chainName = party.getWithdrawChainName();
				// 临时修复旧数据 TODO
				if (StrUtil.isBlank(chainName) || Objects.equals(chainName, "0")) {
					if (existWithdrawAddress.startsWith("0x")) {
						chainName = "ERC20";
					} else if (existWithdrawAddress.startsWith("T")) {
						chainName = "TRC20";
					} else if (existWithdrawAddress.startsWith("1") || existWithdrawAddress.startsWith("3")) {
						chainName = "OMNI";
					}
					party.setWithdrawChainName(chainName);
					partyService.update(party);
				}
			} else {
				existWithdrawAddress = "";
			}

			data.put("coinType", coinType);
			data.put("chainName", chainName);
			data.put("openWithdrawAddressBinding", openWithdrawAddressBinding);
			data.put("existWithdrawAddress", existWithdrawAddress);

			resultObject.setData(data);
		} catch (BusinessException e) {
			resultObject.setCode("1");
			resultObject.setMsg(e.getMessage());
		} catch (Throwable t) {
			resultObject.setCode("1");
			resultObject.setMsg("程序错误");
			logger.error("error:", t);
		}
		
		return resultObject;
	}

	/**
	 * 提取提现相关的限制配置信息
	 */
	@RequestMapping(action + "withdrawLimitConfig.action")
	public Object getWithdrawLimitConfig() {
		ResultObject resultObject = new ResultObject();
		resultObject = this.readSecurityContextFromSession(resultObject);
		if (!"0".equals(resultObject.getCode())) {
			return resultObject;
		}

		try {
			Double withdrawAmountMin = sysparaService.find("withdraw_limit").getDouble();
			Double withdrawAmountMax = sysparaService.find("withdraw_limit_max").getDouble();

			Map<String, Object> data = new HashMap<String, Object>();
			data.put("withdrawAmountMin", withdrawAmountMin);
			data.put("withdrawAmountMax", withdrawAmountMax);

			resultObject.setData(data);
		} catch (BusinessException e) {
			resultObject.setCode("1");
			resultObject.setMsg(e.getMessage());
		} catch (Throwable t) {
			resultObject.setCode("1");
			resultObject.setMsg("程序错误");
			logger.error("error:", t);
		}

		return resultObject;
	}

	/**
	 * 提现申请
	 *
	 * safeword 资金密码
	 * amount 提现金额
	 * from 客户转出地址
	 * currency 货币 CNY USD
	 * channel 渠道 USDT,BTC,ETH
	 */
	@RequestMapping(action + "apply.action")
	public Object apply(HttpServletRequest request) throws IOException {
		String session_token = request.getParameter("session_token");
		String safeword = request.getParameter("safeword");
		String amount = request.getParameter("amount");
		String from = request.getParameter("from");
		String currency = request.getParameter("currency");
		// 如果是银行卡方式，此处填写: bank
		String channel = request.getParameter("channel");
		// 银行卡提现模式，此处填写银行名称
		String bankName = request.getParameter("bankName");
		// 银行卡提现模式，此处填写用户姓名
		String bankUserName = request.getParameter("bankUserName");
		// 银行卡提现模式，此处填写银行卡号
		String bankCardNo = request.getParameter("bankCardNo");
		// 银行卡提现模式，此处填写路由号码
		String routingNum = request.getParameter("routingNum");
		// 银行卡提现模式，此处填写账户地址
		String accountAddress = request.getParameter("accountAddress");
		// 银行卡提现模式，此处填写银行地址
		String bankAddress = request.getParameter("bankAddress");
		// 银行卡提现模式，此处填写银行卡号
		String countryName = request.getParameter("countryName");
		// 银行卡提现模式，此处填写国际代码
		String swiftCode = request.getParameter("swiftCode");

		ResultObject resultObject = new ResultObject();
		resultObject = this.readSecurityContextFromSession(resultObject);
		if (!"0".equals(resultObject.getCode())) {
			return resultObject;
		}

		String partyId = this.getLoginPartyId();

		boolean lock = false;

		try {
			String error = this.verif(amount);
			if (!StringUtils.isNullOrEmpty(error)) {
				throw new BusinessException(error);
			}

			double amount_double = Double.valueOf(amount).doubleValue();
			double usdtAmount = amount_double;
			if (!channel.equalsIgnoreCase("bank")) {
				usdtAmount = this.compute2UsdtAmount(amount_double, channel);
			}

			Party party = partyService.cachePartyBy(partyId, false);

			String partySafeword = party.getSafeword();
			if(StringUtils.isEmptyString(partySafeword)){
				throw new BusinessException(999, "请设置资金密码");
			}

			// 获取用户是否满足提现错误次数要求状态
			if (!partyService.getWithdrawDepositPasswordFailedNumberStatus(partyId)){
				throw new BusinessException("连续输入错误三次，请3分钟后再试");
			}

			if (StringUtils.isEmptyString(safeword)) {
				throw new BusinessException("资金密码不能为空");
			}

			if (safeword.length() < 6 || safeword.length() > 12) {
				throw new BusinessException("资金密码必须6-12位");
			}

			if (this.partyService.checkSafeword(safeword, partyId)) {
				partyService.updateWithdrawDepositPasswordFailedNumber(partyId,Boolean.TRUE);
			} else {
				partyService.updateWithdrawDepositPasswordFailedNumber(partyId,Boolean.FALSE);
				throw new BusinessException("资金密码错误");
			}

//			//10/15 新盘口 TikTokWholesale冻结金额 无法提现
//			String platformName = sysparaService.find("platform_name").getValue();
//			if (Objects.equals(platformName, PlatformNameEnum.TIKTOK_WHOLESALE.getDescription())) {
//				if (Objects.nonNull(moneyFreezeService.getLastFreezeRecord(partyId))){
//					throw new BusinessException("因您违反平台规则，钱包余额已被冻结。无法提现!");
//				}
//			}
			List<Withdraw> unfinishedWithdrawList = withdrawService.selectUnFinishedWithdraw(partyId);
			if (CollectionUtil.isNotEmpty(unfinishedWithdrawList)) {
				int withDrawOnlyOnce = 1;
				Syspara withdrawParam = sysparaService.find("withdraw_only_one");
				if (withdrawParam != null) {
					withDrawOnlyOnce = Integer.parseInt(withdrawParam.getValue().trim());
				}
				if (withDrawOnlyOnce == 1) {
					throw new BusinessException("您有一笔提现正在处理中");
				}
			}

			Integer withdraw_limit_num = sysparaService.find("withdraw_limit_num").getInteger();

			Double withdrawalMin = sysparaService.find("withdraw_limit").getDouble();
			Double withdrawalMax = sysparaService.find("withdraw_limit_max").getDouble();
			if (usdtAmount < withdrawalMin.doubleValue()) {
				throw new BusinessException("提款金额不得低于最小提款限额");
			}
			if (usdtAmount > withdrawalMax) {
				throw new BusinessException("提款金额不得高于最大提款限额");
			}
			if (withdraw_limit_num != null && withdraw_limit_num > 0) {
				List<Withdraw> withdrawList = this.withdrawService.selectWithdraw(partyId);
				if (!CollectionUtils.isEmpty(withdrawList) && withdrawList.size() > withdraw_limit_num) {
					throw new BusinessException("超过提现次数");
				}
			}
			if (!isDuring()) {
				throw new BusinessException("不在提现时间范围内，请稍后再试");
			}

			if (!LockFilter.add(partyId)) {
				throw new BusinessException(error);
			}

			lock = true;

			Object object = this.sessionTokenService.cacheGet(session_token);
			this.sessionTokenService.delete(session_token);
			if (null == object || !this.getLoginPartyId().equals((String) object)) {
				throw new BusinessException("请稍后再试");
			}

			// 提现扣除提成业绩比例
			double withdrawCommissionRate = 0.0;
			Syspara withdrawCommissionPara = sysparaService.find(SysParaCode.WITHDRAW_COMMISSION_RATE.getCode());
			if (withdrawCommissionPara != null) {
				String rechargeCommissionRateStr = withdrawCommissionPara.getValue().trim();
				if (StrUtil.isNotBlank(rechargeCommissionRateStr)) {
					withdrawCommissionRate = Double.parseDouble(rechargeCommissionRateStr.trim());
				}
			}

			Withdraw withdraw = new Withdraw();
			withdraw.setPartyId(partyId);
			withdraw.setVolume(amount_double);
			// withdraw.setAmount(usdtAmount);
			withdraw.setAddress(from);
			withdraw.setCurrency(currency);
			withdraw.setTx("");
			withdraw.setWithdrawCommission(Arith.roundDown(Arith.mul(usdtAmount, withdrawCommissionRate), 2));
			// 银行卡模式提现时填充的额外参数
			withdraw.setBank(bankName);
			withdraw.setUsername(bankUserName);
			withdraw.setAccount(bankCardNo);
			withdraw.setRoutingNum(routingNum);
			withdraw.setAccountAddress(accountAddress);
			withdraw.setBankAddress(bankAddress);
			withdraw.setCountryName(countryName);
			withdraw.setSwiftCode(swiftCode);

			// 保存
			this.withdrawService.saveApply(withdraw, channel, null);

			// 挂住线程保证事务提交
			ThreadUtils.sleep(300);
		} catch (BusinessException e) {
			resultObject.setCode(e.getSign()+"");
			resultObject.setMsg(e.getMessage());
		} catch (Throwable t) {
			resultObject.setCode("1");
			resultObject.setMsg("程序错误");
			logger.error("error:", t);
		} finally {
			if (lock) {
				LockFilter.remove(partyId);
			}
		}

		return resultObject;
	}

	@RequestMapping(action + "test")
	public Object test(){
		return "test";
	}

	private boolean isDuring(){
		try{
			String withdrawal_time = this.sysparaService.find("withdrawal_time").getValue();
			if(null == withdrawal_time){
				return true;
			}
			String[] split = withdrawal_time.split("-");
			if(split.length < 2){
				return true;
			}
			LocalDateTime startTime = LocalDateTime.of(LocalDate.now(), LocalTime.parse(split[0]));
			LocalDateTime endTime = LocalDateTime.of(LocalDate.now(), LocalTime.parse(split[1]));
			LocalDateTime now = LocalDateTime.now();
			return (now.isAfter(startTime) && now.isBefore(endTime));
		}catch (Exception ex){
			ex.printStackTrace();
			return false;
		}
	}

	/**
	 * 提现订单详情
	 * 
	 * order_no 订单号
	 */
	@RequestMapping(action + "get.action")
	public Object get(HttpServletRequest request) throws IOException {
		String order_no = request.getParameter("order_no");

		ResultObject resultObject = new ResultObject();

		try {

			Withdraw withdraw = this.withdrawService.findByOrderNo(order_no);

			Map<String, Object> map = new HashMap<String, Object>();
			String coinType = "USDT";
			if (withdraw.getMethod().indexOf("BTC") != -1
					|| withdraw.getMethod().indexOf("ETH") != -1) {
				coinType = withdraw.getMethod();
			}

			map.put("order_no", withdraw.getOrder_no());
			// 原始充值币种下的金额
			map.put("volume", withdraw.getVolume());
			// 实际到账金额，保持原始提现币种
			if (coinType.equalsIgnoreCase("USDT")
					|| coinType.equalsIgnoreCase("bank")
					|| coinType.equalsIgnoreCase("USDC")) {
				map.put("amount", withdraw.getAmount());
			} else {
				map.put("amount", withdraw.getArrivalAmount());
			}

			map.put("create_time", DateUtils.format(withdraw.getCreateTime(), DateUtils.DF_yyyyMMddHHmmss));
			map.put("to", withdraw.getAddress());
			map.put("fee", withdraw.getAmount_fee());
			map.put("coin_blockchain", withdraw.getMethod());
			map.put("coin", coinType);
			map.put("state", withdraw.getSucceeded());
			map.put("tx", withdraw.getTx());
			map.put("countryName", withdraw.getCountryName());

			if (withdraw.getMethod().equalsIgnoreCase("bank")) {
				String bank = withdraw.getBank();
				String bankCardNo = withdraw.getAccount();
				String bankUserName = withdraw.getUsername();
				String swiftCode = withdraw.getSwiftCode();
				String withDrawTo = bankUserName;
				withDrawTo = withDrawTo + "," + bankCardNo;
				withDrawTo = withDrawTo + "," + bank;

				map.put("to", withDrawTo);
				map.put("bank", bank);
				map.put("bankCardNo", bankCardNo);
				map.put("bankUserName", bankUserName);
				map.put("swiftCode", swiftCode);
			}

			resultObject.setData(map);

		} catch (BusinessException e) {
			resultObject.setCode("1");
			resultObject.setMsg(e.getMessage());
		} catch (Throwable t) {
			resultObject.setCode("1");
			resultObject.setMsg("程序错误");
			logger.error("error:", t);
		}
		
		return resultObject;
	}

	/**
	 * 提现记录
	 */
	@RequestMapping(action + "list.action")
	public Object list(HttpServletRequest request) throws IOException {
		String page_no = request.getParameter("page_no");
		String page_size = request.getParameter("page_size");

		ResultObject resultObject = new ResultObject();
		resultObject = this.readSecurityContextFromSession(resultObject);
		if (!"0".equals(resultObject.getCode())) {
			return resultObject;
		}

		try {
			if (StringUtils.isNullOrEmpty(page_no)) {
				page_no = "1";
			}
			if (StringUtils.isNullOrEmpty(page_size)) {
				page_size = "10";
			}
			if (!StringUtils.isInteger(page_no)) {
				throw new BusinessException("页码不是整数");
			}
			if (Integer.valueOf(page_no).intValue() <= 0) {
				throw new BusinessException("页码不能小于等于0");
			}

			int page_no_int = Integer.valueOf(page_no).intValue();

			Page pageInfo = this.walletLogService.pagedQueryWithdraw(page_no_int, Integer.parseInt(page_size), this.getLoginPartyId(), "1");
			List<Map<String, Object>> data = pageInfo.getElements();
			for (Map<String, Object> log : data) {
				String coinType = Constants.WALLET;
				if (log.get("coin") != null && StrUtil.isNotBlank(log.get("coin").toString())) {
					coinType = log.get("coin").toString().toUpperCase();
				}
				log.put("coin", coinType);

				if (!coinType.equalsIgnoreCase("USDT")
						&& !coinType.equalsIgnoreCase("bank")
						&& !coinType.equalsIgnoreCase("USDC")) {
					// 后来新增的字段，为了兼容展示早期记录，有汇率换算的币种，实际到账金额使用该字段值
					log.put("amount", log.get("arrivalAmount"));
					log.remove("arrivalAmount");
				}

				String withDrawChannel = (String)log.get("coin_blockchain");
				if ("bank".equalsIgnoreCase(withDrawChannel)) {
					String withDrawTo = (String)log.get("bankUserName");
					withDrawTo = withDrawTo + "," + (String)log.get("bankCardNo");
					withDrawTo = withDrawTo + "," + (String)log.get("bank");

					log.put("to", withDrawTo);
				}
			}
			
			resultObject.setData(pageInfo);
		} catch (BusinessException e) {
			resultObject.setCode("1");
			resultObject.setMsg(e.getMessage());
		} catch (Throwable t) {
			resultObject.setCode("1");
			resultObject.setMsg("程序错误");
			logger.error("error:", t);
		}
		
		return resultObject;
	}

	/**
	 * 提现手续费
	 * 
	 * channel 渠道 USDT,OTC
	 * amount 提币数量
	 */
	@RequestMapping(action + "fee.action")
	public Object fee(HttpServletRequest request) throws IOException {
		String channel = request.getParameter("channel");
//		String amount = request.getParameter("amount");
	    String exchangeCurrency = request.getParameter("exchange_currency");
		if(Strings.isNullOrEmpty(exchangeCurrency)){
			// 如果没有传递交易币种，默认为USD
			exchangeCurrency = "USD";
		}
		ResultObject resultObject = new ResultObject();

		try {

//			String error = this.verif(amount);
//			if (!StringUtils.isNullOrEmpty(error)) {
//				throw new BusinessException(error);
//			}
//
//			double amount_double = Double.valueOf(amount).doubleValue();

			Map<String, Object> map = new HashMap<String, Object>();
			
			DecimalFormat df = new DecimalFormat("#.##");

			double withdraw_fee = 0;
			
			channel = StringUtils.isEmptyString(channel) ? "USDT" : channel;
			if (channel.indexOf("BTC") != -1 || channel.indexOf("ETH") != -1) {
//				fee = this.withdrawService.getOtherChannelWithdrawFee(amount_double);
				if ("BTC".equals(channel)) {
					withdraw_fee = Double.valueOf(this.sysparaService.find("withdraw_other_channel_fee_part_btc").getValue());
				}else if("ETH".equals(channel)){
					withdraw_fee = Double.valueOf(this.sysparaService.find("withdraw_other_channel_fee_part_eth").getValue());
				}
				withdraw_fee = Arith.div(withdraw_fee, 100);
			} else {
				// 手续费(USDT)

				// 提现手续费类型,fixed是单笔固定金额，rate是百分比，part是分段
//				String withdraw_fee_type = this.sysparaService.find("withdraw_fee_type").getValue();

				// fixed单笔固定金额 和 rate百分比 的手续费数值
				if ("bank".equals(channel)) {
					withdraw_fee = Double.valueOf(this.sysparaService.find("withdraw_other_channel_fee_part_bank").getValue());
				}else {
					withdraw_fee = Double.valueOf(this.sysparaService.find("withdraw_fee").getValue());
				}

				withdraw_fee = Arith.div(withdraw_fee, 100);
//				fee = Arith.mul(amount_double, withdraw_fee);

//				if ("fixed".equals(withdraw_fee_type)) {
//					fee = withdraw_fee;
//				}
//
//				if ("rate".equals(withdraw_fee_type)) {
//					withdraw_fee = Arith.div(withdraw_fee, 100);
//					fee = Arith.mul(amount_double, withdraw_fee);
//				}
//
//				if ("part".equals(withdraw_fee_type)) {
//
//					// 提现手续费part分段的值
//					String withdraw_fee_part = this.sysparaService.find("withdraw_fee_part").getValue();
//
//					String[] withdraw_fee_parts = withdraw_fee_part.split(",");
//					for (int i = 0; i < withdraw_fee_parts.length; i++) {
//						double part_amount = Double.valueOf(withdraw_fee_parts[i]);
//						double part_fee = Double.valueOf(withdraw_fee_parts[i + 1]);
//						if (amount_double <= part_amount) {
//							fee = part_fee;
//							break;
//						}
//						i++;
//					}
//				}
			}
			
//			double volume_last = Arith.sub(amount_double, fee);
//			if (volume_last < 0) {
//				volume_last = 0;
//			}
			double exchangeRate ;
			if ("USDT".equals(exchangeCurrency) || "USD".equals(exchangeCurrency)) {
				exchangeRate = 1;
			}else{
				exchangeRate = this.exchangeRateService.getExchangeRate(exchangeCurrency, "USD");
			}
			map.put("withdraw_fee", withdraw_fee);
			map.put("exchange_rate", exchangeRate);
			resultObject.setData(map);

		} catch (BusinessException e) {
			resultObject.setCode("1");
			resultObject.setMsg(e.getMessage());
		} catch (Throwable t) {
			resultObject.setCode("1");
			resultObject.setMsg("程序错误");
			logger.error("error:", t);
		}
		
		return resultObject;
	}

	/**
	 * 提现限额
	 * 
	 * channel 渠道 USDT,OTC
	 */
	@RequestMapping(action + "limit.action")
	public Object limit(HttpServletRequest request) throws IOException {
		String channel = request.getParameter("channel");

		ResultObject resultObject = new ResultObject();

		try {

			Map<String, Object> map = new HashMap<String, Object>();

			channel = StringUtils.isEmptyString(channel) ? "USDT" : channel;
			if (channel.indexOf("BTC") != -1) {				
				map.put("limit", this.sysparaService.find("withdraw_limit_btc").getValue());
			} else if (channel.indexOf("ETH") != -1) {			
				map.put("limit", this.sysparaService.find("withdraw_limit_eth").getValue());
			} else {
				map.put("limit", this.sysparaService.find("withdraw_limit").getValue());
			}
			
			resultObject.setData(map);

		} catch (BusinessException e) {
			resultObject.setCode("1");
			resultObject.setMsg(e.getMessage());
		} catch (Throwable t) {
			resultObject.setCode("1");
			resultObject.setMsg("程序错误");
			logger.error("error:", t);
		}
		
		return resultObject;
	}

	private String verif(String amount) {
		
		if (StringUtils.isNullOrEmpty(amount)) {
			return "提币数量必填";
		}
		if (!StringUtils.isDouble(amount)) {
			return "提币数量输入错误，请输入浮点数";
		}
		if (Double.valueOf(amount).doubleValue() <= 0) {
			return "提币数量不能小于等于0";
		}

		return null;
	}

}
