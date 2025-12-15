//package project.mall.activity.service.impl.lottery;
//
//import kernel.exception.BusinessException;
//import kernel.util.DateUtils;
//import kernel.util.StringUtils;
//import kernel.web.Page;
//import org.apache.commons.collections.CollectionUtils;
//import org.hibernate.criterion.DetachedCriteria;
//import org.hibernate.criterion.Order;
//import org.hibernate.criterion.Projections;
//import org.hibernate.criterion.Property;
//import org.springframework.beans.BeanUtils;
//import org.springframework.orm.hibernate5.support.HibernateDaoSupport;
//import project.mall.activity.service.lottery.LotteryInfoPrizeService;
//import project.mall.activity.service.lottery.LotteryPrizeService;
//import project.mall.activity.service.lottery.LotteryService;
//import project.mall.activity.dto.lottery.LotteryDTO;
//import project.mall.activity.model.lottery.Lottery;
//import project.mall.activity.model.lottery.LotteryInfoPrize;
//import project.mall.activity.model.lottery.LotteryPrize;
//
//import java.util.Date;
//import java.util.List;
//import java.util.Objects;
//import java.util.stream.Collectors;
//
//public class LotteryServiceImpl extends HibernateDaoSupport implements LotteryService {
//
//    private LotteryInfoPrizeService lotteryInfoPrizeService;
//
//    private LotteryPrizeService lotteryPrizeService;
//
//    @Override
//    public void add(LotteryDTO lottery) {
//        Lottery entity = new Lottery();
//        BeanUtils.copyProperties(lottery, entity);
//        entity.setStartTime(DateUtils.toDate(lottery.getStartTime()));
//        entity.setEndTime(DateUtils.toDate(lottery.getEndTime()));
//        entity.setCreateTime(new Date());
//        entity.setUpdateTime(new Date());
//        entity.setState(1);
//        this.getHibernateTemplate().save(entity);
//
//        List<String> prizeIds = lottery.getPrizeIds();
//
//        if (CollectionUtils.isNotEmpty(prizeIds)) {
//            for (String prizeId : prizeIds) {
//                LotteryInfoPrize lotteryInfoPrize = new LotteryInfoPrize();
//                lotteryInfoPrize.setLotteryId(entity.getId().toString());
//                lotteryInfoPrize.setPrizeId(prizeId);
//                lotteryInfoPrizeService.add(lotteryInfoPrize);
//            }
//        }
//    }
//
//    @Override
//    public void delete(String id) {
//
//        Lottery lottery = this.getHibernateTemplate().get(Lottery.class, id);
//
//        if (Objects.isNull(lottery)) {
//            throw new BusinessException("活动记录不存在");
//        }
//        getHibernateTemplate().delete(lottery);
//    }
//
//    @Override
//    public void update(LotteryDTO lottery) {
//
//        if (StringUtils.isEmptyString(lottery.getId())) {
//            throw new BusinessException("活动ID不能为空");
//        }
//
//        Lottery entity = this.getHibernateTemplate().get(Lottery.class, lottery.getId());
//
//        BeanUtils.copyProperties(lottery, entity);
//
//        if (StringUtils.isNotEmpty(lottery.getStartTime())) {
//            entity.setStartTime(DateUtils.toDate(lottery.getStartTime()));
//        }
//
//        if (StringUtils.isNotEmpty(lottery.getEndTime())) {
//            entity.setEndTime(DateUtils.toDate(lottery.getEndTime()));
//        }
//
//        entity.setUpdateTime(new Date());
//
//        this.getHibernateTemplate().save(entity);
//
//        List<String> prizeIds = lottery.getPrizeIds();
//
//        if (CollectionUtils.isNotEmpty(prizeIds)) {
//            List<LotteryInfoPrize> lotteryInfoPrizes = lotteryInfoPrizeService.listByLotteryId(lottery.getId());
//            getHibernateTemplate().deleteAll(lotteryInfoPrizes);
//            for (String prizeId : prizeIds) {
//                LotteryInfoPrize lotteryInfoPrize = new LotteryInfoPrize();
//                lotteryInfoPrize.setLotteryId(lottery.getId());
//                lotteryInfoPrize.setPrizeId(prizeId);
//                lotteryInfoPrizeService.add(lotteryInfoPrize);
//            }
//        }
//    }
//
//    @Override
//    public void updateById(Lottery lottery) {
//        getHibernateTemplate().update(lottery);
//    }
//
//    @Override
//    public LotteryDTO detail(String id) {
//
//        LotteryDTO lotteryDTO = new LotteryDTO();
//        Lottery lottery = this.getHibernateTemplate().get(Lottery.class, id);
//
//        BeanUtils.copyProperties(lottery, lotteryDTO);
//        lotteryDTO.setId(lottery.getId().toString());
//        lotteryDTO.setStartTime(DateUtils.toString(lottery.getStartTime()));
//        lotteryDTO.setEndTime(DateUtils.toString(lottery.getEndTime()));
//        lotteryDTO.setCreateTime(DateUtils.toString(lottery.getCreateTime()));
//        lotteryDTO.setUpdateTime(DateUtils.toString(lottery.getUpdateTime()));
//
//        List<LotteryInfoPrize> lotteryInfoPrizes = lotteryInfoPrizeService.listByLotteryId(id);
//
//        if (CollectionUtils.isNotEmpty(lotteryInfoPrizes)) {
//            List<String> prizeIds = lotteryInfoPrizes.stream().map(LotteryInfoPrize::getPrizeId).collect(Collectors.toList());
//            List<LotteryPrize> lotteryPrizes = lotteryPrizeService.listByIds(prizeIds);
//        }
//        return lotteryDTO;
//    }
//
//    @Override
//    public LotteryDTO getByLink(String link) {
//
//        LotteryDTO lotteryDTO = new LotteryDTO();
//        DetachedCriteria query = DetachedCriteria.forClass(Lottery.class);
//        if (StringUtils.isNotEmpty(link)) {
//            query.add(Property.forName("link").eq(link));
//            List<Lottery> lotteries = (List<Lottery>) getHibernateTemplate().findByCriteria(query);
//            Lottery lottery = lotteries.get(0);
//            BeanUtils.copyProperties(lottery, lotteryDTO);
//            lotteryDTO.setId(lottery.getId().toString());
//            lotteryDTO.setStartTime(DateUtils.toString(lottery.getStartTime()));
//            lotteryDTO.setEndTime(DateUtils.toString(lottery.getEndTime()));
//            lotteryDTO.setCreateTime(DateUtils.toString(lottery.getCreateTime()));
//            lotteryDTO.setUpdateTime(DateUtils.toString(lottery.getUpdateTime()));
//            List<LotteryInfoPrize> lotteryInfoPrizes = lotteryInfoPrizeService.listByLotteryId(lottery.getId().toString());
//            if (CollectionUtils.isNotEmpty(lotteryInfoPrizes)) {
//                List<String> prizeIds = lotteryInfoPrizes.stream().map(LotteryInfoPrize::getPrizeId).collect(Collectors.toList());
//                List<LotteryPrize> lotteryPrizes = lotteryPrizeService.listByIds(prizeIds);
//            }
//        }
//        return lotteryDTO;
//    }
//
//    @Override
//    public Lottery findById(String id) {
//        return getHibernateTemplate().get(Lottery.class, id);
//    }
//
//    @Override
//    public Page paged(String name, Integer state, String startTime, String endTime, int pageNum, int pageSize) {
//
//        Page page = new Page();
//
//        DetachedCriteria query = DetachedCriteria.forClass(Lottery.class);
//        if (Objects.nonNull(state)) {
//            query.add(Property.forName("state").eq(state));
//        }
//
//        if (StringUtils.isNotEmpty(name)) {
//            query.add(Property.forName("name").like("%" + name + "%"));
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
//        page.setElements(resultList);
//        page.setThisPageNumber(pageNum);
//        page.setTotalElements(totalCount.intValue());
//        page.setPageSize(pageSize);
//        return page;
//    }
//
//    public void setLotteryPrizeService(LotteryPrizeService lotteryPrizeService) {
//        this.lotteryPrizeService = lotteryPrizeService;
//    }
//
//    public void setLotteryInfoPrizeService(LotteryInfoPrizeService lotteryInfoPrizeService) {
//        this.lotteryInfoPrizeService = lotteryInfoPrizeService;
//    }
//}
