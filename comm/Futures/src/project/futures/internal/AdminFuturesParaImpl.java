package project.futures.internal;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

import kernel.util.Arith;
import kernel.util.StringUtils;
import kernel.web.Page;
import kernel.web.PagedQueryDao;
import project.futures.AdminFuturesParaService;
import project.futures.FuturesPara;
import project.futures.FuturesParaService;

public class AdminFuturesParaImpl extends HibernateDaoSupport implements AdminFuturesParaService{

	private FuturesParaService futuresParaService;
	
	
	private PagedQueryDao pagedQueryDao;
	

	
	public Page pagedQuery(int pageNo, int pageSize,String symbol) {
		StringBuffer queryString = new StringBuffer(
				"SELECT futures.UUID id,"
				//+ "item.NAME name,"
				+ "futures.SYMBOL symbol,"
				+ "futures.TIMENUM timeNum,"
				+ "futures.TIMEUNIT timeUnit,futures.UNIT_AMOUNT unit_amount,futures.UNIT_MAX_AMOUNT unit_max_amount,"
				+ "futures.UNIT_FEE unit_fee,"
				+ "futures.PROFIT_RATIO profit_ratio,futures.PROFIT_RATIO_MAX profit_ratio_max ");
		queryString.append(
				" FROM T_FUTURES_PARA futures "
				//+ "LEFT JOIN T_ITEM item ON "
				//+ " item.symbol = futures.symbol "
				+ "  WHERE 1 = 1 ");

		Map<String, Object> parameters = new HashMap();
		
		if (!StringUtils.isNullOrEmpty(symbol)) {
			queryString.append(" and "
					//+ "( "
					+ "futures.SYMBOL = :symbol "
					//+ "or item.NAME = :name)"
					+ " ");
			parameters.put("symbol", symbol);
			//parameters.put("name", symbol);
		}

		Page page = this.pagedQueryDao.pagedQuerySQL(pageNo, pageSize, queryString.toString(), parameters);
		for(Map<String,Object> map:(List<Map<String,Object>>)page.getElements()) {
			map.put("profit_ratio", Arith.mul(new Double(map.get("profit_ratio").toString()),100));
			map.put("profit_ratio_max", Arith.mul(new Double(map.get("profit_ratio_max").toString()),100));
			map.put("unit_fee", Arith.mul(new Double(map.get("unit_fee").toString()),100));
		}
		return page;
	}

	public FuturesPara getById(String id) {
		return this.getHibernateTemplate().get(FuturesPara.class, id);
	}
	
	public void update(FuturesPara source) {
		this.futuresParaService.update(source);
	}

	public void add(FuturesPara source) {
		this.futuresParaService.add(source);
	}
	public void delete(FuturesPara source) {
		this.futuresParaService.delete(source);
	}
	
	public void setFuturesParaService(FuturesParaService futuresParaService) {
		this.futuresParaService = futuresParaService;
	}

	public void setPagedQueryDao(PagedQueryDao pagedQueryDao) {
		this.pagedQueryDao = pagedQueryDao;
	}
	
	
	
}
