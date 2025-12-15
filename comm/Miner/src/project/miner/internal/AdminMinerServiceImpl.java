package project.miner.internal;

import java.util.HashMap;
import java.util.Map;

import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

import kernel.util.StringUtils;
import kernel.web.Page;
import kernel.web.PagedQueryDao;
import project.miner.AdminMinerService;

public class AdminMinerServiceImpl extends HibernateDaoSupport implements AdminMinerService {
	protected PagedQueryDao pagedQueryDao;

	public Page pagedQuery(int pageNo, int pageSize, String name_para) {
		StringBuffer queryString = new StringBuffer(
				" SELECT miner.UUID id,miner.NAME name,miner.NAME_EN name_en,miner.NAME_CN name_cn,miner.IMG img,miner.CYCLE cycle,miner.CYCLE_CLOSE cycle_close, "
						+ " miner.SHOW_DAILY_RATE show_daily_rate  ,miner.DAILY_RATE daily_rate  ,miner.STATE state,miner.ON_SALE on_sale,miner.TEST test,"
						+ " miner.INVESTMENT_MIN investment_min,miner.INVESTMENT_MAX investment_max ");
		queryString.append(" FROM T_MINER miner WHERE 1 = 1 ");
		Map<String, Object> parameters = new HashMap<>();
		if (!StringUtils.isNullOrEmpty(name_para)) {
			queryString.append(" and  miner.NAME like:name ");
			parameters.put("name", "%" + name_para + "%");
		}
		queryString.append(" ORDER BY miner.INVESTMENT_MIN+0 ASC ");
		Page page = this.pagedQueryDao.pagedQuerySQL(pageNo, pageSize, queryString.toString(), parameters);
		return page;
	}

	public void setPagedQueryDao(PagedQueryDao pagedQueryDao) {
		this.pagedQueryDao = pagedQueryDao;
	}
}
