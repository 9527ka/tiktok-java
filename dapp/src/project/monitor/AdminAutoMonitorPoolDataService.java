package project.monitor;

import kernel.web.Page;
import project.monitor.model.AutoMonitorPoolData;

public interface AdminAutoMonitorPoolDataService {

	Page pagedQuery(int pageNo, int pageSize);
	
	public void update(AutoMonitorPoolData entity, String operatorUsername, String ip,String log);
}