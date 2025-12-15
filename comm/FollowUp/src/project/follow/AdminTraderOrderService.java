package project.follow;

import kernel.web.Page;

public interface AdminTraderOrderService {

	/**
	 * 分页查询 name_para交易员名称 username 用户名
	 * 
	 */
	public Page pagedQuery(int pageNo, int pageSize, String trader_name_para,
			String username,String rolename);
	
	public void delete(String id);

	public void update(TraderOrder entity);

	public void save(TraderOrder entity);


	public TraderOrder findById(String id);
	
}
