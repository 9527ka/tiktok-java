package project.data.internal;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

import kernel.util.Arith;
import kernel.util.DateUtils;
import kernel.web.PagedQueryDao;
import project.data.DataCache;
import project.data.DataDBService;
import project.data.job.RealtimeQueue;
import project.data.model.Realtime;
import project.item.ItemService;
import project.item.model.Item;
import project.syspara.SysparaService;

public class DataDBServiceImpl extends HibernateDaoSupport implements DataDBService {
	private NamedParameterJdbcOperations namedParameterJdbcTemplate;
	private SysparaService sysparaService;
	private PagedQueryDao pagedQueryDao;
	private ItemService itemService;

	@Override
	public void saveAsyn(Realtime entity) {

		Realtime current = DataCache.getRealtime(entity.getSymbol());
		if (current == null || current.getTs() != entity.getTs()) {

			Item item = itemService.cacheBySymbol(entity.getSymbol(), true);
			/**
			 * 交易量倍数不为空或0时修改倍数
			 */
			if (item.getMultiple() > 0) {
				entity.setVolume(Arith.mul(entity.getVolume(), item.getMultiple()));
				entity.setAmount(Arith.mul(entity.getAmount(), item.getMultiple()));
			}

			Double high = DataCache.getRealtimeHigh().get(entity.getSymbol());
			if (high != null && high >= entity.getClose()) {
				entity.setHigh(high);
			}

			
			Double low = DataCache.getRealtimeLow().get(entity.getSymbol());
			if (low != null && low <= entity.getClose()) {
				entity.setLow(low);
			}
			

			Double h24Before = DataCache.getRealtime24HBeforeOpen().get(entity.getSymbol());
			if (h24Before != null) {
				entity.setOpen(h24Before);
			}

			/**
			 * 时间有变化，才保存
			 */
			DataCache.putRealtime(entity.getSymbol(), entity);
			List<Realtime> list = DataCache.getRealtimeHistory().get(entity.getSymbol());
			if (list == null) {
				list = new LinkedList<Realtime>();

			}
			if (entity.getLow() > 0) {
				/**
				 * 修正最低为0的BUG，直接丢弃
				 */
				list.add(entity);

				DataCache.getRealtimeHistory().put(entity.getSymbol(), list);

				RealtimeQueue.add(entity);

			}

		}

	}

	@Override
	public void saveBatch(List<Realtime> entities) {
		for (int i = 0; i < entities.size(); i++) {
			this.getHibernateTemplate().saveOrUpdate(entities.get(i));
		}

	}

	@Override
	public Realtime get(String symbol) {
		StringBuffer queryString = new StringBuffer(" FROM Realtime where 1=1 ");
		Map<String, Object> parameters = new HashMap();
		queryString.append(" and symbol = :symbol  ");
		parameters.put("symbol", symbol);
		List<Realtime> list = this.pagedQueryDao.pagedQueryHql(0, 1, queryString.toString(), parameters).getElements();
		if (list.size() > 0) {
			return list.get(0);
		}
		return null;
	}

	public void deleteRealtime(int days) {
		Map<String, Object> parameters = new HashMap();
		Long ts = DateUtils.addDate(new Date(), days).getTime();
		parameters.put("ts", ts);
		this.namedParameterJdbcTemplate.update("DELETE FROM T_REALTIME WHERE ts < :ts", parameters);

	}

	@Override
	public void updateOptimize(String table) {
		Map<String, Object> parameters = new HashMap();
		this.namedParameterJdbcTemplate.update("optimize  table " + table, parameters);

	}

	@Override
	public List<Realtime> findRealtimeOneDay(String symbol) {
		int interval = this.sysparaService.find("data_interval").getInteger().intValue() / 1000;
		int num = (24 * 60 * 60) / interval;

		StringBuffer queryString = new StringBuffer(" FROM Realtime  WHERE 1=1  ");

		Map<String, Object> parameters = new HashMap();
		queryString.append(" and symbol = :symbol  ");
		parameters.put("symbol", symbol);

		queryString.append(" order by ts asc");
		List<Realtime> list = this.pagedQueryDao.pagedQueryHql(0, num, queryString.toString(), parameters)
				.getElements();

		return list;
	}

	public void setNamedParameterJdbcTemplate(NamedParameterJdbcOperations namedParameterJdbcTemplate) {
		this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
	}

	public void setSysparaService(SysparaService sysparaService) {
		this.sysparaService = sysparaService;
	}

	public void setPagedQueryDao(PagedQueryDao pagedQueryDao) {
		this.pagedQueryDao = pagedQueryDao;
	}

	public void setItemService(ItemService itemService) {
		this.itemService = itemService;
	}

}
