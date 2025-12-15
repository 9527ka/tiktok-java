package project.item;

import kernel.web.Page;
import project.item.model.ItemLever;

public interface AdminItemLeverageService {

	public Page pagedQuery(int pageNo, int pageSize, String itemId);

	public void save(ItemLever entity);

	public void delete(String id);
}
