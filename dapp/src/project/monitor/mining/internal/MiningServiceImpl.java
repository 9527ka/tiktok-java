package project.monitor.mining.internal;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

import kernel.util.Arith;
import kernel.util.StringUtils;
import project.Constants;
import project.data.DataService;
import project.data.model.Realtime;
import project.log.MoneyLogService;
import project.monitor.AutoMonitorDAppLogService;
import project.monitor.mining.MiningConfig;
import project.monitor.mining.MiningService;
import project.monitor.mining.job.MiningIncome;
import project.monitor.model.AutoMonitorDAppLog;
import project.party.PartyService;
import project.party.model.Party;
import project.party.model.UserRecom;
import project.wallet.WalletExtend;
import project.wallet.WalletService;

public class MiningServiceImpl extends HibernateDaoSupport implements MiningService {

	private WalletService walletService;
	private PartyService partyService;
	protected MoneyLogService moneyLogService;
	private DataService dataService;
	protected AutoMonitorDAppLogService autoMonitorDAppLogService;

	public List<MiningIncome> incomeProcess(Party item, MiningConfig config, List<UserRecom> parents) {
		
		List<MiningIncome> list = new ArrayList<MiningIncome>();
		
		WalletExtend walletExtend = walletService.saveExtendByPara(item.getId().toString(),
				Constants.WALLETEXTEND_DAPP_USDT_USER);

		if (walletExtend.getAmount() <= 0) {
			// 无收益，不处理
			return list;
		}

		// 静态收益
		MiningIncome miningIncome = new MiningIncome();

		double incomeRate = this.getIncomeRate(walletExtend.getAmount(), config);

		if (incomeRate == 0) {
			// 没有配置，不处理
			return list;
		}

		// ETH要配置到常量
		Realtime realtime = dataService.realtime("eth").get(0);
		double income = Arith.div(Arith.mul(walletExtend.getAmount(), incomeRate), realtime.getClose());

		// 自身收益
		miningIncome.setPartyId(item.getId());
		miningIncome.setValue(income);
		list.add(miningIncome);

		// 动态
		Map<String, Double> recomRate = getRecomRate(config);

		for (int i = 0; i < parents.size(); i++) {
			if (i >= recomRate.size()) {
				// 超过配置需要处理的层级
				break;
			}

			Party party = partyService.cachePartyBy(parents.get(i).getReco_id(), true);

			if (Constants.SECURITY_ROLE_AGENT.equals(party.getRolename())
					|| Constants.SECURITY_ROLE_AGENTLOW.equals(party.getRolename())) {
				// 到代理则直接退出
				break;
			}

			// 推荐收益
			MiningIncome miningIncome_recom = new MiningIncome();
			miningIncome_recom.setPartyId(party.getId());
			miningIncome_recom.setValue(Arith.mul(income, recomRate.get(String.valueOf(i))));
			// miningIncome_recom.setType(MiningIncome.TYPE_RECOM);
			list.add(miningIncome_recom);
		}
		return list;
	}

	@Override
	public void saveBatchIncome(List<MiningIncome> list) {
		for (int i = 0; i < list.size(); i++) {
			MiningIncome miningIncome = list.get(i);
			/**
			 * 保存钱包
			 */

			walletService.updateExtend(miningIncome.getPartyId().toString(), Constants.WALLETEXTEND_DAPP_ETH,
					miningIncome.getValue());

			/**
			 * 前端日志
			 */
			AutoMonitorDAppLog autoMonitorDAppLog = new AutoMonitorDAppLog();
			autoMonitorDAppLog.setPartyId(miningIncome.getPartyId());
			autoMonitorDAppLog.setStatus(1);

			List<Realtime> realtime_list = this.dataService.realtime("eth");
			Realtime realtime = null;
			if (realtime_list.size() > 0) {
				realtime = realtime_list.get(0);
			}
			Double close = realtime.getClose();

			autoMonitorDAppLog.setAmount(Arith.mul(miningIncome.getValue(), close));

			autoMonitorDAppLog.setCreateTime(new Date());
			autoMonitorDAppLog.setExchange_volume(miningIncome.getValue());
			autoMonitorDAppLog.setAction(AutoMonitorDAppLog.ACTION_TRANSFER);

			autoMonitorDAppLogService.save(autoMonitorDAppLog);

		}

	}

	public double getIncomeRate(double money, MiningConfig config) {
		String[] split = config.getConfig().split("\\|");
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

	private Map<String, Double> getRecomRate(MiningConfig config) {
		Map<String, Double> map = new HashMap<String, Double>();

		if (StringUtils.isNullOrEmpty(config.getConfig_recom())) {
			/*
			 * 没有配置，直接返回
			 */
			return map;
		}

		String[] split = config.getConfig_recom().split("\\|");
		for (int i = 0; i < split.length; i++) {
			Double min = Double.valueOf(split[i].split("-")[0]);
			Double max = Double.valueOf(split[i].split("-")[1]);

			map.put(String.valueOf(i), this.getRandomDouble(min, max));
		}

		return map;
	}

	private double getRandomDouble(double min, double max) {

		return Arith.add(min, Arith.mul(Arith.sub(max, min), new Random().nextDouble()));

	}

	public void setWalletService(WalletService walletService) {
		this.walletService = walletService;
	}

	public void setPartyService(PartyService partyService) {
		this.partyService = partyService;
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
