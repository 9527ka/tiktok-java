package project.data.loadcache;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

import project.party.PartyRedisKeys;
import project.party.model.Party;
import project.party.model.UserRecom;
import project.redis.RedisHandler;

public class PartyLoadCacheService extends HibernateDaoSupport {

	private static final Log logger = LogFactory.getLog(PartyLoadCacheService.class);

	private RedisHandler redisHandler;

	public void loadcache() {
		loadParty();
		loadUserRecom();
		logger.info("完成Party数据加载redis");
	}

	private void loadParty() {
		StringBuffer queryString = new StringBuffer(" FROM Party ");
		List<Party> list =(List<Party>) this.getHibernateTemplate().find(queryString.toString());
		Map<String, Party> cache = new ConcurrentHashMap<String, Party>();
		Map<String, Date> onlineCache = new ConcurrentHashMap<String, Date>();

		for (int i = 0; i < list.size(); i++) {
			Party party = list.get(i);
			redisHandler.setSync(PartyRedisKeys.PARTY_ID + party.getId().toString(), party);
			redisHandler.setSync(PartyRedisKeys.PARTY_USERNAME + party.getUsername(), party);

			cache.put(list.get(i).getId().toString(), party);
		}
		redisHandler.setSync(PartyRedisKeys.PARTY_ONLINEUSER, onlineCache);
	}

	private void loadUserRecom() {
		StringBuffer queryString = new StringBuffer(" FROM UserRecom ");
		List<UserRecom> list = (List<UserRecom>) this.getHibernateTemplate().find(queryString.toString());
		Map<String, List<UserRecom>> map = new ConcurrentHashMap<String, List<UserRecom>>();
		for (int i = 0; i < list.size(); i++) {
			redisHandler.setSync(PartyRedisKeys.USER_RECOM_PARTYID + list.get(i).getPartyId(), list.get(i));
			List<UserRecom> recos = map.get(list.get(i).getReco_id().toString());
			if (recos == null) {
				recos = new ArrayList<UserRecom>();
			}
			recos.add(list.get(i));

			map.put(list.get(i).getReco_id().toString(), recos);
		}

		Map<String, Object> params = new ConcurrentHashMap<String, Object>();
		Iterator<Entry<String, List<UserRecom>>> it = map.entrySet().iterator();
		while (it.hasNext()) {
			Entry<String, List<UserRecom>> entry = it.next();
			params.put(PartyRedisKeys.USER_RECOM_RECO_ID + entry.getKey(), entry.getValue());
		}
		redisHandler.setBatchSync(params);

	}

	public void setRedisHandler(RedisHandler redisHandler) {
		this.redisHandler = redisHandler;
	}

}
