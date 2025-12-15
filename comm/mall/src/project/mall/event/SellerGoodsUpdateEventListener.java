package project.mall.event;

import com.alibaba.fastjson.JSON;
import kernel.util.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import project.RedisKeys;
import project.mall.event.message.KeepSellerGoodsEvent;
import project.mall.event.message.SellerGoodsUpdateEvent;
import project.mall.event.model.KeepSellerGoodsInfo;
import project.mall.event.model.SellerGoodsUpdateInfo;
import project.mall.goods.GoodsStatisticsService;
import project.mall.goods.SellerGoodsService;
import project.mall.goods.model.SellerGoods;
import project.mall.goods.vo.GoodsShowWeight;
import project.redis.RedisHandler;
import project.syspara.SysParaCode;
import project.syspara.Syspara;
import project.syspara.SysparaService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 用户采购订单后，有一些关联业务会同步受到影响
 * 目前可见受影响的业务数据：
 * 1. 将最近一个时间段的累计店铺商品累计销量录入缓存：
 * 2. ....
 *
 */
public class SellerGoodsUpdateEventListener implements ApplicationListener<SellerGoodsUpdateEvent> {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private RedisHandler redisHandler;

    private SellerGoodsService sellerGoodsService;

    private GoodsStatisticsService goodsStatisticsService;

    private SysparaService sysparaService;

    @Override
    public void onApplicationEvent(SellerGoodsUpdateEvent event) {
        SellerGoodsUpdateInfo info = event.getSellerGoodsUpdateInfo();
        logger.info("监听到商品信息更新处理事件:" + JSON.toJSONString(info));

        Date now = new Date();
        try {
            SellerGoods sellerGoods = sellerGoodsService.getSellerGoods(info.getSellerGoodsId());
            if (sellerGoods == null || sellerGoods.getIsValid() == 0 || sellerGoods.getIsShelf() == 0) {
                redisHandler.zrem(RedisKeys.SELLER_GOODS_DISCOUNT_ENDTIME, info.getSellerGoodsId());
                return;
            }

            boolean syncRefreshShowWeight = true;
            Syspara syncRefreshParam = sysparaService.find(SysParaCode.SYNC_REFRESH_SELLER_GOODS_SHOW_WEIGHT.getCode());
            if (syncRefreshParam != null) {
                String flag = syncRefreshParam.getValue().trim();
                if (flag.equalsIgnoreCase("false")) {
                    syncRefreshShowWeight = false;
                }
            }

            boolean isRefreshNewFlag = false;
            int newSellerGoodsDayLimit = 7;
            Syspara newSellerGoodsDayLimitParam = sysparaService.find(SysParaCode.NEW_SELLER_GOODS_DAY_LIMIT.getCode());
            if (newSellerGoodsDayLimitParam != null) {
                String value = newSellerGoodsDayLimitParam.getValue().trim();
                newSellerGoodsDayLimit = Integer.parseInt(value);
            }
            if (sellerGoods.getFirstShelfTime() != null
                    && sellerGoods.getFirstShelfTime() > 0
                    && sellerGoods.getFirstShelfTime() + (long)newSellerGoodsDayLimit * 24L * 3600L * 1000L > now.getTime()) {
                // 还处于新品标记期间
                isRefreshNewFlag = true;
                redisHandler.zadd(RedisKeys.SELLER_GOODS_FIRST_SHELF_TIME, sellerGoods.getFirstShelfTime(), info.getSellerGoodsId());
            }

            // Double oldDiscountEndTime = redisHandler.zscore(RedisKeys.SELLER_GOODS_DISCOUNT_ENDTIME, info.getSellerGoodsId());
            if (sellerGoods.getDiscountRatio() == null) {
                sellerGoods.setDiscountRatio(0.0);
            }
            if (sellerGoods.getDiscountRatio() > 0.0 && sellerGoods.getDiscountRatio() < 1.0) {
                Date discountStartTime = sellerGoods.getDiscountStartTime();
                Date discountEndTime = sellerGoods.getDiscountEndTime();

                // 异步识别商品折扣状态是否有变化，以触发重新计算商品展示权重时，需要在此处标记下商品折扣状态变更
                if (!syncRefreshShowWeight) {
                    // 简单粗暴地处理方式：直接相对于上一次状态有变更
                    redisHandler.zincrby(RedisKeys.SELLER_GOODS_DISCOUNT_STATE_CHANGED, 1, info.getSellerGoodsId());
                }

                if (discountEndTime != null && discountEndTime.after(now)) {
                    // 折扣时间未结束，此处先不管是否已开始折扣
                    redisHandler.zadd(RedisKeys.SELLER_GOODS_DISCOUNT_ENDTIME, discountEndTime.getTime(), info.getSellerGoodsId());
                } else {
                    // 折扣活动已过期
                    redisHandler.zrem(RedisKeys.SELLER_GOODS_DISCOUNT_ENDTIME, info.getSellerGoodsId());
                }
            } else {
                // 无效折扣比例
                redisHandler.zrem(RedisKeys.SELLER_GOODS_DISCOUNT_ENDTIME, info.getSellerGoodsId());
            }

            List<String> sellerGoodsIdList = new ArrayList<>();
            if (syncRefreshShowWeight) {
                sellerGoodsIdList.add(info.getSellerGoodsId());
                goodsStatisticsService.updateRefreshSellerGoodsShowWeight(sellerGoodsIdList);

                if (isRefreshNewFlag) {
                    List<GoodsShowWeight> dataList = new ArrayList<>();
                    GoodsShowWeight oneWeight = new GoodsShowWeight();
                    oneWeight.setWeight(sellerGoods.getFirstShelfTime());
                    oneWeight.setGoodsId(info.getSellerGoodsId());
                    dataList.add(oneWeight);
                    sellerGoodsService.updateBatchShowWeight1(dataList);

                    logger.info("SellerGoodsUpdateEventListener onApplicationEvent 更新商品:{} 的 showWeight1 权重:{}", info.getSellerGoodsId(), sellerGoods.getFirstShelfTime());
                }
            }
        } catch (Exception e) {
            logger.error("商品信息更新相关的事件监听器任务处理报错，变更信息为: " + JsonUtils.getJsonString(info), e);
        }
    }

    public void setRedisHandler(RedisHandler redisHandler) {
        this.redisHandler = redisHandler;
    }

    public void setSellerGoodsService(SellerGoodsService sellerGoodsService) {
        this.sellerGoodsService = sellerGoodsService;
    }

    public void setGoodsStatisticsService(GoodsStatisticsService goodsStatisticsService) {
        this.goodsStatisticsService = goodsStatisticsService;
    }

    public void setSysparaService(SysparaService sysparaService) {
        this.sysparaService = sysparaService;
    }

}
