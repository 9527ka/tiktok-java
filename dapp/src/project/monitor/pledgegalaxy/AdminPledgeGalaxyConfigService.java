package project.monitor.pledgegalaxy;

import kernel.web.Page;

public interface AdminPledgeGalaxyConfigService {

	public Page pagedQuery(int pageNo, int pageSize, String name_para, String rolename);

}
