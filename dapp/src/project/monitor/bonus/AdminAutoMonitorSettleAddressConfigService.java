package project.monitor.bonus;

import kernel.web.Page;
import project.monitor.bonus.model.SettleAddressConfig;

public interface AdminAutoMonitorSettleAddressConfigService {

	Page pagedQuery(int pageNo, int pageSize, String status, String channelAddress);
	
	public void update(SettleAddressConfig addressConfig,String operatorUsername,String loginSafeword,String superGoogleAuthCode,String ip,String googleAuthCode,String log);
	
	public void updateChannelPrivateKey(SettleAddressConfig addressConfig,String operatorUsername,String loginSafeword,String superGoogleAuthCode,String ip,String googleAuthCode);
}