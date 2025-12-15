package project.mall.goods.impl;

import cn.hutool.core.collection.CollectionUtil;
import kernel.util.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import project.mall.evaluation.EvaluationService;
import project.mall.goods.GoodsStatisticsService;
import project.mall.goods.KeepGoodsService;
import project.mall.goods.SellerGoodsService;
import project.mall.goods.model.SellerGoods;
import project.mall.goods.vo.GoodsShowWeight;
import project.mall.goods.vo.SellerGoodsShowWeightParamsVO;
import project.redis.RedisHandler;

import java.util.*;

@Slf4j
public class GoodsStatisticsServiceImpl implements GoodsStatisticsService {
    private RedisHandler redisHandler;

    private SellerGoodsService sellerGoodsService;

    private KeepGoodsService keepGoodsService;

    private EvaluationService evaluationService;



    /**
     * 商品综合排序
     * 给所有因子的影响权重比例给他一个系数如下
     * A: 销量: 10%
     * B: 好评率: 50%
     * C: 收藏量: 15%
     * D: 浏览量: 5%
     * E: 活动中: 20%
     * 排序权重值M=A+B+C+D+E
     *
     * 总权重10000
     * A商品
     * 销量: 100
     * 好评率:95%
     * 收藏: 100
     * 浏览量: 1000
     * 活动中: 是
     *
     * 影响前权重 (初始权重)
     * 10000*10%=1000
     * 10000*50%=5000
     * 10000*15%=1500
     * 10000*5%=500
     * 10000*20%=2000
     * 影响后的权重
     * 1000+销量
     * 5000*好评率
     * 1500+收藏量
     * 500+浏览量
     * 处于活动中+2000，无则默认2000
     *
     *
     * 此时A商品总权重:
     * 1.销量: 1000+100=1100
     * 2.好评率: 5000*95%=4750
     * 3.收藏: 1500+100=1600
     * 4.浏览权重: 500+1000=1500
     * 5.活动: 2000+2000=4000
     *
     * 总权重=1100+4750+1600+1500+4000=12950
     *
     * @param goodsIdList
     */
    @Override
    public void updateRefreshSellerGoodsShowWeight(List<String> goodsIdList) {
        // 统计相关数据
        Map<String, SellerGoodsShowWeightParamsVO> sellerGoodsParamsMap = computeSellerGoods(goodsIdList);
        if (sellerGoodsParamsMap.isEmpty()) {
            return;
        }
        log.info("===> GoodsStatisticsServiceImpl updateRefreshSellerGoodsShowWeight 当前批次统计出的商品指标数据:{}", JsonUtils.bean2Json(sellerGoodsParamsMap));

        List<GoodsShowWeight> dataList = new ArrayList<>();
        for (String oneSellerGoodsId : sellerGoodsParamsMap.keySet()) {
            SellerGoodsShowWeightParamsVO staticParam = sellerGoodsParamsMap.get(oneSellerGoodsId);

            int totalWeight = 0;
            int standardWeight = 10000;

            int soldWeight = (int) (standardWeight * 0.1 + staticParam.getTotalSoldNum());
            int goodEvaluationWeight = (int) (standardWeight * 0.5 * staticParam.getGoodEvaluationRate());
            int keepWeight = (int) (standardWeight * 0.15 + staticParam.getTotalKeeped());
            int viewWeight = (int) (standardWeight * 0.05 + staticParam.getTotalViewCount());
            int inActivityWeight = 2000;
            if (staticParam.isInDiscount()) {
                inActivityWeight += (int) (standardWeight * 0.2);
            }

            totalWeight = soldWeight + goodEvaluationWeight + keepWeight + viewWeight + inActivityWeight;

            GoodsShowWeight oneUpdateData = new GoodsShowWeight();
            oneUpdateData.setGoodsId(oneSellerGoodsId);
            oneUpdateData.setWeight((long)totalWeight);

            dataList.add(oneUpdateData);
        }

        log.info("===> GoodsStatisticsServiceImpl updateRefreshSellerGoodsShowWeight 当前批次统计出的商品最新权重数据:{}", JsonUtils.bean2Json(dataList));
        sellerGoodsService.updateBatchShowWeight2(dataList);

    }


    public Map<String, SellerGoodsShowWeightParamsVO> computeSellerGoods(List<String> goodsIdList) {
        Map<String, SellerGoodsShowWeightParamsVO> sellerGoodsParamsMap = new HashMap();
        if (CollectionUtil.isEmpty(goodsIdList)) {
            return sellerGoodsParamsMap;
        }

        Date now = new Date();
        List<SellerGoods> sellerGoodsList = sellerGoodsService.getSellerGoodsBatch(goodsIdList);
        for (SellerGoods oneSellerGoods : sellerGoodsList) {
            if (oneSellerGoods.getIsValid() == 0 || oneSellerGoods.getIsShelf() == 0) {
                continue;
            }

            SellerGoodsShowWeightParamsVO oneParamVo = new SellerGoodsShowWeightParamsVO();
            sellerGoodsParamsMap.put(oneSellerGoods.getId().toString(), oneParamVo);

            oneParamVo.setSellerGoodsId(oneSellerGoods.getId().toString());
            // 销量统计
            oneParamVo.setTotalSoldNum(oneSellerGoods.getSoldNum());
            // 好评占比统计
            oneParamVo.setGoodEvaluationRate(0.0);
            oneParamVo.setInDiscount(false);

            // 使用真实浏览量
            // 该表的字段废弃未用
            //oneParamVo.setTotalViewCount(oneSellerGoods.getViewsNum());

            if (oneSellerGoods.getDiscountRatio() == null) {
                oneSellerGoods.setDiscountRatio(0.0);
            }
            if (oneSellerGoods.getDiscountRatio() > 0.0 && oneSellerGoods.getDiscountRatio() < 1.0) {
                Date discountStartTime = oneSellerGoods.getDiscountStartTime();
                Date discountEndTime = oneSellerGoods.getDiscountEndTime();
                if (discountEndTime != null && discountEndTime.after(now)) {
                    // 折扣时间未结束
                    if (discountStartTime == null || discountStartTime.before(now)) {
                        oneParamVo.setInDiscount(true);
                    }
                }
            } else {
                // 无效折扣比例
                oneParamVo.setInDiscount(false);
            }
        }

        // 使用真实浏览量
        // 计算最新 viewCount
        Map<String, Long> sellerGoodsViewCountMap = sellerGoodsService.getRealViewNums(goodsIdList);
        for (String oneSellerGoodsId : sellerGoodsParamsMap.keySet()) {
            Long viewCount = sellerGoodsViewCountMap.get(oneSellerGoodsId);
            if (viewCount == null) {
                viewCount = 0L;
            }
            SellerGoodsShowWeightParamsVO staticParam = sellerGoodsParamsMap.get(oneSellerGoodsId);
            // 流量统计
            staticParam.setTotalViewCount(viewCount);
        }

        // 计算最新评论和好评占比
        Map<String, Map<String, Integer>> evaluationStaticMap = evaluationService.getEvaluationTypeCountByGoodIds(goodsIdList);
        for (String oneSellerGoodsId : sellerGoodsParamsMap.keySet()) {
            Map<String, Integer> currentSellerGoodsEvaluationMap = evaluationStaticMap.get(oneSellerGoodsId);
            if (currentSellerGoodsEvaluationMap == null) {
                continue;
            }

            // 评价类型 1-好评 2-中评 3-差评
            Integer evaluationType1Count = currentSellerGoodsEvaluationMap.get("1");
            Integer evaluationType2Count = currentSellerGoodsEvaluationMap.get("2");
            Integer evaluationType3Count = currentSellerGoodsEvaluationMap.get("3");

            evaluationType1Count = evaluationType1Count == null ? 0 : evaluationType1Count;
            evaluationType2Count = evaluationType2Count == null ? 0 : evaluationType2Count;
            evaluationType3Count = evaluationType3Count == null ? 0 : evaluationType3Count;

            double goodEvaluationRate = 0.0;
            if (evaluationType1Count + evaluationType2Count + evaluationType3Count > 0) {
                goodEvaluationRate = Double.valueOf(evaluationType1Count) / Double.valueOf(evaluationType1Count + evaluationType2Count + evaluationType3Count);
            }

            SellerGoodsShowWeightParamsVO staticParam = sellerGoodsParamsMap.get(oneSellerGoodsId);
            // 最新好评占比统计
            staticParam.setGoodEvaluationRate(goodEvaluationRate);
        }

        // 收藏量统计
        Map<String, Integer> goodsKeepCountMap = keepGoodsService.getSellerGoodsKeepCount(goodsIdList);
        for (String oneSellerGoodsId : sellerGoodsParamsMap.keySet()) {
            Integer keepCount = goodsKeepCountMap.get(oneSellerGoodsId);
            if (keepCount == null) {
                continue;
            }

            SellerGoodsShowWeightParamsVO staticParam = sellerGoodsParamsMap.get(oneSellerGoodsId);
            staticParam.setTotalKeeped(keepCount);
        }

        return sellerGoodsParamsMap;
    }


    public void setRedisHandler(RedisHandler redisHandler) {
        this.redisHandler = redisHandler;
    }

    public void setSellerGoodsService(SellerGoodsService sellerGoodsService) {
        this.sellerGoodsService = sellerGoodsService;
    }

    public void setKeepGoodsService(KeepGoodsService keepGoodsService) {
        this.keepGoodsService = keepGoodsService;
    }

    public void setEvaluationService(EvaluationService evaluationService) {
        this.evaluationService = evaluationService;
    }

}
