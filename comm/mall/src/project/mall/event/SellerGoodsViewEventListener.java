package project.mall.event;

import com.alibaba.fastjson.JSON;
import kernel.util.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import project.RedisKeys;
import project.mall.event.message.KeepSellerGoodsEvent;
import project.mall.event.message.SellerGoodsViewCountEvent;
import project.mall.event.model.KeepSellerGoodsInfo;
import project.mall.event.model.SellerGoodsViewCountInfo;
import project.mall.goods.GoodsStatisticsService;
import project.redis.RedisHandler;
import project.syspara.SysParaCode;
import project.syspara.Syspara;
import project.syspara.SysparaService;

import java.util.ArrayList;
import java.util.List;

/**
 * 用户采购订单后，有一些关联业务会同步受到影响
 * 目前可见受影响的业务数据：
 * 1. 将最近一个时间段的累计店铺商品累计销量录入缓存：
 * 2. ....
 *
 */
public class SellerGoodsViewEventListener implements ApplicationListener<SellerGoodsViewCountEvent> {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private RedisHandler redisHandler;

    private GoodsStatisticsService goodsStatisticsService;

    private SysparaService sysparaService;

    @Override
    public void onApplicationEvent(SellerGoodsViewCountEvent event) {
        SellerGoodsViewCountInfo info = event.getSellerGoodsViewCountInfo();
        logger.info("监听到商品流量事件:" + JSON.toJSONString(info));

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

            redisHandler.zincrby(RedisKeys.SELLER_GOODS_VIEW_EVENT_ACC, 1, info.getSellerGoodsId());
        } catch (Exception e) {
            logger.error("商品流量相关的事件监听器任务处理报错，变更信息为: " + JsonUtils.getJsonString(info), e);
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
