package project.bonus.internal;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

import kernel.util.Arith;
import kernel.util.DateUtils;
import kernel.util.StringUtils;
import kernel.util.ThreadUtils;
import project.Constants;
import project.blockchain.RechargeBlockchain;
import project.blockchain.RechargeBlockchainService;
import project.bonus.RechargeBonusService;
import project.log.MoneyLog;
import project.log.MoneyLogService;
import project.party.PartyService;
import project.party.model.Party;
import project.party.model.UserRecom;
import project.party.recom.UserRecomService;
import project.syspara.SysparaService;
import project.user.UserData;
import project.user.UserDataService;
import project.wallet.Wallet;
import project.wallet.WalletLog;
import project.wallet.WalletLogService;
import project.wallet.WalletService;

public class RechargeBonusServiceImpl extends HibernateDaoSupport implements RechargeBonusService {

	protected UserRecomService userRecomService;

	protected SysparaService sysparaService;

	protected WalletService walletService;

	protected MoneyLogService moneyLogService;

	protected RechargeBlockchainService rechargeBlockchainService;

	protected WalletLogService walletLogService;

	protected UserDataService userDataService;

	protected PartyService partyService;

	/**
	 * 每个推广人每天一次收益，每次充值金额最低在（）以上有推广费用，充值分成比在系统参数里
	 * 
	 */
	@Override
	public void saveBounsHandle(RechargeBlockchain entity, double transfer_usdt) {
		List<UserRecom> recom_parents = userRecomService.getParents(entity.getPartyId());
		if (recom_parents == null) {
			return;
		}
		if (recom_parents.size() == 0) {
			return;
		}
		/**
		 * 上级为空则直接结束
		 */

		if ("".equals(recom_parents.get(0).getReco_id()) || recom_parents.get(0).getReco_id() == null) {
			return;
		}
		
		/**
		 * 邀请奖励是否第三代后无限返佣一代 XX% 二代XX% 三代以后 XX% 
		 *  true , false
		 */
		boolean recharge_bonus_forever = sysparaService.find("recharge_bonus_forever").getBoolean();
		/**
		 * 获取数据库奖金分成比例
		 */
		String recharge_bonus_parameters = sysparaService.find("recharge_bonus_parameters").getValue();
		String[] recharge_bonus_array = recharge_bonus_parameters.split(",");
		double base_amount = Double.valueOf(recharge_bonus_array[0]);
		double order_usdt_amount = Arith.mul(transfer_usdt, entity.getVolume());
		/**
		 * 充值奖励类型（默认1）
		 * 1.下级用户每日首次充值超过分成金额则上级奖励；
		 * 2.上级累计充值超过分成金额则有奖励
		 */
		String recharge_bonus_type = sysparaService.find("recharge_bonus_type").getValue();
		if(StringUtils.isEmptyString(recharge_bonus_type)||"1".equals(recharge_bonus_type)) {
			/**
			 * 如果到账usdt金额小于可分成金额，直接退出
			 */
			if (order_usdt_amount < base_amount) {
				return;
			}
			/**
			 * 如果今日该用户还有充值过超过分成金额的记录，则不再奖励
			 */
			List<RechargeBlockchain> orders = rechargeBlockchainService.findByPartyIdAndToday(entity.getPartyId());
			if (orders == null) {
				return;
			}
			if (orders.size() > 1) {
				for (int i = 0; i < orders.size(); i++) {
					RechargeBlockchain order = orders.get(i);
					double order_amount = Arith.mul(order.getVolume(), transfer_usdt);
					if (entity.getOrder_no().equals(order.getOrder_no())) {
						continue;
					}
					if (order_amount >= base_amount && order.getSucceeded() == 1) {
						return;
					}

				}
			}
		}
		
		
		boolean recharge_new_bonus_button = sysparaService.find("recharge_new_bonus_button").getBoolean();

		// --start-- 12.2 新盘需求
		// 1000,10,0.05,0.03,0.003,0.003
		double first_bonus_max_num = 0d;
		if (recharge_new_bonus_button) {
			first_bonus_max_num = Double.valueOf(recharge_bonus_array[1]);
		}
		// --end-- 12.2 新盘需求
		/**
		 * 判断有几个父级代理，最多不超过4个有奖励
		 */
		for (int i = 0; i < recom_parents.size(); i++) {
			if (recharge_new_bonus_button) {
				// --start-- 12.2 新盘需求
				if (i >= 3) {
					return;
				}
				// --end-- 12.2 新盘需求
			} else {
				if (i >= 4 && !recharge_bonus_forever) {
					return;
				}
			}
			/**
			 * 邀请人是正式用户和演示用户才加奖金
			 */
			Party party = new Party();
			party = this.partyService.cachePartyBy(recom_parents.get(i).getReco_id(), true);
			if (!"MEMBER".equals(party.getRolename()) && !"GUEST".equals(party.getRolename())) {
				continue;
			}
			/**
			 * 2.上级累计充值超过分成金额则有奖励
			 */
			if("2".equals(recharge_bonus_type)&&!checkRechargeBonus(party.getId().toString(),order_usdt_amount,base_amount)) {
				continue;
			}
//			double pip_amount = Double.valueOf(recharge_bonus_array[i + 1]);
			double pip_amount = 0d;
			if (recharge_new_bonus_button) {
				// --start-- 12.2 新盘需求
				/**
				 * 直推奖励 3%~5%，满足10人时为5%
				 */
				if (i == 0 && this.userRecomService.findRecoms(recom_parents.get(i).getReco_id())
						.size() >= first_bonus_max_num) {
					pip_amount = Double.valueOf(recharge_bonus_array[i + 2]);
				} else {
					pip_amount = Double.valueOf(recharge_bonus_array[i + 3]);
//					 --end-- 12.2 新盘需求
				}
			} else {
				if(i>=4) {
					pip_amount = Double.valueOf(recharge_bonus_array[4]);
				}else {
					pip_amount = Double.valueOf(recharge_bonus_array[i + 1]);
				}
				
			}
			double get_money = Arith.mul(order_usdt_amount, pip_amount);

			Wallet wallet = walletService.saveWalletByPartyId(recom_parents.get(i).getReco_id());
			double amount_before = wallet.getMoney();
//				wallet.setMoney(Arith.add(wallet.getMoney(), get_money));
			walletService.update(wallet.getPartyId().toString(), get_money);

			/**
			 * 保存资金日志
			 */
			MoneyLog moneyLog = new MoneyLog();
			moneyLog.setCategory(Constants.MONEYLOG_CATEGORY_COIN);
			moneyLog.setAmount_before(amount_before);
			moneyLog.setAmount(get_money);
			moneyLog.setAmount_after(Arith.add(amount_before, get_money));
			moneyLog.setLog("第" + (i + 1) + "代用户充值到账了币种" + entity.getSymbol() + "，数量" + entity.getVolume() + "，订单号["
					+ entity.getOrder_no() + "]所奖励");
			moneyLog.setPartyId(recom_parents.get(i).getReco_id());
			moneyLog.setWallettype("USDT");
			moneyLog.setContent_type(Constants.MONEYLOG_CONTENT_RECHARGE);
			moneyLogService.save(moneyLog);

			WalletLog walletLog = new WalletLog();
			walletLog.setCategory(Constants.MONEYLOG_CATEGORY_RECHARGE);
			walletLog.setPartyId(recom_parents.get(i).getReco_id());
			walletLog.setOrder_no(entity.getOrder_no());
			walletLog.setWallettype(Constants.WALLET);
			walletLog.setStatus(1);

			walletLog.setAmount(get_money);
			// 换算成USDT单位 TODO
			walletLog.setUsdtAmount(get_money);
			walletLogService.save(walletLog);
		}
	}
	/**
	 * 累计充值是否超过分成金额
	 * @param partyId
	 * @param usdtAmount
	 * @param baseAmount
	 * @return
	 */
	private boolean checkRechargeBonus(String partyId,double usdtAmount,double baseAmount) {
		if(usdtAmount>=baseAmount) {
			return true;
		}
		Map<String, UserData> map = userDataService.cacheByPartyId(partyId);
		double rechargeMoney = rechargeMoney(map, null, null);
		return rechargeMoney>=baseAmount;
	}
	/**
	 * 时间范围内的充值总额
	 * 
	 * @param datas
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	private double rechargeMoney(Map<String, UserData> datas, String startTime, String endTime) {
		if (datas == null || datas.isEmpty())
			return 0;
		double userRecharge = 0;
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
			userRecharge = Arith.add(userdata.getRecharge_usdt(), userRecharge);
		}

		return userRecharge;
	}
	/**
	 * 每日定时返佣
	 */
	public void saveDailyBounsHandle() {
		//是否开启每日定时任务返佣，为空则不开启 0.5% 0.3% 0.2% = 0.005,0.003,0.002
		String daily_recharge_recom = this.sysparaService.find("daily_recharge_recom").getValue();
		
		List<UserData> userDatas = findBydate(new Date());
		for(int j=0;j < userDatas.size() ;j++) {
			UserData userData = userDatas.get(j);
			String partyId = (String) userData.getPartyId();
			double amount = userData.getRecharge();

			// 获取时间 查询 昨天 的 userdata 所有订单，里面 充值不为0的则 开始 返佣
			Party party_user = this.partyService.cachePartyBy(partyId, true);
			
			List<UserRecom> recom_parents = userRecomService.getParents(partyId);
			if (recom_parents == null) {
				continue;
			}
			if (recom_parents.size() == 0) {
				continue;
			}

			// 上级为空则直接结束
			if ("".equals(recom_parents.get(0).getReco_id()) || recom_parents.get(0).getReco_id() == null) {
				continue;
			}
			
			String[] recharge_bonus_array = daily_recharge_recom.split(",");
			/**
			 * 判断有几个父级代理，最多不超过3个有奖励
			 */
			for (int i = 0; i < recom_parents.size(); i++) {
				if (i < 3) {
					/**
					 * 邀请人是正式用户和演示用户才加奖金
					 */
					Party party = new Party();
					party = this.partyService.cachePartyBy(recom_parents.get(i).getReco_id(), true);
					if (!"MEMBER".equals(party.getRolename()) && !"GUEST".equals(party.getRolename())) {
						continue;
					}
					double pip_amount = Double.valueOf(recharge_bonus_array[i]);
					double get_money = Arith.mul(amount, pip_amount);

					String parentPartyId = String.valueOf(recom_parents.get(i).getReco_id());
					Wallet wallet = walletService.saveWalletByPartyId(parentPartyId);
					double amount_before = wallet.getMoney();
					walletService.update(parentPartyId, get_money);

					// 保存资金日志
					MoneyLog moneyLog = new MoneyLog();
					moneyLog.setCategory(Constants.MONEYLOG_CATEGORY_COIN);
					moneyLog.setAmount_before(amount_before);
					moneyLog.setAmount(get_money);
					moneyLog.setAmount_after(Arith.add(amount_before, get_money));
					moneyLog.setLog("第" + (i + 1) + "代用户"+party_user.getUsername()+"日充值数量总价值" + amount + "USDT，所奖励");
					moneyLog.setPartyId(parentPartyId);
					moneyLog.setWallettype("USDT");
					moneyLog.setContent_type(Constants.MONEYLOG_CONTENT_RECHARGE);
					moneyLogService.save(moneyLog);

					WalletLog walletLog = new WalletLog();
					walletLog.setCategory(Constants.MONEYLOG_CATEGORY_RECHARGE);
					walletLog.setPartyId(parentPartyId);
					walletLog.setOrder_no("");
					walletLog.setWallettype(Constants.WALLET);
					walletLog.setStatus(1);
					walletLog.setAmount(get_money);
					// 换算成USDT单位
					walletLog.setUsdtAmount(get_money);
					walletLogService.save(walletLog);
					
					// 记录userdata表充值返佣
					userDataService.saveUserDataForRechargeRecom(parentPartyId, get_money);
				}
			}
		}
		logger.info("DailyBounsHandle profit finished ,count:" + userDatas.size());
	}
	
	
	/**
	 * 查找某一天的前一天的 有 充值记录 的
	 */
	private List<UserData> findBydate( Date date) {
		Date createTime_begin = null;
		Date createTime_end = null;
		if (date != null) {
			createTime_end = DateUtils.toDate(DateUtils.format(date, "yyyy-MM-dd"));
			createTime_begin= DateUtils.addDate(createTime_end, -1);
		}
		List<UserData> list = (List<UserData>) getHibernateTemplate().find("FROM UserData WHERE  createTime >= ? and createTime < ? and recharge > 0 ",
				new Object[] { createTime_begin, createTime_end });
		if (list.size() > 0) {
			return list;
		}
		return null;
	}
	


	public void setUserRecomService(UserRecomService userRecomService) {
		this.userRecomService = userRecomService;
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

	public void setRechargeBlockchainService(RechargeBlockchainService rechargeBlockchainService) {
		this.rechargeBlockchainService = rechargeBlockchainService;
	}

	public void setWalletLogService(WalletLogService walletLogService) {
		this.walletLogService = walletLogService;
	}

	public void setUserDataService(UserDataService userDataService) {
		this.userDataService = userDataService;
	}

	public void setPartyService(PartyService partyService) {
		this.partyService = partyService;
	}

}
