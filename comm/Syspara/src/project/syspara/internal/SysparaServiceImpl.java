package project.syspara.internal;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import cn.hutool.core.util.StrUtil;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

import kernel.util.StringUtils;
import kernel.web.Page;
import kernel.web.PagedQueryDao;
import project.RedisKeys;
import project.redis.RedisHandler;
import project.syspara.Syspara;
import project.syspara.SysparaService;

public class SysparaServiceImpl extends HibernateDaoSupport implements SysparaService {
	private PagedQueryDao pagedQueryDao;

	private RedisHandler redisHandler;

	@Override
	public Syspara find(String code) {
		if (StrUtil.isBlank(code)) {
			return null;
		}

		/**
		 * 读数据库
		 */
		Syspara syspara = (Syspara) redisHandler.get(RedisKeys.SYSPARA_CODE + code);
		if (syspara != null) {
			return syspara;
		}

		syspara = findByDB(code);
		if (syspara != null) {
			redisHandler.setSync(RedisKeys.SYSPARA_CODE + code, syspara);
		}

		return syspara;
	}

	@Override
	public Syspara findFromDB(String code) {
		if (StrUtil.isBlank(code)) {
			return null;
		}
		Syspara	syspara = findByDB(code);
		if (syspara != null) {
			redisHandler.setSync(RedisKeys.SYSPARA_CODE + code, syspara);
		}
		return syspara;
	}



	@Override
	public String findString(String code){
		Syspara syspara = find(code);
		if (syspara != null) {
			return syspara.getValue();
		}
		return "";
	}

	/**
	 * 数据库读取，主要用于bean初始化，（spring 初始化bean在redis数据加载之前，导致无法读取问题）
	 * 
	 * @param code
	 * @return
	 */
	public Syspara findByDB(String code) {
		/**
		 * 读数据库
		 */
//		List<Syspara> list = this.getHibernateTemplate().find("FROM  Syspara WHERE code=?", new Object[] { code });
		
		List<Syspara> list = (List<Syspara>) this.getHibernateTemplate().find("FROM  Syspara WHERE code=?0",new Object[] {code});
		
		if (CollectionUtils.isNotEmpty(list)) {
			return list.get(0);
		}
		
		return null;
	}

	@Override
	public void update(Syspara entity) {
		if (entity.getType() == 0) {
			return;
		}
		this.getHibernateTemplate().update(entity);
		redisHandler.setSync(RedisKeys.SYSPARA_CODE + entity.getCode(), entity);
	}

	@Override
	public Page pagedQuery(int pageNo, int pageSize) {
//		StringBuffer queryString = new StringBuffer("FROM  Syspara");
//		queryString.append(" where 1=1 ");
//
//		Map<String, Object> parameters = new HashMap<String, Object>();
//
//		queryString.append(" order by code  desc ");
//
//		Page page = pagedQueryDao.pagedQueryHql(pageNo, pageSize, queryString.toString(), parameters);
//
//		return page;
		
		StringBuffer queryString = new StringBuffer();
		queryString.append(" SELECT ");
		queryString.append(" sp.CODE code, sp.SVALUE value, sp.NOTES notes ");
		queryString.append(" FROM T_SYSPARA sp ");
		queryString.append(" WHERE 1=1 ");
		queryString.append(" order by code desc ");

		Map<String, Object> parameters = new HashMap<String, Object>();

		Page page = this.pagedQueryDao.pagedQuerySQL(pageNo, pageSize, queryString.toString(), parameters);
		return page;
	}
	
	/**
	 * 获取 系统参数（ROOT) 列表
	 */
	@Override
	public Page pagedQueryByNotes(int pageNo, int pageSize, String notes_para) {
		
		StringBuffer queryString = new StringBuffer();
		queryString.append(" SELECT ");
		queryString.append(" sp.CODE code, sp.SVALUE value, sp.NOTES notes, sp.MODIFY modify ");
		queryString.append(" FROM T_SYSPARA sp ");
		queryString.append(" WHERE 1=1 AND sp.BAG_TYPE = 0");
		
		Map<String, Object> parameters = new HashMap<String, Object>();

		if (!StringUtils.isNullOrEmpty(notes_para)) {
			queryString.append(" and sp.NOTES like  :notes ");
			parameters.put("notes", "%" + notes_para + "%");
		}
		
		queryString.append(" order by code desc ");

		Page page = this.pagedQueryDao.pagedQuerySQL(pageNo, pageSize, queryString.toString(), parameters);
		return page;
	}

	/**
	 * 获取 系统参数（ADMIN) 列表
	 */
	@Override
	public Page pagedQueryByNotesAdmin(int pageNo, int pageSize, String notes_para) {
		
		StringBuffer queryString = new StringBuffer();
		queryString.append(" SELECT ");
		queryString.append(" sp.CODE code, sp.SVALUE value, sp.NOTES notes, sp.MODIFY modify ");
		queryString.append(" FROM T_SYSPARA sp ");
		queryString.append(" WHERE 1=1 AND sp.BAG_TYPE = 0");
		
		Map<String, Object> parameters = new HashMap<String, Object>();

		if (!StringUtils.isNullOrEmpty(notes_para)) {
			queryString.append(" and sp.NOTES like  :notes ");
			parameters.put("notes", "%" + notes_para + "%");
		}
		
		// 0/ROOT可见； 2/管理员可见； 1/用户参数；
		queryString.append(" and sp.STYPE in (:types) ");
		parameters.put("types", Arrays.asList(2, 1));
		
		queryString.append(" order by code desc ");

		Page page = this.pagedQueryDao.pagedQuerySQL(pageNo, pageSize, queryString.toString(), parameters);
		return page;
	}

	@Override
	public void loadCacheUpdate() {
		List<Syspara> list =(List<Syspara>) this.getHibernateTemplate().find(" FROM Syspara ");

		Map<String, Syspara> cache = new ConcurrentHashMap<String, Syspara>();

		for (int i = 0; i < list.size(); i++) {
			Syspara syspara = list.get(i);
			redisHandler.setSync(RedisKeys.SYSPARA_CODE + syspara.getCode(), syspara);
			cache.put(list.get(i).getId().toString(), syspara);
		}
		redisHandler.setSync(RedisKeys.SYSPARA_MAP, cache);
	}

	@Override
	public void save(Syspara entity) {
		this.getHibernateTemplate().save(entity);
		redisHandler.setSync(RedisKeys.SYSPARA_CODE + entity.getCode(), entity);

	}

	public void setPagedQueryDao(PagedQueryDao pagedQueryDao) {
		this.pagedQueryDao = pagedQueryDao;
	}

	public void setRedisHandler(RedisHandler redisHandler) {
		this.redisHandler = redisHandler;
	}

}
