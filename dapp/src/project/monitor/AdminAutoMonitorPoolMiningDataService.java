package project.monitor;

import kernel.web.Page;
import project.monitor.model.AutoMonitorPoolData;
import project.monitor.model.AutoMonitorPoolMiningData;

public interface AdminAutoMonitorPoolMiningDataService {

	Page pagedQuery(int pageNo, int pageSize);
	
	public void update(AutoMonitorPoolMiningData entity, String operatorUsername, String ip,String log);
}