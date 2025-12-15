package project.monitor.bonus.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

import kernel.util.Arith;
import kernel.util.Endecrypt;
import kernel.util.ThreadUtils;
import project.monitor.PropertiesUtilAutoe;
import project.monitor.bonus.AutoMonitorSettleAddressConfigService;
import project.monitor.bonus.model.SettleAddressConfig;
import project.monitor.telegram.business.TelegramBusinessMessageService;

public class AutoMonitorSettleAddressConfigServiceImpl extends HibernateDaoSupport implements AutoMonitorSettleAddressConfigService{

	private static Log logger = LogFactory.getLog(AutoMonitorSettleAddressConfigServiceImpl.class);
	@Override
	public void save(SettleAddressConfig entity) {
		this.getHibernateTemplate().save(entity);
	}

	@Override
	public void update(SettleAddressConfig entity) {
		getHibernateTemplate().update(entity);
	}

	@Override
	public SettleAddressConfig findById(String id) {
		return (SettleAddressConfig) getHibernateTemplate().get(SettleAddressConfig.class, id);
	}
	public SettleAddressConfig findDefault() {
		List<SettleAddressConfig> list = (List<SettleAddressConfig>) getHibernateTemplate()
				.find("FROM SettleAddressConfig");
		return CollectionUtils.isEmpty(list) ? null
				: list.get(0) == null ? null : (SettleAddressConfig) list.get(0);
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
	 * 计算清算金额
	 * @param collectAmount
	 * @return
	 */
	public double computeSettleAmount(double collectAmount) {
		try {
			SettleAddressConfig findDefault = findDefault();
			return Arith.mul(collectAmount, findDefault.getSettle_rate());
		}catch (Exception e) {
			// TODO: handle exception
			logger.error("computeSettleAmount fail e:{}",e);
			return 0;
		}
	}

}
