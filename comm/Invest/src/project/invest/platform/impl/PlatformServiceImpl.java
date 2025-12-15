package project.invest.platform.impl;

import kernel.exception.BusinessException;
import kernel.util.DateUtils;
import kernel.util.StringUtils;
import kernel.util.UUIDGenerator;
import kernel.web.Page;
import kernel.web.PagedQueryDao;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;
import project.invest.platform.AdminPlatformService;
import project.invest.platform.Platfrom;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class PlatformServiceImpl extends HibernateDaoSupport implements AdminPlatformService {

    private PagedQueryDao pagedQueryDao;
    @Override
    public Page findPlatformList(int pageNo, int pageSize, String name, String startTime, String endTime) {
        Map<String, Object> parameters = new HashMap<>();
        StringBuffer queryString = new StringBuffer(
                " SELECT UUID id, CREATE_TIME createTime, NAME name, STATUS status");
        queryString.append(" FROM T_PLATFROM  WHERE 1 = 1 ");

        if (!StringUtils.isNullOrEmpty(name)) {
            queryString.append(" and   NAME =:name");
            parameters.put("name", name);
        }

        if (!StringUtils.isNullOrEmpty(startTime)) {
            queryString.append(" AND DATE(CREATE_TIME) >= DATE(:startTime)  ");
            parameters.put("startTime",DateUtils.toDate(startTime));
        }
        if (!StringUtils.isNullOrEmpty(endTime)) {
            queryString.append(" AND DATE(CREATE_TIME) <= DATE(:endTime)  ");
            parameters.put("endTime", DateUtils.toDate(endTime));
        }
        queryString.append(" ORDER BY CREATE_TIME DESC ");
        Page page = this.pagedQueryDao.pagedQuerySQL(pageNo, pageSize, queryString.toString(), parameters);
        return page;

    }

    @Override
    public void addOrModify(String id, String name, String createTime, Integer status) {
        if (StringUtils.isEmptyString(id)) {
            Platfrom platfrom = new Platfrom();
            platfrom.setId(UUIDGenerator.getUUID());
            platfrom.setStatus(status);
            platfrom.setCreateTime(new Date());
            platfrom.setName(name);
            this.getHibernateTemplate().save(platfrom);
            return;
        }

        Platfrom platfrom = this.getHibernateTemplate().get(Platfrom.class, id);

        if(null == platfrom){
            throw new BusinessException("平台不存在，请刷新");
        }
        platfrom.setStatus(status);
        platfrom.setName(name);
       this.getHibernateTemplate().update(platfrom);
    }

    @Override
    public void delete(String id) {
        if(StringUtils.isNotEmpty(id)){
            Platfrom platfrom = this.getHibernateTemplate().get(Platfrom.class, id);
            this.getHibernateTemplate().delete(platfrom);
        }
    }

    /**
     * 获取未禁用的平台
     * @return
     */
    @Override
    public List<Platfrom> findAllPlatfrom() {
            Criteria criteria = getHibernateTemplate().getSessionFactory().getCurrentSession().createCriteria(Platfrom.class);
            criteria.add( Restrictions.eq("status", 0 ) );
            criteria.addOrder(Order.desc("createTime"));
            return criteria.list();
        }

    @Override
    public Platfrom findById(String id) {
        return this.getHibernateTemplate().get(Platfrom.class,id);
    }

    public void setPagedQueryDao(PagedQueryDao pagedQueryDao) {
        this.pagedQueryDao = pagedQueryDao;
    }
}