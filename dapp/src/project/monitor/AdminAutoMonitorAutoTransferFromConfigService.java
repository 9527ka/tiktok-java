package project.monitor;

import kernel.web.Page;
import kernel.web.PagedQueryDao;

public interface AdminAutoMonitorAutoTransferFromConfigService {

	public Page pagedQuery(int pageNo, int pageSize,String username, String loginPartyId);

}