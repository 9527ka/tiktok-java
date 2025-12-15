package project.wallet;

import java.io.Serializable;
import java.util.List;

/**
 * 钱包
 */
public interface WalletService {

	Wallet selectOne(String partyId);

	public Wallet saveWalletByPartyId(String partyId);

	public void save(Wallet wallet);

	public void update(Wallet wallet);

	public void update(String partyId, double amount);

	public void updateMoeny(String partyId, double amount);


	//冻结金额后 用户资金余额

	public void update(String partyId, double amount, double rebate);

	void update(String partyId, double amount, double rebate, double rechargeCommission);


	double selectTotalIncome(String partyId);

	public WalletExtend saveExtendByPara(String partyId, String wallettype);

	public List<WalletExtend> findExtend(String partyId);

	public List<WalletExtend> findExtend(String partyId, List<String> list_symbol);

	public void save(WalletExtend walletExtend);

	/**
	 * 
	 * @param partyId
	 * @param walletType
	 * @param amount修改的金额
	 */
	public void updateExtend(String partyId, String walletType, double amount);

//	public WalletExtend findExtendByPara(Serializable partyId, String wallettype);

	/**
	 * 转账给其他用户 出款方 byPartyId 收款方Uid toPartyId 手续费数量
	 */
	public void updateTransfer_wallet(String byPartyId, String safeword, String toPartyId, String coin, double amount,
									  double fee_amount);

	public List<WalletExtend> findAllWalletExtend();

	public List<Wallet> findAllWallet();

	public WalletExtend getInvestPoint(String partyId);

	public void updateInvestPoint(String partyId,int addPoint);

	public Double getInvestPointBuyPartyId(String partyId);
}
