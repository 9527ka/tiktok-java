package project.miner;

import kernel.web.Page;

public interface AdminMinerService {

	/**
	 * 代理分页查询
	 * 
	 */
	public Page pagedQuery(int pageNo, int pageSize, String name_para);
}
