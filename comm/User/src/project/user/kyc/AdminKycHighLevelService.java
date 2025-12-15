package project.user.kyc;

import java.io.Serializable;
import java.util.Date;

import kernel.web.Page;

public interface AdminKycHighLevelService {
	public Page pagedQuery(int pageNo, int pageSize, String name_para, Integer status_para,String rolename_para, String checkedPartyId);

	public KycHighLevel findByPartyId(Serializable partyId);
	
	public KycHighLevel findById(Serializable id);

	public void savePassed(String partyId);

	public void saveFailed(String partyId, String msg);
	/**
	 * 某个时间后未处理数量,没有时间则全部
	 *  @param time
	 * @return
	 */
	public Long getUntreatedCount(Date time, String loginPartyId);
}
