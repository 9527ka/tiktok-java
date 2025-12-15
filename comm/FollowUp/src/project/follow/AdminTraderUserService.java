package project.follow;

import java.util.List;

import kernel.web.Page;

/**
 * 用户跟随交易员记录
 */
public interface AdminTraderUserService {

	/**
	 * 分页查询
	 */
	public Page pagedQuery(int pageNo, int pageSize, String name_para, String username);

	public void save(TraderUser entity);

	public void delete(String id);

	public void update(TraderUser entity);

	public List<TraderUser> findByPartyId(String partyId);

	public List<TraderUser> findByTraderPartyId(String traderPartyId);

	public TraderUser findById(String id);
}
