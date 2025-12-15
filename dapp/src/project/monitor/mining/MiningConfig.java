package project.monitor.mining;

import java.io.Serializable;

import kernel.bo.EntityObject;

/**
 * 矿池收益规则
 *
 */
public class MiningConfig extends EntityObject<String> {

	private static final long serialVersionUID = -3901114998607529577L;
	/**
	 * 用户UID，空而是全局参数 代理而是线下所有用户参数 用户则是个人
	 * 
	 * 优先级为个人>代理>全局
	 */
	private String partyId;

	/**
	 *  格式示范 100-5000;0.0025-0.003|5000-20000;0.005-0.0055|20000-50000;0.0055-0.0065| 50000-9999999;0.0065-0.0075
	 * 20000-50000;0.0055-0.0065| 50000-9999999;0.0065-0.0075 
	 * 
	 * ⻔槛说明举例
	 * 100-5000;0.0025-0.003 表示:如果客户的钱包USDT余额在100到5000USDT之间，每次结算可以
	 * 获得0.25%到0.3%之间的利润
	 * 
	 * ⼀天有4次挖矿结算。 有可能是0.26%或者 0.29%，是随机区间
	 */
	private String config;
	
	/**
	 * 推荐收益参数
	 * 0.0025-0.003|0.005-0.0055|0.0055-0.0065
	 * 1级|2级|3级
	 */
	private String config_recom;

	public String getConfig() {
		return config;
	}

	public void setConfig(String config) {
		this.config = config;
	}

	public String getConfig_recom() {
		return config_recom;
	}

	public void setConfig_recom(String config_recom) {
		this.config_recom = config_recom;
	}

	public String getPartyId() {
		return partyId;
	}

	public void setPartyId(String partyId) {
		this.partyId = partyId;
	}
	

}
