package project.futures;

import kernel.web.Page;

public interface AdminFuturesOrderService {

	public Page pagedQuery(int pageNo, int pageSize, String state, String rolename, String loginPartyId,
			String username,String orderNo,String symbol,String direction,Double volume);

	public FuturesOrder get(String id);
}
