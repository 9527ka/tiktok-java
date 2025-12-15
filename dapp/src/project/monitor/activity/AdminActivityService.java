package project.monitor.activity;

import kernel.web.Page;

public interface AdminActivityService {

	/**
	 * 代理分页查询
	 * 
	 */
	public Page pagedQuery(int pageNo, int pageSize, String name_para,String title_para);
	
	public void save(Activity entity);

	public void update(Activity entity);

	public Activity findById(String id);

	public void delete(String id);

	public Activity findByPartyId(String partyId);


}
