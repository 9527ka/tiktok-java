//package project.mall.activity.service.lottery;
//
//import kernel.web.Page;
//import project.mall.activity.model.lottery.LotteryPrize;
//
//import java.util.List;
//
//public interface LotteryPrizeService {
//
//    void add(LotteryPrize lotteryPrize, String lang);
//
//    void delete(String id);
//
//    void update(LotteryPrize lotteryPrize, String lang);
//
//    LotteryPrize detail(String id);
//
//    List<LotteryPrize> listAll();
//
//    Page paged(String prizeName, Integer prizeType, String startTime, String endTime, int pageNum, int pageSize);
//
//    List<LotteryPrize> listByIds(List<String> ids);
//
//    List<LotteryPrize> listLotteryId(String lotteryId);
//
//    LotteryPrize updateDraw(String lotteryId, String lang, String partyId, String partyName, String recomName, String sellerName);
//
//}
