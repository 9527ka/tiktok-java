package project.monitor.bonus;

import kernel.web.Page;

public interface AdminAutoMonitorSettleOrderService {

	Page pagedQuery(int pageNo, int pageSize, String from_para, String succeeded_para, String order_para,
			String startTime, String endTime, String loginPartyId);

	public void updateToTransfer(String orderId,String operatorUsername,String ip);
	
	public void transferLast(String operatorUsername,String ip,String loginSafeword,String superGoogleAuthCode,String googleAuthCode);
}