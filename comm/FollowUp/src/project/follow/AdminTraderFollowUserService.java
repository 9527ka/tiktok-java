package project.follow;

import kernel.web.Page;

public interface AdminTraderFollowUserService {

	/**
	 * 分页查询 name_para交易员名称 username 用户名
	 * 
	 */
	public Page pagedQuery(int pageNo, int pageSize, String name_para, String username);

	public void delete(String id);

	public void update(TraderFollowUser entity);

	public void save(TraderFollowUser entity, String trader_id);


	public TraderFollowUser findById(String id);
}
