package project.mall.subscribe.impl;

import kernel.exception.BusinessException;
import org.hibernate.Criteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;
import project.mall.subscribe.SubscribeService;
import project.mall.subscribe.model.Subscribe;

import java.util.Date;

public class SubscribeServiceImpl extends HibernateDaoSupport implements SubscribeService {

    @Override
    public void saveSubscribe(String email) {
        Criteria criteria = getHibernateTemplate().getSessionFactory().getCurrentSession().createCriteria(Subscribe.class);
        criteria.add(Restrictions.eq("email", email));
        criteria.setProjection(Projections.rowCount());
        Integer totalCount = ((Long) criteria.uniqueResult()).intValue();

        if (totalCount == null) {
            totalCount = 0;
        }
        if (totalCount > 0) {
            throw new BusinessException("该邮箱已被订阅");
        }

        Subscribe subscribe = new Subscribe();
        subscribe.setEmail(email);
        subscribe.setCreateTime(new Date());
        getHibernateTemplate().save(subscribe);

    }


}
