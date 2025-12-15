package project.finance.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.orm.hibernate5.support.HibernateDaoSupport;
import org.springframework.security.providers.encoding.PasswordEncoder;

import kernel.exception.BusinessException;
import project.finance.Finance;
import project.finance.FinanceRedisKeys;
import project.finance.FinanceService;
import project.redis.RedisHandler;
import security.SecUser;
import security.internal.SecUserService;

public class FinanceServiceImpl extends HibernateDaoSupport implements FinanceService {

	private RedisHandler redisHandler;
	private SecUserService  secUserService;
	private PasswordEncoder passwordEncoder;

	public void save(Finance entity,String login_safeword,String operaterUsername) {

		SecUser sec =  this.secUserService.findUserByLoginName(operaterUsername);
		String sysSafeword =sec.getSafeword();
		String safeword_md5 = passwordEncoder.encodePassword(login_safeword,operaterUsername);
		if (!safeword_md5.equals(sysSafeword)) {
			throw new BusinessException("资金密码错误");
		}
		this.getHibernateTemplate().save(entity);
		redisHandler.setSync(FinanceRedisKeys.FINANCE_ID + entity.getId().toString(), entity);

		Map<String, Finance> cacheMap = (Map<String, Finance>) redisHandler.get(FinanceRedisKeys.FINANCE_MAP);
		if (cacheMap == null) {
			cacheMap = new ConcurrentHashMap<String, Finance>();
		}
		cacheMap.put(entity.getId().toString(), entity);
		redisHandler.setSync(FinanceRedisKeys.FINANCE_MAP, cacheMap);
		
	}

	public void update(Finance entity,String login_safeword,String operaterUsername) {
		
		SecUser sec =  this.secUserService.findUserByLoginName(operaterUsername);
		String sysSafeword =sec.getSafeword();
		String safeword_md5 = passwordEncoder.encodePassword(login_safeword,operaterUsername);
		if (!safeword_md5.equals(sysSafeword)) {
			throw new BusinessException("资金密码错误");
		}
		
		getHibernateTemplate().update(entity);
		redisHandler.setSync(FinanceRedisKeys.FINANCE_ID + entity.getId().toString(), entity);

		Map<String, Finance> cacheMap = (Map<String, Finance>) redisHandler.get(FinanceRedisKeys.FINANCE_MAP);
		if (cacheMap == null) {
			cacheMap = new ConcurrentHashMap<String, Finance>();
		}
		cacheMap.put(entity.getId().toString(), entity);
		redisHandler.setSync(FinanceRedisKeys.FINANCE_MAP, cacheMap);
	
	}

	public void delete(String id,String login_safeword,String operaterUsername) {

		SecUser sec =  this.secUserService.findUserByLoginName(operaterUsername);
		String sysSafeword =sec.getSafeword();
		String safeword_md5 = passwordEncoder.encodePassword(login_safeword,operaterUsername);
		if (!safeword_md5.equals(sysSafeword)) {
			throw new BusinessException("资金密码错误");
		}

		
		Finance entity = findById(id);
		getHibernateTemplate().delete(entity);
		redisHandler.remove(FinanceRedisKeys.FINANCE_ID + entity.getId().toString());

		Map<String, Finance> cacheMap = (Map<String, Finance>) redisHandler.get(FinanceRedisKeys.FINANCE_MAP);
		if (cacheMap != null && !cacheMap.isEmpty()) {
			cacheMap.remove(entity.getId().toString());
			redisHandler.setSync(FinanceRedisKeys.FINANCE_MAP, cacheMap);
		}
		
		
	}

	public Finance findById(String id) {
		return (Finance) redisHandler.get(FinanceRedisKeys.FINANCE_ID + id);
	}

	public List<Finance> findAll() {
		Map<String, Finance> cacheMap = (Map<String, Finance>) redisHandler.get(FinanceRedisKeys.FINANCE_MAP);
		if (cacheMap != null && !cacheMap.isEmpty()) {
			return new ArrayList<Finance>(cacheMap.values());
		}
		return new ArrayList<Finance>();
	}

	public List<Finance> findAllState_1() {
		List<Finance> list = (List<Finance>)getHibernateTemplate().find(" FROM Finance WHERE state = ?0", new Object[] { "1" });
		if (list.size() > 0)
			return list;
		return null;
	}
	
	
	

	public void setRedisHandler(RedisHandler redisHandler) {
		this.redisHandler = redisHandler;
	}

	
	public void setSecUserService(SecUserService secUserService) {
		this.secUserService = secUserService;
	}

	public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
		this.passwordEncoder = passwordEncoder;
	}
	
	

}
