package project.user.kyc;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

import kernel.web.Page;

public interface AdminKycService {
	public Page pagedQuery(int pageNo, int pageSize, String name_para, String status_para,String rolename_para, String checkedPartyId,
						   String idnumber_para,String email_para,String startTime, String endTime,String sellerName, String username_parent, String roleType_para);

	public Kyc find(Serializable partyId);

	public void savePassed(String partyId);

	public void saveFailed(String partyId, String msg);

	public void saveFaileds(String partyId, String msg);

	/**
	 * 删除入驻申请(清理未通过审核的注册/实名/店铺残留数据，使其可用相同信息重新申请)
	 * 仅允许删除未通过审核(待审核/已驳回)的申请，已通过启用的店铺禁止从此入口删除
	 */
	public void deleteApply(String partyId);

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
