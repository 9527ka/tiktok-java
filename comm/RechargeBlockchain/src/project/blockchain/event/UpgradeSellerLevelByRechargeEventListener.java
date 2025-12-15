package project.blockchain.event;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import kernel.constants.ValueTypeEnum;
import kernel.util.Arith;
import kernel.util.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import project.Constants;
import project.RedisKeys;
import project.blockchain.RechargeBlockchainService;
import project.blockchain.event.message.RechargeSuccessEvent;
import project.blockchain.event.model.RechargeInfo;
import project.blockchain.internal.FundChangeService;
import project.invest.platform.Platfrom;
import project.log.MoneyLog;
import project.mall.seller.MallLevelService;
import project.mall.seller.SellerService;
import project.mall.seller.constant.UpgradeMallLevelCondParamTypeEnum;
import project.mall.seller.dto.MallLevelCondExpr;
import project.mall.seller.dto.MallLevelDTO;
import project.mall.seller.model.MallLevel;
import project.mall.seller.model.Seller;
import project.mall.utils.PlatformNameEnum;
import project.party.PartyService;
import project.party.UserMetricsService;
import project.party.model.Party;
import project.party.model.UserMetrics;
import project.party.model.UserRecom;
import project.party.recom.UserRecomService;
import project.redis.RedisHandler;
import project.syspara.SysParaCode;
import project.syspara.Syspara;
import project.syspara.SysparaService;
import project.user.kyc.Kyc;
import project.user.kyc.KycService;
import project.wallet.Wallet;
import project.wallet.WalletLog;
import project.wallet.WalletLogService;
import project.wallet.WalletService;

import java.io.Serializable;
import java.util.*;

/**
 * 用户充值审核通过后，有一些关联业务会同步受到影响
 * 目前可见受影响的业务数据：
 * 1. 检查当前充值用户是否满足升级指标：
 * 2. 检查当前充值用户直接上级代理是否满足升级指标
 *
 */
public class UpgradeSellerLevelByRechargeEventListener implements ApplicationListener<RechargeSuccessEvent> {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private WalletLogService walletLogService;

    private SellerService sellerService;

    private PartyService partyService;

    private UserRecomService userRecomService;

    private MallLevelService mallLevelService;

    private SysparaService sysparaService;

    private FundChangeService fundChangeService;

    private RedisHandler redisHandler;

    private KycService kycService;

    @Override
    public void onApplicationEvent(RechargeSuccessEvent event) {
//        2024-01-21 需求argos3 关闭自动升级
        String platformName = sysparaService.find("platform_name").getValue();
        if (PlatformNameEnum.ARGOS3.getDescription().equals(platformName)) {
            return;
        }

        RechargeInfo changeInfo = event.getRechargeInfo();
        logger.info("监听到用户成功充值事件，将判断当前用户或相关用户等级是否满足提升指标:" + JSON.toJSONString(changeInfo));

        try {
            Date now = new Date();
            redisHandler.zadd(RedisKeys.RECHARGE_PASS_TIME, now.getTime(), changeInfo.getApplyUserId());

            Seller sellerEntity = sellerService.getSeller(changeInfo.getApplyUserId());
            Kyc kyc = kycService.get(changeInfo.getApplyUserId());

            if (null == kyc && kyc.getStatus() != 2) {
                // 不是商家类型用户，不做后续处理
                return;
            }

            Party currentParty = this.partyService.getById(changeInfo.getApplyUserId());
            // 上级推荐人
            String parentPartyId = "";
            UserRecom firstRecom = userRecomService.findByPartyId(currentParty.getId().toString());
            if (firstRecom != null) {
                Party parentParty = this.partyService.getById(firstRecom.getReco_id().toString());
                if (parentParty != null) {
                    parentPartyId = parentParty.getId().toString();
                }
            }

            // 提取用于店铺升级业务的有效充值用户的充值金额临界值
            double limitRechargeAmount = 100.0;
            // 提取用于店铺升级业务的计算团队人数充值金额临界值
            double limitRechargeAmountOnTeam = 100.0;
            Syspara syspara = sysparaService.find(SysParaCode.VALID_RECHARGE_AMOUNT_FOR_SELLER_UPGRADE.getCode());
            Syspara sysparaOnTeam = sysparaService.find(SysParaCode.VALID_RECHARGE_AMOUNT_FOR_TEAM_NUM.getCode());
            if (syspara != null) {
                String validRechargeAmountInfo = syspara.getValue().trim();
                if (StrUtil.isNotBlank(validRechargeAmountInfo)) {
                    limitRechargeAmount = Double.parseDouble(validRechargeAmountInfo);
                }
            }
            if (sysparaOnTeam != null) {
                String rechargeAmountOnTeamInfo = sysparaOnTeam.getValue().trim();
                if (StrUtil.isNotBlank(rechargeAmountOnTeamInfo)) {
                    limitRechargeAmountOnTeam = Double.parseDouble(rechargeAmountOnTeamInfo);
                }
            }

            // 对等级集合进行排序，方便升级判断
            List<MallLevel> levelEntityList = this.mallLevelService.listLevel();
            Map<String, Integer> levelSortMap = new HashMap<>();

            levelSortMap.put("C", 1);
            levelSortMap.put("B", 2);
            levelSortMap.put("A", 3);
            levelSortMap.put("S", 4);
            levelSortMap.put("SS", 5);
            levelSortMap.put("SSS", 6);

            CollUtil.sort(levelEntityList, new Comparator<MallLevel>() {
                @Override
                public int compare(MallLevel o1, MallLevel o2) {
                    Integer seq1 = levelSortMap.get(o1.getLevel());
                    Integer seq2 = levelSortMap.get(o2.getLevel());
                    seq1 = seq1 == null ? 0 : seq1;
                    seq2 = seq2 == null ? 0 : seq2;

                    return seq1 - seq2;
                }
            });

            // 处理当前用户的升级
            fundChangeService.upgradeMallLevelProcess(changeInfo.getApplyUserId(), limitRechargeAmount, limitRechargeAmountOnTeam,
                    levelEntityList, levelSortMap,changeInfo.getAmount());


            // =================== 顺别识别当前充值用户的直接上级商家是否满足升级条件 =========================
            if (StrUtil.isBlank(parentPartyId) || Objects.equals(parentPartyId, "0")) {
                logger.info("-------> UpgradeSellerLevelByRechargeEventListener.onApplicationEvent 当前用户:{} 的上级商家不存在，不再处理上级商家的升级判断处理",
                        changeInfo.getApplyUserId());
                return;
            }
            // 处理当前用户的直接上级用户的升级逻辑
            fundChangeService.upgradeMallLevelProcess(parentPartyId, limitRechargeAmount, limitRechargeAmountOnTeam,
                    levelEntityList, levelSortMap,0.0);

        } catch (Exception e) {
            logger.error("用户充值审核通过后，计算更新相关用户的店铺等级处理报错，变更信息为:{} ", JsonUtils.getJsonString(changeInfo), e);
        }

    }


    public void setWalletLogService(WalletLogService walletLogService) {
        this.walletLogService = walletLogService;
    }

    public void setSellerService(SellerService sellerService) {
        this.sellerService = sellerService;
    }

    public void setPartyService(PartyService partyService) {
        this.partyService = partyService;
    }

    public void setUserRecomService(UserRecomService userRecomService) {
        this.userRecomService = userRecomService;
    }

    public void setMallLevelService(MallLevelService mallLevelService) {
        this.mallLevelService = mallLevelService;
    }

    public void setSysparaService(SysparaService sysparaService) {
        this.sysparaService = sysparaService;
    }

    public void setFundChangeService(FundChangeService fundChangeService) {
        this.fundChangeService = fundChangeService;
    }

    public void setRedisHandler(RedisHandler redisHandler) {
        this.redisHandler = redisHandler;
    }

    public void setKycService(KycService kycService) {
        this.kycService = kycService;
    }
}
