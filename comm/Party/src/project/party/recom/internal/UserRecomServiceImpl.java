package project.party.recom.internal;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

import kernel.exception.BusinessException;
import kernel.util.StringUtils;
import project.Constants;
import project.invest.vip.VipService;
import project.party.PartyRedisKeys;
import project.party.model.Party;
import project.party.model.UserRecom;
import project.party.recom.UserRecomService;
import project.redis.RedisHandler;

public class UserRecomServiceImpl extends HibernateDaoSupport implements UserRecomService {
	private RedisHandler redisHandler;

	private VipService vipService;

	public void save(UserRecom entity) {
		getHibernateTemplate().save(entity);

		redisHandler.setSync(PartyRedisKeys.USER_RECOM_PARTYID + entity.getPartyId().toString(), entity);
		List recos = (List) redisHandler.get(PartyRedisKeys.USER_RECOM_RECO_ID + entity.getReco_id().toString());
		if (recos == null) {
			recos = new ArrayList();
		}
		recos.add(entity);

		redisHandler.setSync(PartyRedisKeys.USER_RECOM_RECO_ID + entity.getReco_id().toString(), recos);
		vipService.updatePartyVip(entity.getReco_id().toString());
	}

	public void update(String partyId, String reco_id) {
		boolean find = checkBranch(partyId, reco_id);
		if (find) {
			throw new BusinessException("直线关系，不能修改推荐");
		}

		UserRecom entity = findByPartyId(partyId);
		if (entity == null) {
			entity = new UserRecom();
			entity.setPartyId(partyId);
		}
		Serializable reco_id_old = entity.getReco_id();
		entity.setReco_id(reco_id);
		getHibernateTemplate().merge(entity);
		redisHandler.setSync(PartyRedisKeys.USER_RECOM_PARTYID + entity.getPartyId().toString(), entity);
		vipService.updatePartyVip(entity.getReco_id().toString());
		if (reco_id_old != null) {
			List<UserRecom> recos_old = (List) redisHandler
					.get(PartyRedisKeys.USER_RECOM_RECO_ID + reco_id_old.toString());
			if (recos_old == null) {
				recos_old = new ArrayList();
			}
			List recos_old_reset = new ArrayList();
			for (UserRecom userRecom : recos_old) {
				if (!partyId.toString().equals(userRecom.getPartyId().toString())) {
					recos_old_reset.add(userRecom);
				}
			}

			redisHandler.setSync(PartyRedisKeys.USER_RECOM_RECO_ID + reco_id_old.toString(), recos_old_reset);
			vipService.updatePartyVip(reco_id_old.toString());
		}

		List recos = (List) redisHandler.get(PartyRedisKeys.USER_RECOM_RECO_ID + reco_id.toString());
		if (recos == null) {
			recos = new ArrayList();
		}
		recos.add(entity);
		redisHandler.setSync(PartyRedisKeys.USER_RECOM_RECO_ID + reco_id.toString(), recos);
	}

	public UserRecom findByPartyId(Serializable partyId) {
		if (partyId == null) {
			return null;
		}

		return (UserRecom) redisHandler.get(PartyRedisKeys.USER_RECOM_PARTYID + partyId);
	}

	public List<UserRecom> getParents(Serializable partyId) {
		List list = new LinkedList();
		if (partyId == null) {
			return list;
		}
		list = findParents(partyId, list);

		return list;
	}

	public Party getAgentParty(Serializable partyId) {
		Party agentParty = new Party();
		List list = new LinkedList();
		if (partyId == null) {
			return agentParty;
		}
		List<UserRecom> userRecoms = findParents(partyId, list);
		if (CollectionUtils.isNotEmpty(userRecoms)){
			for (UserRecom userRecom : userRecoms) {
				Party party = (Party) redisHandler.get(PartyRedisKeys.PARTY_ID + userRecom.getReco_id());
				if (null != party && party.getRolename().equals(Constants.SECURITY_ROLE_AGENT)){
					agentParty = party;
					break;
				}
			}
		}
		return agentParty;
	}

	private List<UserRecom> findParents(Serializable partyId, List<UserRecom> list) {
		UserRecom userRecom = findByPartyId(partyId);
		if (userRecom != null) {
			list.add(userRecom);
			findParents(userRecom.getReco_id(), list);
		}
		return list;
	}

	public List<UserRecom> findRecoms(Serializable partyId) {
		List list = (List) redisHandler.get(PartyRedisKeys.USER_RECOM_RECO_ID + partyId.toString());
		if (list == null) {
			list = new ArrayList();
		}
		return list;
	}

	@Override
	public List<String> getParentsToPartyId(Serializable partyId) {
		List<UserRecom> parents = new ArrayList();
		List<String> result = new ArrayList<>();
		parents = findParents(partyId, parents);
		for (int i = 0; i < parents.size(); i++) {
			result.add(parents.get(i).getReco_id().toString());
		}
		return result;
	}

	public List<String> findRecomsToPartyId(Serializable partyId) {
		List recom_list = findRecoms(partyId);
		List list = new ArrayList();
		for (int i = 0; i < recom_list.size(); i++) {
			list.add(((UserRecom) recom_list.get(i)).getPartyId().toString());
		}
		return list;
	}

	public boolean checkBranch(Serializable partyId1, Serializable partyId2) {
		boolean find = false;

		List recom_list1 = getParents(partyId1);
		for (int i = 0; i < recom_list1.size(); i++) {
			if (partyId2.toString().equals(((UserRecom) recom_list1.get(i)).getReco_id().toString())) {
				find = true;
				return find;
			}
		}

		List recom_list2 = getParents(partyId2);
		for (int i = 0; i < recom_list2.size(); i++) {
			if (partyId1.toString().equals(((UserRecom) recom_list2.get(i)).getReco_id().toString())) {
				find = true;
				return find;
			}
		}

		return find;
	}

	public boolean checkParents(Serializable partyId1, Serializable partyId2) {
		boolean find = false;
		if (partyId1.toString().equals(partyId2.toString())) {
			find = true;
			return find;
		}

		List recom_list2 = getParents(partyId2);
		for (int i = 0; i < recom_list2.size(); i++) {
			if (partyId1.toString().equals(((UserRecom) recom_list2.get(i)).getReco_id().toString())) {
				find = true;
				return find;
			}
		}
		return find;
	}

	/**
	 * 查找所有下级用户
	 *
	 * @param partyId
	 * @return
	 */
	public List<String> findChildren(Serializable partyId) {
		List list = new ArrayList();
		list = findChildren(partyId, list);
		return list;
	}

	private List<String> findChildren(Serializable partyId, List<String> list) {
		List recom_list = findRecoms(partyId);
		for (int i = 0; i < recom_list.size(); i++) {
			list.add(((UserRecom) recom_list.get(i)).getPartyId().toString());
			findChildren(((UserRecom) recom_list.get(i)).getPartyId().toString(), list);
		}

		return list;
	}

	public List<String> findDirectlyChildrens(Serializable partyId) {
		List list = new ArrayList();
		list = findDirectlyChildrens(partyId, list);
		return list;
	}


	private List<String> findDirectlyChildrens(Serializable partyId, List<String> list) {
		List recom_list = findRecoms(partyId);
		for (int i = 0; i < recom_list.size(); i++) {
			list.add(((UserRecom) recom_list.get(i)).getPartyId().toString());
			// 查直接下属，不要级联
//			findChildren(((UserRecom) recom_list.get(i)).getPartyId().toString(), list);
		}

		return list;
	}

	

	public String findChildrensIds(String loginPartyId) {
		String childrensId = "";
		if (!StringUtils.isNullOrEmpty(loginPartyId)) {
			List<String> children = this.findChildren(loginPartyId);
			if (children.size() == 0) {
				return null;
			}
			List<String> ids = new LinkedList<String>();
			for (String p : children) {
				ids.add("'" + p + "'");
			}
			childrensId = String.join(",", ids);
		}
		return childrensId;
	}

	public void setRedisHandler(RedisHandler redisHandler) {
		this.redisHandler = redisHandler;
	}

	public void setVipService(VipService vipService) {
		this.vipService = vipService;
	}

}
