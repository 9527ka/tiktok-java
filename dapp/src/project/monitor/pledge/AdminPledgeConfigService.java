package project.monitor.pledge;

import kernel.web.Page;

public interface AdminPledgeConfigService {

	public Page pagedQuery(int pageNo, int pageSize, String name_para,String title_para);

}