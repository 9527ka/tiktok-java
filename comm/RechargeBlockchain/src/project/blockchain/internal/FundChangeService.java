package project.blockchain.internal;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import kernel.util.Arith;
import kernel.util.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.Constants;
import project.log.MoneyLog;
import project.log.MoneyLogService;
import project.mall.seller.SellerService;
import project.mall.seller.constant.UpgradeMallLevelCondParamTypeEnum;
import project.mall.seller.dto.MallLevelCondExpr;
import project.mall.seller.model.MallLevel;
import project.mall.seller.model.Seller;
import project.party.PartyService;
import project.party.model.Party;
import project.party.model.UserRecom;
import project.party.recom.UserRecomService;
import project.wallet.Wallet;
import project.wallet.WalletLog;
import project.wallet.WalletLogService;
import project.wallet.WalletService;

import java.util.*;

//@Service
@Slf4j
public class FundChangeService {

    private WalletService walletService;

    private WalletLogService walletLogService;

    private UserRecomService userRecomService;

    private SellerService sellerService;

    private MoneyLogService moneyLogService;

    private PartyService partyService;

    /**
     * 仅更新团队人数和直属人数
     * @param partyId
     * @param limitRechargeAmount
     * @param limitRechargeAmountOnTeam
     * @param levelEntityList
     * @param levelSortMap
     * @param amount
     */
    @Transactional
    public void updateTeamNumAndChildNumOnly(String partyId,
                                             double limitRechargeAmount,
                                             double limitRechargeAmountOnTeam,
                                             List<MallLevel> levelEntityList,
                                             Map<String, Integer> levelSortMap, double amount){
        Seller sellerEntity = sellerService.getSeller(partyId);
        if (sellerEntity == null) {
            // 不是商家类型用户，不做后续处理
            log.info("-------> FundChangeService.upgradeMallLevelProcess 开始处理当前用户:{} 不存在商铺记录，不处理店铺升级逻辑 ", partyId);
            return;
        }
        // 提取当前用户的有效直属人数
        List<String> rechargePartyIdList = new ArrayList();
        rechargePartyIdList.add(partyId);
        // 当前用户的直接下级用户（不是商家类用户也可以吗？）
        List<String> childrenPartyIdList = userRecomService.findDirectlyChildrens(partyId);
        rechargePartyIdList.addAll(childrenPartyIdList);
        // 批量统计多个用户的累计充值数据
        Map<String, Double> userRechargeAmountMap = walletLogService.getComputeRechargeAmount(rechargePartyIdList, 0.0);

        Map<String, Seller> sellerMap = new HashMap<>();
        List<Seller> sellerList = sellerService.getSellerBatch(rechargePartyIdList);
        for (Seller oneSeller : sellerList) {
            sellerMap.put(oneSeller.getId().toString(), oneSeller);
        }

        double currentUserRechargeAmount = amount;
        int validRechargeUserCount = 0;
        for (String onePartyId : userRechargeAmountMap.keySet()) {
            if (!sellerMap.containsKey(onePartyId)) {
                // 不是商家用户，跳过
                log.info("-------> FundChangeService.upgradeMallLevelProcess 当前用户:{} 不是商家用户，跳过...", onePartyId);
                continue;
            }
            if (Objects.equals(onePartyId, partyId)) {
//                充值金额已经从 T_WALLET_LOG 中求和得到此处不可重复加入计算
                continue;
            }
            // 统计直接下属商家的有效充值用户数量
            if (userRechargeAmountMap.get(onePartyId) >= limitRechargeAmount) {
                validRechargeUserCount++;
            }
        }
        log.info("-------> FundChangeService.updateTeamNumAndChildNumOnly 开始处理当前用户:{} 的迁移当前店铺的直属人数， validRechargeUserCount:{} ",partyId, validRechargeUserCount);
//        更新下级人数
        sellerEntity.setChildNum(validRechargeUserCount);
        sellerService.updateSeller(sellerEntity);

//        更新团队人数
        if (amount> 0) {
            List<String> recomIds = userRecomService.getParentsToPartyId(partyId);
            for (String recomId : recomIds) {
//                    更新团队人数
                updateTeamNum(recomId,limitRechargeAmountOnTeam);
//                    更新上级店铺的等级
                upgradeMallLevelSingle(recomId,limitRechargeAmount,levelEntityList,levelSortMap,amount);
            }
        }
    }

    @Transactional
    public void upgradeMallLevelProcess(String partyId,
                                        double limitRechargeAmount,
                                        double limitRechargeAmountOnTeam,
                                        List<MallLevel> levelEntityList,
                                        Map<String, Integer> levelSortMap, double amount) {

//        当前用户处理
        upgradeMallLevelSingle(partyId,limitRechargeAmount,levelEntityList,levelSortMap,amount);
            //如果是当前商家充值满足团队充值条件还需要处理该店铺的全部上级的团队人数,以及上级店铺的升级条件判断
            if (amount> 0) {
                List<String> recomIds = userRecomService.getParentsToPartyId(partyId);
                for (String recomId : recomIds) {
//                    更新团队人数
                    updateTeamNum(recomId,limitRechargeAmountOnTeam);
//                    更新上级店铺的等级
                    upgradeMallLevelSingle(recomId,limitRechargeAmount,levelEntityList,levelSortMap,0);
                }
            }

        }





    /**
     * 根据id更新店铺的团队人数
     * @param partyId
     * @param limitRechargeAmountOnTeam
     */
    public void updateTeamNum(String partyId,double limitRechargeAmountOnTeam){
        Seller sellerEntity = sellerService.getSeller(partyId);
        if (sellerEntity == null) {
            // 不是商家类型用户，不更新团队人数
            log.info("-------> FundChangeService.updateTeamNum 开始处理当前用户:{} 不存在商铺记录，不处理店团队人数 ", partyId);
            return;
        }
        //当前用户的所有下级(即团队人数)
        List<String> teamPartyIdList = userRecomService.findChildren(partyId);
        //批量统计多个用户的累计充值数据
        Map<String, Double> teamRechargeAmountMap = walletLogService.getComputeRechargeAmount(teamPartyIdList, 0.0);
        Map<String, Seller> teamSellerMap = new HashMap<>();
        List<Seller> teamSellerList = sellerService.getSellerBatch(teamPartyIdList);
        for (Seller oneSeller : teamSellerList) {
            teamSellerMap.put(oneSeller.getId().toString(), oneSeller);
        }
        int teamValidRechargeUserCount = 0;
        for (String onePartyId : teamRechargeAmountMap.keySet()) {
            Party party = this.partyService.cachePartyBy(onePartyId, true);
            if (!Constants.SECURITY_ROLE_MEMBER.equals(party.getRolename())) {
                // 不是商家用户，跳过
                log.info("-------> FundChangeService.updateTeamNum 当前用户:{} 不是正式用户用户，跳过更新团队人数...", onePartyId);
                continue;
            }
            if (!teamSellerMap.containsKey(onePartyId)) {
                // 不是商家用户，跳过
                log.info("-------> FundChangeService.updateTeamNum 当前用户:{} 不是商家用户，跳过更新团队人数...", onePartyId);
                continue;
            }
            // 统计团队下属全部商家的有效充值用户数量
            if (teamRechargeAmountMap.get(onePartyId) >= limitRechargeAmountOnTeam) {
                teamValidRechargeUserCount++;
            }
        }
        if (teamValidRechargeUserCount!=sellerEntity.getTeamNum()) {
            sellerEntity.setTeamNum(teamValidRechargeUserCount);
            sellerService.updateSeller(sellerEntity);
        }
    }

    /**
     * 根据id更新单个店铺的等级
     */
    public void upgradeMallLevelSingle(String partyId,
                                       double limitRechargeAmount,
                                       List<MallLevel> levelEntityList,
                                       Map<String, Integer> levelSortMap, double amount){

        Seller sellerEntity = sellerService.getSeller(partyId);
        if (sellerEntity == null) {
            // 不是商家类型用户，不做后续处理
            log.info("-------> FundChangeService.upgradeMallLevelProcess 开始处理当前用户:{} 不存在商铺记录，不处理店铺升级逻辑 ", partyId);
            return;
        }
        String currentLevel = StrUtil.trimToEmpty(sellerEntity.getMallLevel());

        // 提取当前用户用于判断能否升级商铺的相关指标数据
        List<String> rechargePartyIdList = new ArrayList();
        rechargePartyIdList.add(partyId);
        // 当前用户的直接下级用户（不是商家类用户也可以吗？）TODO
        List<String> childrenPartyIdList = userRecomService.findDirectlyChildrens(partyId);
        rechargePartyIdList.addAll(childrenPartyIdList);
        // 批量统计多个用户的累计充值数据
        Map<String, Double> userRechargeAmountMap = walletLogService.getComputeRechargeAmount(rechargePartyIdList, 0.0);

        Map<String, Seller> sellerMap = new HashMap<>();
        List<Seller> sellerList = sellerService.getSellerBatch(rechargePartyIdList);
        for (Seller oneSeller : sellerList) {
            sellerMap.put(oneSeller.getId().toString(), oneSeller);
        }

        double currentUserRechargeAmount = amount;
        int validRechargeUserCount = 0;
        for (String onePartyId : userRechargeAmountMap.keySet()) {
            if (!sellerMap.containsKey(onePartyId)) {
                // 不是商家用户，跳过
                log.info("-------> FundChangeService.upgradeMallLevelProcess 当前用户:{} 不是商家用户，跳过...", onePartyId);
                continue;
            }
            if (Objects.equals(onePartyId, partyId)) {
//                currentUserRechargeAmount = Arith.add(userRechargeAmountMap.get(partyId),currentUserRechargeAmount);
//                充值金额已经从 T_WALLET_LOG 中求和得到此处不可重复加入计算
                currentUserRechargeAmount = Arith.add(userRechargeAmountMap.get(partyId),0.0D);
                continue;
            }
            // 统计直接下属商家的有效充值用户数量
            if (userRechargeAmountMap.get(onePartyId) >= limitRechargeAmount) {
                validRechargeUserCount++;
            }
        }

        log.info("-------> FundChangeService.upgradeMallLevelProcess 开始处理当前用户:{} 的店铺升级判断逻辑，currentUserRechargeAmount:{}, validRechargeUserCount:{} ",
                partyId, currentUserRechargeAmount, validRechargeUserCount);

        Integer currentLevelIdx = levelSortMap.get(currentLevel);
        currentLevelIdx = currentLevelIdx == null ? 0 : currentLevelIdx;
        for (MallLevel oneLevelEntity : levelEntityList) {
            Integer tmpLevelIdx = levelSortMap.get(oneLevelEntity.getLevel().trim());
            if (tmpLevelIdx == null) {
                log.error("-------> FundChangeService.upgradeMallLevelProcess 当前店铺等级配置不能识别:{} ", oneLevelEntity.getLevel());
                return;
            }
            if (currentLevelIdx >= tmpLevelIdx) {
                // 无需再次比较
                log.info("-------> FundChangeService.upgradeMallLevelProcess 当前商家等级:{} 不低于当前等级记录:{}，跳过升级处理，商家记录:{}",
                        currentLevel, oneLevelEntity.getLevel(), partyId);
                continue;
            }

            // 检查是否能够升级
            boolean isOk = checkMeetUpgradeCondition(oneLevelEntity, partyId,
                    currentUserRechargeAmount, validRechargeUserCount,sellerEntity.getTeamNum());

            log.info("-------> FundChangeService.upgradeMallLevelProcess 判断当前商家:{} 升级到:{} 级的结论:{}",
                    partyId, oneLevelEntity.getLevel(), isOk);
            if (isOk) {
                // 满足升级条件
                int awardCash = oneLevelEntity.getUpgradeCash();
                String newMallLevel = oneLevelEntity.getLevel();
                currentLevel = newMallLevel;
                currentLevelIdx = levelSortMap.get(newMallLevel);
                sellerEntity.setMallLevel(newMallLevel);
                sellerEntity.setChildNum(validRechargeUserCount);
                sellerService.updateSeller(sellerEntity);
                log.info("-------> FundChangeService.upgradeMallLevelProcess 将当前商家:{} 升级到:{} 级，开始执行升级奖励...",
                        partyId, oneLevelEntity.getLevel());

                updateUserFundByUpgrade(partyId, awardCash, newMallLevel,currentUserRechargeAmount,validRechargeUserCount,sellerEntity.getTeamNum());
            } else {
                //不满足升级条件但是直属人数有增加时，仍然更新下级人数
                if (sellerEntity.getChildNum()!=validRechargeUserCount) {
                    sellerEntity.setChildNum(validRechargeUserCount);
                    sellerService.updateSeller(sellerEntity);
                }
                // 退出
                break;
            }
            // 用户可能连升多级
        }
    }

    /**
     * 基于相关指标数据，判断当前商户是否满足升级条件.
     *
     * @param oneLevelEntity
     * @param sellerId
     * @param currentUserRechargeAmount
     * @param subValidRechargeUserCount
     * @return
     */
    public boolean checkMeetUpgradeCondition(MallLevel oneLevelEntity, String sellerId, double currentUserRechargeAmount, int subValidRechargeUserCount,int teamNum) {
        // 检查是否能够升级
        boolean isOk = false;
        String cndJson = oneLevelEntity.getCondExpr();
        if (StrUtil.isNotBlank(cndJson)) {
            MallLevelCondExpr cndObj = JsonUtils.json2Object(cndJson, MallLevelCondExpr.class);
            log.info("------> FundChangeService.checkMeetUpgradeCondition 当前商铺等级:{} 的升级条件:{}", oneLevelEntity.getLevel(), cndObj.getExpression());
            List<MallLevelCondExpr.Param> params = cndObj.getParams();

            SpelExpressionParser parser = new SpelExpressionParser();
            EvaluationContext context = new StandardEvaluationContext();
            Expression expression = parser.parseExpression(cndObj.getExpression());

            if (CollectionUtil.isNotEmpty(params)) {
                for (MallLevelCondExpr.Param oneCndParam : params) {
                    UpgradeMallLevelCondParamTypeEnum cndType = UpgradeMallLevelCondParamTypeEnum.codeOf(oneCndParam.getCode().trim());
                    // ValueTypeEnum valueType = ValueTypeEnum.codeOf(oneCndParam.getValueType());

                    if (cndType == UpgradeMallLevelCondParamTypeEnum.RECHARGE_AMOUNT) {
                        context.setVariable(cndType.getCode(), currentUserRechargeAmount);
                    } else if (cndType == UpgradeMallLevelCondParamTypeEnum.POPULARIZE_UNDERLING_NUMBER) {
                        context.setVariable(cndType.getCode(), subValidRechargeUserCount);
                    } else if (cndType == UpgradeMallLevelCondParamTypeEnum.TEAM_NUM){
                        context.setVariable(cndType.getCode(),teamNum);
                    }
                }
            }

            isOk = expression.getValue(context, Boolean.class);
        } else {
            log.info("当前店铺:{} 记录未设置升级条件:{}", sellerId, JsonUtils.bean2Json(oneLevelEntity));
        }

        return isOk;
    }

    @Transactional
    public void updateUserFundByUpgrade(String partyId, double awardCash, String mallLevel, double currentUserRechargeAmount, int validRechargeUserCount, int teamNum) {
        log.info("-------> FundChangeService.updateUserFundByUpgrade 商家:{} 升级后基于规则奖励金额:{}，开始处理...",
                partyId, awardCash);
        if (awardCash <= 0.0) {
            return;
        }
        
        Date now = new Date();
        // 产生奖励资金记录
        // 更新金额
        Wallet wallet = this.walletService.saveWalletByPartyId(partyId);
        double amountBefore = wallet.getMoney();
        this.walletService.updateMoeny(partyId, awardCash);

        // 账变日志
        MoneyLog moneyLog = new MoneyLog();
        moneyLog.setCategory(Constants.MONEYLOG_CATEGORY_COIN);
        moneyLog.setLog("店铺升级到:" + mallLevel + " 级奖励资金:" + awardCash+",升级条件中有效充值金额为："+currentUserRechargeAmount+" 分店数为："+validRechargeUserCount+" 团队人数为："+teamNum);
        moneyLog.setAmount_before(amountBefore);
        moneyLog.setAmount(awardCash);
        moneyLog.setAmount_after(Arith.add(amountBefore, awardCash));
        moneyLog.setPartyId(partyId);
        moneyLog.setWallettype(Constants.WALLET);
        moneyLog.setContent_type(Constants.MALL_LEVEL_UPGRADE_AWARD);
        moneyLog.setCreateTime(now);
        moneyLog.setFreeze(0);
        moneyLogService.save(moneyLog);

        // 钱包日志
        WalletLog walletLog = new WalletLog();
        walletLog.setCategory(Constants.MALL_LEVEL_UPGRADE_AWARD);
        walletLog.setPartyId(partyId);
        walletLog.setOrder_no("");
        walletLog.setStatus(1);
        walletLog.setAmount(awardCash);
        // 换算成USDT单位
        walletLog.setUsdtAmount(awardCash);
        walletLog.setWallettype("USDT");
        walletLog.setCreateTime(now);
        walletLogService.save(walletLog);

        // 需要考虑是否调用 userDataService.saveRechargeHandle  方法 TODO

    }


    public void setWalletService(WalletService walletService) {
        this.walletService = walletService;
    }

    public void setWalletLogService(WalletLogService walletLogService) {
        this.walletLogService = walletLogService;
    }

    public void setUserRecomService(UserRecomService userRecomService) {
        this.userRecomService = userRecomService;
    }

    public void setSellerService(SellerService sellerService) {
        this.sellerService = sellerService;
    }

    public void setMoneyLogService(MoneyLogService moneyLogService) {
        this.moneyLogService = moneyLogService;
    }

    public void setPartyService(PartyService partyService) {
        this.partyService = partyService;
    }
}
