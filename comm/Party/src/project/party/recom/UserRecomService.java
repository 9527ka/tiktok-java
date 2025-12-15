package project.party.recom;

import java.io.Serializable;
import java.util.List;

import project.party.model.Party;
import project.party.model.UserRecom;

/**
 * 推荐人
 *
 */
public interface UserRecomService {
	public void save(UserRecom paramUserRecom);

	public void update(String partyId, String reco_id);

	public UserRecom findByPartyId(Serializable partyId);

	/**
	 * 查找所有线下用户
	 * 
	 * @param partyId
	 * @return
	 */
	public List<String> findChildren(Serializable partyId);


	/**
	 * 查找所有直属线下用户
	 *
	 * @param partyId
	 * @return
	 */
	public List<String> findDirectlyChildrens(Serializable partyId);

	/**
	 * 查找直推
	 * 
	 * @param partyId
	 * @return
	 */
	public List<UserRecom> findRecoms(Serializable partyId);

	/**
	 * 查找直推 partyId
	 * 
	 */
	public List<String> findRecomsToPartyId(Serializable partyId);

	public List<UserRecom> getParents(Serializable paramSerializable);

	/**
	 * 查询所有上级
	 * @param partyId
	 * @return
	 */
	public List<String> getParentsToPartyId(Serializable partyId);

	/**
	 * 获取所属代理
	 * @param paramSerializable
	 * @return
	 */
	Party getAgentParty(Serializable paramSerializable);


	/**
	 * 直接返回字符串id，例如 "'1','2','3'"
	 * 
	 * @param loginPartyId
	 * @return
	 */
	public String findChildrensIds(String loginPartyId);

}
