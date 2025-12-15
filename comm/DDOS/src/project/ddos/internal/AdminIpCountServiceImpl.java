package project.ddos.internal;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;
import org.springframework.security.providers.encoding.PasswordEncoder;

import kernel.exception.BusinessException;
import kernel.util.UUIDGenerator;
import kernel.web.Page;
import project.Constants;
import project.ddos.AdminIpCountService;
import project.ddos.CheckIpRequestCountService;
import project.ddos.DdosRedisKeys;
import project.ddos.IpMenuService;
import project.ddos.model.IpMenu;
import project.log.Log;
import project.log.LogService;
import project.redis.RedisHandler;
import project.syspara.SysparaService;
import security.SecUser;
import security.internal.SecUserService;

public class AdminIpCountServiceImpl extends HibernateDaoSupport implements AdminIpCountService {

	private SecUserService secUserService;
	private LogService logService;
	private SysparaService sysparaService;
	private PasswordEncoder passwordEncoder;
	private IpMenuService ipMenuService;
	private CheckIpRequestCountService checkIpRequestCountService;
	private RedisHandler redisHandler;
	private JdbcTemplate jdbcTemplate;

	@Override
	public Page cachePagedQuery(int pageNo, int pageSize, String ip_para, String type_para, Long limit_count) {
		List<Map<String, Object>> cachePagedQueryIpCount = checkIpRequestCountService.cachePagedQueryIpCount(pageNo,
				pageSize, ip_para, type_para, limit_count, true);
		Page page = new Page(pageNo, pageSize, Integer.MAX_VALUE);
		page.setElements(cachePagedQueryIpCount);
		return page;
	}

	public Map<String, Object> sumDates() {
		return checkIpRequestCountService.ipCountSumDates();
	}

	public void batchAddBlack(Long limitCount, String operatorUsername, String loginSafeword, String ip) {
		checkLoginSafeword(operatorUsername, loginSafeword);
		// 异步线程添加黑名单
		BlackDelayThread lockDelayThread = new BlackDelayThread(ip, limitCount, operatorUsername);
		Thread t = new Thread(lockDelayThread);
		t.start();

	}

	public class BlackDelayThread implements Runnable {
		private String ip;
		private Long limitCount;
		private String operatorUsername;

		public void run() {
			try {
				List<Map<String, Object>> cachePagedQueryIpCount = checkIpRequestCountService.cachePagedQueryIpCount(1,
						Integer.MAX_VALUE, null, null, limitCount, true);
				Map<String, Object> batchData = new HashMap<String, Object>();

				List<IpMenu> batchUpdateList = new ArrayList<IpMenu>();
				List<IpMenu> batchInsertList = new ArrayList<IpMenu>();
				List<String> ips = new ArrayList<String>();
				for (Map<String, Object> data : cachePagedQueryIpCount) {
					ips.add(data.get("ip").toString());
					IpMenu ipMenu = ipMenuService.cacheByIp(data.get("ip").toString());
					if (ipMenu == null) {
						ipMenu = new IpMenu();
						ipMenu.setCreate_time(new Date());
						ipMenu.setIp(data.get("ip").toString());
						ipMenu.setDelete_status(0);
						ipMenu.setType(IpMenu.IP_BLACK);
						ipMenu.setLast_opera_time(new Date());
						batchInsertList.add(ipMenu);
					} else {
						ipMenu.setDelete_status(0);
						ipMenu.setType(IpMenu.IP_BLACK);
						ipMenu.setLast_opera_time(new Date());
						batchUpdateList.add(ipMenu);
					}
					batchData.put(DdosRedisKeys.IP_MENU_IP + ipMenu.getIp(), ipMenu);
				}
				updateBatchIpMenu(batchUpdateList);
				insertBatchIpMenu(batchInsertList);
				redisHandler.setBatchSync(batchData);

				SecUser user = secUserService.findUserByLoginName(operatorUsername);
				saveLog(user, operatorUsername,
						"ip:" + ip + "管理员批量添加IP黑名单,警戒线为[" + limitCount + "],ip名单为[" + String.join(",", ips) + "]");
			} catch (Exception e) {
				logger.error("BlackDelayThread error:", e);
			}

		}

		public BlackDelayThread(String ip, Long limitCount, String operatorUsername) {
			this.ip = ip;
			this.limitCount = limitCount;
			this.operatorUsername = operatorUsername;
		}

	}

	/**
	 * 批量更新黑名单
	 * 
	 * @param orderList
	 */
	protected void updateBatchIpMenu(final List<IpMenu> dataList) {
		String sql = "UPDATE T_IP_MENU SET DELETE_STATUS=?,LAST_OPERA_TIME=?,TYPE=? WHERE IP=?";
		int[] batchUpdate = jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				ps.setInt(1, dataList.get(i).getDelete_status());
				ps.setTimestamp(2, new Timestamp(dataList.get(i).getLast_opera_time().getTime()));
				ps.setString(3, dataList.get(i).getType());
				ps.setString(4, dataList.get(i).getIp());
			}

			@Override
			public int getBatchSize() {
				return dataList.size();
			}
		});

//		log.info("end miner batch update attr:{}",batchUpdate);
	}

	/**
	 * 批量新增黑名单
	 * 
	 * @param orderList
	 */
	protected void insertBatchIpMenu(final List<IpMenu> dataList) {
		String sql = "INSERT INTO T_IP_MENU(UUID,IP,TYPE,DELETE_STATUS,LAST_OPERA_TIME,CREATE_TIME) VALUES(?,?,?,?,?,?)";
		int[] batchUpdate = jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				ps.setString(1, UUIDGenerator.getUUID());
				ps.setString(2, dataList.get(i).getIp());
				ps.setString(3, dataList.get(i).getType());
				ps.setInt(4, dataList.get(i).getDelete_status());
				ps.setTimestamp(5, new Timestamp(dataList.get(i).getLast_opera_time().getTime()));
				ps.setTimestamp(6, new Timestamp(dataList.get(i).getCreate_time().getTime()));
			}

			@Override
			public int getBatchSize() {
				return dataList.size();
			}
		});

//		log.info("end miner batch update attr:{}",batchUpdate);
	}

	public void clearData(String operatorUsername, String loginSafeword, String ip) {
		checkLoginSafeword(operatorUsername, loginSafeword);
		checkIpRequestCountService.clearIpCountCache();
		checkIpRequestCountService.clearIpTimeCacheCache();
		SecUser user = secUserService.findUserByLoginName(operatorUsername);
		saveLog(user, operatorUsername, "ip:" + ip + "管理员清除了所有ip请求数据");
	}

	/**
	 * 获取到url的访问数
	 * 
	 * @param ip
	 * @return
	 */
	public List<Map<String, Object>> getUrlsCount(String ip) {
		Map<String, AtomicLong> cacheUrlCount = this.checkIpRequestCountService.cacheUrlCount(ip);
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		if (cacheUrlCount == null || cacheUrlCount.isEmpty()) {
			return result;
		}
		for (String key : cacheUrlCount.keySet()) {
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("url", key);
			data.put("count", cacheUrlCount.get(key).get());
			result.add(data);
		}
		return result;
	}

	/**
	 * 验证登录人资金密码
	 * 
	 * @param operatorUsername
	 * @param loginSafeword
	 */
	private void checkLoginSafeword(String operatorUsername, String loginSafeword) {
		SecUser sec = this.secUserService.findUserByLoginName(operatorUsername);
		String sysSafeword = sec.getSafeword();
		String safeword_md5 = passwordEncoder.encodePassword(loginSafeword, operatorUsername);
		if (!safeword_md5.equals(sysSafeword)) {
			throw new BusinessException("登录人资金密码错误");
		}

	}

	public void saveLog(SecUser secUser, String operator, String context) {
		Log log = new Log();
		log.setCategory(Constants.LOG_CATEGORY_OPERATION);
		log.setOperator(operator);
		log.setUsername(secUser.getUsername());
		log.setPartyId(secUser.getPartyId());
		log.setLog(context);
		log.setCreateTime(new Date());
		logService.saveSync(log);
	}

	public void setSecUserService(SecUserService secUserService) {
		this.secUserService = secUserService;
	}

	public void setLogService(LogService logService) {
		this.logService = logService;
	}

	public void setSysparaService(SysparaService sysparaService) {
		this.sysparaService = sysparaService;
	}

	public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
		this.passwordEncoder = passwordEncoder;
	}

	public void setIpMenuService(IpMenuService ipMenuService) {
		this.ipMenuService = ipMenuService;
	}

	public void setCheckIpRequestCountService(CheckIpRequestCountService checkIpRequestCountService) {
		this.checkIpRequestCountService = checkIpRequestCountService;
	}

	public void setRedisHandler(RedisHandler redisHandler) {
		this.redisHandler = redisHandler;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

}
