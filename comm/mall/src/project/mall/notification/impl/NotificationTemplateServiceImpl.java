package project.mall.notification.impl;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Property;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;
import project.mall.notification.NotificationTemplateService;
import project.mall.notification.model.NotificationTemplate;

import java.util.List;

public class NotificationTemplateServiceImpl extends HibernateDaoSupport implements NotificationTemplateService {

//    private JdbcTemplate jdbcTemplate;
//
//    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
//        this.jdbcTemplate = jdbcTemplate;
//    }

    public NotificationTemplate save(NotificationTemplate entity) {
        if (entity.getId() != null && !entity.getId().toString().trim().isEmpty()) {
            throw new RuntimeException("新增记录不能有主键值");
        }
        entity.setId(null);

        getHibernateTemplate().save(entity);
        return entity;
    }

    public NotificationTemplate update(NotificationTemplate entity) {
        if (entity.getId() == null || entity.getId().toString().trim().isEmpty()) {
            throw new RuntimeException("没有主键值");
        }

        getHibernateTemplate().update(entity);
        return entity;
    }

    public NotificationTemplate getTemplateByBizType(String bizType, String language) {
        DetachedCriteria query = DetachedCriteria.forClass(NotificationTemplate.class);
        query.add(Property.forName("bizType").eq(bizType.trim()));
        query.add(Property.forName("language").eq(language.trim()));

        List<NotificationTemplate> list = (List<NotificationTemplate>)getHibernateTemplate().findByCriteria(query, 0, 1);
        if (list == null || list.isEmpty()) {
            return null;
        }

        return list.get(0);
    }

    public List<NotificationTemplate> listNotificationTemplate(int type, int module, String language, int status) {
        DetachedCriteria query = DetachedCriteria.forClass(NotificationTemplate.class);
        if (type > 0) {
            query.add(Property.forName("type").eq(type));
        }
        if (module > 0) {
            query.add(Property.forName("module").eq(module));
        }
        if (language != null && !language.trim().isEmpty()) {
            query.add(Property.forName("language").eq(language.trim()));
        }
        if (status > 0) {
            query.add(Property.forName("status").eq(status));
        }

        return (List<NotificationTemplate>)getHibernateTemplate().findByCriteria(query);
    }

    public List<NotificationTemplate> listMessageTemplateByBiz(String bizType, String language, int status) {
        DetachedCriteria query = DetachedCriteria.forClass(NotificationTemplate.class);
        query.add(Property.forName("bizType").eq(bizType.trim()));
        if (language != null && !language.trim().isEmpty()) {
            query.add(Property.forName("language").eq(language.trim()));
        }
        //query.add(Property.forName("type").eq(3));
        if (status > 0) {
            query.add(Property.forName("status").eq(status));
        }

        return (List<NotificationTemplate>)getHibernateTemplate().findByCriteria(query);
    }

    public NotificationTemplate getById(String id) {
        if (id == null || id.trim().isEmpty()) {
            return null;
        }

        return getHibernateTemplate().get(NotificationTemplate.class, id);
    }

    public NotificationTemplate getByTemplateCode(String templateCode) {
        if (templateCode == null || templateCode.trim().isEmpty()) {
            return null;
        }

        DetachedCriteria query = DetachedCriteria.forClass(NotificationTemplate.class);
        query.add(Property.forName("templateCode").eq(templateCode.trim()));

        List list = getHibernateTemplate().findByCriteria(query);
        if (list == null || list.isEmpty()) {
            return null;
        }

        return (NotificationTemplate) list.get(0);
    }

}
