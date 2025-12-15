package project.invest.expert.impl;

import kernel.util.DateUtils;
import kernel.web.Page;
import kernel.web.PagedQueryDao;
import org.apache.commons.lang3.StringUtils;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;
import project.invest.expert.AdminExpertService;
import project.invest.expert.model.Expert;

import java.util.HashMap;
import java.util.Map;

public class AdminExpertServiceImpl extends HibernateDaoSupport implements AdminExpertService {

    private PagedQueryDao pagedQueryDao;
    @Override
    public Page pagedQuery(int pageNo, int pageSize, String name, String lang, String startTime, String endTime, Integer status) {
        StringBuffer queryString = new StringBuffer(
                " SELECT UUID id, NAME name, CONTENT content, Lang lang, ICON_IMG iconImg, "
                        + " CREATE_TIME createTime, SORT sort, STATUS status, SUMMARY summary ");
        queryString.append(
                " FROM T_INVEST_EXPERT  WHERE 1 = 1 ");

        Map<String, Object> parameters = new HashMap();
        if (StringUtils.isNotEmpty(lang)) {
            queryString.append(" AND LANG =:lang ");
            parameters.put("lang", lang);
        }
        if (StringUtils.isNotEmpty(name)) {
            queryString.append(" AND NAME like:name ");
            parameters.put("name",  "%" + name + "%");
        }

        if (-2 != status) {
            queryString.append(" and STATUS =:status");
            parameters.put("status", status);
        }

        if (!kernel.util.StringUtils.isNullOrEmpty(startTime)) {
            queryString.append(" AND DATE(CREATE_TIME) >= DATE(:startTime)  ");
            parameters.put("startTime", DateUtils.toDate(startTime));
        }

        if (!kernel.util.StringUtils.isNullOrEmpty(endTime)) {
            queryString.append(" AND DATE(CREATE_TIME) <= DATE(:endTime)  ");
            parameters.put("endTime", DateUtils.toDate(endTime));
        }

        queryString.append(" order by CREATE_TIME desc ");
        Page page = this.pagedQueryDao.pagedQuerySQL(pageNo, pageSize, queryString.toString(), parameters);
        return page;
    }

    @Override
    public void save(Expert expert) {
        this.getHibernateTemplate().save(expert);
    }

    @Override
    public Expert findById(String id) {
        return getHibernateTemplate().get(Expert.class, id);
    }

    @Override
    public void update(Expert expert) {
       this.getHibernateTemplate().update(expert);
    }

    @Override
    public void delete(Expert expert) {
        this.getHibernateTemplate().delete(expert);
    }


    public void setPagedQueryDao(PagedQueryDao pagedQueryDao) {
        this.pagedQueryDao = pagedQueryDao;
    }
}