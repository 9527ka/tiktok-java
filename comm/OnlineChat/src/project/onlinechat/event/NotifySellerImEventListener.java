package project.onlinechat.event;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import project.log.MallLogRedisKeys;
import project.mall.seller.SellerService;
import project.mall.task.MallOrdersJob;
import project.onlinechat.OnlineChatUserMessage;
import project.onlinechat.OnlineChatUserMessageService;
import project.onlinechat.event.message.ImSendEvent;
import project.party.PartyService;
import project.party.model.Party;
import project.redis.RedisHandler;
import project.syspara.SysparaService;
import project.user.token.TokenService;

import javax.annotation.Resource;

public class NotifySellerImEventListener implements ApplicationListener<ImSendEvent> {
    private Log logger = LogFactory.getLog(this.getClass());

    //@Resource
    private RedisHandler redisHandler;

    //@Resource
    private PartyService partyService;

//    @Resource
//    private SellerService sellerService;
//    @Resource
//    private OnlineChatUserMessageService onlineChatUserMessageService;

    @Override
    public void onApplicationEvent(ImSendEvent event) {
        OnlineChatUserMessage imMessage = event.getImMessage();
        // chatId = sendPartyId + "-" + receivePartyId;
        String chatId = imMessage.getChatId();
        String[] chatInfoArr = chatId.split("-");
        String senderId = chatInfoArr[0];
        String receiverId = chatInfoArr[1];
        logger.info("收到发送IM消息事件, chatId:" + chatId);
        try {
            Party senderEntity = partyService.cachePartyBy(senderId, true);
            if (senderEntity == null) {
                return;
            }

            // 如果消息发送者是买家，就检查下等待通知队列中是否有事件，如果有，就跳过
            // 如果消息发送者是卖家，则检查下等待通知队列中是否有事件，如果有，就删除该缓存
            // zset 缓存 value 的拼接规则：{sellerId : buyerId}
            String cacheValue = "";
            if (senderEntity.getRoleType() == 0) {
                // 买家
                cacheValue = receiverId.trim() + ":" + senderId.trim();
                Double sendTime = redisHandler.zscore(MallLogRedisKeys.SELLER_IM_REPLY_NOTIFY, cacheValue);
                if (sendTime == null) {
                    long now = System.currentTimeMillis();
                    // 产生待通知元素
                    redisHandler.zadd(MallLogRedisKeys.SELLER_IM_REPLY_NOTIFY, now, cacheValue);

                    // 注意：是站在卖家的视角生成缓存
                    String hashField = receiverId.trim() + ":" + senderId.trim();
                    String hashValue = imMessage.getId().toString() + ":" + now;
                    redisHandler.put(MallLogRedisKeys.SELLER_IM_LAST_BUYER_MESSAGE, hashField, hashValue);
                } else {
                    // 已有元素，则以最早的为准
                }
            } else {
                // 卖家回复，则清理通知缓存
                cacheValue = senderId.trim() + ":" + receiverId.trim();
                String hashField = senderId.trim() + ":" + receiverId.trim();
                redisHandler.zrem(MallLogRedisKeys.SELLER_IM_REPLY_NOTIFY, cacheValue);
                redisHandler.remove(MallLogRedisKeys.SELLER_IM_LAST_BUYER_MESSAGE, hashField);
            }
        } catch (Exception e) {
            logger.error("买家用户:" + senderId + " 向商家:" + receiverId + " 发送咨询消息，生成提醒消息时报错", e);
        }
    }

    public void setRedisHandler(RedisHandler redisHandler) {
        this.redisHandler = redisHandler;
    }

    public void setPartyService(PartyService partyService) {
        this.partyService = partyService;
    }
}
