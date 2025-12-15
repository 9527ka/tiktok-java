package project.ddos;

import kernel.web.Page;
import project.ddos.model.IpMenu;

public interface AdminIpMenuService {

	Page pagedQuery(int pageNo, int pageSize, String ip_para, String startTime, String endTime);

	public void save(IpMenu entity, String operatorUsername, String loginSafeword, String ip);

	public void update(IpMenu entity, String operatorUsername, String loginSafeword, String ip);

	public void delete(String menu_ip, String operatorUsername, String loginSafeword, String ip);

	void updateIp(IpMenu ipMenu, String old, String usernameLogin, String loginSafeword, String ip);
}