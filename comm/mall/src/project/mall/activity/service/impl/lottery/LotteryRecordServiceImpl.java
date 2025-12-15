package project.mall.activity.service.impl.lottery;

import kernel.util.DateUtils;
import kernel.web.Page;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;
import org.springframework.transaction.annotation.Transactional;
import project.Constants;
import project.mall.LanguageEnum;
import project.mall.activity.ActivityTypeEnum;
import project.mall.activity.dto.lottery.LotteryRecordDTO;
import project.mall.activity.dto.lottery.LotteryRecordSumDTO;
import project.mall.activity.model.ActivityPrize;
import project.mall.activity.model.lottery.LotteryReceive;
import project.mall.activity.model.lottery.LotteryRecord;
import project.mall.activity.service.ActivityPrizeService;
import project.mall.activity.service.lottery.LotteryReceiveService;
import project.mall.activity.service.lottery.LotteryRecordService;
import project.tip.TipConstants;
import project.tip.TipService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

//import project.mall.activity.model.lottery.LotteryPrize;
//import project.mall.activity.service.lottery.LotteryPrizeService;

public class LotteryRecordServiceImpl extends HibernateDaoSupport implements LotteryRecordService {
    private ActivityPrizeService activityPrizeService;
    //private LotteryPrizeService lotteryPrizeService;

    private LotteryReceiveService lotteryReceiveService;

    private JdbcTemplate jdbcTemplate;

    private TipService tipService;

    @Override
    public void add(LotteryRecord lotteryRecord, String lang) {
        lotteryRecord.setCreateTime(new Date());
        lotteryRecord.setUpdateTime(new Date());
        ActivityPrize prize = activityPrizeService.getById(lotteryRecord.getPrizeId());

        if (Objects.nonNull(prize)) {
            lotteryRecord.setPrizeName(prize.getPrizeNameEn());
            if (lang.equals(LanguageEnum.CN.getLang())) {
                lotteryRecord.setPrizeName(prize.getPrizeNameCn());
            }

            lotteryRecord.setPrizeType(prize.getPrizeType());
            lotteryRecord.setReceiveState(0);
            lotteryRecord.setPrizeAmount(prize.getPrizeAmount());
            getHibernateTemplate().save(lotteryRecord);
        }
    }

    @Override
    public void add(LotteryRecord lotteryRecord) {
        lotteryRecord.setCreateTime(new Date());
        lotteryRecord.setUpdateTime(new Date());
        getHibernateTemplate().save(lotteryRecord);
    }

    @Override
    public void delete(String id) {
        LotteryRecord lotteryRecord = this.getHibernateTemplate().get(LotteryRecord.class, id);

        if (Objects.nonNull(lotteryRecord)) {
            getHibernateTemplate().delete(id);
        }
    }

    @Override
    public LotteryRecord detail(String id) {
        return this.getHibernateTemplate().get(LotteryRecord.class, id);
    }

//    @Override
//    public void update(LotteryRecord lotteryRecord, String lang) {
//
//        lotteryRecord.setUpdateTime(new Date());
//        LotteryPrize prize = lotteryPrizeService.detail(lotteryRecord.getPartyId());
//
//        if (Objects.nonNull(prize)) {
//
//            lotteryRecord.setPrizeName(prize.getPrizeNameEn());
//            if (lang.equals(LanguageEnum.CN.getLang())) {
//                lotteryRecord.setPrizeName(prize.getPrizeNameCn());
//            }
//            lotteryRecord.setPrizeType(prize.getPrizeType());
//            lotteryRecord.setPrizeAmount(prize.getPrizeAmount());
//            getHibernateTemplate().update(lotteryRecord);
//        }
//    }

    @Transactional
    @Override
    public void updateByApplyReceivePrizes(Integer prizeType, String partyId, String partyName, String recommendName, String sellerName, String activityId) {
        DetachedCriteria query = DetachedCriteria.forClass(LotteryRecord.class);

        if (Objects.nonNull(prizeType)) {
            query.add(Property.forName("prizeType").eq(prizeType));
        }
        if (StringUtils.isNotEmpty(partyId)) {
            query.add(Property.forName("partyId").eq(partyId));
        }
        if (StringUtils.isNotEmpty(activityId)) {
            query.add(Property.forName("activityId").eq(activityId));
        }

        query.add(Property.forName("receiveState").eq(0));

        List<LotteryRecord> results = (List<LotteryRecord>) getHibernateTemplate().findByCriteria(query);

        if (CollectionUtils.isNotEmpty(results)) {
            for (LotteryRecord record : results) {
                record.setReceiveTime(new Date());
                record.setReceiveState(1);
                getHibernateTemplate().update(record);
            }

            LotteryReceive receive = new LotteryReceive();

            List<String> prizeIdList = results.stream().map(LotteryRecord::getPrizeId).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(prizeIdList)) {
                String prizeIds = String.join(",", prizeIdList);
                receive.setPrizeIds(prizeIds);
            }

            String lotteryName = results.stream().map(LotteryRecord::getLotteryName).findFirst().get();
            BigDecimal prizeAmount = BigDecimal.ZERO;
            for (LotteryRecord oneRecord : results) {
                prizeAmount = prizeAmount.add(oneRecord.getPrizeAmount());
            }

            receive.setApplyTime(new Date());
            receive.setPrizeType(prizeType);
            receive.setState(0);
            receive.setActivityId(activityId);
            receive.setActivityId(activityId);
            receive.setLotteryName(lotteryName);

            receive.setPartyId(partyId);
            receive.setPartyName(partyName);
            receive.setPrizeAmount(prizeAmount);
            receive.setRecommendName(recommendName);
            receive.setSellerName(sellerName);
            receive.setActivityType(ActivityTypeEnum.SIMPLE_LOTTERY.getIndex());

            lotteryReceiveService.add(receive);

            this.tipService.saveTip(receive.getId().toString(), TipConstants.MARKETING_ACTIVITY_LOTTERY);
        }
    }

    @Override
    public LotteryRecordSumDTO getSumRecord(String activityId, String partyId) {
        DetachedCriteria query = DetachedCriteria.forClass(LotteryRecord.class);
        if (StringUtils.isNotEmpty(partyId)) {
            query.add(Property.forName("partyId").eq(partyId));
        }
        if (StringUtils.isNotEmpty(activityId)) {
            query.add(Property.forName("activityId").eq(activityId));
        }

        BigDecimal totalPrizeAmount = BigDecimal.ZERO;

        LotteryRecordSumDTO lotteryRecordSumDTO = new LotteryRecordSumDTO();
        lotteryRecordSumDTO.setAmount(totalPrizeAmount);
        lotteryRecordSumDTO.setGoodsNum(0L);

        List<LotteryRecord> lotteryRecords = (List<LotteryRecord>) getHibernateTemplate().findByCriteria(query);

        if (CollectionUtils.isNotEmpty(lotteryRecords)) {
            long count = lotteryRecords.stream().filter(value -> value.getPrizeType() == 1 && value.getReceiveState() == 0).count();
            lotteryRecordSumDTO.setGoodsNum(count);

            for (LotteryRecord oneRecord : lotteryRecords) {
                if (oneRecord.getPrizeType() == 2 && oneRecord.getReceiveState() == 0) {
                    totalPrizeAmount = totalPrizeAmount.add(oneRecord.getPrizeAmount());
                }
            }
            totalPrizeAmount = totalPrizeAmount.setScale(2, RoundingMode.DOWN);
            lotteryRecordSumDTO.setAmount(totalPrizeAmount);
        }
        return lotteryRecordSumDTO;
    }

    @Override
    public Page pagedByPartyId(String activityId, String partyId, Integer pageNum, Integer pageSize) {
        Page page = new Page();

        DetachedCriteria query = DetachedCriteria.forClass(LotteryRecord.class);
        query.add(Property.forName("activityId").eq(activityId));
        if (Objects.nonNull(partyId)) {
            query.add(Property.forName("partyId").eq(partyId));
        }

        query.addOrder(Order.desc("createTime"));

        // 查询总条数
        Long totalCount = (Long) query.setProjection(Projections.rowCount()).getExecutableCriteria(this.getHibernateTemplate().getSessionFactory().getCurrentSession()).uniqueResult();

        query.setProjection(null);

        List<?> resultList = getHibernateTemplate().findByCriteria(query, (pageNum - 1) * pageSize, pageSize);
        page.setTotalElements(totalCount.intValue());
        page.setElements(resultList);
        page.setPageSize(pageSize);
        page.setThisPageNumber(pageNum);
        return page;
    }

    @Override
    public Page paged(String username, String uid, String sellerName, Integer receiveState, Integer prizeType, String startTime, String endTime, Integer pageNum, Integer pageSize) {

        Page page = new Page();

        StringBuilder queryString = new StringBuilder("SELECT ");

        StringBuilder countString = new StringBuilder("select count(1) from ACTIVITY_LOTTERY_RECORD t1 INNER JOIN PAT_PARTY t2 ON t1.PARTY_ID = t2.UUID  where 1=1 ");
        List<Object> params = new ArrayList<>();

        queryString.append(
                "t1.PARTY_ID AS partyId," +
                        "t1.PARTY_NAME AS partyName," +
                        "t1.PRIZE_NAME as prizeName," +
                        "t1.SELLER_NAME as sellerName," +
                        "t1.PRIZE_ID as prizeId," +
                        "t1.ACTIVITY_ID as lotteryId," +
                        "t1.LOTTERY_NAME as lotteryName," +
                        "t1.CREATE_TIME as createTime," +
                        "t1.RECOMMEND_NAME AS recommendName," +
                        "t1.LOTTERY_TIME AS lotteryTime," +
                        "t1.RECEIVE_TIME AS receiveTime," +
                        "t1.RECEIVE_STATE AS receiveState," +
                        "t1.PRIZE_AMOUNT AS prizeAmount," +
                        "t1.PRIZE_TYPE AS prizeType," +
                        "t2.EMAIL email," +
                        "t2.PHONE AS phone," +
                        "t2.USERCODE AS uid," +
                        "t1.UUID AS id ");

        queryString.append("FROM ");

        queryString.append(
                " ACTIVITY_LOTTERY_RECORD t1 " +
                        "INNER JOIN PAT_PARTY t2 ON t1.PARTY_ID = t2.UUID  where 1=1  "
        );

        if (StringUtils.isNotEmpty(username)) {
            queryString.append(" and t1.PARTY_NAME like ? ");
            countString.append(" and t1.PARTY_NAME like ? ");
            params.add("%" + username + "%");
        }

        if (StringUtils.isNotEmpty(uid)) {
            queryString.append(" and t2.USERCODE =  ? ");
            countString.append(" and t2.USERCODE =  ? ");
            params.add(uid);
        }

        if (StringUtils.isNotEmpty(sellerName)) {
            queryString.append(" and t1.SELLER_NAME like ? ");
            countString.append(" and t1.SELLER_NAME like ? ");
            params.add("%" + sellerName + "%");
        }

        if (Objects.nonNull(receiveState)) {
            queryString.append(" and t1.RECEIVE_STATE =  ? ");
            countString.append(" and t1.RECEIVE_STATE =  ? ");
            params.add(receiveState);
        }

        if (Objects.nonNull(prizeType)) {
            queryString.append(" and t1.PRIZE_TYPE =  ? ");
            countString.append(" and t1.PRIZE_TYPE =  ? ");
            params.add(prizeType);
        }

        if (StringUtils.isNotEmpty(startTime)) {
            queryString.append(" and t1.CREATE_TIME > ? ");
            countString.append(" and t1.CREATE_TIME > ? ");
            params.add(startTime);
        }

        if (StringUtils.isNotEmpty(endTime)) {
            queryString.append(" and t1.CREATE_TIME <= ? ");
            countString.append(" and t1.CREATE_TIME <= ? ");
            params.add(startTime);
        }


        queryString.append(" order by t1.CREATE_TIME desc ");

        queryString.append(" limit " + (pageNum - 1) * pageSize + "," + pageSize);

        List list = jdbcTemplate.queryForList(queryString.toString(), params.toArray());
        int totalCount = jdbcTemplate.queryForObject(countString.toString(), params.toArray(), Integer.class);
        Iterator iterator = list.iterator();
        List<LotteryRecordDTO> resultList = new ArrayList<>();
        while (iterator.hasNext()) {
            Map rowMap = (Map) iterator.next();
            String id = (String) rowMap.get("id");
            LocalDateTime createTime = (LocalDateTime) rowMap.get("createTime");
            LocalDateTime lotteryTime = (LocalDateTime) rowMap.get("lotteryTime");
            LocalDateTime receiveTime = (LocalDateTime) rowMap.get("receiveTime");

            String prizeName = (String) rowMap.getOrDefault("prizeName", "");
            String prizeId = (String) rowMap.getOrDefault("prizeId", "");
            String rpartyId = (String) rowMap.getOrDefault("partyId", "");
            String rpuid = (String) rowMap.getOrDefault("uid", "");
            String partyName = (String) rowMap.getOrDefault("partyName", "");
            String recommendName = (String) rowMap.getOrDefault("recommendName", "");
            Integer rstate = (Integer) rowMap.get("receiveState");
            BigDecimal prizeAmount = (BigDecimal) rowMap.get("prizeAmount");
            Integer rprizeType = (Integer) rowMap.get("prizeType");
            String email = (String) rowMap.getOrDefault("email", "");
            String lotteryId = (String) rowMap.getOrDefault("lotteryId", "");
            String lotteryName = (String) rowMap.getOrDefault("lotteryName", "");
            String phone = (String) rowMap.getOrDefault("phone", "");
            String rusername = (String) rowMap.getOrDefault("partyName", "");
            String rsellerName = (String) rowMap.getOrDefault("sellerName", "");

            LotteryRecordDTO dto = new LotteryRecordDTO();
            dto.setId(id);
            dto.setEmail(email);
            dto.setPartyId(rpartyId);
            dto.setUid(rpuid);
            dto.setPartyName(partyName);
            dto.setPrizeId(prizeId);
            dto.setPrizeName(prizeName);
            dto.setPrizeAmount(prizeAmount);
            dto.setPhone(phone);
            dto.setRecommendName(recommendName);
            dto.setUsername(rusername);
            dto.setReceiveState(rstate);
            dto.setPrizeType(rprizeType);
            dto.setSellerName(rsellerName);
            dto.setLotteryId(lotteryId);
            dto.setLotteryName(lotteryName);
            dto.setLotteryTime(DateUtils.format(Date.from(lotteryTime.atZone(ZoneId.systemDefault()).toInstant()), DateUtils.NORMAL_DATE_FORMAT));
            if (Objects.nonNull(receiveTime)) {
                dto.setReceiveTime(DateUtils.format(Date.from(receiveTime.atZone(ZoneId.systemDefault()).toInstant()), DateUtils.NORMAL_DATE_FORMAT));
            }
            dto.setCreateTime(DateUtils.format(Date.from(createTime.atZone(ZoneId.systemDefault()).toInstant()), DateUtils.NORMAL_DATE_FORMAT));
            resultList.add(dto);
        }

        page.setPageSize(pageSize);
        page.setElements(resultList);
        page.setThisPageNumber(pageNum);
        page.setTotalElements(totalCount);

        return page;
    }

//    public void setLotteryPrizeService(LotteryPrizeService lotteryPrizeService) {
//        this.lotteryPrizeService = lotteryPrizeService;
//    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void setLotteryReceiveService(LotteryReceiveService lotteryReceiveService) {
        this.lotteryReceiveService = lotteryReceiveService;
    }

    public void setActivityPrizeService(ActivityPrizeService activityPrizeService) {
        this.activityPrizeService = activityPrizeService;
    }

    public void setTipService(TipService tipService) {
        this.tipService = tipService;
    }
}
