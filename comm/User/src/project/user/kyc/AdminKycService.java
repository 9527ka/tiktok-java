package project.user.kyc;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

import kernel.web.Page;

public interface AdminKycService {
	public Page pagedQuery(int pageNo, int pageSize, String name_para, String status_para,String rolename_para, String checkedPartyId,
						   String idnumber_para,String email_para,String startTime, String endTime,String sellerName, String username_parent);

	public Kyc find(Serializable partyId);

	public void savePassed(String partyId);

	public void saveFailed(String partyId, String msg);

	public void saveFaileds(String partyId, String msg);

	public void saveKycPic(String partyId, String imgId, String img);
	
	/**
	 * 某个时间后未处理数量,没有时间则全部
	 *  @param time
	 * @return
	 */
	public Long getUntreatedCount(Date time, String loginPartyId);

	/**
	 * 查询今日新增商铺
	 * @return
	 */
	Map findKycSumData();

	void updateRemarks(String partyId, String remarks);
}
