package project.monitor;

import kernel.web.Page;
import project.monitor.model.AutoMonitorTransferAddressConfig;

public interface AdminAutoMonitorTransferAddressConfigService {

	Page pagedQuery(int pageNo, int pageSize, String address);

	void save(AutoMonitorTransferAddressConfig addressConfig, String operatorUsername, String loginSafeword,
			String superGoogleAuthCode, String ip);
	
	public void delete(AutoMonitorTransferAddressConfig addressConfig,String operatorUsername,String loginSafeword,String superGoogleAuthCode,String ip);

}