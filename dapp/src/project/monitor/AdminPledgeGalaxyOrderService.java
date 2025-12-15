package project.monitor;

import kernel.web.Page;

public interface AdminPledgeGalaxyOrderService {

	public Page pagedQuery(int pageNo, int pageSize, String order_no, String name, String rolename, Integer status, Integer type, String loginPartyId);

}
