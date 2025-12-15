package project.blockchain;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 区块链充值订单接口
 *
 * @author User
 */
public interface RechargeBlockchainService {

	public void save(RechargeBlockchain recharge, double exchangeRate);

//	public void save_api(RechargeBlockchain recharge);

    public void update(RechargeBlockchain recharge);

    public RechargeBlockchain findByOrderNo(String order_no);

    /**
     * 查找当日内用户的所有充值订单
     *
	 * @param partyId
	 * @return
	 */
	public List<RechargeBlockchain> findByPartyIdAndToday(String partyId);

    /**
     * 查找所有用户几天前的指定一天的的所有充值订单
     *
	 * @param succeeded days 几天前
     * @return
     */
    public List<RechargeBlockchain> findBySucceededAndDay(int succeeded, Integer days);

    /**
	 * 
	 * @param order_no
	 */
	public Map saveSucceeded(String order_no, String operator, String transfer_usdt, String success_amount, double rechargeCommission,String remarks);

    /**
	 * 
	 * @param recharge
     */
    public boolean saveReject(RechargeBlockchain recharge);

    /**
     * 是否是商家首次充值成功
     *
     * @param order_no
     * @return
     */
	public boolean updateFirstSuccessRecharge(String order_no);

	/**
	 * 查询充值成功的订单条数
	 * @param partyId
	 * @return
	 */
	public List<RechargeBlockchain> findSuccessByPartyId(String partyId);

    List<RechargeBlockchain> selectUnFinishedRecharge(String partyId);

    double getComputeRechargeAmount(String partyId);

	/**
	 * 根据充值订单单号，赠送拉人礼金
	 * @param order_no
	 */
    void updateFirstSuccessInviteReward(String order_no);
}
