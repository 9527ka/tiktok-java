package project.party.internal;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;
import project.party.UserMetricsService;
import project.mall.seller.model.FocusSeller;
import project.mall.utils.MallPageInfo;
import project.mall.utils.MallPageInfoUtil;
import project.party.model.UserMetrics;

import java.util.Date;
import java.util.List;

public class UserMetricsServiceImpl extends HibernateDaoSupport implements UserMetricsService {

    /**
     * 注意：需要防止并发冲突问题，最好调用方校验下设置的指标值是否一致.
     * 最恰当的使用方式是通过本方法尝试初始化创建一条 UserMetrics，然后再使用本方法的返回值设置具体业务的指标数据执行更新。
     *
     * @param entity
     * @return
     */
    @Override
    public UserMetrics save(UserMetrics entity) {
        if (entity == null || StrUtil.isBlank(entity.getPartyId())) {
            return null;
        }

        Date now = new Date();
        if (entity.getCreateTime() == null) {
            entity.setCreateTime(now);
        }
        if (entity.getUpdateTime() == null) {
            entity.setUpdateTime(now);
        }

        try {
            getHibernateTemplate().save(entity);
//            getHibernateTemplate().flush();
            return entity;
        } catch (Exception e) {
//            getHibernateTemplate().clear();
            UserMetrics exist = getByPartyId(entity.getPartyId());
            if (exist != null) {
                return exist;
            }

            throw e;
        }
    }

    @Override
    public UserMetrics getByPartyId(String partyId) {
        DetachedCriteria query = DetachedCriteria.forClass(UserMetrics.class);
        query.add(Restrictions.eq("partyId", partyId));
        query.add(Restrictions.eq("status", 1));

        List<UserMetrics> list = (List<UserMetrics>) this.getHibernateTemplate().findByCriteria(query);
        if (CollectionUtil.isEmpty(list)) {
            return null;
        }

        return list.get(0);
    }

    @Override
    public void update(UserMetrics entity) {
        if (entity == null || entity.getId() == null) {
            return;
        }

        entity.setUpdateTime(new Date());
        getHibernateTemplate().update(entity);
    }

}
