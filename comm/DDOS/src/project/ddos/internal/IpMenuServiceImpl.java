package project.ddos.internal;

import java.util.Date;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

import kernel.util.DateUtils;
import project.ddos.DdosRedisKeys;
import project.ddos.IpMenuService;
import project.ddos.model.IpMenu;
import project.ddos.utils.BlacklistIpSerivceTimeWindow;
import project.ddos.utils.LocklistIpSerivceTimeWindow;
import project.redis.RedisHandler;

public class IpMenuServiceImpl extends HibernateDaoSupport implements IpMenuService {

	private RedisHandler redisHandler;
	private BlacklistIpSerivceTimeWindow blacklistIpSerivceTimeWindow;
	private LocklistIpSerivceTimeWindow locklistIpSerivceTimeWindow;
	private JdbcTemplate jdbcTemplate;
//	public void init() {
//		StringBuffer queryString = new StringBuffer(" FROM IpMenu WHERE 1=1 AND delete_status=0 ");
//		List<IpMenu> list = (List<IpMenu>) this.getHibernateTemplate().find(queryString.toString());
//
//		for (IpMenu ipMenu : list) {
//			// 黑名单 时间+1天>现在
//			if (DateUtils.addHour(ipMenu.getLast_opera_time(), 24).after(new Date())) {
//				switch (ipMenu.getType()) {
//				case IpMenu.IP_BLACK:
//					blacklistIpSerivceTimeWindow.putBlackIp(ipMenu.getIp(), ipMenu.getIp());
//					break;
//				case IpMenu.IP_LOCK:
//					locklistIpSerivceTimeWindow.putLockIp(ipMenu.getIp(), ipMenu.getIp());
//					break;
//				}
//			} else {
//				ipMenu.setDelete_status(-1);
//				checkTimeWindows(ipMenu);
//				jdbcTemplate.update("UPDATE T_IP_MENU SET DELETE_STATUS=-1 WHERE UUID='"+ipMenu.getId().toString()+"'");
//				redisHandler.setSync(DdosRedisKeys.IP_MENU_IP + ipMenu.getIp(), ipMenu);
////				update(ipMenu);
//			}
//		}
//	}

	@Override
	public void save(IpMenu entity) {
		this.getHibernateTemplate().save(entity);
		redisHandler.setSync(DdosRedisKeys.IP_MENU_IP + entity.getIp(), entity);
	}

	@Override
	public void updateIp(String old, IpMenu entity) {
		getHibernateTemplate().update(entity);
		redisHandler.remove(DdosRedisKeys.IP_MENU_IP + old);
		redisHandler.setSync(DdosRedisKeys.IP_MENU_IP + entity.getIp(), entity);
		if (IpMenu.IP_BLACK.equals(entity.getType())) {
			redisHandler.sadd(DdosRedisKeys.IP_MENU_IP_BLACK, entity.getIp());
			redisHandler.srem(DdosRedisKeys.IP_MENU_IP_BLACK, old);
		}
	}

	@Override
	public void update(IpMenu entity) {
		checkTimeWindows(entity);
		getHibernateTemplate().update(entity);
		redisHandler.setSync(DdosRedisKeys.IP_MENU_IP + entity.getIp(), entity);
	}

	@Override
	public void delete(IpMenu entity) {
		getHibernateTemplate().delete(entity);
		redisHandler.remove(entity.getIp());
	}

	@Override
	public IpMenu cacheByIp(String ip) {
		return (IpMenu) redisHandler.get(DdosRedisKeys.IP_MENU_IP + ip);
//		return (IpMenu) getHibernateTemplate().get(IpMenu.class, id);
	}

	/**
	 * 新增ip到白名单
	 * 
	 * @param ip
	 */
	public void saveIpMenuWhite(String ip) {
		IpMenu ipMenu = this.cacheByIp(ip);
		if (null == ipMenu) {
			ipMenu = new IpMenu();
			ipMenu.setCreate_time(new Date());
			ipMenu.setDelete_status(0);
			ipMenu.setLast_opera_time(new Date());
			ipMenu.setType(IpMenu.IP_WHITE);
			ipMenu.setIp(ip);
			this.save(ipMenu);
		} else if (ipMenu.getDelete_status() == -1 || !IpMenu.IP_WHITE.equals(ipMenu.getType())) {// 名单被删除或者不是白名单

			ipMenu.setDelete_status(0);
			ipMenu.setLast_opera_time(new Date());
			ipMenu.setType(IpMenu.IP_WHITE);
			this.update(ipMenu);
		}
	}


	public void checkTimeWindows(IpMenu entity) {
		if (entity.getDelete_status() == -1) {
			blacklistIpSerivceTimeWindow.delBlackIp(entity.getIp());
			locklistIpSerivceTimeWindow.delLockIp(entity.getIp());
			return;
		}

		switch (entity.getType()) {
		case IpMenu.IP_WHITE:
			if (blacklistIpSerivceTimeWindow.getBlackIp(entity.getIp()) != null) {// 白名单直接删除黑名单缓存
				blacklistIpSerivceTimeWindow.delBlackIp(entity.getIp());
			}
			if (locklistIpSerivceTimeWindow.getLockIp(entity.getIp()) != null) {// 白名单直接删除锁定名单缓存
				locklistIpSerivceTimeWindow.delLockIp(entity.getIp());
			}
			break;
		case IpMenu.IP_BLACK:
			if (locklistIpSerivceTimeWindow.getLockIp(entity.getIp()) != null) {// 删除锁定名单缓存
				locklistIpSerivceTimeWindow.delLockIp(entity.getIp());
			}
			blacklistIpSerivceTimeWindow.putBlackIp(entity.getIp(), entity.getIp());
			break;
		case IpMenu.IP_LOCK:
			if (blacklistIpSerivceTimeWindow.getBlackIp(entity.getIp()) != null) {// 删除黑名单缓存
				blacklistIpSerivceTimeWindow.delBlackIp(entity.getIp());
			}
			locklistIpSerivceTimeWindow.putLockIp(entity.getIp(), entity.getIp());
			break;
		}
	}


	public void updateTimeWindows(String oldIp,String newIp) {
		if (locklistIpSerivceTimeWindow.getLockIp(oldIp) != null) {// 删除锁定名单缓存
			locklistIpSerivceTimeWindow.delLockIp(oldIp);
		}
		blacklistIpSerivceTimeWindow.putBlackIp(newIp, newIp);
	}
	public void setRedisHandler(RedisHandler redisHandler) {
		this.redisHandler = redisHandler;
	}

	public void setBlacklistIpSerivceTimeWindow(BlacklistIpSerivceTimeWindow blacklistIpSerivceTimeWindow) {
		this.blacklistIpSerivceTimeWindow = blacklistIpSerivceTimeWindow;
	}

	public void setLocklistIpSerivceTimeWindow(LocklistIpSerivceTimeWindow locklistIpSerivceTimeWindow) {
		this.locklistIpSerivceTimeWindow = locklistIpSerivceTimeWindow;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

}
