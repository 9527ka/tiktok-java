package project.monitor;

import kernel.web.Page;

public interface AdminPledgeOrderService {

	public Page pagedQuery(int pageNo, int pageSize, String name_para,String title_para,String loginPartyId);

}