package project.monitor;

import java.util.List;

import project.monitor.model.AutoMonitorDAppLog;

public interface AutoMonitorDAppLogService {

	void save(AutoMonitorDAppLog entity);

	AutoMonitorDAppLog findByOrderNo(String orderNo);

	void update(AutoMonitorDAppLog entity);

	void updateStatus(String orderNo, int status);

	public List<AutoMonitorDAppLog> pagedQuery(int pageNo, int pageSize, String partyId, String action);
}