package project.mall.notification;

import cn.hutool.core.util.StrUtil;
import kernel.exception.BusinessException;
import org.apache.commons.lang.StringUtils;
import project.Constants;
import project.log.CodeLogService;
import project.log.MallLogRedisKeys;
import project.log.MoneyFreeze;
import project.log.MoneyLog;
import project.mall.notification.utils.notify.CommonNotifyManager;
import project.mall.notification.utils.notify.NotificationBizTypeEnum;
import project.mall.notification.utils.notify.client.NotificationHelperClient;
import project.mall.notification.utils.notify.dto.RechargeData;
import project.mall.notification.utils.notify.dto.WithdrawData;
import project.mall.notification.utils.notify.request.DefaultEmailNotifyRequest;
import project.mall.notification.utils.notify.request.DefaultNotifyRequest;
import project.mall.notification.utils.notify.request.DefaultSmsNotifyRequest;
import project.mall.notification.utils.notify.request.NotifyReplyBuyerRequest;
import project.mall.orders.model.MallOrdersPrize;
import project.mall.seller.SellerService;
import project.mall.seller.model.Complaint;
import project.mall.seller.model.Seller;
import project.mall.seller.model.SellerCredit;
import project.onlinechat.OnlineChatUserMessage;
import project.party.PartyService;
import project.party.model.Party;
import project.redis.RedisHandler;
import project.syspara.Syspara;
import project.syspara.SysparaService;
import project.user.idcode.BlacklistIpTimeWindow;
import project.user.idcode.IdentifyingCodeTimeWindowService;
import project.user.idcode.SendCountTimeWindow;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 消息通知服务相关的业务处理类
 * 为防止循环依赖，单独提取出该服务类
 * 该服务类使用于 data 服务，其他应用通过 rpc 方式调用本服务的接口（NotificationHelperClient）
 */
public class NotificationHelper implements NotificationHelperClient {
    protected org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(NotificationHelper.class);

    private CommonNotifyManager commonNotifyManager;

    private IdentifyingCodeTimeWindowService identifyingCodeTimeWindowService;
    private SendCountTimeWindow sendCountTimeWindow;
    private BlacklistIpTimeWindow blacklistIpTimeWindow;
    private SysparaService sysparaService;
    private CodeLogService codeLogService;

    protected RedisHandler redisHandler;
    protected PartyService partyService;
    protected SellerService sellerService;
    private NotificationService notificationService;

    private Map<String, Integer> ipCache = new ConcurrentHashMap<String, Integer>();

    /**
     * @param order
     * @param sendChannelType : 消息发送渠道：1-纯短信，2-纯邮件，3-纯站内信，
     *                        4-短信+站内信，5-邮件+站内信，6-短信+邮件，7-短信+邮件+站内信
     */
    public void notifySellerWithCreateOrder(MallOrdersPrize order, int sendChannelType) {
        sendChannelType = getNotifyLevel();

        if (sendChannelType == 1) {
            notifySellerWithCreateOrderBySms(order);
        } else if (sendChannelType == 2) {
            notifySellerWithCreateOrderByEmail(order);
        } else if (sendChannelType == 3) {
            notifySellerWithCreateOrderByInbox(order);
        } else if (sendChannelType == 4) {
            // 短信+站内信
            notifySellerWithCreateOrderBySms(order);
            notifySellerWithCreateOrderByInbox(order);
        } else if (sendChannelType == 5) {
            // 邮件+站内信
            notifySellerWithCreateOrderByEmail(order);
            notifySellerWithCreateOrderByInbox(order);
        } else if (sendChannelType == 6) {
            // 短信+邮件
            notifySellerWithCreateOrderBySms(order);
            notifySellerWithCreateOrderByEmail(order);
        } else if (sendChannelType == 7) {
            // 短信+邮件+站内信
            notifySellerWithCreateOrderBySms(order);
            notifySellerWithCreateOrderByEmail(order);
            notifySellerWithCreateOrderByInbox(order);
        }
    }

    /**
     * @param order
     * @param sendChannelType : 消息发送渠道：1-纯短信，2-纯邮件，3-纯站内信，
     *                        4-短信+站内信，5-邮件+站内信，6-短信+邮件，7-短信+邮件+站内信
     */
    @Override
    public void notifySellerWithPushTimeOut(MallOrdersPrize order, int sendChannelType) {
        sendChannelType = getNotifyLevel();

        if (sendChannelType == 1) {
            notifySellerWithPushTimeOutBySms(order);
        } else if (sendChannelType == 2) {
            notifySellerWithPushTimeOutByEmail(order);
        } else if (sendChannelType == 3) {
            notifySellerWithPushTimeOutByInbox(order);
        } else if (sendChannelType == 4) {
            // 短信+站内信
            notifySellerWithPushTimeOutBySms(order);
            notifySellerWithPushTimeOutByInbox(order);
        } else if (sendChannelType == 5) {
            // 邮件+站内信
            notifySellerWithPushTimeOutByEmail(order);
            notifySellerWithPushTimeOutByInbox(order);
        } else if (sendChannelType == 6) {
            // 短信+邮件
            notifySellerWithPushTimeOutBySms(order);
            notifySellerWithPushTimeOutByEmail(order);
        } else if (sendChannelType == 7) {
            // 短信+邮件+站内信
            notifySellerWithPushTimeOutBySms(order);
            notifySellerWithPushTimeOutByEmail(order);
            notifySellerWithPushTimeOutByInbox(order);
        }
    }

    private void notifySellerWithPushTimeOutBySms(MallOrdersPrize order) {
        try {
            DefaultSmsNotifyRequest notifyRequest = new DefaultSmsNotifyRequest();
            notifyRequest.setBizType(NotificationBizTypeEnum.SMS_ORDER_PURCHASED_OVERTIME.getBizType());
            notifyRequest.setFromUserId("0");
            notifyRequest.setLanguageType("en_US");
            notifyRequest.setTargetTopic("0");
            notifyRequest.setTargetUserId(order.getSellerId());
            // 代表 MallOrdersPrize 记录
            notifyRequest.setRefType(1);
            notifyRequest.setRefId(order.getId().toString());

            // 设置消息模板中的动态占位符的值
            //notifyRequest.setValue("orderNo", order.getId());

            String mobileInfo = getPartyMobile(notifyRequest.getTargetUserId());
            if (StringUtils.isBlank(mobileInfo)) {
                logger.warn("---> notifySellerWithPushTimeOutBySms 方法中订单:" + order.getId() + " 对应的商家没有设置手机号码，无法发送短信提醒");
                return;
            }
            if (Objects.equals(mobileInfo, "x")) {
                logger.warn("---> notifySellerWithPushTimeOutBySms 方法中订单:" + order.getId() + " 对应的商家的手机号码还没有校验有效性，无法发送短信提醒");
                return;
            }

            notifyRequest.setMobileInfo(mobileInfo);

            logger.info("---> notifySellerWithPushTimeOutBySms 下单事件触发短信消息，订单为:" + order.getId());
            commonNotifyManager.sendNotify(notifyRequest);
            logger.info("---> notifySellerWithPushTimeOutBySms 下单事件完成短信发送，订单为:" + order.getId());
        } catch (Exception e) {
            logger.error("采购超时事件触发短信报错，订单id:" + order.getId(), e);
        }
    }

    private void notifySellerWithPushTimeOutByEmail(MallOrdersPrize order) {
        try {
            DefaultEmailNotifyRequest notifyRequest = new DefaultEmailNotifyRequest();
            notifyRequest.setBizType(NotificationBizTypeEnum.EMAIL_ORDER_PURCHASED_OVERTIME.getBizType());
            notifyRequest.setFromUserId("0");
            notifyRequest.setLanguageType("en_US");
            notifyRequest.setTargetTopic("0");
            notifyRequest.setTargetUserId(order.getSellerId());
            // 代表 MallOrdersPrize 记录
            notifyRequest.setRefType(1);
            notifyRequest.setRefId(order.getId().toString());

            // 设置消息模板中的动态占位符的值
            //notifyRequest.setValue("orderNo", order.getId());

            String email = getPartyEmail(notifyRequest.getTargetUserId());
            if (StringUtils.isBlank(email)) {
                logger.warn("---> notifySellerWithPushTimeOutByEmail 方法中订单:" + order.getId() + " 对应的商家没有设置邮箱，无法发送邮件提醒");
                return;
            }
            if (Objects.equals(email, "x")) {
                logger.warn("---> notifySellerWithPushTimeOutByEmail 方法中订单:" + order.getId() + " 对应的商家为虚拟用户或对应的商家的邮箱尚未校验有效性，无法发送邮件提醒");
                return;
            }
            notifyRequest.setTargetEmail(email);

            logger.info("---> notifySellerWithPushTimeOutByEmail 采购超时事件触发邮件消息，订单为:" + order.getId());
            commonNotifyManager.sendNotify(notifyRequest);
            logger.info("---> notifySellerWithPushTimeOutByEmail 采购超时事件完成邮件发送，订单为:" + order.getId());
        } catch (Exception e) {
            logger.error("采购超时事件触发邮件报错，订单id:" + order.getId(), e);
        }
    }

    private void notifySellerWithPushTimeOutByInbox(MallOrdersPrize order) {
        try {
            DefaultNotifyRequest notifyRequest = new DefaultNotifyRequest();
            notifyRequest.setBizType(NotificationBizTypeEnum.INBOX_ORDER_PURCHASED_OVERTIME.getBizType());
            notifyRequest.setFromUserId("0");
            notifyRequest.setLanguageType("en_US");
            notifyRequest.setTargetTopic("0");
            notifyRequest.setTargetUserId(order.getSellerId());
            // 代表 MallOrdersPrize 记录
            notifyRequest.setRefType(1);
            notifyRequest.setRefId(order.getId().toString());

            // 设置消息模板中的动态占位符的值
            //notifyRequest.setValue("orderNo", order.getId());

            logger.info("---> notifySellerWithPushTimeOutByInbox 采购超时事件触发站内信消息，订单为:" + order.getId());
            commonNotifyManager.sendNotify(notifyRequest);
            logger.info("---> notifySellerWithPushTimeOutByInbox 采购超时事件完成站内信发送，订单为:" + order.getId());
        } catch (Exception e) {
            logger.error("采购超时事件触发站内信报错，订单id:" + order.getId(), e);
        }
    }

    /**
     * 赠送彩金发送站内信
     * @param order
     */
    @Override
    public void notifyGiveBonusByInbox(MoneyLog order) {
        try {
            Party party = partyService.cachePartyBy(order.getPartyId().toString(), true);
            if (party.getRoleType() != 1){
                return;
            }

            DefaultNotifyRequest notifyRequest = new DefaultNotifyRequest();
            notifyRequest.setBizType(NotificationBizTypeEnum.INBOX_BONUS_SUCCESS.getBizType());
            notifyRequest.setFromUserId("0");
            notifyRequest.setLanguageType("en_US");
            notifyRequest.setTargetTopic("0");
            notifyRequest.setTargetUserId(order.getPartyId().toString());
            // 代表 MallOrdersPrize 记录
            notifyRequest.setRefType(1);
            notifyRequest.setRefId(order.getId().toString());
            // 设置消息模板中的动态占位符的值
            notifyRequest.setValue("amount", order.getAmount());

            logger.info("---> notifyGiveBonusByInbox 赠送彩金时间完成站内信发送，资金订单为:" + order.getId());
            commonNotifyManager.sendNotify(notifyRequest);
            logger.info("---> notifyGiveBonusByInbox 赠送彩金时间完成站内信发送，资金订单为:" + order.getId());
        } catch (Exception e) {
            logger.error("赠送彩金事件触发站内信报错，资金订单为:" + order.getId(), e);
        }
    }

    private void notifySellerWithCreateOrderBySms(MallOrdersPrize order) {
        try {
            DefaultSmsNotifyRequest notifyRequest = new DefaultSmsNotifyRequest();
            notifyRequest.setBizType(NotificationBizTypeEnum.SMS_NEW_ORDER_SUBMITTED.getBizType());
            notifyRequest.setFromUserId("0");
            notifyRequest.setLanguageType("en_US");
            notifyRequest.setTargetTopic("0");
            notifyRequest.setTargetUserId(order.getSellerId());
            // 代表 MallOrdersPrize 记录
            notifyRequest.setRefType(1);
            notifyRequest.setRefId(order.getId().toString());

            // 设置消息模板中的动态占位符的值
            //notifyRequest.setValue("orderNo", order.getId());

            String mobileInfo = getPartyMobile(notifyRequest.getTargetUserId());
            if (StringUtils.isBlank(mobileInfo)) {
                logger.warn("---> notifySellerWithCreateOrderBySms 方法中订单:" + order.getId() + " 对应的商家没有设置手机号码，无法发送短信提醒");
                return;
            }
            if (Objects.equals(mobileInfo, "x")) {
                logger.warn("---> notifySellerWithCreateOrderBySms 方法中订单:" + order.getId() + " 对应的商家的手机号码还没有校验有效性，无法发送短信提醒");
                return;
            }

            notifyRequest.setMobileInfo(mobileInfo);

            logger.info("---> notifySellerWithCreateOrderBySms 下单事件触发短信消息，订单为:" + order.getId());
            commonNotifyManager.sendNotify(notifyRequest);
            logger.info("---> notifySellerWithCreateOrderBySms 下单事件完成短信发送，订单为:" + order.getId());
        } catch (Exception e) {
            logger.error("下单事件触发短信报错，订单id:" + order.getId(), e);
        }
    }

    private void notifySellerWithCreateOrderByEmail(MallOrdersPrize order) {
        try {
            DefaultEmailNotifyRequest notifyRequest = new DefaultEmailNotifyRequest();
            notifyRequest.setBizType(NotificationBizTypeEnum.EMAIL_NEW_ORDER_SUBMITTED.getBizType());
            notifyRequest.setFromUserId("0");
            notifyRequest.setLanguageType("en_US");
            notifyRequest.setTargetTopic("0");
            notifyRequest.setTargetUserId(order.getSellerId());
            // 代表 MallOrdersPrize 记录
            notifyRequest.setRefType(1);
            notifyRequest.setRefId(order.getId().toString());

            // 设置消息模板中的动态占位符的值
            //notifyRequest.setValue("orderNo", order.getId());

            String email = getPartyEmail(notifyRequest.getTargetUserId());
            if (StringUtils.isBlank(email)) {
                logger.warn("---> notifySellerWithCreateOrderByEmail 方法中订单:" + order.getId() + " 对应的商家没有设置邮箱，无法发送邮件提醒");
                return;
            }
            if (Objects.equals(email, "x")) {
                logger.warn("---> notifySellerWithCreateOrderByEmail 方法中订单:" + order.getId() + " 对应的商家为虚拟用户或对应的商家的邮箱尚未校验有效性，无法发送邮件提醒");
                return;
            }
            notifyRequest.setTargetEmail(email);

            logger.info("---> notifySellerWithCreateOrderByEmail 下单事件触发邮件消息，订单为:" + order.getId());
            commonNotifyManager.sendNotify(notifyRequest);
            logger.info("---> notifySellerWithCreateOrderByEmail 下单事件完成邮件发送，订单为:" + order.getId());
        } catch (Exception e) {
            logger.error("下单事件触发邮件报错，订单id:" + order.getId(), e);
        }
    }

    private void notifySellerWithCreateOrderByInbox(MallOrdersPrize order) {
        try {
            DefaultNotifyRequest notifyRequest = new DefaultNotifyRequest();
            notifyRequest.setBizType(NotificationBizTypeEnum.INBOX_NEW_ORDER_SUBMITTED.getBizType());
            notifyRequest.setFromUserId("0");
            notifyRequest.setLanguageType("en_US");
            notifyRequest.setTargetTopic("0");
            notifyRequest.setTargetUserId(order.getSellerId());
            // 代表 MallOrdersPrize 记录
            notifyRequest.setRefType(1);
            notifyRequest.setRefId(order.getId().toString());

            // 设置消息模板中的动态占位符的值
            //notifyRequest.setValue("orderNo", order.getId());

            logger.info("---> notifySellerWithCreateOrderByInbox 下单事件触发站内信消息，订单为:" + order.getId());
            commonNotifyManager.sendNotify(notifyRequest);
            logger.info("---> notifySellerWithCreateOrderByInbox 下单事件完成站内信发送，订单为:" + order.getId());
        } catch (Exception e) {
            logger.error("下单事件触发站内信报错，订单id:" + order.getId(), e);
        }
    }

    public void notifyFinishOrder(MallOrdersPrize order, int sendChannelType) {
        sendChannelType = getNotifyLevel();

        if (sendChannelType == 1) {
            notifyFinishOrderBySms(order);
        } else if (sendChannelType == 2) {
            notifyFinishOrderByEmail(order);
        } else if (sendChannelType == 3) {
            notifyFinishOrderByInbox(order);
        } else if (sendChannelType == 4) {
            // 短信+站内信
            notifyFinishOrderBySms(order);
            notifyFinishOrderByInbox(order);
        } else if (sendChannelType == 5) {
            // 邮件+站内信
            notifyFinishOrderByEmail(order);
            notifyFinishOrderByInbox(order);
        } else if (sendChannelType == 6) {
            // 短信+邮件
            notifyFinishOrderBySms(order);
            notifyFinishOrderByEmail(order);
        } else if (sendChannelType == 7) {
            // 短信+邮件+站内信
            notifyFinishOrderBySms(order);
            notifyFinishOrderByEmail(order);
            notifyFinishOrderByInbox(order);
        }
    }

    private void notifyFinishOrderBySms(MallOrdersPrize order) {
        try {
            DefaultSmsNotifyRequest notifyRequest = new DefaultSmsNotifyRequest();
            notifyRequest.setBizType(NotificationBizTypeEnum.SMS_FINISH_ORDER.getBizType());
            notifyRequest.setFromUserId("0");
            notifyRequest.setLanguageType("en_US");
            notifyRequest.setTargetTopic("0");
            // 代表 MallOrdersPrize 记录
            notifyRequest.setRefType(1);
            notifyRequest.setRefId(order.getId().toString());
            notifyRequest.setTargetUserId(order.getSellerId());

            // 设置消息模板中的动态占位符的值
            notifyRequest.setValue("orderNo", order.getId());

            String mobileInfo = getPartyMobile(notifyRequest.getTargetUserId());
            if (StringUtils.isBlank(mobileInfo)) {
                logger.warn("---> notifyFinishOrderBySms 方法中订单:" + order.getId() + " 对应的商家没有设置手机号码，无法发送短信提醒");
                return;
            }
            if (Objects.equals(mobileInfo, "x")) {
                logger.warn("---> notifyFinishOrderBySms 方法中订单:" + order.getId() + " 对应的商家的手机号码还没有校验有效性，无法发送短信提醒");
                return;
            }
            notifyRequest.setMobileInfo(mobileInfo);

            logger.info("---> notifyFinishOrderBySms 订单完成事件触发短信消息，订单为:" + order.getId());
            commonNotifyManager.sendNotify(notifyRequest);
            logger.info("---> notifyFinishOrderBySms 订单完成事件完成短信发送，订单为:" + order.getId());
        } catch (Exception e) {
            logger.error("订单完成事件触发短信消息报错，订单id:" + order.getId(), e);
        }
    }

    private void notifyFinishOrderByEmail(MallOrdersPrize order) {
        try {
            DefaultEmailNotifyRequest notifyRequest = new DefaultEmailNotifyRequest();
            notifyRequest.setBizType(NotificationBizTypeEnum.EMAIL_FINISH_ORDER.getBizType());
            notifyRequest.setFromUserId("0");
            notifyRequest.setLanguageType("en_US");
            notifyRequest.setTargetTopic("0");
            // 代表 MallOrdersPrize 记录
            notifyRequest.setRefType(1);
            notifyRequest.setRefId(order.getId().toString());
            notifyRequest.setTargetUserId(order.getSellerId());

            // 设置消息模板中的动态占位符的值
            notifyRequest.setValue("orderNo", order.getId());

            String email = getPartyEmail(notifyRequest.getTargetUserId());
            if (StringUtils.isBlank(email)) {
                logger.warn("---> notifyFinishOrderByEmail 方法中订单:" + order.getId() + " 对应的商家没有设置邮箱，无法发送邮件提醒");
                return;
            }
            if (Objects.equals(email, "x")) {
                logger.warn("---> notifyFinishOrderByEmail 方法中订单:" + order.getId() + " 对应的商家为虚拟用户或对应的商家的邮箱尚未校验有效性，无法发送邮件提醒");
                return;
            }
            notifyRequest.setTargetEmail(email);

            logger.info("---> notifyFinishOrderByEmail 订单完成事件触发邮件消息，订单为:" + order.getId());
            commonNotifyManager.sendNotify(notifyRequest);
            logger.info("---> notifyFinishOrderByEmail 订单完成事件完成邮件发送，订单为:" + order.getId());
        } catch (Exception e) {
            logger.error("订单完成事件触发邮件消息报错，订单id:" + order.getId(), e);
        }
    }

    private void notifyFinishOrderByInbox(MallOrdersPrize order) {
        try {
            DefaultNotifyRequest notifyRequest = new DefaultNotifyRequest();
            notifyRequest.setBizType(NotificationBizTypeEnum.INBOX_FINISH_ORDER.getBizType());
            notifyRequest.setFromUserId("0");
            notifyRequest.setLanguageType("en_US");
            notifyRequest.setTargetTopic("0");
            // 代表 MallOrdersPrize 记录
            notifyRequest.setRefType(1);
            notifyRequest.setRefId(order.getId().toString());
            notifyRequest.setTargetUserId(order.getSellerId());

            // 设置消息模板中的动态占位符的值
            notifyRequest.setValue("orderNo", order.getId());

            logger.info("---> notifyFinishOrderByInbox 订单完成事件触发站内信消息，订单为:" + order.getId());
            commonNotifyManager.sendNotify(notifyRequest);
            logger.info("---> notifyFinishOrderByInbox 订单完成事件完成站内信发送，订单为:" + order.getId());
        } catch (Exception e) {
            logger.error("订单完成事件触发站内信消息报错，订单id:" + order.getId(), e);
        }
    }

    /**
     * 提醒卖家及时回复买家的咨询
     *
     * @param lastImInfo
     */
    public void notifyReplyBuyer(OnlineChatUserMessage lastImInfo, int sendChannelType) {
        sendChannelType = getNotifyLevel();

        if (sendChannelType == 1) {
            notifyReplyBuyerBySms(lastImInfo);
        } else if (sendChannelType == 2) {
            notifyReplyBuyerByEmail(lastImInfo);
        } else if (sendChannelType == 3) {
            notifyReplyBuyerByInbox(lastImInfo);
        } else if (sendChannelType == 4) {
            // 短信+站内信
            notifyReplyBuyerBySms(lastImInfo);
            notifyReplyBuyerByInbox(lastImInfo);
        } else if (sendChannelType == 5) {
            // 邮件+站内信
            notifyReplyBuyerByEmail(lastImInfo);
            notifyReplyBuyerByInbox(lastImInfo);
        } else if (sendChannelType == 6) {
            // 短信+邮件
            notifyReplyBuyerBySms(lastImInfo);
            notifyReplyBuyerByEmail(lastImInfo);
        } else if (sendChannelType == 7) {
            // 短信+邮件+站内信
            notifyReplyBuyerBySms(lastImInfo);
            notifyReplyBuyerByEmail(lastImInfo);
            notifyReplyBuyerByInbox(lastImInfo);
        }

        // 这条记录的 chatId 格式是： buyerId - sellerId
        String chatId = lastImInfo.getChatId();
        String[] chatInfoArr = chatId.split("-");
        String buyerId = chatInfoArr[0];
        String sellerId = chatInfoArr[1];
        // 清理缓存
        String cacheValue = sellerId + ":" + buyerId;
        redisHandler.zrem(MallLogRedisKeys.SELLER_IM_REPLY_NOTIFY, cacheValue);
        redisHandler.remove(MallLogRedisKeys.SELLER_IM_LAST_BUYER_MESSAGE, cacheValue);
    }

    private void notifyReplyBuyerBySms(OnlineChatUserMessage lastImInfo) {
        // 这条记录的 chatId 格式是： buyerId - sellerId
        String chatId = lastImInfo.getChatId();
        try {
            String[] chatInfoArr = chatId.split("-");
            String buyerId = chatInfoArr[0];
            String sellerId = chatInfoArr[1];

            NotifyReplyBuyerRequest notifyRequest = new NotifyReplyBuyerRequest();
            notifyRequest.setBizType(NotificationBizTypeEnum.SMS_REPLY_BUYER_IM.getBizType());
            notifyRequest.setFromUserId("0");
            notifyRequest.setLanguageType("en_US");
            notifyRequest.setTargetTopic("0");
            notifyRequest.setTargetUserId(sellerId);
            notifyRequest.setSellerId(sellerId);
            notifyRequest.setBuyerId(buyerId);
            notifyRequest.setLastImMessageId(lastImInfo.getId().toString());
            notifyRequest.setLastImMessage(lastImInfo.getContent());
            notifyRequest.setLastImMessageType(lastImInfo.getContentType());
            // 代表 OnlineChatUserMessage 记录
            String initMessageInfo = redisHandler.get(MallLogRedisKeys.SELLER_IM_LAST_BUYER_MESSAGE, sellerId + ":" + buyerId);
            String initMessageId = "0";
            notifyRequest.setRefType(2);
            if (StringUtils.isNotBlank(initMessageInfo)) {
                String[] messageInfoArr = initMessageInfo.split(":");
                initMessageId = messageInfoArr[0];
            }
            // 最早未看的那条聊天记录ID
            notifyRequest.setRefId(initMessageId);

            // 填充卖家手机，邮箱信息
            String mobileInfo = getPartyMobile(notifyRequest.getTargetUserId());
            if (StringUtils.isBlank(mobileInfo)) {
                logger.warn("---> notifyReplyBuyerBySms 方法中聊天消息:" + chatId + " 对应的商家没有设置手机号码，无法发送短信提醒");
                return;
            }
            if (Objects.equals(mobileInfo, "x")) {
                logger.warn("---> notifyReplyBuyerBySms 方法中聊天消息:" + chatId + " 对应的商家的手机号码还没有校验有效性，无法发送短信提醒");
                return;
            }

            notifyRequest.setSellerMobileInfo(mobileInfo);

            logger.info("---> notifyReplyBuyerBySms 短信提醒商家尽快回复IM中的买家留言:" + chatId);
            commonNotifyManager.sendNotify(notifyRequest);
            logger.info("---> notifyReplyBuyerBySms 完成提醒消息发送:" + chatId);

//            // 清理缓存
//            String cacheValue = sellerId + ":" + buyerId;
//            redisHandler.zrem(MallLogRedisKeys.SELLER_IM_REPLY_NOTIFY, cacheValue);
//            redisHandler.remove(MallLogRedisKeys.SELLER_IM_LAST_BUYER_MESSAGE, cacheValue);
        } catch (Exception e) {
            logger.error("短信提醒商家回复买家咨询时发送通知消息报错, chatId:" + chatId, e);
        }
    }

    private void notifyReplyBuyerByEmail(OnlineChatUserMessage lastImInfo) {
        // 这条记录的 chatId 格式是： buyerId - sellerId
        String chatId = lastImInfo.getChatId();
        try {
            String[] chatInfoArr = chatId.split("-");
            String buyerId = chatInfoArr[0];
            String sellerId = chatInfoArr[1];

            NotifyReplyBuyerRequest notifyRequest = new NotifyReplyBuyerRequest();
            notifyRequest.setBizType(NotificationBizTypeEnum.EMAIL_REPLY_BUYER_IM.getBizType());
            notifyRequest.setFromUserId("0");
            notifyRequest.setLanguageType("en_US");
            notifyRequest.setTargetTopic("0");
            notifyRequest.setTargetUserId(sellerId);
            notifyRequest.setSellerId(sellerId);
            notifyRequest.setBuyerId(buyerId);
            notifyRequest.setLastImMessageId(lastImInfo.getId().toString());
            notifyRequest.setLastImMessage(lastImInfo.getContent());
            notifyRequest.setLastImMessageType(lastImInfo.getContentType());
            // 代表 OnlineChatUserMessage 记录
            String initMessageInfo = redisHandler.get(MallLogRedisKeys.SELLER_IM_LAST_BUYER_MESSAGE, sellerId + ":" + buyerId);
            String initMessageId = "0";
            notifyRequest.setRefType(2);
            if (StringUtils.isNotBlank(initMessageInfo)) {
                String[] messageInfoArr = initMessageInfo.split(":");
                initMessageId = messageInfoArr[0];
            }
            // 最早未看的那条聊天记录ID
            notifyRequest.setRefId(initMessageId);

            // 填充卖家手机，邮箱信息
            String email = getPartyEmail(notifyRequest.getTargetUserId());
            if (StringUtils.isBlank(email)) {
                logger.warn("---> notifyReplyBuyerByEmail 方法中聊天消息:" + chatId + " 对应的商家没有设置邮箱，无法发送邮件提醒");
                return;
            }
            if (Objects.equals(email, "x")) {
                logger.warn("---> notifyReplyBuyerByEmail 方法中聊天消息:" + chatId + " 对应的商家为虚拟用户或对应的商家的邮箱尚未校验有效性，无法发送邮件提醒");
                return;
            }
            notifyRequest.setSellerEmail(email);

            logger.info("---> notifyReplyBuyerByEmail 邮件提醒商家尽快回复IM中的买家留言:" + chatId);
            commonNotifyManager.sendNotify(notifyRequest);
            logger.info("---> notifyReplyBuyerByEmail 完成提醒消息发送:" + chatId);
        } catch (Exception e) {
            logger.error("邮件提醒商家回复买家咨询时发送通知消息报错, chatId:" + chatId, e);
        }
    }

    private void notifyReplyBuyerByInbox(OnlineChatUserMessage lastImInfo) {
        // 这条记录的 chatId 格式是： buyerId - sellerId
        String chatId = lastImInfo.getChatId();
        try {
            String[] chatInfoArr = chatId.split("-");
            String buyerId = chatInfoArr[0];
            String sellerId = chatInfoArr[1];

            NotifyReplyBuyerRequest notifyRequest = new NotifyReplyBuyerRequest();
            notifyRequest.setBizType(NotificationBizTypeEnum.INBOX_REPLY_BUYER_IM.getBizType());
            notifyRequest.setFromUserId("0");
            notifyRequest.setLanguageType("en_US");
            notifyRequest.setTargetTopic("0");
            notifyRequest.setTargetUserId(sellerId);
            notifyRequest.setSellerId(sellerId);
            notifyRequest.setBuyerId(buyerId);
            notifyRequest.setLastImMessageId(lastImInfo.getId().toString());
            notifyRequest.setLastImMessage(lastImInfo.getContent());
            notifyRequest.setLastImMessageType(lastImInfo.getContentType());
            // 代表 OnlineChatUserMessage 记录
            String initMessageInfo = redisHandler.get(MallLogRedisKeys.SELLER_IM_LAST_BUYER_MESSAGE, sellerId + ":" + buyerId);
            String initMessageId = "0";
            notifyRequest.setRefType(2);
            if (StringUtils.isNotBlank(initMessageInfo)) {
                String[] messageInfoArr = initMessageInfo.split(":");
                initMessageId = messageInfoArr[0];
            }
            // 最早未看的那条聊天记录ID
            notifyRequest.setRefId(initMessageId);

//            // 填充卖家手机，邮箱信息
//            Party sellerPartyEntity = partyService.cachePartyBy(buyerId, true);
//            if (sellerPartyEntity == null) {
//                logger.error("---> notifyReplyBuyerByInbox 方法中指定用户记录不存在:" + notifyRequest.getTargetUserId());
//                return;
//            }

            logger.info("---> notifyReplyBuyerByInbox 站内信提醒商家尽快回复IM中的买家留言:" + chatId);
            commonNotifyManager.sendNotify(notifyRequest);
            logger.info("---> notifyReplyBuyerByInbox 完成提醒消息发送:" + chatId);
        } catch (Exception e) {
            logger.error("站内信提醒商家回复买家咨询时发送通知消息报错, chatId:" + chatId, e);
        }
    }


    public void notifyFreezeSellerMoney(MoneyFreeze freezeRecord, int sendChannelType) {
        sendChannelType = getNotifyLevel();

        if (sendChannelType == 1) {
            notifyFreezeSellerMoneyBySms(freezeRecord);
        } else if (sendChannelType == 2) {
            notifyFreezeSellerMoneyByEmail(freezeRecord);
        } else if (sendChannelType == 3) {
            notifyFreezeSellerMoneyByInbox(freezeRecord);
        } else if (sendChannelType == 4) {
            // 短信+站内信
            notifyFreezeSellerMoneyBySms(freezeRecord);
            notifyFreezeSellerMoneyByInbox(freezeRecord);
        } else if (sendChannelType == 5) {
            // 邮件+站内信
            notifyFreezeSellerMoneyByEmail(freezeRecord);
            notifyFreezeSellerMoneyByInbox(freezeRecord);
        } else if (sendChannelType == 6) {
            // 短信+邮件
            notifyFreezeSellerMoneyBySms(freezeRecord);
            notifyFreezeSellerMoneyByEmail(freezeRecord);
        } else if (sendChannelType == 7) {
            // 短信+邮件+站内信
            notifyFreezeSellerMoneyBySms(freezeRecord);
            notifyFreezeSellerMoneyByEmail(freezeRecord);
            notifyFreezeSellerMoneyByInbox(freezeRecord);
        }
    }

    private void notifyFreezeSellerMoneyBySms(MoneyFreeze freezeRecord) {
        try {
            String bizType = NotificationBizTypeEnum.SMS_FREEZE_SELLER_MONEY.getBizType();
            String language = "en_US";
            String targetUserId = freezeRecord.getPartyId().toString();
            String targetTopic = "0";

            DefaultSmsNotifyRequest notifyRequest = new DefaultSmsNotifyRequest();
            notifyRequest.setBizType(bizType);
            notifyRequest.setFromUserId("0");
            notifyRequest.setLanguageType(language);
            notifyRequest.setTargetTopic(targetTopic);
            notifyRequest.setTargetUserId(targetUserId.trim());

            // 根据站内信模板中的占位符设置值
            notifyRequest.setValue("amount", freezeRecord.getAmount());
//            notifyRequest.setValue("days", (freezeRecord.getEndTime().getTime() - freezeRecord.getBeginTime().getTime()) / (24L * 3600L * 1000L));

            // 填充卖家手机，邮箱信息
            String mobileInfo = getPartyMobile(notifyRequest.getTargetUserId());
            if (StringUtils.isBlank(mobileInfo)) {
                logger.warn("---> notifyFreezeSellerMoneyBySms 资金冻结记录:" + freezeRecord.getId() + " 对应的商家没有设置手机号码，无法发送短信提醒");
                return;
            }
            if (Objects.equals(mobileInfo, "x")) {
                logger.warn("---> notifyFreezeSellerMoneyBySms 资金冻结记录:" + freezeRecord.getId() + " 对应的商家的手机号码还没有校验有效性，无法发送短信提醒");
                return;
            }
            notifyRequest.setMobileInfo(mobileInfo);

            logger.info("---> notifyFreezeSellerMoneyBySms 短信提醒商家资金被冻结，冻结记录:" + freezeRecord.getId());
            commonNotifyManager.sendNotify(notifyRequest);
            logger.info("---> notifyFreezeSellerMoneyBySms 完成提醒消息发送，冻结记录:" + freezeRecord.getId());
        } catch (Exception e) {
            logger.error("短信提醒商家资金被冻结，发送通知消息报错, 冻结记录:" + freezeRecord.getId(), e);
        }
    }

    private void notifyFreezeSellerMoneyByEmail(MoneyFreeze freezeRecord) {
        try {
            String bizType = NotificationBizTypeEnum.EMAIL_FREEZE_SELLER_MONEY.getBizType();
            String language = "en_US";
            String targetUserId = freezeRecord.getPartyId().toString();
            String targetTopic = "0";

            DefaultEmailNotifyRequest notifyRequest = new DefaultEmailNotifyRequest();
            notifyRequest.setBizType(bizType);
            notifyRequest.setFromUserId("0");
            notifyRequest.setLanguageType(language);
            notifyRequest.setTargetTopic(targetTopic);
            notifyRequest.setTargetUserId(targetUserId.trim());

            // 根据站内信模板中的占位符设置值
            notifyRequest.setValue("amount", freezeRecord.getAmount());
//            notifyRequest.setValue("days", (freezeRecord.getEndTime().getTime() - freezeRecord.getBeginTime().getTime()) / (24L * 3600L * 1000L));

            // 填充卖家手机，邮箱信息
            String email = getPartyEmail(notifyRequest.getTargetUserId());
            if (StringUtils.isBlank(email)) {
                logger.warn("---> notifyFreezeSellerMoneyByEmail 资金冻结记录:" + freezeRecord.getId() + " 对应的商家没有设置邮箱，无法发送邮件提醒");
                return;
            }
            if (Objects.equals(email, "x")) {
                logger.warn("---> notifyFreezeSellerMoneyByEmail 资金冻结记录:" + freezeRecord.getId() + " 对应的商家为虚拟用户或对应的商家的邮箱尚未校验有效性，无法发送邮件提醒");
                return;
            }
            notifyRequest.setTargetEmail(email);

            logger.info("---> notifyFreezeSellerMoneyByEmail 短信提醒商家资金被冻结，冻结记录:" + freezeRecord.getId());
            commonNotifyManager.sendNotify(notifyRequest);
            logger.info("---> notifyFreezeSellerMoneyByEmail 完成提醒消息发送，冻结记录:" + freezeRecord.getId());
        } catch (Exception e) {
            logger.error("邮件提醒商家资金被冻结，发送通知消息报错, 冻结记录:" + freezeRecord.getId(), e);
        }
    }

    private void notifyFreezeSellerMoneyByInbox(MoneyFreeze freezeRecord) {
        try {
            String bizType = NotificationBizTypeEnum.INBOX_FREEZE_SELLER_MONEY.getBizType();
            String language = "en_US";
            String targetUserId = freezeRecord.getPartyId().toString();
            String targetTopic = "0";

            DefaultNotifyRequest notifyRequest = new DefaultNotifyRequest();
            notifyRequest.setBizType(bizType);
            notifyRequest.setFromUserId("0");
            notifyRequest.setLanguageType(language);
            notifyRequest.setTargetTopic(targetTopic);
            notifyRequest.setTargetUserId(targetUserId.trim());

            // 根据站内信模板中的占位符设置值
            notifyRequest.setValue("amount", freezeRecord.getAmount());
//            notifyRequest.setValue("days", (freezeRecord.getEndTime().getTime() - freezeRecord.getBeginTime().getTime()) / (24L * 3600L * 1000L));

            logger.info("---> notifyFreezeSellerMoneyByInbox 站内信提醒商家资金被冻结，冻结记录:" + freezeRecord.getId());
            commonNotifyManager.sendNotify(notifyRequest);
            logger.info("---> notifyFreezeSellerMoneyByInbox 完成提醒消息发送，冻结记录:" + freezeRecord.getId());
        } catch (Exception e) {
            logger.error("站内信提醒商家资金被冻结，发送通知消息报错, 冻结记录:" + freezeRecord.getId(), e);
        }
    }

    public void notifyUnFreezeSellerMoney(MoneyFreeze freezeRecord, int sendChannelType) {
        sendChannelType = getNotifyLevel();

        if (sendChannelType == 1) {
            notifyUnFreezeSellerMoneyBySms(freezeRecord);
        } else if (sendChannelType == 2) {
            notifyUnFreezeSellerMoneyByEmail(freezeRecord);
        } else if (sendChannelType == 3) {
            notifyUnFreezeSellerMoneyByInbox(freezeRecord);
        } else if (sendChannelType == 4) {
            // 短信+站内信
            notifyUnFreezeSellerMoneyBySms(freezeRecord);
            notifyUnFreezeSellerMoneyByInbox(freezeRecord);
        } else if (sendChannelType == 5) {
            // 邮件+站内信
            notifyUnFreezeSellerMoneyByEmail(freezeRecord);
            notifyUnFreezeSellerMoneyByInbox(freezeRecord);
        } else if (sendChannelType == 6) {
            // 短信+邮件
            notifyUnFreezeSellerMoneyBySms(freezeRecord);
            notifyUnFreezeSellerMoneyByEmail(freezeRecord);
        } else if (sendChannelType == 7) {
            // 短信+邮件+站内信
            notifyUnFreezeSellerMoneyBySms(freezeRecord);
            notifyUnFreezeSellerMoneyByEmail(freezeRecord);
            notifyUnFreezeSellerMoneyByInbox(freezeRecord);
        }
    }

    private void notifyUnFreezeSellerMoneyBySms(MoneyFreeze freezeRecord) {
        try {
            String bizType = NotificationBizTypeEnum.SMS_UNFREEZE_SELLER_MONEY.getBizType();
            String language = "en_US";
            String targetUserId = freezeRecord.getPartyId().toString();
            String targetTopic = "0";

            DefaultSmsNotifyRequest notifyRequest = new DefaultSmsNotifyRequest();
            notifyRequest.setBizType(bizType);
            notifyRequest.setFromUserId("0");
            notifyRequest.setLanguageType(language);
            notifyRequest.setTargetTopic(targetTopic);
            notifyRequest.setTargetUserId(targetUserId.trim());

            // 根据站内信模板中的占位符设置值
            notifyRequest.setValue("amount", freezeRecord.getAmount());

            // 填充卖家手机，邮箱信息
            String mobileInfo = getPartyMobile(notifyRequest.getTargetUserId());
            if (StringUtils.isBlank(mobileInfo)) {
                logger.warn("---> notifyUnFreezeSellerMoneyBySms 资金解冻记录:" + freezeRecord.getId() + " 对应的商家没有设置手机号码，无法发送短信提醒");
                return;
            }
            if (Objects.equals(mobileInfo, "x")) {
                logger.warn("---> notifyUnFreezeSellerMoneyBySms 资金解冻记录:" + freezeRecord.getId() + " 对应的商家的手机号码还没有校验有效性，无法发送短信提醒");
                return;
            }
            notifyRequest.setMobileInfo(mobileInfo);

            logger.info("---> notifyUnFreezeSellerMoneyBySms 短信提醒商家资金被解冻，冻结记录:" + freezeRecord.getId());
            commonNotifyManager.sendNotify(notifyRequest);
            logger.info("---> notifyUnFreezeSellerMoneyBySms 完成提醒消息发送，冻结记录:" + freezeRecord.getId());
        } catch (Exception e) {
            logger.error("短信提醒商家资金被解冻，发送通知消息报错, 冻结记录:" + freezeRecord.getId(), e);
        }
    }

    private void notifyUnFreezeSellerMoneyByEmail(MoneyFreeze freezeRecord) {
        try {
            String bizType = NotificationBizTypeEnum.EMAIL_UNFREEZE_SELLER_MONEY.getBizType();
            String language = "en_US";
            String targetUserId = freezeRecord.getPartyId().toString();
            String targetTopic = "0";

            DefaultEmailNotifyRequest notifyRequest = new DefaultEmailNotifyRequest();
            notifyRequest.setBizType(bizType);
            notifyRequest.setFromUserId("0");
            notifyRequest.setLanguageType(language);
            notifyRequest.setTargetTopic(targetTopic);
            notifyRequest.setTargetUserId(targetUserId.trim());

            // 根据站内信模板中的占位符设置值
            notifyRequest.setValue("amount", freezeRecord.getAmount());

            // 填充卖家手机，邮箱信息
            String email = getPartyEmail(notifyRequest.getTargetUserId());
            if (StringUtils.isBlank(email)) {
                logger.warn("---> notifyUnFreezeSellerMoneyByEmail 资金冻结记录:" + freezeRecord.getId() + " 对应的商家没有设置邮箱，无法发送邮件提醒");
                return;
            }
            if (Objects.equals(email, "x")) {
                logger.warn("---> notifyUnFreezeSellerMoneyByEmail 资金冻结记录:" + freezeRecord.getId() + " 对应的商家为虚拟用户或对应的商家的邮箱尚未校验有效性，无法发送邮件提醒");
                return;
            }
            notifyRequest.setTargetEmail(email);

            logger.info("---> notifyUnFreezeSellerMoneyByEmail 短信提醒商家资金被解冻，冻结记录:" + freezeRecord.getId());
            commonNotifyManager.sendNotify(notifyRequest);
            logger.info("---> notifyUnFreezeSellerMoneyByEmail 完成提醒消息发送，冻结记录:" + freezeRecord.getId());
        } catch (Exception e) {
            logger.error("邮件提醒商家资金被解冻，发送通知消息报错, 冻结记录:" + freezeRecord.getId(), e);
        }
    }

    private void notifyUnFreezeSellerMoneyByInbox(MoneyFreeze freezeRecord) {
        try {
            String bizType = NotificationBizTypeEnum.INBOX_UNFREEZE_SELLER_MONEY.getBizType();
            String language = "en_US";
            String targetUserId = freezeRecord.getPartyId().toString();
            String targetTopic = "0";

            DefaultNotifyRequest notifyRequest = new DefaultNotifyRequest();
            notifyRequest.setBizType(bizType);
            notifyRequest.setFromUserId("0");
            notifyRequest.setLanguageType(language);
            notifyRequest.setTargetTopic(targetTopic);
            notifyRequest.setTargetUserId(targetUserId.trim());

            // 根据站内信模板中的占位符设置值
            notifyRequest.setValue("amount", freezeRecord.getAmount());

            logger.info("---> notifyUnFreezeSellerMoneyByInbox 站内信提醒商家资金被解冻，冻结记录:" + freezeRecord.getId());
            commonNotifyManager.sendNotify(notifyRequest);
            logger.info("---> notifyUnFreezeSellerMoneyByInbox 完成提醒消息发送，冻结记录:" + freezeRecord.getId());
        } catch (Exception e) {
            logger.error("站内信提醒商家资金被解冻，发送通知消息报错, 冻结记录:" + freezeRecord.getId(), e);
        }
    }

    public void notifyUpdateSellerCreditScore(SellerCredit accCredit, int sendChannelType) {
        sendChannelType = getNotifyLevel();

        if (sendChannelType == 1) {
            notifyUpdateSellerCreditScoreBySms(accCredit);
        } else if (sendChannelType == 2) {
            notifyUpdateSellerCreditScoreByEmail(accCredit);
        } else if (sendChannelType == 3) {
            notifyUpdateSellerCreditScoreByInbox(accCredit);
        } else if (sendChannelType == 4) {
            // 短信+站内信
            notifyUpdateSellerCreditScoreBySms(accCredit);
            notifyUpdateSellerCreditScoreByInbox(accCredit);
        } else if (sendChannelType == 5) {
            // 邮件+站内信
            notifyUpdateSellerCreditScoreByEmail(accCredit);
            notifyUpdateSellerCreditScoreByInbox(accCredit);
        } else if (sendChannelType == 6) {
            // 短信+邮件
            notifyUpdateSellerCreditScoreBySms(accCredit);
            notifyUpdateSellerCreditScoreByEmail(accCredit);
        } else if (sendChannelType == 7) {
            // 短信+邮件+站内信
            notifyUpdateSellerCreditScoreBySms(accCredit);
            notifyUpdateSellerCreditScoreByEmail(accCredit);
            notifyUpdateSellerCreditScoreByInbox(accCredit);
        }
    }

    private void notifyUpdateSellerCreditScoreBySms(SellerCredit accCredit) {
        try {
            String bizType = NotificationBizTypeEnum.SMS_SELLER_CREDIT_UPDATED.getBizType();
            String language = "en_US";
            String targetUserId = accCredit.getSellerId();
            String targetTopic = "0";
            Seller seller = sellerService.getSeller(targetUserId);

            DefaultSmsNotifyRequest notifyRequest = new DefaultSmsNotifyRequest();
            notifyRequest.setBizType(bizType);
            notifyRequest.setFromUserId("0");
            notifyRequest.setLanguageType(language);
            notifyRequest.setTargetTopic(targetTopic);
            notifyRequest.setTargetUserId(targetUserId.trim());

            // 根据站内信模板中的占位符设置值
            // 2023-6-2 caster 当前占位符改成扣除分数，不是最新分数
            notifyRequest.setValue("creditScore", -accCredit.getAccScore());
            //notifyRequest.setValue("creditScore", seller.getCreditScore());

            // 填充卖家手机，邮箱信息
            String mobileInfo = getPartyMobile(notifyRequest.getTargetUserId());
            if (StringUtils.isBlank(mobileInfo)) {
                logger.warn("---> notifyUpdateSellerCreditScoreBySms 店铺信誉分变更记录:" + accCredit.getId() + " 对应的商家没有设置手机号码，无法发送短信提醒");
                return;
            }
            if (Objects.equals(mobileInfo, "x")) {
                logger.warn("---> notifyUpdateSellerCreditScoreBySms 店铺信誉分变更记录:" + accCredit.getId() + " 对应的商家的手机号码还没有校验有效性，无法发送短信提醒");
                return;
            }
            notifyRequest.setMobileInfo(mobileInfo);

            logger.info("---> notifyUpdateSellerCreditScoreBySms 短信提醒商家店铺信誉分变更，变更记录:" + accCredit.getId());
            commonNotifyManager.sendNotify(notifyRequest);
            logger.info("---> notifyUpdateSellerCreditScoreBySms 完成提醒消息发送，信誉分变更记录:" + accCredit.getId());
        } catch (Exception e) {
            logger.error("短信提醒商家店铺信誉分变更，发送通知消息报错, 变更记录:" + accCredit.getId(), e);
        }
    }

    private void notifyUpdateSellerCreditScoreByEmail(SellerCredit accCredit) {
        try {
            String bizType = NotificationBizTypeEnum.EMAIL_SELLER_CREDIT_UPDATED.getBizType();
            String language = "en_US";
            String targetUserId = accCredit.getSellerId();
            String targetTopic = "0";
            Seller seller = sellerService.getSeller(targetUserId);

            DefaultEmailNotifyRequest notifyRequest = new DefaultEmailNotifyRequest();
            notifyRequest.setBizType(bizType);
            notifyRequest.setFromUserId("0");
            notifyRequest.setLanguageType(language);
            notifyRequest.setTargetTopic(targetTopic);
            notifyRequest.setTargetUserId(targetUserId.trim());

            // 根据站内信模板中的占位符设置值
            // 2023-6-2 caster 当前占位符改成扣除分数，不是最新分数
            notifyRequest.setValue("creditScore", -accCredit.getAccScore());
            //notifyRequest.setValue("creditScore", seller.getCreditScore());

            // 填充卖家手机，邮箱信息
            String email = getPartyEmail(notifyRequest.getTargetUserId());
            if (StringUtils.isBlank(email)) {
                logger.warn("---> notifyUpdateSellerCreditScoreByEmail 店铺信誉分变更记录:" + accCredit.getId() + " 对应的商家没有设置邮箱，无法发送邮件提醒");
                return;
            }
            if (Objects.equals(email, "x")) {
                logger.warn("---> notifyUpdateSellerCreditScoreByEmail 店铺信誉分变更记录:" + accCredit.getId() + " 对应的商家为虚拟用户或对应的商家的邮箱尚未校验有效性，无法发送邮件提醒");
                return;
            }
            notifyRequest.setTargetEmail(email);

            logger.info("---> notifyUpdateSellerCreditScoreByEmail 邮件提醒商家店铺信誉分变更，变更记录:" + accCredit.getId());
            commonNotifyManager.sendNotify(notifyRequest);
            logger.info("---> notifyUpdateSellerCreditScoreByEmail 完成提醒消息发送，信誉分变更记录:" + accCredit.getId());
        } catch (Exception e) {
            logger.error("邮件提醒商家店铺信誉分变更，发送通知消息报错, 变更记录:" + accCredit.getId(), e);
        }
    }

    private void notifyUpdateSellerCreditScoreByInbox(SellerCredit accCredit) {
        try {
            String bizType = NotificationBizTypeEnum.INBOX_SELLER_CREDIT_UPDATED.getBizType();
            String language = "en_US";
            String targetUserId = accCredit.getSellerId();
            String targetTopic = "0";

            Seller seller = sellerService.getSeller(targetUserId);

            DefaultNotifyRequest notifyRequest = new DefaultNotifyRequest();
            notifyRequest.setBizType(bizType);
            notifyRequest.setFromUserId("0");
            notifyRequest.setLanguageType(language);
            notifyRequest.setTargetTopic(targetTopic);
            notifyRequest.setTargetUserId(targetUserId.trim());

            // 根据站内信模板中的占位符设置值
            // 2023-6-2 caster 当前占位符改成扣除分数，不是最新分数
            notifyRequest.setValue("creditScore", -accCredit.getAccScore());
            //notifyRequest.setValue("creditScore", seller.getCreditScore());

            logger.info("---> notifyUpdateSellerCreditScoreByInbox 站内信提醒商家店铺信誉分变更，变更记录:" + accCredit.getId());
            commonNotifyManager.sendNotify(notifyRequest);
            logger.info("---> notifyUpdateSellerCreditScoreByInbox 完成提醒消息发送，信誉分变更记录:" + accCredit.getId());
        } catch (Exception e) {
            logger.error("站内信提醒商家店铺信誉分变更，发送通知消息报错, 变更记录:" + accCredit.getId(), e);
        }
    }

    public void sendCaptchaCode(String targetInfo, String fromIp, String imgCaptcha, int sendChannelType) {
        if (sendChannelType == 1) {
            sendCaptchaCodeBySms(targetInfo, fromIp, imgCaptcha);
        } else if (sendChannelType == 2) {
            sendCaptchaCodeByEmail(targetInfo, fromIp, imgCaptcha);
        }
    }

    /**
     * 发送验证码
     * 针对业务：短信登录，
     */
    private void sendCaptchaCodeBySms(String mobileInfo, String fromIp, String imgCaptcha) {
        // 校验图片验证码是否正确

        // 校验 ip 白名单

        // 短信验证码的额外处理

        String smsCaptchCode = null;
        //identifyingCodeService.send(mobileInfo, fromIp);

        if (chcekIp(fromIp)) {// 被封的ip直接返回 不操作
            return;
        }

        /**
         * 是否每次发送的code都不一样
         */
        boolean send_code_always_new = this.sysparaService.find("send_code_always_new").getBoolean();

        Object object = this.identifyingCodeTimeWindowService.getAuthCode(mobileInfo);
        if (object == null || send_code_always_new) {
            Random random = new Random();
            smsCaptchCode = String.valueOf(random.nextInt(999999) % 900000 + 100000);
        } else {
            smsCaptchCode = String.valueOf(object);
        }

        String bizType = NotificationBizTypeEnum.SMS_SEND_VERIFICATION_CODE.getBizType();
        DefaultSmsNotifyRequest bizRequestData = new DefaultSmsNotifyRequest();
        bizRequestData.setCaptchCode(smsCaptchCode);
        bizRequestData.setMobileInfo(mobileInfo);
        bizRequestData.setBizType(bizType);


        commonNotifyManager.sendNotify(bizRequestData);

        // 缓存
        this.identifyingCodeTimeWindowService.putAuthCode(mobileInfo, smsCaptchCode);
        System.out.println("获取验证码：" + mobileInfo + "---" + smsCaptchCode);
//        CodeLog codeLog = new CodeLog();
//        codeLog.setTarget(mobileInfo);
//        codeLog.setLog("发送地址：" + mobileInfo + ",验证码：" + smsCaptchCode + ",ip地址：" + fromIp);
//        codeLog.setCreateTime(new Date());
//        codeLogService.saveSync(codeLog);
    }

    private void sendCaptchaCodeByEmail(String email, String fromIp, String imgCaptcha) {
        // 校验图片验证码是否正确

        // 校验 ip 白名单

        // 短信验证码的额外处理

        String captchCode = null;
        //identifyingCodeService.send(mobileInfo, fromIp);

        if (chcekIp(fromIp)) {// 被封的ip直接返回 不操作
            return;
        }

        /**
         * 是否每次发送的code都不一样
         */
        boolean send_code_always_new = this.sysparaService.find("send_code_always_new").getBoolean();

        Object object = this.identifyingCodeTimeWindowService.getAuthCode(email);
        if (object == null || send_code_always_new) {
            Random random = new Random();
            captchCode = String.valueOf(random.nextInt(999999) % 900000 + 100000);
        } else {
            captchCode = String.valueOf(object);
        }

        String bizType = NotificationBizTypeEnum.EMAIL_SEND_VERIFICATION_CODE.getBizType();
        DefaultEmailNotifyRequest bizRequestData = new DefaultEmailNotifyRequest();
        bizRequestData.setTargetEmail(email);
        bizRequestData.setCaptchCode(captchCode);
        bizRequestData.setBizType(bizType);

        commonNotifyManager.sendNotify(bizRequestData);

        // 缓存
        this.identifyingCodeTimeWindowService.putAuthCode(email, captchCode);
    }

    /**
     * 充值审核通过，提醒用户
     *
     * @param info
     * @param sendChannelType : 消息发送渠道：1-纯短信，2-纯邮件，3-纯站内信，
     *                        4-短信+站内信，5-邮件+站内信，6-短信+邮件，7-短信+邮件+站内信
     */
    public void notifyRechargeSuccess(RechargeData info, int sendChannelType) {
        sendChannelType = getNotifyLevel();

        if (sendChannelType == 1) {
            notifyRechargeSuccessBySms(info);
        } else if (sendChannelType == 2) {
            notifyRechargeSuccessByEmail(info);
        } else if (sendChannelType == 3) {
            notifyRechargeSuccessByInbox(info);
        } else if (sendChannelType == 4) {
            // 短信+站内信
            notifyRechargeSuccessBySms(info);
            notifyRechargeSuccessByInbox(info);
        } else if (sendChannelType == 5) {
            // 邮件+站内信
            notifyRechargeSuccessByEmail(info);
            notifyRechargeSuccessByInbox(info);
        } else if (sendChannelType == 6) {
            // 短信+邮件
            notifyRechargeSuccessBySms(info);
            notifyRechargeSuccessByEmail(info);
        } else if (sendChannelType == 7) {
            // 短信+邮件+站内信
            notifyRechargeSuccessBySms(info);
            notifyRechargeSuccessByEmail(info);
            notifyRechargeSuccessByInbox(info);
        }
    }

    private void notifyRechargeSuccessBySms(RechargeData info) {
        try {
            String bizType = NotificationBizTypeEnum.SMS_RECHARGE_SUCCESS.getBizType();
            String language = "en_US";
            String targetUserId = info.getRechargeUserId();
            String targetTopic = "0";
            Seller seller = sellerService.getSeller(targetUserId);

            DefaultSmsNotifyRequest notifyRequest = new DefaultSmsNotifyRequest();
            notifyRequest.setBizType(bizType);
            notifyRequest.setFromUserId("0");
            notifyRequest.setLanguageType(language);
            notifyRequest.setTargetTopic(targetTopic);
            notifyRequest.setTargetUserId(targetUserId.trim());

            // 根据站内信模板中的占位符设置值
            notifyRequest.setValue("orderAmount", info.getAmount());

            // 填充卖家手机，邮箱信息
            String mobileInfo = getPartyMobile(notifyRequest.getTargetUserId());
            if (StringUtils.isBlank(mobileInfo)) {
                logger.warn("---> notifyRechargeSuccessBySms 充值记录对应的用户:{} 没有设置手机号码，无法发送短信提醒", info.getRechargeUserId());
                return;
            }
            if (Objects.equals(mobileInfo, "x")) {
                logger.warn("---> notifyRechargeSuccessBySms 充值记录对应的用户:{} 的手机号码还没有校验有效性，无法发送短信提醒", info.getRechargeUserId());
                return;
            }
            notifyRequest.setMobileInfo(mobileInfo);

            logger.info("---> notifyRechargeSuccessBySms 短信提醒用户:{} 充值审核通过，涉及金额:{}", info.getRechargeUserId(), info.getAmount());
            commonNotifyManager.sendNotify(notifyRequest);
            logger.info("---> notifyRechargeSuccessBySms 完成提醒消息发送，充值用户:{} 涉及金额:{}", info.getRechargeUserId(), info.getAmount());
        } catch (Exception e) {
            logger.error("短信提醒用户充值审核通过，发送通知消息报错, 充值用户:{} 涉及金额:{}", info.getRechargeUserId(), info.getAmount(), e);
        }
    }

    private void notifyRechargeSuccessByEmail(RechargeData info) {
        try {
            String bizType = NotificationBizTypeEnum.EMAIL_RECHARGE_SUCCESS.getBizType();
            String language = "en_US";
            String targetUserId = info.getRechargeUserId();
            String targetTopic = "0";
            Seller seller = sellerService.getSeller(targetUserId);

            DefaultEmailNotifyRequest notifyRequest = new DefaultEmailNotifyRequest();
            notifyRequest.setBizType(bizType);
            notifyRequest.setFromUserId("0");
            notifyRequest.setLanguageType(language);
            notifyRequest.setTargetTopic(targetTopic);
            notifyRequest.setTargetUserId(targetUserId.trim());

            // 根据站内信模板中的占位符设置值
            notifyRequest.setValue("orderAmount", info.getAmount());

            // 填充卖家手机，邮箱信息
            String email = getPartyEmail(notifyRequest.getTargetUserId());
            if (StringUtils.isBlank(email)) {
                logger.warn("---> notifyRechargeSuccessByEmail 充值记录对应的用户:{} 没有设置邮箱，无法发送邮件提醒", info.getRechargeUserId());
                return;
            }
            if (Objects.equals(email, "x")) {
                logger.warn("---> notifyRechargeSuccessByEmail 充值记录对应的用户:{} 的邮箱还没有校验有效性或该用户为虚拟用户，无法发送邮件提醒", info.getRechargeUserId());
                return;
            }
            notifyRequest.setTargetEmail(email);

            logger.info("---> notifyRechargeSuccessByEmail 邮件提醒用户:{} 充值审核通过，涉及金额:{}", info.getRechargeUserId(), info.getAmount());
            commonNotifyManager.sendNotify(notifyRequest);
            logger.info("---> notifyRechargeSuccessByEmail 完成提醒消息发送，充值用户:{} 涉及金额:{}", info.getRechargeUserId(), info.getAmount());
        } catch (Exception e) {
            logger.error("邮件提醒用户充值审核通过，发送通知消息报错, 充值用户:{} 涉及金额:{}", info.getRechargeUserId(), info.getAmount(), e);
        }
    }

    private void notifyRechargeSuccessByInbox(RechargeData info) {
        try {
            String bizType = NotificationBizTypeEnum.INBOX_RECHARGE_SUCCESS.getBizType();
            String language = "en_US";
            String targetUserId = info.getRechargeUserId();
            String targetTopic = "0";

            DefaultNotifyRequest notifyRequest = new DefaultNotifyRequest();
            notifyRequest.setBizType(bizType);
            notifyRequest.setFromUserId("0");
            notifyRequest.setLanguageType(language);
            notifyRequest.setTargetTopic(targetTopic);
            notifyRequest.setTargetUserId(targetUserId.trim());

            // 根据站内信模板中的占位符设置值
            notifyRequest.setValue("orderAmount", info.getAmount());

            logger.info("---> notifyRechargeSuccessByInbox 站内信提醒用户:{} 充值审核通过，涉及金额:{}", info.getRechargeUserId(), info.getAmount());
            commonNotifyManager.sendNotify(notifyRequest);
            logger.info("---> notifyRechargeSuccessByInbox 完成提醒消息发送，充值用户:{} 涉及金额:{}", info.getRechargeUserId(), info.getAmount());
        } catch (Exception e) {
            logger.error("站内信提醒用户充值审核通过，发送通知消息报错, 充值用户:{} 涉及金额:{}", info.getRechargeUserId(), info.getAmount(), e);
        }
    }

    /**
     * 提现审核通过，提醒用户
     *
     * @param info
     * @param sendChannelType : 消息发送渠道：1-纯短信，2-纯邮件，3-纯站内信，
     *                        4-短信+站内信，5-邮件+站内信，6-短信+邮件，7-短信+邮件+站内信
     */
    public void notifyWithdrawSuccess(WithdrawData info, int sendChannelType) {
        sendChannelType = getNotifyLevel();

        if (sendChannelType == 1) {
            notifyWithdrawSuccessBySms(info);
        } else if (sendChannelType == 2) {
            notifyWithdrawSuccessByEmail(info);
        } else if (sendChannelType == 3) {
            notifyWithdrawSuccessByInbox(info);
        } else if (sendChannelType == 4) {
            // 短信+站内信
            notifyWithdrawSuccessBySms(info);
            notifyWithdrawSuccessByInbox(info);
        } else if (sendChannelType == 5) {
            // 邮件+站内信
            notifyWithdrawSuccessByEmail(info);
            notifyWithdrawSuccessByInbox(info);
        } else if (sendChannelType == 6) {
            // 短信+邮件
            notifyWithdrawSuccessBySms(info);
            notifyWithdrawSuccessByEmail(info);
        } else if (sendChannelType == 7) {
            // 短信+邮件+站内信
            notifyWithdrawSuccessBySms(info);
            notifyWithdrawSuccessByEmail(info);
            notifyWithdrawSuccessByInbox(info);
        }
    }

    /**
     * 买家投诉通过审核，对买家发送消息通知
     * @param complaint
     * @param creditScore 扣除信誉分
     * @param sendChannelType 消息发送渠道：1-纯短信，2-纯邮件，3-纯站内信，
     *                        4-短信+站内信，5-邮件+站内信，6-短信+邮件，7-短信+邮件+站内信
     */
    public void notifyBuyerComplaintSuccess(Complaint complaint,Integer creditScore,int sendChannelType){
        sendChannelType = getNotifyLevel();

        if (sendChannelType == 1) {
            notifyBuyerComplaintSuccessBySms(creditScore,complaint);
        } else if (sendChannelType == 2) {
            notifyBuyerComplaintSuccessByEmail(creditScore, complaint);
        } else if (sendChannelType == 3) {
            notifyBuyerComplaintSuccessByInbox(creditScore, complaint);
        } else if (sendChannelType == 4) {
            // 短信+站内信
            notifyBuyerComplaintSuccessBySms(creditScore,complaint);
            notifyBuyerComplaintSuccessByInbox(creditScore, complaint);
        } else if (sendChannelType == 5) {
            // 邮件+站内信
            notifyBuyerComplaintSuccessByEmail(creditScore, complaint);
            notifyBuyerComplaintSuccessByInbox(creditScore, complaint);
        } else if (sendChannelType == 6) {
            // 短信+邮件
            notifyBuyerComplaintSuccessBySms(creditScore,complaint);
            notifyBuyerComplaintSuccessByEmail(creditScore, complaint);
        } else if (sendChannelType == 7) {
            // 短信+邮件+站内信
            notifyBuyerComplaintSuccessBySms(creditScore,complaint);
            notifyBuyerComplaintSuccessByEmail(creditScore, complaint);
            notifyBuyerComplaintSuccessByInbox(creditScore, complaint);
        }
    }


    /**
     * 提现驳回站内信发送
     * @param withdrawData
     */
    @Override
    public void notifyWithdrawFail(WithdrawData withdrawData) {
        try {
            Party party = partyService.cachePartyBy(withdrawData.getWithdrawUserId(), true);
            if (party.getRoleType() != 1){
                return;
            }

            DefaultNotifyRequest notifyRequest = new DefaultNotifyRequest();
            notifyRequest.setBizType(NotificationBizTypeEnum.INBOX_WITHDRAWAL_AUDIT_FAIL.getBizType());
            notifyRequest.setFromUserId("0");
            notifyRequest.setLanguageType("en_US");
            notifyRequest.setTargetTopic("0");
            notifyRequest.setTargetUserId(withdrawData.getWithdrawUserId());
            // 代表 Withdraw 记录
            notifyRequest.setRefType(1);
            notifyRequest.setRefId(withdrawData.getOrderNo());
            // 设置消息模板中的动态占位符的值
            notifyRequest.setValue("amount", withdrawData.getAmount());
            notifyRequest.setValue("reason", withdrawData.getReason());

            logger.info("---> notifyWithdrawFail 提现驳回站内信发送，提现订单号为:" + withdrawData.getOrderNo());
            commonNotifyManager.sendNotify(notifyRequest);
            logger.info("---> notifyGiveBonusByInbox  提现驳回完成站内信发送，提现订单号为:" + withdrawData.getOrderNo());
        } catch (Exception e) {
            logger.error("提现驳回事件触发站内信报错，提现订单ID为:" + withdrawData.getOrderNo(), e);
        }
    }

    /**
     * 充值驳回站内信发送
     * @param rechargeData
     */
    @Override
    public void notifyRechargeFail(RechargeData rechargeData) {
        try {
            Party party = partyService.cachePartyBy(rechargeData.getRechargeUserId(), true);
            if (party.getRoleType() != 1){
                return;
            }

            DefaultNotifyRequest notifyRequest = new DefaultNotifyRequest();
            notifyRequest.setBizType(NotificationBizTypeEnum.INBOX_RECHARGE_AUDIT_FAIL.getBizType());
            notifyRequest.setFromUserId("0");
            notifyRequest.setLanguageType("en_US");
            notifyRequest.setTargetTopic("0");
            notifyRequest.setTargetUserId(rechargeData.getRechargeUserId());
            // 代表 recharge 记录
            notifyRequest.setRefType(1);
            notifyRequest.setRefId(rechargeData.getOrderNo());
            // 设置消息模板中的动态占位符的值
            notifyRequest.setValue("amount", rechargeData.getAmount());
            notifyRequest.setValue("reason", rechargeData.getReason());

            logger.info("---> notifyRechargeFail 充值驳回站内信发送，充值订单号为:" + rechargeData.getOrderNo());
            commonNotifyManager.sendNotify(notifyRequest);
            logger.info("---> notifyRechargeFail  充值驳回完成站内信发送，充值订单号为:" + rechargeData.getOrderNo());
        } catch (Exception e) {
            logger.error("充值驳回事件触发站内信报错，充值订单号为:" + rechargeData.getOrderNo(), e);
        }
    }

    /**
     * 买家投诉审核被驳回，对买家发送消息通知
     * @param complaint
     * @param sendChannelType 消息发送渠道：1-纯短信，2-纯邮件，3-纯站内信，
     *                        4-短信+站内信，5-邮件+站内信，6-短信+邮件，7-短信+邮件+站内信
     */
    public void notifyBuyerComplaintFail(Complaint complaint,int sendChannelType){
        sendChannelType = getNotifyLevel();

        if (sendChannelType == 1) {
            notifyBuyerComplaintFailBySms(complaint);
        } else if (sendChannelType == 2) {
            notifyBuyerComplaintFailByEmail(complaint);
        } else if (sendChannelType == 3) {
            notifyBuyerComplaintFailByInbox(complaint);
        } else if (sendChannelType == 4) {
            // 短信+站内信
            notifyBuyerComplaintFailByInbox(complaint);
            notifyBuyerComplaintFailBySms(complaint);
        } else if (sendChannelType == 5) {
            // 邮件+站内信
            notifyBuyerComplaintFailByEmail(complaint);
            notifyBuyerComplaintFailByInbox(complaint);
        } else if (sendChannelType == 6) {
            // 短信+邮件
            notifyBuyerComplaintFailBySms(complaint);
            notifyBuyerComplaintFailByEmail(complaint);
        } else if (sendChannelType == 7) {
            // 短信+邮件+站内信
            notifyBuyerComplaintFailBySms(complaint);
            notifyBuyerComplaintFailByEmail(complaint);
            notifyBuyerComplaintFailByInbox(complaint);
        }
    }

    /**
     * 店铺被投诉，通知店铺
     * @param complaint
     * @param sendChannelType 消息发送渠道：1-纯短信，2-纯邮件，3-纯站内信，
     *                        4-短信+站内信，5-邮件+站内信，6-短信+邮件，7-短信+邮件+站内信
     */
    public void notifyStoreComplaintSuccess(Complaint complaint,Integer creditScore,int sendChannelType){
        sendChannelType = getNotifyLevel();

        if (sendChannelType == 1) {
            notifyStoreComplaintSuccessBySms(creditScore, complaint);
        } else if (sendChannelType == 2) {
            notifyStoreComplaintSuccessByEmail(creditScore, complaint);
        } else if (sendChannelType == 3) {
            notifyStoreComplaintSuccessByInbox(creditScore, complaint);
        } else if (sendChannelType == 4) {
            // 短信+站内信
            notifyStoreComplaintSuccessBySms(creditScore, complaint);
            notifyStoreComplaintSuccessByInbox(creditScore, complaint);
        } else if (sendChannelType == 5) {
            // 邮件+站内信
            notifyStoreComplaintSuccessByEmail(creditScore, complaint);
            notifyStoreComplaintSuccessByInbox(creditScore, complaint);
        } else if (sendChannelType == 6) {
            // 短信+邮件
            notifyStoreComplaintSuccessBySms(creditScore, complaint);
            notifyStoreComplaintSuccessByEmail(creditScore, complaint);
        } else if (sendChannelType == 7) {
            // 短信+邮件+站内信
            notifyStoreComplaintSuccessBySms(creditScore, complaint);
            notifyStoreComplaintSuccessByEmail(creditScore, complaint);
            notifyStoreComplaintSuccessByInbox(creditScore, complaint);
        }
    }

    private void notifyWithdrawSuccessBySms(WithdrawData info) {
        try {
            String bizType = NotificationBizTypeEnum.SMS_WITHDRAW_SUCCESS.getBizType();
            String language = "en_US";
            String targetUserId = info.getWithdrawUserId();
            String targetTopic = "0";
            Seller seller = sellerService.getSeller(targetUserId);

            DefaultSmsNotifyRequest notifyRequest = new DefaultSmsNotifyRequest();
            notifyRequest.setBizType(bizType);
            notifyRequest.setFromUserId("0");
            notifyRequest.setLanguageType(language);
            notifyRequest.setTargetTopic(targetTopic);
            notifyRequest.setTargetUserId(targetUserId.trim());

            // 根据站内信模板中的占位符设置值
            notifyRequest.setValue("orderAmount", info.getAmount());

            // 填充卖家手机，邮箱信息
            String mobileInfo = getPartyMobile(notifyRequest.getTargetUserId());
            if (StringUtils.isBlank(mobileInfo)) {
                logger.warn("---> notifyWithdrawSuccessBySms 提现记录对应的用户:{} 没有设置手机号码，无法发送短信提醒", info.getWithdrawUserId());
                return;
            }
            if (Objects.equals(mobileInfo, "x")) {
                logger.warn("---> notifyWithdrawSuccessBySms 提现记录对应的用户:{} 的手机号码还没有校验有效性，无法发送短信提醒", info.getWithdrawUserId());
                return;
            }
            notifyRequest.setMobileInfo(mobileInfo);

            logger.info("---> notifyWithdrawSuccessBySms 短信提醒用户:{} 提现审核通过，涉及金额:{}", info.getWithdrawUserId(), info.getAmount());
            commonNotifyManager.sendNotify(notifyRequest);
            logger.info("---> notifyWithdrawSuccessBySms 完成提醒消息发送，提现用户:{} 涉及金额:{}", info.getWithdrawUserId(), info.getAmount());
        } catch (Exception e) {
            logger.error("短信提醒用户提现审核通过，发送通知消息报错, 提现用户:{} 涉及金额:{}", info.getWithdrawUserId(), info.getAmount(), e);
        }
    }

    private void notifyWithdrawSuccessByEmail(WithdrawData info) {
        try {
            String bizType = NotificationBizTypeEnum.EMAIL_WITHDRAW_SUCCESS.getBizType();
            String language = "en_US";
            String targetUserId = info.getWithdrawUserId();
            String targetTopic = "0";

            DefaultEmailNotifyRequest notifyRequest = new DefaultEmailNotifyRequest();
            notifyRequest.setBizType(bizType);
            notifyRequest.setFromUserId("0");
            notifyRequest.setLanguageType(language);
            notifyRequest.setTargetTopic(targetTopic);
            notifyRequest.setTargetUserId(targetUserId.trim());

            // 根据站内信模板中的占位符设置值
            notifyRequest.setValue("orderAmount", info.getAmount());

            // 填充卖家手机，邮箱信息
            String email = getPartyEmail(notifyRequest.getTargetUserId());
            if (StringUtils.isBlank(email)) {
                logger.warn("---> notifyWithdrawSuccessByEmail 提现记录对应的用户:{} 没有设置邮箱，无法发送邮件提醒", info.getWithdrawUserId());
                return;
            }
            if (Objects.equals(email, "x")) {
                logger.warn("---> notifyWithdrawSuccessByEmail 提现记录对应的用户:{} 的邮箱还没有校验有效性或该用户为虚拟用户，无法发送邮件提醒", info.getWithdrawUserId());
                return;
            }
            notifyRequest.setTargetEmail(email);

            logger.info("---> notifyWithdrawSuccessByEmail 邮件提醒用户:{} 提现审核通过，涉及金额:{}", info.getWithdrawUserId(), info.getAmount());
            commonNotifyManager.sendNotify(notifyRequest);
            logger.info("---> notifyWithdrawSuccessByEmail 完成提醒消息发送，提现用户:{} 涉及金额:{}", info.getWithdrawUserId(), info.getAmount());
        } catch (Exception e) {
            logger.error("邮件提醒用户提现审核通过，发送通知消息报错, 提现用户:{} 涉及金额:{}", info.getWithdrawUserId(), info.getAmount(), e);
        }
    }

    private void notifyWithdrawSuccessByInbox(WithdrawData info) {
        try {
            String bizType = NotificationBizTypeEnum.INBOX_WITHDRAW_SUCCESS.getBizType();
            String language = "en_US";
            String targetUserId = info.getWithdrawUserId();
            String targetTopic = "0";

            DefaultNotifyRequest notifyRequest = new DefaultNotifyRequest();
            notifyRequest.setBizType(bizType);
            notifyRequest.setFromUserId("0");
            notifyRequest.setLanguageType(language);
            notifyRequest.setTargetTopic(targetTopic);
            notifyRequest.setTargetUserId(targetUserId.trim());

            // 根据站内信模板中的占位符设置值
            notifyRequest.setValue("orderAmount", info.getAmount());

            logger.info("---> notifyWithdrawSuccessByInbox 站内信提醒用户:{} 提现审核通过，涉及金额:{}", info.getWithdrawUserId(), info.getAmount());
            commonNotifyManager.sendNotify(notifyRequest);
            logger.info("---> notifyWithdrawSuccessByInbox 完成提醒消息发送，提现用户:{} 涉及金额:{}", info.getWithdrawUserId(), info.getAmount());
        } catch (Exception e) {
            logger.error("站内信提醒用户提现审核通过，发送通知消息报错, 提现用户:{} 涉及金额:{}", info.getWithdrawUserId(), info.getAmount(), e);
        }
    }

    @Override
    public void notifyStoreAuditByInbox(String partId, int status, String name, String msg) {
        String client_name = null;
        try {
            client_name = sysparaService.find("mall_client_name").getValue();
        } catch (Exception e) {

        }
        boolean flag = "Argos2".equals(client_name);
        if (status == 3) {
            notifyStoreAuditFailByInbox(partId, name, msg);
            if (flag){
                notifyStoreAuditFailBySms(partId, name, msg);
            }
//            notifyStoreAuditFailByEmail(partId, name, msg);
        } else {
            notifyStoreAuditSuccessByInbox(partId, name);
            if (flag){
                notifyStoreAuditSuccessBySms(partId, name);
            }
//            notifyStoreAuditSuccessByEmail(partId, name);
        }
    }

    /**
     * 站内信买家投诉审核成功通知
     *
     * @param complaint
     * @param creditScore
     */
    private void notifyBuyerComplaintSuccessByInbox(Integer creditScore, Complaint complaint) {
        try {
            String bizType = NotificationBizTypeEnum.INBOX_BUYER_COMPLIANT_SUCCESS.getBizType();
            DefaultNotifyRequest notifyRequest = new DefaultNotifyRequest();
            notifyRequest.setBizType(bizType);
            notifyRequest.setFromUserId("0");
            notifyRequest.setTargetTopic("0");
            notifyRequest.setTargetUserId(complaint.getPartyId());
            notifyRequest.setLanguageType("en_US");

            Seller seller = this.sellerService.getSeller(complaint.getStoreId());
            // 根据站内信模板中的占位符设置值
            notifyRequest.setValue("storeName", seller.getName());
            notifyRequest.setValue("creditScore", creditScore);
            Complaint.ComplaintTypeEnum complaintTypeEnum = Complaint.ComplaintTypeEnum.typeOf(complaint.getComplaintStatus());
            notifyRequest.setValue("complaintReason", complaintTypeEnum.getDescription());
            logger.info("---> notifyBuyerComplaintSuccessByInbox 站内信提醒用户:{} 买家投诉审核成功。", complaint.getPartyId());
            commonNotifyManager.sendNotify(notifyRequest);
            logger.info("---> notifyBuyerComplaintSuccessByInbox 完成提醒消息发送，买家投诉审核成功审核通过，用户:{} ", complaint.getPartyId());
        } catch (Exception e) {
            logger.error("站内信提醒买家投诉审核通过，发送通知消息报错, 发起投诉用户:{} ", complaint.getPartyId(), e);
        }
    }

    /**
     * 邮件买家投诉审核成功通知
     *
     * @param complaint
     * @param creditScore
     */
    private void notifyBuyerComplaintSuccessByEmail(Integer creditScore, Complaint complaint) {
        try {
            String bizType = NotificationBizTypeEnum.EMAIL_BUYER_COMPLIANT_SUCCESS.getBizType();
            DefaultEmailNotifyRequest notifyRequest = new DefaultEmailNotifyRequest();
            notifyRequest.setBizType(bizType);
            notifyRequest.setFromUserId("0");
            notifyRequest.setTargetTopic("0");
            notifyRequest.setTargetUserId(complaint.getPartyId());
            notifyRequest.setLanguageType("en_US");

            Seller seller = this.sellerService.getSeller(complaint.getStoreId());
            // 根据邮件模板中的占位符设置值
            notifyRequest.setValue("storeName", seller.getName());
            notifyRequest.setValue("creditScore", creditScore);
            Complaint.ComplaintTypeEnum complaintTypeEnum = Complaint.ComplaintTypeEnum.typeOf(complaint.getComplaintStatus());
            notifyRequest.setValue("complaintReason", complaintTypeEnum.getDescription());

            // 填充卖家手机，邮箱信息
            String email = getPartyEmail(notifyRequest.getTargetUserId());

            if (StringUtils.isBlank(email)) {
                logger.warn("---> notifyBuyerComplaintSuccessByEmail 投诉成功，发起投诉对应的用户:{} 没有设置邮箱，无法发送邮件提醒", complaint.getPartyId());
                return;
            }
            if (Objects.equals(email, "x")) {
                logger.warn("---> notifyBuyerComplaintSuccessByEmail 投诉成功，发起投诉对应的用户:{} 的邮箱还没有校验有效性或该用户为虚拟用户，无法发送邮件提醒", complaint.getPartyId());
                return;
            }
            notifyRequest.setTargetEmail(email);

            logger.info("---> notifyBuyerComplaintSuccessByEmail 邮件提醒用户:{} 买家投诉审核成功。", complaint.getPartyId());
            commonNotifyManager.sendNotify(notifyRequest);
            logger.info("---> notifyBuyerComplaintSuccessByEmail 完成邮件提醒消息发送，买家投诉审核通过，用户:{} ", complaint.getPartyId());
        } catch (Exception e) {
            logger.error("邮件提醒买家投诉审核通过，发送通知消息报错, 发起投诉用户:{} ", complaint.getPartyId(), e);
        }
    }

    /**
     * 短信-用户投诉卖家成功通知买家
     * @param creditScore
     * @param complaint
     */
    private void notifyBuyerComplaintSuccessBySms(Integer creditScore, Complaint complaint) {
        try {
            String bizType = NotificationBizTypeEnum.SMS_BUYER_COMPLIANT_SUCCESS.getBizType();

            DefaultSmsNotifyRequest notifyRequest = new DefaultSmsNotifyRequest();
            notifyRequest.setBizType(bizType);
            notifyRequest.setFromUserId("0");
            notifyRequest.setLanguageType("en_US");
            notifyRequest.setTargetTopic("0");
            notifyRequest.setTargetUserId(complaint.getPartyId());

            Seller seller = this.sellerService.getSeller(complaint.getStoreId());
            // 根据手机模板中的占位符设置值
            notifyRequest.setValue("storeName", seller.getName());
            notifyRequest.setValue("creditScore", creditScore);
            Complaint.ComplaintTypeEnum complaintTypeEnum = Complaint.ComplaintTypeEnum.typeOf(complaint.getComplaintStatus());
            notifyRequest.setValue("complaintReason", complaintTypeEnum.getDescription());


            // 填充买家手机信息
            String mobileInfo = getPartyMobile(notifyRequest.getTargetUserId());
            if (StringUtils.isBlank(mobileInfo)) {
                logger.warn("---> notifyBuyerComplaintSuccessBySms 对应的用户:{} 没有设置手机号码，无法发送短信提醒", complaint.getPartyId());
                return;
            }
            if (Objects.equals(mobileInfo, "x")) {
                logger.warn("---> notifyBuyerComplaintSuccessBySms 对应的用户:{} 的手机号码还没有校验有效性，无法发送短信提醒", complaint.getPartyId());
                return;
            }
            notifyRequest.setMobileInfo(mobileInfo);

            logger.info("---> notifyBuyerComplaintSuccessBySms 短信提醒用户:{} 用户投诉审核通过。", complaint.getPartyId());
            commonNotifyManager.sendNotify(notifyRequest);
            logger.info("---> notifyBuyerComplaintSuccessBySms 完成短信提醒消息发送，用户审核通过，用户:{} ", complaint.getPartyId());
        } catch (Exception e) {
            logger.error("短信提醒用户投诉审核通过，发送通知消息报错, 发起投诉用户:{} ", complaint.getPartyId(), e);
        }
    }

    /**
     * 站内信买家投诉审核失败通知买家
     *
     * @param complaint
     */
    private void notifyBuyerComplaintFailByInbox(Complaint complaint) {
        try {
            String bizType = NotificationBizTypeEnum.INBOX_BUYER_COMPLIANT_FAIL.getBizType();
            DefaultNotifyRequest notifyRequest = new DefaultNotifyRequest();
            notifyRequest.setBizType(bizType);
            notifyRequest.setFromUserId("0");
            notifyRequest.setTargetTopic("0");
            notifyRequest.setTargetUserId(complaint.getPartyId());
            notifyRequest.setLanguageType("en_US");

            Seller seller = this.sellerService.getSeller(complaint.getStoreId());
            // 根据站内信模板中的占位符设置值
            notifyRequest.setValue("storeName", seller.getName());
            notifyRequest.setValue("rejectionReason", complaint.getRemark());
            Complaint.ComplaintTypeEnum complaintTypeEnum = Complaint.ComplaintTypeEnum.typeOf(complaint.getComplaintStatus());
            notifyRequest.setValue("complaintReason", complaintTypeEnum.getDescription());
            logger.info("---> notifyBuyerComplaintFailByInbox 站内信提醒用户:{} 买家投诉被驳回。", complaint.getPartyId());
            commonNotifyManager.sendNotify(notifyRequest);
            logger.info("---> notifyBuyerComplaintFailByInbox 完成提醒消息发送，买家投诉被驳回，用户:{} ", complaint.getPartyId());
        } catch (Exception e) {
            logger.error("站内信提醒买家买家投诉被驳回，发送通知消息报错, 发起投诉用户:{} ", complaint.getPartyId(), e);
        }
    }

    /**
     * 邮件买家投诉审核失败通知买家
     *
     * @param complaint
     */
    private void notifyBuyerComplaintFailByEmail(Complaint complaint) {
        try {
            String bizType = NotificationBizTypeEnum.EMAIL_BUYER_COMPLIANT_FAIL.getBizType();
            DefaultEmailNotifyRequest notifyRequest = new DefaultEmailNotifyRequest();
            notifyRequest.setBizType(bizType);
            notifyRequest.setFromUserId("0");
            notifyRequest.setTargetTopic("0");
            notifyRequest.setTargetUserId(complaint.getPartyId());
            notifyRequest.setLanguageType("en_US");

            Seller seller = this.sellerService.getSeller(complaint.getStoreId());
            // 根据邮件模板中的占位符设置值
            notifyRequest.setValue("storeName", seller.getName());
            notifyRequest.setValue("rejectionReason", complaint.getRemark());
            Complaint.ComplaintTypeEnum complaintTypeEnum = Complaint.ComplaintTypeEnum.typeOf(complaint.getComplaintStatus());
            notifyRequest.setValue("complaintReason", complaintTypeEnum.getDescription());

            // 填充卖家手机，邮箱信息
            String email = getPartyEmail(notifyRequest.getTargetUserId());

            if (StringUtils.isBlank(email)) {
                logger.warn("---> notifyBuyerComplaintSuccessByEmail 投诉成功对应的用户:{} 没有设置邮箱，无法发送邮件提醒", complaint.getPartyId());
                return;
            }
            if (Objects.equals(email, "x")) {
                logger.warn("---> notifyBuyerComplaintSuccessByEmail 投诉成功对应的用户:{} 的邮箱还没有校验有效性或该用户为虚拟用户，无法发送邮件提醒", complaint.getPartyId());
                return;
            }
            notifyRequest.setTargetEmail(email);

            logger.info("---> notifyBuyerComplaintSuccessByEmail 邮件提醒用户:{} 用户投诉审核被驳回。", complaint.getPartyId());
            commonNotifyManager.sendNotify(notifyRequest);
            logger.info("---> notifyBuyerComplaintSuccessByEmail 完成邮件提醒消息发送，用户投诉审核被驳回，用户:{} ", complaint.getPartyId());
        } catch (Exception e) {
            logger.error("邮件提醒买家投诉审核被驳回，发送通知消息报错, 发起投诉用户:{} ", complaint.getPartyId(), e);
        }
    }

    /**
     * 短信-用户投诉卖家失败通知买家
     * @param complaint
     */
    private void notifyBuyerComplaintFailBySms(Complaint complaint) {
        try {
            String bizType = NotificationBizTypeEnum.SMS_BUYER_COMPLIANT_FAIL.getBizType();

            DefaultSmsNotifyRequest notifyRequest = new DefaultSmsNotifyRequest();
            notifyRequest.setBizType(bizType);
            notifyRequest.setFromUserId("0");
            notifyRequest.setLanguageType("en_US");
            notifyRequest.setTargetTopic("0");
            notifyRequest.setTargetUserId(complaint.getPartyId());

            Seller seller = this.sellerService.getSeller(complaint.getStoreId());
            // 根据手机模板中的占位符设置值
            notifyRequest.setValue("storeName", seller.getName());
            notifyRequest.setValue("rejectionReason", complaint.getRemark());
            Complaint.ComplaintTypeEnum complaintTypeEnum = Complaint.ComplaintTypeEnum.typeOf(complaint.getComplaintStatus());
            notifyRequest.setValue("complaintReason", complaintTypeEnum.getDescription());

            // 填充买家手机信息
            String mobileInfo = getPartyMobile(notifyRequest.getTargetUserId());
            if (StringUtils.isBlank(mobileInfo)) {
                logger.warn("---> notifyBuyerComplaintFailBySms 对应的用户:{} 没有设置手机号码，无法发送短信提醒", complaint.getPartyId());
                return;
            }
            if (Objects.equals(mobileInfo, "x")) {
                logger.warn("---> notifyBuyerComplaintFailBySms 对应的用户:{} 的手机号码还没有校验有效性，无法发送短信提醒", complaint.getPartyId());
                return;
            }
            notifyRequest.setMobileInfo(mobileInfo);

            logger.info("---> notifyBuyerComplaintFailBySms 短信提醒用户:{} 用户投诉审核被驳回。", complaint.getPartyId());
            commonNotifyManager.sendNotify(notifyRequest);
            logger.info("---> notifyBuyerComplaintFailBySms 完成短信提醒消息发送，用户投诉审核被驳回，用户:{} ", complaint.getPartyId());
        } catch (Exception e) {
            logger.error("短信提醒用户投诉审核通过，发送通知消息报错, 发起投诉用户:{} ", complaint.getPartyId(), e);
        }
    }

    /**
     * 店铺被投诉成功通知-站内信
     *
     * @param complaint
     * @param creditScore
     */
    private void notifyStoreComplaintSuccessByInbox(Integer creditScore, Complaint complaint) {
        try {
            String bizType = NotificationBizTypeEnum.INBOX_STORE_COMPLIANT_SUCCESS.getBizType();
            DefaultNotifyRequest notifyRequest = new DefaultNotifyRequest();
            notifyRequest.setBizType(bizType);
            notifyRequest.setFromUserId(complaint.getPartyId());
            notifyRequest.setTargetTopic("0");
            notifyRequest.setTargetUserId(complaint.getStoreId());
            notifyRequest.setLanguageType("en_US");

            Seller seller = this.sellerService.getSeller(complaint.getStoreId());
            // 根据站内信模板中的占位符设置值
            notifyRequest.setValue("storeName", seller.getName());
            notifyRequest.setValue("creditScore", creditScore);
            Complaint.ComplaintTypeEnum complaintTypeEnum = Complaint.ComplaintTypeEnum.typeOf(complaint.getComplaintStatus());
            notifyRequest.setValue("complaintReason", complaintTypeEnum.getDescription());
            logger.info("---> notifyStoreComplaintSuccessByInbox 站内信提醒用户:{} 店铺被投诉通知。", complaint.getStoreId());
            commonNotifyManager.sendNotify(notifyRequest);
            logger.info("---> notifyStoreComplaintSuccessByInbox 完成提醒消息发送，店铺被投诉通知，用户:{} ", complaint.getStoreId());
        } catch (Exception e) {
            logger.error("notifyStoreComplaintSuccessByInbox站内信提醒店铺被投诉通知，发送通知消息报错, 发起投诉用户:{} ", complaint.getPartyId(), e);
        }
    }

    /**
     * 店铺被投诉通知-邮件
     *
     * @param complaint
     * @param creditScore
     */
    private void notifyStoreComplaintSuccessByEmail(Integer creditScore, Complaint complaint) {
        try {
            String bizType = NotificationBizTypeEnum.EMAIL_STORE_COMPLIANT_SUCCESS.getBizType();
            DefaultEmailNotifyRequest notifyRequest = new DefaultEmailNotifyRequest();
            notifyRequest.setBizType(bizType);
            notifyRequest.setFromUserId(complaint.getPartyId());
            notifyRequest.setTargetTopic("0");
            notifyRequest.setTargetUserId(complaint.getStoreId());
            notifyRequest.setLanguageType("en_US");

            Seller seller = this.sellerService.getSeller(complaint.getStoreId());
            // 根据邮件模板中的占位符设置值
            notifyRequest.setValue("storeName", seller.getName());
            notifyRequest.setValue("creditScore", creditScore);
            Complaint.ComplaintTypeEnum complaintTypeEnum = Complaint.ComplaintTypeEnum.typeOf(complaint.getComplaintStatus());
            notifyRequest.setValue("complaintReason", complaintTypeEnum.getDescription());

            // 填充卖家手机，邮箱信息
            String email = getPartyEmail(notifyRequest.getTargetUserId());

            if (StringUtils.isBlank(email)) {
                logger.warn("---> notifyStoreComplaintSuccessByEmail 投诉成功对应的用户:{} 没有设置邮箱，无法发送邮件提醒",complaint.getStoreId());
                return;
            }
            if (Objects.equals(email, "x")) {
                logger.warn("---> notifyStoreComplaintSuccessByEmail 投诉成功对应的用户:{} 的邮箱还没有校验有效性或该用户为虚拟用户，无法发送邮件提醒", complaint.getStoreId());
                return;
            }
            notifyRequest.setTargetEmail(email);

            logger.info("---> notifyBuyerComplaintSuccessByEmail 邮件提醒用户:{} 店铺被投诉通知。",complaint.getStoreId());
            commonNotifyManager.sendNotify(notifyRequest);
            logger.info("---> notifyBuyerComplaintSuccessByEmail 完成邮件提醒消息发送，店铺被投诉通知，被投诉店铺:{} ",complaint.getStoreId());
        } catch (Exception e) {
            logger.error("邮件提醒店铺被投诉通知，发送通知消息报错, 被投诉店铺:{} ",complaint.getStoreId(), e);
        }
    }

    /**
     * 店铺被投诉通知-短信
     * @param creditScore
     * @param complaint
     */
    private void notifyStoreComplaintSuccessBySms(Integer creditScore, Complaint complaint) {
        try {
            String bizType = NotificationBizTypeEnum.SMS_STORE_COMPLIANT_SUCCESS.getBizType();

            DefaultSmsNotifyRequest notifyRequest = new DefaultSmsNotifyRequest();
            notifyRequest.setBizType(bizType);
            notifyRequest.setFromUserId(complaint.getPartyId());
            notifyRequest.setLanguageType("en_US");
            notifyRequest.setTargetTopic("0");
            notifyRequest.setTargetUserId(complaint.getStoreId());

            Seller seller = this.sellerService.getSeller(complaint.getStoreId());
            // 根据手机模板中的占位符设置值
            notifyRequest.setValue("storeName", seller.getName());
            notifyRequest.setValue("creditScore", creditScore);
            Complaint.ComplaintTypeEnum complaintTypeEnum = Complaint.ComplaintTypeEnum.typeOf(complaint.getComplaintStatus());
            notifyRequest.setValue("complaintReason", complaintTypeEnum.getDescription());

            // 填充买家手机信息
            String mobileInfo = getPartyMobile(notifyRequest.getTargetUserId());
            if (StringUtils.isBlank(mobileInfo)) {
                logger.warn("---> notifyStoreComplaintSuccessBySms 对应的用户:{} 没有设置手机号码，无法发送短信提醒", complaint.getStoreId());
                return;
            }
            if (Objects.equals(mobileInfo, "x")) {
                logger.warn("---> notifyStoreComplaintSuccessBySms 对应的用户:{} 的手机号码还没有校验有效性，无法发送短信提醒", complaint.getStoreId());
                return;
            }
            notifyRequest.setMobileInfo(mobileInfo);

            logger.info("---> notifyStoreComplaintSuccessBySms 短信提醒用户:{} 店铺被投诉。",complaint.getStoreId());
            commonNotifyManager.sendNotify(notifyRequest);
            logger.info("---> notifyStoreComplaintSuccessBySms 完成短信提醒消息发送，店铺被投诉，用户:{} ",complaint.getStoreId());
        } catch (Exception e) {
            logger.error("短信提醒店铺被投诉通知，发送通知消息报错, 发起投诉用户:{} ",complaint.getStoreId(), e);
        }
    }

    /**
     * 站内信店铺审核成功通知
     *
     * @param partId 商户Id
     */
    private void notifyStoreAuditSuccessByInbox(String partId, String name) {
        try {
            String bizType = NotificationBizTypeEnum.INBOX_STORE_AUDIT_SUCCESS.getBizType();
            DefaultNotifyRequest notifyRequest = new DefaultNotifyRequest();
            notifyRequest.setBizType(bizType);
            notifyRequest.setFromUserId("0");
            notifyRequest.setTargetTopic("0");
            notifyRequest.setTargetUserId(partId.trim());
            notifyRequest.setLanguageType("en_US");

            // 根据站内信模板中的占位符设置值
            notifyRequest.setValue("shopName", name);
            logger.info("---> notifyWithdrawSuccessByInbox 站内信提醒用户:{} 店铺审核通过。", partId);
            commonNotifyManager.sendNotify(notifyRequest);
            logger.info("---> notifyWithdrawSuccessByInbox 完成提醒消息发送，店铺审核通过，用户:{} ", partId);

//            短信发送审核通过

        } catch (Exception e) {
            logger.error("站内信提醒用户店铺审核通过，发送通知消息报错, 店铺审核用户:{} ", partId, e);
        }
    }

    /**
     * 站内信店铺审核失败通知
     *
     * @param partId 商户Id
     */
    private void notifyStoreAuditFailByInbox(String partId, String name, String msg) {
        try {
            String bizType = NotificationBizTypeEnum.INBOX_STORE_AUDIT_FAIL.getBizType();
            DefaultNotifyRequest notifyRequest = new DefaultNotifyRequest();
            notifyRequest.setBizType(bizType);
            notifyRequest.setFromUserId("0");
            notifyRequest.setTargetTopic("0");
            notifyRequest.setTargetUserId(partId.trim());
            notifyRequest.setLanguageType("en_US");

            // 根据站内信模板中的占位符设置值
            notifyRequest.setValue("shopName", name);
            notifyRequest.setValue("reason", msg);
            logger.info("---> notifyWithdrawSuccessByInbox 站内信提醒用户:{} 店铺审核不通过。", partId);
            commonNotifyManager.sendNotify(notifyRequest);
            logger.info("---> notifyWithdrawSuccessByInbox 完成提醒消息发送，店铺审核不通过，用户:{} ", partId);
        } catch (Exception e) {
            logger.error("站内信提醒用户店铺审核不通过，发送通知消息报错, 店铺审核用户:{} ", partId, e);
        }
    }

    /**
     * 店铺审核失败通知-短信
     *
     * @param partId 商户Id
     */
    private void notifyStoreAuditFailBySms(String partId, String name, String msg) {
        try {
            String bizType = NotificationBizTypeEnum.SMS_STORE_AUDIT_FAIL.getBizType();
            String language = "en_US";
            String targetUserId = partId;
            String targetTopic = "0";

            DefaultSmsNotifyRequest notifyRequest = new DefaultSmsNotifyRequest();
            notifyRequest.setBizType(bizType);
            notifyRequest.setFromUserId("0");
            notifyRequest.setLanguageType(language);
            notifyRequest.setTargetTopic(targetTopic);
            notifyRequest.setTargetUserId(targetUserId.trim());

            // 根据站内信模板中的占位符设置值
            notifyRequest.setValue("shopName", name);
            notifyRequest.setValue("reason", msg);

            // 填充卖家手机信息
            String mobileInfo = getPartyMobile(notifyRequest.getTargetUserId());
            if (StringUtils.isBlank(mobileInfo)) {
                logger.warn("---> notifyStoreAuditFailBySms 店铺对应的用户:{} 没有设置手机号码，无法发送短信提醒", partId);
                return;
            }
            if (Objects.equals(mobileInfo, "x")) {
                logger.warn("---> notifyStoreAuditFailBySms 店铺对应的用户:{} 的手机号码还没有校验有效性，无法发送短信提醒", partId);
                return;
            }
            notifyRequest.setMobileInfo(mobileInfo);

            logger.info("---> notifyStoreAuditFailBySms 短信提醒用户:{} 店铺审核不通过", partId);
            commonNotifyManager.sendNotify(notifyRequest);
            logger.info("---> notifyStoreAuditFailBySms 完成短信提醒用户:{} 店铺审核不通过", partId);
        } catch (Exception e) {
            logger.error("短信提醒用户店铺审核不通过，发送通知消息报错, 被审核用户:{} ", partId, e);
        }
    }

    /**
     * 店铺审核成功通知-短信
     *
     * @param partId 商户Id
     */
    private void notifyStoreAuditSuccessBySms(String partId, String name) {
        try {
            String bizType = NotificationBizTypeEnum.SMS_STORE_AUDIT_SUCCESS.getBizType();

            DefaultSmsNotifyRequest notifyRequest = new DefaultSmsNotifyRequest();
            notifyRequest.setBizType(bizType);
            notifyRequest.setFromUserId("0");
            notifyRequest.setLanguageType("en_US");
            notifyRequest.setTargetTopic("0");
            notifyRequest.setTargetUserId(partId);

            // 根据站内信模板中的占位符设置值
            notifyRequest.setValue("shopName", name);

            // 填充卖家手机信息
            String mobileInfo = getPartyMobile(notifyRequest.getTargetUserId());
            if (StringUtils.isBlank(mobileInfo)) {
                logger.warn("---> notifyStoreAuditFailBySms 店铺对应的用户:{} 没有设置手机号码，无法发送短信提醒", partId);
                return;
            }
            if (Objects.equals(mobileInfo, "x")) {
                logger.warn("---> notifyStoreAuditFailBySms 店铺对应的用户:{} 的手机号码还没有校验有效性，无法发送短信提醒", partId);
                return;
            }
            notifyRequest.setMobileInfo(mobileInfo);

            logger.info("---> notifyStoreAuditSuccessBySms 短信提醒用户:{} 店铺审核通过。", partId);
            commonNotifyManager.sendNotify(notifyRequest);
            logger.info("---> notifyStoreAuditSuccessBySms 完成短信提醒消息发送，店铺审核通过，用户:{} ", partId);
//            短信发送审核通过
        } catch (Exception e) {
            logger.error("短信提醒用户店铺审核通过，发送通知消息报错, 店铺审核用户:{} ", partId, e);
        }
    }

    /**
     * 店铺审核成功通知-邮件
     *
     * @param partId 商户Id
     */
    private void notifyStoreAuditFailByEmail(String partId, String name, String msg) {
        try {
            String bizType = NotificationBizTypeEnum.EMAIL_STORE_AUDIT_FAIL.getBizType();
            String language = "en_US";
            String targetUserId = partId;
            String targetTopic = "0";

            DefaultEmailNotifyRequest notifyRequest = new DefaultEmailNotifyRequest();
            notifyRequest.setBizType(bizType);
            notifyRequest.setFromUserId("0");
            notifyRequest.setLanguageType(language);
            notifyRequest.setTargetTopic(targetTopic);
            notifyRequest.setTargetUserId(targetUserId.trim());

            // 填充卖家手机，邮箱信息
            String email = getPartyEmail(notifyRequest.getTargetUserId());

            if (StringUtils.isBlank(email)) {
                logger.warn("---> notifyRechargeSuccessByEmail 充值记录对应的用户:{} 没有设置邮箱，无法发送邮件提醒", partId);
                return;
            }
            if (Objects.equals(email, "x")) {
                logger.warn("---> notifyRechargeSuccessByEmail 充值记录对应的用户:{} 的邮箱还没有校验有效性或该用户为虚拟用户，无法发送邮件提醒", partId);
                return;
            }
            notifyRequest.setTargetEmail(email);

            // 根据站内信模板中的占位符设置值
            notifyRequest.setValue("shop_name", name);
            notifyRequest.setValue("reason", msg);

            logger.info("---> notifyStoreAuditFailBySms 短信提醒用户:{} 店铺审核不通过", partId);
            commonNotifyManager.sendNotify(notifyRequest);
            logger.info("---> notifyStoreAuditFailBySms 完成短信提醒用户:{} 店铺审核不通过", partId);
        } catch (Exception e) {
            logger.error("短信提醒用户店铺审核不通过，发送通知消息报错, 被审核用户:{} ", partId, e);
        }
    }

    /**
     * 站内信店铺审核成功通知-邮件
     *
     * @param partId 商户Id
     */
    private void notifyStoreAuditSuccessByEmail(String partId, String name) {
        try {
            String bizType = NotificationBizTypeEnum.EMAIL_STORE_AUDIT_SUCCESS.getBizType();
            String language = "en_US";
            String targetUserId = partId;
            String targetTopic = "0";

            DefaultEmailNotifyRequest notifyRequest = new DefaultEmailNotifyRequest();
            notifyRequest.setBizType(bizType);
            notifyRequest.setFromUserId("0");
            notifyRequest.setLanguageType(language);
            notifyRequest.setTargetTopic(targetTopic);
            notifyRequest.setTargetUserId(targetUserId.trim());

            // 填充卖家手机，邮箱信息
            String email = getPartyEmail(notifyRequest.getTargetUserId());

            if (StringUtils.isBlank(email)) {
                logger.warn("---> notifyStoreAuditSuccessByEmail 店铺审核通过对应的用户:{} 没有设置邮箱，无法发送邮件提醒", partId);
                return;
            }
            if (Objects.equals(email, "x")) {
                logger.warn("---> notifyStoreAuditSuccessByEmail 店铺审核通过对应的用户:{} 的邮箱还没有校验有效性或该用户为虚拟用户，无法发送邮件提醒", partId);
                return;
            }
            notifyRequest.setTargetEmail(email);

            // 根据站内信模板中的占位符设置值
            notifyRequest.setValue("shop_name", name);
            logger.info("---> notifyWithdrawSuccessByInbox 邮件提醒用户:{} 店铺审核通过。", partId);
            commonNotifyManager.sendNotify(notifyRequest);
            logger.info("---> notifyWithdrawSuccessByInbox 完成邮件提醒消息发送，店铺审核通过，用户:{} ", partId);
//            短信发送审核通过
        } catch (Exception e) {
            logger.error("邮件提醒用户店铺审核通过，发送通知消息报错, 店铺审核用户:{} ", partId, e);
        }
    }

    /**
     * 返回true:ip已被封， false：ip正常
     *
     * @param ip
     * @return
     */
    private boolean chcekIp(String ip) {
        String check_send_count = sysparaService.find("send_code_check_ip").getValue();
        if (!"true".equals(check_send_count))
            return false;// 不为1时 未开启，直接返回false不做处理
        if (blacklistIpTimeWindow.getBlackIp(ip) != null)
            return true;// ip被封，不发送

        if (sendCountTimeWindow.getIpSend(ip) != null) {
            Integer count = ipCache.get(ip);
            count++;
            if (count >= 30) {// 从ip发送第一条开始
                blacklistIpTimeWindow.putBlackIp(ip, ip);
                ipCache.remove(ip);
                sendCountTimeWindow.delIpSend(ip);
                return true;
            } else {
                ipCache.put(ip, count++);
            }

        } else {
            ipCache.put(ip, 1);
            sendCountTimeWindow.putIpSend(ip, ip);
        }
        return false;
    }

    private String getPartyMobile(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            return "";
        }

        Party partyEntity = partyService.cachePartyBy(userId, false);
        if (partyEntity == null) {
            logger.error("---> getPartyMobile 用户记录不存在:" + userId);
            return "";
        }

        if (StrUtil.isBlank(partyEntity.getPhone())) {
            return "";
        }
        String phone = partyEntity.getPhone().trim();

        boolean checkedPhone = partyEntity.getPhone_authority();
        if (checkedPhone) {
            return phone;
        }

        return "x";
    }

    private String getPartyEmail(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            return "";
        }

        Party partyEntity = partyService.cachePartyBy(userId, false);
        if (partyEntity == null) {
            logger.error("---> getPartyEmail 用户记录不存在:" + userId);
            return "";
        }

        if (StrUtil.isBlank(partyEntity.getEmail())) {
            return "";
        }
//        如果该用户为虚拟用户，
        if (Constants.SECURITY_ROLE_GUEST.equals(partyEntity.getRolename())) {
            return "x";
        }
        String email = partyEntity.getEmail().trim();

        boolean checkedEmail = partyEntity.getEmail_authority();
        if (checkedEmail) {
            return email;
        }

        return "x";
    }

    private int getNotifyLevel() {
        /**
         * 消息发送渠道：1-纯短信，2-纯邮件，3-纯站内信，4-短信+站内信，5-邮件+站内信，6-短信+邮件，7-短信+邮件+站内信
         */
        int level = 5;
        Syspara notifyConfig = this.sysparaService.find("notify_level");
        if (notifyConfig != null) {
            try {
                level = Integer.parseInt(notifyConfig.getValue());
            } catch (Exception e) {
                logger.error("notify_level 参数值不合规:" + notifyConfig.getValue());
            }
        }

        return level;
    }

    public int clearOldNotification(String targetUserId, List<Integer> statusList, Date limitTime) {
        return this.notificationService.deleteOldNotification(targetUserId, statusList, limitTime);
    }

    public void setCommonNotifyManager(CommonNotifyManager commonNotifyManager) {
        this.commonNotifyManager = commonNotifyManager;
    }

    public void setSysparaService(SysparaService sysparaService) {
        this.sysparaService = sysparaService;
    }

    public void setSendCountTimeWindow(SendCountTimeWindow sendCountTimeWindow) {
        this.sendCountTimeWindow = sendCountTimeWindow;
    }

    public void setBlacklistIpTimeWindow(BlacklistIpTimeWindow blacklistIpTimeWindow) {
        this.blacklistIpTimeWindow = blacklistIpTimeWindow;
    }

    public void setIdentifyingCodeTimeWindowService(IdentifyingCodeTimeWindowService identifyingCodeTimeWindowService) {
        this.identifyingCodeTimeWindowService = identifyingCodeTimeWindowService;
    }

    public void setCodeLogService(CodeLogService codeLogService) {
        this.codeLogService = codeLogService;
    }

    public void setRedisHandler(RedisHandler redisHandler) {
        this.redisHandler = redisHandler;
    }

    public void setPartyService(PartyService partyService) {
        this.partyService = partyService;
    }

    public void setSellerService(SellerService sellerService) {
        this.sellerService = sellerService;
    }

    public void setNotificationService(NotificationService notificationService) {
        this.notificationService = notificationService;
    }


}
