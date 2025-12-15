package project.mall.activity.helper;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import kernel.exception.BusinessException;
import kernel.util.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import project.mall.LanguageEnum;
import project.mall.activity.ActivityTypeEnum;
import project.mall.activity.core.vo.ActivityParam;
import project.mall.activity.handler.FirstRechargeFruitDialActivityHandler;
import project.mall.activity.model.ActivityLibrary;
import project.mall.activity.model.ActivityPrize;
import project.mall.activity.model.ActivityUser;
import project.mall.activity.model.lottery.ActivityUserPoints;
import project.mall.activity.rule.FruitDialActivityConfig;
import project.mall.activity.service.ActivityLibraryService;
import project.mall.activity.service.ActivityPrizeService;
import project.mall.activity.service.ActivityUserPointsService;
import project.mall.activity.service.ActivityUserService;
import project.mall.activity.service.lottery.LotteryRecordService;
import project.mall.activity.model.lottery.LotteryRecord;
import project.mall.seller.SellerService;
import project.mall.seller.model.Seller;
import project.party.PartyService;
import project.party.model.Party;
import project.party.model.UserRecom;
import project.party.recom.UserRecomService;

import java.util.*;

public class ActivityRechargeAndLotteryHelper {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    private ActivityPrizeService activityPrizeService;

    private LotteryRecordService lotteryRecordService;

    private ActivityLibraryService activityLibraryService;

    private SellerService sellerService;

    private UserRecomService userRecomService;

    private PartyService partyService;

    private ActivityUserPointsService activityUserPointsService;

    private ActivityUserService activityUserService;

    // 注意：此处不建议依赖 ActivityHelper，防止产生死循环
    //private ActivityHelper activityHelper;

    public FruitDialActivityConfig getActivityConfig(ActivityLibrary activityEntity) {
        ActivityTypeEnum activityType = ActivityTypeEnum.typeOf(activityEntity.getType());
        String configJson = activityEntity.getActivityConfig();

        TypeReference paramType = new TypeReference<List<ActivityParam>>() {
        };
        List<ActivityParam> configInfoList = (List<ActivityParam>) JsonUtils.readValue(configJson, paramType);
        return (FruitDialActivityConfig) activityType.initActivityConfig(configInfoList);
    }

    public void beginLottery(ActivityLibrary activity, String partyId, int batchDrawTimes) {
        if (activity == null || StrUtil.isBlank(partyId) || batchDrawTimes <= 0) {
            throw new BusinessException(2, "不合规的参数");
        }

        String activityId = activity.getId().toString();
        String actionType = FirstRechargeFruitDialActivityHandler.ActivityTouchEventTypeEnum.LOTTERY.getEventType();

        ActivityUserPoints activityUserPoints = activityUserPointsService.saveOrGetUserPoints(activity.getType(), activityId, partyId);

        ActivityUser activityUser = activityUserService.getActivityUser(activityId, partyId, actionType);
        int leftLotteryTimes = 0;
        if (activityUser != null) {
            if (activityUser.getAllowJoinTimes() <= 0) {
                // 抽奖次数不设限制，以及支持随着业务逻辑可以动态增加上限时，allowJoinTimes 的值一开始是为 0 的，此处需要特殊处理 TODO
                leftLotteryTimes = 0;
            } else {
                // allowJoinTimes 值大于 0 代表有了具体的上限值，该值在业务中可能会动态变化
                leftLotteryTimes = activityUser.getAllowJoinTimes() - activityUser.getJoinTimes();
            }
        } else {
            // 一个上级用户还没来得及参加首充，但是其下级用户参加活动导致其产生的活动积分可用吗？
            activityUser = activityUserService.saveOrGetActivityUser(activity.getType(), activityId, partyId, actionType);
        }

        FruitDialActivityConfig activityConfig = getActivityConfig(activity);
        int exchangeRatio = activityConfig.getScoreExchangeLotteryTimeRatio();
        if (leftLotteryTimes >= batchDrawTimes) {
            return;
        }

        if (activityUserPoints.getPoints() / exchangeRatio + leftLotteryTimes < batchDrawTimes) {
            throw new BusinessException(3, "积分不足");
        }

        // 减少积分的数量
        int reducePoints = (batchDrawTimes - leftLotteryTimes) * exchangeRatio;
        activityUserPointsService.updatePoints(activityUserPoints.getId().toString(), -reducePoints);

        // 用户增加抽奖次数
        activityUserService.updateAllowJoinTimes(activityUser.getId().toString(), batchDrawTimes - leftLotteryTimes);
    }

    /**
     * 生成奖项
     *
     * @return
     */
    @Transactional
    public List<ActivityPrize> draw(String activityId, String partyId, String lang, int batchDrawTimes, ActivityPrize defaultPrize) {
        ActivityLibrary lottery = activityLibraryService.findById(activityId);
        List<ActivityPrize> drawedLotteryPrizes = new ArrayList<>();
        ActivityPrize currentDrawedLotteryPrize = null;

        //初始化记载所有奖品信息
        List<ActivityPrize> lotteryPrizes = activityPrizeService.listByActivityId(activityId, 1);
        if (CollectionUtil.isEmpty(lotteryPrizes)) {
            return drawedLotteryPrizes;
        }
        if (batchDrawTimes <= 0) {
            return drawedLotteryPrizes;
        }

        Seller seller = sellerService.getSeller(partyId);
        Party currentParty = partyService.cachePartyBy(partyId, true);

        UserRecom userRecom = userRecomService.findByPartyId(partyId);
        String recomName = null;
        if (Objects.nonNull(userRecom)) {
            Party party = partyService.cachePartyBy(userRecom.getPartyId(), true);
            recomName = party.getUsername();
        }

        if (defaultPrize == null) {
            List<ActivityPrize> defaltPrizeList = activityPrizeService.listDefaltPrize(activityId);
            if (CollectionUtil.isNotEmpty(defaltPrizeList)) {
                // TODO
                defaultPrize = defaltPrizeList.get(0);
            }
        }

        for (int times = 0; times < batchDrawTimes; times++) {
            long result = randomNum(1, 100000000);

            int line = 0;
            int temp = 0;

            for (int i = 0; i < lotteryPrizes.size(); i++) {
                ActivityPrize tempPrice = lotteryPrizes.get(i);

                // 因此中奖几率最低是八位小数，不能再小了
                double d = (tempPrice.getOdds().floatValue() * 100000000);
                int c = (int) d;
                temp = temp + c;
                line = 100000000 - temp;
                if (c != 0) {
                    if (result > line && result <= (line + c)) {
                        currentDrawedLotteryPrize = tempPrice;
                        break;
                    }
                }
            }

            if (currentDrawedLotteryPrize == null) {
                currentDrawedLotteryPrize = defaultPrize;
            }
            if (Objects.nonNull(currentDrawedLotteryPrize)
                    && (currentDrawedLotteryPrize.getId() != null
                    && StrUtil.isNotBlank(currentDrawedLotteryPrize.getId().toString())
                    && !Objects.equals(currentDrawedLotteryPrize.getId().toString(), "0")
                    && currentDrawedLotteryPrize.getPrizeType() != 3)
            ) {
                LotteryRecord lotteryRecord = new LotteryRecord();
                lotteryRecord.setPrizeId(currentDrawedLotteryPrize.getId().toString());
                lotteryRecord.setPartyId(partyId);
                lotteryRecord.setPrizeAmount(currentDrawedLotteryPrize.getPrizeAmount());
                lotteryRecord.setPrizeType(currentDrawedLotteryPrize.getPrizeType());
                lotteryRecord.setLotteryTime(new Date());
                lotteryRecord.setActivityId(currentDrawedLotteryPrize.getActivityId());
                if (LanguageEnum.CN.getLang().equals(lang)) {
                    lotteryRecord.setLotteryName(lottery.getTitleCn());
                } else {
                    lotteryRecord.setLotteryName(lottery.getTitleEn());
                }
                lotteryRecord.setPartyName(currentParty.getUsername());
                lotteryRecord.setRecommendName(recomName);
                lotteryRecord.setSellerName(seller.getName());
                lotteryRecord.setPrizeImage(currentDrawedLotteryPrize.getImage());
                lotteryRecordService.add(lotteryRecord, lang);

                drawedLotteryPrizes.add(currentDrawedLotteryPrize);
            } else {
                // 谢谢惠顾这种非入库奖品，不产生 record 记录
                if (currentDrawedLotteryPrize != null) {
                    drawedLotteryPrizes.add(currentDrawedLotteryPrize);
                }
            }
        }

        return drawedLotteryPrizes;
    }

    // 获取2个值之间的随机数
    private static long randomNum(int smin, int smax) {
        int range = smax - smin;
        double rand = Math.random();
        return (smin + Math.round(rand * range));
    }

    public void setActivityPrizeService(ActivityPrizeService activityPrizeService) {
        this.activityPrizeService = activityPrizeService;
    }

    public void setLotteryRecordService(LotteryRecordService lotteryRecordService) {
        this.lotteryRecordService = lotteryRecordService;
    }

    public void setActivityLibraryService(ActivityLibraryService activityLibraryService) {
        this.activityLibraryService = activityLibraryService;
    }

    public void setSellerService(SellerService sellerService) {
        this.sellerService = sellerService;
    }

    public void setUserRecomService(UserRecomService userRecomService) {
        this.userRecomService = userRecomService;
    }

    public void setPartyService(PartyService partyService) {
        this.partyService = partyService;
    }

    public void setActivityUserPointsService(ActivityUserPointsService activityUserPointsService) {
        this.activityUserPointsService = activityUserPointsService;
    }

    public void setActivityUserService(ActivityUserService activityUserService) {
        this.activityUserService = activityUserService;
    }

}
