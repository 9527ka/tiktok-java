package project.miner.internal;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

import kernel.exception.BusinessException;
import kernel.util.Arith;
import kernel.util.DateUtils;
import kernel.util.StringUtils;
import kernel.web.Page;
import kernel.web.PagedQueryDao;
import project.Constants;
import project.contract.ContractOrder;
import project.contract.ContractRedisKeys;
import project.data.DataService;
import project.data.model.Realtime;
import project.log.Log;
import project.log.LogService;
import project.log.MoneyLog;
import project.log.MoneyLogService;
import project.miner.MinerOrderService;
import project.miner.MinerRedisKeys;
import project.miner.MinerService;
import project.miner.model.Miner;
import project.miner.model.MinerOrder;
import project.party.PartyService;
import project.party.model.Party;
import project.party.model.UserRecom;
import project.party.recom.UserRecomService;
import project.redis.RedisHandler;
import project.syspara.SysparaService;
import project.user.UserDataService;
import project.wallet.Wallet;
import project.wallet.WalletExtend;
import project.wallet.WalletService;
import security.SecUser;
import security.internal.SecUserService;

public class MinerOrderServiceImpl extends HibernateDaoSupport implements MinerOrderService {
	protected PagedQueryDao pagedDao;
	protected WalletService walletService;
	protected MoneyLogService moneyLogService;
	protected MinerService minerService;
	protected UserDataService userDataService;
	protected NamedParameterJdbcOperations namedParameterJdbcTemplate;
	protected UserRecomService userRecomService;
	protected PartyService partyService;
	private Logger log = LoggerFactory.getLogger(MinerOrderServiceImpl.class);
	protected SysparaService sysparaService;
	protected LogService logService;
	protected SecUserService secUserService;
	protected RedisHandler redisHandler;
	protected DataService dataService;

	/**
	 * 管理员新增订单
	 * 
	 * @param entity
	 * @param operator
	 */
	public void saveCreateByManage(MinerOrder entity, String operator) {
		saveCreate(entity, true);
		SecUser secUser = secUserService.findUserByPartyId(entity.getPartyId());
		Log log = new Log();
		log.setCategory(Constants.LOG_CATEGORY_OPERATION);
		log.setExtra(entity.getOrder_no());
		log.setOperator(operator);
		log.setUsername(secUser.getUsername());
		log.setPartyId(entity.getPartyId());
		log.setLog("手动新增矿机订单。订单号[" + entity.getOrder_no() + "],订单金额[" + entity.getAmount() + "]。");

		logService.saveSync(log);
	}

	/**
	 * 矿机下单
	 * 
	 * @param entity
	 * @param isManage 是否后台购买，后台则可以直接解锁所有矿机
	 */
	public void saveCreate(MinerOrder entity, boolean isManage) {

		entity.setCreate_time(new Date());

//		Party party = this.partyService.findPartyById(entity.getPartyId());
//		if (!party.getEnabled()) {
//			throw new BusinessException(1, "No permission");
//		}
		/**
		 * 加入周期
		 */
		Miner miner = minerService.findById(entity.getMinerId());
		if (null == miner) {
			throw new BusinessException("矿机不存在");
		}
		if (!isManage && "0".equals(miner.getOn_sale())) {// 管理员解锁所有，用户正常流程
//			if (!this.getUnLockMiner(entity.getPartyId().toString(), miner.getId().toString())) {
			throw new BusinessException("矿机未解锁，无法购买");
//			}
		}
//		entity.setAmount(Arith.mul(miner.getInvestment_min(), entity.getVolume()));
//		entity.setCycle(miner.getCycle());

		if (miner.getTest() && this.findByTest(entity.getPartyId().toString())) {// 买过体验机则
			throw new BusinessException("您已购买过体验矿机,不得重复购买");
		}
		/**
		 * 买入金额需要在区间内
		 */
//		if (entity.getAmount() < miner.getInvestment_min()) {
//			throw new BusinessException("不得低于该矿机的金额");
//
//		}
		/**
		 * 买入金额需要在区间内(非体验矿机)
		 */
		if (!miner.getTest()
				&& (entity.getAmount() < miner.getInvestment_min() || entity.getAmount() > miner.getInvestment_max())) {
			if (miner.getInvestment_max() != 0) {
				throw new BusinessException("买入金额需要在区间内");
			} else if (entity.getAmount() < miner.getInvestment_min()) {// 无限制的矿机不得小于最小购买金额
				throw new BusinessException("买入金额需要在区间内");
			}
		}

		String minerBuySymbol = sysparaService.find("miner_buy_symbol").getValue();
		// 是否是其他币种购买
		boolean isOtherCoin = !StringUtils.isEmptyString(minerBuySymbol);
		double close = 0d;
		if (isOtherCoin) {
			List<Realtime> realtimes = this.dataService.realtime(minerBuySymbol);
			if (CollectionUtils.isEmpty(realtimes) || null == realtimes.get(0)) {
				throw new BusinessException("系统错误，请稍后重试");
			}
			close = realtimes.get(0).getClose();

			saveMinerBuyOtherCoin(entity, minerBuySymbol);
		} else {
			/**
			 * 查看余额
			 */
			Wallet wallet = this.walletService.saveWalletByPartyId(entity.getPartyId());
			double amount_before = wallet.getMoney();
			if (wallet.getMoney() < entity.getAmount()) {
				throw new BusinessException("余额不足");
			}

//		wallet.setMoney(Arith.sub(wallet.getMoney(), entity.getAmount()));
			this.walletService.update(wallet.getPartyId().toString(), Arith.sub(0, entity.getAmount()));
			/**
			 * 保存资金日志
			 */

			MoneyLog moneylog = new MoneyLog();
			moneylog.setCategory(Constants.MONEYLOG_CATEGORY_MINER);
			moneylog.setAmount_before(amount_before);
			moneylog.setAmount(Arith.sub(0, entity.getAmount()));
			moneylog.setAmount_after(Arith.sub(amount_before, entity.getAmount()));
			moneylog.setLog("购买矿机产品，订单号[" + entity.getOrder_no() + "]");
			moneylog.setPartyId(entity.getPartyId());
			moneylog.setWallettype(Constants.WALLET);
			moneylog.setContent_type(Constants.MONEYLOG_CONTENT_MINER_BUY);

			moneyLogService.save(moneylog);
		}
		/**
		 * 起息时间=确认时间加1天
		 */
		Date date = new Date();// 取时间
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		calendar.add(calendar.DATE, 1);// 把日期往后增加一天.整数往后推,负数往前移动
		date = calendar.getTime(); // 这个时间就是日期往后推一天的结果
		entity.setEarn_time(date);

		if (miner.getTest()) {
			/**
			 * 截止时间=起息时间+周期+1
			 */
//			Date date = new Date();
//			Calendar calendar1 = new GregorianCalendar();
			int days = (int) Arith.sub(miner.getCycle(), 1);
			calendar.add(calendar.DATE, days);
			date = calendar.getTime();

			entity.setStop_time(DateUtils.getDayEnd(date));
			entity.setAmount(0d);// 体验矿机不管输入多少都为0
		}
		if (findByFist(entity.getPartyId().toString())) {
			entity.setFirst_buy("1");// 标识首次购买
		} else {
			entity.setFirst_buy("0");// 标识首次购买
		}
		this.getHibernateTemplate().save(entity);

		redisHandler.setSync(MinerRedisKeys.MINER_ORDER_ORDERNO + entity.getOrder_no(), entity);

		if (!miner.getTest()) {
			
			Map<String, MinerOrder> map_partyid = (Map<String, MinerOrder>) redisHandler
					.get(MinerRedisKeys.MINER_ORDER_PARTY_ID + entity.getPartyId().toString());

			if (map_partyid == null) {
				map_partyid = new ConcurrentHashMap<String, MinerOrder>();
			}
			map_partyid.put(entity.getOrder_no(), entity);
			redisHandler.setSync(MinerRedisKeys.MINER_ORDER_PARTY_ID + entity.getPartyId().toString(), map_partyid);
			
			// 状态：0/正常赎回； 1/ 托管中 ；2/提前赎回 (违约)；3/取消；
			if ("1".equals(entity.getState())) {
				
				// 获取 单个订单 矿机总资产
				Double minerAssetsOrder = entity.getAmount();

				Double minerAssets = (Double) this.redisHandler.get(MinerRedisKeys.MINER_ASSETS_PARTY_ID + entity.getPartyId().toString());

				this.redisHandler.setSync(MinerRedisKeys.MINER_ASSETS_PARTY_ID + entity.getPartyId().toString(), 
						Arith.add(null == minerAssets ? 0.000D : minerAssets, minerAssetsOrder));
			}			
		}
		
		/**
		 * 首次购买则给予上级额外本金百分比奖励
		 */
		if ("1".equals(entity.getFirst_buy()) && !miner.getTest()) {
			List<UserRecom> list_parents = this.userRecomService.getParents(entity.getPartyId());

			if (CollectionUtils.isNotEmpty(list_parents)) {
				String miner_bonus_parameters = "";
				miner_bonus_parameters = sysparaService.find("miner_first_bonus_parameters").getValue();
				String[] miner_bonus_array = miner_bonus_parameters.split(",");
				int loop = 0;
				for (int i = 0; i < list_parents.size(); i++) {
					if (loop >= 3) {
						break;
					}
					Party party_parent = this.partyService.cachePartyBy(list_parents.get(i).getReco_id(), true);
					if (!Constants.SECURITY_ROLE_MEMBER.equals(party_parent.getRolename())) {
						continue;
					}
					loop++;
					Map<String, MinerOrder> map_party = (Map<String, MinerOrder>) redisHandler
							.get(MinerRedisKeys.MINER_ORDER_PARTY_ID + party_parent.getId().toString());
					if (map_party == null || map_party.size() == 0) {
						continue;
					}
					/*
					 * 判断是否
					 */
					int cycle = 0;
					Iterator<Entry<String, MinerOrder>> it = map_party.entrySet().iterator();
					while (it.hasNext()) {
						Entry<String, MinerOrder> entry = it.next();
						MinerOrder minerOrder = entry.getValue();
						if (!"1".equals(minerOrder.getState())) {
							continue;
						}
						Miner miner_party = this.minerService.cacheById(minerOrder.getMinerId());
						if (cycle < miner_party.getCycle()) {
							cycle = miner_party.getCycle();
						}

					}

					if (cycle >= miner.getCycle()) {
						/**
						 * 增加首次推荐人收益
						 */
						double pip_amount = Double.valueOf(miner_bonus_array[loop - 1]);
						double get_money = Arith.mul(entity.getAmount(), pip_amount);

						if (isOtherCoin) {
							firstBuyProfitOtherCoin(minerBuySymbol, party_parent, get_money, i);
							// 转化成usdt，统计计算
							userDataService.saveMinerProfit(party_parent.getId().toString(),
									Arith.div(get_money, close));
						} else {
							Wallet wallet_parent = walletService.saveWalletByPartyId(party_parent.getId().toString());
							double amount_before_parent = wallet_parent.getMoney();
//						wallet_parent.setMoney(Arith.add(wallet_parent.getMoney(), get_money));
//						walletService.update(wallet_parent);
							walletService.update(wallet_parent.getPartyId().toString(), get_money);

							/**
							 * 保存资金日志
							 */
							MoneyLog moneyLog = new MoneyLog();
							moneyLog.setCategory(Constants.MONEYLOG_CATEGORY_MINER);
							moneyLog.setAmount_before(amount_before_parent);
							moneyLog.setAmount(get_money);
							moneyLog.setAmount_after(Arith.add(amount_before_parent, get_money));
							moneyLog.setLog("第" + (i + 1) + "代下级用户，首次购买矿机推荐奖励金");
							moneyLog.setPartyId(party_parent.getId().toString());
							moneyLog.setWallettype(Constants.WALLET);
							moneyLog.setContent_type(Constants.MONEYLOG_CONTENT_MINER_RECOM_PROFIT);
							moneyLogService.save(moneyLog);
							userDataService.saveMinerProfit(party_parent.getId().toString(), get_money);

						}

					}
				}

			}
		}

		/**
		 * userdata 数据
		 */
		if (isOtherCoin) {
			userDataService.saveMinerBuy(minerUserDataOtherCoin(entity, minerBuySymbol, close));
		} else {
			userDataService.saveMinerBuy(entity);
		}

	}

	/**
	 * 增加首次推荐人收益
	 */
	protected void firstBuyProfitOtherCoin(String symbol, Party partyParent, double getMoney, int i) {
		WalletExtend walletExtend = walletService.saveExtendByPara(partyParent.getId().toString(), symbol);
		double amount_before_parent = walletExtend.getAmount();
		this.walletService.updateExtend(walletExtend.getPartyId().toString(), symbol, getMoney);

		/**
		 * 保存资金日志
		 */
		MoneyLog moneyLog = new MoneyLog();
		moneyLog.setCategory(Constants.MONEYLOG_CATEGORY_MINER);
		moneyLog.setAmount_before(amount_before_parent);
		moneyLog.setAmount(getMoney);
		moneyLog.setAmount_after(Arith.add(amount_before_parent, getMoney));
		moneyLog.setLog("第" + (i + 1) + "代下级用户，首次购买矿机推荐奖励金");
		moneyLog.setPartyId(partyParent.getId().toString());
		moneyLog.setWallettype(symbol);
		moneyLog.setContent_type(Constants.MONEYLOG_CONTENT_MINER_RECOM_PROFIT);
		moneyLogService.save(moneyLog);
		userDataService.saveMinerProfit(walletExtend.getPartyId().toString(), getMoney);

	}

	protected MinerOrder minerUserDataOtherCoin(MinerOrder entity, String symbol, double close) {
		MinerOrder order = new MinerOrder();
		// 不改变原有的
		BeanUtils.copyProperties(entity, order);
		// 转化成usdt，统计计算
		order.setAmount(Arith.div(order.getAmount(), close));
		return order;
	}

	protected void saveMinerBuyOtherCoin(MinerOrder entity, String symbol) {
		WalletExtend walletExtend = walletService.saveExtendByPara(entity.getPartyId(), symbol);

		if (entity.getAmount() > walletExtend.getAmount()) {
			throw new BusinessException("持有币种不足");
		}

		double amount_before = walletExtend.getAmount();
//		walletExtend.setAmount(Arith.sub(walletExtend.getAmount(), order.getVolume()));

//		walletService.save(walletExtend);
		walletService.updateExtend(walletExtend.getPartyId().toString(), walletExtend.getWallettype(),
				Arith.sub(0, entity.getAmount()));
		/*
		 * 保存资金日志
		 */
		MoneyLog moneylog = new MoneyLog();
		moneylog.setCategory(Constants.MONEYLOG_CATEGORY_MINER);
		moneylog.setAmount_before(amount_before);
		moneylog.setAmount(Arith.sub(0, entity.getAmount()));
		moneylog.setAmount_after(Arith.sub(amount_before, entity.getAmount()));
		moneylog.setLog("购买矿机产品，订单号[" + entity.getOrder_no() + "]");
		moneylog.setPartyId(entity.getPartyId());
		moneylog.setWallettype(symbol);
		moneylog.setContent_type(Constants.MONEYLOG_CONTENT_MINER_BUY);
		moneyLogService.save(moneylog);
	}

	protected void saveMinerCloseOtherCoin(MinerOrder entity, String symbol) {

		WalletExtend walletExtend = walletService.saveExtendByPara(entity.getPartyId(), symbol);

		double amount_before = walletExtend.getAmount();
		double back_money = entity.getAmount();
//		walletExtend.setAmount(Arith.add(walletExtend.getAmount(), amount));
//		this.walletService.update(walletExtend);
		this.walletService.updateExtend(walletExtend.getPartyId().toString(), walletExtend.getWallettype(), back_money);

		/*
		 * 保存资金日志
		 */
		MoneyLog moneylog_deposit = new MoneyLog();
		moneylog_deposit.setCategory(Constants.MONEYLOG_CATEGORY_MINER);
		moneylog_deposit.setAmount_before(amount_before);
		moneylog_deposit.setAmount(back_money);
		moneylog_deposit.setAmount_after(Arith.add(amount_before, back_money));
		moneylog_deposit.setLog("矿机本金退回，订单号[" + entity.getOrder_no() + "]");
		moneylog_deposit.setPartyId(entity.getPartyId());
		moneylog_deposit.setWallettype(symbol);
		moneylog_deposit.setContent_type(Constants.MONEYLOG_CONTENT_MINER_BACK);

		moneyLogService.save(moneylog_deposit);
	}

	public void saveClose(MinerOrder entity) {
		String minerBuySymbol = sysparaService.find("miner_buy_symbol").getValue();
		// 是否是其他币种购买
		boolean isOtherCoin = !StringUtils.isEmptyString(minerBuySymbol);
		double close = 0;
		if (isOtherCoin) {
			List<Realtime> realtimes = this.dataService.realtime(minerBuySymbol);
			if (CollectionUtils.isEmpty(realtimes) || null == realtimes.get(0)) {
				throw new BusinessException("系统错误，请稍后重试");
			}
			close = realtimes.get(0).getClose();

			saveMinerCloseOtherCoin(entity, minerBuySymbol);
		} else if (entity.getAmount() != 0) {// 体验矿机，购买价为0
			Wallet wallet = this.walletService.saveWalletByPartyId(entity.getPartyId());
			double amount_before = wallet.getMoney();
			// 购买金额-违约金=退还金额
//			double back_money = Arith.sub(entity.getAmount(), entity.getDefault_money());
			double back_money = entity.getAmount();
//			if ("0".equals(entity.getState())) {// 正常状态下 到期后一天 奖励5%
//				double profit = entity.getCompute_profit();
//				back_money = profit;
//			}
//			wallet.setMoney(Arith.add(wallet.getMoney(), back_money));
			this.walletService.update(wallet.getPartyId().toString(), back_money);
			/**
			 * 保存资金日志
			 */
			MoneyLog moneylog = new MoneyLog();
			moneylog.setCategory(Constants.MONEYLOG_CATEGORY_MINER);
			moneylog.setAmount_before(amount_before);
			moneylog.setAmount(Arith.add(0, back_money));
			moneylog.setAmount_after(Arith.add(amount_before, back_money));
			moneylog.setPartyId(entity.getPartyId());
			moneylog.setWallettype(Constants.WALLET);
			moneylog.setLog("矿机本金退回，订单号[" + entity.getOrder_no() + "]");
			moneylog.setContent_type(Constants.MONEYLOG_CONTENT_MINER_BACK);
			moneyLogService.save(moneylog);
		}

		entity.setClose_time(new Date());// 赎回时间
		getHibernateTemplate().update(entity);
		/**
		 * userdata 数据
		 */
		if (isOtherCoin) {
			userDataService.saveMinerClose(minerUserDataOtherCoin(entity, minerBuySymbol, close));
		} else {
			userDataService.saveMinerClose(entity);
		}

		Miner miner = this.minerService.cacheById(entity.getMinerId());

		// 更新矿机订单
		redisHandler.setSync(MinerRedisKeys.MINER_ORDER_ORDERNO + entity.getOrder_no(), entity);
		if (!miner.getTest()) {
			
			Map<String, MinerOrder> map_partyid = (Map<String, MinerOrder>) redisHandler
					.get(MinerRedisKeys.MINER_ORDER_PARTY_ID + entity.getPartyId().toString());
			if (map_partyid == null) {
				map_partyid = new ConcurrentHashMap<String, MinerOrder>();
			}
			
			MinerOrder minerOld = map_partyid.get(entity.getOrder_no());
			
			map_partyid.put(entity.getOrder_no(), entity);
			redisHandler.setSync(MinerRedisKeys.MINER_ORDER_PARTY_ID + entity.getPartyId().toString(), map_partyid);
			
			Double minerAssets = (Double) this.redisHandler.get(MinerRedisKeys.MINER_ASSETS_PARTY_ID + entity.getPartyId().toString());
									
			// 状态：0/正常赎回； 1/ 托管中 ；2/提前赎回 (违约)；3/取消；
			if ("1".equals(minerOld.getState())) {

				// 获取 单个订单 矿机总资产
				Double minerAssetsOld = minerOld.getAmount();
				
				minerAssets = null == minerAssets ? 0.000D - minerAssetsOld : minerAssets - minerAssetsOld;
			}

			// 状态：0/正常赎回； 1/ 托管中 ；2/提前赎回 (违约)；3/取消；
			if ("1".equals(entity.getState())) {

				// 获取 单个订单 矿机总资产
				Double minerAssetsOrder = entity.getAmount();
								
				minerAssets = null == minerAssets ? 0.000D + minerAssetsOrder : minerAssets + minerAssetsOrder;
			}

			this.redisHandler.setSync(MinerRedisKeys.MINER_ASSETS_PARTY_ID + entity.getPartyId().toString(), null == minerAssets ? 0.000D : minerAssets);
		}
	}

	public List<MinerOrder> findAllByState(String state) {
		List<MinerOrder> list = (List<MinerOrder>)this.getHibernateTemplate().find(" FROM MinerOrder WHERE state = ?0 ",
				new Object[] { state });
		return list;
	}

	@Override
	public MinerOrder findByOrder_no(String order_no) {

		return (MinerOrder) redisHandler.get(MinerRedisKeys.MINER_ORDER_ORDERNO + order_no);

	}

	public List<MinerOrder> findByState(String partyId, String state) {
		if (StringUtils.isEmptyString(partyId)) {
			return findAllByState(state);
		}
		/**
		 * 如果是查询已赎回的，则将提前赎回和正常赎回的都查出来
		 */
		List<MinerOrder> list;
		if (StringUtils.isEmptyString(state)) {
			list = (List<MinerOrder>)getHibernateTemplate().find(" FROM MinerOrder WHERE  partyId =?0", new Object[] { partyId });
		} else {
			if ("2".equals(state)) {
				list = (List<MinerOrder>)getHibernateTemplate().find(" FROM MinerOrder WHERE partyId =?0 and ( state = ?1 or state =?2 )  ",
						new Object[] { partyId, "0", "2" });
			} else {
				list = (List<MinerOrder>)getHibernateTemplate().find(" FROM MinerOrder WHERE state = ?0 and partyId =?1",
						new Object[] { state, partyId });
			}
		}

		if (list.size() > 0)
			return list;
		return null;
	}

	public Page pagedQuery(int pageNo, int pageSize, String partyId, String state) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		StringBuffer queryString = new StringBuffer("SELECT ");
		queryString.append(" minerOrder.UUID id,minerOrder.ORDER_NO order_no ,minerOrder.MINER_ID minerId  , ");
		queryString.append(
				" minerOrder.AMOUNT amount,DATE_FORMAT(minerOrder.CREATE_TIME,'%Y-%m-%d %H:%i:%S') create_time, ");
		queryString.append(
				" DATE(minerOrder.EARN_TIME) earn_time,DATE(minerOrder.STOP_TIME) stop_time,minerOrder.PROFIT profit, ");
		queryString.append(" minerOrder.STATE state,minerOrder.BASE_COMPUTE_AMOUNT base_compute_amount, ");
		queryString.append(
				" miner.CYCLE cycle,miner.CYCLE_CLOSE cycle_close,miner.DAILY_RATE daily_rate,miner.TEST test, ");
		queryString.append(" miner.NAME miner_name,miner.NAME_EN miner_name_en,miner.NAME_CN miner_name_cn ");
		queryString.append("FROM T_MINER_ORDER minerOrder ");
		queryString.append("LEFT JOIN T_MINER miner ON miner.UUID= minerOrder.MINER_ID ");

		queryString.append("WHERE 1=1 ");
		queryString.append("AND minerOrder.PARTY_ID=:partyId ");
		parameters.put("partyId", partyId);
		if (StringUtils.isNotEmpty(state)) {
			if ("2".equals(state)) {
				queryString.append("AND minerOrder.STATE in('0','2') ");
			} else {
				queryString.append("AND minerOrder.STATE=:state ");
				parameters.put("state", state);
			}
		}
		queryString.append(" order by minerOrder.CREATE_TIME desc ");

		Page page = this.pagedDao.pagedQuerySQL(pageNo, pageSize, queryString.toString(), parameters);

		return page;
	}

	public void deleteAllByPartyId(String partyId) {
		List<MinerOrder> list = (List<MinerOrder>)this.getHibernateTemplate().find(" FROM MinerOrder WHERE partyId=? ",
				new Object[] { partyId });
		if (!CollectionUtils.isEmpty(list)) {
			for (MinerOrder order : list) {
				this.getHibernateTemplate().delete(order);
				redisHandler.remove(MinerRedisKeys.MINER_ORDER_ORDERNO + order.getOrder_no());
			}
			redisHandler.remove(MinerRedisKeys.MINER_ORDER_PARTY_ID + partyId);
			
			this.redisHandler.remove(MinerRedisKeys.MINER_ASSETS_PARTY_ID + partyId);
		}
	}

	public void setWalletService(WalletService walletService) {
		this.walletService = walletService;
	}

	public void setMoneyLogService(MoneyLogService moneyLogService) {
		this.moneyLogService = moneyLogService;
	}

	public void setMinerService(MinerService minerService) {
		this.minerService = minerService;
	}

	public MinerOrder findById(String id) {// 赎回时使用
		return (MinerOrder) getHibernateTemplate().get(MinerOrder.class, id);
	}

	/**
	 * true:买过体验矿机，false:没买过
	 */
	public boolean findByTest(String partyId) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		StringBuffer queryString = new StringBuffer("SELECT ");
		queryString.append("mo.UUID ");
		queryString.append("FROM T_MINER_ORDER mo ");
		queryString.append("LEFT JOIN T_MINER m ON m.UUID=mo.MINER_ID ");
		queryString.append("WHERE 1=1  ");
		queryString.append("AND PARTY_ID=:partyId AND m.TEST='Y' ");
		parameters.put("partyId", partyId);
		List<Map<String, Object>> list = namedParameterJdbcTemplate.queryForList(queryString.toString(), parameters);
		return list != null && CollectionUtils.isNotEmpty(list) && list.get(0) != null;// 存在返回值，且不为空
	}

	/**
	 * true：首次购买，false:非首次购买
	 * 
	 * @param partyId
	 * @return
	 */
	public boolean findByFist(String partyId) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		StringBuffer queryString = new StringBuffer("SELECT ");
		queryString.append("mo.UUID ");
		queryString.append("FROM T_MINER_ORDER mo ");
		queryString.append("LEFT JOIN T_MINER m ON m.UUID=mo.MINER_ID ");
		queryString.append("WHERE 1=1  ");
		queryString.append("AND PARTY_ID=:partyId AND m.TEST='N' ");
		queryString.append("AND FIRST_BUY='1' ");
		parameters.put("partyId", partyId);
		List<Map<String, Object>> list = namedParameterJdbcTemplate.queryForList(queryString.toString(), parameters);
		return !(list != null && CollectionUtils.isNotEmpty(list) && list.get(0) != null);// 存在返回值，且不为空
	}

	public boolean getUnLockMiner(String partyId, String minerId) {
		Miner miner = this.minerService.cacheById(minerId);

		List<UserRecom> list_userRecoms = this.userRecomService.findRecoms(partyId);
		int cycle = 0;
		for (int i = 0; i < list_userRecoms.size(); i++) {
			Map<String, MinerOrder> map = (Map<String, MinerOrder>) redisHandler
					.get(MinerRedisKeys.MINER_ORDER_PARTY_ID + list_userRecoms.get(i).getPartyId().toString());

			if (map != null) {
				cycle = cycle + map.size();
			}
		}

		return cycle >= miner.getCycle();// 如果周期比该矿机的大，则解锁
	}

	public void setPagedDao(PagedQueryDao pagedDao) {
		this.pagedDao = pagedDao;
	}

	public void setUserDataService(UserDataService userDataService) {
		this.userDataService = userDataService;
	}

	public void setNamedParameterJdbcTemplate(NamedParameterJdbcOperations namedParameterJdbcTemplate) {
		this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
	}

	public void setUserRecomService(UserRecomService userRecomService) {
		this.userRecomService = userRecomService;
	}

	public void setSysparaService(SysparaService sysparaService) {
		this.sysparaService = sysparaService;
	}

	public void setPartyService(PartyService partyService) {
		this.partyService = partyService;
	}

	public void setLogService(LogService logService) {
		this.logService = logService;
	}

	public void setSecUserService(SecUserService secUserService) {
		this.secUserService = secUserService;
	}

	public void setRedisHandler(RedisHandler redisHandler) {
		this.redisHandler = redisHandler;
	}

	public void setDataService(DataService dataService) {
		this.dataService = dataService;
	}

}
