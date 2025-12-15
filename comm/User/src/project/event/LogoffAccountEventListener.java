package project.event;

import kernel.util.JsonUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import project.event.message.LogoffAccountEvent;
import project.event.model.LogoffAccountInfo;
import project.mall.evaluation.EvaluationService;

/**
 * 用户注销账号后，需要同步修改相关的业务数据
 * 目前可见受影响的业务数据：
 * T_MALL_EVALUATION         --- partId, username
 * T_TRADER_FOLLOW_USER      --- 无记录
 * T_CODE_LOG                --- userName 无值
 * T_LOG                     --- partId， createTime
 * T_MESSAGE_USER            --- partId 与 targetUserName 不是同一个人 updateTime
 * T_ONLINECHAT_MESSAGE      --- partId 与 UserName 不是同一个人 createTime
 * PAT_PARTY                 --- 修改 userName， email， phone
 * SCT_USER                  --- 修改 userName， email
 * T_CUSTOMER                --- username， createTime
 * T_TIP                     --- target_username， createTime
 * T_WITHDRAW_ORDER          --- userName 无值
 * T_MALL_ORDER_LOG          --- userName 无值
 * T_AUTO_MONITOR_WITHDRAW_ORDER   --- 无记录
 * T_AUTO_MONITOR_WITHDRAW_COLLECTION_ORDER  --- 无记录
 *
 */
public class LogoffAccountEventListener implements ApplicationListener<LogoffAccountEvent> {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private EvaluationService evaluationService;

    @Override
    public void onApplicationEvent(LogoffAccountEvent event) {
        LogoffAccountInfo accountInfo = event.getInfo();
        logger.info("监听到用户:" + accountInfo.getPartyId() + " 注销了账号:" + JsonUtils.getJsonString(accountInfo));

        try {
            syncEvaluation(accountInfo.getPartyId(), accountInfo.getNewAccount());
        } catch (Exception e) {
            logger.error("用户:" + accountInfo.getPartyId() + " 注销账号后，同步修改订单评论信息报错，用户变更信息为: " + JsonUtils.getJsonString(accountInfo), e);
        }

        try {
            // TODO

        } catch (Exception e) {

        }
    }

    /**
     * 同步修改订单评论记录里的 userName 字段值
     *
     * @param partyId
     * @param newAccount
     */
    private void syncEvaluation(String partyId, String newAccount) {
        logger.info("用户:" + partyId + " 注销了账号，准备同步修改订单评论记录中的 uerName 字段值... 新值:" + newAccount);
        // TODO

        logger.info("用户:" + partyId + " 注销了账号，完成了同步修改订单评论记录中的 uerName 字段值... 新值:" + newAccount);
    }


    public void setEvaluationService(EvaluationService evaluationService) {
        this.evaluationService = evaluationService;
    }

}
