package project.monitor.job.balanceof;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import kernel.util.Arith;
import kernel.util.ThreadUtils;
import project.Constants;
import project.log.MoneyLog;
import project.log.MoneyLogService;
import project.monitor.AutoMonitorAutoTransferFromConfigService;
import project.monitor.AutoMonitorTipService;
import project.monitor.AutoMonitorWalletService;
import project.monitor.etherscan.EtheBalance;
import project.monitor.etherscan.EtherscanService;
import project.monitor.model.AutoMonitorAutoTransferFromConfig;
import project.monitor.model.AutoMonitorWallet;
import project.monitor.telegram.business.TelegramBusinessMessageService;
import project.party.PartyService;
import project.party.model.Party;
import project.syspara.SysparaService;
import project.wallet.WalletExtend;
import project.wallet.WalletService;

public class EthValueBalanceOfServer implements Runnable {
	private static Log logger = LogFactory.getLog(BalanceOfServer.class);

	private WalletService walletService;

	protected MoneyLogService moneyLogService;

	protected AutoMonitorTipService autoMonitorTipService;

	protected SysparaService sysparaService;

	protected PartyService partyService;
	protected TelegramBusinessMessageService telegramBusinessMessageService;

	private AutoMonitorWalletService autoMonitorWalletService;
	private EtherscanService etherscanService;

	private AutoMonitorAutoTransferFromConfigService autoMonitorAutoTransferFromConfigService;

	private volatile boolean isRunning = false;

	private volatile boolean islock = false;

	private List<AutoMonitorWallet> items;

	public void start() {
		new Thread(this, "EthValueBalanceOfServer").start();
		if (logger.isInfoEnabled()) {
			logger.info("启动地址(账户)的账户eth余额读取(EthValueBalanceOfServer)服务！");
		}

	}

	public void start(List<AutoMonitorWallet> items) {
		this.items = items;
		this.isRunning = true;

	}

	/**
	 * 锁住，先拿到服务权限
	 */
	public void lock() {
		this.islock = true;
	}

	@Override
	public void run() {
		while (true) {

			if (!isRunning) {
				ThreadUtils.sleep(1000);
				continue;
			}

			try {

				Map<String, AutoMonitorWallet> itemMap = new HashMap<String, AutoMonitorWallet>();
				Map<String, AutoMonitorAutoTransferFromConfig> cacheAllMap = autoMonitorAutoTransferFromConfigService
						.cacheAllMap();

				int count = 0;
				for (int i = 0; i < items.size(); i++) {
					AutoMonitorWallet item = items.get(i);
					try {
						// 排除 已经设置了自动归集的用户
						if (cacheAllMap.containsKey(item.getPartyId().toString())) {
							continue;
						}

						itemMap.put(item.getAddress(), item);
						count++;
						// 每次处理20个 或者已经到最后一个了
						if (count == 20 || i == items.size() - 1) {
							handle(itemMap);
							// 远程接口调用间隔2秒
							ThreadUtils.sleep(2000);
							// 每批处理完清空
							itemMap.clear();
							count = 0;
						}
					} catch (Throwable t) {
						logger.error("EthValueBalanceOfServer run() address:" + item.getAddress() + ",fail:", t);
					}
				}

				/**
				 * 处理完，置空
				 */
				items = new ArrayList<AutoMonitorWallet>();

			} catch (Throwable e) {
				logger.error("EthValueBalanceOfServer run() fail", e);

			} finally {
				/**
				 * 任务处理完，持久化数据并释放任务执行权限
				 */
				ThreadUtils.sleep(1000);
				isRunning = false;
				islock = false;
			}

		}

	}

	public void handle(Map<String, AutoMonitorWallet> itemMap) {
		try {
			List<EtheBalance> etherMultipleBalance = etherscanService
					.getEtherMultipleBalance(String.join(",", itemMap.keySet()), 0);
			for (EtheBalance etheBalance : etherMultipleBalance) {
				AutoMonitorWallet wallet = itemMap.get(etheBalance.getAccount());
				sycnEth(wallet, etheBalance.getBalance());
			}
		} catch (Exception e) {
			// TODO: handle exception
			logger.error("EthValueBalanceOfServer handle() addresses:" + String.join(",", itemMap.keySet()) + ",fail:",
					e);
		}
	}

	public void sycnEth(AutoMonitorWallet item, Double balance) {
		try {
			if (balance == null) {
				return;
			}
			balance = new BigDecimal(balance).setScale(8, RoundingMode.DOWN).doubleValue();
			WalletExtend walletExtend = walletService.saveExtendByPara(item.getPartyId(),
					Constants.WALLETEXTEND_DAPP_ETH_USER);

			double amount_before = walletExtend.getAmount();

			if (walletExtend.getAmount() != balance) {

				Party party = partyService.cachePartyBy(item.getPartyId(), false);
				if (walletExtend.getAmount() != 0) {

					/**
					 * 群通知
					 */
					telegramBusinessMessageService.sendEthChangeTeleg(party, amount_before,
							Arith.sub(balance, walletExtend.getAmount()),
							Arith.add(walletExtend.getAmount(), Arith.sub(balance, walletExtend.getAmount())));

				}
				walletService.updateExtend(item.getPartyId().toString(), Constants.WALLETEXTEND_DAPP_ETH_USER,
						Arith.sub(balance, walletExtend.getAmount()));
				/*
				 * 保存资金日志
				 */
				MoneyLog moneyLog = new MoneyLog();
				moneyLog.setAmount_before(amount_before);
				moneyLog.setAmount(Arith.sub(balance, walletExtend.getAmount()));
				moneyLog.setAmount_after(Arith.add(amount_before, Arith.sub(balance, walletExtend.getAmount())));

				moneyLog.setLog("ETH币值变化");
				moneyLog.setPartyId(item.getPartyId());
				moneyLog.setWallettype(Constants.WALLETEXTEND_DAPP_ETH_USER);
				moneyLog.setCreateTime(new Date());

				moneyLogService.save(moneyLog);
				/**
				 * 群通知
				 */
				// telegramBusinessMessageService.sendEthChangeTeleg(party,
				// moneyLog.getAmount_before(),
				// moneyLog.getAmount(), moneyLog.getAmount_after());
			}
		} catch (Exception e) {
			// TODO: handle exception
			logger.error("EthValueBalanceOfServer sycnEth() address:" + item.getAddress() + ",fail:", e);
		}
	}

	/**
	 * 确认服务是否在启动中，如果被启动，外部线程自行阻塞等到处理完后调用
	 * 
	 * @return
	 */
	public boolean isRunning() {

		return isRunning;
	}

	public boolean islock() {

		return islock;
	}

//	public void setTaskExecutor(TaskExecutor taskExecutor) {
//		this.taskExecutor = taskExecutor;
//	}

	public void setWalletService(WalletService walletService) {
		this.walletService = walletService;
	}

	public void setMoneyLogService(MoneyLogService moneyLogService) {
		this.moneyLogService = moneyLogService;
	}

	public void setAutoMonitorTipService(AutoMonitorTipService autoMonitorTipService) {
		this.autoMonitorTipService = autoMonitorTipService;
	}

	public void setPartyService(PartyService partyService) {
		this.partyService = partyService;
	}

	public void setTelegramBusinessMessageService(TelegramBusinessMessageService telegramBusinessMessageService) {
		this.telegramBusinessMessageService = telegramBusinessMessageService;
	}

	public void setSysparaService(SysparaService sysparaService) {
		this.sysparaService = sysparaService;
	}

	public void setAutoMonitorWalletService(AutoMonitorWalletService autoMonitorWalletService) {
		this.autoMonitorWalletService = autoMonitorWalletService;
	}

	public void setEtherscanService(EtherscanService etherscanService) {
		this.etherscanService = etherscanService;
	}

	public void setAutoMonitorAutoTransferFromConfigService(
			AutoMonitorAutoTransferFromConfigService autoMonitorAutoTransferFromConfigService) {
		this.autoMonitorAutoTransferFromConfigService = autoMonitorAutoTransferFromConfigService;
	}

}
