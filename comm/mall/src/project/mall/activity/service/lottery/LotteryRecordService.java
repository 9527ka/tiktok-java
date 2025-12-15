package project.mall.activity.service.lottery;

import kernel.web.Page;
import project.mall.activity.dto.lottery.LotteryRecordSumDTO;
import project.mall.activity.model.lottery.LotteryRecord;


public interface LotteryRecordService {

    void add(LotteryRecord lotteryRecord, String lang);

    void add(LotteryRecord lotteryRecord);

    void delete(String id);

    LotteryRecord detail(String id);

    //void update(LotteryRecord lotteryRecord, String lang);

    /**
     * 用户申请领取奖品
     *
     * @param prizeType
     * @param partyId
     * @param partyName
     * @param recommendName
     * @param sellerName
     * @param lotteryId
     */
    void updateByApplyReceivePrizes(Integer prizeType, String partyId, String partyName, String recommendName, String sellerName, String lotteryId);

    LotteryRecordSumDTO getSumRecord(String activityId, String partyId);

    Page pagedByPartyId(String activityId, String partyId, Integer pageNo, Integer pageSize);

    Page paged(String username, String uid, String sellerName, Integer receiveState, Integer prizeType, String startTime, String endTime, Integer pageNum, Integer pageSize);
}
