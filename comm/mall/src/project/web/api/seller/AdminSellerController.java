package project.web.api.seller;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.emoji.EmojiUtil;
import com.alibaba.fastjson.JSONObject;
import kernel.exception.BusinessException;
import kernel.util.DateUtils;
import kernel.util.MyEmojiUtil;
import kernel.util.StringUtils;
import kernel.web.BaseAction;
import kernel.web.ResultObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import project.blockchain.RechargeBlockchain;
import project.blockchain.RechargeBlockchainService;
import project.data.job.HandleObject;
import project.log.MoneyFreeze;
import project.log.MoneyFreezeService;
import project.log.MoneyLogService;
import project.mall.evaluation.EvaluationService;
import project.mall.goods.SellerGoodsService;
import project.mall.goods.model.SellerGoods;
import project.mall.goods.model.ShoppingCart;
import project.mall.seller.FocusSellerService;
import project.mall.seller.SellerCreditService;
import project.mall.seller.SellerService;
import project.mall.seller.model.Seller;
import project.mall.seller.model.SellerCredit;
import project.mall.seller.model.SellerVo;
import project.party.PartyService;
import project.party.UserMetricsService;
import project.party.model.Party;
import project.party.model.UserMetrics;
import project.party.model.UserRecom;
import project.party.recom.UserRecomService;
import project.redis.RedisHandler;
import project.syspara.Syspara;
import project.user.kyc.Kyc;
import project.user.kyc.KycService;
import project.wallet.WalletService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * 商户后台信息管理
 */
@RestController
@CrossOrigin
public class AdminSellerController extends BaseAction {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final String action = "/seller/seller!";

    @Resource
    protected RedisHandler redisHandler;
    @Resource
    protected SellerService sellerService;
    @Resource
    protected PartyService partyService;
    @Resource
    protected UserRecomService userRecomService;
    @Resource
    protected SellerGoodsService sellerGoodsService;
    @Resource
    protected EvaluationService evaluationService;
    @Resource
    protected KycService kycService;
    @Resource
    FocusSellerService focusSellerService;

    @Resource
    protected SellerCreditService sellerCreditService;
    @Resource
    protected WalletService walletService;
    @Resource
    protected MoneyLogService moneyLogService;
    @Resource
    protected MoneyFreezeService moneyFreezeService;
    @Resource
    protected RechargeBlockchainService rechargeBlockchainService;
    @Resource
    protected UserMetricsService userMetricsService;

    @PostMapping(action + "update.action")
    public Object update(HttpServletRequest request) {
        ResultObject resultObject = new ResultObject();
//        String sellerId = request.getParameter("sellerId");
        String partyId = this.getLoginPartyId();
        String name = request.getParameter("name");
        String avatar = request.getParameter("avatar");
        String contact = request.getParameter("contact");
        String shopPhone = request.getParameter("shopPhone");
        String shopRemark = request.getParameter("shopRemark");
        String shopAddress = request.getParameter("shopAddress");
        String banner1 = request.getParameter("banner1");
        String banner2 = request.getParameter("banner2");
        String banner3 = request.getParameter("banner3");
        String facebook = request.getParameter("facebook");
        String instagram = request.getParameter("instagram");
        String twitter = request.getParameter("twitter");
        String google = request.getParameter("google");
        String youtube = request.getParameter("youtube");
        String imInitMessage = request.getParameter("imInitMessage");
        String imDefaultReply = request.getParameter("imDefaultReply");
        logger.info("=============> 修改商铺配置接口 /seller/seller!update.action shopRemark:{}", shopRemark);

        Seller seller = sellerService.getSeller(partyId);
        if (seller == null) {
            resultObject.setCode("1");
            resultObject.setMsg("修改对象不存在");
            return resultObject;
        }

        Seller existSeller = sellerService.getByName(name);
        if (existSeller != null && !existSeller.getId().toString().equals(seller.getId().toString())) {
            resultObject.setCode("1");
            resultObject.setMsg("已存在同名店铺");
            return resultObject;
        }

        if (name != null) seller.setName(name);
        if (avatar != null) seller.setAvatar(avatar);
        if (contact != null) seller.setContact(contact);
        if (shopPhone != null) seller.setShopPhone(shopPhone);
//        if (shopRemark!=null){
//            if (EmojiUtil.containsEmoji(shopRemark)) {
//                shopRemark=MyEmojiUtil.gbEncoding(shopRemark);
//            }
//            seller.setShopRemark(shopRemark);
//        }
        if (shopRemark != null) seller.setShopRemark(shopRemark);
        if (shopAddress != null) seller.setShopAddress(shopAddress);
        if (banner1 != null) seller.setBanner1(banner1);
        if (banner2 != null) seller.setBanner2(banner2);
        if (banner3 != null) seller.setBanner3(banner3);
        if (facebook != null) seller.setFacebook(facebook);
        if (instagram != null) seller.setInstagram(instagram);
        if (twitter != null) seller.setTwitter(twitter);
        if (google != null) seller.setGoogle(google);
        if (youtube != null) seller.setYoutube(youtube);
        if (StrUtil.isNotBlank(imDefaultReply)) {
            seller.setImInitMessage(imDefaultReply);
        }
        if (StrUtil.isNotBlank(imInitMessage)) {
            seller.setImInitMessage(imInitMessage);
        }else {
            seller.setImInitMessage(null);
        }
        try {
            sellerService.updateSeller(seller);
        } catch (BusinessException e) {
            logger.error("修改商铺配置接口 /seller/seller!update.action 报错", e);
            resultObject.setCode(String.valueOf(e.getSign()));
            resultObject.setMsg(e.getMessage());
            return resultObject;
        } catch (Exception e1) {
            e1.printStackTrace();
            resultObject.setCode("1");
            resultObject.setMsg("修改失败");
            return resultObject;
        }

        JSONObject object = new JSONObject();
//        object.put("pageInfo", null);
//        object.put("pageList", null);
        resultObject.setData(seller);
        resultObject.setMsg("操作成功");
        return resultObject;
    }

    @PostMapping(action + "info.action")
    public Object sellerInfo(HttpServletRequest request) {
        ResultObject resultObject = readSecurityContextFromSession(new ResultObject());
        if (!"0".equals(resultObject.getCode())) {
            return resultObject;
        }
        String sellerId = request.getParameter("sellerId");
        if (StrUtil.isEmpty(sellerId)) {
            sellerId = this.getLoginPartyId();
        }
        Party partyEntity = partyService.getById(sellerId);
        if (partyEntity == null) {
            resultObject.setCode("-1");
            resultObject.setMsg("用户不存在");
            return resultObject;
        }

        // 值为 GUEST 代表为演示账号
        String roleName = partyEntity.getRolename();
        // 推荐记录
        UserRecom recomEntity = userRecomService.findByPartyId(sellerId);
        String recomUserName = "";
        if (recomEntity != null) {
            // 推荐用户记录
            Party recomPartyEntity = partyService.getById(recomEntity.getReco_id());
            if (recomPartyEntity != null) {
                recomUserName = recomPartyEntity.getUsername();
            }
        }

        Kyc kyc = kycService.get(sellerId);
        Seller seller = sellerService.getSeller(sellerId);
        SellerVo sellerVo = new SellerVo();
        if (seller == null) {
            seller = new Seller();
            seller.setId(sellerId);
            seller.setName("");
            seller.setAvatar("");
            seller.setCreateTime(new Date());
            seller.setRecTime(0L);
            seller.setStatus(0);
            seller.setCreditScore(0);

            //店铺流量默认值
            seller.setAutoStart(0);
            seller.setAutoEnd(5);
            seller.setBaseTraffic(1);
            seller.setAutoValid(1);
            // 设置商家默认banner
            seller.setBanner1("");
            seller.setBanner2("");
            seller.setBanner3("");
            try {
                sellerService.saveSeller(seller);
            } catch (Exception e) {
                // 高并发产生主键冲突
                boolean isDuplicateError = false;
                int checkTimes = 3;
                Throwable tmpErr = e;
                while (checkTimes-- > 0) {
                    if (tmpErr == null) {
                        break;
                    }
                    if (tmpErr.getMessage() != null && tmpErr.getMessage().contains("Duplicate entry")) {
                        isDuplicateError = true;
                        seller = sellerService.getSeller(sellerId);
                        break;
                    }
                    tmpErr = tmpErr.getCause();
                }
                if (!isDuplicateError) {
                    throw e;
                }
            }
        }

        BeanUtils.copyProperties(seller, sellerVo);
        sellerVo.setRoleName(roleName);
        sellerVo.setRecomUserName(recomUserName);
        if (seller != null && StrUtil.isNotEmpty(seller.getName()) && StrUtil.isNotEmpty(seller.getAvatar())) {
            sellerVo.setSellerSettingFlag("1");
        } else {
            sellerVo.setSellerSettingFlag("0");
        }
        if (kyc != null && kyc.getStatus() == 2) {
            sellerVo.setSellerKycFlag("1");
        } else {
            sellerVo.setSellerKycFlag("0");
        }
        BeanUtils.copyProperties(seller, sellerVo);
        Long goodsNum = sellerGoodsService.getGoodsNumBySellerId(seller.getId().toString());
        if (goodsNum != null && goodsNum > 0) {
            sellerVo.setOnShelvesFlag("1");
        } else {
            sellerVo.setOnShelvesFlag("0");
        }
//        店铺详情解码处理
//        sellerVo.setShopRemark(MyEmojiUtil.decodeUnicode(sellerVo.getShopRemark()));
        sellerVo.setHighOpinion(evaluationService.getHighOpinionBySellerId(sellerId));
        sellerVo.setSoldNum((seller.getSoldNum() == null ? 0L : seller.getSoldNum() )+ (sellerGoodsService.getSoldNumBySellerId(sellerId) == null ? 0L : sellerGoodsService.getSoldNumBySellerId(sellerId)));
        sellerVo.setViewsNum(sellerGoodsService.getViewsNumBySellerId(sellerId));
        sellerVo.setSellerGoodsNum(goodsNum);
        sellerVo.setFocusNum(focusSellerService.getFocusCount(seller.getId().toString()));
        UserMetrics userMetrics = userMetricsService.getByPartyId(sellerId);
        sellerVo.setStoreMoneyRechargeAcc(Objects.nonNull(userMetrics)?userMetrics.getStoreMoneyRechargeAcc():0d);

        resultObject.setData(sellerVo);
        return resultObject;
    }

    /**
     * 修改店铺信誉分
     *
     * @param request
     * @return
     */
    @PostMapping(action + "credit.set")
    public Object setSellerCredit(HttpServletRequest request) {
        ResultObject resultObject = readSecurityContextFromSession(new ResultObject());
        if (!"0".equals(resultObject.getCode())) {
            return resultObject;
        }
        String partyId = this.getLoginPartyId();

        //ResultObject resultObject = new ResultObject();

        String sellerId = request.getParameter("sellerId");
        String scoreStr = request.getParameter("score");
        String reason = request.getParameter("reason");

        int score = Integer.parseInt(scoreStr);
        Integer eventType = 1;
        String eventKey = partyId + "_" + System.currentTimeMillis();

        SellerCredit accCredit = sellerCreditService.addCredit(sellerId, score, eventType, eventKey, reason);

        return resultObject;
    }

//    /**
//     * 解冻冻结旧记录
//     *
//     * @param request
//     * @return
//     */
//    @PostMapping(action + "unfreezeSellerMoney.action")
//    public Object unfreezeSellerMoney(HttpServletRequest request) {
//        ResultObject resultObject = new ResultObject();
//
//        ArrayList<MoneyFreeze> moneyFreezeList = new ArrayList<>();
//        try {
//
//            List<MoneyFreeze> moneyFreezes = moneyFreezeService.listPendingFreezeRecords(300);
//            if (CollectionUtil.isEmpty(moneyFreezes)){
//                throw new BusinessException("未有修复的记录");
//            }
//
//            for (MoneyFreeze moneyFreeze : moneyFreezes) {
//                long day = DateUtils.calcTimeBetweenInDay(moneyFreeze.getBeginTime(), moneyFreeze.getEndTime());
//                if (day < 10000){
//                    moneyFreezeService.updateAutoUnFreezeSeller(moneyFreeze.getId().toString(),"0");
//                    moneyFreezeList.add(moneyFreeze);
//                }
//            }
//        } catch (BusinessException e) {
//            resultObject.setCode("1");
//            resultObject.setMsg(e.getMessage());
//            logger.error(e.getMessage());
//        } catch (Exception e) {
//            resultObject.setCode("1");
//            logger.error("解冻旧记录错误，报错信息为：" + e);
//            return resultObject;
//        }
//        resultObject.setData(moneyFreezeList);
//        return resultObject;
//    }

    /**
     * 设置商铺的虚假销量
     *
     * @param request
     * @return
     */
    @PostMapping(action + "soldNum.set")
    public Object refreshFakeSoldNum(HttpServletRequest request) {
        ResultObject resultObject = readSecurityContextFromSession(new ResultObject());
        if (!"0".equals(resultObject.getCode())) {
            return resultObject;
        }
        String operatorId = this.getLoginPartyId();

        String sellerId = request.getParameter("sellerId");
        String fakeSoldNumStr = request.getParameter("fakeSoldNum");

        int fakeSoldNum = Integer.parseInt(fakeSoldNumStr);
        Seller seller = sellerService.getSeller(sellerId);
        if (seller == null) {
            resultObject.setCode("1");
            resultObject.setMsg("商铺记录不存在");
            return resultObject;
        }
        Integer oldFakeSoldNum = seller.getFakeSoldNum();
        if (oldFakeSoldNum == null) {
            oldFakeSoldNum = 0;
        }
        if (oldFakeSoldNum > fakeSoldNum) {
            resultObject.setCode("1");
            resultObject.setMsg("商铺的虚假销量值不能比以前的小，旧的虚假销量值为:" + oldFakeSoldNum);
            return resultObject;
        }

        sellerService.updateFakeSoldNum(sellerId, fakeSoldNum);
        return resultObject;
    }

    /**
     * 获取商铺的销量信息
     *
     * @param request
     * @return
     */
    @GetMapping(action + "soldNum.get")
    public Object getFakeSoldNum(HttpServletRequest request) {
        ResultObject resultObject = readSecurityContextFromSession(new ResultObject());
        if (!"0".equals(resultObject.getCode())) {
            return resultObject;
        }

        String sellerId = request.getParameter("sellerId");
        Seller seller = sellerService.getSeller(sellerId);
        if (seller == null) {
            resultObject.setCode("1");
            resultObject.setMsg("商铺记录不存在");
            return resultObject;
        }

        Map<String, Object> soldInfo = new HashMap<>();
        Long realSoldNum = sellerGoodsService.getSoldNumBySellerId(sellerId);
        soldInfo.put("realSoldNum", realSoldNum);
        soldInfo.put("fakeSoldNum", seller.getFakeSoldNum() == null ? 0 : seller.getFakeSoldNum());

        resultObject.setData(soldInfo);
        return resultObject;
    }

    @PostMapping(action + "receiveBonus.action")
    public Object receiveBonus(HttpServletRequest request) {
        ResultObject resultObject = new ResultObject();
        resultObject = this.readSecurityContextFromSession(resultObject);
        if (!"0".equals(resultObject.getCode())) {
            return resultObject;
        }
        String sellerId = request.getParameter("sellerId");
        if (StrUtil.isEmpty(sellerId)) {
            sellerId = this.getLoginPartyId();
        }
        Seller seller = sellerService.getSeller(sellerId);
        if (null == seller || null == seller.getStatus() || seller.getStatus() == 0) {
            resultObject.setCode("1");
            resultObject.setMsg("暂无店铺");
            resultObject.setData(seller);
            return resultObject;
        }
        if (seller.getRechargeBonusStatus() != 1) {
            resultObject.setCode("1");
            resultObject.setMsg("不满足领取条件");
            return resultObject;
        }
        String username = partyService.cachePartyBy(sellerId, true).getUsername();
        UserRecom userRecom = userRecomService.findByPartyId(sellerId);
        String recomName = null;
        if (Objects.nonNull(userRecom)) {
            Party party = partyService.cachePartyBy(userRecom.getPartyId(), true);
            recomName = party.getUsername();
        }

        try {
            this.sellerService.updateReceiveBonus(seller, recomName);
        } catch (BusinessException e) {
            resultObject.setCode("1");
            resultObject.setMsg(e.getMessage());
            logger.error(e.getMessage());
        } catch (Exception e) {
            resultObject.setCode("1");
            logger.error("领取首充礼金错误，报错信息为：" + e.getMessage());
            return resultObject;
        }
        return resultObject;
    }

    /**
     * 领取拉人奖励接口
     *
     * @param request
     * @return
     */
    @PostMapping(action + "receiveInviteRewards.action")
    public Object receiveInviteRewards(HttpServletRequest request) {
        ResultObject resultObject = new ResultObject();
        resultObject = this.readSecurityContextFromSession(resultObject);
        if (!"0".equals(resultObject.getCode())) {
            return resultObject;
        }
        String sellerId = request.getParameter("sellerId");
        if (StrUtil.isEmpty(sellerId)) {
            sellerId = this.getLoginPartyId();
        }
        Seller seller = sellerService.getSeller(sellerId);
        if (null == seller || null == seller.getStatus() || seller.getStatus() == 0) {
            resultObject.setCode("1");
            resultObject.setMsg("暂无店铺");
            resultObject.setData(seller);
            return resultObject;
        }
        if (seller.getInviteAmountReward() <= 0) {
            resultObject.setCode("1");
            resultObject.setMsg("不满足领取条件");
            return resultObject;
        }
        String username = partyService.cachePartyBy(sellerId, true).getUsername();
        UserRecom userRecom = userRecomService.findByPartyId(sellerId);
        String recomName = null;
        if (Objects.nonNull(userRecom)) {
            Party party = partyService.cachePartyBy(userRecom.getPartyId(), true);
            recomName = party.getUsername();
        }

        try {
            this.sellerService.updateInviteReceiveRwards(seller, username, recomName);
        } catch (BusinessException e) {
            resultObject.setCode("1");
            resultObject.setMsg(e.getMessage());
            logger.error(e.getMessage());
        } catch (Exception e) {
            resultObject.setCode("1");
            logger.error("领取邀请奖励错误，报错信息为：" + e);
            return resultObject;
        }
        return resultObject;
    }

    @PostMapping(action + "beforeReceiveBonus.action")
    public Object beforeReceiveBonus(HttpServletRequest request) {
        ResultObject resultObject = new ResultObject();
        resultObject = this.readSecurityContextFromSession(resultObject);
        if (!"0".equals(resultObject.getCode())) {
            return resultObject;
        }
        String sellerId = request.getParameter("sellerId");
        if (StrUtil.isEmpty(sellerId)) {
            sellerId = this.getLoginPartyId();
        }
        Seller seller = sellerService.getSeller(sellerId);
        if (null == seller) {
            return resultObject;
        }
        List<RechargeBlockchain> rechargeBlockchains = rechargeBlockchainService.findSuccessByPartyId(seller.getId());
        try {
            if (seller.getRechargeBonusStatus() == 0 && Objects.nonNull(rechargeBlockchains)
                    && rechargeBlockchains.size() >= 1) {//充值成功不止一条并且这个状态为初始值的更新为3
                seller.setRechargeBonusStatus(3);
                this.sellerService.updateSeller(seller);
            }
        } catch (Exception e) {
            resultObject.setCode("1");
            logger.error(e.getMessage());
            return resultObject;
        }
        return resultObject;
    }
}
