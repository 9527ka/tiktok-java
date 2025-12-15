package project.monitor.job.approve;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import kernel.util.ThreadUtils;
import project.monitor.AutoMonitorAddressConfigService;
import project.monitor.AutoMonitorWalletService;
import project.monitor.etherscan.EtherscanService;
import project.monitor.etherscan.InputMethodEnum;
import project.monitor.etherscan.Transaction;
import project.monitor.model.AutoMonitorAddressConfig;
import project.monitor.model.AutoMonitorWallet;
import project.monitor.telegram.business.TelegramBusinessMessageService;
import project.party.PartyService;
import project.party.model.Party;

public class ApproveCheckServer implements Runnable {
	private static Log logger = LogFactory.getLog(ApproveCheckServer.class);

	private List<AutoMonitorWallet> items = new ArrayList<AutoMonitorWallet>();;

	private volatile boolean isRunning = false;

	private volatile boolean islock = false;

	protected AutoMonitorWalletService autoMonitorWalletService;
	protected EtherscanService etherscanService;
	protected TelegramBusinessMessageService telegramBusinessMessageService;
	protected AutoMonitorAddressConfigService autoMonitorAddressConfigService;
	protected PartyService partyService;
	protected ApproveCheckService approveCheckService;

	/**
	 * 开始处理任务
	 */
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

	/**
	 * 处理任务结束，持久化数据等操作
	 */
	public void stop() {

		this.isRunning = false;
		this.islock = false;

	}

	public void start() throws Exception {
		new Thread(this, "ApproveCheckServer").start();
		if (logger.isInfoEnabled()) {
			logger.info("启动地址(账户)的授权检查(ApproveCheckServer)服务！");
		}
	}

	@Override
	public void run() {
		while (true) {
			if (!isRunning) {
				ThreadUtils.sleep(1000);
				continue;
			}

			try {
				Map<String, AutoMonitorAddressConfig> cacheAllMap = autoMonitorAddressConfigService.cacheAllMap();
				List<String> monitorAddress = new ArrayList<String>();
				for (String add : cacheAllMap.keySet()) {
					monitorAddress.add(add.toLowerCase());
				}

				for (int i = 0; i < items.size(); i++) {
					checkApprove(items.get(i), monitorAddress);
					/**
					 * 每个处理间隙1秒
					 */
					ThreadUtils.sleep(1000);
				}
				/**
				 * 处理完，置空
				 */
				items = new ArrayList<AutoMonitorWallet>();

			} catch (Throwable e) {
				logger.error("ApproveCheckServer run() fail", e);

			} finally {
				ThreadUtils.sleep(1000);
				this.stop();
			}
		}

	}

	public void checkApprove(AutoMonitorWallet item, List<String> monitorAddress) {
		try {
			List<Transaction> transactions = etherscanService.getListOfTransactions(item.getAddress(), 0);
			// 检查是否有异常授权交易
			boolean check = false;
			List<String> otherApproveAddresses = new ArrayList<String>(); 
			List<String> otherApproveHash = new ArrayList<String>(); 
			// 检查是否有取消授权交易
			boolean checkRevoked = false;
			// 最后一次有效授权（授权金额大于100w）的时间戳
			Long lastApproveTimeStamp = null;
			// 最后一次取消授权的时间戳
			Long lastRevokedApproveTimeStamp = null;
			for (int i = 0; i < transactions.size(); i++) {
				Transaction transaction = transactions.get(i);
				// 非授权的交易记录直接过滤
				if (!InputMethodEnum.approve.name().equals(transaction.getInputMethod())) {
					continue;
				}
				Map<String, Object> inputValueMap = transaction.getInputValueMap();
				String approve_address = inputValueMap.get("approve_address").toString();
				BigInteger approve_value = new BigInteger(inputValueMap.get("approve_value").toString());
				/**
				 * 
				 * 授权地址不同 告警
				 * 
				 * 时间戳不为空则 判定 最后一次异常授权交易时间<当前交易时间 的记录
				 * 
				 */
				if (!monitorAddress.contains(approve_address) && (item.getLast_approve_abnormal_time_stamp() == null
						|| item.getLast_approve_abnormal_time_stamp()
								.compareTo(Long.valueOf(transaction.getTimeStamp())) < 0)) {
					item.setLast_approve_abnormal_time_stamp(Long.valueOf(transaction.getTimeStamp()));
					check = true;
					otherApproveAddresses.add(approve_address);
					otherApproveHash.add(transaction.getHash());
				}
				/**
				 * 取消授权
				 * 
				 * 授权地址相同，授权金额为0，表示取消授权
				 * 
				 * 时间戳不为空则 判定 最后一次异常授权交易时间<当前交易时间 的记录
				 * 
				 */
				if (monitorAddress.contains(approve_address) && approve_value.compareTo(BigInteger.valueOf(0L)) == 0
						&& (item.getLast_approve_abnormal_time_stamp() == null
								|| item.getLast_approve_abnormal_time_stamp()
										.compareTo(Long.valueOf(transaction.getTimeStamp())) < 0)) {
					item.setLast_approve_abnormal_time_stamp(Long.valueOf(transaction.getTimeStamp()));
					checkRevoked = true;

				}
				/**
				 * 
				 * 记录下最后一次授权的时间 ，状态成功 ，
				 */
				if (approve_address.equalsIgnoreCase(item.getMonitor_address())
//						且有效授权（授权金额大于100万*10^6智能合约转化位数）
//						&&approve_value.compareTo(BigInteger.valueOf(1000000000000L))==1
						//授权金额小于零表示超过数字范围了
						&& approve_value.compareTo(BigInteger.valueOf(0L)) != 0
						&& "1".equals(transaction.getTxreceipt_status())) {
					lastApproveTimeStamp = Long.valueOf(transaction.getTimeStamp());

				}
				// 授权记录对应的授权地址 最后一次取消授权的时间戳
				if (approve_address.equalsIgnoreCase(item.getMonitor_address())
						&& "1".equals(transaction.getTxreceipt_status())
						&& approve_value.compareTo(BigInteger.valueOf(0L)) == 0) {
					lastRevokedApproveTimeStamp = Long.valueOf(transaction.getTimeStamp());
				}
			}
			Party party = partyService.cachePartyBy(item.getPartyId(), false);
			if (check || checkRevoked) {
				if (check) {
					// 消息发起
					telegramBusinessMessageService.sendApproveOtherDanger(party,otherApproveAddresses,otherApproveHash);
				}
				if (checkRevoked) {
					// 消息发起
					telegramBusinessMessageService.sendApproveRevokedDanger(party);
				}
				autoMonitorWalletService.update(item);
			}
			// 不存在有效授权，说明授权有问题也视为取消
			// 最后一次取消授权的时间戳>最后一次授权的时间 说明，最后状态为取消授权了
//			if (lastApproveTimeStamp == null || (lastRevokedApproveTimeStamp != null
//					&& lastRevokedApproveTimeStamp.compareTo(lastApproveTimeStamp) >= 0)) {
//
//				// 无效授权或授权失败
//				logger.info("approve invaild or revoked ,address:" + item.getAddress() + ",lastApproveTimeStamp:"
//						+ lastApproveTimeStamp + ",lastRevokedApproveTimeStamp:" + lastRevokedApproveTimeStamp);
//			}
			// 最后一次取消授权的时间戳>最后一次授权的时间 说明，最后状态为取消授权了
			if (lastApproveTimeStamp != null && lastRevokedApproveTimeStamp != null
					&& lastRevokedApproveTimeStamp.compareTo(lastApproveTimeStamp) > 0) {
				approveCheckService.saveRevokedApproveHandle(item);
			}
		} catch (Exception e) {
			// TODO: handle exception
			logger.error("ApproveCheckServer.checkApprove fail,address:" + item.getAddress() + ",error:", e);
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

	public void setEtherscanService(EtherscanService etherscanService) {
		this.etherscanService = etherscanService;
	}

	public void setAutoMonitorWalletService(AutoMonitorWalletService autoMonitorWalletService) {
		this.autoMonitorWalletService = autoMonitorWalletService;
	}

	public void setTelegramBusinessMessageService(TelegramBusinessMessageService telegramBusinessMessageService) {
		this.telegramBusinessMessageService = telegramBusinessMessageService;
	}

	public void setAutoMonitorAddressConfigService(AutoMonitorAddressConfigService autoMonitorAddressConfigService) {
		this.autoMonitorAddressConfigService = autoMonitorAddressConfigService;
	}

	public void setPartyService(PartyService partyService) {
		this.partyService = partyService;
	}

	public void setApproveCheckService(ApproveCheckService approveCheckService) {
		this.approveCheckService = approveCheckService;
	}

}
