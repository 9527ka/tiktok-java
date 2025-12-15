package project.futures;

import java.io.Serializable;

public interface ProfitAndLossConfigService {
	
	/**
	 * 
	 * @param entity
	 * @param username 操作者
	 */
	public void save(ProfitAndLossConfig entity, String Operater_username);

	public void update(ProfitAndLossConfig entity, String Operater_username);

	public void delete(String id, String Operater_username);

	public ProfitAndLossConfig findById(String id);

	public ProfitAndLossConfig findByPartyId(String partyId);

	public ProfitAndLossConfig cacheByPartyId(String partyId);
}
