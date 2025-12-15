package project.mall.activity.service;

import kernel.web.Page;
import project.mall.activity.model.ActivityPrizePool;

import java.util.List;

public interface ActivityPrizePoolService {

	void save(ActivityPrizePool prizePool);

	void delete(String id);

	void deleteLogic(String id);

	void update(ActivityPrizePool prizePool, String lang);

	ActivityPrizePool detail(String id);

	List<ActivityPrizePool> listAll();

	Page listPrize(String prizeName, int prizeType, int status, String startTime, String endTime, int pageNum, int pageSize);

	List<ActivityPrizePool> listLotteryPrize(int size);

	List<ActivityPrizePool> listByIds(List<String> ids);

	List<ActivityPrizePool> listDefaltPrize(int status, int size);

}
