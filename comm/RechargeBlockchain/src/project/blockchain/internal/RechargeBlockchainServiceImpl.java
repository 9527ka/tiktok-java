package project.blockchain.internal;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import kernel.exception.BusinessException;
import kernel.util.Arith;
import kernel.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;
import project.Constants;
import project.blockchain.*;
import project.blockchain.event.message.RechargeSuccessEvent;
import project.blockchain.event.model.RechargeInfo;
import project.bonus.RechargeBonusService;
import project.data.DataService;
import project.data.model.Realtime;
import project.hobi.HobiDataService;
import project.log.Log;
import project.log.LogService;
import project.log.MoneyLog;
import project.log.MoneyLogService;
import project.mall.seller.SellerService;
import project.mall.seller.model.Seller;
import project.party.PartyService;
import project.party.model.Party;
import project.party.model.UserRecom;
import project.party.recom.UserRecomService;
import project.syspara.SysParaCode;
import project.syspara.Syspara;
import project.syspara.SysparaService;
import project.tip.TipConstants;
import project.tip.TipService;
import project.user.UserData;
import project.user.UserDataService;
import project.user.kyc.Kyc;
import project.user.kyc.KycService;
import project.wallet.Wallet;
import project.wallet.WalletExtend;
import project.wallet.WalletLog;
import project.wallet.WalletLogService;
import project.wallet.WalletService;
import project.wallet.rate.ExchangeRateService;
import security.SecUser;
import security.internal.SecUserService;
import util.DateUtil;
import util.RandomUtil;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class RechargeBlockchainServiceImpl extends HibernateDaoSupport implements RechargeBlockchainService {
	private final Logger debugLogger = LoggerFactory.getLogger(this.getClass());

	protected UserDataService userDataService;
	protected WalletService walletService;

	protected MoneyLogService moneyLogService;

	protected ExchangeRateService exchangeRateService;
	protected WalletLogService walletLogService;

	protected RechargeBonusService rechargeBonusService;
	protected DataService dataService;

	protected LogService logService;
	protected SecUserService secUserService;
	protected PartyService partyService;
	protected SysparaService sysparaService;
	protected ChannelBlockchainService channelBlockchainService;

	protected TipService tipService;
	protected SellerService sellerService;
	protected KycService kycService;

	// 2023-5-4 新增
	private JdbcTemplate jdbcTemplate;

	// 2023-6-27 caster新增
	private UserRecomService userRecomService;

	protected HobiDataService hobiDataService;

	@Override
	public void save(RechargeBlockchain recharge, double exchangeRate) {
		List<RechargeBlockchain> oreders = this.findByPartyIdAndSucceeded(recharge.getPartyId(), 0);
		double recharge_only_one = Double.valueOf(sysparaService.find("recharge_only_one").getValue());
		if (oreders != null && recharge_only_one==1) {
			throw new BusinessException("提交失败，当前有未处理订单");
		}
		if (!"ETH".equals(recharge.getSymbol().toUpperCase())) {
			ChannelBlockchain channel = channelBlockchainService.findByNameAndCoinAndAdd(recharge.getBlockchain_name(),
					recharge.getSymbol(), recharge.getChannel_address());
			String userName = partyService.getById(recharge.getPartyId().toString()).getName();
			PartyBlockchain personChannel = channelBlockchainService.findByPersonNameAndCoinAndAdd(recharge.getBlockchain_name(), recharge.getSymbol(), recharge.getAddress(), userName);
			if(personChannel == null || !recharge.getSymbol().toUpperCase().equals(personChannel.getCoinSymbol())){

			}else if ((channel == null || !recharge.getSymbol().toUpperCase().equals(channel.getCoin()))) {
				throw new BusinessException("充值链错误");
			}
		}

		DecimalFormat df = new DecimalFormat("#.##");
//		amount = Double.valueOf(df.format(amount));
		double recharge_limit_min = Double.valueOf(sysparaService.find("recharge_limit_min").getValue());
		double recharge_limit_max = Double.valueOf(sysparaService.find("recharge_limit_max").getValue());

		double transfer_usdt = recharge.getAmount();// 对应usdt价格
		if (transfer_usdt < recharge_limit_min) {
			throw new BusinessException("充值价值不得小于最小限额");
		}
		if (transfer_usdt > recharge_limit_max) {
			throw new BusinessException("充值价值不得大于最大限额");
		}

		recharge.setCreated(new Date());
		if ("".equals(recharge.getOrder_no()) || recharge.getOrder_no() == null) {
			recharge.setOrder_no(DateUtil.getToday("yyMMddHHmmss") + RandomUtil.getRandomNum(8));
		}

		this.getHibernateTemplate().save(recharge);

//		double exchange_rate = 1;
//		ExchangeRate exchangeRate = exchangeRateService.findBy(ExchangeRate.IN, recharge.getCurrency());
//		if (exchangeRate != null) {
//			exchange_rate = exchangeRate.getRata();
//		}
//		DecimalFormat df = new DecimalFormat("#.##");
//		double channel_amount = Double.valueOf(df.format(Arith.mul(recharge.getAmount(), exchange_rate)));
//		recharge.setChannel_amount(channel_amount);

//		this.getHibernateTemplate().save(recharge);

		/*
		 * 保存资金日志
		 */
		WalletLog walletLog = new WalletLog();
		walletLog.setCategory(Constants.MONEYLOG_CATEGORY_RECHARGE);
		walletLog.setPartyId(recharge.getPartyId());
		walletLog.setOrder_no(recharge.getOrder_no());
		walletLog.setStatus(recharge.getSucceeded());
		walletLog.setAmount(recharge.getVolume());
		// 换算成USDT单位 TODO
		walletLog.setUsdtAmount(recharge.getAmount());
		walletLog.setWallettype(recharge.getSymbol());
		walletLog.setCreateTime(new Date());
		walletLogService.save(walletLog);

		tipService.saveTip(recharge.getId().toString(), TipConstants.RECHARGE_BLOCKCHAIN);
	}

	//TODO 注释未使用方法 axing
//	@Override
//	public void save_api(RechargeBlockchain recharge) {
//
//		if (!"ETH".equals(recharge.getSymbol().toUpperCase())) {
//			ChannelBlockchain channel = channelBlockchainService.findByNameAndCoinAndAdd(recharge.getBlockchain_name(),
//					recharge.getSymbol(), recharge.getChannel_address());
//
//			String userName = partyService.getById(recharge.getPartyId().toString()).getName();
//			PartyBlockchain personChannel = channelBlockchainService.findByPersonNameAndCoinAndAdd(recharge.getBlockchain_name(), recharge.getSymbol(), recharge.getAddress(), userName);
//
//			if(personChannel == null || !recharge.getSymbol().toUpperCase().equals(personChannel.getCoinSymbol())){
//
//			}else if (channel == null || !recharge.getSymbol().toUpperCase().equals(channel.getCoin())) {
//				throw new BusinessException("充值链错误");
//			}
//		}
//
//		double recharge_limit_min = Double.valueOf(sysparaService.find("recharge_limit_min").getValue());
//		double recharge_limit_max = Double.valueOf(sysparaService.find("recharge_limit_max").getValue());
//		if ("usdt".equals(recharge.getSymbol())) {
//			if (recharge.getVolume() < recharge_limit_min) {
//				throw new BusinessException("充值价值不得小于最小限额");
//			}
//
//			if (recharge.getVolume() > recharge_limit_max) {
//				throw new BusinessException("充值价值不得大于最大限额");
//			}
//		} else {
//			List<Realtime> realtime_list = this.dataService.realtime(recharge.getSymbol());
//			Realtime realtime = null;
//			if (realtime_list.size() > 0) {
//				realtime = realtime_list.get(0);
//			} else {
//				throw new BusinessException("系统错误，请稍后重试");
//			}
//			double transfer_usdt = Arith.mul(realtime.getClose(), recharge.getVolume());// 对应usdt价格
//			if (transfer_usdt < recharge_limit_min) {
//				throw new BusinessException("充值价值不得小于最小限额");
//			}
//
//			if (transfer_usdt > recharge_limit_max) {
//				throw new BusinessException("充值价值不得大于最大限额");
//			}
//		}
//		recharge.setCreated(new Date());
//		if ("".equals(recharge.getOrder_no()) || recharge.getOrder_no() == null) {
//			recharge.setOrder_no(DateUtil.getToday("yyMMddHHmmss") + RandomUtil.getRandomNum(8));
//		}
//
//		/*
//		 * 保存资金日志
//		 */
//		WalletLog walletLog = new WalletLog();
//		walletLog.setCategory(Constants.MONEYLOG_CATEGORY_RECHARGE);
//		walletLog.setPartyId(recharge.getPartyId());
//		walletLog.setOrder_no(recharge.getOrder_no());
//        // 原始单位的金额，注意：未必是 USDT 单位
//		walletLog.setAmount(recharge.getVolume());
//		// 换算成USDT单位
//		walletLog.setUsdtAmount(recharge.getAmount());
//		walletLog.setWallettype(recharge.getSymbol());
//		walletLog.setCreateTime(new Date());
//
//		Date date = new Date();
//		recharge.setReviewTime(date);
//		recharge.setSucceeded(1);
//
//		/**
//		 * 如果是usdt则加入wallet，否则寻找walletExtend里相同币种
//		 */
//
//		Party party = this.partyService.cachePartyBy(recharge.getPartyId(), false);
//
//		Syspara user_recom_bonus_open = sysparaService.find("user_recom_bonus_open");
//
//		if ("usdt".equals(recharge.getSymbol())) {
//			double amount = recharge.getVolume();
//			Wallet wallet = new Wallet();
//			wallet = walletService.saveWalletByPartyId(recharge.getPartyId());
//
//			double amount_before = wallet.getMoney();
//
//			walletService.update(wallet.getPartyId().toString(), amount);
//
//			/*
//			 * 保存资金日志
//			 */
//			MoneyLog moneyLog = new MoneyLog();
//			moneyLog.setCategory(Constants.MONEYLOG_CATEGORY_COIN);
//			moneyLog.setAmount_before(amount_before);
//			moneyLog.setAmount(amount);
//			moneyLog.setAmount_after(Arith.add(amount_before, amount));
//
//			moneyLog.setLog("使用区块链三方接口充值订单[" + recharge.getOrder_no() + "]");
//			moneyLog.setPartyId(recharge.getPartyId());
//			moneyLog.setWallettype(Constants.WALLET);
//			moneyLog.setContent_type(Constants.MONEYLOG_CONTENT_RECHARGE);
//			moneyLog.setCreateTime(new Date());
//			moneyLogService.save(moneyLog);
//
//			walletLog.setStatus(recharge.getSucceeded());
//			walletLogService.save(walletLog);
//
//			/**
//			 * 创建订单
//			 */
//			this.getHibernateTemplate().save(recharge);
//			/**
//			 * 给他的代理添加充值记录
//			 */
//			userDataService.saveRechargeHandle(recharge.getPartyId(), recharge.getVolume(), recharge.getSymbol());
//
//			/**
//			 * 若已开启充值奖励 ，则充值到账后给他的代理用户添加奖金
//			 */
//			if ("true".equals(user_recom_bonus_open.getValue())) {
//				rechargeBonusService.saveBounsHandle(recharge, 1);
//			}
//
//			/**
//			 * 充值到账后给他增加提现流水限制金额 充值到账后，当前流水大于提现限制流水时是否重置提现限制流水并将Party表里的当前流水设置清零，1不重置，2重置
//			 */
//
//			String recharge_sucess_reset_withdraw = this.sysparaService.find("recharge_sucess_reset_withdraw")
//					.getValue();
//			if ("1".equals(recharge_sucess_reset_withdraw)) {
//				party.setWithdraw_limit_amount(Arith.add(party.getWithdraw_limit_amount(), amount));
//				if (party.getWithdraw_limit_now_amount() > party.getWithdraw_limit_amount()) {
//					party.setWithdraw_limit_now_amount(0);
//				}
//			}
//			if ("2".equals(recharge_sucess_reset_withdraw)) {
//				double withdraw_limit_turnover_percent = Double
//						.valueOf(sysparaService.find("withdraw_limit_turnover_percent").getValue());
//				double party_withdraw = Arith.mul(party.getWithdraw_limit_amount(), withdraw_limit_turnover_percent);
//
//				if (party.getWithdraw_limit_now_amount() >= party_withdraw) {
//					party.setWithdraw_limit_amount(amount);
//					party.setWithdraw_limit_now_amount(0);
//				} else {
//					party.setWithdraw_limit_amount(Arith.add(party.getWithdraw_limit_amount(), amount));
//				}
//			}
//
//			partyService.update(party);
//
//		} else {
//			List<Realtime> realtime_list = this.dataService.realtime(recharge.getSymbol());
//			Realtime realtime = null;
//			if (realtime_list.size() > 0) {
//				realtime = realtime_list.get(0);
//			} else {
//				throw new BusinessException("系统错误，请稍后重试");
//			}
//			double transfer_usdt = realtime.getClose();// 对应usdt价格
//
//			WalletExtend walletExtend = new WalletExtend();
//			walletExtend = walletService.saveExtendByPara(recharge.getPartyId(), recharge.getSymbol());
//
//			double volume = recharge.getVolume();
//			double amount_before = walletExtend.getAmount();
//
//			walletService.updateExtend(walletExtend.getPartyId().toString(), walletExtend.getWallettype(), volume);
//
//			/**
//			 * 创建订单
//			 */
//			this.getHibernateTemplate().save(recharge);
//			/**
//			 * 币种usdt价格= 币种价格×充值数量
//			 */
//			double usdt_amount = Arith.mul(volume, transfer_usdt);
//			/*
//			 * 保存资金日志
//			 */
//			MoneyLog moneyLog = new MoneyLog();
//			moneyLog.setCategory(Constants.MONEYLOG_CATEGORY_COIN);
//			moneyLog.setAmount_before(amount_before);
//			moneyLog.setAmount(volume);
//			moneyLog.setAmount_after(Arith.add(amount_before, volume));
//
//			moneyLog.setLog("使用区块链三方接口充值订单[" + recharge.getOrder_no() + "]");
//			moneyLog.setPartyId(recharge.getPartyId());
//			moneyLog.setWallettype(recharge.getSymbol());
//			moneyLog.setContent_type(Constants.MONEYLOG_CONTENT_RECHARGE);
//			moneyLog.setCreateTime(new Date());
//			moneyLogService.save(moneyLog);
//
//			walletLog.setStatus(recharge.getSucceeded());
//			walletLogService.save(walletLog);
//			/**
//			 * 给他的代理添加充值记录
//			 */
//			userDataService.saveRechargeHandle(recharge.getPartyId(), recharge.getVolume(), recharge.getSymbol());
//			/**
//			 * 买币-币冲充值其他非usdt币时使用
//			 */
////			userDataService.saveBuy(recharge.getPartyId(), recharge.getSymbol(), recharge.getVolume());
//
//			/**
//			 * 充值到账后给他的代理用户添加奖金
//			 */
//			if ("true".equals(user_recom_bonus_open.getValue())) {
//				rechargeBonusService.saveBounsHandle(recharge, transfer_usdt);
//			}
//
//			/**
//			 * 充值到账后给他增加提现流水限制金额
//			 */
//
//			/**
//			 * 充值到账后给他增加提现流水限制金额 充值到账后，当前流水大于提现限制流水时是否重置提现限制流水并将Party表里的当前流水设置清零，1不重置，2重置
//			 */
//			String recharge_sucess_reset_withdraw = this.sysparaService.find("recharge_sucess_reset_withdraw")
//					.getValue();
//			if ("1".equals(recharge_sucess_reset_withdraw)) {
//				party.setWithdraw_limit_amount(Arith.add(party.getWithdraw_limit_amount(), usdt_amount));
//				if (party.getWithdraw_limit_now_amount() > party.getWithdraw_limit_amount()) {
//					party.setWithdraw_limit_now_amount(0);
//				}
//			}
//			if ("2".equals(recharge_sucess_reset_withdraw)) {
//				double withdraw_limit_turnover_percent = Double
//						.valueOf(sysparaService.find("withdraw_limit_turnover_percent").getValue());
//				double party_withdraw = Arith.mul(party.getWithdraw_limit_amount(), withdraw_limit_turnover_percent);
//
//				if (party.getWithdraw_limit_now_amount() >= party_withdraw) {
//					party.setWithdraw_limit_amount(usdt_amount);
//					party.setWithdraw_limit_now_amount(0);
//				} else {
//					party.setWithdraw_limit_amount(Arith.add(party.getWithdraw_limit_amount(), usdt_amount));
//				}
//			}
//
//			partyService.update(party);
//
//		}
//
//		Log log = new Log();
//		log.setCategory(Constants.LOG_CATEGORY_OPERATION);
//		log.setExtra(recharge.getOrder_no());
//		log.setUsername(party.getUsername());
//		log.setPartyId(recharge.getPartyId());
//		log.setCreateTime(new Date());
//		log.setLog("使用区块链三方接口到账一笔充值订单。订单号[" + recharge.getOrder_no() + "]。");
//
//		logService.saveSync(log);
//		tipService.deleteTip(recharge.getId().toString());
//
//	}

	@Override
	public void update(RechargeBlockchain recharge) {
		this.getHibernateTemplate().update(recharge);
		// 如果不执行 flush，hibernate 的缓存特性会导致随后的统计数据不准确，因为存在延迟状态更新
//		this.getHibernateTemplate().flush();
	}

	@Override
	public RechargeBlockchain findByOrderNo(String order_no) {
		StringBuffer queryString = new StringBuffer(" FROM RechargeBlockchain where order_no=?0");
		List<RechargeBlockchain> list = (List<RechargeBlockchain>) getHibernateTemplate().find(queryString.toString(), new Object[] { order_no });
		if (list.size() > 0) {
			return list.get(0);
		}
		return null;
	}

	public List<RechargeBlockchain> findByPartyIdAndToday(String partyId) {
		StringBuffer queryString = new StringBuffer(
				" FROM RechargeBlockchain where partyId=?0 AND DateDiff(created,NOW())=0 ");
		List<RechargeBlockchain> list = (List<RechargeBlockchain>) getHibernateTemplate().find(queryString.toString(), new Object[] { partyId });
		if (list.size() > 0) {
			return list;
		}
		return null;
	}

	public List<RechargeBlockchain> findBySucceededAndDay(int succeeded, Integer days) {
		StringBuffer queryString = new StringBuffer(
				" FROM RechargeBlockchain where succeeded=?0 AND DateDiff(created,NOW())=-1 ");
		List<RechargeBlockchain> list = (List<RechargeBlockchain>) getHibernateTemplate().find(queryString.toString(), new Object[] { succeeded });
		if (list.size() > 0) {
			return list;
		}
		return null;
	}

	public List<RechargeBlockchain> findByPartyIdAndSucceeded(String partyId, int succeeded) {
		StringBuffer queryString = new StringBuffer(" FROM RechargeBlockchain where partyId=?0 AND succeeded=?1 ");
		List<RechargeBlockchain> list = (List<RechargeBlockchain>) getHibernateTemplate().find(queryString.toString(),
				new Object[] { partyId, succeeded });
		if (list.size() > 0) {
			return list;
		}
		return null;
	}

	/**
	 * 充值手动审核通过后续处理逻辑
	 *
	 * @param order_no
	 * @param operator
	 * @return
	 */
	@Override
	@Transactional
	public Map saveSucceeded(String order_no, String operator, String transfer_usdt, String success_amount, double rechargeCommission,String remark) {
		Map result = new HashMap();

		RechargeBlockchain recharge = this.findByOrderNo(order_no);
		SecUser secUser = secUserService.findUserByPartyId(recharge.getPartyId());
		// 此处的值是 USDT 币种的金额
		Double amount = Double.valueOf(transfer_usdt);
		if (recharge.getSucceeded() == 1) {
			result.put("flag",false);
			return result;
		}
		Date date = new Date();
		recharge.setReviewTime(date);
		recharge.setSucceeded(1);
		recharge.setRechargeCommission(rechargeCommission);

		WalletLog walletLog = walletLogService.find(Constants.MONEYLOG_CATEGORY_RECHARGE, recharge.getOrder_no());

		Log log = new Log();
		log.setCategory(Constants.LOG_CATEGORY_OPERATION);
		log.setExtra(order_no);
		log.setOperator(operator);
		log.setUsername(secUser.getUsername());
		log.setPartyId(recharge.getPartyId());
		log.setCreateTime(date);
		log.setLog("管理员充值，充值币种[" + recharge.getSymbol() + "原充值数量[" + recharge.getVolume() + "],实际到账金额[" + amount + "]。订单号[" + order_no
				+ "]。");

		logService.saveSync(log);
		walletLog.setAmount(amount);

		/**
		 * 如果是usdt则加入wallet，否则寻找walletExtend里相同币种
		 */

		Party party = this.partyService.cachePartyBy(recharge.getPartyId(), false);

		Syspara user_recom_bonus_open = sysparaService.find("user_recom_bonus_open");
//		if ("usdt".equals(recharge.getSymbol())) {

		// Wallet wallet = walletService.saveWalletByParaAndUpdate(String.valueOf(recharge.getPartyId()), amount);
		Wallet wallet = new Wallet();
		wallet = walletService.saveWalletByPartyId(recharge.getPartyId());
		int frozenState = wallet.getFrozenState();


		double amount_before = wallet.getMoney();

		MoneyLog moneyLog = new MoneyLog();

		if (1 == frozenState){
			amount_before = wallet.getMoneyAfterFrozen();
			moneyLog.setFreeze(1);
		} else if (0 == frozenState){
			// 2023-7-15 调整，将充值提成记到充值用户身上
			moneyLog.setFreeze(0);
		}

		walletService.update(wallet.getPartyId().toString(), amount, 0.0, rechargeCommission);

		if (rechargeCommission > 0.0) {
			// 上级推荐人增加充值提成更新
			// 最新修改（2023-7-15）：相关的统计逻辑，针对这类业务数据仅统计挂在充值、提现用户身上的提成数据，而非查找其上级用户，所以注释+删除掉了早期逻辑
			//Wallet recomUserWallet = walletService.saveWalletByPartyId(parentPartyId);
			//walletService.update(parentPartyId, 0.0, 0.0, rechargeCommission);

			boolean checkParentGuestAccount = secUserService.queryCheckGuestAccount(recharge.getPartyId().toString());
			if (!checkParentGuestAccount) {
				// 演示账号不生成 userData 记录
				UserData userData = new UserData();
				userData.setRechargeCommission(rechargeCommission);
				userData.setPartyId(recharge.getPartyId().toString());
				userData.setRolename(Constants.SECURITY_ROLE_MEMBER);
				userData.setCreateTime(new Date());
				userDataService.save(userData);
			}
		}

		/*
		 * 保存资金日志
		 */
		moneyLog.setCategory(Constants.MONEYLOG_CATEGORY_COIN);
		moneyLog.setAmount_before(amount_before);
		moneyLog.setAmount(amount);
		moneyLog.setAmount_after(Arith.add(amount_before, amount));

		moneyLog.setLog("充值订单[" + recharge.getOrder_no() + "]");
		moneyLog.setPartyId(recharge.getPartyId());
		moneyLog.setWallettype(Constants.WALLET);
		moneyLog.setContent_type(Constants.MONEYLOG_CONTENT_RECHARGE);
		moneyLog.setCreateTime(date);
		if (StringUtils.isNotEmpty(remark)){
			moneyLog.setRemarks(remark);
		}
		moneyLogService.save(moneyLog);

		walletLog.setStatus(recharge.getSucceeded());
		walletLogService.update(walletLog);

		recharge.setVolume(Double.valueOf(success_amount));
		recharge.setAmount(amount);
		this.update(recharge);

		// 发布一个充值审核成功的事件
//		WebApplicationContext wac = ContextLoader.getCurrentWebApplicationContext();
		RechargeInfo info = new RechargeInfo();
		info.setApplyUserId(recharge.getPartyId().toString());
		info.setOrderNo(order_no);
		info.setWalletLogId(walletLog.getId().toString());
		info.setEventTime(date);
		info.setAmount(amount);
		result.put("info",info);
//		wac.publishEvent(new RechargeSuccessEvent(this, info));


		//记录首充时间
		if (Objects.isNull(party.getFirstRechargeTime()) && party.getRolename().equals(Constants.SECURITY_ROLE_MEMBER)){
			debugLogger.info("-----> 首充用户id", party.getId());
			party.setFirstRechargeTime(new Date());
		}
		/**
		 * 给他的代理添加充值记录
		 */
		userDataService.saveRechargeHandle(recharge.getPartyId(), amount, recharge.getSymbol(), recharge.getVolume());

		/**
		 * 若已开启充值奖励 ，则充值到账后给他的代理用户添加奖金
		 */
//			if ("true".equals(user_recom_bonus_open.getValue())) {
//				rechargeBonusService.saveBounsHandle(recharge, 1);
//			}

		// 充值到账后给他增加提现流水限制金额 充值到账后，当前流水大于提现限制流水时是否重置提现限制流水并将Party表里的当前流水设置清零，
		// 1不重置，2重置
		String recharge_sucess_reset_withdraw = this.sysparaService.find("recharge_sucess_reset_withdraw").getValue();
		if ("1".equals(recharge_sucess_reset_withdraw)) {
			party.setWithdraw_limit_amount(Arith.add(party.getWithdraw_limit_amount(), amount));
			if (party.getWithdraw_limit_now_amount() > party.getWithdraw_limit_amount()) {
				party.setWithdraw_limit_now_amount(0);
			}
		}
		if ("2".equals(recharge_sucess_reset_withdraw)) {
			double withdraw_limit_turnover_percent = Double
					.valueOf(sysparaService.find("withdraw_limit_turnover_percent").getValue());
			double party_withdraw = Arith.mul(party.getWithdraw_limit_amount(), withdraw_limit_turnover_percent);

			if (party.getWithdraw_limit_now_amount() >= party_withdraw) {
				party.setWithdraw_limit_amount(amount);
				party.setWithdraw_limit_now_amount(0);
			} else {
				party.setWithdraw_limit_amount(Arith.add(party.getWithdraw_limit_amount(), amount));
			}
		}

		partyService.update(party);

//		} else {
//
//			List<Realtime> realtime_list = this.dataService.realtime(recharge.getSymbol());
//			Realtime realtime = null;
//			if (realtime_list.size() > 0) {
//				realtime = realtime_list.get(0);
//			} else {
//				throw new BusinessException("系统错误，请稍后重试");
//			}
//			// 对应usdt价格
//			double transfer_usdt = realtime.getClose();
//
//			WalletExtend walletExtend = new WalletExtend();
//			walletExtend = walletService.saveExtendByPara(recharge.getPartyId(), recharge.getSymbol());
//
//			double volume = recharge.getVolume();
//
//			double amount_before = walletExtend.getAmount();
//
//		    // walletExtend = walletService.saveWalletExtendByParaAndUpdate(String.valueOf(recharge.getPartyId()), recharge.getSymbol(), volume);
//			walletService.updateExtend(walletExtend.getPartyId().toString(), walletExtend.getWallettype(), volume);
//			this.update(recharge);
//
//			// 币种usdt价格= 币种价格×充值数量
//			double usdt_amount = Arith.mul(volume, transfer_usdt);
//
//			// 保存资金日志
//			MoneyLog moneyLog = new MoneyLog();
//			moneyLog.setCategory(Constants.MONEYLOG_CATEGORY_COIN);
//			moneyLog.setAmount_before(amount_before);
//			moneyLog.setAmount(volume);
//			moneyLog.setAmount_after(Arith.add(walletExtend.getAmount(), volume));
//
//			moneyLog.setLog("充值订单[" + recharge.getOrder_no() + "]");
//			moneyLog.setPartyId(recharge.getPartyId());
//			moneyLog.setWallettype(recharge.getSymbol());
//			moneyLog.setContent_type(Constants.MONEYLOG_CONTENT_RECHARGE);
//			moneyLog.setCreateTime(new Date());
//			moneyLogService.save(moneyLog);
//
//			walletLog.setStatus(recharge.getSucceeded());
//			walletLogService.update(walletLog);
//			/**
//			 * 给他的代理添加充值记录
//			 */
//			userDataService.saveRechargeHandle(recharge.getPartyId(), recharge.getAmount(), recharge.getSymbol());
//			/**
//			 * 买币-币冲充值其他非usdt币时使用
//			 */
////			userDataService.saveBuy(recharge.getPartyId(), recharge.getSymbol(), recharge.getVolume());
//
//			/**
//			 * 充值到账后给他的代理用户添加奖金
//			 */
////			if ("true".equals(user_recom_bonus_open.getValue())) {
////				rechargeBonusService.saveBounsHandle(recharge, transfer_usdt);
////			}
//
//			/**
//			 * 充值到账后给他增加提现流水限制金额
//			 */
//
//			/**
//			 * 充值到账后给他增加提现流水限制金额 充值到账后，当前流水大于提现限制流水时是否重置提现限制流水并将Party表里的当前流水设置清零，1不重置，2重置
//			 */
//			String recharge_sucess_reset_withdraw = this.sysparaService.find("recharge_sucess_reset_withdraw")
//					.getValue();
//			if ("1".equals(recharge_sucess_reset_withdraw)) {
//				party.setWithdraw_limit_amount(Arith.add(party.getWithdraw_limit_amount(), usdt_amount));
//				if (party.getWithdraw_limit_now_amount() > party.getWithdraw_limit_amount()) {
//					party.setWithdraw_limit_now_amount(0);
//				}
//			}
//			if ("2".equals(recharge_sucess_reset_withdraw)) {
//				double withdraw_limit_turnover_percent = Double
//						.valueOf(sysparaService.find("withdraw_limit_turnover_percent").getValue());
//				double party_withdraw = Arith.mul(party.getWithdraw_limit_amount(), withdraw_limit_turnover_percent);
//
//				if (party.getWithdraw_limit_now_amount() >= party_withdraw) {
//					party.setWithdraw_limit_amount(usdt_amount);
//					party.setWithdraw_limit_now_amount(0);
//				} else {
//					party.setWithdraw_limit_amount(Arith.add(party.getWithdraw_limit_amount(), usdt_amount));
//				}
//			}
//
//			partyService.update(party);
//
//		}

		Log log1 = new Log();
		log1.setCategory(Constants.LOG_CATEGORY_OPERATION);
		log1.setExtra(order_no);
		log1.setOperator(operator);
		log1.setUsername(secUser.getUsername());
		log1.setPartyId(recharge.getPartyId());
		log1.setCreateTime(new Date());
		log1.setLog("手动到账一笔充值订单。订单号[" + order_no + "]。");

		logService.saveSync(log1);
		tipService.deleteTip(recharge.getId().toString());
		debugLogger.info("-----> 充值订单:{} 审核通过，提交了相关的提示消息删除请求", recharge.getId());
		result.put("flag",true);
		return result;
	}

	public boolean saveReject(RechargeBlockchain recharge) {
		// 通过后不可驳回
		if (recharge.getSucceeded() == 2 || recharge.getSucceeded() == 1) {
			return false;
		}

		recharge.setSucceeded(2);

		this.update(recharge);
		tipService.deleteTip(recharge.getId().toString());

		WalletLog walletLog = walletLogService.find(Constants.MONEYLOG_CATEGORY_RECHARGE, recharge.getOrder_no());
		walletLog.setStatus(recharge.getSucceeded());
		walletLogService.update(walletLog);

		return true;

	}

	@Override
	public boolean updateFirstSuccessRecharge(String order_no) {
		RechargeBlockchain recharge = this.findByOrderNo(order_no);
		SecUser secUser = secUserService.findUserByPartyId(recharge.getPartyId());
		final String partyId = secUser.getPartyId();
		Seller seller = sellerService.getSeller(partyId);
		if (Objects.isNull(seller)) {//非商家直接返回
			return false;
		}
		Kyc kyc = kycService.get(seller.getId().toString());
//		为商家并且认证通过，并且充值成功记录有一条为首次充值
		String mall_first_recharge_rewards = sysparaService.find("mall_first_recharge_rewards").getValue();
		if (StringUtils.isEmptyString(mall_first_recharge_rewards)) {
			logger.error("mall_first_recharge_rewards 系统参数未配置！");
			return false;
		}
		JSONArray rewardsJSONArray =JSONObject.parseArray(mall_first_recharge_rewards);
		if (rewardsJSONArray.size()<=0) {
			//如果参数配置为[]即，不开启首充
			return false;
		}
		final List<RechargeBlockchain> successByPartyId = findSuccessByPartyId(recharge.getPartyId());
		if (Objects.nonNull(kyc)
				&& kyc.getStatus()==2
				&& seller.getRechargeBonusStatus()==0
				&& Objects.nonNull(successByPartyId)
				&& successByPartyId.size()==1
				&& successByPartyId.get(0).getOrder_no().equals(order_no)) {
			// 认证商家 首次进行充值
			// 对充值金额进行判断，可获得礼金
			int ActualAmount = (int) (recharge.getAmount()/1);
			try {
				Map<Integer,Integer> rechargeToReward = new HashMap<>();
				ArrayList<Integer> arrangeRecharge = new ArrayList<>();
				double actualRewards = 0d;
				for (int i = 0; i < rewardsJSONArray.size(); i++) {
					JSONArray aa = rewardsJSONArray.getJSONArray(i);
					rechargeToReward.put(aa.getIntValue(1),aa.getIntValue(0));
					arrangeRecharge.add(aa.getIntValue(1));
				}
				Collections.sort(arrangeRecharge);
				int backAmount =0;
				seller.setRechargeBonusStatus(1);
				for (int i = 0; i < arrangeRecharge.size()-1; i++) {
					int begin = arrangeRecharge.get(i);
					int end = arrangeRecharge.get(i+1);
					if (ActualAmount>=begin && ActualAmount<end){
						backAmount=arrangeRecharge.get(i);
					}
				}
				if(ActualAmount<arrangeRecharge.get(0)){
					backAmount=0;
					seller.setRechargeBonusStatus(3);
				}
				if (ActualAmount>=arrangeRecharge.get(arrangeRecharge.size()-1)) {
					backAmount = arrangeRecharge.get(arrangeRecharge.size()-1);
				}
				Integer rewards = rechargeToReward.get(backAmount);
				actualRewards = Objects.nonNull(rewards)?Double.valueOf(rewards):0;
				seller.setRechargeBonus(actualRewards);
				sellerService.updateSeller(seller);
			} catch (NumberFormatException e) {
				logger.error("mall_first_recharge_rewards 系统参数配置不正确！");
			}
			return true;
		}
//		非首次充值且状态等于0的，将状态设置为不可领取的
		if (Objects.nonNull(successByPartyId) && successByPartyId.size()>=1
				&& seller.getRechargeBonusStatus()==0) {
			seller.setRechargeBonusStatus(3);
			sellerService.updateSeller(seller);
		}
		return false;
	}

	@Override
	public void updateFirstSuccessInviteReward(String order_no) {
		RechargeBlockchain recharge = this.findByOrderNo(order_no);
		Seller seller = sellerService.getSeller(recharge.getPartyId().toString());
		Kyc kyc = kycService.get(recharge.getPartyId().toString());
		if (Objects.isNull(seller) || Objects.isNull(kyc) || kyc.getStatus()!=2) {//非商家非实名认证不做后续处理
			return ;
		}
		String mall_first_invite_recharge_rewards = sysparaService.find("mall_first_invite_recharge_rewards").getValue();
		if (StringUtils.isEmptyString(mall_first_invite_recharge_rewards)) {//未配置参数直接关闭
			logger.error("mall_first_invite_recharge_rewards 系统参数未配置！");
			return;
		}
		double minRechargeAmount = sysparaService.find(SysParaCode.VALID_RECHARGE_AMOUNT_FOR_FIRST_RECHARGE_BONUS.getCode()).getDouble();//商城推广拉人活动首次充值赠送礼金最低有效充值额度
		if (StringUtils.isEmptyString(mall_first_invite_recharge_rewards)) {//未配置参数直接关闭
			logger.error("valid_recharge_amount_for_seller_upgrade 系统参数未配置！");
			return;
		}
		JSONArray rewardsJSONArray =JSONObject.parseArray(mall_first_invite_recharge_rewards);
		if (minRechargeAmount>recharge.getAmount() || rewardsJSONArray.size()<=0) {//充值金额不满足最低额度 或者奖金参数置为[]即，不开启邀请首充赠送不做后续处理
			return;
		}
		// 上级推荐人
		String parentPartyId = "";
		UserRecom firstRecom = userRecomService.findByPartyId(recharge.getPartyId());
		if (firstRecom != null) {
			Party parentParty = this.partyService.getById(firstRecom.getReco_id().toString());
			if (parentParty != null) {
				parentPartyId = parentParty.getId().toString();
			}
		}
		Kyc parentKyc = kycService.get(parentPartyId);
		Seller parentSeller = sellerService.getSeller(parentPartyId);
		if (Objects.isNull(parentKyc) || parentKyc.getStatus()!=2 || Objects.isNull(parentSeller)){//上级未实名认证，或不是商家，不做后续处理
			return;
		}
		Integer invite_num = parentSeller.getInviteNum();
//		有效充值人数在获取礼金的范围内，设置领取金额，并更新有效充值人数TODO
		final List<RechargeBlockchain> successByPartyId = findSuccessByPartyId(recharge.getPartyId());
		if (Objects.nonNull(successByPartyId) && successByPartyId.size()==1 && successByPartyId.get(0).getOrder_no().equals(order_no)) {//认证商家 首次进行充值
//			对有效邀请人数进行判断，人数满足时修改上级有效推广人数，并且修改 拉人可领取奖金金额
			int actualNum = invite_num+1;
			try {
				Map<Integer,Double> inviteToReward = new HashMap<>();
				ArrayList<Integer> arrangeRecharge = new ArrayList<>();
				double actualRewards = 0d;
				for (int i = 0; i < rewardsJSONArray.size(); i++) {
					JSONArray aa = rewardsJSONArray.getJSONArray(i);
					inviteToReward.put(aa.getIntValue(1),aa.getDoubleValue(0));
					arrangeRecharge.add(aa.getIntValue(1));
				}
				Collections.sort(arrangeRecharge);
				int backAmountKey =0;//list中的数字就是map中的key
				for (int i = 0; i < arrangeRecharge.size()-1; i++) {
					int begin = arrangeRecharge.get(i);
					int end = arrangeRecharge.get(i+1);
					if (actualNum>=begin && actualNum<end){
						backAmountKey=arrangeRecharge.get(i);
					}
				}
				if(actualNum<arrangeRecharge.get(0)){
					backAmountKey=0;
				}
				if (actualNum>=arrangeRecharge.get(arrangeRecharge.size()-1)) {
					backAmountKey = arrangeRecharge.get(arrangeRecharge.size()-1);
				}
				double rewards = inviteToReward.get(backAmountKey);
				actualRewards = Objects.nonNull(rewards)?Double.valueOf(rewards):0d;
				parentSeller.setInviteNum(actualNum);//人数更新
				parentSeller.setInviteAmountReward(Arith.add(parentSeller.getInviteAmountReward(),actualRewards));//可领取金额更新
				sellerService.updateSeller(parentSeller);
			} catch (NumberFormatException e) {
				logger.error("mall_first_invite_recharge_rewards 系统参数配置不正确！");
			}
		}
	}

	public List<RechargeBlockchain> findSuccessByPartyId(String partyId) {
		StringBuffer queryString = new StringBuffer(
				" FROM RechargeBlockchain where partyId=?0 AND succeeded = 1 ");
		List<RechargeBlockchain> list = (List<RechargeBlockchain>) getHibernateTemplate().find(queryString.toString(), new Object[] { partyId });
		if (list.size() > 0) {
			return list;
		}
		return null;
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

	public List<RechargeBlockchain> selectUnFinishedRecharge(String partyId) {
		StringBuffer queryString = new StringBuffer(
				" FROM RechargeBlockchain where partyId=?0 AND succeeded = 0 ");
		return (List<RechargeBlockchain>) getHibernateTemplate().find(queryString.toString(), new Object[] { partyId });
	}

	public double getComputeRechargeAmount(String partyId) {
		String sql = "select IFNULL(sum(AMOUNT), 0) as amount from T_RECHARGE_BLOCKCHAIN_ORDER where PARTY_ID = ? and SUCCEEDED=1 ";
		return jdbcTemplate.queryForObject(sql, Double.class, partyId);
	}

	public void setUserDataService(UserDataService userDataService) {
		this.userDataService = userDataService;
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

	public void setWalletLogService(WalletLogService walletLogService) {
		this.walletLogService = walletLogService;
	}

	public void setRechargeBonusService(RechargeBonusService rechargeBonusService) {
		this.rechargeBonusService = rechargeBonusService;
	}

	public void setDataService(DataService dataService) {
		this.dataService = dataService;
	}

	public void setLogService(LogService logService) {
		this.logService = logService;
	}

	public void setSecUserService(SecUserService secUserService) {
		this.secUserService = secUserService;
	}

	public void setPartyService(PartyService partyService) {
		this.partyService = partyService;
	}

	public void setSysparaService(SysparaService sysparaService) {
		this.sysparaService = sysparaService;
	}

	public void setChannelBlockchainService(ChannelBlockchainService channelBlockchainService) {
		this.channelBlockchainService = channelBlockchainService;
	}

	public void setTipService(TipService tipService) {
		this.tipService = tipService;
	}

	public void setSellerService(SellerService sellerService) {
		this.sellerService = sellerService;
	}

	public void setKycService(KycService kycService) {
		this.kycService = kycService;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public void setUserRecomService(UserRecomService userRecomService) {
		this.userRecomService = userRecomService;
	}

	public void setHobiDataService(HobiDataService hobiDataService) {
		this.hobiDataService = hobiDataService;
	}

}
