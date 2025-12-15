package project.monitor.activity;

import kernel.web.Page;

public interface AdminActivityOrderService {

	/**
	 * 代理分页查询
	 * 
	 */
	public Page pagedQuery(int pageNo, int pageSize, String name_para,String title_para,String loginPartyId);


}
