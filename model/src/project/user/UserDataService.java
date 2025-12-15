package project.user;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

import project.contract.ContractOrder;
import project.exchange.ExchangeApplyOrder;
import project.finance.FinanceOrder;
import project.futures.FuturesOrder;
import project.miner.model.MinerOrder;
import project.wallet.dto.PartySumDataDTO;

public interface UserDataService {

	public Map<String, UserData> cacheByPartyId(String partyId);

	public List<Map<String, UserData>> cacheByPartyIds(List<String> partyIds);

	public void save(UserData entity);

	/**
	 * 取到该用户伞下4层被推荐人的partyId，Map是<level_1,List<partyId>>格式
	 * 
	 * @param partyId
	 */
	public ChildrenLever getCacheChildrenLever4(String partyId);
	
	/**
	 * 资金盘
	 */
	public List<Map<String, Object>> getChildrenLevelPagedForGalaxy(int pageNo, int pageSize, String partyId, Integer level);


	/**
	 * 抢单
	 */
	public List<Map<String, Object>> getChildrenLevelPagedForBrush(int pageNo, int pageSize, String partyId, Integer level);

	/**
	 * 交易所
	 */
	public List<Map<String, Object>> getChildrenLevelPaged(int pageNo, int pageSize, String partyId, Integer level);
	
	/**
	 * 
	 */
	public Map<String, String> getPromoteData(String partyId, Map<String, String> data, Date startTime, Date endTime);

	/**
	 * 充值--纯充值 1、前端用户充值USDT。其它币转USDT
	 *
	 * @param partyId
	 * @param volume
	 * @param symbol
	 */
	public void saveRechargeHandle(String partyId, double amount, String symbol,double volume);

	/**
	 * 充值_DAPP
	 */
	public void saveRechargeHandleDapp(String partyId, double volume, String symbol);
	
	/**
	 * 更新充值返佣
	 */
	public void saveUserDataForRechargeRecom(String partyId, double amount);

	/**
	 * 管理员加币操作 ROOT才减币
	 */
	public void saveGiftMoneyHandle(String partyId, double amount);

	/**
	 * 提现
	 * 
	 * @param partyId
	 * @param amount
	 */
	public void saveWithdrawHandle(String partyId, double amount, double amount_fee, String symbol, double arrival_amount);

	/**
	 * 提现_DAPP
	 */
	public void saveWithdrawHandleDapp(String partyId, double amount, double amount_fee, String symbol);

	/**
	 * 抢单返佣
	 */
	public void saveBrushRebate(String partyId, double amount, int level,double ben);


	/**
	 * 购买理财
	 */
	public void saveInsvestBuy(String partyId, double amount);
	
	/**
	 * 买币
	 */
	public void saveBuy(ExchangeApplyOrder order);

	/**
	 * 卖币
	 */
	public void saveSell(ExchangeApplyOrder order);
	
	/**
	 * 赎回理财产品
	 */
	public void saveSellFinance(FinanceOrder order);

	/**
	 * 1、api注册 2、推荐关系更改 -----------------------
	 */
	public void saveRegister(String partyId);
	
	/**
	 * 矿机买入
	 * 
	 * @param order
	 */
	public void saveMinerBuy(MinerOrder order);

	/**
	 * 矿机赎回
	 */
	public void saveMinerClose(MinerOrder order);

	/**
	 * 矿机利息
	 * 
	 * @param partyId 获利人
	 * @param profit  利息
	 */
	public void saveMinerProfit(String partyId, double profit);
	
	/**
	 * 质押2.0收益
	 */
	public void saveUserDataForGalaxy(String partyId, double amount, boolean ifIncome);

	/**
	 * 三方充值
	 * 
	 * @param partyId
	 * @param amount
	 */
	public void saveThirdRechargeMoneyHandle(String partyId, double amount);

	/**
	 * 持有金额（钱包+扩展钱包）
	 * 
	 * @param partyId
	 * @param amount
	 */
	public void saveHodingMoneyHandle(String partyId, double amount);

	/**
	 * 交割奖励
	 * 
	 * @param partyId
	 * @param profit
	 */
	public void saveFuturesProfit(String partyId, double profit);

	/**
	 * 订单释放 结算报表
	 * @param partyId
	 * @param profit
	 */
	public void saveFreedAmountProfit(String partyId, double profit, double sellerTotalSales);


	/**
	 * 根据日期获取当日充值人数
	 * 
	 * @param date 字符串日期，"2020-01-01"
	 * @return
	 */
	public int filterRechargeByDate(String date);

	/**
	 * 转账
	 * 
	 * @param byPartyId       转出用户id
	 * @param toPartyId       转入用户id
	 * @param outAmountToUsdt 转出金额（USDT计价）
	 * @param inAmountToUsdt  转入金额（USDT计价）
	 */
	public void saveTransferMoneyHandle(String byPartyId, String toPartyId, double outAmountToUsdt,
			double inAmountToUsdt);
	
	/**
	 * 合约平仓
	 * 
	 * @param partyId
	 * @param amount
	 */
	public void saveClose(ContractOrder order);

	/**
	 * 交割平仓
	 * 
	 * @param partyId
	 * @param amount
	 */
	public void saveFuturesClose(FuturesOrder order);
	
	/**
	 * 根据partyId获取UserDataSum
	 */
	public UserDataSum saveBySum(String partyId);

	/**
	 * 根据时间统计新客数据
	 * @param startTime
	 * @param endTime
	 * @return
	 */

	PartySumDataDTO getPartyNewDataBtDay(String startTime, String endTime);


	/**
	 * 根据时间统计用户数据
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	PartySumDataDTO getPartyDataBtDay(String startTime, String endTime);
}
