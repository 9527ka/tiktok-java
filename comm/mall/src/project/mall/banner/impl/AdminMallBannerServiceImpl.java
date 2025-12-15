package project.mall.banner.impl;

import kernel.util.DateUtils;
import kernel.util.StringUtils;
import kernel.web.Page;
import kernel.web.PagedQueryDao;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;
import project.mall.banner.AdminMallBannerService;
import project.mall.banner.model.MallBanner;
import project.news.News;

import java.util.HashMap;
import java.util.Map;

public class AdminMallBannerServiceImpl extends HibernateDaoSupport implements AdminMallBannerService {

    private PagedQueryDao pagedQueryDao;

    @Override
    public Page pagedQuery(int pageNo, int pageSize, String type, String endTime, String startTime) {
        StringBuffer queryString = new StringBuffer();
        queryString.append(" SELECT b.UUID id, b.IMG_URL imgUrl, b.SORT sort, b.TYPE type, b.LINK link, b.REMARKS remarks, ");
        queryString.append(" b.IMG_TYPE imgType, b.CREATE_TIME createTime");
        queryString.append(" FROM ");
        queryString.append(" T_MALL_BANNER b ");
        queryString.append(" WHERE 1=1 ");

        Map<String, Object> parameters = new HashMap<String, Object>();
        if (!StringUtils.isNullOrEmpty(type)) {
            queryString.append(" AND b.TYPE =:type ");
            parameters.put("type", type);
        }

        if (!StringUtils.isNullOrEmpty(startTime)) {
            queryString.append(" AND DATE(b.CREATE_TIME) >= DATE(:startTime)  ");
            parameters.put("startTime", DateUtils.toDate(startTime));
        }

        if (!StringUtils.isNullOrEmpty(endTime)) {
            queryString.append(" AND DATE(b.CREATE_TIME) <= DATE(:endTime)  ");
            parameters.put("endTime", DateUtils.toDate(endTime));
        }
        queryString.append(" ORDER BY b.CREATE_TIME DESC ");
        Page page = this.pagedQueryDao.pagedQuerySQL(pageNo, pageSize, queryString.toString(), parameters);
        return page;
    }

    @Override
    public void save(MallBanner banner) {
        this.getHibernateTemplate().save(banner);
    }

    @Override
    public MallBanner findById(String id) {
        return getHibernateTemplate().get(MallBanner.class, id);
    }

    @Override
    public void update(MallBanner banner) {
        this.getHibernateTemplate().update(banner);
    }

    @Override
    public void delete(MallBanner banner) {
        this.getHibernateTemplate().delete(banner);
    }

    public void setPagedQueryDao(PagedQueryDao pagedQueryDao) {
        this.pagedQueryDao = pagedQueryDao;
    }

}
