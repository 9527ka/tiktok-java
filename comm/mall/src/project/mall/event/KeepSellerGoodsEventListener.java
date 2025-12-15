package project.mall.event;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import kernel.util.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import project.RedisKeys;
import project.mall.event.message.EvaluationOrderGoodsEvent;
import project.mall.event.message.KeepSellerGoodsEvent;
import project.mall.event.model.KeepSellerGoodsInfo;
import project.mall.event.model.OrderGoodsEvaluationInfo;
import project.mall.goods.GoodsStatisticsService;
import project.mall.orders.GoodsOrdersService;
import project.mall.orders.model.MallOrdersGoods;
import project.redis.RedisHandler;
import project.syspara.SysParaCode;
import project.syspara.Syspara;
import project.syspara.SysparaService;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 用户采购订单后，有一些关联业务会同步受到影响
 * 目前可见受影响的业务数据：
 * 1. 将最近一个时间段的累计店铺商品累计销量录入缓存：
 * 2. ....
 *
 */
public class KeepSellerGoodsEventListener implements ApplicationListener<KeepSellerGoodsEvent> {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private RedisHandler redisHandler;

    private GoodsStatisticsService goodsStatisticsService;

    private SysparaService sysparaService;

    @Override
    public void onApplicationEvent(KeepSellerGoodsEvent event) {
        KeepSellerGoodsInfo info = event.getKeepSellerGoodsInfo();
        logger.info("监听到商品收藏处理事件:" + JSON.toJSONString(info));

        try {
            boolean syncRefreshShowWeight = true;
            Syspara syspara = sysparaService.find(SysParaCode.SYNC_REFRESH_SELLER_GOODS_SHOW_WEIGHT.getCode());
            if (syspara != null) {
                String flag = syspara.getValue().trim();
                if (flag.equalsIgnoreCase("false")) {
                    syncRefreshShowWeight = false;
                }
            }
            if (syncRefreshShowWeight) {
                List<String> sellerGoodsIdList = new ArrayList<>();
                sellerGoodsIdList.add(info.getSellerGoodsId());
                goodsStatisticsService.updateRefreshSellerGoodsShowWeight(sellerGoodsIdList);
                return;
            }

            redisHandler.zincrby(RedisKeys.SELLER_GOODS_KEEP_EVENT_ACC, 1, info.getSellerGoodsId());
        } catch (Exception e) {
            logger.error("商品收藏相关的事件监听器任务处理报错，变更信息为: " + JsonUtils.getJsonString(info), e);
        }

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
