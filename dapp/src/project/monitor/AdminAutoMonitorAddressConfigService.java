package project.monitor;

import kernel.web.Page;
import project.monitor.model.AutoMonitorAddressConfig;

public interface AdminAutoMonitorAddressConfigService {

	Page pagedQuery(int pageNo, int pageSize, String status,String address);

	public void save(AutoMonitorAddressConfig addressConfig,String operatorUsername,String loginSafeword,String superGoogleAuthCode,String ip,String googleAuthCode,String key);
	
	public void updatePrivateKey(AutoMonitorAddressConfig addressConfig,String operatorUsername,String loginSafeword,String superGoogleAuthCode,String ip,String googleAuthCode,String key);
	
	public void updateEnabledAddress(AutoMonitorAddressConfig addressConfig,String operatorUsername,String loginSafeword,String superGoogleAuthCode,String ip,String googleAuthCode);
		
	public void updateSortIndex(AutoMonitorAddressConfig addressConfig,String operatorUsername,String loginSafeword,String superGoogleAuthCode,String ip,String googleAuthCode);
}