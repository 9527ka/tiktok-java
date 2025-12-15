package project.mall.area.impl;


import kernel.exception.BusinessException;
import kernel.util.StringUtils;
import kernel.web.Page;
import kernel.web.PagedQueryDao;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;
import project.mall.area.AdminMallCountryService;
import project.mall.area.model.MallCity;
import project.mall.area.model.MallCountry;
import project.mall.area.model.MallState;
import project.redis.RedisHandler;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 后台区域管理
 */
public class AdminMallCountryServiceImpl extends HibernateDaoSupport implements AdminMallCountryService {
    private static Log log = LogFactory.getLog(AdminMallCountryServiceImpl.class);

    private PagedQueryDao pagedQueryDao;

    protected RedisHandler redisHandler;
    private JdbcTemplate jdbcTemplate;

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Page pagedQueryCountry(int pageNo, int pageSize, String countryName, Integer flag) {
        StringBuffer queryString = new StringBuffer();
        queryString.append(" SELECT ");
        queryString.append(" a.* ");
        queryString.append(" FROM ");
        queryString.append(" T_MALL_COUNTRIES a");
        queryString.append(" WHERE 1=1 ");
        Map<String, Object> parameters = new HashMap<String, Object>();
        if (!StringUtils.isNullOrEmpty(countryName)) {
            queryString.append(" AND (a.COUNTRY_NAME_CN like:countryNameCn or a.COUNTRY_NAME_EN like:countryNameEn or a.COUNTRY_NAME_TW like:countryNameTw)");
            parameters.put("countryNameCn", "%" + countryName + "%");
            parameters.put("countryNameEn", "%" + countryName + "%");
            parameters.put("countryNameTw", "%" + countryName + "%");
        }
        if (flag != null) {
            queryString.append(" and a.FLAG = :flag  ");
            parameters.put("flag", flag);
        }
        queryString.append(" ORDER BY a.UPDATED_TIME DESC ");
        Page page = this.pagedQueryDao.pagedQuerySQL(pageNo, pageSize, queryString.toString(), parameters);
        return page;
    }


    public List<MallCountry> findAllCountry() {
        Criteria criteria = getHibernateTemplate().getSessionFactory().getCurrentSession().createCriteria(MallCountry.class);
        criteria.add(Restrictions.eq("flag", 1));
        return criteria.list();
    }


    @Override
    public void saveCountry(MallCountry mallCountry) {
        if (StringUtils.isNullOrEmpty(mallCountry.getCountryNameCn())||StringUtils.isNullOrEmpty(mallCountry.getCountryNameEn())
                ||StringUtils.isNullOrEmpty(mallCountry.getCountryNameTw())) {
            throw new BusinessException("国家名称不能为空");
        }

        Criteria criteria = getHibernateTemplate().getSessionFactory().getCurrentSession().createCriteria(MallCountry.class);
        criteria.add(Restrictions.eq("countryNameCn", mallCountry.getCountryNameCn()));
        criteria.add(Restrictions.eq("countryNameEn", mallCountry.getCountryNameEn()));
        criteria.add(Restrictions.eq("countryNameTw", mallCountry.getCountryNameTw()));
        List<MallCountry> list = criteria.list();
        ;
        if (CollectionUtils.isNotEmpty(list)) {
            throw new BusinessException("国家已存在");
        }
        mallCountry.setUpdatedAt(new Date());
        getHibernateTemplate().save(mallCountry);
    }

    @Override
    public MallCountry findCountryById(Long id) {
        return getHibernateTemplate().get(MallCountry.class, id);
    }

    @Override
    public void updateCountry(MallCountry country) {
        if (country.getId() == null) {
            throw new BusinessException("国家ID不能为空");
        }
        country.setUpdatedAt(new Date());
        getHibernateTemplate().update(country);
    }

    @Override
    public void updateCountryStatus(Long id, Integer flag) {
        MallCountry country = getHibernateTemplate().get(MallCountry.class, id);
        if (country == null) {
            throw new BusinessException("国家不存在");
        }
        country.setUpdatedAt(new Date());
        country.setFlag(flag);
        getHibernateTemplate().update(country);
        //更新所属国家州禁用启用状态
        Criteria criteriaState = getHibernateTemplate().getSessionFactory().getCurrentSession().createCriteria(MallState.class);
        criteriaState.add(Restrictions.eq("countryId", id));
        List<MallState> listState = criteriaState.list();
        ;
        if (listState != null && listState.size() > 0) {
            jdbcTemplate.update("UPDATE T_MALL_STATES SET FLAG=" + flag + ",UPDATED_TIME=" + new Date() + ", WHERE COUNTRY_ID=" + id);
        }
        //更新所属国家城市禁用启用状态
        Criteria criteriaCity = getHibernateTemplate().getSessionFactory().getCurrentSession().createCriteria(MallCity.class);
        criteriaCity.add(Restrictions.eq("countryId", id));
        List<MallCity> listCity = criteriaCity.list();
        if (listCity != null && listCity.size() > 0) {
            jdbcTemplate.update("UPDATE T_MALL_CITIES SET FLAG=" + flag + ",UPDATED_TIME=" + new Date() + ", WHERE COUNTRY_ID=" + id);
        }
    }

    @Override
    public void deleteCountry(Long id) {
        MallCountry country = getHibernateTemplate().get(MallCountry.class, id);
        if (country == null) {
            throw new BusinessException("国家不存在");
        }
        getHibernateTemplate().delete(country);
        //删除所属国家州禁用启用状态
        Criteria criteriaState = getHibernateTemplate().getSessionFactory().getCurrentSession().createCriteria(MallState.class);
        criteriaState.add(Restrictions.eq("countryId", id));
        List<MallState> listState = criteriaState.list();
        if (listState != null && listState.size() > 0) {
            getHibernateTemplate().deleteAll(listState);
        }
        //删除所属国家城市禁用启用状态
        Criteria criteriaCity = getHibernateTemplate().getSessionFactory().getCurrentSession().createCriteria(MallCity.class);
        criteriaCity.add(Restrictions.eq("countryId", id));
        List<MallCity> listCity = criteriaCity.list();
        if (listCity != null && listCity.size() > 0) {
            getHibernateTemplate().deleteAll(listCity);
        }
    }

    public void setPagedQueryDao(PagedQueryDao pagedQueryDao) {
        this.pagedQueryDao = pagedQueryDao;
    }

}