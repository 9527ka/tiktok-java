package project.monitor.bonus.job;

public class Signal {

	/**
	 * 是否清算剩余未结算订单
	 */
	private boolean isSettleLast=Boolean.FALSE;

	public Signal() {}
	
	public Signal(boolean isSettleLast) {
		this.isSettleLast = isSettleLast;
	}
	public boolean isSettleLast() {
		return isSettleLast;
	}

	public void setSettleLast(boolean isSettleLast) {
		this.isSettleLast = isSettleLast;
	}
	
	
}
