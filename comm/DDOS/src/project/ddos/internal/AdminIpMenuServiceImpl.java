package project.ddos.internal;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kernel.util.DateUtils;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;
import org.springframework.security.providers.encoding.PasswordEncoder;

import kernel.exception.BusinessException;
import kernel.util.StringUtils;
import kernel.web.Page;
import kernel.web.PagedQueryDao;
import project.Constants;
import project.contract.ContractOrder;
import project.contract.ContractRedisKeys;
import project.ddos.AdminIpMenuService;
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

public class AdminIpMenuServiceImpl extends HibernateDaoSupport implements AdminIpMenuService {

    private PagedQueryDao pagedQueryDao;
    private SecUserService secUserService;
    private LogService logService;
    private SysparaService sysparaService;
    private PasswordEncoder passwordEncoder;
    private IpMenuService ipMenuService;
    private CheckIpRequestCountService checkIpRequestCountService;

    private RedisHandler redisHandler;

	@Override
	public Page pagedQuery(int pageNo, int pageSize, String ip, String startTime, String endTime) {
		StringBuffer queryString = new StringBuffer(" SELECT menu.UUID id,menu.IP ip,menu.REMARK remark, "
				+ " menu.LAST_OPERA_TIME last_opera_time,menu.CREATE_TIME create_time, menu.CREATE_NAME createName ");
		queryString.append(" FROM T_IP_MENU menu WHERE 1 = 1 AND DELETE_STATUS=0 AND menu.TYPE = 'black' ");
		Map<String, Object> parameters = new HashMap<>();

        if (!StringUtils.isNullOrEmpty(ip)) {
            queryString.append(" and  menu.IP like:ip ");
            parameters.put("ip", "%" + ip + "%");
        }

        if (!StringUtils.isNullOrEmpty(startTime)) {
            queryString.append(" AND DATE(menu.CREATE_TIME) >= DATE(:startTime)  ");
            parameters.put("startTime", DateUtils.toDate(startTime));
        }

        if (!StringUtils.isNullOrEmpty(endTime)) {
            queryString.append(" AND DATE(menu.CREATE_TIME) <= DATE(:endTime)  ");
            parameters.put("endTime", DateUtils.toDate(endTime));
        }
        queryString.append(" GROUP BY menu.CREATE_TIME desc ");
        Page page = this.pagedQueryDao.pagedQuerySQL(pageNo, pageSize, queryString.toString(), parameters);
//		handleDatas(page.getElements());
        return page;
    }

    public void handleDatas(List<Map<String, Object>> datas) {
        List<String> ips = new ArrayList<String>();
        for (Map<String, Object> data : datas) {
            ips.add(data.get("ip").toString());
        }
        Map<String, Long> ipCountByIps = checkIpRequestCountService.ipCountByIps(ips);
        for (Map<String, Object> data : datas) {
            data.put("count", ipCountByIps.get(data.get("ip").toString()));
        }
    }

	public IpMenu getBlankIp(String ip){
		StringBuffer queryString = new StringBuffer(" FROM IpMenu where ip=?0 and type = 'black' ");
		List<IpMenu> list = (List<IpMenu>) this.getHibernateTemplate().find(queryString.toString(), new Object[] { ip });
		if (list.size() > 0) {
			return list.get(0);
		}
		return null;
	}

    public void save(IpMenu entity, String operatorUsername, String loginSafeword, String ip) {
        IpMenu ipMenu = getBlankIp(entity.getIp());
        if (ipMenu != null && ipMenu.getDelete_status() == 0) {
            throw new BusinessException("ip名单已经存在");
        }
		SecUser user = this.secUserService.findUserByLoginName(operatorUsername);
		saveLog(user, operatorUsername, "ip:" + ip + "管理员新增黑名单IP,ip名单为[" + entity.getIp() + "]");
        this.getHibernateTemplate().save(entity);
        redisHandler.setSync(DdosRedisKeys.IP_MENU_IP + entity.getIp(), entity);
        if (IpMenu.IP_BLACK.equals(entity.getType())) {
            redisHandler.sadd(DdosRedisKeys.IP_MENU_IP_BLACK, entity.getIp());
        }
    }

	public void update(IpMenu entity, String operatorUsername, String loginSafeword, String ip) {
		IpMenu ipMenu = getBlankIp(entity.getIp());
		if (ipMenu == null || ipMenu.getDelete_status() == -1) {
			throw new BusinessException("ip名单不存在");
		}
		SecUser user = this.secUserService.findUserByLoginName(operatorUsername);
		saveLog(user, operatorUsername, "ip:" + ip + "管理员更新IP名单,ip名单为[" + entity.getIp() + "],原ip为[" + ipMenu.getIp()
				+ "],新ip为[" + entity.getIp() + "]");

		ipMenu.setIp(entity.getIp());
		ipMenu.setLast_opera_time(entity.getLast_opera_time());
		this.getHibernateTemplate().merge(ipMenu);
		redisHandler.remove(DdosRedisKeys.IP_MENU_IP + entity.getIp());
		redisHandler.setSync(DdosRedisKeys.IP_MENU_IP + entity.getIp(),ipMenu);
	}

	public void delete(String menu_ip, String operatorUsername, String loginSafeword, String ip) {
		IpMenu ipMenu = getBlankIp(menu_ip);
		if (ipMenu == null || ipMenu.getDelete_status() == -1) {
			throw new BusinessException("ip名单不存在");
		}
		SecUser user = this.secUserService.findUserByLoginName(operatorUsername);
		saveLog(user, operatorUsername, "ip:" + ip + "管理员删除IP名单,ip名单为[" + menu_ip + "],类型为[" + ipMenu.getType() + "]");
		this.getHibernateTemplate().delete(ipMenu);
		redisHandler.remove(DdosRedisKeys.IP_MENU_IP + ipMenu.getIp());
        if (IpMenu.IP_BLACK.equals(ipMenu.getType())) {
            redisHandler.srem(DdosRedisKeys.IP_MENU_IP_BLACK, ipMenu.getIp());
        }
	}

	@Override
	public void updateIp(IpMenu entity, String oldIp, String usernameLogin, String loginSafeword, String ip) {
		IpMenu ipMenu = getBlankIp(oldIp);
		if (ipMenu == null || ipMenu.getDelete_status() == -1) {
			throw new BusinessException("ip名单不存在");
		}
		SecUser user = this.secUserService.findUserByLoginName(usernameLogin);
		saveLog(user, usernameLogin, "ip:" + ip + "管理员更新IP名单,ip名单为[" + entity.getIp() + "],原类型为[" + ipMenu.getType()
				+ "],新类型为[" + entity.getType() + "]");
		ipMenu.setIp(entity.getIp());
		ipMenu.setDelete_status(entity.getDelete_status());
		ipMenu.setType(entity.getType());
		ipMenu.setLast_opera_time(entity.getLast_opera_time());
		ipMenu.setRemark(entity.getRemark());
		this.ipMenuService.updateIp(oldIp,ipMenu);
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

    public void setPagedQueryDao(PagedQueryDao pagedQueryDao) {
        this.pagedQueryDao = pagedQueryDao;
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
}
