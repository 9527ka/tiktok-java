package project.item;

import java.util.List;
import java.util.Map;

import project.item.model.ItemUserOptional;

public interface ItemUserOptionalService {

	List<ItemUserOptional> cacheListByPartyId(String partyId);

	void update(ItemUserOptional entity);

	void save(ItemUserOptional entity);

	public void delete(String partyId, String symbol);

	public void delete(ItemUserOptional entity);

	/**
	 * 返回自选币种的行情
	 */
	public List<Map<String, Object>> cacheListDataByPartyId(String partyId, String symbol);
}