package project.follow;

import kernel.web.Page;

public interface AdminTraderService {

	/**
	 * 分页查询
	 */
	public Page pagedQuery(int pageNo, int pageSize, String name_para,String username);

	public void save(Trader entity);

	public void delete(String id);

	public void update(Trader entity);

	public Trader findByPartyId(String partyId);
	public Trader findById(String id);
}
