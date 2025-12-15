//package project.mall.activity.service.impl.lottery;
//
//import kernel.exception.BusinessException;
//import kernel.util.StringUtils;
//import kernel.web.Page;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.collections.CollectionUtils;
//import org.hibernate.criterion.DetachedCriteria;
//import org.hibernate.criterion.Order;
//import org.hibernate.criterion.Projections;
//import org.hibernate.criterion.Property;
//import org.springframework.orm.hibernate5.support.HibernateDaoSupport;
//import org.springframework.transaction.annotation.Transactional;
//import project.mall.LanguageEnum;
//import project.mall.activity.model.lottery.ActivityUserPoints;
//import project.mall.activity.service.ActivityUserPointsService;
//import project.mall.activity.service.lottery.LotteryInfoPrizeService;
//import project.mall.activity.service.lottery.LotteryPrizeService;
//import project.mall.activity.service.lottery.LotteryRecordService;
//import project.mall.activity.service.lottery.LotteryService;
//import project.mall.activity.model.lottery.Lottery;
//import project.mall.activity.model.lottery.LotteryInfoPrize;
//import project.mall.activity.model.lottery.LotteryPrize;
//import project.mall.activity.model.lottery.LotteryRecord;
//import project.redis.RedisHandler;
//
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//import java.util.Map;
//import java.util.Objects;
//import java.util.stream.Collectors;
//
//@Slf4j
//public class LotteryPrizeServiceImpl extends HibernateDaoSupport implements LotteryPrizeService {
//
//    private LotteryInfoPrizeService lotteryInfoPrizeService;
//
//    private LotteryRecordService lotteryRecordService;
//
//    private LotteryService lotteryService;
//
//    private ActivityUserPointsService activityUserPointsService;
//
//    private RedisHandler redisHandler;
//
//
//    @Override
//    public void add(LotteryPrize lotteryPrize, String lang) {
//
//        lotteryPrize.setCreateTime(new Date());
//        lotteryPrize.setUpdateTime(new Date());
//
//        if (lang.equals(LanguageEnum.CN.getLang())) {
//            lotteryPrize.setPrizeNameCn(lotteryPrize.getPrizeNameCn());
//        } else {
//            lotteryPrize.setPrizeNameEn(lotteryPrize.getPrizeNameEn());
//        }
//        getHibernateTemplate().save(lotteryPrize);
//    }
//
//    @Override
//    public void delete(String id) {
//
//        LotteryPrize lotteryPrize = this.getHibernateTemplate().get(LotteryPrize.class, id);
//
//        if (Objects.isNull(lotteryPrize)) {
//            throw new BusinessException("记录不存在");
//        }
//
//        getHibernateTemplate().delete(lotteryPrize);
//    }
//
//    @Override
//    public void update(LotteryPrize lotteryPrize, String lang) {
//
//        lotteryPrize.setUpdateTime(new Date());
//
//        if (lang.equals(LanguageEnum.CN.getLang())) {
//            lotteryPrize.setPrizeNameCn(lotteryPrize.getPrizeNameCn());
//        } else {
//            lotteryPrize.setPrizeNameEn(lotteryPrize.getPrizeNameEn());
//        }
//
//        this.getHibernateTemplate().update(lotteryPrize);
//    }
//
//    @Override
//    public LotteryPrize detail(String id) {
//        return this.getHibernateTemplate().get(LotteryPrize.class, id);
//    }
//
//    @Override
//    public List<LotteryPrize> listAll() {
//        return getHibernateTemplate().loadAll(LotteryPrize.class);
//    }
//
//    @Override
//    public Page paged(String prizeName, Integer prizeType, String startTime, String endTime, int pageNum, int pageSize) {
//
//        Page page = new Page();
//
//        DetachedCriteria query = DetachedCriteria.forClass(LotteryPrize.class);
//        if (Objects.nonNull(prizeType)) {
//            query.add(Property.forName("prizeType").eq(prizeType));
//        }
//
//        if (StringUtils.isNotEmpty(prizeName)) {
//            query.add(Property.forName("prizeNameEn").like("%" + prizeName + "%"));
//        }
//
//        if (StringUtils.isNotEmpty(startTime) && StringUtils.isNotEmpty(endTime)) {
//            query.add(Property.forName("createTime").gt(startTime));
//            query.add(Property.forName("createTime").lt(endTime));
//        }
//
//        query.addOrder(Order.desc("createTime"));
//
//        // 查询总条数
//        Long totalCount = (Long) query.setProjection(Projections.rowCount()).getExecutableCriteria(this.getHibernateTemplate().getSessionFactory().getCurrentSession()).uniqueResult();
//        query.setProjection(null);
//
//        List<?> resultList = getHibernateTemplate().findByCriteria(query, (pageNum - 1) * pageSize, pageSize);
//
//        page.setThisPageNumber(pageNum);
//        page.setTotalElements(totalCount.intValue());
//        page.setElements(resultList);
//        page.setPageSize(pageSize);
//        return page;
//
//    }
//
//    @Override
//    public List<LotteryPrize> listByIds(List<String> ids) {
//
//        DetachedCriteria query = DetachedCriteria.forClass(LotteryPrize.class);
//
//        query.add(Property.forName("id").in(ids));
//
//        List<LotteryPrize> results = (List<LotteryPrize>) getHibernateTemplate().findByCriteria(query);
//        return results;
//    }
//
//    @Override
//    public List<LotteryPrize> listLotteryId(String lotteryId) {
//
//        List<LotteryPrize> lotteryPrizes = new ArrayList<>();
//
//        List<LotteryInfoPrize> lotteryInfoPrizes = lotteryInfoPrizeService.listByLotteryId(lotteryId);
//        if (CollectionUtils.isNotEmpty(lotteryInfoPrizes)) {
//            List<String> prizeIds = lotteryInfoPrizes.stream().map(value -> value.getPrizeId()).collect(Collectors.toList());
//            lotteryPrizes = listByIds(prizeIds);
//        }
//        return lotteryPrizes;
//    }
//
//    public void setLotteryInfoPrizeService(LotteryInfoPrizeService lotteryInfoPrizeService) {
//        this.lotteryInfoPrizeService = lotteryInfoPrizeService;
//    }
//
//    /**
//     * 生成奖项
//     *
//     * @return
//     */
//    @Transactional
//    @Override
//    public LotteryPrize updateDraw(String lotteryId, String lang, String partyId, String partyName, String recomName, String sellerName) {
//
//        Lottery lottery = lotteryService.findById(lotteryId);
//
//        List<LotteryPrize> lotteryPrizes = null;
//
//        LotteryPrize lotteryPrize = null;
//
//
//        //初始化记载所有奖品信息
//        List<LotteryInfoPrize> lotteryInfoPrizes = lotteryInfoPrizeService.listAll();
//        if (CollectionUtils.isNotEmpty(lotteryInfoPrizes)) {
//            Map<String, List<String>> infoMaps = lotteryInfoPrizes.stream().collect(Collectors.groupingBy(LotteryInfoPrize::getLotteryId,
//                    Collectors.mapping(LotteryInfoPrize::getPrizeId, Collectors.toList())));
//            List<String> prizeIds = infoMaps.get(lotteryId);
//            lotteryPrizes = listByIds(prizeIds);
//        }
//
//
//        if (CollectionUtils.isNotEmpty(lotteryPrizes)) {
//
//            long result = randomNum(1, 100000000);
//
//            int line = 0;
//            int temp = 0;
//
//            for (int i = 0; i < lotteryPrizes.size(); i++) {
//                LotteryPrize tempPrice = lotteryPrizes.get(i);
//
//                double d = (tempPrice.getRate() * 100000000);
//                int c = (int) d;
//                temp = temp + c;
//                line = 100000000 - temp;
//                if (c != 0) {
//                    if (result > line && result <= (line + c)) {
//                        lotteryPrize = tempPrice;
//                        break;
//                    }
//                }
//            }
//
//            if (Objects.nonNull(lotteryPrize)) {
//
//                LotteryRecord lotteryRecord = new LotteryRecord();
//                lotteryRecord.setPrizeId(lotteryPrize.getId().toString());
//                lotteryRecord.setPartyId(partyId);
//                lotteryRecord.setPrizeAmount(lotteryPrize.getPrizeAmount());
//                lotteryRecord.setPrizeType(lotteryPrize.getPrizeType());
//                lotteryRecord.setLotteryTime(new Date());
//                lotteryRecord.setActivityId(lotteryId);
//                lotteryRecord.setLotteryName(lottery.getName());
//                lotteryRecord.setPartyName(partyName);
//                lotteryRecord.setRecommendName(recomName);
//                lotteryRecord.setSellerName(sellerName);
//                lotteryRecordService.add(lotteryRecord, lang);
//            }
//            ActivityUserPoints userPoints = activityUserPointsService.getByActivityId(lotteryId, partyId);
//            double amount = userPoints.getPoints() - lottery.getMinPoints();
//            userPoints.setPoints(amount);
//            activityUserPointsService.update(userPoints);
//        }
//        return lotteryPrize;
//    }
//
//    // 获取2个值之间的随机数
//    private static long randomNum(int smin, int smax) {
//        int range = smax - smin;
//        double rand = Math.random();
//        return (smin + Math.round(rand * range));
//    }
//
//    public void setRedisHandler(RedisHandler redisHandler) {
//        this.redisHandler = redisHandler;
//    }
//
//    public void setLotteryRecordService(LotteryRecordService lotteryRecordService) {
//        this.lotteryRecordService = lotteryRecordService;
//    }
//
//    public void setLotteryService(LotteryService lotteryService) {
//        this.lotteryService = lotteryService;
//    }
//
//    public void setActivityUserPointsService(ActivityUserPointsService activityUserPointsService) {
//        this.activityUserPointsService = activityUserPointsService;
//    }
//}
