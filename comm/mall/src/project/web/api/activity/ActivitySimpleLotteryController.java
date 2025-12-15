package project.web.api.activity;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import kernel.exception.BusinessException;
import kernel.service.TransactionMethodFragmentFun;
import kernel.util.JsonUtils;
import kernel.util.PageInfo;
import kernel.util.StringUtils;
import kernel.web.BaseAction;
import kernel.web.Page;
import kernel.web.ResultObject;
import org.apache.commons.collections.CollectionUtils;
import org.codehaus.jackson.type.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;
import project.blockchain.event.message.RechargeSuccessEvent;
import project.blockchain.event.model.RechargeInfo;
import project.mall.LanguageEnum;
import project.mall.activity.ActivityTypeEnum;
import project.mall.activity.core.ActivityHandlerLoader;
import project.mall.activity.core.ActivityHelper;
import project.mall.activity.core.vo.ActivityParam;
import project.mall.activity.dto.ActivityEditInfoDTO;
import project.mall.activity.dto.ActivityPrizeDTO;
import project.mall.activity.dto.MultiLanguageField;
import project.mall.activity.dto.lottery.LotteryDTO;
import project.mall.activity.dto.lottery.LotteryPrizeDTO;
import project.mall.activity.dto.lottery.LotteryRecordSumDTO;
import project.mall.activity.event.message.ActivityUserLotteryMessage;
import project.mall.activity.handler.ActivityHandler;
import project.mall.activity.handler.FirstRechargeFruitDialActivityHandler;
import project.mall.activity.helper.ActivityRechargeAndLotteryHelper;
import project.mall.activity.helper.ActivitySimpleLotteryHelper;
import project.mall.activity.model.ActivityLibrary;
import project.mall.activity.model.ActivityPrize;
import project.mall.activity.model.lottery.ActivityUserPoints;
import project.mall.activity.rule.SimpleLotteryActivityConfig;
import project.mall.activity.service.ActivityLibraryService;
import project.mall.activity.service.ActivityPrizeService;
import project.mall.activity.service.ActivityUserPointsService;
import project.mall.activity.service.ActivityUserService;
import project.mall.activity.service.lottery.LotteryRecordService;
import project.mall.seller.SellerService;
import project.mall.seller.model.Seller;
import project.party.PartyService;
import project.party.model.Party;
import project.party.model.UserRecom;
import project.party.recom.UserRecomService;
import project.user.kyc.Kyc;
import project.user.kyc.KycService;
import project.web.api.dto.ActivityPointsDTO;
import util.DateUtil;
import util.concurrent.gofun.core.FunParams;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

@RestController
@CrossOrigin
public class ActivitySimpleLotteryController extends BaseAction {

    @Resource
    private LotteryRecordService lotteryRecordService;

    @Resource
    private ActivityPrizeService activityPrizeService;

    @Resource
    private ActivityLibraryService activityLibraryService;

    @Resource
    private ActivityHelper activityHelper;

    @Resource
    private ActivityUserPointsService activityUserPointsService;

    @Resource
    private PartyService partyService;

    @Resource
    private ActivityUserService activityUserService;

    @Resource
    private UserRecomService userRecomService;

    @Resource
    private SellerService sellerService;

    @Resource
    private KycService kycService;

    @Resource
    private TransactionMethodFragmentFun transactionMethodFragmentFun;

    @Autowired
    private ActivitySimpleLotteryHelper activityLotteryHelper;

    private final String action = "/api/activity/lottery!";

    /**
     * 取代：count.action
     *
     * @param activityId
     * @return
     */
    @GetMapping(action + "countPrize.action")
    public ResultObject count(HttpServletRequest request, String activityId) {
        ResultObject resultObject = readSecurityContextFromSession(new ResultObject());
        if (!"0".equals(resultObject.getCode())) {
            return resultObject;
        }

        String partyId = getLoginPartyId();
        String userId = request.getParameter("userId");
        // 临时测试使用 TODO
        //partyId = userId;

        LotteryRecordSumDTO sumRecord = this.lotteryRecordService.getSumRecord(activityId, partyId);
        resultObject.setData(sumRecord);
        return resultObject;
    }

    /**
     * @return
     */
    @GetMapping(action + "getCurrentActivity.action")
    public ResultObject getCurrentActivity(HttpServletRequest request) {
        ResultObject resultObject = new ResultObject();

        String lang = this.getLanguage(request);

        String type = ActivityTypeEnum.SIMPLE_LOTTERY.getType();
        List<ActivityLibrary> activityList = activityLibraryService.getShowActivity(type);

        Map<String, Object> retData = new HashMap<>();

        if (CollectionUtil.isNotEmpty(activityList)) {
            Date now = new Date();
            ActivityLibrary activity = activityList.get(0);
            retData.put("detailUrl", activity.getDetailUrl());
            if (Objects.equals(lang, "cn") || Objects.equals(lang, "zh")) {
                retData.put("title", activity.getTitleCn());
            } else {
                retData.put("title", activity.getTitleEn());
            }

            retData.put("id", activity.getId());

            retData.put("status", activity.getStatus());
            if (activity.getStartTime().after(now) || activity.getEndTime().before(now)) {
                // 活动未达到开始时间，或者已过期
                retData.put("running", "0");
            } else {
                retData.put("running", "1");
            }
        }

        resultObject.setData(retData);
        return resultObject;
    }

    /**
     * 用户端加载活动详情
     *
     * @param request
     * @return
     */
    @GetMapping(action + "detail.action")
    public Object activityShowDetail(HttpServletRequest request) {
        ResultObject resultObject = new ResultObject();

        String language = getLanguage(request);
        String partyId = getLoginPartyId();
        String activityId = request.getParameter("activityId");
//        String activityCode = request.getParameter("activityCode");

        ActivityLibrary activityLibrary = activityLibraryService.findById(activityId.trim());
//        if (StrUtil.isNotBlank(activityId)) {
//            activityLibrary = activityLibraryService.findById(activityId.trim());
//        }
//        if (activityLibrary == null && StrUtil.isNotBlank(activityCode)) {
//            activityLibrary = activityLibraryService.getByCode(activityCode.trim());
//        }
        if (activityLibrary == null) {
            resultObject.setCode("-1");
            resultObject.setMsg("活动记录不存在");
            return resultObject;
        }

        ActivityTypeEnum activityType = ActivityTypeEnum.typeOf(activityLibrary.getType());
        ActivityHandler handler = ActivityHandlerLoader.getInstance().getHandler(activityType);

        ActivityEditInfoDTO activityInfo = handler.getActivityDetail(activityId, language);
        List<MultiLanguageField> i18nDescriptions = activityInfo.getI18nDescriptions();
        List<MultiLanguageField> i18nTitles = activityInfo.getI18nTitles();

        SimpleLotteryActivityConfig activityConfig = activityLotteryHelper.getActivityConfig(activityLibrary);

        LotteryDTO lotteryDTO = new LotteryDTO();
        lotteryDTO.setId(activityInfo.getId());
        lotteryDTO.setStartTime(activityInfo.getStartTime());
        lotteryDTO.setEndTime(activityInfo.getEndTime());
        lotteryDTO.setImages(activityInfo.getImageUrl());
        lotteryDTO.setLink(activityInfo.getDetailUrl());
        if (LanguageEnum.CN.getLang().equals(language)) {
            lotteryDTO.setName(activityLibrary.getTitleCn());
            lotteryDTO.setDescription(activityLibrary.getDescriptionCn());
        } else {
            lotteryDTO.setName(activityLibrary.getTitleEn());
            lotteryDTO.setDescription(activityLibrary.getDescriptionEn());
        }
        lotteryDTO.setLink(activityLibrary.getDetailUrl());
        lotteryDTO.setInvitePoints(0);//activityConfig.getInviteAwardScore()
        lotteryDTO.setLotteryCondition(0.0);//activityConfig.getFirstRechargeAmountLimit()
        // activityConfig.getInitLotteryScore() / activityConfig.getScoreExchangeLotteryTimeRatio()
        lotteryDTO.setLotteryNumber(0);
        lotteryDTO.setPointsToNumber(activityConfig.getScoreExchangeLotteryTimeRatio());
        lotteryDTO.setMinPoints(activityConfig.getMinReceiveMoneyThreshold());
        lotteryDTO.setState(activityLibrary.getStatus());
        lotteryDTO.setCreateTime(DateUtil.formatDate(activityLibrary.getCreateTime(), DateUtil.DATE_FORMAT));
        lotteryDTO.setUpdateTime(DateUtil.formatDate(activityLibrary.getUpdateTime(), DateUtil.DATE_FORMAT));

        if (CollectionUtil.isNotEmpty(i18nTitles)) {
            for (MultiLanguageField oneTitle : i18nTitles) {
                if (oneTitle.getLang().equalsIgnoreCase(language)) {
                    lotteryDTO.setName(oneTitle.getContent());
                }
            }
        }
        if (CollectionUtil.isNotEmpty(i18nDescriptions)) {
            for (MultiLanguageField oneDescription : i18nDescriptions) {
                if (oneDescription.getLang().equalsIgnoreCase(language)) {
                    lotteryDTO.setDescription(oneDescription.getContent());
                }
            }
        }

        // 活动奖品信息加载
        List<ActivityPrizeDTO> prizeDtoList = activityInfo.getPrizeList();
        lotteryDTO.setPrizeList(prizeDtoList);
        List<String> prizeIdList = new ArrayList<>();
        lotteryDTO.setPrizeIds(prizeIdList);
        if (CollectionUtils.isNotEmpty(prizeDtoList)) {
            for (ActivityPrizeDTO onePrize : prizeDtoList) {
                prizeIdList.add(onePrize.getId());
            }
        }

        ActivityUserPoints userPoints = activityUserPointsService.getByActivityId(activityId, partyId);

        if (Objects.nonNull(userPoints)) {
            lotteryDTO.setPoints(Objects.isNull(userPoints.getPoints()) ? 0 : userPoints.getPoints());
        } else {
            lotteryDTO.setPoints(0);
        }
        resultObject.setData(lotteryDTO);
        return resultObject;
    }


    /**
     * 根据活动ID查询用户积分
     *
     * @param activityId
     * @return
     */
    @GetMapping(action + "getPoints.action")
    public ResultObject getPoints(String activityId) {
        ResultObject resultObject = readSecurityContextFromSession(new ResultObject());
        if (!"0".equals(resultObject.getCode())) {
            return resultObject;
        }

        ActivityLibrary activityLibrary = activityLibraryService.findById(activityId);

        String partyId = this.getLoginPartyId();
        ActivityUserPoints userPoints = null;
        if (activityLibrary.getType().equals(ActivityTypeEnum.SIMPLE_LOTTERY.getType())) {
            // 特殊
            userPoints = activityUserPointsService.getByActivityId("0", partyId);
        }  else {
            userPoints = activityUserPointsService.getByActivityId(activityId, partyId);
        }

        resultObject.setData(0);
        if (Objects.nonNull(userPoints) && Objects.nonNull(userPoints.getPoints())) {
            resultObject.setData(userPoints.getPoints());
        }
        return resultObject;
    }


    /**
     * 根据活动ID计算邀请人数，以及积分
     *
     * @param activityId
     * @return
     */
    @PostMapping(action + "getCountPoints.action")
    public ResultObject getCountPoints(String activityId) {
        ResultObject resultObject = new ResultObject();

        String loginPartyId = getLoginPartyId();
        ActivityLibrary activityLibrary = activityLibraryService.findById(activityId);
        List<String> childrens = userRecomService.findChildren(loginPartyId);
        ActivityLibrary activityEntity = activityLibraryService.findById(activityId);
        ActivityPointsDTO activityPointsDTO = new ActivityPointsDTO();
        activityPointsDTO.setNumber(0);
        activityPointsDTO.setPoints(0);

        if (CollectionUtils.isNotEmpty(childrens)) {

            activityPointsDTO.setNumber(childrens.size());
            if (StringUtils.isNotEmpty(activityLibrary.getActivityConfig())) {

                ActivityTypeEnum activityType = ActivityTypeEnum.typeOf(activityEntity.getType());
                String configJson = activityEntity.getActivityConfig();

                TypeReference paramType = new TypeReference<List<ActivityParam>>() {
                };
                List<ActivityParam> configInfoList = (List<ActivityParam>) JsonUtils.readValue(configJson, paramType);
                SimpleLotteryActivityConfig fruitDialActivityConfig = (SimpleLotteryActivityConfig) activityType.initActivityConfig(configInfoList);
                Integer number = activityUserService.count(activityLibrary, childrens);
                activityPointsDTO.setNumber(number);

                ActivityUserPoints userPoints = activityUserPointsService.saveOrGetUserPoints(activityLibrary.getType(), "0", loginPartyId);
                activityPointsDTO.setPoints(userPoints.getPoints());
            }
        }
        resultObject.setData(activityPointsDTO);
        return resultObject;
    }

    /**
     * 领取奖品，取代：receive.action
     *
     * @param prizeType
     * @return
     */
    @PostMapping(action + "receivePrize.action")
    public Object receive(Integer prizeType, String activityId) {
        ResultObject resultObject = readSecurityContextFromSession(new ResultObject());
        if (!"0".equals(resultObject.getCode())) {
            return resultObject;
        }

        String partyId = this.getLoginPartyId();
        String username = partyService.cachePartyBy(partyId, true).getUsername();
        UserRecom userRecom = userRecomService.findByPartyId(partyId);
        String recomName = null;
        if (Objects.nonNull(userRecom)) {
            Party party = partyService.cachePartyBy(userRecom.getPartyId(), true);
            recomName = party.getUsername();
        }

        String sellerName = sellerService.getSeller(partyId).getName();

        this.lotteryRecordService.updateByApplyReceivePrizes(prizeType, partyId, username, recomName, sellerName, activityId);
        resultObject.setCode("0");
        resultObject.setMsg("操作成功");
        return resultObject;
    }

    /**
     * 我的中奖列表，取代：pageRecordList.action
     *
     * @param request
     * @return
     */
    @GetMapping(action + "pageListMyPrize.action")
    public ResultObject listRecord(HttpServletRequest request) {
        ResultObject resultObject = readSecurityContextFromSession(new ResultObject());
        if (!"0".equals(resultObject.getCode())) {
            return resultObject;
        }

        String partyId = getLoginPartyId();
        String activityId = request.getParameter("activityId");
        if (StrUtil.isBlank(activityId)) {
            throw new BusinessException("未指定活动ID");
        }
        PageInfo pageInfo = getPageInfo(request);

        Page mallPageInfo = this.lotteryRecordService.pagedByPartyId(activityId, partyId, pageInfo.getPageNum(), pageInfo.getPageSize());
        pageInfo.setTotalElements(mallPageInfo.getTotalElements());
        JSONObject object = new JSONObject();
        object.put("pageInfo", pageInfo);
        object.put("pageList", mallPageInfo.getElements());
        resultObject.setData(object);
        return resultObject;
    }

    /**
     * 普通积分抽奖代码
     */
    @PostMapping(action + "draw.action")
    public Object doDraw(HttpServletRequest request) {
        ResultObject resultObject = readSecurityContextFromSession(new ResultObject());
        if (!"0".equals(resultObject.getCode())) {
            return resultObject;
        }

        String language = getLanguage(request);
        String loginPartyId = getLoginPartyId();

        String activityId = request.getParameter("activityId");
        String drawTimesStr = request.getParameter("drawTimes");

        ActivityLibrary activityLibrary = activityLibraryService.findById(activityId);
        if (activityLibrary == null) {
            resultObject.setCode("1");
            resultObject.setMsg("活动不存在");
            return resultObject;
        }
        int drawTimes = 1;
        if (StrUtil.isNotBlank(drawTimesStr)) {
            drawTimes = Integer.parseInt(drawTimesStr.trim());
        }
        if (drawTimes <= 0) {
            resultObject.setCode("1");
            resultObject.setMsg("抽奖次数不对");
            return resultObject;
        }

        ResultObject check = this.check(loginPartyId, activityId);
        if (check != null) {
            return check;
        }


        try {
            // 将以下代码放到事务中处理
            FunParams inputParams = FunParams.newParam().set("drawTimes", drawTimes);
            FunParams result = transactionMethodFragmentFun.runInTransaction(inputParams, param -> {
                int paramDrawTimes = param.get("drawTimes").getAsInt();

                // 先将积分兑换成抽奖次数
                activityLotteryHelper.beginLottery(activityLibrary, loginPartyId, paramDrawTimes);

                Date now = new Date();
                List<ActivityPrize> allDrawedPrizeList = new ArrayList();
                String action = FirstRechargeFruitDialActivityHandler.ActivityTouchEventTypeEnum.LOTTERY.getEventType();
                for (int i = 0; i < paramDrawTimes; i++) {
                    ActivityUserLotteryMessage extraInfo = new ActivityUserLotteryMessage();
                    extraInfo.setActivityId(activityId);
                    extraInfo.setLang(language);
                    extraInfo.setUserId(loginPartyId);
                    extraInfo.setEventId(String.valueOf(now.getTime()));
                    extraInfo.setBatchJoinTimes(1);
                    extraInfo.setRefTime(now.getTime());

                    // 抽奖逻辑通过这个流程来实现，将在其中的 award 实现里写
                    activityHelper.joinActivity(activityId, loginPartyId, action, extraInfo);

                    // 从上下文中提取用户抽奖结果
                    List<ActivityPrize> drawedPrizeList = (List<ActivityPrize>) activityHelper.getActivityResult(activityLibrary.getType(),
                            "lottery_activity_drawed_prizes").getValue();

                    if (CollectionUtil.isNotEmpty(drawedPrizeList)) {
                        allDrawedPrizeList.addAll(drawedPrizeList);
                    }
                }

                return param.set("drawedPrizeList", allDrawedPrizeList);
            });

            List<ActivityPrize> drawedPrizeList = (List<ActivityPrize>) result.get("drawedPrizeList").getValue();
            List<LotteryPrizeDTO> retData = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(drawedPrizeList)) {
                for (ActivityPrize onePrize : drawedPrizeList) {
                    LotteryPrizeDTO oneDto = new LotteryPrizeDTO();
                    oneDto.setId(onePrize.getId().toString());
                    oneDto.setPrizeAmount(onePrize.getPrizeAmount());
                    if (Objects.equals(language, LanguageEnum.CN.getLang())) {
                        oneDto.setPrizeName(onePrize.getPrizeNameCn());
                    } else {
                        oneDto.setPrizeName(onePrize.getPrizeNameEn());
                    }

                    retData.add(oneDto);
                }
                resultObject.setData(retData);
            }
        } catch (BusinessException e) {
            if (1 == e.getSign()) {
                resultObject.setCode("1");
                resultObject.setMsg("活动不存在");
            }

            if (2 == e.getSign()) {
                resultObject.setCode("2");
                resultObject.setMsg("不合规的参数");
            }
            if (3 == e.getSign()) {
                resultObject.setCode("3");
                resultObject.setMsg("积分不足");
            }

            if (4 == e.getSign()) {
                resultObject.setCode("4");
                resultObject.setMsg("禁用活动");
            }

            if (5 == e.getSign()) {
                resultObject.setCode("5");
                resultObject.setMsg("活动未开始");
            }

            if (6 == e.getSign()) {
                resultObject.setCode("6");
                resultObject.setMsg("活动结束");
            }

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            resultObject.setCode("0");
            resultObject.setMsg("系统错误");
        }
        return resultObject;
    }

    /**
     * 根据活动查询奖品信息，取代旧接口：listPrize.action
     */
    @GetMapping(action + "listActivityPrize.action")
    public Object listPrize(HttpServletRequest request, String activityId) {
        ResultObject resultObject = readSecurityContextFromSession(new ResultObject());
        if (!"0".equals(resultObject.getCode())) {
            return resultObject;
        }

        List<ActivityPrize> lotteryPrizes = activityPrizeService.listByActivityId(activityId, 1);

        resultObject.setData(lotteryPrizes);
        return resultObject;
    }

    private ResultObject check(String loginPartyId, String activityId) {
        ResultObject resultObject = new ResultObject();

        Seller seller = sellerService.getSeller(loginPartyId);
        if (Objects.isNull(seller)) {
            resultObject.setCode("7");
            resultObject.setMsg("未认证商家");
            return resultObject;
        }

        Kyc kyc = kycService.get(loginPartyId);
        if (Objects.isNull(kyc) || 2 != kyc.getStatus()) {
            resultObject.setCode("7");
            resultObject.setMsg("未认证商家");
            return resultObject;
        }
        return null;
    }
}
