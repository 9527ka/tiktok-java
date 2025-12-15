package project.mall.activity.service.impl;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Property;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;
import project.mall.activity.model.ActivityPrizePool;
import project.mall.activity.model.ActivityTemplate;
import project.mall.activity.service.ActivityTemplateService;

import java.util.List;

public class ActivityTemplateServiceImpl extends HibernateDaoSupport implements ActivityTemplateService {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    @Override
    public List<ActivityTemplate> listAllValidActivityType() {
        DetachedCriteria query = DetachedCriteria.forClass(ActivityTemplate.class);
        query.add(Property.forName("status").eq(1));
        query.add(Property.forName("deleted").eq(0));
        query.addOrder(Order.desc("createTime"));

        List<ActivityTemplate> resultList = (List<ActivityTemplate>)getHibernateTemplate().findByCriteria(query);

        return resultList;
    }

    @Override
    public ActivityTemplate getById(String id) {
        return this.getHibernateTemplate().get(ActivityTemplate.class, id);
    }


}
