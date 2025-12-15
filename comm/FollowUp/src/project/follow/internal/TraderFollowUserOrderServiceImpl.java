package project.follow.internal;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

import kernel.util.Arith;
import kernel.util.DateUtils;
import kernel.util.ThreadUtils;
import kernel.web.Page;
import kernel.web.PagedQueryDao;
import project.Constants;
import project.contract.ContractApplyOrder;
import project.contract.ContractApplyOrderService;
import project.contract.ContractOrder;
import project.contract.ContractOrderService;
import project.follow.Trader;
import project.follow.TraderFollowUser;
import project.follow.TraderFollowUserOrder;
import project.follow.TraderFollowUserOrderService;
import project.follow.TraderFollowUserService;
import project.follow.TraderOrder;
import project.follow.TraderOrderService;
import project.follow.TraderService;
import project.follow.TraderUser;
import project.follow.TraderUserService;
import project.log.MoneyLog;
import project.log.MoneyLogService;
import project.party.PartyService;
import project.party.model.Party;
import project.party.model.UserRecom;
import project.party.recom.UserRecomService;
import project.syspara.SysparaService;
import project.wallet.Wallet;
import project.wallet.WalletService;

public class TraderFollowUserOrderServiceImpl extends HibernateDaoSupport implements TraderFollowUserOrderService {
	private TraderService traderService;
	private ContractApplyOrderService contractApplyOrderService;
	private ContractOrderService contractOrderService;
	private TraderFollowUserService traderFollowUserService;
	private TraderUserService traderUserService;
	private PagedQueryDao pagedQueryDao;

	private WalletService walletService;
	private MoneyLogService moneyLogService;

	private TraderOrderService traderOrderService;
	private UserRecomService userRecomService;
	private PartyService partyService;
	private SysparaService sysparaService;

	public List<Map<String, Object>> getPaged(int pageNo, int pageSize, String partyId) {
		StringBuffer queryString = new StringBuffer(" SELECT orders.SYMBOL symbol,orders.AMOUNT_CLOSE amount_close,  "
				+ " orders.TRADE_AVG_PRICE trade_avg_price,  "
				+ " orders.DIRECTION direction,orders.UNIT_AMOUNT unit_amount,  "
				+ "  orders.STATE state,orders.FEE fee,orders.PROFIT profit, "
				+ " orders.DEPOSIT deposit,orders.DEPOSIT_OPEN deposit_open,orders.CLOSE_AVG_PRICE close_avg_price,  "
				+ "  orders.CLOSE_TIME closeTime,orders.CREATE_TIME createTime, "
				+ "  orders.VOLUME_OPEN volume_open,orders.VOLUME volume,item.NAME itemname, "
				+ " trader_user_order.USER_ORDER_NO order_no   ");
		queryString.append(" FROM T_TRADER_FOLLOW_USER_ORDER trader_user_order  ");
		queryString
				.append(" LEFT JOIN T_CONTRACT_ORDER orders ON orders.ORDER_NO  = trader_user_order.USER_ORDER_NO  ");
		queryString.append(" LEFT JOIN T_ITEM item ON orders.SYMBOL = item.SYMBOL  ");

		queryString.append("  WHERE 1 = 1 ");

		Map<String, Object> parameters = new HashMap();
		queryString.append(" and trader_user_order.PARTY_ID =:partyId");
		parameters.put("partyId", partyId);

		queryString.append(" order by trader_user_order.CREATE_TIME desc ");
		Page page = this.pagedQueryDao.pagedQuerySQL(pageNo, pageSize, queryString.toString(), parameters);

		List<Map<String, Object>> data = this.bulidData(page.getElements());
		return data;
	}

	private List<Map<String, Object>> bulidData(List<Map<String, Object>> traders) {
		List<Map<String, Object>> result_traders = new ArrayList();
		DecimalFormat df2 = new DecimalFormat("#.##");
		df2.setRoundingMode(RoundingMode.FLOOR);// 向下取整
		if (traders == null) {
			return result_traders;
		}
		for (int i = 0; i < traders.size(); i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			Map<String, Object> entity = traders.get(i);
			map.put("order_no", entity.get("order_no"));

			map.put("close_avg_price", entity.get("close_avg_price"));
			map.put("amount_close", entity.get("amount_close"));
			map.put("trade_avg_price", entity.get("trade_avg_price"));
			map.put("direction", entity.get("direction"));
			map.put("unit_amount", entity.get("unit_amount"));
			map.put("state", entity.get("state"));
			map.put("fee", entity.get("fee"));
			map.put("profit", entity.get("profit"));
			map.put("deposit", entity.get("deposit"));
			map.put("deposit_open", entity.get("deposit_open"));
			if (entity.get("closeTime") != null) {
				map.put("closeTime",
						DateUtils.format(
								DateUtils.toDate(entity.get("closeTime").toString(), DateUtils.DF_yyyyMMddHHmmss),
								"MM-dd HH:mm:ss"));
			} else {
				map.put("closeTime", "");
			}
			map.put("createTime",
					DateUtils.format(DateUtils.toDate(entity.get("createTime").toString(), DateUtils.DF_yyyyMMddHHmmss),
							"MM-dd HH:mm:ss"));

//			map.put("createTime", entity.get("createTime"));
			map.put("volume_open", entity.get("volume_open"));
			map.put("volume", entity.get("volume"));
			map.put("itemname", entity.get("itemname"));
			map.put("volume", entity.get("volume"));
			map.put("change_ratio", getChange_ratio(entity.get("state").toString(),
					Double.valueOf(entity.get("amount_close").toString()),
					Double.valueOf(entity.get("profit").toString()), Double.valueOf(entity.get("deposit").toString()),
					Double.valueOf(entity.get("deposit_open").toString())));

			result_traders.add(map);
		}

		return result_traders;

	}

	public Double getChange_ratio(String state, double amount_close, double profit, double deposit,
			double deposit_open) {
		double change_ratio = 0;
		if (ContractOrder.STATE_SUBMITTED.equals(state)) {
			change_ratio = Arith.div(Arith.sub(Arith.add(Arith.add(amount_close, profit), deposit), deposit_open),
					deposit_open);
		} else {
			change_ratio = Arith.div(Arith.sub(Arith.add(amount_close, deposit), deposit_open), deposit_open);

		}

		change_ratio = Arith.mul(change_ratio, 100);
		DecimalFormat df = new DecimalFormat("#.##");
		return Double.valueOf(df.format(change_ratio));
	}

	/**
	 * 判断是否是交易员
	 */
	public boolean isOrNotTrader(String partyId) {
		Trader trader = this.traderService.findByPartyId(partyId);
		if (trader != null) {
			return true;
		} else {
			return false;
		}
	}

	public void update(TraderFollowUserOrder entity) {
		this.getHibernateTemplate().update(entity);
	}

	@Override
	public void traderOpen(ContractOrder contractOrder) {
		if (isOrNotTrader(contractOrder.getPartyId().toString())) {
			CreateDelayThread lockDelayThread = new CreateDelayThread(contractOrder, this.contractApplyOrderService);
			Thread t = new Thread(lockDelayThread);
			t.start();
		}
	}

	@Override
	public void traderClose(ContractOrder contractOrder) {
		if (isOrNotTrader(contractOrder.getPartyId().toString())) {
			CloseDelayThread lockDelayThread = new CloseDelayThread(contractOrder, this.contractOrderService);
			Thread t = new Thread(lockDelayThread);
			t.start();

		}
//			else {
//			CloseOrderDelayThread lockOrderDelayThread = new CloseOrderDelayThread(contractOrder);
//			Thread t = new Thread(lockOrderDelayThread);
//			t.start();
//
//		}

	}

	/**
	 * 用户跟随交易员创建委托单
	 */
	public class CreateDelayThread implements Runnable {
		private ContractOrder contractOrder;
		private ContractApplyOrderService contractApplyOrderService;

		public void run() {
			try {
				List<TraderFollowUser> users = traderFollowUserService
						.findByTrader_partyId(contractOrder.getPartyId().toString());
				if (users != null) {
					for (TraderFollowUser user : users) {
						if (!"".equals(user.getPartyId())) {
							/**
							 * 判断当前用户最多还可以买几张
							 */
							try {
								List<TraderFollowUserOrder> userOrders = findByPartyIdAndTraderPartyIdAndState(
										user.getPartyId().toString(), contractOrder.getPartyId().toString(),
										ContractOrder.STATE_SUBMITTED);
								double volume_last = user.getVolume_max();
								if (userOrders != null) {
									for (TraderFollowUserOrder userOrder : userOrders) {
										volume_last = Arith.sub(volume_last, userOrder.getVolume());
									}
								}
								if (volume_last <= 0) {
									continue;
								}

								ContractApplyOrder order = new ContractApplyOrder();
								order.setPartyId(user.getPartyId());
								order.setSymbol(contractOrder.getSymbol());
								order.setDirection(contractOrder.getDirection());
								order.setOffset(ContractApplyOrder.OFFSET_OPEN);

								/**
								 * 跟单固定张数/固定比例---选择 1,固定张数，2，固定比例
								 */
								if ("1".equals(user.getFollow_type())) {
									if (volume_last < user.getVolume()) {
										order.setVolume(volume_last);
										order.setVolume_open(volume_last);
									} else {
										order.setVolume(user.getVolume());
										order.setVolume_open(user.getVolume());
									}
								}
								if ("2".equals(user.getFollow_type())) {
									if (volume_last < Arith.mul(contractOrder.getVolume_open(), user.getVolume())) {
										order.setVolume(volume_last);
										order.setVolume_open(volume_last);
									} else {
										order.setVolume(Arith.mul(contractOrder.getVolume_open(), user.getVolume()));
										order.setVolume_open(
												Arith.mul(contractOrder.getVolume_open(), user.getVolume()));
									}
								}
								order.setLever_rate(contractOrder.getLever_rate());
//								order.setPrice(contractOrder.getPrice());
								order.setStop_price_profit(contractOrder.getStop_price_profit());
								order.setStop_price_loss(contractOrder.getStop_price_loss());
								/**
								 * 默认市价单
								 */
								order.setOrder_price_type(ContractApplyOrder.ORDER_PRICE_TYPE_OPPONENT);

								if (order.getVolume_open() <= 0) {
									continue;
								}

								this.contractApplyOrderService.saveCreate(order);
								/**
								 * 跟单产生的交易手续费，奖励给推荐人
								 */
								saveFeeBounsHandle(order);
								/**
								 * 跟随交易员添加下单记录
								 */
								TraderFollowUserOrder traderFollowUserOrder = new TraderFollowUserOrder();
								traderFollowUserOrder.setPartyId(user.getPartyId());
								traderFollowUserOrder.setTrader_partyId(contractOrder.getPartyId());
								/**
								 * 交易员的持仓单号
								 */
								traderFollowUserOrder.setTrader_order_no(contractOrder.getOrder_no());
								traderFollowUserOrder.setUser_order_no(order.getOrder_no());
								traderFollowUserOrder.setVolume(order.getVolume_open());
								traderFollowUserOrder.setState(order.getState());
								traderFollowUserOrder.setCreate_time(order.getCreate_time());
								getHibernateTemplate().save(traderFollowUserOrder);

								/**
								 * 将数据加入用户跟随总累计收益表
								 */

								TraderUser traderUser = traderUserService.saveTraderUserByPartyId(user.getPartyId());
								traderUser.setAmount_sum(Arith.add(traderUser.getAmount_sum(),
										Arith.mul(order.getVolume_open(), order.getUnit_amount())));
								traderUserService.update(traderUser);
								/**
								 * 给用户跟随表添加累计金额
								 */
								user.setAmount_sum(Arith.add(user.getAmount_sum(),
										Arith.mul(order.getVolume_open(), order.getUnit_amount())));
								traderFollowUserService.update(user);
							} catch (Exception e) {
								logger.error("TraderFollowUserOrderServiceImpl_error:", e);
							}
						}
						ThreadUtils.sleep(10);
					}
				}
				/**
				 * 交易员自身数据更新
				 */
				Trader trader = traderService.findByPartyId(contractOrder.getPartyId().toString());
				trader.setOrder_amount(Arith.add(trader.getOrder_amount(),
						Arith.mul(contractOrder.getVolume_open(), contractOrder.getUnit_amount())));
				trader.setOrder_sum((int) Arith.add(trader.getOrder_sum(), 1));
				/**
				 * 近三周数据更新 累计数据更新
				 */
				traderService.updateTrader(trader);

			} catch (Exception e) {
				logger.error("error:", e);
			}
		}

		public CreateDelayThread(ContractOrder contractOrder, ContractApplyOrderService contractApplyOrderService) {
			this.contractOrder = contractOrder;
			this.contractApplyOrderService = contractApplyOrderService;
		}

	}

	/**
	 * 交易员平仓
	 *
	 */
	public class CloseDelayThread implements Runnable {
		private ContractOrder contractOrder;
		private ContractOrderService contractOrderService;

		public void run() {
			try {
				if (ContractOrder.STATE_CREATED.equals(contractOrder.getState())) {
					List<TraderFollowUserOrder> orders = findByTraderPartyIdAndOrder_no(
							contractOrder.getPartyId().toString(), contractOrder.getOrder_no(),
							ContractOrder.STATE_SUBMITTED);
					if (orders != null) {
						for (TraderFollowUserOrder order : orders) {
							try {
								if (ContractOrder.STATE_SUBMITTED.equals(order.getState())) {
									ContractOrder user_contract_order = this.contractOrderService
											.saveClose(order.getPartyId().toString(), order.getUser_order_no());
									order.setState(ContractOrder.STATE_CREATED);
									getHibernateTemplate().update(order);

									if (user_contract_order != null) {
										closeUserContractOrder(user_contract_order);
									}

								}
							} catch (Exception e) {
								logger.error("error:", e);
							} finally {
							}
							ThreadUtils.sleep(10);
						}
					}
					/**
					 * 交易员自身数据更新
					 */
					Trader trader = traderService.findByPartyId(contractOrder.getPartyId().toString());
					trader.setProfit(Arith.add(trader.getProfit(), contractOrder.getProfit()));
					if (contractOrder.getProfit() >= 0) {
						trader.setOrder_profit((int) Arith.add(trader.getOrder_profit(), 1));
					} else {
						trader.setOrder_loss((int) Arith.add(trader.getOrder_loss(), 1));
					}
					/**
					 * 近三周数据更新 累计数据更新
					 */
					traderService.updateTrader(trader);

					TraderOrder trader_order = new TraderOrder();
					trader_order.setPartyId(contractOrder.getPartyId());
					trader_order.setOrder_no(contractOrder.getOrder_no());
					trader_order.setSymbol(contractOrder.getSymbol());
					trader_order.setProfit(contractOrder.getProfit());
					trader_order.setChange_ratio(contractOrder.getChange_ratio());
					trader_order.setClose_avg_price(contractOrder.getClose_avg_price());
					trader_order.setTrade_avg_price(contractOrder.getTrade_avg_price());
					trader_order.setClose_time(contractOrder.getClose_time());
					trader_order.setCreate_time(contractOrder.getCreate_time());
					trader_order.setDirection(contractOrder.getDirection());
					trader_order.setLever_rate(contractOrder.getLever_rate());
					trader_order.setState(contractOrder.getState());
					trader_order.setVolume_open(contractOrder.getVolume_open());

					traderOrderService.save(trader_order);

				}

			} catch (Exception e) {
				logger.error("error:", e);
			}

		}

		public CloseDelayThread(ContractOrder contractOrder, ContractOrderService contractOrderService) {
			this.contractOrder = contractOrder;
			this.contractOrderService = contractOrderService;
		}

	}

	/**
	 * 用户平仓
	 * 
	 * @param contractOrder
	 */
	public void closeUserContractOrder(ContractOrder contractOrder) {
		ThreadUtils.sleep(1000);
		TraderFollowUserOrder traderFollowUserOrder = findByPartyIdAndOrderNo(contractOrder.getPartyId().toString(),
				contractOrder.getOrder_no());
		/**
		 * 是否跟单判断
		 */

		double follow_order_profit = 0;
		if (traderFollowUserOrder != null && contractOrder.getProfit() > 0) {
			Trader trader = traderService.findByPartyId(traderFollowUserOrder.getTrader_partyId().toString());
			follow_order_profit = Arith.mul(contractOrder.getProfit(), trader.getProfit_share_ratio());

			Wallet wallet = walletService.saveWalletByPartyId(contractOrder.getPartyId().toString());
			double wallet_before = wallet.getMoney();
			walletService.update(contractOrder.getPartyId().toString(), Arith.sub(0, follow_order_profit));
			MoneyLog moneylog = new MoneyLog();
			moneylog.setCategory(Constants.MONEYLOG_CATEGORY_CONTRACT);
			moneylog.setAmount_before(wallet_before);
			moneylog.setAmount(Arith.sub(0, follow_order_profit));
			moneylog.setAmount_after(Arith.sub(wallet_before, follow_order_profit));
			moneylog.setLog("交易员订单号[" + traderFollowUserOrder.getTrader_order_no() + "],跟单用户订单号["
					+ contractOrder.getOrder_no() + "],跟单手续费[" + Arith.sub(0, follow_order_profit) + "]");
			moneylog.setPartyId(contractOrder.getPartyId().toString());
			moneylog.setWallettype(Constants.WALLET);
			moneylog.setContent_type(Constants.MONEYLOG_CONTENT_FOLLOW_UP_FEE);

			moneyLogService.save(moneylog);

			Wallet wallet_trader = walletService.saveWalletByPartyId(trader.getPartyId().toString());
			double wallet_trader_before = wallet_trader.getMoney();
			walletService.update(wallet_trader.getPartyId().toString(), follow_order_profit);

			MoneyLog moneylog_trader = new MoneyLog();
			moneylog_trader.setCategory(Constants.MONEYLOG_CATEGORY_CONTRACT);
			moneylog_trader.setAmount_before(wallet_trader_before);
			moneylog_trader.setAmount(follow_order_profit);
			moneylog_trader.setAmount_after(Arith.add(wallet_trader_before, follow_order_profit));
			moneylog_trader.setLog("交易员订单号[" + traderFollowUserOrder.getTrader_order_no() + "],跟单用户订单号["
					+ contractOrder.getOrder_no() + "],带单手续费收益[" + follow_order_profit + "]");
			moneylog_trader.setPartyId(wallet_trader.getPartyId().toString());
			moneylog_trader.setWallettype(Constants.WALLET);
			moneylog_trader.setContent_type(Constants.MONEYLOG_CONTENT_FOLLOW_UP_FEE);

			moneyLogService.save(moneylog_trader);

			/**
			 * 检查是否是跟单订单，如果是需要将TraderFollowUserOrder里的订单状态修改
			 */

			if (traderFollowUserOrder != null) {
				traderFollowUserOrder.setState(contractOrder.getState());
				update(traderFollowUserOrder);

				/**
				 * 将收益加入用户跟随累计
				 */
				TraderUser traderUser = traderUserService.saveTraderUserByPartyId(contractOrder.getPartyId());
				traderUser.setProfit(Arith.add(traderUser.getProfit(), contractOrder.getProfit()));
				traderUserService.update(traderUser);

				TraderFollowUser traderFollowUser = traderFollowUserService.findByPartyIdAndTrader_partyId(
						traderFollowUserOrder.getPartyId().toString(),
						traderFollowUserOrder.getTrader_partyId().toString());
				/**
				 * 给用户跟随表添加累计金额
				 */
				traderFollowUser.setProfit(Arith.add(traderFollowUser.getProfit(), contractOrder.getProfit()));
				traderFollowUserService.update(traderFollowUser);

			}
			saveProfitBounsHandle(contractOrder);
		}
	}

	public TraderFollowUserOrder findByPartyIdAndOrderNo(String partyId, String apply_oder_no) {
		StringBuffer queryString = new StringBuffer(
				" FROM TraderFollowUserOrder where partyId=?0 and  user_order_no= ?1 ");
		List<TraderFollowUserOrder> list = (List<TraderFollowUserOrder>) getHibernateTemplate().find(queryString.toString(),
				new Object[] { partyId, apply_oder_no });
		if (list.size() > 0) {
			return list.get(0);
		}
		return null;
	}

	public List<TraderFollowUserOrder> findByPartyIdAndTraderPartyIdAndState(String partyId, String trader_partyId,
			String state) {
		StringBuffer queryString = new StringBuffer(
				" FROM TraderFollowUserOrder where partyId=?0 and trader_partyId = ?1 and state = ?2 ");
		List<TraderFollowUserOrder> list = (List<TraderFollowUserOrder>) getHibernateTemplate().find(queryString.toString(),
				new Object[] { partyId, trader_partyId, state });
		if (list.size() > 0) {
			return list;
		}
		return null;
	}

	public List<TraderFollowUserOrder> findByTraderPartyIdAndOrder_no(String trader_partyId, String trader_order_no,
			String state) {
		StringBuffer queryString = new StringBuffer(
				" FROM TraderFollowUserOrder where trader_partyId=?0 and  trader_order_no= ?1 and state = ?2 ");
		List<TraderFollowUserOrder> list = (List<TraderFollowUserOrder>) getHibernateTemplate().find(queryString.toString(),
				new Object[] { trader_partyId, trader_order_no, state });
		if (list.size() > 0) {
			return list;
		}
		return null;
	}

	/**
	 * 跟单产生手续费，奖励给推荐人
	 * 
	 * @param entity
	 */
	public void saveFeeBounsHandle(ContractApplyOrder entity) {
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
		 * 获取数据库奖金分成比例
		 */
		String trade_follow_bonus_parameters = sysparaService.find("trade_follow_bonus_parameters").getValue();
		String[] trade_follow_bonus_array = trade_follow_bonus_parameters.split(",");

		/**
		 * 判断有几个父级代理，最多不超过3个有奖励
		 */
		for (int i = 0; i < recom_parents.size(); i++) {
			if (i >= 3) {
				return;
			}
			/**
			 * 邀请人是正式用户和演示用户才加奖金
			 */
			Party party = new Party();
			party = this.partyService.cachePartyBy(recom_parents.get(i).getReco_id(), true);
			if (!"MEMBER".equals(party.getRolename()) && !"GUEST".equals(party.getRolename())) {
				continue;
			}
			double pip_amount = Double.valueOf(trade_follow_bonus_array[i]);
			double get_money = Arith.mul(entity.getFee(), pip_amount);

			Wallet wallet = walletService.saveWalletByPartyId(recom_parents.get(i).getReco_id());
			double amount_before = wallet.getMoney();
//				wallet.setMoney(Arith.add(wallet.getMoney(), get_money));
			walletService.update(wallet.getPartyId().toString(), get_money);

			/**
			 * 保存资金日志
			 */
			MoneyLog moneyLog = new MoneyLog();
			moneyLog.setCategory(Constants.MONEYLOG_CATEGORY_REWARD);
			moneyLog.setAmount_before(amount_before);
			moneyLog.setAmount(get_money);
			moneyLog.setAmount_after(Arith.add(amount_before, get_money));
			moneyLog.setLog("第" + (i + 1) + "代用户跟单产生了交易，手续费奖励[" + get_money + "]");
			moneyLog.setPartyId(recom_parents.get(i).getReco_id());
			moneyLog.setWallettype(Constants.WALLET);
			moneyLog.setContent_type(Constants.MONEYLOG_CONTENT_REWARD);
			moneyLogService.save(moneyLog);

		}

	}

	/**
	 * 跟单产生收益，奖励给推荐人
	 * 
	 * @param entity
	 */
	public void saveProfitBounsHandle(ContractOrder entity) {
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
		 * 获取数据库奖金分成比例
		 */
		String trade_follow_profit_bonus_parameters = sysparaService.find("trade_follow_profit_bonus_parameters")
				.getValue();
		String[] trade_follow_profit_bonus_array = trade_follow_profit_bonus_parameters.split(",");

		/**
		 * 判断有几个父级代理，最多不超过1个有奖励
		 */
		for (int i = 0; i < recom_parents.size(); i++) {
			if (i >= 1) {
				return;
			}
			/**
			 * 邀请人是正式用户和演示用户才加奖金
			 */
			Party party = new Party();
			party = this.partyService.cachePartyBy(recom_parents.get(i).getReco_id(), false);
			if (!"MEMBER".equals(party.getRolename()) && !"GUEST".equals(party.getRolename())) {
				continue;
			}
			double pip_amount = Double.valueOf(trade_follow_profit_bonus_array[i]);
			double get_money = Arith.mul(entity.getProfit(), pip_amount);

			Wallet wallet = walletService.saveWalletByPartyId(recom_parents.get(i).getReco_id());
			double amount_before = wallet.getMoney();
//				wallet.setMoney(Arith.add(wallet.getMoney(), get_money));
			walletService.update(wallet.getPartyId().toString(), get_money);

			/**
			 * 保存资金日志
			 */
			MoneyLog moneyLog = new MoneyLog();
			moneyLog.setCategory(Constants.MONEYLOG_CATEGORY_REWARD);
			moneyLog.setAmount_before(amount_before);
			moneyLog.setAmount(get_money);
			moneyLog.setAmount_after(Arith.add(amount_before, get_money));
			moneyLog.setLog("第" + (i + 1) + "代用户跟单产生了交易，分红奖励[" + get_money + "]");
			moneyLog.setPartyId(recom_parents.get(i).getReco_id());
			moneyLog.setWallettype(Constants.WALLET);
			moneyLog.setContent_type(Constants.MONEYLOG_CONTENT_REWARD);
			moneyLogService.save(moneyLog);

		}

	}

	public void setTraderService(TraderService traderService) {
		this.traderService = traderService;
	}

	public void setContractApplyOrderService(ContractApplyOrderService contractApplyOrderService) {
		this.contractApplyOrderService = contractApplyOrderService;
	}

	public void setTraderFollowUserService(TraderFollowUserService traderFollowUserService) {
		this.traderFollowUserService = traderFollowUserService;
	}

	public void setContractOrderService(ContractOrderService contractOrderService) {
		this.contractOrderService = contractOrderService;
	}

	public void setTraderUserService(TraderUserService traderUserService) {
		this.traderUserService = traderUserService;
	}

	public void setPagedQueryDao(PagedQueryDao pagedQueryDao) {
		this.pagedQueryDao = pagedQueryDao;
	}

	public void setWalletService(WalletService walletService) {
		this.walletService = walletService;
	}

	public void setMoneyLogService(MoneyLogService moneyLogService) {
		this.moneyLogService = moneyLogService;
	}

	public void setTraderOrderService(TraderOrderService traderOrderService) {
		this.traderOrderService = traderOrderService;
	}

	public void setUserRecomService(UserRecomService userRecomService) {
		this.userRecomService = userRecomService;
	}

	public void setPartyService(PartyService partyService) {
		this.partyService = partyService;
	}

	public void setSysparaService(SysparaService sysparaService) {
		this.sysparaService = sysparaService;
	}

}
