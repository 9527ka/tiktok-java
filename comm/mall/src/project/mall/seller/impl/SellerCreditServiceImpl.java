package project.mall.seller.impl;

import kernel.exception.BusinessException;
import org.hibernate.Session;
import org.hibernate.query.NativeQuery;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;
import org.springframework.transaction.annotation.Transactional;
import project.mall.seller.SellerCreditService;
import project.mall.seller.SellerService;
import project.mall.seller.model.Seller;
import project.mall.seller.model.SellerCredit;

import java.util.Date;

public class SellerCreditServiceImpl extends HibernateDaoSupport implements SellerCreditService {

    private SellerService sellerService;

    public void setSellerService(SellerService sellerService) {
        this.sellerService = sellerService;
    }

    @Override
    public SellerCredit addCredit(String sellerId, int accScore, Integer eventType, String eventKey, String reason) {
        if (sellerId == null
                || sellerId.trim().isEmpty()) {
            throw new BusinessException("缺失必要参数");
        }

        Seller seller = sellerService.getSeller(sellerId);
        if (seller == null) {
            throw new BusinessException("店铺:" + sellerId + " 记录不存在");
        }

        Integer oriCredit = seller.getCreditScore();
        if (oriCredit == null) {
            oriCredit = 0;
        }
//        if (oriCredit + accScore < 0 || oriCredit + accScore > 200) {
//            throw new BusinessException("店铺信誉分最低 0 分最高 200 分");
//        }

        SellerCredit creditEntity = new SellerCredit();
        creditEntity.setId(null);
        creditEntity.setSellerId(sellerId);
        // 可能是正值，可能是负值
        creditEntity.setAccScore(accScore);
        creditEntity.setReason(reason);
        creditEntity.setOperationTime(new Date());
        if (eventType == null) {
            creditEntity.setEventType(0);
        } else {
            creditEntity.setEventType(eventType);
        }
        if (eventKey == null || eventKey.trim().isEmpty()) {
            creditEntity.setEventKey("0");
        } else {
            creditEntity.setEventKey(eventKey);
        }
        creditEntity.setTimeRegion("0");

        // 产生新增的信誉分变更记录
        getHibernateTemplate().save(creditEntity);

        // 更新店铺的信誉分
        Session currentSession = getHibernateTemplate().getSessionFactory().getCurrentSession();
        String sql = " update T_MALL_SELLER set CREDIT_SCORE= :creditScore where uuid= :id ";
        NativeQuery query = currentSession.createSQLQuery(sql);
        query.setParameter("creditScore", oriCredit + accScore);
        query.setParameter("id", seller.getId());
        query.executeUpdate();

        return creditEntity;
    }


}
