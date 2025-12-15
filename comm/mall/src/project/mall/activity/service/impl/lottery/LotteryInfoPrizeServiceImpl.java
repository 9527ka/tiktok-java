//package project.mall.activity.service.impl.lottery;
//
//import org.hibernate.criterion.DetachedCriteria;
//import org.hibernate.criterion.Property;
//import org.springframework.orm.hibernate5.support.HibernateDaoSupport;
//import project.mall.activity.service.lottery.LotteryInfoPrizeService;
//import project.mall.activity.model.lottery.LotteryInfoPrize;
//
//import java.util.Date;
//import java.util.List;
//
//public class LotteryInfoPrizeServiceImpl extends HibernateDaoSupport implements LotteryInfoPrizeService {
//
//    @Override
//    public void add(LotteryInfoPrize lotteryInfoPrize) {
//        lotteryInfoPrize.setCreateTime(new Date());
//        lotteryInfoPrize.setUpdateTime(new Date());
//        getHibernateTemplate().save(lotteryInfoPrize);
//    }
//
//    @Override
//    public List<LotteryInfoPrize> listByLotteryId(String lotteryId) {
//
//        DetachedCriteria query = DetachedCriteria.forClass(LotteryInfoPrize.class);
//        query.add(Property.forName("lotteryId").eq(lotteryId));
//        List<LotteryInfoPrize> results = (List<LotteryInfoPrize>) getHibernateTemplate().findByCriteria(query);
//
//        return results;
//    }
//
//    @Override
//    public List<LotteryInfoPrize> listAll() {
//        return getHibernateTemplate().loadAll(LotteryInfoPrize.class);
//    }
//}
