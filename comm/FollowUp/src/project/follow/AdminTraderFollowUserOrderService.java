package project.follow;

import kernel.web.Page;

public interface AdminTraderFollowUserOrderService {

	/**
	 * 分页查询 name_para交易员名称 username 用户名
	 * 
	 */
	public Page pagedQuery(int pageNo, int pageSize, String trader_name_para,
			String username,String rolename);

	
}
