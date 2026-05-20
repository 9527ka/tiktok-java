package project.mall.orders.impl;

import kernel.exception.BusinessException;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Property;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;
import org.springframework.transaction.annotation.Transactional;
import project.mall.orders.OrderTrackingService;
import project.mall.orders.model.OrderTracking;

import java.util.Date;
import java.util.List;

public class OrderTrackingServiceImpl extends HibernateDaoSupport implements OrderTrackingService {

    @Override
    @Transactional
    public OrderTracking add(OrderTracking tracking) {
        if (tracking == null) {
            throw new BusinessException("参数不能为空");
        }
        if (tracking.getOrderId() == null || tracking.getOrderId().isEmpty()) {
            throw new BusinessException("orderId 不能为空");
        }
        Date now = new Date();
        if (tracking.getEventTime() == null) {
            tracking.setEventTime(now);
        }
        tracking.setCreateTime(now);
        tracking.setUpdateTime(now);
        getHibernateTemplate().save(tracking);
        return tracking;
    }

    @Override
    @Transactional
    public void update(OrderTracking tracking) {
        if (tracking == null || tracking.getId() == null) {
            throw new BusinessException("id 不能为空");
        }
        OrderTracking exists = getHibernateTemplate().get(OrderTracking.class, tracking.getId());
        if (exists == null) {
            throw new BusinessException("记录不存在");
        }
        if (tracking.getEventTime() != null) {
            exists.setEventTime(tracking.getEventTime());
        }
        if (tracking.getLocation() != null) {
            exists.setLocation(tracking.getLocation());
        }
        if (tracking.getStatus() != null) {
            exists.setStatus(tracking.getStatus());
        }
        if (tracking.getDescription() != null) {
            exists.setDescription(tracking.getDescription());
        }
        exists.setUpdateTime(new Date());
        getHibernateTemplate().update(exists);
    }

    @Override
    @Transactional
    public void delete(String id) {
        if (id == null || id.isEmpty()) {
            throw new BusinessException("id 不能为空");
        }
        OrderTracking exists = getHibernateTemplate().get(OrderTracking.class, id);
        if (exists == null) {
            return;
        }
        getHibernateTemplate().delete(exists);
    }

    @Override
    public OrderTracking getById(String id) {
        if (id == null || id.isEmpty()) {
            return null;
        }
        return getHibernateTemplate().get(OrderTracking.class, id);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<OrderTracking> listByOrderId(String orderId) {
        DetachedCriteria query = DetachedCriteria.forClass(OrderTracking.class);
        query.add(Property.forName("orderId").eq(orderId));
        query.addOrder(Order.desc("eventTime"));
        return (List<OrderTracking>) getHibernateTemplate().findByCriteria(query);
    }
}
