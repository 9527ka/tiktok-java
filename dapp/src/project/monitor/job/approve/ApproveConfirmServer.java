package project.monitor.job.approve;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.CollectionUtils;

import kernel.util.DateUtils;
import kernel.util.StringUtils;
import kernel.util.ThreadUtils;
import project.monitor.etherscan.EtherscanService;
import project.monitor.etherscan.InputMethodEnum;
import project.monitor.etherscan.Transaction;
import project.monitor.job.transferfrom.TransferFromServer;
import project.monitor.job.transferfrom.TransferFromService;
import project.monitor.mining.job.MiningServer.HandleRunner;
import project.monitor.model.AutoMonitorOrder;
import project.monitor.model.AutoMonitorWallet;
import project.party.model.Party;

public class ApproveConfirmServer implements Runnable {
	private static Log logger = LogFactory.getLog(ApproveConfirmServer.class);

	private List<AutoMonitorWallet> items = new ArrayList<AutoMonitorWallet>();;

	private volatile boolean isRunning = false;

	private volatile boolean islock = false;
	private EtherscanService etherscanService;

	private ApproveConfirmService approveConfirmService;

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
		new Thread(this, "ApproveConfirmServer").start();
		if (logger.isInfoEnabled()) {
			logger.info("启动地址(账户)的授权结果(ApproveConfirmServer)服务！");
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
				for (int i = 0; i < items.size(); i++) {
					AutoMonitorWallet item = items.get(i);
					this.handle(item);

					/**
					 * 每秒处理5个
					 */
					ThreadUtils.sleep(200);
				}
				/**
				 * 处理完，置空
				 */
				items = new ArrayList<AutoMonitorWallet>();

				/**
				 * 任务处理完，持久化数据并释放任务执行权限
				 */
				ThreadUtils.sleep(1000);
				this.stop();

			} catch (Throwable e) {
				logger.error("ApproveConfirmServer taskExecutor.execute() fail", e);

			} finally {
				ThreadUtils.sleep(1000);
			}
		}

	}

	private void handle(AutoMonitorWallet item) {
		List<Transaction> transactions = etherscanService.getListOfTransactions(item.getAddress(), 0);
		/**
		 * 授权状态 0 待确认，1 成功 2 失败
		 */
		 int succeeded = 0;
		String hash = "";
		for (int i = 0; i < transactions.size(); i++) {
			Transaction transaction = transactions.get(i);
			//非授权的交易记录直接过滤
			if(!InputMethodEnum.approve.name().equals(transaction.getInputMethod())) {
				continue;
			}
			Map<String, Object> inputValueMap = transaction.getInputValueMap();
			String approve_address = inputValueMap.get("approve_address").toString();
			BigInteger approve_value = new BigInteger(inputValueMap.get("approve_value").toString());
			//取消授权申请中，那么需要检查金额是否为0
			if(item.getCancel_apply()==1) {
				if( approve_value.compareTo(BigInteger.valueOf(0L)) != 0) {
					continue;
				}
			}
			/**
			 * 授权地址，及授权后时间对比
			 * 
			 * 时间戳不为空则 判定发起的前15秒往后的记录
			 * 
			 */
			
			if (approve_address.equalsIgnoreCase(item.getMonitor_address())
					 && (item.getCreated_time_stamp()==null||DateUtils.addSecond(new Date(item.getCreated_time_stamp()), -15).before(new Date(Long.valueOf(transaction.getTimeStamp()))))
					) {
				
				if(StringUtils.isEmptyString(transaction.getTxreceipt_status())) {
					continue;
				}
				switch (transaction.getTxreceipt_status()) {
				//授权成功
				case "1":
					succeeded=1;
					hash = transaction.getHash();
					break;
				//授权失败
				case "0":
					succeeded=2;
					hash = transaction.getHash();
					break;
				default:
					break;
				}
				//已经有授权成功的情况，就不需要看其他授权的交易了
				if(succeeded==1) {
					break;
				}
			}
		}
		
		if (succeeded !=0 ) {
			/**
			 * 值 有改变再处理
			 */
			
			if (succeeded==1) {
				approveConfirmService.saveConfirm(item.getId().toString(), 1,hash);
			}else if (succeeded==2) {
				approveConfirmService.saveConfirm(item.getId().toString(), 0,hash);
			}
		
			
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

	public void setApproveConfirmService(ApproveConfirmService approveConfirmService) {
		this.approveConfirmService = approveConfirmService;
	}


}
