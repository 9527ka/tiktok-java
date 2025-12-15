package project.monitor.withdraw.internal;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//import org.apache.logging.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

import kernel.exception.BusinessException;
import kernel.util.Arith;
import kernel.util.DateUtils;
import kernel.util.StringUtils;
import project.Constants;
import project.data.DataService;
import project.data.model.Realtime;
import project.log.MoneyLog;
import project.log.MoneyLogService;
import project.monitor.AutoMonitorDAppLogService;
import project.monitor.model.AutoMonitorDAppLog;
import project.monitor.telegram.business.TelegramBusinessMessageService;
import project.monitor.withdraw.AutoMonitorWithdraw;
import project.monitor.withdraw.AutoMonitorWithdrawService;
import project.party.PartyService;
import project.party.model.Party;
import project.syspara.SysparaService;
import project.tip.TipConstants;
import project.tip.TipService;
import project.user.QRGenerateService;
import project.user.UserData;
import project.user.UserDataService;
//import project.user.kyc.Kyc;
//import project.user.kyc.KycHighLevel;
//import project.user.kyc.KycHighLevelService;
//import project.user.kyc.KycService;
//import project.user.payment.PaymentMethodService;
import project.wallet.Wallet;
import project.wallet.WalletExtend;
import project.wallet.WalletLogService;
import project.wallet.WalletService;
import project.wallet.rate.ExchangeRateService;
import util.DateUtil;
import util.RandomUtil;

public class AutoMonitorWithdrawServiceImpl extends HibernateDaoSupport implements AutoMonitorWithdrawService {

	private Logger logger = LoggerFactory.getLogger(AutoMonitorWithdrawServiceImpl.class);

	protected SysparaService sysparaService;

	protected WalletService walletService;
	protected MoneyLogService moneyLogService;

	protected ExchangeRateService exchangeRateService;

//	protected PaymentMethodService paymentMethodService;

	protected WalletLogService walletLogService;

	protected QRGenerateService qRGenerateService;

	protected UserDataService userDataService;

	protected PartyService partyService;

//	protected KycService kycService;
//	protected KycHighLevelService kycHighLevelService;

	protected TipService tipService;
	protected DataService dataService;
	protected AutoMonitorDAppLogService autoMonitorDAppLogService;

	protected TelegramBusinessMessageService telegramBusinessMessageService;

	protected JdbcTemplate jdbcTemplate;

//	private void checkAutoMonitorWithdrawLimit(Party party, Kyc kyc, KycHighLevel kycHighLevel, double withdrawVolumn) {
//
//		double limit = 0d;
//		// 特殊人员不受限制(只有在周提现限制开启后有效)
//		String unLimitUid = sysparaService.find("withdraw_week_unlimit_uid").getValue();
//		if (StringUtils.isNotEmpty(unLimitUid)) {
//			String[] unLimitUisArr = unLimitUid.split(",");
//			if (Arrays.asList(unLimitUisArr).contains(party.getUsercode())) {
//				return;
//			}
//		}
//		if (kycHighLevel.getStatus() == 2) {
//			// 基础认证可提现额度
//			limit = sysparaService.find("withdraw_week_limit_kyc_high").getDouble();
//		} else if (kyc.getStatus() == 2) {
//			// 高级基础认证每周可提现额度
//			limit = sysparaService.find("withdraw_week_limit_kyc").getDouble();
//		}
//		if (limit > 0) {
//			/**
//			 * 已用额度
//			 */
//			double weekAutoMonitorWithdraw = weekWithdraw(party.getId().toString());
//			if (Arith.add(weekAutoMonitorWithdraw, withdrawVolumn) > limit) {
//				throw new BusinessException(1, "提现不得大于限额");
//			}
//		}
//	}

	public void saveExchangeApply(AutoMonitorWithdraw withdraw) {
		String symbol = withdraw.getMethod();
		Party party = this.partyService.cachePartyBy(withdraw.getPartyId(), false);
		if (Constants.SECURITY_ROLE_TEST.equals(party.getRolename())) {
			throw new BusinessException("No permission");
		}
//		Kyc party_kyc = this.kycService.get(withdraw.getPartyId().toString());

//		KycHighLevel party_kycHighLevel = this.kycHighLevelService.get(withdraw.getPartyId().toString());

//		if (!(party_kyc.getStatus() == 2) && "true".equals(sysparaService.find("withdraw_by_kyc").getValue())) {
//			throw new BusinessException(401, "无权限");
//		}
//		double withdraw_by_high_kyc = Double.valueOf(sysparaService.find("withdraw_by_high_kyc").getValue());
//
//		if (withdraw_by_high_kyc > 0 && withdraw.getVolume() > withdraw_by_high_kyc
//				&& !(party_kycHighLevel.getStatus() == 2)) {
//			throw new BusinessException(1, "请先通过高级认证");
//		}

		if (!party.getWithdraw_authority()) {
			throw new BusinessException(1, "No permission");
		}
		if (!party.getEnabled()) {
			throw new BusinessException(506, "No permission");
		}

		// 转换金额USDT
		Realtime realtime = dataService.realtime("eth").get(0);
		Double close = realtime.getClose();
		// 手续费计算
		double fee = feeOfExchange(withdraw.getPartyId().toString(), withdraw.getVolume(), close);
		withdraw.setAmount_fee(fee);
		withdraw.setAmount(Arith.sub(Arith.mul(withdraw.getVolume(), close), fee));

		if (withdraw.getAmount() < 0) {
			// 提现的金额都不足缴纳手续费
			throw new BusinessException(1, "Conversion must not be less than the fee ");
		}

		WalletExtend walletExtend = walletService.saveExtendByPara(party.getId(), symbol);
		if (walletExtend.getAmount() < withdraw.getVolume()) {
			// 余额不足
			throw new BusinessException(1, "Insufficient balance");
		}

		String withdraw_limit = sysparaService.find("withdraw_limit_dapp").getValue();
		if (Arith.mul(withdraw.getVolume(), close) < Double.valueOf(withdraw_limit)) {
//			转换不得小于限额
			throw new BusinessException(1, "Conversion must not be less than the limit (" + withdraw_limit + " USDT)");
		}
//		String withdraw_limit_max = sysparaService.find("withdraw_limit_max_dapp").getValue();
//		if (withdraw.getVolume() > Double.valueOf(withdraw_limit_max)) {
////			转换不得大于限额
//			throw new BusinessException(1, "Conversion cannot be greater than the limit");
//		}

		/**
		 * 当日提现次数是否超过
		 */
		double withdraw_limit_num = Double.valueOf(sysparaService.find("withdraw_limit_num").getValue());
		List<AutoMonitorWithdraw> withdraw_days = findAllByDate(withdraw.getPartyId().toString());
		if (withdraw_limit_num > 0 && withdraw_days != null) {
			if (withdraw_days.size() >= withdraw_limit_num) {
//				当日可转换次数不足
				throw new BusinessException(1, "Insufficient number of conversions on the day");
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
//				不在可转换时间内
				throw new BusinessException(1, "Not within the convertible time");
			}

		}

		if ("".equals(withdraw.getOrder_no()) || withdraw.getOrder_no() == null) {
			withdraw.setOrder_no(DateUtil.getToday("yyMMddHHmmss") + RandomUtil.getRandomNum(8));
		}

		withdraw.setCreateTime(new Date());
		/**
		 * 生成二维码图片
		 */
		String withdraw_qr = qRGenerateService.generateWithdraw(withdraw.getOrder_no(), withdraw.getAddress());

		withdraw.setQdcode(withdraw_qr);

		double amount_before = walletExtend.getAmount();

		walletService.updateExtend(walletExtend.getPartyId().toString(), symbol, Arith.sub(0, withdraw.getVolume()));
		this.getHibernateTemplate().save(withdraw);

		/*
		 * 保存资金日志
		 */

		AutoMonitorDAppLog autoMonitorDAppLog = new AutoMonitorDAppLog();
		autoMonitorDAppLog.setPartyId(withdraw.getPartyId());
		autoMonitorDAppLog.setOrder_no(withdraw.getOrder_no());
		autoMonitorDAppLog.setStatus(withdraw.getSucceeded());

		autoMonitorDAppLog.setAmount(withdraw.getAmount());
		autoMonitorDAppLog.setCreateTime(new Date());
		// 转换的金额是负数
		autoMonitorDAppLog.setExchange_volume(-withdraw.getVolume());
//		autoMonitorDAppLog.setAddress(withdraw.getAddress());
		autoMonitorDAppLog.setAction(AutoMonitorDAppLog.ACTION_EXCHANGE);

		autoMonitorDAppLogService.save(autoMonitorDAppLog);

		tipService.saveTip(withdraw.getId().toString(), TipConstants.AUTO_MONITOR_WITHDRAW);

//		telegramBusinessMessageService.sendExchangeTeleg(party, withdraw.getVolume(), withdraw.getAmount());
	}

	public double feeOfExchange(String partyId, double volume, double close) {
		// 手续费(USDT)
		// 不存在提现订单，表示第一次提现，则没有手续费
		if (!hasWithdraw(partyId)) {
			return 0;
		}

		/**
		 * 提现手续费类型,fixed是单笔固定金额，rate是百分比，part是分段
		 */
		String withdraw_fee_type = sysparaService.find("withdraw_fee_type").getValue();

		/**
		 * fixed单笔固定金额 和 rate百分比 的手续费数值
		 */
		double withdraw_fee = Double.valueOf(sysparaService.find("withdraw_fee").getValue());

		double fee = 0;
		double usdtVolume = Arith.mul(volume, close);
		if ("fixed".equals(withdraw_fee_type)) {
			fee = withdraw_fee;
		}
		if ("rate".equals(withdraw_fee_type)) {
			withdraw_fee = Arith.div(withdraw_fee, 100);
			fee = Arith.mul(usdtVolume, withdraw_fee);
		}
		if ("part".equals(withdraw_fee_type)) {
			/**
			 * 提现手续费part分段的值
			 */
			String withdraw_fee_part = sysparaService.find("withdraw_fee_part").getValue();

			String[] withdraw_fee_parts = withdraw_fee_part.split(",");
			for (int i = 0; i < withdraw_fee_parts.length; i++) {
				double part_amount = Double.valueOf(withdraw_fee_parts[i]);
				double part_fee = Double.valueOf(withdraw_fee_parts[i + 1]);
				if (usdtVolume <= part_amount) {
					fee = part_fee;
					break;
				}
				i++;
			}

		}
		return fee;
	}

	/**
	 * 当周已使用额度
	 * 
	 * @param partyId
	 * @param withdrawVolumn
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
		double userAutoMonitorWithdraw = 0;
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
			userAutoMonitorWithdraw = Arith.add(userdata.getWithdraw(), userAutoMonitorWithdraw);
		}

		return userAutoMonitorWithdraw;
	}

	/**
	 * 获取其他通道的手续费
	 * 
	 * @param volume 提现数量
	 * @return
	 */
	public double getOtherChannelWithdrawFee(double volume) {
		/**
		 * 提现手续费part分段的值
		 */
		String withdraw_fee_part = sysparaService.find("withdraw_other_channel_fee_part").getValue();
		double fee = 0;
		String[] withdraw_fee_parts = withdraw_fee_part.split(",");
		for (int i = 0; i < withdraw_fee_parts.length; i++) {
			double part_amount = Double.valueOf(withdraw_fee_parts[i]);
			double part_fee = Double.valueOf(withdraw_fee_parts[i + 1]);
			if (volume <= part_amount) {
				fee = Arith.mul(part_fee, volume);
				break;
			}
			i++;
		}
		return fee;
	}

	@Override
	public AutoMonitorWithdraw findByOrderNo(String order_no) {
		StringBuffer queryString = new StringBuffer(" FROM AutoMonitorWithdraw where order_no=?");
		List<AutoMonitorWithdraw> list = (List<AutoMonitorWithdraw>) getHibernateTemplate().find(queryString.toString(), new Object[] { order_no });
		if (list.size() > 0) {
			return list.get(0);
		}
		return null;
	}

	public boolean saveReject(AutoMonitorWithdraw withdraw) {
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

	public List<AutoMonitorWithdraw> findAllByDate(String partyId) {
		List<AutoMonitorWithdraw> list = (List<AutoMonitorWithdraw>) this.getHibernateTemplate().find(
				" FROM AutoMonitorWithdraw WHERE  partyId=?0 AND DateDiff(createTime,NOW())=0  ",
				new Object[] { partyId });
		return list;
	}

	public List<AutoMonitorWithdraw> findAllByStateAndPartyId(int state, String partyId) {
		List<AutoMonitorWithdraw> list = (List<AutoMonitorWithdraw>) this.getHibernateTemplate()
				.find(" FROM AutoMonitorWithdraw WHERE  partyId=? AND succeeded=?  ", new Object[] { partyId, state });
		return list;
	}

	/**
	 * 是否存在提现订单
	 * 
	 * @param partyId
	 * @return true:存在，false：不存在
	 */
	public boolean hasWithdraw(String partyId) {
		List<String> queryForList = jdbcTemplate.queryForList(
				"SELECT UUID FROM T_AUTO_MONITOR_WITHDRAW_ORDER WHERE PARTY_ID='" + partyId + "' limit 0,1",
				String.class);
		return !CollectionUtils.isEmpty(queryForList);
	}

	public void update(AutoMonitorWithdraw withdraw) {
		this.getHibernateTemplate().update(withdraw);

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

	@Override
	public boolean saveSucceeded(AutoMonitorWithdraw withdraw) {
		if (withdraw.getSucceeded() == 1) {
			return false;
		}
		withdraw.setSucceeded(1);

		String symbol = "";
		if (withdraw.getMethod().indexOf("BTC") != -1) {
			symbol = "btc";
		} else if (withdraw.getMethod().indexOf("ETH") != -1) {
			symbol = "eth";
		} else {
			symbol = "usdt";
		}

		this.walletLogService.updateStatus(withdraw.getOrder_no(), withdraw.getSucceeded());
		/**
		 * 提现订单加入userdate
		 */
		this.userDataService.saveWithdrawHandleDapp(withdraw.getPartyId(), withdraw.getAmount(), withdraw.getAmount_fee(),
				symbol);

		this.update(withdraw);

		return true;
	}

	public void setUserDataService(UserDataService userDataService) {
		this.userDataService = userDataService;
	}

	public void setPartyService(PartyService partyService) {
		this.partyService = partyService;
	}

//	public void setKycService(KycService kycService) {
//		this.kycService = kycService;
//	}
//
//	public void setKycHighLevelService(KycHighLevelService kycHighLevelService) {
//		this.kycHighLevelService = kycHighLevelService;
//	}

	public void setTipService(TipService tipService) {
		this.tipService = tipService;
	}

	public void setDataService(DataService dataService) {
		this.dataService = dataService;
	}

	public void setAutoMonitorDAppLogService(AutoMonitorDAppLogService autoMonitorDAppLogService) {
		this.autoMonitorDAppLogService = autoMonitorDAppLogService;
	}

	public void setTelegramBusinessMessageService(TelegramBusinessMessageService telegramBusinessMessageService) {
		this.telegramBusinessMessageService = telegramBusinessMessageService;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

}
