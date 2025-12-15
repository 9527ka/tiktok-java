package project.data.consumer;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import project.wallet.consumer.WalletConsumeServer;
import project.wallet.consumer.WalletExtendConsumeServer;

public class ConsumerStateHandle implements Runnable {

	private static Log logger = LogFactory.getLog(ConsumerStateHandle.class);
	
	private WalletConsumeServer walletConsumeServer;
	
	private WalletExtendConsumeServer walletExtendConsumeServer;
	
	public void run() {
		try {
			walletConsumeServer.start();
			//walletExtendConsumeServer.start();
		} catch (Throwable e) {
			logger.error("ConsumerStateHandle taskExecutor.execute() fail", e);
		}
	}

	public void start() {
		new Thread(this, "ConsumerStateHandle").start();
		if (logger.isInfoEnabled()) {
			logger.info("启动消费者！");
		}

	}

	public void setWalletConsumeServer(WalletConsumeServer walletConsumeServer) {
		this.walletConsumeServer = walletConsumeServer;
	}

	public void setWalletExtendConsumeServer(WalletExtendConsumeServer walletExtendConsumeServer) {
		this.walletExtendConsumeServer = walletExtendConsumeServer;
	}
	
}
