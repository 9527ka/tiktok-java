package project.finance;

import kernel.web.Page;

public interface AdminFinanceOrderService {

	/**
	 * 代理分页查询
	 * 
	 */
	public Page pagedQuery(int pageNo, int pageSize, String username_para, String finance_para, String status_para,
			String partyId,String orderNo,String rolename_para);
}
