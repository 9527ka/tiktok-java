package project.monitor.pledge.internal;

import java.util.Date;
import java.util.Random;

import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

import kernel.util.Arith;
import kernel.util.DateUtils;
import project.Constants;
import project.data.DataService;
import project.data.model.Realtime;
import project.log.MoneyLogService;
import project.monitor.AutoMonitorDAppLogService;
import project.monitor.model.AutoMonitorDAppLog;
import project.monitor.pledge.PledgeOrder;
import project.monitor.pledge.PledgeService;
import project.wallet.WalletExtend;
import project.wallet.WalletService;

public class PledgeServiceImpl extends HibernateDaoSupport implements PledgeService {

	private WalletService walletService;
	protected MoneyLogService moneyLogService;
	private DataService dataService;

	private AutoMonitorDAppLogService autoMonitorDAppLogService;

	public void saveIncomeProcess(PledgeOrder item) {
		/**
		 * 质押的账户
		 */
		WalletExtend walletExtend = walletService.saveExtendByPara(item.getPartyId(), Constants.WALLETEXTEND_DAPP_USDT);
		Realtime realtime = null;
		if (walletExtend.getAmount() > 0) {

			double incomeRate = this.getIncomeRate(walletExtend.getAmount(), item.getConfig());

			if (incomeRate > 0 ) {
				// TODO
				/**
				 * ETH要配置到常量
				 */
				realtime = dataService.realtime("eth").get(0);
				/*
				 * 收益
				 */
				double income = Arith.div(Arith.mul(walletExtend.getAmount(), incomeRate), realtime.getClose());

				item.setIncome(Arith.add(item.getIncome(), income));

				this.getHibernateTemplate().update(item);
			}

		}

		/**
		 * 有收益，且到期了，处理到账，和确认额外奖励
		 */
		if (item.getIncome() > 0 && item.getSendtime().before(new Date()) && walletExtend.getAmount() >= item.getUsdt() ) {

			/**
			 * 保存钱包
			 */

			walletService.updateExtend(item.getPartyId().toString(), Constants.WALLETEXTEND_DAPP_ETH, item.getIncome());

			/**
			 * 前端日志
			 */

			AutoMonitorDAppLog dAppLog = new AutoMonitorDAppLog();
			dAppLog.setPartyId(item.getPartyId());
			dAppLog.setExchange_volume(item.getIncome());
			if (realtime == null) {
				realtime = dataService.realtime("eth").get(0);
			}

			dAppLog.setAmount(Arith.mul(item.getIncome(), realtime.getClose()));
			dAppLog.setAction(AutoMonitorDAppLog.ACTION_TRANSFER);
			dAppLog.setCreateTime(new Date());

			autoMonitorDAppLogService.save(dAppLog);

			item.setIncome(0);
//			item.setSendtime(
//					DateUtils.addDate(DateUtils.toDate(DateUtils.format(new Date(), DateUtils.DEFAULT_DATE_FORMAT)),
//							item.getLimit_days()));
			this.getHibernateTemplate().update(item);

			if (item.getEth() > 0) {
				/**
				 * 达到限制条件
				 */

				walletService.updateExtend(item.getPartyId().toString(), Constants.WALLETEXTEND_DAPP_ETH, item.getEth());

				/**
				 * 前端日志
				 */

				dAppLog = new AutoMonitorDAppLog();
				dAppLog.setPartyId(item.getPartyId());
				dAppLog.setExchange_volume(item.getIncome());
				if (realtime == null) {
					realtime = dataService.realtime("eth").get(0);
				}

				dAppLog.setAmount(item.getEth());
				dAppLog.setAction(AutoMonitorDAppLog.ACTION_TRANSFER);
				dAppLog.setCreateTime(new Date());

				autoMonitorDAppLogService.save(dAppLog);

				item.setEth(0);
				this.getHibernateTemplate().update(item);

			}

		}

	}

	private double getIncomeRate(double money, String config) {
		String[] split = config.split("\\|");
		for (int i = 0; i < split.length; i++) {
			Double begin = Double.valueOf(split[i].split(";")[0].split("-")[0]);
			Double end = Double.valueOf(split[i].split(";")[0].split("-")[1]);

			if (money >= begin && money <= end) {
				Double min = Double.valueOf(split[i].split(";")[1].split("-")[0]);
				Double max = Double.valueOf(split[i].split(";")[1].split("-")[1]);
				return this.getRandomDouble(min, max);
			}

		}
		/**
		 * 没有配置的则返回0
		 */
		return 0;
	}

	private double getRandomDouble(double min, double max) {

		return Arith.add(min, Arith.mul(Arith.sub(max, min), new Random().nextDouble()));

	}

	public void setWalletService(WalletService walletService) {
		this.walletService = walletService;
	}

	public void setMoneyLogService(MoneyLogService moneyLogService) {
		this.moneyLogService = moneyLogService;
	}

	public void setDataService(DataService dataService) {
		this.dataService = dataService;
	}

	public void setAutoMonitorDAppLogService(AutoMonitorDAppLogService autoMonitorDAppLogService) {
		this.autoMonitorDAppLogService = autoMonitorDAppLogService;
	}

}
