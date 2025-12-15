package project.monitor.report;

import java.util.List;
import java.util.Map;

public interface AdminAutoMonitorUserMoneyStatisticsService {

	List<Map<String, Object>> getAll(String loginPartyId);


}