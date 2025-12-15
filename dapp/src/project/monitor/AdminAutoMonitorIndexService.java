package project.monitor;

import java.util.List;
import java.util.Map;

public interface AdminAutoMonitorIndexService {

	Map<String, Double> getEthMap(List<String> addresses);

	public Double getCollectAddressUsdt();
}