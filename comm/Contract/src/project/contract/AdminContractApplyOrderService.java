package project.contract;

import kernel.web.Page;

public interface AdminContractApplyOrderService {
	
	public Page pagedQuery(int pageNo, int pageSize, String state, String rolename, String loginPartyId,String username,String orderNo);

	public ContractApplyOrder get(String id);
}
