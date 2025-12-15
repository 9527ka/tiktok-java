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
import project.mall.area.AdminMallStateService;
import project.mall.area.model.MallCity;
import project.mall.area.model.MallState;
import project.redis.RedisHandler;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 后台区域管理
 */
public class AdminMallStateServiceImpl extends HibernateDaoSupport implements AdminMallStateService {
    private static Log log = LogFactory.getLog(AdminMallStateServiceImpl.class);

    private PagedQueryDao pagedQueryDao;

    protected RedisHandler redisHandler;
    private JdbcTemplate jdbcTemplate;

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Page pagedQueryState(int pageNo, int pageSize, String stateName, Integer flag) {
        StringBuffer queryString = new StringBuffer();
        queryString.append(" SELECT ");
        queryString.append(" a.*");
        queryString.append(" FROM ");
        queryString.append(" T_MALL_STATES a");
        queryString.append(" LEFT JOIN T_MALL_COUNTRIES c ON c.ID=a.COUNTRY_ID");
        queryString.append(" WHERE 1=1 ");
        Map<String, Object> parameters = new HashMap<String, Object>();
        if (!StringUtils.isNullOrEmpty(stateName)) {
            queryString.append(" AND (a.STATE_NAME_CN like:stateNameCn or a.STATE_NAME_EN like:stateNameEn or a.STATE_NAME_TW like:stateNameTw)");
            parameters.put("stateNameCn", "%" + stateName + "%");
            parameters.put("stateNameEn", "%" + stateName + "%");
            parameters.put("stateNameTw", "%" + stateName + "%");
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
    public void saveState(MallState mallState) {
        if (StringUtils.isNullOrEmpty(mallState.getStateNameCn())
        ||StringUtils.isNullOrEmpty(mallState.getStateNameEn())||StringUtils.isNullOrEmpty(mallState.getStateNameTw())) {
            throw new BusinessException("城市名称不能为空");
        }

        if (mallState.getCountryId() == null) {
            throw new BusinessException("国家ID不能为空");
        }
        Criteria criteria = getHibernateTemplate().getSessionFactory().getCurrentSession().createCriteria(MallCity.class);
        criteria.add(Restrictions.eq("stateNameCn", mallState.getStateNameCn()));
        criteria.add(Restrictions.eq("stateNameEn", mallState.getStateNameEn()));
        criteria.add(Restrictions.eq("stateNameTw", mallState.getStateNameTw()));
        criteria.add(Restrictions.eq("countryId", mallState.getCountryId()));
        List<MallState> list =criteria.list();;
        if (CollectionUtils.isNotEmpty(list)) {
            throw new BusinessException("州已存在");
        }
        mallState.setUpdatedAt(new Date());
        getHibernateTemplate().save(mallState);
    }


    @Override
    public MallState findStateById(Long id) {
        return getHibernateTemplate().get(MallState.class, id);
    }

    @Override
    public void updateState(MallState state) {
        if (state.getId() == null) {
            throw new BusinessException("州ID不能为空");
        }
        state.setUpdatedAt(new Date());
        getHibernateTemplate().update(state);
    }


    @Override
    public void updateStateStatus(Long id, Integer flag) {
        MallState state = getHibernateTemplate().get(MallState.class, id);
        if (state == null) {
            throw new BusinessException("国家不存在");
        }
        state.setUpdatedAt(new Date());
        state.setFlag(flag);
        getHibernateTemplate().update(state);

        //更新所属州城市禁用启用状态
        Criteria criteriaCity = getHibernateTemplate().getSessionFactory().getCurrentSession().createCriteria(MallCity.class);
        criteriaCity.add(Restrictions.eq("stateId", id));
        List<MallCity> listCity = criteriaCity.list();
        if (listCity != null && listCity.size() > 0) {
            jdbcTemplate.update("UPDATE T_MALL_CITIES SET FLAG=" + flag + ",UPDATED_TIME=" + new Date() + ", WHERE STATE_ID=" + id);
        }
    }

    @Override
    public void deleteState(Long id) {
        MallState state = getHibernateTemplate().get(MallState.class, id);
        if (state == null) {
            throw new BusinessException("州不存在");
        }
        getHibernateTemplate().delete(state);

        //删除所属州城市
        Criteria criteriaCity = getHibernateTemplate().getSessionFactory().getCurrentSession().createCriteria(MallCity.class);
        criteriaCity.add(Restrictions.eq("stateId", id));
        List<MallCity> listCity = criteriaCity.list();
        if (listCity != null && listCity.size() > 0) {
            getHibernateTemplate().deleteAll(listCity);
        }
    }
    public void setPagedQueryDao(PagedQueryDao pagedQueryDao) {
        this.pagedQueryDao = pagedQueryDao;
    }

}