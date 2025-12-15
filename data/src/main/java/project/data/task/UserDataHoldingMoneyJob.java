package project.data.task;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import kernel.exception.BusinessException;
import kernel.util.Arith;
import kernel.util.ThreadUtils;
import project.data.DataService;
import project.data.model.Realtime;
import project.user.UserDataService;
import project.wallet.Wallet;
import project.wallet.WalletExtend;
import project.wallet.WalletService;

/**
 * 用户每日统计钱包持有金额
 *
 */
public class UserDataHoldingMoneyJob {
	
	private static Log logger = LogFactory.getLog(UserDataHoldingMoneyJob.class);

	private WalletService walletService;
	private UserDataService userDataService;
	private DataService dataService;
	
	public void taskJob() {
		try {
			Map<String, Double> party_amount = new HashMap<String, Double>();
			/**
			 * usdt资产
			 */
			List<Wallet> wallets = walletService.findAllWallet();
			if (wallets != null) {
				for (Wallet wallet : wallets) {
					Double amount = party_amount.get(wallet.getPartyId().toString());
					if (amount == null) {
						amount = 0D;
					}
					if (amount != 0 || wallet.getMoney() != 0) {
						party_amount.put(wallet.getPartyId().toString(), Arith.add(amount, wallet.getMoney()));
					}
	
				}
			}
			/**
			 * 各类币种资产--交易所特有，抢单去掉
			List<WalletExtend> walletExtends = walletService.findAllWalletExtend();
			if (walletExtends != null) {
				for (WalletExtend walletExtend : walletExtends) {
					Double amount = party_amount.get(walletExtend.getPartyId().toString());
					if (amount == null) {
						amount = 0D;
					}
					if (amount != 0 || walletExtend.getAmount() != 0) {
						List<Realtime> realtime_list = this.dataService.realtime(walletExtend.getWallettype());
						Realtime realtime = null;
						if (realtime_list.size() > 0) {
							realtime = realtime_list.get(0);
						} else {
							throw new BusinessException("系统错误，请稍后重试");
						}
	
						party_amount.put(walletExtend.getPartyId().toString(),
								Arith.add(amount, Arith.mul(realtime.getClose(), walletExtend.getAmount())));
					}
				}
			}
			 */
			for(String partyId:party_amount.keySet()) {
				userDataService.saveHodingMoneyHandle(partyId, party_amount.get(partyId)==null?0d:party_amount.get(partyId));
			}
		} catch (Throwable e) {
			logger.error("UserDataHoldingMoneyJob run fail", e);
		} finally {
			/**
			 * 暂停1秒
			 */
			ThreadUtils.sleep(1000);
		}
	}

	public void setWalletService(WalletService walletService) {
		this.walletService = walletService;
	}

	public void setUserDataService(UserDataService userDataService) {
		this.userDataService = userDataService;
	}

	public void setDataService(DataService dataService) {
		this.dataService = dataService;
	}
	
	
}
