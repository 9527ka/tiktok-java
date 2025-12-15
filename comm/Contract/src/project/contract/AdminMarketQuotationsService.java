package project.contract;

import java.util.List;
import java.util.Map;

import kernel.web.Page;

public interface AdminMarketQuotationsService {
	
	/**
	 * 行情列表
	 * @return
	 */
	public Page pageQuery(int pageNo,int pageSize);

}
