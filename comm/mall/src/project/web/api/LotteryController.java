//package project.web.api;
//
//import cn.hutool.core.util.StrUtil;
//import com.alibaba.fastjson.JSONObject;
//import kernel.exception.BusinessException;
//import kernel.util.PageInfo;
//import kernel.web.BaseAction;
//import kernel.web.Page;
//import kernel.web.ResultObject;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.collections.CollectionUtils;
//import org.springframework.web.bind.annotation.CrossOrigin;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RestController;
//import project.mall.activity.model.lottery.ActivityUserPoints;
//import project.mall.activity.service.ActivityUserPointsService;
//import project.mall.activity.service.lottery.LotteryPrizeService;
//import project.mall.activity.service.lottery.LotteryRecordService;
//import project.mall.activity.service.lottery.LotteryService;
//import project.mall.activity.dto.lottery.LotteryDTO;
//import project.mall.activity.dto.lottery.LotteryRecordSumDTO;
//import project.mall.activity.model.lottery.Lottery;
//import project.mall.activity.model.lottery.LotteryPrize;
//import project.mall.seller.SellerService;
//import project.mall.seller.model.Seller;
//import project.party.PartyService;
//import project.party.model.Party;
//import project.party.model.UserRecom;
//import project.party.recom.UserRecomService;
//import project.user.kyc.Kyc;
//import project.user.kyc.KycService;
//import project.web.api.dto.ActivityPointsDTO;
//
//import javax.annotation.Resource;
//import javax.servlet.http.HttpServletRequest;
//import java.util.Date;
//import java.util.List;
//import java.util.Objects;
//
//@Slf4j
//@RestController
//@CrossOrigin
//public class LotteryController extends BaseAction {
//
//    @Resource
//    private LotteryRecordService lotteryRecordService;
//
//    @Resource
//    private LotteryPrizeService lotteryPrizeService;
//
//    @Resource
//    private LotteryService lotteryService;
//
//    @Resource
//    private ActivityUserPointsService activityUserPointsService;
//
//    @Resource
//    private PartyService partyService;
//
//    @Resource
//    private UserRecomService userRecomService;
//
//    @Resource
//    private SellerService sellerService;
//
//    @Resource
//    private KycService kycService;
//
//
//    private final String action = "/api/seller/lottery!";
//
//    @PostMapping(action + "count.action")
//    public ResultObject count(String lotteryId) {
//
//        ResultObject resultObject = readSecurityContextFromSession(new ResultObject());
//        if (!"0".equals(resultObject.getCode())) {
//            return resultObject;
//        }
//        String partyId = getLoginPartyId();
//        LotteryRecordSumDTO sumRecord = this.lotteryRecordService.getSumRecord(partyId, lotteryId);
//        resultObject.setData(sumRecord);
//        return resultObject;
//    }
//
//    /**
//     * 根据连接查询活动详情
//     *
//     * @param link
//     * @return
//     */
//    @PostMapping(action + "getByLink.action")
//    public ResultObject getByLink(String link) {
//        ResultObject resultObject = new ResultObject();
//        LotteryDTO lotteryDTO = this.lotteryService.getByLink(link);
//        resultObject.setData(lotteryDTO);
//        return resultObject;
//    }
//
//    /**
//     * 根据活动ID查询用户积分
//     *
//     * @param lotteryId
//     * @return
//     */
//    @PostMapping(action + "getPoints.action")
//    public ResultObject getPoints(String lotteryId) {
//        ResultObject resultObject = new ResultObject();
//        String partyId = this.getLoginPartyId();
//        ActivityUserPoints userPoints = activityUserPointsService.getByActivityId(lotteryId, partyId);
//        resultObject.setData(userPoints);
//        return resultObject;
//    }
//
//    /**
//     * 根据活动ID计算邀请人数，以及积分
//     *
//     * @param lotteryId
//     * @return
//     */
//    @PostMapping(action + "getCountPoints.action")
//    public ResultObject getCountPoints(String lotteryId) {
//
//        ResultObject resultObject = new ResultObject();
//
//        String loginPartyId = getLoginPartyId();
//        Lottery lottery = lotteryService.findById(lotteryId);
//        List<String> childrens = userRecomService.findChildren(loginPartyId);
//
//        ActivityPointsDTO activityPointsDTO = new ActivityPointsDTO();
//        activityPointsDTO.setNumber(0);
//        activityPointsDTO.setPoints(0);
//
//        if (CollectionUtils.isNotEmpty(childrens)) {
//            activityPointsDTO.setNumber(childrens.size());
//            activityPointsDTO.setPoints(lottery.getInvitePoints() * childrens.size());
//        }
//
//        resultObject.setData(activityPointsDTO);
//        return resultObject;
//    }
//
//    /**
//     * 领取奖金
//     *
//     * @param prizeType
//     * @return
//     */
//    @PostMapping(action + "receive.action")
//    public Object receive(Integer prizeType, String lotteryId) {
//
//        ResultObject resultObject = readSecurityContextFromSession(new ResultObject());
//        if (!"0".equals(resultObject.getCode())) {
//            return resultObject;
//        }
//        String partyId = this.getLoginPartyId();
//        String username = partyService.cachePartyBy(partyId, true).getUsername();
//        UserRecom userRecom = userRecomService.findByPartyId(partyId);
//        String recomName = null;
//        if (Objects.nonNull(userRecom)) {
//            Party party = partyService.cachePartyBy(userRecom.getPartyId(), true);
//            recomName = party.getUsername();
//        }
//
//        String sellerName = sellerService.getSeller(partyId).getName();
//
//        this.lotteryRecordService.updateByApplyReceivePrizes(prizeType, partyId, username, recomName, sellerName, lotteryId);
//        resultObject.setCode("0");
//        resultObject.setMsg("操作成功");
//        return resultObject;
//    }
//
//    /**
//     * 我的中奖列表
//     *
//     * @param request
//     * @return
//     */
//    @PostMapping(action + "pageRecordList.action")
//    public ResultObject listRecord(HttpServletRequest request) {
//
//        ResultObject resultObject = readSecurityContextFromSession(new ResultObject());
//        if (!"0".equals(resultObject.getCode())) {
//            return resultObject;
//        }
//        String partyId = getLoginPartyId();
//        String activityId = request.getParameter("activityId");
//        if (StrUtil.isBlank(activityId)) {
//            throw new BusinessException("未指定活动ID");
//        }
//
//        PageInfo pageInfo = getPageInfo(request);
//
//        Page mallPageInfo = this.lotteryRecordService.pagedByPartyId(activityId, partyId, pageInfo.getPageNum(), pageInfo.getPageSize());
//        pageInfo.setTotalElements(mallPageInfo.getTotalElements());
//        JSONObject object = new JSONObject();
//        object.put("pageInfo", pageInfo);
//        object.put("pageList", mallPageInfo.getElements());
//        resultObject.setData(object);
//        return resultObject;
//    }
//
//    /**
//     * 抽奖代码
//     */
//    @PostMapping(action + "draw.action")
//    public ResultObject doDraw(HttpServletRequest request, String lotteryId) {
//
//        ResultObject resultObject = readSecurityContextFromSession(new ResultObject());
//        if (!"0".equals(resultObject.getCode())) {
//            return resultObject;
//        }
//
//        String language = getLanguage(request);
//        String loginPartyId = getLoginPartyId();
//        String username = partyService.cachePartyBy(loginPartyId, true).getUsername();
//        UserRecom userRecom = userRecomService.findByPartyId(loginPartyId);
//        String recomName = null;
//        if (Objects.nonNull(userRecom)) {
//            Party party = partyService.cachePartyBy(userRecom.getPartyId(), true);
//            recomName = party.getUsername();
//        }
//
//        ResultObject check = this.check(loginPartyId, lotteryId);
//
//        if (Objects.nonNull(check)) {
//            return check;
//        }
//
//        Seller seller = sellerService.getSeller(loginPartyId);
//
//        LotteryPrize draw = lotteryPrizeService.updateDraw(lotteryId, language, loginPartyId, username, recomName, seller.getName());
//        resultObject.setData(draw);
//        return resultObject;
//    }
//
//    /**
//     * 根据活动查询奖品信息
//     */
//    @PostMapping(action + "listPrize.action")
//    public ResultObject listPrize(String lotteryId) {
//        ResultObject resultObject = readSecurityContextFromSession(new ResultObject());
//        if (!"0".equals(resultObject.getCode())) {
//            return resultObject;
//        }
//        List<LotteryPrize> lotteryPrizes = lotteryPrizeService.listLotteryId(lotteryId);
//
//        resultObject.setData(lotteryPrizes);
//        return resultObject;
//    }
//
//    private ResultObject check(String loginPartyId, String lotteryId) {
//
//        ResultObject resultObject = new ResultObject();
//
//        ActivityUserPoints activityUserPoints = activityUserPointsService.getByActivityId(lotteryId, loginPartyId);
//
//        Seller seller = sellerService.getSeller(loginPartyId);
//
//        if (Objects.isNull(seller)) {
//            resultObject.setCode("1");
//            resultObject.setMsg("未认证商家");
//            return resultObject;
//        }
//
//
//        Kyc kyc = kycService.get(loginPartyId);
//
//        if (Objects.isNull(kyc) || 2 != kyc.getStatus()) {
//            resultObject.setCode("2");
//            resultObject.setMsg("未认证商家");
//            return resultObject;
//        }
//
//        Date now = new Date();
//
//        Lottery lottery = lotteryService.findById(lotteryId);
//
//        if (now.before(lottery.getStartTime())) {
//
//            resultObject.setCode("3");
//            resultObject.setMsg("活动未开始");
//            return resultObject;
//        }
//
//        if (now.after(lottery.getEndTime())) {
//            resultObject.setCode("4");
//            resultObject.setMsg("活动结束");
//            return resultObject;
//        }
//
//        if (lottery.getState() == 0) {
//            resultObject.setCode("5");
//            resultObject.setMsg("禁用活动");
//            return resultObject;
//        }
//
//        if (Objects.isNull(activityUserPoints) || activityUserPoints.getPoints() <= 0
//                || activityUserPoints.getPoints() < lottery.getPointsToNumber()) {
//            resultObject.setCode("6");
//            resultObject.setMsg("积分不足");
//            return resultObject;
//        }
//
//        return null;
//    }
//
//}
