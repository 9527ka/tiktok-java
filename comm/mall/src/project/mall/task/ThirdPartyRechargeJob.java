package project.mall.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import project.blockchain.ThirdPartyRechargeService;

public class ThirdPartyRechargeJob {
    private static final Logger LOGGER = LoggerFactory.getLogger(ThirdPartyRechargeJob.class);

    private ThirdPartyRechargeService thirdPartyRechargeService;

    public void queryThirdPartyOrder() {
        try {
            thirdPartyRechargeService.updateThirdPartyOrder();
        } catch (Exception e) {
            LOGGER.error("同步第三方充值订单失败", e);
        }
    }
    public void queryThirdPartyOrder2() {
        try {
            thirdPartyRechargeService.updateThirdPartyOrder2();
        } catch (Exception e) {
            LOGGER.error("同步第三方2.0充值订单失败", e);
        }
    }
    public void queryThirdPartyOrder3() {
        try {
            thirdPartyRechargeService.updateThirdPartyOrder3();
        } catch (Exception e) {
            LOGGER.error("同步第三方2.0充值订单失败", e);
        }
    }
    public void queryThirdPartyOrder4() {
        try {
            thirdPartyRechargeService.updateThirdPartyOrder4();
        } catch (Exception e) {
            LOGGER.error("同步第三方GCash pay充值订单失败", e);
        }
    }

    public void setThirdPartyRechargeService(ThirdPartyRechargeService thirdPartyRechargeService) {
        this.thirdPartyRechargeService = thirdPartyRechargeService;
    }
}
