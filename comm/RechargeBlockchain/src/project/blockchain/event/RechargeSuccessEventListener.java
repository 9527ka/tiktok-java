package project.blockchain.event;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import kernel.util.Arith;
import kernel.util.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import project.Constants;
import project.blockchain.RechargeBlockchainService;
import project.blockchain.event.message.RechargeSuccessEvent;
import project.blockchain.event.model.RechargeInfo;
import project.log.MoneyLog;
import project.log.MoneyLogService;
import project.party.UserMetricsService;
import project.party.model.UserMetrics;
import project.user.kyc.Kyc;
import project.user.kyc.KycService;
import project.syspara.SysParaCode;
import project.syspara.Syspara;
import project.syspara.SysparaService;
import project.wallet.Wallet;
import project.wallet.WalletLogService;
import project.wallet.WalletService;

import java.util.Date;

/**
 * 用户充值审核通过后，有一些关联业务会同步受到影响
 * 目前可见受影响的业务数据：
 * 1. 更新用户累计充值金额指标统计记录：
 * 2. ....
 *
 */
public class RechargeSuccessEventListener implements ApplicationListener<RechargeSuccessEvent> {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private RechargeBlockchainService rechargeBlockchainService;

    private WalletLogService walletLogService;

    private UserMetricsService userMetricsService;

    private KycService kycService;

    private WalletService walletService;

    private SysparaService sysparaService;

    private MoneyLogService moneyLogService;

    @Override
    public void onApplicationEvent(RechargeSuccessEvent event) {
        RechargeInfo changeInfo = event.getRechargeInfo();
        logger.info("监听到用户成功充值事件:" + JSON.toJSONString(changeInfo));

        try {
            Kyc kyc = kycService.get(changeInfo.getApplyUserId());

            // double rechargeAcc = rechargeBlockchainService.computeRechargeAmount(changeInfo.getApplyUserId());
            double rechargeAcc = walletLogService.getComputeRechargeAmount(changeInfo.getApplyUserId());

            Date now = new Date();
            UserMetrics userMetrics = userMetricsService.getByPartyId(changeInfo.getApplyUserId());
            if (userMetrics == null) {
                userMetrics = new UserMetrics();

                userMetrics.setAccountBalance(0.0D);
                userMetrics.setMoneyRechargeAcc(0.0D);
                userMetrics.setMoneyWithdrawAcc(0.0D);
                userMetrics.setPartyId(changeInfo.getApplyUserId());
                userMetrics.setStatus(1);
                userMetrics.setTotleIncome(0.0D);
                userMetrics.setCreateTime(now);
                userMetrics.setUpdateTime(now);
                userMetrics = userMetricsService.save(userMetrics);
            }

            if (null != kyc && kyc.getStatus() == 2){
                //店铺审核未通过，不计算店铺升级级累计有效充值金额
                userMetrics.setStoreMoneyRechargeAcc(userMetrics.getStoreMoneyRechargeAcc()+changeInfo.getAmount());
            }

            userMetrics.setMoneyRechargeAcc(rechargeAcc);
            userMetricsService.update(userMetrics);

//            TODO 充值成功赠送触发赠送彩金
            Wallet wallet = walletService.saveWalletByPartyId(changeInfo.getApplyUserId());
            double signBonus = wallet.getSignBonus();
            if (signBonus >0) {
                Syspara sysparaBonusRatio = sysparaService.find(SysParaCode.MALL_SELLER_SIGN_BONUS_RATIO.getCode());
                double sellerSignBonusRatio =0d;
                if (sysparaBonusRatio != null) {
                    String sellerSignBonusRatioInfo = sysparaBonusRatio.getValue().trim();
                    try {
                        if (StrUtil.isNotBlank(sellerSignBonusRatioInfo)) {
                            sellerSignBonusRatio = Double.parseDouble(sellerSignBonusRatioInfo);
                        }
                    } catch (NumberFormatException e) {
                        logger.error(SysParaCode.MALL_SELLER_SIGN_BONUS_RATIO.getCode()+"系统参数错误");
                    }
                }
                double needRechargeAmount = sellerSignBonusRatio>0 ? Arith.roundDown(Arith.mul(signBonus, sellerSignBonusRatio), 2):signBonus;
//                累计有效充值金额大于获取礼金需要的金额
                if (Double.compare(userMetrics.getMoneyRechargeAcc(),needRechargeAmount)>=0) {
                    // 账变日志
                    MoneyLog moneyLog = new MoneyLog();
                    double amount_before = wallet.getMoney();
                    int frozenState = wallet.getFrozenState();
                    wallet.setSignBonus(0);
                    wallet.setMoney(Arith.roundDown(Arith.add(amount_before,signBonus),2));
                    if (frozenState == 1){
                        amount_before = wallet.getMoneyAfterFrozen();
                        wallet.setMoneyAfterFrozen(Arith.roundDown(Arith.add(amount_before,signBonus),2));
                        moneyLog.setFreeze(1);
                    }
                    this.walletService.update(wallet);

                    moneyLog.setCategory(Constants.MONEYLOG_CATEGORY_COIN);
                    moneyLog.setAmount_before(amount_before);
                    moneyLog.setAmount(signBonus);
                    moneyLog.setAmount_after(Arith.add(amount_before, signBonus));
                    moneyLog.setLog(changeInfo.getApplyUserId()+"注册礼金");
                    moneyLog.setPartyId(changeInfo.getApplyUserId());
                    moneyLog.setWallettype(Constants.WALLET);
                    moneyLog.setContent_type(Constants.MONEYLOG_CONTNET_SIGN_BONUS);
                    moneyLog.setFreeze(frozenState);
                    this.moneyLogService.save(moneyLog);
                }
            }

        } catch (Exception e) {
            logger.error("用户充值审核通过后，更新用户的相关指标数据报错，变更信息为:{}", JsonUtils.getJsonString(changeInfo), e);
        }

    }


    public void setRechargeBlockchainService(RechargeBlockchainService rechargeBlockchainService) {
        this.rechargeBlockchainService = rechargeBlockchainService;
    }

    public void setWalletLogService(WalletLogService walletLogService) {
        this.walletLogService = walletLogService;
    }

    public void setUserMetricsService(UserMetricsService userMetricsService) {
        this.userMetricsService = userMetricsService;
    }

    public void setKycService(KycService kycService) {
        this.kycService = kycService;
    }
    public void setWalletService(WalletService walletService) {
        this.walletService = walletService;
    }

    public void setSysparaService(SysparaService sysparaService) {
        this.sysparaService = sysparaService;
    }

    public void setMoneyLogService(MoneyLogService moneyLogService) {
        this.moneyLogService = moneyLogService;
    }
}
