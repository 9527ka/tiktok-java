package project.monitor;

import kernel.web.Page;

public interface AdminAutoMonitorDAppLogService {

	Page pagedQueryMoneyLog(int pageNo, int pageSize, String action_para, String name_para, String loginPartyId,
			String rolename_para, String startTime, String endTime);

}