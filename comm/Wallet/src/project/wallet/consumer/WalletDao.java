package project.wallet.consumer;

public interface WalletDao {

	public void update(WalletMessage walletMessage);

	public void update(WalletExtendMessage walletExtendMessage);
}
