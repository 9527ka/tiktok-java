package project.contract;

import kernel.web.Page;

public interface AdminContractOrderService {

	public Page pagedQuery(int pageNo, int pageSize, String state, String rolename, String loginPartyId,String startTime,String endTime,String username,String orderNo);

	public ContractOrder get(String id);
}
