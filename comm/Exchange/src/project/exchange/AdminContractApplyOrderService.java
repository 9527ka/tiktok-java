package project.exchange;

import kernel.web.Page;

public interface AdminContractApplyOrderService {

	public Page pagedQuery(int pageNo, int pageSize, String state, String rolename, String loginPartyId);

	public ExchangeApplyOrder get(String id);
}
