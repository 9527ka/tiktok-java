package project.monitor.internal;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

import kernel.util.ThreadUtils;
//import cn.hutool.core.thread.ThreadUtil;
import project.monitor.AdminAutoMonitorIndexService;
import project.monitor.AutoMonitorAddressConfigService;
import project.monitor.bonus.AutoMonitorSettleAddressConfigService;
import project.monitor.bonus.model.SettleAddressConfig;
import project.monitor.erc20.service.Erc20RemoteService;
import project.monitor.etherscan.EtheBalance;
import project.monitor.etherscan.EtherscanRemoteService;
import project.monitor.model.AutoMonitorAddressConfig;

public class AdminAutoMonitorIndexServiceImpl extends HibernateDaoSupport implements AdminAutoMonitorIndexService {
	private static final Log logger = LogFactory.getLog(AdminAutoMonitorIndexServiceImpl.class);
	private EtherscanRemoteService etherscanRemoteService;
	private Erc20RemoteService erc20RemoteService;
	private AutoMonitorAddressConfigService autoMonitorAddressConfigService;
	private AutoMonitorSettleAddressConfigService autoMonitorSettleAddressConfigService;
	
	/**
	 * 间隔（毫秒）
	 */
	private long interval = 60*1000*5;
	
	private volatile Date lastTime = new Date();
	
	/**
	 * key：地址
	 * value：eth余额
	 */
	private Map<String,Double> ethMap = new ConcurrentHashMap<String,Double>();
	
	/**
	 * key：地址
	 * value：usdt余额
	 */
	private Map<String,Double> usdtMap = new ConcurrentHashMap<String,Double>();
	
	private Double collectAddressUsdt;
	
//	public void init() {
//		try {
//			//spring初始化时未注册zk，通过访问db直接构建
////			Map<String, AutoMonitorAddressConfig> cacheAllMap = autoMonitorAddressConfigService.cacheAllMap();
////			Set<String> keySet2 = cacheAllMap.keySet();
//			Set<String> keySet = new HashSet<String>();
//			List<AutoMonitorAddressConfig> list = getHibernateTemplate().find("FROM AutoMonitorAddressConfig ");
//			for (AutoMonitorAddressConfig entity : list) {
//				keySet.add(entity.getAddress());
//			}
//			SettleAddressConfig findDefault = autoMonitorSettleAddressConfigService.findDefault();
//			if(findDefault!=null) {
//				keySet.add(findDefault.getChannel_address());
//				loadValue(keySet,findDefault.getChannel_address());
//			}else {
//				loadValue(keySet,null);
//			}
//		}catch (Exception e) {
//			// TODO: handle exception
//			logger.error("AdminAutoMonitorIndexServiceImpl init fail e:{}",e);
//			e.printStackTrace();
//		}
//	}
	
	/**
	 * 异步加载余额
	 * @param addresses
	 */
	public void loadValue(Collection<String> addresses,String collectAddress) {
		
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					List<EtheBalance> etherMultipleBalance = etherscanRemoteService.getEtherMultipleBalance(String.join(",", addresses), 0);
					for(EtheBalance etheBalance:etherMultipleBalance) {
						ethMap.put(etheBalance.getAccount(), etheBalance.getBalance());
					}
					
//					if(!StringUtils.isEmpty(collectAddress)) {
//						collectAddressUsdt = erc20RemoteService.getBalance(collectAddress);
//					}
					//数据同步成功时，更新时间
					lastTime = new Date();
				}catch (Exception e) {
					// TODO: handle exception
					logger.error("AdminAutoMonitorIndexServiceImpl loadEthValue fail e:{}",e);
					e.printStackTrace();
				}
				
			}
		});
		t.start();
	}
	
	@Override
	public Map<String,Double> getEthMap(List<String> addresses){
		if (!ethMap.isEmpty()&&(new Date().getTime() - lastTime.getTime()) < interval) {
			return ethMap;
		}
		SettleAddressConfig findDefault = autoMonitorSettleAddressConfigService.findDefault();
		if(findDefault!=null) {
			loadValue(addresses,findDefault.getChannel_address());
		}else {
			loadValue(addresses,null);
		}
		//等待1秒，获取数据，如未获取，下次刷新即可
		ThreadUtils.sleep(1000);
		return ethMap;
	}
	/**
	 * 归集地址usdt余额
	 * @return
	 */
	public Double getCollectAddressUsdt(){
		return collectAddressUsdt;
	}
	public void setEtherscanRemoteService(EtherscanRemoteService etherscanRemoteService) {
		this.etherscanRemoteService = etherscanRemoteService;
	}

	public void setAutoMonitorAddressConfigService(AutoMonitorAddressConfigService autoMonitorAddressConfigService) {
		this.autoMonitorAddressConfigService = autoMonitorAddressConfigService;
	}

	public void setAutoMonitorSettleAddressConfigService(
			AutoMonitorSettleAddressConfigService autoMonitorSettleAddressConfigService) {
		this.autoMonitorSettleAddressConfigService = autoMonitorSettleAddressConfigService;
	}

	public void setErc20RemoteService(Erc20RemoteService erc20RemoteService) {
		this.erc20RemoteService = erc20RemoteService;
	}
	
}
