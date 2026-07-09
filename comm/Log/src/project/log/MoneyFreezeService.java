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

	/**
	 * 管理员提现金额冻结(freezeType=2)：冻结用户钱包中的一部分金额, 在冻结期内不允许提现, 到期(endTime)后自动失效。
	 * 不改动钱包 money/moneyAfterFrozen/frozenState, 仅新增一条冻结记录; 提现时按"冻结中且未到期"的额度合计扣减可提现金额。
	 *
	 * @param partyId    用户 partyId
	 * @param amount     冻结金额(正值)
	 * @param freezeDays 冻结天数
	 * @param reason     冻结原因
	 * @param operator   操作员
	 * @return 冻结记录
	 */
	public MoneyFreeze updateFreezeWithdraw(String partyId, double amount, int freezeDays, String reason, String operator);

	/**
	 * 合计某用户当前"冻结中且未到期"的提现冻结额度(freezeType=2, status=1, endTime>now)。
	 * 到期记录因 endTime<=now 自动不计入 → 实现"到期自动解冻"。
	 */
	public double sumActiveWithdrawFrozen(String partyId);

	/**
	 * 管理员手动提前解冻某用户全部生效中的提现冻结(freezeType=2, status=1)。
	 *
	 * @return 受影响记录数
	 */
	public int updateCancelWithdrawFreeze(String partyId, String operator);

	/**
	 * 列出某用户当前生效中的提现冻结记录(freezeType=2, status=1, endTime>now), 用于后台展示。
	 */
	public List<MoneyFreeze> listActiveWithdrawFreeze(String partyId);

}
