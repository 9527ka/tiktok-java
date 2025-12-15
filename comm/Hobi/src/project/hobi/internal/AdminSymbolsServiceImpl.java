package project.hobi.internal;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

import kernel.util.StringUtils;
import kernel.web.Page;
import kernel.web.PagedQueryDao;
import project.data.model.Symbols;
import project.hobi.AdminSymbolsService;
import project.hobi.HobiDataService;;

public class AdminSymbolsServiceImpl extends HibernateDaoSupport implements AdminSymbolsService {
	private HobiDataService hobiDataService;
	private PagedQueryDao pagedQueryDao;

	@Override
	public void saveReload() {
		List<Symbols> list = hobiDataService.symbols();
		if (list.size() > 0) {
			this.delete(this.getAll());
			for (int i = 0; i < list.size(); i++) {
				this.getHibernateTemplate().saveOrUpdate(list.get(i));
			}
		}
	}

	private List<Symbols> getAll() {
		List<Symbols> list = (List<Symbols>) this.getHibernateTemplate().find("FROM Symbols  ");
		return list;
	}

	@Override
	public Page pagedQuery(int pageNo, int pageSize, String quote_currency, String base_currency) {
		StringBuffer queryString = new StringBuffer();
		queryString.append(" FROM Symbols  where 1 = 1 ");

		Map<String, Object> parameters = new HashMap();

		if (!StringUtils.isNullOrEmpty(quote_currency)) {
			queryString.append(" and quote_currency = :quote_currency  ");
			parameters.put("quote_currency", quote_currency);
		}

		if (!StringUtils.isNullOrEmpty(base_currency)) {
			queryString.append(" and  base_currency =:base_currency ");
			parameters.put("base_currency", base_currency);
		}

		Page page = this.pagedQueryDao.pagedQueryHql(pageNo, pageSize, queryString.toString(), parameters);

		return page;
	}
	@Override
	public Page pagedQuery(int pageNo, int pageSize) {
		StringBuffer queryString = new StringBuffer(" SELECT DISTINCT(QUOTE_CURRENCY)  ");

		queryString.append(
				" FROM T_SYMBOLS  WHERE 1 = 1 ");

		Map parameters = new HashMap();

		Page page = this.pagedQueryDao.pagedQuerySQL(pageNo, pageSize, queryString.toString(), parameters);

		return page;
	}
	@Override
	public Page pagedQueryMap(int pageNo, int pageSize) {
		StringBuffer queryString = new StringBuffer(" SELECT DISTINCT(QUOTE_CURRENCY) quote_currency ");

		queryString.append(
				" FROM T_SYMBOLS  WHERE 1 = 1 ");

		Map parameters = new HashMap();

		Page page = this.pagedQueryDao.pagedQuerySQL(pageNo, pageSize, queryString.toString(), parameters);

		return page;
	}

	private void delete(List<Symbols> list) {
		this.getHibernateTemplate().deleteAll(list);
	}

	public void setHobiDataService(HobiDataService hobiDataService) {
		this.hobiDataService = hobiDataService;
	}

	public void setPagedQueryDao(PagedQueryDao pagedQueryDao) {
		this.pagedQueryDao = pagedQueryDao;
	}

}
