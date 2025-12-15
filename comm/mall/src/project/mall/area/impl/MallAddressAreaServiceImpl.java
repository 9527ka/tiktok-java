package project.mall.area.impl;


import cn.hutool.core.util.StrUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;
import project.mall.MallRedisKeys;
import project.mall.area.MallAddressAreaService;
import project.mall.area.model.MallCity;
import project.mall.area.model.MallCountry;
import project.mall.area.model.MallState;
import project.mall.area.model.MobilePrefix;
import project.redis.RedisHandler;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * app区域获取查询
 */
public class MallAddressAreaServiceImpl extends HibernateDaoSupport implements MallAddressAreaService {
    private static Log log = LogFactory.getLog(MallAddressAreaServiceImpl.class);

    protected RedisHandler redisHandler;
    private JdbcTemplate jdbcTemplate;

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void setRedisHandler(RedisHandler redisHandler) {
        this.redisHandler = redisHandler;
    }

    public List<MallCountry> listAllCountry() {
        Criteria criteria = getHibernateTemplate().getSessionFactory().getCurrentSession().createCriteria(MallCountry.class);
        criteria.add(Restrictions.eq("flag", 1));
        return criteria.list();
    }

    @Override
    public List<MallCountry> listCountry(String countryName, String language) {
        Criteria criteria = getHibernateTemplate().getSessionFactory().getCurrentSession().createCriteria(MallCountry.class);
        if (StrUtil.isNotEmpty(countryName)) {
            if ("cn".equalsIgnoreCase(language)) {
                criteria.add(Restrictions.like("countryNameCn", countryName));
            } else if ("en".equalsIgnoreCase(language)) {
                criteria.add(Restrictions.like("countryNameEn", countryName));
            } else if ("tw".equalsIgnoreCase(language)) {
                criteria.add(Restrictions.like("countryNameTw", countryName));
            } else {
                log.error("listCountry 不支持的语言：" + language);
            }
        }
        criteria.add(Restrictions.eq("flag", 1));
        return criteria.list();
    }

    @Override
    public List<MallState> listAllState(Long countryId) {
        Criteria criteria = getHibernateTemplate().getSessionFactory().getCurrentSession().createCriteria(MallState.class);
        criteria.add(Restrictions.eq("flag", 1));
        criteria.add(Restrictions.eq("countryId", countryId));
        return criteria.list();
    }

//    @Override
//    public List<MallState> listByStateNameCn(String stateNameCn) {
//        Criteria criteria = getHibernateTemplate().getSessionFactory().getCurrentSession().createCriteria(MallState.class);
//        criteria.add(Restrictions.eq("flag", 1));
//        criteria.add(Restrictions.eq("stateNameCn", stateNameCn));
//        return criteria.list();
//    }


    @Override
    public List<MallState> listState(String stateName, Long countryId, String language) {
        Criteria criteria = getHibernateTemplate().getSessionFactory().getCurrentSession().createCriteria(MallState.class);
        if (StrUtil.isNotEmpty(stateName)) {
            if ("cn".equalsIgnoreCase(language)) {
                criteria.add(Restrictions.like("stateNameCn", stateName));
            } else if ("en".equalsIgnoreCase(language)) {
                criteria.add(Restrictions.like("stateNameEn", stateName));
            } else if ("tw".equalsIgnoreCase(language)) {
                criteria.add(Restrictions.like("stateNameTw", stateName));
            } else {
                log.error("listCountry 不支持的语言：" + language);
            }
        }
        criteria.add(Restrictions.eq("flag", 1));
        criteria.add(Restrictions.eq("countryId", countryId));
        return criteria.list();
    }

    @Override
    public List<MallCity> listAllCity(Long stateId) {
        Criteria criteria = getHibernateTemplate().getSessionFactory().getCurrentSession().createCriteria(MallCity.class);
        criteria.add(Restrictions.eq("flag", 1));
        criteria.add(Restrictions.eq("stateId", stateId));
        return criteria.list();
    }

    @Override
    public List<MallCity> listCity(String cityName, Long stateId, String language) {
        Criteria criteria = getHibernateTemplate().getSessionFactory().getCurrentSession().createCriteria(MallCity.class);
        if (StrUtil.isNotEmpty(cityName)) {
            if ("cn".equalsIgnoreCase(language)) {
                criteria.add(Restrictions.like("cityNameCn", cityName));
            } else if ("en".equalsIgnoreCase(language)) {
                criteria.add(Restrictions.like("cityNameEn", cityName));
            } else if ("tw".equalsIgnoreCase(language)) {
                criteria.add(Restrictions.like("cityNameTw", cityName));
            } else {
                log.error("listCountry 不支持的语言：" + language);
            }
        }
        criteria.add(Restrictions.eq("flag", 1));
        criteria.add(Restrictions.eq("stateId", stateId));
        return criteria.list();
    }

    @Override
    public MallCountry findCountryById(Long id) {
        MallCountry mallCountry = null;
        mallCountry = (MallCountry) redisHandler.get(MallRedisKeys.MALL_COUNTRY + id);
        if (Objects.nonNull(mallCountry)) {
            return mallCountry;
        }
        return getHibernateTemplate().get(MallCountry.class, id);
    }

    @Override
    public MallState findMallStateById(Long id) {
        MallState mallState = null;
        mallState = (MallState) redisHandler.get(MallRedisKeys.MALL_STATE + id);
        if (Objects.nonNull(mallState)) {
            return mallState;
        }
        return getHibernateTemplate().get(MallState.class, id);
    }

    @Override
    public MallCity findCityById(Long id) {
        MallCity mallCity = null;
        mallCity = (MallCity) redisHandler.get(MallRedisKeys.MALL_CITY + id);
        if (Objects.nonNull(mallCity)) {
            return mallCity;
        }
        return getHibernateTemplate().get(MallCity.class, id);
    }

    @Override
    public List<MobilePrefix> listAllMobilePrefix() {
        Criteria criteria = getHibernateTemplate().getSessionFactory().getCurrentSession().createCriteria(MobilePrefix.class);
        return criteria.list();
    }

    @Override
    public List<String> findAddressWithCodeAndLanguage(Long countryId, Long stateId, Long cityId, String language) {
        String countryName = "";
        String stateName = "";
        String cityName = "";
        MallCountry mallCountry = (MallCountry) redisHandler.get(MallRedisKeys.MALL_COUNTRY + countryId);
        MallState mallState = (MallState) redisHandler.get(MallRedisKeys.MALL_STATE + stateId);
        MallCity mallCity = (MallCity) redisHandler.get(MallRedisKeys.MALL_CITY + cityId);
        if ("cn".equalsIgnoreCase(language)) {
            if (Objects.nonNull(mallCountry)) countryName=mallCountry.getCountryNameCn();
            if (Objects.nonNull(mallState)) stateName=mallState.getStateNameCn();
            if (Objects.nonNull(mallCity)) cityName=mallCity.getCityNameCn();
        } else if ("tw".equalsIgnoreCase(language)) {
            if (Objects.nonNull(mallCountry)) countryName=mallCountry.getCountryNameTw();
            if (Objects.nonNull(mallState)) stateName=mallState.getStateNameTw();
            if (Objects.nonNull(mallCity)) cityName=mallCity.getCityNameTw();
        } else {//其他语言默认英语
            if (Objects.nonNull(mallCountry)) countryName=mallCountry.getCountryNameEn();
            if (Objects.nonNull(mallState)) stateName=mallState.getStateNameEn();
            if (Objects.nonNull(mallCity)) cityName=mallCity.getCityNameEn();
        }
        return Arrays.asList(countryName,stateName,cityName);
    }
}