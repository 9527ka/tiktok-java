package project.finance.internal;

import java.util.HashMap;
import java.util.Map;

import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

import kernel.util.StringUtils;
import kernel.web.Page;
import kernel.web.PagedQueryDao;
import project.finance.AdminFinanceService;

public class AdminFinanceServiceImpl extends HibernateDaoSupport implements AdminFinanceService {

	protected PagedQueryDao pagedQueryDao;

	public Page pagedQuery(int pageNo, int pageSize, String name_para) {
		StringBuffer queryString = new StringBuffer(
				" SELECT finance.UUID id,finance.NAME name,finance.NAME_EN name_en,finance.NAME_CN name_cn,"
						+ " finance.NAME_KN name_kn,finance.NAME_JN name_jn,finance.IMG img,finance.CYCLE cycle, "
						+ " finance.DAILY_RATE daily_rate  ,finance.DAILY_RATE_MAX daily_rate_max  ,finance.TODAY_RATE today_rate ,finance.STATE state,"
						+ " finance.DEFAULT_RATIO default_ratio  ,finance.INVESTMENT_MIN investment_min"
						+ " ,finance.INVESTMENT_MAX investment_max    ");
		queryString.append(" FROM T_FINANCE finance WHERE 1 = 1 ");
		Map<String, Object> parameters = new HashMap<>();
		if (!StringUtils.isNullOrEmpty(name_para)) {
			queryString.append(" and  finance.NAME like:name ");
			parameters.put("name", "%" + name_para + "%");
		}
		Page page = this.pagedQueryDao.pagedQuerySQL(pageNo, pageSize, queryString.toString(), parameters);
		return page;
	}

	public void setPagedQueryDao(PagedQueryDao pagedQueryDao) {
		this.pagedQueryDao = pagedQueryDao;
	}
}