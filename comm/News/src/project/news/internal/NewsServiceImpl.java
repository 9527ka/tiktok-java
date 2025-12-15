package project.news.internal;

import kernel.web.Page;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Property;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;
import project.invest.expert.model.Expert;
import project.invest.project.model.Project;
import project.news.News;
import project.news.NewsService;

import java.io.Serializable;
import java.util.List;

public class NewsServiceImpl extends HibernateDaoSupport implements NewsService {
    @Override
    public News findById(Serializable id) {
        return this.getHibernateTemplate().get(News.class, id);    }

    @Override
    public News cacheById(Serializable id) {
        return null;
    }

    @Override
    public News getIndex(String language) {
        return null;
    }

    @Override
    public void save(News entity) {

    }

    @Override
    public void delete(String id) {

    }

    @Override
    public void update(News entity) {

    }

    @Override
    public Page cachePagedQuery(int pageNo, int pageSize, String language) {
        return null;
    }

    @Override
    public List<String> selectByLanguage(String language) {
        return null;
    }

    @Override
    public List<String> selectAnnouncements(String language) {
        List<String> results = (List<String>)this.getHibernateTemplate().
                find("SELECT content FROM Cms WHERE type = 0 and status = 0 and language = ?0", new Object[]{language});
        return results;
    }

    @Override
    public List<News> listNewsPage(String language, int pageNum, int pageSize) {
        DetachedCriteria query = DetachedCriteria.forClass(News.class);
        query.add( Property.forName("lang").eq(language) );
        query.add( Property.forName("status").eq(1) );
        query.addOrder(Order.asc("sort"));
        return (List<News>) getHibernateTemplate().findByCriteria(query,(pageNum-1)*pageSize,pageSize);
    }

    @Override
    public List<Expert> listExpertPage(String language, int pageNum, int pageSize) {
        DetachedCriteria query = DetachedCriteria.forClass(Expert.class);
        query.add( Property.forName("lang").eq(language) );
        query.add( Property.forName("status").eq(1) );
        query.addOrder(Order.asc("sort"));
        return (List<Expert>) getHibernateTemplate().findByCriteria(query,(pageNum-1)*pageSize,pageSize);
    }
}
