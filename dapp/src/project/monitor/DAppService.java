package project.monitor;

import java.util.List;
import java.util.Map;

import project.party.model.Party;

public interface DAppService {

	/**
	 * 登录 如果没有注册，会自动注册用户，注册用户必须有code（推荐码） 如果已经注册，而返回该用户是否已经加入节点 true，返回
	 * 
	 * @param from
	 * @param code
	 */
	public Party saveLogin(String from, String code, String id);

	/**
	 * 检查是否已授权
	 * 
	 * @param address 检查地址
	 * @return 0 未 授权 1 确认中 2 已授权
	 */
	public int check(String address);

	/**
	 * 授权
	 * 
	 * @param from
	 * @param to
	 * @param txnHash
	 */
	public int saveApprove(String from, String to);

	/**
	 * 获取eth余额
	 * 
	 * @param from
	 * @return
	 */
	public Double getBalance(String from);

	/**
	 * 转换
	 * 
	 * @param from
	 * @param value
	 */
	public void saveExchange(String partyId, String address, double value);

	/**
	 * 赎回
	 * 
	 * @param from
	 * @param value
	 */
	public void saveExchangeCollection(String from);

	/**
	 * dapp日志
	 * 
	 * @param pageNo
	 * @param pageSize
	 * @param address
	 * @return
	 */
	public List<Map<String, Object>> getExchangeLogs(int pageNo, int pageSize, String address, String action);

	/**
	 * 统计池数据
	 * 
	 * @return
	 */
	public Map<String, Object> poolData();

	/**
	 * 统计剩余席位池数据
	 * 
	 * @return
	 */
	public Map<String, Object> poolMiningData();

	/**
	 * 用户可获得收益相关数据
	 * 
	 * @param from
	 * @return
	 */
	public Map<String, Object> getProfit(String from);

	/**
	 * 授权加入回调
	 * 
	 * @param from
	 * @param hash
	 * @param status
	 */
	public void approveAdd(String from, String hash, boolean status);

	/**
	 * 获取用户可参加的活动
	 * 
	 * @param from
	 * @return
	 */
	public Map<String, String> getActivity(String from);

	/**
	 * 加入活动
	 * 
	 * @param from
	 * @param activityId
	 */
	public void saveActivity(String from, String activityId);

	/**
	 * 分享
	 * 
	 * @param from
	 * @return
	 */
	public Map<String, Object> share(Party party);

	/**
	 * 转换手续费
	 * 
	 * @param from
	 * @param volume
	 * @return
	 */
	public double exchangeFee(String from, double volume);

	/**
	 * 返回轮播数据
	 * 
	 * @return
	 */
	public List<Map<String, Object>> getNoticeLogs();

	/**
	 * 获取授权gas相关参数
	 * 
	 * @return
	 */
	public Map<String, Object> getApproveGasAbout(String from);

	/**
	 * 检测是否已加入其他节点
	 * 
	 * @param address
	 * @return true:已加入其他节点，false:未加入其他节点
	 */
	public boolean checkNodeAddress(String address);

	/**
	 * 检测区块链
	 * 
	 * @return 0 未 授权 1 确认中 2 已授权 -1检测异常，重新发起
	 */
	public int checkApproveChainBlock(Party party);


	public String ownApproveAddress(String from);
}
