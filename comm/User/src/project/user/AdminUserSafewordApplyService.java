package project.user;

import java.util.Date;

import kernel.web.Page;

public interface AdminUserSafewordApplyService {
	
	public Page pagedQuery(int pageNo, int pageSize, String name_para, Integer status_para,String rolename_para, String checkedPartyId, Integer operate);

	public void savePassed(String id,String operatorUsername,String safeword);

	public void saveFailed(String id, String msg);
	
	/**
	 * 某个时间后未处理数量,没有时间则全部
	 */
	public Long getUntreatedCount(Date time, String loginPartyId);
	
}
