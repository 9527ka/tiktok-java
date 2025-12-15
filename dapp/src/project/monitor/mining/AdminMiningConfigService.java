package project.monitor.mining;

import kernel.web.Page;

/**
 * 矿池收益规则
 * 
 *
 */
public interface AdminMiningConfigService {

	public Page pagedQuery(int pageNo, int pageSize, String name_para);
	
	public void save(MiningConfig entity);

	public void update(MiningConfig entity);

	public MiningConfig findById(String id);
	
	public MiningConfig findByPartyId(String partyId);

	public void delete(String id);

}
