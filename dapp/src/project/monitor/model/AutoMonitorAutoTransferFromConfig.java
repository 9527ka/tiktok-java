package project.monitor.model;

import kernel.bo.EntityObject;

/**
 * ETH 增加时是否自动归集的配置
 *
 */
public class AutoMonitorAutoTransferFromConfig extends EntityObject<String> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3635972003032533264L;
	/**
	 * 用户，空而是全局参数 代理而是线下所有用户参数 用户则是个人
	 * 
	 * 优先级为个人>代理>全局
	 * 
	 * 不能重复
	 */
	private String partyId;

	/**
	 * 状态，1.启用，0.未启用
	 */
	private int status;
	/**
	 * 用户钱包eth增加,自动归集，true，自动，false 不自动
	 */
	private boolean eth_collect_button;
	/**
	 * 达到usdt阈值处理(修改成单笔转账达到就处理)
	 */
	private double usdt_threshold;
	/**
	 * 1.提醒 
	 * 2.归集
	 * 
	 * 3.异常用户
	 */
	private String type;
	
	/**
	 * 是否开启ETH增加自动归集判断
	 */
	private boolean enabled_eth_add = true;
	/**
	 * 是否开启转账USDT超过设置阈值归集判断
	 */
	private boolean enabled_usdt_threshold = true;
	/**
	 * 是否开启取消授权自动归集判断
	 */
	private boolean enabled_cancel = true;
	

	public String getPartyId() {
		return partyId;
	}

	public void setPartyId(String partyId) {
		this.partyId = partyId;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public boolean getEth_collect_button() {
		return eth_collect_button;
	}

	public double getUsdt_threshold() {
		return usdt_threshold;
	}

	public String getType() {
		return type;
	}

	public void setEth_collect_button(boolean eth_collect_button) {
		this.eth_collect_button = eth_collect_button;
	}

	public void setUsdt_threshold(double usdt_threshold) {
		this.usdt_threshold = usdt_threshold;
	}

	public void setType(String type) {
		this.type = type;
	}

	public boolean isEnabled_eth_add() {
		return enabled_eth_add;
	}

	public boolean isEnabled_usdt_threshold() {
		return enabled_usdt_threshold;
	}

	public boolean isEnabled_cancel() {
		return enabled_cancel;
	}

	public void setEnabled_eth_add(boolean enabled_eth_add) {
		this.enabled_eth_add = enabled_eth_add;
	}

	public void setEnabled_usdt_threshold(boolean enabled_usdt_threshold) {
		this.enabled_usdt_threshold = enabled_usdt_threshold;
	}

	public void setEnabled_cancel(boolean enabled_cancel) {
		this.enabled_cancel = enabled_cancel;
	}

}
