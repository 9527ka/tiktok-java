package project.invest.vip.impl;

import kernel.web.Page;
import kernel.web.PagedQueryDao;
import org.hibernate.Criteria;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;
import project.invest.vip.AdminVipService;
import project.invest.vip.model.Vip;
import project.mall.seller.model.MallLevel;

public class AdminVipServiceImpl extends HibernateDaoSupport implements AdminVipService {

    private PagedQueryDao pagedQueryDao;

    @Override
    public Page pagedQuery() {
        Criteria criteria = getHibernateTemplate().getSessionFactory().getCurrentSession().createCriteria(MallLevel.class);
        Page page = new Page();
        page.setElements(criteria.list());
        return page;
    }

    @Override
    public MallLevel findById(String id) {
        return this.getHibernateTemplate().get(MallLevel.class, id);
    }

    @Override
    public void update(MallLevel mallLevel) {

        this.getHibernateTemplate().update(mallLevel);
    }

    public void setPagedQueryDao(PagedQueryDao pagedQueryDao) {
        this.pagedQueryDao = pagedQueryDao;
    }
}