package mall;

import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.jetbrains.annotations.NotNull;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

import java.util.List;

public abstract class BaseDaoSupport extends HibernateDaoSupport {
    public <T> T getEntity(Class<T> entityClass, String id) {
        return getTemplate().get(entityClass, id);
    }

    /**
     * 根据ID获取对象列表
     * @param criteria 查询条件
     * @param clazz 实体类
     * @return  实体对象
     */
    @SuppressWarnings("unchecked")
    public <T> List<T> findBy(DetachedCriteria criteria, Class<T> clazz) {
        return (List<T>)getTemplate().findByCriteria(criteria);
    }

    @NotNull
    protected HibernateTemplate getTemplate() {
        HibernateTemplate hibernateTemplate = getHibernateTemplate();
        assert hibernateTemplate != null;
        return hibernateTemplate;
    }

    /**
     * 保存实体对象
     * @param entity 实体对象
     */
    protected void save(Object entity) {
        getTemplate().save(entity);
    }
}
