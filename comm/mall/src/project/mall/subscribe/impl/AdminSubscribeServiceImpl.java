package project.mall.subscribe.impl;

import kernel.util.DateUtils;
import kernel.util.StringUtils;
import kernel.web.Page;
import kernel.web.PagedQueryDao;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;
import project.mall.goods.model.Evaluation;
import project.mall.subscribe.AdminSubscribeService;
import project.mall.subscribe.model.Subscribe;

import java.util.HashMap;
import java.util.Map;

public class AdminSubscribeServiceImpl extends HibernateDaoSupport implements AdminSubscribeService {

    private PagedQueryDao pagedQueryDao;

    @Override
    public Page pagedQuery(int pageNo, int pageSize, String startTime, String endTime, String email) {

        StringBuffer queryString = new StringBuffer();
        queryString.append(" SELECT s.UUID id, s.EMAIL email, s.CREATE_TIME createTime");
        queryString.append(" FROM ");
        queryString.append(" T_MALL_SUBSCRIBE s ");
        queryString.append(" WHERE 1=1 ");

        Map<String, Object> parameters = new HashMap<String, Object>();
        if (!StringUtils.isNullOrEmpty(email)) {
            queryString.append(" AND s.EMAIL =:email ");
            parameters.put("email", email);
        }

        if (!StringUtils.isNullOrEmpty(startTime)) {
            queryString.append(" AND DATE(s.CREATE_TIME) >= DATE(:startTime)  ");
            parameters.put("startTime", DateUtils.toDate(startTime));
        }

        if (!StringUtils.isNullOrEmpty(endTime)) {
            queryString.append(" AND DATE(s.CREATE_TIME) <= DATE(:endTime)  ");
            parameters.put("endTime", DateUtils.toDate(endTime));
        }
        queryString.append(" ORDER BY s.CREATE_TIME DESC ");
        Page page = this.pagedQueryDao.pagedQuerySQL(pageNo, pageSize, queryString.toString(), parameters);
        return page;
    }

    public Subscribe findSubscribeById(String id) {
        return this.getHibernateTemplate().get(Subscribe.class, id);
    }

    @Override
    public void delete(String id) {
        Subscribe subscribe = findSubscribeById(id);
        this.getHibernateTemplate().delete(subscribe);
    }
    public void setPagedQueryDao(PagedQueryDao pagedQueryDao) {
        this.pagedQueryDao = pagedQueryDao;
    }
}
