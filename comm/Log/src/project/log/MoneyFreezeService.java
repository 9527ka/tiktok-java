package project.log;

import kernel.web.Page;

import java.util.List;

public interface MoneyFreezeService {

	public void save(MoneyFreeze entity);

	public MoneyFreeze getById(String id);

	/**
	 * 冻结资金的完整逻辑
	 *
	 * @param sellerId
	 * @param freezeAmout
	 * @param freezeDays
	 * @param freezeReason
	 * @param operator
	 */
	public MoneyFreeze updateFreezeSeller(String sellerId, double freezeAmout, int freezeDays, String freezeReason, String operator);

	/**
	 * 解冻资金的完整逻辑
	 *
	 * @param id
	 * @param operator
	 */
	public int updateUnFreezeSeller(String id, String operator);

	/**
	 * 定时器解冻资金的完整旧逻辑
	 *
	 * @param id
	 * @param operator
	 */
	public int updateAutoUnFreezeSeller(String id, String operator);

	/**
	 * 将冻结记录状态修改为结束冻结
	 *
	 * @param id
	 * @param operator
	 * @return
	 */
	public int updateSetUnFreezeState(String id, String operator);

	public List<String> listPendingFreezeRecords();

	public List<MoneyFreeze> listPendingFreezeRecords(int size);

	public Page pagedListFreeze(String partyId, int status, int pageNum, int pageSize);

	public List<MoneyFreeze> listByIds(List<String> ids);

	public MoneyFreeze getLastFreezeRecord(String sellerId);

}
