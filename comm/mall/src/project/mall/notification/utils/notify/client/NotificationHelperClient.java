package project.mall.notification.utils.notify.client;

import project.blockchain.event.model.RechargeInfo;
import project.log.MoneyFreeze;
import project.log.MoneyLog;
import project.mall.notification.utils.notify.dto.RechargeData;
import project.mall.notification.utils.notify.dto.WithdrawData;
import project.mall.orders.model.MallOrdersPrize;
import project.mall.seller.model.Complaint;
import project.mall.seller.model.SellerCredit;
import project.onlinechat.OnlineChatUserMessage;

public interface NotificationHelperClient {

    /**
     * 冻结商家资金，提醒商家
     *
     * @param freezeRecord
     * @param sendChannelType : 消息发送渠道：1-纯短信，2-纯邮件，3-纯站内信，
     *                        4-短信+站内信，5-邮件+站内信，6-短信+邮件，7-短信+邮件+站内信
     */
    public void notifyFreezeSellerMoney(MoneyFreeze freezeRecord, int sendChannelType);

    /**
     * 解冻商家资金，提醒商家
     *
     * @param freezeRecord
     * @param sendChannelType : 消息发送渠道：1-纯短信，2-纯邮件，3-纯站内信，
     *                        4-短信+站内信，5-邮件+站内信，6-短信+邮件，7-短信+邮件+站内信
     */
    public void notifyUnFreezeSellerMoney(MoneyFreeze freezeRecord, int sendChannelType);

    /**
     * 有买家下单，提醒商家
     *
     * @param order
     * @param sendChannelType : 消息发送渠道：1-纯短信，2-纯邮件，3-纯站内信，
     *                        4-短信+站内信，5-邮件+站内信，6-短信+邮件，7-短信+邮件+站内信
     */
    public void notifySellerWithCreateOrder(MallOrdersPrize order, int sendChannelType);

    /**
     * 采购超时，提醒商家
     *
     * @param order
     * @param sendChannelType : 消息发送渠道：1-纯短信，2-纯邮件，3-纯站内信，
     *                        4-短信+站内信，5-邮件+站内信，6-短信+邮件，7-短信+邮件+站内信
     */
    public void notifySellerWithPushTimeOut(MallOrdersPrize order, int sendChannelType);

    /**
     * 订单完成返佣处理，提醒商家
     *
     * @param order
     * @param sendChannelType : 消息发送渠道：1-纯短信，2-纯邮件，3-纯站内信，
     *                        4-短信+站内信，5-邮件+站内信，6-短信+邮件，7-短信+邮件+站内信
     */
    public void notifyFinishOrder(MallOrdersPrize order, int sendChannelType);

    /**
     * 有买家咨询，商家未及时回复，提醒商家
     *
     * @param lastImInfo
     * @param sendChannelType : 消息发送渠道：1-纯短信，2-纯邮件，3-纯站内信，
     *                        4-短信+站内信，5-邮件+站内信，6-短信+邮件，7-短信+邮件+站内信
     */
    public void notifyReplyBuyer(OnlineChatUserMessage lastImInfo, int sendChannelType);

    /**
     * 订单超时未发货，提醒商家
     *
     * @param lastImInfo
     * @param sendChannelType : 消息发送渠道：1-纯短信，2-纯邮件，3-纯站内信，
     *                          4-短信+站内信，5-邮件+站内信，6-短信+邮件，7-短信+邮件+站内信
     */
    //public void notifySippedOvertime(OnlineChatUserMessage lastImInfo, int sendChannelType);

    /**
     * 修改了店铺信誉分，提醒商家
     *
     * @param accCredit
     * @param sendChannelType : 消息发送渠道：1-纯短信，2-纯邮件，3-纯站内信，
     *                        4-短信+站内信，5-邮件+站内信，6-短信+邮件，7-短信+邮件+站内信
     */
    public void notifyUpdateSellerCreditScore(SellerCredit accCredit, int sendChannelType);

    /**
     * 发送验证码
     *
     * @param mobileInfo
     * @param fromIp
     * @param imgCaptcha
     * @param sendChannelType : 消息发送渠道：1-纯短信，2-纯邮件
     */
    public void sendCaptchaCode(String mobileInfo, String fromIp, String imgCaptcha, int sendChannelType);

    /**
     * 充值审核通过，提醒用户
     *
     * @param info
     * @param sendChannelType : 消息发送渠道：1-纯短信，2-纯邮件，3-纯站内信，
     *                        4-短信+站内信，5-邮件+站内信，6-短信+邮件，7-短信+邮件+站内信
     */
    public void notifyRechargeSuccess(RechargeData info, int sendChannelType);

    /**
     * 提现审核通过，提醒用户
     *
     * @param info
     * @param sendChannelType : 消息发送渠道：1-纯短信，2-纯邮件，3-纯站内信，
     *                        4-短信+站内信，5-邮件+站内信，6-短信+邮件，7-短信+邮件+站内信
     */
    public void notifyWithdrawSuccess(WithdrawData info, int sendChannelType);


    /**
     * 提现审核失败，站内信提醒用户
     *
     */
    public void notifyWithdrawFail(WithdrawData info);

    /**
     * 充值审核失败，站内信提醒用户
     *
     */
    public void notifyRechargeFail(RechargeData info);

    /**
     * 买家投诉通过审核，对买家发送消息通知
     * @param complaint
     * @param creditScore 扣除信誉分
     * @param sendChannelType 消息发送渠道：1-纯短信，2-纯邮件，3-纯站内信，
     *                        4-短信+站内信，5-邮件+站内信，6-短信+邮件，7-短信+邮件+站内信
     */
    public void notifyBuyerComplaintSuccess(Complaint complaint, Integer creditScore, int sendChannelType);

    /**
     * 买家投诉审核被驳回，对买家发送消息通知
     * @param complaint
     * @param sendChannelType 消息发送渠道：1-纯短信，2-纯邮件，3-纯站内信，
     *                        4-短信+站内信，5-邮件+站内信，6-短信+邮件，7-短信+邮件+站内信
     */
    public void notifyBuyerComplaintFail(Complaint complaint,int sendChannelType);

    /**
     * 店铺被投诉，通知店铺
     * @param complaint
     * @param sendChannelType 消息发送渠道：1-纯短信，2-纯邮件，3-纯站内信，
     *                        4-短信+站内信，5-邮件+站内信，6-短信+邮件，7-短信+邮件+站内信
     */
    public void notifyStoreComplaintSuccess(Complaint complaint,Integer creditScore,int sendChannelType);


    /**
     * 店铺审核提醒用户
     *
     * @param partId 用户Id
     * @param status 审核状态
     * @param name   店铺名称
     * @param msg    原因
     */
    public void notifyStoreAuditByInbox(String partId, int status, String name, String msg);

    /**
     * 赠送彩金通知商家
     * @param moneyLog
     */
    public void notifyGiveBonusByInbox(MoneyLog moneyLog);
}
