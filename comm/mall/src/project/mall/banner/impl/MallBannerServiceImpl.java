package project.mall.banner.impl;

import kernel.util.PageInfo;
import kernel.util.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;
import project.mall.banner.MallBannerService;
import project.mall.banner.model.MallBanner;

import java.util.List;

public class MallBannerServiceImpl extends HibernateDaoSupport implements MallBannerService {
    @Override
    public List<MallBanner> getBannerList(String type, String imgType, PageInfo pageInfo) {
        DetachedCriteria criteria = DetachedCriteria.forClass(MallBanner.class);
        criteria.add(Restrictions.eq("type", type));
        if(!StringUtils.isEmptyString(imgType)){
            criteria.add(Restrictions.eq("imgType", Integer.valueOf(imgType)));
        }
        criteria.addOrder(Order.asc("sort"));
        criteria.addOrder(Order.desc("createTime"));
        return  (List<MallBanner>)this.getHibernateTemplate().findByCriteria(criteria, pageInfo.getPageNum()-1, pageInfo.getPageSize());
    }
}
