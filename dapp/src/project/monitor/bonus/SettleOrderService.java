package project.monitor.bonus;

import java.io.Serializable;
import java.util.List;

import project.monitor.bonus.model.SettleOrder;

public interface SettleOrderService {

	public void save(SettleOrder entity);

	public void update(SettleOrder entity);

	public List<SettleOrder> findBySucceeded(int succeeded);
	
	public List<SettleOrder> findUntreated();

	public SettleOrder findById(Serializable id);
}
