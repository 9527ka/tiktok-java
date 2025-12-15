package project.miner.internal;

import java.util.HashMap;
import java.util.Map;

import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

import kernel.util.StringUtils;
import kernel.web.Page;
import kernel.web.PagedQueryDao;
import project.miner.AdminMinerParaService;

public class AdminMinerParaServiceImpl extends HibernateDaoSupport implements AdminMinerParaService {

	private PagedQueryDao pagedQueryDao;

	public Page pagedQuery(int pageNo, int pageSize,String miner_id) {
		StringBuffer queryString = new StringBuffer(
				" SELECT minerPara.UUID id,minerPara.MINER_ID miner_id,minerPara.CYCLE cycle,minerPara.AMOUNT amount ");
		queryString.append(" FROM T_MINER_PARA minerPara WHERE 1 = 1 ");
		Map<String, Object> parameters = new HashMap<>();
		if (!StringUtils.isNullOrEmpty(miner_id)) {
			queryString.append(" and  minerPara.MINER_ID =:miner_id ");
			parameters.put("miner_id", miner_id);
		}
		queryString.append(" ORDER BY minerPara.CYCLE+0 ASC ");
		Page page = this.pagedQueryDao.pagedQuerySQL(pageNo, pageSize, queryString.toString(), parameters);
		return page;
	}

	public void setPagedQueryDao(PagedQueryDao pagedQueryDao) {
		this.pagedQueryDao = pagedQueryDao;
	}
}
