package project.withdraw.internal;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import cn.hutool.core.util.StrUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

import kernel.exception.BusinessException;
import kernel.util.Arith;
import kernel.util.DateUtils;
import kernel.util.StringUtils;
import project.Constants;
import project.hobi.HobiDataService;
import project.log.MoneyLog;
import project.log.MoneyLogService;
import project.party.PartyService;
import project.party.model.Party;
import project.syspara.SysparaService;
import project.tip.TipConstants;
import project.tip.TipService;
import project.user.QRGenerateService;
import project.user.UserData;
import project.user.UserDataService;
import project.user.kyc.Kyc;
//import project.user.kyc.KycHighLevel;
//import project.user.kyc.KycHighLevelService;
import project.user.kyc.KycService;
//import project.user.payment.PaymentMethodService;
import project.wallet.Wallet;
import project.wallet.WalletExtend;
import project.wallet.WalletLog;
import project.wallet.WalletLogService;
import project.wallet.WalletService;
import project.wallet.rate.ExchangeRateService;
import project.withdraw.Withdraw;
import project.withdraw.WithdrawService;
import util.DateUtil;
import util.RandomUtil;

public class WithdrawServiceImpl extends HibernateDaoSupport implements WithdrawService {

	private Logger logger = LogManager.getLogger(WithdrawServiceImpl.class);

	protected SysparaService sysparaService;

	protected WalletService walletService;
	protected MoneyLogService moneyLogService;

	protected ExchangeRateService exchangeRateService;

//	protected PaymentMethodService paymentMethodService;

	protected WalletLogService walletLogService;

	protected QRGenerateService qRGenerateService;

	protected UserDataService userDataService;

	protected PartyService partyService;

	protected KycService kycService;
//	protected KycHighLevelService kycHighLevelService;

	protected TipService tipService;

	protected HobiDataService hobiDataService;

	@Override
	public void saveApply(Withdraw withdraw, String channel, String method_id) {
		withdraw.setMethod(channel);
		if (channel.indexOf("BTC") != -1) {
			saveApplyOtherChannel(withdraw, "btc");
			return;
		} else if (channel.indexOf("ETH") != -1) {
			saveApplyOtherChannel(withdraw, "eth");
			return;
		}

		Party party = this.partyService.cachePartyBy(withdraw.getPartyId(), false);
		if (Constants.SECURITY_ROLE_TEST.equals(party.getRolename())) {
			throw new BusinessException(1, "无权限");
		}
		Kyc party_kyc = this.kycService.get(withdraw.getPartyId().toString());

//		KycHighLevel party_kycHighLevel = this.kycHighLevelService.get(withdraw.getPartyId().toString());

		if (!(party_kyc.getStatus() == 2) && "true".equals(sysparaService.find("withdraw_by_kyc").getValue())) {
			throw new BusinessException(401, "无权限");
		}

		if (!party.getWithdraw_authority()) {
			throw new BusinessException(1, "无权限");
		}
		if (!party.getEnabled()) {
			throw new BusinessException("业务已锁定，请联系客服！");
		}

		Wallet wallet = walletService.saveWalletByPartyId(withdraw.getPartyId());

		double money = wallet.getMoney();

		if (wallet.getFrozenState() == 1){
			money = wallet.getMoneyAfterFrozen();
		}

		if (money < withdraw.getVolume()) {
			throw new BusinessException(1, "余额不足");
		}

		// 手续费(USDT)
		/**
		 * 提现手续费类型,fixed是单笔固定金额，rate是百分比，part是分段
		 */
//		String withdraw_fee_type = sysparaService.find("withdraw_fee_type").getValue();
		String withdraw_fee_type = "rate";

		/**
		 * fixed单笔固定金额 和 rate百分比 的手续费数值
		 */
		double withdraw_fee = 0D;
		if ("bank".equals(channel)) {
			withdraw_fee = Double.valueOf(sysparaService.find("withdraw_other_channel_fee_part_bank").getValue());
		}else {
			withdraw_fee = Double.valueOf(sysparaService.find("withdraw_fee").getValue());
		}


		double fee = 0;
		if ("fixed".equals(withdraw_fee_type)) {
			fee = withdraw_fee;
		} else if ("rate".equals(withdraw_fee_type)) {
			withdraw_fee = Arith.div(withdraw_fee, 100);
			fee = Arith.mul(withdraw.getVolume(), withdraw_fee);
		} else if ("part".equals(withdraw_fee_type)) {
			/**
			 * 提现手续费part分段的值
			 */
			String withdraw_fee_part = sysparaService.find("withdraw_fee_part").getValue();

			String[] withdraw_fee_parts = withdraw_fee_part.split(",");
			for (int i = 0; i < withdraw_fee_parts.length; i++) {
				double part_amount = Double.valueOf(withdraw_fee_parts[i]);
				double part_fee = Double.valueOf(withdraw_fee_parts[i + 1]);
				if (withdraw.getVolume() <= part_amount) {
					fee = part_fee;
					break;
				}
				i++;
			}
		}

		withdraw.setAmount_fee(fee);

		// 实际到账金额
		withdraw.setAmount(Arith.sub(withdraw.getVolume(), fee));
		withdraw.setArrivalAmount(Arith.sub(withdraw.getVolume(), fee));

		if (channel.indexOf("USDT") >= 0 || channel.indexOf("USDC") >= 0) {
			withdraw.setMethod(channel);
		} else if (channel.indexOf("bank") != -1) {
			// 银行卡提现
			withdraw.setMethod(channel);
		} else if ("OTC".equals(channel)) {
			throw new BusinessException(1, "渠道未开通");
		} else {
			throw new BusinessException(1, "渠道未开通");
		}
		if ("".equals(withdraw.getOrder_no()) || withdraw.getOrder_no() == null) {
			withdraw.setOrder_no(DateUtil.getToday("yyMMddHHmmss") + RandomUtil.getRandomNum(8));
		}

		withdraw.setCreateTime(new Date());
		String withdraw_qr = "";
		/**
		 * 生成二维码图片
		 */
		if (StringUtils.isNotEmpty(channel) && !channel.equals("bank")){
			withdraw_qr = qRGenerateService.generateWithdraw(withdraw.getOrder_no(), withdraw.getAddress());
		}

		withdraw.setQdcode(withdraw_qr);

		double amount_before = wallet.getMoney();

		if (wallet.getFrozenState() == 1){
			amount_before = wallet.getMoneyAfterFrozen();
			wallet.setMoneyAfterFrozen(Arith.roundDown(Arith.sub(wallet.getMoneyAfterFrozen(), withdraw.getVolume()),2));
		} else {
			wallet.setMoney(Arith.roundDown(Arith.sub(wallet.getMoney(), withdraw.getVolume()),2));
		}
		walletService.update(wallet);

		this.getHibernateTemplate().save(withdraw);

		/*
		 * 保存资金日志
		 */
		MoneyLog moneyLog = new MoneyLog();
		// 银行卡提现时，此处是否需要更改其值？TODO
		moneyLog.setCategory(Constants.MONEYLOG_CATEGORY_COIN);
		moneyLog.setAmount_before(amount_before);
		moneyLog.setAmount(Arith.sub(0, withdraw.getVolume()));

		if (wallet.getFrozenState() == 1){
			moneyLog.setAmount_after(wallet.getMoneyAfterFrozen());
			moneyLog.setFreeze(1);
		} else {
			moneyLog.setAmount_after(wallet.getMoney());
		}

		moneyLog.setLog("提现订单[" + withdraw.getOrder_no() + "]");
		// moneyLog.setExtra(withdraw.getOrder_no());
		moneyLog.setPartyId(withdraw.getPartyId());
		moneyLog.setWallettype(Constants.WALLET);
		moneyLog.setContent_type(Constants.MONEYLOG_CONTENT_WITHDRAW);

		moneyLogService.save(moneyLog);

		/*
		 * 保存资金日志
		 */
		WalletLog walletLog = new WalletLog();
		walletLog.setCategory("withdraw");
		walletLog.setPartyId(withdraw.getPartyId());
		walletLog.setOrder_no(withdraw.getOrder_no());
		walletLog.setStatus(withdraw.getSucceeded());
		walletLog.setAmount(withdraw.getVolume());
		// 换算成USDT单位
		walletLog.setUsdtAmount(withdraw.getVolume());
		walletLog.setWallettype("bank".equals(withdraw.getMethod())?Constants.WALLETEXTEND_USD:Constants.WALLET);
		walletLogService.save(walletLog);

		tipService.saveTip(withdraw.getId().toString(), TipConstants.WITHDRAW);
	}

//	private void checkWithdrawLimit(Party party, Kyc kyc, KycHighLevel kycHighLevel, double withdrawVolumn) {
	private void checkWithdrawLimit(Party party, Kyc kyc, double withdrawVolumn) {
		
		double limit = 0d;
		// 特殊人员不受限制(只有在周提现限制开启后有效)
		String unLimitUid = sysparaService.find("withdraw_week_unlimit_uid").getValue();
		if (StringUtils.isNotEmpty(unLimitUid)) {
			String[] unLimitUisArr = unLimitUid.split(",");
			if (Arrays.asList(unLimitUisArr).contains(party.getUsercode())) {
				return;
			}
		}
//		if (kycHighLevel.getStatus() == 2) {
//			// 基础认证可提现额度
//			limit = sysparaService.find("withdraw_week_limit_kyc_high").getDouble();
//		} else 
		if (kyc.getStatus() == 2) {
			// 高级基础认证每周可提现额度
			limit = sysparaService.find("withdraw_week_limit_kyc").getDouble();
		}
		if (limit > 0) {
			/**
			 * 已用额度
			 */
			double weekWithdraw = weekWithdraw(party.getId().toString());
			if (Arith.add(weekWithdraw, withdrawVolumn) > limit) {
				throw new BusinessException(1, "提现不得大于限额");
			}
		}
	}

	/**
	 * 当周已使用额度
	 * 
	 * @param partyId
	 * @return
	 */
	public double weekWithdraw(String partyId) {
		Map<String, UserData> map = userDataService.cacheByPartyId(partyId);
		Date now = new Date();
		String endTime = DateUtils.getDateStr(new Date());
		String startTime = DateUtils.getDateStr(DateUtils.addDay(now, -6));
		// 一周内已用额度
		double withdrawMoney = withdrawMoney(map, startTime, endTime);
		return withdrawMoney;
//		double remain = Arith.sub(maxLimit, withdrawMoney);
//		if (Arith.add(withdrawMoney, withdrawVolumn) > maxLimit) {
//			throw new BusinessException("提现不得大于限额");
//		}
	}

	/**
	 * 时间范围内的充值总额
	 * 
	 * @param datas
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	private double withdrawMoney(Map<String, UserData> datas, String startTime, String endTime) {
		if (datas == null || datas.isEmpty())
			return 0;
		double userWithdraw = 0;
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
			userWithdraw = Arith.add(userdata.getWithdraw(), userWithdraw);
		}

		return userWithdraw;
	}

	public void saveApplyOtherChannel(Withdraw withdraw, String symbol) {
		Party party = this.partyService.cachePartyBy(withdraw.getPartyId(), false);
		if (Constants.SECURITY_ROLE_TEST.equals(party.getRolename())) {
			throw new BusinessException("无权限");
		}
		Kyc party_kyc = this.kycService.get(withdraw.getPartyId().toString());

//		KycHighLevel party_kycHighLevel = this.kycHighLevelService.get(withdraw.getPartyId().toString());

		if (!(party_kyc.getStatus() == 2) && "true".equals(sysparaService.find("withdraw_by_kyc").getValue())) {
			throw new BusinessException(401, "无权限");
		}
//		double withdraw_by_high_kyc = Double.valueOf(sysparaService.find("withdraw_by_high_kyc").getValue());
//
//		if (withdraw_by_high_kyc > 0 && withdraw.getVolume() > withdraw_by_high_kyc
//				&& !(party_kycHighLevel.getStatus() == 2)) {
//			throw new BusinessException(1, "请先通过高级认证");
//		}

		if (!party.getWithdraw_authority()) {
			throw new BusinessException(1, "无权限");
		}
		if (!party.getEnabled()) {
			throw new BusinessException(506, "无权限");
		}

		double usdtAmount = compute2UsdtAmount(withdraw.getVolume(), symbol);

		// caster 2023-8-3 注释掉该逻辑，使用 USDT 钱包余额来做校验
		WalletExtend walletExtend = walletService.saveExtendByPara(party.getId(), symbol);
//		if (walletExtend.getAmount() < usdtAmount) {
//			throw new BusinessException(1, "余额不足");
//		}

		// 2023-8-3 caster 使用本方式判断余额
		Wallet wallet = walletService.saveWalletByPartyId(withdraw.getPartyId());
		if (wallet.getMoney() < usdtAmount) {
			throw new BusinessException(1, "余额不足");
		}

		String withdraw_limit = sysparaService.find("withdraw_limit_" + symbol).getValue();
		if (usdtAmount < Double.valueOf(withdraw_limit)) {
			throw new BusinessException(1, "提现不得小于限额");
		}
		String withdraw_limit_max = sysparaService.find("withdraw_limit_max").getValue();
		if (usdtAmount > Double.valueOf(withdraw_limit_max)) {
			throw new BusinessException(1, "提现不得大于限额");
		}

		/**
		 * 当日提现次数是否超过
		 */
		double withdraw_limit_num = Double.valueOf(sysparaService.find("withdraw_limit_num").getValue());
		List<Withdraw> withdraw_days = findAllByDate(withdraw.getPartyId().toString());
		if (withdraw_limit_num > 0 && withdraw_days != null) {
			if (withdraw_days.size() >= withdraw_limit_num) {
				throw new BusinessException(1, "当日可提现次数不足");
			}
		}

		/**
		 * 是否在当日提现时间内
		 */
		SimpleDateFormat sdf = new SimpleDateFormat();// 格式化时间
		sdf.applyPattern("HH:mm:ss");// a为am/pm的标记
		Date date = new Date();// 获取当前时间
		String withdraw_limit_time = sysparaService.find("withdraw_limit_time").getValue();
		if (!"".equals(withdraw_limit_time) && withdraw_limit_time != null) {
			String[] withdraw_time = withdraw_limit_time.split("-");
			//
			String dateString = sdf.format(date);
			if (dateString.compareTo(withdraw_time[0]) < 0 || dateString.compareTo(withdraw_time[1]) > 0) {
				throw new BusinessException(1, "不在可提现时间内");
			}
		}

		/**
		 * 可提现差额开启 取party Withdraw_limit_amount 的可提现金 和剩余金额与流水中的最小值相加
		 * 流水为Userdate里的交割，合约，理财，矿池的交易量
		 */
		String withdraw_limit_open = sysparaService.find("withdraw_limit_open").getValue();
		if ("true".equals(withdraw_limit_open)) {

			// 提现限制流水开启后，提现判断用的用户当前流水是使用UserData表的当日流水1还是使用Party表里的用户当前流水2
			String withdraw_limit_open_use_type = sysparaService.find("withdraw_limit_open_use_type").getValue();
			// 当使用userdata流水提现时，提现限制流水是否加入永续合约流水1增加，2不增加
			String withdraw_limit_contract_or = sysparaService.find("withdraw_limit_contract_or").getValue();

			if ("1".equals(withdraw_limit_open_use_type)) {
				/**
				 * 还差多少可提现金额
				 */
				double fact_withdraw_amount = 0;
				/**
				 * 用户Party表里可提现金额参数 -----可为负数
				 */
				double party_withdraw = party.getWithdraw_limit_amount();
				/**
				 * usdt剩余余额
				 */
				// double last_usdt_amount = wallet.getMoney();
				/**
				 * userdata交易流水
				 */
				double userdata_turnover = 0;
//				Map<String, UserData> data_all = this.userDataService.getCache().get(withdraw.getPartyId());
				Map<String, UserData> data_all = this.userDataService.cacheByPartyId(withdraw.getPartyId().toString());
				if (data_all != null) {
					SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");
					Date date_now = new Date();
					for (Entry<String, UserData> valueEntry : data_all.entrySet()) {
						UserData userdata = valueEntry.getValue();
						// 如果日期等于当天就赋值
						if (fmt.format(date_now).equals(fmt.format(userdata.getCreateTime()))) {
							/**
							 * 永续合约下单金额amount 理财买入金额finance_amount 币币exchange_amount 矿机下单金额miner_amount
							 * 交割合约下单金额furtures_amount
							 */
							// 当使用userdata流水提现时，提现限制流水是否加入永续合约流水1增加，2不增加
							double contract_amount = userdata.getAmount();
							if ("2".equals(withdraw_limit_contract_or)) {
								contract_amount = 0;
							}
							double amount_finance_amount = Arith.add(contract_amount, userdata.getFinance_amount());
//							币币交易流水不加入
							double exchange_amount_miner_amount = Arith.add(0, userdata.getMiner_amount());

							userdata_turnover = Arith.add(userdata.getFurtures_amount(),
									Arith.add(amount_finance_amount, exchange_amount_miner_amount));
						}
					}
				}
				double withdraw_limit_turnover_percent = Double
						.valueOf(sysparaService.find("withdraw_limit_turnover_percent").getValue());
				party_withdraw = Arith.mul(party_withdraw, withdraw_limit_turnover_percent);
				// 流水小于限额
				if (userdata_turnover < party_withdraw) {
					fact_withdraw_amount = Arith.sub(party_withdraw, userdata_turnover);
					throw new BusinessException(105, fact_withdraw_amount + "");
				}
			}

			if ("2".equals(withdraw_limit_open_use_type)) {
				/**
				 * 还差多少可提现金额
				 */
				double fact_withdraw_amount = 0;
				/**
				 * 用户Party表里可提现金额参数 -----可为负数
				 */
				double party_withdraw = party.getWithdraw_limit_amount();
				/**
				 * usdt剩余余额
				 */
				// double last_usdt_amount = wallet.getMoney();
				/**
				 * userdata交易流水
				 */
				double userdata_turnover = party.getWithdraw_limit_now_amount();

//				
				double withdraw_limit_turnover_percent = Double
						.valueOf(sysparaService.find("withdraw_limit_turnover_percent").getValue());
				party_withdraw = Arith.mul(party_withdraw, withdraw_limit_turnover_percent);
				// 流水小于限额
				if (userdata_turnover < party_withdraw) {
					fact_withdraw_amount = Arith.sub(party_withdraw, userdata_turnover);
					throw new BusinessException(105, fact_withdraw_amount + "");
				}
			}
		}

		// 计算手续费
		double usdtFee = getOtherChannelWithdrawFee(usdtAmount,symbol);
		withdraw.setAmount_fee(usdtFee);
		// 换算成 USDT 币种后的实际到账资金
		withdraw.setAmount(Arith.sub(usdtAmount, usdtFee));

		// 对应提现币种下的手续费
		double coinTypeFee = computeUsdt2Amount(usdtFee, symbol);
		withdraw.setArrivalAmount(withdraw.getVolume() - coinTypeFee);

		if ("".equals(withdraw.getOrder_no()) || withdraw.getOrder_no() == null) {
			withdraw.setOrder_no(DateUtil.getToday("yyMMddHHmmss") + RandomUtil.getRandomNum(8));
		}

		withdraw.setCreateTime(new Date());

		/**
		 * 生成二维码图片
		 */
		String withdraw_qr = qRGenerateService.generateWithdraw(withdraw.getOrder_no(), withdraw.getAddress());

		withdraw.setQdcode(withdraw_qr);

		// 注释旧代码
		// double amount_before = walletExtend.getAmount();
		double amount_before = wallet.getMoney();

		// 这段代码和对应的表在充值提现业务中没看出意义所在，暂时保留
		walletService.updateExtend(walletExtend.getPartyId().toString(), symbol, Arith.sub(0, withdraw.getVolume()));
		this.getHibernateTemplate().save(withdraw);

//		wallet.setMoney(Arith.sub(wallet.getMoney(), usdtAmount));
		walletService.update(wallet.getPartyId().toString(), Arith.sub(0, usdtAmount));

		/*
		 * 保存资金日志
		 */
		MoneyLog moneyLog = new MoneyLog();
		moneyLog.setCategory(Constants.MONEYLOG_CATEGORY_COIN);
		// 存的是 USDT 的金额
		moneyLog.setAmount_before(amount_before);
		// 存的是 USDT 的金额，修改
		moneyLog.setAmount(Arith.sub(0, usdtAmount));
		// 修改
		moneyLog.setAmount_after(Arith.sub(amount_before, usdtAmount));

		moneyLog.setLog("提现订单[" + withdraw.getOrder_no() + "]");
		// moneyLog.setExtra(withdraw.getOrder_no());
		moneyLog.setPartyId(withdraw.getPartyId());
		// 2023-8-3 caster 修改为 USDT，查看其他币种充值时，moneyLog 记录使用的也是 USDT 单位来存储数据
		moneyLog.setWallettype(Constants.WALLET);
		// moneyLog.setWallettype(symbol.toUpperCase());
		moneyLog.setContent_type(Constants.MONEYLOG_CONTENT_WITHDRAW);

		moneyLogService.save(moneyLog);

		/*
		 * 保存资金日志
		 */
		WalletLog walletLog = new WalletLog();
		walletLog.setCategory("withdraw");
		walletLog.setPartyId(withdraw.getPartyId());
		walletLog.setOrder_no(withdraw.getOrder_no());
		walletLog.setStatus(withdraw.getSucceeded());
		walletLog.setAmount(withdraw.getVolume());
		// 换算成USDT单位
		walletLog.setUsdtAmount(usdtAmount);
		walletLog.setWallettype(symbol.toUpperCase());
		walletLogService.save(walletLog);

		tipService.saveTip(withdraw.getId().toString(), TipConstants.WITHDRAW);
	}

	/**
	 * 获取其他通道的手续费
	 * 
	 * @param volume 提现数量
	 * @return
	 */
	public double getOtherChannelWithdrawFee(double volume,String symbol) {
		/**
		 * 提现手续费part分段的值
		 */
		String withdraw_fee_part = null;
		if ("btc".equals(symbol)) {
			withdraw_fee_part = sysparaService.find("withdraw_other_channel_fee_part_btc").getValue();
		}else if("eth".equals(symbol)){
			withdraw_fee_part = sysparaService.find("withdraw_other_channel_fee_part_eth").getValue();
		}
//		之前的实现方式为 分段 参数为   '1000,0.03,999999999999,0.03'，现在改为固定费率不分段
//		double fee = 0;
		/*String[] withdraw_fee_parts = withdraw_fee_part.split(",");
		for (int i = 0; i < withdraw_fee_parts.length; i++) {
			double part_amount = Double.valueOf(withdraw_fee_parts[i]);
			double part_fee = Double.valueOf(withdraw_fee_parts[i + 1]);
			if (volume <= part_amount) {
				fee = Arith.mul(part_fee, volume);
				break;
			}
			i++;
		}*/
		double fee = Arith.mul(volume,Arith.div(Double.valueOf(withdraw_fee_part),100));

		return Arith.roundDown(fee, 6);
	}

	@Override
	public Withdraw findByOrderNo(String order_no) {
		StringBuffer queryString = new StringBuffer(" FROM Withdraw where order_no=?0");
		List<Withdraw> list = (List<Withdraw>) getHibernateTemplate().find(queryString.toString(), new Object[] { order_no });
		if (list.size() > 0) {
			return list.get(0);
		}
		return null;
	}

	public boolean saveReject(Withdraw withdraw) {
		if (withdraw.getSucceeded() == 2 || withdraw.getSucceeded() == 1) {// 通过后不可驳回
			return false;
		}
		withdraw.setSucceeded(2);
		String symbol = "";
		if (withdraw.getMethod().indexOf("BTC") != -1) {
			symbol = "btc";
		} else if (withdraw.getMethod().indexOf("ETH") != -1) {
			symbol = "eth";
		} else {
			symbol = "usdt";
		}

		if ("usdt".equals(symbol)) {
			Wallet wallet = walletService.saveWalletByPartyId(withdraw.getPartyId());
			double amount_before = wallet.getMoney();
			walletService.update(wallet.getPartyId().toString(),
					Arith.add(withdraw.getAmount(), withdraw.getAmount_fee()));

			/*
			 * 保存资金日志
			 */
			MoneyLog moneyLog = new MoneyLog();
			moneyLog.setCategory(Constants.MONEYLOG_CATEGORY_COIN);
			moneyLog.setAmount_before(amount_before);
			moneyLog.setAmount(Arith.add(withdraw.getAmount(), withdraw.getAmount_fee()));
			moneyLog.setAmount_after(Arith.add(amount_before, Arith.add(withdraw.getAmount(), withdraw.getAmount_fee())));

			moneyLog.setLog("驳回提现[" + withdraw.getOrder_no() + "]");
			// moneyLog.setExtra(withdraw.getOrder_no());
			moneyLog.setPartyId(withdraw.getPartyId());
			moneyLog.setWallettype(Constants.WALLET);
			moneyLog.setContent_type(Constants.MONEYLOG_CONTENT_WITHDRAW);

			moneyLogService.save(moneyLog);

		} else {
			WalletExtend walletExtend = walletService.saveExtendByPara(withdraw.getPartyId(), symbol);
			double amount_before = walletExtend.getAmount();
			walletService.updateExtend(withdraw.getPartyId().toString(), symbol, withdraw.getVolume());

			/*
			 * 保存资金日志
			 */
			MoneyLog moneyLog = new MoneyLog();
			moneyLog.setCategory(Constants.MONEYLOG_CATEGORY_COIN);
			moneyLog.setAmount_before(amount_before);
			moneyLog.setAmount(withdraw.getVolume());
			moneyLog.setAmount_after(Arith.add(amount_before, withdraw.getVolume()));

			moneyLog.setLog("驳回提现[" + withdraw.getOrder_no() + "]");
			// moneyLog.setExtra(withdraw.getOrder_no());
			moneyLog.setPartyId(withdraw.getPartyId());
			moneyLog.setWallettype(symbol.toUpperCase());
			moneyLog.setContent_type(Constants.MONEYLOG_CONTENT_WITHDRAW);

			moneyLogService.save(moneyLog);
		}
		this.update(withdraw);
		this.walletLogService.updateStatus(withdraw.getOrder_no(), withdraw.getSucceeded());

		return true;

	}

	public List<Withdraw> findAllByDate(String partyId) {
		List<Withdraw> list = (List<Withdraw>) this.getHibernateTemplate()
				.find(" FROM Withdraw WHERE  partyId=?0 AND DateDiff(createTime,NOW())=0  ", new Object[] { partyId });
		return list;
	}

	public List<Withdraw> findAllByStateAndPartyId(int state, String partyId) {
		List<Withdraw> list = (List<Withdraw>) this.getHibernateTemplate().find(" FROM Withdraw WHERE  partyId=?0 AND succeeded=?1  ",
				new Object[] { partyId, state });
		return list;
	}

	@Override
	public List<Withdraw> selectWithdraw(String partyId) {
		return (List<Withdraw>) getHibernateTemplate().find("FROM Withdraw WHERE to_days(CREATE_TIME) = to_days(now()) and partyId=?0 ",
				new Object[] { partyId });
	}

	public void update(Withdraw withdraw) {
		this.getHibernateTemplate().update(withdraw);

	}

	@Override
	public List<Withdraw> selectUnFinishedWithdraw(String partyId) {
		return (List<Withdraw>) getHibernateTemplate().find("FROM Withdraw WHERE partyId=?0 and succeeded=0 ",
				new Object[] { partyId });
	}

	/**
	 * 将指定币种的值转化成 USDT 币种对应的金额.
	 *
	 * @param amount
	 * @param coinType
	 * @return
	 */
	protected double compute2UsdtAmount(double amount, String coinType) {
		if (StrUtil.isBlank(coinType)) {
			throw new BusinessException("参数错误");
		}

		double fee = 0.0;
		if (coinType.equalsIgnoreCase("BTC")) {
			fee = Double.parseDouble(hobiDataService.getSymbolRealPrize("btc"));
		} else if (coinType.equalsIgnoreCase("ETH")) {
			fee = Double.parseDouble(hobiDataService.getSymbolRealPrize("eth"));
		} else {
			// USDT、USDC 币种也支持 ERC20 类型的链，经同事确认也是比率 1:1
			fee = 1;
		}

		return Arith.roundDown(Arith.mul(amount, fee), 6);
	}

	/**
	 * 将 usdt 币种对应的金额转化成指定币种的值.
	 *
	 * @param usdtAmount
	 * @param coinType
	 * @return
	 */
	protected double computeUsdt2Amount(double usdtAmount, String coinType) {
		if (StrUtil.isBlank(coinType)) {
			throw new BusinessException("参数错误");
		}

		double fee = 0.0;
		if (coinType.equalsIgnoreCase("BTC")) {
			fee = Double.parseDouble(hobiDataService.getSymbolRealPrize("btc"));
		} else if (coinType.equalsIgnoreCase("ETH")) {
			fee = Double.parseDouble(hobiDataService.getSymbolRealPrize("eth"));
		} else {
			// USDT、USDC 币种也支持 ERC20 类型的链，经同事确认也是比率 1:1
			fee = 1;
		}

		return Arith.roundDown(Arith.div(usdtAmount, fee), 6);
	}

	public void setSysparaService(SysparaService sysparaService) {
		this.sysparaService = sysparaService;
	}

	public void setWalletService(WalletService walletService) {
		this.walletService = walletService;
	}

	public void setMoneyLogService(MoneyLogService moneyLogService) {
		this.moneyLogService = moneyLogService;
	}

	public void setExchangeRateService(ExchangeRateService exchangeRateService) {
		this.exchangeRateService = exchangeRateService;
	}

//	public void setPaymentMethodService(PaymentMethodService paymentMethodService) {
//		this.paymentMethodService = paymentMethodService;
//	}

	public void setWalletLogService(WalletLogService walletLogService) {
		this.walletLogService = walletLogService;
	}

	public void setqRGenerateService(QRGenerateService qRGenerateService) {
		this.qRGenerateService = qRGenerateService;
	}

//	@Override
//	public boolean saveSucceeded(Withdraw withdraw) {
//		if (withdraw.getSucceeded() == 1) {
//			return false;
//		}
//		withdraw.setSucceeded(1);
//
//		String symbol = "";
//		if (withdraw.getMethod().indexOf("BTC") != -1) {
//			symbol = "btc";
//		} else if (withdraw.getMethod().indexOf("ETH") != -1) {
//			symbol = "eth";
//		} else {
//			symbol = "usdt";
//		}
//
//		this.walletLogService.updateStatus(withdraw.getOrder_no(), withdraw.getSucceeded());
//		/**
//		 * 提现订单加入userdate
//		 */
//		this.userDataService.saveWithdrawHandle(withdraw.getPartyId(), withdraw.getAmount(), withdraw.getAmount_fee(),
//				symbol);
//
//		this.update(withdraw);
//
//		return true;
//	}

	public void setUserDataService(UserDataService userDataService) {
		this.userDataService = userDataService;
	}

	public void setPartyService(PartyService partyService) {
		this.partyService = partyService;
	}

	public void setKycService(KycService kycService) {
		this.kycService = kycService;
	}

//	public void setKycHighLevelService(KycHighLevelService kycHighLevelService) {
//		this.kycHighLevelService = kycHighLevelService;
//	}

	public void setTipService(TipService tipService) {
		this.tipService = tipService;
	}

	public void setHobiDataService(HobiDataService hobiDataService) {
		this.hobiDataService = hobiDataService;
	}

}
