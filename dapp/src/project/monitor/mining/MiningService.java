package project.monitor.mining;

import java.util.List;

import project.monitor.mining.job.MiningIncome;
import project.party.model.Party;
import project.party.model.UserRecom;

public interface MiningService {
	/**
	 * 矿池收益计算 parents 是上级所有用户，调用处已经查过，传递到方法里减少访问量
	 */
	public List<MiningIncome> incomeProcess(Party item, MiningConfig config, List<UserRecom> parents);

	/**
	 * 批量保存，一次最好500个以内
	 */
	public void saveBatchIncome(List<MiningIncome> list);

	/**
	 * 获取到收益比率配置
	 * 
	 * @param money  金额
	 * @param config
	 * @return
	 */
	public double getIncomeRate(double money, MiningConfig config);
}
