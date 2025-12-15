package project.mall.seller.impl;

import cn.hutool.core.collection.CollectionUtil;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;
import project.mall.seller.MallLevelService;
import project.mall.seller.model.MallLevel;

import java.util.List;

public class MallLevelServiceImpl extends HibernateDaoSupport implements MallLevelService {

    public List<MallLevel> listLevel() {
        DetachedCriteria query = DetachedCriteria.forClass(MallLevel.class);
        query.addOrder(Order.asc("id"));
        // query.add(Property.forName("status").eq(1));

        return (List<MallLevel>) getHibernateTemplate().findByCriteria(query);
    }

    public MallLevel findByLevel(String level){
        DetachedCriteria criteria = DetachedCriteria.forClass(MallLevel.class);
        criteria.add(Restrictions.eq("level", level));
        List<MallLevel> list = (List<MallLevel>) this.getHibernateTemplate().findByCriteria(criteria);
        if (CollectionUtil.isNotEmpty(list)) {
            return list.get(0);
        }
        return null;
    }

}
