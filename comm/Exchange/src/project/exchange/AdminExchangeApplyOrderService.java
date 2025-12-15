package project.exchange;

import kernel.web.Page;

public interface AdminExchangeApplyOrderService {

	public Page pagedQuery(int pageNo, int pageSize, String state, String rolename, String loginPartyId,String name_para,String orderNo);

	public ExchangeApplyOrder get(String id);
}
