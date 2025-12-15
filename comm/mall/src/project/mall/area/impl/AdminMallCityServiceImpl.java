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
import project.mall.area.AdminMallCityService;
import project.mall.area.model.MallCity;
import project.redis.RedisHandler;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 后台区域管理
 */
public class AdminMallCityServiceImpl extends HibernateDaoSupport implements AdminMallCityService {
    private static Log log = LogFactory.getLog(AdminMallCityServiceImpl.class);

    private PagedQueryDao pagedQueryDao;

    protected RedisHandler redisHandler;
    private JdbcTemplate jdbcTemplate;

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    @Override
    public Page pagedQueryCity(int pageNo, int pageSize, String cityName, Integer flag) {
        StringBuffer queryString = new StringBuffer();
        queryString.append(" SELECT ");
        queryString.append(" a.* ");
        queryString.append(" FROM ");
        queryString.append(" T_MALL_CITIES a ");
        queryString.append(" LEFT JOIN T_MALL_COUNTRIES c ON c.ID = a.COUNTRY_ID ");
        queryString.append(" LEFT JOIN T_MALL_STATES s ON a.STATE_ID= s.ID ");
        queryString.append(" WHERE 1=1 ");
        Map<String, Object> parameters = new HashMap<String, Object>();
        if (!StringUtils.isNullOrEmpty(cityName)) {
            queryString.append(" and (a.CITY_NAME_CN like:cityNameCn or a.CITY_NAME_EN like:cityNameEn   or a.CITY_NAME_TW like:cityNameTw) ");
            parameters.put("cityNameCn", "%" + cityName + "%");
            parameters.put("cityNameEn", "%" + cityName + "%");
            parameters.put("cityNameTw", "%" + cityName + "%");
        }
        if (flag != null) {
            queryString.append(" and a.FLAG = :flag  ");
            parameters.put("flag", flag);
        }
        queryString.append(" ORDER BY a.UPDATED_TIME DESC ");
        Page page = this.pagedQueryDao.pagedQuerySQL(pageNo, pageSize, queryString.toString(), parameters);
        return page;
    }


    @Override
    public void saveCity(MallCity mallCity) {
        if (StringUtils.isNullOrEmpty(mallCity.getCityNameCn())||StringUtils.isNullOrEmpty(mallCity.getCityNameEn())
        ||StringUtils.isNullOrEmpty(mallCity.getCityNameTw())) {
            throw new BusinessException("城市名称不能为空");
        }

        if (mallCity.getCountryId() == null) {
            throw new BusinessException("国家Id不能为空");
        }
        if (mallCity.getStateId() == null) {
            throw new BusinessException("州Id不能为空");
        }
        Criteria criteria = getHibernateTemplate().getSessionFactory().getCurrentSession().createCriteria(MallCity.class);
        criteria.add(Restrictions.eq("cityNameCn", mallCity.getCityNameCn()));
        criteria.add(Restrictions.eq("cityNameEn", mallCity.getCityNameEn()));
        criteria.add(Restrictions.eq("cityNameTw", mallCity.getCityNameTw()));
        List<MallCity> list = criteria.list();;
        if (CollectionUtils.isNotEmpty(list)) {
            throw new BusinessException("城市已存在");
        }
        mallCity.setUpdatedAt(new Date());
        getHibernateTemplate().save(mallCity);
    }


    @Override
    public MallCity findCityById(Long id) {
        return getHibernateTemplate().get(MallCity.class, id);
    }


    @Override
    public void updateCity(MallCity city) {
        if (city.getId() == null) {
            throw new BusinessException("州ID不能为空");
        }
        city.setUpdatedAt(new Date());
        getHibernateTemplate().update(city);
    }


    @Override
    public void updateCityStatus(Long id, Integer flag) {
        MallCity city = getHibernateTemplate().get(MallCity.class, id);
        if (city == null) {
            throw new BusinessException("国家不存在");
        }
        city.setUpdatedAt(new Date());
        city.setFlag(flag);
        getHibernateTemplate().update(city);
    }

    @Override
    public void deleteCity(Long id) {
        MallCity city = getHibernateTemplate().get(MallCity.class, id);
        if (city == null) {
            throw new BusinessException("城市不存在");
        }
        getHibernateTemplate().delete(city);
    }
    public void setPagedQueryDao(PagedQueryDao pagedQueryDao) {
        this.pagedQueryDao = pagedQueryDao;
    }

}