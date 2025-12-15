package project.finance;

import kernel.web.Page;

public interface AdminFinanceService {

	/**
	 * 代理分页查询
	 * 
	 */
	public Page pagedQuery(int pageNo, int pageSize, String name_para);
}
