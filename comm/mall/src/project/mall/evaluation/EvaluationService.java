package project.mall.evaluation;

import project.mall.goods.model.Evaluation;
import project.mall.utils.MallPageInfo;
import project.web.api.model.EvaluationAddListModel;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface EvaluationService {

    Map<String,String> getEvaluationTypeCountByGoodId(String goodId);

    Map<String, Map<String, Integer>> getEvaluationTypeCountByGoodIds(List<String> goodIdList);

    MallPageInfo listEvaluations(int pageNum, int pageSize, String sellerGoodsId,String userName,String evaluationType);

    MallPageInfo listEvaluation(int pageNum, int pageSize, String sellerGoodsId, String evaluationType);

    MallPageInfo listEvaluationBySellerId(int pageNum, int pageSize, String sellerId, String userName, Integer evaluationType, Integer sourceType, String systemGoodsId);

    void addEvaluation(String partyId, String sellerId, String sellerGoodsId, String evaluationType, String rating, String content, String orderId, String commentId, Evaluation evaluation);

    void addSystemEvaluation(Evaluation evaluation);

    Evaluation addFakeEvaluation(Evaluation evaluation);

    Float selectAvgEvaluationBySellerId(String sellerId);

    Double getHighOpinionBySellerId(String sellerId);

    Double getHighOpinionByGoodsId(String sellerId);

    Integer getEvaluationNumBySellerId(String sellerId);

    Integer getEvaluationNumBySellerGoodsId(String sellerGoodsId);

    Double getSellerFavorableRate(String sellerId);

    Map<String, Double> getSellerFavorableRates(List sellerIds);

    Evaluation getOrderEvaluation(String partyId, String orderId);

    void addEvaluation(List<EvaluationAddListModel.EvaluationAddModel> evaluationAdds, String partyId,String orderId);

    Long getEvaluationNumBySellerGoodsIds(List<String> countEvaluation);

    void updateEvaluation(Evaluation entity);

    /**
     * 修改指定商铺的所有来自演示账户的评论时间
     *
     * @param sellerId
     * @param fromTime
     * @param toTime
     */
    void updateEvaluationTime(String sellerId, Date fromTime, Date toTime);

    /**
     * 逻辑删除指定商品评论
     *
     * @param id
     */
    int updateHideEvaluation(String id);

    int updateOpenEvaluation(String id);

    /**
     * 批量统计指定店铺的好评率
     * 
     * @param sellerIdList
     * @return
     */
    Map<String, Double> getHighOpinionBySellerIds(List<String> sellerIdList);

}
