package project.mall.activity.service.lottery;

import kernel.web.Page;
import project.mall.activity.model.lottery.LotteryReceive;

public interface LotteryReceiveService {

    void add(LotteryReceive lotteryReceive);

    void delete(String id);

    LotteryReceive detail(String id);

    //void update(LotteryReceive lotteryReceive, String lang);

    void update(LotteryReceive lotteryReceive);

    Page paged(String username, String uid, String sellerName, Integer state, Integer prizeType, String startTime, String endTime, Integer pageNum, Integer pageSize);

    void updatePayout(String partyId, double amount);

}
