package project.follow.internal;

import java.io.Serializable;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

import kernel.util.Arith;
import kernel.util.StringUtils;
import kernel.web.Page;
import kernel.web.PagedQueryDao;
import project.Constants;
import project.follow.Trader;
import project.follow.TraderUser;
import project.follow.TraderUserService;
import project.party.PartyService;
import project.party.model.Party;

public class TraderUserServiceImpl extends HibernateDaoSupport implements TraderUserService {
	private PartyService partyService;
	private PagedQueryDao pagedQueryDao;
	
	
	public List<Map<String, Object>> getPaged(int pageNo, int pageSize, String partyId, String type){
		StringBuffer queryString = new StringBuffer("");
		queryString.append(" FROM ");
		queryString.append(" TraderUser ");
		queryString.append(" where 1=1 ");

		Map<String, Object> parameters = new HashMap();

			queryString.append(" and partyId = :partyId");
			parameters.put("partyId",  partyId );

//		if (!StringUtils.isNullOrEmpty(state)) {
//			queryString.append(" and state =:state ");
//			parameters.put("state", state);
//		}

		
		Page page = this.pagedQueryDao.pagedQueryHql(pageNo, pageSize, queryString.toString(), parameters);
		List<Map<String, Object>> data = this.bulidData(page.getElements());
		return data;
	}
	
	private List<Map<String, Object>> bulidData(List<TraderUser> traderUsers) {
		List<Map<String, Object>> result_traders = new ArrayList();
		DecimalFormat df2 = new DecimalFormat("#.##");
		df2.setRoundingMode(RoundingMode.FLOOR);// 向下取整
		if (traderUsers == null) {
			return result_traders;
		}
		for (int i = 0; i < traderUsers.size(); i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			TraderUser entity = traderUsers.get(i);
			map.put("name", entity.getName());
			/**
			 * 累计金额order_amount
			 */
			map.put("amount_sum", df2.format(entity.getAmount_sum()));

			map.put("profit", df2.format(entity.getProfit()));

			result_traders.add(map);
		}

		return result_traders;

	}
	

	@Override
	public TraderUser saveTraderUserByPartyId(String partyId) {
		List<TraderUser> list = (List<TraderUser>) getHibernateTemplate().find(" FROM TraderUser WHERE partyId = ?",
				new Object[] { partyId });
		if (list.size() > 0) {
			TraderUser traderUser = list.get(0);
			return traderUser;

		} else {
			Party party = this.partyService.cachePartyBy(partyId, true);
			TraderUser traderUser = new TraderUser();
			traderUser.setPartyId(partyId);
			traderUser.setName(party.getUsername());
			traderUser.setCreate_time(party.getCreateTime());
			save(traderUser);
			return traderUser;
		}
	}

	public void save(TraderUser traderUser) {
		this.getHibernateTemplate().save(traderUser);
	}

	public void update(TraderUser traderUser) {
		this.getHibernateTemplate().update(traderUser);
	}

	public void setPartyService(PartyService partyService) {
		this.partyService = partyService;
	}

	public void setPagedQueryDao(PagedQueryDao pagedQueryDao) {
		this.pagedQueryDao = pagedQueryDao;
	}

}
