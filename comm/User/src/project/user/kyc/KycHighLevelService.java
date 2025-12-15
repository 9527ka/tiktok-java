package project.user.kyc;

import kernel.exception.BusinessException;

public interface KycHighLevelService {
	public KycHighLevel get(String partyId);

	/**
	 * 申请或修改实名认证
	 */
	public void save(KycHighLevel entity);

	/**
	 * 验证审核结果
	 */
	public String checkApplyResult(String partyId) throws BusinessException;

	/**
	 * 删除高级认证
	 * 
	 * @param partyId
	 */
	public void delete(String partyId);
}
