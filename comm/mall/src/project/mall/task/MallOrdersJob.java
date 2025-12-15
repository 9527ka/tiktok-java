package project.mall.task;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import com.sun.org.apache.bcel.internal.generic.IF_ACMPEQ;
import kernel.util.DateUtils;
import org.springframework.scheduling.annotation.Scheduled;
import project.RedisKeys;
import project.log.MallLogRedisKeys;
import project.log.MoneyFreeze;
import project.log.MoneyFreezeService;
import project.mall.goods.GoodsStatisticsService;
import project.mall.goods.SellerGoodsService;
import project.mall.goods.model.SellerGoods;
import project.mall.goods.vo.GoodsShowWeight;
import project.mall.notification.NotificationHelper;
import project.mall.notification.utils.notify.CommonNotifyManager;
import project.mall.orders.GoodsOrdersService;
import project.mall.orders.model.MallOrdersPrize;
import project.mall.seller.SellerService;
import project.mall.seller.model.Seller;
import project.onlinechat.OnlineChatUserMessage;
import project.onlinechat.OnlineChatUserMessageService;
import project.redis.RedisHandler;
import project.redis.interal.KeyValue;
import project.syspara.SysParaCode;
import project.syspara.Syspara;
import project.syspara.SysparaService;

import java.time.Duration;
import java.time.Instant;
import java.util.*;


/**
 * 60秒
 */
public class MallOrdersJob {

    protected GoodsOrdersService goodsOrdersService;
    protected MoneyFreezeService moneyFreezeService;
    protected NotificationHelper notificationHelper;
    protected RedisHandler redisHandler;
    protected OnlineChatUserMessageService onlineChatUserMessageService;
    protected GoodsStatisticsService goodsStatisticsService;
    protected SellerGoodsService sellerGoodsService;
    protected SysparaService sysparaService;

    protected SellerService sellerService;

    //private static Log logger = LogFactory.getLog(MallOrdersJob.class);
    protected static org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(MallOrdersJob.class);

    /**
     * 正常订单超时未付款，自动关闭
     */
    public void autoCancelJob() {
        long start = System.currentTimeMillis();
        logger.info("**********取消订单开始**********");
        Integer c = goodsOrdersService.updateAutoCancel();
        logger.info("取消单数:" + c + ",花费时间:" + (System.currentTimeMillis() - start) + "ms");
    }

    /**
     * 虚拟订单自动发货
     */
    public void autoVirtualOrderdelivery() {
        long start = System.currentTimeMillis();
        logger.info("**********虚拟订单自动发货开始**********");
        List<MallOrdersPrize> mallOrdersPrizes = goodsOrdersService.listAutoVirtualOrderDelivery();
        for (MallOrdersPrize mallOrdersPrize : mallOrdersPrizes) {
            //异步
            goodsOrdersService.updateVirtualOrderdelivery(mallOrdersPrize.getId().toString());
        }
        logger.info("虚拟订单发货单数:" + (Objects.isNull(mallOrdersPrizes) ? 0 : mallOrdersPrizes.size()) + ",花费时间:" + (System.currentTimeMillis() - start) + "ms");
    }

    /**
     * 发货超过时未收货，自动完成收货
     */
    public void autoReceiptJob() {
        long start = System.currentTimeMillis();
        logger.info(DateUtils.toMillString(new Date()) + " **********自动收货开始**********");
        Integer c = goodsOrdersService.updateAutoReceipt();
        logger.info(DateUtils.toMillString(new Date()) + " 自动收货单数:" + c + ",花费时间:" + (System.currentTimeMillis() - start) + "ms");
    }

    public void autoSellerInfoJob(){
        long start = System.currentTimeMillis();
        logger.info(DateUtils.toMillString(new Date()) + " **********自动更新店铺，真实销量，好评率，商品数量定时任务开始**********");
        sellerService.updateAutoSellerInfo();
        logger.info(DateUtils.toMillString(new Date()) + " 自动更新店铺，真实销量，好评率，商品数量定时任务开始 花费时间:" + (System.currentTimeMillis() - start) + "ms");
    }

    /**
     * 停止直通车商品
     * 1.查询所有直通车购买记录。
     * 2.通过购买记录查询店铺ID，过期时间。
     * 3.查询店铺商的商品，修改商品为直通车过期
     */
    public void autoStopComboJob() {
        long start = System.currentTimeMillis();
        logger.info(DateUtils.toMillString(new Date()) + " **********停止到期的商品直通车开始**********");
        Integer c = goodsOrdersService.updateStopCombo();
        logger.info(DateUtils.toMillString(new Date()) + " 停止到期的商品直通车:" + c + ",花费时间:" + (System.currentTimeMillis() - start) + "ms");
    }

    /**
     * 订单完成收货后，自动释放冻结资金
     */
    @Scheduled(fixedRate = 120000)
    public void autoProfitJob() {
        try {
            long start = System.currentTimeMillis();
            logger.info(DateUtils.toMillString(new Date()) + " **********利润订单开始**********");
            if (!CommonNotifyManager.getInstance().isInited()) {
                logger.warn(" ********** 系统还未正常启动，忽略本次利润订单处理定时任务 **********");
                return;
            }

            List<MallOrdersPrize> list = goodsOrdersService.listAutoProfit();
            for (MallOrdersPrize brushOrders : list) {
                try {
                    //异步
                    goodsOrdersService.updateAutoProfit(brushOrders.getId().toString());
                } catch (Exception e) {
                    logger.error(DateUtils.toMillString(new Date()) + " 处理订单:" + brushOrders.getId() + " 的返佣时报错", e);
                    continue;
                }

                try {
                    // 发送通知
                    MallOrdersPrize order = goodsOrdersService.getMallOrdersPrize(brushOrders.getId().toString());
                    if (order == null) {
                        continue;
                    }
                    notificationHelper.notifyFinishOrder(order, 5);
                } catch (Exception e) {
                    logger.error(DateUtils.toMillString(new Date()) + " 订单完成后发送消息通知处理报错", e);
                }
            }

            logger.info(DateUtils.toMillString(new Date()) + " 利润单数:" + list.size() + ",花费时间:" + (System.currentTimeMillis() - start) + "ms");
        } catch (Exception e) {
            logger.error("释放佣金异常", e);
        }
    }

    /**
     * 自动评价
     */
    public void autoCommentJob() {
        long start = System.currentTimeMillis();
        logger.info("**********自动评分订单开始**********");
        List<MallOrdersPrize> list = goodsOrdersService.listAutoComment();
        for (MallOrdersPrize commentOrder : list) {
            try {
                goodsOrdersService.updateAutoComment(commentOrder.getId().toString());
            } catch (Exception e) {
                logger.error(DateUtils.toMillString(new Date()) + " 处理订单:" + commentOrder.getId() + " 的自动评价报错", e);
                continue;
            }
        }
        logger.info("自动评分订单数:" + list.size() + ",花费时间:" + (System.currentTimeMillis() - start) + "ms");
    }

    /**
     * 卖家采购自动超时
     */
    public void autoPurchTimeOutJob() {
        long start = System.currentTimeMillis();
        logger.info("**********采购自动超时订单开始**********");
        List<MallOrdersPrize> list = goodsOrdersService.listAutoPurchTimeOut();
        for (MallOrdersPrize order : list) {
            try {
                goodsOrdersService.updatePurchTimeOut(order.getId().toString());
            } catch (Exception e) {
                logger.error(DateUtils.toMillString(new Date()) + " 处理订单:" + order.getId() + " 的采购自动超时报错 ", e);
                continue;
            }
        }
        logger.info("采购自动超时订单数:" + list.size() + ",花费时间:" + (System.currentTimeMillis() - start) + "ms");
    }

    /**
     * 店铺推荐自动取消
     */
    public void autoSellerRecTimeOutJob() {
        long start = System.currentTimeMillis();
        logger.info("**********自动取消店铺推荐开始**********");
        List<Seller> sellers = sellerService.listAutoSellerRecTime();
        int autoSeller = 0;
        for (Seller seller : sellers) {

            Instant recTimeInstant = Instant.ofEpochMilli(seller.getRecTime());
            Instant expirationInstant = recTimeInstant.plus(Duration.ofDays(seller.getRecommendDuration()));
            Instant currentInstant = Instant.now();

            if (expirationInstant.isBefore(currentInstant)) {
                seller.setRecTime(0L);
                autoSeller++;
            }
            try {
                sellerService.updateSeller(seller);
            } catch (Exception e) {
                logger.error(DateUtils.toMillString(new Date()) + " 店铺推荐:" + seller.getId() + " 的自动取消报错 ", e);
            }
        }
        logger.info("店铺推荐自动取消:" + autoSeller + ",花费时间:" + (System.currentTimeMillis() - start) + "ms");
    }

    /**
     * 店铺商品：每日上新、推荐产品、热销产品时间到期取消
     */
    public void autoSellerGoodsOutJob() {

        // 1 推荐商品
        long recStart = System.currentTimeMillis();
        logger.info("**********推荐商品到期取消**********");
        List<SellerGoods> autoSysRecList = sellerGoodsService.listAutoSellerGoodsList(1);
        Integer autoSysRec = autoSellerGoods(autoSysRecList,1,"推荐商品");
        logger.info("推荐商品自动取消:" + autoSysRec + ",花费时间:" + (System.currentTimeMillis() - recStart) + "ms");

        //2 每日上新
        long systemNewStart = System.currentTimeMillis();
        logger.info("**********每日上新到期取消**********");
        List<SellerGoods> systemNewList = sellerGoodsService.listAutoSellerGoodsList(2);
        Integer autoSystemNew = autoSellerGoods(systemNewList,2,"每日上新");
        logger.info("每日上新自动取消:" + autoSystemNew + ",花费时间:" + (System.currentTimeMillis() - systemNewStart) + "ms");

        //3 热销商品
        long sellWellStart = System.currentTimeMillis();
        logger.info("**********热销商品到期取消**********");
        List<SellerGoods> sellWellList = sellerGoodsService.listAutoSellerGoodsList(3);
        Integer autoSellWell = autoSellerGoods(sellWellList,3,"热销商品");
        logger.info("热销商品自动取消:" + autoSellWell + ",花费时间:" + (System.currentTimeMillis() - sellWellStart) + "ms");
    }

    private Integer autoSellerGoods(List<SellerGoods> sellerGoodsList,int type, String message){
        int autoCount = 0;
        for (SellerGoods sellerGoods : sellerGoodsList) {

            Instant startTiemInstant = null;
            int durationDays = 0;

            // 1 推荐商品 2新商品 3热销商品
            switch (type){
                case 1 :
                    startTiemInstant = Instant.ofEpochMilli(sellerGoods.getSystemRecTime());
                    durationDays = sellerGoods.getSysRecDuration();
                    break;
                case 2 :
                    startTiemInstant = Instant.ofEpochMilli(sellerGoods.getSystemNewTime());
                    durationDays = sellerGoods.getSysNewDuration();
                    break;
                case 3 :
                    startTiemInstant = Instant.ofEpochMilli(sellerGoods.getSellWellTime());
                    durationDays = sellerGoods.getSellWellDuration();
                    break;
                default: continue;
            }

            // 到期的Instant
            Instant expirationInstant = startTiemInstant.plus(Duration.ofDays(durationDays));
            Instant currentInstant = Instant.now();

            if (expirationInstant.isBefore(currentInstant)) {
                if (type == 1){
                    sellerGoods.setSystemRecTime(0L);
                } else if (type == 2){
                    sellerGoods.setSystemNewTime(0L);
                } else {
                    sellerGoods.setSellWellTime(0L);
                }
                autoCount++;
            }
            try {
                sellerGoodsService.updateSellerGoods(sellerGoods);
            } catch (Exception e) {
                logger.error(DateUtils.toMillString(new Date()) + message + ":" + sellerGoods.getId() + " 的自动取消报错 ", e);
            }
        }
        return autoCount;
    }

    /**
     * 虚拟订单自动确认
     */
    public void autoConfirm() {
        long start = System.currentTimeMillis();
        logger.info("**********虚拟订单自动确认开始**********");
        List<MallOrdersPrize> list = goodsOrdersService.listAutoConfirm();
        for (MallOrdersPrize mallOrdersPrize : list) {
            goodsOrdersService.updateAutoConfirm(mallOrdersPrize);
        }
        logger.info("虚拟订单自动确认订单数量:" + list.size() + ",花费时间:" + (System.currentTimeMillis() - start) + "ms");
    }


    /**
     * 定时增加访问量
     */
    public void autoIncreaseViewCount() {
        long start = System.currentTimeMillis();
        logger.info("**********随机增加店铺商品访问量开始**********");
        goodsOrdersService.updateAutoIncreaseViewCount();
        logger.info("随机增加店铺商品访问量结束:花费时间:" + (System.currentTimeMillis() - start) + "ms");

    }

    public void autoUnFreezeMoney() {
        long start = System.currentTimeMillis();
        logger.info(DateUtils.toMillString(new Date()) + " ********** 开始扫描需要解冻的商户资金 **********");
        int unfreezeCount = 0;

        List<String> pendingIdList = moneyFreezeService.listPendingFreezeRecords();
        // 建议后续做分页优化 TODO
        for (String oneFreezeId : pendingIdList) {
            int unfreezeResult = moneyFreezeService.updateAutoUnFreezeSeller(oneFreezeId, "0");
            unfreezeCount = unfreezeCount + unfreezeResult;
        }

        List<MoneyFreeze> pendingList = moneyFreezeService.listPendingFreezeRecords(50);
        for (MoneyFreeze oneFreezeRecord : pendingList) {
            int unfreezeResult = moneyFreezeService.updateAutoUnFreezeSeller(oneFreezeRecord.getId().toString(), "0");
            unfreezeCount = unfreezeCount + unfreezeResult;
        }

        logger.info(DateUtils.toMillString(new Date()) + " 定时任务解冻了:" + unfreezeCount + " 个商户的资金, 花费时间:" + (System.currentTimeMillis() - start) + " 毫秒");
    }

    public void autoClearChatHistory() {
        long start = System.currentTimeMillis();
        logger.info(" ********** 开始执行需要删除的聊天记录 **********");
        onlineChatUserMessageService.updateAutoClearChatHistory();
        logger.info(" 定时任务清除了消息记录，花费时间:" + (System.currentTimeMillis() - start) + " 毫秒");
    }

    public void autoNotifySellerReplyIm() {
        long start = System.currentTimeMillis();
        logger.debug("********** 开始扫描需要回复咨询的记录 **********");
        int notifyCount = 0;

        try {
            double min = 0;
            double max = System.currentTimeMillis() - 5L * 60L * 1000L; // 5 分钟之前的消息
            Set<KeyValue<String, Double>> pendingItems = redisHandler.zRange(MallLogRedisKeys.SELLER_IM_REPLY_NOTIFY, min, max);
            List<String> pendingInfoList = new ArrayList();
            if (pendingItems == null || pendingItems.isEmpty()) {
                return;
            }
            for (KeyValue<String, Double> oneItem : pendingItems) {
                pendingInfoList.add(oneItem.getKey());
            }

            for (String onePendingInfo : pendingInfoList) {
                String[] onePendingInfoArr = onePendingInfo.split(":");
                String sellerId = onePendingInfoArr[0];
                String buyerId = onePendingInfoArr[1];
                // 取出对应的最后一条买家向卖家发起的聊天记录
                // 聊天信息 dto 对象
                // 优先取不是图片的消息
                OnlineChatUserMessage lastImInfo = onlineChatUserMessageService.getLastImMessage(buyerId, sellerId, false, 0L);
                // 如果最近全是图片消息，再选择图片类消息
                if (lastImInfo == null) {
                    // 兜底逻辑，全是图片的情况较少
                    lastImInfo = onlineChatUserMessageService.getLastImMessage(buyerId, sellerId, true, 0L);
                }
                if (lastImInfo == null) {
                    continue;
                }

                notificationHelper.notifyReplyBuyer(lastImInfo, 5);
                notifyCount++;
            }
        } catch (Exception e) {
            logger.error("定时任务发送提醒商家回复买家咨询的通知的处理报错");
        }

        logger.debug("定时任务发送了:" + notifyCount + " 条提醒商家回复买家咨询的通知, 花费时间:" + (System.currentTimeMillis() - start) + " 毫秒");
    }

    /**
     * 一个粗糙地标记商品折扣状态是否发生变化的定时任务。
     * 主要用于优化刷新商品展示权重的处理逻辑.
     */
    public void refreshGoodsDiscount() {
        long start = System.currentTimeMillis();
        logger.info("********** 开始扫描商品的折扣状态 **********");

        // 默认定时任务每隔 10 分钟跑一次
        long jobInterval = 10L * 60L * 1000L;
        long now = System.currentTimeMillis();
        try {
            // 提取商品的折扣时间缓存
            Set<KeyValue<String, Double>> discountGoodsList = redisHandler.zRange(RedisKeys.SELLER_GOODS_DISCOUNT_ENDTIME, 0, Long.MAX_VALUE);
            if (CollectionUtil.isEmpty(discountGoodsList)) {
                return;
            }

            for (KeyValue<String, Double> oneInfo : discountGoodsList) {
                String sellerGoodsId = oneInfo.getKey();
                long discountEndTime = oneInfo.getValue().longValue();

                if (discountEndTime <= now) {
                    // 折扣时间过期，直接删除
                    redisHandler.zrem(RedisKeys.SELLER_GOODS_DISCOUNT_ENDTIME, sellerGoodsId);

                    // 相对于上一次状态有变更
                    redisHandler.zincrby(RedisKeys.SELLER_GOODS_DISCOUNT_STATE_CHANGED, 1, sellerGoodsId);
                    continue;
                } else {
                    // 折扣时间未过期，检查下开始时间是否命中
                    SellerGoods sellerGoods = sellerGoodsService.getSellerGoods(sellerGoodsId);
                    if (sellerGoods == null || sellerGoods.getIsShelf() == 0 || sellerGoods.getIsValid() == 0) {
                        continue;
                    }

                    Date discountStartTime = sellerGoods.getDiscountStartTime();
                    if (discountStartTime == null) {
                        continue;
                    }
                    if (discountStartTime.getTime() > now) {
                        // 折扣活动尚未开始
                    } else {
                        // 折扣活动已经开始
                        if (jobInterval + discountStartTime.getTime() >= now) {
                            // 一个粗略的实现，10 分钟内折扣活动开始生效，则代表折扣状态出现了变化
                            redisHandler.zincrby(RedisKeys.SELLER_GOODS_DISCOUNT_STATE_CHANGED, 1, sellerGoodsId);
                            continue;
                        }
                    }
                }
            }

        } catch (Exception e) {
            logger.error("=========> 定时任务刷新商品折扣状态报错", e);
        }
    }


    /**
     * 更新商品的新品标记.
     */
    public void refreshGoodsNewFlag() {
        long start = System.currentTimeMillis();
        logger.info("********** 开始扫描商品的新品状态 **********");

        // 默认定时任务每隔 10 分钟跑一次
        long jobInterval = 10L * 60L * 1000L;
        long now = System.currentTimeMillis();
        try {
            int newSellerGoodsDayLimit = 7;
            Syspara newSellerGoodsDayLimitParam = sysparaService.find(SysParaCode.NEW_SELLER_GOODS_DAY_LIMIT.getCode());
            if (newSellerGoodsDayLimitParam != null) {
                String value = newSellerGoodsDayLimitParam.getValue().trim();
                newSellerGoodsDayLimit = Integer.parseInt(value);
            }

            // 在此之前上架的商品都不再是新品，更新其 showWeight1 值
            long newGoodsTimeLimit = now - (long)newSellerGoodsDayLimit * 24L * 3600L * 1000L;
            int total = 0;
            int currentPage = 1;
            int pageSize = 100;
            int max = 100000;
            while (true) {
                // 都是需要更新 showWeight1 字段的记录
                List<SellerGoods> pageList = sellerGoodsService.pagedOldSellerGoods(newGoodsTimeLimit, currentPage, pageSize);
                if (CollectionUtil.isEmpty(pageList)) {
                    break;
                }
                if (total >= max) {
                    logger.warn("[MallOrdersJob refreshGoodsNewFlag] 完成:{} 个商品新品状态的变更，超出了本次处理的上限，可能程序存在死循环，强制退出", total);
                    break;
                }
                // 每次更新商品状态，都会导致第二次分页查询 offset 的变化，所以永远只查第一页就够了
                //currentPage++;

                List<GoodsShowWeight> dataList = new ArrayList<>();
                for (SellerGoods oneGoods : pageList) {
                    String sellerGoodsId = oneGoods.getId().toString();
                    redisHandler.zrem(RedisKeys.SELLER_GOODS_FIRST_SHELF_TIME, sellerGoodsId);
                    total++;

                    GoodsShowWeight updateWeight = new GoodsShowWeight();
                    updateWeight.setGoodsId(sellerGoodsId);
                    updateWeight.setWeight(0L);

                    dataList.add(updateWeight);
                }

                // 批次更新
                sellerGoodsService.updateBatchShowWeight1(dataList);
            }

            logger.info("[MallOrdersJob refreshGoodsNewFlag] 完成:{} 个商品新品缓存的加载", total);
            // logger.info("===========> 新品标记更新列表:{}", JsonUtils.bean2Json(dataList));
        } catch (Exception e) {
            logger.error("=========> 定时任务刷新商品新品状态报错", e);
        }
    }

    /**
     * 定时更新店铺商品 showWeight 字段值
     */
    public void refreshSellerGoodsShowWeight() {
        long start = System.currentTimeMillis();
        logger.info("********** 开始扫描需要刷新 showWeight 属性值的商品 **********");

        try {
            // 提取有搜藏事件的商品信息
            Set<KeyValue<String, Double>> keepedGoodsList = redisHandler.zRange(RedisKeys.SELLER_GOODS_KEEP_EVENT_ACC, 1, Long.MAX_VALUE);

            // 提取有评论事件的商品
            Set<KeyValue<String, Double>> evaluationGoodsList = redisHandler.zRange(RedisKeys.ORDER_GOODS_EVALUATION_ACC, 1, Long.MAX_VALUE);

            // 提取销量有变化的商品信息
            Set<KeyValue<String, Double>> soldGoodsList = redisHandler.zRange(RedisKeys.SELLER_GOODS_PURCHASE_ACC, 1, Long.MAX_VALUE);

            // 折扣活动有变化的商品
            Set<KeyValue<String, Double>> goodsDiscountChangedList = redisHandler.zRange(RedisKeys.SELLER_GOODS_DISCOUNT_STATE_CHANGED, 1, Long.MAX_VALUE);

            // 浏览量有变化的商品信息
            Set<KeyValue<String, Double>> viewCountChangedGoodsList = redisHandler.zRange(RedisKeys.SELLER_GOODS_VIEW_EVENT_ACC, 1, Long.MAX_VALUE);

            // 提取 goodsId 集合，同时清零相关缓存
            Set<String> goodsIds = new HashSet<>();

            // 提取并清理商品被收藏/取消收藏的事件累计信息
            for (KeyValue<String, Double> one : keepedGoodsList) {
                int keepedEventCount = one.getValue().intValue();
                // 原子式地清理累计事件数量，在此期间可能会有新的同类事件产生
                redisHandler.zincrby(RedisKeys.SELLER_GOODS_KEEP_EVENT_ACC, -keepedEventCount, one.getKey());

                goodsIds.add(one.getKey());
            }

            // 提取并清理商品被评论的事件累计信息
            for (KeyValue<String, Double> one : evaluationGoodsList) {
                int evaluationEventCount = one.getValue().intValue();
                // 原子式地清理累计事件数量，在此期间可能会有新的同类事件产生
                redisHandler.zincrby(RedisKeys.ORDER_GOODS_EVALUATION_ACC, -evaluationEventCount, one.getKey());

                goodsIds.add(one.getKey());
            }

            // 提取并清理商品被购买的增量信息
            for (KeyValue<String, Double> one : soldGoodsList) {
                int soldCount = one.getValue().intValue();
                // 原子式地清理累计事件数量，在此期间可能会有新的同类事件产生
                redisHandler.zincrby(RedisKeys.SELLER_GOODS_PURCHASE_ACC, -soldCount, one.getKey());

                goodsIds.add(one.getKey());
            }

            // 提取并清理商品折扣活动变更信息
            for (KeyValue<String, Double> one : goodsDiscountChangedList) {
                int changeCount = one.getValue().intValue();
                // 原子式地清理累计事件数量，在此期间可能会有新的同类事件产生
                // 一次性消费掉商品折扣变化事件数量，这样，如果该商品未修改折扣时间，或者折扣活动状态未发生变更时，减少重新计算商品展示权重的商品数量，提升性能
                redisHandler.zincrby(RedisKeys.SELLER_GOODS_DISCOUNT_STATE_CHANGED, -changeCount, one.getKey());

                goodsIds.add(one.getKey());
            }

            // 提取并清理商品流量变更事件信息
            for (KeyValue<String, Double> one : viewCountChangedGoodsList) {
                int viewedEventCount = one.getValue().intValue();
                // 原子式地清理累计事件数量，在此期间可能会有新的同类事件产生
                redisHandler.zincrby(RedisKeys.SELLER_GOODS_VIEW_EVENT_ACC, -viewedEventCount, one.getKey());

                goodsIds.add(one.getKey());
            }

            if (goodsIds.isEmpty()) {
                return;
            }

            int pageSize = 20;
            int offset = 0;
            List<String> goodsIdList = new ArrayList<>(goodsIds);
            while (offset < goodsIds.size()) {
                List<String> pageGoodsIdList = null;
                if (offset + pageSize <= goodsIds.size()) {
                    pageGoodsIdList = goodsIdList.subList(offset, offset + pageSize);
                } else {
                    pageGoodsIdList = goodsIdList.subList(offset, goodsIds.size());
                }
                offset = offset + pageGoodsIdList.size();
                // ogger.info("===> MallOrdersJob refreshSellerGoodsShowWeight 当前分页需要重新计算展示权重的商品集合为:{}", pageGoodsIdList);

                try {
                    // 防止 redis 缓存在服务器重启后丢失，导致无法刷新 showWeight1
                    List<SellerGoods> goodsEntityList = sellerGoodsService.getSellerGoodsBatch(pageGoodsIdList);
                    for (SellerGoods oneGoodsEntity : goodsEntityList) {
                        redisHandler.zadd(RedisKeys.SELLER_GOODS_FIRST_SHELF_TIME, oneGoodsEntity.getFirstShelfTime(), oneGoodsEntity.getId().toString());
                    }
                } catch (Exception e) {
                    logger.error("===> MallOrdersJob refreshSellerGoodsShowWeight 刷新 redis 缓存失败", e);
                }

                try {
                    goodsStatisticsService.updateRefreshSellerGoodsShowWeight(pageGoodsIdList);
                } catch (Exception e) {
                    logger.error("===> MallOrdersJob refreshSellerGoodsShowWeight 刷新当前批次的商品权重报错，goodsIds:{}, error: ", pageGoodsIdList, e);
                }
            }

            long endTime = System.currentTimeMillis();
            logger.info("===> MallOrdersJob refreshSellerGoodsShowWeight 定时任务更新所有相关商品的展示权重耗时:{} ms", endTime - start);
        } catch (Exception e) {
            logger.error("=========> 定时任务刷新商品 showWeight 的处理报错", e);
        }
    }


    /**
     * 定时清理旧的消息通知记录
     */
    public void clearOldNotification() {
        logger.info("********** 开始扫描需要清理的消息通知记录 **********");

        try {
            // 清理 2 个月之前的记录
            Date now = new Date();
            Date twoMonthAgo = DateUtil.offsetDay(now, -60);
            int clearCount = notificationHelper.clearOldNotification(null, null, twoMonthAgo);
            logger.info("===> MallOrdersJob clearOldNotification 定时任务清理过期消息通知记录数量:{}", clearCount);
        } catch (Exception e) {
            logger.error("=========> 定时任务清理过期消息通知记录报错", e);
        }
    }

    /**
     * 定时清理旧的浏览量记录
     */
    public void clearOldViews(){
        logger.info("********** 开始扫描需要清理的商品浏览量记录 **********");
        try {
            // 清理 1 个月之前的记录
            Date now = new Date();
            Date oneMonthAgo = DateUtil.offsetDay(now, -30);
            int clearViews = sellerGoodsService.updateCleanView(null,null,null,oneMonthAgo);
            logger.info("===> MallOrdersJob clearOldViews 定时任务清理清理的商品浏览量记录数量:{}", clearViews);
        } catch (Exception e) {
            logger.error("=========> 定时任务清理的商品浏览量记录报错",e);
        }

    }

    /**
     * 定时任务，修复早期记录没有 flag 值的订单
     */
    public void fillOrderFlag() {
        logger.info("********** 开始扫描需要填充 flag 值的订单记录 **********");

        try {
            // 清理 2 个月之前的记录
            long beginTime = System.currentTimeMillis();
            int count = goodsOrdersService.updateOrderFlag(null);
            long endTime = System.currentTimeMillis();

            logger.info("===> MallOrdersJob fillOrderFlag 定时任务填充的订单记录数量:{}, 耗时:{} ms", count, (endTime - beginTime));
        } catch (Exception e) {
            logger.error("=========> 定时任务扫描更新订单 flag 值的处理报错", e);
        }
    }


    public void setGoodsOrdersService(GoodsOrdersService goodsOrdersService) {
        this.goodsOrdersService = goodsOrdersService;
    }

    public void setMoneyFreezeService(MoneyFreezeService moneyFreezeService) {
        this.moneyFreezeService = moneyFreezeService;
    }

    public void setNotificationHelper(NotificationHelper notificationHelper) {
        this.notificationHelper = notificationHelper;
    }

    public void setRedisHandler(RedisHandler redisHandler) {
        this.redisHandler = redisHandler;
    }

    public void setOnlineChatUserMessageService(OnlineChatUserMessageService onlineChatUserMessageService) {
        this.onlineChatUserMessageService = onlineChatUserMessageService;
    }

    public void setGoodsStatisticsService(GoodsStatisticsService goodsStatisticsService) {
        this.goodsStatisticsService = goodsStatisticsService;
    }

    public void setSellerGoodsService(SellerGoodsService sellerGoodsService) {
        this.sellerGoodsService = sellerGoodsService;
    }

    public void setSysparaService(SysparaService sysparaService) {
        this.sysparaService = sysparaService;
    }

    public void setSellerService(SellerService sellerService) {
        this.sellerService = sellerService;
    }
}
