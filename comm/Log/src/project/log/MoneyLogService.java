package project.log;

import java.util.Date;
import java.util.List;

import kernel.web.Page;
import project.mall.orders.model.MallOrderRebate;

public interface MoneyLogService {
	public void save(MoneyLog paramMoneyLog);

	public MoneyLog findById(String id);

	public Page pagedQuery(int pageNo, int pageSize, String category, String content_type, String partyId, Date startTime, Date endTime);

	public List<MoneyLog> findLogsByConentTypeAndDate(String type, String date);

	public List<MoneyLog> findByLog(String type, String log);

	List<MallOrderRebate> getOrderRebate(String orderId);

	void update(MoneyLog money);
}
