package project.hobi;

import java.util.List;

import kernel.web.Page;

public interface AdminContractSymbolsService {

	public void saveReload();

	public Page pagedQuery(int pageNo, int pageSize, String quote_currency, String base_currency);

	public List<String> getQuoteList();
}
