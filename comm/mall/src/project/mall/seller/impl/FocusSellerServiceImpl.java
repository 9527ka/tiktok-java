package project.mall.seller.impl;

import org.hibernate.Criteria;
import org.hibernate.FlushMode;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;
import project.log.MoneyLogService;
import project.mall.seller.FocusSellerService;
import project.mall.seller.SellerService;
import project.mall.seller.model.FocusSeller;
import project.mall.seller.model.Seller;
import project.mall.utils.MallPageInfo;
import project.mall.utils.MallPageInfoUtil;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FocusSellerServiceImpl extends HibernateDaoSupport implements FocusSellerService {

    @Resource
    private SellerService sellerService;

    @Override
    public MallPageInfo listFocusSeller(int pageNum, int pageSize, String partyId) {
        DetachedCriteria query = DetachedCriteria.forClass(FocusSeller.class);
        query.add(Restrictions.eq("partyId", partyId));
        query.addOrder(Order.desc("createTime"));

        // 查询总条数
        Long totalCount = (Long) query.setProjection(Projections.rowCount()).getExecutableCriteria(this.getHibernateTemplate().getSessionFactory().getCurrentSession()).uniqueResult();
        query.setProjection(null);

        MallPageInfo mallPageInfo = MallPageInfoUtil.getMallPage(pageSize, pageNum, totalCount, getHibernateTemplate().findByCriteria(query, (pageNum - 1) * pageSize, pageSize));

        return mallPageInfo;
    }

    @Override
    public Integer queryIsFocus(String sellerId, String partyId) {
        DetachedCriteria query = DetachedCriteria.forClass(FocusSeller.class);
        query.add(Restrictions.eq("partyId", partyId));
        query.add(Restrictions.eq("sellerId", sellerId));
        List<FocusSeller> list = (List<FocusSeller>) getHibernateTemplate().findByCriteria(query);
        if (list != null && !list.isEmpty()) {
            return 1;
        }
        return 0;

    }

    @Override
    public Integer getFocusSellerCount(String partyId) {
        Criteria criteria = getHibernateTemplate().getSessionFactory().getCurrentSession().createCriteria(FocusSeller.class);
        criteria.add(Restrictions.eq("partyId", partyId));
        criteria.setProjection(Projections.rowCount());
        Integer allConst = ((Long) criteria.uniqueResult()).intValue();
        return allConst;

    }

    @Override
    public Integer getFocusCount(String sellerId) {

//        return this.jdbcTemplate.queryForObject(
//                "SELECT " +
//                        " COUNT( * )  " +
//                        "FROM " +
//                        " T_MALL_FOCUS_SELLER  " +
//                        "WHERE " +
//                        " SELLER_ID = '" + sellerId + "'", Integer.class);
        Criteria criteria = getHibernateTemplate().getSessionFactory().getCurrentSession().createCriteria(FocusSeller.class);
        criteria.add(Restrictions.eq("sellerId", sellerId));
        criteria.setProjection(Projections.rowCount());
        Integer allConst = ((Long) criteria.uniqueResult()).intValue();
        return allConst;

    }

    public Map<String, Integer> getFocusCounts(List<String> sellerIds) {
        Map<String, Integer> focusCounts = new HashMap<>();
        Criteria criteria = getHibernateTemplate().getSessionFactory().getCurrentSession().createCriteria(FocusSeller.class);
        criteria.add(Restrictions.in("sellerId", sellerIds));
        criteria.setProjection(Projections.projectionList()
                .add(Projections.groupProperty("sellerId"))
                .add(Projections.rowCount(), "count"));
        List<Object[]> results = criteria.list();
        for (Object[] result : results) {
            String sellerId = (String) result[0];
            Long count = (Long) result[1];
            focusCounts.put(sellerId, count.intValue());
        }
        return focusCounts;
    }

    @Override
    public void addFocus(String partyId, Seller seller) {
        FocusSeller focusSeller = new FocusSeller();
        focusSeller.setSellerId(seller.getId().toString());
        focusSeller.setPartyId(partyId);
        focusSeller.setCreateTime(new Date());
        getHibernateTemplate().save(focusSeller);
        seller.setReals(seller.getReals() == null ? 1 : seller.getReals() + 1);
        getHibernateTemplate().update(seller);
//        getHibernateTemplate().flush();
    }

    @Override
    public void deleteFocus(String sellerId, String partyId) {
        try {
            DetachedCriteria query = DetachedCriteria.forClass(FocusSeller.class);
            query.add(Restrictions.eq("sellerId", sellerId));
            query.add(Restrictions.eq("partyId", partyId));
            this.getHibernateTemplate().getSessionFactory().getCurrentSession().setHibernateFlushMode(FlushMode.AUTO);
            List<FocusSeller> list = (List<FocusSeller>) getHibernateTemplate().findByCriteria(query);
            if (list == null || list.isEmpty()) {
                return;
            }
            for (FocusSeller focusSeller : list) {
                getHibernateTemplate().delete(focusSeller);
            }
            Seller seller = sellerService.getSeller(sellerId);
            if (seller == null || seller.getReals() <= 0) {
                return;
            }
            seller.setReals(seller.getReals() - 1);
            sellerService.updateSeller(seller);
        }
        finally {
//            getHibernateTemplate().flush();
        }
    }

    @Override
    public void deleteAllFocus(String sellerId) {
        DetachedCriteria query = DetachedCriteria.forClass(FocusSeller.class);
        query.add(Restrictions.eq("sellerId", sellerId));
        this.getHibernateTemplate().getSessionFactory().getCurrentSession().setHibernateFlushMode(FlushMode.AUTO);
        List<FocusSeller> list = (List<FocusSeller>) getHibernateTemplate().findByCriteria(query);
        if (list == null || list.isEmpty()) {
            return;
        }
        for (FocusSeller focusSeller : list) {
            getHibernateTemplate().delete(focusSeller);
        }
    }

    public void setSellerService(SellerService sellerService) {
        this.sellerService = sellerService;
    }
}
