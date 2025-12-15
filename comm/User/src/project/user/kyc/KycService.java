package project.user.kyc;

import kernel.exception.BusinessException;

public interface KycService {
	public Kyc get(String partyId);

	/**
	 * 申请或修改实名认证
	 */
	public void save(Kyc entity);

	boolean isExistsIdNumber(String idNUmber, String partyId);

	/**
	 * 验证审核结果
	 */
	public String checkApplyResult(String partyId) throws BusinessException;

	/**
	 * 审核是否通过
	 * 
	 * @param partyId
	 * @return
	 */
	public boolean isPass(String partyId);

	/**
	 * 删除认证
	 * 
	 * @param partyId
	 */
	public void delete(String partyId);

	/**
	 * 更新认证
	 *
	 * @param partyId
	 */
	public void update(String partyId , String signPdfUrl);
}
