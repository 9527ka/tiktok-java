package project.mall.event;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSON;
import kernel.util.JsonUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import project.RedisKeys;
import project.blockchain.RechargeBlockchainService;
import project.blockchain.event.message.RechargeSuccessEvent;
import project.blockchain.event.model.RechargeInfo;
import project.mall.event.message.PurchaseOrderGoodsEvent;
import project.mall.event.model.PurchaseOrderInfo;
import project.mall.goods.GoodsStatisticsService;
import project.mall.orders.GoodsOrdersService;
import project.mall.orders.model.MallOrdersGoods;
import project.party.UserMetricsService;
import project.party.model.UserMetrics;
import project.redis.RedisHandler;
import project.syspara.SysParaCode;
import project.syspara.Syspara;
import project.syspara.SysparaService;
import project.wallet.WalletLogService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * 用户采购订单后，有一些关联业务会同步受到影响
 * 目前可见受影响的业务数据：
 * 1. 将最近一个时间段的累计店铺商品累计销量录入缓存：
 * 2. ....
 *
 */
public class PurchaseOrderGoodsEventListener implements ApplicationListener<PurchaseOrderGoodsEvent> {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private GoodsOrdersService goodsOrdersService;

    private RedisHandler redisHandler;

    private GoodsStatisticsService goodsStatisticsService;

    private SysparaService sysparaService;

    @Override
    public void onApplicationEvent(PurchaseOrderGoodsEvent event) {
        PurchaseOrderInfo info = event.getPurchaseOrderInfo();
        logger.info("监听到商家采购订单事件:" + JSON.toJSONString(info));

        try {
            List<MallOrdersGoods> orderGoodsList = goodsOrdersService.getOrderGoods(info.getOrderId());
            logger.info("采购订单:{} 相关的商品数量为:{}", info.getOrderId(), orderGoodsList.size());
            if (CollectionUtil.isEmpty(orderGoodsList)) {
                return;
            }

            List<String> sellerGoodsIdList = new ArrayList<>();
            for (MallOrdersGoods oneGoods : orderGoodsList) {
                sellerGoodsIdList.add(oneGoods.getGoodsId());
            }

            boolean syncRefreshShowWeight = true;
            Syspara syspara = sysparaService.find(SysParaCode.SYNC_REFRESH_SELLER_GOODS_SHOW_WEIGHT.getCode());
            if (syspara != null) {
                String flag = syspara.getValue().trim();
                if (flag.equalsIgnoreCase("false")) {
                    syncRefreshShowWeight = false;
                }
            }
            if (syncRefreshShowWeight) {
                goodsStatisticsService.updateRefreshSellerGoodsShowWeight(sellerGoodsIdList);
                return;
            }

            for (MallOrdersGoods oneGoods : orderGoodsList) {
                redisHandler.zincrby(RedisKeys.SELLER_GOODS_PURCHASE_ACC, oneGoods.getGoodsNum(), oneGoods.getGoodsId());
            }

        } catch (Exception e) {
            logger.error("商家采购订单相关的事件监听器任务处理报错，变更信息为: " + JsonUtils.getJsonString(info), e);
        }

    }


    public void setGoodsOrdersService(GoodsOrdersService goodsOrdersService) {
        this.goodsOrdersService = goodsOrdersService;
    }

    public void setRedisHandler(RedisHandler redisHandler) {
        this.redisHandler = redisHandler;
    }

    public void setGoodsStatisticsService(GoodsStatisticsService goodsStatisticsService) {
        this.goodsStatisticsService = goodsStatisticsService;
    }

    public void setSysparaService(SysparaService sysparaService) {
        this.sysparaService = sysparaService;
    }

}
