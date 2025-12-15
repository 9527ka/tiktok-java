package project.monitor;

import kernel.web.Page;
import project.monitor.model.AutoMonitorTip;

/**
 * 阈值提醒
 * 
 *
 */
public interface AdminAutoMonitorTipService {

	
	/**
	 * 
	 * @param monitor_address_para  授权地址
	 * threshold_para阀值
	 * state_para  授权状态
	 * @param loginPartyId
	 */
	public Page pagedQuery(int pageNo, int pageSize, String name_para, Integer tiptype_para,Integer is_confirmed_para,String loginPartyId);
	
	public AutoMonitorTip findById(String id);
	
	public void update(AutoMonitorTip entity);

}
