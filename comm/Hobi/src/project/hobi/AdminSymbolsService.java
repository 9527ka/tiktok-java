package project.hobi;

import java.util.List;
import java.util.Map;

import kernel.web.Page;
import project.data.model.Symbols;

public interface AdminSymbolsService {

	public void saveReload();

	public Page pagedQuery(int pageNo, int pageSize, String quote_currency, String base_currency);
	public Page pagedQuery(int pageNo, int pageSize);
	
	
	/**
	 * 查询报价单位
	 */
	public Page pagedQueryMap(int pageNo, int pageSize);

}
